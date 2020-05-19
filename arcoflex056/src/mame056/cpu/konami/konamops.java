/**
 * Ported to 0.56
 */
package mame056.cpu.konami;

import static arcadeflex056.osdepend.logerror;
import static mame056.memoryH.*;
import static mame056.cpu.konami.konamtbl.*;
import static mame056.cpu.konami.konami.*;

/**
 *
 * @author chusogar
 */
public class konamops {
    
	/*

	HNZVC

	? = undefined
	* = affected
	- = unaffected
	0 = cleared
	1 = set
	# = CCr directly affected by instruction
	@ = special - carry set if bit 7 is set

	*/
 
	public static opcode illegal = new opcode() {
            public void handler() {
		System.out.println("KONAMI: illegal opcode at "+PC());
                logerror("KONAMI: illegal opcode at %04x\n",PC());
            }
        };

/*TODO*///	/* $00 NEG direct ?**** */
/*TODO*///	INLINE void neg_di( void )
/*TODO*///	{
/*TODO*///		UINT16 r,t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = -t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(0,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $01 ILLEGAL */
/*TODO*///
/*TODO*///	/* $02 ILLEGAL */
/*TODO*///
/*TODO*///	/* $03 COM direct -**01 */
/*TODO*///	INLINE void com_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		t = ~t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(t);
/*TODO*///		SEC;
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $04 LSR direct -0*-* */
/*TODO*///	INLINE void lsr_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t & CC_C);
/*TODO*///		t >>= 1;
/*TODO*///		SET_Z8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $05 ILLEGAL */
/*TODO*///
/*TODO*///	/* $06 ROR direct -**-* */
/*TODO*///	INLINE void ror_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r= (CC & CC_C) << 7;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t & CC_C);
/*TODO*///		r |= t>>1;
/*TODO*///		SET_NZ8(r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $07 ASR direct ?**-* */
/*TODO*///	INLINE void asr_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t & CC_C);
/*TODO*///		t = (t & 0x80) | (t >> 1);
/*TODO*///		SET_NZ8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $08 ASL direct ?**** */
/*TODO*///	INLINE void asl_di( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = t << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $09 ROL direct -**** */
/*TODO*///	INLINE void rol_di( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = (CC & CC_C) | (t << 1);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $0A DEC direct -***- */
/*TODO*///	INLINE void dec_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		--t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8D(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $0B ILLEGAL */
/*TODO*///
/*TODO*///	/* $OC INC direct -***- */
/*TODO*///	INLINE void inc_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		++t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8I(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $OD TST direct -**0- */
/*TODO*///	INLINE void tst_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $0E JMP direct ----- */
/*TODO*///	INLINE void jmp_di( void )
/*TODO*///	{
/*TODO*///		DIRECT;
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $0F CLR direct -0100 */
/*TODO*///	INLINE void clr_di( void )
/*TODO*///	{
/*TODO*///		DIRECT;
/*TODO*///		WM(EAD,0);
/*TODO*///		CLR_NZVC;
/*TODO*///		SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____1x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $10 FLAG */
/*TODO*///
/*TODO*///	/* $11 FLAG */
/*TODO*///
/*TODO*///	/* $12 NOP inherent ----- */
/*TODO*///	INLINE void nop( void )
/*TODO*///	{
/*TODO*///		;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $13 SYNC inherent ----- */
/*TODO*///	INLINE void sync( void )
/*TODO*///	{
/*TODO*///		/* SYNC stops processing instructions until an interrupt request happens. */
/*TODO*///		/* This doesn't require the corresponding interrupt to be enabled: if it */
/*TODO*///		/* is disabled, execution continues with the next instruction. */
/*TODO*///		konami.int_state |= KONAMI_SYNC;
/*TODO*///		CHECK_IRQ_LINES;
/*TODO*///		/* if KONAMI_SYNC has not been cleared by CHECK_IRQ_LINES,
/*TODO*///		 * stop execution until the interrupt lines change. */
/*TODO*///		if( (konami.int_state & KONAMI_SYNC) && konami_ICount > 0 )
/*TODO*///			konami_ICount = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $14 ILLEGAL */
/*TODO*///
/*TODO*///	/* $15 ILLEGAL */
/*TODO*///
/*TODO*///	/* $16 LBRA relative ----- */
/*TODO*///	INLINE void lbra( void )
/*TODO*///	{
/*TODO*///		IMMWORD(ea);
/*TODO*///		PC += EA;
/*TODO*///		change_pc16(PCD);
/*TODO*///
/*TODO*///		/* EHC 980508 speed up busy loop */
/*TODO*///		if( EA == 0xfffd && konami_ICount > 0 )
/*TODO*///			konami_ICount = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $17 LBSR relative ----- */
/*TODO*///	INLINE void lbsr( void )
/*TODO*///	{
/*TODO*///		IMMWORD(ea);
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PC += EA;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $18 ILLEGAL */
/*TODO*///
/*TODO*///	#if 1
/*TODO*///	/* $19 DAA inherent (A) -**0* */
/*TODO*///	INLINE void daa( void )
/*TODO*///	{
/*TODO*///		UINT8 msn, lsn;
/*TODO*///		UINT16 t, cf = 0;
/*TODO*///		msn = A & 0xf0; lsn = A & 0x0f;
/*TODO*///		if( lsn>0x09 || CC & CC_H) cf |= 0x06;
/*TODO*///		if( msn>0x80 && lsn>0x09 ) cf |= 0x60;
/*TODO*///		if( msn>0x90 || CC & CC_C) cf |= 0x60;
/*TODO*///		t = cf + A;
/*TODO*///		CLR_NZV; /* keep carry from previous operation */
/*TODO*///		SET_NZ8((UINT8)t); SET_C8(t);
/*TODO*///		A = t;
/*TODO*///	}
/*TODO*///	#else
/*TODO*///	/* $19 DAA inherent (A) -**0* */
/*TODO*///	INLINE void daa( void )
/*TODO*///	{
/*TODO*///		UINT16 t;
/*TODO*///		t = A;
/*TODO*///		if (CC & CC_H) t+=0x06;
/*TODO*///		if ((t&0x0f)>9) t+=0x06;		/* ASG -- this code is broken! $66+$99=$FF -> DAA should = $65, we get $05! */
/*TODO*///		if (CC & CC_C) t+=0x60;
/*TODO*///		if ((t&0xf0)>0x90) t+=0x60;
/*TODO*///		if (t&0x100) SEC;
/*TODO*///		A = t;
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $1A ORCC immediate ##### */
/*TODO*///	INLINE void orcc( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		CC |= t;
/*TODO*///		CHECK_IRQ_LINES;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1B ILLEGAL */
/*TODO*///
/*TODO*///	/* $1C ANDCC immediate ##### */
/*TODO*///	INLINE void andcc( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		CC &= t;
/*TODO*///		CHECK_IRQ_LINES;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1D SEX inherent -**0- */
/*TODO*///	INLINE void sex( void )
/*TODO*///	{
/*TODO*///		UINT16 t;
/*TODO*///		t = SIGNED(B);
/*TODO*///		D = t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1E EXG inherent ----- */
/*TODO*///	INLINE void exg( void )
/*TODO*///	{
/*TODO*///		UINT16 t1 = 0, t2 = 0;
/*TODO*///		UINT8 tb;
/*TODO*///
/*TODO*///		IMMBYTE(tb);
/*TODO*///
/*TODO*///		GETREG( t1, tb >> 4 );
/*TODO*///		GETREG( t2, tb & 0x0f );
/*TODO*///
/*TODO*///		SETREG( t2, tb >> 4 );
/*TODO*///		SETREG( t1, tb & 0x0f );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1F TFR inherent ----- */
/*TODO*///	INLINE void tfr( void )
/*TODO*///	{
/*TODO*///		UINT8 tb;
/*TODO*///		UINT16 t = 0;
/*TODO*///
/*TODO*///		IMMBYTE(tb);
/*TODO*///
/*TODO*///		GETREG( t, tb & 0x0f );
/*TODO*///		SETREG( t, ( tb >> 4 ) & 0x07 );
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____2x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $20 BRA relative ----- */
/*TODO*///	INLINE void bra( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		PC += SIGNED(t);
/*TODO*///		change_pc16(PCD);
/*TODO*///		/* JB 970823 - speed up busy loops */
/*TODO*///		if( t == 0xfe && konami_ICount > 0 )
/*TODO*///			konami_ICount = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $21 BRN relative ----- */
/*TODO*///	INLINE void brn( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1021 LBRN relative ----- */
/*TODO*///	INLINE void lbrn( void )
/*TODO*///	{
/*TODO*///		IMMWORD(ea);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $22 BHI relative ----- */
/*TODO*///	INLINE void bhi( void )
/*TODO*///	{
/*TODO*///		BRANCH( !(CC & (CC_Z|CC_C)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1022 LBHI relative ----- */
/*TODO*///	INLINE void lbhi( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !(CC & (CC_Z|CC_C)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $23 BLS relative ----- */
/*TODO*///	INLINE void bls( void )
/*TODO*///	{
/*TODO*///		BRANCH( (CC & (CC_Z|CC_C)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1023 LBLS relative ----- */
/*TODO*///	INLINE void lbls( void )
/*TODO*///	{
/*TODO*///		LBRANCH( (CC&(CC_Z|CC_C)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $24 BCC relative ----- */
/*TODO*///	INLINE void bcc( void )
/*TODO*///	{
/*TODO*///		BRANCH( !(CC&CC_C) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1024 LBCC relative ----- */
/*TODO*///	INLINE void lbcc( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !(CC&CC_C) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $25 BCS relative ----- */
/*TODO*///	INLINE void bcs( void )
/*TODO*///	{
/*TODO*///		BRANCH( (CC&CC_C) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1025 LBCS relative ----- */
/*TODO*///	INLINE void lbcs( void )
/*TODO*///	{
/*TODO*///		LBRANCH( (CC&CC_C) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $26 BNE relative ----- */
/*TODO*///	INLINE void bne( void )
/*TODO*///	{
/*TODO*///		BRANCH( !(CC&CC_Z) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1026 LBNE relative ----- */
/*TODO*///	INLINE void lbne( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !(CC&CC_Z) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $27 BEQ relative ----- */
/*TODO*///	INLINE void beq( void )
/*TODO*///	{
/*TODO*///		BRANCH( (CC&CC_Z) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1027 LBEQ relative ----- */
/*TODO*///	INLINE void lbeq( void )
/*TODO*///	{
/*TODO*///		LBRANCH( (CC&CC_Z) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $28 BVC relative ----- */
/*TODO*///	INLINE void bvc( void )
/*TODO*///	{
/*TODO*///		BRANCH( !(CC&CC_V) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1028 LBVC relative ----- */
/*TODO*///	INLINE void lbvc( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !(CC&CC_V) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $29 BVS relative ----- */
/*TODO*///	INLINE void bvs( void )
/*TODO*///	{
/*TODO*///		BRANCH( (CC&CC_V) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1029 LBVS relative ----- */
/*TODO*///	INLINE void lbvs( void )
/*TODO*///	{
/*TODO*///		LBRANCH( (CC&CC_V) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $2A BPL relative ----- */
/*TODO*///	INLINE void bpl( void )
/*TODO*///	{
/*TODO*///		BRANCH( !(CC&CC_N) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $102A LBPL relative ----- */
/*TODO*///	INLINE void lbpl( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !(CC&CC_N) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $2B BMI relative ----- */
/*TODO*///	INLINE void bmi( void )
/*TODO*///	{
/*TODO*///		BRANCH( (CC&CC_N) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $102B LBMI relative ----- */
/*TODO*///	INLINE void lbmi( void )
/*TODO*///	{
/*TODO*///		LBRANCH( (CC&CC_N) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $2C BGE relative ----- */
/*TODO*///	INLINE void bge( void )
/*TODO*///	{
/*TODO*///		BRANCH( !NXORV );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $102C LBGE relative ----- */
/*TODO*///	INLINE void lbge( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !NXORV );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $2D BLT relative ----- */
/*TODO*///	INLINE void blt( void )
/*TODO*///	{
/*TODO*///		BRANCH( NXORV );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $102D LBLT relative ----- */
/*TODO*///	INLINE void lblt( void )
/*TODO*///	{
/*TODO*///		LBRANCH( NXORV );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $2E BGT relative ----- */
/*TODO*///	INLINE void bgt( void )
/*TODO*///	{
/*TODO*///		BRANCH( !(NXORV || (CC&CC_Z)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $102E LBGT relative ----- */
/*TODO*///	INLINE void lbgt( void )
/*TODO*///	{
/*TODO*///		LBRANCH( !(NXORV || (CC&CC_Z)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $2F BLE relative ----- */
/*TODO*///	INLINE void ble( void )
/*TODO*///	{
/*TODO*///		BRANCH( (NXORV || (CC&CC_Z)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $102F LBLE relative ----- */
/*TODO*///	INLINE void lble( void )
/*TODO*///	{
/*TODO*///		LBRANCH( (NXORV || (CC&CC_Z)) );
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____3x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $30 LEAX indexed --*-- */
/*TODO*///	INLINE void leax( void )
/*TODO*///	{
/*TODO*///		X = EA;
/*TODO*///		CLR_Z;
/*TODO*///		SET_Z(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $31 LEAY indexed --*-- */
/*TODO*///	INLINE void leay( void )
/*TODO*///	{
/*TODO*///		Y = EA;
/*TODO*///		CLR_Z;
/*TODO*///		SET_Z(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $32 LEAS indexed ----- */
/*TODO*///	INLINE void leas( void )
/*TODO*///	{
/*TODO*///		S = EA;
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $33 LEAU indexed ----- */
/*TODO*///	INLINE void leau( void )
/*TODO*///	{
/*TODO*///		U = EA;
/*TODO*///	}

	/* $34 PSHS inherent ----- */
	public static opcode pshs = new opcode() {
            public void handler() {
            
		int t;
		t = IMMBYTE();
		if(( t&0x80 ) != 0) { PUSHWORD(pPC()); konami_ICount[0] -= 2; }
		if(( t&0x40 ) != 0) { PUSHWORD(pU());  konami_ICount[0] -= 2; }
		if(( t&0x20 ) != 0) { PUSHWORD(pY());  konami_ICount[0] -= 2; }
		if(( t&0x10 ) != 0) { PUSHWORD(pX());  konami_ICount[0] -= 2; }
		if(( t&0x08 ) != 0) { PUSHBYTE(DP());  konami_ICount[0] -= 1; }
		if(( t&0x04 ) != 0) { PUSHBYTE(B());   konami_ICount[0] -= 1; }
		if(( t&0x02 ) != 0) { PUSHBYTE(A());   konami_ICount[0] -= 1; }
		if(( t&0x01 ) != 0) { PUSHBYTE(CC());  konami_ICount[0] -= 1; }
            }
        };

	/* 35 PULS inherent ----- */
	public static opcode puls = new opcode() {
            public void handler() {
		int t;
		t = IMMBYTE();
		if(( t&0x01 ) != 0) { PULLBYTE(CC()); konami_ICount[0] -= 1; }
		if(( t&0x02 ) != 0) { PULLBYTE(A());  konami_ICount[0] -= 1; }
		if(( t&0x04 ) != 0) { PULLBYTE(B());  konami_ICount[0] -= 1; }
		if(( t&0x08 ) != 0) { PULLBYTE(DP()); konami_ICount[0] -= 1; }
		if(( t&0x10 ) != 0) { PULLWORD(XD()); konami_ICount[0] -= 2; }
		if(( t&0x20 ) != 0) { PULLWORD(YD()); konami_ICount[0] -= 2; }
		if(( t&0x40 ) != 0) { PULLWORD(UD()); konami_ICount[0] -= 2; }
		if(( t&0x80 ) != 0) { PULLWORD(PCD()); change_pc16(PCD()); konami_ICount[0] -= 2; }

		/* check after all PULLs */
		if(( t&0x01 ) != 0) { CHECK_IRQ_LINES(); }
            }
        };

	/* $36 PSHU inherent ----- */
	public static opcode pshu = new opcode() {
            public void handler() {
            	int t;
		t = IMMBYTE();
		if(( t&0x80 ) != 0) { PSHUWORD(pPC()); konami_ICount[0] -= 2; }
		if(( t&0x40 ) != 0) { PSHUWORD(pS());  konami_ICount[0] -= 2; }
		if(( t&0x20 ) != 0) { PSHUWORD(pY());  konami_ICount[0] -= 2; }
		if(( t&0x10 ) != 0) { PSHUWORD(pX());  konami_ICount[0] -= 2; }
		if(( t&0x08 ) != 0) { PSHUBYTE(DP());  konami_ICount[0] -= 1; }
		if(( t&0x04 ) != 0) { PSHUBYTE(B());   konami_ICount[0] -= 1; }
		if(( t&0x02 ) != 0) { PSHUBYTE(A());   konami_ICount[0] -= 1; }
		if(( t&0x01 ) != 0) { PSHUBYTE(CC());  konami_ICount[0] -= 1; }
            }
        };

	/* 37 PULU inherent ----- */
	public static opcode pulu = new opcode() {
            public void handler() {
		int t;
		t = IMMBYTE();
		if(( t&0x01 ) != 0) { PULUBYTE(CC()); konami_ICount[0] -= 1; }
		if(( t&0x02 ) != 0) { PULUBYTE(A());  konami_ICount[0] -= 1; }
		if(( t&0x04 ) != 0) { PULUBYTE(B());  konami_ICount[0] -= 1; }
		if(( t&0x08 ) != 0) { PULUBYTE(DP()); konami_ICount[0] -= 1; }
		if(( t&0x10 ) != 0) { PULUWORD(XD()); konami_ICount[0] -= 2; }
		if(( t&0x20 ) != 0) { PULUWORD(YD()); konami_ICount[0] -= 2; }
		if(( t&0x40 ) != 0) { PULUWORD(SD()); konami_ICount[0] -= 2; }
		if(( t&0x80 ) != 0) { PULUWORD(PCD()); change_pc16(PCD()); konami_ICount[0] -= 2; }

		/* check after all PULLs */
		if(( t&0x01 ) != 0) { CHECK_IRQ_LINES(); }
            }
        };

/*TODO*///	/* $38 ILLEGAL */
/*TODO*///
/*TODO*///	/* $39 RTS inherent ----- */
/*TODO*///	INLINE void rts( void )
/*TODO*///	{
/*TODO*///		PULLWORD(PCD);
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $3A ABX inherent ----- */
/*TODO*///	INLINE void abx( void )
/*TODO*///	{
/*TODO*///		X += B;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $3B RTI inherent ##### */
/*TODO*///	INLINE void rti( void )
/*TODO*///	{
/*TODO*///		PULLBYTE(CC);
/*TODO*///		if( CC & CC_E ) /* entire state saved? */
/*TODO*///		{
/*TODO*///			konami_ICount -= 9;
/*TODO*///			PULLBYTE(A);
/*TODO*///			PULLBYTE(B);
/*TODO*///			PULLBYTE(DP);
/*TODO*///			PULLWORD(XD);
/*TODO*///			PULLWORD(YD);
/*TODO*///			PULLWORD(UD);
/*TODO*///		}
/*TODO*///		PULLWORD(PCD);
/*TODO*///		change_pc16(PCD);
/*TODO*///		CHECK_IRQ_LINES;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $3C CWAI inherent ----1 */
/*TODO*///	INLINE void cwai( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		CC &= t;
/*TODO*///		/*
/*TODO*///		 * CWAI stacks the entire machine state on the hardware stack,
/*TODO*///		 * then waits for an interrupt; when the interrupt is taken
/*TODO*///		 * later, the state is *not* saved again after CWAI.
/*TODO*///		 */
/*TODO*///		CC |= CC_E; 		/* HJB 990225: save entire state */
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PUSHWORD(pU);
/*TODO*///		PUSHWORD(pY);
/*TODO*///		PUSHWORD(pX);
/*TODO*///		PUSHBYTE(DP);
/*TODO*///		PUSHBYTE(B);
/*TODO*///		PUSHBYTE(A);
/*TODO*///		PUSHBYTE(CC);
/*TODO*///		konami.int_state |= KONAMI_CWAI;
/*TODO*///		CHECK_IRQ_LINES;
/*TODO*///		if( (konami.int_state & KONAMI_CWAI) && konami_ICount > 0 )
/*TODO*///			konami_ICount = 0;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $3D MUL inherent --*-@ */
/*TODO*///	INLINE void mul( void )
/*TODO*///	{
/*TODO*///		UINT16 t;
/*TODO*///		t = A * B;
/*TODO*///		CLR_ZC; SET_Z16(t); if(t&0x80) SEC;
/*TODO*///		D = t;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $3E ILLEGAL */
/*TODO*///
/*TODO*///	/* $3F SWI (SWI2 SWI3) absolute indirect ----- */
/*TODO*///	INLINE void swi( void )
/*TODO*///	{
/*TODO*///		CC |= CC_E; 			/* HJB 980225: save entire state */
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PUSHWORD(pU);
/*TODO*///		PUSHWORD(pY);
/*TODO*///		PUSHWORD(pX);
/*TODO*///		PUSHBYTE(DP);
/*TODO*///		PUSHBYTE(B);
/*TODO*///		PUSHBYTE(A);
/*TODO*///		PUSHBYTE(CC);
/*TODO*///		CC |= CC_IF | CC_II;	/* inhibit FIRQ and IRQ */
/*TODO*///		PCD=RM16(0xfffa);
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $103F SWI2 absolute indirect ----- */
/*TODO*///	INLINE void swi2( void )
/*TODO*///	{
/*TODO*///		CC |= CC_E; 			/* HJB 980225: save entire state */
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PUSHWORD(pU);
/*TODO*///		PUSHWORD(pY);
/*TODO*///		PUSHWORD(pX);
/*TODO*///		PUSHBYTE(DP);
/*TODO*///		PUSHBYTE(B);
/*TODO*///		PUSHBYTE(A);
/*TODO*///		PUSHBYTE(CC);
/*TODO*///		PCD=RM16(0xfff4);
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $113F SWI3 absolute indirect ----- */
/*TODO*///	INLINE void swi3( void )
/*TODO*///	{
/*TODO*///		CC |= CC_E; 			/* HJB 980225: save entire state */
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PUSHWORD(pU);
/*TODO*///		PUSHWORD(pY);
/*TODO*///		PUSHWORD(pX);
/*TODO*///		PUSHBYTE(DP);
/*TODO*///		PUSHBYTE(B);
/*TODO*///		PUSHBYTE(A);
/*TODO*///		PUSHBYTE(CC);
/*TODO*///		PCD=RM16(0xfff2);
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____4x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $40 NEGA inherent ?**** */
/*TODO*///	INLINE void nega( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		r = -A;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(0,A,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $41 ILLEGAL */
/*TODO*///
/*TODO*///	/* $42 ILLEGAL */
/*TODO*///
/*TODO*///	/* $43 COMA inherent -**01 */
/*TODO*///	INLINE void coma( void )
/*TODO*///	{
/*TODO*///		A = ~A;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		SEC;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $44 LSRA inherent -0*-* */
/*TODO*///	INLINE void lsra( void )
/*TODO*///	{
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (A & CC_C);
/*TODO*///		A >>= 1;
/*TODO*///		SET_Z8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $45 ILLEGAL */
/*TODO*///
/*TODO*///	/* $46 RORA inherent -**-* */
/*TODO*///	INLINE void rora( void )
/*TODO*///	{
/*TODO*///		UINT8 r;
/*TODO*///		r = (CC & CC_C) << 7;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (A & CC_C);
/*TODO*///		r |= A >> 1;
/*TODO*///		SET_NZ8(r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $47 ASRA inherent ?**-* */
/*TODO*///	INLINE void asra( void )
/*TODO*///	{
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (A & CC_C);
/*TODO*///		A = (A & 0x80) | (A >> 1);
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $48 ASLA inherent ?**** */
/*TODO*///	INLINE void asla( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		r = A << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,A,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $49 ROLA inherent -**** */
/*TODO*///	INLINE void rola( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = A;
/*TODO*///		r = (CC & CC_C) | (t<<1);
/*TODO*///		CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $4A DECA inherent -***- */
/*TODO*///	INLINE void deca( void )
/*TODO*///	{
/*TODO*///		--A;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8D(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $4B ILLEGAL */
/*TODO*///
/*TODO*///	/* $4C INCA inherent -***- */
/*TODO*///	INLINE void inca( void )
/*TODO*///	{
/*TODO*///		++A;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8I(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $4D TSTA inherent -**0- */
/*TODO*///	INLINE void tsta( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $4E ILLEGAL */
/*TODO*///
/*TODO*///	/* $4F CLRA inherent -0100 */
/*TODO*///	INLINE void clra( void )
/*TODO*///	{
/*TODO*///		A = 0;
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____5x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $50 NEGB inherent ?**** */
/*TODO*///	INLINE void negb( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		r = -B;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(0,B,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $51 ILLEGAL */
/*TODO*///
/*TODO*///	/* $52 ILLEGAL */
/*TODO*///
/*TODO*///	/* $53 COMB inherent -**01 */
/*TODO*///	INLINE void comb( void )
/*TODO*///	{
/*TODO*///		B = ~B;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		SEC;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $54 LSRB inherent -0*-* */
/*TODO*///	INLINE void lsrb( void )
/*TODO*///	{
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (B & CC_C);
/*TODO*///		B >>= 1;
/*TODO*///		SET_Z8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $55 ILLEGAL */
/*TODO*///
/*TODO*///	/* $56 RORB inherent -**-* */
/*TODO*///	INLINE void rorb( void )
/*TODO*///	{
/*TODO*///		UINT8 r;
/*TODO*///		r = (CC & CC_C) << 7;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (B & CC_C);
/*TODO*///		r |= B >> 1;
/*TODO*///		SET_NZ8(r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $57 ASRB inherent ?**-* */
/*TODO*///	INLINE void asrb( void )
/*TODO*///	{
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (B & CC_C);
/*TODO*///		B= (B & 0x80) | (B >> 1);
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $58 ASLB inherent ?**** */
/*TODO*///	INLINE void aslb( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		r = B << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,B,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $59 ROLB inherent -**** */
/*TODO*///	INLINE void rolb( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = B;
/*TODO*///		r = CC & CC_C;
/*TODO*///		r |= t << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(t,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $5A DECB inherent -***- */
/*TODO*///	INLINE void decb( void )
/*TODO*///	{
/*TODO*///		--B;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8D(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $5B ILLEGAL */
/*TODO*///
/*TODO*///	/* $5C INCB inherent -***- */
/*TODO*///	INLINE void incb( void )
/*TODO*///	{
/*TODO*///		++B;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8I(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $5D TSTB inherent -**0- */
/*TODO*///	INLINE void tstb( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $5E ILLEGAL */
/*TODO*///
/*TODO*///	/* $5F CLRB inherent -0100 */
/*TODO*///	INLINE void clrb( void )
/*TODO*///	{
/*TODO*///		B = 0;
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____6x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $60 NEG indexed ?**** */
/*TODO*///	INLINE void neg_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 r,t;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = -t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(0,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $61 ILLEGAL */
/*TODO*///
/*TODO*///	/* $62 ILLEGAL */
/*TODO*///
/*TODO*///	/* $63 COM indexed -**01 */
/*TODO*///	INLINE void com_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = ~RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(t);
/*TODO*///		SEC;
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $64 LSR indexed -0*-* */
/*TODO*///	INLINE void lsr_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = RM(EAD);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t & CC_C);
/*TODO*///		t>>=1; SET_Z8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $65 ILLEGAL */
/*TODO*///
/*TODO*///	/* $66 ROR indexed -**-* */
/*TODO*///	INLINE void ror_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = (CC & CC_C) << 7;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t & CC_C);
/*TODO*///		r |= t>>1; SET_NZ8(r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $67 ASR indexed ?**-* */
/*TODO*///	INLINE void asr_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = RM(EAD);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t & CC_C);
/*TODO*///		t=(t&0x80)|(t>>1);
/*TODO*///		SET_NZ8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $68 ASL indexed ?**** */
/*TODO*///	INLINE void asl_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = t << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $69 ROL indexed -**** */
/*TODO*///	INLINE void rol_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = CC & CC_C;
/*TODO*///		r |= t << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $6A DEC indexed -***- */
/*TODO*///	INLINE void dec_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = RM(EAD) - 1;
/*TODO*///		CLR_NZV; SET_FLAGS8D(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $6B ILLEGAL */
/*TODO*///
/*TODO*///	/* $6C INC indexed -***- */
/*TODO*///	INLINE void inc_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = RM(EAD) + 1;
/*TODO*///		CLR_NZV; SET_FLAGS8I(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $6D TST indexed -**0- */
/*TODO*///	INLINE void tst_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $6E JMP indexed ----- */
/*TODO*///	INLINE void jmp_ix( void )
/*TODO*///	{
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $6F CLR indexed -0100 */
/*TODO*///	INLINE void clr_ix( void )
/*TODO*///	{
/*TODO*///		WM(EAD,0);
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____7x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $70 NEG extended ?**** */
/*TODO*///	INLINE void neg_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 r,t;
/*TODO*///		EXTBYTE(t); r=-t;
/*TODO*///		CLR_NZVC; SET_FLAGS8(0,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $71 ILLEGAL */
/*TODO*///
/*TODO*///	/* $72 ILLEGAL */
/*TODO*///
/*TODO*///	/* $73 COM extended -**01 */
/*TODO*///	INLINE void com_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); t = ~t;
/*TODO*///		CLR_NZV; SET_NZ8(t); SEC;
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $74 LSR extended -0*-* */
/*TODO*///	INLINE void lsr_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); CLR_NZC; CC |= (t & CC_C);
/*TODO*///		t>>=1; SET_Z8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $75 ILLEGAL */
/*TODO*///
/*TODO*///	/* $76 ROR extended -**-* */
/*TODO*///	INLINE void ror_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		EXTBYTE(t); r=(CC & CC_C) << 7;
/*TODO*///		CLR_NZC; CC |= (t & CC_C);
/*TODO*///		r |= t>>1; SET_NZ8(r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $77 ASR extended ?**-* */
/*TODO*///	INLINE void asr_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); CLR_NZC; CC |= (t & CC_C);
/*TODO*///		t=(t&0x80)|(t>>1);
/*TODO*///		SET_NZ8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $78 ASL extended ?**** */
/*TODO*///	INLINE void asl_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t); r=t<<1;
/*TODO*///		CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $79 ROL extended -**** */
/*TODO*///	INLINE void rol_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t); r = (CC & CC_C) | (t << 1);
/*TODO*///		CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7A DEC extended -***- */
/*TODO*///	INLINE void dec_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); --t;
/*TODO*///		CLR_NZV; SET_FLAGS8D(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7B ILLEGAL */
/*TODO*///
/*TODO*///	/* $7C INC extended -***- */
/*TODO*///	INLINE void inc_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); ++t;
/*TODO*///		CLR_NZV; SET_FLAGS8I(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7D TST extended -**0- */
/*TODO*///	INLINE void tst_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); CLR_NZV; SET_NZ8(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7E JMP extended ----- */
/*TODO*///	INLINE void jmp_ex( void )
/*TODO*///	{
/*TODO*///		EXTENDED;
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7F CLR extended -0100 */
/*TODO*///	INLINE void clr_ex( void )
/*TODO*///	{
/*TODO*///		EXTENDED;
/*TODO*///		WM(EAD,0);
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____8x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $80 SUBA immediate ?**** */
/*TODO*///	INLINE void suba_im( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $81 CMPA immediate ?**** */
/*TODO*///	INLINE void cmpa_im( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $82 SBCA immediate ?**** */
/*TODO*///	INLINE void sbca_im( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = A - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $83 SUBD (CMPD CMPU) immediate -**** */
/*TODO*///	INLINE void subd_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1083 CMPD immediate -**** */
/*TODO*///	INLINE void cmpd_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1183 CMPU immediate -**** */
/*TODO*///	INLINE void cmpu_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r, d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = U;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $84 ANDA immediate -**0- */
/*TODO*///	INLINE void anda_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		A &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $85 BITA immediate -**0- */
/*TODO*///	INLINE void bita_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = A & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}

	/* $86 LDA immediate -**0- */
	public static opcode lda_im = new opcode() {
            public void handler() {            
		A(IMMBYTE());
		CLR_NZV();
		SET_NZ8(A());
            }
        };

/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $87 STA immediate -**0- */
/*TODO*///	INLINE void sta_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		IMM8;
/*TODO*///		WM(EAD,A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $88 EORA immediate -**0- */
/*TODO*///	INLINE void eora_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		A ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $89 ADCA immediate ***** */
/*TODO*///	INLINE void adca_im( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = A + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $8A ORA immediate -**0- */
/*TODO*///	INLINE void ora_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		A |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}

	/* $8B ADDA immediate ***** */
	public static opcode adda_im = new opcode() {
            public void handler() {            
		int t,r;
		t = IMMBYTE();
		r = A() + t;
		CLR_HNZVC();
		SET_FLAGS8(A(),t,r);
		SET_H(A(),t,r);
		A(r);
            }
        };

/*TODO*///	/* $8C CMPX (CMPY CMPS) immediate -**** */
/*TODO*///	INLINE void cmpx_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = X;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $108C CMPY immediate -**** */
/*TODO*///	INLINE void cmpy_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = Y;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $118C CMPS immediate -**** */
/*TODO*///	INLINE void cmps_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = S;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $8D BSR ----- */
/*TODO*///	INLINE void bsr( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PC += SIGNED(t);
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $8E LDX (LDY) immediate -**0- */
/*TODO*///	INLINE void ldx_im( void )
/*TODO*///	{
/*TODO*///		IMMWORD(pX);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $108E LDY immediate -**0- */
/*TODO*///	INLINE void ldy_im( void )
/*TODO*///	{
/*TODO*///		IMMWORD(pY);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $8F STX (STY) immediate -**0- */
/*TODO*///	INLINE void stx_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $108F STY immediate -**0- */
/*TODO*///	INLINE void sty_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pY);
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____9x____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $90 SUBA direct ?**** */
/*TODO*///	INLINE void suba_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $91 CMPA direct ?**** */
/*TODO*///	INLINE void cmpa_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $92 SBCA direct ?**** */
/*TODO*///	INLINE void sbca_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $93 SUBD (CMPD CMPU) direct -**** */
/*TODO*///	INLINE void subd_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1093 CMPD direct -**** */
/*TODO*///	INLINE void cmpd_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $1193 CMPU direct -**** */
/*TODO*///	INLINE void cmpu_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = U;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(U,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $94 ANDA direct -**0- */
/*TODO*///	INLINE void anda_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		A &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $95 BITA direct -**0- */
/*TODO*///	INLINE void bita_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $96 LDA direct -**0- */
/*TODO*///	INLINE void lda_di( void )
/*TODO*///	{
/*TODO*///		DIRBYTE(A);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $97 STA direct -**0- */
/*TODO*///	INLINE void sta_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		DIRECT;
/*TODO*///		WM(EAD,A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $98 EORA direct -**0- */
/*TODO*///	INLINE void eora_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		A ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $99 ADCA direct ***** */
/*TODO*///	INLINE void adca_di( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9A ORA direct -**0- */
/*TODO*///	INLINE void ora_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		A |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9B ADDA direct ***** */
/*TODO*///	INLINE void adda_di( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A + t;
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9C CMPX (CMPY CMPS) direct -**** */
/*TODO*///	INLINE void cmpx_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = X;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $109C CMPY direct -**** */
/*TODO*///	INLINE void cmpy_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = Y;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $119C CMPS direct -**** */
/*TODO*///	INLINE void cmps_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = S;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9D JSR direct ----- */
/*TODO*///	INLINE void jsr_di( void )
/*TODO*///	{
/*TODO*///		DIRECT;
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9E LDX (LDY) direct -**0- */
/*TODO*///	INLINE void ldx_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pX);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $109E LDY direct -**0- */
/*TODO*///	INLINE void ldy_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pY);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9F STX (STY) direct -**0- */
/*TODO*///	INLINE void stx_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $109F STY direct -**0- */
/*TODO*///	INLINE void sty_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pY);
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____Ax____
/*TODO*///	#endif
/*TODO*///
/*TODO*///
/*TODO*///	/* $a0 SUBA indexed ?**** */
/*TODO*///	INLINE void suba_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a1 CMPA indexed ?**** */
/*TODO*///	INLINE void cmpa_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a2 SBCA indexed ?**** */
/*TODO*///	INLINE void sbca_ix( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = A - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a3 SUBD (CMPD CMPU) indexed -**** */
/*TODO*///	INLINE void subd_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10a3 CMPD indexed -**** */
/*TODO*///	INLINE void cmpd_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $11a3 CMPU indexed -**** */
/*TODO*///	INLINE void cmpu_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		r = U - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(U,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a4 ANDA indexed -**0- */
/*TODO*///	INLINE void anda_ix( void )
/*TODO*///	{
/*TODO*///		A &= RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a5 BITA indexed -**0- */
/*TODO*///	INLINE void bita_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 r;
/*TODO*///		r = A & RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a6 LDA indexed -**0- */
/*TODO*///	INLINE void lda_ix( void )
/*TODO*///	{
/*TODO*///		A = RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a7 STA indexed -**0- */
/*TODO*///	INLINE void sta_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		WM(EAD,A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a8 EORA indexed -**0- */
/*TODO*///	INLINE void eora_ix( void )
/*TODO*///	{
/*TODO*///		A ^= RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $a9 ADCA indexed ***** */
/*TODO*///	INLINE void adca_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = A + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $aA ORA indexed -**0- */
/*TODO*///	INLINE void ora_ix( void )
/*TODO*///	{
/*TODO*///		A |= RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $aB ADDA indexed ***** */
/*TODO*///	INLINE void adda_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = A + t;
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $aC CMPX (CMPY CMPS) indexed -**** */
/*TODO*///	INLINE void cmpx_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		d = X;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10aC CMPY indexed -**** */
/*TODO*///	INLINE void cmpy_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		d = Y;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $11aC CMPS indexed -**** */
/*TODO*///	INLINE void cmps_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		d = S;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $aD JSR indexed ----- */
/*TODO*///	INLINE void jsr_ix( void )
/*TODO*///	{
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $aE LDX (LDY) indexed -**0- */
/*TODO*///	INLINE void ldx_ix( void )
/*TODO*///	{
/*TODO*///		X=RM16(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10aE LDY indexed -**0- */
/*TODO*///	INLINE void ldy_ix( void )
/*TODO*///	{
/*TODO*///		Y=RM16(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $aF STX (STY) indexed -**0- */
/*TODO*///	INLINE void stx_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10aF STY indexed -**0- */
/*TODO*///	INLINE void sty_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///		WM16(EAD,&pY);
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____Bx____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $b0 SUBA extended ?**** */
/*TODO*///	INLINE void suba_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b1 CMPA extended ?**** */
/*TODO*///	INLINE void cmpa_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b2 SBCA extended ?**** */
/*TODO*///	INLINE void sbca_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b3 SUBD (CMPD CMPU) extended -**** */
/*TODO*///	INLINE void subd_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10b3 CMPD extended -**** */
/*TODO*///	INLINE void cmpd_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $11b3 CMPU extended -**** */
/*TODO*///	INLINE void cmpu_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = U;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b4 ANDA extended -**0- */
/*TODO*///	INLINE void anda_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		A &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b5 BITA extended -**0- */
/*TODO*///	INLINE void bita_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A & t;
/*TODO*///		CLR_NZV; SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b6 LDA extended -**0- */
/*TODO*///	INLINE void lda_ex( void )
/*TODO*///	{
/*TODO*///		EXTBYTE(A);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b7 STA extended -**0- */
/*TODO*///	INLINE void sta_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		EXTENDED;
/*TODO*///		WM(EAD,A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b8 EORA extended -**0- */
/*TODO*///	INLINE void eora_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		A ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b9 ADCA extended ***** */
/*TODO*///	INLINE void adca_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bA ORA extended -**0- */
/*TODO*///	INLINE void ora_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		A |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bB ADDA extended ***** */
/*TODO*///	INLINE void adda_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A + t;
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///		SET_H(A,t,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bC CMPX (CMPY CMPS) extended -**** */
/*TODO*///	INLINE void cmpx_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = X;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10bC CMPY extended -**** */
/*TODO*///	INLINE void cmpy_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = Y;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $11bC CMPS extended -**** */
/*TODO*///	INLINE void cmps_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = S;
/*TODO*///		r = d - b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bD JSR extended ----- */
/*TODO*///	INLINE void jsr_ex( void )
/*TODO*///	{
/*TODO*///		EXTENDED;
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bE LDX (LDY) extended -**0- */
/*TODO*///	INLINE void ldx_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pX);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10bE LDY extended -**0- */
/*TODO*///	INLINE void ldy_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pY);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bF STX (STY) extended -**0- */
/*TODO*///	INLINE void stx_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10bF STY extended -**0- */
/*TODO*///	INLINE void sty_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pY);
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____Cx____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $c0 SUBB immediate ?**** */
/*TODO*///	INLINE void subb_im( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c1 CMPB immediate ?**** */
/*TODO*///	INLINE void cmpb_im( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC; SET_FLAGS8(B,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c2 SBCB immediate ?**** */
/*TODO*///	INLINE void sbcb_im( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = B - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c3 ADDD immediate -**** */
/*TODO*///	INLINE void addd_im( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		IMMWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d + b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c4 ANDB immediate -**0- */
/*TODO*///	INLINE void andb_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		B &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c5 BITB immediate -**0- */
/*TODO*///	INLINE void bitb_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = B & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}

	/* $c6 LDB immediate -**0- */
	public static opcode ldb_im = new opcode() {
            public void handler() {            
		B( IMMBYTE() );
		CLR_NZV();
		SET_NZ8(B());
            }
        };

/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $c7 STB immediate -**0- */
/*TODO*///	INLINE void stb_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		IMM8;
/*TODO*///		WM(EAD,B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c8 EORB immediate -**0- */
/*TODO*///	INLINE void eorb_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		B ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $c9 ADCB immediate ***** */
/*TODO*///	INLINE void adcb_im( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		IMMBYTE(t);
/*TODO*///		r = B + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $cA ORB immediate -**0- */
/*TODO*///	INLINE void orb_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///		B |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}

	/* $cB ADDB immediate ***** */
	public static opcode addb_im = new opcode() {
            public void handler() {            
		int t,r;
		t = IMMBYTE();
		r = B() + t;
		CLR_HNZVC();
		SET_FLAGS8(B(),t,r);
		SET_H(B(),t,r);
		B( r );
            }
        };

/*TODO*///	/* $cC LDD immediate -**0- */
/*TODO*///	INLINE void ldd_im( void )
/*TODO*///	{
/*TODO*///		IMMWORD(pD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $cD STD immediate -**0- */
/*TODO*///	INLINE void std_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $cE LDU (LDS) immediate -**0- */
/*TODO*///	INLINE void ldu_im( void )
/*TODO*///	{
/*TODO*///		IMMWORD(pU);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10cE LDS immediate -**0- */
/*TODO*///	INLINE void lds_im( void )
/*TODO*///	{
/*TODO*///		IMMWORD(pS);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $cF STU (STS) immediate -**0- */
/*TODO*///	INLINE void stu_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $10cF STS immediate -**0- */
/*TODO*///	INLINE void sts_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pS);
/*TODO*///	}
/*TODO*///
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____Dx____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $d0 SUBB direct ?**** */
/*TODO*///	INLINE void subb_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d1 CMPB direct ?**** */
/*TODO*///	INLINE void cmpb_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d2 SBCB direct ?**** */
/*TODO*///	INLINE void sbcb_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d3 ADDD direct -**** */
/*TODO*///	INLINE void addd_di( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		DIRWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d + b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d4 ANDB direct -**0- */
/*TODO*///	INLINE void andb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		B &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d5 BITB direct -**0- */
/*TODO*///	INLINE void bitb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d6 LDB direct -**0- */
/*TODO*///	INLINE void ldb_di( void )
/*TODO*///	{
/*TODO*///		DIRBYTE(B);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d7 STB direct -**0- */
/*TODO*///	INLINE void stb_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		DIRECT;
/*TODO*///		WM(EAD,B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d8 EORB direct -**0- */
/*TODO*///	INLINE void eorb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		B ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d9 ADCB direct ***** */
/*TODO*///	INLINE void adcb_di( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dA ORB direct -**0- */
/*TODO*///	INLINE void orb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		B |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dB ADDB direct ***** */
/*TODO*///	INLINE void addb_di( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B + t;
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dC LDD direct -**0- */
/*TODO*///	INLINE void ldd_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dD STD direct -**0- */
/*TODO*///	INLINE void std_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dE LDU (LDS) direct -**0- */
/*TODO*///	INLINE void ldu_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pU);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10dE LDS direct -**0- */
/*TODO*///	INLINE void lds_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pS);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dF STU (STS) direct -**0- */
/*TODO*///	INLINE void stu_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10dF STS direct -**0- */
/*TODO*///	INLINE void sts_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pS);
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____Ex____
/*TODO*///	#endif
/*TODO*///
/*TODO*///
/*TODO*///	/* $e0 SUBB indexed ?**** */
/*TODO*///	INLINE void subb_ix( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e1 CMPB indexed ?**** */
/*TODO*///	INLINE void cmpb_ix( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e2 SBCB indexed ?**** */
/*TODO*///	INLINE void sbcb_ix( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = B - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e3 ADDD indexed -**** */
/*TODO*///	INLINE void addd_ix( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		b.d=RM16(EAD);
/*TODO*///		d = D;
/*TODO*///		r = d + b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e4 ANDB indexed -**0- */
/*TODO*///	INLINE void andb_ix( void )
/*TODO*///	{
/*TODO*///		B &= RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e5 BITB indexed -**0- */
/*TODO*///	INLINE void bitb_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 r;
/*TODO*///		r = B & RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e6 LDB indexed -**0- */
/*TODO*///	INLINE void ldb_ix( void )
/*TODO*///	{
/*TODO*///		B = RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e7 STB indexed -**0- */
/*TODO*///	INLINE void stb_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		WM(EAD,B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e8 EORB indexed -**0- */
/*TODO*///	INLINE void eorb_ix( void )
/*TODO*///	{
/*TODO*///		B ^= RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $e9 ADCB indexed ***** */
/*TODO*///	INLINE void adcb_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = B + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $eA ORB indexed -**0- */
/*TODO*///	INLINE void orb_ix( void )
/*TODO*///	{
/*TODO*///		B |= RM(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $eb ADDB indexed ***** */
/*TODO*///	INLINE void addb_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		t = RM(EAD);
/*TODO*///		r = B + t;
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $ec LDD indexed -**0- */
/*TODO*///	INLINE void ldd_ix( void )
/*TODO*///	{
/*TODO*///		D=RM16(EAD);
/*TODO*///		CLR_NZV; SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $eD STD indexed -**0- */
/*TODO*///	INLINE void std_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $eE LDU (LDS) indexed -**0- */
/*TODO*///	INLINE void ldu_ix( void )
/*TODO*///	{
/*TODO*///		U=RM16(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10eE LDS indexed -**0- */
/*TODO*///	INLINE void lds_ix( void )
/*TODO*///	{
/*TODO*///		S=RM16(EAD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $eF STU (STS) indexed -**0- */
/*TODO*///	INLINE void stu_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10eF STS indexed -**0- */
/*TODO*///	INLINE void sts_ix( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		WM16(EAD,&pS);
/*TODO*///	}
/*TODO*///
/*TODO*///	#ifdef macintosh
/*TODO*///	#pragma mark ____Fx____
/*TODO*///	#endif
/*TODO*///
/*TODO*///	/* $f0 SUBB extended ?**** */
/*TODO*///	INLINE void subb_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f1 CMPB extended ?**** */
/*TODO*///	INLINE void cmpb_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f2 SBCB extended ?**** */
/*TODO*///	INLINE void sbcb_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B - t - (CC & CC_C);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f3 ADDD extended -**** */
/*TODO*///	INLINE void addd_ex( void )
/*TODO*///	{
/*TODO*///		UINT32 r,d;
/*TODO*///		PAIR b;
/*TODO*///		EXTWORD(b);
/*TODO*///		d = D;
/*TODO*///		r = d + b.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(d,b.d,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f4 ANDB extended -**0- */
/*TODO*///	INLINE void andb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		B &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f5 BITB extended -**0- */
/*TODO*///	INLINE void bitb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f6 LDB extended -**0- */
/*TODO*///	INLINE void ldb_ex( void )
/*TODO*///	{
/*TODO*///		EXTBYTE(B);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f7 STB extended -**0- */
/*TODO*///	INLINE void stb_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		EXTENDED;
/*TODO*///		WM(EAD,B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f8 EORB extended -**0- */
/*TODO*///	INLINE void eorb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		B ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f9 ADCB extended ***** */
/*TODO*///	INLINE void adcb_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B + t + (CC & CC_C);
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fA ORB extended -**0- */
/*TODO*///	INLINE void orb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		B |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fB ADDB extended ***** */
/*TODO*///	INLINE void addb_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B + t;
/*TODO*///		CLR_HNZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///		SET_H(B,t,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fC LDD extended -**0- */
/*TODO*///	INLINE void ldd_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fD STD extended -**0- */
/*TODO*///	INLINE void std_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fE LDU (LDS) extended -**0- */
/*TODO*///	INLINE void ldu_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pU);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10fE LDS extended -**0- */
/*TODO*///	INLINE void lds_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pS);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fF STU (STS) extended -**0- */
/*TODO*///	INLINE void stu_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10fF STS extended -**0- */
/*TODO*///	INLINE void sts_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pS);
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void setline_im( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		IMMBYTE(t);
/*TODO*///
/*TODO*///		if ( konami_cpu_setlines_callback )
/*TODO*///			(*konami_cpu_setlines_callback)( t );
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void setline_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		t = RM(EA);
/*TODO*///
/*TODO*///		if ( konami_cpu_setlines_callback )
/*TODO*///			(*konami_cpu_setlines_callback)( t );
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void setline_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///
/*TODO*///		if ( konami_cpu_setlines_callback )
/*TODO*///			(*konami_cpu_setlines_callback)( t );
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void setline_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		if ( konami_cpu_setlines_callback )
/*TODO*///			(*konami_cpu_setlines_callback)( t );
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void bmove( void )
/*TODO*///	{
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		while( U != 0 ) {
/*TODO*///			t = RM(Y);
/*TODO*///			WM(X,t);
/*TODO*///			Y++;
/*TODO*///			X++;
/*TODO*///			U--;
/*TODO*///			konami_ICount -= 2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void move( void )
/*TODO*///	{
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		t = RM(Y);
/*TODO*///		WM(X,t);
/*TODO*///		Y++;
/*TODO*///		X++;
/*TODO*///		U--;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* CLRD inherent -0100 */
/*TODO*///	INLINE void clrd( void )
/*TODO*///	{
/*TODO*///		D = 0;
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* CLRW indexed -0100 */
/*TODO*///	INLINE void clrw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		t.d = 0;
/*TODO*///		WM16(EAD,&t);
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* CLRW direct -0100 */
/*TODO*///	INLINE void clrw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		t.d = 0;
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&t);
/*TODO*///		CLR_NZVC;
/*TODO*///		SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* CLRW extended -0100 */
/*TODO*///	INLINE void clrw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		t.d = 0;
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&t);
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRD immediate -0*-* */
/*TODO*///	INLINE void lsrd( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		IMMBYTE( t );
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D >>= 1;
/*TODO*///			SET_Z16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORD immediate -**-* */
/*TODO*///	INLINE void rord( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		IMMBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = (CC & CC_C) << 15;
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			r |= D >> 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRD immediate ?**-* */
/*TODO*///	INLINE void asrd( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		IMMBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D = (D & 0x8000) | (D >> 1);
/*TODO*///			SET_NZ16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLD immediate ?**** */
/*TODO*///	INLINE void asld( void )
/*TODO*///	{
/*TODO*///		UINT32	r;
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		IMMBYTE( t );
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = D << 1;
/*TODO*///			CLR_NZVC;
/*TODO*///			SET_FLAGS16(D,D,r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLD immediate -**-* */
/*TODO*///	INLINE void rold( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		IMMBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			if ( D & 0x8000 ) SEC;
/*TODO*///			r = CC & CC_C;
/*TODO*///			r |= D << 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DECB,JNZ relative ----- */
/*TODO*///	INLINE void decbjnz( void )
/*TODO*///	{
/*TODO*///		--B;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS8D(B);
/*TODO*///		BRANCH( !(CC&CC_Z) );
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DECX,JNZ relative ----- */
/*TODO*///	INLINE void decxjnz( void )
/*TODO*///	{
/*TODO*///		--X;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);	/* should affect V as well? */
/*TODO*///		BRANCH( !(CC&CC_Z) );
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void bset( void )
/*TODO*///	{
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		while( U != 0 ) {
/*TODO*///			t = A;
/*TODO*///			WM(XD,t);
/*TODO*///			X++;
/*TODO*///			U--;
/*TODO*///			konami_ICount -= 2;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	INLINE void bset2( void )
/*TODO*///	{
/*TODO*///		while( U != 0 ) {
/*TODO*///			WM16(XD,&pD);
/*TODO*///			X += 2;
/*TODO*///			U--;
/*TODO*///			konami_ICount -= 3;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LMUL inherent --*-@ */
/*TODO*///	INLINE void lmul( void )
/*TODO*///	{
/*TODO*///		UINT32 t;
/*TODO*///		t = X * Y;
/*TODO*///		X = (t >> 16);
/*TODO*///		Y = (t & 0xffff);
/*TODO*///		CLR_ZC; SET_Z(t); if( t & 0x8000 ) SEC;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DIVX inherent --*-@ */
/*TODO*///	INLINE void divx( void )
/*TODO*///	{
/*TODO*///		UINT16 t;
/*TODO*///		UINT8 r;
/*TODO*///		if ( B != 0 )
/*TODO*///		{
/*TODO*///			t = X / B;
/*TODO*///			r = X % B;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* ?? */
/*TODO*///			t = 0;
/*TODO*///			r = 0;
/*TODO*///		}
/*TODO*///		CLR_ZC; SET_Z16(t); if ( t & 0x80 ) SEC;
/*TODO*///		X = t;
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* INCD inherent -***- */
/*TODO*///	INLINE void incd( void )
/*TODO*///	{
/*TODO*///		UINT32 r;
/*TODO*///		r = D + 1;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(D,D,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* INCW direct -***- */
/*TODO*///	INLINE void incw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r = t;
/*TODO*///		++r.d;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(t.d, t.d, r.d);;
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* INCW indexed -***- */
/*TODO*///	INLINE void incw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		r = t;
/*TODO*///		++r.d;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(t.d, t.d, r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* INCW extended -***- */
/*TODO*///	INLINE void incw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t, r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r = t;
/*TODO*///		++r.d;
/*TODO*///		CLR_NZV; SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DECD inherent -***- */
/*TODO*///	INLINE void decd( void )
/*TODO*///	{
/*TODO*///		UINT32 r;
/*TODO*///		r = D - 1;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(D,D,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DECW direct -***- */
/*TODO*///	INLINE void decw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r = t;
/*TODO*///		--r.d;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(t.d, t.d, r.d);;
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DECW indexed -***- */
/*TODO*///	INLINE void decw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t, r;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		r = t;
/*TODO*///		--r.d;
/*TODO*///		CLR_NZV; SET_FLAGS16(t.d, t.d, r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* DECW extended -***- */
/*TODO*///	INLINE void decw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t, r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r = t;
/*TODO*///		--r.d;
/*TODO*///		CLR_NZV; SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* TSTD inherent -**0- */
/*TODO*///	INLINE void tstd( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* TSTW direct -**0- */
/*TODO*///	INLINE void tstw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		CLR_NZV;
/*TODO*///		DIRWORD(t);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* TSTW indexed -**0- */
/*TODO*///	INLINE void tstw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		CLR_NZV;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* TSTW extended -**0- */
/*TODO*///	INLINE void tstw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		CLR_NZV;
/*TODO*///		EXTWORD(t);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRW direct -0*-* */
/*TODO*///	INLINE void lsrw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		DIRWORD(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d >>= 1;
/*TODO*///		SET_Z16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRW indexed -0*-* */
/*TODO*///	INLINE void lsrw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d >>= 1;
/*TODO*///		SET_Z16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRW extended -0*-* */
/*TODO*///	INLINE void lsrw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		EXTWORD(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d >>= 1;
/*TODO*///		SET_Z16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORW direct -**-* */
/*TODO*///	INLINE void rorw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = (CC & CC_C) << 15;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		r.d |= t.d>>1;
/*TODO*///		SET_NZ16(r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORW indexed -**-* */
/*TODO*///	INLINE void rorw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		r.d = (CC & CC_C) << 15;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		r.d |= t.d>>1;
/*TODO*///		SET_NZ16(r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORW extended -**-* */
/*TODO*///	INLINE void rorw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r.d = (CC & CC_C) << 15;
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		r.d |= t.d>>1;
/*TODO*///		SET_NZ16(r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRW direct ?**-* */
/*TODO*///	INLINE void asrw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		DIRWORD(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d = (t.d & 0x8000) | (t.d >> 1);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRW indexed ?**-* */
/*TODO*///	INLINE void asrw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d = (t.d & 0x8000) | (t.d >> 1);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRW extended ?**-* */
/*TODO*///	INLINE void asrw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		EXTWORD(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d = (t.d & 0x8000) | (t.d >> 1);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLW direct ?**** */
/*TODO*///	INLINE void aslw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = t.d << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLW indexed ?**** */
/*TODO*///	INLINE void aslw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		r.d = t.d << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLW extended ?**** */
/*TODO*///	INLINE void aslw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r.d = t.d << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLW direct -**** */
/*TODO*///	INLINE void rolw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = (CC & CC_C) | (t.d << 1);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLW indexed -**** */
/*TODO*///	INLINE void rolw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		r.d = (CC & CC_C) | (t.d << 1);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLW extended -**** */
/*TODO*///	INLINE void rolw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r.d = (CC & CC_C) | (t.d << 1);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* NEGD inherent ?**** */
/*TODO*///	INLINE void negd( void )
/*TODO*///	{
/*TODO*///		UINT32 r;
/*TODO*///		r = -D;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,D,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* NEGW direct ?**** */
/*TODO*///	INLINE void negw_di( void )
/*TODO*///	{
/*TODO*///		PAIR r,t;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = -t.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* NEGW indexed ?**** */
/*TODO*///	INLINE void negw_ix( void )
/*TODO*///	{
/*TODO*///		PAIR r,t;
/*TODO*///		t.d=RM16(EAD);
/*TODO*///		r.d = -t.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* NEGW extended ?**** */
/*TODO*///	INLINE void negw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR r,t;
/*TODO*///		EXTWORD(t);
/*TODO*///		r.d = -t.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ABSA inherent ?**** */
/*TODO*///	INLINE void absa( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		if (A & 0x80)
/*TODO*///			r = -A;
/*TODO*///		else
/*TODO*///			r = A;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(0,A,r);
/*TODO*///		A = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ABSB inherent ?**** */
/*TODO*///	INLINE void absb( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		if (B & 0x80)
/*TODO*///			r = -B;
/*TODO*///		else
/*TODO*///			r = B;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(0,B,r);
/*TODO*///		B = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ABSD inherent ?**** */
/*TODO*///	INLINE void absd( void )
/*TODO*///	{
/*TODO*///		UINT32 r;
/*TODO*///		if (D & 0x8000)
/*TODO*///			r = -D;
/*TODO*///		else
/*TODO*///			r = D;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,D,r);
/*TODO*///		D = r;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRD direct -0*-* */
/*TODO*///	INLINE void lsrd_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		DIRBYTE( t );
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D >>= 1;
/*TODO*///			SET_Z16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORD direct -**-* */
/*TODO*///	INLINE void rord_di( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		DIRBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = (CC & CC_C) << 15;
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			r |= D >> 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRD direct ?**-* */
/*TODO*///	INLINE void asrd_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		DIRBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D = (D & 0x8000) | (D >> 1);
/*TODO*///			SET_NZ16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLD direct ?**** */
/*TODO*///	INLINE void asld_di( void )
/*TODO*///	{
/*TODO*///		UINT32	r;
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		DIRBYTE( t );
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = D << 1;
/*TODO*///			CLR_NZVC;
/*TODO*///			SET_FLAGS16(D,D,r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLD direct -**-* */
/*TODO*///	INLINE void rold_di( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		DIRBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			if ( D & 0x8000 ) SEC;
/*TODO*///			r = CC & CC_C;
/*TODO*///			r |= D << 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRD indexed -0*-* */
/*TODO*///	INLINE void lsrd_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		t=RM(EA);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D >>= 1;
/*TODO*///			SET_Z16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORD indexed -**-* */
/*TODO*///	INLINE void rord_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		t=RM(EA);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = (CC & CC_C) << 15;
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			r |= D >> 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRD indexed ?**-* */
/*TODO*///	INLINE void asrd_ix( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		t=RM(EA);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D = (D & 0x8000) | (D >> 1);
/*TODO*///			SET_NZ16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLD indexed ?**** */
/*TODO*///	INLINE void asld_ix( void )
/*TODO*///	{
/*TODO*///		UINT32	r;
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		t=RM(EA);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = D << 1;
/*TODO*///			CLR_NZVC;
/*TODO*///			SET_FLAGS16(D,D,r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLD indexed -**-* */
/*TODO*///	INLINE void rold_ix( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		t=RM(EA);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			if ( D & 0x8000 ) SEC;
/*TODO*///			r = CC & CC_C;
/*TODO*///			r |= D << 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRD extended -0*-* */
/*TODO*///	INLINE void lsrd_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D >>= 1;
/*TODO*///			SET_Z16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* RORD extended -**-* */
/*TODO*///	INLINE void rord_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = (CC & CC_C) << 15;
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			r |= D >> 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASRD extended ?**-* */
/*TODO*///	INLINE void asrd_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			CC |= (D & CC_C);
/*TODO*///			D = (D & 0x8000) | (D >> 1);
/*TODO*///			SET_NZ16(D);
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ASLD extended ?**** */
/*TODO*///	INLINE void asld_ex( void )
/*TODO*///	{
/*TODO*///		UINT32	r;
/*TODO*///		UINT8	t;
/*TODO*///
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			r = D << 1;
/*TODO*///			CLR_NZVC;
/*TODO*///			SET_FLAGS16(D,D,r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///	/* ROLD extended -**-* */
/*TODO*///	INLINE void rold_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 r;
/*TODO*///		UINT8  t;
/*TODO*///
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		while ( t-- ) {
/*TODO*///			CLR_NZC;
/*TODO*///			if ( D & 0x8000 ) SEC;
/*TODO*///			r = CC & CC_C;
/*TODO*///			r |= D << 1;
/*TODO*///			SET_NZ16(r);
/*TODO*///			D = r;
/*TODO*///		}
/*TODO*///	}
/*TODO*///
	public static opcode opcode2 = new opcode() {
            public void handler() {
                System.out.println("opcode2 NOT DEFINED!!!!");
/*TODO*///		UINT8 ireg2 = ROP_ARG(PCD);
/*TODO*///		PC++;
/*TODO*///
/*TODO*///		switch ( ireg2 ) {
/*TODO*///	//	case 0x00: EA=0; break; /* auto increment */
/*TODO*///	//	case 0x01: EA=0; break; /* double auto increment */
/*TODO*///	//	case 0x02: EA=0; break; /* auto decrement */
/*TODO*///	//	case 0x03: EA=0; break; /* double auto decrement */
/*TODO*///	//	case 0x04: EA=0; break; /* postbyte offs */
/*TODO*///	//	case 0x05: EA=0; break; /* postword offs */
/*TODO*///	//	case 0x06: EA=0; break; /* normal */
/*TODO*///		case 0x07:
/*TODO*///			EAD=0;
/*TODO*///			(*konami_extended[konami.ireg])();
/*TODO*///			konami_ICount -= 2;
/*TODO*///			return;
/*TODO*///	//	case 0x08: EA=0; break; /* indirect - auto increment */
/*TODO*///	//	case 0x09: EA=0; break; /* indirect - double auto increment */
/*TODO*///	//	case 0x0a: EA=0; break; /* indirect - auto decrement */
/*TODO*///	//	case 0x0b: EA=0; break; /* indirect - double auto decrement */
/*TODO*///	//	case 0x0c: EA=0; break; /* indirect - postbyte offs */
/*TODO*///	//	case 0x0d: EA=0; break; /* indirect - postword offs */
/*TODO*///	//	case 0x0e: EA=0; break; /* indirect - normal */
/*TODO*///		case 0x0f:				/* indirect - extended */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0x10: EA=0; break; /* auto increment */
/*TODO*///	//	case 0x11: EA=0; break; /* double auto increment */
/*TODO*///	//	case 0x12: EA=0; break; /* auto decrement */
/*TODO*///	//	case 0x13: EA=0; break; /* double auto decrement */
/*TODO*///	//	case 0x14: EA=0; break; /* postbyte offs */
/*TODO*///	//	case 0x15: EA=0; break; /* postword offs */
/*TODO*///	//	case 0x16: EA=0; break; /* normal */
/*TODO*///	//	case 0x17: EA=0; break; /* extended */
/*TODO*///	//	case 0x18: EA=0; break; /* indirect - auto increment */
/*TODO*///	//	case 0x19: EA=0; break; /* indirect - double auto increment */
/*TODO*///	//	case 0x1a: EA=0; break; /* indirect - auto decrement */
/*TODO*///	//	case 0x1b: EA=0; break; /* indirect - double auto decrement */
/*TODO*///	//	case 0x1c: EA=0; break; /* indirect - postbyte offs */
/*TODO*///	//	case 0x1d: EA=0; break; /* indirect - postword offs */
/*TODO*///	//	case 0x1e: EA=0; break; /* indirect - normal */
/*TODO*///	//	case 0x1f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	/* base X */
/*TODO*///		case 0x20:              /* auto increment */
/*TODO*///			EA=X;
/*TODO*///			X++;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x21:				/* double auto increment */
/*TODO*///			EA=X;
/*TODO*///			X+=2;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x22:				/* auto decrement */
/*TODO*///			X--;
/*TODO*///			EA=X;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x23:				/* double auto decrement */
/*TODO*///			X-=2;
/*TODO*///			EA=X;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x24:				/* postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=X+SIGNED(EA);
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x25:				/* postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=X;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x26:				/* normal */
/*TODO*///			EA=X;
/*TODO*///			break;
/*TODO*///	//	case 0x27: EA=0; break; /* extended */
/*TODO*///		case 0x28:				/* indirect - auto increment */
/*TODO*///			EA=X;
/*TODO*///			X++;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x29:				/* indirect - double auto increment */
/*TODO*///			EA=X;
/*TODO*///			X+=2;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x2a:				/* indirect - auto decrement */
/*TODO*///			X--;
/*TODO*///			EA=X;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x2b:				/* indirect - double auto decrement */
/*TODO*///			X-=2;
/*TODO*///			EA=X;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x2c:				/* indirect - postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=X+SIGNED(EA);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x2d:				/* indirect - postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=X;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0x2e:				/* indirect - normal */
/*TODO*///			EA=X;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///	//	case 0x2f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	/* base Y */
/*TODO*///		case 0x30:              /* auto increment */
/*TODO*///			EA=Y;
/*TODO*///			Y++;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x31:				/* double auto increment */
/*TODO*///			EA=Y;
/*TODO*///			Y+=2;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x32:				/* auto decrement */
/*TODO*///			Y--;
/*TODO*///			EA=Y;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x33:				/* double auto decrement */
/*TODO*///			Y-=2;
/*TODO*///			EA=Y;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x34:				/* postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=Y+SIGNED(EA);
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x35:				/* postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=Y;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x36:				/* normal */
/*TODO*///			EA=Y;
/*TODO*///			break;
/*TODO*///	//	case 0x37: EA=0; break; /* extended */
/*TODO*///		case 0x38:				/* indirect - auto increment */
/*TODO*///			EA=Y;
/*TODO*///			Y++;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x39:				/* indirect - double auto increment */
/*TODO*///			EA=Y;
/*TODO*///			Y+=2;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x3a:				/* indirect - auto decrement */
/*TODO*///			Y--;
/*TODO*///			EA=Y;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x3b:				/* indirect - double auto decrement */
/*TODO*///			Y-=2;
/*TODO*///			EA=Y;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x3c:				/* indirect - postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=Y+SIGNED(EA);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x3d:				/* indirect - postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=Y;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0x3e:				/* indirect - normal */
/*TODO*///			EA=Y;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///	//	case 0x3f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	//  case 0x40: EA=0; break; /* auto increment */
/*TODO*///	//	case 0x41: EA=0; break; /* double auto increment */
/*TODO*///	//	case 0x42: EA=0; break; /* auto decrement */
/*TODO*///	//	case 0x43: EA=0; break; /* double auto decrement */
/*TODO*///	//	case 0x44: EA=0; break; /* postbyte offs */
/*TODO*///	//	case 0x45: EA=0; break; /* postword offs */
/*TODO*///	//	case 0x46: EA=0; break; /* normal */
/*TODO*///	//	case 0x47: EA=0; break; /* extended */
/*TODO*///	//	case 0x48: EA=0; break; /* indirect - auto increment */
/*TODO*///	//	case 0x49: EA=0; break; /* indirect - double auto increment */
/*TODO*///	//	case 0x4a: EA=0; break; /* indirect - auto decrement */
/*TODO*///	//	case 0x4b: EA=0; break; /* indirect - double auto decrement */
/*TODO*///	//	case 0x4c: EA=0; break; /* indirect - postbyte offs */
/*TODO*///	//	case 0x4d: EA=0; break; /* indirect - postword offs */
/*TODO*///	//	case 0x4e: EA=0; break; /* indirect - normal */
/*TODO*///	//	case 0x4f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	/* base U */
/*TODO*///		case 0x50:              /* auto increment */
/*TODO*///			EA=U;
/*TODO*///			U++;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x51:				/* double auto increment */
/*TODO*///			EA=U;
/*TODO*///			U+=2;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x52:				/* auto decrement */
/*TODO*///			U--;
/*TODO*///			EA=U;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x53:				/* double auto decrement */
/*TODO*///			U-=2;
/*TODO*///			EA=U;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x54:				/* postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=U+SIGNED(EA);
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x55:				/* postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=U;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x56:				/* normal */
/*TODO*///			EA=U;
/*TODO*///			break;
/*TODO*///	//	case 0x57: EA=0; break; /* extended */
/*TODO*///		case 0x58:				/* indirect - auto increment */
/*TODO*///			EA=U;
/*TODO*///			U++;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x59:				/* indirect - double auto increment */
/*TODO*///			EA=U;
/*TODO*///			U+=2;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x5a:				/* indirect - auto decrement */
/*TODO*///			U--;
/*TODO*///			EA=U;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x5b:				/* indirect - double auto decrement */
/*TODO*///			U-=2;
/*TODO*///			EA=U;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x5c:				/* indirect - postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=U+SIGNED(EA);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x5d:				/* indirect - postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=U;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0x5e:				/* indirect - normal */
/*TODO*///			EA=U;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///	//	case 0x5f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	/* base S */
/*TODO*///		case 0x60:              /* auto increment */
/*TODO*///			EAD=SD;
/*TODO*///			S++;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x61:				/* double auto increment */
/*TODO*///			EAD=SD;
/*TODO*///			S+=2;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x62:				/* auto decrement */
/*TODO*///			S--;
/*TODO*///			EAD=SD;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x63:				/* double auto decrement */
/*TODO*///			S-=2;
/*TODO*///			EAD=SD;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x64:				/* postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=S+SIGNED(EA);
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x65:				/* postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=S;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x66:				/* normal */
/*TODO*///			EAD=SD;
/*TODO*///			break;
/*TODO*///	//	case 0x67: EA=0; break; /* extended */
/*TODO*///		case 0x68:				/* indirect - auto increment */
/*TODO*///			EAD=SD;
/*TODO*///			S++;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x69:				/* indirect - double auto increment */
/*TODO*///			EAD=SD;
/*TODO*///			S+=2;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x6a:				/* indirect - auto decrement */
/*TODO*///			S--;
/*TODO*///			EAD=SD;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x6b:				/* indirect - double auto decrement */
/*TODO*///			S-=2;
/*TODO*///			EAD=SD;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x6c:				/* indirect - postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=S+SIGNED(EA);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x6d:				/* indirect - postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=S;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0x6e:				/* indirect - normal */
/*TODO*///			EAD=SD;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///	//	case 0x6f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	/* base PC */
/*TODO*///		case 0x70:              /* auto increment */
/*TODO*///			EAD=PCD;
/*TODO*///			PC++;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x71:				/* double auto increment */
/*TODO*///			EAD=PCD;
/*TODO*///			PC+=2;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x72:				/* auto decrement */
/*TODO*///			PC--;
/*TODO*///			EAD=PCD;
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x73:				/* double auto decrement */
/*TODO*///			PC-=2;
/*TODO*///			EAD=PCD;
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///		case 0x74:				/* postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=PC-1+SIGNED(EA);
/*TODO*///			konami_ICount-=2;
/*TODO*///			break;
/*TODO*///		case 0x75:				/* postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=PC-2;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x76:				/* normal */
/*TODO*///			EAD=PCD;
/*TODO*///			break;
/*TODO*///	//	case 0x77: EA=0; break; /* extended */
/*TODO*///		case 0x78:				/* indirect - auto increment */
/*TODO*///			EAD=PCD;
/*TODO*///			PC++;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x79:				/* indirect - double auto increment */
/*TODO*///			EAD=PCD;
/*TODO*///			PC+=2;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x7a:				/* indirect - auto decrement */
/*TODO*///			PC--;
/*TODO*///			EAD=PCD;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=5;
/*TODO*///			break;
/*TODO*///		case 0x7b:				/* indirect - double auto decrement */
/*TODO*///			PC-=2;
/*TODO*///			EAD=PCD;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=6;
/*TODO*///			break;
/*TODO*///		case 0x7c:				/* indirect - postbyte offs */
/*TODO*///			IMMBYTE(EA);
/*TODO*///			EA=PC-1+SIGNED(EA);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0x7d:				/* indirect - postword offs */
/*TODO*///			IMMWORD(ea);
/*TODO*///			EA+=PC-2;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0x7e:				/* indirect - normal */
/*TODO*///			EAD=PCD;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=3;
/*TODO*///			break;
/*TODO*///	//	case 0x7f: EA=0; break; /* indirect - extended */
/*TODO*///
/*TODO*///	//  case 0x80: EA=0; break; /* register a */
/*TODO*///	//	case 0x81: EA=0; break; /* register b */
/*TODO*///	//	case 0x82: EA=0; break; /* ???? */
/*TODO*///	//	case 0x83: EA=0; break; /* ???? */
/*TODO*///	//	case 0x84: EA=0; break; /* ???? */
/*TODO*///	//	case 0x85: EA=0; break; /* ???? */
/*TODO*///	//	case 0x86: EA=0; break; /* ???? */
/*TODO*///	//	case 0x87: EA=0; break; /* register d */
/*TODO*///	//	case 0x88: EA=0; break; /* indirect - register a */
/*TODO*///	//	case 0x89: EA=0; break; /* indirect - register b */
/*TODO*///	//	case 0x8a: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x8b: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x8c: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x8d: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x8e: EA=0; break; /* indirect - register d */
/*TODO*///	//	case 0x8f: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x90: EA=0; break; /* register a */
/*TODO*///	//	case 0x91: EA=0; break; /* register b */
/*TODO*///	//	case 0x92: EA=0; break; /* ???? */
/*TODO*///	//	case 0x93: EA=0; break; /* ???? */
/*TODO*///	//	case 0x94: EA=0; break; /* ???? */
/*TODO*///	//	case 0x95: EA=0; break; /* ???? */
/*TODO*///	//	case 0x96: EA=0; break; /* ???? */
/*TODO*///	//	case 0x97: EA=0; break; /* register d */
/*TODO*///	//	case 0x98: EA=0; break; /* indirect - register a */
/*TODO*///	//	case 0x99: EA=0; break; /* indirect - register b */
/*TODO*///	//	case 0x9a: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x9b: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x9c: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x9d: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0x9e: EA=0; break; /* indirect - register d */
/*TODO*///	//	case 0x9f: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xa0:				/* register a */
/*TODO*///			EA=X+SIGNED(A);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///		case 0xa1:				/* register b */
/*TODO*///			EA=X+SIGNED(B);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///	//	case 0xa2: EA=0; break; /* ???? */
/*TODO*///	//	case 0xa3: EA=0; break; /* ???? */
/*TODO*///	//	case 0xa4: EA=0; break; /* ???? */
/*TODO*///	//	case 0xa5: EA=0; break; /* ???? */
/*TODO*///	//	case 0xa6: EA=0; break; /* ???? */
/*TODO*///		case 0xa7:				/* register d */
/*TODO*///			EA=X+D;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xa8:				/* indirect - register a */
/*TODO*///			EA=X+SIGNED(A);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xa9:				/* indirect - register b */
/*TODO*///			EA=X+SIGNED(B);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0xaa: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xab: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xac: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xad: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xae: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xaf:				/* indirect - register d */
/*TODO*///			EA=X+D;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0xb0:				/* register a */
/*TODO*///			EA=Y+SIGNED(A);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///		case 0xb1:				/* register b */
/*TODO*///			EA=Y+SIGNED(B);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///	//	case 0xb2: EA=0; break; /* ???? */
/*TODO*///	//	case 0xb3: EA=0; break; /* ???? */
/*TODO*///	//	case 0xb4: EA=0; break; /* ???? */
/*TODO*///	//	case 0xb5: EA=0; break; /* ???? */
/*TODO*///	//	case 0xb6: EA=0; break; /* ???? */
/*TODO*///		case 0xb7:				/* register d */
/*TODO*///			EA=Y+D;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xb8:				/* indirect - register a */
/*TODO*///			EA=Y+SIGNED(A);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xb9:				/* indirect - register b */
/*TODO*///			EA=Y+SIGNED(B);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0xba: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xbb: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xbc: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xbd: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xbe: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xbf:				/* indirect - register d */
/*TODO*///			EA=Y+D;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///	//	case 0xc0: EA=0; break; /* register a */
/*TODO*///	//	case 0xc1: EA=0; break; /* register b */
/*TODO*///	//	case 0xc2: EA=0; break; /* ???? */
/*TODO*///	//	case 0xc3: EA=0; break; /* ???? */
/*TODO*///		case 0xc4:
/*TODO*///			EAD=0;
/*TODO*///			(*konami_direct[konami.ireg])();
/*TODO*///			konami_ICount -= 1;
/*TODO*///			return;
/*TODO*///	//	case 0xc5: EA=0; break; /* ???? */
/*TODO*///	//	case 0xc6: EA=0; break; /* ???? */
/*TODO*///	//	case 0xc7: EA=0; break; /* register d */
/*TODO*///	//	case 0xc8: EA=0; break; /* indirect - register a */
/*TODO*///	//	case 0xc9: EA=0; break; /* indirect - register b */
/*TODO*///	//	case 0xca: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xcb: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xcc:				/* indirect - direct */
/*TODO*///			DIRWORD(ea);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0xcd: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xce: EA=0; break; /* indirect - register d */
/*TODO*///	//	case 0xcf: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xd0:				/* register a */
/*TODO*///			EA=U+SIGNED(A);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///		case 0xd1:				/* register b */
/*TODO*///			EA=U+SIGNED(B);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///	//	case 0xd2: EA=0; break; /* ???? */
/*TODO*///	//	case 0xd3: EA=0; break; /* ???? */
/*TODO*///	//	case 0xd4: EA=0; break; /* ???? */
/*TODO*///	//	case 0xd5: EA=0; break; /* ???? */
/*TODO*///	//	case 0xd6: EA=0; break; /* ???? */
/*TODO*///		case 0xd7:				/* register d */
/*TODO*///			EA=U+D;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xd8:				/* indirect - register a */
/*TODO*///			EA=U+SIGNED(A);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xd9:				/* indirect - register b */
/*TODO*///			EA=U+SIGNED(B);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0xda: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xdb: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xdc: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xdd: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xde: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xdf:				/* indirect - register d */
/*TODO*///			EA=U+D;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0xe0:				/* register a */
/*TODO*///			EA=S+SIGNED(A);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///		case 0xe1:				/* register b */
/*TODO*///			EA=S+SIGNED(B);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///	//	case 0xe2: EA=0; break; /* ???? */
/*TODO*///	//	case 0xe3: EA=0; break; /* ???? */
/*TODO*///	//	case 0xe4: EA=0; break; /* ???? */
/*TODO*///	//	case 0xe5: EA=0; break; /* ???? */
/*TODO*///	//	case 0xe6: EA=0; break; /* ???? */
/*TODO*///		case 0xe7:				/* register d */
/*TODO*///			EA=S+D;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xe8:				/* indirect - register a */
/*TODO*///			EA=S+SIGNED(A);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xe9:				/* indirect - register b */
/*TODO*///			EA=S+SIGNED(B);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0xea: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xeb: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xec: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xed: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xee: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xef:				/* indirect - register d */
/*TODO*///			EA=S+D;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		case 0xf0:				/* register a */
/*TODO*///			EA=PC+SIGNED(A);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///		case 0xf1:				/* register b */
/*TODO*///			EA=PC+SIGNED(B);
/*TODO*///			konami_ICount-=1;
/*TODO*///			break;
/*TODO*///	//	case 0xf2: EA=0; break; /* ???? */
/*TODO*///	//	case 0xf3: EA=0; break; /* ???? */
/*TODO*///	//	case 0xf4: EA=0; break; /* ???? */
/*TODO*///	//	case 0xf5: EA=0; break; /* ???? */
/*TODO*///	//	case 0xf6: EA=0; break; /* ???? */
/*TODO*///		case 0xf7:				/* register d */
/*TODO*///			EA=PC+D;
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xf8:				/* indirect - register a */
/*TODO*///			EA=PC+SIGNED(A);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///		case 0xf9:				/* indirect - register b */
/*TODO*///			EA=PC+SIGNED(B);
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=4;
/*TODO*///			break;
/*TODO*///	//	case 0xfa: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xfb: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xfc: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xfd: EA=0; break; /* indirect - ???? */
/*TODO*///	//	case 0xfe: EA=0; break; /* indirect - ???? */
/*TODO*///		case 0xff:				/* indirect - register d */
/*TODO*///			EA=PC+D;
/*TODO*///			EA=RM16(EAD);
/*TODO*///			konami_ICount-=7;
/*TODO*///			break;
/*TODO*///		default:
/*TODO*///			logerror("KONAMI: Unknown/Invalid postbyte at PC = %04x\n", PC -1 );
/*TODO*///			EAD = 0;
/*TODO*///		}
/*TODO*///		(*konami_indexed[konami.ireg])();
            }
        };
    
}
