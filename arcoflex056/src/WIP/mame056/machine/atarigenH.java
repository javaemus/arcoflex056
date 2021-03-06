/*##########################################################################

	atarigen.h

	General functions for Atari raster games.

##########################################################################*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.machine;

public class atarigenH
{
/*TODO*///	
/*TODO*///	#ifndef __MACHINE_ATARIGEN__
/*TODO*///	#define __MACHINE_ATARIGEN__
	
	
	/*##########################################################################
		CONSTANTS
	##########################################################################*/
	
        public static int ATARI_CLOCK_14MHz	= 14318180;
	public static int ATARI_CLOCK_20MHz	= 20000000;
/*TODO*///	#define ATARI_CLOCK_32MHz	32000000
/*TODO*///	#define ATARI_CLOCK_50MHz	50000000
	
	
	
	/*##########################################################################
		TYPES & STRUCTURES
	##########################################################################*/
	
        public static abstract interface atarigen_int_callback {
            public abstract void handler();
        }

        public static abstract interface atarigen_scanline_callback {
            public abstract void handler(int scanline);
        }
/*TODO*///	struct atarivc_state_desc
/*TODO*///	{
/*TODO*///		int latch1;								/* latch #1 value (-1 means disabled) */
/*TODO*///		int latch2;								/* latch #2 value (-1 means disabled) */
/*TODO*///		int rowscroll_enable;					/* true if row-scrolling is enabled */
/*TODO*///		int palette_bank;						/* which palette bank is enabled */
/*TODO*///		int pf0_xscroll;						/* playfield 1 xscroll */
/*TODO*///		int pf0_xscroll_raw;					/* playfield 1 xscroll raw value */
/*TODO*///		int pf0_yscroll;						/* playfield 1 yscroll */
/*TODO*///		int pf1_xscroll;						/* playfield 2 xscroll */
/*TODO*///		int pf1_xscroll_raw;					/* playfield 2 xscroll raw value */
/*TODO*///		int pf1_yscroll;						/* playfield 2 yscroll */
/*TODO*///		int mo_xscroll;							/* sprite xscroll */
/*TODO*///		int mo_yscroll;							/* sprite xscroll */
/*TODO*///	};
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*##########################################################################
/*TODO*///		GLOBALS
/*TODO*///	##########################################################################*/
/*TODO*///	
/*TODO*///	
/*TODO*///	extern const data16_t *	atarigen_eeprom_default;
/*TODO*///	extern data16_t *		atarigen_eeprom;
/*TODO*///	extern size_t 			atarigen_eeprom_size;
/*TODO*///	
/*TODO*///	
/*TODO*///	extern data16_t *		atarivc_data;
/*TODO*///	extern data16_t *		atarivc_eof_data;
/*TODO*///	extern struct atarivc_state_desc atarivc_state;
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*##########################################################################
/*TODO*///		FUNCTION PROTOTYPES
/*TODO*///	##########################################################################*/
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		INTERRUPT HANDLING
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarigen_interrupt_reset(atarigen_int_callback update_int);
/*TODO*///	
/*TODO*///	void atarigen_scanline_int_set(int scanline);
/*TODO*///	WRITE16_HANDLER( atarigen_scanline_int_ack_w );
/*TODO*///	WRITE32_HANDLER( atarigen_scanline_int_ack32_w );
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarigen_sound_int_ack_w );
/*TODO*///	WRITE32_HANDLER( atarigen_sound_int_ack32_w );
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarigen_video_int_ack_w );
/*TODO*///	WRITE32_HANDLER( atarigen_video_int_ack32_w );
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		EEPROM HANDLING
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarigen_eeprom_enable_w );
/*TODO*///	WRITE16_HANDLER( atarigen_eeprom_w );
/*TODO*///	READ16_HANDLER( atarigen_eeprom_r );
/*TODO*///	READ16_HANDLER( atarigen_eeprom_upper_r );
/*TODO*///	
/*TODO*///	WRITE32_HANDLER( atarigen_eeprom_enable32_w );
/*TODO*///	WRITE32_HANDLER( atarigen_eeprom32_w );
/*TODO*///	READ32_HANDLER( atarigen_eeprom_upper32_r );
/*TODO*///	
/*TODO*///	void atarigen_nvram_handler(void *file,int read_or_write);
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		SLAPSTIC HANDLING
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarigen_slapstic_init(int cpunum, int base, int chipnum);
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarigen_slapstic_w );
/*TODO*///	READ16_HANDLER( atarigen_slapstic_r );
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		SOUND I/O
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarigen_sound_io_reset(int cpu_num);
/*TODO*///	
/*TODO*///	
/*TODO*///	void atarigen_ym2151_irq_gen(int irq);
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarigen_sound_w );
/*TODO*///	READ16_HANDLER( atarigen_sound_r );
/*TODO*///	WRITE16_HANDLER( atarigen_sound_upper_w );
/*TODO*///	READ16_HANDLER( atarigen_sound_upper_r );
/*TODO*///	
/*TODO*///	WRITE32_HANDLER( atarigen_sound_upper32_w );
/*TODO*///	READ32_HANDLER( atarigen_sound_upper32_r );
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarigen_sound_reset_w );
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		SOUND HELPERS
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarigen_init_6502_speedup(int cpunum, int compare_pc1, int compare_pc2);
/*TODO*///	void atarigen_set_ym2151_vol(int volume);
/*TODO*///	void atarigen_set_ym2413_vol(int volume);
/*TODO*///	void atarigen_set_pokey_vol(int volume);
/*TODO*///	void atarigen_set_tms5220_vol(int volume);
/*TODO*///	void atarigen_set_oki6295_vol(int volume);
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		VIDEO CONTROLLER
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarivc_reset(data16_t *eof_data);
/*TODO*///	void atarivc_update(const data16_t *data);
/*TODO*///	
/*TODO*///	WRITE16_HANDLER( atarivc_w );
/*TODO*///	READ16_HANDLER( atarivc_r );
/*TODO*///	
/*TODO*///	INLINE void atarivc_update_pf_xscrolls(void)
/*TODO*///	{
/*TODO*///		atarivc_state.pf0_xscroll = atarivc_state.pf0_xscroll_raw + ((atarivc_state.pf1_xscroll_raw) & 7);
/*TODO*///		atarivc_state.pf1_xscroll = atarivc_state.pf1_xscroll_raw + 4;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		VIDEO HELPERS
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarigen_scanline_timer_reset(atarigen_scanline_callback update_graphics, int frequency);
/*TODO*///	WRITE16_HANDLER( atarigen_halt_until_hblank_0_w );
/*TODO*///	WRITE16_HANDLER( atarigen_666_paletteram_w );
/*TODO*///	WRITE16_HANDLER( atarigen_expanded_666_paletteram_w );
/*TODO*///	WRITE32_HANDLER( atarigen_666_paletteram32_w );
/*TODO*///	
/*TODO*///	
/*TODO*///	/*---------------------------------------------------------------
/*TODO*///		MISC HELPERS
/*TODO*///	---------------------------------------------------------------*/
/*TODO*///	
/*TODO*///	void atarigen_invert_region(int region);
/*TODO*///	void atarigen_swap_mem(void *ptr1, void *ptr2, int bytes);
/*TODO*///	
/*TODO*///	#endif
}
