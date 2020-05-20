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
/*TODO*///	public static opcode neg_di( void )
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
/*TODO*///	public static opcode com_di( void )
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
/*TODO*///	public static opcode lsr_di( void )
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
/*TODO*///	public static opcode ror_di( void )
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
/*TODO*///	public static opcode asr_di( void )
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
/*TODO*///	public static opcode asl_di( void )
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
/*TODO*///	public static opcode rol_di( void )
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
/*TODO*///	public static opcode dec_di( void )
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
/*TODO*///	public static opcode inc_di( void )
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
/*TODO*///	public static opcode tst_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $0E JMP direct ----- */
/*TODO*///	public static opcode jmp_di( void )
/*TODO*///	{
/*TODO*///		DIRECT;
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $0F CLR direct -0100 */
/*TODO*///	public static opcode clr_di( void )
/*TODO*///	{
/*TODO*///		DIRECT;
/*TODO*///		WM(EAD,0);
/*TODO*///		CLR_NZVC;
/*TODO*///		SEZ;
/*TODO*///	}

	
	/* $10 FLAG */

	/* $11 FLAG */

	/* $12 NOP inherent ----- */
	public static opcode nop = new opcode() {
            public void handler() {
                ;
            }
        };
	
/*TODO*///	/* $13 SYNC inherent ----- */
/*TODO*///	public static opcode sync( void )
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

	/* $14 ILLEGAL */

	/* $15 ILLEGAL */

	/* $16 LBRA relative ----- */
	public static opcode lbra = new opcode() {
            public void handler() {
		ea = IMMWORD();
		PC( PC() + EA() );
		change_pc16(PCD());

		/* EHC 980508 speed up busy loop */
		if( EA() == 0xfffd && konami_ICount[0] > 0 )
			konami_ICount[0] = 0;
            }
        };

	/* $17 LBSR relative ----- */
	public static opcode lbsr = new opcode() {
            public void handler() {
		ea = IMMWORD();
		PUSHWORD(pPC());
		PC( PC() + EA() );
		change_pc16(PCD());
            }
        };

	/* $18 ILLEGAL */

/*TODO*///	#if 1
	/* $19 DAA inherent (A) -**0* */
	public static opcode daa = new opcode() {
            public void handler() {
		int msn, lsn;
		int t, cf = 0;
		msn = A() & 0xf0; lsn = A() & 0x0f;
		if( lsn>0x09 || (CC() & CC_H)!=0) cf |= 0x06;
		if( msn>0x80 && lsn>0x09 ) cf |= 0x60;
		if( msn>0x90 || (CC() & CC_C)!=0) cf |= 0x60;
		t = cf + A();
		CLR_NZV(); /* keep carry from previous operation */
		SET_NZ8(t&0xFF); SET_C8(t);
		A( t );
            }
        };
/*TODO*///	#else
/*TODO*///	/* $19 DAA inherent (A) -**0* */
/*TODO*///	public static opcode daa( void )
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

	/* $1A ORCC immediate ##### */
	public static opcode orcc = new opcode() {
            public void handler() {
		int t;
		t = IMMBYTE();
		CC( CC() | t );
		CHECK_IRQ_LINES();
            }
        };

	/* $1B ILLEGAL */

	/* $1C ANDCC immediate ##### */
	public static opcode andcc = new opcode() {
            public void handler() {            
		int t;
		t = IMMBYTE();
		CC( CC() & t );
		CHECK_IRQ_LINES();
            }
        };

	/* $1D SEX inherent -**0- */
	public static opcode sex = new opcode() {
            public void handler() {
		int t;
		t = SIGNED(B());
		D( t );
		CLR_NZV();
		SET_NZ16(t);
            }
        };

	/* $1E EXG inherent ----- */
	public static opcode exg = new opcode() {
            public void handler() {
		int t1 = 0, t2 = 0;
		int tb;

		tb = IMMBYTE();

		t1 = GETREG( tb >> 4 );
		t2 = GETREG( tb & 0x0f );

		SETREG( t2, tb >> 4 );
		SETREG( t1, tb & 0x0f );
            }
        };

	/* $1F TFR inherent ----- */
	public static opcode tfr = new opcode() {
            public void handler() {
		int tb;
		int t = 0;

		tb = IMMBYTE();

		t = GETREG( tb & 0x0f );
		SETREG( t, ( tb >> 4 ) & 0x07 );
            }
        };


	/* $20 BRA relative ----- */
	public static opcode bra = new opcode() {
            public void handler() {
		int t;
		t = IMMBYTE();
		PC( PC() + SIGNED(t) );
		change_pc16(PCD());
		/* JB 970823 - speed up busy loops */
		if( t == 0xfe && konami_ICount[0] > 0 )
			konami_ICount[0] = 0;
            }
        };

	/* $21 BRN relative ----- */
	public static opcode brn = new opcode() {
            public void handler() {
		int t;
		t = IMMBYTE();
            }
        };

	/* $1021 LBRN relative ----- */
	public static opcode lbrn = new opcode() {
            public void handler() {
		ea = IMMWORD();
            }
        };

	/* $22 BHI relative ----- */
	public static opcode bhi = new opcode() {
            public void handler() {            
		BRANCH( (CC() & (CC_Z|CC_C)) == 0 );
            }
        };

	/* $1022 LBHI relative ----- */
	public static opcode lbhi = new opcode() {
            public void handler() {
		LBRANCH( (CC() & (CC_Z|CC_C)) == 0 );
            }
        };

	/* $23 BLS relative ----- */
	public static opcode bls = new opcode() {
            public void handler() {
		BRANCH( (CC() & (CC_Z|CC_C)) != 0 );
            }
        };

	/* $1023 LBLS relative ----- */
	public static opcode lbls = new opcode() {
            public void handler() {
		LBRANCH( (CC()&(CC_Z|CC_C)) != 0 );
            }
        };

	/* $24 BCC relative ----- */
	public static opcode bcc = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_C) == 0 );
            }
        };

	/* $1024 LBCC relative ----- */
	public static opcode lbcc = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_C) == 0 );
            }
        };

	/* $25 BCS relative ----- */
	public static opcode bcs = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_C) != 0 );
            }
        };

	/* $1025 LBCS relative ----- */
	public static opcode lbcs = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_C) != 0 );
            }
        };

	/* $26 BNE relative ----- */
	public static opcode bne = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_Z) == 0 );
            }
        };

	/* $1026 LBNE relative ----- */
	public static opcode lbne = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_Z) == 0 );
            }
        };

	/* $27 BEQ relative ----- */
	public static opcode beq = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_Z) != 0 );
            }
        };

	/* $1027 LBEQ relative ----- */
	public static opcode lbeq = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_Z) != 0 );
            }
        };

	/* $28 BVC relative ----- */
	public static opcode bvc = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_V) == 0 );
            }
        };

	/* $1028 LBVC relative ----- */
	public static opcode lbvc = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_V) == 0 );
            }
        };

	/* $29 BVS relative ----- */
	public static opcode bvs = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_V)!=0 );
            }
        };

	/* $1029 LBVS relative ----- */
	public static opcode lbvs = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_V) != 0 );
            }
        };

	/* $2A BPL relative ----- */
	public static opcode bpl = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_N) == 0 );
            }
        };

	/* $102A LBPL relative ----- */
	public static opcode lbpl = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_N) == 0 );
            }
        };

	/* $2B BMI relative ----- */
	public static opcode bmi = new opcode() {
            public void handler() {
		BRANCH( (CC()&CC_N) != 0 );
            }
        };

	/* $102B LBMI relative ----- */
	public static opcode lbmi = new opcode() {
            public void handler() {
		LBRANCH( (CC()&CC_N) != 0 );
            }
        };

	/* $2C BGE relative ----- */
	public static opcode bge = new opcode() {
            public void handler() {
		BRANCH( NXORV() == 0 );
            }
        };

	/* $102C LBGE relative ----- */
	public static opcode lbge = new opcode() {
            public void handler() {
		LBRANCH( NXORV()==0 );
            }
        };

	/* $2D BLT relative ----- */
	public static opcode blt = new opcode() {
            public void handler() {
		BRANCH( NXORV()!=0 );
            }
        };

	/* $102D LBLT relative ----- */
	public static opcode lblt = new opcode() {
            public void handler() {
		LBRANCH( NXORV() != 0 );
            }
        };

	/* $2E BGT relative ----- */
	public static opcode bgt = new opcode() {
            public void handler() {
		BRANCH( (NXORV()!=0 || (CC()&CC_Z)!=0) == false);
            }
        };

	/* $102E LBGT relative ----- */
	public static opcode lbgt = new opcode() {
            public void handler() {
		LBRANCH( !(NXORV()!=0 || (CC()&CC_Z)!=0) );
            }
        };

	/* $2F BLE relative ----- */
	public static opcode ble = new opcode() {
            public void handler() {
		BRANCH( (NXORV()!=0 || (CC()&CC_Z)!=0) );
            }
        };

	/* $102F LBLE relative ----- */
	public static opcode lble = new opcode() {
            public void handler() {
		LBRANCH( (NXORV()!=0 || (CC()&CC_Z)!=0) );
            }
        };


	/* $30 LEAX indexed --*-- */
	public static opcode leax = new opcode() {
            public void handler() {
		X(EA());
		CLR_Z();
		SET_Z(X());
            }
        };

	/* $31 LEAY indexed --*-- */
	public static opcode leay = new opcode() {
            public void handler() {
		Y( EA() );
		CLR_Z();
		SET_Z(Y());
            }
        };

	/* $32 LEAS indexed ----- */
	public static opcode leas = new opcode() {
            public void handler() {
		S( EA() );
		konami.int_state |= KONAMI_LDS;
            }
        };

	/* $33 LEAU indexed ----- */
	public static opcode leau = new opcode() {
            public void handler() {
		U( EA() );
            }
        };

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

	/* $38 ILLEGAL */

	/* $39 RTS inherent ----- */
	public static opcode rts = new opcode() {
            public void handler() {
		PULLWORD(PCD());
		change_pc16(PCD());
            }
        };

	/* $3A ABX inherent ----- */
	public static opcode abx = new opcode() {
            public void handler() {
		X( X() + B() );
            }
        };

	/* $3B RTI inherent ##### */
	public static opcode rti = new opcode() {
            public void handler() {            
		PULLBYTE(CC());
		if(( CC() & CC_E ) != 0) /* entire state saved? */
		{
			konami_ICount[0] -= 9;
			PULLBYTE(A());
			PULLBYTE(B());
			PULLBYTE(DP());
			PULLWORD(XD());
			PULLWORD(YD());
			PULLWORD(UD());
		}
		PULLWORD(PCD());
		change_pc16(PCD());
		CHECK_IRQ_LINES();
            }
        };

/*TODO*///	/* $3C CWAI inherent ----1 */
/*TODO*///	public static opcode cwai( void )
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

	/* $3D MUL inherent --*-@ */
	public static opcode mul = new opcode() {
            public void handler() {
		int t;
		t = A() * B();
		CLR_ZC(); SET_Z16(t); if((t&0x80)!=0) SEC();
		D( t );
            }
        };

/*TODO*///	/* $3E ILLEGAL */
/*TODO*///
/*TODO*///	/* $3F SWI (SWI2 SWI3) absolute indirect ----- */
/*TODO*///	public static opcode swi( void )
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
/*TODO*///	public static opcode swi2( void )
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
/*TODO*///	public static opcode swi3( void )
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

	/* $40 NEGA inherent ?**** */
	public static opcode nega = new opcode() {
            public void handler() {
		int r;
		r = -A();
		CLR_NZVC();
		SET_FLAGS8(0,A(),r);
		A( r );
            }
        };

	/* $41 ILLEGAL */

	/* $42 ILLEGAL */

	/* $43 COMA inherent -**01 */
	public static opcode coma = new opcode() {
            public void handler() {            
		A( ~A());
		CLR_NZV();
		SET_NZ8(A());
		SEC();
            }
        };

	/* $44 LSRA inherent -0*-* */
	public static opcode lsra = new opcode() {
            public void handler() {
		CLR_NZC();
		CC( CC() | (A() & CC_C) );
		A( A() >> 1 );
		SET_Z8(A());
            }
        };

	/* $45 ILLEGAL */

	/* $46 RORA inherent -**-* */
	public static opcode rora = new opcode() {
            public void handler() {
		int r;
		r = (CC() & CC_C) << 7;
		CLR_NZC();
		CC( CC() | (A() & CC_C) );
		r |= A() >> 1;
		SET_NZ8(r);
		A( r );
            }
        };

	/* $47 ASRA inherent ?**-* */
	public static opcode asra = new opcode() {
            public void handler() {
		CLR_NZC();
		CC( CC() | (A() & CC_C) );
		A( (A() & 0x80) | (A() >> 1) );
		SET_NZ8(A());
            }
        };

	/* $48 ASLA inherent ?**** */
	public static opcode asla = new opcode() {
            public void handler() {
		int r;
		r = A() << 1;
		CLR_NZVC();
		SET_FLAGS8(A(),A(),r);
		A( r );
            }
        };

	/* $49 ROLA inherent -**** */
	public static opcode rola = new opcode() {
            public void handler() {
		int t,r;
		t = A();
		r = (CC() & CC_C) | (t<<1);
		CLR_NZVC();
                SET_FLAGS8(t,t,r);
		A( r );
            }
        };

	/* $4A DECA inherent -***- */
	public static opcode deca = new opcode() {
            public void handler() {
		A(A()-1);
		CLR_NZV();
		SET_FLAGS8D(A());
            }
        };

	/* $4B ILLEGAL */

	/* $4C INCA inherent -***- */
	public static opcode inca = new opcode() {
            public void handler() {
		A(A()+1);
		CLR_NZV();
		SET_FLAGS8I(A());
            }
        };

	/* $4D TSTA inherent -**0- */
	public static opcode tsta = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $4E ILLEGAL */

	/* $4F CLRA inherent -0100 */
	public static opcode clra = new opcode() {
            public void handler() {            
		A( 0 );
		CLR_NZVC();
                SEZ();
            }
        };


	/* $50 NEGB inherent ?**** */
	public static opcode negb = new opcode() {
            public void handler() {
		int r;
		r = -B();
		CLR_NZVC();
		SET_FLAGS8(0,B(),r);
		B( r );
            }
        };

	/* $51 ILLEGAL */

	/* $52 ILLEGAL */

	/* $53 COMB inherent -**01 */
	public static opcode comb = new opcode() {
            public void handler() {
		B( ~B() );
		CLR_NZV();
		SET_NZ8(B());
		SEC();
            }
        };

	/* $54 LSRB inherent -0*-* */
	public static opcode lsrb = new opcode() {
            public void handler() {
		CLR_NZC();
		CC( CC() | (B() & CC_C) );
		B( B() >> 1 );
		SET_Z8(B());
            }
        };

	/* $55 ILLEGAL */

	/* $56 RORB inherent -**-* */
	public static opcode rorb = new opcode() {
            public void handler() {
		int r;
		r = (CC() & CC_C) << 7;
		CLR_NZC();
		CC( CC() | (B() & CC_C) );
		r |= B() >> 1;
		SET_NZ8(r);
		B( r );
            }
        };

	/* $57 ASRB inherent ?**-* */
	public static opcode asrb = new opcode() {
            public void handler() {
		CLR_NZC();
		CC( CC() | (B() & CC_C) );
		B( (B() & 0x80) | (B() >> 1) );
		SET_NZ8(B());
            }
        };

	/* $58 ASLB inherent ?**** */
	public static opcode aslb = new opcode() {
            public void handler() {
		int r;
		r = B() << 1;
		CLR_NZVC();
		SET_FLAGS8(B(),B(),r);
		B( r );
            }
        };

	/* $59 ROLB inherent -**** */
	public static opcode rolb = new opcode() {
            public void handler() {
		int t,r;
		t = B();
		r = CC() & CC_C;
		r |= t << 1;
		CLR_NZVC();
		SET_FLAGS8(t,t,r);
		B( r );
            }
        };

	/* $5A DECB inherent -***- */
	public static opcode decb = new opcode() {
            public void handler() {
		B(B()-1);
		CLR_NZV();
		SET_FLAGS8D(B());
            }
        };

	/* $5B ILLEGAL */

	/* $5C INCB inherent -***- */
	public static opcode incb = new opcode() {
            public void handler() {
		B(B()+1);
		CLR_NZV();
		SET_FLAGS8I(B());
            }
        };

	/* $5D TSTB inherent -**0- */
	public static opcode tstb = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $5E ILLEGAL */

	/* $5F CLRB inherent -0100 */
	public static opcode clrb = new opcode() {
            public void handler() {            
		B( 0 );
		CLR_NZVC();
                SEZ();
            }
        };

	/* $60 NEG indexed ?**** */
	public static opcode neg_ix = new opcode() {
            public void handler() {
		int r,t;
		t = RM(EAD());
		r = -t;
		CLR_NZVC();
		SET_FLAGS8(0,t,r);
		WM(EAD(),r);
            }
        };

	/* $61 ILLEGAL */

	/* $62 ILLEGAL */

	/* $63 COM indexed -**01 */
	public static opcode com_ix = new opcode() {
            public void handler() {
		int t;
		t = ~RM(EAD());
		CLR_NZV();
		SET_NZ8(t);
		SEC();
		WM(EAD(),t);
            }
        };

	/* $64 LSR indexed -0*-* */
	public static opcode lsr_ix = new opcode() {
            public void handler() {
		int t;
		t = RM(EAD());
		CLR_NZC();
		CC( CC() | (t & CC_C) );
		t>>=1; SET_Z8(t);
		WM(EAD(),t);
            }
        };

	/* $65 ILLEGAL */

	/* $66 ROR indexed -**-* */
	public static opcode ror_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = (CC() & CC_C) << 7;
		CLR_NZC();
		CC( CC() | (t & CC_C) );
		r |= t>>1; SET_NZ8(r);
		WM(EAD(),r);
            }
        };

	/* $67 ASR indexed ?**-* */
	public static opcode asr_ix = new opcode() {
            public void handler() {
		int t;
		t = RM(EAD());
		CLR_NZC();
		CC( CC() | (t & CC_C) );
		t=(t&0x80)|(t>>1);
		SET_NZ8(t);
		WM(EAD(),t);
            }
        };

	/* $68 ASL indexed ?**** */
	public static opcode asl_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = t << 1;
		CLR_NZVC();
		SET_FLAGS8(t,t,r);
		WM(EAD(),r);
            }
        };

	/* $69 ROL indexed -**** */
	public static opcode rol_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = CC() & CC_C;
		r |= t << 1;
		CLR_NZVC();
		SET_FLAGS8(t,t,r);
		WM(EAD(),r);
            }
        };

	/* $6A DEC indexed -***- */
	public static opcode dec_ix = new opcode() {
            public void handler() {
		int t;
		t = RM(EAD()) - 1;
		CLR_NZV(); SET_FLAGS8D(t);
		WM(EAD(),t);
            }
        };

	/* $6B ILLEGAL */

	/* $6C INC indexed -***- */
	public static opcode inc_ix = new opcode() {
            public void handler() {
		int t;
		t = RM(EAD()) + 1;
		CLR_NZV(); SET_FLAGS8I(t);
		WM(EAD(),t);
            }
        };

	/* $6D TST indexed -**0- */
	public static opcode tst_ix = new opcode() {
            public void handler() {
		int t;
		t = RM(EAD());
		CLR_NZV();
		SET_NZ8(t);
            }
        };

	/* $6E JMP indexed ----- */
	public static opcode jmp_ix = new opcode() {
            public void handler() {
		PCD(EAD());
		change_pc16(PCD());
            }
        };

	/* $6F CLR indexed -0100 */
	public static opcode clr_ix = new opcode() {
            public void handler() {
		WM(EAD(),0);
		CLR_NZVC(); SEZ();
            }
        };

/*TODO*///	/* $70 NEG extended ?**** */
/*TODO*///	public static opcode neg_ex( void )
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
/*TODO*///	public static opcode com_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); t = ~t;
/*TODO*///		CLR_NZV; SET_NZ8(t); SEC;
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $74 LSR extended -0*-* */
/*TODO*///	public static opcode lsr_ex( void )
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
/*TODO*///	public static opcode ror_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		EXTBYTE(t); r=(CC & CC_C) << 7;
/*TODO*///		CLR_NZC; CC |= (t & CC_C);
/*TODO*///		r |= t>>1; SET_NZ8(r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $77 ASR extended ?**-* */
/*TODO*///	public static opcode asr_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); CLR_NZC; CC |= (t & CC_C);
/*TODO*///		t=(t&0x80)|(t>>1);
/*TODO*///		SET_NZ8(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $78 ASL extended ?**** */
/*TODO*///	public static opcode asl_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t); r=t<<1;
/*TODO*///		CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $79 ROL extended -**** */
/*TODO*///	public static opcode rol_ex( void )
/*TODO*///	{
/*TODO*///		UINT16 t,r;
/*TODO*///		EXTBYTE(t); r = (CC & CC_C) | (t << 1);
/*TODO*///		CLR_NZVC; SET_FLAGS8(t,t,r);
/*TODO*///		WM(EAD,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7A DEC extended -***- */
/*TODO*///	public static opcode dec_ex( void )
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
/*TODO*///	public static opcode inc_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); ++t;
/*TODO*///		CLR_NZV; SET_FLAGS8I(t);
/*TODO*///		WM(EAD,t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7D TST extended -**0- */
/*TODO*///	public static opcode tst_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t); CLR_NZV; SET_NZ8(t);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7E JMP extended ----- */
/*TODO*///	public static opcode jmp_ex( void )
/*TODO*///	{
/*TODO*///		EXTENDED;
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $7F CLR extended -0100 */
/*TODO*///	public static opcode clr_ex( void )
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

	/* $80 SUBA immediate ?**** */
	public static opcode suba_im = new opcode() {
            public void handler() {            
		int t,r;
		t = IMMBYTE();
		r = A() - t;
		CLR_NZVC();
		SET_FLAGS8(A(),t,r);
		A( r );
            }
        };

	/* $81 CMPA immediate ?**** */
	public static opcode cmpa_im = new opcode() {
            public void handler() { 
		int	  t,r;
		t = IMMBYTE();
		r = A() - t;
		CLR_NZVC();
		SET_FLAGS8(A(),t,r);
            }
        };

	/* $82 SBCA immediate ?**** */
	public static opcode sbca_im = new opcode() {
            public void handler() {            
		int	  t,r;
		t = IMMBYTE();
		r = A() - t - (CC() & CC_C);
		CLR_NZVC();
		SET_FLAGS8(A(),t,r);
		A( r );
            }
        };

	/* $83 SUBD (CMPD CMPU) immediate -**** */
	public static opcode subd_im = new opcode() {
            public void handler() { 
		int r,d;
		int b;
		b = IMMWORD();
		d = D();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
		D( r );
            }
        };

	/* $1083 CMPD immediate -**** */
	public static opcode cmpd_im = new opcode() {
            public void handler() { 
		int r,d;
		int b;
		b = IMMWORD();
		d = D();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $1183 CMPU immediate -**** */
	public static opcode cmpu_im = new opcode() {
            public void handler() { 
		int r, d;
		int b;
		b = IMMWORD();
		d = U();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $84 ANDA immediate -**0- */
	public static opcode anda_im = new opcode() {
            public void handler() {            
		int t;
		t = IMMBYTE();
		A( A() & t );
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $85 BITA immediate -**0- */
	public static opcode bita_im = new opcode() {
            public void handler() {
		int t,r;
		t = IMMBYTE();
		r = A() & t;
		CLR_NZV();
		SET_NZ8(r);
            }
        };

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
/*TODO*///	public static opcode sta_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		IMM8;
/*TODO*///		WM(EAD,A);
/*TODO*///	}

	/* $88 EORA immediate -**0- */
	public static opcode eora_im = new opcode() {
            public void handler() { 
		int t;
		t = IMMBYTE();
		A( A() ^ t );
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $89 ADCA immediate ***** */
	public static opcode adca_im = new opcode() {
            public void handler() {            
		int t,r;
		t = IMMBYTE();
		r = A() + t + (CC() & CC_C);
		CLR_HNZVC();
		SET_FLAGS8(A(),t,r);
		SET_H(A(),t,r);
		A( r );
            }
        };

	/* $8A ORA immediate -**0- */
	public static opcode ora_im = new opcode() {
            public void handler() { 
		int t;
		t = IMMBYTE();
		A( A() | t );
		CLR_NZV();
		SET_NZ8(A());
            }
        };

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

	/* $8C CMPX (CMPY CMPS) immediate -**** */
	public static opcode cmpx_im = new opcode() {
            public void handler() {  
		int r,d;
		int b;
		b = IMMWORD();
		d = X();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $108C CMPY immediate -**** */
	public static opcode cmpy_im = new opcode() {
            public void handler() { 
		int r,d;
		int b;
		b = IMMWORD();
		d = Y();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $118C CMPS immediate -**** */
	public static opcode cmps_im = new opcode() {
            public void handler() { 
		int r,d;
		int b;
		b = IMMWORD();
		d = S();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $8D BSR ----- */
	public static opcode bsr = new opcode() {
            public void handler() { 
		int t;
		t = IMMBYTE();
		PUSHWORD(pPC());
		PC( PC() + SIGNED(t) );
		change_pc16(PCD());
            }
        };

	/* $8E LDX (LDY) immediate -**0- */
	public static opcode ldx_im = new opcode() {
            public void handler() {            
		pX(IMMWORD());
		CLR_NZV();
		SET_NZ16(X());
            }
        };

	/* $108E LDY immediate -**0- */
	public static opcode ldy_im = new opcode() {
            public void handler() {
		pY(IMMWORD());
		CLR_NZV();
		SET_NZ16(Y());
            }
        };

/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $8F STX (STY) immediate -**0- */
/*TODO*///	public static opcode stx_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $108F STY immediate -**0- */
/*TODO*///	public static opcode sty_im( void )
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
/*TODO*///	public static opcode suba_di( void )
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
/*TODO*///	public static opcode cmpa_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $92 SBCA direct ?**** */
/*TODO*///	public static opcode sbca_di( void )
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
/*TODO*///	public static opcode subd_di( void )
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
/*TODO*///	public static opcode cmpd_di( void )
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
/*TODO*///	public static opcode cmpu_di( void )
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
/*TODO*///	public static opcode anda_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		A &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $95 BITA direct -**0- */
/*TODO*///	public static opcode bita_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = A & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $96 LDA direct -**0- */
/*TODO*///	public static opcode lda_di( void )
/*TODO*///	{
/*TODO*///		DIRBYTE(A);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $97 STA direct -**0- */
/*TODO*///	public static opcode sta_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		DIRECT;
/*TODO*///		WM(EAD,A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $98 EORA direct -**0- */
/*TODO*///	public static opcode eora_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		A ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $99 ADCA direct ***** */
/*TODO*///	public static opcode adca_di( void )
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
/*TODO*///	public static opcode ora_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		A |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9B ADDA direct ***** */
/*TODO*///	public static opcode adda_di( void )
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
/*TODO*///	public static opcode cmpx_di( void )
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
/*TODO*///	public static opcode cmpy_di( void )
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
/*TODO*///	public static opcode cmps_di( void )
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
/*TODO*///	public static opcode jsr_di( void )
/*TODO*///	{
/*TODO*///		DIRECT;
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9E LDX (LDY) direct -**0- */
/*TODO*///	public static opcode ldx_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pX);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $109E LDY direct -**0- */
/*TODO*///	public static opcode ldy_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pY);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $9F STX (STY) direct -**0- */
/*TODO*///	public static opcode stx_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $109F STY direct -**0- */
/*TODO*///	public static opcode sty_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pY);
/*TODO*///	}


	/* $a0 SUBA indexed ?**** */
	public static opcode suba_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = A() - t;
		CLR_NZVC();
		SET_FLAGS8(A(),t,r);
		A( r );
            }
        };

	/* $a1 CMPA indexed ?**** */
	public static opcode cmpa_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = A() - t;
		CLR_NZVC();
		SET_FLAGS8(A(),t,r);
            }
        };

	/* $a2 SBCA indexed ?**** */
	public static opcode sbca_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = A() - t - (CC() & CC_C);
		CLR_NZVC();
		SET_FLAGS8(A(),t,r);
		A( r );
            }
        };

	/* $a3 SUBD (CMPD CMPU) indexed -**** */
	public static opcode subd_ix = new opcode() {
            public void handler() {
		int r,d;
		int b;
		b=RM16(EAD());
		d = D();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
		D( r );
            }
        };

	/* $10a3 CMPD indexed -**** */
	public static opcode cmpd_ix = new opcode() {
            public void handler() {
		int r,d;
		int b;
		b=RM16(EAD());
		d = D();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $11a3 CMPU indexed -**** */
	public static opcode cmpu_ix = new opcode() {
            public void handler() {
		int r;
		int b;
		b=RM16(EAD());
		r = U() - b;
		CLR_NZVC();
		SET_FLAGS16(U(),b,r);
            }
        };

	/* $a4 ANDA indexed -**0- */
	public static opcode anda_ix = new opcode() {
            public void handler() {
		A( A() & RM(EAD()) );
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $a5 BITA indexed -**0- */
	public static opcode bita_ix = new opcode() {
            public void handler() {
		int r;
		r = A() & RM(EAD());
		CLR_NZV();
		SET_NZ8(r);
                ldb_ix.handler();
            }
        };

	/* $a6 LDA indexed -**0- */
	public static opcode lda_ix = new opcode() {
            public void handler() {
		A(RM(EAD()));
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $a7 STA indexed -**0- */
	public static opcode sta_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ8(A());
		WM(EAD(),A());
            }
        };

	/* $a8 EORA indexed -**0- */
	public static opcode eora_ix = new opcode() {
            public void handler() {
		A(A() ^ RM(EAD()));
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $a9 ADCA indexed ***** */
	public static opcode adca_ix = new opcode() {
            public void handler() { 
		int t,r;
		t = RM(EAD());
		r = A() + t + (CC() & CC_C);
		CLR_HNZVC();
		SET_FLAGS8(A(),t,r);
		SET_H(A(),t,r);
		A( r );
            }
        };

	/* $aA ORA indexed -**0- */
	public static opcode ora_ix = new opcode() {
            public void handler() {
		A( A() | RM(EAD()) );
		CLR_NZV();
		SET_NZ8(A());
            }
        };

	/* $aB ADDA indexed ***** */
	public static opcode adda_ix = new opcode() {
            public void handler() { 
		int t,r;
		t = RM(EAD());
		r = A() + t;
		CLR_HNZVC();
		SET_FLAGS8(A(),t,r);
		SET_H(A(),t,r);
		A( r );
            }
        };

	/* $aC CMPX (CMPY CMPS) indexed -**** */
	public static opcode cmpx_ix = new opcode() {
            public void handler() {
		int r,d;
		int b;
		b=RM16(EAD());
		d = X();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $10aC CMPY indexed -**** */
	public static opcode cmpy_ix = new opcode() {
            public void handler() {
		int r,d;
		int b;
		b=RM16(EAD());
		d = Y();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $11aC CMPS indexed -**** */
	public static opcode cmps_ix = new opcode() {
            public void handler() {
		int r,d;
		int b;
		b=RM16(EAD());
		d = S();
		r = d - b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
            }
        };

	/* $aD JSR indexed ----- */
	public static opcode jsr_ix = new opcode() {
            public void handler() {
		PUSHWORD(pPC());
		PCD(EAD());
		change_pc16(PCD());
            }
        };

	/* $aE LDX (LDY) indexed -**0- */
	public static opcode ldx_ix = new opcode() {
            public void handler() {
		X(RM16(EAD()));
		CLR_NZV();
		SET_NZ16(X());
            }
        };

	/* $10aE LDY indexed -**0- */
	public static opcode ldy_ix = new opcode() {
            public void handler() {
		Y(RM16(EAD()));
		CLR_NZV();
		SET_NZ16(Y());
            }
        };

	/* $aF STX (STY) indexed -**0- */
	public static opcode stx_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ16(X());
		WM16(EAD(),pX());
            }
        };

	/* $10aF STY indexed -**0- */
	public static opcode sty_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ16(Y());
		WM16(EAD(),pY());
            }
        };

/*TODO*///	/* $b0 SUBA extended ?**** */
/*TODO*///	public static opcode suba_ex( void )
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
/*TODO*///	public static opcode cmpa_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(A,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b2 SBCA extended ?**** */
/*TODO*///	public static opcode sbca_ex( void )
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
/*TODO*///	public static opcode subd_ex( void )
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
/*TODO*///	public static opcode cmpd_ex( void )
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
/*TODO*///	public static opcode cmpu_ex( void )
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
/*TODO*///	public static opcode anda_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		A &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b5 BITA extended -**0- */
/*TODO*///	public static opcode bita_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = A & t;
/*TODO*///		CLR_NZV; SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b6 LDA extended -**0- */
/*TODO*///	public static opcode lda_ex( void )
/*TODO*///	{
/*TODO*///		EXTBYTE(A);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b7 STA extended -**0- */
/*TODO*///	public static opcode sta_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///		EXTENDED;
/*TODO*///		WM(EAD,A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b8 EORA extended -**0- */
/*TODO*///	public static opcode eora_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		A ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $b9 ADCA extended ***** */
/*TODO*///	public static opcode adca_ex( void )
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
/*TODO*///	public static opcode ora_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		A |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(A);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bB ADDA extended ***** */
/*TODO*///	public static opcode adda_ex( void )
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
/*TODO*///	public static opcode cmpx_ex( void )
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
/*TODO*///	public static opcode cmpy_ex( void )
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
/*TODO*///	public static opcode cmps_ex( void )
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
/*TODO*///	public static opcode jsr_ex( void )
/*TODO*///	{
/*TODO*///		EXTENDED;
/*TODO*///		PUSHWORD(pPC);
/*TODO*///		PCD=EAD;
/*TODO*///		change_pc16(PCD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bE LDX (LDY) extended -**0- */
/*TODO*///	public static opcode ldx_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pX);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10bE LDY extended -**0- */
/*TODO*///	public static opcode ldy_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pY);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(Y);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $bF STX (STY) extended -**0- */
/*TODO*///	public static opcode stx_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(X);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pX);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10bF STY extended -**0- */
/*TODO*///	public static opcode sty_ex( void )
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

	/* $c0 SUBB immediate ?**** */
	public static opcode subb_im = new opcode() {
            public void handler() {            
		int	  t,r;
		t = IMMBYTE();
		r = B() - t;
		CLR_NZVC();
		SET_FLAGS8(B(),t,r);
		B( r );
            }
        };

	/* $c1 CMPB immediate ?**** */
	public static opcode cmpb_im = new opcode() {
            public void handler() { 
		int	  t,r;
		t = IMMBYTE();
		r = B() - t;
		CLR_NZVC();
                SET_FLAGS8(B(),t,r);
            }
        };

	/* $c2 SBCB immediate ?**** */
	public static opcode sbcb_im = new opcode() {
            public void handler() {            
		int	  t,r;
		t = IMMBYTE();
		r = B() - t - (CC() & CC_C);
		CLR_NZVC();
		SET_FLAGS8(B(),t,r);
		B( r );
            }
        };

	/* $c3 ADDD immediate -**** */
	public static opcode addd_im = new opcode() {
            public void handler() { 
		int r,d;
		int b;
		b = IMMWORD();
		d = D();
		r = d + b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
		D( r );
            }
        };

	/* $c4 ANDB immediate -**0- */
	public static opcode andb_im = new opcode() {
            public void handler() {            
		int t;
		t = IMMBYTE();
		B (B() & t);
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $c5 BITB immediate -**0- */
	public static opcode bitb_im = new opcode() {
            public void handler() {
		int t,r;
		t = IMMBYTE();
		r = B() & t;
		CLR_NZV();
		SET_NZ8(r);
            }
        };

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
/*TODO*///	public static opcode stb_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		IMM8;
/*TODO*///		WM(EAD,B);
/*TODO*///	}

	/* $c8 EORB immediate -**0- */
	public static opcode eorb_im = new opcode() {
            public void handler() { 
		int t;
		t = IMMBYTE();
		B( B() ^ t );
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $c9 ADCB immediate ***** */
	public static opcode adcb_im = new opcode() {
            public void handler() {            
		int t,r;
		t = IMMBYTE();
		r = B() + t + (CC() & CC_C);
		CLR_HNZVC();
		SET_FLAGS8(B(),t,r);
		SET_H(B(),t,r);
		B( r );
            }
        };

	/* $cA ORB immediate -**0- */
	public static opcode orb_im = new opcode() {
            public void handler() { 
		int t;
		t = IMMBYTE();
		B( B() | t );
		CLR_NZV();
		SET_NZ8(B());
            }
        };

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

	/* $cC LDD immediate -**0- */
	public static opcode ldd_im = new opcode() {
            public void handler() { 
		pD(IMMWORD());
		CLR_NZV();
		SET_NZ16(D());
            }
        };

/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $cD STD immediate -**0- */
/*TODO*///	public static opcode std_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}

	/* $cE LDU (LDS) immediate -**0- */
	public static opcode ldu_im = new opcode() {
            public void handler() {
		pU(IMMWORD());
		CLR_NZV();
		SET_NZ16(U());
            }
        };

	/* $10cE LDS immediate -**0- */
	public static opcode lds_im = new opcode() {
            public void handler() {
		pS(IMMWORD());
		CLR_NZV();
		SET_NZ16(S());
		konami.int_state |= KONAMI_LDS;
            }
        };

/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $cF STU (STS) immediate -**0- */
/*TODO*///	public static opcode stu_im( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		IMM16;
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* is this a legal instruction? */
/*TODO*///	/* $10cF STS immediate -**0- */
/*TODO*///	public static opcode sts_im( void )
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
/*TODO*///	public static opcode subb_di( void )
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
/*TODO*///	public static opcode cmpb_di( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d2 SBCB direct ?**** */
/*TODO*///	public static opcode sbcb_di( void )
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
/*TODO*///	public static opcode addd_di( void )
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
/*TODO*///	public static opcode andb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		B &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d5 BITB direct -**0- */
/*TODO*///	public static opcode bitb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		DIRBYTE(t);
/*TODO*///		r = B & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d6 LDB direct -**0- */
/*TODO*///	public static opcode ldb_di( void )
/*TODO*///	{
/*TODO*///		DIRBYTE(B);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d7 STB direct -**0- */
/*TODO*///	public static opcode stb_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		DIRECT;
/*TODO*///		WM(EAD,B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d8 EORB direct -**0- */
/*TODO*///	public static opcode eorb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		B ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $d9 ADCB direct ***** */
/*TODO*///	public static opcode adcb_di( void )
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
/*TODO*///	public static opcode orb_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///		B |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dB ADDB direct ***** */
/*TODO*///	public static opcode addb_di( void )
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
/*TODO*///	public static opcode ldd_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dD STD direct -**0- */
/*TODO*///	public static opcode std_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dE LDU (LDS) direct -**0- */
/*TODO*///	public static opcode ldu_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pU);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10dE LDS direct -**0- */
/*TODO*///	public static opcode lds_di( void )
/*TODO*///	{
/*TODO*///		DIRWORD(pS);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $dF STU (STS) direct -**0- */
/*TODO*///	public static opcode stu_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10dF STS direct -**0- */
/*TODO*///	public static opcode sts_di( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		DIRECT;
/*TODO*///		WM16(EAD,&pS);
/*TODO*///	}
/*TODO*///

	/* $e0 SUBB indexed ?**** */
	public static opcode subb_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = B() - t;
		CLR_NZVC();
		SET_FLAGS8(B(),t,r);
		B( r );
            }
        };

	/* $e1 CMPB indexed ?**** */
	public static opcode cmpb_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = B() - t;
		CLR_NZVC();
		SET_FLAGS8(B(),t,r);
            }
        };

	/* $e2 SBCB indexed ?**** */
	public static opcode sbcb_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = B() - t - (CC() & CC_C);
		CLR_NZVC();
		SET_FLAGS8(B(),t,r);
		B( r );
            }
        };

	/* $e3 ADDD indexed -**** */
	public static opcode addd_ix = new opcode() {
            public void handler() {
		int r,d;
		int b;
		b=RM16(EAD());
		d = D();
		r = d + b;
		CLR_NZVC();
		SET_FLAGS16(d,b,r);
		D( r );
            }
        };

	/* $e4 ANDB indexed -**0- */
	public static opcode andb_ix = new opcode() {
            public void handler() {
		B( B() & RM(EAD()) );
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $e5 BITB indexed -**0- */
	public static opcode bitb_ix = new opcode() {
            public void handler() {
		int r;
		r = B() & RM(EAD());
		CLR_NZV();
		SET_NZ8(r);
            }
        };

	/* $e6 LDB indexed -**0- */
	public static opcode ldb_ix = new opcode() {
            public void handler() {
		B( RM(EAD()) );
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $e7 STB indexed -**0- */
	public static opcode stb_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ8(B());
		WM(EAD(),B());
            }
        };

	/* $e8 EORB indexed -**0- */
	public static opcode eorb_ix = new opcode() {
            public void handler() {
		B(B() ^ RM(EAD()));
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $e9 ADCB indexed ***** */
	public static opcode adcb_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = B() + t + (CC() & CC_C);
		CLR_HNZVC();
		SET_FLAGS8(B(),t,r);
		SET_H(B(),t,r);
		B( r );
            }
        };

	/* $eA ORB indexed -**0- */
	public static opcode orb_ix = new opcode() {
            public void handler() {
		B( B() | RM(EAD()) );
		CLR_NZV();
		SET_NZ8(B());
            }
        };

	/* $eb ADDB indexed ***** */
	public static opcode addb_ix = new opcode() {
            public void handler() {
		int t,r;
		t = RM(EAD());
		r = B() + t;
		CLR_HNZVC();
		SET_FLAGS8(B(),t,r);
		SET_H(B(),t,r);
		B( r );
            }
        };

	/* $ec LDD indexed -**0- */
	public static opcode ldd_ix = new opcode() {
            public void handler() {
		D(RM16(EAD()));
		CLR_NZV(); SET_NZ16(D());
            }
        };

	/* $eD STD indexed -**0- */
	public static opcode std_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ16(D());
		WM16(EAD(),pD());
            }
        };

	/* $eE LDU (LDS) indexed -**0- */
	public static opcode ldu_ix = new opcode() {
            public void handler() {
		U(RM16(EAD()));
		CLR_NZV();
		SET_NZ16(U());
            }
        };

	/* $10eE LDS indexed -**0- */
	public static opcode lds_ix = new opcode() {
            public void handler() {
		S(RM16(EAD()));
		CLR_NZV();
		SET_NZ16(S());
		konami.int_state |= KONAMI_LDS;
            }
        };

	/* $eF STU (STS) indexed -**0- */
	public static opcode stu_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ16(U());
		WM16(EAD(),pU());
            }
        };

	/* $10eF STS indexed -**0- */
	public static opcode sts_ix = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ16(S());
		WM16(EAD(),pS());
            }
        };

/*TODO*///	/* $f0 SUBB extended ?**** */
/*TODO*///	public static opcode subb_ex( void )
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
/*TODO*///	public static opcode cmpb_ex( void )
/*TODO*///	{
/*TODO*///		UINT16	  t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B - t;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS8(B,t,r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f2 SBCB extended ?**** */
/*TODO*///	public static opcode sbcb_ex( void )
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
/*TODO*///	public static opcode addd_ex( void )
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
/*TODO*///	public static opcode andb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		B &= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f5 BITB extended -**0- */
/*TODO*///	public static opcode bitb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t,r;
/*TODO*///		EXTBYTE(t);
/*TODO*///		r = B & t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(r);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f6 LDB extended -**0- */
/*TODO*///	public static opcode ldb_ex( void )
/*TODO*///	{
/*TODO*///		EXTBYTE(B);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f7 STB extended -**0- */
/*TODO*///	public static opcode stb_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///		EXTENDED;
/*TODO*///		WM(EAD,B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f8 EORB extended -**0- */
/*TODO*///	public static opcode eorb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		B ^= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $f9 ADCB extended ***** */
/*TODO*///	public static opcode adcb_ex( void )
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
/*TODO*///	public static opcode orb_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///		B |= t;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ8(B);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fB ADDB extended ***** */
/*TODO*///	public static opcode addb_ex( void )
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
/*TODO*///	public static opcode ldd_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pD);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fD STD extended -**0- */
/*TODO*///	public static opcode std_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(D);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pD);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fE LDU (LDS) extended -**0- */
/*TODO*///	public static opcode ldu_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pU);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10fE LDS extended -**0- */
/*TODO*///	public static opcode lds_ex( void )
/*TODO*///	{
/*TODO*///		EXTWORD(pS);
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		konami.int_state |= KONAMI_LDS;
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $fF STU (STS) extended -**0- */
/*TODO*///	public static opcode stu_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(U);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pU);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* $10fF STS extended -**0- */
/*TODO*///	public static opcode sts_ex( void )
/*TODO*///	{
/*TODO*///		CLR_NZV;
/*TODO*///		SET_NZ16(S);
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&pS);
/*TODO*///	}

	public static opcode setline_im = new opcode() {
            public void handler() {            
		int t;
		t = IMMBYTE();

		if ( konami_cpu_setlines_callback != null )
			(konami_cpu_setlines_callback).handler(t );
            }
        };

	public static opcode setline_ix = new opcode() {
            public void handler() {
		int t;
		t = RM(EA());

		if ( konami_cpu_setlines_callback != null )
			(konami_cpu_setlines_callback).handler( t );
            }
        };

/*TODO*///	public static opcode setline_di( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		DIRBYTE(t);
/*TODO*///
/*TODO*///		if ( konami_cpu_setlines_callback )
/*TODO*///			(*konami_cpu_setlines_callback)( t );
/*TODO*///	}
/*TODO*///
/*TODO*///	public static opcode setline_ex( void )
/*TODO*///	{
/*TODO*///		UINT8 t;
/*TODO*///		EXTBYTE(t);
/*TODO*///
/*TODO*///		if ( konami_cpu_setlines_callback )
/*TODO*///			(*konami_cpu_setlines_callback)( t );
/*TODO*///	}

	public static opcode bmove = new opcode() {
            public void handler() {
		int	t;

		while( U() != 0 ) {
			t = RM(Y());
			WM(X(),t);
			Y(Y()+1);
			X(X()+1);
			U(U()-1);
			konami_ICount[0] -= 2;
		}
            }
        };

	public static opcode move = new opcode() {
            public void handler() {
		int	t;

		t = RM(Y());
		WM(X(),t);
		Y(Y()+1);
		X(X()+1);
		U(U()-1);
            }
        };

	/* CLRD inherent -0100 */
	public static opcode clrd = new opcode() {
            public void handler() {
		D( 0 );
		CLR_NZVC(); SEZ();
            }
        };

	/* CLRW indexed -0100 */
	public static opcode clrw_ix = new opcode() {
            public void handler() {
		int t;
		t = 0;
		WM16(EAD(),t);
		CLR_NZVC(); SEZ();
            }
        };

/*TODO*///	/* CLRW direct -0100 */
/*TODO*///	public static opcode clrw_di( void )
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
/*TODO*///	public static opcode clrw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		t.d = 0;
/*TODO*///		EXTENDED;
/*TODO*///		WM16(EAD,&t);
/*TODO*///		CLR_NZVC; SEZ;
/*TODO*///	}

	/* LSRD immediate -0*-* */
	public static opcode lsrd = new opcode() {
            public void handler() {
		int t;

		t = IMMBYTE();

		while ( t-- != 0 ) {
			CLR_NZC();
			CC( CC() | (D() & CC_C) );
			D( D() >> 1 );
			SET_Z16(D());
		}
            }
        };

	/* RORD immediate -**-* */
	public static opcode rord = new opcode() {
            public void handler() {
		int r;
		int  t;

		t = IMMBYTE();

		while ( t-- != 0 ) {
			r = (CC() & CC_C) << 15;
			CLR_NZC();
			CC( CC() | (D() & CC_C) );
			r |= D() >> 1;
			SET_NZ16(r);
			D( r );
		}
            }
        };

	/* ASRD immediate ?**-* */
	public static opcode asrd = new opcode() {
            public void handler() {
		int t;

		t = IMMBYTE();

		while ( t-- != 0 ) {
			CLR_NZC();
			CC( CC() | (D() & CC_C) );
			D( (D() & 0x8000) | (D() >> 1) );
			SET_NZ16(D());
		}
            }
        };

	/* ASLD immediate ?**** */
	public static opcode asld = new opcode() {
            public void handler() {
		int	r;
		int	t;

		t = IMMBYTE();

		while ( t-- != 0 ) {
			r = D() << 1;
			CLR_NZVC();
			SET_FLAGS16(D(),D(),r);
			D( r );
		}
            }
        };

	/* ROLD immediate -**-* */
	public static opcode rold = new opcode() {
            public void handler() { 
		int r;
		int  t;

		t = IMMBYTE();

		while ( t-- != 0 ) {
			CLR_NZC();
			if (( D() & 0x8000 ) != 0) SEC();
			r = CC() & CC_C;
			r |= D() << 1;
			SET_NZ16(r);
			D( r );
		}
            }
        };

	/* DECB,JNZ relative ----- */
	public static opcode decbjnz = new opcode() {
            public void handler() {            
		B(B()-1);
		CLR_NZV();
		SET_FLAGS8D(B());
		BRANCH( (CC()&CC_Z)==0 );
            }
        };

	/* DECX,JNZ relative ----- */
	public static opcode decxjnz = new opcode() {
            public void handler() { 
		X(X()-1);
		CLR_NZV();
		SET_NZ16(X());	/* should affect V as well? */
		BRANCH( (CC()&CC_Z)==0 );
            }
        };

	public static opcode bset = new opcode() {
            public void handler() { 
		int	t;

		while( U() != 0 ) {
			t = A();
			WM(XD(),t);
			X(X()+1);
			U(U()-1);
			konami_ICount[0] -= 2;
		}
            }
        };

	public static opcode bset2 = new opcode() {
            public void handler() { 
		while( U() != 0 ) {
			WM16(XD(),pD());
			X( X() + 2 );
			U(U()-1);
			konami_ICount[0] -= 3;
		}
            }
        };

	/* LMUL inherent --*-@ */
	public static opcode lmul = new opcode() {
            public void handler() { 
		int t;
		t = X() * Y();
		X( (t >> 16) );
		Y( (t & 0xffff) );
		CLR_ZC(); SET_Z(t); if(( t & 0x8000 )!=0) SEC();
            }
        };

	/* DIVX inherent --*-@ */
	public static opcode divx = new opcode() {
            public void handler() { 
		int t;
		int r;
		if ( B() != 0 )
		{
			t = X() / B();
			r = X() % B();
		}
		else
		{
			/* ?? */
			t = 0;
			r = 0;
		}
		CLR_ZC(); SET_Z16(t); if (( t & 0x80 )!=0) SEC();
		X( t );
		B( r );
            }
        };

	/* INCD inherent -***- */
	public static opcode incd = new opcode() {
            public void handler() {
		int r;
		r = D() + 1;
		CLR_NZV();
		SET_FLAGS16(D(),D(),r);
		D( r );
            }
        };

/*TODO*///	/* INCW direct -***- */
/*TODO*///	public static opcode incw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r = t;
/*TODO*///		++r.d;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(t.d, t.d, r.d);;
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* INCW indexed -***- */
	public static opcode incw_ix = new opcode() {
            public void handler() {
		int t,r;
		t=RM16(EAD());
		r = t;
		++r;
		CLR_NZV();
		SET_FLAGS16(t, t, r);
		WM16(EAD(),r);
            }
        };

/*TODO*///	/* INCW extended -***- */
/*TODO*///	public static opcode incw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t, r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r = t;
/*TODO*///		++r.d;
/*TODO*///		CLR_NZV; SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* DECD inherent -***- */
	public static opcode decd = new opcode() {
            public void handler() {
		int r;
		r = D() - 1;
		CLR_NZV();
		SET_FLAGS16(D(),D(),r);
		D( r );
            }
        };

/*TODO*///	/* DECW direct -***- */
/*TODO*///	public static opcode decw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r = t;
/*TODO*///		--r.d;
/*TODO*///		CLR_NZV;
/*TODO*///		SET_FLAGS16(t.d, t.d, r.d);;
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* DECW indexed -***- */
	public static opcode decw_ix = new opcode() {
            public void handler() {
		int t, r;
		t=RM16(EAD());
		r = t;
		--r;
		CLR_NZV(); SET_FLAGS16(t, t, r);
		WM16(EAD(),r);
            }
        };

/*TODO*///	/* DECW extended -***- */
/*TODO*///	public static opcode decw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t, r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r = t;
/*TODO*///		--r.d;
/*TODO*///		CLR_NZV; SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* TSTD inherent -**0- */
	public static opcode tstd = new opcode() {
            public void handler() {
		CLR_NZV();
		SET_NZ16(D());
            }
        };

/*TODO*///	/* TSTW direct -**0- */
/*TODO*///	public static opcode tstw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		CLR_NZV;
/*TODO*///		DIRWORD(t);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///	}

	/* TSTW indexed -**0- */
	public static opcode tstw_ix = new opcode() {
            public void handler() {
		int t;
		CLR_NZV();
		t=RM16(EAD());
		SET_NZ16(t);
            }
        };

/*TODO*///	/* TSTW extended -**0- */
/*TODO*///	public static opcode tstw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		CLR_NZV;
/*TODO*///		EXTWORD(t);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///	}
/*TODO*///
/*TODO*///	/* LSRW direct -0*-* */
/*TODO*///	public static opcode lsrw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		DIRWORD(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d >>= 1;
/*TODO*///		SET_Z16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}

	/* LSRW indexed -0*-* */
	public static opcode lsrw_ix = new opcode() {
            public void handler() {
		int t;
		t=RM16(EAD());
		CLR_NZC();
		CC( CC() | (t & CC_C) );
		t >>= 1;
		SET_Z16(t);
		WM16(EAD(),t);
            }
        };

/*TODO*///	/* LSRW extended -0*-* */
/*TODO*///	public static opcode lsrw_ex( void )
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
/*TODO*///	public static opcode rorw_di( void )
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

	/* RORW indexed -**-* */
	public static opcode rorw_ix = new opcode() {
            public void handler() {
		int t,r;
		t=RM16(EAD());
		r = (CC() & CC_C) << 15;
		CLR_NZC();
		CC( CC() | (t & CC_C) );
		r |= t>>1;
		SET_NZ16(r);
		WM16(EAD(),r);
            }
        };

/*TODO*///	/* RORW extended -**-* */
/*TODO*///	public static opcode rorw_ex( void )
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
/*TODO*///	public static opcode asrw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t;
/*TODO*///		DIRWORD(t);
/*TODO*///		CLR_NZC;
/*TODO*///		CC |= (t.d & CC_C);
/*TODO*///		t.d = (t.d & 0x8000) | (t.d >> 1);
/*TODO*///		SET_NZ16(t.d);
/*TODO*///		WM16(EAD,&t);
/*TODO*///	}

	/* ASRW indexed ?**-* */
	public static opcode asrw_ix = new opcode() {
            public void handler() {
		int t;
		t=RM16(EAD());
		CLR_NZC();
		CC( CC() | (t & CC_C) );
		t = (t & 0x8000) | (t >> 1);
		SET_NZ16(t);
		WM16(EAD(),t);
            }
        };

/*TODO*///	/* ASRW extended ?**-* */
/*TODO*///	public static opcode asrw_ex( void )
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
/*TODO*///	public static opcode aslw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = t.d << 1;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* ASLW indexed ?**** */
	public static opcode aslw_ix = new opcode() {
            public void handler() {
		int t,r;
		t=RM16(EAD());
		r = t << 1;
		CLR_NZVC();
		SET_FLAGS16(t,t,r);
		WM16(EAD(),r);
            }
        };

/*TODO*///	/* ASLW extended ?**** */
/*TODO*///	public static opcode aslw_ex( void )
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
/*TODO*///	public static opcode rolw_di( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = (CC & CC_C) | (t.d << 1);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* ROLW indexed -**** */
	public static opcode rolw_ix = new opcode() {
            public void handler() {
		int t,r;
		t=RM16(EAD());
		r = (CC() & CC_C) | (t << 1);
		CLR_NZVC();
		SET_FLAGS16(t,t,r);
		WM16(EAD(),r);
            }
        };

/*TODO*///	/* ROLW extended -**** */
/*TODO*///	public static opcode rolw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR t,r;
/*TODO*///		EXTWORD(t);
/*TODO*///		r.d = (CC & CC_C) | (t.d << 1);
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(t.d,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* NEGD inherent ?**** */
	public static opcode negd = new opcode() {
            public void handler() {
		int r;
		r = -D();
		CLR_NZVC();
		SET_FLAGS16(0,D(),r);
		D( r );
            }
        };

/*TODO*///	/* NEGW direct ?**** */
/*TODO*///	public static opcode negw_di( void )
/*TODO*///	{
/*TODO*///		PAIR r,t;
/*TODO*///		DIRWORD(t);
/*TODO*///		r.d = -t.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* NEGW indexed ?**** */
	public static opcode negw_ix = new opcode() {
            public void handler() {
		int r,t;
		t=RM16(EAD());
		r = -t;
		CLR_NZVC();
		SET_FLAGS16(0,t,r);
		WM16(EAD(),r);
            }
        };

/*TODO*///	/* NEGW extended ?**** */
/*TODO*///	public static opcode negw_ex( void )
/*TODO*///	{
/*TODO*///		PAIR r,t;
/*TODO*///		EXTWORD(t);
/*TODO*///		r.d = -t.d;
/*TODO*///		CLR_NZVC;
/*TODO*///		SET_FLAGS16(0,t.d,r.d);
/*TODO*///		WM16(EAD,&r);
/*TODO*///	}

	/* ABSA inherent ?**** */
	public static opcode absa = new opcode() {
            public void handler() {
		int r;
		if ((A() & 0x80) != 0)
			r = -A();
		else
			r = A();
		CLR_NZVC();
		SET_FLAGS8(0,A(),r);
		A( r );
            }
        };

	/* ABSB inherent ?**** */
	public static opcode absb = new opcode() {
            public void handler() {
		int r;
		if ((B() & 0x80) != 0)
			r = -B();
		else
			r = B();
		CLR_NZVC();
		SET_FLAGS8(0,B(),r);
		B( r );
            }
        };

	/* ABSD inherent ?**** */
	public static opcode absd = new opcode() {
            public void handler() {
		int r;
		if ((D() & 0x8000) != 0)
			r = -D();
		else
			r = D();
		CLR_NZVC();
		SET_FLAGS16(0,D(),r);
		D( r );
            }
        };

/*TODO*///	/* LSRD direct -0*-* */
/*TODO*///	public static opcode lsrd_di( void )
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
/*TODO*///	public static opcode rord_di( void )
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
/*TODO*///	public static opcode asrd_di( void )
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
/*TODO*///	public static opcode asld_di( void )
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
/*TODO*///	public static opcode rold_di( void )
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

	/* LSRD indexed -0*-* */
	public static opcode lsrd_ix = new opcode() {
            public void handler() {
		int t;

		t=RM(EA());

		while ( t-- != 0 ) {
			CLR_NZC();
			CC( CC() | (D() & CC_C) );
			D( D() >> 1 );
			SET_Z16(D());
		}
            }
        };

	/* RORD indexed -**-* */
	public static opcode rord_ix = new opcode() {
            public void handler() {
		int r;
		int  t;

		t=RM(EA());

		while ( t-- != 0 ) {
			r = (CC() & CC_C) << 15;
			CLR_NZC();
			CC( CC() | (D() & CC_C) );
			r |= D() >> 1;
			SET_NZ16(r);
			D( r );
		}
            }
        };

	/* ASRD indexed ?**-* */
	public static opcode asrd_ix = new opcode() {
            public void handler() {
		int t;

		t=RM(EA());

		while ( t-- != 0 ) {
			CLR_NZC();
			CC( CC() | (D() & CC_C) );
			D( (D() & 0x8000) | (D() >> 1) );
			SET_NZ16(D());
		}
            }
        };

	/* ASLD indexed ?**** */
	public static opcode asld_ix = new opcode() {
            public void handler() {
		int	r;
		int	t;

		t=RM(EA());

		while ( t-- != 0 ) {
			r = D() << 1;
			CLR_NZVC();
			SET_FLAGS16(D(),D(),r);
			D( r );
		}
            }
        };

	/* ROLD indexed -**-* */
	public static opcode rold_ix = new opcode() {
            public void handler() {
		int r;
		int  t;

		t=RM(EA());

		while ( t-- != 0 ) {
			CLR_NZC();
			if (( D() & 0x8000 ) != 0) SEC();
			r = CC() & CC_C;
			r |= D() << 1;
			SET_NZ16(r);
			D( r );
		}
            }
        };

/*TODO*///	/* LSRD extended -0*-* */
/*TODO*///	public static opcode lsrd_ex( void )
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
/*TODO*///	public static opcode rord_ex( void )
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
/*TODO*///	public static opcode asrd_ex( void )
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
/*TODO*///	public static opcode asld_ex( void )
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
/*TODO*///	public static opcode rold_ex( void )
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
                int ireg2 = ROP_ARG(PCD());
		PC(PC()+1);

		switch ( ireg2 ) {
	//	case 0x00: EA=0; break; /* auto increment */
	//	case 0x01: EA=0; break; /* double auto increment */
	//	case 0x02: EA=0; break; /* auto decrement */
	//	case 0x03: EA=0; break; /* double auto decrement */
	//	case 0x04: EA=0; break; /* postbyte offs */
	//	case 0x05: EA=0; break; /* postword offs */
	//	case 0x06: EA=0; break; /* normal */
		case 0x07:
                        System.out.println("konami_extended table NOT IMPLEMENTED!!!!");
			EAD(0);
			(konami_extended[konami.ireg]).handler();
			konami_ICount[0] -= 2;
			return;
	//	case 0x08: EA=0; break; /* indirect - auto increment */
	//	case 0x09: EA=0; break; /* indirect - double auto increment */
	//	case 0x0a: EA=0; break; /* indirect - auto decrement */
	//	case 0x0b: EA=0; break; /* indirect - double auto decrement */
	//	case 0x0c: EA=0; break; /* indirect - postbyte offs */
	//	case 0x0d: EA=0; break; /* indirect - postword offs */
	//	case 0x0e: EA=0; break; /* indirect - normal */
		case 0x0f:				/* indirect - extended */
			ea = IMMWORD();
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
	//	case 0x10: EA=0; break; /* auto increment */
	//	case 0x11: EA=0; break; /* double auto increment */
	//	case 0x12: EA=0; break; /* auto decrement */
	//	case 0x13: EA=0; break; /* double auto decrement */
	//	case 0x14: EA=0; break; /* postbyte offs */
	//	case 0x15: EA=0; break; /* postword offs */
	//	case 0x16: EA=0; break; /* normal */
	//	case 0x17: EA=0; break; /* extended */
	//	case 0x18: EA=0; break; /* indirect - auto increment */
	//	case 0x19: EA=0; break; /* indirect - double auto increment */
	//	case 0x1a: EA=0; break; /* indirect - auto decrement */
	//	case 0x1b: EA=0; break; /* indirect - double auto decrement */
	//	case 0x1c: EA=0; break; /* indirect - postbyte offs */
	//	case 0x1d: EA=0; break; /* indirect - postword offs */
	//	case 0x1e: EA=0; break; /* indirect - normal */
	//	case 0x1f: EA=0; break; /* indirect - extended */

	/* base X */
		case 0x20:              /* auto increment */
			EA(X());
			X(X()+1);
			konami_ICount[0]-=2;
			break;
		case 0x21:				/* double auto increment */
			EA(X());
			X(X()+2);
			konami_ICount[0]-=3;
			break;
		case 0x22:				/* auto decrement */
			X(X()-1);
			EA(X());
			konami_ICount[0]-=2;
			break;
		case 0x23:				/* double auto decrement */
			X(X()-2);
			EA(X());
			konami_ICount[0]-=3;
			break;
		case 0x24:				/* postbyte offs */
			EA(IMMBYTE());
			EA(X()+SIGNED(EA()));
			konami_ICount[0]-=2;
			break;
		case 0x25:				/* postword offs */
			ea=IMMWORD();
			EA(EA()+X());
			konami_ICount[0]-=4;
			break;
		case 0x26:				/* normal */
			EA(X());
			break;
	//	case 0x27: EA=0; break; /* extended */
		case 0x28:				/* indirect - auto increment */
			EA(X());
			X(X()+1);
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x29:				/* indirect - double auto increment */
			EA(X());
			X(X()+2);
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x2a:				/* indirect - auto decrement */
			X(X()-1);
			EA(X());
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x2b:				/* indirect - double auto decrement */
			X(X()-2);
			EA(X());
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x2c:				/* indirect - postbyte offs */
			EA(IMMBYTE());
			EA(X()+SIGNED(EA()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0x2d:				/* indirect - postword offs */
			ea=IMMWORD();
			EA(EA()+X());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0x2e:				/* indirect - normal */
			EA(X());
			EA(RM16(EAD()));
			konami_ICount[0]-=3;
			break;
	//	case 0x2f: EA=0; break; /* indirect - extended */

	/* base Y */
		case 0x30:              /* auto increment */
			EA(Y());
			Y(Y()+1);
			konami_ICount[0]-=2;
			break;
		case 0x31:				/* double auto increment */
			EA(Y());
			Y(Y()+2);
			konami_ICount[0]-=3;
			break;
		case 0x32:				/* auto decrement */
			Y(Y()-1);
			EA(Y());
			konami_ICount[0]-=2;
			break;
		case 0x33:				/* double auto decrement */
			Y(Y()-2);
			EA(Y());
			konami_ICount[0]-=3;
			break;
		case 0x34:				/* postbyte offs */
			EA(IMMBYTE());
			EA(Y()+SIGNED(EA()));
			konami_ICount[0]-=2;
			break;
		case 0x35:				/* postword offs */
			ea=IMMWORD();
			EA(EA()+Y());
			konami_ICount[0]-=4;
			break;
		case 0x36:				/* normal */
			EA(Y());
			break;
	//	case 0x37: EA=0; break; /* extended */
		case 0x38:				/* indirect - auto increment */
			EA(Y());
			Y(Y()+1);
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x39:				/* indirect - double auto increment */
			EA(Y());
			Y(Y()+2);
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x3a:				/* indirect - auto decrement */
			Y(Y()-1);
			EA(Y());
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x3b:				/* indirect - double auto decrement */
			Y(Y()-2);
			EA(Y());
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x3c:				/* indirect - postbyte offs */
			EA(IMMBYTE());
			EA(Y()+SIGNED(EA()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0x3d:				/* indirect - postword offs */
			ea=IMMWORD();
			EA(EA()+Y());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0x3e:				/* indirect - normal */
			EA(Y());
			EA(RM16(EAD()));
			konami_ICount[0]-=3;
			break;
	//	case 0x3f: EA=0; break; /* indirect - extended */

	//  case 0x40: EA=0; break; /* auto increment */
	//	case 0x41: EA=0; break; /* double auto increment */
	//	case 0x42: EA=0; break; /* auto decrement */
	//	case 0x43: EA=0; break; /* double auto decrement */
	//	case 0x44: EA=0; break; /* postbyte offs */
	//	case 0x45: EA=0; break; /* postword offs */
	//	case 0x46: EA=0; break; /* normal */
	//	case 0x47: EA=0; break; /* extended */
	//	case 0x48: EA=0; break; /* indirect - auto increment */
	//	case 0x49: EA=0; break; /* indirect - double auto increment */
	//	case 0x4a: EA=0; break; /* indirect - auto decrement */
	//	case 0x4b: EA=0; break; /* indirect - double auto decrement */
	//	case 0x4c: EA=0; break; /* indirect - postbyte offs */
	//	case 0x4d: EA=0; break; /* indirect - postword offs */
	//	case 0x4e: EA=0; break; /* indirect - normal */
	//	case 0x4f: EA=0; break; /* indirect - extended */

	/* base U */
		case 0x50:              /* auto increment */
			EA(U());
			U(U()+1);
			konami_ICount[0]-=2;
			break;
		case 0x51:				/* double auto increment */
			EA(U());
			U(U()+2);
			konami_ICount[0]-=3;
			break;
		case 0x52:				/* auto decrement */
			U(U()-1);
			EA(U());
			konami_ICount[0]-=2;
			break;
		case 0x53:				/* double auto decrement */
			U(U()-2);
			EA(U());
			konami_ICount[0]-=3;
			break;
		case 0x54:				/* postbyte offs */
			EA(IMMBYTE());
			EA(U()+SIGNED(EA()));
			konami_ICount[0]-=2;
			break;
		case 0x55:				/* postword offs */
			ea=IMMWORD();
			EA( EA()+U());
			konami_ICount[0]-=4;
			break;
		case 0x56:				/* normal */
			EA(U());
			break;
	//	case 0x57: EA=0; break; /* extended */
		case 0x58:				/* indirect - auto increment */
			EA(U());
			U(U()+1);
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x59:				/* indirect - double auto increment */
			EA(U());
			U(U()+2);
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x5a:				/* indirect - auto decrement */
			U(U()-1);
			EA(U());
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x5b:				/* indirect - double auto decrement */
			U(U()-2);
			EA(U());
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x5c:				/* indirect - postbyte offs */
			EA(IMMBYTE());
			EA(U()+SIGNED(EA()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0x5d:				/* indirect - postword offs */
			ea=IMMWORD();
			EA(EA()+U());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0x5e:				/* indirect - normal */
			EA(U());
			EA(RM16(EAD()));
			konami_ICount[0]-=3;
			break;
	//	case 0x5f: EA=0; break; /* indirect - extended */

	/* base S */
		case 0x60:              /* auto increment */
			EAD(SD());
			S(S()+1);
			konami_ICount[0]-=2;
			break;
		case 0x61:				/* double auto increment */
			EAD(SD());
			S(S()+2);
			konami_ICount[0]-=3;
			break;
		case 0x62:				/* auto decrement */
			S(S()-1);
			EAD(SD());
			konami_ICount[0]-=2;
			break;
		case 0x63:				/* double auto decrement */
			S(S()-2);
			EAD(SD());
			konami_ICount[0]-=3;
			break;
		case 0x64:				/* postbyte offs */
			EA(IMMBYTE());
			EA(S()+SIGNED(EA()));
			konami_ICount[0]-=2;
			break;
		case 0x65:				/* postword offs */
			ea=IMMWORD();
			EA(EA()+S());
			konami_ICount[0]-=4;
			break;
		case 0x66:				/* normal */
			EAD(SD());
			break;
	//	case 0x67: EA=0; break; /* extended */
		case 0x68:				/* indirect - auto increment */
			EAD(SD());
			S(S()+1);
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x69:				/* indirect - double auto increment */
			EAD(SD());
			S(S()+2);
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x6a:				/* indirect - auto decrement */
			S(S()-1);
			EAD(SD());
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x6b:				/* indirect - double auto decrement */
			S(S()-2);
			EAD(SD());
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x6c:				/* indirect - postbyte offs */
			EA(IMMBYTE());
			EA(S()+SIGNED(EA()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0x6d:				/* indirect - postword offs */
			ea=IMMWORD();
			EA(EA()+S());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0x6e:				/* indirect - normal */
			EAD(SD());
			EA(RM16(EAD()));
			konami_ICount[0]-=3;
			break;
	//	case 0x6f: EA=0; break; /* indirect - extended */

	/* base PC */
		case 0x70:              /* auto increment */
			EAD(PCD());
			PC(PC()+1);
			konami_ICount[0]-=2;
			break;
		case 0x71:				/* double auto increment */
			EAD(PCD());
			PC(PC()+2);
			konami_ICount[0]-=3;
			break;
		case 0x72:				/* auto decrement */
			PC(PC()-1);
			EAD(PCD());
			konami_ICount[0]-=2;
			break;
		case 0x73:				/* double auto decrement */
			PC(PC()-2);
			EAD(PCD());
			konami_ICount[0]-=3;
			break;
		case 0x74:				/* postbyte offs */
			EA(IMMBYTE());
			EA(PC()-1+SIGNED(EA()));
			konami_ICount[0]-=2;
			break;
		case 0x75:				/* postword offs */
			ea=IMMWORD();
			EA(EA()+PC()-2);
			konami_ICount[0]-=4;
			break;
		case 0x76:				/* normal */
			EAD(PCD());
			break;
	//	case 0x77: EA=0; break; /* extended */
		case 0x78:				/* indirect - auto increment */
			EAD(PCD());
			PC(PC()+1);
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x79:				/* indirect - double auto increment */
			EAD(PCD());
			PC(PC()+2);
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x7a:				/* indirect - auto decrement */
			PC(PC()-1);
			EAD(PCD());
			EA(RM16(EAD()));
			konami_ICount[0]-=5;
			break;
		case 0x7b:				/* indirect - double auto decrement */
			PC(PC()-2);
			EAD(PCD());
			EA(RM16(EAD()));
			konami_ICount[0]-=6;
			break;
		case 0x7c:				/* indirect - postbyte offs */
			EA(IMMBYTE());
			EA(PC()-1+SIGNED(EA()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0x7d:				/* indirect - postword offs */
			ea=IMMWORD();
			EA(EA()+PC()-2);
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0x7e:				/* indirect - normal */
			EAD(PCD());
			EA(RM16(EAD()));
			konami_ICount[0]-=3;
			break;
	//	case 0x7f: EA=0; break; /* indirect - extended */

	//  case 0x80: EA=0; break; /* register a */
	//	case 0x81: EA=0; break; /* register b */
	//	case 0x82: EA=0; break; /* ???? */
	//	case 0x83: EA=0; break; /* ???? */
	//	case 0x84: EA=0; break; /* ???? */
	//	case 0x85: EA=0; break; /* ???? */
	//	case 0x86: EA=0; break; /* ???? */
	//	case 0x87: EA=0; break; /* register d */
	//	case 0x88: EA=0; break; /* indirect - register a */
	//	case 0x89: EA=0; break; /* indirect - register b */
	//	case 0x8a: EA=0; break; /* indirect - ???? */
	//	case 0x8b: EA=0; break; /* indirect - ???? */
	//	case 0x8c: EA=0; break; /* indirect - ???? */
	//	case 0x8d: EA=0; break; /* indirect - ???? */
	//	case 0x8e: EA=0; break; /* indirect - register d */
	//	case 0x8f: EA=0; break; /* indirect - ???? */
	//	case 0x90: EA=0; break; /* register a */
	//	case 0x91: EA=0; break; /* register b */
	//	case 0x92: EA=0; break; /* ???? */
	//	case 0x93: EA=0; break; /* ???? */
	//	case 0x94: EA=0; break; /* ???? */
	//	case 0x95: EA=0; break; /* ???? */
	//	case 0x96: EA=0; break; /* ???? */
	//	case 0x97: EA=0; break; /* register d */
	//	case 0x98: EA=0; break; /* indirect - register a */
	//	case 0x99: EA=0; break; /* indirect - register b */
	//	case 0x9a: EA=0; break; /* indirect - ???? */
	//	case 0x9b: EA=0; break; /* indirect - ???? */
	//	case 0x9c: EA=0; break; /* indirect - ???? */
	//	case 0x9d: EA=0; break; /* indirect - ???? */
	//	case 0x9e: EA=0; break; /* indirect - register d */
	//	case 0x9f: EA=0; break; /* indirect - ???? */
		case 0xa0:				/* register a */
			EA(X()+SIGNED(A()));
			konami_ICount[0]-=1;
			break;
		case 0xa1:				/* register b */
			EA(X()+SIGNED(B()));
			konami_ICount[0]-=1;
			break;
	//	case 0xa2: EA=0; break; /* ???? */
	//	case 0xa3: EA=0; break; /* ???? */
	//	case 0xa4: EA=0; break; /* ???? */
	//	case 0xa5: EA=0; break; /* ???? */
	//	case 0xa6: EA=0; break; /* ???? */
		case 0xa7:				/* register d */
			EA(X()+D());
			konami_ICount[0]-=4;
			break;
		case 0xa8:				/* indirect - register a */
			EA(X()+SIGNED(A()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0xa9:				/* indirect - register b */
			EA(X()+SIGNED(B()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
	//	case 0xaa: EA=0; break; /* indirect - ???? */
	//	case 0xab: EA=0; break; /* indirect - ???? */
	//	case 0xac: EA=0; break; /* indirect - ???? */
	//	case 0xad: EA=0; break; /* indirect - ???? */
	//	case 0xae: EA=0; break; /* indirect - ???? */
		case 0xaf:				/* indirect - register d */
			EA(X()+D());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0xb0:				/* register a */
			EA(Y()+SIGNED(A()));
			konami_ICount[0]-=1;
			break;
		case 0xb1:				/* register b */
			EA(Y()+SIGNED(B()));
			konami_ICount[0]-=1;
			break;
	//	case 0xb2: EA=0; break; /* ???? */
	//	case 0xb3: EA=0; break; /* ???? */
	//	case 0xb4: EA=0; break; /* ???? */
	//	case 0xb5: EA=0; break; /* ???? */
	//	case 0xb6: EA=0; break; /* ???? */
		case 0xb7:				/* register d */
			EA(Y()+D());
			konami_ICount[0]-=4;
			break;
		case 0xb8:				/* indirect - register a */
			EA(Y()+SIGNED(A()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0xb9:				/* indirect - register b */
			EA(Y()+SIGNED(B()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
	//	case 0xba: EA=0; break; /* indirect - ???? */
	//	case 0xbb: EA=0; break; /* indirect - ???? */
	//	case 0xbc: EA=0; break; /* indirect - ???? */
	//	case 0xbd: EA=0; break; /* indirect - ???? */
	//	case 0xbe: EA=0; break; /* indirect - ???? */
		case 0xbf:				/* indirect - register d */
			EA(Y()+D());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
	//	case 0xc0: EA=0; break; /* register a */
	//	case 0xc1: EA=0; break; /* register b */
	//	case 0xc2: EA=0; break; /* ???? */
	//	case 0xc3: EA=0; break; /* ???? */
		case 0xc4:
                        System.out.println("konami_direct NOT IMPLEMENTED!!!!");
			EAD(0);
			(konami_direct[konami.ireg]).handler();
			konami_ICount[0] -= 1;
			return;
	//	case 0xc5: EA=0; break; /* ???? */
	//	case 0xc6: EA=0; break; /* ???? */
	//	case 0xc7: EA=0; break; /* register d */
	//	case 0xc8: EA=0; break; /* indirect - register a */
	//	case 0xc9: EA=0; break; /* indirect - register b */
	//	case 0xca: EA=0; break; /* indirect - ???? */
	//	case 0xcb: EA=0; break; /* indirect - ???? */
		case 0xcc:				/* indirect - direct */
			ea=DIRWORD();
			konami_ICount[0]-=4;
			break;
	//	case 0xcd: EA=0; break; /* indirect - ???? */
	//	case 0xce: EA=0; break; /* indirect - register d */
	//	case 0xcf: EA=0; break; /* indirect - ???? */
		case 0xd0:				/* register a */
			EA(U()+SIGNED(A()));
			konami_ICount[0]-=1;
			break;
		case 0xd1:				/* register b */
			EA(U()+SIGNED(B()));
			konami_ICount[0]-=1;
			break;
	//	case 0xd2: EA=0; break; /* ???? */
	//	case 0xd3: EA=0; break; /* ???? */
	//	case 0xd4: EA=0; break; /* ???? */
	//	case 0xd5: EA=0; break; /* ???? */
	//	case 0xd6: EA=0; break; /* ???? */
		case 0xd7:				/* register d */
			EA(U()+D());
			konami_ICount[0]-=4;
			break;
		case 0xd8:				/* indirect - register a */
			EA(U()+SIGNED(A()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0xd9:				/* indirect - register b */
			EA(U()+SIGNED(B()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
	//	case 0xda: EA=0; break; /* indirect - ???? */
	//	case 0xdb: EA=0; break; /* indirect - ???? */
	//	case 0xdc: EA=0; break; /* indirect - ???? */
	//	case 0xdd: EA=0; break; /* indirect - ???? */
	//	case 0xde: EA=0; break; /* indirect - ???? */
		case 0xdf:				/* indirect - register d */
			EA(U()+D());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0xe0:				/* register a */
			EA(S()+SIGNED(A()));
			konami_ICount[0]-=1;
			break;
		case 0xe1:				/* register b */
			EA(S()+SIGNED(B()));
			konami_ICount[0]-=1;
			break;
	//	case 0xe2: EA=0; break; /* ???? */
	//	case 0xe3: EA=0; break; /* ???? */
	//	case 0xe4: EA=0; break; /* ???? */
	//	case 0xe5: EA=0; break; /* ???? */
	//	case 0xe6: EA=0; break; /* ???? */
		case 0xe7:				/* register d */
			EA(S()+D());
			konami_ICount[0]-=4;
			break;
		case 0xe8:				/* indirect - register a */
			EA(S()+SIGNED(A()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0xe9:				/* indirect - register b */
			EA(S()+SIGNED(B()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
	//	case 0xea: EA=0; break; /* indirect - ???? */
	//	case 0xeb: EA=0; break; /* indirect - ???? */
	//	case 0xec: EA=0; break; /* indirect - ???? */
	//	case 0xed: EA=0; break; /* indirect - ???? */
	//	case 0xee: EA=0; break; /* indirect - ???? */
		case 0xef:				/* indirect - register d */
			EA(S()+D());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		case 0xf0:				/* register a */
			EA(PC()+SIGNED(A()));
			konami_ICount[0]-=1;
			break;
		case 0xf1:				/* register b */
			EA(PC()+SIGNED(B()));
			konami_ICount[0]-=1;
			break;
	//	case 0xf2: EA=0; break; /* ???? */
	//	case 0xf3: EA=0; break; /* ???? */
	//	case 0xf4: EA=0; break; /* ???? */
	//	case 0xf5: EA=0; break; /* ???? */
	//	case 0xf6: EA=0; break; /* ???? */
		case 0xf7:				/* register d */
			EA(PC()+D());
			konami_ICount[0]-=4;
			break;
		case 0xf8:				/* indirect - register a */
			EA(PC()+SIGNED(A()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
		case 0xf9:				/* indirect - register b */
			EA(PC()+SIGNED(B()));
			EA(RM16(EAD()));
			konami_ICount[0]-=4;
			break;
	//	case 0xfa: EA=0; break; /* indirect - ???? */
	//	case 0xfb: EA=0; break; /* indirect - ???? */
	//	case 0xfc: EA=0; break; /* indirect - ???? */
	//	case 0xfd: EA=0; break; /* indirect - ???? */
	//	case 0xfe: EA=0; break; /* indirect - ???? */
		case 0xff:				/* indirect - register d */
			EA(PC()+D());
			EA(RM16(EAD()));
			konami_ICount[0]-=7;
			break;
		default:
			logerror("KONAMI: Unknown/Invalid postbyte at PC = %04x\n", PC() -1 );
			EAD( 0 );
		}
                System.out.println("konami_indexed NOT IMPLEMENTED!!!!");
		(konami_indexed[konami.ireg]).handler();
            }
        };
    
}
