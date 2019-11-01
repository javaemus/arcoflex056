/***************************************************************************

  a2600.c

  Driver file to handle emulation of the Atari 2600.

  --Have to implement the playfield graphics register--

 Contains the addresses of the 2600 hardware

 TIA *Write* Addresses (6 bit)


    VSYNC   0x00     Vertical Sync Set-Clear
    VBLANK  0x01     Vertical Blank Set-Clear
    WSYNC   0x02     Wait for Horizontal Blank
    RSYNC   0x03     Reset Horizontal Sync Counter
    NUSIZ0  0x04     Number-Size player/missle 0
    NUSIZ1  0x05     Number-Size player/missle 1
    COLUP0  0x06     Color-Luminance Player 0
    COLUP1  0x07     Color-Luminance Player 1
    COLUPF  0x08     Color-Luminance Playfield
    COLUBK  0x09     Color-Luminance BackGround
    CTRLPF  0x0A     Control Playfield, Ball, Collisions
    REFP0   0x0B     Reflection Player 0
    REFP1   0x0C     Reflection Player 1
    PF0     0x0D     Playfield Register Byte 0
    PF1     0x0E     Playfield Register Byte 1
    PF2     0x0F     Playfield Register Byte 2
    RESP0   0x10     Reset Player 0
    RESP1   0x11     Reset Player 1
    RESM0   0x12     Reset Missle 0
    RESM1   0x13     Reset Missle 1
    RESBL   0x14     Reset Ball

    AUDC0   0x15    Audio Control 0
    AUDC1   0x16    Audio Control 1
    AUDF0   0x17    Audio Frequency 0
    AUDF1   0x18    Audio Frequency 1
    AUDV0   0x19    Audio Volume 0
    AUDV1   0x1A    Audio Volume 1
    GRP0    0x1B    Graphics Register Player 0
    GRP1    0x1C    Graphics Register Player 0
    ENAM0   0x1D    Graphics Enable Missle 0
    ENAM1   0x1E    Graphics Enable Missle 1
    ENABL   0x1F    Graphics Enable Ball
    HMP0    0x20    Horizontal Motion Player 0
    HMP1    0x21    Horizontal Motion Player 0
    HMM0    0x22    Horizontal Motion Missle 0
    HMM1    0x23    Horizontal Motion Missle 1
    HMBL    0x24    Horizontal Motion Ball
    VDELP0  0x25    Vertical Delay Player 0
    VDELP1  0x26    Vertical Delay Player 1
    VDELBL  0x27    Vertical Delay Ball
    RESMP0  0x28    Reset Missle 0 to Player 0
    RESMP1  0x29    Reset Missle 1 to Player 1
    HMOVE   0x2A    Apply Horizontal Motion
    HMCLR   0x2B    Clear Horizontal Move Registers
    CXCLR   0x2C    Clear Collision Latches


 TIA *Read* Addresses
                                  bit 6  bit 7
    CXM0P   0x0    Read Collision M0-P1  M0-P0
    CXM1P   0x1                   M1-P0  M1-P1
    CXP0FB  0x2                   P0-PF  P0-BL
    CXP1FB  0x3                   P1-PF  P1-BL
    CXM0FB  0x4                   M0-PF  M0-BL
    CXM1FB  0x5                   M1-PF  M1-BL
    CXBLPF  0x6                   BL-PF  -----
    CXPPMM  0x7                   P0-P1  M0-M1
    INPT0   0x8     Read Pot Port 0
    INPT1   0x9     Read Pot Port 1
    INPT2   0xA     Read Pot Port 2
    INPT3   0xB     Read Pot Port 3
    INPT4   0xC     Read Input (Trigger) 0
    INPT5   0xD     Read Input (Trigger) 1


 RIOT Addresses

    RAM     0x80 - 0xff           RAM 0x0180-0x01FF

    SWCHA   0x280   Port A data rwegister (joysticks)
    SWACNT  0x281   Port A data direction register (DDR)
    SWCHB   0x282   Port B data (Console Switches)
    SWBCNT  0x283   Port B DDR
    INTIM   0x284   Timer Output

    TIM1T   0x294   set 1 clock interval
    TIM8T   0x295   set 8 clock interval
    TIM64T  0x296   set 64 clock interval
    T1024T  0x297   set 1024 clock interval
                      these are also at 0x380-0x397

    ROM 0xF000   To FFFF,0x1000-1FFF

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.systems;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static common.libc.cstdio.*;
import static common.libc.cstring.*;
import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrf.*;
import static mame056.cpuintrfH.*;
import static mame056.drawgfxH.*;
import static mame056.driverH.*;
import static mame056.inptport.*;
import static mame056.inptportH.*;
import static mame056.inputH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mame056.sndintrf.*;
import static mame056.sndintrfH.*;
import static mess056.device.*;
import static mess056.deviceH.*;

import static mess056.machine.a2600.*;
import static mess056.messH.*;
import static mess056.sound.tiaintfH.*;

public class a2600
{
	
	
	/* This code is not to be used yet */
	//#define USE_SCANLINE_WSYNC
	
	/* horrid memory mirroring ahead */
	static Memory_ReadAddress readmem[] = {
            new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
            new Memory_ReadAddress( 0x0000, 0x003F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0040, 0x007F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0080, 0x00FF, MRA_RAM     ),
	
	    new Memory_ReadAddress( 0x0100, 0x013F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0140, 0x017F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0180, 0x01FF, MRA_RAM     ),
	
	    new Memory_ReadAddress( 0x0200, 0x023F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0240, 0x027F, a2600_TIA_r ),
	
	    new Memory_ReadAddress( 0x0300, 0x033F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0340, 0x037F, a2600_TIA_r ),
	    new Memory_ReadAddress( 0x0280, 0x0297, a2600_riot_r ),   /* RIOT reads for a2600 */
	
	    new Memory_ReadAddress( 0x1000, 0x17FF, MRA_ROM     ),
	    new Memory_ReadAddress( 0x1800, 0x1FDF, MRA_ROM     ),
	    new Memory_ReadAddress( 0x1FE0, 0x1FFF, a2600_bs_r  ),    /* for bankswitching */
	    new Memory_ReadAddress( 0xF000, 0xF7FF, MRA_ROM     ),
	    new Memory_ReadAddress( 0xF800, 0xFFDF, MRA_ROM     ),
	    new Memory_ReadAddress( 0xFFE0, 0xFFF9, a2600_bs_r  ),
	    new Memory_ReadAddress( 0xFFFA, 0xFFFF, MRA_ROM     ),
            new Memory_ReadAddress(MEMPORT_MARKER, 0) /* end of table */
        };
	
	static Memory_WriteAddress writemem[] = { 
            new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
            new Memory_WriteAddress( 0x0000, 0x003F, a2600_TIA_w ),
	    new Memory_WriteAddress( 0x0040, 0x007F, a2600_TIA_w ),
	    new Memory_WriteAddress( 0x0080, 0x00FF, MWA_RAM  ),
	
	    new Memory_WriteAddress( 0x0100, 0x013F, a2600_TIA_w ),
	    new Memory_WriteAddress( 0x0140, 0x017F, a2600_TIA_w ),
	    new Memory_WriteAddress( 0x0180, 0x01FF, MWA_RAM  ),
	
	    new Memory_WriteAddress( 0x0200, 0x023F, a2600_TIA_w ),
	    new Memory_WriteAddress( 0x0240, 0x027F, a2600_TIA_w ),
	
	    new Memory_WriteAddress( 0x0280, 0x0297, a2600_riot_w ),   /* RIOT writes for a2600 */
	
	    new Memory_WriteAddress( 0x0300, 0x033F, a2600_TIA_w ),
	    new Memory_WriteAddress( 0x0340, 0x037F, a2600_TIA_w ),
	
	    new Memory_WriteAddress( 0x1000, 0x17FF, MWA_ROM  ),
	    new Memory_WriteAddress( 0x1800, 0x1FFF, MWA_ROM  ),   /* ROM mirror for 2k images */
	    new Memory_WriteAddress( 0xF000, 0xF7FF, MWA_ROM  ),
	    new Memory_WriteAddress( 0xF800, 0xFFFF, MWA_ROM  ),   /* ROM mirror for 2k images */
            new Memory_WriteAddress(MEMPORT_MARKER, 0) /* end of table */
        };
	
	
	static InputPortPtr input_ports_a2600 = new InputPortPtr(){ public void handler() { 
	
	
	    PORT_START();  /* SWCHA 0x280 RIOT */
	    PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2 );
	    PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );
	    PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
	    PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
	    PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);
	    PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN);
	    PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);
	    PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
	
	    PORT_START();  /* SWACNT 0x281 RIOT */
	    PORT_BIT ( 0x00, IP_ACTIVE_HIGH, IPT_UNKNOWN);
	
	    PORT_START();  /* SWCHB 0x282 RIOT */
	    PORT_BITX( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN, "Reset", KEYCODE_R, IP_JOY_DEFAULT);
	    PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN, "Start", KEYCODE_S, IP_JOY_DEFAULT);
	    PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_UNUSED);
	    PORT_BITX( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN, "Color/BW", KEYCODE_C, IP_JOY_DEFAULT);
	
	INPUT_PORTS_END(); }}; 
	
	
	static char palette[] =
	{
	    /* Grey */
	    0x00, 0x00, 0x00,
	    0x1c, 0x1c, 0x1c,
	    0x39, 0x39, 0x39,
	    0x59, 0x59, 0x59,
	    0x79, 0x79, 0x79,
	    0x92, 0x92, 0x92,
	    0xab, 0xab, 0xab,
	    0xbc, 0xbc, 0xbc,
	    0xcd, 0xcd, 0xcd,
	    0xd9, 0xd9, 0xd9,
	    0xe6, 0xe6, 0xe6,
	    0xec, 0xec, 0xec,
	    0xf2, 0xf2, 0xf2,
	    0xf8, 0xf8, 0xf8,
	    0xff, 0xff, 0xff,
	    0xff, 0xff, 0xff,
	
	/* Gold */
	    0x39, 0x17, 0x01,
	    0x5e, 0x23, 0x04,
	    0x83, 0x30, 0x08,
	    0xa5, 0x47, 0x16,
	    0xc8, 0x5f, 0x24,
	    0xe3, 0x78, 0x20,
	    0xff, 0x91, 0x1d,
	    0xff, 0xab, 0x1d,
	    0xff, 0xc5, 0x1d,
	    0xff, 0xce, 0x34,
	    0xff, 0xd8, 0x4c,
	    0xff, 0xe6, 0x51,
	    0xff, 0xf4, 0x56,
	    0xff, 0xf9, 0x77,
	    0xff, 0xff, 0x98,
	    0xff, 0xff, 0x98,
	
	/* Orange */
	    0x45, 0x19, 0x04,
	    0x72, 0x1e, 0x11,
	    0x9f, 0x24, 0x1e,
	    0xb3, 0x3a, 0x20,
	    0xc8, 0x51, 0x22,
	    0xe3, 0x69, 0x20,
	    0xff, 0x81, 0x1e,
	    0xff, 0x8c, 0x25,
	    0xff, 0x98, 0x2c,
	    0xff, 0xae, 0x38,
	    0xff, 0xc5, 0x45,
	    0xff, 0xc5, 0x59,
	    0xff, 0xc6, 0x6d,
	    0xff, 0xd5, 0x87,
	    0xff, 0xe4, 0xa1,
	    0xff, 0xe4, 0xa1,
	
	/* Red Orange */
	    0x4a, 0x17, 0x04,
	    0x7e, 0x1a, 0x0d,
	    0xb2, 0x1d, 0x17,
	    0xc8, 0x21, 0x19,
	    0xdf, 0x25, 0x1c,
	    0xec, 0x3b, 0x38,
	    0xfa, 0x52, 0x55,
	    0xfc, 0x61, 0x61,
	    0xff, 0x70, 0x6e,
	    0xff, 0x7f, 0x7e,
	    0xff, 0x8f, 0x8f,
	    0xff, 0x9d, 0x9e,
	    0xff, 0xab, 0xad,
	    0xff, 0xb9, 0xbd,
	    0xff, 0xc7, 0xce,
	    0xff, 0xc7, 0xce,
	
	/* Pink */
	    0x05, 0x05, 0x68,
	    0x3b, 0x13, 0x6d,
	    0x71, 0x22, 0x72,
	    0x8b, 0x2a, 0x8c,
	    0xa5, 0x32, 0xa6,
	    0xb9, 0x38, 0xba,
	    0xcd, 0x3e, 0xcf,
	    0xdb, 0x47, 0xdd,
	    0xea, 0x51, 0xeb,
	    0xf4, 0x5f, 0xf5,
	    0xfe, 0x6d, 0xff,
	    0xfe, 0x7a, 0xfd,
	    0xff, 0x87, 0xfb,
	    0xff, 0x95, 0xfd,
	    0xff, 0xa4, 0xff,
	    0xff, 0xa4, 0xff,
	
	/* Purple */
	    0x28, 0x04, 0x79,
	    0x40, 0x09, 0x84,
	    0x59, 0x0f, 0x90,
	    0x70, 0x24, 0x9d,
	    0x88, 0x39, 0xaa,
	    0xa4, 0x41, 0xc3,
	    0xc0, 0x4a, 0xdc,
	    0xd0, 0x54, 0xed,
	    0xe0, 0x5e, 0xff,
	    0xe9, 0x6d, 0xff,
	    0xf2, 0x7c, 0xff,
	    0xf8, 0x8a, 0xff,
	    0xff, 0x98, 0xff,
	    0xfe, 0xa1, 0xff,
	    0xfe, 0xab, 0xff,
	    0xfe, 0xab, 0xff,
	
	/* Blue Purple */
	    0x35, 0x08, 0x8a,
	    0x42, 0x0a, 0xad,
	    0x50, 0x0c, 0xd0,
	    0x64, 0x28, 0xd0,
	    0x79, 0x45, 0xd0,
	    0x8d, 0x4b, 0xd4,
	    0xa2, 0x51, 0xd9,
	    0xb0, 0x58, 0xec,
	    0xbe, 0x60, 0xff,
	    0xc5, 0x6b, 0xff,
	    0xcc, 0x77, 0xff,
	    0xd1, 0x83, 0xff,
	    0xd7, 0x90, 0xff,
	    0xdb, 0x9d, 0xff,
	    0xdf, 0xaa, 0xff,
	    0xdf, 0xaa, 0xff,
	
	/* Blue */
	    0x05, 0x1e, 0x81,
	    0x06, 0x26, 0xa5,
	    0x08, 0x2f, 0xca,
	    0x26, 0x3d, 0xd4,
	    0x44, 0x4c, 0xde,
	    0x4f, 0x5a, 0xee,
	    0x5a, 0x68, 0xff,
	    0x65, 0x75, 0xff,
	    0x71, 0x83, 0xff,
	    0x80, 0x91, 0xff,
	    0x90, 0xa0, 0xff,
	    0x97, 0xa9, 0xff,
	    0x9f, 0xb2, 0xff,
	    0xaf, 0xbe, 0xff,
	    0xc0, 0xcb, 0xff,
	    0xc0, 0xcb, 0xff,
	
	/* Blue */
	    0x0c, 0x04, 0x8b,
	    0x22, 0x18, 0xa0,
	    0x38, 0x2d, 0xb5,
	    0x48, 0x3e, 0xc7,
	    0x58, 0x4f, 0xda,
	    0x61, 0x59, 0xec,
	    0x6b, 0x64, 0xff,
	    0x7a, 0x74, 0xff,
	    0x8a, 0x84, 0xff,
	    0x91, 0x8e, 0xff,
	    0x99, 0x98, 0xff,
	    0xa5, 0xa3, 0xff,
	    0xb1, 0xae, 0xff,
	    0xb8, 0xb8, 0xff,
	    0xc0, 0xc2, 0xff,
	    0xc0, 0xc2, 0xff,
	
	/* Light Blue */
	    0x1d, 0x29, 0x5a,
	    0x1d, 0x38, 0x76,
	    0x1d, 0x48, 0x92,
	    0x1c, 0x5c, 0xac,
	    0x1c, 0x71, 0xc6,
	    0x32, 0x86, 0xcf,
	    0x48, 0x9b, 0xd9,
	    0x4e, 0xa8, 0xec,
	    0x55, 0xb6, 0xff,
	    0x70, 0xc7, 0xff,
	    0x8c, 0xd8, 0xff,
	    0x93, 0xdb, 0xff,
	    0x9b, 0xdf, 0xff,
	    0xaf, 0xe4, 0xff,
	    0xc3, 0xe9, 0xff,
	    0xc3, 0xe9, 0xff,
	
	/* Turquoise */
	    0x2f, 0x43, 0x02,
	    0x39, 0x52, 0x02,
	    0x44, 0x61, 0x03,
	    0x41, 0x7a, 0x12,
	    0x3e, 0x94, 0x21,
	    0x4a, 0x9f, 0x2e,
	    0x57, 0xab, 0x3b,
	    0x5c, 0xbd, 0x55,
	    0x61, 0xd0, 0x70,
	    0x69, 0xe2, 0x7a,
	    0x72, 0xf5, 0x84,
	    0x7c, 0xfa, 0x8d,
	    0x87, 0xff, 0x97,
	    0x9a, 0xff, 0xa6,
	    0xad, 0xff, 0xb6,
	    0xad, 0xff, 0xb6,
	
	/* Green blue */
	    0x0a, 0x41, 0x08,
	    0x0d, 0x54, 0x0a,
	    0x10, 0x68, 0x0d,
	    0x13, 0x7d, 0x0f,
	    0x16, 0x92, 0x12,
	    0x19, 0xa5, 0x14,
	    0x1c, 0xb9, 0x17,
	    0x1e, 0xc9, 0x19,
	    0x21, 0xd9, 0x1b,
	    0x47, 0xe4, 0x2d,
	    0x6e, 0xf0, 0x40,
	    0x78, 0xf7, 0x4d,
	    0x83, 0xff, 0x5b,
	    0x9a, 0xff, 0x7a,
	    0xb2, 0xff, 0x9a,
	    0xb2, 0xff, 0x9a,
	
	/* Green */
	    0x04, 0x41, 0x0b,
	    0x05, 0x53, 0x0e,
	    0x06, 0x66, 0x11,
	    0x07, 0x77, 0x14,
	    0x08, 0x88, 0x17,
	    0x09, 0x9b, 0x1a,
	    0x0b, 0xaf, 0x1d,
	    0x48, 0xc4, 0x1f,
	    0x86, 0xd9, 0x22,
	    0x8f, 0xe9, 0x24,
	    0x99, 0xf9, 0x27,
	    0xa8, 0xfc, 0x41,
	    0xb7, 0xff, 0x5b,
	    0xc9, 0xff, 0x6e,
	    0xdc, 0xff, 0x81,
	    0xdc, 0xff, 0x81,
	
	/* Yellow Green */
	    0x02, 0x35, 0x0f,
	    0x07, 0x3f, 0x15,
	    0x0c, 0x4a, 0x1c,
	    0x2d, 0x5f, 0x1e,
	    0x4f, 0x74, 0x20,
	    0x59, 0x83, 0x24,
	    0x64, 0x92, 0x28,
	    0x82, 0xa1, 0x2e,
	    0xa1, 0xb0, 0x34,
	    0xa9, 0xc1, 0x3a,
	    0xb2, 0xd2, 0x41,
	    0xc4, 0xd9, 0x45,
	    0xd6, 0xe1, 0x49,
	    0xe4, 0xf0, 0x4e,
	    0xf2, 0xff, 0x53,
	    0xf2, 0xff, 0x53,
	
	/* Orange Green */
	    0x26, 0x30, 0x01,
	    0x24, 0x38, 0x03,
	    0x23, 0x40, 0x05,
	    0x51, 0x54, 0x1b,
	    0x80, 0x69, 0x31,
	    0x97, 0x81, 0x35,
	    0xaf, 0x99, 0x3a,
	    0xc2, 0xa7, 0x3e,
	    0xd5, 0xb5, 0x43,
	    0xdb, 0xc0, 0x3d,
	    0xe1, 0xcb, 0x38,
	    0xe2, 0xd8, 0x36,
	    0xe3, 0xe5, 0x34,
	    0xef, 0xf2, 0x58,
	    0xfb, 0xff, 0x7d,
	    0xfb, 0xff, 0x7d,
	
	/* Light Orange */
	    0x40, 0x1a, 0x02,
	    0x58, 0x1f, 0x05,
	    0x70, 0x24, 0x08,
	    0x8d, 0x3a, 0x13,
	    0xab, 0x51, 0x1f,
	    0xb5, 0x64, 0x27,
	    0xbf, 0x77, 0x30,
	    0xd0, 0x85, 0x3a,
	    0xe1, 0x93, 0x44,
	    0xed, 0xa0, 0x4e,
	    0xf9, 0xad, 0x58,
	    0xfc, 0xb7, 0x5c,
	    0xff, 0xc1, 0x60,
	    0xff, 0xc6, 0x71,
	    0xff, 0xcb, 0x83,
	    0xff, 0xcb, 0x83
	};
	
	static char colortable[] = {
	    0, 0x00,
	    0, 0x01,
	    0, 0x02,
	    0, 0x03,
	    0, 0x04,
	    0, 0x05,
	    0, 0x06,
	    0, 0x07,
	    0, 0x08,
	    0, 0x09,
	    0, 0x0a,
	    0, 0x0b,
	    0, 0x0c,
	    0, 0x0d,
	    0, 0x0e,
	    0, 0x0f,
	
	    0, 0x10,
	    0, 0x11,
	    0, 0x12,
	    0, 0x13,
	    0, 0x14,
	    0, 0x15,
	    0, 0x16,
	    0, 0x17,
	    0, 0x18,
	    0, 0x19,
	    0, 0x1a,
	    0, 0x1b,
	    0, 0x1c,
	    0, 0x1d,
	    0, 0x1e,
	    0, 0x1f,
	
	    0, 0x20,
	    0, 0x21,
	    0, 0x22,
	    0, 0x23,
	    0, 0x24,
	    0, 0x25,
	    0, 0x26,
	    0, 0x27,
	    0, 0x28,
	    0, 0x29,
	    0, 0x2a,
	    0, 0x2b,
	    0, 0x2c,
	    0, 0x2d,
	    0, 0x2e,
	    0, 0x2f,
	
	    0, 0x30,
	    0, 0x31,
	    0, 0x32,
	    0, 0x33,
	    0, 0x34,
	    0, 0x35,
	    0, 0x36,
	    0, 0x37,
	    0, 0x38,
	    0, 0x39,
	    0, 0x3a,
	    0, 0x3b,
	    0, 0x3c,
	    0, 0x3d,
	    0, 0x3e,
	    0, 0x3f,
	
	    0, 0x40,
	    0, 0x41,
	    0, 0x42,
	    0, 0x43,
	    0, 0x44,
	    0, 0x45,
	    0, 0x46,
	    0, 0x47,
	    0, 0x48,
	    0, 0x49,
	    0, 0x4a,
	    0, 0x4b,
	    0, 0x4c,
	    0, 0x4d,
	    0, 0x4e,
	    0, 0x4f,
	
	    0, 0x50,
	    0, 0x51,
	    0, 0x52,
	    0, 0x53,
	    0, 0x54,
	    0, 0x55,
	    0, 0x56,
	    0, 0x57,
	    0, 0x58,
	    0, 0x59,
	    0, 0x5a,
	    0, 0x5b,
	    0, 0x5c,
	    0, 0x5d,
	    0, 0x5e,
	    0, 0x5f,
	
	    0, 0x60,
	    0, 0x61,
	    0, 0x62,
	    0, 0x63,
	    0, 0x64,
	    0, 0x65,
	    0, 0x66,
	    0, 0x67,
	    0, 0x68,
	    0, 0x69,
	    0, 0x6a,
	    0, 0x6b,
	    0, 0x6c,
	    0, 0x6d,
	    0, 0x6e,
	    0, 0x6f,
	
	    0, 0x70,
	    0, 0x71,
	    0, 0x72,
	    0, 0x73,
	    0, 0x74,
	    0, 0x75,
	    0, 0x76,
	    0, 0x77,
	    0, 0x78,
	    0, 0x79,
	    0, 0x7a,
	    0, 0x7b,
	    0, 0x7c,
	    0, 0x7d,
	    0, 0x7e,
	    0, 0x7f,
	
	    0, 0x80,
	    0, 0x81,
	    0, 0x82,
	    0, 0x83,
	    0, 0x84,
	    0, 0x85,
	    0, 0x86,
	    0, 0x87,
	    0, 0x88,
	    0, 0x89,
	    0, 0x8a,
	    0, 0x8b,
	    0, 0x8c,
	    0, 0x8d,
	    0, 0x8e,
	    0, 0x8f,
	
	    0, 0x90,
	    0, 0x91,
	    0, 0x92,
	    0, 0x93,
	    0, 0x94,
	    0, 0x95,
	    0, 0x96,
	    0, 0x97,
	    0, 0x98,
	    0, 0x99,
	    0, 0x9a,
	    0, 0x9b,
	    0, 0x9c,
	    0, 0x9d,
	    0, 0x9e,
	    0, 0x9f,
	
	    0, 0xa0,
	    0, 0xa1,
	    0, 0xa2,
	    0, 0xa3,
	    0, 0xa4,
	    0, 0xa5,
	    0, 0xa6,
	    0, 0xa7,
	    0, 0xa8,
	    0, 0xa9,
	    0, 0xaa,
	    0, 0xab,
	    0, 0xac,
	    0, 0xad,
	    0, 0xae,
	    0, 0xaf,
	
	    0, 0xb0,
	    0, 0xb1,
	    0, 0xb2,
	    0, 0xb3,
	    0, 0xb4,
	    0, 0xb5,
	    0, 0xb6,
	    0, 0xb7,
	    0, 0xb8,
	    0, 0xb9,
	    0, 0xba,
	    0, 0xbb,
	    0, 0xbc,
	    0, 0xbd,
	    0, 0xbe,
	    0, 0xbf,
	
	    0, 0xc0,
	    0, 0xc1,
	    0, 0xc2,
	    0, 0xc3,
	    0, 0xc4,
	    0, 0xc5,
	    0, 0xc6,
	    0, 0xc7,
	    0, 0xc8,
	    0, 0xc9,
	    0, 0xca,
	    0, 0xcb,
	    0, 0xcc,
	    0, 0xcd,
	    0, 0xce,
	    0, 0xcf,
	
	    0, 0xd0,
	    0, 0xd1,
	    0, 0xd2,
	    0, 0xd3,
	    0, 0xd4,
	    0, 0xd5,
	    0, 0xd6,
	    0, 0xd7,
	    0, 0xd8,
	    0, 0xd9,
	    0, 0xda,
	    0, 0xdb,
	    0, 0xdc,
	    0, 0xdd,
	    0, 0xde,
	    0, 0xdf,
	
	    0, 0xe0,
	    0, 0xe1,
	    0, 0xe2,
	    0, 0xe3,
	    0, 0xe4,
	    0, 0xe5,
	    0, 0xe6,
	    0, 0xe7,
	    0, 0xe8,
	    0, 0xe9,
	    0, 0xea,
	    0, 0xeb,
	    0, 0xec,
	    0, 0xed,
	    0, 0xee,
	    0, 0xef,
	
	    0, 0xf0,
	    0, 0xf1,
	    0, 0xf2,
	    0, 0xf3,
	    0, 0xf4,
	    0, 0xf5,
	    0, 0xf6,
	    0, 0xf7,
	    0, 0xf8,
	    0, 0xf9,
	    0, 0xfa,
	    0, 0xfb,
	    0, 0xfc,
	    0, 0xfd,
	    0, 0xfe,
	    0, 0xff
	};
	
	/* Initialise the palette */
	static VhConvertColorPromPtr a2600_init_palette = new VhConvertColorPromPtr() {
            public void handler(char[] sys_palette, char[] sys_colortable, UBytePtr color_prom) {
                memcpy(sys_palette, palette, palette.length);
                memcpy(sys_colortable, colortable, colortable.length);
            }
        };
	
	static TIAinterface tia_interface = new TIAinterface
        (
	    31400,
	    255,
	    TIA_DEFAULT_GAIN
        );
	
	//#ifdef USE_SCANLINE_WSYNC
	//extern #endif
	static MachineDriver machine_driver_a2600 = new MachineDriver
	(
	    /* basic machine hardware */
	    new MachineCPU[] {
	        new MachineCPU(
	            CPU_M6502,
	            3584160/3,					/* 1.19Mhz */
	            readmem, writemem, null, null,
	//#ifndef USE_SCANLINE_WSYNC
	//            null, 0                        /* for screen updates per scanline */
	//#else
	            a2600_scanline_int, 262     /* for screen updates per scanline */
	//#endif
	        )
	    },
	    60, DEFAULT_60HZ_VBLANK_DURATION,
	    1,
	    a2600_init_machine,                 /* init_machine */
	    //a2600_stop_machine,                 /* stop_machine */
	
	    /* video hardware */
	    228, 262,
	    new rectangle(68, 227, 40, 261),
	    null,
	    palette.length / 3,
	    colortable.length,
	    a2600_init_palette,
	
	    VIDEO_TYPE_RASTER | VIDEO_SUPPORTS_DIRTY,
	    null,
	    a2600_vh_start,
	    a2600_vh_stop,
	    a2600_vh_screenrefresh,
	
	    /* sound hardware */
	    0, 0, 0, 0,
	    /*TODO*///new MachineSound[] {
	    /*TODO*///    new MachineSound(
	    /*TODO*///        SOUND_TIA,
	    /*TODO*///        tia_interface
	    /*TODO*///    )
	
	    /*TODO*///}
            null
	
	);
	
	
	/***************************************************************************
	
	  Game driver
	
	***************************************************************************/
	
	public static InitDriverPtr init_a2600 = new InitDriverPtr() { public void handler() 
	{
	} };
	
	static RomLoadPtr rom_a2600 = new RomLoadPtr(){ public void handler(){ 
	    ROM_REGION(0x20000, REGION_CPU1,0);       /* 6502 memory */
	ROM_END(); }}; 
	
	static IODevice io_a2600[] =
	{
	    new IODevice(
	        IO_CARTSLOT,                    /* type */
	        1,                              /* count */
	        "bin\0",                        /* file extensions */
	        IO_RESET_ALL,                   /* reset if file changed */
	        null,
	        a2600_load_rom,                 /* init */
	        null,				/* exit */
                null,				/* info */
                null,				/* open */
                null,				/* close */
                null,				/* status */
                null,				/* seek */
                null,                           /* tell */
                null,				/* input */
                null,				/* output */
                null,				/* input_chunk */
                null				/* output_chunk */
	    ),
	    new IODevice(IO_END)
	};
	
	/*    YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
	//CONSX(19??, a2600,    0,        a2600,    a2600,    a2600,    "Atari",  "Atari 2600", GAME_NOT_WORKING)
        public static GameDriver driver_a2600 = new GameDriver("19??", "a2600", "a2600.java", rom_a2600, null, machine_driver_a2600, input_ports_a2600, null, io_a2600, "Atari", "Atari 2600");
	
	/*TODO*///#ifdef RUNTIME_LOADER
	/*TODO*///extern void vcs_runtime_loader_init(void)
	/*TODO*///{
	/*TODO*///	int i;
	/*TODO*///	for (i=0; drivers[i]; i++) {
	/*TODO*///		if ( strcmp(drivers[i].name,"a2600")==0) drivers[i]=&driver_a2600;
	/*TODO*///	}
	/*TODO*///}
	/*TODO*///#endif
}
