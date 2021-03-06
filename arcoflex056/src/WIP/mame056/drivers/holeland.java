/***************************************************************************

Hole Land

driver by Mathis Rosenhauer

TODO:
- tile/sprite priority in holeland
- missing high bit of sprite X coordinate? (see round 2 and 3 of attract mode
  in crzrally)

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.drivers;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static mame056.commonH.*;
import static mame056.common.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrfH.*;
import static mame056.memory.*;
import static mame056.sound.mixerH.*;
import static mame056.drawgfxH.*;
import static mame056.driverH.*;
import static mame056.inptport.*;
import static mame056.inptportH.*;
import static mame056.memoryH.*;
import static mame056.palette.*;
import static mame056.sndintrf.*;
import static mame056.sndintrfH.*;
import static mame056.timer.*;
import static mame056.timerH.*;
import static WIP.mame056.vidhrdw.holeland.*;
import static mame056.sound.ay8910.*;
import static mame056.sound.ay8910H.*;
import static arcadeflex056.osdepend.logerror;
import static mame056.machine._6812pia.*;
import static mame056.machine._6812pia.*;
import static mame056.machine._6812piaH.*;
import static mame056.machine._8255ppiH.*;
import static mame056.machine._8255ppi.*;

import static mame056.sound._2203intf.*;
import static mame056.sound._2203intfH.*;
import static mame056.sound._3526intf.*;
import static mame056.sound._3812intfH.*;
import static WIP.mame056.sound._5220intf.*;
import static WIP.mame056.sound._5220intfH.*;
import static mame056.mame.Machine;
import static mame056.sound.sn76496.*;
import static mame056.sound.sn76496H.*;
import static mame056.sound.dac.*;
import static mame056.sound.dacH.*;

import static mame056.sound.MSM5205.*;
import static mame056.sound.MSM5205H.*;

import static mame056.tilemapC.*;
import static mame056.tilemapH.*;

import static mame056.vidhrdw.generic.*;

public class holeland
{
	
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf3ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0xa000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc001, holeland_pal_offs_w ),
		new Memory_WriteAddress( 0xc006, 0xc007, holeland_flipscreen_w ),
		new Memory_WriteAddress( 0xe000, 0xe3ff, holeland_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe400, 0xe7ff, holeland_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xf000, 0xf3ff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress crzrally_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xc7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xebff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress crzrally_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xc7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe000, 0xe3ff, holeland_colorram_w, colorram ),
		new Memory_WriteAddress( 0xe400, 0xe7ff, holeland_videoram_w, videoram, videoram_size ),
		new Memory_WriteAddress( 0xe800, 0xebff, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0xf000, 0xf000, holeland_scroll_w ),
		new Memory_WriteAddress( 0xf800, 0xf801, holeland_pal_offs_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x01, 0x01, watchdog_reset_r ),	/* ? */
		new IO_ReadPort( 0x04, 0x04, AY8910_read_port_0_r ),
		new IO_ReadPort( 0x06, 0x06, AY8910_read_port_1_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x04, 0x04, AY8910_control_port_0_w ),
		new IO_WritePort( 0x05, 0x05, AY8910_write_port_0_w ),
		new IO_WritePort( 0x06, 0x06, AY8910_control_port_1_w ),
		new IO_WritePort( 0x07, 0x07, AY8910_write_port_1_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_holeland = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_SERVICE( 0x01, IP_ACTIVE_HIGH );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x00, "Language" );
		PORT_DIPSETTING(    0x00, "English" );
		PORT_DIPSETTING(    0x04, "Nihongo" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "60000" );
		PORT_DIPSETTING(    0x20, "90000" );
		PORT_DIPNAME( 0x40, 0x00, "Fase 3 Difficulty");
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x40, "Hard" );
		PORT_DIPNAME( 0x80, 0x80, "Coin Mode" );
		PORT_DIPSETTING(    0x80, "A" );
		PORT_DIPSETTING(    0x00, "B" );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, "Coin Case" );
		PORT_DIPSETTING(    0x00, "1" );
		PORT_DIPSETTING(    0x01, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x03, "4" );
		PORT_DIPNAME( 0x0c, 0x04, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0x04, "3" );
		PORT_DIPSETTING(    0x08, "4" );
		PORT_DIPSETTING(    0x0c, "5" );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Very Easy" );
		PORT_DIPSETTING(    0x10, "Easy" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x30, "Very Hard" );
		PORT_DIPNAME( 0x40, 0x40, "Monsters" );
		PORT_DIPSETTING(    0x40, "Min" );
		PORT_DIPSETTING(    0x00, "Max" );
		PORT_DIPNAME( 0x80, 0x80, "Mode" );/* seems to have no effect */
		PORT_DIPSETTING(    0x00, "Stop" );
		PORT_DIPSETTING(    0x80, "Play" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_crzrally = new InputPortPtr(){ public void handler() { 
		PORT_START();       /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START();       /* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_SERVICE( 0x01, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x0c, 0x00, "Drive" );
		PORT_DIPSETTING(    0x00, "A" );
		PORT_DIPSETTING(    0x04, "B" );
		PORT_DIPSETTING(    0x08, "C" );
		PORT_DIPSETTING(    0x0c, "D" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Free_Play") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x60, 0x60, "Extra Time" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPSETTING(    0x20, "5 Sec" );
		PORT_DIPSETTING(    0x40, "10 Sec" );
		PORT_DIPSETTING(    0x60, "15 Sec" );
		PORT_DIPNAME( 0x80, 0x00, "Coin Mode" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Cocktail") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x03, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easy" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPNAME( 0x40, 0x40, "Controller" );
		PORT_DIPSETTING(    0x40, "Joystick" );
		PORT_DIPSETTING(    0x00, "Wheel" );
		PORT_DIPNAME( 0x80, 0x80, "Mode" );/* seems to have no effect */
		PORT_DIPSETTING(    0x00, "Stop" );
		PORT_DIPSETTING(    0x80, "Play" );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout holeland_charlayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		2,
		new int[] { 4, 0 },
		new int[] { 0,0, 1,1, 2,2, 3,3, 8+0,8+0, 8+1,8+1, 8+2,8+2, 8+3,8+3 },
		new int[] { 0*16,0*16, 1*16,1*16, 2*16,2*16, 3*16,3*16, 4*16,4*16, 5*16,5*16, 6*16,6*16, 7*16,7*16 },
		8*16
	);
	
	static GfxLayout crzrally_charlayout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		8*16
	);
	
	static GfxLayout holeland_spritelayout = new GfxLayout
	(
		32,32,
		RGN_FRAC(1,4),
		2,
		new int[] { 4, 0 },
		new int[] { 0, 2, 1, 3, 1*8+0, 1*8+2, 1*8+1, 1*8+3, 2*8+0, 2*8+2, 2*8+1, 2*8+3, 3*8+0, 3*8+2, 3*8+1, 3*8+3,
				4*8+0, 4*8+2, 4*8+1, 4*8+3, 5*8+0, 5*8+2, 5*8+1, 5*8+3, 6*8+0, 6*8+2, 6*8+1, 6*8+3, 7*8+0, 7*8+2, 7*8+1, 7*8+3 },
		new int[] { 0, 4*64, RGN_FRAC(1,4), RGN_FRAC(1,4)+4*64, RGN_FRAC(2,4), RGN_FRAC(2,4)+4*64, RGN_FRAC(3,4), RGN_FRAC(3,4)+4*64,
			1*64, 5*64, RGN_FRAC(1,4)+1*64, RGN_FRAC(1,4)+5*64, RGN_FRAC(2,4)+1*64, RGN_FRAC(2,4)+5*64, RGN_FRAC(3,4)+1*64, RGN_FRAC(3,4)+5*64,
			2*64, 6*64, RGN_FRAC(1,4)+2*64, RGN_FRAC(1,4)+6*64, RGN_FRAC(2,4)+2*64, RGN_FRAC(2,4)+6*64, RGN_FRAC(3,4)+2*64, RGN_FRAC(3,4)+6*64,
			3*64, 7*64, RGN_FRAC(1,4)+3*64, RGN_FRAC(1,4)+7*64, RGN_FRAC(2,4)+3*64, RGN_FRAC(2,4)+7*64, RGN_FRAC(3,4)+3*64, RGN_FRAC(3,4)+7*64 },
		64*8
	);
	
	static GfxLayout crzrally_spritelayout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		2,
		new int[] { 0, 1 },
		new int[] { 3*2, 2*2, 1*2, 0*2, 7*2, 6*2, 5*2, 4*2,
				16+3*2, 16+2*2, 16+1*2, 16+0*2, 16+7*2, 16+6*2, 16+5*2, 16+4*2 },
		new int[] {		RGN_FRAC(3,4)+0*16, RGN_FRAC(2,4)+0*16, RGN_FRAC(1,4)+0*16, RGN_FRAC(0,4)+0*16,
				RGN_FRAC(3,4)+2*16, RGN_FRAC(2,4)+2*16, RGN_FRAC(1,4)+2*16, RGN_FRAC(0,4)+2*16,
				RGN_FRAC(3,4)+4*16, RGN_FRAC(2,4)+4*16, RGN_FRAC(1,4)+4*16, RGN_FRAC(0,4)+4*16,
				RGN_FRAC(3,4)+6*16, RGN_FRAC(2,4)+6*16, RGN_FRAC(1,4)+6*16, RGN_FRAC(0,4)+6*16 },
		8*16
	);
	
	static GfxDecodeInfo holeland_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, holeland_charlayout,   0, 256 ),
		new GfxDecodeInfo( REGION_GFX2, 0, holeland_spritelayout, 0, 256 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	static GfxDecodeInfo crzrally_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, crzrally_charlayout,   0, 256 ),
		new GfxDecodeInfo( REGION_GFX2, 0, crzrally_spritelayout, 0, 256 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static AY8910interface ay8910_interface = new AY8910interface
	(
		2,	/* 2 chips */
		1818182,	/* 1.82 MHz ? */
		new int[] { 25, 25 },
		new ReadHandlerPtr[] { input_port_0_r, input_port_2_r },
		new ReadHandlerPtr[] { input_port_1_r, input_port_3_r },
		new WriteHandlerPtr[] { null, null },
		new WriteHandlerPtr[] { null, null }
	);
	
	
	
	static MachineDriver machine_driver_holeland = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,        /* 4 MHz ? */
				readmem, writemem,readport, writeport,
				interrupt, 1
                        )
		},
		60, DEFAULT_60HZ_VBLANK_DURATION,
		1,      /* single CPU, no need for interleaving */
		null,
	
		/* video hardware */
		32*16, 32*16, new rectangle( 0*16, 32*16-1, 2*16, 30*16-1 ),
		holeland_gfxdecodeinfo,
		256, 0,
		palette_RRRR_GGGG_BBBB_convert_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		holeland_vh_start,
		null,
		holeland_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_AY8910,
				ay8910_interface
			)
		}
	);
	
	static MachineDriver machine_driver_crzrally = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80,
				4000000,        /* 4 MHz ? */
				crzrally_readmem, crzrally_writemem,readport, writeport,
				interrupt, 1
			)
		},
		60, DEFAULT_60HZ_VBLANK_DURATION,
		1,      /* single CPU, no need for interleaving */
		null,
	
		/* video hardware */
		32*8, 32*8, new rectangle( 0*8, 32*8-1, 2*8, 30*8-1 ),
		crzrally_gfxdecodeinfo,
		256, 0,
		palette_RRRR_GGGG_BBBB_convert_prom,
	
		VIDEO_TYPE_RASTER,
		null,
		crzrally_vh_start,
		null,
		crzrally_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_AY8910,
				ay8910_interface
			)
		}
	);
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_holeland = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "holeland.0",  0x0000, 0x2000, 0xb640e12b );
		ROM_LOAD( "holeland.1",  0x2000, 0x2000, 0x2f180851 );
		ROM_LOAD( "holeland.2",  0x4000, 0x2000, 0x35cfde75 );
		ROM_LOAD( "holeland.3",  0x6000, 0x2000, 0x5537c22e );
		ROM_LOAD( "holeland.4",  0xa000, 0x2000, 0xc95c355d );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );
		ROM_LOAD( "holeland.5",  0x0000, 0x2000, 0x7f19e1f9 );
		ROM_LOAD( "holeland.6",  0x2000, 0x2000, 0x844400e3 );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "holeland.7",  0x0000, 0x2000, 0xd7feb25b );
		ROM_LOAD( "holeland.8",  0x2000, 0x2000, 0x4b6eec16 );
		ROM_LOAD( "holeland.9",  0x4000, 0x2000, 0x6fe7fcc0 );
		ROM_LOAD( "holeland.10", 0x6000, 0x2000, 0xe1e11e8f );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "3m",          0x0000, 0x0100, 0x9d6fef5a ); /* Red component */
		ROM_LOAD( "3l",          0x0100, 0x0100, 0xf6682705 ); /* Green component */
		ROM_LOAD( "3n",          0x0200, 0x0100, 0x3d7b3af6 ); /* Blue component */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_crzrally = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );
		ROM_LOAD( "1.7g",        0x0000, 0x4000, 0x8fe01f86 );
		ROM_LOAD( "2.7f",        0x4000, 0x4000, 0x67110f1d );
		ROM_LOAD( "3.7d",        0x8000, 0x4000, 0x25c861c3 );
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE | ROMREGION_INVERT );
		ROM_LOAD( "4.5g",        0x0000, 0x2000, 0x29dece8b );
		ROM_LOAD( "5.5f",        0x2000, 0x2000, 0xb34aa904 );
	
		ROM_REGION( 0x8000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "6.1f",        0x0000, 0x2000, 0xa909ff0f );
		ROM_LOAD( "7.1l",        0x2000, 0x2000, 0x38fb0a16 );
		ROM_LOAD( "8.1k",        0x4000, 0x2000, 0x660aa0f0 );
		ROM_LOAD( "9.1i",        0x6000, 0x2000, 0x37d0790e );
	
		ROM_REGION( 0x0300, REGION_PROMS, 0 );
		ROM_LOAD( "82s129.9n",   0x0000, 0x0100, 0x98ff725a ); /* Red component */
		ROM_LOAD( "82s129.9m",   0x0100, 0x0100, 0xd41f5800 ); /* Green component */
		ROM_LOAD( "82s129.9l",   0x0200, 0x0100, 0x9ed49cb4 ); /* Blue component */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_holeland	   = new GameDriver("1984"	,"holeland"	,"holeland.java"	,rom_holeland,null	,machine_driver_holeland	,input_ports_holeland	,null	,ROT0	,	"Tecfri", "Hole Land", GAME_IMPERFECT_GRAPHICS );
	public static GameDriver driver_crzrally	   = new GameDriver("1985"	,"crzrally"	,"holeland.java"	,rom_crzrally,null	,machine_driver_crzrally	,input_ports_crzrally	,null	,ROT270	,	"Tecfri", "Crazy Rally", GAME_IMPERFECT_GRAPHICS );
}
