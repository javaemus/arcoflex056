/************************************************************

 NEC uPD7759 ADPCM Speech Processor
 by: Juergen Buchmueller, Mike Balfour and Howie Cohen


 Description:
 The uPD7759 is a speech processing LSI that, with an external
 ROM, utilizes ADPCM to produce speech.  The uPD7759 can
 directly address up to 1Mbits of external data ROM, or the
 host CPU can control the speech data transfer.  Three sample
 frequencies are selectable - 5, 6, or 8 kHz.  The external
 ROM can store a maximum of 256 different messages and up to
 50 seconds of speech.

 The uPD7759 should always be hooked up to a 640 kHz clock.

 TODO:
 1) find bugs
 2) fix bugs
 3) Bankswitching and frequency selection may not be 100%

NOTES:

There are 2 types of upd7759 sound roms, master and slave.

A master rom has a header at the beginning of the rom
for example : 15 5A A5 69 55 (this is the POW header)

-the 1st byte (15) is the number of samples stored in the rom
 (actually the number of samples minus 1 - NS)
-the next 4 bytes seems standard in every upd7759 rom used in
master mode (5A A5 69 55)
-after that there is table of (sample offsets)/2 we use this to
calculate the sample table on the fly. Then the samples start,
each sample has a short header with sample rate info in it.
A master rom can have up to 256 samples , and there should be
only one rom per upd7759.

a slave rom has no header... but each sample starts with
FF 00 00 00 00 10 (followed by a few other bytes that are
usually the same but not always)

Clock rates:
in master mode the clock rate should always be 64000000
sample frequencies are selectable - 5, 6, or 8 kHz. This
info is coded in the uPD7759 roms, it selects the sample
frequency for you.

slave mode is still some what of a mystery.  Everything
we know about slave mode (and 1/2 of everything in master
mode) is from guesswork. As far as we know the clock rate
is the same for all samples in slave mode on a per game basis
so valid clock rates are: 40000000, 48000000, 64000000

Differances between master/slave mode.
(very basic explanation based on my understanding)

Master mode: the sound cpu sends a sample number to the upd7759
it then sends a trigger command to it, and the upd7759 plays the
sample directly from the rom (like an adpcm chip)

Slave mode:  the sound cpu sends data to the upd7759 to select
the bank for the sound data, each bank is 0x4000 in size, and
the sample offset. The sound cpu then sends the data for the sample
a byte(?) at a time (dac / cvsd like) which the upd7759 plays till
it reaches the header of the next sample (FF 00 00 00 00)

Changes:
05/99	HJB
	Tried to figure better index_shift and diff_lookutp tables and
	also adjusted sample value range. It seems the 4 bits of the
	ADPCM data are signed (0x0f == -1)! Also a signal and step
	width fall off seems to be closer to the real thing.
	Reduced work load by adding a wrap around buffer for slave
	mode data that is stuffed by the sound CPU.
	Finally removed (now obsolete) 8 bit sample support.

 *************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.sound;

import static arcadeflex056.fucPtr.*;
import static arcadeflex056.osdepend.logerror;

import static common.libc.cstdio.*;
import static common.libc.cstring.memset;
import static common.ptr.*;
import common.subArrays.IntArray;

import static mame056.common.*;
import static mame056.mame.*;
import static mame056.sndintrf.*;
import static mame056.sndintrfH.*;

import static mame056.sound.upd7759H.*;
import static mame056.sound.streams.*;
import static mame056.timer.*;
import static mame056.timerH.*;

public class upd7759 extends snd_interface
{
	
	
/*TODO*///	#define VERBOSE 0
	
/*TODO*///	#if VERBOSE
/*TODO*///	#define LOG(n,x)  if((n)>=VERBOSE) logerror x
/*TODO*///	#else
/*TODO*///	#define LOG(n,x)
/*TODO*///	#endif
	
	/* number of samples stuffed into the rom */
	static int numsam;
	
	/* playback rate for the streams interface */
	/* BASE_CLOCK or a multiple (if oversampling is active) */
	static int emulation_rate;
	
	static int base_rate;
	/* define the output rate */
	public static final int CLOCK_DIVIDER	= 80;
	
	public static final int OVERSAMPLING	= 0;	/* 1 use oversampling, 0 don't */
	
	/* signal fall off factor */
	public static int FALL_OFF(int n){ return ((n)-(((n)+7)/8)); }
	
	public static final int SIGNAL_BITS 	= 15;	/* signal range */
	public static int SIGNAL_MAX(){ return (0x7fff >> (15-SIGNAL_BITS)); }
	public static int SIGNAL_MIN(){ return -SIGNAL_MAX(); }
	
	public static final int STEP_MAX	= 32;
	public static final int STEP_MIN	= 0;
	
	public static final int DATA_MAX	= 512;
        
        public upd7759(){
            this.sound_num = SOUND_UPD7759;
            this.name = "uPD7759";
            
        }
	
	public static class UPD7759sample
	{
		public int offset;	/* offset in that region */
		public int length;    /* length of the sample */
		public int freq;		/* play back freq of sample */
	};
	
	
	/* struct describing a single playing ADPCM voice */
	public static class UPD7759voice
	{
		public int playing;            /* 1 if we are actively playing */
		public UBytePtr base = new UBytePtr();    /* pointer to the base memory location */
		public int mask;               /* mask to keep us within the buffer */
		public int sample; 			/* current sample number (sample data in slave mode) */
		public int freq;				/* current sample playback freq */
		public int count;              /* total samples to play */
		public int signal;             /* current ADPCM signal */
/*TODO*///	#if OVERSAMPLING
		public int old_signal; 		/* last ADPCM signal */
/*TODO*///	#endif
                public int step;               /* current ADPCM step */
		public int counter;			/* sample counter */
		public timer_entry timer;			/* timer used in slave mode */
		public int[] data = new int[DATA_MAX]; 	/* data array used in slave mode */
		public int head;			/* head of data array used in slave mode */
		public int tail;			/* tail of data array used in slave mode */
		public int available;
	};
	
	/* global pointer to the current interface */
	static UPD7759_interface upd7759_intf;
	static int[] upd7759_bank_base = new int[MAX_UPD7759];
	
	/* array of ADPCM voices */
	static UPD7759voice[] updadpcm = new UPD7759voice[MAX_UPD7759];
	
/*TODO*///	#if OVERSAMPLING
	/* oversampling factor, ie. playback_rate / BASE_CLOCK */
	static int oversampling;
/*TODO*///	#endif
	/* array of channels returned by streams.c */
	static int[] channel = new int[MAX_UPD7759];
	
	/* stores the current sample number */
	static int[] sampnum = new int[MAX_UPD7759];
	
	/* step size index shift table */
	public static final int INDEX_SHIFT_MAX = 16;
	static int index_shift[] = {
		0,	 1,  2,  3,  6,  7, 10, 15,
		0,  15, 10,  7,  6,  3,  2,  1
	};
	
	/* lookup table for the precomputed difference */
	static int[] diff_lookup = new int[(STEP_MAX+1)*16];
	
	/*
	 *   Compute the difference table
	 */
        static int nbl2bit[][] = {
			{ 1, 0, 0, 0}, { 1, 0, 0, 1}, { 1, 0, 1, 0}, { 1, 0, 1, 1},
			{ 1, 1, 0, 0}, { 1, 1, 0, 1}, { 1, 1, 1, 0}, { 1, 1, 1, 1},
			{-1, 0, 0, 0}, {-1, 0, 0, 1}, {-1, 0, 1, 0}, {-1, 0, 1, 1},
			{-1, 1, 0, 0}, {-1, 1, 0, 1}, {-1, 1, 1, 0}, {-1, 1, 1, 1},
		};
	
	static void ComputeTables ()
	{
		/* nibble to bit map */
	    
	    int step, nib;
	
		/* loop over all possible steps */
		for (step = 0; step <= STEP_MAX; step++)
		{
	        /* compute the step value */
			int stepval = 6 * (step+1) * (step+1);
/*TODO*///			LOG(1,("step %2d:", step));
			/* loop over all nibbles and compute the difference */
			for (nib = 0; nib < 16; nib++)
			{
				diff_lookup[step*16 + nib] = nbl2bit[nib][0] *
					(stepval   * nbl2bit[nib][1] +
					 stepval/2 * nbl2bit[nib][2] +
					 stepval/4 * nbl2bit[nib][3] +
					 stepval/8);
/*TODO*///				LOG(1,(" %+6d", diff_lookup[step*16 + nib]));
	        }
/*TODO*///			LOG(1,("\n"));
	    }

    
	}
	
        @Override
        public int chips_num(MachineSound msound) {
            return ((UPD7759_interface) msound.sound_interface).num;
        }

        @Override
        public int chips_clock(MachineSound msound) {
            return ((UPD7759_interface) msound.sound_interface).clock_rate;
        }

        @Override
        public int start(MachineSound msound) {
            System.out.println("Starting SOUND_UPD7759");
            return UPD7759_sh_start(msound);
        }

        @Override
        public void stop() {
            UPD7759_sh_stop();
        }

        @Override
        public void update() {
            // NOTHING TO DO
        }

        @Override
        public void reset() {
            // NOTHING TO DO
        }
	
	
	static UPD7759sample find_sample(int num, int sample_num, UPD7759sample sample)
	{
		int j;
		int nextoff = 0;
		UBytePtr memrom;
		UBytePtr header;   /* upd7759 has a 4 byte what we assume is an identifier (bytes 1-4)*/
		UBytePtr data;
	
	
		memrom = new UBytePtr(memory_region(upd7759_intf.region[num]), upd7759_bank_base[num]);
	
		numsam = memrom.read(0); /* get number of samples from sound rom */
		header = new UBytePtr(memrom, 1);
	
/*TODO*///		if (memcmp (header, "\x5A\xA5\x69\x55",4) == 0)
/*TODO*///		{
/*TODO*///			LOG(1,("uPD7759 header verified\n"));
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			LOG(1,("uPD7759 header verification failed\n"));
/*TODO*///		}
/*TODO*///	
/*TODO*///		LOG(1,("Number of samples in UPD7759 rom = %d\n",numsam));
	
		/* move the header pointer to the start of the sample offsets */
		header = new UBytePtr(memrom, 5);
	
	
		if (sample_num > numsam) return null;	/* sample out of range */
	
	
		nextoff = 2 * sample_num;
		sample.offset = ((((header.read(nextoff)))<<8)+(header.read(nextoff+1)))*2;
		sample.offset += upd7759_bank_base[num];
		data = new UBytePtr(memory_region(upd7759_intf.region[num]), sample.offset);
		/* guesswork, probably wrong */
		j = 0;
		if (data.read(j) == 0) j++;
		if ((data.read(j) & 0xf0) != 0x50) j++;
	
		/* Added and Modified by Takahiro Nogi. 1999/10/28 */
/*TODO*///	#if 0	/* original */
/*TODO*///		switch (data[j])
/*TODO*///		{
/*TODO*///			case 0x53: sample.freq = 8000; break;
/*TODO*///			case 0x59: sample.freq = 6000; break;
/*TODO*///			case 0x5f: sample.freq = 5000; break;
/*TODO*///			default:
/*TODO*///				sample.freq = 5000;
/*TODO*///		}
/*TODO*///	#else	// modified by Takahiro Nogi. 1999/10/28
		switch (data.read(j) & 0x1f)
		{
			case 0x13: sample.freq = 8000; break;
			case 0x19: sample.freq = 6000; break;
			case 0x1f: sample.freq = 5000; break;
			default:				// ???
				sample.freq = 5000;
		}
/*TODO*///	#endif
	
		if (sample_num == numsam)
		{
			sample.length = 0x20000 - sample.offset;
		}
		else
			sample.length = ((((header.read(nextoff+2)))<<8)+(header.read(nextoff+3)))*2 -
								((((header.read(nextoff)))<<8)+(header.read(nextoff+1)))*2;
	
		data = new UBytePtr(memory_region(upd7759_intf.region[num]), sample.offset);
		logerror("play sample %3d, offset $%06x, length %5d, freq = %4d [data $%02x $%02x $%02x]\n",
			sample_num,
			sample.offset,
			sample.length,
			sample.freq,
			data.read(0),data.read(1),data.read(2));
	
		return sample;
	}
	
	
	/*
	 *   Start emulation of several ADPCM output streams
	 */
	
	
	static int UPD7759_sh_start (MachineSound msound)
	{
            System.out.println("UPD7759_sh_start");
		int i;
		upd7759_intf = (UPD7759_interface) msound.sound_interface;
	
		if( Machine.sample_rate == 0 )
			return 0;
	
	    /* compute the difference tables */
		ComputeTables ();
	
	    /* copy the interface pointer to a global */
		base_rate = upd7759_intf.clock_rate / CLOCK_DIVIDER;
	
		memset(upd7759_bank_base, 0, upd7759_bank_base.length);
	
                if (OVERSAMPLING != 0){
                        oversampling = (Machine.sample_rate / base_rate);
                        if (oversampling == 0) oversampling = 1;
                        emulation_rate = base_rate * oversampling;
                } else {
                        emulation_rate = base_rate;
                }
	
	
/*TODO*///		memset(updadpcm,0,updadpcm.length);
		for (i = 0; i < upd7759_intf.num; i++)
		{
			String name="";
                        
                        updadpcm[i] = new UPD7759voice();
	
			updadpcm[i].mask = 0xffffffff;
			updadpcm[i].signal = 0;
			updadpcm[i].step = 0;
			updadpcm[i].counter = emulation_rate / 2;
	
			name = sprintf("uPD7759 #%d",i);
	
			channel[i] = stream_init(name,upd7759_intf.volume[i],emulation_rate,i,UPD7759_update);
		}
		return 0;
	}
	
	
	/*
	 *   Stop emulation of several UPD7759 output streams
	 */
	
	static void UPD7759_sh_stop ()
	{
	}
	
	
	/*
	 *   Set an offset to the base of the ROM within the region
	 */
	
	static void UPD7759_set_bank_base(int which, int base)
	{
		if (which < MAX_UPD7759)
			upd7759_bank_base[which] = base;
	}
	
	
	/*
	 *   Update emulation of an uPD7759 output stream
	 */
	static StreamInitPtr UPD7759_update = new StreamInitPtr() {
            @Override
            public void handler(int chip, ShortPtr buffer, int left) {
                //UPD7759voice voice = updadpcm[chip];
		int i;
	
		/* see if there's actually any need to generate samples */
/*TODO*///		LOG(3,("UPD7759_update %d (%d)\n", left, voice.available));
	
	    if (left > 0)
		{
	        /* if this voice is active */
			if (updadpcm[chip].playing != 0)
			{
				updadpcm[chip].available -= left;
				if( upd7759_intf.mode == UPD7759_SLAVE_MODE )
				{
					while( left-- > 0 )
					{
						buffer.write( updadpcm[chip].data[updadpcm[chip].tail] );
                                                buffer.offset++;
/*TODO*///	#ifdef OVERSAMPLE
/*TODO*///						if( (voice.counter++ % OVERSAMPLE) == 0 )
/*TODO*///	#endif
	                    updadpcm[chip].tail = (updadpcm[chip].tail + 1) % DATA_MAX;
					}
				}
				else
				{
					UBytePtr base = new UBytePtr(updadpcm[chip].base);
	                int val;
/*TODO*///	#if OVERSAMPLING
					int delta;
/*TODO*///	#endif
	
	                while( left > 0 )
					{
						/* compute the new amplitude and update the current voice.step */
						val = base.read((updadpcm[chip].sample / 2) & updadpcm[chip].mask) >> (((updadpcm[chip].sample & 1) << 2) ^ 4);
						updadpcm[chip].step = FALL_OFF(updadpcm[chip].step) + index_shift[val & (INDEX_SHIFT_MAX-1)];
						if (updadpcm[chip].step > STEP_MAX) updadpcm[chip].step = STEP_MAX;
						else if (updadpcm[chip].step < STEP_MIN) updadpcm[chip].step = STEP_MIN;
						updadpcm[chip].signal = FALL_OFF(updadpcm[chip].signal) + diff_lookup[updadpcm[chip].step * 16 + (val & 15)];
						if (updadpcm[chip].signal > SIGNAL_MAX()) updadpcm[chip].signal = SIGNAL_MAX();
						else if (updadpcm[chip].signal < SIGNAL_MIN()) updadpcm[chip].signal = SIGNAL_MIN();
/*TODO*///	#if OVERSAMPLING
/*TODO*///						i = 0;
/*TODO*///						delta = voice.signal - voice.old_signal;
/*TODO*///						while (voice.counter > 0 && left > 0)
/*TODO*///						{
/*TODO*///							*sample++ = voice.old_signal + delta * i / oversampling;
/*TODO*///							if (++i == oversampling) i = 0;
/*TODO*///							voice.counter -= voice.freq;
/*TODO*///							left--;
/*TODO*///						}
/*TODO*///						voice.old_signal = voice.signal;
/*TODO*///	#else
						while (updadpcm[chip].counter > 0 && left > 0)
						{
							buffer.write( updadpcm[chip].signal );
                                                        buffer.offset++;
							updadpcm[chip].counter -= updadpcm[chip].freq;
							left--;
						}
/*TODO*///	#endif
						updadpcm[chip].counter += emulation_rate;
	
						/* next! */
						if( ++updadpcm[chip].sample > updadpcm[chip].count )
						{
							while (left-- > 0)
							{
								buffer.write(updadpcm[chip].signal);
                                                                buffer.offset++;
								updadpcm[chip].signal = FALL_OFF(updadpcm[chip].signal);
							}
							updadpcm[chip].playing = 0;
							break;
						}
					}
	            }
			}
			else
			{
				/* voice is not playing */
				for (i = 0; i < left; i++){
					buffer.write( updadpcm[chip].signal );
                                        buffer.offset++;
                                }
			}
		}
            }
        };
	
	/************************************************************
	 UPD7759_message_w
	
	 Store the inputs to I0-I7 externally to the uPD7759.
	
	 I0-I7 input the message number of the message to be
	 reproduced. The inputs are latched at the rising edge of the
	 !ST input. Unused pins should be grounded.
	
	 In slave mode it seems like the ADPCM data is stuffed
	 here from an external source (eg. Z80 NMI code).
	 *************************************************************/
	
	public static void UPD7759_message_w (int num, int data)
	{
		//UPD7759voice voice = updadpcm[num];
	
		/* bail if we're not playing anything */
		if (Machine.sample_rate == 0)
			return;
	
		/* range check the numbers */
		if( num >= upd7759_intf.num )
		{
/*TODO*///			LOG(1,("error: UPD7759_SNDSELECT() called with channel = %d, but only %d channels allocated\n", num, upd7759_intf.num));
			return;
		}
	
		if (upd7759_intf.mode == UPD7759_SLAVE_MODE)
		{
			int offset = -1;
	
			//LOG(1,("upd7759_message_w $%02x\n", data));
			logerror("upd7759_message_w $%2x\n",data);
	
	        switch (data) {
	
				case 0x00: 							/* roms 0x10000 & 0x20000 in size */
				case 0x38: offset = 0x10000; break; /* roms 0x8000 in size */
	
				case 0x01: 							/* roms 0x10000 & 0x20000 in size */
				case 0x39: offset = 0x14000; break; /* roms 0x8000 in size */
	
				case 0x02: 							/* roms 0x10000 & 0x20000 in size */
				case 0x34: offset = 0x18000; break; /* roms 0x8000 in size */
	
				case 0x03: 							/* roms 0x10000 & 0x20000 in size */
				case 0x35: offset = 0x1c000; break; /* roms 0x8000 in size */
	
				case 0x04:							/* roms 0x10000 & 0x20000 in size */
				case 0x2c: offset = 0x20000; break; /* roms 0x8000 in size */
	
				case 0x05: 							/* roms 0x10000 & 0x20000 in size */
				case 0x2d: offset = 0x24000; break; /* roms 0x8000 in size */
	
				case 0x06:							/* roms 0x10000 & 0x20000 in size */
				case 0x1c: offset = 0x28000; break;	/* roms 0x8000 in size in size */
	
				case 0x07: 							/* roms 0x10000 & 0x20000 in size */
				case 0x1d: offset = 0x2c000; break;	/* roms 0x8000 in size */
	
				case 0x08: offset = 0x30000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x09: offset = 0x34000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x0a: offset = 0x38000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x0b: offset = 0x3c000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x0c: offset = 0x40000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x0d: offset = 0x44000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x0e: offset = 0x48000; break; /* roms 0x10000 & 0x20000 in size */
				case 0x0f: offset = 0x4c000; break; /* roms 0x10000 & 0x20000 in size */
	
				default:
	
					//LOG(1,("upd7759_message_w unhandled $%02x\n", data));
					logerror("upd7759_message_w unhandled $%02x\n", data);
					if ((data & 0xc0) == 0xc0)
					{
						if (updadpcm[num].timer != null)
						{
							timer_remove(updadpcm[num].timer);
							updadpcm[num].timer = null;
						}
						updadpcm[num].playing = 0;
					}
	        }
			if (offset > 0)
			{
				updadpcm[num].base = new UBytePtr(memory_region(upd7759_intf.region[num]), offset);
				//LOG(1,("upd7759_message_w set base $%08x\n", offset));
				logerror("upd7759_message_w set base $%08x\n", offset);
	        }
		}
		else
		{
	
/*TODO*///			LOG(1,("uPD7759 calling sample : %d\n", data));
			sampnum[num] = data;
	
	    }
	}
	
	/************************************************************
	 UPD7759_dac
	
	 Called by the timer interrupt at twice the sample rate.
	 The first time the external irq callback is called, the
	 second time the ADPCM msb is converted and the resulting
	 signal is sent to the DAC.
	 ************************************************************/
        static int dac_msb = 0;
        
	static timer_callback UPD7759_dac = new timer_callback() {
            public void handler(int num) {
		
		//UPD7759voice voice = updadpcm[num];
	
		dac_msb ^= 1;
		if( dac_msb != 0 )
		{
/*TODO*///			LOG(3,("UPD7759_dac:    $%x ", voice.sample & 15));
	        /* convert lower nibble */
			updadpcm[num].step = FALL_OFF(updadpcm[num].step) + index_shift[updadpcm[num].sample & (INDEX_SHIFT_MAX-1)];
	        if (updadpcm[num].step > STEP_MAX) updadpcm[num].step = STEP_MAX;
	        else if (updadpcm[num].step < STEP_MIN) updadpcm[num].step = STEP_MIN;
			updadpcm[num].signal = FALL_OFF(updadpcm[num].signal) + diff_lookup[updadpcm[num].step * 16 + (updadpcm[num].sample & 15)];
			if (updadpcm[num].signal > SIGNAL_MAX()) updadpcm[num].signal = SIGNAL_MAX();
			else if (updadpcm[num].signal < SIGNAL_MIN()) updadpcm[num].signal = SIGNAL_MIN();
/*TODO*///			LOG(3,("step: %3d signal: %+5d\n", voice.step, voice.signal));
			updadpcm[num].head = (updadpcm[num].head + 1) % DATA_MAX;
			updadpcm[num].data[updadpcm[num].head] = updadpcm[num].signal;
			updadpcm[num].available++;
                } else {
                            if( upd7759_intf.irqcallback[num] != null )
                                    (upd7759_intf.irqcallback[num]).handler(num);
                }
            }
        };
	
	/************************************************************
	 UPD7759_start_w
	
	 !ST pin:
	 Setting the !ST input low while !CS is low will start
	 speech reproduction of the message in the speech ROM locations
	 addressed by the contents of I0-I7. If the device is in
	 standby mode, standby mode will be released.
	 NOTE: While !BUSY is low, another !ST will not be accepted.
	 *************************************************************/
	
	public static void UPD7759_start_w (int num, int data)
	{
		//UPD7759voice voice = updadpcm[num];
	
		/* bail if we're not playing anything */
		if (Machine.sample_rate == 0)
			return;
	
		/* range check the numbers */
		if( num >= upd7759_intf.num )
		{
/*TODO*///			LOG(1,("error: UPD7759_play_stop() called with channel = %d, but only %d channels allocated\n", num, upd7759_intf.num));
			return;
		}
	
		/* handle the slave mode */
		if (upd7759_intf.mode == UPD7759_SLAVE_MODE)
		{
			if (updadpcm[num].playing != 0)
			{
	            /* if the chip is busy this should be the ADPCM data */
				data &= 0xff;	/* be sure to use 8 bits value only */
/*TODO*///				LOG(3,("UPD7759_data_w: $%x ", (data >> 4) & 15));
	
	            /* detect end of a sample by inspection of the last 5 bytes */
				/* FF 00 00 00 00 is the start of the next sample */
				if( updadpcm[num].count > 5 && updadpcm[num].sample == 0xff && data == 0x00 )
				{
	                /* remove an old timer */
	                if (updadpcm[num].timer != null)
	                {
	                    timer_remove(updadpcm[num].timer);
	                    updadpcm[num].timer = null;
					}
	                /* stop playing this sample */
					updadpcm[num].playing = 0;
					return;
	            }
	
				/* save the data written in voice.sample */
				updadpcm[num].sample = data;
				updadpcm[num].count++;
	
	            /* conversion of the ADPCM data to a new signal value */
                    updadpcm[num].step = FALL_OFF(updadpcm[num].step) + index_shift[(updadpcm[num].sample >> 4) & (INDEX_SHIFT_MAX-1)];
	            if (updadpcm[num].step > STEP_MAX) updadpcm[num].step = STEP_MAX;
	            else if (updadpcm[num].step < STEP_MIN) updadpcm[num].step = STEP_MIN;
				updadpcm[num].signal = FALL_OFF(updadpcm[num].signal) + diff_lookup[updadpcm[num].step * 16 + ((updadpcm[num].sample >> 4) & 15)];
	            if (updadpcm[num].signal > SIGNAL_MAX()) updadpcm[num].signal = SIGNAL_MAX();
	            else if (updadpcm[num].signal < SIGNAL_MIN()) updadpcm[num].signal = SIGNAL_MIN();
/*TODO*///				LOG(3,("step: %3d signal: %+5d\n", voice.step, voice.signal));
				updadpcm[num].head = (updadpcm[num].head + 1) % DATA_MAX;
				updadpcm[num].data[updadpcm[num].head] = updadpcm[num].signal;
				updadpcm[num].available++;
			}
			else
			{
/*TODO*///				LOG(2,("UPD7759_start_w: $%02x\n", data));
                                /* remove an old timer */
				if (updadpcm[num].timer != null)
				{
                                    timer_remove(updadpcm[num].timer);
                                    updadpcm[num].timer = null;
                                }
				/* bring the chip in sync with the CPU */
				stream_update(channel[num], 0);
	            /* start a new timer */
				updadpcm[num].timer = timer_pulse(TIME_IN_HZ(base_rate), num, UPD7759_dac );
				updadpcm[num].signal = 0;
				updadpcm[num].step = 0;	/* reset the step width */
				updadpcm[num].count = 0;	/* reset count for the detection of an sample ending */
				updadpcm[num].playing = 1; /* this voice is now playing */
                                updadpcm[num].tail = 0;
				updadpcm[num].head = 0;
				updadpcm[num].available = 0;
                    }
		}
		else
		{
			UPD7759sample sample=new UPD7759sample();
	
			/* if !ST is high, do nothing */ /* EHC - 13/08/99 */
			if (data > 0)
				return;
	
			/* bail if the chip is busy */
			if (updadpcm[num].playing != 0)
				return;
	
/*TODO*///			LOG(2,("UPD7759_start_w: %d\n", data));
	
			/* find a match */
                        sample=find_sample(num,sampnum[num],sample);
                        
			if (sample != null)
			{
				/* update the  voice */
				stream_update(channel[num], 0);
				updadpcm[num].freq = sample.freq;
				/* set up the voice to play this sample */
				updadpcm[num].playing = 1;
				updadpcm[num].base = new UBytePtr(memory_region(upd7759_intf.region[num]), sample.offset);
				updadpcm[num].sample = 0;
				/* sample length needs to be doubled (counting nibbles) */
				updadpcm[num].count = sample.length * 2;
	
				/* also reset the chip parameters */
				updadpcm[num].step = 0;
				updadpcm[num].counter = emulation_rate / 2;
	
				return;
			}
	
/*TODO*///			LOG(1,("warning: UPD7759_playing_w() called with invalid number = %08x\n",data));
		}
	}
	
	/************************************************************
	 UPD7759_data_r
	
	 External read data from the UPD7759 memory region based
	 on voice.base. Used in slave mode to retrieve data to
	 stuff into UPD7759_message_w.
	 *************************************************************/
	
	static int UPD7759_data_r(int num, int offs)
	{
            //UPD7759voice voice = updadpcm[num];
	
	    /* If there's no sample rate, do nothing */
	    if (Machine.sample_rate == 0)
			return 0x00;
	
	    /* range check the numbers */
		if( num >= upd7759_intf.num )
		{
/*TODO*///			LOG(1,("error: UPD7759_data_r() called with channel = %d, but only %d channels allocated\n", num, upd7759_intf.num));
			return 0x00;
	    }
	
		if ( updadpcm[num].base == null )
		{
/*TODO*///			LOG(1,("error: UPD7759_data_r() called with channel = %d, but updadpcm[%d].base == NULL\n", num, num));
			return 0x00;
		}
	
/*TODO*///	#if VERBOSE
/*TODO*///	    if (!(offs&0xff)) LOG(1, ("UPD7759#%d sample offset = $%04x\n", num, offs));
	/*TODO*///#endif
	
		return updadpcm[num].base.read(offs);
	}
	
	/************************************************************
	 UPD7759_busy_r
	
	 !BUSY pin:
	 !BUSY outputs the status of the uPD7759. It goes low during
	 speech decode and output operations. When !ST is received,
	 !BUSY goes low. While !BUSY is low, another !ST will not be
	 accepted. In standby mode, !BUSY becomes high impedance. This
	 is an active low output.
	 *************************************************************/
	
	static int UPD7759_busy_r (int num)
	{
		//UPD7759voice voice = updadpcm[num];
	
		/* If there's no sample rate, return not busy */
		if ( Machine.sample_rate == 0 )
			return 1;
	
		/* range check the numbers */
		if( num >= upd7759_intf.num )
		{
/*TODO*///			LOG(1,("error: UPD7759_busy_r() called with channel = %d, but only %d channels allocated\n", num, upd7759_intf.num));
			return 1;
		}
	
		/* bring the chip in sync with the CPU */
		stream_update(channel[num], 0);
	
		if ( updadpcm[num].playing == 0 )
		{
/*TODO*///			LOG(1,("uPD7759 not busy\n"));
			return 1;
		}
		else
		{
/*TODO*///			LOG(1,("uPD7759 busy\n"));
			return 0;
		}
	
/*TODO*///		return 1;
	}
	
	/************************************************************
	 UPD7759_reset_w
	
	 !RESET pin:
	 The !RESET input initialized the chip. Use !RESET following
	 power-up to abort speech reproduction or to release standby
	 mode. !RESET must remain low at least 12 oscillator clocks.
	 At power-up or when recovering from standby mode, !RESET
	 must remain low at least 12 more clocks after clock
	 oscillation stabilizes.
	 *************************************************************/
	
	public static void UPD7759_reset_w (int num, int data)
	{
		//UPD7759voice voice = updadpcm[num];
	
		/* If there's no sample rate, do nothing */
		if( Machine.sample_rate == 0 )
			return;
	
		/* range check the numbers */
		if( num >= upd7759_intf.num )
		{
/*TODO*///			LOG(1,("error: UPD7759_reset_w() called with channel = %d, but only %d channels allocated\n", num, upd7759_intf.num));
			return;
		}
	
		/* if !RESET is high, do nothing */
		if (data > 0)
			return;
	
		/* mark the uPD7759 as NOT PLAYING */
		/* (Note: do we need to do anything else?) */
		updadpcm[num].playing = 0;
	}
	
	
	/* helper functions to be used as memory read handler function pointers */
	public static WriteHandlerPtr UPD7759_0_message_w = new WriteHandlerPtr() {public void handler(int offset, int data)	{ UPD7759_message_w(0,data); } };
	public static WriteHandlerPtr UPD7759_0_start_w = new WriteHandlerPtr() {public void handler(int offset, int data)	{ UPD7759_start_w(0,data); } };
	public static ReadHandlerPtr UPD7759_0_busy_r  = new ReadHandlerPtr() { public int handler(int offset)	{ return UPD7759_busy_r(0); } };
	public static ReadHandlerPtr UPD7759_0_data_r  = new ReadHandlerPtr() { public int handler(int offset)	{ return UPD7759_data_r(0,offset); } };
	public static ReadHandlerPtr UPD7759_1_data_r  = new ReadHandlerPtr() { public int handler(int offset)	{ return UPD7759_data_r(1,offset); } };
}
