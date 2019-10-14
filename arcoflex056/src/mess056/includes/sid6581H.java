/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.includes;

import static mame056.sndintrfH.*;

public class sid6581H
{
	
	/* 
	   c64 / c128 sound interface
	   cbmb series interface
	   c16 sound card
	   c64 hardware modification for second sid
	   c65 has 2 inside
	
	   selection between two type 6581 8580 would be nice 
	
	   sid6582 6581 with other input voltages 
	*/
	public static int MAX_SID6581 = 2;
	
	public static int MOS6581 = 0;
        public static int MOS8580 = 1; // SIDTYPE;
        
        public static abstract interface SID_ad_readPtr {

            public abstract int handler(int channel);
        }
        
        public static class _chips {
            /* bypassed to mixer_allocate_channel, so use
               the macros defined in src/sound/mixer.h to load values*/
            public int default_mixer_level;
            public int type;
            public int clock;
            public SID_ad_readPtr ad_read;
        }
	
        public static class SID6581_interface {
		/* this is here, until this sound approximation is added to
		   mame's sound devices */
		public CustomSound_interface custom;
	
		public int count;
		public _chips[] chips = new _chips[MAX_SID6581];
	};
	
}
