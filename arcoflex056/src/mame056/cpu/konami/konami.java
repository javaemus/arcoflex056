/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.cpu.konami;

import static common.libc.cstdio.*;
import static arcadeflex056.osdepend.logerror;
import static mame056.cpuintrfH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mame056.cpu.konami.konamiH.*;
import static mame056.cpu.konami.konamtbl.*;

/**
 *
 * @author chusogar
 */
public class konami extends cpu_interface {
    
    public static FILE konamilog = null;//fopen("konami.log", "wa");  //for debug purposes

    @Override
    public void init() {
        konami_init();
    }

    @Override
    public void reset(Object param) {
        konami_reset(param);
    }

    @Override
    public void exit() {
        konami_exit();
    }

    @Override
    public int execute(int cycles) {
        return konami_execute(cycles);
    }

    @Override
    public Object init_context() {
        Object reg = new konami_Regs();
        return reg;
    }

    @Override
    public Object get_context() {
        return konami_get_context(null);
    }

    @Override
    public void set_context(Object reg) {
        konami_set_context(reg);
    }

    @Override
    public int[] get_cycle_table(int which) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_cycle_table(int which, int[] new_table) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_reg(int regnum) {
        return konami_get_reg(regnum);
    }

    @Override
    public void set_reg(int regnum, int val) {
        konami_set_reg(regnum, val);
    }

    @Override
    public void set_irq_line(int irqline, int linestate) {
        konami_set_irq_line(irqline, linestate);
    }

    @Override
    public void set_irq_callback(irqcallbacksPtr callback) {
        konami_set_irq_callback(callback);
    }

    @Override
    public String cpu_info(Object context, int regnum) {
        /*TODO*///	static char buffer[16][47+1];
/*TODO*///	static int which = 0;
/*TODO*///	konami_Regs *r = context;
/*TODO*///
/*TODO*///	which = ++which % 16;
/*TODO*///    buffer[which][0] = '\0';
/*TODO*///	if( !context )
/*TODO*///		r = &konami;
/*TODO*///
        switch (regnum) {
            case CPU_INFO_NAME:
                return "KONAMI";
            case CPU_INFO_FAMILY:
                return "KONAMI 5000x";
            case CPU_INFO_VERSION:
                return "1.0";
            case CPU_INFO_FILE:
                return "konami.java";
            case CPU_INFO_CREDITS:
                return "Copyright (C) The MAME Team 1999";
            /*TODO*///		case CPU_INFO_REG_LAYOUT: return (const char*)konami_reg_layout;
/*TODO*///		case CPU_INFO_WIN_LAYOUT: return (const char*)konami_win_layout;
/*TODO*///
/*TODO*///		case CPU_INFO_FLAGS:
/*TODO*///			sprintf(buffer[which], "%c%c%c%c%c%c%c%c",
/*TODO*///				r->cc & 0x80 ? 'E':'.',
/*TODO*///				r->cc & 0x40 ? 'F':'.',
/*TODO*///                r->cc & 0x20 ? 'H':'.',
/*TODO*///                r->cc & 0x10 ? 'I':'.',
/*TODO*///                r->cc & 0x08 ? 'N':'.',
/*TODO*///                r->cc & 0x04 ? 'Z':'.',
/*TODO*///                r->cc & 0x02 ? 'V':'.',
/*TODO*///                r->cc & 0x01 ? 'C':'.');
/*TODO*///            break;
/*TODO*///		case CPU_INFO_REG+KONAMI_PC: sprintf(buffer[which], "PC:%04X", r->pc.w.l); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_S: sprintf(buffer[which], "S:%04X", r->s.w.l); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_CC: sprintf(buffer[which], "CC:%02X", r->cc); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_U: sprintf(buffer[which], "U:%04X", r->u.w.l); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_A: sprintf(buffer[which], "A:%02X", r->d.b.h); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_B: sprintf(buffer[which], "B:%02X", r->d.b.l); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_X: sprintf(buffer[which], "X:%04X", r->x.w.l); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_Y: sprintf(buffer[which], "Y:%04X", r->y.w.l); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_DP: sprintf(buffer[which], "DP:%02X", r->dp.b.h); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_NMI_STATE: sprintf(buffer[which], "NMI:%X", r->nmi_state); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_IRQ_STATE: sprintf(buffer[which], "IRQ:%X", r->irq_state[KONAMI_IRQ_LINE]); break;
/*TODO*///		case CPU_INFO_REG+KONAMI_FIRQ_STATE: sprintf(buffer[which], "FIRQ:%X", r->irq_state[KONAMI_FIRQ_LINE]); break;
        }
        throw new UnsupportedOperationException("unsupported konami cpu_info");
        /*TODO*///	return buffer[which];
    }

    @Override
    public String cpu_dasm(String buffer, int pc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int memory_read(int offset) {
        return cpu_readmem16(offset);
    }

    @Override
    public void memory_write(int offset, int data) {
        cpu_writemem16(offset, data);
    }

    @Override
    public int internal_read(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void internal_write(int offset, int data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_op_base(int pc) {
        cpu_setOPbase16.handler(pc);
    }

    @Override
    public int mem_address_bits_of_cpu() {
        return 16;
    }
    
    public static abstract interface konami_cpu_setlines_callbackPtr {
        public abstract void handler(int lines);
    }
    

    public konami() {
        cpu_num = CPU_KONAMI;
        num_irqs = 2;
        default_vector = 0;
        overclock = 1.0;
        //no_int = KONAMI_INT_NONE;
        irq_int = IRQ_LINE_NMI;
        //nmi_int = KONAMI_INT_NMI;
        address_shift = 0;
        address_bits = 16;
        databus_width = 8;
        endianess = CPU_IS_BE;
        align_unit = 1;
        max_inst_len = 4;
        //abits1 = ABITS1_16;
        //abits2 = ABITS2_16;
        //abitsmin = ABITS_MIN_16;

        icount = konami_ICount;
        konami_ICount[0] = 50000;

        //intialize interfaces
        //burn = burn_function;

    }
    
/*TODO*///      static UINT8 konami_reg_layout[] = {
/*TODO*///		KONAMI_PC, KONAMI_S, KONAMI_CC, KONAMI_A, KONAMI_B, KONAMI_X, -1,
/*TODO*///		KONAMI_Y, KONAMI_U, KONAMI_DP, KONAMI_NMI_STATE, KONAMI_IRQ_STATE, KONAMI_FIRQ_STATE, 0
/*TODO*///	};
/*TODO*///	
/*TODO*///	/* Layout of the debugger windows x,y,w,h */
/*TODO*///	static UINT8 konami_win_layout[] = {
/*TODO*///		27, 0,53, 4,	/* register window (top, right rows) */
/*TODO*///		 0, 0,26,22,	/* disassembler window (left colums) */
/*TODO*///		27, 5,53, 8,	/* memory #1 window (right, upper middle) */
/*TODO*///		27,14,53, 8,	/* memory #2 window (right, lower middle) */
/*TODO*///		 0,23,80, 1,	/* command line window (bottom rows) */
/*TODO*///	};
/*TODO*///

    /* Konami Registers */
    public static class konami_Regs {

        public /*PAIR*/ int pc; 		/* Program counter */

        public /*PAIR*/ int ppc;		/* Previous program counter */

        //public int a;
        //public int b;   //PAIR	d;		/* Accumulator a and b */

        public int d;
        public /*PAIR*/ int dp; 		/* Direct Page register (page in MSB) */

        public int u;
        public int s;//PAIR	u, s;		/* Stack pointers */
        public int x;
        public int y;//PAIR	x, y;		/* Index registers */
        public int /*UINT8*/ cc;
        public int /*UINT8*/ ireg;		/* First opcode */

        public int[] /*UINT8*/ irq_state = new int[2];
        public int extra_cycles; /* cycles used up by interrupts */

        public irqcallbacksPtr irq_callback;
        public int /*UINT8*/ int_state;  /* SYNC and CWAI flags */

        public int /*UINT8*/ nmi_state;
    }

    	
/*TODO*///	/* Konami Registers */
/*TODO*///	typedef struct
/*TODO*///	{
/*TODO*///		PAIR	pc; 		/* Program counter */
/*TODO*///	    PAIR    ppc;        /* Previous program counter */
/*TODO*///	    PAIR    d;          /* Accumulator a and b */
/*TODO*///	    PAIR    dp;         /* Direct Page register (page in MSB) */
/*TODO*///		PAIR	u, s;		/* Stack pointers */
/*TODO*///		PAIR	x, y;		/* Index registers */
/*TODO*///	    UINT8   cc;
/*TODO*///		UINT8	ireg;		/* first opcode */
/*TODO*///	    UINT8   irq_state[2];
/*TODO*///	    int     extra_cycles; /* cycles used up by interrupts */
/*TODO*///	    int     (*irq_callback)(int irqline);
/*TODO*///	    UINT8   int_state;  /* SYNC and CWAI flags */
/*TODO*///		UINT8	nmi_state;
/*TODO*///	} konami_Regs;
	
	/* flag bits in the cc register */
        public static final int CC_C    = 0x01;        /* Carry */
        public static final int CC_V    = 0x02;        /* Overflow */
        public static final int CC_Z    = 0x04;        /* Zero */
        public static final int CC_N    = 0x08;        /* Negative */
	public static final int CC_II   = 0x10;        /* Inhibit IRQ */
        public static final int CC_H    = 0x20;        /* Half (auxiliary) carry */
	public static final int CC_IF   = 0x40;        /* Inhibit FIRQ */
        public static final int CC_E    = 0x80;        /* entire state pushed */
	
	/* Konami registers */
	static konami_Regs konami;
	
	public static int pPPC(){ return konami.ppc; }
        public static void pPPC(int val){ konami.ppc = val; }
        public static int pPC(){ return konami.pc; }
        public static void pPC(int val){ konami.pc = val; }
	public static int pU(){ return konami.u; }
        public static void pU(int val){ konami.u = val; }
	public static int pS(){ return konami.s; }
        public static void pS(int val){ konami.s = val; }
	public static int pX(){ return konami.x; }
        public static void pX(int val){ konami.x = val; }
	public static int pY(){ return konami.y; }
        public static void pY(int val){ konami.y = val; }
	public static int pD(){ return konami.d; }
        public static void pD(int val){ konami.d = val; }
	
	public static int PPC(){ return konami.ppc & 0xFF; }
        public static void PPC(int val){ konami.ppc |= val & 0xFF; };
	public static int PC(){ return konami.pc & 0xFF; }
        public static void PC(int val){ konami.pc |= val & 0xFF; };
	public static int PCD(){ return konami.pc; }
        public static void PCD(int val){ konami.pc = val; }
        public static int U(){ return konami.u & 0xFF; }
        public static void U(int val){ konami.u |= val & 0xFF; }
	public static int UD(){ return konami.u; }
        public static void UD(int val){ konami.u = val; }
	public static int S(){ return konami.s & 0xFF; }
        public static void S(int val){ konami.s |= val & 0xFF; }
	public static int SD(){ return konami.s; }
        public static void SD(int val){ konami.s = val; }
	public static int X(){ return konami.x & 0xFF; }
        public static void X(int val){ konami.x |= val & 0xFF; }
	public static int XD(){ return konami.x; }
        public static void XD(int val){ konami.x = val; }
        public static int Y(){ return konami.y & 0xFF; }
        public static void Y(int val){ konami.y |= val & 0xFF; }
        public static int YD(){ return konami.y; }
        public static void YD(int val){ konami.y = val; }
        public static int D(){ return konami.d; }           // CHECK THIS!!!!!!
        public static void D(int val){ konami.d = val; }    // CHECK THIS!!!!!!
        public static int A(){ return ((konami.d & 0xFF00)>>8); }
        public static void A(int val){ konami.d |= (val<<8); }
        public static int B() { return konami.d & 0xFF; }
        public static void B(int val) { konami.d |= val & 0xFF; }
        public static int DP(){ return ((konami.dp & 0xFF00)>>8); }
        public static void DP(int val){ konami.dp |= (val<<8); }
	public static int DPD() { return konami.dp; }
        public static void DPD(int val) { konami.dp = val; }
	public static int CC() { return konami.cc; }
        public static void CC(int val) { konami.cc = val; }
	
        public static int ea;         /* effective address */
        public static int EA(){ return	ea & 0xFF; }
        public static void EA(int val){ ea |= (val & 0xFF); }
        public static int EAD(){ return ea; }
        public static void EAD(int val){ ea = val; }

	public static int KONAMI_CWAI		= 8;	/* set when CWAI is waiting for an interrupt */
	public static int KONAMI_SYNC		= 16;	/* set when SYNC is waiting for an interrupt */
	public static int KONAMI_LDS		= 32;	/* set when LDS occured at least once */
	
	public static void CHECK_IRQ_LINES()
        {
		if( konami.irq_state[KONAMI_IRQ_LINE] != CLEAR_LINE ||
			konami.irq_state[KONAMI_FIRQ_LINE] != CLEAR_LINE )
			konami.int_state &= ~KONAMI_SYNC; /* clear SYNC flag */
		if( konami.irq_state[KONAMI_FIRQ_LINE]!=CLEAR_LINE && (CC() & CC_IF)==0 )
		{
			/* fast IRQ */
			/* state already saved by CWAI? */
			if(( konami.int_state & KONAMI_CWAI ) != 0)
			{
				konami.int_state &= ~KONAMI_CWAI;  /* clear CWAI */
				konami.extra_cycles += 7;		 /* subtract +7 cycles */
                        }
			else
			{
				CC( CC() & ~CC_E );				/* save 'short' state */
				PUSHWORD(pPC());
				PUSHBYTE(CC());
				konami.extra_cycles += 10;	/* subtract +10 cycles */
			}
			CC( CC() | CC_IF | CC_II );			/* inhibit FIRQ and IRQ */
			PCD( RM16(0xfff6) );
			change_pc16(PC());					/* TS 971002 */
			konami.irq_callback.handler(KONAMI_FIRQ_LINE);
		}
		else
		if( konami.irq_state[KONAMI_IRQ_LINE]!=CLEAR_LINE && (CC() & CC_II)==0 )
		{
			/* standard IRQ */
			/* state already saved by CWAI? */
			if(( konami.int_state & KONAMI_CWAI ) != 0)
			{
				konami.int_state &= ~KONAMI_CWAI;  /* clear CWAI flag */
				konami.extra_cycles += 7;		 /* subtract +7 cycles */
			}
			else
			{
				CC( CC() | CC_E ); 				/* save entire state */
				PUSHWORD(pPC());
				PUSHWORD(pU());
				PUSHWORD(pY());
				PUSHWORD(pX());
				PUSHBYTE(DP());
				PUSHBYTE(B());
				PUSHBYTE(A());
				PUSHBYTE(CC());
				konami.extra_cycles += 19;	 /* subtract +19 cycles */
			}
			CC( CC() | CC_II );					/* inhibit IRQ */
			PCD( RM16(0xfff8) );
			change_pc16(PC());					/* TS 971002 */
			konami.irq_callback.handler(KONAMI_IRQ_LINE);
		}
        }
	
	/* public globals */
        public static int[] konami_ICount = new int[1];
        public static konami_cpu_setlines_callbackPtr konami_cpu_setlines_callback; /* callback called when A16-A23 are set */
	
/*TODO*///	int konami_Flags;	/* flags for speed optimization (obsolete!!) */

	/* these are re-defined in konami.h TO RAM, ROM or functions in memory.c */
	public static int RM(int Addr){ return KONAMI_RDMEM(Addr); }
	public static void WM(int Addr, int Value){ KONAMI_WRMEM(Addr,Value); }
	public static int ROP(int Addr){ return KONAMI_RDOP(Addr); }
	public static int ROP_ARG(int Addr){ return KONAMI_RDOP_ARG(Addr); }
	
        public static int SIGNED(int a){ return a & 0xFFFF;}

/*TODO*///	/* macros to access memory */
        public static int IMMBYTE()	{ 
            int reg = ROP_ARG(PCD());
            PC(PC()+1);
            return reg; 
        }
        public static int IMMWORD()	{ 
            int reg = (ROP_ARG(PCD())<<8) | ROP_ARG(PCD()+1);
            PC(PC()+2);
            return reg;        
        }

	public static void PUSHBYTE(int b){ S(S()-1); WM(SD(),b); }
	public static void PUSHWORD(int w){ S(S()-1); WM(SD(),w&0xFF); S(S()-1); WM(SD(),(w>>8)&0xFF); }
	public static void PULLBYTE(int b){ b=KONAMI_RDMEM(SD()); S(S()+1); }
	public static void PULLWORD(int w){ w=KONAMI_RDMEM(SD())<<8; S(S()+1); w|=KONAMI_RDMEM(SD()); S(S()+1); }

        public static void PSHUBYTE(int b){ U(U()-1); WM(UD(),b); }
	public static void PSHUWORD(int w){ U(U()-1); WM(UD(),w&0xff); U(U()-1); WM(UD(),(w>>8)&0xFF); }
        public static void PULUBYTE(int b){ b=KONAMI_RDMEM(UD()); U(U()+1); }
        public static void PULUWORD(int w){ w=KONAMI_RDMEM(UD())<<8; U(U()+1); w|=KONAMI_RDMEM(UD()); U(U()+1); }

        public static void CLR_HNZVC(){	CC( CC() & ~(CC_H|CC_N|CC_Z|CC_V|CC_C)); }
	public static void CLR_NZV(){ CC( CC() & ~(CC_N|CC_Z|CC_V)); }
        public static void CLR_HNZC(){ CC( CC() & ~(CC_H|CC_N|CC_Z|CC_C)); }
        public static void CLR_NZVC(){ CC( CC() & ~(CC_N|CC_Z|CC_V|CC_C)); }
        public static void CLR_Z(){ CC(CC() & ~(CC_Z)); }
        public static void CLR_NZC(){ CC( CC() & ~(CC_N|CC_Z|CC_C) ); }
        public static void CLR_ZC(){ CC( CC() & ~(CC_Z|CC_C)); }

	/* macros for CC -- CC bits affected should be reset before calling */
        public static void SET_Z(int a){ if (a == 0)SEZ(); }
        public static void SET_Z8(int a){ SET_Z(a); }
        public static void SET_Z16(int a){ SET_Z(a); }
        public static void SET_N8(int a){ CC( CC() |((a&0x80)>>4) ); }
        public static void SET_N16(int a){ CC( CC() | ((a&0x8000)>>12)) ; }
	public static void SET_H(int a, int b, int r){ CC( CC() | (((a^b^r)&0x10)<<1)); }
	public static void SET_C8(int a){ CC( CC() | ((a&0x100)>>8)); }
        public static void SET_C16(int a){ CC( CC() | ((a&0x10000)>>16)); }
        public static void SET_V8(int a, int b, int r){	CC( CC() | (((a^b^r^(r>>1))&0x80)>>6)); }
        public static void SET_V16(int a, int b, int r){ CC( CC() | (((a^b^r^(r>>1))&0x8000)>>14)); }

	static int flags8i[]=	 /* increment */
	{
	CC_Z,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	CC_N|CC_V,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N
	};
        
	static int flags8d[]= /* decrement */
	{
	CC_Z,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,CC_V,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,
	CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N,CC_N
	};
        
        public static void SET_FLAGS8I(int a){ CC( CC() | flags8i[(a)&0xff] ); }
        public static void SET_FLAGS8D(int a){ CC( CC() | flags8d[(a)&0xff] ); }
	
	/* combos */
        public static void SET_NZ8(int a){ SET_N8(a);SET_Z(a); }
        public static void SET_NZ16(int a){ SET_N16(a);SET_Z(a); }
        public static void SET_FLAGS8(int a, int b, int r){ SET_N8(r);SET_Z8(r);SET_V8(a,b,r);SET_C8(r);}
        public static void SET_FLAGS16(int a, int b, int r){ SET_N16(r);SET_Z16(r);SET_V16(a,b,r);SET_C16(r); }

/*TODO*///	/* macros for addressing modes (postbytes have their own code) */
        public static void DIRECT(){ EAD( DPD() ); ea |= IMMBYTE()&0xFF; }
/*TODO*///	#define IMM8	EAD = PCD; PC++
/*TODO*///	#define IMM16	EAD = PCD; PC+=2
/*TODO*///	#define EXTENDED IMMWORD(ea)
/*TODO*///	
/*TODO*///	/* macros to set status flags */
        public static void SEC(){ CC( CC() | CC_C ); }
/*TODO*///	#define CLC CC&=~CC_C
        public static void SEZ(){ CC( CC() | CC_Z ); }
/*TODO*///	#define CLZ CC&=~CC_Z
/*TODO*///	#define SEN CC|=CC_N
/*TODO*///	#define CLN CC&=~CC_N
/*TODO*///	#define SEV CC|=CC_V
/*TODO*///	#define CLV CC&=~CC_V
/*TODO*///	#define SEH CC|=CC_H
/*TODO*///	#define CLH CC&=~CC_H
/*TODO*///	
/*TODO*///	/* macros for convenience */
/*TODO*///	#define DIRBYTE(b) DIRECT; b=RM(EAD)
        public static int DIRWORD(){ int w=0; DIRECT(); w=RM16(EAD()); return w; }
/*TODO*///	#define EXTBYTE(b) EXTENDED; b=RM(EAD)
/*TODO*///	#define EXTWORD(w) EXTENDED; w.d=RM16(EAD)
	
	/* macros for branch instructions */
	public static void BRANCH(boolean f) {
		int t;
		t = IMMBYTE();
		if( f )
		{
			PC( PC() + SIGNED(t) );
			change_pc16(PC());	/* TS 971002 */
		}
	}
	
        public static void LBRANCH(boolean f) {
		int t;
		t = IMMWORD();
		if( f )
		{
			konami_ICount[0] -= 1;
			PC( PC() + t&0xFF );
			change_pc16(PC());	/* TS 971002 */
		}
	}
	
        public static int NXORV(){ return ((CC()&CC_N)^((CC()&CC_V)<<2)); }
	
	/* macros for setting/getting registers in TFR/EXG instructions */
	public static int GETREG(int reg){
                int val = 0;
		
                switch(reg) {						
                    case 0: val = A();	break;			
                    case 1: val = B(); 	break; 			
                    case 2: val = X(); 	break;			
                    case 3: val = Y();	break; 			
                    case 4: val = S(); 	break; /* ? */	
                    case 5: val = U();	break;			
                    default: val = 0xff; logerror("Unknown TFR/EXG idx at PC:%04xn", PC() ); break;
                }
                
                return val;
	}
	
	public static void SETREG(int val, int reg){
		switch(reg) {						
                    case 0: A(val);	break;			
                    case 1: B(val);	break;			
                    case 2: X(val); 	break;			
                    case 3: Y(val); 	break;			
                    case 4: S(val); 	break;			/* ? */	
                    case 5: U(val); 	break;			
                    default: logerror("Unknown TFR/EXG idx at PC:%04xn", PC() ); break; 
                }
        }
	
	/* opcode timings */
	static int cycles1[] =
	{
		/*	 0	1  2  3  4	5  6  7  8	9  A  B  C	D  E  F */
	  /*0*/  1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 5, 5, 5, 5,
	  /*1*/  2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
	  /*2*/  2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
	  /*3*/  2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 7, 6,
	  /*4*/  3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 3, 3, 4, 4,
	  /*5*/  4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 1, 1, 1,
	  /*6*/  3, 3, 3, 3, 3, 3, 3, 3, 5, 5, 5, 5, 5, 5, 5, 5,
	  /*7*/  3, 3, 3, 3, 3, 3, 3, 3, 5, 5, 5, 5, 5, 5, 5, 5,
	  /*8*/  2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 5,
	  /*9*/  2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 6,
	  /*A*/  2, 2, 2, 4, 4, 4, 4, 4, 2, 2, 2, 2, 3, 3, 2, 1,
	  /*B*/  3, 2, 2,11,22,11, 2, 4, 3, 3, 3, 3, 3, 3, 3, 3,
	  /*C*/  3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 3, 2,
	  /*D*/  2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	  /*E*/  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	  /*F*/  1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	};
	
	public static int RM16( int Addr )
	{
		int result = RM(Addr) << 8;
		return result | RM((Addr+1)&0xffff);
	}
	
        public static void WM16( int Addr, int p )
	{
		WM( Addr, (p>>8)&0xFF );
		WM( (Addr+1)&0xffff, p&0xFF );
	}
	
	/****************************************************************************
	 * Get all registers in given buffer
	 ****************************************************************************/
	public static Object konami_get_context(Object dst)
	{
		if( dst != null )
			dst = (konami_Regs) konami;
		return (konami_Regs) konami;
	}
	
	/****************************************************************************
	 * Set all registers to given values
	 ****************************************************************************/
	public static void konami_set_context(Object src)
	{
		if( src != null )
			konami = (konami_Regs)src;
	    
                change_pc16(PC());    /* TS 971002 */
	
                CHECK_IRQ_LINES();
	}
	
	/****************************************************************************/
	/* Return a specific register                                               */
	/****************************************************************************/
	public static int konami_get_reg(int regnum)
	{
		switch( regnum )
		{
			case REG_PC:
			case KONAMI_PC: return PC();
			case REG_SP:
			case KONAMI_S: return S();
			case KONAMI_CC: return CC();
			case KONAMI_U: return U();
			case KONAMI_A: return A();
			case KONAMI_B: return B();
			case KONAMI_X: return X();
			case KONAMI_Y: return Y();
			case KONAMI_DP: return DP();
			case KONAMI_NMI_STATE: return konami.nmi_state;
			case KONAMI_IRQ_STATE: return konami.irq_state[KONAMI_IRQ_LINE];
			case KONAMI_FIRQ_STATE: return konami.irq_state[KONAMI_FIRQ_LINE];
			case REG_PREVIOUSPC: return PPC();
			default:
				if( regnum <= REG_SP_CONTENTS )
				{
					int offset = S() + 2 * (REG_SP_CONTENTS - regnum);
					if( offset < 0xffff )
						return ( RM( offset ) << 8 ) | RM( offset + 1 );
				}
		}
		return 0;
	}
	
	
	/****************************************************************************/
	/* Set a specific register                                                  */
	/****************************************************************************/
	public static void konami_set_reg(int regnum, int val)
	{
		switch( regnum )
		{
			case REG_PC:
			case KONAMI_PC: PC( val ); change_pc16(PC()); break;
			case REG_SP:
			case KONAMI_S: S( val ); break;
			case KONAMI_CC: CC( val ); CHECK_IRQ_LINES(); break;
			case KONAMI_U: U( val ); break;
			case KONAMI_A: A( val ); break;
			case KONAMI_B: B( val ); break;
			case KONAMI_X: X( val ); break;
			case KONAMI_Y: Y( val ); break;
			case KONAMI_DP: DP( val ); break;
			case KONAMI_NMI_STATE: konami.nmi_state = val; break;
			case KONAMI_IRQ_STATE: konami.irq_state[KONAMI_IRQ_LINE] = val; break;
			case KONAMI_FIRQ_STATE: konami.irq_state[KONAMI_FIRQ_LINE] = val; break;
			default:
				if( regnum <= REG_SP_CONTENTS )
				{
					int offset = S() + 2 * (REG_SP_CONTENTS - regnum);
					if( offset < 0xffff )
					{
						WM( offset, (val >> 8) & 0xff );
						WM( offset+1, val & 0xff );
					}
				}
	    }
	}
	
	
	/****************************************************************************/
	/* Reset registers to their initial values									*/
	/****************************************************************************/
	public static void konami_init()
	{
            
	}
	
	public static void konami_reset(Object param)
	{
		konami.int_state = 0;
		konami.nmi_state = CLEAR_LINE;
		konami.irq_state[0] = CLEAR_LINE;
		konami.irq_state[0] = CLEAR_LINE;
	
		DPD( 0 );			/* Reset direct page register */
	
                CC( CC() | CC_II );        /* IRQ disabled */
                CC( CC() | CC_IF );        /* FIRQ disabled */
	
		PCD( RM16(0xfffe) );
                change_pc16(PC());    /* TS 971002 */
	}
	
	public static void konami_exit()
	{
		/* just make sure we deinit this, so the next game set its own */
		konami_cpu_setlines_callback = null;
	}
	
	/* Generate interrupts */
	/****************************************************************************
	 * Set IRQ line state
	 ****************************************************************************/
	public static void konami_set_irq_line(int irqline, int state)
	{
		if (irqline == IRQ_LINE_NMI)
		{
			if (konami.nmi_state == state) return;
			konami.nmi_state = state;
/*TODO*///			LOG(("KONAMI#%d set_nmi_line %d\n", cpu_getactivecpu(), state));
			if( state == CLEAR_LINE ) return;
	
			/* if the stack was not yet initialized */
		    if( (konami.int_state & KONAMI_LDS) == 0 ) return;
	
		    konami.int_state &= ~KONAMI_SYNC;
			/* state already saved by CWAI? */
			if(( konami.int_state & KONAMI_CWAI ) != 0)
			{
				konami.int_state &= ~KONAMI_CWAI;
				konami.extra_cycles += 7;	/* subtract +7 cycles next time */
		    }
			else
			{
				CC( CC() | CC_E ); 				/* save entire state */
				PUSHWORD(pPC());
				PUSHWORD(pU());
				PUSHWORD(pY());
				PUSHWORD(pX());
				PUSHBYTE(DP());
				PUSHBYTE(B());
				PUSHBYTE(A());
				PUSHBYTE(CC());
				konami.extra_cycles += 19;	/* subtract +19 cycles next time */
			}
			CC( CC() | CC_IF | CC_II );			/* inhibit FIRQ and IRQ */
			PCD( RM16(0xfffc) );
			change_pc16(PC());					/* TS 971002 */
		}
		else if (irqline < 2)
		{
/*TODO*///		    LOG(("KONAMI#%d set_irq_line %d, %d\n", cpu_getactivecpu(), irqline, state));
			konami.irq_state[irqline] = state;
			if (state == CLEAR_LINE) return;
			CHECK_IRQ_LINES();
		}
	}
	
	/****************************************************************************
	 * Set IRQ vector callback
	 ****************************************************************************/
	public static void konami_set_irq_callback(irqcallbacksPtr callback)
	{
		konami.irq_callback = callback;
	}
/*TODO*///	#if 0
/*TODO*///	/****************************************************************************
/*TODO*///	 * Save CPU state
/*TODO*///	 ****************************************************************************/
/*TODO*///	static void state_save(void *file, const char *module)
/*TODO*///	{
/*TODO*///		int cpu = cpu_getactivecpu();
/*TODO*///		state_save_UINT16(file, module, cpu, "PC", &PC, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "U", &U, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "S", &S, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "X", &X, 1);
/*TODO*///		state_save_UINT16(file, module, cpu, "Y", &Y, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "DP", &DP, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "CC", &CC, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "INT", &konami.int_state, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "NMI", &konami.nmi_state, 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "IRQ", &konami.irq_state[0], 1);
/*TODO*///		state_save_UINT8(file, module, cpu, "FIRQ", &konami.irq_state[1], 1);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/****************************************************************************
/*TODO*///	 * Load CPU state
/*TODO*///	 ****************************************************************************/
/*TODO*///	static void state_load(void *file, const char *module)
/*TODO*///	{
/*TODO*///		int cpu = cpu_getactivecpu();
/*TODO*///		state_load_UINT16(file, module, cpu, "PC", &PC, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "U", &U, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "S", &S, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "X", &X, 1);
/*TODO*///		state_load_UINT16(file, module, cpu, "Y", &Y, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "DP", &DP, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "CC", &CC, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "INT", &konami.int_state, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "NMI", &konami.nmi_state, 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "IRQ", &konami.irq_state[0], 1);
/*TODO*///		state_load_UINT8(file, module, cpu, "FIRQ", &konami.irq_state[1], 1);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void konami_state_save(void *file) { state_save(file, "konami"); }
/*TODO*///	void konami_state_load(void *file) { state_load(file, "konami"); }
/*TODO*///	#endif
/*TODO*///	/****************************************************************************
/*TODO*///	 * Return a formatted string for a register
/*TODO*///	 ****************************************************************************/
/*TODO*///	const char *konami_info(void *context, int regnum)
/*TODO*///	{
/*TODO*///		static char buffer[16][47+1];
/*TODO*///		static int which = 0;
/*TODO*///		konami_Regs *r = context;
/*TODO*///	
/*TODO*///		which = (which+1) % 16;
/*TODO*///	    buffer[which][0] = '\0';
/*TODO*///		if (context == 0)
/*TODO*///			r = &konami;
/*TODO*///	
/*TODO*///		switch( regnum )
/*TODO*///		{
/*TODO*///			case CPU_INFO_NAME: return "KONAMI";
/*TODO*///			case CPU_INFO_FAMILY: return "KONAMI 5000x";
/*TODO*///			case CPU_INFO_VERSION: return "1.0";
/*TODO*///			case CPU_INFO_FILE: return __FILE__;
/*TODO*///			case CPU_INFO_CREDITS: return "Copyright (C) The MAME Team 1999";
/*TODO*///			case CPU_INFO_REG_LAYOUT: return (const char*)konami_reg_layout;
/*TODO*///			case CPU_INFO_WIN_LAYOUT: return (const char*)konami_win_layout;
/*TODO*///	
/*TODO*///			case CPU_INFO_FLAGS:
/*TODO*///				sprintf(buffer[which], "%c%c%c%c%c%c%c%c",
/*TODO*///					r->cc & 0x80 ? 'E':'.',
/*TODO*///					r->cc & 0x40 ? 'F':'.',
/*TODO*///	                r->cc & 0x20 ? 'H':'.',
/*TODO*///	                r->cc & 0x10 ? 'I':'.',
/*TODO*///	                r->cc & 0x08 ? 'N':'.',
/*TODO*///	                r->cc & 0x04 ? 'Z':'.',
/*TODO*///	                r->cc & 0x02 ? 'V':'.',
/*TODO*///	                r->cc & 0x01 ? 'C':'.');
/*TODO*///	            break;
/*TODO*///			case CPU_INFO_REG+KONAMI_PC: sprintf(buffer[which], "PC:%04X", r->pc.w.l); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_S: sprintf(buffer[which], "S:%04X", r->s.w.l); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_CC: sprintf(buffer[which], "CC:%02X", r->cc); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_U: sprintf(buffer[which], "U:%04X", r->u.w.l); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_A: sprintf(buffer[which], "A:%02X", r->d.b.h); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_B: sprintf(buffer[which], "B:%02X", r->d.b.l); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_X: sprintf(buffer[which], "X:%04X", r->x.w.l); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_Y: sprintf(buffer[which], "Y:%04X", r->y.w.l); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_DP: sprintf(buffer[which], "DP:%02X", r->dp.b.h); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_NMI_STATE: sprintf(buffer[which], "NMI:%X", r->nmi_state); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_IRQ_STATE: sprintf(buffer[which], "IRQ:%X", r->irq_state[KONAMI_IRQ_LINE]); break;
/*TODO*///			case CPU_INFO_REG+KONAMI_FIRQ_STATE: sprintf(buffer[which], "FIRQ:%X", r->irq_state[KONAMI_FIRQ_LINE]); break;
/*TODO*///		}
/*TODO*///		return buffer[which];
/*TODO*///	}
/*TODO*///	
/*TODO*///	unsigned konami_dasm(char *buffer, unsigned pc)
/*TODO*///	{
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	    return Dasmknmi(buffer,pc);
/*TODO*///	#else
/*TODO*///		sprintf( buffer, "$%02X", cpu_readop(pc) );
/*TODO*///		return 1;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* includes the static function prototypes and the master opcode table */
/*TODO*///	
/*TODO*///	/* includes the actual opcode implementations */
	
	/* execute instructions on this CPU until icount expires */
	public static int konami_execute(int cycles)
	{
		konami_ICount[0] = cycles - konami.extra_cycles;
		konami.extra_cycles = 0;
	
		if(( konami.int_state & (KONAMI_CWAI | KONAMI_SYNC) ) != 0)
		{
			konami_ICount[0] = 0;
		}
		else
		{
			do
			{
				pPPC( pPC() );
	
/*TODO*///				CALL_MAME_DEBUG;
	
				konami.ireg = ROP(PCD());
				PC(PC() + 1);
	
	            konami_main[konami.ireg].handler();
	
	            konami_ICount[0] -= cycles1[konami.ireg];
	
	        } while( konami_ICount[0] > 0 );
	
	        konami_ICount[0] -= konami.extra_cycles;
		konami.extra_cycles = 0;
	    }
	
		return cycles - konami_ICount[0];
	}

}
