/***************************************************************************

  gb.c

  Driver file to handle emulation of the Nintendo Gameboy.
  By:

  Hans de Goede               1998

  Todo list:
  Done entries kept for historical reasons, besides that it's nice to see
  what is already done instead of what has to be done.

Priority:  Todo:                                                  Done:
  2        Replace Marat's  vidhrdw/gb.c  by Playboy code           *
  2        Clean & speed up vidhrdw/gb.c                            *
  2        Replace Marat's  Z80gb/Z80gb.c by Playboy code           *
  2        Transform Playboys Z80gb.c to big case method            *
  2        Clean up Z80gb.c                                         *
  2        Fix / optimise halt instruction                          *
  2        Do correct lcd stat timing
  2        Generate lcd stat interrupts
  2        Replace Marat's code in machine/gb.c by Playboy code
  1        Check, and fix if needed flags bug which troubles ffa
  1        Save/restore battery backed ram
           (urgent needed to play zelda ;)
  1        Add sound
  0        Add supergb support
  0        Add palette editting, save & restore
  0        Add somekind of backdrop support
  0        Speedups if remotly possible

  2 = has to be done before first public release
  1 = should be added later on
  0 = bells and whistles

***************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.systems;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static common.libc.cstring.*;
import static mame056.commonH.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrf.*;
import static mame056.cpuintrfH.*;
import static mame056.drawgfxH.*;
import static mame056.driverH.*;
import static mame056.inptport.*;
import static mame056.inptportH.*;
import static mame056.inputH.*;
import static mame056.memoryH.*;
import static mame056.sndintrfH.*;
import static mame056.vidhrdw.generic.*;
import static mess056.deviceH.*;
import static mess056.machine.gb.*;
import static mess056.messH.*;
import static mess056.sndhrdw.gb.*;
import static mess056.vidhrdw.gb.*;

public class gb
{
	
	static Memory_ReadAddress readmem[] = {
            new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
            new Memory_ReadAddress(0x0000, 0x3fff, MRA_ROM ),			/* 16k fixed ROM BANK #0*/
            new Memory_ReadAddress(0x4000, 0x7fff, MRA_BANK1 ),			/* 16k switched ROM bank */
            new Memory_ReadAddress(0x8000, 0x9fff, MRA_RAM ),			/* 8k video ram */
            new Memory_ReadAddress(0xa000, 0xbfff, MRA_BANK2 ),			/* 8k switched RAM bank (on cartridge) */
            new Memory_ReadAddress(0xc000, 0xfe9f, MRA_RAM ),			/* internal ram + echo + sprite Ram & IO */
            new Memory_ReadAddress(0xfea0, 0xfeff, MRA_NOP ),			/* Unusable */
            new Memory_ReadAddress(0xff00, 0xff7f, gb_r_io ),			/* gb io */
            new Memory_ReadAddress(0xff80, 0xffff, MRA_RAM ),			/* plain ram (high) */            
            new Memory_ReadAddress(MEMPORT_MARKER, 0) /* end of table */
        };
	
	static Memory_WriteAddress writemem[] = { 
            new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
            new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),            /* plain rom */
            new Memory_WriteAddress( 0x2000, 0x3fff, gb_rom_bank_select ), /* rom bank select */
            new Memory_WriteAddress( 0x4000, 0x5fff, gb_ram_bank_select ), /* ram bank select */
            new Memory_WriteAddress( 0x6000, 0x7fff, gb_mem_mode_select ),            /* plain rom */
            new Memory_WriteAddress( 0x8000, 0x9fff, MWA_RAM ),            /* plain ram */
            new Memory_WriteAddress( 0xa000, 0xbfff, MWA_BANK2 ),          /* banked (cartridge) ram */
            new Memory_WriteAddress( 0xc000, 0xfe9f, MWA_RAM, videoram, videoram_size ), /* video & sprite ram */
            new Memory_WriteAddress( 0xfea0, 0xfeff, MWA_NOP ),
            new Memory_WriteAddress( 0xff00, 0xff7f, gb_w_io ),			/* gb io */
            new Memory_WriteAddress( 0xff80, 0xfffe, MWA_RAM ),			/* plain ram (high) */
            new Memory_WriteAddress( 0xffff, 0xffff, gb_w_ie ),			/* gb io (interrupt enable) */
            
            new Memory_WriteAddress(MEMPORT_MARKER, 0) /* end of table */
            
        };
        
        static GfxLayout gb_charlayout = new GfxLayout(
		8,8,
		256,
		1,						/* 1 bits per pixel */
	
		new int[] { 0 },					/* no bitplanes; 1 bit per pixel */
	
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0, 8*256, 16*256, 24*256, 32*256, 40*256, 48*256, 56*256 },
	
		8				/* every char takes 1 consecutive byte */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
            /*new GfxDecodeInfo( 0, 0x0, gb_charlayout, 0, 0x80 ),
		new GfxDecodeInfo( 0, 0x0, gb_charlayout, 0, 0x80 ),
		new GfxDecodeInfo( 0, 0x0, gb_charlayout, 0, 0x80 ),*/
		new GfxDecodeInfo( -1 )
	};
	
	static InputPortPtr input_ports_gameboy = new InputPortPtr(){ public void handler() { 
		PORT_START();	/* IN0 */
                PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT);
                PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT );
                PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP   );
                PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN );
                PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1       );
                PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2       );
		/*PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN, "Select", KEYCODE_LSHIFT, IP_JOY_DEFAULT );*/
		/*PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN, "Start",  KEYCODE_Z,      IP_JOY_DEFAULT );*/
		PORT_BITX( 0x40, IP_ACTIVE_LOW, IPT_KEYBOARD, "Select", KEYCODE_5, IP_JOY_DEFAULT );
		PORT_BITX( 0x80, IP_ACTIVE_LOW, IPT_KEYBOARD, "Start",  KEYCODE_1, IP_JOY_DEFAULT );
	INPUT_PORTS_END(); }}; 
	
	static char palette[] =
	{
            /*0xFF,0xFF,0xFF,
            0x00,0x00,0x00,*/
            0xFF,0xFB,0x87,
            0xB1,0xAE,0x4E,
            0x84,0x80,0x4E,
            0x4E,0x4E,0x4E
            /*248, 224, 136,
            216, 176, 88,
            152, 120, 56,
            72, 56, 24 */
	};
	
	static char colortable[] = {
            0,1,2,3,
            0,1,2,3,
	    0,1,2,3,    /* Background colours */
	    0,1,2,3,    /* Sprite 0 colours */
	    0,1,2,3,    /* Sprite 1 colours */
	    0,1,2,3,    /* Window colours */
	};
	
	/* Initialise the palette */
	static VhConvertColorPromPtr gb_init_palette = new VhConvertColorPromPtr() {
            public void handler(char[] sys_palette, char[] sys_colortable, UBytePtr color_prom) {
                memcpy(sys_palette,palette,palette.length);
		memcpy(sys_colortable,colortable,colortable.length);
            }
        };
	
	static CustomSound_interface gameboy_sound_interface = new CustomSound_interface
        (
		gameboy_sh_start,
		null,
		null
	);
	
	static MachineDriver machine_driver_gameboy = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_Z80GB,
				4194304,	  /* 4.194304 Mhz */
				readmem,writemem,null,null,
				gb_scanline_interrupt, 154 *3 /* 1 int each scanline ! */
			)
		},
		60, 0,	/* frames per second, vblank duration */
		1,
		gb_init_machine,
		gb_shutdown_machine,	/* shutdown machine */
	
		/* video hardware (double size) */
		160, 144,
		new rectangle( 0, 160-1, 0, 144-1 ),
		gfxdecodeinfo,
		palette.length/3,
		16,
		gb_init_palette,				/* init palette */
	
		VIDEO_TYPE_RASTER,
		null,
		gb_vh_start,					/* vh_start */
                gb_vh_stop,                     /* vh_stop */
		gb_vh_screen_refresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound( SOUND_CUSTOM, gameboy_sound_interface )
		}
	);
	
	static IODevice io_gameboy[] = {
            new IODevice(
			IO_CARTSLOT,		/* type */
			1,					/* count */
			"gb\0gmb\0cgb\0gbc\0sgb\0",		/* file extensions */
			IO_RESET_ALL,		/* reset if file changed */
                        null,
			gb_load_rom,		/* init */
			null,				/* exit */
			null,				/* info */
			null,               /* open */
			null,               /* close */
			null,               /* status */
			null,               /* seek */
			null,				/* tell */
                        null,               /* input */
			null,               /* output */
			null,               /* input_chunk */
			null                /* output_chunk */
                ),
		new IODevice(IO_END)
            };
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_gameboy = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION(0x10000,REGION_CPU1,0);		
	ROM_END(); }}; 
	
	/*     YEAR  NAME      PARENT    MACHINE   INPUT     INIT      COMPANY   FULLNAME */
	//CONSX( 1990, gameboy,  0,		 gameboy,  gameboy,  0,		   "Nintendo", "GameBoy", GAME_NO_SOUND )
	public static GameDriver driver_gameboy = new GameDriver("1990", "gameboy", "gameboy.java", rom_gameboy, null, machine_driver_gameboy, input_ports_gameboy, null, io_gameboy, "Nintendo", "GameBoy");
}
