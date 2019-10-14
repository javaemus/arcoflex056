/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

import static common.ptr.*;
import static mess056.machine.gb.gb_ram;

public class gbH
{
	
	public static final int VBL_IFLAG = 0x01;
	public static final int LCD_IFLAG = 0x02;
	public static final int TIM_IFLAG = 0x04;
	public static final int SIO_IFLAG = 0x08;
	public static final int EXT_IFLAG = 0x10;
	
	public static final int VBL_INT = 0;
	public static final int LCD_INT = 1;
	public static final int TIM_INT = 2;
	public static final int SIO_INT = 3;
	public static final int EXT_INT = 4;
	
	public static int JOYPAD(){  return gb_ram.read(0xFF00);} /* Joystick: 1.1.P15.P14.P13.P12.P11.P10      */
        public static void JOYPAD(int val){  gb_ram.write(0xFF00, val);}
	public static int SIODATA(){ return gb_ram.read(0xFF01);} /* Serial IO data buffer 					 */
        public static void SIODATA(int val){ gb_ram.write(0xFF01, val);}
	public static int SIOCONT(){ return gb_ram.read(0xFF02);} /* Serial IO control register				 */
        public static void SIOCONT(int val){ gb_ram.write(0xFF02, val);}
	public static int DIVREG(){  return gb_ram.read(0xFF04);} /* Divider register (???)					 */
	public static int TIMECNT(){ return gb_ram.read(0xFF05);} /* Timer counter. Gen. int. when it overflows */
        public static void TIMECNT(int val){ gb_ram.write(0xFF05, val);}
	public static int TIMEMOD(){ return gb_ram.read(0xFF06);} /* New value of TimeCount after it overflows  */
        public static void TIMEMOD(int val){ gb_ram.write(0xFF06, val);}
	public static int TIMEFRQ(){ return gb_ram.read(0xFF07);} /* Timer frequency and start/stop switch 	 */
        public static void TIMEFRQ(int val){ gb_ram.write(0xFF07, val);}
	public static int IFLAGS(){  return gb_ram.read(0xFF0F);} /* Interrupt flags: 0.0.0.JST.SIO.TIM.LCD.VBL */
        public static void IFLAGS(int val){  gb_ram.write(0xFF0F, val);}
	public static int ISWITCH(){ return gb_ram.read(0xFFFF);} /* Switches to enable/disable interrupts 	 */
        public static void ISWITCH(int val){ gb_ram.write(0xFFFF, val);}
	public static int LCDCONT(){ return gb_ram.read(0xFF40);} /* LCD control register						 */
        public static void LCDCONT(int val){ gb_ram.write(0xFF40, val);}
	public static int LCDSTAT(){ return gb_ram.read(0xFF41);} /* LCD status register						 */
        public static void LCDSTAT(int val){ gb_ram.write(0xFF41, val);}
	public static int SCROLLY(){ return gb_ram.read(0xFF42);} /* Starting Y position of the background 	 */
	public static int SCROLLX(){ return gb_ram.read(0xFF43);} /* Starting X position of the background 	 */
	public static int CURLINE(){ return gb_ram.read(0xFF44);} /* Current screen line being scanned 		 */
        public static void CURLINE(int val){ gb_ram.write(0xFF44, val);}
	public static int CMPLINE(){ return gb_ram.read(0xFF45);} /* Gen. int. when scan reaches this line 	 */
        public static void CMPLINE(int val){ gb_ram.write(0xFF45, val);}
	public static int BGRDPAL(){ return gb_ram.read(0xFF47);} /* Background palette						 */
        public static void BGRDPAL(int val){ gb_ram.write(0xFF47, val);}
	public static int SPR0PAL(){ return gb_ram.read(0xFF48);} /* Sprite palette #0 						 */
        public static void SPR0PAL(int val){ gb_ram.write(0xFF48, val);}
	public static int SPR1PAL(){ return gb_ram.read(0xFF49);} /* Sprite palette #1 						 */
        public static void SPR1PAL(int val){ gb_ram.write(0xFF49, val);}
	public static int WNDPOSY(){ return gb_ram.read(0xFF4A);} /* Window Y position 						 */
	public static int WNDPOSX(){ return gb_ram.read(0xFF4B);} /* Window X position 						 */
	
	public static final int OAM  = 0xFE00;
	public static final int VRAM = 0x8000;
	
        public static int[] gb_bpal = new int[4];			/* Background palette */
	public static int[] gb_spal0 = new int[4];			/* Sprite 0 palette */
	public static int[] gb_spal1 = new int[4];			/* Sprite 1 palette */
	public static UBytePtr gb_chrgen = new UBytePtr();		/* Character generator */
	public static UBytePtr gb_bgdtab = new UBytePtr();		/* Background character table */
	public static UBytePtr gb_wndtab = new UBytePtr();		/* Window character table */
        public static int gb_divcount;
	public static int gb_timer_count;
	public static int gb_timer_shift;
	public static int gb_tile_no_mod;
}
