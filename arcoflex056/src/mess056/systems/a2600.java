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
import static mess056.machine.riot.*;
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

            //new Memory_ReadAddress( 0x0280, 0x0297, riot_0_r    ),	/* RIOT reads for a2600 */
            new Memory_ReadAddress( 0x0280, 0x028F, riot_0_r    ),	/* RIOT reads for a2600 */
            new Memory_ReadAddress( 0x0290, 0x0297, riot_0_r    ),	/* RIOT reads for a2600 */

            new Memory_ReadAddress( 0x0300, 0x033F, a2600_TIA_r ),
            new Memory_ReadAddress( 0x0340, 0x037F, a2600_TIA_r ),

            //new Memory_ReadAddress( 0x0380, 0x0397, riot_0_r    ),	/* RIOT reads for a2600 */
            new Memory_ReadAddress( 0x0380, 0x038F, riot_0_r    ),	/* RIOT reads for a2600 */
            new Memory_ReadAddress( 0x0390, 0x0397, riot_0_r    ),	/* RIOT reads for a2600 */

            new Memory_ReadAddress( 0x1000, 0x17FF, MRA_ROM     ),
            new Memory_ReadAddress( 0x1800, 0x1FFF, MRA_ROM     ),	/* ROM mirror for 2k images */
            new Memory_ReadAddress( 0xF000, 0xF7FF, MRA_ROM     ),
            new Memory_ReadAddress( 0xF800, 0xFFFF, MRA_ROM     ),	/* ROM mirror for 2k images */
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
            //new Memory_WriteAddress( 0x0280, 0x0297, riot_0_w    ),	/* RIOT writes for a2600 */
            new Memory_WriteAddress( 0x0280, 0x028F, riot_0_w    ),	/* RIOT writes for a2600 */
            new Memory_WriteAddress( 0x0290, 0x0297, riot_0_w    ),	/* RIOT writes for a2600 */

            new Memory_WriteAddress( 0x0300, 0x033F, a2600_TIA_w ),
            new Memory_WriteAddress( 0x0340, 0x037F, a2600_TIA_w ),
            //new Memory_WriteAddress( 0x0380, 0x0397, riot_0_w    ),	/* RIOT writes for a2600 */
            new Memory_WriteAddress( 0x0380, 0x038F, riot_0_w    ),	/* RIOT writes for a2600 */
            new Memory_WriteAddress( 0x0390, 0x0397, riot_0_w    ),	/* RIOT writes for a2600 */

            new Memory_WriteAddress( 0x1000, 0x17FF, MWA_ROM  ),
            new Memory_WriteAddress( 0x1800, 0x1FFF, MWA_ROM  ),	/* ROM mirror for 2k images */
            new Memory_WriteAddress( 0xF000, 0xF7FF, MWA_ROM  ),
            new Memory_WriteAddress( 0xF800, 0xFFFF, MWA_ROM  ),	/* ROM mirror for 2k images */
            new Memory_WriteAddress(MEMPORT_MARKER, 0) /* end of table */
        };
	
	
	static InputPortPtr input_ports_a2600 = new InputPortPtr(){ public void handler() { 
	
	
	    PORT_START();      /* IN0 DONE!*/
            PORT_BIT ( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_PLAYER2 );
            PORT_BIT ( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_PLAYER2 );
            PORT_BIT ( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
            PORT_BIT ( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
            PORT_BIT ( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP);
            PORT_BIT ( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN);
            PORT_BIT ( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT);
            PORT_BIT ( 0x80, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);

            PORT_START();      /* IN1 */
            PORT_BIT ( 0x01, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_PLAYER2 );
            PORT_BIT ( 0x02, IP_ACTIVE_HIGH, IPT_BUTTON1 );
            PORT_BIT ( 0x04, IP_ACTIVE_HIGH, IPT_BUTTON2 | IPF_PLAYER2 );
            PORT_BIT ( 0x08, IP_ACTIVE_HIGH, IPT_BUTTON2 );
            PORT_BIT ( 0xF0, IP_ACTIVE_LOW, IPT_UNUSED );

            PORT_START();      /* IN2 */
            PORT_BIT (0x7F, IP_ACTIVE_LOW, IPT_UNUSED);
            //PORT_BIT (0x80, IP_ACTIVE_HIGH, IPT_VBLANK)

            PORT_START();      /* IN3 */
            PORT_BITX( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN, "Reset", KEYCODE_R, IP_JOY_DEFAULT);
            PORT_BITX( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN, "Start", KEYCODE_S, IP_JOY_DEFAULT);
            PORT_BIT ( 0xFC, IP_ACTIVE_LOW, IPT_UNUSED);
	
	INPUT_PORTS_END(); }}; 
	
	
	static char palette[] =
	{
	    0x00, 0x00, 0x00, /* Transparent? */
            0x00, 0x00, 0x00, /* Black */
            0x20, 0xC0, 0x20, /* Medium Green */
            0x60, 0xE0, 0x60, /* Light Green */
            0x20, 0x20, 0xE0, /* Dark Blue */
            0x40, 0x60, 0xE0, /* Light Blue */
            0xA0, 0x20, 0x20, /* Dark Red */
            0x40, 0xC0, 0xE0, /* Cyan */
            0xE0, 0x20, 0x20, /* Medium Red */
            0xE0, 0x60, 0x60, /* Light Red */
            0xC0, 0xC0, 0x20, /* Dark Yellow */
            0xC0, 0xC0, 0x80, /* Light Yellow */
            0x20, 0x80, 0x20, /* Dark Green */
            0xC0, 0x40, 0xA0, /* Magenta */
            0xA0, 0xA0, 0xA0, /* Gray */
            0xE0, 0xE0, 0xE0, /* White */
	};
	
	static char colortable[] = {
	    0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15
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
        
        static GfxLayout pixel4_width_1 = new GfxLayout
        (
                4,1,					/* 4 x 1 pixels (PF0) */
                16, 					/* 16 codes */
                1,                      /* 1 bits per pixel */
                new int[]{ 0 },                  /* no bitplanes; 1 bit per pixel */
                /* x offsets */
                new int[]{ 0, 1, 2, 3 },
                /* y offsets */
                new int[]{ 0 },
                8*1 					/* every code takes 1 byte */
        );

        static GfxLayout pixel4_width_2 = new GfxLayout
        (
                2*4,1,					/* 2*4 x 1 pixels (PF0) */
                16, 					/* 16 codes */
                1,                      /* 1 bits per pixel */
                new int[]{ 0 },                  /* no bitplanes; 1 bit per pixel */
                /* x offsets */
                new int[]{ 0, 0, 1, 1, 2, 2, 3, 3 },
                /* y offsets */
                new int[]{ 0 },
                8*1 					/* every code takes 1 byte */
        );

        static GfxLayout pixel8_width_1 = new GfxLayout
        (
                8,1,					/* 8 x 1 pixels (PF0) */
                256,					/* 256 codes */
                1,                      /* 1 bits per pixel */
                new int[]{ 0 },                  /* no bitplanes; 1 bit per pixel */
                /* x offsets */
                new int[]{ 7, 6, 5, 4, 3, 2, 1, 0 },
                /* y offsets */
                new int[]{ 0 },
                8*1 					/* every code takes 1 byte */
        );

        static GfxLayout pixel8_width_2 = new GfxLayout
        (
                2*8,1,					/* 2*8 x 1 pixels (PF0) */
                256,					/* 256 codes */
                1,                      /* 1 bits per pixel */
                new int[]{ 0 },                  /* no bitplanes; 1 bit per pixel */
                /* x offsets */
                new int[]{ 7,7, 6,6, 5,5, 4,4, 3,3, 2,2, 1,1, 0,0 },
                /* y offsets */
                new int[]{ 0 },
                8*1 					/* every code takes 1 byte */
        );

        static GfxDecodeInfo gfxdecodeinfo[] =
        {
            new GfxDecodeInfo(REGION_GFX1, 0x0000, pixel4_width_1, 0, 16),
            new GfxDecodeInfo(REGION_GFX1, 0x0000, pixel8_width_1, 0, 16),
            new GfxDecodeInfo(REGION_GFX1, 0x0000, pixel4_width_2, 0, 16),
            new GfxDecodeInfo(REGION_GFX1, 0x0000, pixel8_width_2, 0, 16),
                
            new GfxDecodeInfo( -1 )
        };
	
	//#ifdef USE_SCANLINE_WSYNC
	//extern #endif
	static MachineDriver machine_driver_a2600 = new MachineDriver
	(
	    /* basic machine hardware */
	    new MachineCPU[] {
	        new MachineCPU(
	            CPU_M6502,
	            1190000,        /* 1.19Mhz */
                    readmem,writemem,null,null,
                    a2600_scanline_interrupt,262  /* for screen updates per scanline */
                    //null, 0
	        )
	    },
	    60, DEFAULT_60HZ_VBLANK_DURATION,
            1,
            a2600_init_machine, /* init_machine */
            a2600_stop_machine, /* stop_machine */

            /* video hardware */
            228,262, new rectangle(68,227,37,231),
            gfxdecodeinfo,
            palette.length / 3,
            colortable.length,
            a2600_init_palette,

            VIDEO_TYPE_RASTER,
            null,
            a2600_vh_start,
            a2600_vh_stop,
            a2600_vh_screenrefresh,

            /* sound hardware */
            0,0,0,0,
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
            UBytePtr gfx = new UBytePtr(memory_region(REGION_GFX1));
            int i;
            for( i = 0; i < 256; i++ )
                    gfx.write(i, i);
	} };
	
	static RomLoadPtr rom_a2600 = new RomLoadPtr(){ public void handler(){ 
	    ROM_REGION( 0x20000, REGION_CPU1, 0 ); /* 6502 memory */
            ROM_REGION( 0x00100, REGION_GFX1, 0 ); /* memory for bit patterns */
            //ROM_REGION(0x20000, REGION_CPU1,0);       /* 6502 memory */
	ROM_END(); }}; 
	
	static IODevice io_a2600[] =
	{
	    new IODevice(
	        IO_CARTSLOT,                    /* type */
	        1,                              /* count */
	        "bin\0",                        /* file extensions */
	        IO_RESET_ALL,                   /* reset if file changed */
	        null,//a2600_id_rom,                   /* id */
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
