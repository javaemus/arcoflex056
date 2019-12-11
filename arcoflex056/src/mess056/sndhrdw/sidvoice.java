/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.sndhrdw;

import mess056.sndhrdw.sidvoiceH.sidOperator;

/**
 *
 * @author chusogar
 */
public class sidvoice {
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	static ubyte triangleTable[4096];
/*TODO*///	static ubyte sawtoothTable[4096];
/*TODO*///	static ubyte squareTable[2*4096];
/*TODO*///	static ubyte* waveform30;
/*TODO*///	static ubyte* waveform50;
/*TODO*///	static ubyte* waveform60;
/*TODO*///	static ubyte* waveform70;
/*TODO*///	#if defined(LARGE_NOISE_TABLE)
/*TODO*///	  static ubyte noiseTableMSB[1<<8];
/*TODO*///	  static ubyte noiseTableLSB[1L<<16];
/*TODO*///	#else
/*TODO*///	  static ubyte noiseTableMSB[1<<8];
/*TODO*///	  static ubyte noiseTableMID[1<<8];
/*TODO*///	  static ubyte noiseTableLSB[1<<8];
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	static sbyte* ampMod1x8;
/*TODO*///	
/*TODO*///	static const udword noiseSeed = 0x7ffff8;
/*TODO*///	
/*TODO*///	void sidInitMixerEngine(void)
/*TODO*///	{
/*TODO*///		uword uk;
/*TODO*///		sdword si, sj    ;
/*TODO*///	
/*TODO*///		/* 8-bit volume modulation tables. */
/*TODO*///		float filterAmpl = 1.0;
/*TODO*///	
/*TODO*///		filterAmpl = 0.7;
/*TODO*///	
/*TODO*///		ampMod1x8=(INT8*)malloc(256*256);
/*TODO*///		if (ampMod1x8 == 0) {
/*TODO*///			printf("out of memory\n");exit(1);
/*TODO*///		}
/*TODO*///	
/*TODO*///		uk = 0;
/*TODO*///		for ( si = 0; si < 256; si++ )
/*TODO*///		{
/*TODO*///			for ( sj = -128; sj < 128; sj++, uk++ )
/*TODO*///			{
/*TODO*///				ampMod1x8[uk] = (sbyte)(((si*sj)/255)*filterAmpl);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE void waveAdvance(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.waveStep.l += pVoice.waveStepAdd.l;
/*TODO*///		pVoice.waveStep.w[HI] &= 4095;
/*TODO*///	#else
/*TODO*///		pVoice.waveStepPnt += pVoice.waveStepAddPnt;
/*TODO*///		pVoice.waveStep += pVoice.waveStepAdd;
/*TODO*///		if (pVoice.waveStepPnt > 65535 ) pVoice.waveStep++;
/*TODO*///		pVoice.waveStepPnt &= 0xFFFF;
/*TODO*///		pVoice.waveStep &= 4095;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE void noiseAdvance(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		pVoice.noiseStep += pVoice.noiseStepAdd;
/*TODO*///		if (pVoice.noiseStep >= (1L<<20))
/*TODO*///		{
/*TODO*///			pVoice.noiseStep -= (1L<<20);
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			pVoice.noiseReg.l = (pVoice.noiseReg.l << 1) |
/*TODO*///				(((pVoice.noiseReg.l >> 22) ^ (pVoice.noiseReg.l >> 17)) & 1);
/*TODO*///	#else
/*TODO*///			pVoice.noiseReg = (pVoice.noiseReg << 1) |
/*TODO*///				(((pVoice.noiseReg >> 22) ^ (pVoice.noiseReg >> 17)) & 1);
/*TODO*///	#endif
/*TODO*///	#if defined(DIRECT_FIXPOINT) && defined(LARGE_NOISE_TABLE)
/*TODO*///			pVoice.noiseOutput = (noiseTableLSB[pVoice.noiseReg.w[LO]]
/*TODO*///								   |noiseTableMSB[pVoice.noiseReg.w[HI]&0xff]);
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///			pVoice.noiseOutput = (noiseTableLSB[pVoice.noiseReg.b[LOLO]]
/*TODO*///								   |noiseTableMID[pVoice.noiseReg.b[LOHI]]
/*TODO*///								   |noiseTableMSB[pVoice.noiseReg.b[HILO]]);
/*TODO*///	#else
/*TODO*///			pVoice.noiseOutput = (noiseTableLSB[pVoice.noiseReg&0xff]
/*TODO*///								   |noiseTableMID[pVoice.noiseReg>>8&0xff]
/*TODO*///								   |noiseTableMSB[pVoice.noiseReg>>16&0xff]);
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE void noiseAdvanceHp(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		udword tmp = pVoice.noiseStepAdd;
/*TODO*///		while (tmp >= (1L<<20))
/*TODO*///		{
/*TODO*///			tmp -= (1L<<20);
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			pVoice.noiseReg.l = (pVoice.noiseReg.l << 1) |
/*TODO*///				(((pVoice.noiseReg.l >> 22) ^ (pVoice.noiseReg.l >> 17)) & 1);
/*TODO*///	#else
/*TODO*///			pVoice.noiseReg = (pVoice.noiseReg << 1) |
/*TODO*///				(((pVoice.noiseReg >> 22) ^ (pVoice.noiseReg >> 17)) & 1);
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///		pVoice.noiseStep += tmp;
/*TODO*///		if (pVoice.noiseStep >= (1L<<20))
/*TODO*///		{
/*TODO*///			pVoice.noiseStep -= (1L<<20);
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			pVoice.noiseReg.l = (pVoice.noiseReg.l << 1) |
/*TODO*///				(((pVoice.noiseReg.l >> 22) ^ (pVoice.noiseReg.l >> 17)) & 1);
/*TODO*///	#else
/*TODO*///			pVoice.noiseReg = (pVoice.noiseReg << 1) |
/*TODO*///				(((pVoice.noiseReg >> 22) ^ (pVoice.noiseReg >> 17)) & 1);
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///	#if defined(DIRECT_FIXPOINT) && defined(LARGE_NOISE_TABLE)
/*TODO*///		pVoice.noiseOutput = (noiseTableLSB[pVoice.noiseReg.w[LO]]
/*TODO*///							   |noiseTableMSB[pVoice.noiseReg.w[HI]&0xff]);
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.noiseOutput = (noiseTableLSB[pVoice.noiseReg.b[LOLO]]
/*TODO*///							   |noiseTableMID[pVoice.noiseReg.b[LOHI]]
/*TODO*///							   |noiseTableMSB[pVoice.noiseReg.b[HILO]]);
/*TODO*///	#else
/*TODO*///		pVoice.noiseOutput = (noiseTableLSB[pVoice.noiseReg&0xff]
/*TODO*///							   |noiseTableMID[pVoice.noiseReg>>8&0xff]
/*TODO*///							   |noiseTableMSB[pVoice.noiseReg>>16&0xff]);
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///	  #define triangle triangleTable[pVoice.waveStep.w[HI]]
/*TODO*///	  #define sawtooth sawtoothTable[pVoice.waveStep.w[HI]]
/*TODO*///	  #define square squareTable[pVoice.waveStep.w[HI] + pVoice.pulseIndex]
/*TODO*///	  #define triSaw waveform30[pVoice.waveStep.w[HI]]
/*TODO*///	  #define triSquare waveform50[pVoice.waveStep.w[HI] + pVoice.SIDpulseWidth]
/*TODO*///	  #define sawSquare waveform60[pVoice.waveStep.w[HI] + pVoice.SIDpulseWidth]
/*TODO*///	  #define triSawSquare waveform70[pVoice.waveStep.w[HI] + pVoice.SIDpulseWidth]
/*TODO*///	#else
/*TODO*///	  #define triangle triangleTable[pVoice.waveStep]
/*TODO*///	  #define sawtooth sawtoothTable[pVoice.waveStep]
/*TODO*///	  #define square squareTable[pVoice.waveStep + pVoice.pulseIndex]
/*TODO*///	  #define triSaw waveform30[pVoice.waveStep]
/*TODO*///	  #define triSquare waveform50[pVoice.waveStep + pVoice.SIDpulseWidth]
/*TODO*///	  #define sawSquare waveform60[pVoice.waveStep + pVoice.SIDpulseWidth]
/*TODO*///	  #define triSawSquare waveform70[pVoice.waveStep + pVoice.SIDpulseWidth]
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	
/*TODO*///	static void sidMode00(sidOperator* pVoice)  {
/*TODO*///		pVoice.output = (pVoice.filtIO-0x80);
/*TODO*///		waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	#if 0
/*TODO*///	/* not used */
/*TODO*///	static void sidModeReal00(sidOperator* pVoice)  {
/*TODO*///		pVoice.output = 0;
/*TODO*///		waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	static void sidMode10(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = triangle;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode20(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = sawtooth;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode30(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = triSaw;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode40(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = square;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode50(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = triSquare;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode60(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = sawSquare;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode70(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = triSawSquare;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode80(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = pVoice.noiseOutput;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	  noiseAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode80hp(sidOperator* pVoice)  {
/*TODO*///	  pVoice.output = pVoice.noiseOutput;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	  noiseAdvanceHp(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidModeLock(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		pVoice.noiseIsLocked = true;
/*TODO*///		pVoice.output = (pVoice.filtIO-0x80);
/*TODO*///		waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	static void sidMode14(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///	  if ( pVoice.modulator.waveStep.w[HI] < 2048 )
/*TODO*///	#else
/*TODO*///	  if ( pVoice.modulator.waveStep < 2048 )
/*TODO*///	#endif
/*TODO*///		pVoice.output = triangle;
/*TODO*///	  else
/*TODO*///		pVoice.output = 0xFF ^ triangle;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode34(sidOperator* pVoice)  {
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///	  if ( pVoice.modulator.waveStep.w[HI] < 2048 )
/*TODO*///	#else
/*TODO*///	  if ( pVoice.modulator.waveStep < 2048 )
/*TODO*///	#endif
/*TODO*///		pVoice.output = triSaw;
/*TODO*///	  else
/*TODO*///		pVoice.output = 0xFF ^ triSaw;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode54(sidOperator* pVoice)  {
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///	  if ( pVoice.modulator.waveStep.w[HI] < 2048 )
/*TODO*///	#else
/*TODO*///	  if ( pVoice.modulator.waveStep < 2048 )
/*TODO*///	#endif
/*TODO*///		pVoice.output = triSquare;
/*TODO*///	  else
/*TODO*///	    pVoice.output = 0xFF ^ triSquare;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void sidMode74(sidOperator* pVoice)  {
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///	  if ( pVoice.modulator.waveStep.w[HI] < 2048 )
/*TODO*///	#else
/*TODO*///	  if ( pVoice.modulator.waveStep < 2048 )
/*TODO*///	#endif
/*TODO*///		pVoice.output = triSawSquare;
/*TODO*///	  else
/*TODO*///	    pVoice.output = 0xFF ^ triSawSquare;
/*TODO*///	  waveAdvance(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	INLINE void waveCalcCycleLen(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.cycleAddLen.w[HI] = 0;
/*TODO*///		pVoice.cycleAddLen.l += pVoice.cycleLen.l;
/*TODO*///		pVoice.cycleLenCount = pVoice.cycleAddLen.w[HI];
/*TODO*///	#else
/*TODO*///		pVoice.cycleAddLenPnt += pVoice.cycleLenPnt;
/*TODO*///		pVoice.cycleLenCount = pVoice.cycleLen;
/*TODO*///		if ( pVoice.cycleAddLenPnt > 65535 ) pVoice.cycleLenCount++;
/*TODO*///		pVoice.cycleAddLenPnt &= 0xFFFF;
/*TODO*///	#endif
/*TODO*///		/* If we keep the value cycleLen between 1 <= x <= 65535, */
/*TODO*///		/* the following check is not required. */
/*TODO*///	/*	if ( pVoice.cycleLenCount == 0 ) */
/*TODO*///	/*	{ */
/*TODO*///	/*#if defined(DIRECT_FIXPOINT) */
/*TODO*///	/*		pVoice.waveStep.l = 0; */
/*TODO*///	/*#else */
/*TODO*///	/*		pVoice.waveStep = (pVoice.waveStepPnt = 0); */
/*TODO*///	/*#endif */
/*TODO*///	/*		pVoice.cycleLenCount = 0; */
/*TODO*///	/*	} */
/*TODO*///	/*	else */
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			register uword diff = pVoice.cycleLenCount - pVoice.cycleLen.w[HI];
/*TODO*///	#else
/*TODO*///			register uword diff = pVoice.cycleLenCount - pVoice.cycleLen;
/*TODO*///	#endif
/*TODO*///			if ( pVoice.wavePre[diff].len != pVoice.cycleLenCount )
/*TODO*///			{
/*TODO*///				pVoice.wavePre[diff].len = pVoice.cycleLenCount;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///				pVoice.wavePre[diff].stp = (pVoice.waveStepAdd.l = (4096UL*65536UL) / pVoice.cycleLenCount);
/*TODO*///	#else
/*TODO*///				pVoice.wavePre[diff].stp = (pVoice.waveStepAdd = 4096UL / pVoice.cycleLenCount);
/*TODO*///				pVoice.wavePre[diff].pnt = (pVoice.waveStepAddPnt = ((4096UL % pVoice.cycleLenCount) * 65536UL) / pVoice.cycleLenCount);
/*TODO*///	#endif
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///				pVoice.waveStepAdd.l = pVoice.wavePre[diff].stp;
/*TODO*///	#else
/*TODO*///				pVoice.waveStepAdd = pVoice.wavePre[diff].stp;
/*TODO*///				pVoice.waveStepAddPnt = pVoice.wavePre[diff].pnt;
/*TODO*///	#endif
/*TODO*///			}
/*TODO*///		}  /* see above (opening bracket) */
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE void waveCalcFilter(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		if ( pVoice.filtEnabled )
/*TODO*///		{
/*TODO*///			if ( pVoice.sid.filter.Type != 0 )
/*TODO*///			{
/*TODO*///				if ( pVoice.sid.filter.Type == 0x20 )
/*TODO*///				{
/*TODO*///					filterfloat tmp;
/*TODO*///					pVoice.filtLow += ( pVoice.filtRef * pVoice.sid.filter.Dy );
/*TODO*///					tmp = (filterfloat)pVoice.filtIO - pVoice.filtLow;
/*TODO*///					tmp -= pVoice.filtRef * pVoice.sid.filter.ResDy;
/*TODO*///					pVoice.filtRef += ( tmp * (pVoice.sid.filter.Dy) );
/*TODO*///					pVoice.filtIO = (sbyte)(pVoice.filtRef-pVoice.filtLow/4);
/*TODO*///				}
/*TODO*///				else if (pVoice.sid.filter.Type == 0x40)
/*TODO*///				{
/*TODO*///					filterfloat tmp, tmp2;
/*TODO*///					pVoice.filtLow += ( pVoice.filtRef * pVoice.sid.filter.Dy * 0.1 );
/*TODO*///					tmp = (filterfloat)pVoice.filtIO - pVoice.filtLow;
/*TODO*///					tmp -= pVoice.filtRef * pVoice.sid.filter.ResDy;
/*TODO*///					pVoice.filtRef += ( tmp * (pVoice.sid.filter.Dy) );
/*TODO*///					tmp2 = pVoice.filtRef - pVoice.filtIO/8;
/*TODO*///					if (tmp2 < -128)
/*TODO*///						tmp2 = -128;
/*TODO*///					if (tmp2 > 127)
/*TODO*///						tmp2 = 127;
/*TODO*///					pVoice.filtIO = (sbyte)tmp2;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					filterfloat sample, sample2;
/*TODO*///					int tmp;
/*TODO*///					pVoice.filtLow += ( pVoice.filtRef * pVoice.sid.filter.Dy );
/*TODO*///					sample = pVoice.filtIO;
/*TODO*///					sample2 = sample - pVoice.filtLow;
/*TODO*///					tmp = (int)sample2;
/*TODO*///					sample2 -= pVoice.filtRef * pVoice.sid.filter.ResDy;
/*TODO*///					pVoice.filtRef += ( sample2 * pVoice.sid.filter.Dy );
/*TODO*///	
/*TODO*///					if ( pVoice.sid.filter.Type == 0x10 )
/*TODO*///					{
/*TODO*///						pVoice.filtIO = (sbyte)pVoice.filtLow;
/*TODO*///					}
/*TODO*///					else if ( pVoice.sid.filter.Type == 0x30 )
/*TODO*///					{
/*TODO*///						pVoice.filtIO = (sbyte)pVoice.filtLow;
/*TODO*///					}
/*TODO*///					else if ( pVoice.sid.filter.Type == 0x50 )
/*TODO*///					{
/*TODO*///						pVoice.filtIO = (sbyte)(sample - (tmp >> 1));
/*TODO*///					}
/*TODO*///					else if ( pVoice.sid.filter.Type == 0x60 )
/*TODO*///					{
/*TODO*///						pVoice.filtIO = (sbyte)tmp;
/*TODO*///					}
/*TODO*///					else if ( pVoice.sid.filter.Type == 0x70 )
/*TODO*///					{
/*TODO*///						pVoice.filtIO = (sbyte)(sample - (tmp >> 1));
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else /* pVoice.sid.filter.Type == 0x00 */
/*TODO*///			{
/*TODO*///				pVoice.filtIO = 0;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	sbyte waveCalcMute(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		(*pVoice.ADSRproc)(pVoice);  /* just process envelope */
/*TODO*///		return pVoice.filtIO;//&pVoice.outputMask;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	sbyte sidWaveCalcNormal(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		if ( pVoice.cycleLenCount <= 0 )
/*TODO*///		{
/*TODO*///			waveCalcCycleLen(pVoice);
/*TODO*///			if (( pVoice.SIDctrl & 0x40 ) == 0x40 )
/*TODO*///			{
/*TODO*///				pVoice.pulseIndex = pVoice.newPulseIndex;
/*TODO*///				if ( pVoice.pulseIndex > 2048 )
/*TODO*///				{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///					pVoice.waveStep.w[HI] = 0;
/*TODO*///	#else
/*TODO*///					pVoice.waveStep = 0;
/*TODO*///	#endif
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		(*pVoice.waveProc)(pVoice);
/*TODO*///		pVoice.filtIO = ampMod1x8[(*pVoice.ADSRproc)(pVoice)|pVoice.output];
/*TODO*///	//	pVoice.filtIO = pVoice.sid.masterVolume; // test for digi sound
/*TODO*///		waveCalcFilter(pVoice);
/*TODO*///		return pVoice.filtIO;//&pVoice.outputMask;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	sbyte waveCalcRangeCheck(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.waveStepOld = pVoice.waveStep.w[HI];
/*TODO*///		(*pVoice.waveProc)(pVoice);
/*TODO*///		if (pVoice.waveStep.w[HI] < pVoice.waveStepOld)
/*TODO*///	#else
/*TODO*///		pVoice.waveStepOld = pVoice.waveStep;
/*TODO*///		(*pVoice.waveProc)(pVoice);
/*TODO*///		if (pVoice.waveStep < pVoice.waveStepOld)
/*TODO*///	#endif
/*TODO*///		{
/*TODO*///			/* Next step switch back to normal calculation. */
/*TODO*///			pVoice.cycleLenCount = 0;
/*TODO*///			pVoice.outProc = &sidWaveCalcNormal;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///					pVoice.waveStep.w[HI] = 4095;
/*TODO*///	#else
/*TODO*///					pVoice.waveStep = 4095;
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///		pVoice.filtIO = ampMod1x8[(*pVoice.ADSRproc)(pVoice)|pVoice.output];
/*TODO*///		waveCalcFilter(pVoice);
/*TODO*///		return pVoice.filtIO;//&pVoice.outputMask;
/*TODO*///	}
	
	/* -------------------------------------------------- Operator frame set-up 1 */
	
	public static void sidEmuSet(sidOperator pVoice)
	{
            System.out.println("sidEmuSet NOT IMPLEMENTED!!!!");
/*TODO*///	    ubyte enveTemp, newWave, oldWave;
/*TODO*///	    ubyte ADtemp;
/*TODO*///	    ubyte SRtemp;
/*TODO*///	    ubyte tmpSusVol;
/*TODO*///	    
/*TODO*///	    pVoice.SIDfreq = pVoice.reg[0]|(pVoice.reg[1]<<8);
/*TODO*///	    
/*TODO*///	    pVoice.SIDpulseWidth = (pVoice.reg[2]|(pVoice.reg[3]<<8)) & 0x0FFF;
/*TODO*///	    pVoice.newPulseIndex = 4096 - pVoice.SIDpulseWidth;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///	    if ( ((pVoice.waveStep.w[HI] + pVoice.pulseIndex) >= 0x1000)
/*TODO*///		 && ((pVoice.waveStep.w[HI] + pVoice.newPulseIndex) >= 0x1000) )
/*TODO*///	    {
/*TODO*///		pVoice.pulseIndex = pVoice.newPulseIndex;
/*TODO*///	    }
/*TODO*///	    else if ( ((pVoice.waveStep.w[HI] + pVoice.pulseIndex) < 0x1000)
/*TODO*///		      && ((pVoice.waveStep.w[HI] + pVoice.newPulseIndex) < 0x1000) )
/*TODO*///	    {
/*TODO*///		pVoice.pulseIndex = pVoice.newPulseIndex;
/*TODO*///	    }
/*TODO*///	#else
/*TODO*///	    if ( ((pVoice.waveStep + pVoice.pulseIndex) >= 0x1000)
/*TODO*///		 && ((pVoice.waveStep + pVoice.newPulseIndex) >= 0x1000) )
/*TODO*///	    {
/*TODO*///		pVoice.pulseIndex = pVoice.newPulseIndex;
/*TODO*///	    }
/*TODO*///	    else if ( ((pVoice.waveStep + pVoice.pulseIndex) < 0x1000)
/*TODO*///		      && ((pVoice.waveStep + pVoice.newPulseIndex) < 0x1000) )
/*TODO*///	    {
/*TODO*///		pVoice.pulseIndex = pVoice.newPulseIndex;
/*TODO*///	    }
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	
/*TODO*///	    oldWave = pVoice.SIDctrl;
/*TODO*///	    enveTemp = pVoice.ADSRctrl;
/*TODO*///	    pVoice.SIDctrl = (newWave = pVoice.reg[4]|(pVoice.reg[5]<<8));
/*TODO*///	    
/*TODO*///	    if (( newWave & 1 ) ==0 )
/*TODO*///	    {
/*TODO*///		if (( oldWave & 1 ) !=0 )
/*TODO*///		    enveTemp = ENVE_STARTRELEASE;
/*TODO*///	/*		else if ( pVoice.gateOnCtrl ) */
/*TODO*///	/*		{ */
/*TODO*///	/*			enveTemp = ENVE_STARTSHORTATTACK; */
/*TODO*///	/*		} */
/*TODO*///	    }
/*TODO*///	    else if ( /*pVoice.gateOffCtrl || */((oldWave&1)==0) )
/*TODO*///	    {
/*TODO*///		enveTemp = ENVE_STARTATTACK;
/*TODO*///	    }
/*TODO*///	    
/*TODO*///	    if ((( oldWave ^ newWave ) & 0xF0 ) != 0 )
/*TODO*///	    {
/*TODO*///		pVoice.cycleLenCount = 0;
/*TODO*///	    }
/*TODO*///	    
/*TODO*///	    ADtemp = pVoice.reg[5];
/*TODO*///	    SRtemp = pVoice.reg[6];
/*TODO*///	    if ( pVoice.SIDAD != ADtemp )
/*TODO*///	    {
/*TODO*///		enveTemp |= ENVE_ALTER;
/*TODO*///	    }
/*TODO*///	    else if ( pVoice.SIDSR != SRtemp )
/*TODO*///	    {
/*TODO*///		enveTemp |= ENVE_ALTER;
/*TODO*///	    }
/*TODO*///	    pVoice.SIDAD = ADtemp;
/*TODO*///	    pVoice.SIDSR = SRtemp;
/*TODO*///	    tmpSusVol = masterVolumeLevels[SRtemp >> 4];
/*TODO*///	    if (pVoice.ADSRctrl != ENVE_SUSTAIN)  /* !!! */
/*TODO*///	    {
/*TODO*///		pVoice.enveSusVol = tmpSusVol;
/*TODO*///	    }
/*TODO*///	    else
/*TODO*///	    {
/*TODO*///		if ( pVoice.enveSusVol > pVoice.enveVol )
/*TODO*///		    pVoice.enveSusVol = 0;
/*TODO*///		else
/*TODO*///		    pVoice.enveSusVol = tmpSusVol;
/*TODO*///	    }
/*TODO*///	    
/*TODO*///	    pVoice.ADSRproc = enveModeTable[enveTemp>>1];  /* shifting out the KEY-bit */
/*TODO*///	    pVoice.ADSRctrl = enveTemp & (255-ENVE_ALTER-1);
/*TODO*///	    
/*TODO*///	    pVoice.filtEnabled = pVoice.sid.filter.Enabled &&
/*TODO*///	        ((pVoice.sid.reg[0x17]&pVoice.filtVoiceMask)!=0);
	}
	
/*TODO*///	/* -------------------------------------------------- Operator frame set-up 2 */
/*TODO*///	
/*TODO*///	/* MOS-8580, MOS-6581 (no 70) */
/*TODO*///	static ptr2sidVoidFunc sidModeNormalTable[16] =
/*TODO*///	{
/*TODO*///	  sidMode00, sidMode10, sidMode20, sidMode30, sidMode40, sidMode50, sidMode60, sidMode70,
/*TODO*///	  sidMode80, sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock
/*TODO*///	};
/*TODO*///	
/*TODO*///	/* MOS-8580, MOS-6581 (no 74) */
/*TODO*///	static ptr2sidVoidFunc sidModeRingTable[16] =
/*TODO*///	{
/*TODO*///	  sidMode00, sidMode14, sidMode00, sidMode34, sidMode00, sidMode54, sidMode00, sidMode74,
/*TODO*///	  sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock, sidModeLock
/*TODO*///	};
/*TODO*///	
	public static void sidClearOperator( sidOperator pVoice )
	{
            System.out.println("sidClearOperator NOT IMPLEMENTED!!!!");
/*TODO*///		pVoice.SIDfreq = 0;
/*TODO*///		pVoice.SIDctrl = 0;
/*TODO*///		pVoice.SIDAD = 0;
/*TODO*///		pVoice.SIDSR = 0;
/*TODO*///	
/*TODO*///		pVoice.sync = false;
/*TODO*///	
/*TODO*///		pVoice.pulseIndex = (pVoice.newPulseIndex = (pVoice.SIDpulseWidth = 0));
/*TODO*///		pVoice.curSIDfreq = (pVoice.curNoiseFreq = 0);
/*TODO*///	
/*TODO*///		pVoice.output = (pVoice.noiseOutput = 0);
/*TODO*///		pVoice.filtIO = 0;
/*TODO*///	
/*TODO*///		pVoice.filtEnabled = false;
/*TODO*///		pVoice.filtLow = (pVoice.filtRef = 0);
/*TODO*///	
/*TODO*///		pVoice.cycleLenCount = 0;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.cycleLen.l = (pVoice.cycleAddLen.l = 0);
/*TODO*///	#else
/*TODO*///		pVoice.cycleLen = (pVoice.cycleLenPnt = 0);
/*TODO*///		pVoice.cycleAddLenPnt = 0;
/*TODO*///	#endif
/*TODO*///	
/*TODO*///		pVoice.outProc = waveCalcMute;
/*TODO*///	
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.waveStepAdd.l = (pVoice.waveStep.l = 0);
/*TODO*///		pVoice.wavePre[0].len = (pVoice.wavePre[0].stp = 0);
/*TODO*///		pVoice.wavePre[1].len = (pVoice.wavePre[1].stp = 0);
/*TODO*///	#else
/*TODO*///		pVoice.waveStepAdd = (pVoice.waveStepAddPnt = 0);
/*TODO*///		pVoice.waveStep = (pVoice.waveStepPnt = 0);
/*TODO*///		pVoice.wavePre[0].len = 0;
/*TODO*///		pVoice.wavePre[0].stp = (pVoice.wavePre[0].pnt = 0);
/*TODO*///		pVoice.wavePre[1].len = 0;
/*TODO*///		pVoice.wavePre[1].stp = (pVoice.wavePre[1].pnt = 0);
/*TODO*///	#endif
/*TODO*///		pVoice.waveStepOld = 0;
/*TODO*///	
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.noiseReg.l = noiseSeed;
/*TODO*///	#else
/*TODO*///		pVoice.noiseReg = noiseSeed;
/*TODO*///	#endif
/*TODO*///		pVoice.noiseStepAdd = (pVoice.noiseStep = 0);
/*TODO*///		pVoice.noiseIsLocked = false;
	}

	public static void sidEmuSet2(sidOperator pVoice)
	{
            System.out.println("sidEmuSet2 NOT IMPLEMENTED!!!!");
/*TODO*///	    pVoice.outProc = &sidWaveCalcNormal;
/*TODO*///	    pVoice.sync = false;
/*TODO*///	    
/*TODO*///	    if ( (pVoice.SIDfreq < 16) || ((pVoice.SIDctrl & 8) != 0) )
/*TODO*///	//    if ( /*(pVoice.SIDfreq < 16) || */((pVoice.SIDctrl & 8) != 0) )
/*TODO*///	    {
/*TODO*///		pVoice.outProc = waveCalcMute;
/*TODO*///		if (pVoice.SIDfreq == 0)
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		    pVoice.cycleLen.l = (pVoice.cycleAddLen.l = 0);
/*TODO*///		    pVoice.waveStep.l = 0;
/*TODO*///	#else
/*TODO*///		    pVoice.cycleLen = (pVoice.cycleLenPnt = 0);
/*TODO*///		    pVoice.cycleAddLenPnt = 0;
/*TODO*///		    pVoice.waveStep = 0;
/*TODO*///		    pVoice.waveStepPnt = 0;
/*TODO*///	#endif
/*TODO*///		    pVoice.curSIDfreq = (pVoice.curNoiseFreq = 0);
/*TODO*///		    pVoice.noiseStepAdd = 0;
/*TODO*///		    pVoice.cycleLenCount = 0;
/*TODO*///		}
/*TODO*///		if ((pVoice.SIDctrl & 8) != 0)
/*TODO*///		{
/*TODO*///		    if (pVoice.noiseIsLocked)
/*TODO*///		    {
/*TODO*///			pVoice.noiseIsLocked = false;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			pVoice.noiseReg.l = noiseSeed;
/*TODO*///	#else
/*TODO*///			pVoice.noiseReg = noiseSeed;
/*TODO*///	#endif
/*TODO*///		    }
/*TODO*///		}
/*TODO*///	    }
/*TODO*///	    else
/*TODO*///	    {
/*TODO*///		if ( pVoice.curSIDfreq != pVoice.SIDfreq )
/*TODO*///		{
/*TODO*///		    pVoice.curSIDfreq = pVoice.SIDfreq;
/*TODO*///		    /* We keep the value cycleLen between 1 <= x <= 65535. */
/*TODO*///		    /* This makes a range-check in waveCalcCycleLen() unrequired. */
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		    pVoice.cycleLen.l = ((pVoice.sid.PCMsid << 12) / pVoice.SIDfreq) << 4;
/*TODO*///		    if (pVoice.cycleLenCount > 0)
/*TODO*///		    {
/*TODO*///			waveCalcCycleLen(pVoice);
/*TODO*///			pVoice.outProc = &waveCalcRangeCheck;
/*TODO*///		    }
/*TODO*///	#else
/*TODO*///		    pVoice.cycleLen = pVoice.sid.PCMsid / pVoice.SIDfreq;
/*TODO*///		    pVoice.cycleLenPnt = (( pVoice.sid.PCMsid % pVoice.SIDfreq ) * 65536UL ) / pVoice.SIDfreq;
/*TODO*///		    if (pVoice.cycleLenCount > 0)
/*TODO*///		    {
/*TODO*///			waveCalcCycleLen(pVoice);
/*TODO*///			pVoice.outProc = &waveCalcRangeCheck;
/*TODO*///		    }
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///		
/*TODO*///		if ((( pVoice.SIDctrl & 0x80 ) == 0x80 ) && ( pVoice.curNoiseFreq != pVoice.SIDfreq ))
/*TODO*///		{
/*TODO*///		    pVoice.curNoiseFreq = pVoice.SIDfreq;
/*TODO*///		    pVoice.noiseStepAdd = (pVoice.sid.PCMsidNoise * pVoice.SIDfreq) >> 8;
/*TODO*///		    if (pVoice.noiseStepAdd >= (1L<<21))
/*TODO*///			sidModeNormalTable[8] = sidMode80hp;
/*TODO*///		    else
/*TODO*///			sidModeNormalTable[8] = sidMode80;
/*TODO*///		}
/*TODO*///		
/*TODO*///		if (( pVoice.SIDctrl & 2 ) != 0 )
/*TODO*///		{
/*TODO*///		    if ( ( pVoice.modulator.SIDfreq == 0 ) || (( pVoice.modulator.SIDctrl & 8 ) != 0 ) )
/*TODO*///		    {
/*TODO*///			;
/*TODO*///		    }
/*TODO*///		    else if ( (( pVoice.carrier.SIDctrl & 2 ) != 0 ) &&
/*TODO*///			      ( pVoice.modulator.SIDfreq >= ( pVoice.SIDfreq << 1 )) )
/*TODO*///		    {
/*TODO*///			;
/*TODO*///		    }
/*TODO*///		    else
/*TODO*///		    {
/*TODO*///			pVoice.sync = true;
/*TODO*///		    }
/*TODO*///		}
/*TODO*///		
/*TODO*///		if ((( pVoice.SIDctrl & 0x14 ) == 0x14 ) && ( pVoice.modulator.SIDfreq != 0 ))
/*TODO*///		    pVoice.waveProc = sidModeRingTable[pVoice.SIDctrl >> 4];
/*TODO*///		else
/*TODO*///		    pVoice.waveProc = sidModeNormalTable[pVoice.SIDctrl >> 4];
/*TODO*///	    }
	}
	
	public static void sidInitWaveformTables(int type)
	{
            System.out.println("sidInitWaveformTables NOT IMPLEMENTED!!!!");
/*TODO*///		int i,j;
/*TODO*///		uword k;
/*TODO*///	
/*TODO*///		k = 0;
/*TODO*///		for ( i = 0; i < 256; i++ )
/*TODO*///			for ( j = 0; j < 8; j++ )
/*TODO*///				triangleTable[k++] = i;
/*TODO*///		for ( i = 255; i >= 0; i-- )
/*TODO*///			for ( j = 0; j < 8; j++ )
/*TODO*///				triangleTable[k++] = i;
/*TODO*///	
/*TODO*///		k = 0;
/*TODO*///		for ( i = 0; i < 256; i++ )
/*TODO*///			for ( j = 0; j < 16; j++ )
/*TODO*///				sawtoothTable[k++] = i;
/*TODO*///	
/*TODO*///		k = 0;
/*TODO*///		for ( i = 0; i < 4096; i++ )
/*TODO*///		    squareTable[k++] = 255; //0; my estimation; especial for digi sound
/*TODO*///		for ( i = 0; i < 4096; i++ )
/*TODO*///		    squareTable[k++] = 0; //255;
/*TODO*///	
/*TODO*///		if ( type==MOS8580 )
/*TODO*///		{
/*TODO*///	        waveform30 = waveform30_8580;
/*TODO*///	        waveform50 = waveform50_8580;
/*TODO*///	        waveform60 = waveform60_8580;
/*TODO*///	        waveform70 = waveform70_8580;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///	        waveform30 = waveform30_6581;
/*TODO*///	        waveform50 = waveform50_6581;
/*TODO*///	        waveform60 = waveform60_6581;
/*TODO*///	        waveform70 = waveform70_6581;  /* really audible? */
/*TODO*///		}
/*TODO*///	
/*TODO*///		for ( i = 4096; i < 8192; i++ )
/*TODO*///		{
/*TODO*///			waveform50[i] = 0;
/*TODO*///			waveform60[i] = 0;
/*TODO*///			waveform70[i] = 0;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if ( type==MOS8580 )
/*TODO*///		{
/*TODO*///			sidModeNormalTable[3] = sidMode30;
/*TODO*///			sidModeNormalTable[6] = sidMode60;
/*TODO*///			sidModeNormalTable[7] = sidMode70;
/*TODO*///			sidModeRingTable[7] = sidMode74;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			sidModeNormalTable[3] = sidMode30;
/*TODO*///			sidModeNormalTable[6] = sidMode60;
/*TODO*///			sidModeNormalTable[7] = sidMode00;  /* really audible? */
/*TODO*///			sidModeRingTable[7] = sidMode00;    /* */
/*TODO*///		}
/*TODO*///	
/*TODO*///		{
/*TODO*///	#if defined(LARGE_NOISE_TABLE)
/*TODO*///		udword ni;
/*TODO*///		for (ni = 0; ni < sizeof(noiseTableLSB); ni++)
/*TODO*///		{
/*TODO*///			noiseTableLSB[ni] = (ubyte)
/*TODO*///				(((ni >> (13-4)) & 0x10) |
/*TODO*///				 ((ni >> (11-3)) & 0x08) |
/*TODO*///				 ((ni >> (7-2)) & 0x04) |
/*TODO*///				 ((ni >> (4-1)) & 0x02) |
/*TODO*///				 ((ni >> (2-0)) & 0x01));
/*TODO*///		}
/*TODO*///		for (ni = 0; ni < sizeof(noiseTableMSB); ni++)
/*TODO*///		{
/*TODO*///			noiseTableMSB[ni] = (ubyte)
/*TODO*///				(((ni << (7-(22-16))) & 0x80) |
/*TODO*///				 ((ni << (6-(20-16))) & 0x40) |
/*TODO*///				 ((ni << (5-(16-16))) & 0x20));
/*TODO*///		}
/*TODO*///	#else
/*TODO*///		udword ni;
/*TODO*///		for (ni = 0; ni < sizeof(noiseTableLSB); ni++)
/*TODO*///		{
/*TODO*///			noiseTableLSB[ni] = (ubyte)
/*TODO*///				(((ni >> (7-2)) & 0x04) |
/*TODO*///				 ((ni >> (4-1)) & 0x02) |
/*TODO*///				 ((ni >> (2-0)) & 0x01));
/*TODO*///		}
/*TODO*///		for (ni = 0; ni < sizeof(noiseTableMID); ni++)
/*TODO*///		{
/*TODO*///			noiseTableMID[ni] = (ubyte)
/*TODO*///				(((ni >> (13-8-4)) & 0x10) |
/*TODO*///				 ((ni << (3-(11-8))) & 0x08));
/*TODO*///		}
/*TODO*///		for (ni = 0; ni < sizeof(noiseTableMSB); ni++)
/*TODO*///		{
/*TODO*///			noiseTableMSB[ni] = (ubyte)
/*TODO*///				(((ni << (7-(22-16))) & 0x80) |
/*TODO*///				 ((ni << (6-(20-16))) & 0x40) |
/*TODO*///				 ((ni << (5-(16-16))) & 0x20));
/*TODO*///		}
/*TODO*///	#endif
/*TODO*///		}
	}
    
}
