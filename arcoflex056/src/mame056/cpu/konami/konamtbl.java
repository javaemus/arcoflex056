/**
 * Ported to 0.56
 */
package mame056.cpu.konami;

import static mame056.cpu.konami.konamops.*;

/**
 *
 * @author chusogar
 */
public class konamtbl {
    
    public abstract interface opcode {
        public abstract void handler();
    }
    
    static opcode[] konami_main = {
	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 00 */
/*TODO*///	opcode2,opcode2,opcode2,opcode2,pshs   ,pshu   ,puls   ,pulu   ,
/*TODO*///	lda_im ,ldb_im ,opcode2,opcode2,adda_im,addb_im,opcode2,opcode2,	/* 10 */
/*TODO*///	adca_im,adcb_im,opcode2,opcode2,suba_im,subb_im,opcode2,opcode2,
/*TODO*///	sbca_im,sbcb_im,opcode2,opcode2,anda_im,andb_im,opcode2,opcode2,	/* 20 */
/*TODO*///	bita_im,bitb_im,opcode2,opcode2,eora_im,eorb_im,opcode2,opcode2,
/*TODO*///	ora_im ,orb_im ,opcode2,opcode2,cmpa_im,cmpb_im,opcode2,opcode2,	/* 30 */
/*TODO*///	setline_im,opcode2,opcode2,opcode2,andcc,orcc  ,exg    ,tfr    ,
/*TODO*///	ldd_im ,opcode2,ldx_im ,opcode2,ldy_im ,opcode2,ldu_im ,opcode2,	/* 40 */
/*TODO*///	lds_im ,opcode2,cmpd_im,opcode2,cmpx_im,opcode2,cmpy_im,opcode2,
/*TODO*///	cmpu_im,opcode2,cmps_im,opcode2,addd_im,opcode2,subd_im,opcode2,	/* 50 */
/*TODO*///	opcode2,opcode2,opcode2,opcode2,opcode2,illegal,illegal,illegal,
/*TODO*///	bra    ,bhi    ,bcc    ,bne    ,bvc    ,bpl    ,bge    ,bgt    ,	/* 60 */
/*TODO*///	lbra   ,lbhi   ,lbcc   ,lbne   ,lbvc   ,lbpl   ,lbge   ,lbgt   ,
/*TODO*///	brn    ,bls    ,bcs    ,beq    ,bvs    ,bmi    ,blt    ,ble    ,	/* 70 */
/*TODO*///	lbrn   ,lbls   ,lbcs   ,lbeq   ,lbvs   ,lbmi   ,lblt   ,lble   ,
/*TODO*///	clra   ,clrb   ,opcode2,coma   ,comb   ,opcode2,nega   ,negb   ,	/* 80 */
/*TODO*///	opcode2,inca   ,incb   ,opcode2,deca   ,decb   ,opcode2,rts    ,
/*TODO*///	tsta   ,tstb   ,opcode2,lsra   ,lsrb   ,opcode2,rora   ,rorb   ,	/* 90 */
/*TODO*///	opcode2,asra   ,asrb   ,opcode2,asla   ,aslb   ,opcode2,rti    ,
/*TODO*///	rola   ,rolb   ,opcode2,opcode2,opcode2,opcode2,opcode2,opcode2,	/* a0 */
/*TODO*///	opcode2,opcode2,bsr    ,lbsr   ,decbjnz,decxjnz,nop    ,illegal,
/*TODO*///	abx    ,daa	   ,sex    ,mul    ,lmul   ,divx   ,bmove  ,move   ,	/* b0 */
/*TODO*///	lsrd   ,opcode2,rord   ,opcode2,asrd   ,opcode2,asld   ,opcode2,
/*TODO*///	rold   ,opcode2,clrd   ,opcode2,negd   ,opcode2,incd   ,opcode2,	/* c0 */
/*TODO*///	decd   ,opcode2,tstd   ,opcode2,absa   ,absb   ,absd   ,bset   ,
/*TODO*///	bset2  ,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* d0 */
	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* e0 */
	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* f0 */
	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal
    };

/*TODO*///static void (*konami_indexed[0x100])(void) = {
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 00 */
/*TODO*///	leax   ,leay   ,leau   ,leas   ,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,lda_ix ,ldb_ix ,illegal,illegal,adda_ix,addb_ix,	/* 10 */
/*TODO*///	illegal,illegal,adca_ix,adcb_ix,illegal,illegal,suba_ix,subb_ix,
/*TODO*///	illegal,illegal,sbca_ix,sbcb_ix,illegal,illegal,anda_ix,andb_ix,	/* 20 */
/*TODO*///	illegal,illegal,bita_ix,bitb_ix,illegal,illegal,eora_ix,eorb_ix,
/*TODO*///	illegal,illegal,ora_ix ,orb_ix ,illegal,illegal,cmpa_ix,cmpb_ix,	/* 30 */
/*TODO*///	illegal,setline_ix,sta_ix,stb_ix,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,ldd_ix ,illegal,ldx_ix ,illegal,ldy_ix ,illegal,ldu_ix ,	/* 40 */
/*TODO*///	illegal,lds_ix ,illegal,cmpd_ix,illegal,cmpx_ix,illegal,cmpy_ix,
/*TODO*///	illegal,cmpu_ix,illegal,cmps_ix,illegal,addd_ix,illegal,subd_ix,	/* 50 */
/*TODO*///	std_ix ,stx_ix ,sty_ix ,stu_ix ,sts_ix ,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 60 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 70 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,clr_ix ,illegal,illegal,com_ix ,illegal,illegal,	/* 80 */
/*TODO*///	neg_ix ,illegal,illegal,inc_ix ,illegal,illegal,dec_ix ,illegal,
/*TODO*///	illegal,illegal,tst_ix ,illegal,illegal,lsr_ix ,illegal,illegal,	/* 90 */
/*TODO*///	ror_ix ,illegal,illegal,asr_ix ,illegal,illegal,asl_ix ,illegal,
/*TODO*///	illegal,illegal,rol_ix ,lsrw_ix,rorw_ix,asrw_ix,aslw_ix,rolw_ix,	/* a0 */
/*TODO*///	jmp_ix ,jsr_ix ,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* b0 */
/*TODO*///	illegal,lsrd_ix,illegal,rord_ix,illegal,asrd_ix,illegal,asld_ix,
/*TODO*///	illegal,rold_ix,illegal,clrw_ix,illegal,negw_ix,illegal,incw_ix,	/* c0 */
/*TODO*///	illegal,decw_ix,illegal,tstw_ix,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* d0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* e0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* f0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal
/*TODO*///};
/*TODO*///
/*TODO*///static void (*konami_direct[0x100])(void) = {
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 00 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,lda_di ,ldb_di ,illegal,illegal,adda_di,addb_di,	/* 10 */
/*TODO*///	illegal,illegal,adca_di,adcb_di,illegal,illegal,suba_di,subb_di,
/*TODO*///	illegal,illegal,sbca_di,sbcb_di,illegal,illegal,anda_di,andb_di,	/* 20 */
/*TODO*///	illegal,illegal,bita_di,bitb_di,illegal,illegal,eora_di,eorb_di,
/*TODO*///	illegal,illegal,ora_di ,orb_di ,illegal,illegal,cmpa_di,cmpb_di,	/* 30 */
/*TODO*///	illegal,setline_di,sta_di,stb_di,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,ldd_di ,illegal,ldx_di ,illegal,ldy_di ,illegal,ldu_di ,	/* 40 */
/*TODO*///	illegal,lds_di ,illegal,cmpd_di,illegal,cmpx_di,illegal,cmpy_di,
/*TODO*///	illegal,cmpu_di,illegal,cmps_di,illegal,addd_di,illegal,subd_di,	/* 50 */
/*TODO*///	std_di ,stx_di ,sty_di ,stu_di ,sts_di ,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 60 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 70 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,clr_di ,illegal,illegal,com_di ,illegal,illegal,	/* 80 */
/*TODO*///	neg_di ,illegal,illegal,inc_di ,illegal,illegal,dec_di ,illegal,
/*TODO*///	illegal,illegal,tst_di ,illegal,illegal,lsr_di ,illegal,illegal,	/* 90 */
/*TODO*///	ror_di ,illegal,illegal,asr_di ,illegal,illegal,asl_di ,illegal,
/*TODO*///	illegal,illegal,rol_di ,lsrw_di,rorw_di,asrw_di,aslw_di,rolw_di,	/* a0 */
/*TODO*///	jmp_di ,jsr_di ,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* b0 */
/*TODO*///	illegal,lsrd_di,illegal,rord_di,illegal,asrd_di,illegal,asld_di,
/*TODO*///	illegal,rold_di,illegal,clrw_di,illegal,negw_di,illegal,incw_di,	/* c0 */
/*TODO*///	illegal,decw_di,illegal,tstw_di,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* d0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* e0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* f0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal
/*TODO*///};
/*TODO*///
/*TODO*///static void (*konami_extended[0x100])(void) = {
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 00 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,lda_ex ,ldb_ex ,illegal,illegal,adda_ex,addb_ex,	/* 10 */
/*TODO*///	illegal,illegal,adca_ex,adcb_ex,illegal,illegal,suba_ex,subb_ex,
/*TODO*///	illegal,illegal,sbca_ex,sbcb_ex,illegal,illegal,anda_ex,andb_ex,	/* 20 */
/*TODO*///	illegal,illegal,bita_ex,bitb_ex,illegal,illegal,eora_ex,eorb_ex,
/*TODO*///	illegal,illegal,ora_ex ,orb_ex ,illegal,illegal,cmpa_ex,cmpb_ex,	/* 30 */
/*TODO*///	illegal,setline_ex,sta_ex,stb_ex,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,ldd_ex ,illegal,ldx_ex ,illegal,ldy_ex ,illegal,ldu_ex ,	/* 40 */
/*TODO*///	illegal,lds_ex ,illegal,cmpd_ex,illegal,cmpx_ex,illegal,cmpy_ex,
/*TODO*///	illegal,cmpu_ex,illegal,cmps_ex,illegal,addd_ex,illegal,subd_ex,	/* 50 */
/*TODO*///	std_ex ,stx_ex ,sty_ex ,stu_ex ,sts_ex ,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 60 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* 70 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,clr_ex ,illegal,illegal,com_ex ,illegal,illegal,	/* 80 */
/*TODO*///	neg_ex ,illegal,illegal,inc_ex ,illegal,illegal,dec_ex ,illegal,
/*TODO*///	illegal,illegal,tst_ex ,illegal,illegal,lsr_ex ,illegal,illegal,	/* 90 */
/*TODO*///	ror_ex ,illegal,illegal,asr_ex ,illegal,illegal,asl_ex ,illegal,
/*TODO*///	illegal,illegal,rol_ex ,lsrw_ex,rorw_ex,asrw_ex,aslw_ex,rolw_ex,	/* a0 */
/*TODO*///	jmp_ex ,jsr_ex ,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* b0 */
/*TODO*///	illegal,lsrd_ex,illegal,rord_ex,illegal,asrd_ex,illegal,asld_ex,
/*TODO*///	illegal,rold_ex,illegal,clrw_ex,illegal,negw_ex,illegal,incw_ex,	/* c0 */
/*TODO*///	illegal,decw_ex,illegal,tstw_ex,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* d0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* e0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal,	/* f0 */
/*TODO*///	illegal,illegal,illegal,illegal,illegal,illegal,illegal,illegal
/*TODO*///};

}
