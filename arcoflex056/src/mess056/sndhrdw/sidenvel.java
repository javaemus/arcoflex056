/*========================================================================= */
/* This source implements the ADSR volume envelope of the SID-chip. */
/* Two different envelope shapes are implemented, an exponential */
/* approximation and the linear shape, which can easily be determined */
/* by reading the registers of the third SID operator. */
/* */
/* Accurate volume envelope times as of November 1994 are used */
/* courtesy of George W. Taylor <aa601@cfn.cs.dal.ca>, <yurik@io.org> */
/* They are slightly modified. */
/* */
/* To use the rounded envelope times from the C64 Programmers Reference */
/* Book define SID_REFTIMES at the Makefile level. */
/* */
/* To perform realtime calculations with floating point precision define */
/* SID_FPUENVE at the Makefile level. On high-end FPUs (not Pentium !), */
/* this can result in speed improvement. Default is integer fixpoint. */
/* */
/* Global Makefile definables: */
/* */
/*   DIRECT_FIXPOINT - use a union to access integer fixpoint operands */
/*                     in memory. This makes an assumption about the */
/*                     hardware and software architecture and therefore */
/*                     is considered a hack ! */
/* */
/* Local (or Makefile) definables: */
/* */
/*   SID_REFTIMES - use rounded envelope times */
/*   SID_FPUENVE  - use floating point precision for calculations */
/*                  (will override the global DIRECT_FIXPOINT setting !) */
/* */
/*========================================================================= */

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
public class sidenvel {
/*TODO*///	
/*TODO*///	#define VERBOSE_DBG 0
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	const ubyte masterVolumeLevels[16] =
/*TODO*///	{
/*TODO*///	    0,  17,  34,  51,  68,  85, 102, 119,
/*TODO*///	  136, 153, 170, 187, 204, 221, 238, 255
/*TODO*///	};
/*TODO*///	
/*TODO*///	static uword masterAmplModTable[16*256];
/*TODO*///	
/*TODO*///	static float attackTimes[16] =
/*TODO*///	{
/*TODO*///	  /* milliseconds */
/*TODO*///	#if defined(SID_REFTIMES)
/*TODO*///	  2,8,16,24,38,56,68,80,
/*TODO*///	  100,250,500,800,1000,3000,5000,8000
/*TODO*///	#else
/*TODO*///	  2.2528606, 8.0099577, 15.7696042, 23.7795619, 37.2963655, 55.0684591,
/*TODO*///	  66.8330845, 78.3473987,
/*TODO*///	  98.1219818, 244.554021, 489.108042, 782.472742, 977.715461, 2933.64701,
/*TODO*///	  4889.07793, 7822.72493
/*TODO*///	#endif
/*TODO*///	};
/*TODO*///	
/*TODO*///	static float decayReleaseTimes[16] =
/*TODO*///	{
/*TODO*///	  /* milliseconds */
/*TODO*///	#if defined(SID_REFTIMES)
/*TODO*///	  8,24,48,72,114,168,204,240,
/*TODO*///	  300,750,1500,2400,3000,9000,15000,24000
/*TODO*///	#else
/*TODO*///	  8.91777693, 24.594051, 48.4185907, 73.0116639, 114.512475, 169.078356,
/*TODO*///	  205.199432, 240.551975,
/*TODO*///	  301.266125, 750.858245, 1501.71551, 2402.43682, 3001.89298, 9007.21405,
/*TODO*///	  15010.998, 24018.2111
/*TODO*///	#endif
/*TODO*///	};
/*TODO*///	
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///	  static float attackRates[16];
/*TODO*///	  static float decayReleaseRates[16];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///	  static udword attackRates[16];
/*TODO*///	  static udword decayReleaseRates[16];
/*TODO*///	#else
/*TODO*///	  static udword attackRates[16];
/*TODO*///	  static udword attackRatesP[16];
/*TODO*///	  static udword decayReleaseRates[16];
/*TODO*///	  static udword decayReleaseRatesP[16];
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	const udword attackTabLen = 255;
/*TODO*///	static udword releaseTabLen;
/*TODO*///	static udword releasePos[256];
/*TODO*///	
/*TODO*///	
/*TODO*///	void enveEmuInit( udword updateFreq, bool measuredValues )
/*TODO*///	{
/*TODO*///		udword i, j, k;
/*TODO*///	
/*TODO*///		releaseTabLen = sizeof(releaseTab);
/*TODO*///		for ( i = 0; i < 256; i++ )
/*TODO*///		{
/*TODO*///			j = 0;
/*TODO*///			while (( j < releaseTabLen ) && (releaseTab[j] > i) )
/*TODO*///			{
/*TODO*///				j++;
/*TODO*///			}
/*TODO*///			if ( j < releaseTabLen )
/*TODO*///			{
/*TODO*///				releasePos[i] = j;
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				releasePos[i] = releaseTabLen -1;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		k = 0;
/*TODO*///		for ( i = 0; i < 16; i++ )
/*TODO*///		{
/*TODO*///			for ( j = 0; j < 256; j++ )
/*TODO*///			{
/*TODO*///				uword tmpVol = j;
/*TODO*///				if (measuredValues)
/*TODO*///				{
/*TODO*///					tmpVol = (uword) ((293.0*(1-exp(j/-130.0)))+4.0);
/*TODO*///					if (j == 0)
/*TODO*///						tmpVol = 0;
/*TODO*///					if (tmpVol > 255)
/*TODO*///						tmpVol = 255;
/*TODO*///				}
/*TODO*///				/* Want the modulated volume value in the high byte. */
/*TODO*///				masterAmplModTable[k++] = ((tmpVol * masterVolumeLevels[i]) / 255) << 8;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		for ( i = 0; i < 16; i++ )
/*TODO*///		{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///			double scaledenvelen = floor(( attackTimes[i] * updateFreq ) / 1000UL );
/*TODO*///			if (scaledenvelen == 0)
/*TODO*///				scaledenvelen = 1;
/*TODO*///			attackRates[i] = attackTabLen / scaledenvelen;
/*TODO*///	
/*TODO*///			scaledenvelen = floor(( decayReleaseTimes[i] * updateFreq ) / 1000UL );
/*TODO*///			if (scaledenvelen == 0)
/*TODO*///				scaledenvelen = 1;
/*TODO*///			decayReleaseRates[i] = releaseTabLen / scaledenvelen;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///			udword scaledenvelen = (udword)floor(( attackTimes[i] * updateFreq ) / 1000UL );
/*TODO*///			if (scaledenvelen == 0)
/*TODO*///				scaledenvelen = 1;
/*TODO*///			attackRates[i] = (attackTabLen << 16) / scaledenvelen;
/*TODO*///	
/*TODO*///			scaledenvelen = (udword)floor(( decayReleaseTimes[i] * updateFreq ) / 1000UL );
/*TODO*///			if (scaledenvelen == 0)
/*TODO*///				scaledenvelen = 1;
/*TODO*///			decayReleaseRates[i] = (releaseTabLen << 16) / scaledenvelen;
/*TODO*///	#else
/*TODO*///			udword scaledenvelen = (udword)(/*floor*/(( attackTimes[i] * updateFreq ) / 1000UL ));
/*TODO*///	
/*TODO*///			if (scaledenvelen == 0)
/*TODO*///				scaledenvelen = 1;
/*TODO*///			attackRates[i] = attackTabLen / scaledenvelen;
/*TODO*///			attackRatesP[i] = (( attackTabLen % scaledenvelen ) * 65536UL ) / scaledenvelen;
/*TODO*///	
/*TODO*///			scaledenvelen = (udword)(/*floor*/(( decayReleaseTimes[i] * updateFreq ) / 1000UL ));
/*TODO*///			if (scaledenvelen == 0)
/*TODO*///				scaledenvelen = 1;
/*TODO*///			decayReleaseRates[i] = releaseTabLen / scaledenvelen;
/*TODO*///			decayReleaseRatesP[i] = (( releaseTabLen % scaledenvelen ) * 65536UL ) / scaledenvelen;
/*TODO*///	#endif
/*TODO*///	  }
/*TODO*///	}
	
	/* Reset op. */
	
	public static void enveEmuResetOperator(sidOperator pVoice)
	{
            System.out.println("enveEmuResetOperator NOT IMPLEMENTED!!!!");
/*TODO*///		/* mute, end of R-phase */
/*TODO*///		pVoice.ADSRctrl = ENVE_MUTE;
/*TODO*///	//	pVoice.gateOnCtrl = (pVoice.gateOffCtrl = false);
/*TODO*///	
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStep = (pVoice.fenveStepAdd = 0);
/*TODO*///		pVoice.enveStep = 0;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStep.l = (pVoice.enveStepAdd.l = 0);
/*TODO*///	#else
/*TODO*///		pVoice.enveStep = (pVoice.enveStepPnt = 0);
/*TODO*///		pVoice.enveStepAdd = (pVoice.enveStepAddPnt = 0);
/*TODO*///	#endif
/*TODO*///		pVoice.enveSusVol = 0;
/*TODO*///		pVoice.enveVol = 0;
/*TODO*///		pVoice.enveShortAttackCount = 0;
	}

/*TODO*///	INLINE uword enveEmuStartAttack(sidOperator*);
/*TODO*///	INLINE uword enveEmuStartDecay(sidOperator*);
/*TODO*///	INLINE uword enveEmuStartRelease(sidOperator*);
/*TODO*///	INLINE uword enveEmuAlterAttack(sidOperator*);
/*TODO*///	INLINE uword enveEmuAlterDecay(sidOperator*);
/*TODO*///	INLINE uword enveEmuAlterSustain(sidOperator*);
/*TODO*///	INLINE uword enveEmuAlterSustainDecay(sidOperator*);
/*TODO*///	INLINE uword enveEmuAlterRelease(sidOperator*);
/*TODO*///	INLINE uword enveEmuAttack(sidOperator*);
/*TODO*///	INLINE uword enveEmuDecay(sidOperator*);
/*TODO*///	INLINE uword enveEmuSustain(sidOperator*);
/*TODO*///	INLINE uword enveEmuSustainDecay(sidOperator*);
/*TODO*///	INLINE uword enveEmuRelease(sidOperator*);
/*TODO*///	INLINE uword enveEmuMute(sidOperator*);
/*TODO*///	
/*TODO*///	INLINE uword enveEmuStartShortAttack(sidOperator*);
/*TODO*///	INLINE uword enveEmuAlterShortAttack(sidOperator*);
/*TODO*///	INLINE uword enveEmuShortAttack(sidOperator*);
/*TODO*///	
/*TODO*///	
/*TODO*///	ptr2sidUwordFunc enveModeTable[] =
/*TODO*///	{
/*TODO*///		/* 0 */
/*TODO*///		&enveEmuStartAttack, &enveEmuStartRelease,
/*TODO*///		&enveEmuAttack, &enveEmuDecay, &enveEmuSustain, &enveEmuRelease,
/*TODO*///		&enveEmuSustainDecay, &enveEmuMute,
/*TODO*///		/* 16 */
/*TODO*///		&enveEmuStartShortAttack,
/*TODO*///		&enveEmuMute, &enveEmuMute, &enveEmuMute,
/*TODO*///		&enveEmuMute, &enveEmuMute, &enveEmuMute, &enveEmuMute,
/*TODO*///	    /* 32		 */
/*TODO*///		&enveEmuStartAttack, &enveEmuStartRelease,
/*TODO*///		&enveEmuAlterAttack, &enveEmuAlterDecay, &enveEmuAlterSustain, &enveEmuAlterRelease,
/*TODO*///		&enveEmuAlterSustainDecay, &enveEmuMute,
/*TODO*///	    /* 48		 */
/*TODO*///		&enveEmuStartShortAttack,
/*TODO*///		&enveEmuMute, &enveEmuMute, &enveEmuMute,
/*TODO*///		&enveEmuMute, &enveEmuMute, &enveEmuMute, &enveEmuMute
/*TODO*///	};
/*TODO*///	
/*TODO*///	/* Real-time functions. */
/*TODO*///	/* Order is important because of inline optimizations. */
/*TODO*///	/* */
/*TODO*///	/* ADSRctrl is (index*2) to enveModeTable[], because of KEY-bit. */
/*TODO*///	
/*TODO*///	INLINE void enveEmuEnveAdvance(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStep += pVoice.fenveStepAdd;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStep.l += pVoice.enveStepAdd.l;
/*TODO*///	#else
/*TODO*///		pVoice.enveStepPnt += pVoice.enveStepAddPnt;
/*TODO*///		pVoice.enveStep += pVoice.enveStepAdd + ( pVoice.enveStepPnt > 65535 );
/*TODO*///		pVoice.enveStepPnt &= 0xFFFF;
/*TODO*///	#endif
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* Mute/Idle. */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	/* Only used in the beginning. */
/*TODO*///	INLINE uword enveEmuMute(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* Release */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	INLINE uword enveEmuRelease(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.enveStep = (uword)pVoice.fenveStep;
/*TODO*///	#endif
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///		if ( pVoice.enveStep.w[HI] >= releaseTabLen )
/*TODO*///	#else
/*TODO*///		if ( pVoice.enveStep >= releaseTabLen )
/*TODO*///	#endif
/*TODO*///		{
/*TODO*///			pVoice.enveVol = releaseTab[releaseTabLen -1];
/*TODO*///			return masterAmplModTable[ pVoice.sid.masterVolumeAmplIndex + pVoice.enveVol ];
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///			pVoice.enveVol = releaseTab[pVoice.enveStep.w[HI]];
/*TODO*///	#else
/*TODO*///			pVoice.enveVol = releaseTab[pVoice.enveStep];
/*TODO*///	#endif
/*TODO*///			enveEmuEnveAdvance(pVoice);
/*TODO*///			return masterAmplModTable[ pVoice.sid.masterVolumeAmplIndex + pVoice.enveVol ];
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuAlterRelease(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		ubyte release = pVoice.SIDSR & 0x0F;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStepAdd = decayReleaseRates[release];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStepAdd.l = decayReleaseRates[release];
/*TODO*///	#else
/*TODO*///		pVoice.enveStepAdd = decayReleaseRates[release];
/*TODO*///		pVoice.enveStepAddPnt = decayReleaseRatesP[release];
/*TODO*///	#endif
/*TODO*///		pVoice.ADSRproc = &enveEmuRelease;
/*TODO*///		return enveEmuRelease(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuStartRelease(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		pVoice.ADSRctrl = ENVE_RELEASE;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStep = releasePos[pVoice.enveVol];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStep.w[HI] = releasePos[pVoice.enveVol];
/*TODO*///		pVoice.enveStep.w[LO] = 0;
/*TODO*///	#else
/*TODO*///		pVoice.enveStep = releasePos[pVoice.enveVol];
/*TODO*///		pVoice.enveStepPnt = 0;
/*TODO*///	#endif
/*TODO*///		return enveEmuAlterRelease(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* Sustain */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	INLINE uword enveEmuSustain(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		return masterAmplModTable[pVoice.sid.masterVolumeAmplIndex+pVoice.enveVol];
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuSustainDecay(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.enveStep = (uword)pVoice.fenveStep;
/*TODO*///	#endif
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///		if ( pVoice.enveStep.w[HI] >= releaseTabLen )
/*TODO*///	#else
/*TODO*///		if ( pVoice.enveStep >= releaseTabLen )
/*TODO*///	#endif
/*TODO*///		{
/*TODO*///			pVoice.enveVol = releaseTab[releaseTabLen-1];
/*TODO*///			return enveEmuAlterSustain(pVoice);
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///			pVoice.enveVol = releaseTab[pVoice.enveStep.w[HI]];
/*TODO*///	#else
/*TODO*///			pVoice.enveVol = releaseTab[pVoice.enveStep];
/*TODO*///	#endif
/*TODO*///			/* Will be controlled from sidEmuSet2(). */
/*TODO*///			if ( pVoice.enveVol <= pVoice.enveSusVol )
/*TODO*///			{
/*TODO*///				pVoice.enveVol = pVoice.enveSusVol;
/*TODO*///				return enveEmuAlterSustain(pVoice);
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				enveEmuEnveAdvance(pVoice);
/*TODO*///				return masterAmplModTable[ pVoice.sid.masterVolumeAmplIndex + pVoice.enveVol ];
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* This is the same as enveEmuStartSustainDecay(). */
/*TODO*///	INLINE uword enveEmuAlterSustainDecay(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		ubyte decay = pVoice.SIDAD & 0x0F ;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStepAdd = decayReleaseRates[decay];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStepAdd.l = decayReleaseRates[decay];
/*TODO*///	#else
/*TODO*///		pVoice.enveStepAdd = decayReleaseRates[decay];
/*TODO*///		pVoice.enveStepAddPnt = decayReleaseRatesP[decay];
/*TODO*///	#endif
/*TODO*///		pVoice.ADSRproc = &enveEmuSustainDecay;
/*TODO*///		return enveEmuSustainDecay(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* This is the same as enveEmuStartSustain(). */
/*TODO*///	INLINE uword enveEmuAlterSustain(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		if ( pVoice.enveVol > pVoice.enveSusVol )
/*TODO*///		{
/*TODO*///			pVoice.ADSRctrl = ENVE_SUSTAINDECAY;
/*TODO*///			pVoice.ADSRproc = &enveEmuSustainDecay;
/*TODO*///			return enveEmuAlterSustainDecay(pVoice);
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			pVoice.ADSRctrl = ENVE_SUSTAIN;
/*TODO*///			pVoice.ADSRproc = &enveEmuSustain;
/*TODO*///			return enveEmuSustain(pVoice);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* Decay */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	INLINE uword enveEmuDecay(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.enveStep = (uword)pVoice.fenveStep;
/*TODO*///	#endif
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///		if ( pVoice.enveStep.w[HI] >= releaseTabLen )
/*TODO*///	#else
/*TODO*///		if ( pVoice.enveStep >= releaseTabLen )
/*TODO*///	#endif
/*TODO*///		{
/*TODO*///			pVoice.enveVol = pVoice.enveSusVol;
/*TODO*///			return enveEmuAlterSustain(pVoice);  /* start sustain */
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///			pVoice.enveVol = releaseTab[pVoice.enveStep.w[HI]];
/*TODO*///	#else
/*TODO*///			pVoice.enveVol = releaseTab[pVoice.enveStep];
/*TODO*///	#endif
/*TODO*///			/* Will be controlled from sidEmuSet2(). */
/*TODO*///			if ( pVoice.enveVol <= pVoice.enveSusVol )
/*TODO*///			{
/*TODO*///				pVoice.enveVol = pVoice.enveSusVol;
/*TODO*///				return enveEmuAlterSustain(pVoice);  /* start sustain */
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				enveEmuEnveAdvance(pVoice);
/*TODO*///				return masterAmplModTable[ pVoice.sid.masterVolumeAmplIndex + pVoice.enveVol ];
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuAlterDecay(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		ubyte decay = pVoice.SIDAD & 0x0F ;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStepAdd = decayReleaseRates[decay];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStepAdd.l = decayReleaseRates[decay];
/*TODO*///	#else
/*TODO*///		pVoice.enveStepAdd = decayReleaseRates[decay];
/*TODO*///		pVoice.enveStepAddPnt = decayReleaseRatesP[decay];
/*TODO*///	#endif
/*TODO*///		pVoice.ADSRproc = &enveEmuDecay;
/*TODO*///		return enveEmuDecay(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuStartDecay(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		pVoice.ADSRctrl = ENVE_DECAY;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStep = 0;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStep.l = 0;
/*TODO*///	#else
/*TODO*///		pVoice.enveStep = (pVoice.enveStepPnt = 0);
/*TODO*///	#endif
/*TODO*///		return enveEmuAlterDecay(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* Attack */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	INLINE uword enveEmuAttack(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.enveStep = (uword)pVoice.fenveStep;
/*TODO*///	#endif
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///		if ( pVoice.enveStep.w[HI] > attackTabLen )
/*TODO*///	#else
/*TODO*///		if ( pVoice.enveStep >= attackTabLen )
/*TODO*///	#endif
/*TODO*///			return enveEmuStartDecay(pVoice);
/*TODO*///		else
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///			pVoice.enveVol = pVoice.enveStep.w[HI];
/*TODO*///	#else
/*TODO*///			pVoice.enveVol = pVoice.enveStep;
/*TODO*///	#endif
/*TODO*///			enveEmuEnveAdvance(pVoice);
/*TODO*///			return masterAmplModTable[ pVoice.sid.masterVolumeAmplIndex + pVoice.enveVol ];
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuAlterAttack(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		ubyte attack = pVoice.SIDAD >> 4;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStepAdd = attackRates[attack];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStepAdd.l = attackRates[attack];
/*TODO*///	#else
/*TODO*///		pVoice.enveStepAdd = attackRates[attack];
/*TODO*///		pVoice.enveStepAddPnt = attackRatesP[attack];
/*TODO*///	#endif
/*TODO*///		pVoice.ADSRproc = &enveEmuAttack;
/*TODO*///		return enveEmuAttack(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuStartAttack(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		pVoice.ADSRctrl = ENVE_ATTACK;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStep = (float)pVoice.enveVol;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStep.w[HI] = pVoice.enveVol;
/*TODO*///		pVoice.enveStep.w[LO] = 0;
/*TODO*///	#else
/*TODO*///		pVoice.enveStep = pVoice.enveVol;
/*TODO*///		pVoice.enveStepPnt = 0;
/*TODO*///	#endif
/*TODO*///		return enveEmuAlterAttack(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* */
/*TODO*///	/* Experimental. */
/*TODO*///	/* */
/*TODO*///	
/*TODO*///	/*/*
/*TODO*///	INLINE uword enveEmuShortAttack(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.enveStep = (uword)pVoice.fenveStep;
/*TODO*///	#endif
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///		if ((pVoice.enveStep.w[HI] > attackTabLen) ||
/*TODO*///			(pVoice.enveShortAttackCount == 0))
/*TODO*///	#else
/*TODO*///		if ((pVoice.enveStep >= attackTabLen) ||
/*TODO*///			(pVoice.enveShortAttackCount == 0))
/*TODO*///	#endif
/*TODO*///	/*		return enveEmuStartRelease(pVoice); */
/*TODO*///			return enveEmuStartDecay(pVoice);
/*TODO*///		else
/*TODO*///		{
/*TODO*///	#if defined(DIRECT_FIXPOINT) && !defined(SID_FPUENVE)
/*TODO*///			pVoice.enveVol = pVoice.enveStep.w[HI];
/*TODO*///	#else
/*TODO*///			pVoice.enveVol = pVoice.enveStep;
/*TODO*///	#endif
/*TODO*///		    pVoice.enveShortAttackCount--;
/*TODO*///	/*		cout << hex << pVoice.enveShortAttackCount << " / " << pVoice.enveVol << endl; */
/*TODO*///			enveEmuEnveAdvance(pVoice);
/*TODO*///			return masterAmplModTable[ pVoice.sid.masterVolumeAmplIndex + pVoice.enveVol ];
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuAlterShortAttack(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		ubyte attack = pVoice.SIDAD >> 4;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStepAdd = attackRates[attack];
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStepAdd.l = attackRates[attack];
/*TODO*///	#else
/*TODO*///		pVoice.enveStepAdd = attackRates[attack];
/*TODO*///		pVoice.enveStepAddPnt = attackRatesP[attack];
/*TODO*///	#endif
/*TODO*///		pVoice.ADSRproc = &enveEmuShortAttack;
/*TODO*///		return enveEmuShortAttack(pVoice);
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE uword enveEmuStartShortAttack(sidOperator* pVoice)
/*TODO*///	{
/*TODO*///		pVoice.ADSRctrl = ENVE_SHORTATTACK;
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		pVoice.fenveStep = (float)pVoice.enveVol;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		pVoice.enveStep.w[HI] = pVoice.enveVol;
/*TODO*///		pVoice.enveStep.w[LO] = 0;
/*TODO*///	#else
/*TODO*///		pVoice.enveStep = pVoice.enveVol;
/*TODO*///		pVoice.enveStepPnt = 0;
/*TODO*///	#endif
/*TODO*///		pVoice.enveShortAttackCount = 65535;  /* unused */
/*TODO*///		return enveEmuAlterShortAttack(pVoice);
/*TODO*///	}    
}
