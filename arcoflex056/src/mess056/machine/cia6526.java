/**********************************************************************
	Metal Oxid Semiconductor / Commodore Business Machines
        Complex Interface Adapter 6526

	based on 6522via emulation
**********************************************************************/
/*
 only few tested (for c64)

 state:
 port a,b
 handshake support, flag input, not pc output
 timer a,b
  not counting of external clocks
  not switching port b pins
 interrupt system
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

import static arcadeflex056.fucPtr.*;
import static mame056.timer.*;
import static mame056.timerH.*;
import static mess056.includes.cia6526H.*;
import static mess056.machine.cia6526.cia;

public class cia6526
{
	
/*TODO*///	#define VERBOSE_DBG 1				   /* general debug messages */
	
	
	/* todin pin 50 or 60 hertz frequency */
	public static int TODIN_50HZ(_CIA6526 This){ return (This.cra&0x80); }  /* else 60 Hz */
	
/*TODO*///	#define TIMER1_B6 (This->cra&2)
/*TODO*///	#define TIMER1_B6_TOGGLE (This->cra&4)	/* else single pulse */
        public static int TIMER1_ONESHOT(_CIA6526 This){ return((This.cra&8)!=0?1:0); } /* else continuous */
        public static int TIMER1_STOP(_CIA6526 This){ return((This.cra&1)!=0?1:0); }
        public static int TIMER1_RELOAD(_CIA6526 This){ return((This.cra&0x10)!=0?1:0); }
        public static int TIMER1_COUNT_CNT(_CIA6526 This){ return((This.cra&0x20)!=0?1:0); }	/* else clock 2 input */

        public static int TIMER2_ONESHOT(_CIA6526 This){ return(This.crb&8)!=0 ? 1: 0; } /* else continuous */
        public static int TIMER2_STOP(_CIA6526 This){ return ((This.crb&1)!=0?1:0); }
        public static int TIMER2_RELOAD(_CIA6526 This){ return ((This.crb&0x10)!=0?1:0); }
        public static int TIMER2_COUNT_CLOCK(_CIA6526 This){ return (This.crb&0x60)==0?1:0; }
/*TODO*///	#define TIMER2_COUNT_CNT ((This->crb&0x60)==0x20)
        public static int TIMER2_COUNT_TIMER1(_CIA6526 This){ return (This.crb&0x60)==0x40?1:0; }
        public static int TIMER2_COUNT_TIMER1_CNT(_CIA6526 This){ return (This.crb&0x60)==0x60?1:0; }

        public static int SERIAL_MODE_OUT(_CIA6526 This){ return (This.cra&0x40)!=0 ? 1:0; }
        public static int TOD_ALARM(_CIA6526 This){ return (This.crb&0x80); }   /* else write to tod clock */
        public static int BCD_INC(int v){ return ( ((v)&0xf)==9?(v)+=0x10-9:(v)++); }
	
	public static class _CIA6526 {
                public int number; /* number of cia, to allow callback generate address */
		public cia6526_interface intf;

		public int in_a;
		public int out_a;
		public int ddr_a;
	
		public int in_b;
		public int out_b;
		public int ddr_b;
	
                public int t1c;
		public int t1l=0;
                public timer_entry timer1;
		public int timer1_state;

                public int t2c;
		public int t2l=0;
                public timer_entry timer2;
                public int timer2_state;
	
		public int tod10ths, todsec, todmin, todhour;
		public int alarm10ths, alarmsec, alarmmin, alarmhour;
	
		public int latch10ths, latchsec, latchmin, latchhour;
		public int todlatched;

		public int todstopped;
                public timer_entry todtimer;

		public int flag;						   /* input */

                public int sdr;
		public int cnt,						   /* input or output */
		 sp;							   /* input or output */
		public int serial, shift, loaded;

		public int cra, crb;

		public int ier;
		public int ifr;
	};
	
	public static _CIA6526[] cia=new _CIA6526[MAX_CIA];
        
        static {
            for (int _i=0 ; _i<MAX_CIA ; _i++)
                cia[_i] = new _CIA6526();
        }
/*TODO*///	{
/*TODO*///		{0}
/*TODO*///	};
/*TODO*///	
/*TODO*///	static void cia_timer1_timeout (int which);
/*TODO*///	static void cia_timer2_timeout (int which);
/*TODO*///	static void cia_tod_timeout (int which);
	
	/******************* configuration *******************/
	
	public static void cia6526_config (int which, cia6526_interface intf)
	{
		if (which >= MAX_CIA)
			return;
		//memset (cia + which, 0, sizeof (cia[which]));
                cia[which] = new _CIA6526();
		cia[which].number=which;
		cia[which].intf = intf;
	}
	
	
	/******************* reset *******************/
	
	public static void cia6526_reset ()
	{
		int i;
/*TODO*///	
/*TODO*///		assert (((int) cia[0].intf & 3) == 0);
/*TODO*///	
		/* zap each structure, preserving the interface and swizzle */
		for (i = 0; i < MAX_CIA; i++)
		{
                    if (cia[i]!=null){
                        
                        cia6526_interface intf = cia[i].intf;
	
			if (cia[i].timer1 != null)
				timer_remove (cia[i].timer1);
			if (cia[i].timer2 != null)
				timer_remove (cia[i].timer2);
			if (cia[i].todtimer != null)
				timer_remove (cia[i].todtimer);
			//memset (&cia[i], 0, sizeof (cia[i]));
                        cia[i] = new _CIA6526();
			cia[i].number = i;
			cia[i].intf = intf;
			cia[i].t1l = 0xffff;
			cia[i].t2l = 0xffff;
			if (cia[i].intf!=null) cia[i].todtimer=timer_set(0.1,i,cia_tod_timeout);
                    }
		}
	}
	
	/******************* external interrupt check *******************/
	
	public static void cia_set_interrupt (int This, int data)
	{
		cia[This].ifr |= data;
		if ((cia[This].ier & data) != 0)
		{
			if ((cia[This].ifr & 0x80)==0)
			{
/*TODO*///				DBG_LOG (3, "cia set interrupt", ("%d %.2x\n", cia[This].number, data));
				if (cia[This].intf.irq_func != null)
					cia[This].intf.irq_func.handler(1);
				cia[This].ifr |= 0x80;
			}
		}
	}
	
	public static void cia_clear_interrupt (int This, int data)
	{
		cia[This].ifr &= ~data;
		if ((cia[This].ifr & 0x9f) == 0x80)
		{
			cia[This].ifr &= ~0x80;
			if (cia[This].intf.irq_func != null)
				cia[This].intf.irq_func.handler(0);
		}
	}
	
	/******************* Timer timeouts *************************/
	public static timer_callback cia_tod_timeout = new timer_callback() {
            public void handler(int which) {
                cia[which].tod10ths++;
		if (cia[which].tod10ths > 9)
		{
			cia[which].tod10ths = 0;
			cia[which].todsec = BCD_INC (cia[which].todsec);
			if (cia[which].todsec > 0x59)
			{
				cia[which].todsec = 0;
				cia[which].todmin = BCD_INC (cia[which].todmin);
				if (cia[which].todmin > 0x59)
				{
					cia[which].todmin = 0;
					if (cia[which].todhour == 0x91)
						cia[which].todhour = 0;
					else if (cia[which].todhour == 0x89)
						cia[which].todhour = 0x90;
					else if (cia[which].todhour == 0x11)
						cia[which].todhour = 0x80;
					else if (cia[which].todhour == 0x09)
						cia[which].todhour = 0x10;
					else
						cia[which].todhour++;
				}
			}
		}
		if ((cia[which].todhour == cia[which].alarmhour)
			&& (cia[which].todmin == cia[which].alarmmin)
			&& (cia[which].todsec == cia[which].alarmsec)
			&& (cia[which].tod10ths == cia[which].alarm10ths))
			cia_set_interrupt(which, 4);
		if (TODIN_50HZ(cia[which]) != 0)
		{
			if (cia[which].intf.todin50hz != 0)
				timer_reset (cia[which].todtimer, 0.1);
			else
				timer_reset (cia[which].todtimer, 5.0 / 60);
		}
		else
		{
			if (cia[which].intf.todin50hz != 0)
				timer_reset (cia[which].todtimer, 6.0 / 50);
			else
				timer_reset (cia[which].todtimer, 0.1);
		}
            }
        };
	
	
	public static void cia_timer1_state (int This)
	{
	
/*TODO*///		DBG_LOG (1, "timer1 state", ("%d\n", This.timer1_state));
		switch (cia[This].timer1_state)
		{
		case 0:						   /* timer stopped */
			if (TIMER1_RELOAD(cia[This]) != 0)
			{
				cia[This].cra &= ~0x10;
				cia[This].t1c = cia[This].t1l;
			}
			if (TIMER1_STOP(cia[This]) == 0)
			{
				if (TIMER1_COUNT_CNT(cia[This]) != 0)
				{
					cia[This].timer1_state = 2;
				}
				else
				{
					cia[This].timer1_state = 1;
					cia[This].timer1 = timer_set (TIME_IN_CYCLES (cia[This].t1c, 0),
											  cia[This].number, cia_timer1_timeout);
				}
			}
			break;
		case 1:						   /* counting clock input */
			if (TIMER1_RELOAD(cia[This]) != 0)
			{
				cia[This].cra &= ~0x10;
				cia[This].t1c = cia[This].t1l;
				if (TIMER1_STOP(cia[This]) == 0)
					timer_reset (cia[This].timer1, TIME_IN_CYCLES (cia[This].t1c, 0));
			}
			if (TIMER1_STOP(cia[This]) != 0)
			{
				cia[This].timer1_state = 0;
				timer_remove (cia[This].timer1);
				cia[This].timer1 = null;
			}
			else if (TIMER1_COUNT_CNT(cia[This]) != 0)
			{
				cia[This].timer1_state = 2;
				timer_remove (cia[This].timer1);
				cia[This].timer1 = null;
			}
			break;
		case 2:						   /* counting cnt input */
			if (TIMER1_RELOAD(cia[This]) != 0)
			{
				cia[This].cra &= ~0x10;
				cia[This].t1c = cia[This].t1l;
			}
			if (TIMER1_STOP(cia[This]) != 0)
			{
				cia[This].timer1_state = 0;
			}
			else if (TIMER1_COUNT_CNT(cia[This]) == 0)
			{
				cia[This].timer1 = timer_set (TIME_IN_CYCLES (cia[This].t1c, 0),
										  cia[This].number, cia_timer1_timeout);
				cia[This].timer1_state = 1;
			}
			break;
		}
/*TODO*///		DBG_LOG (1, "timer1 state", ("%d\n", This.timer1_state));
	}

	public static void cia_timer2_state (int This)
	{
		switch (cia[This].timer2_state)
		{
		case 0:						   /* timer stopped */
			if (TIMER2_RELOAD(cia[This]) != 0)
			{
				cia[This].crb &= ~0x10;
				cia[This].t2c = cia[This].t2l;
			}
			if (TIMER2_STOP(cia[This]) == 0)
			{
				if (TIMER2_COUNT_CLOCK(cia[This]) != 0)
				{
					cia[This].timer2_state = 1;
					cia[This].timer2 = timer_set (TIME_IN_CYCLES (cia[This].t2c, 0),
											  cia[This].number, cia_timer2_timeout);
				}
				else
				{
					cia[This].timer2_state = 2;
				}
			}
			break;
		case 1:						   /* counting clock input */
			if (TIMER2_RELOAD(cia[This]) != 0)
			{
				cia[This].crb &= ~0x10;
				cia[This].t2c = cia[This].t2l;
				timer_reset (cia[This].timer2, TIME_IN_CYCLES (cia[This].t2c, 0));
			}
			if (TIMER2_STOP(cia[This]) != 0 )
			{
				cia[This].timer2_state = 0;
				timer_remove (cia[This].timer2);
				cia[This].timer2 = null;
			}
			else if (TIMER2_COUNT_CLOCK(cia[This]) == 0)
			{
				cia[This].timer2_state = 2;
				timer_remove (cia[This].timer2);
				cia[This].timer2 = null;
			}
			break;
		case 2:						   /* counting cnt, timer1  input */
			if (cia[This].t2c == 0)
			{
				cia_set_interrupt (This, 2);
				cia[This].crb |= 0x10;
			}
			if (TIMER2_RELOAD(cia[This]) != 0)
			{
				cia[This].crb &= ~0x10;
				cia[This].t2c = cia[This].t2l;
			}
			if (TIMER2_STOP(cia[This]) != 0)
			{
				cia[This].timer2_state = 0;
			}
			else if (TIMER2_COUNT_CLOCK(cia[This]) != 0)
			{
				cia[This].timer2 = timer_set (TIME_IN_CYCLES (cia[This].t2c, 0),
										  cia[This].number, cia_timer2_timeout);
				cia[This].timer2_state = 1;
			}
			break;
		}
	}
	
	public static timer_callback cia_timer1_timeout = new timer_callback() {
            public void handler(int which) {
                cia[which].t1c = cia[which].t1l;
	
		if (TIMER1_ONESHOT(cia[which]) != 0)
		{
			cia[which].cra &= ~1;
			cia[which].timer1_state = 0;
		}
		else
		{
			timer_reset (cia[which].timer1, TIME_IN_CYCLES (cia[which].t1c, 0));
		}
		cia_set_interrupt (which, 1);
		if (SERIAL_MODE_OUT(cia[which]) != 0)
		{
			if (cia[which].shift!=0 || cia[which].loaded !=0)
			{
				if (cia[which].cnt != 0)
				{
					if (cia[which].shift == 0)
					{
						cia[which].loaded = 0;
						cia[which].serial = cia[which].sdr;
					}
					cia[which].sp = (cia[which].serial & 0x80)!=0 ? 1 : 0;
					cia[which].shift++;
					cia[which].serial <<= 1;
					cia[which].cnt = 0;
				}
				else
				{
					cia[which].cnt = 1;
					if (cia[which].shift == 8)
					{
						cia_set_interrupt (which, 8);
						cia[which].shift = 0;
					}
				}
			}
		}
	
		/*  cia_timer1_state(This); */
	
		if (TIMER2_COUNT_TIMER1(cia[which]) != 0 || ((TIMER2_COUNT_TIMER1_CNT(cia[which]) != 0 ) && (cia[which].cnt != 0)))
		{
			cia[which].t2c--;
			cia_timer2_state (which);
		}
            }
        };
	
	public static timer_callback cia_timer2_timeout = new timer_callback() {
            public void handler(int which) {
                //CIA6526 *This = cia + which;
	
		cia[which].t2c = cia[which].t2l;
	
		if (TIMER2_ONESHOT(cia[which]) != 0)
		{
			cia[which].crb &= ~1;
			cia[which].timer2_state = 0;
		}
		else
		{
			timer_reset (cia[which].timer2, TIME_IN_CYCLES (cia[which].t2c, 0));
		}
	
		cia_set_interrupt (which, 2);
		/*  cia_timer2_state(This); */
            }
        };
	
	/******************* CPU interface for VIA read *******************/
	
	public static int cia6526_read (int This, int offset)
	{
		int val = 0;
	
		offset &= 0xf;
		switch (offset)
		{
		case 0:
			if (cia[This].intf.in_a_func != null)
				cia[This].in_a = cia[This].intf.in_a_func.handler(cia[This].number);
			val = ((cia[This].out_a & cia[This].ddr_a)
				   | (cia[This].intf.a_pullup & ~cia[This].ddr_a)) & cia[This].in_a;
			break;
		case 1:
			if (cia[This].intf.in_b_func != null)
				cia[This].in_b = cia[This].intf.in_b_func.handler(cia[This].number);
			val = ((cia[This].out_b & cia[This].ddr_b)
				   | (cia[This].intf.b_pullup & ~cia[This].ddr_b)) & cia[This].in_b;
			break;
		case 2:
			val = cia[This].ddr_a;
			break;
		case 3:
			val = cia[This].ddr_b;
			break;
		case 8:
			if (cia[This].todlatched != 0)
				val = cia[This].latch10ths;
			else
				val = cia[This].tod10ths;
			cia[This].todlatched = 0;
			break;
		case 9:
			if (cia[This].todlatched != 0)
				val = cia[This].latchsec;
			else
				val = cia[This].todsec;
			break;
		case 0xa:
			if (cia[This].todlatched != 0)
				val = cia[This].latchmin;
			else
				val = cia[This].todmin;
			break;
		case 0xb:
			cia[This].latch10ths = cia[This].tod10ths;
			cia[This].latchsec = cia[This].todsec;
			cia[This].latchmin = cia[This].todmin;
			val = cia[This].latchhour = cia[This].todhour;
			cia[This].todlatched = 1;
			break;
		case 0xd:
			val = cia[This].ifr & ~0x60;
			cia_clear_interrupt(This, 0x1f);
			break;
		case 4:
			if (cia[This].timer1 != null)
				val = TIME_TO_CYCLES (0, timer_timeleft (cia[This].timer1)) & 0xff;
			else
				val = cia[This].t1c & 0xff;
/*TODO*///			DBG_LOG (3, "cia timer 1 lo", ("%d %.2x\n", cia[This].number, val));
			break;
		case 5:
			if (cia[This].timer1 != null)
				val = TIME_TO_CYCLES (0, timer_timeleft (cia[This].timer1)) >> 8;
			else
				val = cia[This].t1c >> 8;
/*TODO*///			DBG_LOG (3, "cia timer 1 hi", ("%d %.2x\n", cia[This].number, val));
			break;
		case 6:
			if (cia[This].timer2 != null)
				val = TIME_TO_CYCLES (0, timer_timeleft (cia[This].timer2)) & 0xff;
			else
				val = cia[This].t2c & 0xff;
/*TODO*///			DBG_LOG (3, "cia timer 2 lo", ("%d %.2x\n", cia[This].number, val));
			break;
		case 7:
			if (cia[This].timer2 != null)
				val = TIME_TO_CYCLES (0, timer_timeleft (cia[This].timer2)) >> 8;
			else
				val = cia[This].t2c >> 8;
/*TODO*///			DBG_LOG (3, "cia timer 2 hi", ("%d %.2x\n", cia[This].number, val));
			break;
		case 0xe:
			val = cia[This].cra;
			break;
		case 0xf:
			val = cia[This].crb;
			break;
		case 0xc:
			val = cia[This].sdr;
			break;
		}
/*TODO*///		DBG_LOG (1, "cia read", ("%d %.2x:%.2x\n", cia[This].number, offset, val));
		return val;
	}
	
	
	/******************* CPU interface for VIA write *******************/
	
	static void cia6526_write (int This, int offset, int data)
	{
/*TODO*///		DBG_LOG (1, "cia write", ("%d %.2x:%.2x\n", This->number, offset, data));
		offset &= 0xf;
	
		switch (offset)
		{
		case 0:
			cia[This].out_a = data;
			if (cia[This].intf.out_a_func != null)
				cia[This].intf.out_a_func.handler(cia[This].number, (cia[This].out_a & cia[This].ddr_a)
									 | (~cia[This].ddr_a & cia[This].intf.a_pullup));
			break;
		case 1:
			cia[This].out_b = data;
			if (cia[This].intf.out_b_func != null)
				cia[This].intf.out_b_func.handler(cia[This].number, (cia[This].out_b & cia[This].ddr_b)
									 | (~cia[This].ddr_b & cia[This].intf.b_pullup));
			break;
		case 2:
			cia[This].ddr_a = data;
			if (cia[This].intf.out_a_func != null)
				cia[This].intf.out_a_func.handler(cia[This].number, (cia[This].out_a & cia[This].ddr_a)
									 | (~cia[This].ddr_a & cia[This].intf.a_pullup));
			break;
		case 3:
			cia[This].ddr_b = data;
			if (cia[This].intf.out_b_func != null)
				cia[This].intf.out_b_func.handler(cia[This].number, (cia[This].out_b & cia[This].ddr_b)
									 | (~cia[This].ddr_b & cia[This].intf.b_pullup));
			break;
		case 8:
			if (TOD_ALARM(cia[This]) != 0)
				cia[This].alarm10ths = data;
			else
			{
				cia[This].tod10ths = data;
				if (cia[This].todstopped != 0)
				{
					if (TODIN_50HZ(cia[This]) != 0)
					{
						if (cia[This].intf.todin50hz != 0)
							cia[This].todtimer = timer_set (0.1, cia[This].number, cia_tod_timeout);
						else
							cia[This].todtimer = timer_set (5.0 / 60, cia[This].number, cia_tod_timeout);
					}
					else
					{
						if (cia[This].intf.todin50hz != 0)
							cia[This].todtimer = timer_set (60 / 5.0, cia[This].number, cia_tod_timeout);
						else
							cia[This].todtimer = timer_set (0.1, cia[This].number, cia_tod_timeout);
					}
				}
				cia[This].todstopped = 0;
			}
			break;
		case 9:
			if (TOD_ALARM(cia[This]) != 0)
				cia[This].alarmsec = data;
			else
				cia[This].todsec = data;
			break;
		case 0xa:
			if (TOD_ALARM(cia[This]) != 0)
				cia[This].alarmmin = data;
			else
				cia[This].todmin = data;
			break;
		case 0xb:
			if (TOD_ALARM(cia[This]) != 0)
				cia[This].alarmhour = data;
			else
			{
				if (cia[This].todtimer != null)
					timer_remove (cia[This].todtimer);
				cia[This].todtimer = null;
				cia[This].todstopped = 1;
				cia[This].todhour = data;
			}
			break;
		case 0xd:
/*TODO*///			DBG_LOG (1, "cia interrupt enable", ("%d %.2x\n", cia[This].number, data));
			if ((data & 0x80) != 0)
			{
				cia[This].ier |= data;
				cia_set_interrupt (This, 0);
			}
			else
			{
				cia[This].ier &= ~data;
				cia_clear_interrupt (This, data & 0x1f);
			}
			break;
		case 4:
			cia[This].t1l = (cia[This].t1l & ~0xff) | data;
			if (cia[This].t1l == 0)
				cia[This].t1l = 0x10000;		   /*avoid hanging in timer_schedule */
/*TODO*///			DBG_LOG (3, "cia timer 1 lo write", ("%d %.2x\n", cia[This].number, data));
			break;
		case 5:
			cia[This].t1l = (cia[This].t1l & 0xff) | (data << 8);
			if (cia[This].t1l == 0)
				cia[This].t1l = 0x10000;		   /*avoid hanging in timer_schedule */
			if (TIMER1_STOP(cia[This]) != 0)
				cia[This].t1c = cia[This].t1l;
/*TODO*///			DBG_LOG (3, "cia timer 1 hi write", ("%d %.2x\n", This.number, data));
			break;
		case 6:
			cia[This].t2l = (cia[This].t2l & ~0xff) | data;
			if (cia[This].t2l == 0)
				cia[This].t2l = 0x10000;		   /*avoid hanging in timer_schedule */
/*TODO*///			DBG_LOG (3, "cia timer 2 lo write", ("%d %.2x\n", cia[This].number, data));
			break;
		case 7:
			cia[This].t2l = (cia[This].t2l & 0xff) | (data << 8);
			if (cia[This].t2l == 0)
				cia[This].t2l = 0x10000;		   /*avoid hanging in timer_schedule */
			if (TIMER2_STOP(cia[This]) != 0)
				cia[This].t2c = cia[This].t2l;
/*TODO*///			DBG_LOG (3, "cia timer 2 hi write", ("%d %.2x\n", cia[This].number, data));
			break;
		case 0xe:
/*TODO*///			DBG_LOG (3, "cia write cra", ("%d %.2x\n", cia[This].number, data));
			if ((cia[This].cra & 0x40) != (data & 0x40))
			{
				if ((cia[This].cra & 0x40)==0)
				{
					cia[This].loaded = 0;
					cia[This].shift = 0;
					cia[This].cnt = 1;
				}
			}
			cia[This].cra = data;
			cia_timer1_state (This);
			break;
		case 0xf:
/*TODO*///			DBG_LOG (3, "cia write crb", ("%d %.2x\n", This.number, data));
			cia[This].crb = data;
			cia_timer2_state (This);
			break;
		case 0xc:
			cia[This].sdr = data;
			if (SERIAL_MODE_OUT(cia[This]) != 0)
			{
				cia[This].loaded = 1;
			}
			break;
		}
	}
	
/*TODO*///	static void cia_set_input_a (CIA6526 *This, int data)
/*TODO*///	{
/*TODO*///		This->in_a = data;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void cia_set_input_b (CIA6526 *This, int data)
/*TODO*///	{
/*TODO*///		This->in_b = data;
/*TODO*///	}
	
	public static void cia6526_set_input_flag (int This, int data)
	{
		if (cia[This].flag!=0 && data==0)
			cia_set_interrupt (This, 0x10);
		cia[This].flag = data;
	}
	
/*TODO*///	static void cia6526_set_input_sp (CIA6526 *This, int data)
/*TODO*///	{
/*TODO*///		This->sp = data;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void cia6526_set_input_cnt (CIA6526 *This, int data)
/*TODO*///	{
/*TODO*///		if (!This->cnt && data)
/*TODO*///		{
/*TODO*///			if (SERIAL_MODE_OUT == 0)
/*TODO*///			{
/*TODO*///				This->serial >>= 1;
/*TODO*///				if (This->sp)
/*TODO*///					This->serial |= 0x80;
/*TODO*///				if (++This->shift == 8)
/*TODO*///				{
/*TODO*///					This->sdr = This->serial;
/*TODO*///					This->serial = 0;
/*TODO*///					This->shift = 0;
/*TODO*///					cia_set_interrupt (This, 8);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		This->cnt = data;
/*TODO*///	}
/*TODO*///	
        public static ReadHandlerPtr cia6526_0_port_r = new ReadHandlerPtr() {
            public int handler(int offset) {
                return cia6526_read (0, offset);
            }
        };

        public static ReadHandlerPtr cia6526_1_port_r = new ReadHandlerPtr() {
            public int handler(int offset) {
		return cia6526_read (1, offset);
            }
        };
        
/*TODO*///	READ_HANDLER ( cia6526_2_port_r )
/*TODO*///	{
/*TODO*///		return cia6526_read (cia+2, offset);
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_3_port_r )
/*TODO*///	{
/*TODO*///		return cia6526_read (cia+3, offset);
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_4_port_r )
/*TODO*///	{
/*TODO*///		return cia6526_read (cia+4, offset);
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_5_port_r )
/*TODO*///	{
/*TODO*///		return cia6526_read (cia+5, offset);
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_6_port_r )
/*TODO*///	{
/*TODO*///		return cia6526_read (cia+6, offset);
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_7_port_r )
/*TODO*///	{
/*TODO*///		return cia6526_read (cia+7, offset);
/*TODO*///	}
/*TODO*///	
        public static WriteHandlerPtr cia6526_0_port_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                cia6526_write (0, offset, data);
            }
        };

        public static WriteHandlerPtr cia6526_1_port_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
		cia6526_write (1, offset, data);
            }
        };
        
/*TODO*///	WRITE_HANDLER ( cia6526_2_port_w )
/*TODO*///	{
/*TODO*///		cia6526_write (cia+2, offset, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_3_port_w )
/*TODO*///	{
/*TODO*///		cia6526_write (cia+3, offset, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_4_port_w )
/*TODO*///	{
/*TODO*///		cia6526_write (cia+4, offset, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_5_port_w )
/*TODO*///	{
/*TODO*///		cia6526_write (cia+5, offset, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_6_port_w )
/*TODO*///	{
/*TODO*///		cia6526_write (cia+6, offset, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_7_port_w )
/*TODO*///	{
/*TODO*///		cia6526_write (cia+7, offset, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/******************* 8-bit A/B port interfaces *******************/
/*TODO*///	
/*TODO*///	WRITE_HANDLER ( cia6526_0_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_1_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+1, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_2_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+2, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_3_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+3, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_4_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+4, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_5_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+5, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_6_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+6, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_7_porta_w )
/*TODO*///	{
/*TODO*///		cia_set_input_a (cia+7, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	WRITE_HANDLER ( cia6526_0_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_1_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+1, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_2_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+2, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_3_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+3, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_4_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+4, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_5_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+5, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_6_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+6, data);
/*TODO*///	}
/*TODO*///	WRITE_HANDLER ( cia6526_7_portb_w )
/*TODO*///	{
/*TODO*///		cia_set_input_b (cia+7, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	READ_HANDLER ( cia6526_0_porta_r )
/*TODO*///	{
/*TODO*///		return cia[0].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_1_porta_r )
/*TODO*///	{
/*TODO*///		return cia[1].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_2_porta_r )
/*TODO*///	{
/*TODO*///		return cia[2].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_3_porta_r )
/*TODO*///	{
/*TODO*///		return cia[3].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_4_porta_r )
/*TODO*///	{
/*TODO*///		return cia[4].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_5_porta_r )
/*TODO*///	{
/*TODO*///		return cia[5].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_6_porta_r )
/*TODO*///	{
/*TODO*///		return cia[6].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_7_porta_r )
/*TODO*///	{
/*TODO*///		return cia[7].in_a;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_0_portb_r )
/*TODO*///	{
/*TODO*///		return cia[0].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_1_portb_r )
/*TODO*///	{
/*TODO*///		return cia[1].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_2_portb_r )
/*TODO*///	{
/*TODO*///		return cia[2].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_3_portb_r )
/*TODO*///	{
/*TODO*///		return cia[3].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_4_portb_r )
/*TODO*///	{
/*TODO*///		return cia[4].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_5_portb_r )
/*TODO*///	{
/*TODO*///		return cia[5].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_6_portb_r )
/*TODO*///	{
/*TODO*///		return cia[6].in_b;
/*TODO*///	}
/*TODO*///	READ_HANDLER ( cia6526_7_portb_r )
/*TODO*///	{
/*TODO*///		return cia[7].in_b;
/*TODO*///	}
	
	public static void cia6526_0_set_input_flag (int data)
	{
		cia6526_set_input_flag (0, data);
	}
/*TODO*///	void cia6526_1_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+1, data);
/*TODO*///	}
/*TODO*///	void cia6526_2_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+2, data);
/*TODO*///	}
/*TODO*///	void cia6526_3_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+3, data);
/*TODO*///	}
/*TODO*///	void cia6526_4_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+4, data);
/*TODO*///	}
/*TODO*///	void cia6526_5_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+5, data);
/*TODO*///	}
/*TODO*///	void cia6526_6_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+6, data);
/*TODO*///	}
/*TODO*///	void cia6526_7_set_input_flag (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_flag (cia+7, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void cia6526_0_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia, data);
/*TODO*///	}
/*TODO*///	void cia6526_1_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+1, data);
/*TODO*///	}
/*TODO*///	void cia6526_2_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+2, data);
/*TODO*///	}
/*TODO*///	void cia6526_3_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+3, data);
/*TODO*///	}
/*TODO*///	void cia6526_4_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+4, data);
/*TODO*///	}
/*TODO*///	void cia6526_5_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+5, data);
/*TODO*///	}
/*TODO*///	void cia6526_6_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+6, data);
/*TODO*///	}
/*TODO*///	void cia6526_7_set_input_sp (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_sp (cia+7, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void cia6526_0_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia, data);
/*TODO*///	}
/*TODO*///	void cia6526_1_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+1, data);
/*TODO*///	}
/*TODO*///	void cia6526_2_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+2, data);
/*TODO*///	}
/*TODO*///	void cia6526_3_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+3, data);
/*TODO*///	}
/*TODO*///	void cia6526_4_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+4, data);
/*TODO*///	}
/*TODO*///	void cia6526_5_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+5, data);
/*TODO*///	}
/*TODO*///	void cia6526_6_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+6, data);
/*TODO*///	}
/*TODO*///	void cia6526_7_set_input_cnt (int data)
/*TODO*///	{
/*TODO*///		cia6526_set_input_cnt (cia+7, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void cia6526_status (char *text, int size)
/*TODO*///	{
/*TODO*///		text[0] = 0;
/*TODO*///	#if VERBOSE_DBG
/*TODO*///	#if 0
/*TODO*///		snprintf (text, size, "cia ier:%.2x ifr:%.2x %d", cia[1].ier, cia[1].ifr,
/*TODO*///				  cia[1].flag);
/*TODO*///	#endif
/*TODO*///	#if 0
/*TODO*///		snprintf (text, size, "cia 1 %.2x %.2x %.2x %.2x",
/*TODO*///				  cia[1].tod10ths, cia[1].todsec,
/*TODO*///				  cia[1].todmin, cia[1].todhour);
/*TODO*///	
/*TODO*///	#endif
/*TODO*///	#endif
/*TODO*///	}
}
