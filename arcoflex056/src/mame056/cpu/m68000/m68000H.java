/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.cpu.m68000;

import static arcadeflex056.fucPtr.*;
import static mame056.cpu.m68000.m68kmame.m68k_ICount;

public class m68000H
{

	/* NOTE: M68K_SP fetches the current SP, be it USP, ISP, or MSP */
	public static final int M68K_PC         = 1;
        public static final int M68K_SP         = 2;
        public static final int M68K_ISP        = 3;
        public static final int M68K_USP        = 4;
        public static final int M68K_MSP        = 5;
        public static final int M68K_SR         = 6;
        public static final int M68K_VBR        = 7;
	public static final int M68K_SFC        = 8;
        public static final int M68K_DFC        = 9;
        public static final int M68K_CACR       = 10;
        public static final int M68K_CAAR       = 11;
        public static final int M68K_PREF_ADDR  = 12;
        public static final int M68K_PREF_DATA  = 13;
	public static final int M68K_D0         = 14;
        public static final int M68K_D1         = 15;
        public static final int M68K_D2         = 16;
        public static final int M68K_D3         = 17;
        public static final int M68K_D4         = 18;
        public static final int M68K_D5         = 20;
        public static final int M68K_D6         = 21;
        public static final int M68K_D7         = 22;
	public static final int M68K_A0         = 23;
        public static final int M68K_A1         = 24;
        public static final int M68K_A2         = 25;
        public static final int M68K_A3         = 26;
        public static final int M68K_A4         = 27;
        public static final int M68K_A5         = 28;
        public static final int M68K_A6         = 29;
        public static final int M68K_A7         = 30;
	
	
	/* Redirect memory calls */
	
	public static class m68k_memory_interface
	{
		public int                      opcode_xor;			// Address Calculation
		public ReadHandlerPtr		read8;				// Normal read 8 bit
		public ReadHandlerPtr16         read16;				// Normal read 16 bit
		public ReadHandlerPtr32         read32;				// Normal read 32 bit
		public WriteHandlerPtr		write8;                         // Write 8 bit
		public WriteHandlerPtr16	write16;                        // Write 16 bit
		public WriteHandlerPtr32	write32;                        // Write 32 bit
		public ChangePcHandlerPtr	changepc;			// Change PC routine
	
	    // For Encrypted Stuff
	
		public ReadHandlerPtr		read8pc;			// PC Relative read 8 bit
		public ReadHandlerPtr16         read16pc;			// PC Relative read 16 bit
		public ReadHandlerPtr32         read32pc;			// PC Relative read 32 bit
	
		public ReadHandlerPtr16         read16d;			// Direct read 16 bit
		public ReadHandlerPtr32         read32d;			// Direct read 32 bit
	};
	
	public static class m68k_encryption_interface
	{
		public ReadHandlerPtr		read8pc;			// PC Relative read 8 bit
		public ReadHandlerPtr16         read16pc;			// PC Relative read 16 bit
		public ReadHandlerPtr32         read32pc;			// PC Relative read 32 bit
	
		public ReadHandlerPtr16         read16d;			// Direct read 16 bit
		public ReadHandlerPtr32         read32d;			// Direct read 32 bit
	};
	
	/* The MAME API for MC68000 */
	
	public static final int MC68000_IRQ_1    = 1;
	public static final int MC68000_IRQ_2    = 2;
	public static final int MC68000_IRQ_3    = 3;
	public static final int MC68000_IRQ_4    = 4;
	public static final int MC68000_IRQ_5    = 5;
	public static final int MC68000_IRQ_6    = 6;
	public static final int MC68000_IRQ_7    = 7;
	
	public static final int MC68000_INT_ACK_AUTOVECTOR    = -1;
	public static final int MC68000_INT_ACK_SPURIOUS      = -2;
	
	public static int[] m68000_ICount                     = m68k_ICount;
/*TODO*///	extern unsigned m68000_get_context(void *dst);
/*TODO*///	extern unsigned m68000_get_reg(int regnum);
/*TODO*///	extern const char *m68000_info(void *context, int regnum);
/*TODO*///	extern unsigned m68000_dasm(char *buffer, unsigned pc);
	
	/****************************************************************************
	 * M68010 section
	 ****************************************************************************/
/*TODO*///	#if HAS_M68010
/*TODO*///	#define MC68010_IRQ_1					MC68000_IRQ_1
/*TODO*///	#define MC68010_IRQ_2					MC68000_IRQ_2
/*TODO*///	#define MC68010_IRQ_3					MC68000_IRQ_3
/*TODO*///	#define MC68010_IRQ_4					MC68000_IRQ_4
/*TODO*///	#define MC68010_IRQ_5					MC68000_IRQ_5
/*TODO*///	#define MC68010_IRQ_6					MC68000_IRQ_6
/*TODO*///	#define MC68010_IRQ_7					MC68000_IRQ_7
/*TODO*///	#define MC68010_INT_ACK_AUTOVECTOR		MC68000_INT_ACK_AUTOVECTOR
/*TODO*///	#define MC68010_INT_ACK_SPURIOUS		MC68000_INT_ACK_SPURIOUS
/*TODO*///	
/*TODO*///	#define m68010_ICount                   m68k_ICount
/*TODO*///	extern unsigned m68010_get_context(void *dst);
/*TODO*///	extern unsigned m68010_get_reg(int regnum);
/*TODO*///	const char *m68010_info(void *context, int regnum);
/*TODO*///	extern unsigned m68010_dasm(char *buffer, unsigned pc);
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * M68EC020 section
/*TODO*///	 ****************************************************************************/
/*TODO*///	#if HAS_M68EC020
/*TODO*///	#define MC68EC020_IRQ_1					MC68000_IRQ_1
/*TODO*///	#define MC68EC020_IRQ_2					MC68000_IRQ_2
/*TODO*///	#define MC68EC020_IRQ_3					MC68000_IRQ_3
/*TODO*///	#define MC68EC020_IRQ_4					MC68000_IRQ_4
/*TODO*///	#define MC68EC020_IRQ_5					MC68000_IRQ_5
/*TODO*///	#define MC68EC020_IRQ_6					MC68000_IRQ_6
/*TODO*///	#define MC68EC020_IRQ_7					MC68000_IRQ_7
/*TODO*///	#define MC68EC020_INT_ACK_AUTOVECTOR	MC68000_INT_ACK_AUTOVECTOR
/*TODO*///	#define MC68EC020_INT_ACK_SPURIOUS		MC68000_INT_ACK_SPURIOUS
/*TODO*///	
/*TODO*///	#define m68ec020_ICount                 m68k_ICount
/*TODO*///	extern unsigned m68ec020_get_context(void *dst);
/*TODO*///	extern unsigned m68ec020_get_reg(int regnum);
/*TODO*///	const char *m68ec020_info(void *context, int regnum);
/*TODO*///	extern unsigned m68ec020_dasm(char *buffer, unsigned pc);
/*TODO*///	#endif
	
	/****************************************************************************
	 * M68020 section
	 ****************************************************************************/
/*TODO*///	#if HAS_M68020
/*TODO*///	#define MC68020_IRQ_1					MC68000_IRQ_1
/*TODO*///	#define MC68020_IRQ_2					MC68000_IRQ_2
/*TODO*///	#define MC68020_IRQ_3					MC68000_IRQ_3
/*TODO*///	#define MC68020_IRQ_4					MC68000_IRQ_4
/*TODO*///	#define MC68020_IRQ_5					MC68000_IRQ_5
/*TODO*///	#define MC68020_IRQ_6					MC68000_IRQ_6
/*TODO*///	#define MC68020_IRQ_7					MC68000_IRQ_7
/*TODO*///	#define MC68020_INT_ACK_AUTOVECTOR		MC68000_INT_ACK_AUTOVECTOR
/*TODO*///	#define MC68020_INT_ACK_SPURIOUS		MC68000_INT_ACK_SPURIOUS
/*TODO*///	
/*TODO*///	#define m68020_ICount                   m68k_ICount
/*TODO*///	extern unsigned m68020_get_context(void *dst);
/*TODO*///	extern unsigned m68020_get_reg(int regnum);
/*TODO*///	const char *m68020_info(void *context, int regnum);
/*TODO*///	extern unsigned m68020_dasm(char *buffer, unsigned pc);
/*TODO*///	#endif
	
	// C Core header
	

}

