/*
  copyright peter trauner 2000
  
  based on michael schwend's sid play

  Noise generation algorithm is used courtesy of Asger Alstrup Nielsen.
  His original publication can be found on the SID home page.

  Noise table optimization proposed by Phillip Wooller. The output of
  each table does not differ.

  MOS-8580 R5 combined waveforms recorded by Dennis "Deadman" Lindroos.
*/


/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.sndhrdw;

import static mame056.sound.streams.*;
import static mess056.sndhrdw.sid6581.*;
import static mess056.sndhrdw.sidH.*;
import static mess056.sndhrdw.sidenvel.*;
import static mess056.sndhrdw.sidvoice.*;

public class sid
{
	
/*TODO*///	#define VERBOSE_DBG 0
/*TODO*///	
/*TODO*///	filterfloat filterTable[0x800];
/*TODO*///	filterfloat bandPassParam[0x800];
/*TODO*///	#define lowPassParam filterTable
/*TODO*///	filterfloat filterResTable[16];
/*TODO*///	
/*TODO*///	#define maxLogicalVoices 4
/*TODO*///	
/*TODO*///	static const int mix16monoMiddleIndex = 256*maxLogicalVoices/2;
/*TODO*///	static uword mix16mono[256*maxLogicalVoices];
/*TODO*///	
/*TODO*///	static uword zero16bit=0;  /* either signed or unsigned */
/*TODO*///	udword splitBufferLen;
/*TODO*///	
/*TODO*///	void MixerInit(bool threeVoiceAmplify)
/*TODO*///	{
/*TODO*///		long si;
/*TODO*///		uword ui;
/*TODO*///		long ampDiv = maxLogicalVoices;
/*TODO*///	
/*TODO*///		if (threeVoiceAmplify)
/*TODO*///		{
/*TODO*///			ampDiv = (maxLogicalVoices-1);
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* Mixing formulas are optimized by sample input value. */
/*TODO*///	
/*TODO*///		si = (-128*maxLogicalVoices) * 256;
/*TODO*///		for (ui = 0; ui < sizeof(mix16mono)/sizeof(uword); ui++ )
/*TODO*///		{
/*TODO*///			mix16mono[ui] = (uword)(si/ampDiv) + zero16bit;
/*TODO*///			si+=256;
/*TODO*///		}
/*TODO*///	
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	INLINE void syncEm(SID6581 *This)
/*TODO*///	{
/*TODO*///		bool sync1 = (This.optr1.modulator.cycleLenCount <= 0);
/*TODO*///		bool sync2 = (This.optr2.modulator.cycleLenCount <= 0);
/*TODO*///		bool sync3 = (This.optr3.modulator.cycleLenCount <= 0);
/*TODO*///	
/*TODO*///		This.optr1.cycleLenCount--;
/*TODO*///		This.optr2.cycleLenCount--;
/*TODO*///		This.optr3.cycleLenCount--;
/*TODO*///	
/*TODO*///		if (This.optr1.sync && sync1)
/*TODO*///		{
/*TODO*///			This.optr1.cycleLenCount = 0;
/*TODO*///			This.optr1.outProc = &sidWaveCalcNormal;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			optr1.waveStep.l = 0;
/*TODO*///	#else
/*TODO*///			This.optr1.waveStep = (This.optr1.waveStepPnt = 0);
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///		if (This.optr2.sync && sync2)
/*TODO*///		{
/*TODO*///			This.optr2.cycleLenCount = 0;
/*TODO*///			This.optr2.outProc = &sidWaveCalcNormal;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			This.optr2.waveStep.l = 0;
/*TODO*///	#else
/*TODO*///			This.optr2.waveStep = (This.optr2.waveStepPnt = 0);
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///		if (This.optr3.sync && sync3)
/*TODO*///		{
/*TODO*///			This.optr3.cycleLenCount = 0;
/*TODO*///			This.optr3.outProc = &sidWaveCalcNormal;
/*TODO*///	#if defined(DIRECT_FIXPOINT)
/*TODO*///			optr3.waveStep.l = 0;
/*TODO*///	#else
/*TODO*///			This.optr3.waveStep = (This.optr3.waveStepPnt = 0);
/*TODO*///	#endif
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	void sidEmuFillBuffer(SID6581 *This, void* buffer, udword bufferLen )
/*TODO*///	{
/*TODO*///	//void* fill16bitMono( SID6581 *This, void* buffer, udword numberOfSamples )
/*TODO*///	    sword* buffer16bit = (sword*)buffer;
/*TODO*///	    for ( ; bufferLen > 0; bufferLen-- )
/*TODO*///	    {
/*TODO*///		*buffer16bit++ = mix16mono[(unsigned)(mix16monoMiddleIndex
/*TODO*///						      +(*This.optr1.outProc)(&This.optr1)
/*TODO*///						      +(*This.optr2.outProc)(&This.optr2)
/*TODO*///						      +(This.optr3.outProc(&This.optr3)&This.optr3_outputmask)
/*TODO*///	/* hack for digi sounds
/*TODO*///	   does n't seam to come from a tone operator
/*TODO*///	   ghostbusters and goldrunner everything except volume zeroed */
/*TODO*///						      +(This.masterVolume<<2)
/*TODO*///	//						  +(*sampleEmuRout)()
/*TODO*///		    )];
/*TODO*///		syncEm(This);
/*TODO*///	    }
/*TODO*///	}
/*TODO*///	
	/* --------------------------------------------------------------------- Init */
	
	
	/* Reset. */
	
	public static boolean sidEmuReset(_SID6581 This)
	{
		sidClearOperator( This.optr1 );
		enveEmuResetOperator( This.optr1 );
		sidClearOperator( This.optr2 );
		enveEmuResetOperator( This.optr2 );
		sidClearOperator( This.optr3 );
		enveEmuResetOperator( This.optr3 );
		This.optr3_outputmask = ~0;  /* on */
	
	//	sampleEmuReset();
	
		This.filter.Type = (This.filter.CurType = 0);
		This.filter.Value = 0;
		This.filter.Dy = (This.filter.ResDy = 0);
	
		sidEmuSet( This.optr1 );
		sidEmuSet( This.optr2 );
		sidEmuSet( This.optr3 );
	
		sidEmuSet2( This.optr1 );
		sidEmuSet2( This.optr2 );
		sidEmuSet2( This.optr3 );
	
	
		return true;
	}
	
	
/*TODO*///	void filterTableInit(void)
/*TODO*///	{
/*TODO*///		uword uk;
/*TODO*///		/* Parameter calculation has not been moved to a separate function */
/*TODO*///		/* by purpose. */
/*TODO*///		const float filterRefFreq = 44100.0;
/*TODO*///	
/*TODO*///	/*	extern filterfloat filterTable[0x800]; */
/*TODO*///		float yMax = 1.0;
/*TODO*///		float yMin = 0.01;
/*TODO*///		float yAdd;
/*TODO*///		float yTmp, rk, rk2;
/*TODO*///	
/*TODO*///		float resDyMax;
/*TODO*///		float resDyMin;
/*TODO*///		float resDy;
/*TODO*///	
/*TODO*///		uk = 0;
/*TODO*///		for ( rk = 0; rk < 0x800; rk++ )
/*TODO*///		{
/*TODO*///			filterTable[uk] = (((exp(rk/0x800*log(400.0))/60.0)+0.05)
/*TODO*///				*filterRefFreq) / options.samplerate;
/*TODO*///			if ( filterTable[uk] < yMin )
/*TODO*///				filterTable[uk] = yMin;
/*TODO*///			if ( filterTable[uk] > yMax )
/*TODO*///				filterTable[uk] = yMax;
/*TODO*///			uk++;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/*extern filterfloat bandPassParam[0x800]; */
/*TODO*///		yMax = 0.22;
/*TODO*///		yMin = 0.05;  /* less for some R1/R4 chips */
/*TODO*///		yAdd = (yMax-yMin)/2048.0;
/*TODO*///		yTmp = yMin;
/*TODO*///		uk = 0;
/*TODO*///		/* Some C++ compilers still have non-local scope! */
/*TODO*///		for ( rk2 = 0; rk2 < 0x800; rk2++ )
/*TODO*///		{
/*TODO*///			bandPassParam[uk] = (yTmp*filterRefFreq) / options.samplerate;
/*TODO*///			yTmp += yAdd;
/*TODO*///			uk++;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/*extern filterfloat filterResTable[16]; */
/*TODO*///		resDyMax = 1.0;
/*TODO*///		resDyMin = 2.0;
/*TODO*///		resDy = resDyMin;
/*TODO*///		for ( uk = 0; uk < 16; uk++ )
/*TODO*///		{
/*TODO*///			filterResTable[uk] = resDy;
/*TODO*///			resDy -= (( resDyMin - resDyMax ) / 15 );
/*TODO*///		}
/*TODO*///		filterResTable[0] = resDyMin;
/*TODO*///		filterResTable[15] = resDyMax;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void sid6581_init (SID6581 *This)
/*TODO*///	{
/*TODO*///		This.optr1.sid=This;
/*TODO*///		This.optr2.sid=This;
/*TODO*///		This.optr3.sid=This;
/*TODO*///	
/*TODO*///		This.optr1.modulator = &This.optr3;
/*TODO*///		This.optr3.carrier = &This.optr1;
/*TODO*///		This.optr1.filtVoiceMask = 1;
/*TODO*///	
/*TODO*///		This.optr2.modulator = &This.optr1;
/*TODO*///		This.optr1.carrier = &This.optr2;
/*TODO*///		This.optr2.filtVoiceMask = 2;
/*TODO*///	
/*TODO*///		This.optr3.modulator = &This.optr2;
/*TODO*///		This.optr2.carrier = &This.optr3;
/*TODO*///		This.optr3.filtVoiceMask = 4;
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///		This.PCMsid = (udword)(This.PCMfreq * (16777216.0 / This.clock));
/*TODO*///		This.PCMsidNoise = (udword)((This.clock*256.0)/This.PCMfreq);
/*TODO*///	
/*TODO*///		This.filter.Enabled = true;
/*TODO*///	
/*TODO*///		sidInitMixerEngine();
/*TODO*///		filterTableInit();
/*TODO*///	
/*TODO*///		sidInitWaveformTables(This.type);
/*TODO*///	
/*TODO*///		enveEmuInit(This.PCMfreq, true);
/*TODO*///	
/*TODO*///		MixerInit(0);
/*TODO*///	
/*TODO*///		sidEmuReset(This);
/*TODO*///	}
	
	public static void sid6581_port_w (int This, int offset, int data)
	{
/*TODO*///	    DBG_LOG (1, "sid6581 write", ("offset %.2x value %.2x\n", offset, data));
	    offset &= 0x1f;
	
	    switch (offset)
	    {
	    case 0x19: case 0x1a: case 0x1b: case 0x1c:
	    case 0x1d:
	    case 0x1e:
	    case 0x1f:
		break;
	    case 0x15: case 0x16: case 0x17: 
	    case 0x18:
		stream_update(_sid6581[This].mixer_channel,0);
		_sid6581[This].reg[offset] = data;
		_sid6581[This].masterVolume = ( _sid6581[This].reg[0x18] & 15 );
		_sid6581[This].masterVolumeAmplIndex = _sid6581[This].masterVolume << 8;	    
	
		if ((_sid6581[This].reg[0x18]&0x80)!=0
                        && ((_sid6581[This].reg[0x17]&_sid6581[This].optr3.filtVoiceMask)==0)
                        ){
		    _sid6581[This].optr3_outputmask = 0;     /* off */
		} else {
		    _sid6581[This].optr3_outputmask = ~0;  /* on */
		}
		_sid6581[This].filter.Type = _sid6581[This].reg[0x18] & 0x70;
		if (_sid6581[This].filter.Type != _sid6581[This].filter.CurType)
		{
		    _sid6581[This].filter.CurType = _sid6581[This].filter.Type;
		    _sid6581[This].optr1.filtLow = (_sid6581[This].optr1.filtRef = 0);
		    _sid6581[This].optr2.filtLow = (_sid6581[This].optr2.filtRef = 0);
		    _sid6581[This].optr3.filtLow = (_sid6581[This].optr3.filtRef = 0);
		}
		if ( _sid6581[This].filter.Enabled )
		{
		    _sid6581[This].filter.Value = 0x7ff & ( (_sid6581[This].reg[0x15]&7) | ( _sid6581[This].reg[0x16] << 3 ));
		    if (_sid6581[This].filter.Type == 0x20){
/*TODO*///			sid6581[This].filter.Dy = bandPassParam[sid6581[This].filter.Value];
                    } else {
/*TODO*///			sid6581[This].filter.Dy = lowPassParam[sid6581[This].filter.Value];
                    }
/*TODO*///		    sid6581[This].filter.ResDy = filterResTable[sid6581[This].reg[0x17] >> 4] - sid6581[This].filter.Dy;
		    if ( _sid6581[This].filter.ResDy < 1.0 )
			_sid6581[This].filter.ResDy = (float) 1.0;
		}
	
		sidEmuSet( _sid6581[This].optr1 );
		sidEmuSet( _sid6581[This].optr3 );	
		sidEmuSet( _sid6581[This].optr2 );
	
		// relies on sidEmuSet also for other channels!
		sidEmuSet2( _sid6581[This].optr1 );
		sidEmuSet2( _sid6581[This].optr2 );
		sidEmuSet2( _sid6581[This].optr3 );
	
		break;
	    default:
		stream_update(_sid6581[This].mixer_channel,0);
		_sid6581[This].reg[offset] = data;
		
		if (offset<7) {
		    _sid6581[This].optr1.reg[offset] = data;
		} else if (offset<14) {
		    _sid6581[This].optr2.reg[offset-7] = data;
		} else if (offset<21) {
		    _sid6581[This].optr3.reg[offset-14] = data;
		}
	
		sidEmuSet( _sid6581[This].optr1 );
		sidEmuSet( _sid6581[This].optr3 );	
		sidEmuSet( _sid6581[This].optr2 );
	
		// relies on sidEmuSet also for other channels!
		sidEmuSet2( _sid6581[This].optr1 );
		sidEmuSet2( _sid6581[This].optr2 );
		sidEmuSet2( _sid6581[This].optr3 );
	    }
	}
	
	public static int sid6581_port_r (int This, int offset)
	{
	    int data = 0;
	/* SIDPLAY reads last written at a sid address value */
	    offset &= 0x1f;
	    switch (offset)
	    {
	    case 0x1d:
	    case 0x1e:
	    case 0x1f:
		data=0xff;
		break;
	    case 0x19:						   /* paddle 1 */
		if (_sid6581[This].ad_read != null)
		    data=_sid6581[This].ad_read.handler(0);
		else
		    data=0;
		break;
	    case 0x1a:						   /* paddle 2 */
		if (_sid6581[This].ad_read != null)
		    data=_sid6581[This].ad_read.handler(1);
		else
		    data=0;
		break;
	    case 0x1b:
		stream_update(_sid6581[This].mixer_channel,0);
		data = _sid6581[This].optr3.output;
		break;
	    case 0x1c:
		stream_update(_sid6581[This].mixer_channel,0);
		data = _sid6581[This].optr3.enveVol;
		break;
	    default:
		data=_sid6581[This].reg[offset];
	    }
/*TODO*///	    DBG_LOG (1, "sid6581 read", ("offset %.2x value %.2x\n", offset, data));
	    return data;
	}
	
}
