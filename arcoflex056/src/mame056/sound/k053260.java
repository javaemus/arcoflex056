/*********************************************************

	Konami 053260 PCM Sound Chip

*********************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.sound;

import static arcadeflex056.fucPtr.*;
import static common.libc.cstdio.sprintf;
import static common.ptr.*;
import static mame056.sndintrf.*;
import static mame056.sndintrfH.*;
import static mame056.common.*;
import static mame056.timer.*;
import static mame056.timerH.*;
import static mame056.cpuintrfH.*;
import static mame056.mame.*;
import static mame056.sound.streams.*;
import static mame056.sound.k053260H.*;
import static arcadeflex056.osdepend.logerror;

public class k053260  extends snd_interface {

	
/*TODO*///	#define LOG 0
	
	public static int BASE_SHIFT	= 16;

        @Override
        public int chips_num(MachineSound msound) {
            return intf.num;
        }

        @Override
        public int chips_clock(MachineSound msound) {
            return intf.clock[0];
        }

        @Override
        public int start(MachineSound msound) {
            return K053260_sh_start(msound);
        }

        @Override
        public void stop() {
            K053260_sh_stop();
        }

        @Override
        public void update() {
            // nothing to do
        }

        @Override
        public void reset() {
            K053260_reset(0);
            K053260_reset(1);
        }

    
	public class K053260_channel_def {
		public long		rate;
		public long		size;
		public long		start;
		public long		bank;
		public long		volume;
		public int					play;
		public long		pan;
		public long		pos;
		public int					loop;
		public int					ppcm; /* packed PCM ( 4 bit signed ) */
		public int					ppcm_data;
	};
	
	public class K053260_chip_def {
		public int				channel;
		public int				mode;
		public int[]				regs = new int[0x30];
		public UBytePtr                         rom = new UBytePtr();
		public int				rom_size;
		public timer_entry			timer; /* SH1 int timer */
		public long[]                           delta_table;
		public K053260_channel_def[]            channels = new K053260_channel_def[4];
                
	};
	
	static K053260_chip_def[] K053260_chip = new K053260_chip_def[MAX_053260];

	
	/* local copy of the interface */
	static K053260_interface intf;
	
	static void InitDeltaTable( int chip ) {
		int		i;
		double	base = ( double )Machine.sample_rate;
		double	max = (double)( intf.clock[chip] ); /* Hz */
		long val;
	
		for( i = 0; i < 0x1000; i++ ) {
			double v = ( double )( 0x1000 - i );
			double target = max / v;
			double fixed = ( double )( 1 << BASE_SHIFT );
	
			if ( target!=0 && base!=0 ) {
				target = fixed / ( base / target );
				val = (long) target;
				if ( val == 0 )
					val = 1;
			} else
				val = 1;
	
			K053260_chip[chip].delta_table[i] = val;
		}
	}
	
	static void K053260_reset( int chip ) {
		int i;
	
		for( i = 0; i < 4; i++ ) {
			K053260_chip[chip].channels[i].rate = 0;
			K053260_chip[chip].channels[i].size = 0;
			K053260_chip[chip].channels[i].start = 0;
			K053260_chip[chip].channels[i].bank = 0;
			K053260_chip[chip].channels[i].volume = 0;
			K053260_chip[chip].channels[i].play = 0;
			K053260_chip[chip].channels[i].pan = 0;
			K053260_chip[chip].channels[i].pos = 0;
			K053260_chip[chip].channels[i].loop = 0;
			K053260_chip[chip].channels[i].ppcm = 0;
			K053260_chip[chip].channels[i].ppcm_data = 0;
		}
	}
	
	public static int limit( int val, int max, int min ) {
		if ( val > max )
			val = max;
		else if ( val < min )
			val = min;
	
		return val;
	}
	
	public static int MAXOUT = 0x7fff;
	public static int MINOUT = -0x8000;
        
        static long dpcmcnv[] = { 0, 1, 4, 9, 16, 25, 36, 49, -64, -49, -36, -25, -16, -9, -4, -1 };
	
	public static StreamInitMultiPtr K053260_update = new StreamInitMultiPtr() {
            public void handler(int param, ShortPtr[] buffer, int length) {
            
		
		int i, j;
                int[] lvol=new int[4], rvol=new int[4], play=new int[4], loop=new int[4], ppcm_data=new int[4], ppcm=new int[4];
		UBytePtr[] rom = new UBytePtr[4];
		long[] delta=new long[4], end=new long[4], pos=new long[4];
		int dataL, dataR;
		int d;
		K053260_chip_def ic = K053260_chip[param];
	
		/* precache some values */
		for ( i = 0; i < 4; i++ ) {
			rom[i]= new UBytePtr(ic.rom,((int)(ic.channels[i].start + ( ic.channels[i].bank << 16 ))));
			delta[i] = ic.delta_table[(int)ic.channels[i].rate];
			lvol[i] = (int) (ic.channels[i].volume * ic.channels[i].pan);
			rvol[i] = (int) (ic.channels[i].volume * ( 8 - ic.channels[i].pan ));
			end[i] = ic.channels[i].size;
			pos[i] = ic.channels[i].pos;
			play[i] = ic.channels[i].play;
			loop[i] = ic.channels[i].loop;
			ppcm[i] = ic.channels[i].ppcm;
			ppcm_data[i] = ic.channels[i].ppcm_data;
			if ( ppcm[i] != 0 )
				delta[i] /= 2;
		}
	
			for ( j = 0; j < length; j++ ) {
	
				dataL = dataR = 0;
	
				for ( i = 0; i < 4; i++ ) {
					/* see if the voice is on */
					if ( play[i] != 0 ) {
						/* see if we're done */
						if ( ( pos[i] >> BASE_SHIFT ) >= end[i] ) {
	
							ppcm_data[i] = 0;
	
							if ( loop[i] != 0 )
								pos[i] = 0;
							else {
								play[i] = 0;
								continue;
							}
						}
	
						if ( ppcm[i] != 0 ) { /* Packed PCM */
							/* we only update the signal if we're starting or a real sound sample has gone by */
							/* this is all due to the dynamic sample rate convertion */
							if ( pos[i] == 0 || ( ( pos[i] ^ ( pos[i] - delta[i] ) ) & 0x8000 ) == 0x8000 ) {
								int newdata;
								if (( pos[i] & 0x8000 ) != 0)
									newdata = rom[i].read((int) (pos[i] >> BASE_SHIFT)) & 0x0f;
								else
									newdata = ( ( rom[i].read((int) (pos[i] >> BASE_SHIFT)) ) >> 4 ) & 0x0f;
	
								ppcm_data[i] = (int) (( ( ppcm_data[i] * 62 ) >> 6 ) + dpcmcnv[newdata]);
	
								if ( ppcm_data[i] > 127 )
									ppcm_data[i] = 127;
								else
									if ( ppcm_data[i] < -128 )
										ppcm_data[i] = -128;
							}
	
							d = ppcm_data[i];
	
							pos[i] += delta[i];
						} else { /* PCM */
							d = rom[i].read((int) (pos[i] >> BASE_SHIFT));
	
							pos[i] += delta[i];
						}
	
						if (( ic.mode & 2 ) != 0) {
							dataL += ( d * lvol[i] ) >> 2;
							dataR += ( d * rvol[i] ) >> 2;
						}
					}
				}
	
				buffer[1].write(j, limit( dataL, MAXOUT, MINOUT ));
				buffer[0].write(j, limit( dataR, MAXOUT, MINOUT ));
			}
	
		/* update the regs now */
		for ( i = 0; i < 4; i++ ) {
			ic.channels[i].pos = pos[i];
			ic.channels[i].play = play[i];
			ic.channels[i].ppcm_data = ppcm_data[i];
		}
            }
        };
	
	int K053260_sh_start(MachineSound msound) {
		String[] names = new String[2];
		String[] ch_names = new String[2];
		int i, ics;
	
		/* Initialize our chip structure */
		intf = (K053260_interface) msound.sound_interface;
	
		if ( intf.num > MAX_053260 )
			return -1;
	
/*TODO*///		K053260_chip = ( struct K053260_chip_def * )malloc( sizeof( struct K053260_chip_def ) * intf.num );
	
/*TODO*///		if ( K053260_chip == 0 )
/*TODO*///			return -1;

                for (int _i=0 ; _i<MAX_053260 ; _i++){
                    K053260_chip[_i] = new K053260_chip_def();
                    for (int _j=0 ; _j<4 ; _j++)
                        K053260_chip[_i].channels[_j] = new K053260_channel_def();
                }
                
                
		for( ics = 0; ics < intf.num; ics++ ) {
			K053260_chip_def ic = K053260_chip[ics];
	
			ic.mode = 0;
			ic.rom = new UBytePtr(memory_region(intf.region[ics]));
			ic.rom_size = memory_region_length(intf.region[ics]) - 1;
	
			K053260_reset( ics );
	
			for ( i = 0; i < 0x30; i++ )
				ic.regs[i] = 0;
	
			ic.delta_table = new long[0x1000];
	
			if ( ic.delta_table == null )
				return -1;
	
			for ( i = 0; i < 2; i++ ) {
				names[i] = ch_names[i];
				ch_names[i] = sprintf("%s #%d Ch %d",sound_name(msound),ics,i);
			}
	
			ic.channel = stream_init_multi( 2, names, intf.mixing_level[ics], Machine.sample_rate, ics, K053260_update );
	
			InitDeltaTable( ics );
	
			/* setup SH1 timer if necessary */
			if ( intf.irq[ics] != null )
				ic.timer = timer_pulse( TIME_IN_HZ( ( intf.clock[ics] / 32 ) ), 0, intf.irq[ics] );
			else
				ic.timer = null;
		}
	
	    return 0;
	}
	
	static void K053260_sh_stop() {
		int ics;
	
		if ( K053260_chip != null ) {
			for( ics = 0; ics < intf.num; ics++ ) {
				K053260_chip_def ic = K053260_chip[ics];
	
				if ( ic.delta_table != null )
					ic.delta_table = null;
	
				ic.delta_table = null;
	
				if ( ic.timer != null )
					timer_remove( ic.timer );
	
				ic.timer = null;
			}
	
			K053260_chip = null;
		}
	}
	
	public static void check_bounds( int chip, int channel ) {
		K053260_chip_def ic = K053260_chip[chip];
	
		int channel_start = (int) (( ic.channels[channel].bank << 16 ) + ic.channels[channel].start);
		int channel_end = (int) (channel_start + ic.channels[channel].size - 1);
	
		if ( channel_start > ic.rom_size ) {
			logerror("K53260: Attempting to start playing past the end of the rom ( start = %06x, end = %06x ).\n", channel_start, channel_end );
	
			ic.channels[channel].play = 0;
	
			return;
		}
	
		if ( channel_end > ic.rom_size ) {
			logerror("K53260: Attempting to play past the end of the rom ( start = %06x, end = %06x ).\n", channel_start, channel_end );
	
			ic.channels[channel].size = ic.rom_size - channel_start;
		}
//	#if LOG
		logerror("K053260: Sample Start = %06x, Sample End = %06x, Sample rate = %04lx, PPCM = %s\n", channel_start, channel_end, ic.channels[channel].rate, ic.channels[channel].ppcm != 0 ? "yes" : "no" );
//	#endif
	}
	
	public static void K053260_write( int chip, int offset, int data )
	{
		int i, t;
		int r = offset;
		int v = data;
	
		K053260_chip_def ic = K053260_chip[chip];
	
		if ( r > 0x2f ) {
			logerror("K053260: Writing past registers\n" );
			return;
		}
	
		if ( Machine.sample_rate != 0 )
			stream_update( ic.channel, 0 );
	
		/* before we update the regs, we need to check for a latched reg */
		if ( r == 0x28 ) {
			t = ic.regs[r] ^ v;
	
			for ( i = 0; i < 4; i++ ) {
				if (( t & ( 1 << i ) ) != 0) {
					if (( v & ( 1 << i ) ) != 0) {
						ic.channels[i].play = 1;
						ic.channels[i].pos = 0;
						ic.channels[i].ppcm_data = 0;
						check_bounds( chip, i );
					} else
						ic.channels[i].play = 0;
				}
			}
	
			ic.regs[r] = v;
			return;
		}
	
		/* update regs */
		ic.regs[r] = v;
	
		/* communication registers */
		if ( r < 8 )
			return;
	
		/* channel setup */
		if ( r < 0x28 ) {
			int channel = ( r - 8 ) / 8;
	
			switch ( ( r - 8 ) & 0x07 ) {
				case 0: /* sample rate low */
					ic.channels[channel].rate &= 0x0f00;
					ic.channels[channel].rate |= v;
				break;
	
				case 1: /* sample rate high */
					ic.channels[channel].rate &= 0x00ff;
					ic.channels[channel].rate |= ( v & 0x0f ) << 8;
				break;
	
				case 2: /* size low */
					ic.channels[channel].size &= 0xff00;
					ic.channels[channel].size |= v;
				break;
	
				case 3: /* size high */
					ic.channels[channel].size &= 0x00ff;
					ic.channels[channel].size |= v << 8;
				break;
	
				case 4: /* start low */
					ic.channels[channel].start &= 0xff00;
					ic.channels[channel].start |= v;
				break;
	
				case 5: /* start high */
					ic.channels[channel].start &= 0x00ff;
					ic.channels[channel].start |= v << 8;
				break;
	
				case 6: /* bank */
					ic.channels[channel].bank = v & 0xff;
				break;
	
				case 7: /* volume is 7 bits. Convert to 8 bits now. */
					ic.channels[channel].volume = ( ( v & 0x7f ) << 1 ) | ( v & 1 );
				break;
			}
	
			return;
		}
	
		switch( r ) {
			case 0x2a: /* loop, ppcm */
				for ( i = 0; i < 4; i++ )
					ic.channels[i].loop = ( v & ( 1 << i ) );
	
				for ( i = 4; i < 8; i++ )
					ic.channels[i-4].ppcm = ( v & ( 1 << i ) );
			break;
	
			case 0x2c: /* pan */
				ic.channels[0].pan = v & 7;
				ic.channels[1].pan = ( v >> 3 ) & 7;
			break;
	
			case 0x2d: /* more pan */
				ic.channels[2].pan = v & 7;
				ic.channels[3].pan = ( v >> 3 ) & 7;
			break;
	
			case 0x2f: /* control */
				ic.mode = v & 7;
				/* bit 0 = read ROM */
				/* bit 1 = enable sound output */
				/* bit 2 = unknown */
			break;
		}
	}
	
	public static int K053260_read( int chip, int offset )
	{
		K053260_chip_def ic = K053260_chip[chip];
	
		switch ( offset ) {
			case 0x29: /* channel status */
				{
					int i, status = 0;
	
					for ( i = 0; i < 4; i++ )
						status |= ic.channels[i].play << i;
	
					return status;
				}
			//break;
	
			case 0x2e: /* read rom */
				if (( ic.mode & 1 ) != 0) {
					long offs = ic.channels[0].start + ( ic.channels[0].pos >> BASE_SHIFT ) + ( ic.channels[0].bank << 16 );
	
					ic.channels[0].pos += ( 1 << 16 );
	
					if ( offs > ic.rom_size ) {
						logerror("%06x: K53260: Attempting to read past rom size in rom Read Mode (offs = %06x, size = %06x).\n",cpu_get_pc(),offs,ic.rom_size );
	
						return 0;
					}
	
					return ic.rom.read((int) offs);
				}
			break;
		}
	
		return ic.regs[offset];
	}
	
	/**************************************************************************************************/
	/* Accesors */
	
	public static ReadHandlerPtr K053260_0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return K053260_read( 0, offset );
	} };
	
	public static WriteHandlerPtr K053260_0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		K053260_write( 0, offset, data );
	} };
	
	public static ReadHandlerPtr K053260_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return K053260_read( 1, offset );
	} };
	
	public static WriteHandlerPtr K053260_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		K053260_write( 1, offset, data );
	} };
	
	public static WriteHandlerPtr K053260_0_lsb_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
/*TODO*///		if (ACCESSING_LSB)
			K053260_0_w.handler(offset, data & 0xff);
	} };
	
	public static ReadHandlerPtr K053260_0_lsb_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return K053260_0_r.handler(offset);
	} };
	
	public static WriteHandlerPtr K053260_1_lsb_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
/*TODO*///		if (ACCESSING_LSB)
			K053260_1_w.handler(offset, data & 0xff);
	} };
	
	public static ReadHandlerPtr K053260_1_lsb_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return K053260_1_r.handler(offset);
	} };
}
