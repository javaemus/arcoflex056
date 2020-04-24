/***************************************************************************

Eggs & Dommy

Very similar to Burger Time hardware (and uses its video driver)

driver by Nicola Salmoria

To Do:
Sprite Priorities in Dommy

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.drivers;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;

import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.commonH.REGION_CPU1;
import static mame056.commonH.REGION_GFX1;
import static mame056.commonH.REGION_PROMS;
import static mame056.commonH.ROMREGION_DISPOSE;
import static mame056.commonH.ROM_CONTINUE;
import static mame056.commonH.ROM_END;
import static mame056.commonH.ROM_LOAD;
import static mame056.commonH.ROM_REGION;
import static mame056.commonH.ROM_RELOAD;
import static mame056.cpuexec.*;
import static mame056.cpuexec.interrupt;
import static mame056.cpuintrfH.CPU_M6502;
import static mame056.driverH.ROT270;
import static mame056.driverH.VIDEO_TYPE_RASTER;
import static mame056.inptport.input_port_0_r;
import static mame056.inptport.input_port_1_r;
import static mame056.inptport.input_port_2_r;
import static mame056.inptport.input_port_3_r;
import static mame056.inptportH.*;
import static mame056.inptport.*;
import static mame056.cpuexecH.*;
import static mame056.cpuexec.*;
import static mame056.cpuintrfH.*;
import static mame056.driverH.*;
import static mame056.inptportH.DEF_STR;
import static mame056.inptportH.INPUT_PORTS_END;
import static mame056.inptportH.IPF_4WAY;
import static mame056.inptportH.IPF_COCKTAIL;
import static mame056.inptportH.IPT_BUTTON1;
import static mame056.inptportH.IPT_COIN1;
import static mame056.inptportH.IPT_COIN2;
import static mame056.inptportH.IPT_JOYSTICK_DOWN;
import static mame056.inptportH.IPT_JOYSTICK_LEFT;
import static mame056.inptportH.IPT_JOYSTICK_RIGHT;
import static mame056.inptportH.IPT_JOYSTICK_UP;
import static mame056.inptportH.IPT_START1;
import static mame056.inptportH.IPT_START2;
import static mame056.inptportH.IPT_UNKNOWN;
import static mame056.inptportH.IPT_VBLANK;
import static mame056.inptportH.IP_ACTIVE_HIGH;
import static mame056.inptportH.IP_ACTIVE_LOW;
import static mame056.inptportH.PORT_BIT;
import static mame056.inptportH.PORT_DIPNAME;
import static mame056.inptportH.PORT_DIPSETTING;
import static mame056.inptportH.PORT_START;
import static mame056.memoryH.*;
import static mame056.memory.*;
import static mame056.memoryH.MEMPORT_DIRECTION_READ;
import static mame056.memoryH.MEMPORT_DIRECTION_WRITE;
import static mame056.memoryH.MEMPORT_MARKER;
import static mame056.memoryH.MEMPORT_TYPE_MEM;
import static mame056.memoryH.MEMPORT_WIDTH_8;
import static mame056.memoryH.MRA_RAM;
import static mame056.memoryH.MRA_ROM;
import static mame056.memoryH.MWA_NOP;
import static mame056.memoryH.MWA_RAM;
import static mame056.memoryH.MWA_ROM;
import static mame056.sndintrfH.SOUND_AY8910;
import static mame056.sound.ay8910.AY8910_control_port_0_w;
import static mame056.sound.ay8910.AY8910_control_port_1_w;
import static mame056.sound.ay8910.AY8910_write_port_0_w;
import static mame056.sound.ay8910.AY8910_write_port_1_w;
import static mame056.timer.*;
import static mame056.timerH.*;
import static mame056.inptport.*;
import static mame056.drawgfxH.*;
import static mame056.palette.*;
import static mame056.inputH.*;
import static mame056.sndintrfH.*;
import static mame056.sndintrf.*;

import static arcadeflex056.osdepend.logerror;

import static mame056.vidhrdw.btime.btime_mirrorcolorram_r;
import static mame056.vidhrdw.btime.btime_mirrorcolorram_w;
import static mame056.vidhrdw.btime.btime_mirrorvideoram_r;
import static mame056.vidhrdw.btime.btime_mirrorvideoram_w;
import static mame056.vidhrdw.btime.btime_vh_convert_color_prom;
import static mame056.vidhrdw.btime.btime_vh_start;
import static mame056.vidhrdw.btime.btime_video_control_w;
import static mame056.vidhrdw.btime.eggs_vh_screenrefresh;
import static mame056.vidhrdw.generic.*;

import static arcadeflex056.fileio.*;
import static mame056.palette.game_palette;
import static mame056.inptport.*;

import static mame056.vidhrdw.btime.*;
import static common.libc.cstring.*;
import static mame056.sound.ay8910.*;
import static mame056.sound.ay8910H.*;
import static mame056.vidhrdw.generic.colorram;
import static mame056.vidhrdw.generic.colorram_w;
import static mame056.vidhrdw.generic.generic_vh_stop;
import static mame056.vidhrdw.generic.videoram;
import static mame056.vidhrdw.generic.videoram_size;
import static mame056.vidhrdw.generic.videoram_w;

public class scregg
{
	
	public static Memory_ReadAddress dommy_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2000, 0x27ff, MRA_RAM ),
		new Memory_ReadAddress( 0x2800, 0x2bff, btime_mirrorvideoram_r ),
		new Memory_ReadAddress( 0x4000, 0x4000, input_port_2_r ),     /* DSW1 */
		new Memory_ReadAddress( 0x4001, 0x4001, input_port_3_r ),     /* DSW2 */
	/*	new Memory_ReadAddress( 0x4004, 0x4004, ),  */ /* this is read */
		new Memory_ReadAddress( 0x4002, 0x4002, input_port_0_r ),     /* IN0 */
		new Memory_ReadAddress( 0x4003, 0x4003, input_port_1_r ),     /* IN1 */
		new Memory_ReadAddress( 0xa000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress dommy_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_RAM ),
		new Memory_WriteAddress( 0x2000, 0x23ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x2400, 0x27ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x2800, 0x2bff, btime_mirrorvideoram_w ),
		new Memory_WriteAddress( 0x4000, 0x4000, MWA_NOP ),
		new Memory_WriteAddress( 0x4001, 0x4001, btime_video_control_w ),
		new Memory_WriteAddress( 0x4004, 0x4004, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x4005, 0x4005, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x4006, 0x4006, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0x4007, 0x4007, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0xa000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress eggs_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1000, 0x17ff, MRA_RAM ),
		new Memory_ReadAddress( 0x1800, 0x1bff, btime_mirrorvideoram_r ),
		new Memory_ReadAddress( 0x1c00, 0x1fff, btime_mirrorcolorram_r ),
		new Memory_ReadAddress( 0x2000, 0x2000, input_port_2_r ),     /* DSW1 */
		new Memory_ReadAddress( 0x2001, 0x2001, input_port_3_r ),     /* DSW2 */
		new Memory_ReadAddress( 0x2002, 0x2002, input_port_0_r ),     /* IN0 */
		new Memory_ReadAddress( 0x2003, 0x2003, input_port_1_r ),     /* IN1 */
		new Memory_ReadAddress( 0x3000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xffff, MRA_ROM ),    /* reset/interrupt vectors */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress eggs_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, MWA_RAM ),
		new Memory_WriteAddress( 0x1000, 0x13ff, videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0x1400, 0x17ff, colorram_w, colorram ),
		new Memory_WriteAddress( 0x1800, 0x1bff, btime_mirrorvideoram_w ),
		new Memory_WriteAddress( 0x1c00, 0x1fff, btime_mirrorcolorram_w ),
		new Memory_WriteAddress( 0x2000, 0x2000, btime_video_control_w ),
		new Memory_WriteAddress( 0x2001, 0x2001, MWA_NOP ),
		new Memory_WriteAddress( 0x2004, 0x2004, AY8910_control_port_0_w ),
		new Memory_WriteAddress( 0x2005, 0x2005, AY8910_write_port_0_w ),
		new Memory_WriteAddress( 0x2006, 0x2006, AY8910_control_port_1_w ),
		new Memory_WriteAddress( 0x2007, 0x2007, AY8910_write_port_1_w ),
		new Memory_WriteAddress( 0x3000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_scregg = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START();       /* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Cocktail") );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_VBLANK  );
	
		PORT_START();       /* DSW2 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "5" );
		PORT_DIPNAME( 0x06, 0x04, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x04, "30000" );
		PORT_DIPSETTING(    0x02, "50000" );
		PORT_DIPSETTING(    0x00, "70000"  );
		PORT_DIPSETTING(    0x06, "Never"  );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unused") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x80, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
	INPUT_PORTS_END(); }}; 
	
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,    /* 8*8 characters */
		1024,   /* 1024 characters */
		3,      /* 3 bits per pixel */
		new int[] { 2*1024*8*8, 1024*8*8, 0 },    /* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8     /* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout spritelayout = new GfxLayout
	(
		16,16,  /* 16*16 sprites */
		256,    /* 256 sprites */
		3,      /* 3 bits per pixel */
		new int[] { 2*256*16*16, 256*16*16, 0 },  /* the bitplanes are separated */
		new int[] { 16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7,
		  0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
		  8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8    /* every sprite takes 32 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,          0, 1 ),     /* char set #1 */
		new GfxDecodeInfo( REGION_GFX1, 0, spritelayout,        0, 1 ),     /* sprites */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,      /* 2 chips */
		1500000,        /* 1.5 MHz ? (hand tuned) */
		new int[] { 23, 23 },
		new ReadHandlerPtr[] { null, null },
		new ReadHandlerPtr[] { null, null },
		new WriteHandlerPtr[] { null, null },
		new WriteHandlerPtr[] { null, null }
	);
	
	
	static MachineDriver machine_driver_dommy = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				1500000,
				dommy_readmem,dommy_writemem,null,null,
				interrupt,16
			)
		},
		57, 3072,        /* frames per second, vblank duration taken from Burger Time */
		1,      /* single CPU, no need from interleaving  */
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 31*8-1, 1*8, 31*8-1 ),
		gfxdecodeinfo,
		8, 8,
		btime_vh_convert_color_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		btime_vh_start,
		generic_vh_stop,
		eggs_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_AY8910,
				ay8910_interface
			)
		}
	);
	
	static MachineDriver machine_driver_scregg = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M6502,
				1500000,
				eggs_readmem,eggs_writemem,null,null,
				interrupt,16
			)
		},
		57, 3072,        /* frames per second, vblank duration taken from Burger Time */
		1,      /* single CPU, no need from interleaving  */
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 1*8, 32*8-1, 1*8, 32*8-1 ),
		gfxdecodeinfo,
		8, 8,
		btime_vh_convert_color_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		btime_vh_start,
		generic_vh_stop,
		eggs_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_AY8910,
				ay8910_interface
			)
		}
	);
	
	static RomLoadPtr rom_dommy = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "dommy.e01",  0xa000, 0x2000, 0x9ae064ed );
		ROM_LOAD( "dommy.e11",  0xc000, 0x2000, 0x7c4fad5c );
		ROM_LOAD( "dommy.e21",  0xe000, 0x2000, 0xcd1a4d55 );
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "dommy.e50",  0x0000, 0x2000, 0x5e9db0a4 );
		ROM_LOAD( "dommy.e40",  0x2000, 0x2000, 0x4d1c36fb );
		ROM_LOAD( "dommy.e30",  0x4000, 0x2000, 0x4e68bb12 );
	
		ROM_REGION( 0x0040, REGION_PROMS, 0 );/* palette decoding is probably wrong */
		ROM_LOAD( "dommy.e70",  0x0018, 0x0008, 0x50c1d86e );/* palette */
		ROM_CONTINUE(			  0x0000, 0x0018 );
		ROM_LOAD( "dommy.e60",  0x0020, 0x0020, 0x24da2b63 );/* unknown */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_scregg = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "scregg.e14",   0x3000, 0x1000, 0x29226d77 );
		ROM_LOAD( "scregg.d14",   0x4000, 0x1000, 0xeb143880 );
		ROM_LOAD( "scregg.c14",   0x5000, 0x1000, 0x4455f262 );
		ROM_LOAD( "scregg.b14",   0x6000, 0x1000, 0x044ac5d2 );
		ROM_LOAD( "scregg.a14",   0x7000, 0x1000, 0xb5a0814a );
		ROM_RELOAD(               0xf000, 0x1000 );       /* for reset/interrupt vectors */
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "scregg.j12",   0x0000, 0x1000, 0xa485c10c );
		ROM_LOAD( "scregg.j10",   0x1000, 0x1000, 0x1fd4e539 );
		ROM_LOAD( "scregg.h12",   0x2000, 0x1000, 0x8454f4b2 );
		ROM_LOAD( "scregg.h10",   0x3000, 0x1000, 0x72bd89ee );
		ROM_LOAD( "scregg.g12",   0x4000, 0x1000, 0xff3c2894 );
		ROM_LOAD( "scregg.g10",   0x5000, 0x1000, 0x9c20214a );
	
		ROM_REGION( 0x0040, REGION_PROMS, 0 );
		ROM_LOAD( "screggco.c6",  0x0000, 0x0020, 0xff23bdd6 );/* palette */
		ROM_LOAD( "screggco.b4",  0x0020, 0x0020, 0x7cc4824b );/* unknown */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_eggs = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "e14.bin",      0x3000, 0x1000, 0x4e216f9d );
		ROM_LOAD( "d14.bin",      0x4000, 0x1000, 0x4edb267f );
		ROM_LOAD( "c14.bin",      0x5000, 0x1000, 0x15a5c48c );
		ROM_LOAD( "b14.bin",      0x6000, 0x1000, 0x5c11c00e );
		ROM_LOAD( "a14.bin",      0x7000, 0x1000, 0x953faf07 );
		ROM_RELOAD(               0xf000, 0x1000 );  /* for reset/interrupt vectors */
	
		ROM_REGION( 0x6000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "j12.bin",      0x0000, 0x1000, 0xce4a2e46 );
		ROM_LOAD( "j10.bin",      0x1000, 0x1000, 0xa1bcaffc );
		ROM_LOAD( "h12.bin",      0x2000, 0x1000, 0x9562836d );
		ROM_LOAD( "h10.bin",      0x3000, 0x1000, 0x3cfb3a8e );
		ROM_LOAD( "g12.bin",      0x4000, 0x1000, 0x679f8af7 );
		ROM_LOAD( "g10.bin",      0x5000, 0x1000, 0x5b58d3b5 );
	
		ROM_REGION( 0x0040, REGION_PROMS, 0 );
		ROM_LOAD( "eggs.c6",      0x0000, 0x0020, 0xe8408c81 );/* palette */
		ROM_LOAD( "screggco.b4",  0x0020, 0x0020, 0x7cc4824b );/* unknown */
	ROM_END(); }}; 
	
	
	public static GameDriver driver_dommy	   = new GameDriver("198?"	,"dommy"	,"scregg.java"	,rom_dommy,null	,machine_driver_dommy	,input_ports_scregg	,null	,ROT270	,	"Technos", "Dommy" );
	public static GameDriver driver_scregg	   = new GameDriver("1983"	,"scregg"	,"scregg.java"	,rom_scregg,null	,machine_driver_scregg	,input_ports_scregg	,null	,ROT270	,	"Technos", "Scrambled Egg" );
	public static GameDriver driver_eggs	   = new GameDriver("1983"	,"eggs"	,"scregg.java"	,rom_eggs,driver_scregg	,machine_driver_scregg	,input_ports_scregg	,null	,ROT270	,	"[Technos] Universal USA", "Eggs" );
}