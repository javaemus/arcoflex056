/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mame056.cpu.hd6309;

import static mame056.cpu.hd6309.hd6309H.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrfH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static common.libc.cstdio.*;
import static arcadeflex056.osdepend.*;
import static common.ptr.*;
import static mame056.cpu.hd6309._6309tbl.*;
import static mame056.cpu.hd6309._6309ops.*;

/**
 *
 * @author jagsanchez
 */
public class hd6309 extends cpu_interface {
    
    public hd6309() {
        cpu_num = CPU_M6809;
        num_irqs = 2;
        default_vector = 0;
        icount = hd6309_ICount;
        overclock = 1.00;
        irq_int = HD6309_IRQ_LINE;
        databus_width = 8;
        pgm_memory_base = 0;
        address_shift = 0;
        address_bits = 16;
        endianess = CPU_IS_BE;
        align_unit = 1;
        max_inst_len = 4;
    }

    @Override
    public void init() {
        hd6309_init();
    }

    @Override
    public void reset(Object param) {
        hd6309_reset(param);
    }

    @Override
    public void exit() {
        hd6309_exit();
    }

    @Override
    public int execute(int cycles) {
        return hd6309_execute(cycles);
    }

    @Override
    public Object init_context() {
        Object reg = new hd6309_Regs();
        return reg;
    }

    @Override
    public Object get_context() {
        return hd6309_get_context();
    }

    @Override
    public void set_context(Object reg) {
        hd6309_set_context(reg);
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
        return hd6309_get_reg(regnum);
    }

    @Override
    public void set_reg(int regnum, int val) {
        hd6309_set_reg(regnum, val);
    }

    @Override
    public void set_irq_line(int irqline, int linestate) {
        hd6309_set_irq_line(irqline, linestate);
    }

    @Override
    public void set_irq_callback(irqcallbacksPtr callback) {
        hd6309_set_irq_callback(callback);
    }

    @Override
    public String cpu_info(Object context, int regnum) {
/*TODO*///        static char buffer[16][47+1];
/*TODO*///		static int which = 0;
/*TODO*///		hd6309_Regs *r = context;
/*TODO*///	
/*TODO*///		which = (which+1) % 16;
/*TODO*///		buffer[which][0] = '\0';
/*TODO*///		if (context == 0)
/*TODO*///			r = &hd6309;
	
		switch( regnum )
		{
			case CPU_INFO_NAME: return "HD6309";
			case CPU_INFO_FAMILY: return "Hitachi 6309";
			case CPU_INFO_VERSION: return "1.0";
			case CPU_INFO_FILE: return "hd6309.java";
			case CPU_INFO_CREDITS: return "Copyright (C) John Butler 1997 and Tim Lindner 2000";
/*TODO*///			case CPU_INFO_REG_LAYOUT: return (const char*)hd6309_reg_layout;
/*TODO*///			case CPU_INFO_WIN_LAYOUT: return (const char*)hd6309_win_layout;
	
/*TODO*///			case CPU_INFO_FLAGS:
/*TODO*///				sprintf(buffer[which], "%c%c%c%c%c%c%c%c (MD:%c%c%c%c)",
/*TODO*///					r->cc & 0x80 ? 'E':'.',
/*TODO*///					r->cc & 0x40 ? 'F':'.',
/*TODO*///					r->cc & 0x20 ? 'H':'.',
/*TODO*///					r->cc & 0x10 ? 'I':'.',
/*TODO*///					r->cc & 0x08 ? 'N':'.',
/*TODO*///					r->cc & 0x04 ? 'Z':'.',
/*TODO*///					r->cc & 0x02 ? 'V':'.',
/*TODO*///					r->cc & 0x01 ? 'C':'.',
/*TODO*///	
/*TODO*///					r->md & 0x80 ? 'E':'e',
/*TODO*///					r->md & 0x40 ? 'F':'f',
/*TODO*///					r->md & 0x02 ? 'I':'i',
/*TODO*///					r->md & 0x01 ? 'Z':'z');
/*TODO*///				break;
/*TODO*///			case CPU_INFO_REG+HD6309_PC: sprintf(buffer[which], "PC:%04X", r->pc.w.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_S: sprintf(buffer[which], "S:%04X", r->s.w.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_CC: sprintf(buffer[which], "CC:%02X", r->cc); break;
/*TODO*///			case CPU_INFO_REG+HD6309_MD: sprintf(buffer[which], "MD:%02X", r->md); break;
/*TODO*///			case CPU_INFO_REG+HD6309_U: sprintf(buffer[which], "U:%04X", r->u.w.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_A: sprintf(buffer[which], "A:%02X", r->d.b.h); break;
/*TODO*///			case CPU_INFO_REG+HD6309_B: sprintf(buffer[which], "B:%02X", r->d.b.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_E: sprintf(buffer[which], "E:%02X", r->w.b.h); break;
/*TODO*///			case CPU_INFO_REG+HD6309_F: sprintf(buffer[which], "F:%02X", r->w.b.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_X: sprintf(buffer[which], "X:%04X", r->x.w.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_Y: sprintf(buffer[which], "Y:%04X", r->y.w.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_V: sprintf(buffer[which], "V:%04X", r->v.w.l); break;
/*TODO*///			case CPU_INFO_REG+HD6309_DP: sprintf(buffer[which], "DP:%02X", r->dp.b.h); break;
/*TODO*///			case CPU_INFO_REG+HD6309_NMI_STATE: sprintf(buffer[which], "NMI:%X", r->nmi_state); break;
/*TODO*///			case CPU_INFO_REG+HD6309_IRQ_STATE: sprintf(buffer[which], "IRQ:%X", r->irq_state[HD6309_IRQ_LINE]); break;
/*TODO*///			case CPU_INFO_REG+HD6309_FIRQ_STATE: sprintf(buffer[which], "FIRQ:%X", r->irq_state[HD6309_FIRQ_LINE]); break;
		}
/*TODO*///		return buffer[which];
        throw new UnsupportedOperationException("unsupported hd6309 cpu_info");
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
    
/*TODO*///static static 
/*TODO*///	static UINT8 hd6309_reg_layout[] = {
/*TODO*///		HD6309_A, HD6309_B, HD6309_E, HD6309_F, HD6309_MD, HD6309_CC, HD6309_DP,  -1,
/*TODO*///		HD6309_X, HD6309_Y, HD6309_S, HD6309_U, HD6309_V, -1,
/*TODO*///		HD6309_PC, HD6309_NMI_STATE, HD6309_IRQ_STATE, HD6309_FIRQ_STATE, 0
/*TODO*///	};
/*TODO*///	
/*TODO*///	/* Layout of the debugger windows x,y,w,h */
/*TODO*///	static UINT8 hd6309_win_layout[] = {
/*TODO*///		27, 0,53, 4,	/* register window (top, right rows) */
/*TODO*///		 0, 0,26,22,	/* disassembler window (left colums) */
/*TODO*///		27, 5,53, 8,	/* memory #1 window (right, upper middle) */
/*TODO*///		27,14,53, 8,	/* memory #2 window (right, lower middle) */
/*TODO*///		 0,23,80, 1,	/* command line window (bottom rows) */
/*TODO*///	};


	/* 6309 Registers */
        public static class hd6309_Regs
	{
                public /*PAIR*/ char pc; 		/* Program counter */
		public /*PAIR*/ char ppc;		/* Previous program counter */
                public /*PAIR*/ char d,w;		/* Accumlator d and w (ab = d, ef = w, abef = q) */
                public /*PAIR*/ char dp; 		/* Direct Page register (page in MSB) */
                public /*PAIR*/ char u, s;		/* Stack pointers */
                public /*PAIR*/ char x, y;		/* Index registers */
                public /*PAIR*/ char v;			/* New 6309 register */
                public int /*UINT8*/ cc;
                public int /*UINT8*/ md; 		/* Special mode register */
                public int /*UINT8*/ ireg;		/* First opcode */
                public int[] /*UINT8*/ irq_state = new int[2];
                public int /*int*/ extra_cycles; /* cycles used up by interrupts */
                public irqcallbacksPtr irq_callback;
                public int /*UINT8*/ int_state;	/* SYNC and CWAI flags */
                public int /*UINT8*/ nmi_state;
	} ;
        
        

	/* flag bits in the cc register */
	public static final int CC_C	= 0x01;		/* Carry */
	public static final int CC_V	= 0x02;		/* Overflow */
	public static final int CC_Z	= 0x04;		/* Zero */
	public static final int CC_N	= 0x08;		/* Negative */
	public static final int CC_II	= 0x10;		/* Inhibit IRQ */
	public static final int CC_H	= 0x20;		/* Half (auxiliary) carry */
	public static final int CC_IF	= 0x40;		/* Inhibit FIRQ */
	public static final int CC_E	= 0x80;		/* entire state pushed */

	/* flag bits in the md register */
	public static final int MD_EM	= 0x01;		/* Execution mode */
	public static final int MD_FM	= 0x02;		/* FIRQ mode */
	public static final int MD_II	= 0x40;		/* Illegal instruction */
	public static final int MD_DZ	= 0x80;		/* Division by zero */

	/* 6309 registers */
        public static hd6309_Regs hd6309 = new hd6309_Regs();
        public static int hd6309_slapstic = 0;

	public static int pPPC(){ return hd6309.ppc; }
        public static void pPPC(int val){ hd6309.ppc = (char) val; }
	public static int pPC(){ return hd6309.pc; }
	public static int pU(){ return hd6309.u; }
        public static int pS(){ return hd6309.s; }
        public static int pX(){ return hd6309.x; }
        public static int pY() { return hd6309.y; }
        public static int pV() { return hd6309.v; }
/*TODO*///	/*#define pQ		hd6309.q*/
        public static int pD() { return hd6309.d; }
        public static int pW() { return hd6309.w; }
/*TODO*///        public static int pZ() { return hd6309.z; }
	
	public static int PPC() { return hd6309.ppc & 0xff; }
	public static int PC() { return hd6309.pc & 0xff; }
        public static void PC(int val) { hd6309.pc = (char) val; }
	public static int PCD() { return hd6309.pc; }
        public static void PCD(int value) { hd6309.pc = (char) value; }
	public static int U() { return hd6309.u & 0xff; }
        public static void U(int val) { hd6309.u = (char) val; }
	public static int UD() { return hd6309.u; }
	public static int S() { return hd6309.s & 0xff; }
        public static void S(int val) { hd6309.s = (char) val; }
	public static int SD() { return hd6309.s; }
	public static int X() { return hd6309.x & 0xff; }
        public static void X(int val) { hd6309.x = (char) val; }
	public static int XD() { return hd6309.x; }
	public static int Y() { return hd6309.y & 0xff; }
        public static void Y(int val) { hd6309.y = (char) val; }
	public static int YD() { return hd6309.y; }
	public static int V() { return hd6309.v & 0xff; }
        public static void V(int val) { hd6309.v = (char) val; }
	public static int VD() { return hd6309.v; }
	public static int D() { return hd6309.d & 0xff; }
        public static void D(int val) { hd6309.d = (char) (val & 0xff); }
	public static int A() { return hd6309.d >> 8; }
        public static void A(int val) { hd6309.d = (char) (val<<8); }
	public static int B() { return hd6309.d & 0xff; }
        public static void B(int val) { hd6309.d = (char) val; }
	public static int W() { return hd6309.w & 0xff; }
        public static void W(int val) { hd6309.w = (char) (val & 0xff); }
	public static int E() { return hd6309.w >> 8; }
        public static void E(int val) { hd6309.w = (char) (val<<8); }
	public static int F() { return hd6309.w & 0xff; }
        public static void F(int val) { hd6309.w = (char) val; }
	public static int DP(){ return hd6309.dp >> 8; }
        public static void DP(int val) { hd6309.dp = (char) (val<<8); }
        public static void DPD(int value){ hd6309.dp = (char) value; }
        public static int DPD(){ return hd6309.dp; }
        public static void CC(int value){ hd6309.cc = value; }
        public static int CC(){ return hd6309.cc; }
        
        public static void MD(int value){ hd6309.md = value; }
        public static int MD(){ return hd6309.md; }

	static int ea; 		/* effective address */
        public static int EA(){	return ea>>8; }
        public static int EAD(){ return ea; }
        public static void EAD(int val){ ea = val; }

        public static void CHANGE_PC(){
            change_pc16(PCD());
        }
        
/*TODO*///	#if 0
/*TODO*///	public static void CHANGE_PC()	{
/*TODO*///		if( hd6309_slapstic != 0 )
/*TODO*///			cpu_setOPbase16.handler(hd6309.pc & 0xFFFF);
/*TODO*///		else
/*TODO*///			change_pc16(hd6309.pc);		
/*TODO*///        }

        public static final int HD6309_CWAI 	= 8;	/* set when CWAI is waiting for an interrupt */
	public static final int HD6309_SYNC 	= 16;	/* set when SYNC is waiting for an interrupt */
        public static final int HD6309_LDS	= 32;	/* set when LDS occured at least once */

	/* public globals */
	public static int[] hd6309_ICount={50000};

	/* these are re-defined in hd6309.h TO RAM, ROM or functions in cpuintrf.c */
        public static int RM(int mAddr)
        {
            return HD6309_RDMEM(mAddr);
        }
        
	public static void WM(int mAddr, int Value)
        {
            HD6309_WRMEM(mAddr, Value);
        }
        
	public static int ROP(int mAddr) { return HD6309_RDOP(mAddr); }
	public static int ROP_ARG(int mAddr) {	return HD6309_RDOP_ARG(mAddr); }
	
	/* macros to access memory */
	public static int IMMBYTE(int b){ b = ROP_ARG(PCD()); PC(PC()+1) ; return b; }
	public static int IMMWORD(int w){ w = (ROP_ARG(PCD())<<8) | ROP_ARG((PCD()+1)&0xffff); PC( PC()+2 ); return w; }
/*TODO*///	#define IMMLONG(w)	w.d = (ROP_ARG(PCD)<<24) + (ROP_ARG(PCD+1)<<16) + (ROP_ARG(PCD+2)<<8) + (ROP_ARG(PCD+3)); PC+=4

	public static void PUSHBYTE(int b)
        {
            hd6309.s = (char) (hd6309.s - 1);
            WM(hd6309.s, b);
        }
        
	public static void PUSHWORD(int w)
        {
            hd6309.s = (char) (hd6309.s - 1);            
            WM(hd6309.s, w & 0xFF); 
            hd6309.s = (char) (hd6309.s - 1); 
            WM(hd6309.s,w >>> 8);
        }
/*TODO*///	#define PULLBYTE(b) b = RM(SD); S++
/*TODO*///	#define PULLWORD(w) w = RM(SD)<<8; S++; w |= RM(SD); S++
/*TODO*///	
/*TODO*///	#define PSHUBYTE(b) --U; WM(UD,b);
/*TODO*///	#define PSHUWORD(w) --U; WM(UD,w.b.l); --U; WM(UD,w.b.h)
/*TODO*///	#define PULUBYTE(b) b = RM(UD); U++
/*TODO*///	#define PULUWORD(w) w = RM(UD)<<8; U++; w |= RM(UD); U++
/*TODO*///	
	public static void CLR_HNZVC(){	CC( CC() & ~(CC_H|CC_N|CC_Z|CC_V|CC_C)); }
	public static void CLR_NZV(){ CC( CC() & ~(CC_N|CC_Z|CC_V)); }
/*TODO*///	#define CLR_HNZC	CC&=~(CC_H|CC_N|CC_Z|CC_C)
	public static void CLR_NZVC(){	CC( CC() & ~(CC_N|CC_Z|CC_V|CC_C)); }
/*TODO*///	#define CLR_Z		CC&=~(CC_Z)
/*TODO*///	#define CLR_N		CC&=~(CC_N)
	public static void CLR_NZC(){ CC( CC()& ~(CC_N|CC_Z|CC_C) ); }
/*TODO*///	#define CLR_ZC		CC&=~(CC_Z|CC_C)

/*TODO*///	/* macros for CC -- CC bits affected should be reset before calling */
	public static void SET_Z(int a){ if (a == 0) SEZ(); }
	public static void SET_Z8(int a){ SET_Z(a); }
	public static void SET_Z16(int a){ SET_Z(a); }
        
        public static int GET_Z() { 
            if ((CC()&CC_Z) != 0)
                return 1;
            else
                return 0;
        }
        
	public static void SET_N8(int a){ CC( CC() |((a&0x80)>>4)); }
	public static void SET_N16(int a){ CC(CC() | ((a&0x8000)>>12)); }
/*TODO*///	#define SET_N32(a)		CC|=((a&0x8000)>>20)
/*TODO*///	#define SET_H(a,b,r)	CC|=(((a^b^r)&0x10)<<1)
	public static void SET_C8(int a){ CC( CC() | ((a&0x100)>>8)); }
	public static void SET_C16(int a){ CC(CC()|((a&0x10000)>>16)); }
	public static void SET_V8(int a, int b, int r){	CC( CC() | (((a^b^r^(r>>1))&0x80)>>6)); }
	public static void SET_V16(int a, int b, int r){ CC(CC()|(((a^b^r^(r>>1))&0x8000)>>14)); }

	public static void SET_FLAGS8I(int a){ CC( CC() | flags8i[(a)&0xff]); }
	public static void SET_FLAGS8D(int a){ CC( CC() | flags8d[(a)&0xff]);}
	
	static int[] cycle_counts_page0;
	static int[] cycle_counts_page01;
        static int[] cycle_counts_page11;
        static int[] index_cycle;

	/* combos */
	public static void SET_NZ8(int a){ SET_N8(a); SET_Z(a); }
	public static void SET_NZ16(int a) { SET_N16(a); SET_Z(a); }
	public static void SET_FLAGS8(int a, int b, int r){ SET_N8(r);SET_Z8(r);SET_V8(a,b,r);SET_C8(r); }
	public static void SET_FLAGS16(int a, int b, int r){ SET_N16(r);SET_Z16(r);SET_V16(a,b,r);SET_C16(r); }
	
	public static int NXORV(){ return ((CC()&CC_N)^((CC()&CC_V)<<2)); }

	/* for treating an unsigned byte as a signed word */
	public static int SIGNED(int b){ return (((b&0x80)!=0?b|0xff00:b)); }
	/* for treating an unsigned short as a signed long */
	public static int SIGNED_16(int b){ return (((b&0x8000)!=0?b|0xffff0000:b)); }

	/* macros for addressing modes (postbytes have their own code) */
	public static void DIRECT(){ EAD( DPD() ); IMMBYTE(ea & 0xff); }
/*TODO*///	#define IMM8	EAD = PCD; PC++
/*TODO*///	#define IMM16	EAD = PCD; PC+=2
/*TODO*///	#define EXTENDED IMMWORD(ea)

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
/*TODO*///	/* Macros to set mode flags */
	public static void SEDZ(){  MD( MD() | MD_DZ ); }
/*TODO*///	#define CLDZ MD&=~MD_DZ
	public static void SEII(){  MD( MD() | MD_II ) ; }
/*TODO*///	#define CLII MD&=~MD_II
/*TODO*///	#define SEFM MD|=MD_FM
/*TODO*///	#define CLFM MD&=~MD_FM
/*TODO*///	#define SEEM MD|=MD_EM
/*TODO*///	#define CLEM MD&=~MD_EM
/*TODO*///	
/*TODO*///	/* macros for convenience */
	public static int DIRBYTE(int b){ DIRECT();b=RM(EAD()); return b; }
/*TODO*///	#define DIRWORD(w) {DIRECT;w.d=RM16(EAD);}
/*TODO*///	#define DIRLONG(lng) {DIRECT;lng.w.h=RM16(EAD);lng.w.l=RM16(EAD+2);}
/*TODO*///	#define EXTBYTE(b) {EXTENDED;b=RM(EAD);}
/*TODO*///	#define EXTWORD(w) {EXTENDED;w.d=RM16(EAD);}
/*TODO*///	#define EXTLONG(lng) {EXTENDED;lng.w.h=RM16(EAD);lng.w.l=RM16(EAD+2);}
	
	/* includes the static function prototypes and other tables */
	
	/* macros for branch instructions */
	public static void BRANCH(int f) {
		int t=0;
		t = IMMBYTE(t);
		if( f!=0 )
		{
			PC( PC() + SIGNED(t) );
			CHANGE_PC();
		}
	}
	
	public static void LBRANCH(int f) {
		int t=0;
		t = IMMWORD(t);
		if( f!=0 )
		{
			hd6309_ICount[0] -= 1;
			PC( PC() + (t&0xff) );
			CHANGE_PC();
		}
	}
	
	public static int RM16( int mAddr )
	{
		int result = RM(mAddr) << 8;
		return result | RM((mAddr+1)&0xffff);
	}
	
/*TODO*///	INLINE UINT32 RM32( UINT32 mAddr );
/*TODO*///	INLINE UINT32 RM32( UINT32 mAddr )
/*TODO*///	{
/*TODO*///		UINT32 result = RM(mAddr) << 24;
/*TODO*///		result += RM(mAddr+1) << 16;
/*TODO*///		result += RM(mAddr+2) << 8;
/*TODO*///		result += RM(mAddr+3);
/*TODO*///		return result;
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE void WM16( UINT32 mAddr, PAIR *p );
/*TODO*///	INLINE void WM16( UINT32 mAddr, PAIR *p )
/*TODO*///	{
/*TODO*///		WM( mAddr, p->b.h );
/*TODO*///		WM( (mAddr+1)&0xffff, p->b.l );
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE void WM32( UINT32 mAddr, PAIR *p );
/*TODO*///	INLINE void WM32( UINT32 mAddr, PAIR *p )
/*TODO*///	{
/*TODO*///		WM( mAddr, p->b.h3 );
/*TODO*///		WM( (mAddr+1)&0xffff, p->b.h2 );
/*TODO*///		WM( (mAddr+2)&0xffff, p->b.h );
/*TODO*///		WM( (mAddr+3)&0xffff, p->b.l );
/*TODO*///	}
	
	public static void UpdateState()
	{
		if (( hd6309.md & MD_EM ) != 0)
		{
			cycle_counts_page0  = ccounts_page0_na;
			cycle_counts_page01 = ccounts_page01_na;
			cycle_counts_page11 = ccounts_page11_na;
			index_cycle         = index_cycle_na;
		}
		else
		{
			cycle_counts_page0  = ccounts_page0_em;
			cycle_counts_page01 = ccounts_page01_em;
			cycle_counts_page11 = ccounts_page11_em;
			index_cycle         = index_cycle_em;
		}
	}
	
	public static void CHECK_IRQ_LINES()
	{
		if( hd6309.irq_state[HD6309_IRQ_LINE] != CLEAR_LINE ||
			hd6309.irq_state[HD6309_FIRQ_LINE] != CLEAR_LINE )
			hd6309.int_state &= ~HD6309_SYNC; /* clear SYNC flag */
		if( hd6309.irq_state[HD6309_FIRQ_LINE]!=CLEAR_LINE && (hd6309.cc & CC_IF)==0)
		{
			/* fast IRQ */
			/* HJB 990225: state already saved by CWAI? */
			if(( hd6309.int_state & HD6309_CWAI ) != 0)
			{
				hd6309.int_state &= ~HD6309_CWAI;
				hd6309.extra_cycles += 7;		 /* subtract +7 cycles */
			}
			else
			{
				if (( hd6309.md & MD_FM ) != 0)
				{
					hd6309.cc |= CC_E; 				/* save entire state */
					PUSHWORD(hd6309.pc);
					PUSHWORD(hd6309.u);
					PUSHWORD(hd6309.y);
					PUSHWORD(hd6309.x);
					PUSHBYTE(DP());
					if (( hd6309.md & MD_EM ) != 0)
					{
						PUSHBYTE(F());
						PUSHBYTE(E());
						hd6309.extra_cycles += 2; /* subtract +2 cycles */
					}
					PUSHBYTE(B());
					PUSHBYTE(A());
					PUSHBYTE(hd6309.cc);
					hd6309.extra_cycles += 19;	 /* subtract +19 cycles */
				}
				else
				{
					hd6309.cc &= ~CC_E;				/* save 'short' state */
					PUSHWORD(hd6309.pc);
					PUSHBYTE(hd6309.cc);
					hd6309.extra_cycles += 10;	/* subtract +10 cycles */
				}
			}
			hd6309.cc |= CC_IF | CC_II;			/* inhibit FIRQ and IRQ */
			hd6309.pc = (char) RM16(0xfff6);
			CHANGE_PC();
			hd6309.irq_callback.handler(HD6309_FIRQ_LINE);
		}
		else
		if( hd6309.irq_state[HD6309_IRQ_LINE]!=CLEAR_LINE && (hd6309.cc & CC_II)==0 )
		{
			/* standard IRQ */
			/* HJB 990225: state already saved by CWAI? */
			if(( hd6309.int_state & HD6309_CWAI ) != 0)
			{
				hd6309.int_state &= ~HD6309_CWAI;  /* clear CWAI flag */
				hd6309.extra_cycles += 7;		 /* subtract +7 cycles */
			}
			else
			{
				hd6309.cc |= CC_E; 				/* save entire state */
				PUSHWORD(hd6309.pc);
				PUSHWORD(hd6309.u);
				PUSHWORD(hd6309.y);
				PUSHWORD(hd6309.x);
				PUSHBYTE(DP());
				if (( hd6309.md & MD_EM ) != 0)
				{
					PUSHBYTE(F());
					PUSHBYTE(E());
					hd6309.extra_cycles += 2; /* subtract +2 cycles */
				}
				PUSHBYTE(B());
				PUSHBYTE(A());
				PUSHBYTE(hd6309.cc);
				hd6309.extra_cycles += 19;	 /* subtract +19 cycles */
			}
			hd6309.cc |= CC_II;					/* inhibit IRQ */
			hd6309.pc = (char) RM16(0xfff8);
			CHANGE_PC();
			hd6309.irq_callback.handler(HD6309_IRQ_LINE);
		}
	}
	
	/****************************************************************************
	 * Get all registers in given buffer
	 ****************************************************************************/
	public static Object hd6309_get_context()
	{
/*TODO*///		if( dst )
/*TODO*///			*(hd6309_Regs*)dst = hd6309;
/*TODO*///		return sizeof(hd6309_Regs);
            
            hd6309_Regs regs = new hd6309_Regs();
            regs.pc = hd6309.pc;
            regs.ppc = hd6309.ppc;
            regs.d = hd6309.d;
            regs.w = hd6309.w;
            regs.dp = hd6309.dp;
            regs.u = hd6309.u;
            regs.s = hd6309.s;
            regs.x = hd6309.x;
            regs.y = hd6309.y;
            regs.v = hd6309.v;
            regs.cc = hd6309.cc;
            regs.md = hd6309.md;
            regs.ireg = hd6309.ireg;
            regs.irq_state = hd6309.irq_state;
            regs.extra_cycles = hd6309.extra_cycles;
            regs.irq_callback = hd6309.irq_callback;
            regs.int_state = hd6309.int_state;
            regs.nmi_state = hd6309.nmi_state;
                
            return regs;
        
	}
	
	/****************************************************************************
	 * Set all registers to given values
	 ****************************************************************************/
	public static void hd6309_set_context(Object reg)
	{
/*TODO*///		if( src )
/*TODO*///			hd6309 = *(hd6309_Regs*)src;

            hd6309_Regs regs = (hd6309_Regs) reg;
            
            regs.pc = hd6309.pc;
            regs.ppc = hd6309.ppc;
            regs.d = hd6309.d;
            regs.w = hd6309.w;
            regs.dp = hd6309.dp;
            regs.u = hd6309.u;
            regs.s = hd6309.s;
            regs.x = hd6309.x;
            regs.y = hd6309.y;
            regs.v = hd6309.v;
            regs.cc = hd6309.cc;
            regs.md = hd6309.md;
            regs.ireg = hd6309.ireg;
            regs.irq_state = hd6309.irq_state;
            regs.extra_cycles = hd6309.extra_cycles;
            regs.irq_callback = hd6309.irq_callback;
            regs.int_state = hd6309.int_state;
            regs.nmi_state = hd6309.nmi_state;
            
            CHANGE_PC();
            CHECK_IRQ_LINES();

            UpdateState();
            
	}
	
	
	/****************************************************************************/
	/* Return a specific register												*/
	/****************************************************************************/
	public static int hd6309_get_reg(int regnum)
	{
		switch( regnum )
		{
			case REG_PC:
			case HD6309_PC: return PC();
			case REG_SP:
			case HD6309_S: return S();
			case HD6309_CC: return CC();
			case HD6309_MD: return MD();
			case HD6309_U: return U();
			case HD6309_A: return A();
			case HD6309_B: return B();
			case HD6309_E: return E();
			case HD6309_F: return F();
			case HD6309_X: return X();
			case HD6309_Y: return Y();
			case HD6309_V: return V();
			case HD6309_DP: return DP();
			case HD6309_NMI_STATE: return hd6309.nmi_state;
			case HD6309_IRQ_STATE: return hd6309.irq_state[HD6309_IRQ_LINE];
			case HD6309_FIRQ_STATE: return hd6309.irq_state[HD6309_FIRQ_LINE];
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
	/* Set a specific register													*/
	/****************************************************************************/
	public static void hd6309_set_reg(int regnum, int val)
	{
		switch( regnum )
		{
			case REG_PC:
			case HD6309_PC: PC( val ); CHANGE_PC(); break;
			case REG_SP:
			case HD6309_S: S( val ); break;
			case HD6309_CC: CC ( val ); CHECK_IRQ_LINES(); break;
			case HD6309_MD: 
                            MD( val ); 
                            UpdateState(); 
                            break;
			case HD6309_U: U( val ); break;
			case HD6309_A: A( val ); break;
			case HD6309_B: B( val ); break;
			case HD6309_E: E( val ); break;
			case HD6309_F: F( val ); break;
			case HD6309_X: X( val ); break;
			case HD6309_Y: Y( val ); break;
			case HD6309_V: V( val ); break;
			case HD6309_DP: DP( val ); break;
			case HD6309_NMI_STATE: hd6309.nmi_state = val; break;
			case HD6309_IRQ_STATE: hd6309.irq_state[HD6309_IRQ_LINE] = val; break;
			case HD6309_FIRQ_STATE: hd6309.irq_state[HD6309_FIRQ_LINE] = val; break;
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
	
	public static void hd6309_init()
	{
/*TODO*///		int cpu = cpu_getactivecpu();
/*TODO*///		state_save_register_UINT16("hd6309", cpu, "PC", &PC, 1);
/*TODO*///		state_save_register_UINT16("hd6309", cpu, "U", &U, 1);
/*TODO*///		state_save_register_UINT16("hd6309", cpu, "S", &S, 1);
/*TODO*///		state_save_register_UINT16("hd6309", cpu, "X", &X, 1);
/*TODO*///		state_save_register_UINT16("hd6309", cpu, "Y", &Y, 1);
/*TODO*///		state_save_register_UINT16("hd6309", cpu, "V", &V, 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "DP", &DP, 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "CC", &CC, 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "MD", &MD, 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "INT", &hd6309.int_state, 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "NMI", &hd6309.nmi_state, 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "IRQ", &hd6309.irq_state[0], 1);
/*TODO*///		state_save_register_UINT8("hd6309", cpu, "FIRQ", &hd6309.irq_state[1], 1);
	}

	/****************************************************************************/
	/* Reset registers to their initial values									*/
	/****************************************************************************/
	public static void hd6309_reset(Object param)
	{
		hd6309.int_state = 0;
		hd6309.nmi_state = CLEAR_LINE;
		hd6309.irq_state[0] = CLEAR_LINE;
		hd6309.irq_state[0] = CLEAR_LINE;
	
		DPD( 0 );			/* Reset direct page register */
	
		MD( 0 ); 			/* Mode register gets reset */
		CC( CC() | CC_II );		/* IRQ disabled */
		CC( CC() | CC_IF );		/* FIRQ disabled */
	
		PCD( RM16(0xfffe) );
		CHANGE_PC();
		UpdateState();
	}
	
	public static void hd6309_exit()
	{
		/* nothing to do ? */
	}
	
	/* Generate interrupts */
	/****************************************************************************
	 * Set IRQ line state
	 ****************************************************************************/
	public static void hd6309_set_irq_line(int irqline, int state)
	{
		if (irqline == IRQ_LINE_NMI)
		{
			if (hd6309.nmi_state == state) return;
			hd6309.nmi_state = state;
/*TODO*///			LOG(("HD6309#%d set_irq_line (NMI) %d\n", cpu_getactivecpu(), state));
			if( state == CLEAR_LINE ) return;
	
			/* if the stack was not yet initialized */
			if( (hd6309.int_state & HD6309_LDS) == 0 ) return;
	
			hd6309.int_state &= ~HD6309_SYNC;
			/* HJB 990225: state already saved by CWAI? */
			if(( hd6309.int_state & HD6309_CWAI ) != 0)
			{
				hd6309.int_state &= ~HD6309_CWAI;
				hd6309.extra_cycles += 7;	/* subtract +7 cycles next time */
			}
			else
			{
				CC( CC() | CC_E ); 				/* save entire state */
				PUSHWORD(pPC());
				PUSHWORD(pU());
				PUSHWORD(pY());
				PUSHWORD(pX());
				PUSHBYTE(DP());
				if (( MD() & MD_EM ) != 0)
				{
					PUSHBYTE(F());
					PUSHBYTE(E());
					hd6309.extra_cycles += 2; /* subtract +2 cycles */
				}
	
				PUSHBYTE(B());
				PUSHBYTE(A());
				PUSHBYTE(CC());
				hd6309.extra_cycles += 19;	/* subtract +19 cycles next time */
			}
			CC( CC() | CC_IF | CC_II );			/* inhibit FIRQ and IRQ */
			PCD( RM16(0xfffc) );
			CHANGE_PC();
		}
		else if (irqline < 2)
		{
/*TODO*///			LOG(("HD6309#%d set_irq_line %d, %d\n", cpu_getactivecpu(), irqline, state));
			hd6309.irq_state[irqline] = state;
			if (state == CLEAR_LINE) return;
			CHECK_IRQ_LINES();
		}
	}
	
	/****************************************************************************
	 * Set IRQ vector callback
	 ****************************************************************************/
	public static void hd6309_set_irq_callback(irqcallbacksPtr callback)
	{
		hd6309.irq_callback = callback;
	}
	
	/* includes the actual opcode implementations */
	
	/* execute instructions on this CPU until icount expires */
	public static int hd6309_execute(int cycles)	/* NS 970908 */
	{
		hd6309_ICount[0] = cycles - hd6309.extra_cycles;
		hd6309.extra_cycles = 0;
	
		if ((hd6309.int_state & (HD6309_CWAI | HD6309_SYNC)) != 0)
		{
			hd6309_ICount[0] = 0;
		}
		else
		{
			do
			{
				pPPC( pPC() );
	
/*TODO*///				CALL_MAME_DEBUG;
	
				hd6309.ireg = ROP(PCD());
				PC(PC() + 1);
	
				switch( hd6309.ireg )
				{
                                    case 0x00: neg_di();   				break;
                                    case 0x01: oim_di();   				break;
                                    case 0x02: aim_di();   				break;
                                    case 0x03: com_di();   				break;
                                    case 0x04: lsr_di();   				break;
                                    case 0x05: eim_di();   				break;
                                    case 0x06: ror_di();   				break;
                                    case 0x07: asr_di();   				break;
                                    case 0x08: asl_di();   				break;
                                    case 0x09: rol_di();   				break;
                                    case 0x0a: dec_di();   				break;
                                    case 0x0b: tim_di();   				break;
                                    case 0x0c: inc_di();   				break;
                                    case 0x0d: tst_di();   				break;
                                    case 0x0e: jmp_di();   				break;
                                    case 0x0f: clr_di();   				break;
                                    case 0x10: pref10();				break;
                                    case 0x11: pref11();				break;
                                    case 0x12: nop();	   				break;
                                    case 0x13: sync();	   				break;
                                    case 0x14: sexw();	   				break;
                                    case 0x15: IIError();				break;
                                    case 0x16: lbra();	   				break;
                                    case 0x17: lbsr();	   				break;
                                    case 0x18: IIError();				break;
                                    case 0x19: daa();	   				break;
                                    case 0x1a: orcc();	   				break;
                                    case 0x1b: IIError();				break;
                                    case 0x1c: andcc();    				break;
                                    case 0x1d: sex();	   				break;
                                    case 0x1e: exg();	   				break;
                                    case 0x1f: tfr();	   				break;
                                    case 0x20: bra();	   				break;
                                    case 0x21: brn();	   				break;
                                    case 0x22: bhi();	   				break;
                                    case 0x23: bls();	   				break;
                                    case 0x24: bcc();	   				break;
                                    case 0x25: bcs();	   				break;
                                    case 0x26: bne();	   				break;
                                    case 0x27: beq();	   				break;
                                    case 0x28: bvc();	   				break;
                                    case 0x29: bvs();	   				break;
                                    case 0x2a: bpl();	   				break;
                                    case 0x2b: bmi();	   				break;
                                    case 0x2c: bge();	   				break;
                                    case 0x2d: blt();	   				break;
                                    case 0x2e: bgt();	   				break;
                                    case 0x2f: ble();	   				break;
                                    case 0x30: leax();	   				break;
                                    case 0x31: leay();	   				break;
                                    case 0x32: leas();	   				break;
                                    case 0x33: leau();	   				break;
                                    case 0x34: pshs();	   				break;
                                    case 0x35: puls();	   				break;
                                    case 0x36: pshu();	   				break;
                                    case 0x37: pulu();	   				break;
                                    case 0x38: IIError();				break;
                                    case 0x39: rts();	   				break;
                                    case 0x3a: abx();	   				break;
                                    case 0x3b: rti();	   				break;
                                    case 0x3c: cwai();					break;
                                    case 0x3d: mul();					break;
                                    case 0x3e: IIError();				break;
                                    case 0x3f: swi();					break;
                                    case 0x40: nega();	   				break;
                                    case 0x41: IIError();				break;
                                    case 0x42: IIError();				break;
                                    case 0x43: coma();	   				break;
                                    case 0x44: lsra();	   				break;
                                    case 0x45: IIError();				break;
                                    case 0x46: rora();	   				break;
                                    case 0x47: asra();	   				break;
                                    case 0x48: asla();	   				break;
                                    case 0x49: rola();	   				break;
                                    case 0x4a: deca();	   				break;
                                    case 0x4b: IIError();				break;
                                    case 0x4c: inca();	   				break;
                                    case 0x4d: tsta();	   				break;
                                    case 0x4e: IIError();				break;
                                    case 0x4f: clra();	   				break;
                                    case 0x50: negb();	   				break;
                                    case 0x51: IIError();				break;
                                    case 0x52: IIError();				break;
                                    case 0x53: comb();	   				break;
                                    case 0x54: lsrb();	   				break;
                                    case 0x55: IIError();				break;
                                    case 0x56: rorb();	   				break;
                                    case 0x57: asrb();	   				break;
                                    case 0x58: aslb();	   				break;
                                    case 0x59: rolb();	   				break;
                                    case 0x5a: decb();	   				break;
                                    case 0x5b: IIError();				break;
                                    case 0x5c: incb();	   				break;
                                    case 0x5d: tstb();	   				break;
                                    case 0x5e: IIError();				break;
                                    case 0x5f: clrb();	   				break;
                                    case 0x60: neg_ix();   				break;
                                    case 0x61: oim_ix();   				break;
                                    case 0x62: aim_ix();   				break;
                                    case 0x63: com_ix();   				break;
                                    case 0x64: lsr_ix();   				break;
                                    case 0x65: eim_ix();   				break;
                                    case 0x66: ror_ix();   				break;
                                    case 0x67: asr_ix();   				break;
                                    case 0x68: asl_ix();   				break;
                                    case 0x69: rol_ix();   				break;
                                    case 0x6a: dec_ix();   				break;
                                    case 0x6b: tim_ix();   				break;
                                    case 0x6c: inc_ix();   				break;
                                    case 0x6d: tst_ix();   				break;
                                    case 0x6e: jmp_ix();   				break;
                                    case 0x6f: clr_ix();   				break;
                                    case 0x70: neg_ex();   				break;
                                    case 0x71: oim_ex();   				break;
                                    case 0x72: aim_ex();   				break;
                                    case 0x73: com_ex();   				break;
                                    case 0x74: lsr_ex();   				break;
                                    case 0x75: eim_ex();   				break;
                                    case 0x76: ror_ex();   				break;
                                    case 0x77: asr_ex();   				break;
                                    case 0x78: asl_ex();   				break;
                                    case 0x79: rol_ex();   				break;
                                    case 0x7a: dec_ex();   				break;
                                    case 0x7b: tim_ex();   				break;
                                    case 0x7c: inc_ex();   				break;
                                    case 0x7d: tst_ex();   				break;
                                    case 0x7e: jmp_ex();   				break;
                                    case 0x7f: clr_ex();   				break;
                                    case 0x80: suba_im();  				break;
                                    case 0x81: cmpa_im();  				break;
                                    case 0x82: sbca_im();  				break;
                                    case 0x83: subd_im();  				break;
                                    case 0x84: anda_im();  				break;
                                    case 0x85: bita_im();  				break;
                                    case 0x86: lda_im();   				break;
                                    case 0x87: IIError(); 				break;
                                    case 0x88: eora_im();  				break;
                                    case 0x89: adca_im();  				break;
                                    case 0x8a: ora_im();   				break;
                                    case 0x8b: adda_im();  				break;
                                    case 0x8c: cmpx_im();  				break;
                                    case 0x8d: bsr();	   			break;
                                    case 0x8e: ldx_im();   				break;
                                    case 0x8f: IIError();  				break;
                                    case 0x90: suba_di();  				break;
                                    case 0x91: cmpa_di();  				break;
                                    case 0x92: sbca_di();  				break;
                                    case 0x93: subd_di();  				break;
                                    case 0x94: anda_di();  				break;
                                    case 0x95: bita_di();  				break;
                                    case 0x96: lda_di();   				break;
                                    case 0x97: sta_di();   				break;
                                    case 0x98: eora_di();  				break;
                                    case 0x99: adca_di();  				break;
                                    case 0x9a: ora_di();   				break;
                                    case 0x9b: adda_di();  				break;
                                    case 0x9c: cmpx_di();  				break;
                                    case 0x9d: jsr_di();   				break;
                                    case 0x9e: ldx_di();   				break;
                                    case 0x9f: stx_di();   				break;
                                    case 0xa0: suba_ix();  				break;
                                    case 0xa1: cmpa_ix();  				break;
                                    case 0xa2: sbca_ix();  				break;
                                    case 0xa3: subd_ix();  				break;
                                    case 0xa4: anda_ix();  				break;
                                    case 0xa5: bita_ix();  				break;
                                    case 0xa6: lda_ix();   				break;
                                    case 0xa7: sta_ix();   				break;
                                    case 0xa8: eora_ix();  				break;
                                    case 0xa9: adca_ix();  				break;
                                    case 0xaa: ora_ix();   				break;
                                    case 0xab: adda_ix();  				break;
                                    case 0xac: cmpx_ix();  				break;
                                    case 0xad: jsr_ix();   				break;
                                    case 0xae: ldx_ix();   				break;
                                    case 0xaf: stx_ix();   				break;
                                    case 0xb0: suba_ex();  				break;
                                    case 0xb1: cmpa_ex();  				break;
                                    case 0xb2: sbca_ex();  				break;
                                    case 0xb3: subd_ex();  				break;
                                    case 0xb4: anda_ex();  				break;
                                    case 0xb5: bita_ex();  				break;
                                    case 0xb6: lda_ex();   				break;
                                    case 0xb7: sta_ex();   				break;
                                    case 0xb8: eora_ex();  				break;
                                    case 0xb9: adca_ex();  				break;
                                    case 0xba: ora_ex();   				break;
                                    case 0xbb: adda_ex();  				break;
                                    case 0xbc: cmpx_ex();  				break;
                                    case 0xbd: jsr_ex();   				break;
                                    case 0xbe: ldx_ex();   				break;
                                    case 0xbf: stx_ex();   				break;
                                    case 0xc0: subb_im();  				break;
                                    case 0xc1: cmpb_im();  				break;
                                    case 0xc2: sbcb_im();  				break;
                                    case 0xc3: addd_im();  				break;
                                    case 0xc4: andb_im();  				break;
                                    case 0xc5: bitb_im();  				break;
                                    case 0xc6: ldb_im();   				break;
                                    case 0xc7: IIError(); 				break;
                                    case 0xc8: eorb_im();  				break;
                                    case 0xc9: adcb_im();  				break;
                                    case 0xca: orb_im();   				break;
                                    case 0xcb: addb_im();  				break;
                                    case 0xcc: ldd_im();   				break;
                                    case 0xcd: ldq_im();   				break; /* in m6809 was std_im */
                                    case 0xce: ldu_im();   				break;
                                    case 0xcf: IIError();  				break;
                                    case 0xd0: subb_di();  				break;
                                    case 0xd1: cmpb_di();  				break;
                                    case 0xd2: sbcb_di();  				break;
                                    case 0xd3: addd_di();  				break;
                                    case 0xd4: andb_di();  				break;
                                    case 0xd5: bitb_di();  				break;
                                    case 0xd6: ldb_di();   				break;
                                    case 0xd7: stb_di();   				break;
                                    case 0xd8: eorb_di();  				break;
                                    case 0xd9: adcb_di();  				break;
                                    case 0xda: orb_di();   				break;
                                    case 0xdb: addb_di();  				break;
                                    case 0xdc: ldd_di();   				break;
                                    case 0xdd: std_di();   				break;
                                    case 0xde: ldu_di();   				break;
                                    case 0xdf: stu_di();   				break;
                                    case 0xe0: subb_ix();  				break;
                                    case 0xe1: cmpb_ix();  				break;
                                    case 0xe2: sbcb_ix();  				break;
                                    case 0xe3: addd_ix();  				break;
                                    case 0xe4: andb_ix();  				break;
                                    case 0xe5: bitb_ix();  				break;
                                    case 0xe6: ldb_ix();   				break;
                                    case 0xe7: stb_ix();   				break;
                                    case 0xe8: eorb_ix();  				break;
                                    case 0xe9: adcb_ix();  				break;
                                    case 0xea: orb_ix();   				break;
                                    case 0xeb: addb_ix();  				break;
                                    case 0xec: ldd_ix();   				break;
                                    case 0xed: std_ix();   				break;
                                    case 0xee: ldu_ix();   				break;
                                    case 0xef: stu_ix();   				break;
                                    case 0xf0: subb_ex();  				break;
                                    case 0xf1: cmpb_ex();  				break;
                                    case 0xf2: sbcb_ex();  				break;
                                    case 0xf3: addd_ex();  				break;
                                    case 0xf4: andb_ex();  				break;
                                    case 0xf5: bitb_ex();  				break;
                                    case 0xf6: ldb_ex();   				break;
                                    case 0xf7: stb_ex();   				break;
                                    case 0xf8: eorb_ex();  				break;
                                    case 0xf9: adcb_ex();  				break;
                                    case 0xfa: orb_ex();   				break;
                                    case 0xfb: addb_ex();  				break;
                                    case 0xfc: ldd_ex();   				break;
                                    case 0xfd: std_ex();   				break;
                                    case 0xfe: ldu_ex();   				break;
                                    case 0xff: stu_ex();   				break;
				}
/*TODO*///	#else
/*TODO*///				(*hd6309_main[hd6309.ireg])();
/*TODO*///	#endif    /* BIG_SWITCH */
	
				hd6309_ICount[0] -= cycle_counts_page0[hd6309.ireg];
	
			} while( hd6309_ICount[0] > 0 );
	
			hd6309_ICount[0] -= hd6309.extra_cycles;
			hd6309.extra_cycles = 0;
		}
	
		return cycles - hd6309_ICount[0];	 /* NS 970908 */
	}

/*TODO*///	INLINE void fetch_effective_address( void )
/*TODO*///	{
/*TODO*///		UINT8 postbyte = ROP_ARG(PCD);
/*TODO*///		PC++;
/*TODO*///	
/*TODO*///		switch(postbyte)
/*TODO*///		{
/*TODO*///		case 0x00: EA=X;													break;
/*TODO*///		case 0x01: EA=X+1;													break;
/*TODO*///		case 0x02: EA=X+2;													break;
/*TODO*///		case 0x03: EA=X+3;													break;
/*TODO*///		case 0x04: EA=X+4;													break;
/*TODO*///		case 0x05: EA=X+5;													break;
/*TODO*///		case 0x06: EA=X+6;													break;
/*TODO*///		case 0x07: EA=X+7;													break;
/*TODO*///		case 0x08: EA=X+8;													break;
/*TODO*///		case 0x09: EA=X+9;													break;
/*TODO*///		case 0x0a: EA=X+10; 												break;
/*TODO*///		case 0x0b: EA=X+11; 												break;
/*TODO*///		case 0x0c: EA=X+12; 												break;
/*TODO*///		case 0x0d: EA=X+13; 												break;
/*TODO*///		case 0x0e: EA=X+14; 												break;
/*TODO*///		case 0x0f: EA=X+15; 												break;
/*TODO*///	
/*TODO*///		case 0x10: EA=X-16; 												break;
/*TODO*///		case 0x11: EA=X-15; 												break;
/*TODO*///		case 0x12: EA=X-14; 												break;
/*TODO*///		case 0x13: EA=X-13; 												break;
/*TODO*///		case 0x14: EA=X-12; 												break;
/*TODO*///		case 0x15: EA=X-11; 												break;
/*TODO*///		case 0x16: EA=X-10; 												break;
/*TODO*///		case 0x17: EA=X-9;													break;
/*TODO*///		case 0x18: EA=X-8;													break;
/*TODO*///		case 0x19: EA=X-7;													break;
/*TODO*///		case 0x1a: EA=X-6;													break;
/*TODO*///		case 0x1b: EA=X-5;													break;
/*TODO*///		case 0x1c: EA=X-4;													break;
/*TODO*///		case 0x1d: EA=X-3;													break;
/*TODO*///		case 0x1e: EA=X-2;													break;
/*TODO*///		case 0x1f: EA=X-1;													break;
/*TODO*///	
/*TODO*///		case 0x20: EA=Y;													break;
/*TODO*///		case 0x21: EA=Y+1;													break;
/*TODO*///		case 0x22: EA=Y+2;													break;
/*TODO*///		case 0x23: EA=Y+3;													break;
/*TODO*///		case 0x24: EA=Y+4;													break;
/*TODO*///		case 0x25: EA=Y+5;													break;
/*TODO*///		case 0x26: EA=Y+6;													break;
/*TODO*///		case 0x27: EA=Y+7;													break;
/*TODO*///		case 0x28: EA=Y+8;													break;
/*TODO*///		case 0x29: EA=Y+9;													break;
/*TODO*///		case 0x2a: EA=Y+10; 												break;
/*TODO*///		case 0x2b: EA=Y+11; 												break;
/*TODO*///		case 0x2c: EA=Y+12; 												break;
/*TODO*///		case 0x2d: EA=Y+13; 												break;
/*TODO*///		case 0x2e: EA=Y+14; 												break;
/*TODO*///		case 0x2f: EA=Y+15; 												break;
/*TODO*///	
/*TODO*///		case 0x30: EA=Y-16; 												break;
/*TODO*///		case 0x31: EA=Y-15; 												break;
/*TODO*///		case 0x32: EA=Y-14; 												break;
/*TODO*///		case 0x33: EA=Y-13; 												break;
/*TODO*///		case 0x34: EA=Y-12; 												break;
/*TODO*///		case 0x35: EA=Y-11; 												break;
/*TODO*///		case 0x36: EA=Y-10; 												break;
/*TODO*///		case 0x37: EA=Y-9;													break;
/*TODO*///		case 0x38: EA=Y-8;													break;
/*TODO*///		case 0x39: EA=Y-7;													break;
/*TODO*///		case 0x3a: EA=Y-6;													break;
/*TODO*///		case 0x3b: EA=Y-5;													break;
/*TODO*///		case 0x3c: EA=Y-4;													break;
/*TODO*///		case 0x3d: EA=Y-3;													break;
/*TODO*///		case 0x3e: EA=Y-2;													break;
/*TODO*///		case 0x3f: EA=Y-1;													break;
/*TODO*///	
/*TODO*///		case 0x40: EA=U;													break;
/*TODO*///		case 0x41: EA=U+1;													break;
/*TODO*///		case 0x42: EA=U+2;													break;
/*TODO*///		case 0x43: EA=U+3;													break;
/*TODO*///		case 0x44: EA=U+4;													break;
/*TODO*///		case 0x45: EA=U+5;													break;
/*TODO*///		case 0x46: EA=U+6;													break;
/*TODO*///		case 0x47: EA=U+7;													break;
/*TODO*///		case 0x48: EA=U+8;													break;
/*TODO*///		case 0x49: EA=U+9;													break;
/*TODO*///		case 0x4a: EA=U+10; 												break;
/*TODO*///		case 0x4b: EA=U+11; 												break;
/*TODO*///		case 0x4c: EA=U+12; 												break;
/*TODO*///		case 0x4d: EA=U+13; 												break;
/*TODO*///		case 0x4e: EA=U+14; 												break;
/*TODO*///		case 0x4f: EA=U+15; 												break;
/*TODO*///	
/*TODO*///		case 0x50: EA=U-16; 												break;
/*TODO*///		case 0x51: EA=U-15; 												break;
/*TODO*///		case 0x52: EA=U-14; 												break;
/*TODO*///		case 0x53: EA=U-13; 												break;
/*TODO*///		case 0x54: EA=U-12; 												break;
/*TODO*///		case 0x55: EA=U-11; 												break;
/*TODO*///		case 0x56: EA=U-10; 												break;
/*TODO*///		case 0x57: EA=U-9;													break;
/*TODO*///		case 0x58: EA=U-8;													break;
/*TODO*///		case 0x59: EA=U-7;													break;
/*TODO*///		case 0x5a: EA=U-6;													break;
/*TODO*///		case 0x5b: EA=U-5;													break;
/*TODO*///		case 0x5c: EA=U-4;													break;
/*TODO*///		case 0x5d: EA=U-3;													break;
/*TODO*///		case 0x5e: EA=U-2;													break;
/*TODO*///		case 0x5f: EA=U-1;													break;
/*TODO*///	
/*TODO*///		case 0x60: EA=S;													break;
/*TODO*///		case 0x61: EA=S+1;													break;
/*TODO*///		case 0x62: EA=S+2;													break;
/*TODO*///		case 0x63: EA=S+3;													break;
/*TODO*///		case 0x64: EA=S+4;													break;
/*TODO*///		case 0x65: EA=S+5;													break;
/*TODO*///		case 0x66: EA=S+6;													break;
/*TODO*///		case 0x67: EA=S+7;													break;
/*TODO*///		case 0x68: EA=S+8;													break;
/*TODO*///		case 0x69: EA=S+9;													break;
/*TODO*///		case 0x6a: EA=S+10; 												break;
/*TODO*///		case 0x6b: EA=S+11; 												break;
/*TODO*///		case 0x6c: EA=S+12; 												break;
/*TODO*///		case 0x6d: EA=S+13; 												break;
/*TODO*///		case 0x6e: EA=S+14; 												break;
/*TODO*///		case 0x6f: EA=S+15; 												break;
/*TODO*///	
/*TODO*///		case 0x70: EA=S-16; 												break;
/*TODO*///		case 0x71: EA=S-15; 												break;
/*TODO*///		case 0x72: EA=S-14; 												break;
/*TODO*///		case 0x73: EA=S-13; 												break;
/*TODO*///		case 0x74: EA=S-12; 												break;
/*TODO*///		case 0x75: EA=S-11; 												break;
/*TODO*///		case 0x76: EA=S-10; 												break;
/*TODO*///		case 0x77: EA=S-9;													break;
/*TODO*///		case 0x78: EA=S-8;													break;
/*TODO*///		case 0x79: EA=S-7;													break;
/*TODO*///		case 0x7a: EA=S-6;													break;
/*TODO*///		case 0x7b: EA=S-5;													break;
/*TODO*///		case 0x7c: EA=S-4;													break;
/*TODO*///		case 0x7d: EA=S-3;													break;
/*TODO*///		case 0x7e: EA=S-2;													break;
/*TODO*///		case 0x7f: EA=S-1;													break;
/*TODO*///	
/*TODO*///		case 0x80: EA=X;	X++;											break;
/*TODO*///		case 0x81: EA=X;	X+=2;											break;
/*TODO*///		case 0x82: X--; 	EA=X;											break;
/*TODO*///		case 0x83: X-=2;	EA=X;											break;
/*TODO*///		case 0x84: EA=X;													break;
/*TODO*///		case 0x85: EA=X+SIGNED(B);											break;
/*TODO*///		case 0x86: EA=X+SIGNED(A);											break;
/*TODO*///		case 0x87: EA=X+SIGNED(E);											break;
/*TODO*///		case 0x88: IMMBYTE(EA); 	EA=X+SIGNED(EA);						break;
/*TODO*///		case 0x89: IMMWORD(ea); 	EA+=X;									break;
/*TODO*///		case 0x8a: EA=X+SIGNED(F);											break;
/*TODO*///		case 0x8b: EA=X+D;													break;
/*TODO*///		case 0x8c: IMMBYTE(EA); 	EA=PC+SIGNED(EA);						break;
/*TODO*///		case 0x8d: IMMWORD(ea); 	EA+=PC; 								break;
/*TODO*///		case 0x8e: EA=X+W;													break;
/*TODO*///		case 0x8f: EA=W;		 											break;
/*TODO*///	
/*TODO*///		case 0x90: EA=W;								EAD=RM16(EAD);		break;
/*TODO*///		case 0x91: EA=X;	X+=2;						EAD=RM16(EAD);		break;
/*TODO*///		case 0x92: X--; 	EA=X;						EAD=RM16(EAD);		break;
/*TODO*///		case 0x93: X-=2;	EA=X;						EAD=RM16(EAD);		break;
/*TODO*///		case 0x94: EA=X;								EAD=RM16(EAD);		break;
/*TODO*///		case 0x95: EA=X+SIGNED(B);						EAD=RM16(EAD);		break;
/*TODO*///		case 0x96: EA=X+SIGNED(A);						EAD=RM16(EAD);		break;
/*TODO*///		case 0x97: EA=X+SIGNED(E);						EAD=RM16(EAD);		break;
/*TODO*///		case 0x98: IMMBYTE(EA); 	EA=X+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0x99: IMMWORD(ea); 	EA+=X;				EAD=RM16(EAD);		break;
/*TODO*///		case 0x9a: EA=X+SIGNED(F);						EAD=RM16(EAD);		break;
/*TODO*///		case 0x9b: EA=X+D;								EAD=RM16(EAD);		break;
/*TODO*///		case 0x9c: IMMBYTE(EA); 	EA=PC+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0x9d: IMMWORD(ea); 	EA+=PC; 			EAD=RM16(EAD);		break;
/*TODO*///		case 0x9e: EA=X+W;								EAD=RM16(EAD);		break;
/*TODO*///		case 0x9f: IMMWORD(ea); 						EAD=RM16(EAD);		break;
/*TODO*///	
/*TODO*///		case 0xa0: EA=Y;	Y++;											break;
/*TODO*///		case 0xa1: EA=Y;	Y+=2;											break;
/*TODO*///		case 0xa2: Y--; 	EA=Y;											break;
/*TODO*///		case 0xa3: Y-=2;	EA=Y;											break;
/*TODO*///		case 0xa4: EA=Y;													break;
/*TODO*///		case 0xa5: EA=Y+SIGNED(B);											break;
/*TODO*///		case 0xa6: EA=Y+SIGNED(A);											break;
/*TODO*///		case 0xa7: EA=Y+SIGNED(E);											break;
/*TODO*///		case 0xa8: IMMBYTE(EA); 	EA=Y+SIGNED(EA);						break;
/*TODO*///		case 0xa9: IMMWORD(ea); 	EA+=Y;									break;
/*TODO*///		case 0xaa: EA=Y+SIGNED(F);											break;
/*TODO*///		case 0xab: EA=Y+D;													break;
/*TODO*///		case 0xac: IMMBYTE(EA); 	EA=PC+SIGNED(EA);						break;
/*TODO*///		case 0xad: IMMWORD(ea); 	EA+=PC; 								break;
/*TODO*///		case 0xae: EA=Y+W;													break;
/*TODO*///		case 0xaf: IMMWORD(ea);     EA+=W;									break;
/*TODO*///	
/*TODO*///		case 0xb0: IMMWORD(ea); 	EA+=W;				EAD=RM16(EAD);		break;
/*TODO*///		case 0xb1: EA=Y;	Y+=2;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xb2: Y--; 	EA=Y;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xb3: Y-=2;	EA=Y;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xb4: EA=Y;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xb5: EA=Y+SIGNED(B);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xb6: EA=Y+SIGNED(A);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xb7: EA=Y+SIGNED(E);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xb8: IMMBYTE(EA); 	EA=Y+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0xb9: IMMWORD(ea); 	EA+=Y;				EAD=RM16(EAD);		break;
/*TODO*///		case 0xba: EA=Y+SIGNED(F);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xbb: EA=Y+D;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xbc: IMMBYTE(EA); 	EA=PC+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0xbd: IMMWORD(ea); 	EA+=PC; 			EAD=RM16(EAD);		break;
/*TODO*///		case 0xbe: EA=Y+W;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xbf: IMMWORD(ea); 						EAD=RM16(EAD);		break;
/*TODO*///	
/*TODO*///		case 0xc0: EA=U;			U++;									break;
/*TODO*///		case 0xc1: EA=U;			U+=2;									break;
/*TODO*///		case 0xc2: U--; 			EA=U;									break;
/*TODO*///		case 0xc3: U-=2;			EA=U;									break;
/*TODO*///		case 0xc4: EA=U;													break;
/*TODO*///		case 0xc5: EA=U+SIGNED(B);											break;
/*TODO*///		case 0xc6: EA=U+SIGNED(A);											break;
/*TODO*///		case 0xc7: EA=U+SIGNED(E);											break;
/*TODO*///		case 0xc8: IMMBYTE(EA); 	EA=U+SIGNED(EA);						break;
/*TODO*///		case 0xc9: IMMWORD(ea); 	EA+=U;									break;
/*TODO*///		case 0xca: EA=U+SIGNED(F);											break;
/*TODO*///		case 0xcb: EA=U+D;													break;
/*TODO*///		case 0xcc: IMMBYTE(EA); 	EA=PC+SIGNED(EA);						break;
/*TODO*///		case 0xcd: IMMWORD(ea); 	EA+=PC; 								break;
/*TODO*///		case 0xce: EA=U+W;													break;
/*TODO*///		case 0xcf: EA=W;            W+=2;									break;
/*TODO*///	
/*TODO*///		case 0xd0: EA=W;	W+=2;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd1: EA=U;	U+=2;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd2: U--; 	EA=U;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd3: U-=2;	EA=U;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd4: EA=U;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xd5: EA=U+SIGNED(B);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd6: EA=U+SIGNED(A);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd7: EA=U+SIGNED(E);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xd8: IMMBYTE(EA); 	EA=U+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0xd9: IMMWORD(ea); 	EA+=U;				EAD=RM16(EAD);		break;
/*TODO*///		case 0xda: EA=U+SIGNED(F);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xdb: EA=U+D;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xdc: IMMBYTE(EA); 	EA=PC+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0xdd: IMMWORD(ea); 	EA+=PC; 			EAD=RM16(EAD);		break;
/*TODO*///		case 0xde: EA=U+W;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xdf: IMMWORD(ea); 						EAD=RM16(EAD);		break;
/*TODO*///	
/*TODO*///		case 0xe0: EA=S;	S++;											break;
/*TODO*///		case 0xe1: EA=S;	S+=2;											break;
/*TODO*///		case 0xe2: S--; 	EA=S;											break;
/*TODO*///		case 0xe3: S-=2;	EA=S;											break;
/*TODO*///		case 0xe4: EA=S;													break;
/*TODO*///		case 0xe5: EA=S+SIGNED(B);											break;
/*TODO*///		case 0xe6: EA=S+SIGNED(A);											break;
/*TODO*///		case 0xe7: EA=S+SIGNED(E);											break;
/*TODO*///		case 0xe8: IMMBYTE(EA); 	EA=S+SIGNED(EA);						break;
/*TODO*///		case 0xe9: IMMWORD(ea); 	EA+=S;									break;
/*TODO*///		case 0xea: EA=S+SIGNED(F);											break;
/*TODO*///		case 0xeb: EA=S+D;													break;
/*TODO*///		case 0xec: IMMBYTE(EA); 	EA=PC+SIGNED(EA);						break;
/*TODO*///		case 0xed: IMMWORD(ea); 	EA+=PC; 								break;
/*TODO*///		case 0xee: EA=S+W;													break;
/*TODO*///		case 0xef: W-=2;	EA=W;											break;
/*TODO*///	
/*TODO*///		case 0xf0: W-=2;	EA=W;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf1: EA=S;	S+=2;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf2: S--; 	EA=S;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf3: S-=2;	EA=S;						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf4: EA=S;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xf5: EA=S+SIGNED(B);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf6: EA=S+SIGNED(A);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf7: EA=S+SIGNED(E);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xf8: IMMBYTE(EA); 	EA=S+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0xf9: IMMWORD(ea); 	EA+=S;				EAD=RM16(EAD);		break;
/*TODO*///		case 0xfa: EA=S+SIGNED(F);						EAD=RM16(EAD);		break;
/*TODO*///		case 0xfb: EA=S+D;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xfc: IMMBYTE(EA); 	EA=PC+SIGNED(EA);	EAD=RM16(EAD);		break;
/*TODO*///		case 0xfd: IMMWORD(ea); 	EA+=PC; 			EAD=RM16(EAD);		break;
/*TODO*///		case 0xfe: EA=S+W;								EAD=RM16(EAD);		break;
/*TODO*///		case 0xff: IMMWORD(ea); 						EAD=RM16(EAD);		break;
/*TODO*///		}
/*TODO*///	
/*TODO*///		hd6309_ICount -= index_cycle[postbyte];
/*TODO*///	}

}
