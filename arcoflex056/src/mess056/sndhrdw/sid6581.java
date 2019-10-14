/***************************************************************************

  MOS sound interface device sid6581

***************************************************************************/

/* uses Michael Schwendt's sidplay (copyright message in 6581_.cpp)
   problematic and much work to integrate, so better to redo bugfixes
   in the converted code

   now only 1 sid chip allowed!
   needs rework
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.sndhrdw;

import static mess056.includes.sid6581H.*;

public class sid6581
{
	
	public static int VERBOSE_DBG = 1;
	
	
	/*TODO*///SID6581[] sid6581 = new SID6581[MAX_SID6581];
	
	void sid6581_set_type (int number, int type)
	{
	    /*TODO*///sid6581[number].type=type;
	    /*TODO*///sidInitWaveformTables(type);
	}
	
	void sid6581_reset(int number)
	{
		/*TODO*///sidEmuReset(sid6581+number);
	}
	
	/*TODO*///READ_HANDLER ( sid6581_0_port_r )
/*TODO*///	{
/*TODO*///		return sid6581_port_r(sid6581, offset);
/*TODO*///	}
/*TODO*///	
/*TODO*///	READ_HANDLER ( sid6581_1_port_r )
/*TODO*///	{
/*TODO*///		return sid6581_port_r(sid6581+1, offset);
/*TODO*///	}
/*TODO*///	
/*TODO*///	WRITE_HANDLER ( sid6581_0_port_w )
/*TODO*///	{
/*TODO*///		sid6581_port_w(sid6581, offset, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	WRITE_HANDLER ( sid6581_1_port_w )
/*TODO*///	{
/*TODO*///		sid6581_port_w(sid6581+1, offset, data);
/*TODO*///	}
/*TODO*///	
/*TODO*///	void sid6581_update()
/*TODO*///	{
/*TODO*///		int i;
/*TODO*///		for (i=0; i<MAX_SID6581; i++) {
/*TODO*///			if (sid6581[i].on)
/*TODO*///				stream_update(sid6581[i].mixer_channel,0);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	void sid6581_sh_update(int param, INT16 *buffer, int length)
/*TODO*///	{
/*TODO*///		sidEmuFillBuffer(sid6581+param,buffer, length);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int sid6581_custom_start (const struct MachineSound *driver)
/*TODO*///	{
/*TODO*///		const SID6581_interface *iface=(const SID6581_interface*)
/*TODO*///			driver->sound_interface;
/*TODO*///		int i;
/*TODO*///	
/*TODO*///		for (i=0; i< iface->count; i++) {
/*TODO*///			char name[10];
/*TODO*///			if (iface->count!=1) sprintf(name,"SID%d",i);
/*TODO*///			else sprintf(name,"SID");
/*TODO*///			sid6581[i].mixer_channel = stream_init (name, iface->chips[i].default_mixer_level, options.samplerate, i, sid6581_sh_update);
/*TODO*///	
/*TODO*///			sid6581[i].PCMfreq = options.samplerate;	
/*TODO*///			sid6581[i].type=iface->chips[i].type;
/*TODO*///			sid6581[i].clock=iface->chips[i].clock;
/*TODO*///			sid6581[i].ad_read=iface->chips[i].ad_read;
/*TODO*///			sid6581[i].on=1;
/*TODO*///			sid6581_init(sid6581+i);
/*TODO*///		}
/*TODO*///	
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void sid6581_custom_stop(void) {}
/*TODO*///	void sid6581_custom_update(void) {}
	
}
