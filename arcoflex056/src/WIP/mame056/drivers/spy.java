/***************************************************************************

S.P.Y. (c) 1989 Konami

Similar to Bottom of the Ninth

driver by Nicola Salmoria

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
import static mame056.drawgfxH.*;
import static mame056.driverH.*;
import static mame056.inptport.*;
import static mame056.inptportH.*;
import static mame056.memoryH.*;
import static mame056.palette.*;
import static mame056.sndintrf.*;
import static mame056.sndintrfH.*;
import static mame056.sound._2203intf.*;
import static mame056.sound._2203intfH.*;
import static mame056.sound.vlm5030.*;
import static mame056.sound.vlm5030H.*;
import static mame056.sound.streams.*;
import static WIP.mame056.vidhrdw.spy.*;
import static WIP.mame056.vidhrdw.konamiic.*;
import static mame056.sound.MSM5205.*;
import static mame056.vidhrdw.generic.*;
import static mame056.cpu.m6809.m6809H.*;
import static mame056.inputH.*;
import static mame056.sound.MSM5205.*;
import static mame056.sound.MSM5205H.*;
import static mame056.sound._3526intf.*;
import static mame056.sound._3812intf.*;
import static mame056.sound._3812intfH.*;
import static common.libc.cstring.*;
import static mame056.mame.Machine;
import static mame056.sound._2151intf.*;
import static mame056.sound._2151intfH.*;
import static mame056.sound.mixerH.MIXER_PAN_LEFT;
import static mame056.sound.mixerH.MIXER_PAN_RIGHT;
import static mame056.sound.k007232.*;
import static mame056.sound.k007232H.*;
import static mame056.sound.mixerH.MIXER_PAN_CENTER;

public class spy
{
	
	public static InterruptPtr spy_interrupt = new InterruptPtr() { public int handler() 
	{
		if (K052109_is_IRQ_enabled() != 0)
		{
			if (cpu_getiloops() != 0) return M6809_INT_FIRQ;	/* ??? */
			else return interrupt.handler();
		}
		else return ignore_interrupt.handler();
	} };
	
	
	static int rambank;
	static UBytePtr ram = new UBytePtr();
	
	public static ReadHandlerPtr spy_bankedram1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (rambank == 0) return ram.read(offset);
		else return paletteram_r.handler(offset);
	} };
	
	public static WriteHandlerPtr spy_bankedram1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (rambank == 0) ram.write(offset, data);
		else paletteram_xBBBBBGGGGGRRRRR_swap_w.handler(offset,data);
	} };
	
	public static WriteHandlerPtr bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UBytePtr rom = new UBytePtr(memory_region(REGION_CPU1));
		int offs;
	
		/* bit 0 = RAM bank? */
/*TODO*///	if ((data & 1) == 0) usrintf_showmessage("bankswitch RAM bank 0");
	
		/* bit 1-4 = ROM bank */
		if ((data & 0x10)!=0) offs = 0x20000 + (data & 0x06) * 0x1000;
		else offs = 0x10000 + (data & 0x0e) * 0x1000;
		cpu_setbank(1, new UBytePtr(rom, offs));
	} };
	
	public static WriteHandlerPtr spy_3f90_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* bits 0/1 = coin counters */
		coin_counter_w.handler(0,data & 0x01);
		coin_counter_w.handler(1,data & 0x02);
	
		/* bit 2 = enable char ROM reading through the video RAM */
		K052109_set_RMRD_line((data & 0x04)!=0 ? ASSERT_LINE : CLEAR_LINE);
	
		/* bit 4 = select RAM at 0000 */
		rambank = data & 0x10;
	
		/* other bits unknown */
	} };
	
	
	public static WriteHandlerPtr spy_sh_irqtrigger_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		cpu_cause_interrupt(1,0xff);
	} };
	
	public static WriteHandlerPtr sound_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UBytePtr rom = new UBytePtr();
		int bank_A,bank_B;
	
		rom = memory_region(REGION_SOUND1);
		bank_A = 0x20000 * ((data >> 0) & 0x03);
		bank_B = 0x20000 * ((data >> 2) & 0x03);
		K007232_bankswitch(0, new UBytePtr(rom, bank_A), new UBytePtr(rom, bank_B));
		rom = memory_region(REGION_SOUND2);
		bank_A = 0x20000 * ((data >> 4) & 0x03);
		bank_B = 0x20000 * ((data >> 6) & 0x03);
		K007232_bankswitch(1,new UBytePtr(rom, bank_A), new UBytePtr(rom, bank_B));
	} };
	
	
	
	public static Memory_ReadAddress spy_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x07ff, spy_bankedram1_r ),
		new Memory_ReadAddress( 0x0800, 0x1aff, MRA_RAM ),
		new Memory_ReadAddress( 0x3fd0, 0x3fd0, input_port_4_r ),
		new Memory_ReadAddress( 0x3fd1, 0x3fd1, input_port_0_r ),
		new Memory_ReadAddress( 0x3fd2, 0x3fd2, input_port_1_r ),
		new Memory_ReadAddress( 0x3fd3, 0x3fd3, input_port_2_r ),
		new Memory_ReadAddress( 0x3fe0, 0x3fe0, input_port_3_r ),
		new Memory_ReadAddress( 0x2000, 0x5fff, K052109_051960_r ),
		new Memory_ReadAddress( 0x6000, 0x7fff, MRA_BANK1 ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_ROM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress spy_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x07ff, spy_bankedram1_w, ram ),
		new Memory_WriteAddress( 0x0800, 0x1aff, MWA_RAM ),
		new Memory_WriteAddress( 0x3f80, 0x3f80, bankswitch_w ),
		new Memory_WriteAddress( 0x3f90, 0x3f90, spy_3f90_w ),
		new Memory_WriteAddress( 0x3fa0, 0x3fa0, watchdog_reset_w ),
		new Memory_WriteAddress( 0x3fb0, 0x3fb0, soundlatch_w ),
		new Memory_WriteAddress( 0x3fc0, 0x3fc0, spy_sh_irqtrigger_w ),
		new Memory_WriteAddress( 0x2000, 0x5fff, K052109_051960_w ),
		new Memory_WriteAddress( 0x6000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0xffff, MWA_ROM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress spy_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x87ff, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa00d, K007232_read_port_0_r ),
		new Memory_ReadAddress( 0xb000, 0xb00d, K007232_read_port_1_r ),
		new Memory_ReadAddress( 0xc000, 0xc000, YM3812_status_port_0_r ),
		new Memory_ReadAddress( 0xd000, 0xd000, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress spy_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x87ff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x9000, sound_bank_w ),
		new Memory_WriteAddress( 0xa000, 0xa00d, K007232_write_port_0_w ),
		new Memory_WriteAddress( 0xb000, 0xb00d, K007232_write_port_1_w ),
		new Memory_WriteAddress( 0xc000, 0xc000, YM3812_control_port_0_w ),
		new Memory_WriteAddress( 0xc001, 0xc001, YM3812_write_port_0_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_spy = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 
		PORT_DIPNAME( 0x0f, 0x0f, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x02, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x05, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0x0f, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x07, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0e, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x06, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0x0d, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0b, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x0a, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x09, DEF_STR( "1C_7C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0xf0, 0xf0, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x20, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(    0x50, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "4C_3C") );
		PORT_DIPSETTING(    0xf0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x30, DEF_STR( "3C_4C") );
		PORT_DIPSETTING(    0x70, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "2C_5C") );
		PORT_DIPSETTING(    0xd0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0xc0, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0xb0, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(    0x90, DEF_STR( "1C_7C") );
	//	PORT_DIPSETTING(    0x00, "Invalid" );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x02, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "2" );
		PORT_DIPSETTING(    0x02, "3" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x00, "7" );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x18, 0x08, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x18, "10k and every 20k" );
		PORT_DIPSETTING(    0x10, "20k and every 30k" );
		PORT_DIPSETTING(    0x08, "20k" );
		PORT_DIPSETTING(    0x00, "30k" );
		PORT_DIPNAME( 0x60, 0x40, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(	0x60, "Easy" );
		PORT_DIPSETTING(	0x40, "Normal" );
		PORT_DIPSETTING(	0x20, "Difficult" );
		PORT_DIPSETTING(	0x00, "Very Difficult" );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(	0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(	0x00, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_SERVICE( 0x40, IP_ACTIVE_LOW );
		PORT_DIPNAME( 0x80, 0x80, "Continues" );
		PORT_DIPSETTING(    0x80, "Unlimited" );
		PORT_DIPSETTING(    0x00, "5 Times" );
	INPUT_PORTS_END(); }}; 
	
	
	
	static WriteYmHandlerPtr volume_callback0 = new WriteYmHandlerPtr() {
            public void handler(int v) {            
		K007232_set_volume(0,0,(v >> 4) * 0x11,0);
		K007232_set_volume(0,1,0,(v & 0x0f) * 0x11);
            }
        };
	
	static WriteYmHandlerPtr volume_callback1 = new WriteYmHandlerPtr() {
            public void handler(int v) {         
		K007232_set_volume(1,0,(v >> 4) * 0x11,0);
		K007232_set_volume(1,1,0,(v & 0x0f) * 0x11);
            }
        };
	
	static K007232_interface k007232_interface = new K007232_interface
	(
		2,			/* number of chips */
		new int[]{ REGION_SOUND1, REGION_SOUND2 },	/* memory regions */
		new int[]{ K007232_VOL(40,MIXER_PAN_CENTER,40,MIXER_PAN_CENTER),
                    K007232_VOL(40,MIXER_PAN_CENTER,40,MIXER_PAN_CENTER) },	/* volume */
		new WriteYmHandlerPtr[]{ volume_callback0, volume_callback1 }	/* external port callback */
	);
	
	
	static WriteYmHandlerPtr irqhandler = new WriteYmHandlerPtr() {
            public void handler(int linestate) {
                
		cpu_set_nmi_line(1,linestate);
            }
        };
	
	static YM3812interface ym3812_interface = new YM3812interface
	(
		1,			/* 1 chip */
		3579545,	/* ??? */
		new int[]{ 100 },	/* volume */
		new WriteYmHandlerPtr[]{ irqhandler }
	);
	
	
	
	static MachineDriver machine_driver_spy = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_HD6309,
				3000000, /* ? */
				spy_readmem,spy_writemem,null,null,
				spy_interrupt,2
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3579545,
				spy_sound_readmem, spy_sound_writemem,null,null,
				ignore_interrupt,0	/* irq is triggered by the main CPU */
									/* nmi by the sound chip */
			)
		},
		60, DEFAULT_60HZ_VBLANK_DURATION,	/* frames per second, vblank duration */
		1,	/* 1 CPU slice per frame - interleaving is forced when a sound command is written */
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 14*8, (64)*8-1, 2*8, 32*8-1 ),
		null,	/* gfx decoded by konamiic.c */
		1024, 0,
		null,
	
		VIDEO_TYPE_RASTER | VIDEO_HAS_SHADOWS,
		null,
		spy_vh_start,
		spy_vh_stop,
		spy_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_YM3812,
				ym3812_interface
			),
			new MachineSound(
				SOUND_K007232,
				k007232_interface
			)
		}
	);
	
	
	/***************************************************************************
	
	  Game ROMs
	
	***************************************************************************/
	
	static RomLoadPtr rom_spy = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x28800, REGION_CPU1, 0 );/* code + banked roms + space for banked ram */
		ROM_LOAD( "857m03.bin",   0x10000, 0x10000, 0x3bd87fa4 );
	    ROM_LOAD( "857m02.bin",   0x20000, 0x08000, 0x306cc659 );
	    ROM_CONTINUE(             0x08000, 0x08000 );
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );/* Z80 code */
		ROM_LOAD( "857d01.bin",   0x0000, 0x8000, 0xaad4210f );
	
		ROM_REGION( 0x080000, REGION_GFX1, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "857b09.bin",   0x00000, 0x40000, 0xb8780966 );/* characters */
		ROM_LOAD( "857b08.bin",   0x40000, 0x40000, 0x3e4d8d50 );
	
		ROM_REGION( 0x100000, REGION_GFX2, 0 );/* graphics ( dont dispose as the program can read them, 0 ) */
		ROM_LOAD( "857b06.bin",   0x00000, 0x80000, 0x7b515fb1 );/* sprites */
		ROM_LOAD( "857b05.bin",   0x80000, 0x80000, 0x27b0f73b );
	
		ROM_REGION( 0x0200, REGION_PROMS, 0 );
		ROM_LOAD( "857a10.bin",   0x0000, 0x0100, 0x32758507 );/* priority encoder (not used) */
	
		ROM_REGION( 0x40000, REGION_SOUND1, 0 );/* samples for 007232 #0 */
		ROM_LOAD( "857b07.bin",   0x00000, 0x40000, 0xce3512d4 );
	
		ROM_REGION( 0x40000, REGION_SOUND2, 0 );/* samples for 007232 #1 */
		ROM_LOAD( "857b04.bin",   0x00000, 0x40000, 0x20b83c13 );
	ROM_END(); }}; 
	
	
	
	static void gfx_untangle()
	{
		konami_rom_deinterleave_2(REGION_GFX1);
		konami_rom_deinterleave_2(REGION_GFX2);
	}
	
	public static InitDriverPtr init_spy = new InitDriverPtr() { public void handler()
	{
		paletteram = new UBytePtr(memory_region(REGION_CPU1), 0x28000);
		gfx_untangle();
	} };
	
	
	
	public static GameDriver driver_spy	   = new GameDriver("1989"	,"spy"	,"spy.java"	,rom_spy,null	,machine_driver_spy	,input_ports_spy	,init_spy	,ROT0	,	"Konami", "S.P.Y. - Special Project Y (US)", GAME_NOT_WORKING );
}
