/*
	Interface routine for 68kem <-> Mame
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.cpu.m68000;

public class asmintf
{
	
/*TODO*///	struct m68k_memory_interface a68k_memory_intf;
/*TODO*///	
/*TODO*///	// If we are only using assembler cores, we need to define these
/*TODO*///	// otherwise they are declared by the C core.
/*TODO*///	
/*TODO*///	#ifdef A68K0
/*TODO*///	#ifdef A68K2
/*TODO*///	public static int[] m68k_ICount = new int[1];
/*TODO*///	struct m68k_memory_interface m68k_memory_intf;
/*TODO*///	#endif
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	enum
/*TODO*///	{
/*TODO*///		M68K_CPU_TYPE_INVALID,
/*TODO*///		M68K_CPU_TYPE_68000,
/*TODO*///		M68K_CPU_TYPE_68010,
/*TODO*///		M68K_CPU_TYPE_68EC020,
/*TODO*///		M68K_CPU_TYPE_68020,
/*TODO*///		M68K_CPU_TYPE_68030,	/* Supported by disassembler ONLY */
/*TODO*///		M68K_CPU_TYPE_68040		/* Supported by disassembler ONLY */
/*TODO*///	};
/*TODO*///	
/*TODO*///	#define A68K_SET_PC_CALLBACK(A)     (*a68k_memory_intf.changepc)(A)
/*TODO*///	
/*TODO*///	int illegal_op = 0 ;
/*TODO*///	int illegal_pc = 0 ;
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	void m68k_illegal_opcode(void)
/*TODO*///	{
/*TODO*///		logerror("Illegal Opcode %4x at %8x\n",illegal_op,illegal_pc);
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	unsigned int m68k_disassemble(char* str_buff, unsigned int pc, unsigned int cpu_type);
/*TODO*///	
/*TODO*///	#ifdef WIN32
/*TODO*///	#define CONVENTION __cdecl
/*TODO*///	#else
/*TODO*///	#define CONVENTION
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	/* Use the x86 assembly core */
/*TODO*///	typedef struct
/*TODO*///	{
/*TODO*///	    UINT32 d[8];             /* 0x0004 8 Data registers */
/*TODO*///	    UINT32 a[8];             /* 0x0024 8 Address registers */
/*TODO*///	
/*TODO*///	    UINT32 isp;              /* 0x0048 */
/*TODO*///	
/*TODO*///	    UINT32 sr_high;          /* 0x004C System registers */
/*TODO*///	    UINT32 ccr;              /* 0x0050 CCR in Intel Format */
/*TODO*///	    UINT32 x_carry;          /* 0x0054 Extended Carry */
/*TODO*///	
/*TODO*///	    UINT32 pc;               /* 0x0058 Program Counter */
/*TODO*///	
/*TODO*///	    UINT32 IRQ_level;        /* 0x005C IRQ level you want the MC68K process (0=None)  */
/*TODO*///	
/*TODO*///	    /* Backward compatible with C emulator - Only set in Debug compile */
/*TODO*///	
/*TODO*///	    UINT16 sr;
/*TODO*///	    UINT16 filler;
/*TODO*///	
/*TODO*///	    int (*irq_callback)(int irqline);
/*TODO*///	
/*TODO*///	    UINT32 previous_pc;      /* last PC used */
/*TODO*///	
/*TODO*///	    int (*reset_callback)(void);
/*TODO*///	
/*TODO*///	    UINT32 sfc;              /* Source Function Code. (68010) */
/*TODO*///	    UINT32 dfc;              /* Destination Function Code. (68010) */
/*TODO*///	    UINT32 usp;              /* User Stack (All) */
/*TODO*///	    UINT32 vbr;              /* Vector Base Register. (68010) */
/*TODO*///	
/*TODO*///	    UINT32 BankID;			 /* Memory bank in use */
/*TODO*///	    UINT32 CPUtype;		  	 /* CPU Type 0=68000,1=68010,2=68020 */
/*TODO*///		UINT32 FullPC;
/*TODO*///	
/*TODO*///		struct m68k_memory_interface Memory_Interface;
/*TODO*///	
/*TODO*///	} a68k_cpu_context;
/*TODO*///	
/*TODO*///	
/*TODO*///	static UINT8 M68K_layout[] = {
/*TODO*///		M68K_PC, M68K_ISP, -1,
/*TODO*///		M68K_SR, M68K_USP, -1,
/*TODO*///		M68K_D0, M68K_A0, -1,
/*TODO*///		M68K_D1, M68K_A1, -1,
/*TODO*///		M68K_D2, M68K_A2, -1,
/*TODO*///		M68K_D3, M68K_A3, -1,
/*TODO*///		M68K_D4, M68K_A4, -1,
/*TODO*///		M68K_D5, M68K_A5, -1,
/*TODO*///		M68K_D6, M68K_A6, -1,
/*TODO*///		M68K_D7, M68K_A7, 0
/*TODO*///	};
/*TODO*///	
/*TODO*///	static UINT8 m68k_win_layout[] = {
/*TODO*///		48, 0,32,13,	/* register window (top right) */
/*TODO*///		 0, 0,47,13,	/* disassembler window (top left) */
/*TODO*///		 0,14,47, 8,	/* memory #1 window (left, middle) */
/*TODO*///		48,14,32, 8,	/* memory #2 window (right, middle) */
/*TODO*///		 0,23,80, 1 	/* command line window (bottom rows) */
/*TODO*///	};
/*TODO*///	
/*TODO*///	#ifdef A68K0
/*TODO*///	extern a68k_cpu_context M68000_regs;
/*TODO*///	
/*TODO*///	
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#ifdef A68K2
/*TODO*///	extern a68k_cpu_context M68020_regs;
/*TODO*///	
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	/***************************************************************************/
/*TODO*///	/* Save State stuff                                                        */
/*TODO*///	/***************************************************************************/
/*TODO*///	
/*TODO*///	static int IntelFlag[32] = {
/*TODO*///		0x0000,0x0001,0x0800,0x0801,0x0040,0x0041,0x0840,0x0841,
/*TODO*///	    0x0080,0x0081,0x0880,0x0881,0x00C0,0x00C1,0x08C0,0x08C1,
/*TODO*///	    0x0100,0x0101,0x0900,0x0901,0x0140,0x0141,0x0940,0x0941,
/*TODO*///	    0x0180,0x0181,0x0980,0x0981,0x01C0,0x01C1,0x09C0,0x09C1
/*TODO*///	};
/*TODO*///	
/*TODO*///	
/*TODO*///	// The assembler engine only keeps flags in intel format, so ...
/*TODO*///	
/*TODO*///	static UINT32 zero = 0;
/*TODO*///	static int stopped = 0;
/*TODO*///	
/*TODO*///	static void a68k_prepare_substate(void)
/*TODO*///	{
/*TODO*///		stopped = ((M68000_regs.IRQ_level & 0x80) != 0);
/*TODO*///	
/*TODO*///		M68000_regs.sr = ((M68000_regs.ccr >> 4) & 0x1C)
/*TODO*///	                   | (M68000_regs.ccr & 0x01)
/*TODO*///	                   | ((M68000_regs.ccr >> 10) & 0x02)
/*TODO*///	                   | (M68000_regs.sr_high << 8);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void a68k_post_load(void)
/*TODO*///	{
/*TODO*///		int intel = M68000_regs.sr & 0x1f;
/*TODO*///	
/*TODO*///	    M68000_regs.sr_high = M68000_regs.sr >> 8;
/*TODO*///	    M68000_regs.x_carry = (IntelFlag[intel] >> 8) & 0x01;
/*TODO*///	    M68000_regs.ccr     = IntelFlag[intel] & 0x0EFF;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void a68k_state_register(const char *type)
/*TODO*///	{
/*TODO*///		int cpu = cpu_getactivecpu();
/*TODO*///	
/*TODO*///		state_save_register_UINT32(type, cpu, "D"         , &M68000_regs.d[0], 8);
/*TODO*///		state_save_register_UINT32(type, cpu, "A"         , &M68000_regs.a[0], 8);
/*TODO*///		state_save_register_UINT32(type, cpu, "PPC"       , &M68000_regs.previous_pc, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "PC"        , &M68000_regs.pc, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "USP"       , &M68000_regs.usp, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "ISP"       , &M68000_regs.isp, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "MSP"       , &zero, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "VBR"       , &M68000_regs.vbr, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "SFC"       , &M68000_regs.sfc, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "DFC"       , &M68000_regs.dfc, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "CACR"      , &zero, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "CAAR"      , &zero, 1);
/*TODO*///		state_save_register_UINT16(type, cpu, "SR"        , &M68000_regs.sr, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "INT_LEVEL" , &M68000_regs.IRQ_level, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "INT_CYCLES", (UINT32 *)&m68k_ICount, 1);
/*TODO*///		state_save_register_int   (type, cpu, "STOPPED"   , &stopped);
/*TODO*///		state_save_register_int   (type, cpu, "HALTED"    , (int *)&zero);
/*TODO*///		state_save_register_UINT32(type, cpu, "PREF_ADDR" , &zero, 1);
/*TODO*///		state_save_register_UINT32(type, cpu, "PREF_DATA" , &zero, 1);
/*TODO*///	  	state_save_register_func_presave(a68k_prepare_substate);
/*TODO*///	  	state_save_register_func_postload(a68k_post_load);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * 24-bit address, 16-bit data memory interface
/*TODO*///	 ****************************************************************************/
/*TODO*///	
/*TODO*///	#ifdef A68K0
/*TODO*///	
/*TODO*///	static data32_t readlong_a24_d16(offs_t address)
/*TODO*///	{
/*TODO*///		data32_t result = cpu_readmem24bew_word(address) << 16;
/*TODO*///		return result | cpu_readmem24bew_word(address + 2);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void writelong_a24_d16(offs_t address, data32_t data)
/*TODO*///	{
/*TODO*///		cpu_writemem24bew_word(address, data >> 16);
/*TODO*///		cpu_writemem24bew_word(address + 2, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void changepc_a24_d16(offs_t pc)
/*TODO*///	{
/*TODO*///		change_pc24bew(pc);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* interface for 24-bit address bus, 16-bit data bus (68000, 68010) */
/*TODO*///	static const struct m68k_memory_interface interface_a24_d16 =
/*TODO*///	{
/*TODO*///		0,
/*TODO*///		cpu_readmem24bew,
/*TODO*///		cpu_readmem24bew_word,
/*TODO*///		readlong_a24_d16,
/*TODO*///		cpu_writemem24bew,
/*TODO*///		cpu_writemem24bew_word,
/*TODO*///		writelong_a24_d16,
/*TODO*///		changepc_a24_d16,
/*TODO*///		cpu_readmem24bew,				// Encrypted Versions
/*TODO*///		cpu_readmem24bew_word,
/*TODO*///		readlong_a24_d16,
/*TODO*///		cpu_readmem24bew_word,
/*TODO*///		readlong_a24_d16
/*TODO*///	};
/*TODO*///	
/*TODO*///	#endif // A68k0
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * 24-bit address, 32-bit data memory interface
/*TODO*///	 ****************************************************************************/
/*TODO*///	
/*TODO*///	#ifdef A68K2
/*TODO*///	
/*TODO*///	/* potentially misaligned 16-bit reads with a 32-bit data bus (and 24-bit address bus) */
/*TODO*///	static data16_t readword_a24_d32(offs_t address)
/*TODO*///	{
/*TODO*///		data16_t result;
/*TODO*///	
/*TODO*///		if (!(address & 1))
/*TODO*///			return cpu_readmem24bedw_word(address);
/*TODO*///		result = cpu_readmem24bedw(address) << 8;
/*TODO*///		return result | cpu_readmem24bedw(address + 1);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* potentially misaligned 16-bit writes with a 32-bit data bus (and 24-bit address bus) */
/*TODO*///	static void writeword_a24_d32(offs_t address, data16_t data)
/*TODO*///	{
/*TODO*///		if (!(address & 1))
/*TODO*///		{
/*TODO*///			cpu_writemem24bedw_word(address, data);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		cpu_writemem24bedw(address, data >> 8);
/*TODO*///		cpu_writemem24bedw(address + 1, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* potentially misaligned 32-bit reads with a 32-bit data bus (and 24-bit address bus) */
/*TODO*///	static data32_t readlong_a24_d32(offs_t address)
/*TODO*///	{
/*TODO*///		data32_t result;
/*TODO*///	
/*TODO*///		if (!(address & 3))
/*TODO*///			return cpu_readmem24bedw_dword(address);
/*TODO*///		else if (!(address & 1))
/*TODO*///		{
/*TODO*///			result = cpu_readmem24bedw_word(address) << 16;
/*TODO*///			return result | cpu_readmem24bedw_word(address + 2);
/*TODO*///		}
/*TODO*///		result = cpu_readmem24bedw(address) << 24;
/*TODO*///		result |= cpu_readmem24bedw_word(address + 1) << 8;
/*TODO*///		return result | cpu_readmem24bedw(address + 3);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* potentially misaligned 32-bit writes with a 32-bit data bus (and 24-bit address bus) */
/*TODO*///	static void writelong_a24_d32(offs_t address, data32_t data)
/*TODO*///	{
/*TODO*///		if (!(address & 3))
/*TODO*///		{
/*TODO*///			cpu_writemem24bedw_dword(address, data);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		else if (!(address & 1))
/*TODO*///		{
/*TODO*///			cpu_writemem24bedw_word(address, data >> 16);
/*TODO*///			cpu_writemem24bedw_word(address + 2, data);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		cpu_writemem24bedw(address, data >> 24);
/*TODO*///		cpu_writemem24bedw_word(address + 1, data >> 8);
/*TODO*///		cpu_writemem24bedw(address + 3, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void changepc_a24_d32(offs_t pc)
/*TODO*///	{
/*TODO*///		change_pc24bedw(pc);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* interface for 24-bit address bus, 32-bit data bus (68EC020) */
/*TODO*///	static const struct m68k_memory_interface interface_a24_d32 =
/*TODO*///	{
/*TODO*///		WORD_XOR_BE(0),
/*TODO*///		cpu_readmem24bedw,
/*TODO*///		readword_a24_d32,
/*TODO*///		readlong_a24_d32,
/*TODO*///		cpu_writemem24bedw,
/*TODO*///		writeword_a24_d32,
/*TODO*///		writelong_a24_d32,
/*TODO*///		changepc_a24_d32,
/*TODO*///		cpu_readmem24bedw,
/*TODO*///		readword_a24_d32,
/*TODO*///		readlong_a24_d32,
/*TODO*///		readword_a24_d32,
/*TODO*///		readlong_a24_d32
/*TODO*///	};
/*TODO*///	
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * 32-bit address, 32-bit data memory interface
/*TODO*///	 ****************************************************************************/
/*TODO*///	
/*TODO*///	/* potentially misaligned 16-bit reads with a 32-bit data bus (and 32-bit address bus) */
/*TODO*///	static data16_t readword_a32_d32(offs_t address)
/*TODO*///	{
/*TODO*///		data16_t result;
/*TODO*///	
/*TODO*///		if (!(address & 1))
/*TODO*///			return cpu_readmem32bedw_word(address);
/*TODO*///		result = cpu_readmem32bedw(address) << 8;
/*TODO*///		return result | cpu_readmem32bedw(address + 1);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* potentially misaligned 16-bit writes with a 32-bit data bus (and 32-bit address bus) */
/*TODO*///	static void writeword_a32_d32(offs_t address, data16_t data)
/*TODO*///	{
/*TODO*///		if (!(address & 1))
/*TODO*///		{
/*TODO*///			cpu_writemem32bedw_word(address, data);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		cpu_writemem32bedw(address, data >> 8);
/*TODO*///		cpu_writemem32bedw(address + 1, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* potentially misaligned 32-bit reads with a 32-bit data bus (and 32-bit address bus) */
/*TODO*///	static data32_t readlong_a32_d32(offs_t address)
/*TODO*///	{
/*TODO*///		data32_t result;
/*TODO*///	
/*TODO*///		if (!(address & 3))
/*TODO*///			return cpu_readmem32bedw_dword(address);
/*TODO*///		else if (!(address & 1))
/*TODO*///		{
/*TODO*///			result = cpu_readmem32bedw_word(address) << 16;
/*TODO*///			return result | cpu_readmem32bedw_word(address + 2);
/*TODO*///		}
/*TODO*///		result = cpu_readmem32bedw(address) << 24;
/*TODO*///		result |= cpu_readmem32bedw_word(address + 1) << 8;
/*TODO*///		return result | cpu_readmem32bedw(address + 3);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* potentially misaligned 32-bit writes with a 32-bit data bus (and 32-bit address bus) */
/*TODO*///	static void writelong_a32_d32(offs_t address, data32_t data)
/*TODO*///	{
/*TODO*///		if (!(address & 3))
/*TODO*///		{
/*TODO*///			cpu_writemem32bedw_dword(address, data);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		else if (!(address & 1))
/*TODO*///		{
/*TODO*///			cpu_writemem32bedw_word(address,     data >> 16);
/*TODO*///			cpu_writemem32bedw_word(address + 2, data);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		cpu_writemem32bedw(address, data >> 24);
/*TODO*///		cpu_writemem32bedw_word(address + 1, data >> 8);
/*TODO*///		cpu_writemem32bedw(address + 3, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void changepc_a32_d32(offs_t pc)
/*TODO*///	{
/*TODO*///		change_pc32bedw(pc);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* interface for 24-bit address bus, 32-bit data bus (68020) */
/*TODO*///	static const struct m68k_memory_interface interface_a32_d32 =
/*TODO*///	{
/*TODO*///		WORD_XOR_BE(0),
/*TODO*///		cpu_readmem32bedw,
/*TODO*///		readword_a32_d32,
/*TODO*///		readlong_a32_d32,
/*TODO*///		cpu_writemem32bedw,
/*TODO*///		writeword_a32_d32,
/*TODO*///		writelong_a32_d32,
/*TODO*///		changepc_a32_d32,
/*TODO*///		cpu_readmem32bedw,
/*TODO*///		readword_a32_d32,
/*TODO*///		readlong_a32_d32,
/*TODO*///		readword_a32_d32,
/*TODO*///		readlong_a32_d32
/*TODO*///	};
/*TODO*///	
/*TODO*///	#endif // A68K2
/*TODO*///	
/*TODO*///	/********************************************/
/*TODO*///	/* Interface routines to link Mame -> 68KEM */
/*TODO*///	/********************************************/
/*TODO*///	
/*TODO*///	#define READOP(a)	(cpu_readop16((a) ^ a68k_memory_intf.opcode_xor))
/*TODO*///	
/*TODO*///	#ifdef A68K0
/*TODO*///	
/*TODO*///	void m68000_init(void)
/*TODO*///	{
/*TODO*///		a68k_state_register("m68000");
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void m68k16_reset_common(void)
/*TODO*///	{
/*TODO*///		memset(&M68000_regs,0,sizeof(M68000_regs));
/*TODO*///	
/*TODO*///	    M68000_regs.a[7] = M68000_regs.isp = (( READOP(0) << 16 ) | READOP(2));
/*TODO*///	    M68000_regs.pc   = (( READOP(4) << 16 ) | READOP(6)) & 0xffffff;
/*TODO*///	    M68000_regs.sr_high = 0x27;
/*TODO*///	
/*TODO*///		#ifdef MAME_DEBUG
/*TODO*///			M68000_regs.sr = 0x2700;
/*TODO*///		#endif
/*TODO*///	
/*TODO*///	    M68000_RESET();
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_reset(void *param)
/*TODO*///	{
/*TODO*///		struct m68k_encryption_interface *interface = param;
/*TODO*///		
/*TODO*///	    // Default Memory Routines
/*TODO*///		if (a68k_memory_intf.read8 != cpu_readmem24bew)
/*TODO*///			a68k_memory_intf = interface_a24_d16;
/*TODO*///		
/*TODO*///		// Import encryption routines if present
/*TODO*///		if (param)
/*TODO*///		{
/*TODO*///			a68k_memory_intf.read8pc = interface->read8pc;
/*TODO*///			a68k_memory_intf.read16pc = interface->read16pc;
/*TODO*///			a68k_memory_intf.read32pc = interface->read32pc;
/*TODO*///			a68k_memory_intf.read16d = interface->read16d;
/*TODO*///			a68k_memory_intf.read32d = interface->read32d;
/*TODO*///		}
/*TODO*///	
/*TODO*///		m68k16_reset_common();
/*TODO*///	    M68000_regs.Memory_Interface = a68k_memory_intf;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_exit(void)
/*TODO*///	{
/*TODO*///		/* nothing to do ? */
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	#ifdef TRACE68K 							/* Trace */
/*TODO*///		static int skiptrace=0;
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	int m68000_execute(int cycles)
/*TODO*///	{
/*TODO*///		if (M68000_regs.IRQ_level == 0x80) return cycles;		/* STOP with no IRQs */
/*TODO*///	
/*TODO*///		m68k_ICount = cycles;
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	    do
/*TODO*///	    {
/*TODO*///			if (mame_debug)
/*TODO*///	        {
/*TODO*///				#ifdef TRACE68K
/*TODO*///	
/*TODO*///				int StartCycle = m68k_ICount;
/*TODO*///	
/*TODO*///	            skiptrace++;
/*TODO*///	
/*TODO*///	            if (skiptrace > 0)
/*TODO*///	            {
/*TODO*///				    int mycount, areg, dreg;
/*TODO*///	
/*TODO*///	                areg = dreg = 0;
/*TODO*///		            for (mycount=7;mycount>=0;mycount--)
/*TODO*///	                {
/*TODO*///	            	    areg = areg + M68000_regs.a[mycount];
/*TODO*///	                    dreg = dreg + M68000_regs.d[mycount];
/*TODO*///	                }
/*TODO*///	
/*TODO*///	           	    logerror("=> %8x %8x ",areg,dreg);
/*TODO*///				    logerror("%6x %4x %d\n",M68000_regs.pc,M68000_regs.sr & 0x271F,m68k_ICount);
/*TODO*///	            }
/*TODO*///	            #endif
/*TODO*///	
/*TODO*///	//	        m68k_memory_intf = a68k_memory_intf;
/*TODO*///				MAME_Debug();
/*TODO*///	            M68000_RUN();
/*TODO*///	
/*TODO*///	            #ifdef TRACE68K
/*TODO*///	            if ((M68000_regs.IRQ_level & 0x80) || (cpu_getstatus(cpu_getactivecpu()) == 0))
/*TODO*///	    			m68k_ICount = 0;
/*TODO*///	            else
/*TODO*///					m68k_ICount = StartCycle - 12;
/*TODO*///	            #endif
/*TODO*///	        }
/*TODO*///	        else
/*TODO*///				M68000_RUN();
/*TODO*///	
/*TODO*///	    } while (m68k_ICount > 0);
/*TODO*///	
/*TODO*///	#else
/*TODO*///	
/*TODO*///		M68000_RUN();
/*TODO*///	
/*TODO*///	#endif /* MAME_DEBUG */
/*TODO*///	
/*TODO*///		return (cycles - m68k_ICount);
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	unsigned m68000_get_context(void *dst)
/*TODO*///	{
/*TODO*///		if( dst )
/*TODO*///			*(a68k_cpu_context*)dst = M68000_regs;
/*TODO*///		return sizeof(a68k_cpu_context);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_set_context(void *src)
/*TODO*///	{
/*TODO*///		if( src )
/*TODO*///		{
/*TODO*///			M68000_regs = *(a68k_cpu_context*)src;
/*TODO*///	        a68k_memory_intf = M68000_regs.Memory_Interface;
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68000_get_reg(int regnum)
/*TODO*///	{
/*TODO*///	    switch( regnum )
/*TODO*///	    {
/*TODO*///	    	case REG_PC:
/*TODO*///			case M68K_PC: return M68000_regs.pc;
/*TODO*///			case REG_SP:
/*TODO*///			case M68K_ISP: return M68000_regs.isp;
/*TODO*///			case M68K_USP: return M68000_regs.usp;
/*TODO*///			case M68K_SR: return M68000_regs.sr;
/*TODO*///			case M68K_VBR: return M68000_regs.vbr;
/*TODO*///			case M68K_SFC: return M68000_regs.sfc;
/*TODO*///			case M68K_DFC: return M68000_regs.dfc;
/*TODO*///			case M68K_D0: return M68000_regs.d[0];
/*TODO*///			case M68K_D1: return M68000_regs.d[1];
/*TODO*///			case M68K_D2: return M68000_regs.d[2];
/*TODO*///			case M68K_D3: return M68000_regs.d[3];
/*TODO*///			case M68K_D4: return M68000_regs.d[4];
/*TODO*///			case M68K_D5: return M68000_regs.d[5];
/*TODO*///			case M68K_D6: return M68000_regs.d[6];
/*TODO*///			case M68K_D7: return M68000_regs.d[7];
/*TODO*///			case M68K_A0: return M68000_regs.a[0];
/*TODO*///			case M68K_A1: return M68000_regs.a[1];
/*TODO*///			case M68K_A2: return M68000_regs.a[2];
/*TODO*///			case M68K_A3: return M68000_regs.a[3];
/*TODO*///			case M68K_A4: return M68000_regs.a[4];
/*TODO*///			case M68K_A5: return M68000_regs.a[5];
/*TODO*///			case M68K_A6: return M68000_regs.a[6];
/*TODO*///			case M68K_A7: return M68000_regs.a[7];
/*TODO*///			case REG_PREVIOUSPC: return M68000_regs.previous_pc;
/*TODO*///	/* TODO: Verify that this is the right thing to do for the purpose? */
/*TODO*///			default:
/*TODO*///				if( regnum <= REG_SP_CONTENTS )
/*TODO*///				{
/*TODO*///					unsigned offset = M68000_regs.isp + 4 * (REG_SP_CONTENTS - regnum);
/*TODO*///					if( offset < 0xfffffd )
/*TODO*///						return (*a68k_memory_intf.read32)( offset );
/*TODO*///	            }
/*TODO*///	    }
/*TODO*///	    return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_set_reg(int regnum, unsigned val)
/*TODO*///	{
/*TODO*///	    switch( regnum )
/*TODO*///	    {
/*TODO*///	    	case REG_PC:
/*TODO*///			case M68K_PC: M68000_regs.pc = val; break;
/*TODO*///			case REG_SP:
/*TODO*///			case M68K_ISP: M68000_regs.isp = val; break;
/*TODO*///			case M68K_USP: M68000_regs.usp = val; break;
/*TODO*///			case M68K_SR: M68000_regs.sr = val; break;
/*TODO*///			case M68K_VBR: M68000_regs.vbr = val; break;
/*TODO*///			case M68K_SFC: M68000_regs.sfc = val; break;
/*TODO*///			case M68K_DFC: M68000_regs.dfc = val; break;
/*TODO*///			case M68K_D0: M68000_regs.d[0] = val; break;
/*TODO*///			case M68K_D1: M68000_regs.d[1] = val; break;
/*TODO*///			case M68K_D2: M68000_regs.d[2] = val; break;
/*TODO*///			case M68K_D3: M68000_regs.d[3] = val; break;
/*TODO*///			case M68K_D4: M68000_regs.d[4] = val; break;
/*TODO*///			case M68K_D5: M68000_regs.d[5] = val; break;
/*TODO*///			case M68K_D6: M68000_regs.d[6] = val; break;
/*TODO*///			case M68K_D7: M68000_regs.d[7] = val; break;
/*TODO*///			case M68K_A0: M68000_regs.a[0] = val; break;
/*TODO*///			case M68K_A1: M68000_regs.a[1] = val; break;
/*TODO*///			case M68K_A2: M68000_regs.a[2] = val; break;
/*TODO*///			case M68K_A3: M68000_regs.a[3] = val; break;
/*TODO*///			case M68K_A4: M68000_regs.a[4] = val; break;
/*TODO*///			case M68K_A5: M68000_regs.a[5] = val; break;
/*TODO*///			case M68K_A6: M68000_regs.a[6] = val; break;
/*TODO*///			case M68K_A7: M68000_regs.a[7] = val; break;
/*TODO*///	/* TODO: Verify that this is the right thing to do for the purpose? */
/*TODO*///			default:
/*TODO*///				if( regnum <= REG_SP_CONTENTS )
/*TODO*///				{
/*TODO*///					unsigned offset = M68000_regs.isp + 4 * (REG_SP_CONTENTS - regnum);
/*TODO*///					if( offset < 0xfffffd )
/*TODO*///						(*a68k_memory_intf.write32)( offset, val );
/*TODO*///	            }
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68k_assert_irq(int int_line)
/*TODO*///	{
/*TODO*///		/* Save icount */
/*TODO*///		int StartCount = m68k_ICount;
/*TODO*///	
/*TODO*///		M68000_regs.IRQ_level = int_line;
/*TODO*///	
/*TODO*///	    /* Now check for Interrupt */
/*TODO*///	
/*TODO*///		m68k_ICount = -1;
/*TODO*///	    M68000_RUN();
/*TODO*///	
/*TODO*///	    /* Restore Count */
/*TODO*///		m68k_ICount = StartCount;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68k_clear_irq(int int_line)
/*TODO*///	{
/*TODO*///		M68000_regs.IRQ_level = 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_set_irq_line(int irqline, int state)
/*TODO*///	{
/*TODO*///		if (irqline == IRQ_LINE_NMI)
/*TODO*///			irqline = 7;
/*TODO*///		switch(state)
/*TODO*///		{
/*TODO*///			case CLEAR_LINE:
/*TODO*///				m68k_clear_irq(irqline);
/*TODO*///				return;
/*TODO*///			case ASSERT_LINE:
/*TODO*///				m68k_assert_irq(irqline);
/*TODO*///				return;
/*TODO*///			default:
/*TODO*///				m68k_assert_irq(irqline);
/*TODO*///				return;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_set_irq_callback(int (*callback)(int irqline))
/*TODO*///	{
/*TODO*///		M68000_regs.irq_callback = callback;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68000_set_reset_callback(int (*callback)(void))
/*TODO*///	{
/*TODO*///		M68000_regs.reset_callback = callback;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Return a formatted string for a register
/*TODO*///	 ****************************************************************************/
/*TODO*///	const char *m68000_info(void *context, int regnum)
/*TODO*///	{
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	//#endif
/*TODO*///	
/*TODO*///	    static char buffer[32][47+1];
/*TODO*///		static int which;
/*TODO*///		a68k_cpu_context *r = context;
/*TODO*///	
/*TODO*///		which = (which+1) % 32;
/*TODO*///		buffer[which][0] = '\0';
/*TODO*///		if (context == 0)
/*TODO*///			r = &M68000_regs;
/*TODO*///	
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case CPU_INFO_REG+M68K_PC: sprintf(buffer[which], "PC:%06X", r->pc); break;
/*TODO*///			case CPU_INFO_REG+M68K_ISP: sprintf(buffer[which], "ISP:%08X", r->isp); break;
/*TODO*///			case CPU_INFO_REG+M68K_USP: sprintf(buffer[which], "USP:%08X", r->usp); break;
/*TODO*///			case CPU_INFO_REG+M68K_SR: sprintf(buffer[which], "SR:%08X", r->sr); break;
/*TODO*///			case CPU_INFO_REG+M68K_VBR: sprintf(buffer[which], "VBR:%08X", r->vbr); break;
/*TODO*///			case CPU_INFO_REG+M68K_SFC: sprintf(buffer[which], "SFC:%08X", r->sfc); break;
/*TODO*///			case CPU_INFO_REG+M68K_DFC: sprintf(buffer[which], "DFC:%08X", r->dfc); break;
/*TODO*///			case CPU_INFO_REG+M68K_D0: sprintf(buffer[which], "D0:%08X", r->d[0]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D1: sprintf(buffer[which], "D1:%08X", r->d[1]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D2: sprintf(buffer[which], "D2:%08X", r->d[2]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D3: sprintf(buffer[which], "D3:%08X", r->d[3]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D4: sprintf(buffer[which], "D4:%08X", r->d[4]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D5: sprintf(buffer[which], "D5:%08X", r->d[5]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D6: sprintf(buffer[which], "D6:%08X", r->d[6]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D7: sprintf(buffer[which], "D7:%08X", r->d[7]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A0: sprintf(buffer[which], "A0:%08X", r->a[0]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A1: sprintf(buffer[which], "A1:%08X", r->a[1]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A2: sprintf(buffer[which], "A2:%08X", r->a[2]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A3: sprintf(buffer[which], "A3:%08X", r->a[3]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A4: sprintf(buffer[which], "A4:%08X", r->a[4]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A5: sprintf(buffer[which], "A5:%08X", r->a[5]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A6: sprintf(buffer[which], "A6:%08X", r->a[6]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A7: sprintf(buffer[which], "A7:%08X", r->a[7]); break;
/*TODO*///			case CPU_INFO_FLAGS:
/*TODO*///				sprintf(buffer[which], "%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c",
/*TODO*///					r->sr & 0x8000 ? 'T':'.',
/*TODO*///					r->sr & 0x4000 ? '?':'.',
/*TODO*///					r->sr & 0x2000 ? 'S':'.',
/*TODO*///					r->sr & 0x1000 ? '?':'.',
/*TODO*///					r->sr & 0x0800 ? '?':'.',
/*TODO*///					r->sr & 0x0400 ? 'I':'.',
/*TODO*///					r->sr & 0x0200 ? 'I':'.',
/*TODO*///					r->sr & 0x0100 ? 'I':'.',
/*TODO*///					r->sr & 0x0080 ? '?':'.',
/*TODO*///					r->sr & 0x0040 ? '?':'.',
/*TODO*///					r->sr & 0x0020 ? '?':'.',
/*TODO*///					r->sr & 0x0010 ? 'X':'.',
/*TODO*///					r->sr & 0x0008 ? 'N':'.',
/*TODO*///					r->sr & 0x0004 ? 'Z':'.',
/*TODO*///					r->sr & 0x0002 ? 'V':'.',
/*TODO*///					r->sr & 0x0001 ? 'C':'.');
/*TODO*///	            break;
/*TODO*///			case CPU_INFO_NAME: return "68000";
/*TODO*///			case CPU_INFO_FAMILY: return "Motorola 68K";
/*TODO*///			case CPU_INFO_VERSION: return "0.16";
/*TODO*///			case CPU_INFO_FILE: return __FILE__;
/*TODO*///			case CPU_INFO_CREDITS: return "Copyright 1998,99 Mike Coates, Darren Olafson. All rights reserved";
/*TODO*///			case CPU_INFO_REG_LAYOUT: return (const char*)M68K_layout;
/*TODO*///	        case CPU_INFO_WIN_LAYOUT: return (const char*)m68k_win_layout;
/*TODO*///		}
/*TODO*///		return buffer[which];
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68000_dasm(char *buffer, unsigned pc)
/*TODO*///	{
/*TODO*///		A68K_SET_PC_CALLBACK(pc);
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///		m68k_memory_intf = a68k_memory_intf;
/*TODO*///		return m68k_disassemble(buffer, pc, M68K_CPU_TYPE_68000);
/*TODO*///	#else
/*TODO*///		sprintf(buffer, "$%04X", cpu_readop16(pc) );
/*TODO*///		return 2;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * M68010 section
/*TODO*///	 ****************************************************************************/
/*TODO*///	
/*TODO*///	#if (HAS_M68010)
/*TODO*///	
/*TODO*///	void m68010_reset(void *param)
/*TODO*///	{
/*TODO*///		if (a68k_memory_intf.read8 != cpu_readmem24bew)
/*TODO*///			a68k_memory_intf = interface_a24_d16;
/*TODO*///	
/*TODO*///		m68k16_reset_common();
/*TODO*///	
/*TODO*///	    M68000_regs.CPUtype=1;
/*TODO*///	    M68000_regs.Memory_Interface = a68k_memory_intf;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68010_init(void) { m68000_init(); }
/*TODO*///	void m68010_exit(void) { m68000_exit(); }
/*TODO*///	int  m68010_execute(int cycles) { return m68000_execute(cycles); }
/*TODO*///	unsigned m68010_get_context(void *dst) { return m68000_get_context(dst); }
/*TODO*///	
/*TODO*///	void m68010_set_context(void *src)
/*TODO*///	{
/*TODO*///		if( src )
/*TODO*///	    {
/*TODO*///			M68000_regs = *(a68k_cpu_context*)src;
/*TODO*///	        a68k_memory_intf = M68000_regs.Memory_Interface;
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68010_get_reg(int regnum) { return m68000_get_reg(regnum); }
/*TODO*///	void m68010_set_reg(int regnum, unsigned val) { m68000_set_reg(regnum,val); }
/*TODO*///	void m68010_set_irq_line(int irqline, int state)  { m68000_set_irq_line(irqline,state); }
/*TODO*///	void m68010_set_irq_callback(int (*callback)(int irqline))  { m68000_set_irq_callback(callback); }
/*TODO*///	
/*TODO*///	const char *m68010_info(void *context, int regnum)
/*TODO*///	{
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case CPU_INFO_NAME: return "68010";
/*TODO*///		}
/*TODO*///		return m68000_info(context,regnum);
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68010_dasm(char *buffer, unsigned pc)
/*TODO*///	{
/*TODO*///		A68K_SET_PC_CALLBACK(pc);
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///		m68k_memory_intf = a68k_memory_intf;
/*TODO*///		return m68k_disassemble(buffer, pc, M68K_CPU_TYPE_68010);
/*TODO*///	#else
/*TODO*///		sprintf(buffer, "$%04X", cpu_readop16(pc) );
/*TODO*///		return 2;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	
/*TODO*///	#endif // A68K0
/*TODO*///	
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * M68020 section
/*TODO*///	 ****************************************************************************/
/*TODO*///	
/*TODO*///	#ifdef A68K2
/*TODO*///	
/*TODO*///	void m68020_init(void)
/*TODO*///	{
/*TODO*///		a68k_state_register("m68020");
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void m68k32_reset_common(void)
/*TODO*///	{
/*TODO*///		memset(&M68020_regs,0,sizeof(M68020_regs));
/*TODO*///	
/*TODO*///	    M68020_regs.a[7] = M68020_regs.isp = (( READOP(0) << 16 ) | READOP(2));
/*TODO*///	    M68020_regs.pc   = (( READOP(4) << 16 ) | READOP(6)) & 0xffffff;
/*TODO*///	    M68020_regs.sr_high = 0x27;
/*TODO*///	
/*TODO*///		#ifdef MAME_DEBUG
/*TODO*///			M68020_regs.sr = 0x2700;
/*TODO*///		#endif
/*TODO*///	
/*TODO*///	    M68020_RESET();
/*TODO*///	}
/*TODO*///	
/*TODO*///	#if (HAS_M68020)
/*TODO*///	
/*TODO*///	void m68020_reset(void *param)
/*TODO*///	{
/*TODO*///		if (a68k_memory_intf.read8 != cpu_readmem32bedw)
/*TODO*///			a68k_memory_intf = interface_a32_d32;
/*TODO*///	
/*TODO*///		m68k32_reset_common();
/*TODO*///	
/*TODO*///	    M68020_regs.CPUtype=2;
/*TODO*///	    M68020_regs.Memory_Interface = a68k_memory_intf;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_exit(void)
/*TODO*///	{
/*TODO*///		/* nothing to do ? */
/*TODO*///	}
/*TODO*///	
/*TODO*///	int m68020_execute(int cycles)
/*TODO*///	{
/*TODO*///		if (M68020_regs.IRQ_level == 0x80) return cycles;		/* STOP with no IRQs */
/*TODO*///	
/*TODO*///		m68k_ICount = cycles;
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	    do
/*TODO*///	    {
/*TODO*///			if (mame_debug)
/*TODO*///	        {
/*TODO*///				#ifdef TRACE68K
/*TODO*///	
/*TODO*///				int StartCycle = m68k_ICount;
/*TODO*///	
/*TODO*///	            skiptrace++;
/*TODO*///	
/*TODO*///	            if (skiptrace > 0)
/*TODO*///	            {
/*TODO*///				    int mycount, areg, dreg;
/*TODO*///	
/*TODO*///	                areg = dreg = 0;
/*TODO*///		            for (mycount=7;mycount>=0;mycount--)
/*TODO*///	                {
/*TODO*///	            	    areg = areg + M68020_regs.a[mycount];
/*TODO*///	                    dreg = dreg + M68020_regs.d[mycount];
/*TODO*///	                }
/*TODO*///	
/*TODO*///	           	    logerror("=> %8x %8x ",areg,dreg);
/*TODO*///				    logerror("%6x %4x %d\n",M68020_regs.pc,M68020_regs.sr & 0x271F,m68k_ICount);
/*TODO*///	            }
/*TODO*///	            #endif
/*TODO*///	
/*TODO*///	//	        m68k_memory_intf = a68k_memory_intf;
/*TODO*///				MAME_Debug();
/*TODO*///	            M68020_RUN();
/*TODO*///	
/*TODO*///	            #ifdef TRACE68K
/*TODO*///	            if ((M68020_regs.IRQ_level & 0x80) || (cpu_getstatus(cpu_getactivecpu()) == 0))
/*TODO*///	    			m68k_ICount = 0;
/*TODO*///	            else
/*TODO*///					m68k_ICount = StartCycle - 12;
/*TODO*///	            #endif
/*TODO*///	        }
/*TODO*///	        else
/*TODO*///				M68020_RUN();
/*TODO*///	
/*TODO*///	    } while (m68k_ICount > 0);
/*TODO*///	
/*TODO*///	#else
/*TODO*///	
/*TODO*///		M68020_RUN();
/*TODO*///	
/*TODO*///	#endif /* MAME_DEBUG */
/*TODO*///	
/*TODO*///		return (cycles - m68k_ICount);
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68020_get_context(void *dst)
/*TODO*///	{
/*TODO*///		if( dst )
/*TODO*///			*(a68k_cpu_context*)dst = M68020_regs;
/*TODO*///		return sizeof(a68k_cpu_context);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_set_context(void *src)
/*TODO*///	{
/*TODO*///		if( src )
/*TODO*///	    {
/*TODO*///			M68020_regs = *(a68k_cpu_context*)src;
/*TODO*///	        a68k_memory_intf = M68020_regs.Memory_Interface;
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68020_get_reg(int regnum)
/*TODO*///	{
/*TODO*///	    switch( regnum )
/*TODO*///	    {
/*TODO*///	    	case REG_PC:
/*TODO*///			case M68K_PC: return M68020_regs.pc;
/*TODO*///			case REG_SP:
/*TODO*///			case M68K_ISP: return M68020_regs.isp;
/*TODO*///			case M68K_USP: return M68020_regs.usp;
/*TODO*///			case M68K_SR: return M68020_regs.sr;
/*TODO*///			case M68K_VBR: return M68020_regs.vbr;
/*TODO*///			case M68K_SFC: return M68020_regs.sfc;
/*TODO*///			case M68K_DFC: return M68020_regs.dfc;
/*TODO*///			case M68K_D0: return M68020_regs.d[0];
/*TODO*///			case M68K_D1: return M68020_regs.d[1];
/*TODO*///			case M68K_D2: return M68020_regs.d[2];
/*TODO*///			case M68K_D3: return M68020_regs.d[3];
/*TODO*///			case M68K_D4: return M68020_regs.d[4];
/*TODO*///			case M68K_D5: return M68020_regs.d[5];
/*TODO*///			case M68K_D6: return M68020_regs.d[6];
/*TODO*///			case M68K_D7: return M68020_regs.d[7];
/*TODO*///			case M68K_A0: return M68020_regs.a[0];
/*TODO*///			case M68K_A1: return M68020_regs.a[1];
/*TODO*///			case M68K_A2: return M68020_regs.a[2];
/*TODO*///			case M68K_A3: return M68020_regs.a[3];
/*TODO*///			case M68K_A4: return M68020_regs.a[4];
/*TODO*///			case M68K_A5: return M68020_regs.a[5];
/*TODO*///			case M68K_A6: return M68020_regs.a[6];
/*TODO*///			case M68K_A7: return M68020_regs.a[7];
/*TODO*///			case REG_PREVIOUSPC: return M68020_regs.previous_pc;
/*TODO*///	/* TODO: Verify that this is the right thing to do for the purpose? */
/*TODO*///			default:
/*TODO*///				if( regnum <= REG_SP_CONTENTS )
/*TODO*///				{
/*TODO*///					unsigned offset = M68020_regs.isp + 4 * (REG_SP_CONTENTS - regnum);
/*TODO*///					if( offset < 0xfffffd )
/*TODO*///						return (*a68k_memory_intf.read32)( offset );
/*TODO*///	            }
/*TODO*///	    }
/*TODO*///	    return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_set_reg(int regnum, unsigned val)
/*TODO*///	{
/*TODO*///	    switch( regnum )
/*TODO*///	    {
/*TODO*///	    	case REG_PC:
/*TODO*///			case M68K_PC: M68020_regs.pc = val; break;
/*TODO*///			case REG_SP:
/*TODO*///			case M68K_ISP: M68020_regs.isp = val; break;
/*TODO*///			case M68K_USP: M68020_regs.usp = val; break;
/*TODO*///			case M68K_SR: M68020_regs.sr = val; break;
/*TODO*///			case M68K_VBR: M68020_regs.vbr = val; break;
/*TODO*///			case M68K_SFC: M68020_regs.sfc = val; break;
/*TODO*///			case M68K_DFC: M68020_regs.dfc = val; break;
/*TODO*///			case M68K_D0: M68020_regs.d[0] = val; break;
/*TODO*///			case M68K_D1: M68020_regs.d[1] = val; break;
/*TODO*///			case M68K_D2: M68020_regs.d[2] = val; break;
/*TODO*///			case M68K_D3: M68020_regs.d[3] = val; break;
/*TODO*///			case M68K_D4: M68020_regs.d[4] = val; break;
/*TODO*///			case M68K_D5: M68020_regs.d[5] = val; break;
/*TODO*///			case M68K_D6: M68020_regs.d[6] = val; break;
/*TODO*///			case M68K_D7: M68020_regs.d[7] = val; break;
/*TODO*///			case M68K_A0: M68020_regs.a[0] = val; break;
/*TODO*///			case M68K_A1: M68020_regs.a[1] = val; break;
/*TODO*///			case M68K_A2: M68020_regs.a[2] = val; break;
/*TODO*///			case M68K_A3: M68020_regs.a[3] = val; break;
/*TODO*///			case M68K_A4: M68020_regs.a[4] = val; break;
/*TODO*///			case M68K_A5: M68020_regs.a[5] = val; break;
/*TODO*///			case M68K_A6: M68020_regs.a[6] = val; break;
/*TODO*///			case M68K_A7: M68020_regs.a[7] = val; break;
/*TODO*///	/* TODO: Verify that this is the right thing to do for the purpose? */
/*TODO*///			default:
/*TODO*///				if( regnum <= REG_SP_CONTENTS )
/*TODO*///				{
/*TODO*///					unsigned offset = M68020_regs.isp + 4 * (REG_SP_CONTENTS - regnum);
/*TODO*///					if( offset < 0xfffffd )
/*TODO*///						(*a68k_memory_intf.write32)( offset, val );
/*TODO*///	            }
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_assert_irq(int int_line)
/*TODO*///	{
/*TODO*///		/* Save icount */
/*TODO*///		int StartCount = m68k_ICount;
/*TODO*///	
/*TODO*///		M68020_regs.IRQ_level = int_line;
/*TODO*///	
/*TODO*///	    /* Now check for Interrupt */
/*TODO*///	
/*TODO*///		m68k_ICount = -1;
/*TODO*///	    M68020_RUN();
/*TODO*///	
/*TODO*///	    /* Restore Count */
/*TODO*///		m68k_ICount = StartCount;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_clear_irq(int int_line)
/*TODO*///	{
/*TODO*///		M68020_regs.IRQ_level = 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_set_irq_line(int irqline, int state)
/*TODO*///	{
/*TODO*///		if (irqline == IRQ_LINE_NMI)
/*TODO*///			irqline = 7;
/*TODO*///		switch(state)
/*TODO*///		{
/*TODO*///			case CLEAR_LINE:
/*TODO*///				m68020_clear_irq(irqline);
/*TODO*///				return;
/*TODO*///			case ASSERT_LINE:
/*TODO*///				m68020_assert_irq(irqline);
/*TODO*///				return;
/*TODO*///			default:
/*TODO*///				m68020_assert_irq(irqline);
/*TODO*///				return;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_set_irq_callback(int (*callback)(int irqline))
/*TODO*///	{
/*TODO*///		M68020_regs.irq_callback = callback;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68020_set_reset_callback(int (*callback)(void))
/*TODO*///	{
/*TODO*///		M68020_regs.reset_callback = callback;
/*TODO*///	}
/*TODO*///	
/*TODO*///	const char *m68020_info(void *context, int regnum)
/*TODO*///	{
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	//#endif
/*TODO*///	
/*TODO*///	    static char buffer[32][47+1];
/*TODO*///		static int which;
/*TODO*///		a68k_cpu_context *r = context;
/*TODO*///	
/*TODO*///		which = (which + 1) % 32;
/*TODO*///		buffer[which][0] = '\0';
/*TODO*///		if (context == 0)
/*TODO*///			r = &M68020_regs;
/*TODO*///	
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case CPU_INFO_REG+M68K_PC: sprintf(buffer[which], "PC:%06X", r->pc); break;
/*TODO*///			case CPU_INFO_REG+M68K_ISP: sprintf(buffer[which], "ISP:%08X", r->isp); break;
/*TODO*///			case CPU_INFO_REG+M68K_USP: sprintf(buffer[which], "USP:%08X", r->usp); break;
/*TODO*///			case CPU_INFO_REG+M68K_SR: sprintf(buffer[which], "SR:%08X", r->sr); break;
/*TODO*///			case CPU_INFO_REG+M68K_VBR: sprintf(buffer[which], "VBR:%08X", r->vbr); break;
/*TODO*///			case CPU_INFO_REG+M68K_SFC: sprintf(buffer[which], "SFC:%08X", r->sfc); break;
/*TODO*///			case CPU_INFO_REG+M68K_DFC: sprintf(buffer[which], "DFC:%08X", r->dfc); break;
/*TODO*///			case CPU_INFO_REG+M68K_D0: sprintf(buffer[which], "D0:%08X", r->d[0]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D1: sprintf(buffer[which], "D1:%08X", r->d[1]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D2: sprintf(buffer[which], "D2:%08X", r->d[2]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D3: sprintf(buffer[which], "D3:%08X", r->d[3]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D4: sprintf(buffer[which], "D4:%08X", r->d[4]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D5: sprintf(buffer[which], "D5:%08X", r->d[5]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D6: sprintf(buffer[which], "D6:%08X", r->d[6]); break;
/*TODO*///			case CPU_INFO_REG+M68K_D7: sprintf(buffer[which], "D7:%08X", r->d[7]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A0: sprintf(buffer[which], "A0:%08X", r->a[0]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A1: sprintf(buffer[which], "A1:%08X", r->a[1]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A2: sprintf(buffer[which], "A2:%08X", r->a[2]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A3: sprintf(buffer[which], "A3:%08X", r->a[3]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A4: sprintf(buffer[which], "A4:%08X", r->a[4]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A5: sprintf(buffer[which], "A5:%08X", r->a[5]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A6: sprintf(buffer[which], "A6:%08X", r->a[6]); break;
/*TODO*///			case CPU_INFO_REG+M68K_A7: sprintf(buffer[which], "A7:%08X", r->a[7]); break;
/*TODO*///			case CPU_INFO_FLAGS:
/*TODO*///				sprintf(buffer[which], "%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c",
/*TODO*///					r->sr & 0x8000 ? 'T':'.',
/*TODO*///					r->sr & 0x4000 ? '?':'.',
/*TODO*///					r->sr & 0x2000 ? 'S':'.',
/*TODO*///					r->sr & 0x1000 ? '?':'.',
/*TODO*///					r->sr & 0x0800 ? '?':'.',
/*TODO*///					r->sr & 0x0400 ? 'I':'.',
/*TODO*///					r->sr & 0x0200 ? 'I':'.',
/*TODO*///					r->sr & 0x0100 ? 'I':'.',
/*TODO*///					r->sr & 0x0080 ? '?':'.',
/*TODO*///					r->sr & 0x0040 ? '?':'.',
/*TODO*///					r->sr & 0x0020 ? '?':'.',
/*TODO*///					r->sr & 0x0010 ? 'X':'.',
/*TODO*///					r->sr & 0x0008 ? 'N':'.',
/*TODO*///					r->sr & 0x0004 ? 'Z':'.',
/*TODO*///					r->sr & 0x0002 ? 'V':'.',
/*TODO*///					r->sr & 0x0001 ? 'C':'.');
/*TODO*///	            break;
/*TODO*///			case CPU_INFO_NAME: return "68020";
/*TODO*///			case CPU_INFO_FAMILY: return "Motorola 68K";
/*TODO*///			case CPU_INFO_VERSION: return "0.16";
/*TODO*///			case CPU_INFO_FILE: return __FILE__;
/*TODO*///			case CPU_INFO_CREDITS: return "Copyright 1998,99 Mike Coates, Darren Olafson. All rights reserved";
/*TODO*///			case CPU_INFO_REG_LAYOUT: return (const char*)M68K_layout;
/*TODO*///	        case CPU_INFO_WIN_LAYOUT: return (const char*)m68k_win_layout;
/*TODO*///		}
/*TODO*///		return buffer[which];
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68020_dasm(char *buffer, unsigned pc)
/*TODO*///	{
/*TODO*///		A68K_SET_PC_CALLBACK(pc);
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///		m68k_memory_intf = a68k_memory_intf;
/*TODO*///		return m68k_disassemble(buffer, pc, M68K_CPU_TYPE_68020);
/*TODO*///	#else
/*TODO*///		sprintf(buffer, "$%04X", cpu_readop16(pc) );
/*TODO*///		return 2;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#if (HAS_M68EC020)
/*TODO*///	
/*TODO*///	void m68ec020_reset(void *param)
/*TODO*///	{
/*TODO*///		if (a68k_memory_intf.read8 != cpu_readmem24bedw)
/*TODO*///			a68k_memory_intf = interface_a24_d32;
/*TODO*///	
/*TODO*///		m68k32_reset_common();
/*TODO*///	
/*TODO*///	    M68020_regs.CPUtype=2;
/*TODO*///	    M68020_regs.Memory_Interface = a68k_memory_intf;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void m68ec020_init(void) { m68020_init(); }
/*TODO*///	void m68ec020_exit(void) { m68020_exit(); }
/*TODO*///	int  m68ec020_execute(int cycles) { return m68020_execute(cycles); }
/*TODO*///	unsigned m68ec020_get_context(void *dst) { return m68020_get_context(dst); }
/*TODO*///	
/*TODO*///	void m68ec020_set_context(void *src)
/*TODO*///	{
/*TODO*///		if( src )
/*TODO*///	    {
/*TODO*///			M68020_regs = *(a68k_cpu_context*)src;
/*TODO*///	        a68k_memory_intf = M68020_regs.Memory_Interface;
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68ec020_get_reg(int regnum) { return m68020_get_reg(regnum); }
/*TODO*///	void m68ec020_set_reg(int regnum, unsigned val) { m68020_set_reg(regnum,val); }
/*TODO*///	void m68ec020_set_irq_line(int irqline, int state)  { m68020_set_irq_line(irqline,state); }
/*TODO*///	void m68ec020_set_irq_callback(int (*callback)(int irqline))  { m68020_set_irq_callback(callback); }
/*TODO*///	
/*TODO*///	const char *m68ec020_info(void *context, int regnum)
/*TODO*///	{
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case CPU_INFO_NAME: return "68EC020";
/*TODO*///		}
/*TODO*///		return m68020_info(context,regnum);
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned m68ec020_dasm(char *buffer, unsigned pc)
/*TODO*///	{
/*TODO*///		A68K_SET_PC_CALLBACK(pc);
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///		m68k_memory_intf = a68k_memory_intf;
/*TODO*///		return m68k_disassemble(buffer, pc, M68K_CPU_TYPE_68EC020);
/*TODO*///	#else
/*TODO*///		sprintf(buffer, "$%04X", cpu_readop16(pc) );
/*TODO*///		return 2;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#endif // A68K2
}
