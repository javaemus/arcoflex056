/*
  approximation of the sid6581 chip
  this part is for 1 (of the 3) voices of a chip
*/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.sndhrdw;

/**
 *
 * @author chusogar
 */
public class sidvoiceH {
/*TODO*///	
/*TODO*///	struct sw_storage
/*TODO*///	{
/*TODO*///		uword len;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		udword stp;
/*TODO*///	#else
/*TODO*///		udword pnt;
/*TODO*///		sword stp;
/*TODO*///	#endif
/*TODO*///	};
/*TODO*///	
/*TODO*///	struct _SID6581;
/*TODO*///	
        public static class sidOperator
	{
/*TODO*///		struct _SID6581 *sid;
		public int[] reg = new int[7];
/*TODO*///		udword SIDfreq;
/*TODO*///		uword SIDpulseWidth;
/*TODO*///		ubyte SIDctrl;
/*TODO*///		ubyte SIDAD, SIDSR;
/*TODO*///		
/*TODO*///		struct _sidOperator* carrier;
/*TODO*///		struct _sidOperator* modulator;
/*TODO*///		bool sync;
/*TODO*///		
/*TODO*///		uword pulseIndex, newPulseIndex;
/*TODO*///		uword curSIDfreq;
/*TODO*///		uword curNoiseFreq;
/*TODO*///		
                public int output;//, outputMask;
/*TODO*///		
		public char filtVoiceMask;
/*TODO*///		bool filtEnabled;
		public float filtLow, filtRef;
/*TODO*///		sbyte filtIO;
/*TODO*///		
/*TODO*///		sdword cycleLenCount;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		cpuLword cycleLen, cycleAddLen;
/*TODO*///	#else
/*TODO*///		udword cycleAddLenPnt;
/*TODO*///		uword cycleLen, cycleLenPnt;
/*TODO*///	#endif
/*TODO*///		
/*TODO*///		sbyte(*outProc)(struct _sidOperator *);
/*TODO*///		void(*waveProc)(struct _sidOperator *);
/*TODO*///	
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///		cpuLword waveStep, waveStepAdd;
/*TODO*///	#else
/*TODO*///		uword waveStep, waveStepAdd;
/*TODO*///		udword waveStepPnt, waveStepAddPnt;
/*TODO*///	#endif
/*TODO*///		uword waveStepOld;
/*TODO*///		struct sw_storage wavePre[2];
/*TODO*///	
/*TODO*///	#if defined(DIRECT_FIXPOINT) && defined(LARGE_NOISE_TABLE)
/*TODO*///		cpuLword noiseReg;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		cpuLBword noiseReg;
/*TODO*///	#else
/*TODO*///		udword noiseReg;
/*TODO*///	#endif
/*TODO*///		udword noiseStep, noiseStepAdd;
/*TODO*///		ubyte noiseOutput;
/*TODO*///		bool noiseIsLocked;
/*TODO*///	
/*TODO*///		ubyte ADSRctrl;
/*TODO*///	//	bool gateOnCtrl, gateOffCtrl;
/*TODO*///		uword (*ADSRproc)(struct _sidOperator *);
/*TODO*///		
/*TODO*///	#ifdef SID_FPUENVE
/*TODO*///		filterfloat fenveStep, fenveStepAdd;
/*TODO*///		udword enveStep;
/*TODO*///	#elif defined(DIRECT_FIXPOINT)
/*TODO*///		cpuLword enveStep, enveStepAdd;
/*TODO*///	#else
/*TODO*///		uword enveStep, enveStepAdd;
/*TODO*///		udword enveStepPnt, enveStepAddPnt;
/*TODO*///	#endif
		public int enveVol, enveSusVol;
/*TODO*///		uword enveShortAttackCount;
	};

/*TODO*///	typedef sbyte (*ptr2sidFunc)(sidOperator *);
/*TODO*///	typedef uword (*ptr2sidUwordFunc)(sidOperator *);
/*TODO*///	typedef void (*ptr2sidVoidFunc)(sidOperator *);
/*TODO*///	
/*TODO*///	void sidClearOperator( sidOperator* pVoice );
/*TODO*///	
/*TODO*///	void sidEmuSet(sidOperator* pVoice);
/*TODO*///	void sidEmuSet2(sidOperator* pVoice);
/*TODO*///	sbyte sidWaveCalcNormal(sidOperator* pVoice);
/*TODO*///	
/*TODO*///	void sidInitWaveformTables(SIDTYPE type);
/*TODO*///	void sidInitMixerEngine(void);
/*TODO*///	
/*TODO*///	#if 0
/*TODO*///	extern ptr2sidVoidFunc sid6581ModeNormalTable[16];
/*TODO*///	extern ptr2sidVoidFunc sid6581ModeRingTable[16];
/*TODO*///	extern ptr2sidVoidFunc sid8580ModeNormalTable[16];
/*TODO*///	extern ptr2sidVoidFunc sid8580ModeRingTable[16];
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#endif
    
}
