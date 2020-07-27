package mame056.cpu.i8x41;

import static arcadeflex056.fucPtr.*;
import common.libc.cstdio.FILE;
import static common.libc.cstdio.fprintf;
import common.ptr.UBytePtr;
import static mame056.common.memory_region;
import static mame056.commonH.REGION_CPU1;
import static mame056.cpu.h6280.h6280H.*;
import static mame056.cpuintrfH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static arcadeflex056.osdepend.logerror;
import static mame056.cpu.i8x41.i8x41H.*;

/**
 *
 * @author chusogar
 */
public class i8x41  extends cpu_interface {

    @Override
    public void init() {
        i8x41_init();
    }

    @Override
    public void reset(Object param) {
        i8x41_reset(param);
    }

    @Override
    public void exit() {
        i8x41_exit();
    }

    @Override
    public int execute(int cycles) {
        return i8x41_execute(cycles);
    }

    @Override
    public Object init_context() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object get_context() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_context(Object reg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] get_cycle_table(int which) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String cpu_info(Object context, int regnum) {
        return i8x41_info(context, regnum);
    }

    @Override
    public void set_cycle_table(int which, int[] new_table) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_reg(int regnum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_reg(int regnum, int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_irq_line(int irqline, int linestate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_irq_callback(irqcallbacksPtr callback) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String cpu_dasm(String buffer, int pc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int memory_read(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void memory_write(int offset, int data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int mem_address_bits_of_cpu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static class I8X41_Regs {
	int	ppc;
	int	pc;
	int	timer;
	int	subtype;
	int	a;
	int	psw;
	int	state;
	int	tovf;
	int	enable;
	int	dbbi;
	int	dbbo;
	UBytePtr	ram;
        public irqcallbacksPtr irq_callback;
    };

    public static int[] i8x41_ICount = new int[1];
    
    static I8X41_Regs i8x41 = new I8X41_Regs();

/*TODO*////* Layout of the registers in the debugger */
/*TODO*///static UINT8 i8x41_reg_layout[] = {
/*TODO*///	I8X41_PC, I8X41_SP, I8X41_PSW, I8X41_A, I8X41_T, I8X41_DATA, I8X41_CMND, -1,
/*TODO*///	I8X41_R0, I8X41_R1, I8X41_R2, I8X41_R3, I8X41_R4, I8X41_R5, I8X41_R6, I8X41_R7, 0
/*TODO*///};
/*TODO*///
/*TODO*////* Layout of the debugger windows x,y,w,h */
/*TODO*///static UINT8 i8x41_win_layout[] = {
/*TODO*///	 0, 0,80, 2,	/* register window (top rows) */
/*TODO*///	 0, 3,24,19,	/* disassembler window (left colums) */
/*TODO*///	25, 3,55, 9,	/* memory #1 window (right, upper middle) */
/*TODO*///	25,13,55, 9,	/* memory #2 window (right, lower middle) */
/*TODO*///	 0,23,80, 1,	/* command line window (bottom rows) */
/*TODO*///};

    public static int RM(int a){ return	cpu_readmem16(a); }
    public static void WM(int a, int v){ cpu_writemem16(a,v); }
    public static int ROP(int pc){ return cpu_readop(pc); }
    public static int ROP_ARG(int pc){ return cpu_readop_arg(pc); }

    /* PC vectors */
    public static final int V_RESET = 0x000;	/* power on address */
    public static final int V_IBF   = 0x003;	/* input buffer full interrupt vector */
    public static final int V_TIMER = 0x007;	/* timer/counter interrupt vector */

    /*
     * Memory locations
     * Note:
     * 000-3ff		internal ROM for 8x41 (1K)
     * 400-7ff		(more) internal for 8x42 type (2K)
     * 800-8ff		internal RAM
     */
    public static final int M_IRAM  = 0x800;	/* internal RAM is mapped here */
    public static final int M_BANK0 = 0x800;	/* register bank 0 (8 times 8 bits) */
    public static final int M_STACK = 0x808;	/* stack (8 times 16 bits) */
    public static final int M_BANK1 = 0x818;	/* register bank 1 (8 times 8 bits) */
    public static final int M_USER  = 0x820;	/* user memory (224 times 8 bits) */

    /* PSW flag bits */
    public static final int FC		= 0x80;	/* carry flag */
    public static final int FA		= 0x40;	/* auxiliary carry flag */
    public static final int F0		= 0x20;	/* flag 0 */
    public static final int F1		= 0x10;	/* flag 1 (also used as bank select!?) */
    public static final int F3		= 0x08;	/* unused */
    public static final int SP		= 0x07;	/* lower three bits are used as stack pointer */

    /* STATE flag bits */
    public static final int OBF 	= 0x01;	/* output buffer full */
    public static final int IBF 	= 0x02;	/* input buffer full */
    public static final int TEST0	= 0x04;	/* test0 line */
    public static final int TEST1	= 0x08;	/* test1 line */

    /* ENABLE flag bits */
    public static int IBFI	= 0x01;	/* input buffer full interrupt */
    public static int TCNTI	= 0x02;	/* timer/counter interrupt */
    public static int DMA 	= 0x04;	/* DMA mode */
    public static int FLAGS	= 0x08;	/* FLAGS mode */
    public static int T		= 0x10;	/* timer */
    public static int CNT 	= 0x20;	/* counter */

/*TODO*////* shorter names for the I8x41 structure elements */
/*TODO*///#define PPC 	i8x41.ppc
/*TODO*///#define PC		i8x41.pc
/*TODO*///#define A		i8x41.a
/*TODO*///#define PSW 	i8x41.psw
/*TODO*///#define DBBI	i8x41.dbbi
/*TODO*///#define DBBO	i8x41.dbbo
    public static int R(int n){ return i8x41.ram.read(((i8x41.psw & F1)!=0 ? M_BANK1:M_BANK0)+(n)); }    
/*TODO*///#define STATE	i8x41.state
/*TODO*///#define ENABLE	i8x41.enable
/*TODO*///#define TIMER	i8x41.timer
/*TODO*///#define TOVF	i8x41.tovf

    static int i8x41_cycles[] = {
            1,1,1,2,2,1,1,1,2,2,2,2,2,2,2,2,
            1,1,2,2,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,1,2,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,2,1,2,1,2,1,2,2,2,2,2,2,2,2,
            1,1,1,2,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,2,2,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,
            1,1,2,1,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,1,2,2,1,2,1,2,2,2,2,2,2,2,2,
            1,1,2,2,1,1,2,1,2,2,2,2,2,2,2,2,
            1,1,1,2,2,1,1,1,1,1,1,1,1,1,1,1,
            2,2,2,2,2,1,2,1,2,2,2,2,2,2,2,2,
            1,1,1,1,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,2,1,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,1,2,2,1,2,1,1,1,1,1,1,1,1,1,
            1,1,2,1,2,1,2,1,2,2,2,2,2,2,2,2
    };

    /***********************************
     *	illegal opcodes
     ***********************************/
    public static void illegal()
    {
            logerror("i8x41 #%d: illegal opcode at 0x%03x: %02x\n", cpu_getactivecpu(), i8x41.pc, ROP(i8x41.pc));
    }

/*TODO*////***********************************
/*TODO*/// *	0110 1rrr *  ADD	 A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void add_r(int r)
/*TODO*///{
/*TODO*///	UINT8 res = A + R(r);
/*TODO*///	if( res < A ) PSW |= FC;
/*TODO*///	if( (res & 0x0f) < (A & 0x0f) ) PSW |= FA;
/*TODO*///	A = res;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0110 000r
/*TODO*/// *	ADD 	A,@Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void add_rm(int r)
/*TODO*///{
/*TODO*///	UINT8 res = A + RM( M_IRAM + (R(r) & 0x3f) );
/*TODO*///	if( res < A ) PSW |= FC;
/*TODO*///	if( (res & 0x0f) < (A & 0x0f) ) PSW |= FA;
/*TODO*///	A = res;
/*TODO*///}

    /***********************************
     *	0000 0011 7654 3210
     *	ADD 	A,#n
     ***********************************/
    public static void add_i()
    {
            int res = i8x41.a + ROP_ARG(i8x41.pc);
            i8x41.pc++;
            if( res < i8x41.a ) i8x41.psw |= FC;
            if( (res & 0x0f) < (i8x41.a & 0x0f) ) i8x41.psw |= FA;
            i8x41.a = res;
    }

/*TODO*////***********************************
/*TODO*/// *	0111 1rrr
/*TODO*/// *	ADDC	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void addc_r(int r)
/*TODO*///{
/*TODO*///	UINT8 res = A + R(r) + (PSW >> 7);
/*TODO*///	if( res <= A ) PSW |= FC;
/*TODO*///	if( (res & 0x0f) <= (A & 0x0f) ) PSW |= FA;
/*TODO*///	A = res;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0111 000r
/*TODO*/// *	ADDC	A,@Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void addc_rm(int r)
/*TODO*///{
/*TODO*///	UINT8 res = A + RM( M_IRAM+ (R(r) & 0x3f) ) + (PSW >> 7);
/*TODO*///	if( res <= A ) PSW |= FC;
/*TODO*///	if( (res & 0x0f) <= (A & 0x0f) ) PSW |= FA;
/*TODO*///	A = res;
/*TODO*///}

    /***********************************
     *	0001 0011 7654 3210
     *	ADDC	A,#n
     ***********************************/
    public static void addc_i()
    {
            int res = i8x41.a + ROP_ARG(i8x41.pc);
            i8x41.pc++;
            if( res < i8x41.a ) i8x41.psw |= FC;
            if( (res & 0x0f) < (i8x41.a & 0x0f) ) i8x41.psw |= FA;
            i8x41.a = res;
    }

/*TODO*////***********************************
/*TODO*/// *	0101 1rrr
/*TODO*/// *	ANL 	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void anl_r(int r)
/*TODO*///{
/*TODO*///	A = A & R(r);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0101 000r
/*TODO*/// *	ANL 	A,@Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void anl_rm(int r)
/*TODO*///{
/*TODO*///	A = A & RM( M_IRAM + (R(r) & 0x3f) );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0101 0011 7654 3210
/*TODO*/// *	ANL 	A,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void anl_i(void)
/*TODO*///{
/*TODO*///	A = A & ROP_ARG(PC);
/*TODO*///	PC++;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1001 10pp 7654 3210
/*TODO*/// *	ANL 	Pp,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void anl_p_i(int p)
/*TODO*///{
/*TODO*///	UINT8 val = ROP_ARG(PC);
/*TODO*///	PC++;
/*TODO*///	val = val & cpu_readport16(p);
/*TODO*///	cpu_writeport16(p, val);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1001 11pp 7654 3210
/*TODO*/// *	ANLD	Pp,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void anld_p_a(int p)
/*TODO*///{
/*TODO*///	UINT8 val = A & 0x0f;
/*TODO*///	val = (val | 0xf0) & cpu_readport16(p);
/*TODO*///	cpu_writeport16(p, val);
/*TODO*///}

    /***********************************
     *	aaa1 0100 7654 3210
     *	CALL	addr
     ***********************************/
    public static void call_i(int page)
    {
            int adr = ROP_ARG(i8x41.pc);
            i8x41.pc++;
            WM( M_STACK + (i8x41.psw&SP) * 2 + 0, i8x41.pc & 0xff);
            WM( M_STACK + (i8x41.psw&SP) * 2 + 1, ((i8x41.pc >> 8) & 0x0f) | (i8x41.psw & 0xf0) );
            i8x41.psw = (i8x41.psw & ~SP) | ((i8x41.psw + 1) & SP);
            i8x41.pc = page | adr;
    }

    /***********************************
     *	0010 0111
     *	CLR 	A
     ***********************************/
    public static void clr_a()
    {
            i8x41.a = 0;
    }

/*TODO*////***********************************
/*TODO*/// *	1001 0111
/*TODO*/// *	CLR 	C
/*TODO*/// ***********************************/
/*TODO*///INLINE void clr_c(void)
/*TODO*///{
/*TODO*///	PSW &= ~FC;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1000 0101
/*TODO*/// *	CLR 	F0
/*TODO*/// ***********************************/
/*TODO*///INLINE void clr_f0(void)
/*TODO*///{
/*TODO*///	PSW &= ~F0;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1010 0101
/*TODO*/// *	CLR 	F1
/*TODO*/// ***********************************/
/*TODO*///INLINE void clr_f1(void)
/*TODO*///{
/*TODO*///	PSW &= ~F1;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0011 0111
/*TODO*/// *	CPL 	A
/*TODO*/// ***********************************/
/*TODO*///INLINE void cpl_a(void)
/*TODO*///{
/*TODO*///	A = ~A;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1010 0111
/*TODO*/// *	CPL 	C
/*TODO*/// ***********************************/
/*TODO*///INLINE void cpl_c(void)
/*TODO*///{
/*TODO*///	PSW ^= FC;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1001 0101
/*TODO*/// *	CPL 	F0
/*TODO*/// ***********************************/
/*TODO*///INLINE void cpl_f0(void)
/*TODO*///{
/*TODO*///	PSW ^= F0;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1011 0101
/*TODO*/// *	CPL 	F1
/*TODO*/// ***********************************/
/*TODO*///INLINE void cpl_f1(void)
/*TODO*///{
/*TODO*///	PSW ^= F1;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0101 0111
/*TODO*/// *	DA		A
/*TODO*/// ***********************************/
/*TODO*///INLINE void da_a(void)
/*TODO*///{
/*TODO*///	UINT8 res = A + ((PSW & FA) || ((A & 0x0f) > 0x09)) ? 0x06 : 0x00;
/*TODO*///	if( (PSW & FC) || ((res & 0xf0) > 0x90) )
/*TODO*///		res += 0x60;
/*TODO*///	if( res < A )
/*TODO*///		PSW |= FC;
/*TODO*///	else
/*TODO*///		PSW &= ~FC;
/*TODO*///	A = res;
/*TODO*///}

    /***********************************
     *	0000 0111
     *	DEC 	A
     ***********************************/
    public static void dec_a()
    {
            i8x41.a -= 1;
    }

/*TODO*////***********************************
/*TODO*/// *	1100 1rrr
/*TODO*/// *	DEC 	Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void dec_r(int r)
/*TODO*///{
/*TODO*///	R(r) -= 1;
/*TODO*///}

    /***********************************
     *	0001 0101
     *	DIS 	I
     ***********************************/
    public static void dis_i()
    {
            i8x41.enable &= ~IBFI;	/* disable input buffer full interrupt */
    }

/*TODO*////***********************************
/*TODO*/// *	0011 0101
/*TODO*/// *	DIS 	TCNTI
/*TODO*/// ***********************************/
/*TODO*///INLINE void dis_tcnti(void)
/*TODO*///{
/*TODO*///	ENABLE &= ~TCNTI;	/* disable timer/counter interrupt */
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0111 1rrr 7654 3210
/*TODO*/// *	DJNZ	Rr,addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void djnz_r_i(int r)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC++;
/*TODO*///	R(r) -= 1;
/*TODO*///	if( R(r) )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1110 0101
/*TODO*/// *	EN		DMA
/*TODO*/// ***********************************/
/*TODO*///INLINE void en_dma(void)
/*TODO*///{
/*TODO*///	ENABLE |= DMA;		/* enable DMA handshake lines */
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1111 0101
/*TODO*/// *	EN		FLAGS
/*TODO*/// ***********************************/
/*TODO*///INLINE void en_flags(void)
/*TODO*///{
/*TODO*///	ENABLE |= FLAGS;	/* enable flags handshake lines P24 & P25 */
/*TODO*///}

    /***********************************
     *	0000 0101
     *	EN		I
     ***********************************/
    public static void en_i()
    {
            if (0 == (i8x41.enable & IBFI))
            {
                    i8x41.enable |= IBFI; 	/* enable input buffer full interrupt */
                    if ((i8x41.state & IBF) != 0)	/* already got data in the buffer? */
                            i8x41_set_irq_line(I8X41_INT_IBF, HOLD_LINE);
            }
    }

    /***********************************
     *	0010 0101
     *	EN		TCNTI
     ***********************************/
    public static void en_tcnti()
    {
            i8x41.enable |= TCNTI;	/* enable timer/counter interrupt */
    }

    /***********************************
     *	0010 0010
     *	IN		A,DBB
     ***********************************/
    public static void in_a_dbb()
    {
            if (i8x41.irq_callback != null)
                    (i8x41.irq_callback).handler(I8X41_INT_IBF);	/* clear input buffer full flag */
            i8x41.state &= ~IBF;
            i8x41.a = i8x41.dbbi;				/* DBB input buffer */
    }

    /***********************************
     *	0000 10pp
     *	IN		A,Pp
     ***********************************/
    public static void in_a_p(int p)
    {
            i8x41.a = cpu_readport16(p);	/* should read port 0/3 be prevented? */
    }

    /***********************************
     *	0001 0111
     *	INC 	A
     ***********************************/
    public static void inc_a()
    {
            i8x41.a += 1;
    }

    /***********************************
     *	0001 1rrr
     *	DEC 	Rr
     ***********************************/
    public static void inc_r(int r)
    {
/*TODO*///            R(r) += 1;
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /***********************************
     *	0001 000r
     *	INC  @	Rr
     ***********************************/
    public static void inc_rm(int r)
    {
            int addr = M_IRAM + (R(r) & 0x3f);
            WM( addr, RM(addr) + 1 );
    }

    /***********************************
     *	bbb1 0010
     *	JBb 	addr
     ***********************************/
    public static void jbb_i(int bit)
    {
            int adr = ROP_ARG(i8x41.pc);
            i8x41.pc += 1;
            if(( i8x41.a & (1 << bit) ) != 0)
                    i8x41.pc = (i8x41.pc & 0x700) | adr;
    }

/*TODO*////***********************************
/*TODO*/// *	1111 0110
/*TODO*/// *	JC		addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jc_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( PSW & FC )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1011 0110
/*TODO*/// *	JF0 	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jf0_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( PSW & F0 )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0111 0110
/*TODO*/// *	JF1 	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jf1_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( PSW & F1 )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}

    /***********************************
     *	aaa0 0100
     *	JMP 	addr
     ***********************************/
    public static void jmp_i(int page)
    {
            /* err.. do we have 10 or 11 PC bits?
             * CALL is said to use 0aa1 (4 pages)
             * JMP is said to use aaa0 (8 pages)
             */
            int adr = ROP_ARG(i8x41.pc);
            i8x41.pc += 1;
            i8x41.pc = page | adr;
    }

/*TODO*////***********************************
/*TODO*/// *	1011 0011
/*TODO*/// *	JMP  @	A
/*TODO*/// ***********************************/
/*TODO*///INLINE void jmpp_a(void)
/*TODO*///{
/*TODO*///	UINT16 adr = (PC & 0x700) | A;
/*TODO*///	PC = (PC & 0x700) | RM(adr);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1110 0110
/*TODO*/// *	JNC 	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jnc_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( !(PSW & FC) )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1101 0110
/*TODO*/// *	JNIBF	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jnibf_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( 0 == (STATE & IBF) )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}

    /***********************************
     *	0010 0110
     *	JNT0	addr
     ***********************************/
    public static void jnt0_i()
    {
            int adr = ROP_ARG(i8x41.pc);
            i8x41.pc += 1;
            if( (i8x41.state & TEST0) == 0 )
                    i8x41.pc = (i8x41.pc & 0x700) | adr;
    }

/*TODO*////***********************************
/*TODO*/// *	0100 0110
/*TODO*/// *	JNT1	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jnt1_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( !(STATE & TEST1) )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1001 0110
/*TODO*/// *	JNZ 	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jnz_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( A )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1000 0110
/*TODO*/// *	JOBF	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jobf_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( STATE & OBF )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}

    /***********************************
     *	0001 0110
     *	JTF 	addr
     ***********************************/
    public static void jtf_i()
    {
            int adr = ROP_ARG(i8x41.pc);
            i8x41.pc += 1;
            if( i8x41.tovf != 0 )
                    i8x41.pc = (i8x41.pc & 0x700) | adr;
            i8x41.tovf = 0;
    }

/*TODO*////***********************************
/*TODO*/// *	0011 0110
/*TODO*/// *	JT0 	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jt0_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( STATE & TEST0 )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0101 0110
/*TODO*/// *	JT1 	addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jt1_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( STATE & TEST1 )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1100 0110
/*TODO*/// *	JZ		addr
/*TODO*/// ***********************************/
/*TODO*///INLINE void jz_i(void)
/*TODO*///{
/*TODO*///	UINT8 adr = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	if( !A )
/*TODO*///		PC = (PC & 0x700) | adr;
/*TODO*///}

    /***********************************
     *	0010 0011
     *	MOV 	A,#n
     ***********************************/
    public static void mov_a_i()
    {
            i8x41.a = ROP(i8x41.pc);
            i8x41.pc += 1;
    }

/*TODO*////***********************************
/*TODO*/// *	1100 0111
/*TODO*/// *	MOV 	A,PSW
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_a_psw(void)
/*TODO*///{
/*TODO*///	A = PSW;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1111 1rrr
/*TODO*/// *	MOV 	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_a_r(int r)
/*TODO*///{
/*TODO*///	A = R(r);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1111 000r
/*TODO*/// *	MOV 	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_a_rm(int r)
/*TODO*///{
/*TODO*///	A = RM( M_IRAM + (R(r) & 0x3f) );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0100 0010
/*TODO*/// *	MOV 	A,T
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_a_t(void)
/*TODO*///{
/*TODO*///	A = (UINT8) (TIMER / 32);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1101 0111
/*TODO*/// *	MOV 	PSW,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_psw_a(void)
/*TODO*///{
/*TODO*///	PSW = A;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1010 1rrr
/*TODO*/// *	MOV 	Rr,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_r_a(int r)
/*TODO*///{
/*TODO*///	R(r) = A;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1011 1rrr
/*TODO*/// *	MOV 	Rr,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_r_i(int r)
/*TODO*///{
/*TODO*///	UINT8 val = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	R(r) = val;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1010 000r
/*TODO*/// *	MOV 	@Rr,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_rm_a(int r)
/*TODO*///{
/*TODO*///	WM( M_IRAM + (R(r) & 0x3f), A );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1011 000r
/*TODO*/// *	MOV 	@Rr,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_rm_i(int r)
/*TODO*///{
/*TODO*///	UINT8 val = ROP_ARG(PC);
/*TODO*///	PC += 1;
/*TODO*///	WM( M_IRAM + (R(r) & 0x3f), val );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1001 0000
/*TODO*/// *	MOV 	STS,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_sts_a(void)
/*TODO*///{
/*TODO*///	STATE = (STATE & 0x0f) | (A & 0xf0);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0110 0010
/*TODO*/// *	MOV 	T,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void mov_t_a(void)
/*TODO*///{
/*TODO*///	TIMER = A * 32;
/*TODO*///}

    /***********************************
     *	0000 11pp
     *	MOVD	A,Pp
     ***********************************/
    public static void movd_a_p(int p)
    {
            int val = cpu_readport16(p);
            i8x41.a = val & 0x0f;
    }

/*TODO*////***********************************
/*TODO*/// *	0011 11pp
/*TODO*/// *	MOVD	Pp,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void movd_p_a(int p)
/*TODO*///{
/*TODO*///	cpu_writeport16(p, A & 0x0f);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1010 0011
/*TODO*/// *	MOVP	A,@A
/*TODO*/// ***********************************/
/*TODO*///INLINE void movp_a_am(void)
/*TODO*///{
/*TODO*///	UINT16 addr = (PC & 0x700) | A;
/*TODO*///	A = RM(addr);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1110 0011
/*TODO*/// *	MOVP3	A,@A
/*TODO*/// ***********************************/
/*TODO*///INLINE void movp3_a_am(void)
/*TODO*///{
/*TODO*///	UINT16 addr = 0x300 | A;
/*TODO*///	A = RM(addr);
/*TODO*///}

    /***********************************
     *	0000 0000
     *	NOP
     ***********************************/
    public static void nop()
    {
    }

/*TODO*////***********************************
/*TODO*/// *	0100 1rrr
/*TODO*/// *	ORL 	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void orl_r(int r)
/*TODO*///{
/*TODO*///	A = A | R(r);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0100 000r
/*TODO*/// *	ORL 	A,@Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void orl_rm(int r)
/*TODO*///{
/*TODO*///	A = A | RM( M_IRAM + (R(r) & 0x3f) );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0100 0011 7654 3210
/*TODO*/// *	ORL 	A,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void orl_i(void)
/*TODO*///{
/*TODO*///	UINT8 val = ROP_ARG(PC);
/*TODO*///	PC++;
/*TODO*///	A = A | val;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1000 10pp 7654 3210
/*TODO*/// *	ORL 	Pp,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void orl_p_i(int p)
/*TODO*///{
/*TODO*///	UINT8 val = ROP_ARG(PC);
/*TODO*///	PC++;
/*TODO*///	val = val | cpu_readport16(p);
/*TODO*///	cpu_writeport16(p, val);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1000 11pp 7654 3210
/*TODO*/// *	ORLD	Pp,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void orld_p_a(int p)
/*TODO*///{
/*TODO*///	UINT8 val = A & 0x0f;
/*TODO*///	val = val | cpu_readport16(p);
/*TODO*///	cpu_writeport16(p, val);
/*TODO*///}

    /***********************************
     *	0000 0010
     *	OUT 	DBB,A
     ***********************************/
    public static void out_dbb_a()
    {
            i8x41.dbbo = i8x41.a;			/* DBB output buffer */
            i8x41.state |= OBF;		/* assert the output buffer full flag */
    }

/*TODO*////***********************************
/*TODO*/// *	0011 10pp
/*TODO*/// *	OUT 	Pp,A
/*TODO*/// ***********************************/
/*TODO*///INLINE void out_p_a(int p)
/*TODO*///{
/*TODO*///	cpu_writeport16(p, A);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1000 0011
/*TODO*/// *	RET
/*TODO*/// ***********************************/
/*TODO*///INLINE void ret(void)
/*TODO*///{
/*TODO*///	UINT8 msb;
/*TODO*///	PSW = (PSW & ~SP) | ((PSW - 1) & SP);
/*TODO*///	msb = RM(M_STACK + (PSW&SP) * 2 + 1);
/*TODO*///	PC = RM(M_STACK + (PSW&SP) * 2 + 0);
/*TODO*///	PC |= (msb << 8) & 0x700;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1001 0011
/*TODO*/// *	RETR
/*TODO*/// ***********************************/
/*TODO*///INLINE void retr(void)
/*TODO*///{
/*TODO*///	UINT8 msb;
/*TODO*///	PSW = (PSW & ~SP) | ((PSW - 1) & SP);
/*TODO*///	msb = RM(M_STACK + (PSW&SP) * 2 + 1);
/*TODO*///	PC = RM(M_STACK + (PSW&SP) * 2 + 0);
/*TODO*///	PC |= (msb << 8) & 0x700;
/*TODO*///	PSW = (PSW & 0x0f) | (msb & 0xf0);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1110 0111
/*TODO*/// *	RL		A
/*TODO*/// ***********************************/
/*TODO*///INLINE void rl_a(void)
/*TODO*///{
/*TODO*///	A = (A << 1) | (A >> 7);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1111 0111
/*TODO*/// *	RLC 	A
/*TODO*/// ***********************************/
/*TODO*///INLINE void rlc_a(void)
/*TODO*///{
/*TODO*///	UINT8 c = PSW >> 7;
/*TODO*///	PSW = (PSW & ~FC) | (A >> 7);
/*TODO*///	A = (A << 1) | c;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0111 0111
/*TODO*/// *	RR		A
/*TODO*/// ***********************************/
/*TODO*///INLINE void rr_a(void)
/*TODO*///{
/*TODO*///	A = (A >> 1) | (A << 7);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0110 0111
/*TODO*/// *	RRC 	A
/*TODO*/// ***********************************/
/*TODO*///INLINE void rrc_a(void)
/*TODO*///{
/*TODO*///	UINT8 c = PSW & 0x80;
/*TODO*///	PSW = (PSW & ~FC) | (A << 7);
/*TODO*///	A = (A >> 1) | c;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1100 0101
/*TODO*/// *	SEL 	RB0
/*TODO*/// ***********************************/
/*TODO*///INLINE void sel_rb0(void)
/*TODO*///{
/*TODO*///	PSW &= ~F1;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1101 0101
/*TODO*/// *	SEL 	RB1
/*TODO*/// ***********************************/
/*TODO*///INLINE void sel_rb1(void)
/*TODO*///{
/*TODO*///	PSW |= F1;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0110 0101
/*TODO*/// *	STOP	TCNT
/*TODO*/// ***********************************/
/*TODO*///INLINE void stop_tcnt(void)
/*TODO*///{
/*TODO*///	ENABLE &= ~(T|CNT);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0100 0101
/*TODO*/// *	STRT	CNT
/*TODO*/// ***********************************/
/*TODO*///INLINE void strt_cnt(void)
/*TODO*///{
/*TODO*///	ENABLE |= CNT;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0101 0101
/*TODO*/// *	STRT	T
/*TODO*/// ***********************************/
/*TODO*///INLINE void strt_t(void)
/*TODO*///{
/*TODO*///	ENABLE |= T;
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0100 0111
/*TODO*/// *	SWAP	A
/*TODO*/// ***********************************/
/*TODO*///INLINE void swap_a(void)
/*TODO*///{
/*TODO*///	A = (A << 4) | (A >> 4);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	0010 1rrr
/*TODO*/// *	XCH 	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void xch_a_r(int r)
/*TODO*///{
/*TODO*///	UINT8 tmp = R(r);
/*TODO*///	R(r) = A;
/*TODO*///	A = tmp;
/*TODO*///}

    /***********************************
     *	0010 000r
     *	XCH 	A,@Rr
     ***********************************/
    public static void xch_a_rm(int r)
    {
            int addr = M_IRAM + (R(r) & 0x3f);
            int tmp = RM(addr);
            WM( addr, i8x41.a );
            i8x41.a = tmp;
    }

/*TODO*////***********************************
/*TODO*/// *	0011 000r
/*TODO*/// *	XCHD	A,@Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void xchd_a_rm(int r)
/*TODO*///{
/*TODO*///	UINT8 addr = M_IRAM + (R(r) & 0x3f);
/*TODO*///	UINT8 tmp = RM(addr);
/*TODO*///	WM( addr, (tmp & 0xf0) | (A & 0x0f) );
/*TODO*///	A = (A & 0xf0) | (tmp & 0x0f);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1101 1rrr
/*TODO*/// *	XRL 	A,Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void xrl_r(int r)
/*TODO*///{
/*TODO*///	A = A ^ R(r);
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1101 000r
/*TODO*/// *	XRL 	A,@Rr
/*TODO*/// ***********************************/
/*TODO*///INLINE void xrl_rm(int r)
/*TODO*///{
/*TODO*///	A = A ^ RM( M_IRAM + (R(r) & 0x3f) );
/*TODO*///}
/*TODO*///
/*TODO*////***********************************
/*TODO*/// *	1101 0011 7654 3210
/*TODO*/// *	XRL 	A,#n
/*TODO*/// ***********************************/
/*TODO*///INLINE void xrl_i(void)
/*TODO*///{
/*TODO*///	UINT8 val = ROP_ARG(PC);
/*TODO*///	PC++;
/*TODO*///	A = A ^ val;
/*TODO*///}

    public void i8x41_init()
    {
/*TODO*///	int cpu = cpu_getactivecpu();
/*TODO*///	state_save_register_UINT16("i8x41", cpu, "PPC",       &i8x41.ppc,    1);
/*TODO*///	state_save_register_UINT16("i8x41", cpu, "PC",        &i8x41.pc,     1);
/*TODO*///	state_save_register_UINT16("i8x41", cpu, "TIMER",     &i8x41.timer,  1);
/*TODO*///	state_save_register_UINT16("i8x41", cpu, "SUBTYPE",   &i8x41.subtype,1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "A",         &i8x41.a,      1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "PSW",       &i8x41.psw,    1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "STATE",     &i8x41.state,  1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "TOVF",      &i8x41.tovf,   1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "ENABLE",    &i8x41.enable, 1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "DBBI",      &i8x41.dbbi,   1);
/*TODO*///	state_save_register_UINT8 ("i8x41", cpu, "DBBO",      &i8x41.dbbo,   1);
    }

    /* Reset registers to the initial values */
    public void i8x41_reset(Object param)
    {
            //memset(&i8x41, 0, sizeof(I8X41));
            i8x41 = new I8X41_Regs();
            /* default to 8041 behaviour for DBBI/DBBO and extended commands */
            i8x41.subtype = 8041;
            /* ugly hack.. excuse my lazyness */
            i8x41.ram = memory_region(REGION_CPU1 + cpu_getactivecpu());
            i8x41.enable = IBFI | TCNTI;
            i8x41.dbbi = 0xff;
            i8x41.dbbo = 0xff;
    }

    /* Shut down CPU core */
    public void i8x41_exit()
    {
            /* nothing to do */
    }

    /* Execute cycles - returns number of cycles actually run */
    public int i8x41_execute(int cycles)
    {
            i8x41_ICount[0] = cycles;

            do
            {
                    int op = cpu_readop(i8x41.pc);

                    i8x41.ppc = i8x41.pc;

    /*TODO*///		CALL_MAME_DEBUG;

                    i8x41.pc += 1;
                    i8x41_ICount[0] -= i8x41_cycles[op];

                    if(( i8x41.enable & T ) != 0)
                            i8x41.timer += i8x41_cycles[op];

                    if( i8x41.timer > 0x1fff )
                    {
                            i8x41.timer &= 0x1fff;
                            i8x41.tovf = 1;
                            if(( i8x41.enable & TCNTI ) != 0)
                            {
                                    WM( M_STACK + (i8x41.psw&SP) * 2 + 1, ((i8x41.pc >> 8) & 0x0f) | (i8x41.psw & 0xf0) );
                                    i8x41.psw = (i8x41.psw & ~SP) | ((i8x41.psw + 1) & SP);
                                    i8x41.pc = V_TIMER;
                            }
                    }

                    switch( op )
                    {
                    /* opcode cycles bitmask */
                    case 0x00: /* 1: 0000 0000 */
                            nop();
                            break;
                    case 0x01: /* 1: 0000 0001 */
                            illegal();
                            break;
                    case 0x02: /* 1: 0000 0010 */
                            out_dbb_a();
                            break;
                    case 0x03: /* 2: 0000 0011 */
                            add_i();
                            break;
                    case 0x04: /* 2: aaa0 0100 */
                            jmp_i(0x000);
                            break;
                    case 0x05: /* 1: 0000 0101 */
                            en_i();
                            break;
                    case 0x06: /* 1: 0000 0110 */
                            illegal();
                            break;
                    case 0x07: /* 1: 0000 0111 */
                            dec_a();
                            break;
                    case 0x08: /* 2: 0000 10pp */
                    case 0x09: /* 2: 0000 10pp */
                    case 0x0a: /* 2: 0000 10pp */
                    case 0x0b: /* 2: 0000 10pp */
                            in_a_p(op & 3);
                            break;
                    case 0x0c: /* 2: 0000 11pp */
                    case 0x0d: /* 2: 0000 11pp */
                    case 0x0e: /* 2: 0000 11pp */
                    case 0x0f: /* 2: 0000 11pp */
                            movd_a_p(op & 3);
                            break;
                    case 0x10: /* 1: 0001 000r */
                            inc_rm(0);
                            break;
                    case 0x11: /* 1: 0001 000r */
                            inc_rm(1);
                            break;
                    case 0x12: /* 2: bbb1 0010 */
                            jbb_i(0);
                            break;
                    case 0x13: /* 2: 0001 0011 */
                            addc_i();
                            break;
                    case 0x14: /* 2: aaa1 0100 */
                            call_i(0x000);
                            break;
                    case 0x15: /* 1: 0001 0101 */
                            dis_i();
                            break;
                    case 0x16: /* 2: 0001 0110 */
                            jtf_i();
                            break;
                    case 0x17: /* 1: 0001 0111 */
                            inc_a();
                            break;
                    case 0x18: /* 1: 0001 1rrr */
                    case 0x19: /* 1: 0001 1rrr */
                    case 0x1a: /* 1: 0001 1rrr */
                    case 0x1b: /* 1: 0001 1rrr */
                    case 0x1c: /* 1: 0001 1rrr */
                    case 0x1d: /* 1: 0001 1rrr */
                    case 0x1e: /* 1: 0001 1rrr */
                    case 0x1f: /* 1: 0001 1rrr */
                            inc_r(op & 7);
                            break;
                    case 0x20: /* 1: 0010 000r */
                            xch_a_rm(0);
                            break;
                    case 0x21: /* 1: 0010 000r */
                            xch_a_rm(1);
                            break;
                    case 0x22: /* 1: 0010 0010 */
                            in_a_dbb();
                            break;
                    case 0x23: /* 2: 0010 0011 */
                            mov_a_i();
                            break;
                    case 0x24: /* 2: aaa0 0100 */
                            jmp_i(0x100);
                            break;
                    case 0x25: /* 1: 0010 0101 */
                            en_tcnti();
                            break;
                    case 0x26: /* 2: 0010 0110 */
                            jnt0_i();
                            break;
                    case 0x27: /* 1: 0010 0111 */
                            clr_a();
                            break;
                    case 0x28: /* 1: 0010 1rrr */
                    case 0x29: /* 1: 0010 1rrr */
                    case 0x2a: /* 1: 0010 1rrr */
                    case 0x2b: /* 1: 0010 1rrr */
                    case 0x2c: /* 1: 0010 1rrr */
                    case 0x2d: /* 1: 0010 1rrr */
                    case 0x2e: /* 1: 0010 1rrr */
                    case 0x2f: /* 1: 0010 1rrr */
                            xch_a_r(op & 7);
                            break;
                    case 0x30: /* 1: 0011 000r */
                            xchd_a_rm(0);
                            break;
                    case 0x31: /* 1: 0011 000r */
                            xchd_a_rm(1);
                            break;
                    case 0x32: /* 2: bbb1 0010 */
                            jbb_i(1);
                            break;
                    case 0x33: /* 1: 0011 0101 */
                            illegal();
                            break;
                    case 0x34: /* 2: aaa1 0100 */
                            call_i(0x100);
                            break;
                    case 0x35: /* 1: 0000 0101 */
                            dis_tcnti();
                            break;
                    case 0x36: /* 2: 0011 0110 */
                            jt0_i();
                            break;
                    case 0x37: /* 1: 0011 0111 */
                            cpl_a();
                            break;
                    case 0x38: /* 2: 0011 10pp */
                    case 0x39: /* 2: 0011 10pp */
                    case 0x3a: /* 2: 0011 10pp */
                    case 0x3b: /* 2: 0011 10pp */
                            out_p_a(op & 3);
                            break;
                    case 0x3c: /* 2: 0011 11pp */
                    case 0x3d: /* 2: 0011 11pp */
                    case 0x3e: /* 2: 0011 11pp */
                    case 0x3f: /* 2: 0011 11pp */
                            movd_p_a(op & 3);
                            break;
                    case 0x40: /* 1: 0100 000r */
                            orl_rm(0);
                            break;
                    case 0x41: /* 1: 0100 000r */
                            orl_rm(1);
                            break;
                    case 0x42: /* 1: 0100 0010 */
                            mov_a_t();
                            break;
                    case 0x43: /* 2: 0100 0011 */
                            orl_i();
                            break;
                    case 0x44: /* 2: aaa0 0100 */
                            jmp_i(0x200);
                            break;
                    case 0x45: /* 1: 0100 0101 */
                            strt_cnt();
                            break;
                    case 0x46: /* 2: 0100 0110 */
                            jnt1_i();
                            break;
                    case 0x47: /* 1: 0100 0111 */
                            swap_a();
                            break;
                    case 0x48: /* 1: 0100 1rrr */
                    case 0x49: /* 1: 0100 1rrr */
                    case 0x4a: /* 1: 0100 1rrr */
                    case 0x4b: /* 1: 0100 1rrr */
                    case 0x4c: /* 1: 0100 1rrr */
                    case 0x4d: /* 1: 0100 1rrr */
                    case 0x4e: /* 1: 0100 1rrr */
                    case 0x4f: /* 1: 0100 1rrr */
                            orl_r(op & 7);
                            break;
                    case 0x50: /* 1: 0101 000r */
                            anl_rm(0);
                            break;
                    case 0x51: /* 1: 0101 000r */
                            anl_rm(1);
                            break;
                    case 0x52: /* 2: bbb1 0010 */
                            jbb_i(2);
                            break;
                    case 0x53: /* 2: 0101 0011 */
                            anl_i();
                            break;
                    case 0x54: /* 2: aaa1 0100 */
                            call_i(0x200);
                            break;
                    case 0x55: /* 1: 0101 0101 */
                            strt_t();
                            break;
                    case 0x56: /* 2: 0101 0110 */
                            jt1_i();
                            break;
                    case 0x57: /* 1: 0101 0111 */
                            da_a();
                            break;
                    case 0x58: /* 1: 0101 1rrr */
                    case 0x59: /* 1: 0101 1rrr */
                    case 0x5a: /* 1: 0101 1rrr */
                    case 0x5b: /* 1: 0101 1rrr */
                    case 0x5c: /* 1: 0101 1rrr */
                    case 0x5d: /* 1: 0101 1rrr */
                    case 0x5e: /* 1: 0101 1rrr */
                    case 0x5f: /* 1: 0101 1rrr */
                            anl_r(op & 7);
                            break;
                    case 0x60: /* 1: 0110 000r */
                            add_rm(0);
                            break;
                    case 0x61: /* 1: 0110 000r */
                            add_rm(1);
                            break;
                    case 0x62: /* 1: 0110 0010 */
                            mov_t_a();
                            break;
                    case 0x63: /* 1: 0110 0011 */
                            illegal();
                            break;
                    case 0x64: /* 2: aaa0 0100 */
                            jmp_i(0x300);
                            break;
                    case 0x65: /* 1: 0110 0101 */
                            stop_tcnt();
                            break;
                    case 0x66: /* 1: 0110 0110 */
                            illegal();
                            break;
                    case 0x67: /* 1: 0110 0111 */
                            rrc_a();
                            break;
                    case 0x68: /* 1: 0110 1rrr */
                    case 0x69: /* 1: 0110 1rrr */
                    case 0x6a: /* 1: 0110 1rrr */
                    case 0x6b: /* 1: 0110 1rrr */
                    case 0x6c: /* 1: 0110 1rrr */
                    case 0x6d: /* 1: 0110 1rrr */
                    case 0x6e: /* 1: 0110 1rrr */
                    case 0x6f: /* 1: 0110 1rrr */
                            add_r(op & 7);
                            break;
                    case 0x70: /* 1: 0111 000r */
                            addc_rm(0);
                            break;
                    case 0x71: /* 1: 0111 000r */
                            addc_rm(1);
                            break;
                    case 0x72: /* 2: bbb1 0010 */
                            jbb_i(3);
                            break;
                    case 0x73: /* 1: 0111 0011 */
                            illegal();
                            break;
                    case 0x74: /* 2: aaa1 0100 */
                            call_i(0x300);
                            break;
                    case 0x75: /* 1: 0111 0101 */
                            illegal();
                            break;
                    case 0x76: /* 2: 0111 0110 */
                            jf1_i();
                            break;
                    case 0x77: /* 1: 0111 0111 */
                            rr_a();
                            break;
                    case 0x78: /* 1: 0111 1rrr */
                    case 0x79: /* 1: 0111 1rrr */
                    case 0x7a: /* 1: 0111 1rrr */
                    case 0x7b: /* 1: 0111 1rrr */
                    case 0x7c: /* 1: 0111 1rrr */
                    case 0x7d: /* 1: 0111 1rrr */
                    case 0x7e: /* 1: 0111 1rrr */
                    case 0x7f: /* 1: 0111 1rrr */
                            addc_r(op & 7);
                            break;
                    case 0x80: /* 1: 1000 0000 */
                            illegal();
                            break;
                    case 0x81: /* 1: 1000 0001 */
                            illegal();
                            break;
                    case 0x82: /* 1: 1000 0010 */
                            illegal();
                            break;
                    case 0x83: /* 2: 1000 0011 */
                            ret();
                            break;
                    case 0x84: /* 2: aaa0 0100 */
                            jmp_i(0x400);
                            break;
                    case 0x85: /* 1: 1000 0101 */
                            clr_f0();
                            break;
                    case 0x86: /* 2: 1000 0110 */
                            jobf_i();
                            break;
                    case 0x87: /* 1: 1000 0111 */
                            illegal();
                            break;
                    case 0x88: /* 2: 1000 10pp */
                    case 0x89: /* 2: 1000 10pp */
                    case 0x8a: /* 2: 1000 10pp */
                    case 0x8b: /* 2: 1000 10pp */
                            orl_p_i(op & 3);
                            break;
                    case 0x8c: /* 2: 1000 11pp */
                    case 0x8d: /* 2: 1000 11pp */
                    case 0x8e: /* 2: 1000 11pp */
                    case 0x8f: /* 2: 1000 11pp */
                            orld_p_a(op & 7);
                            break;
                    case 0x90: /* 1: 1001 0000 */
                            mov_sts_a();
                            break;
                    case 0x91: /* 1: 1001 0001 */
                            illegal();
                            break;
                    case 0x92: /* 2: bbb1 0010 */
                            jbb_i(4);
                            break;
                    case 0x93: /* 2: 1001 0011 */
                            retr();
                            break;
                    case 0x94: /* 1: aaa1 0100 */
                            call_i(0x400);
                            break;
                    case 0x95: /* 1: 1001 0101 */
                            cpl_f0();
                            break;
                    case 0x96: /* 2: 1001 0110 */
                            jnz_i();
                            break;
                    case 0x97: /* 1: 1001 0111 */
                            clr_c();
                            break;
                    case 0x98: /* 2: 1001 10pp */
                    case 0x99: /* 2: 1001 10pp */
                    case 0x9a: /* 2: 1001 10pp */
                    case 0x9b: /* 2: 1001 10pp */
                            anl_p_i(op & 3);
                            break;
                    case 0x9c: /* 2: 1001 11pp */
                    case 0x9d: /* 2: 1001 11pp */
                    case 0x9e: /* 2: 1001 11pp */
                    case 0x9f: /* 2: 1001 11pp */
                            anld_p_a(op & 7);
                            break;
                    case 0xa0: /* 1: 1010 000r */
                            mov_rm_a(0);
                            break;
                    case 0xa1: /* 1: 1010 000r */
                            mov_rm_a(1);
                            break;
                    case 0xa2: /* 1: 1010 0010 */
                            illegal();
                            break;
                    case 0xa3: /* 2: 1010 0011 */
                            movp_a_am();
                            break;
                    case 0xa4: /* 2: aaa0 0100 */
                            jmp_i(0x500);
                            break;
                    case 0xa5: /* 1: 1010 0101 */
                            clr_f1();
                            break;
                    case 0xa6: /* 1: 1010 0110 */
                            illegal();
                            break;
                    case 0xa7: /* 1: 1010 0111 */
                            cpl_c();
                            break;
                    case 0xa8: /* 1: 1010 1rrr */
                    case 0xa9: /* 1: 1010 1rrr */
                    case 0xaa: /* 1: 1010 1rrr */
                    case 0xab: /* 1: 1010 1rrr */
                    case 0xac: /* 1: 1010 1rrr */
                    case 0xad: /* 1: 1010 1rrr */
                    case 0xae: /* 1: 1010 1rrr */
                    case 0xaf: /* 1: 1010 1rrr */
                            mov_r_a(op & 7);
                            break;
                    case 0xb0: /* 2: 1011 000r */
                            mov_rm_i(0);
                            break;
                    case 0xb1: /* 2: 1011 000r */
                            mov_rm_i(1);
                            break;
                    case 0xb2: /* 2: bbb1 0010 */
                            jbb_i(5);
                            break;
                    case 0xb3: /* 2: 1011 0011 */
                            jmpp_a();
                            break;
                    case 0xb4: /* 2: aaa1 0100 */
                            call_i(0x500);
                            break;
                    case 0xb5: /* 1: 1011 0101 */
                            cpl_f1();
                            break;
                    case 0xb6: /* 2: 1011 0110 */
                            jf0_i();
                            break;
                    case 0xb7: /* 1: 1011 0111 */
                            illegal();
                            break;
                    case 0xb8: /* 2: 1011 1rrr */
                    case 0xb9: /* 2: 1011 1rrr */
                    case 0xba: /* 2: 1011 1rrr */
                    case 0xbb: /* 2: 1011 1rrr */
                    case 0xbc: /* 2: 1011 1rrr */
                    case 0xbd: /* 2: 1011 1rrr */
                    case 0xbe: /* 2: 1011 1rrr */
                    case 0xbf: /* 2: 1011 1rrr */
                            mov_r_i(op & 7);
                            break;
                    case 0xc0: /* 1: 1100 0000 */
                            illegal();
                            break;
                    case 0xc1: /* 1: 1100 0001 */
                            illegal();
                            break;
                    case 0xc2: /* 1: 1100 0010 */
                            illegal();
                            break;
                    case 0xc3: /* 1: 1100 0011 */
                            illegal();
                            break;
                    case 0xc4: /* 2: aaa0 0100 */
                            jmp_i(0x600);
                            break;
                    case 0xc5: /* 1: 1100 0101 */
                            sel_rb0();
                            break;
                    case 0xc6: /* 2: 1100 0110 */
                            jz_i();
                            break;
                    case 0xc7: /* 1: 1100 0111 */
                            mov_a_psw();
                            break;
                    case 0xc8: /* 1: 1100 1rrr */
                    case 0xc9: /* 1: 1100 1rrr */
                    case 0xca: /* 1: 1100 1rrr */
                    case 0xcb: /* 1: 1100 1rrr */
                    case 0xcc: /* 1: 1100 1rrr */
                    case 0xcd: /* 1: 1100 1rrr */
                    case 0xcf: /* 1: 1100 1rrr */
                            dec_r(op & 7);
                            break;
                    case 0xd0: /* 1: 1101 000r */
                            xrl_rm(0);
                            break;
                    case 0xd1: /* 1: 1101 000r */
                            xrl_rm(1);
                            break;
                    case 0xd2: /* 2: bbb1 0010 */
                            jbb_i(6);
                            break;
                    case 0xd3: /* 1: 1101 0011 */
                            xrl_i();
                            break;
                    case 0xd4: /* 2: aaa1 0100 */
                            call_i(0x600);
                            break;
                    case 0xd5: /* 1: 1101 0101 */
                            sel_rb1();
                            break;
                    case 0xd6: /* 2: 1101 0110 */
                            jnibf_i();
                            break;
                    case 0xd7: /* 1: 1101 0111 */
                            mov_psw_a();
                            break;
                    case 0xd8: /* 1: 1101 1rrr */
                    case 0xd9: /* 1: 1101 1rrr */
                    case 0xda: /* 1: 1101 1rrr */
                    case 0xdb: /* 1: 1101 1rrr */
                    case 0xdc: /* 1: 1101 1rrr */
                    case 0xdd: /* 1: 1101 1rrr */
                    case 0xde: /* 1: 1101 1rrr */
                    case 0xdf: /* 1: 1101 1rrr */
                            xrl_r(op & 7);
                            break;
                    case 0xe0: /* 1: 1110 0000 */
                            illegal();
                            break;
                    case 0xe1: /* 1: 1110 0001 */
                            illegal();
                            break;
                    case 0xe2: /* 1: 1110 0010 */
                            illegal();
                            break;
                    case 0xe3: /* 2: 1110 0011 */
                            movp3_a_am();
                            break;
                    case 0xe4: /* 2: aaa0 0100 */
                            jmp_i(0x700);
                            break;
                    case 0xe5: /* 1: 1110 0101 */
                            en_dma();
                            break;
                    case 0xe6: /* 2: 1110 0110 */
                            jnc_i();
                            break;
                    case 0xe7: /* 1: 1110 0111 */
                            rl_a();
                            break;
                    case 0xe8: /* 2: 1111 1rrr */
                    case 0xe9: /* 2: 1111 1rrr */
                    case 0xea: /* 2: 1111 1rrr */
                    case 0xeb: /* 2: 1111 1rrr */
                    case 0xec: /* 2: 1111 1rrr */
                    case 0xed: /* 2: 1111 1rrr */
                    case 0xee: /* 2: 1111 1rrr */
                    case 0xef: /* 2: 1111 1rrr */
                            djnz_r_i(op & 7);
                            break;
                    case 0xf0: /* 1: 1111 000r */
                            mov_a_rm(0);
                            break;
                    case 0xf1: /* 1: 1111 000r */
                            mov_a_rm(1);
                            break;
                    case 0xf2: /* 2: bbb1 0010 */
                            jbb_i(7);
                            break;
                    case 0xf3: /* 1: 1111 0011 */
                            illegal();
                            break;
                    case 0xf4: /* 2: aaa1 0100 */
                            call_i(0x700);
                            break;
                    case 0xf5: /* 1: 1111 0101 */
                            en_flags();
                            break;
                    case 0xf6: /* 2: 1111 0110 */
                            jc_i();
                            break;
                    case 0xf7: /* 1: 1111 0111 */
                            rlc_a();
                            break;
                    case 0xf8: /* 1: 1110 1rrr */
                    case 0xf9: /* 1: 1110 1rrr */
                    case 0xfa: /* 1: 1110 1rrr */
                    case 0xfb: /* 1: 1110 1rrr */
                    case 0xfc: /* 1: 1110 1rrr */
                    case 0xfd: /* 1: 1110 1rrr */
                    case 0xfe: /* 1: 1110 1rrr */
                    case 0xff: /* 1: 1110 1rrr */
                            mov_a_r(op & 7);
                            break;
                    }
            } while( i8x41_ICount[0] > 0 );

            return cycles - i8x41_ICount[0];
    }

/*TODO*////* Get registers, return context size */
/*TODO*///unsigned i8x41_get_context(void *dst)
/*TODO*///{
/*TODO*///	if( dst )
/*TODO*///		memcpy(dst, &i8x41, sizeof(I8X41));
/*TODO*///	return sizeof(I8X41);
/*TODO*///}
/*TODO*///
/*TODO*////* Set registers */
/*TODO*///void i8x41_set_context(void *src)
/*TODO*///{
/*TODO*///	if( src )
/*TODO*///		memcpy(&i8x41, src, sizeof(I8X41));
/*TODO*///}
/*TODO*///
/*TODO*///unsigned i8x41_get_reg(int regnum)
/*TODO*///{
/*TODO*///	switch( regnum )
/*TODO*///	{
/*TODO*///	case REG_PC:
/*TODO*///	case I8X41_PC:	return PC;
/*TODO*///	case REG_SP:
/*TODO*///	case I8X41_SP:	return PSW & SP;
/*TODO*///	case I8X41_PSW: return PSW;
/*TODO*///	case I8X41_A:	return A;
/*TODO*///	case I8X41_T:	return TIMER;
/*TODO*///	case I8X41_R0:	return R(0);
/*TODO*///	case I8X41_R1:	return R(1);
/*TODO*///	case I8X41_R2:	return R(2);
/*TODO*///	case I8X41_R3:	return R(3);
/*TODO*///	case I8X41_R4:	return R(4);
/*TODO*///	case I8X41_R5:	return R(5);
/*TODO*///	case I8X41_R6:	return R(6);
/*TODO*///	case I8X41_R7:	return R(7);
/*TODO*///	case I8X41_DATA:
/*TODO*///			STATE &= ~OBF;	/* reset the output buffer full flag */
/*TODO*///			return DBBO;
/*TODO*///	case I8X41_STAT:
/*TODO*///			return STATE;
/*TODO*///	case REG_PREVIOUSPC: return PPC;
/*TODO*///	default:
/*TODO*///		if( regnum <= REG_SP_CONTENTS )
/*TODO*///		{
/*TODO*///			unsigned offset = (PSW & SP) + (REG_SP_CONTENTS - regnum);
/*TODO*///			if( offset < 8 )
/*TODO*///				return RM( M_STACK + offset ) | ( RM( M_STACK + offset + 1 ) << 8 );
/*TODO*///		}
/*TODO*///	}
/*TODO*///	return 0;
/*TODO*///}
/*TODO*///
/*TODO*///void i8x41_set_reg (int regnum, unsigned val)
/*TODO*///{
/*TODO*///	switch( regnum )
/*TODO*///	{
/*TODO*///	case REG_PC:
/*TODO*///	case I8X41_PC:	PC = val & 0x7ff;
/*TODO*///	case REG_SP:
/*TODO*///	case I8X41_SP:	PSW = (PSW & ~SP) | (val & SP);
/*TODO*///	case I8X41_PSW: PSW = val;
/*TODO*///	case I8X41_A:	A = val;
/*TODO*///	case I8X41_T:	TIMER = val & 0x1fff;
/*TODO*///	case I8X41_R0:	R(0) = val; break;
/*TODO*///	case I8X41_R1:	R(1) = val; break;
/*TODO*///	case I8X41_R2:	R(2) = val; break;
/*TODO*///	case I8X41_R3:	R(3) = val; break;
/*TODO*///	case I8X41_R4:	R(4) = val; break;
/*TODO*///	case I8X41_R5:	R(5) = val; break;
/*TODO*///	case I8X41_R6:	R(6) = val; break;
/*TODO*///	case I8X41_R7:	R(7) = val; break;
/*TODO*///	case I8X41_DATA:
/*TODO*///			PSW &= ~F1;
/*TODO*///			DBBI = val;
/*TODO*///			if (i8x41.subtype == 8041) /* plain 8041 had no split input/output DBB buffers */
/*TODO*///				DBBO = val;
/*TODO*///			if (ENABLE & IBFI)
/*TODO*///				i8x41_set_irq_line(I8X41_INT_IBF, HOLD_LINE);
/*TODO*///			else
/*TODO*///				STATE |= IBF;
/*TODO*///			break;
/*TODO*///	case I8X41_CMND:
/*TODO*///			PSW |= F1;
/*TODO*///			DBBI = val;
/*TODO*///			if (i8x41.subtype == 8041) /* plain 8041 had no split input/output DBB buffers */
/*TODO*///				DBBO = val;
/*TODO*///			if (ENABLE & IBFI)
/*TODO*///				i8x41_set_irq_line(I8X41_INT_IBF, HOLD_LINE);
/*TODO*///			else
/*TODO*///				STATE |= IBF;
/*TODO*///			break;
/*TODO*///	case I8X41_STAT:
/*TODO*///			/* writing status.. hmm, should we issue interrupts here too? */
/*TODO*///			STATE = val;
/*TODO*///			break;
/*TODO*///	default:
/*TODO*///		if( regnum <= REG_SP_CONTENTS )
/*TODO*///		{
/*TODO*///			unsigned offset = (PSW & SP) + (REG_SP_CONTENTS - regnum);
/*TODO*///			if( offset < 8 )
/*TODO*///			{
/*TODO*///				WM( M_STACK + offset, val & 0xff );
/*TODO*///				WM( M_STACK + offset + 1, (val >> 8) & 0xff );
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///}

    public static void i8x41_set_irq_line(int irqline, int state)
    {
            switch( irqline )
            {
            case I8X41_INT_IBF:
                    if (state != CLEAR_LINE)
                    {
                            i8x41.state |= IBF;
                            if ((i8x41.enable & IBFI) != 0)
                            {
                                    WM( M_STACK + (i8x41.psw&SP) * 2 + 0, i8x41.pc & 0xff);
                                    WM( M_STACK + (i8x41.psw&SP) * 2 + 1, ((i8x41.pc >> 8) & 0x0f) | (i8x41.psw & 0xf0) );
                                    i8x41.psw = (i8x41.psw & ~SP) | ((i8x41.psw + 1) & SP);
                                    i8x41.pc = V_IBF;
                            }
                    }
                    else
                    {
                            i8x41.state &= ~IBF;
                    }
                    break;

            case I8X41_INT_TEST0:
                    if (state != CLEAR_LINE)
                            i8x41.state |= TEST0;
                    else
                            i8x41.state &= ~TEST0;
                    break;

            case I8X41_INT_TEST1:
                    if (state != CLEAR_LINE)
                    {
                            i8x41.state |= TEST1;
                    }
                    else
                    {
                            /* high to low transition? */
                            if ((i8x41.state & TEST1) != 0)
                            {
                                    /* counting enabled? */
                                    if ((i8x41.enable & CNT) != 0)
                                    {
                                            if (++i8x41.timer > 0x1fff)
                                            {
                                                    i8x41.tovf = 1;
                                                    if ((i8x41.enable & TCNTI) != 0)
                                                    {
                                                            WM( M_STACK + (i8x41.psw&SP) * 2 + 0, i8x41.pc & 0xff);
                                                            WM( M_STACK + (i8x41.psw&SP) * 2 + 1, ((i8x41.pc >> 8) & 0x0f) | (i8x41.psw & 0xf0) );
                                                            i8x41.psw = (i8x41.psw & ~SP) | ((i8x41.psw + 1) & SP);
                                                            i8x41.pc = V_TIMER;
                                                    }
                                            }
                                    }
                            }
                            i8x41.state &= ~TEST1;
                    }
                    break;
            }
    }

/*TODO*///void i8x41_set_irq_callback(int (*callback)(int irqline))
/*TODO*///{
/*TODO*///	i8x41.irq_callback = callback;
/*TODO*///}
/*TODO*///
/*TODO*///void i8x41_state_save(void *file)
/*TODO*///{
/*TODO*///}
/*TODO*///
/*TODO*///void i8x41_state_load(void *file)
/*TODO*///{
/*TODO*///}
/*TODO*///
    
    public String i8x41_info(Object context, int regnum)
    {
/*TODO*///	static char buffer[8][15+1];
/*TODO*///	static int which = 0;
/*TODO*///	I8X41 *r = context;
/*TODO*///
/*TODO*///	which = (which+1) % 8;
/*TODO*///	buffer[which][0] = '\0';
/*TODO*///	if( !context )
/*TODO*///		r = &i8x41;
/*TODO*///
	switch( regnum )
	{
/*TODO*///		case CPU_INFO_REG+I8X41_PC: sprintf(buffer[which], "PC:%04X", r->pc); break;
/*TODO*///		case CPU_INFO_REG+I8X41_SP: sprintf(buffer[which], "S:%X", r->psw & SP); break;
/*TODO*///		case CPU_INFO_REG+I8X41_PSW:sprintf(buffer[which], "PSW:%02X", r->psw); break;
/*TODO*///		case CPU_INFO_REG+I8X41_A:	sprintf(buffer[which], "A:%02X", r->a); break;
/*TODO*///		case CPU_INFO_REG+I8X41_T:	sprintf(buffer[which], "T:%04X", r->timer); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R0: sprintf(buffer[which], "R0:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 0]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R1: sprintf(buffer[which], "R1:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 1]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R2: sprintf(buffer[which], "R2:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 2]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R3: sprintf(buffer[which], "R3:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 3]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R4: sprintf(buffer[which], "R4:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 4]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R5: sprintf(buffer[which], "R5:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 5]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R6: sprintf(buffer[which], "R6:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 6]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_R7: sprintf(buffer[which], "R7:%02X", i8x41.ram[((r->psw & F1) ? M_BANK1 : M_BANK0) + 7]); break;
/*TODO*///		case CPU_INFO_REG+I8X41_DATA:sprintf(buffer[which], "DBBI:%02X", i8x41.dbbi); break;
/*TODO*///		case CPU_INFO_REG+I8X41_CMND:sprintf(buffer[which], "DBBO:%02X", i8x41.dbbo); break;
/*TODO*///		case CPU_INFO_REG+I8X41_STAT:sprintf(buffer[which], "STAT:%02X", i8x41.state); break;
/*TODO*///		case CPU_INFO_FLAGS:
/*TODO*///			sprintf(buffer[which], "%c%c%c%c%c%c%c%c",
/*TODO*///				r->psw & 0x80 ? 'C':'.',
/*TODO*///				r->psw & 0x40 ? 'A':'.',
/*TODO*///				r->psw & 0x20 ? '0':'.',
/*TODO*///				r->psw & 0x10 ? 'B':'.',
/*TODO*///				r->psw & 0x08 ? '?':'.',
/*TODO*///				r->psw & 0x04 ? 's':'.',
/*TODO*///				r->psw & 0x02 ? 's':'.',
/*TODO*///				r->psw & 0x01 ? 's':'.');
/*TODO*///			break;
		case CPU_INFO_NAME: return "I8X41";
		case CPU_INFO_FAMILY: return "Intel 8x41";
		case CPU_INFO_VERSION: return "0.1";
		case CPU_INFO_FILE: return "i8X41.java";
		case CPU_INFO_CREDITS: return "Copyright (c) 1999 Juergen Buchmueller, all rights reserved.";
/*TODO*///		case CPU_INFO_REG_LAYOUT: return (const char*)i8x41_reg_layout;
/*TODO*///		case CPU_INFO_WIN_LAYOUT: return (const char*)i8x41_win_layout;
	}
/*TODO*///	return buffer[which];
        throw new UnsupportedOperationException("Not supported yet.");
    }

/*TODO*///unsigned i8x41_dasm(char *buffer, unsigned pc)
/*TODO*///{
/*TODO*///#ifdef MAME_DEBUG
/*TODO*///	return Dasm8x41( buffer, pc );
/*TODO*///#else
/*TODO*///	sprintf( buffer, "$%02X", cpu_readop(pc) );
/*TODO*///	return 1;
/*TODO*///#endif
/*TODO*///}
/*TODO*///
    
}
