/***************************************************************************
	commodore c64 home computer

    peter.trauner@jk.uni-linz.ac.at
    documentation
     www.funet.fi
***************************************************************************/

/*
  unsolved problems:
   execution of code in the io devices
    (program write some short test code into the vic sprite register)
 */

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

import static arcadeflex056.fucPtr.*;
import static arcadeflex056.osdepend.logerror;
import static common.libc.cstring.*;
import static common.ptr.*;
import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.cpu.m6502.m6502H.M6502_IRQ_LINE;
import static mame056.cpu.m6502.m6502H.M6510_IRQ_LINE;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrfH.cpu_getactivecpu;
import static mame056.mame.Machine;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mess056.includes.c64H.*;
import static mess056.includes.cbmH.*;
import static mess056.includes.cbmserbH.*;
import static mess056.includes.cia6526H.*;
import static mess056.machine.vc20tape.*;
import static mess056.includes.vc20tapeH.*;
import static mess056.machine.cbm.*;
import static mess056.machine.cbmserb.*;
import static mess056.machine.cia6526.*;
import static mess056.sndhrdw.sid6581.*;
import static mess056.vidhrdw.vic6567.*;

public class c64
{
/*TODO*///#define VERBOSE_DBG 1
	
	
	
	/* keyboard lines */
	public static int c64_keyline[] =
	{
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff
	};
	
/*TODO*///	/* expansion port lines input */
        public static int c64_pal = 0;
	public static int c64_game=1, c64_exrom=1;
/*TODO*///	
/*TODO*///	/* cpu port */
	public static int c64_port6510, c64_ddr6510;
        public static int c128=0;
        public static int c65=0;
/*TODO*///	int c128_va1617;
        public static UBytePtr c64_vicaddr, c128_vicaddr;
        public static UBytePtr c64_memory=new UBytePtr(0x15000);
        public static UBytePtr c64_colorram;
        public static UBytePtr c64_basic=new UBytePtr(0x15000);
        public static UBytePtr c64_kernal=new UBytePtr(0x15000);
        public static UBytePtr c64_chargen;
        public static UBytePtr c64_roml=null;
        public static UBytePtr c64_romh=null;

        public static UBytePtr  roml=null, romh=null;
	public static int ultimax = 0;
        public static int c64_tape_on = 0;
        public static int c64_cia1_on = 1;

        public static int cartridge = 0;

        public static int CartridgeAuto = 0;
        public static int CartridgeUltimax = 1;
        public static int CartridgeC64 = 3;
        public static int CartridgeSuperGames = 4;
        public static int CartridgeRobocop2 = 5;

        public static int cartridgetype = CartridgeAuto;
        public static int cia0porta=1;
        public static int serial_clock, serial_data, serial_atn;
	public static int vicirq = 1, cia0irq = 1;

/*TODO*///	/*
/*TODO*///	 * cia 0
/*TODO*///	 * port a
/*TODO*///	 * 7-0 keyboard line select
/*TODO*///	 * 7,6: paddle select( 01 port a, 10 port b)
/*TODO*///	 * 4: joystick a fire button
/*TODO*///	 * 3,2: Paddles port a fire button
/*TODO*///	 * 3-0: joystick a direction
/*TODO*///	 * port b
/*TODO*///	 * 7-0: keyboard raw values
/*TODO*///	 * 4: joystick b fire button, lightpen select
/*TODO*///	 * 3,2: paddle b fire buttons (left,right)
/*TODO*///	 * 3-0: joystick b direction
/*TODO*///	 * flag cassette read input, serial request in
/*TODO*///	 * irq to irq connected
/*TODO*///	 */
        public static ReadHandlerPtr c64_cia0_port_a_r = new ReadHandlerPtr() {
            public int handler(int offset) {
                System.out.println("c64_cia0_port_a_r inicio");
                int value = 0xff;
	
		if (JOYSTICK_SWAP() != 0) value = c64_keyline[8];
                else 
                    value = c64_keyline[9];
                
                System.out.println("c64_cia0_port_a_r="+value);
		return value;
            }
        };
	
        public static ReadHandlerPtr c64_cia0_port_b_r = new ReadHandlerPtr() {
            public int handler(int offset) {
                 System.out.println("c64_cia0_port_b_r antes");
		int value = 0xff;
	
		if ((cia0porta & 0x80)==0)
			value &= c64_keyline[7];
		if ((cia0porta & 0x40)==0)
			value &= c64_keyline[6];
		if ((cia0porta & 0x20)==0)
			value &= c64_keyline[5];
		if ((cia0porta & 0x10)==0)
			value &= c64_keyline[4];
		if ((cia0porta & 8)==0)
			value &= c64_keyline[3];
		if ((cia0porta & 4)==0)
			value &= c64_keyline[2];
		if ((cia0porta & 2)==0)
			value &= c64_keyline[1];
		if ((cia0porta & 1)==0)
			value &= c64_keyline[0];
	
		if (JOYSTICK_SWAP() != 0) value &= c64_keyline[9];
		else value &= c64_keyline[8];
	
/*TODO*///		if (c128 != 0)
/*TODO*///		{
/*TODO*///			if (!vic2e_k0_r ())
/*TODO*///				value &= c128_keyline[0];
/*TODO*///			if (!vic2e_k1_r ())
/*TODO*///				value &= c128_keyline[1];
/*TODO*///			if (!vic2e_k2_r ())
/*TODO*///				value &= c128_keyline[2];
/*TODO*///		}
/*TODO*///		if (c65 != 0) {
/*TODO*///			if (!(c65_6511_port&2))
/*TODO*///				value&=c65_keyline;
/*TODO*///		}
            System.out.println("c64_cia0_port_b_r="+value);
		return value;
            }
        };
	
        public static WriteHandlerPtr c64_cia0_port_a_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                cia0porta = data;
                System.out.println("c64_cia0_port_a_w");
            }
        };
	
        public static WriteHandlerPtr c64_cia0_port_b_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                System.out.println("c64_cia0_port_b_w");
/*TODO*///		vic2_lightpen_write (data & 0x10);
            }
        };
        
	static int old_level = -5;
        
	public static void c64_irq (int level)
	{
/*TODO*///		
/*TODO*///	
            System.out.println("c64_irq");
		if (level != old_level)
		{
/*TODO*///			DBG_LOG (3, "mos6510", ("irq %s\n", level ? "start" : "end"));
			if (c128 != 0) {
				if ((cpu_getactivecpu()==0)) {
					cpu_set_irq_line (0, Z80_IRQ_INT, level);
				} else {
					cpu_set_irq_line (1, M6502_INT_IRQ, level);
				}
			} else {
                            System.out.println("IRQ!!!!");
				cpu_set_irq_line (0, M6502_INT_IRQ, level);
			}
			old_level = level;
		}
	}
/*TODO*///	
/*TODO*///	WRITE_HANDLER(c64_tape_read)
/*TODO*///	{
/*TODO*///		cia6526_0_set_input_flag (data);
/*TODO*///	}
/*TODO*///	
        public static ReadHandlerPtr c64_cia0_interrupt = new ReadHandlerPtr() {
            public int handler(int level) {
                System.out.println("c64_cia0_interrupt");
		if (level != cia0irq)
		{
			c64_irq ((level!=0 || vicirq!=0)? 1 : 0);
			cia0irq = level;
		}
                
                return 1;
            }
        };
	
        public static ReadHandlerPtr c64_vic_interrupt = new ReadHandlerPtr() {
            public int handler(int level) {
                System.out.println("c64_vic_interrupt");
/*TODO*///	#if 1
		if (level != vicirq)
		{
			c64_irq ((level!=0 || cia0irq!=0) ? 1 : 0);
			vicirq = level;
		}
/*TODO*///	#endif
                
                return 1;
            }
        };
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * cia 1
/*TODO*///	 * port a
/*TODO*///	 * 7 serial bus data input
/*TODO*///	 * 6 serial bus clock input
/*TODO*///	 * 5 serial bus data output
/*TODO*///	 * 4 serial bus clock output
/*TODO*///	 * 3 serial bus atn output
/*TODO*///	 * 2 rs232 data output
/*TODO*///	 * 1-0 vic-chip system memory bank select
/*TODO*///	 *
/*TODO*///	 * port b
/*TODO*///	 * 7 user rs232 data set ready
/*TODO*///	 * 6 user rs232 clear to send
/*TODO*///	 * 5 user
/*TODO*///	 * 4 user rs232 carrier detect
/*TODO*///	 * 3 user rs232 ring indicator
/*TODO*///	 * 2 user rs232 data terminal ready
/*TODO*///	 * 1 user rs232 request to send
/*TODO*///	 * 0 user rs232 received data
/*TODO*///	 * flag restore key or rs232 received data input
/*TODO*///	 * irq to nmi connected ?
/*TODO*///	 */
        public static ReadHandlerPtr c64_cia1_port_a_r = new ReadHandlerPtr() {
            public int handler(int level) {
                System.out.println("c64_cia1_port_a_r");
		int value = 0xff;
	
		if (serial_clock==0 || cbm_serial_clock_read ()==0)
			value &= ~0x40;
		if (serial_data==0 || cbm_serial_data_read ()==0)
			value &= ~0x80;
		return value;
	}
            };
        
        static int helper[] =
		{0xc000, 0x8000, 0x4000, 0x0000};
        
        public static WriteHandlerPtr c64_cia1_port_a_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                
	
		cbm_serial_clock_write (serial_clock = (data & 0x10)==0?1:0);
		cbm_serial_data_write (serial_data = (data & 0x20)==0?1:0);
		cbm_serial_atn_write (serial_atn = (data & 8)==0?1:0);
		c64_vicaddr = new UBytePtr(c64_memory, helper[data & 3]);
/*TODO*///		if (c128 != 0) {
/*TODO*///			c128_vicaddr = c64_memory + helper[data & 3] + c128_va1617;
/*TODO*///		}
            }
        };

	
	public static ReadHandlerPtr c64_cia1_interrupt = new ReadHandlerPtr() {
            public int handler(int level) {
                System.out.println("c64_cia1_interrupt");
                if (level != old_level)
		{
/*TODO*///			DBG_LOG (1, "mos6510", ("nmi %s\n", level ? "start" : "end"));
/*TODO*///			/*      cpu_set_nmi_line(0, level); */
			old_level = level;
		}
                
                return 1;
            }
        };

	public static cia6526_interface c64_cia0 = new cia6526_interface
        (
		c64_cia0_port_a_r,
		c64_cia0_port_b_r,
		c64_cia0_port_a_w,
		c64_cia0_port_b_w,
		null,								   /*c64_cia0_pc_w */
		null,								   /*c64_cia0_sp_r */
		null,								   /*c64_cia0_sp_w */
		null,								   /*c64_cia0_cnt_r */
		null,								   /*c64_cia0_cnt_w */
		c64_cia0_interrupt,
		0xff, 0xff, 0
	), c64_cia1 = new cia6526_interface
        (
		c64_cia1_port_a_r,
		null,								   /*c64_cia1_port_b_r, */
		c64_cia1_port_a_w,
		null,								   /*c64_cia1_port_b_w, */
		null,								   /*c64_cia1_pc_w */
		null,								   /*c64_cia1_sp_r */
		null,								   /*c64_cia1_sp_w */
		null,								   /*c64_cia1_cnt_r */
		null,								   /*c64_cia1_cnt_w */
		c64_cia1_interrupt,
		0xc7, 0xff, 0
	);
	
/*TODO*///	static void c64_robocop2_w(int offset, int value)
/*TODO*///	{
/*TODO*///		/* robocop2 0xe00
/*TODO*///		 * 80 94 80 94 80
/*TODO*///		 * 80 81 80 82 83 80
/*TODO*///		 */
/*TODO*///		roml=cbm_rom[value&0xf].chip;
/*TODO*///		romh=cbm_rom[(value&0xf)+0x10].chip;
/*TODO*///		if ((value & 0x80) != 0)
/*TODO*///			{
/*TODO*///				c64_game = value & 0x10;
/*TODO*///				c64_exrom = 1;
/*TODO*///			}
/*TODO*///		else
/*TODO*///			{
/*TODO*///				c64_game = c64_exrom = 1;
/*TODO*///			}
/*TODO*///		if (c128 != 0)
/*TODO*///			c128_bankswitch_64 (0);
/*TODO*///		else
/*TODO*///			c64_bankswitch (0);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_supergames_w(int offset, int value)
/*TODO*///	{
/*TODO*///		/* supergam 0xf00
/*TODO*///		 * 4 9 4
/*TODO*///		 * 4 0 c
/*TODO*///		 */
/*TODO*///		roml=cbm_rom[value&3].chip;
/*TODO*///		romh=cbm_rom[value&3].chip+0x2000;
/*TODO*///		if ((value & 4) != 0)
/*TODO*///			{
/*TODO*///				c64_game = 0;
/*TODO*///				c64_exrom = 1;
/*TODO*///			}
/*TODO*///		else
/*TODO*///			{
/*TODO*///				c64_game = c64_exrom = 1;
/*TODO*///			}
/*TODO*///		if (value == 0xc)
/*TODO*///			{
/*TODO*///				c64_game = c64_exrom = 0;
/*TODO*///			}
/*TODO*///		if (c128 != 0)
/*TODO*///			c128_bankswitch_64 (0);
/*TODO*///		else
/*TODO*///			c64_bankswitch (0);
/*TODO*///	}
	
	public static WriteHandlerPtr c64_write_io = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset < 0x400) {
			vic2_port_w.handler(offset & 0x3ff, data);
		} else if (offset < 0x800) {
			sid6581_0_port_w.handler(offset & 0x3ff, data);
		} else if (offset < 0xc00)
			c64_colorram.write(offset & 0x3ff, data | 0xf0);
		else if (offset < 0xd00)
			cia6526_0_port_w.handler(offset & 0xff, data);
		else if (offset < 0xe00)
		{
			if (c64_cia1_on != 0)
				cia6526_1_port_w.handler(offset & 0xff, data);
/*TODO*///			else
/*TODO*///				DBG_LOG (1, "io write", ("%.3x %.2x\n", offset, data));
		}
		else if (offset < 0xf00)
		{
/*TODO*///			/* i/o 1 */
/*TODO*///			if (cartridge && (cartridgetype == CartridgeRobocop2))
/*TODO*///			{
/*TODO*///				c64_robocop2_w(offset&0xff, data);
/*TODO*///			}
/*TODO*///			else
/*TODO*///				DBG_LOG (1, "io write", ("%.3x %.2x\n", offset, data));
		}
		else
		{
/*TODO*///			/* i/o 2 */
/*TODO*///			if (cartridge && (cartridgetype == CartridgeSuperGames))
/*TODO*///			{
/*TODO*///				c64_supergames_w(offset&0xff, data);
/*TODO*///			}
/*TODO*///			else
/*TODO*///				DBG_LOG (1, "io write", ("%.3x %.2x\n", offset, data));
		}
	} };
	
	public static ReadHandlerPtr c64_read_io  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (offset < 0x400)
			return vic2_port_r.handler(offset & 0x3ff);
		else if (offset < 0x800)
			return sid6581_0_port_r.handler(offset & 0x3ff);
		else if (offset < 0xc00)
			return c64_colorram.read(offset & 0x3ff);
		else if (offset < 0xd00)
			return cia6526_0_port_r.handler(offset & 0xff);
		else if (c64_cia1_on!=0 && (offset < 0xe00))
			return cia6526_1_port_r.handler(offset & 0xff);
/*TODO*///		DBG_LOG (1, "io read", ("%.3x\n", offset));
		return 0xff;
	} };
	
/*TODO*///	/*
/*TODO*///	 * two devices access bus, cpu and vic
/*TODO*///	 *
/*TODO*///	 * romh, roml chip select lines on expansion bus
/*TODO*///	 * loram, hiram, charen bankswitching select by cpu
/*TODO*///	 * exrom, game bankswitching select by cartridge
/*TODO*///	 * va15, va14 bank select by cpu for vic
/*TODO*///	 *
/*TODO*///	 * exrom, game: normal c64 mode
/*TODO*///	 * exrom, !game: ultimax mode
/*TODO*///	 *
/*TODO*///	 * romh: 8k rom at 0xa000 (hiram && !game && exrom)
/*TODO*///	 * or 8k ram at 0xe000 (!game exrom)
/*TODO*///	 * roml: 8k rom at 0x8000 (loram hiram !exrom)
/*TODO*///	 * or 8k ram at 0x8000 (!game exrom)
/*TODO*///	 * roml vic: upper 4k rom at 0x3000, 0x7000, 0xb000, 0xd000 (!game exrom)
/*TODO*///	 *
/*TODO*///	 * basic rom: 8k rom at 0xa000 (loram hiram game)
/*TODO*///	 * kernal rom: 8k rom at 0xe000 (hiram !exrom, hiram game)
/*TODO*///	 * char rom: 4k rom at 0xd000 (!exrom charen hiram
/*TODO*///	 * game charen !hiram loram
/*TODO*///	 * game charen hiram)
/*TODO*///	 * cpu
/*TODO*///	 *
/*TODO*///	 * (write colorram)
/*TODO*///	 * gr_w = !read&&!cas&&((address&0xf000)==0xd000)
/*TODO*///	 *
/*TODO*///	 * i_o = !game exrom !read ((address&0xf000)==0xd000)
/*TODO*///	 * !game exrom ((address&0xf000)==0xd000)
/*TODO*///	 * charen !hiram loram !read ((address&0xf000)==0xd000)
/*TODO*///	 * charen !hiram loram ((address&0xf000)==0xd000)
/*TODO*///	 * charen hiram !read ((address&0xf000)==0xd000)
/*TODO*///	 * charen hiram ((address&0xf000)==0xd000)
/*TODO*///	 *
/*TODO*///	 * vic
/*TODO*///	 * char rom: x101 (game, !exrom)
/*TODO*///	 * romh: 0011 (!game, exrom)
/*TODO*///	 *
/*TODO*///	 * exrom !game (ultimax mode)
/*TODO*///	 * addr    CPU     VIC-II
/*TODO*///	 * ----    ---     ------
/*TODO*///	 * 0000    RAM     RAM
/*TODO*///	 * 1000    -       RAM
/*TODO*///	 * 2000    -       RAM
/*TODO*///	 * 3000    -       ROMH (upper half)
/*TODO*///	 * 4000    -       RAM
/*TODO*///	 * 5000    -       RAM
/*TODO*///	 * 6000    -       RAM
/*TODO*///	 * 7000    -       ROMH
/*TODO*///	 * 8000    ROML    RAM
/*TODO*///	 * 9000    ROML    RAM
/*TODO*///	 * A000    -       RAM
/*TODO*///	 * B000    -       ROMH
/*TODO*///	 * C000    -       RAM
/*TODO*///	 * D000    I/O     RAM
/*TODO*///	 * E000    ROMH    RAM
/*TODO*///	 * F000    ROMH    ROMH
/*TODO*///	 */
        static int old = -1, exrom, game;
        
	public static void c64_bankswitch (int reset)
	{
		
		int data, loram, hiram, charen;
	
		data = ((c64_port6510 & c64_ddr6510) | (c64_ddr6510 ^ 0xff)) & 7;
		if ((data == old)&&(exrom==c64_exrom)&&(game==c64_game)&&reset==0) return;
	
/*TODO*///		DBG_LOG (1, "bankswitch", ("%d\n", data & 7));
		loram = (data & 1)!=0 ? 1 : 0;
		hiram = (data & 2)!=0 ? 1 : 0;
		charen = (data & 4)!=0 ? 1 : 0;
	
		if ((c64_game==0 && c64_exrom!=0)
			|| (loram!=0 && hiram!=0 && c64_exrom==0))
		{
			cpu_setbank (1, roml);
			memory_set_bankhandler_w(2, 0, MWA_NOP);
		}
		else
		{
			cpu_setbank (1, new UBytePtr(c64_memory, 0x8000));
			memory_set_bankhandler_w (2, 0, MWA_RAM);
		}
	
		if ((c64_game==0 && c64_exrom!=0 && hiram!=0)
			|| (c64_exrom==0))
		{
                    System.out.println("A");
			cpu_setbank (3, romh);
		}
		else if (loram!=0 && hiram!=0)
		{
                    System.out.println("B");
			cpu_setbank (3, new UBytePtr(c64_basic));
                        
		}
		else
		{
                    System.out.println("C");
			cpu_setbank (3, new UBytePtr(c64_memory, 0xa000));
		}
	
		if ((c64_game==0 && c64_exrom!=0)
			|| (charen!=0 && (loram!=0 || hiram!=0)))
		{
			memory_set_bankhandler_r (5, 0, c64_read_io);
			memory_set_bankhandler_w (6, 0, c64_write_io);
		}
		else
		{
			memory_set_bankhandler_r (5, 0, MRA_BANK5);
			memory_set_bankhandler_w (6, 0, MWA_BANK6);
			cpu_setbank (6, new UBytePtr(c64_memory, 0xd000));
			if (charen==0 && (loram!=0 || hiram!=0))
			{
                            System.out.println("A1");
				cpu_setbank (5, c64_chargen);
			}
			else
			{
                            System.out.println("A2");
				cpu_setbank (5, new UBytePtr(c64_memory, 0xd000));
			}
		}
	
		if (c64_game==0 && c64_exrom!=0)
		{
			cpu_setbank (7, romh);
			memory_set_bankhandler_w (8, 0, MWA_NOP);
		}
		else
		{
			memory_set_bankhandler_w (8, 0, MWA_RAM);
			if (hiram != 0)
			{
                            System.out.println("A11");
				cpu_setbank (7, new UBytePtr(c64_kernal));
			}
			else
			{
                            System.out.println("A12");
				cpu_setbank (7, new UBytePtr(c64_memory, 0xe000));
			}
		}
		game=c64_game;
		exrom=c64_exrom;
		old = data;
	}
	
/*TODO*///	/**
/*TODO*///	  ddr bit 1 port line is output
/*TODO*///	  port bit 1 port line is high
/*TODO*///	
/*TODO*///	  p0 output loram
/*TODO*///	  p1 output hiram
/*TODO*///	  p2 output charen
/*TODO*///	  p3 output cassette data
/*TODO*///	  p4 input cassette switch
/*TODO*///	  p5 output cassette motor
/*TODO*///	  p6,7 not available on M6510
/*TODO*///	 */
        public static WriteHandlerPtr c64_m6510_port_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                if (offset != 0)
		{
			if (c64_port6510 != data)
				c64_port6510 = data;
		}
		else
		{
			if (c64_ddr6510 != data)
				c64_ddr6510 = data;
		}
		data = (c64_port6510 & c64_ddr6510) | (c64_ddr6510 ^ 0xff);
/*TODO*///		if (c64_tape_on != 0) {
/*TODO*///			vc20_tape_write (!(data & 8));
/*TODO*///			vc20_tape_motor (data & 0x20);
/*TODO*///		}
/*TODO*///		if (c128 != 0)
/*TODO*///			c128_bankswitch_64 (0);
/*TODO*///		else if (c65 != 0)
/*TODO*///			c65_bankswitch();
/*TODO*///		else 
                if (ultimax==0)
			c64_bankswitch (0);
            }
        };

        
        public static ReadHandlerPtr c64_m6510_port_r = new ReadHandlerPtr() {
            public int handler(int offset) {
                if (offset != 0)
		{
			int data = (c64_ddr6510 & c64_port6510) | (c64_ddr6510 ^ 0xff);
	
			if (c64_tape_on!=0 && (c64_ddr6510 & 0x10)==0 && vc20_tape_switch ()==0)
				data &= ~0x10;
/*TODO*///			if (c128 && !c128_capslock_r ())
/*TODO*///				data &= ~0x40;
/*TODO*///			if (c65 && C65_KEY_DIN) data &= ~0x40; /*? */
			return data;
		}
		else
		{
			return c64_ddr6510;
		}
            }
        };

/*TODO*///	
/*TODO*///	int c64_paddle_read (int which)
/*TODO*///	{
/*TODO*///		int pot1=0xff, pot2=0xff, pot3=0xff, pot4=0xff, temp;
/*TODO*///		if (PADDLES34 != 0) {
/*TODO*///			if (which != 0) pot4=PADDLE4_VALUE;
/*TODO*///			else pot3=PADDLE3_VALUE;
/*TODO*///		}
/*TODO*///		if (JOYSTICK2_2BUTTON&&which) {
/*TODO*///			if (JOYSTICK_2_BUTTON2 != 0) pot4=0x00;
/*TODO*///		}
/*TODO*///		if (MOUSE2 != 0) {
/*TODO*///			if (which != 0) pot4=MOUSE2_Y;
/*TODO*///			else pot3=MOUSE2_X;
/*TODO*///		}
/*TODO*///		if (PADDLES12 != 0) {
/*TODO*///			if (which != 0) pot2=PADDLE2_VALUE;
/*TODO*///			else pot1=PADDLE1_VALUE;
/*TODO*///		}
/*TODO*///		if (JOYSTICK1_2BUTTON&&which) {
/*TODO*///			if (JOYSTICK_1_BUTTON2 != 0) pot1=0x00;
/*TODO*///		}
/*TODO*///		if (MOUSE1 != 0) {
/*TODO*///			if (which != 0) pot2=MOUSE1_Y;
/*TODO*///			else pot1=MOUSE1_X;
/*TODO*///		}
/*TODO*///		if (JOYSTICK_SWAP != 0) {
/*TODO*///			temp=pot1;pot1=pot2;pot2=pot1;
/*TODO*///			temp=pot3;pot3=pot4;pot4=pot3;
/*TODO*///		}
/*TODO*///		switch (cia0porta & 0xc0) {
/*TODO*///		case 0x40:
/*TODO*///			if (which != 0) return pot2;
/*TODO*///			return pot1;
/*TODO*///		case 0x80:
/*TODO*///			if (which != 0) return pot4;
/*TODO*///				return pot3;
/*TODO*///		default:
/*TODO*///			return 0;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	READ_HANDLER(c64_colorram_read)
/*TODO*///	{
/*TODO*///		return c64_colorram[offset & 0x3ff];
/*TODO*///	}
	
	public static WriteHandlerPtr c64_colorram_write = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		c64_colorram.write(offset & 0x3ff, data | 0xf0);
	} };
	
/*TODO*///	/*
/*TODO*///	 * only 14 address lines
/*TODO*///	 * a15 and a14 portlines
/*TODO*///	 * 0x1000-0x1fff, 0x9000-0x9fff char rom
/*TODO*///	 */
        public static ReadHandlerPtr c64_dma_read = new ReadHandlerPtr() {
            public int handler(int offset) {
		if (c64_game==0 && c64_exrom!=0)
		{
			if (offset < 0x3000)
				return c64_memory.read(offset);
			return c64_romh.read(offset & 0x1fff);
		}
		if (((c64_vicaddr.offset - c64_memory.offset + offset) & 0x7000) == 0x1000)
			return c64_chargen.read(offset & 0xfff);
		return c64_vicaddr.read(offset);
            }
        };
	
/*TODO*///	static int c64_dma_read_ultimax (int offset)
/*TODO*///	{
/*TODO*///		if (offset < 0x3000)
/*TODO*///			return c64_memory[offset];
/*TODO*///		return c64_romh[offset & 0x1fff];
/*TODO*///	}
/*TODO*///	
        public static ReadHandlerPtr c64_dma_read_color = new ReadHandlerPtr() {
            public int handler(int offset) {
		return c64_colorram.read(offset & 0x3ff) & 0xf;
            }
        };
	
	public static void c64_common_driver_init ()
	{
/*TODO*///		/*    memset(c64_memory, 0, 0xfd00); */
/*TODO*///	
/*TODO*///		if (c64_tape_on != 0)
/*TODO*///			vc20_tape_open (c64_tape_read);
/*TODO*///	
/*TODO*///		if (c64_cia1_on != 0)
/*TODO*///		{
/*TODO*///			cbm_drive_open ();
/*TODO*///	
/*TODO*///			cbm_drive_attach_fs (0);
/*TODO*///			cbm_drive_attach_fs (1);
/*TODO*///		}
/*TODO*///	
/*TODO*///		sid6581_0_init (c64_paddle_read, c64_pal);
		c64_cia0.todin50hz = c64_pal;
		cia6526_config (0, c64_cia0);
		if (c64_cia1_on != 0)
		{
			c64_cia1.todin50hz = c64_pal;
			cia6526_config (1, c64_cia1);
		}
	
/*TODO*///		if (ultimax != 0)
/*TODO*///		{
/*TODO*///			vic6567_init (0, c64_pal, c64_dma_read_ultimax, c64_dma_read_color,
/*TODO*///						  c64_vic_interrupt);
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
			vic6567_init(0, c64_pal, c64_dma_read, c64_dma_read_color,
						  c64_vic_interrupt);
/*TODO*///		}
/*TODO*///		raster1.display_state=c64_state;
	}

        public static InitDriverPtr c64_driver_init = new InitDriverPtr() {
            public void handler() {
		c64_common_driver_init ();
            }
        };
	
        public static InitDriverPtr c64pal_driver_init = new InitDriverPtr() {
            public void handler() {
		c64_pal = 1;
		c64_common_driver_init ();
            }
        };
	
        public static InitDriverPtr ultimax_driver_init = new InitDriverPtr() {
            public void handler() {
		ultimax = 1;
                c64_cia1_on = 0;
		c64_common_driver_init ();
/*TODO*///		if (cbm_rom[0].size==0) {
/*TODO*///		  printf("no cartridge found\n");
/*TODO*///		  exit(1);
		}
	};
	
        public static InitDriverPtr c64gs_driver_init = new InitDriverPtr() {
            public void handler() {
		c64_pal = 1;
		c64_tape_on = 0;
                c64_cia1_on = 1;
		c64_common_driver_init ();
            }
        };
	
/*TODO*///	void sx64_driver_init (void)
/*TODO*///	{
/*TODO*///		VC1541_CONFIG vc1541= { 1, 8 };
/*TODO*///		c64_tape_on = 0;
/*TODO*///		c64_pal = 1;
/*TODO*///		c64_common_driver_init ();
/*TODO*///		vc1541_config (0, 0, &vc1541);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64_driver_shutdown (void)
/*TODO*///	{
/*TODO*///		if (!ultimax)
/*TODO*///		{
/*TODO*///			cbm_drive_close ();
/*TODO*///		}
/*TODO*///		if (c64_tape_on != 0)
/*TODO*///			vc20_tape_close ();
/*TODO*///	}
	
	public static InitMachinePtr c64_common_init_machine = new InitMachinePtr() { public void handler() 
	{
            System.out.println("c64_common_init_machine");
            c64_basic=new UBytePtr(memory_region(REGION_CPU1), 0x10000);
			c64_kernal=new UBytePtr(memory_region(REGION_CPU1), 0x12000);
			c64_chargen=new UBytePtr(memory_region(REGION_CPU1), 0x14000);
			c64_colorram=new UBytePtr(memory_region(REGION_CPU1), 0x15000);
			c64_roml=new UBytePtr(memory_region(REGION_CPU1), 0x15400);
			c64_romh=new UBytePtr(memory_region(REGION_CPU1), 0x17400);
/*TODO*///	#ifdef VC1541
/*TODO*///		vc1541_reset ();
/*TODO*///	#endif
/*TODO*///		sid6581_0_configure(SID8580);
		if (c64_cia1_on != 0)
		{
			cbm_serial_reset_write (0);
/*TODO*///			cbm_drive_0_config (SERIAL8ON()!=0 ? SERIAL : 0, c65!=0?10:8);
/*TODO*///			cbm_drive_1_config (SERIAL9ON()!=0 ? SERIAL : 0, c65!=0?11:9);
			serial_clock = serial_data = serial_atn = 1;
		}
		cia6526_reset ();
		c64_vicaddr = new UBytePtr(c64_memory);
		vicirq = 0;
                cia0irq = 0;
		c64_port6510 = 0xff;
		c64_ddr6510 = 0;
	} };
	
	public static InitMachinePtr c64_init_machine = new InitMachinePtr() { public void handler() 
	{
		c64_common_init_machine.handler();
	
		c64_rom_recognition ();
		c64_rom_load();

/*TODO*///		if (c128 != 0)
/*TODO*///			c128_bankswitch_64 (1);
		if (ultimax==0)
			c64_bankswitch (1);
	} };
	
/*TODO*///	void c64_shutdown_machine (void)
/*TODO*///	{
/*TODO*///	}
/*TODO*///	
/*TODO*///	int c64_rom_id (int id)
/*TODO*///	{
/*TODO*///		/* magic lowrom at offset 0x8003: $c3 $c2 $cd $38 $30 */
/*TODO*///		/* jumped to offset 0 (0x8000) */
/*TODO*///		int retval = 0;
/*TODO*///		unsigned char magic[] =
/*TODO*///		{0xc3, 0xc2, 0xcd, 0x38, 0x30}, buffer[sizeof (magic)];
/*TODO*///		FILE *romfile;
/*TODO*///		char *cp;
/*TODO*///	
/*TODO*///		logerror("c64_rom_id %s\n", device_filename(IO_CARTSLOT,id));
/*TODO*///		retval = 0;
/*TODO*///		if (!(romfile = (FILE*)image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE_R, 0)))
/*TODO*///		{
/*TODO*///			logerror("rom %s not found\n", device_filename(IO_CARTSLOT,id));
/*TODO*///			return 0;
/*TODO*///		}
/*TODO*///	
/*TODO*///		osd_fseek (romfile, 3, SEEK_SET);
/*TODO*///		osd_fread (romfile, buffer, sizeof (magic));
/*TODO*///		osd_fclose (romfile);
/*TODO*///	
/*TODO*///		if (memcmp (magic, buffer, sizeof (magic)) == 0)
/*TODO*///		{
/*TODO*///			/* cartridgetype=CartridgeC64; */
/*TODO*///			retval = 1;
/*TODO*///		}
/*TODO*///		else if ((cp = strrchr (device_filename(IO_CARTSLOT,id), '.')) != NULL)
/*TODO*///		{
/*TODO*///			if ((stricmp (cp + 1, "prg") == 0)
/*TODO*///				|| (stricmp (cp + 1, "crt") == 0)
/*TODO*///				|| (stricmp (cp + 1, "80") == 0)
/*TODO*///				|| (stricmp (cp + 1, "90") == 0)
/*TODO*///				|| (stricmp (cp + 1, "e0") == 0)
/*TODO*///				|| (stricmp (cp + 1, "f0") == 0)
/*TODO*///				|| (stricmp (cp + 1, "a0") == 0)
/*TODO*///				|| (stricmp (cp + 1, "b0") == 0)
/*TODO*///				|| (stricmp (cp + 1, "lo") == 0) || (stricmp (cp + 1, "hi") == 0))
/*TODO*///				retval = 1;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (retval != 0)
/*TODO*///			logerror("rom %s recognized\n", device_filename(IO_CARTSLOT,id) );
/*TODO*///		else
/*TODO*///			logerror("rom %s not recognized\n", device_filename(IO_CARTSLOT,id));
/*TODO*///		return retval;
/*TODO*///	}
	
	public static boolean BETWEEN(int value1, int value2, int bottom, int top){
	    return ( ((value2)>=(bottom))&&((value1)<(top)) );
        }
	
	public static void c64_rom_recognition ()
	{
		int i;
		cartridgetype=CartridgeAuto;
		for (i=0; (i<cbm_rom.length)
				 &&(cbm_rom[i].size!=0); i++) {
			cartridge=1;
			if ( BETWEEN(0xa000, 0xbfff, cbm_rom[i].addr,
						 cbm_rom[i].addr+cbm_rom[i].size) ) {
				cartridgetype=CartridgeC64;
			} else if ( BETWEEN(0xe000, 0xffff, cbm_rom[i].addr,
								cbm_rom[i].addr+cbm_rom[i].size) ) {
				cartridgetype=CartridgeUltimax;
			}
		}
		if (i==4) cartridgetype=CartridgeSuperGames;
		if (i==32) cartridgetype=CartridgeRobocop2;
	}
	
	public static void c64_rom_load()
	{
		int i;
	
		c64_exrom = 1;
		c64_game = 1;
		if (cartridge != 0)
		{
			if (AUTO_MODULE()!=0 && (cartridgetype == CartridgeAuto))
			{
				logerror("Cartridge type not recognized using Machine type\n");
			}
			if (C64_MODULE()!=0 && (cartridgetype == CartridgeUltimax))
			{
				logerror("Cartridge could be ultimax type!?\n");
			}
			if (ULTIMAX_MODULE()!=0 && (cartridgetype == CartridgeC64))
			{
				logerror("Cartridge could be c64 type!?\n");
			}
			if (C64_MODULE() != 0)
				cartridgetype = CartridgeC64;
			else if (ULTIMAX_MODULE() != 0)
				cartridgetype = CartridgeUltimax;
			else if (SUPERGAMES_MODULE() != 0)
				cartridgetype = CartridgeSuperGames;
			else if (ROBOCOP2_MODULE() != 0)
				cartridgetype = CartridgeRobocop2;
			if (ultimax!=0 || (cartridgetype == CartridgeUltimax)) {
				c64_game = 0;
			} else {
				c64_exrom = 0;
			}
			if (ultimax != 0) {
				for (i=0; (i<cbm_rom.length)
						 &&(cbm_rom[i].size!=0); i++) {
					if (cbm_rom[i].addr==CBM_ROM_ADDR_LO) {
						memcpy(new UBytePtr(c64_memory, 0x8000+0x2000-cbm_rom[i].size),
							   cbm_rom[i].chip, cbm_rom[i].size);
					} else if ((cbm_rom[i].addr==CBM_ROM_ADDR_HI)
							   ||(cbm_rom[i].addr==CBM_ROM_ADDR_UNKNOWN)) {
						memcpy(new UBytePtr(c64_memory, 0xe000+0x2000-cbm_rom[i].size),
							   cbm_rom[i].chip, cbm_rom[i].size);
					} else {
						memcpy(new UBytePtr(c64_memory, cbm_rom[i].addr), cbm_rom[i].chip,
							   cbm_rom[i].size);
					}
				}
	        } else if ( (cartridgetype==CartridgeRobocop2)
						||(cartridgetype==CartridgeSuperGames) ) {
				roml=null;
				romh=null;
				for (i=0; (i<cbm_rom.length)
						 &&(cbm_rom[i].size!=0); i++) {
					if (roml==null
						&& ((cbm_rom[i].addr==CBM_ROM_ADDR_UNKNOWN)
							||(cbm_rom[i].addr==CBM_ROM_ADDR_LO)
							||(cbm_rom[i].addr==0x8000)) ) {
						roml=cbm_rom[i].chip;
					}
					if (romh==null
						&& ((cbm_rom[i].addr==CBM_ROM_ADDR_HI)
							||(cbm_rom[i].addr==0xa000) ) ){
						romh=cbm_rom[i].chip;
					}
					if (romh==null
						&& (cbm_rom[i].addr==0x8000)
						&&(cbm_rom[i].size==0x4000) ){
						romh=new UBytePtr(cbm_rom[i].chip, 0x2000);
					}
				}
			} else /*if ((cartridgetype == CartridgeC64)||
					 (cartridgetype == CartridgeUltimax) )*/{
				roml=c64_roml;
				romh=c64_romh;
				memset(roml, 0, 0x2000);
				memset(romh, 0, 0x2000);
				for (i=0; (i<cbm_rom.length)
						 &&(cbm_rom[i].size!=0); i++) {
					if ((cbm_rom[i].addr==CBM_ROM_ADDR_UNKNOWN)
						||(cbm_rom[i].addr==CBM_ROM_ADDR_LO) ) {
						memcpy(new UBytePtr(roml, 0x2000-cbm_rom[i].size),
							   cbm_rom[i].chip, cbm_rom[i].size);
					} else if ( ((cartridgetype == CartridgeC64)
						  &&(cbm_rom[i].addr==CBM_ROM_ADDR_HI))
						 ||((cartridgetype==CartridgeUltimax)
							&&(cbm_rom[i].addr==CBM_ROM_ADDR_HI)) ) {
						memcpy(new UBytePtr(romh, 0x2000-cbm_rom[i].size),
							   cbm_rom[i].chip, cbm_rom[i].size);
					} else if (cbm_rom[i].addr<0xc000) {
						memcpy(new UBytePtr(roml, cbm_rom[i].addr-0x8000), cbm_rom[i].chip,
							   cbm_rom[i].size);
					} else {
						memcpy(new UBytePtr(romh, cbm_rom[i].addr-0xe000),
							   cbm_rom[i].chip, cbm_rom[i].size);
					}
				}
			}
		}
	}
        
        static int quickload = 0;
		static int nmilevel = 0;
		static int monitor=-1;
	
	public static InterruptPtr c64_frame_interrupt = new InterruptPtr() { public int handler() 
	{
		
		int value, value2;
	
/*TODO*///		sid6581_update();
		if (nmilevel != KEY_RESTORE())
		{
			if (c128 != 0) {
				if (cpu_getactivecpu()==0) { /* z80 */
					cpu_set_nmi_line (0, KEY_RESTORE());
				} else {
					cpu_set_nmi_line (1, KEY_RESTORE());
				}
			} else {
				cpu_set_nmi_line (0, KEY_RESTORE());
			}
			nmilevel = KEY_RESTORE();
		}
	
/*TODO*///		if (!quickload && QUICKLOAD) {
/*TODO*///			if (c65 != 0) {
/*TODO*///				cbm_c65_quick_open (0, 0, c64_memory);
/*TODO*///			} else
/*TODO*///				cbm_quick_open (0, 0, c64_memory);
/*TODO*///		}
/*TODO*///		quickload = QUICKLOAD;
/*TODO*///	
/*TODO*///		if (c128 != 0) {
/*TODO*///			if (MONITOR_TV!=monitor) {
/*TODO*///				if (MONITOR_TV != 0) {
/*TODO*///					vic2_set_rastering(0);
/*TODO*///					vdc8563_set_rastering(1);
/*TODO*///					osd_set_visible_area(0,655,0,215);
/*TODO*///				} else {
/*TODO*///					vic2_set_rastering(1);
/*TODO*///					vdc8563_set_rastering(0);
/*TODO*///					osd_set_visible_area(0,335,0,215);
/*TODO*///				}
/*TODO*///				vdc8563_update();
/*TODO*///				monitor=MONITOR_TV;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
		value = 0xff;
/*TODO*///		if (c128 != 0) {
/*TODO*///			if (C128_KEY_CURSOR_DOWN != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C128_KEY_F5 != 0)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (C128_KEY_F3 != 0)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (C128_KEY_F1 != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C128_KEY_F7 != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (C128_KEY_CURSOR_RIGHT != 0)
/*TODO*///				value &= ~4;
/*TODO*///		} else if (c65 != 0) {
/*TODO*///			if (C65_KEY_CURSOR_DOWN != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C65_KEY_F5 != 0)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (C65_KEY_F3 != 0)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (C65_KEY_F1 != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C65_KEY_F7 != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (C65_KEY_CURSOR_RIGHT != 0)
/*TODO*///				value &= ~4;
/*TODO*///		} else {
			if (KEY_CURSOR_DOWN() != 0)
				value &= ~0x80;
			if (KEY_F5() != 0)
				value &= ~0x40;
			if (KEY_F3() != 0)
				value &= ~0x20;
			if (KEY_F1() != 0)
				value &= ~0x10;
			if (KEY_F7() != 0)
				value &= ~8;
			if (KEY_CURSOR_RIGHT() != 0)
				value &= ~4;
/*TODO*///		}
		if (KEY_RETURN() != 0)
			value &= ~2;
		if (KEY_DEL() != 0)
			value &= ~1;
		c64_keyline[0] = value;
	
		value = 0xff;
		if (KEY_LEFT_SHIFT() != 0)
			value &= ~0x80;
		if (KEY_E() != 0)
			value &= ~0x40;
		if (KEY_S() != 0)
			value &= ~0x20;
		if (KEY_Z() != 0)
			value &= ~0x10;
		if (KEY_4() != 0) value &= ~8;
		if (KEY_A() != 0)
			value &= ~4;
		if (KEY_W() != 0)
			value &= ~2;
		if (KEY_3() != 0) value &= ~1;
		c64_keyline[1] = value;
	
		value = 0xff;
		if (KEY_X() != 0)
			value &= ~0x80;
		if (KEY_T() != 0)
			value &= ~0x40;
		if (KEY_F() != 0)
			value &= ~0x20;
		if (KEY_C() != 0)
			value &= ~0x10;
		if (KEY_6() != 0) value &= ~8;
		if (KEY_D() != 0)
			value &= ~4;
		if (KEY_R() != 0)
			value &= ~2;
		if (KEY_5() != 0) value &= ~1;
		c64_keyline[2] = value;
	
		value = 0xff;
		if (KEY_V() != 0)
			value &= ~0x80;
		if (KEY_U() != 0)
			value &= ~0x40;
		if (KEY_H() != 0)
			value &= ~0x20;
		if (KEY_B() != 0)
			value &= ~0x10;
		if (KEY_8() != 0) value &= ~8;
		if (KEY_G() != 0)
			value &= ~4;
		if (KEY_Y() != 0)
			value &= ~2;
		if (KEY_7() != 0) value &= ~1;
		c64_keyline[3] = value;
	
		value = 0xff;
		if (KEY_N() != 0)
			value &= ~0x80;
		if (KEY_O() != 0)
			value &= ~0x40;
		if (KEY_K() != 0)
			value &= ~0x20;
		if (KEY_M() != 0)
			value &= ~0x10;
		if (KEY_0() != 0)
			value &= ~8;
		if (KEY_J() != 0)
			value &= ~4;
		if (KEY_I() != 0)
			value &= ~2;
		if (KEY_9() != 0)
			value &= ~1;
		c64_keyline[4] = value;
	
		value = 0xff;
		if (KEY_COMMA() != 0)
			value &= ~0x80;
		if (KEY_ATSIGN() != 0)
			value &= ~0x40;
		if (KEY_SEMICOLON() != 0)
			value &= ~0x20;
		if (KEY_POINT() != 0)
			value &= ~0x10;
		if (KEY_MINUS() != 0)
			value &= ~8;
		if (KEY_L() != 0)
			value &= ~4;
		if (KEY_P() != 0)
			value &= ~2;
		if (KEY_PLUS() != 0)
			value &= ~1;
		c64_keyline[5] = value;
	
	
		value = 0xff;
		if (KEY_SLASH() != 0)
			value &= ~0x80;
		if (KEY_ARROW_UP() != 0)
			value &= ~0x40;
		if (KEY_EQUALS() != 0)
			value &= ~0x20;
/*TODO*///		if (c128 != 0) {
/*TODO*///			if (C128_KEY_RIGHT_SHIFT != 0)
/*TODO*///			value &= ~0x10;
/*TODO*///		} else if (c65 != 0) {
/*TODO*///			if (C65_KEY_RIGHT_SHIFT != 0)
/*TODO*///			value &= ~0x10;
/*TODO*///		} else {
			if (KEY_RIGHT_SHIFT() != 0)
			value &= ~0x10;
/*TODO*///		}
		if (KEY_HOME() != 0)
			value &= ~8;
		if (KEY_COLON() != 0)
			value &= ~4;
		if (KEY_ASTERIX() != 0)
			value &= ~2;
		if (KEY_POUND() != 0)
			value &= ~1;
		c64_keyline[6] = value;
	
		value = 0xff;
/*TODO*///		if (c65 != 0) {
/*TODO*///			if (C65_KEY_STOP != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C65_KEY_SPACE != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C65_KEY_CTRL != 0)
/*TODO*///				value &= ~4;
/*TODO*///		} else {
			if (KEY_STOP() != 0)
				value &= ~0x80;
			if (KEY_SPACE() != 0)
				value &= ~0x10;
			if (KEY_CTRL() != 0)
				value &= ~4;
/*TODO*///		}
		if (KEY_Q() != 0)
			value &= ~0x40;
		if (KEY_CBM() != 0)
			value &= ~0x20;
		if (KEY_2() != 0) value &= ~8;
		if (KEY_ARROW_LEFT() != 0)
			value &= ~2;
		if (KEY_1() != 0) value &= ~1;
		c64_keyline[7] = value;
	
		value = 0xff;
/*TODO*///		if (JOYSTICK1||JOYSTICK1_2BUTTON) {
/*TODO*///			if (JOYSTICK_1_BUTTON != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (JOYSTICK_1_RIGHT != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (JOYSTICK_1_LEFT != 0)
/*TODO*///				value &= ~4;
/*TODO*///			if (JOYSTICK_1_DOWN != 0)
/*TODO*///				value &= ~2;
/*TODO*///			if (JOYSTICK_1_UP != 0)
/*TODO*///				value &= ~1;
/*TODO*///		} else if (PADDLES12 != 0) {
/*TODO*///			if (PADDLE2_BUTTON != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (PADDLE1_BUTTON != 0)
/*TODO*///				value &= ~4;
/*TODO*///		} else if (MOUSE1 != 0) {
/*TODO*///			if (MOUSE1_BUTTON1 != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (MOUSE1_BUTTON2 != 0)
/*TODO*///				value &= ~1;
/*TODO*///		}
		c64_keyline[8] = value;
	
		value2 = 0xff;
/*TODO*///		if (JOYSTICK2||JOYSTICK2_2BUTTON) {
/*TODO*///			if (JOYSTICK_2_BUTTON != 0)
/*TODO*///				value2 &= ~0x10;
/*TODO*///			if (JOYSTICK_2_RIGHT != 0)
/*TODO*///				value2 &= ~8;
/*TODO*///			if (JOYSTICK_2_LEFT != 0)
/*TODO*///				value2 &= ~4;
/*TODO*///			if (JOYSTICK_2_DOWN != 0)
/*TODO*///				value2 &= ~2;
/*TODO*///			if (JOYSTICK_2_UP != 0)
/*TODO*///				value2 &= ~1;
/*TODO*///		} else if (PADDLES34 != 0) {
/*TODO*///			if (PADDLE4_BUTTON != 0)
/*TODO*///				value2 &= ~8;
/*TODO*///			if (PADDLE3_BUTTON != 0)
/*TODO*///				value2 &= ~4;
/*TODO*///		} else if (MOUSE2 != 0) {
/*TODO*///			if (MOUSE2_BUTTON1 != 0)
/*TODO*///				value2 &= ~0x10;
/*TODO*///			if (MOUSE2_BUTTON2 != 0)
/*TODO*///				value2 &= ~1;
/*TODO*///		}
		c64_keyline[9] = value2;
	
/*TODO*///		if (c128 != 0) {
/*TODO*///			value = 0xff;
/*TODO*///			if (KEY_NUM1 != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (KEY_NUM7 != 0)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (KEY_NUM4 != 0)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (KEY_NUM2 != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (KEY_TAB != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (KEY_NUM5 != 0)
/*TODO*///				value &= ~4;
/*TODO*///			if (KEY_NUM8 != 0)
/*TODO*///				value &= ~2;
/*TODO*///			if (KEY_HELP != 0)
/*TODO*///				value &= ~1;
/*TODO*///			c128_keyline[0] = value;
/*TODO*///	
/*TODO*///			value = 0xff;
/*TODO*///			if (KEY_NUM3 != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (KEY_NUM9 != 0)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (KEY_NUM6 != 0)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (KEY_NUMENTER != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (KEY_LINEFEED != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (KEY_NUMMINUS != 0)
/*TODO*///				value &= ~4;
/*TODO*///			if (KEY_NUMPLUS != 0)
/*TODO*///				value &= ~2;
/*TODO*///			if (KEY_ESCAPE != 0)
/*TODO*///				value &= ~1;
/*TODO*///			c128_keyline[1] = value;
/*TODO*///	
/*TODO*///			value = 0xff;
/*TODO*///			if (KEY_NOSCRL != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (KEY_RIGHT != 0)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (KEY_LEFT != 0)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (KEY_DOWN != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (KEY_UP != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (KEY_NUMPOINT != 0)
/*TODO*///				value &= ~4;
/*TODO*///			if (KEY_NUM0 != 0)
/*TODO*///				value &= ~2;
/*TODO*///			if (KEY_ALT != 0)
/*TODO*///				value &= ~1;
/*TODO*///			c128_keyline[2] = value;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (c65 != 0) {
/*TODO*///			value = 0xff;
/*TODO*///			if (C65_KEY_ESCAPE != 0)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C65_KEY_F13 != 0)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (C65_KEY_F11 != 0)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (C65_KEY_F9 != 0)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C65_KEY_HELP != 0)
/*TODO*///				value &= ~8;
/*TODO*///			if (C65_KEY_ALT != 0) /* non blocking */
/*TODO*///				value &= ~4;
/*TODO*///			if (C65_KEY_TAB != 0)
/*TODO*///				value &= ~2;
/*TODO*///			if (C65_KEY_NOSCRL != 0)
/*TODO*///				value &= ~1;
/*TODO*///			c65_keyline = value;
/*TODO*///		}
	
		vic2_frame_interrupt ();
	
/*TODO*///		if (c64_tape_on != 0) {
/*TODO*///			vc20_tape_config (DATASSETTE, DATASSETTE_TONE);
/*TODO*///			vc20_tape_buttons (DATASSETTE_PLAY, DATASSETTE_RECORD, DATASSETTE_STOP);
/*TODO*///		}
/*TODO*///		osd_led_w (1 /*KB_CAPSLOCK_FLAG */ , KEY_SHIFTLOCK ? 1 : 0);
/*TODO*///		osd_led_w (0 /*KB_NUMLOCK_FLAG */ , JOYSTICK_SWAP ? 1 : 0);
	
		return ignore_interrupt.handler();
	} };
/*TODO*///	
/*TODO*///	void c64_state(PRASTER *This)
/*TODO*///	{
/*TODO*///		int y;
/*TODO*///		char text[70];
/*TODO*///	
/*TODO*///		y = Machine.visible_area.max_y + 1 - Machine.uifont.height;
/*TODO*///	
/*TODO*///	#if VERBOSE_DBG
/*TODO*///	#if 0
/*TODO*///		cia6526_status (text, sizeof (text));
/*TODO*///		praster_draw_text (This, text, &y);
/*TODO*///	
/*TODO*///		snprintf (text, sizeof(text), "c64 vic:%.4x m6510:%d exrom:%d game:%d",
/*TODO*///				  c64_vicaddr - c64_memory, c64_port6510 & 7,
/*TODO*///				  c64_exrom, c64_game);
/*TODO*///		praster_draw_text (This, text, &y);
/*TODO*///	#endif
/*TODO*///	
/*TODO*///		vdc8563_status(text, sizeof(text));
/*TODO*///		praster_draw_text (This, text, &y);
/*TODO*///	#endif
/*TODO*///	
/*TODO*///		vc20_tape_status (text, sizeof (text));
/*TODO*///		praster_draw_text (This, text, &y);
/*TODO*///	#ifdef VC1541
/*TODO*///		vc1541_drive_status (text, sizeof (text));
/*TODO*///	#else
/*TODO*///		cbm_drive_0_status (text, sizeof (text));
/*TODO*///	#endif
/*TODO*///		praster_draw_text (This, text, &y);
/*TODO*///	
/*TODO*///		cbm_drive_1_status (text, sizeof (text));
/*TODO*///		praster_draw_text (This, text, &y);
/*TODO*///	}
	
}

