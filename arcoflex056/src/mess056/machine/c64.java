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
import static common.ptr.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mess056.includes.c64H.*;
import static mess056.includes.cbmserbH.*;
import static mess056.machine.vc20tape.*;
import static mess056.includes.vc20tapeH.*;
import static mess056.machine.cbmserb.*;
import static mess056.machine.cia6526.*;
import static mess056.vidhrdw.vic6567.*;

public class c64
{
	
/*TODO*///	#define VERBOSE_DBG 1
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	unsigned char c65_keyline = { 0xff };
	public static int c65=0;
/*TODO*///	UINT8 c65_6511_port=0xff;
/*TODO*///	
/*TODO*///	/* computer is a c128 */
/*TODO*///	int c128 = 0;
/*TODO*///	
/*TODO*///	UINT8 c128_keyline[3] =
/*TODO*///	{0xff, 0xff, 0xff};
	
	
	/* keyboard lines */
	public static int c64_keyline[] =
	{
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff
	};
	
/*TODO*///	/* expansion port lines input */
/*TODO*///	int c64_pal = 0;
	public static int c64_game=1, c64_exrom=1;
/*TODO*///	
/*TODO*///	/* cpu port */
	public static int c64_port6510, c64_ddr6510;
/*TODO*///	int c128_va1617;
	public static UBytePtr c64_vicaddr=new UBytePtr(), c128_vicaddr=new UBytePtr();
	public static UBytePtr c64_memory = new UBytePtr();
        public static UBytePtr c64_colorram = new UBytePtr();
        public static UBytePtr c64_basic = new UBytePtr();
        public static UBytePtr c64_kernal = new UBytePtr();
        public static UBytePtr c64_chargen = new UBytePtr();
        public static UBytePtr c64_roml=null;
        public static UBytePtr c64_romh=null;

        public static UBytePtr roml=null, romh=null;
/*TODO*///	static int ultimax = 0;
	public static int c64_tape_on = 1;
	public static int c64_cia1_on = 1;
/*TODO*///	static UINT8 cartridge = 0;
/*TODO*///	static enum
/*TODO*///	{
/*TODO*///		CartridgeAuto = 0, CartridgeUltimax, CartridgeC64,
/*TODO*///		CartridgeSuperGames, CartridgeRobocop2
/*TODO*///	}
/*TODO*///	cartridgetype = CartridgeAuto;
	public static int cia0porta, cia0portb;
	public static int serial_clock, serial_data, serial_atn;
	public static int vicirq = 0, cia0irq = 0, cia1irq = 0;
        
        static int nmilevel = 0;
	
	static void c64_nmi()
	{
	    
	    if (nmilevel != KEY_RESTORE()||cia1irq!=0)
	    {
/*TODO*///		if (c128) {
/*TODO*///		    if (cpu_getactivecpu()==0) { /* z80 */
/*TODO*///			cpu_set_nmi_line (0, KEY_RESTORE||cia1irq);
/*TODO*///		    } else {
/*TODO*///			cpu_set_nmi_line (1, KEY_RESTORE||cia1irq);
/*TODO*///		    }
/*TODO*///		} else {
		    cpu_set_nmi_line (0, (KEY_RESTORE()!=0||cia1irq!=0)?1:0);
/*TODO*///		}
		nmilevel = KEY_RESTORE()!=0||cia1irq!=0 ? 1: 0;
	    }
	}
	
	
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
/*TODO*///	int c64_cia0_port_a_r (int offset)
/*TODO*///	{
/*TODO*///	    int value = 0xff;
/*TODO*///	
/*TODO*///	    if (!(cia0portb&0x80)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x80)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x80)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x80)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x80)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x80)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x80)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x80)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x80)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x40)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x40)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x40)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x40)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x40)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x40)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x40)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x40)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x40)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x20)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x20)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x20)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x20)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x20)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x20)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x20)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x20)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x20)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x10)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x10)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x10)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x10)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x10)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x10)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x10)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x10)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x10)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x08)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x08)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x08)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x08)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x08)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x08)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x08)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x08)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x08)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x04)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x04)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x04)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x04)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x04)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x04)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x04)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x04)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x04)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x02)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x02)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x02)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x02)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x02)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x02)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x02)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x02)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x02)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	    if (!(cia0portb&0x01)) {
/*TODO*///		UINT8 t=0xff;
/*TODO*///		if (!(c64_keyline[7]&0x01)) t&=~0x80;
/*TODO*///		if (!(c64_keyline[6]&0x01)) t&=~0x40;
/*TODO*///		if (!(c64_keyline[5]&0x01)) t&=~0x20;
/*TODO*///		if (!(c64_keyline[4]&0x01)) t&=~0x10;
/*TODO*///		if (!(c64_keyline[3]&0x01)) t&=~0x08;
/*TODO*///		if (!(c64_keyline[2]&0x01)) t&=~0x04;
/*TODO*///		if (!(c64_keyline[1]&0x01)) t&=~0x02;
/*TODO*///		if (!(c64_keyline[0]&0x01)) t&=~0x01;
/*TODO*///		value &=t;
/*TODO*///	    }
/*TODO*///	
/*TODO*///	    if (JOYSTICK_SWAP) value &= c64_keyline[8];
/*TODO*///	    else value &= c64_keyline[9];
/*TODO*///	
/*TODO*///	    return value;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int c64_cia0_port_b_r (int offset)
/*TODO*///	{
/*TODO*///	    int value = 0xff;
/*TODO*///	
/*TODO*///	    if (!(cia0porta & 0x80)) value &= c64_keyline[7];
/*TODO*///	    if (!(cia0porta & 0x40)) value &= c64_keyline[6];
/*TODO*///	    if (!(cia0porta & 0x20)) value &= c64_keyline[5];
/*TODO*///	    if (!(cia0porta & 0x10)) value &= c64_keyline[4];
/*TODO*///	    if (!(cia0porta & 8)) value &= c64_keyline[3];
/*TODO*///	    if (!(cia0porta & 4)) value &= c64_keyline[2];
/*TODO*///	    if (!(cia0porta & 2)) value &= c64_keyline[1];
/*TODO*///	    if (!(cia0porta & 1)) value &= c64_keyline[0];
/*TODO*///	
/*TODO*///	    if (JOYSTICK_SWAP) value &= c64_keyline[9];
/*TODO*///	    else value &= c64_keyline[8];
/*TODO*///	
/*TODO*///	    if (c128)
/*TODO*///	    {
/*TODO*///		if (!vic2e_k0_r ())
/*TODO*///		    value &= c128_keyline[0];
/*TODO*///		if (!vic2e_k1_r ())
/*TODO*///		    value &= c128_keyline[1];
/*TODO*///		if (!vic2e_k2_r ())
/*TODO*///		    value &= c128_keyline[2];
/*TODO*///	    }
/*TODO*///	    if (c65) {
/*TODO*///		if (!(c65_6511_port&2))
/*TODO*///		    value&=c65_keyline;
/*TODO*///	    }
/*TODO*///	
/*TODO*///	    return value;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64_cia0_port_a_w (int offset, int data)
/*TODO*///	{
/*TODO*///	    cia0porta = data;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_cia0_port_b_w (int offset, int data)
/*TODO*///	{
/*TODO*///	    cia0portb =data;
/*TODO*///	    vic2_lightpen_write (data & 0x10);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_irq (int level)
/*TODO*///	{
/*TODO*///		static int old_level = 0;
/*TODO*///	
/*TODO*///		if (level != old_level)
/*TODO*///		{
/*TODO*///			DBG_LOG (3, "mos6510", ("irq %s\n", level ? "start" : "end"));
/*TODO*///			if (c128) {
/*TODO*///				if (0&&(cpu_getactivecpu()==0)) {
/*TODO*///					cpu_set_irq_line (0, Z80_IRQ_INT, level);
/*TODO*///				} else {
/*TODO*///					cpu_set_irq_line (1, M6510_IRQ_LINE, level);
/*TODO*///				}
/*TODO*///			} else {
/*TODO*///				cpu_set_irq_line (0, M6510_IRQ_LINE, level);
/*TODO*///			}
/*TODO*///			old_level = level;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	WRITE_HANDLER(c64_tape_read)
/*TODO*///	{
/*TODO*///		cia6526_0_set_input_flag (data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_cia0_interrupt (int level)
/*TODO*///	{
/*TODO*///		if (level != cia0irq)
/*TODO*///		{
/*TODO*///			c64_irq (level || vicirq);
/*TODO*///			cia0irq = level;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64_vic_interrupt (int level)
/*TODO*///	{
/*TODO*///	#if 1
/*TODO*///		if (level != vicirq)
/*TODO*///		{
/*TODO*///			c64_irq (level || cia0irq);
/*TODO*///			vicirq = level;
/*TODO*///		}
/*TODO*///	#endif
/*TODO*///	}
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
/*TODO*///	int c64_cia1_port_a_r (int offset)
/*TODO*///	{
/*TODO*///		int value = 0xff;
/*TODO*///	
/*TODO*///		if (!serial_clock || !cbm_serial_clock_read ())
/*TODO*///			value &= ~0x40;
/*TODO*///		if (!serial_data || !cbm_serial_data_read ())
/*TODO*///			value &= ~0x80;
/*TODO*///		return value;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_cia1_port_a_w (int offset, int data)
/*TODO*///	{
/*TODO*///		static int helper[4] =
/*TODO*///		{0xc000, 0x8000, 0x4000, 0x0000};
/*TODO*///	
/*TODO*///		cbm_serial_clock_write (serial_clock = !(data & 0x10));
/*TODO*///		cbm_serial_data_write (serial_data = !(data & 0x20));
/*TODO*///		cbm_serial_atn_write (serial_atn = !(data & 8));
/*TODO*///		c64_vicaddr = c64_memory + helper[data & 3];
/*TODO*///		if (c128) {
/*TODO*///			c128_vicaddr = c64_memory + helper[data & 3] + c128_va1617;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_cia1_interrupt (int level)
/*TODO*///	{
/*TODO*///		cia1irq=level;
/*TODO*///		c64_nmi();
/*TODO*///	#if 0
/*TODO*///		static int old_level = 0;
/*TODO*///	
/*TODO*///		if (level != old_level)
/*TODO*///		{
/*TODO*///			DBG_LOG (1, "mos6510", ("nmi %s\n", level ? "start" : "end"));
/*TODO*///	
/*TODO*///			/*      cpu_set_nmi_line(0, level); */
/*TODO*///			old_level = level;
/*TODO*///		}
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	struct cia6526_interface c64_cia0 =
/*TODO*///	{
/*TODO*///		c64_cia0_port_a_r,
/*TODO*///		c64_cia0_port_b_r,
/*TODO*///		c64_cia0_port_a_w,
/*TODO*///		c64_cia0_port_b_w,
/*TODO*///		0,								   /*c64_cia0_pc_w */
/*TODO*///		0,								   /*c64_cia0_sp_r */
/*TODO*///		0,								   /*c64_cia0_sp_w */
/*TODO*///		0,								   /*c64_cia0_cnt_r */
/*TODO*///		0,								   /*c64_cia0_cnt_w */
/*TODO*///		c64_cia0_interrupt,
/*TODO*///		0xff, 0xff, 0
/*TODO*///	}, c64_cia1 =
/*TODO*///	{
/*TODO*///		c64_cia1_port_a_r,
/*TODO*///		0,								   /*c64_cia1_port_b_r, */
/*TODO*///		c64_cia1_port_a_w,
/*TODO*///		0,								   /*c64_cia1_port_b_w, */
/*TODO*///		0,								   /*c64_cia1_pc_w */
/*TODO*///		0,								   /*c64_cia1_sp_r */
/*TODO*///		0,								   /*c64_cia1_sp_w */
/*TODO*///		0,								   /*c64_cia1_cnt_r */
/*TODO*///		0,								   /*c64_cia1_cnt_w */
/*TODO*///		c64_cia1_interrupt,
/*TODO*///		0xc7, 0xff, 0
/*TODO*///	};
/*TODO*///	
/*TODO*///	static void c64_bankswitch (int reset);
/*TODO*///	static void c64_robocop2_w(int offset, int value)
/*TODO*///	{
/*TODO*///		/* robocop2 0xe00
/*TODO*///		 * 80 94 80 94 80
/*TODO*///		 * 80 81 80 82 83 80
/*TODO*///		 */
/*TODO*///		roml=cbm_rom[value&0xf].chip;
/*TODO*///		romh=cbm_rom[(value&0xf)+0x10].chip;
/*TODO*///		if (value & 0x80)
/*TODO*///			{
/*TODO*///				c64_game = value & 0x10;
/*TODO*///				c64_exrom = 1;
/*TODO*///			}
/*TODO*///		else
/*TODO*///			{
/*TODO*///				c64_game = c64_exrom = 1;
/*TODO*///			}
/*TODO*///		if (c128)
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
/*TODO*///		if (value & 4)
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
/*TODO*///		if (c128)
/*TODO*///			c128_bankswitch_64 (0);
/*TODO*///		else
/*TODO*///			c64_bankswitch (0);
/*TODO*///	}
	
	public static WriteHandlerPtr c64_write_io = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset < 0x400) {
			vic2_port_w.handler(offset & 0x3ff, data);
		} else if (offset < 0x800) {
/*TODO*///			sid6581_0_port_w (offset & 0x3ff, data);
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
		/*TODO*///else if (offset < 0x800)
		/*TODO*///	return sid6581_0_port_r (offset & 0x3ff);
		else if (offset < 0xc00)
			return c64_colorram.read(offset & 0x3ff);
		else if (offset < 0xd00)
			return cia6526_0_port_r.handler(offset & 0xff);
		else if (c64_cia1_on!=0 && (offset < 0xe00))
			return cia6526_1_port_r.handler(offset & 0xff);
/*TODO*///		DBG_LOG (1, "io read", ("%.3x\n", offset));
		return 0xff;
	} };
	
	/*
	 * two devices access bus, cpu and vic
	 *
	 * romh, roml chip select lines on expansion bus
	 * loram, hiram, charen bankswitching select by cpu
	 * exrom, game bankswitching select by cartridge
	 * va15, va14 bank select by cpu for vic
	 *
	 * exrom, game: normal c64 mode
	 * exrom, !game: ultimax mode
	 *
	 * romh: 8k rom at 0xa000 (hiram && !game && exrom)
	 * or 8k ram at 0xe000 (!game exrom)
	 * roml: 8k rom at 0x8000 (loram hiram !exrom)
	 * or 8k ram at 0x8000 (!game exrom)
	 * roml vic: upper 4k rom at 0x3000, 0x7000, 0xb000, 0xd000 (!game exrom)
	 *
	 * basic rom: 8k rom at 0xa000 (loram hiram game)
	 * kernal rom: 8k rom at 0xe000 (hiram !exrom, hiram game)
	 * char rom: 4k rom at 0xd000 (!exrom charen hiram
	 * game charen !hiram loram
	 * game charen hiram)
	 * cpu
	 *
	 * (write colorram)
	 * gr_w = !read&&!cas&&((address&0xf000)==0xd000)
	 *
	 * i_o = !game exrom !read ((address&0xf000)==0xd000)
	 * !game exrom ((address&0xf000)==0xd000)
	 * charen !hiram loram !read ((address&0xf000)==0xd000)
	 * charen !hiram loram ((address&0xf000)==0xd000)
	 * charen hiram !read ((address&0xf000)==0xd000)
	 * charen hiram ((address&0xf000)==0xd000)
	 *
	 * vic
	 * char rom: x101 (game, !exrom)
	 * romh: 0011 (!game, exrom)
	 *
	 * exrom !game (ultimax mode)
	 * addr    CPU     VIC-II
	 * ----    ---     ------
	 * 0000    RAM     RAM
	 * 1000    -       RAM
	 * 2000    -       RAM
	 * 3000    -       ROMH (upper half)
	 * 4000    -       RAM
	 * 5000    -       RAM
	 * 6000    -       RAM
	 * 7000    -       ROMH
	 * 8000    ROML    RAM
	 * 9000    ROML    RAM
	 * A000    -       RAM
	 * B000    -       ROMH
	 * C000    -       RAM
	 * D000    I/O     RAM
	 * E000    ROMH    RAM
	 * F000    ROMH    ROMH
	 */
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
		    || (loram!=0 && c64_exrom==0)) // for omega race cartridge
	//	    || (loram && hiram && !c64_exrom))
		{
			cpu_setbank (1, roml);
			memory_set_bankhandler_w (2, 0, MWA_RAM); // always ram: pitstop
		}
		else
		{
			cpu_setbank (1, new UBytePtr(c64_memory, 0x8000));
			memory_set_bankhandler_w (2, 0, MWA_RAM);
		}
	
/*TODO*///	#if 1
		if ((c64_game==0 && c64_exrom==0 && hiram!=0))
/*TODO*///		    /*|| (!c64_exrom)*/) // must be disabled for 8kb c64 cartridges! like space action, super expander, ...
/*TODO*///	#else
/*TODO*///		if ((!c64_game && c64_exrom && hiram)
/*TODO*///		    || (!c64_exrom) )
/*TODO*///	#endif
		{
			cpu_setbank (3, romh);
		}
		else if (loram!=0 && hiram!=0 &&c64_game!=0)
		{
			cpu_setbank (3, new UBytePtr(c64_basic));
		}
		else
		{
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
				cpu_setbank (5, c64_chargen);
			}
			else
			{
				cpu_setbank (5, new UBytePtr(c64_memory, 0xd000));
			}
		}
	
		if (c64_game==0 && c64_exrom!=0)
		{
			cpu_setbank (7, new UBytePtr(romh));
			memory_set_bankhandler_w (8, 0, MWA_NOP);
		}
		else
		{
			memory_set_bankhandler_w (8, 0, MWA_RAM);
			if (hiram != 0)
			{
				cpu_setbank (7, new UBytePtr(c64_kernal));
			}
			else
			{
				cpu_setbank (7, new UBytePtr(c64_memory, 0xe000));
			}
		}
		game=c64_game;
		exrom=c64_exrom;
		old = data;
	}
	
	/**
	  ddr bit 1 port line is output
	  port bit 1 port line is high
	
	  p0 output loram
	  p1 output hiram
	  p2 output charen
	  p3 output cassette data
	  p4 input cassette switch
	  p5 output cassette motor
	  p6,7 not available on M6510
	 */
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
		if (c64_tape_on != 0) {
			vc20_tape_write ((data & 8)!=0?0:1);
			vc20_tape_motor (data & 0x20);
		}
/*TODO*///		if (c128)
/*TODO*///			c128_bankswitch_64 (0);
/*TODO*///		else if (c65)
/*TODO*///			c65_bankswitch();
/*TODO*///		else if (ultimax == 0)
			c64_bankswitch (0);
            }
        };
	
    public static ReadHandlerPtr c64_m6510_port_r = new ReadHandlerPtr() {
        public int handler(int offset) {
		if (offset != 0)
		{
			int data = (c64_ddr6510 & c64_port6510) | (c64_ddr6510 ^ 0xff);
	
			if ((c64_tape_on!=0) && (c64_ddr6510 & 0x10)!=0 && vc20_tape_switch ()==0)
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
/*TODO*///		if (PADDLES34) {
/*TODO*///			if (which) pot4=PADDLE4_VALUE;
/*TODO*///			else pot3=PADDLE3_VALUE;
/*TODO*///		}
/*TODO*///		if (JOYSTICK2_2BUTTON&&which) {
/*TODO*///			if (JOYSTICK_2_BUTTON2) pot4=0x00;
/*TODO*///		}
/*TODO*///		if (MOUSE2) {
/*TODO*///			if (which) pot4=MOUSE2_Y;
/*TODO*///			else pot3=MOUSE2_X;
/*TODO*///		}
/*TODO*///		if (PADDLES12) {
/*TODO*///			if (which) pot2=PADDLE2_VALUE;
/*TODO*///			else pot1=PADDLE1_VALUE;
/*TODO*///		}
/*TODO*///		if (JOYSTICK1_2BUTTON&&which) {
/*TODO*///			if (JOYSTICK_1_BUTTON2) pot1=0x00;
/*TODO*///		}
/*TODO*///		if (MOUSE1) {
/*TODO*///			if (which) pot2=MOUSE1_Y;
/*TODO*///			else pot1=MOUSE1_X;
/*TODO*///		}
/*TODO*///		if (JOYSTICK_SWAP) {
/*TODO*///			temp=pot1;pot1=pot2;pot2=pot1;
/*TODO*///			temp=pot3;pot3=pot4;pot4=pot3;
/*TODO*///		}
/*TODO*///		switch (cia0porta & 0xc0) {
/*TODO*///		case 0x40:
/*TODO*///			if (which) return pot2;
/*TODO*///			return pot1;
/*TODO*///		case 0x80:
/*TODO*///			if (which) return pot4;
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
/*TODO*///	static int c64_dma_read (int offset)
/*TODO*///	{
/*TODO*///		if (!c64_game && c64_exrom)
/*TODO*///		{
/*TODO*///			if (offset < 0x3000)
/*TODO*///				return c64_memory[offset];
/*TODO*///			return c64_romh[offset & 0x1fff];
/*TODO*///		}
/*TODO*///		if (((c64_vicaddr - c64_memory + offset) & 0x7000) == 0x1000)
/*TODO*///			return c64_chargen[offset & 0xfff];
/*TODO*///		return c64_vicaddr[offset];
/*TODO*///	}
/*TODO*///	
/*TODO*///	static int c64_dma_read_ultimax (int offset)
/*TODO*///	{
/*TODO*///		if (offset < 0x3000)
/*TODO*///			return c64_memory[offset];
/*TODO*///		return c64_romh[offset & 0x1fff];
/*TODO*///	}
/*TODO*///	
/*TODO*///	static int c64_dma_read_color (int offset)
/*TODO*///	{
/*TODO*///		return c64_colorram[offset & 0x3ff] & 0xf;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void c64_common_driver_init (void)
/*TODO*///	{
/*TODO*///		/*    memset(c64_memory, 0, 0xfd00); */
/*TODO*///		if (ultimax == 0) {
/*TODO*///			c64_basic=memory_region(REGION_CPU1)+0x10000;
/*TODO*///			c64_kernal=memory_region(REGION_CPU1)+0x12000;
/*TODO*///			c64_chargen=memory_region(REGION_CPU1)+0x14000;
/*TODO*///			c64_colorram=memory_region(REGION_CPU1)+0x15000;
/*TODO*///			c64_roml=memory_region(REGION_CPU1)+0x15400;
/*TODO*///			c64_romh=memory_region(REGION_CPU1)+0x17400;
/*TODO*///	#if 0
/*TODO*///		{0x10000, 0x11fff, MWA_ROM, &c64_basic},	/* basic at 0xa000 */
/*TODO*///		{0x12000, 0x13fff, MWA_ROM, &c64_kernal},	/* kernal at 0xe000 */
/*TODO*///		{0x14000, 0x14fff, MWA_ROM, &c64_chargen},	/* charrom at 0xd000 */
/*TODO*///		{0x15000, 0x153ff, MWA_RAM, &c64_colorram},		/* colorram at 0xd800 */
/*TODO*///		{0x15400, 0x173ff, MWA_ROM, &c64_roml},	/* basic at 0xa000 */
/*TODO*///		{0x17400, 0x193ff, MWA_ROM, &c64_romh},	/* kernal at 0xe000 */
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///		if (c64_tape_on)
/*TODO*///			vc20_tape_open (c64_tape_read);
/*TODO*///	
/*TODO*///		if (c64_cia1_on)
/*TODO*///		{
/*TODO*///			cbm_drive_open ();
/*TODO*///	
/*TODO*///			cbm_drive_attach_fs (0);
/*TODO*///			cbm_drive_attach_fs (1);
/*TODO*///		}
/*TODO*///	
/*TODO*///		c64_cia0.todin50hz = c64_pal;
/*TODO*///		cia6526_config (0, &c64_cia0);
/*TODO*///		if (c64_cia1_on)
/*TODO*///		{
/*TODO*///			c64_cia1.todin50hz = c64_pal;
/*TODO*///			cia6526_config (1, &c64_cia1);
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (ultimax)
/*TODO*///		{
/*TODO*///			vic6567_init (0, c64_pal, c64_dma_read_ultimax, c64_dma_read_color,
/*TODO*///						  c64_vic_interrupt);
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			vic6567_init (0, c64_pal, c64_dma_read, c64_dma_read_color,
/*TODO*///						  c64_vic_interrupt);
/*TODO*///		}
/*TODO*///		state_add_function(c64_state);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64_driver_init (void)
/*TODO*///	{
/*TODO*///		c64_common_driver_init ();
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64pal_driver_init (void)
/*TODO*///	{
/*TODO*///		c64_pal = 1;
/*TODO*///		c64_common_driver_init ();
/*TODO*///	}
/*TODO*///	
/*TODO*///	void ultimax_driver_init (void)
/*TODO*///	{
/*TODO*///		ultimax = 1;
/*TODO*///	    c64_cia1_on = 0;
/*TODO*///		c64_common_driver_init ();
/*TODO*///		if (cbm_rom[0].size==0) {
/*TODO*///		  printf("no cartridge found\n");
/*TODO*///		  exit(1);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64gs_driver_init (void)
/*TODO*///	{
/*TODO*///		c64_pal = 1;
/*TODO*///		c64_tape_on = 0;
/*TODO*///	    c64_cia1_on = 1;
/*TODO*///		c64_common_driver_init ();
/*TODO*///	}
/*TODO*///	
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
/*TODO*///		if (ultimax == 0)
/*TODO*///		{
/*TODO*///			cbm_drive_close ();
/*TODO*///		}
/*TODO*///		if (c64_tape_on)
/*TODO*///			vc20_tape_close ();
/*TODO*///	}
	
	public static void c64_common_init_machine ()
	{
/*TODO*///	#ifdef VC1541
/*TODO*///		vc1541_reset ();
/*TODO*///	#endif
/*TODO*///		sid6581_reset(0);
/*TODO*///		sid6581_set_type(0, SID8580);
		if (c64_cia1_on != 0)
		{
			cbm_serial_reset_write (0);
			cbm_drive_0_config (SERIAL8ON()!=0 ? SERIAL : 0, c65!=0?10:8);
			cbm_drive_1_config (SERIAL9ON()!=0 ? SERIAL : 0, c65!=0?11:9);
			serial_clock = serial_data = serial_atn = 1;
		}
		cia6526_reset();
		c64_vicaddr = new UBytePtr(c64_memory);
		vicirq = cia0irq = 0;
		c64_port6510 = 0xff;
		c64_ddr6510 = 0;
	}
	
	public static InitMachinePtr c64_init_machine = new InitMachinePtr() { public void handler() 
	{
		c64_common_init_machine ();
	
/*TODO*///		c64_rom_recognition ();
/*TODO*///		c64_rom_load();
	
/*TODO*///		if (c128)
/*TODO*///			c128_bankswitch_64 (1);
/*TODO*///		if (ultimax == 0)
/*TODO*///			c64_bankswitch (1);
            }
        };
	
/*TODO*///	void c64_shutdown_machine (void)
/*TODO*///	{
/*TODO*///	}
/*TODO*///	
/*TODO*///	#ifdef VERIFY_IMAGE
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
/*TODO*///		if (!(romfile = (FILE*)image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE, 0)))
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
/*TODO*///		if (retval)
/*TODO*///			logerror("rom %s recognized\n", device_filename(IO_CARTSLOT,id) );
/*TODO*///		else
/*TODO*///			logerror("rom %s not recognized\n", device_filename(IO_CARTSLOT,id));
/*TODO*///		return retval;
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#define BETWEEN(value1,value2,bottom,top) \
/*TODO*///	    ( ((value2)>=(bottom))&&((value1)<(top)) )
/*TODO*///	
/*TODO*///	void c64_rom_recognition (void)
/*TODO*///	{
/*TODO*///	    int i;
/*TODO*///	    cartridgetype=CartridgeAuto;
/*TODO*///	    for (i=0; (i<sizeof(cbm_rom)/sizeof(cbm_rom[0]))
/*TODO*///		     &&(cbm_rom[i].size!=0); i++) {
/*TODO*///		cartridge=1;
/*TODO*///		if ( BETWEEN(0xa000, 0xbfff, cbm_rom[i].addr,
/*TODO*///			     cbm_rom[i].addr+cbm_rom[i].size) ) {
/*TODO*///		    cartridgetype=CartridgeC64;
/*TODO*///		} else if ( BETWEEN(0xe000, 0xffff, cbm_rom[i].addr,
/*TODO*///				    cbm_rom[i].addr+cbm_rom[i].size) ) {
/*TODO*///		    cartridgetype=CartridgeUltimax;
/*TODO*///		}
/*TODO*///	    }
/*TODO*///	    if (i==4) cartridgetype=CartridgeSuperGames;
/*TODO*///	    if (i==32) cartridgetype=CartridgeRobocop2;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void c64_rom_load(void)
/*TODO*///	{
/*TODO*///	    int i;
/*TODO*///	
/*TODO*///	    c64_exrom = 1;
/*TODO*///	    c64_game = 1;
/*TODO*///	    if (cartridge)
/*TODO*///	    {
/*TODO*///		if (AUTO_MODULE && (cartridgetype == CartridgeAuto))
/*TODO*///		{
/*TODO*///		    logerror("Cartridge type not recognized using Machine type\n");
/*TODO*///		}
/*TODO*///		if (C64_MODULE && (cartridgetype == CartridgeUltimax))
/*TODO*///		{
/*TODO*///		    logerror("Cartridge could be ultimax type!?\n");
/*TODO*///		}
/*TODO*///		if (ULTIMAX_MODULE && (cartridgetype == CartridgeC64))
/*TODO*///		{
/*TODO*///		    logerror("Cartridge could be c64 type!?\n");
/*TODO*///		}
/*TODO*///		if (C64_MODULE)
/*TODO*///		    cartridgetype = CartridgeC64;
/*TODO*///		else if (ULTIMAX_MODULE)
/*TODO*///		    cartridgetype = CartridgeUltimax;
/*TODO*///		else if (SUPERGAMES_MODULE)
/*TODO*///		    cartridgetype = CartridgeSuperGames;
/*TODO*///		else if (ROBOCOP2_MODULE)
/*TODO*///		    cartridgetype = CartridgeRobocop2;
/*TODO*///		if ((cbm_c64_exrom!=-1)&&(cbm_c64_game!=-1)) {
/*TODO*///		    c64_exrom=cbm_c64_exrom;
/*TODO*///		    c64_game=cbm_c64_game;
/*TODO*///		} else if (ultimax || (cartridgetype == CartridgeUltimax)) {
/*TODO*///		    c64_game = 0;
/*TODO*///		} else {
/*TODO*///		    c64_exrom = 0;
/*TODO*///		}
/*TODO*///		if (ultimax) {
/*TODO*///		    for (i=0; (i<sizeof(cbm_rom)/sizeof(cbm_rom[0]))
/*TODO*///			     &&(cbm_rom[i].size!=0); i++) {
/*TODO*///			if (cbm_rom[i].addr==CBM_ROM_ADDR_LO) {
/*TODO*///			    memcpy(c64_memory+0x8000+0x2000-cbm_rom[i].size,
/*TODO*///				   cbm_rom[i].chip, cbm_rom[i].size);
/*TODO*///			} else if ((cbm_rom[i].addr==CBM_ROM_ADDR_HI)
/*TODO*///				   ||(cbm_rom[i].addr==CBM_ROM_ADDR_UNKNOWN)) {
/*TODO*///			    memcpy(c64_memory+0xe000+0x2000-cbm_rom[i].size,
/*TODO*///				   cbm_rom[i].chip, cbm_rom[i].size);
/*TODO*///			} else {
/*TODO*///			    memcpy(c64_memory+cbm_rom[i].addr, cbm_rom[i].chip,
/*TODO*///				   cbm_rom[i].size);
/*TODO*///			}
/*TODO*///		    }
/*TODO*///	        } else if ( (cartridgetype==CartridgeRobocop2)
/*TODO*///			    ||(cartridgetype==CartridgeSuperGames) ) {
/*TODO*///		    roml=0;
/*TODO*///		    romh=0;
/*TODO*///		    for (i=0; (i<sizeof(cbm_rom)/sizeof(cbm_rom[0]))
/*TODO*///			     &&(cbm_rom[i].size!=0); i++) {
/*TODO*///			if (!roml
/*TODO*///			    && ((cbm_rom[i].addr==CBM_ROM_ADDR_UNKNOWN)
/*TODO*///				||(cbm_rom[i].addr==CBM_ROM_ADDR_LO)
/*TODO*///				||(cbm_rom[i].addr==0x8000)) ) {
/*TODO*///			    roml=cbm_rom[i].chip;
/*TODO*///			}
/*TODO*///			if (!romh
/*TODO*///			    && ((cbm_rom[i].addr==CBM_ROM_ADDR_HI)
/*TODO*///				||(cbm_rom[i].addr==0xa000) ) ){
/*TODO*///			    romh=cbm_rom[i].chip;
/*TODO*///			}
/*TODO*///			if (!romh
/*TODO*///			    && (cbm_rom[i].addr==0x8000)
/*TODO*///			    &&(cbm_rom[i].size=0x4000) ){
/*TODO*///			    romh=cbm_rom[i].chip+0x2000;
/*TODO*///			}
/*TODO*///		    }
/*TODO*///		} else /*if ((cartridgetype == CartridgeC64)||
/*TODO*///					 (cartridgetype == CartridgeUltimax) )*/{
/*TODO*///		    roml=c64_roml;
/*TODO*///		    romh=c64_romh;
/*TODO*///		    memset(roml, 0, 0x2000);
/*TODO*///		    memset(romh, 0, 0x2000);
/*TODO*///		    for (i=0; (i<sizeof(cbm_rom)/sizeof(cbm_rom[0]))
/*TODO*///			     &&(cbm_rom[i].size!=0); i++) {
/*TODO*///			if ((cbm_rom[i].addr==CBM_ROM_ADDR_UNKNOWN)
/*TODO*///			    ||(cbm_rom[i].addr==CBM_ROM_ADDR_LO) ) {
/*TODO*///			    memcpy(roml+0x2000-cbm_rom[i].size,
/*TODO*///				   cbm_rom[i].chip, cbm_rom[i].size);
/*TODO*///			} else if ( ((cartridgetype == CartridgeC64)
/*TODO*///				     &&(cbm_rom[i].addr==CBM_ROM_ADDR_HI))
/*TODO*///				    ||((cartridgetype==CartridgeUltimax)
/*TODO*///				       &&(cbm_rom[i].addr==CBM_ROM_ADDR_HI)) ) {
/*TODO*///			    memcpy(romh+0x2000-cbm_rom[i].size,
/*TODO*///				   cbm_rom[i].chip, cbm_rom[i].size);
/*TODO*///			} else if (cbm_rom[i].addr<0xc000) {
/*TODO*///			    memcpy(roml+cbm_rom[i].addr-0x8000, cbm_rom[i].chip,
/*TODO*///				   cbm_rom[i].size);
/*TODO*///			} else {
/*TODO*///			    memcpy(romh+cbm_rom[i].addr-0xe000,
/*TODO*///				   cbm_rom[i].chip, cbm_rom[i].size);
/*TODO*///			}
/*TODO*///		    }
/*TODO*///		}
/*TODO*///	    }
/*TODO*///	}

        static int quickload = 0;
        static int monitor=-1;
        
	public static InterruptPtr c64_frame_interrupt = new InterruptPtr() {
            public int handler() {
	
                int value, value2;
/*TODO*///		sid6581_update();
	
		c64_nmi();
	
		if (quickload==0 && QUICKLOAD()!=0) {
/*TODO*///			if (c65) {
/*TODO*///				cbm_c65_quick_open (0, 0, c64_memory);
/*TODO*///			} else
/*TODO*///				cbm_quick_open(0, 0, c64_memory);
		}
		quickload = QUICKLOAD();
	
/*TODO*///		if (c128) {
/*TODO*///			if (MONITOR_TV!=monitor) {
/*TODO*///				if (MONITOR_TV) {
/*TODO*///					vic2_set_rastering(0);
/*TODO*///					vdc8563_set_rastering(1);
/*TODO*///					osd_set_visible_area(0,655,0,215);
/*TODO*///				} else {
/*TODO*///					vic2_set_rastering(1);
/*TODO*///					vdc8563_set_rastering(0);
/*TODO*///					osd_set_visible_area(0,335,0,215);
/*TODO*///				}
/*TODO*///				monitor=MONITOR_TV;
/*TODO*///			}
/*TODO*///		}
	
		value = 0xff;
/*TODO*///		if (c128) {
/*TODO*///			if (C128_KEY_CURSOR_DOWN)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C128_KEY_F5)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (C128_KEY_F3)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (C128_KEY_F1)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C128_KEY_F7)
/*TODO*///				value &= ~8;
/*TODO*///			if (C128_KEY_CURSOR_RIGHT)
/*TODO*///				value &= ~4;
/*TODO*///		} else if (c65) {
/*TODO*///			if (C65_KEY_CURSOR_DOWN)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C65_KEY_F5)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (C65_KEY_F3)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (C65_KEY_F1)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C65_KEY_F7)
/*TODO*///				value &= ~8;
/*TODO*///			if (C65_KEY_CURSOR_RIGHT)
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
/*TODO*///		if (c128) {
/*TODO*///			if (C128_KEY_RIGHT_SHIFT)
/*TODO*///			value &= ~0x10;
/*TODO*///		} else if (c65) {
/*TODO*///			if (C65_KEY_RIGHT_SHIFT)
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
/*TODO*///		if (c65) {
/*TODO*///			if (C65_KEY_STOP)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C65_KEY_SPACE)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C65_KEY_CTRL)
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
		if (JOYSTICK1()!=0||JOYSTICK1_2BUTTON()!=0) {
			if (JOYSTICK_1_BUTTON() != 0)
				value &= ~0x10;
			if (JOYSTICK_1_RIGHT() != 0)
				value &= ~8;
			if (JOYSTICK_1_LEFT() != 0)
				value &= ~4;
			if (JOYSTICK_1_DOWN() != 0)
				value &= ~2;
			if (JOYSTICK_1_UP() != 0)
				value &= ~1;
		} else if (PADDLES12()!=0) {
			if (PADDLE2_BUTTON() != 0)
				value &= ~8;
			if (PADDLE1_BUTTON() != 0)
				value &= ~4;
		} else if (MOUSE1()!=0) {
			if (MOUSE1_BUTTON1() != 0)
				value &= ~0x10;
			if (MOUSE1_BUTTON2() != 0)
				value &= ~1;
		}
		c64_keyline[8] = value;
	
		value2 = 0xff;
		if (JOYSTICK2()!=0||JOYSTICK2_2BUTTON()!=0) {
			if (JOYSTICK_2_BUTTON() != 0)
				value2 &= ~0x10;
			if (JOYSTICK_2_RIGHT() != 0)
				value2 &= ~8;
			if (JOYSTICK_2_LEFT() != 0)
				value2 &= ~4;
			if (JOYSTICK_2_DOWN() != 0)
				value2 &= ~2;
			if (JOYSTICK_2_UP() != 0)
				value2 &= ~1;
		} else if (PADDLES34()!=0) {
			if (PADDLE4_BUTTON() != 0)
				value2 &= ~8;
			if (PADDLE3_BUTTON() != 0)
				value2 &= ~4;
		} else if (MOUSE2()!=0) {
			if (MOUSE2_BUTTON1() != 0)
				value2 &= ~0x10;
			if (MOUSE2_BUTTON2() != 0)
				value2 &= ~1;
		}
		c64_keyline[9] = value2;
	
/*TODO*///		if ( c128 ) {
/*TODO*///			value = 0xff;
/*TODO*///			if (KEY_NUM1)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (KEY_NUM7)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (KEY_NUM4)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (KEY_NUM2)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (KEY_TAB)
/*TODO*///				value &= ~8;
/*TODO*///			if (KEY_NUM5)
/*TODO*///				value &= ~4;
/*TODO*///			if (KEY_NUM8)
/*TODO*///				value &= ~2;
/*TODO*///			if (KEY_HELP)
/*TODO*///				value &= ~1;
/*TODO*///			c128_keyline[0] = value;
/*TODO*///	
/*TODO*///			value = 0xff;
/*TODO*///			if (KEY_NUM3)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (KEY_NUM9)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (KEY_NUM6)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (KEY_NUMENTER)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (KEY_LINEFEED)
/*TODO*///				value &= ~8;
/*TODO*///			if (KEY_NUMMINUS)
/*TODO*///				value &= ~4;
/*TODO*///			if (KEY_NUMPLUS)
/*TODO*///				value &= ~2;
/*TODO*///			if (KEY_ESCAPE)
/*TODO*///				value &= ~1;
/*TODO*///			c128_keyline[1] = value;
/*TODO*///	
/*TODO*///			value = 0xff;
/*TODO*///			if (KEY_NOSCRL)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (KEY_RIGHT)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (KEY_LEFT)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (KEY_DOWN)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (KEY_UP)
/*TODO*///				value &= ~8;
/*TODO*///			if (KEY_NUMPOINT)
/*TODO*///				value &= ~4;
/*TODO*///			if (KEY_NUM0)
/*TODO*///				value &= ~2;
/*TODO*///			if (KEY_ALT)
/*TODO*///				value &= ~1;
/*TODO*///			c128_keyline[2] = value;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (c65) {
/*TODO*///			value = 0xff;
/*TODO*///			if (C65_KEY_ESCAPE)
/*TODO*///				value &= ~0x80;
/*TODO*///			if (C65_KEY_F13)
/*TODO*///				value &= ~0x40;
/*TODO*///			if (C65_KEY_F11)
/*TODO*///				value &= ~0x20;
/*TODO*///			if (C65_KEY_F9)
/*TODO*///				value &= ~0x10;
/*TODO*///			if (C65_KEY_HELP)
/*TODO*///				value &= ~8;
/*TODO*///			if (C65_KEY_ALT) /* non blocking */
/*TODO*///				value &= ~4;
/*TODO*///			if (C65_KEY_TAB)
/*TODO*///				value &= ~2;
/*TODO*///			if (C65_KEY_NOSCRL)
/*TODO*///				value &= ~1;
/*TODO*///			c65_keyline = value;
/*TODO*///		}
	
		vic2_frame_interrupt();
	
		if (c64_tape_on != 0) {
			vc20_tape_config (DATASSETTE(), DATASSETTE_TONE());
			vc20_tape_buttons (DATASSETTE_PLAY(), DATASSETTE_RECORD(), DATASSETTE_STOP());
		}
/*TODO*///		set_led_status (1 /*KB_CAPSLOCK_FLAG */ , KEY_SHIFTLOCK ? 1 : 0);
/*TODO*///		set_led_status (0 /*KB_NUMLOCK_FLAG */ , JOYSTICK_SWAP ? 1 : 0);
/*TODO*///	
		return ignore_interrupt.handler();
            }
        };


/*TODO*///	void c64_state(void)
/*TODO*///	{
/*TODO*///		char text[70];
/*TODO*///	
/*TODO*///	#if VERBOSE_DBG
/*TODO*///	#if 0
/*TODO*///		cia6526_status (text, sizeof (text));
/*TODO*///		state_display_text (text);
/*TODO*///	
/*TODO*///		snprintf (text, sizeof(text), "c64 vic:%.4x m6510:%d exrom:%d game:%d",
/*TODO*///				  c64_vicaddr - c64_memory, c64_port6510 & 7,
/*TODO*///				  c64_exrom, c64_game);
/*TODO*///		state_display_text (text);
/*TODO*///	#endif
/*TODO*///	
/*TODO*///		vdc8563_state();
/*TODO*///	//	state_display_text (text);
/*TODO*///	#endif
/*TODO*///	
/*TODO*///		vc20_tape_status (text, sizeof (text));
/*TODO*///		state_display_text (text);
/*TODO*///	#ifdef VC1541
/*TODO*///		vc1541_drive_status (text, sizeof (text));
/*TODO*///	#else
/*TODO*///		cbm_drive_0_status (text, sizeof (text));
/*TODO*///	#endif
/*TODO*///		state_display_text (text);
/*TODO*///	
/*TODO*///		cbm_drive_1_status (text, sizeof (text));
/*TODO*///		state_display_text (text);
/*TODO*///	}
	
}
