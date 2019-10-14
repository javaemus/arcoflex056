
/***************************************************************************

	v9938 / v9958 emulation

***************************************************************************/

/*
 todo:

 - sprite collision 
 - vdp engine -- make run at correct speed
 - vr/hr/fh flags: double-check all of that
 - make vdp engine work in exp. ram
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.vidhrdw;

import static arcadeflex056.fucPtr.*;
import static arcadeflex056.osdepend.logerror;
import static arcadeflex056.video.osd_skip_this_frame;
import static common.ptr.*;
import static mame056.commonH.*;
import static mame056.mame.*;
import static mame056.cpuexec.*;
import static mess056.vidhrdw.tms9928a.INTCallbackPtr;
import static mess056.vidhrdw.v9938H.*;

public class v9938
{
	
	public static class _V9938 {
		/* general */
		public int model;
		public int offset_x, offset_y, visible_y, mode;
		/* palette */
		public int	pal_write_first, cmd_write_first;
		public int pal_write, cmd_write;
		public int[] palReg=new int[32], statReg=new int[10], contReg=new int[48];
                public int read_ahead;
		/* memory */
		public int address_latch;
		public UBytePtr vram = new UBytePtr(), vram_exp = new UBytePtr();
		public int vram_size;
	    /* interrupt */
	    /*TODO*///public UINT8 INT;
	    /*TODO*///void (*INTCallback)(int);
		public int scanline;
                /* blinking */
                public int blink, blink_count;
                /* sprites */
                public int sprite_limit;
		/* size */
		public int size, size_old, size_auto, size_now;
	};
	
	
	
	static char[] pal_ind16=new char[16], pal_ind256=new char[256], pal_indYJK;
	
	public static int V9938_MODE_TEXT1	= (0);
	public static int V9938_MODE_MULTI	= (1);
	public static int V9938_MODE_GRAPHIC1	= (2);
	public static int V9938_MODE_GRAPHIC2	= (3);
	public static int V9938_MODE_GRAPHIC3	= (4);
	public static int V9938_MODE_GRAPHIC4	= (5);
	public static int V9938_MODE_GRAPHIC5	= (6);
	public static int V9938_MODE_GRAPHIC6	= (7);
	public static int V9938_MODE_GRAPHIC7	= (8);
	public static int V9938_MODE_TEXT2	= (9);
	public static int V9938_MODE_UNKNOWN	= (10);
	
	
	static String v9938_modes[] = {
		"TEXT 1", "MULTICOLOR", "GRAPHIC 1", "GRAPHIC 2", "GRAPHIC 3",
		"GRAPHIC 4", "GRAPHIC 5", "GRAPHIC 6", "GRAPHIC 7", "TEXT 2",
		"UNKNOWN" };
	
	
	/***************************************************************************
	
		Palette functions
	
	***************************************************************************/
	
	/*
	About the colour burst registers:
	
	The color burst registers will only have effect on the composite video outputfrom
	the V9938. but the output is only NTSC (Never The Same Color ,so the
	effects are already present) . this system is not used in europe
	the european machines use a separate PAL  (Phase Alternating Line) encoder
	or no encoder at all , only RGB output.
	
	Erik de Boer.
	
	--
	Right now they're not emulated. For completeness sake they should -- with
	a dip-switch to turn them off. I really don't know how they work though. :(
	*/
	
	/*
	 In screen 8, the colors are encoded as:
	
	 7  6  5  4  3  2  1  0
	+--+--+--+--+--+--+--+--+
	|g2|g1|g0|r2|r1|r0|b2|b1|
	+--+--+--+--+--+--+--+--+
	
	b0 is set if b2 and b1 are set (remember, color bus is 3 bits)
	
	*/
	
	public static VhConvertColorPromPtr v9938_init_palette = new VhConvertColorPromPtr() {
            public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
                int	i,red;
                int _palette = 0;
	
		if (Machine.scrbitmap.depth == 8)
			{
			/* create 256 colour palette -- this is actually the graphic 7
			   palette, with duplicate entries so the core fill shrink it to
			   256 colours */
			for (i=0;i<512;i++)
				{
				palette[_palette++] = (char)(((i >> 6) & 7) * 36); /* red */
				palette[_palette++] = (char)(((i >> 3) & 7) * 36); /* green */
				red = (i & 6); if (red == 6) red++;
				palette[_palette++] = (char)(red * 36); /* blue */
				}
			}
		else
			{
			/* create the full 512 colour palette */
			for (i=0;i<512;i++)
				{
				palette[_palette++] = (char)(((i >> 6) & 7) * 36); /* red */
				palette[_palette++] = (char)(((i >> 3) & 7) * 36); /* green */
				palette[_palette++] = (char)((i & 7) * 36); /* blue */
				}
			}
            }
        };
		
	/*
	
	The v9958 can display up to 19286 colours. For this we need a larger palette.
	
	The colours are encoded in 17 bits; however there are just 19268 different colours.
	Here we calculate the palette and a 2^17 reference table to the palette,
	which is: pal_indYJK. It's 256K in size, but I can't think of a faster way
	to emulate this. Also it keeps the palette a reasonable size. :)
	
	*/
	
	public static VhConvertColorPromPtr v9958_init_palette = new VhConvertColorPromPtr() {
            public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
		int r,g,b,y,j,k,i,k0,j0,n;
		char[] pal;
	
		pal = new UBytePtr(palette, 512*3).memory;
		v9938_init_palette.handler(palette, colortable, color_prom);
	
		/* set up YJK table */
		if (pal_indYJK == null)
			{
			pal_indYJK = new char[0x20000];
			if (pal_indYJK == null)
				{
				logerror ("Fatal: cannot malloc () in v9958_init_palette (), cannot exit\n");
				return;
				}
			}
	
		logerror ("Building YJK table for V9958 screens, may take a while ... \n");
		i = 0;
		for (y=0;y<32;y++) for (k=0;k<64;k++) for (j=0;j<64;j++)
			{
			/* calculate the color */
			if (k >= 32) k0 = (k - 64); else k0 = k;
			if (j >= 32) j0 = (j - 64); else j0 = j;
			r = y + j0;
                        b = (y * 5 - 2 * j0 - k0) / 4;
                        g = y + k0;
			if (r < 0) r = 0; else if (r > 31) r = 31;
			if (g < 0) g = 0; else if (g > 31) g = 31;
			if (b < 0) b = 0; else if (b > 31) b = 31;
	
			if (Machine.scrbitmap.depth == 8)
				{
				/* we don't have the space for more entries, so map it to the
				   256 colours we already have */
				r /= 4; g /= 4; b /= 4;
				pal_indYJK[y | j << 5 | k << (5 + 6)] = (char)((r << 6) | (g << 3) | b);
				}
			else
				{
				r = (255 * r) / 31;
				b = (255 * g) / 31;
				g = (255 * r) / 31;
				/* have we seen this one before */
				n = 0;
				while (n < i)
					{
					if (pal[n*3+0] == r && pal[n*3+1] == g && pal[n*3+2] == b)
						{
						pal_indYJK[y | j << 5 | k << (5 + 6)] = (char)(n + 512);
						break;
						}
					n++;
					}
	
				if (i == n)
					{
					/* so we haven't; add it */
					pal[i*3+0] = (char) r;
					pal[i*3+1] = (char) g;
					pal[i*3+2] = (char) b;
					pal_indYJK[y | j << 5 | k << (5 + 6)] = (char) (i + 512);
					i++;
					}
				}
			}
	
		if (i != 19268)
			logerror ("Table creation failed - %d colours out of 19286 created\n", i);
		}
        };
	
	/*
	
	 so lookups for screen 12 will look like:
	
	 int ind;
	
	 ind = (*data & 7) << 11 | (*(data + 1) & 7) << 14 |
		   (*(data + 2) & 7) << 5 | (*(data + 3) & 7) << 8;
	
	 pixel0 = pal_indYJK[ind | (*data >> 3) & 31];
	 pixel1 = pal_indYJK[ind | (*(data + 1) >> 3) & 31];
	 pixel2 = pal_indYJK[ind | (*(data + 2) >> 3) & 31];
	 pixel3 = pal_indYJK[ind | (*(data + 3) >> 3) & 31];
	
	and for screen 11:
	
	pixel0 = (*data) & 8 ? pal_ind16[(*data) >> 4] : pal_indYJK[ind | (*data >> 3) & 30];
	pixel1 = *(data+1) & 8 ? pal_ind16[*(data+1) >> 4] : pal_indYJK[ind | *(data+1) >> 3) & 30];
	pixel2 = *(data+2) & 8 ? pal_ind16[*(data+2) >> 4] : pal_indYJK[ind | *(data+2) >> 3) & 30];
	pixel3 = *(data+3) & 8 ? pal_ind16[*(data+3) >> 4] : pal_indYJK[ind | *(data+3) >> 3) & 30];
	
	*/
	
        public static _V9938 _vdp = new _V9938();
        
	public static WriteHandlerPtr v9938_palette_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                int indexp;
	
		if (_vdp.pal_write_first != 0)
			{
			/* store in register */
			indexp = _vdp.contReg[0x10] & 15;
			_vdp.palReg[indexp*2] = _vdp.pal_write & 0x77;
			_vdp.palReg[indexp*2+1] = data & 0x07;
			/* update palette */
			pal_ind16[indexp] = (char) ((((int)_vdp.pal_write << 2) & 0x01c0)  |
                                (((int)data << 3) & 0x0038)  |
                                ((int)_vdp.pal_write & 0x0007));
	
			_vdp.contReg[0x10] = (_vdp.contReg[0x10] + 1) & 15;
			_vdp.pal_write_first = 0;
			}
		else
			{
			_vdp.pal_write = data;
			_vdp.pal_write_first = 1;
			}
            }
            
        };
		
                /* taken from V9938 Technical Data book, page 148. it's in G-R-B format */
		static char pal16[] = {
			0, 0, 0, /* 0: black/transparent */
			0, 0, 0, /* 1: black */
			6, 1, 1, /* 2: medium green */
			7, 3, 3, /* 3: light green */
			1, 1, 7, /* 4: dark blue */
			3, 2, 7, /* 5: light blue */
			1, 5, 1, /* 6: dark red */
			6, 2, 7, /* 7: cyan */
			1, 7, 1, /* 8: medium red */
			3, 7, 3, /* 9: light red */
			6, 6, 1, /* 10: dark yellow */
			6, 6, 4, /* 11: light yellow */
			4, 1, 1, /* 12: dark green */
			2, 6, 5, /* 13: magenta */
			5, 5, 5, /* 14: gray */
			7, 7, 7  /* 15: white */
	        };
                
	static void v9938_reset_palette ()
		{
		
		int i, red, ind;
	
		for (i=0;i<16;i++)
			{
			/* set the palette registers */
			_vdp.palReg[i*2+0] = pal16[i*3+1] << 4 | pal16[i*3+2];
			_vdp.palReg[i*2+1] = pal16[i*3];
			/* set the reference table */
			pal_ind16[i] = (char) (pal16[i*3+1] << 6 | pal16[i*3] << 3 | pal16[i*3+2]);
			}
	
		/* set internal palette GRAPHIC 7 */
		for (i=0;i<256;i++)
			{
			ind = (i << 4) & 0x01c0;
			ind |= (i >> 2) & 0x0038;
			red = (i << 1) & 6; if (red == 6) red++;
			ind |= red;
	
			pal_ind256[i] = (char) ind;
			}
		}
	
	/***************************************************************************
	
		Memory functions
	
	***************************************************************************/
	
	static void v9938_vram_write (int offset, int data)
		{
		int newoffset;
	
		if ( (_vdp.mode == V9938_MODE_GRAPHIC6) || (_vdp.mode == V9938_MODE_GRAPHIC7) )
			{
	        newoffset = ((offset & 1) << 16) | (offset >> 1);
	   		if (newoffset < _vdp.vram_size)
	        	_vdp.vram.write(newoffset, data);
			}
		else
			{
			if (offset < _vdp.vram_size)
				_vdp.vram.write(offset, data);
	        }
		}
	
	static int v9938_vram_read (int offset)
		{
		if ( (_vdp.mode == V9938_MODE_GRAPHIC6) || (_vdp.mode == V9938_MODE_GRAPHIC7) )
			return _vdp.vram.read(((offset & 1) << 16) | (offset >> 1));
		else
			return _vdp.vram.read(offset);
		}
	
	public static WriteHandlerPtr v9938_vram_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                int address;
	
		/*v9938_update_command ();*/
	
		_vdp.cmd_write_first = 0;
	
	    address = ((int)_vdp.contReg[14] << 14) | _vdp.address_latch;
	
	    if ((_vdp.contReg[47] & 0x20)!=0)
	        {
	        if (_vdp.vram_exp!=null && address < 0x10000)
	            _vdp.vram_exp.write(address, data);
	        }
	    else
	        {
			v9938_vram_write (address, data);
	        }
	
		_vdp.address_latch = (_vdp.address_latch + 1) & 0x3fff;
		if (_vdp.address_latch==0 && (_vdp.contReg[0] & 0x0c)!=0 ) /* correct ??? */
			{
			_vdp.contReg[14] = (_vdp.contReg[14] + 1) & 7;
			}
            }
        };
		
	public static ReadHandlerPtr v9938_vram_r = new ReadHandlerPtr() {
            public int handler(int offset) {
                int ret;
		int address;
	
		address = ((int)_vdp.contReg[14] << 14) | _vdp.address_latch;
	
		_vdp.cmd_write_first = 0;
	
		ret = _vdp.read_ahead;
	
		if ((_vdp.contReg[47] & 0x20) != 0)
			{
			/* correct? */
			if (_vdp.vram_exp!=null && address < 0x10000)
				_vdp.read_ahead = _vdp.vram_exp.read(address);
			else
				_vdp.read_ahead = 0xff;
			}
		else
			{
			_vdp.read_ahead = v9938_vram_read (address);
			}
	
		_vdp.address_latch = (_vdp.address_latch + 1) & 0x3fff;
		if (_vdp.address_latch==0 && (_vdp.contReg[0] & 0x0c)!=0 ) /* correct ??? */
			{
			_vdp.contReg[14] = (_vdp.contReg[14] + 1) & 7;
			}
	
		return ret;
            }
        };
		
	public static WriteHandlerPtr v9938_command_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
		if (_vdp.cmd_write_first != 0)
			{
			if ((data & 0x80) != 0)
				v9938_register_write(data & 0x3f, _vdp.cmd_write);
			else
				{
				_vdp.address_latch =
					((data << 8) | _vdp.cmd_write) & 0x3fff;
				if ( (data & 0x40) == 0 ) v9938_vram_r.handler(0); /* read ahead! */
				}
	
			_vdp.cmd_write_first = 0;
			}
		else
			{
			_vdp.cmd_write = data;
			_vdp.cmd_write_first = 1;
			}
		}
        };
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Init/stop/reset/Interrupt functions
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
	public static int v9938_init (int model, int vram_size, INTCallbackPtr callback )
		{
/*TODO*///		memset (&_vdp, 0, sizeof (_vdp) );
/*TODO*///	
/*TODO*///		_vdp.model = model;
/*TODO*///		_vdp.vram_size = vram_size;
/*TODO*///		_vdp.INTCallback = callback;
/*TODO*///		_vdp.size_old = -1;
/*TODO*///	
/*TODO*///		/* allocate VRAM */
/*TODO*///		_vdp.vram = malloc (0x20000);
/*TODO*///		if (!_vdp.vram) return 1;
/*TODO*///		memset (_vdp.vram, 0, 0x20000);
/*TODO*///		if (_vdp.vram_size < 0x20000)
/*TODO*///			{
/*TODO*///			/* set unavailable RAM to 0xff */
/*TODO*///			memset (_vdp.vram + _vdp.vram_size, 0xff, (0x20000 - _vdp.vram_size) );
/*TODO*///			}
/*TODO*///		/* do we have expanded memory? */
/*TODO*///		if (_vdp.vram_size > 0x20000)
/*TODO*///			{
/*TODO*///			_vdp.vram_exp = malloc (0x10000);
/*TODO*///			if (!_vdp.vram_exp)
/*TODO*///				{
/*TODO*///				free (_vdp.vram);
/*TODO*///				return 1;
/*TODO*///				}
/*TODO*///			memset (_vdp.vram_exp, 0, 0x10000);
/*TODO*///			}
/*TODO*///		else
/*TODO*///			_vdp.vram_exp = null;
/*TODO*///	
		return 0;
		}
	
	public static void v9938_reset ()
		{
		int i;
	
		/* offset reset */
		_vdp.offset_x = 7;
		_vdp.offset_y = 7 + 10;
		_vdp.visible_y = 192;
		/* register reset */
		v9938_reset_palette (); /* palette registers */
		for (i=0;i<10;i++) _vdp.statReg[i] = 0;
		_vdp.statReg[2] = 0x0c;
		if (_vdp.model == MODEL_V9958) _vdp.statReg[1] |= 4;
		for (i=0;i<48;i++) _vdp.contReg[i] = 0;
		_vdp.cmd_write_first = _vdp.pal_write_first = 0;
/*TODO*///		_vdp.INT = 0;
		_vdp.read_ahead = 0; _vdp.address_latch = 0; /* ??? */
		_vdp.scanline = 0;
		}
	
	public static VhStopPtr v9938_exit = new VhStopPtr() {
            @Override
            public void handler() {
                _vdp.vram = null;
		if (_vdp.vram_exp != null) _vdp.vram_exp=null;
		if (pal_indYJK != null)
			{ pal_indYJK = null; }
            }
        };

	static void v9938_check_int ()
		{
/*TODO*///		int n;
/*TODO*///	
/*TODO*///		n = ( (_vdp.contReg[1] & 0x20) && (_vdp.statReg[0] & 0x80) ) ||
/*TODO*///			( (_vdp.statReg[1] & 0x01) && (_vdp.contReg[0] & 0x10) );
/*TODO*///	
/*TODO*///		if (n != _vdp.INT)
/*TODO*///			{
/*TODO*///			_vdp.INT = n;
/*TODO*///			logerror ("V9938: IRQ line %s\n", n!=0 ? "up" : "down");
/*TODO*///			}
/*TODO*///	
/*TODO*///		/* 
/*TODO*///	    ** Somehow the IRQ request is going down without cpu_irq_line () being
/*TODO*///	    ** called; because of this Mr. Ghost, Xevious and SD Snatcher don't
/*TODO*///	    ** run. As a patch it's called every scanline 
/*TODO*///	    */
/*TODO*///		_vdp.INTCallback (n);
		}
	
	public static void v9938_set_sprite_limit (int i)
		{
		_vdp.sprite_limit = i;
		}
	
	public static void v9938_set_resolution (int i)
		{
		if (i == RENDER_AUTO)
			{
			_vdp.size_auto = 1;
			}
		else
			{
			_vdp.size = i;
			_vdp.size_auto = 0;
			}	
		}
	
	/***************************************************************************
	
		Register functions
	
	***************************************************************************/
	
	public static WriteHandlerPtr v9938_register_w = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                int reg;
	
		reg = _vdp.contReg[17] & 0x3f;
		if (reg != 17) v9938_register_write (reg, data); /* true ? */
		if ( (_vdp.contReg[17] & 0x80) == 0 )
			_vdp.contReg[17] = (_vdp.contReg[17] + 1) & 0x3f;
            }
        };
		
        static int reg_mask[] = {
			0x7e, 0x7b, 0x7f, 0xff, 0x3f, 0xff, 0x3f, 0xff,
			0xfb, 0xbf, 0x07, 0x03, 0xff, 0xff, 0x07, 0x0f,
			0x0f, 0xbf, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff,
			0x00, 0x7f, 0x3f, 0x07 };
        
	public static void v9938_register_write (int reg, int data)
		{
		
	
		if (reg <= 27)
			{
			data &= reg_mask[reg];
			if (_vdp.contReg[reg] == data) return;
			}
	
		if (reg > 46)
			{
			logerror ("V9938: Attempted to write to non-existant R#%d\n", reg);
			return;
			}
	
		/*v9938_update_command (); */
	
		switch (reg)
			{
			/* registers that affect interrupt and display mode */
			case 0:
			case 1:
				_vdp.contReg[reg] = data;
				v9938_set_mode ();
				v9938_check_int ();
				logerror ("V9938: mode = %s\n", v9938_modes[_vdp.mode]);
				break;
			case 18:
			case 9:
				_vdp.contReg[reg] = data;
				/* recalc offset */
				_vdp.offset_x = ( (~_vdp.contReg[18] - 8) & 0x0f);
				_vdp.offset_y = (~(_vdp.contReg[18]>>4) - 8) & 0x0f;
				if ((_vdp.contReg[9] & 0x80) != 0)
					_vdp.visible_y = 212;
				else
					{
					_vdp.visible_y = 192;
					_vdp.offset_y += 10;
					}
	
				break;
			case 15:
				_vdp.pal_write_first = 0;
				break;
			/* color burst registers aren't emulated */
			case 20:
			case 21:
			case 22:
				logerror ("V9938: Write %02xh to R#%d; color burst not emulated\n",
					data, reg);
				break;
			case 25:
			case 26:
			case 27:
				if (_vdp.model != MODEL_V9958)
					{
					logerror ("V9938: Attempting to write %02xh to V9958 R#%d\n");
					data = 0;
					}
				break;
			case 44:
				v9938_cpu_to_vdp (data);
				break;
			case 46:
				v9938_command_unit_w(data);
				break;
			}
	
		if (reg != 15)
			logerror ("V9938: Write %02x to R#%d\n", data, reg);
	
		_vdp.contReg[reg] = data;
		}
	
	public static ReadHandlerPtr v9938_status_r = new ReadHandlerPtr() {
            public int handler(int offset) {
                int reg, n;
		int ret;
	
		_vdp.cmd_write_first = 0;
	
		reg = _vdp.contReg[15] & 0x0f;
		if (reg > 9)
			return 0xff;
	
		switch (reg)
			{
			case 0:
				ret = _vdp.statReg[0];
				_vdp.statReg[0] &= 0x1f;
				break;
			case 1:
				ret = _vdp.statReg[1];
				_vdp.statReg[1] &= 0xfe;
				break;
			case 2:
				/*v9938_update_command ();*/
				n = cycles_currently_ran ();
				if ( (n < 28) || (n > 199) ) _vdp.statReg[2] |= 0x20;
				else _vdp.statReg[2] &= ~0x20;
				ret = _vdp.statReg[2];
				break;
			case 7:
				ret = _vdp.statReg[7];
/*TODO*///				_vdp.statReg[7] = _vdp.contReg[44] = v9938_vdp_to_cpu () ;
				break;
			default:
				ret = _vdp.statReg[reg];
				break;
			}
	
		logerror ("V9938: Read %02x from S#%d\n", ret, reg);
		v9938_check_int ();
	
		return ret;
            }
        };
		
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Refresh / render function
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	#define V9938_SECOND_FIELD ( !(((_vdp.contReg[9] & 0x04) && !(_vdp.statReg[2] & 2)) || _vdp.blink)) 
/*TODO*///	
/*TODO*///	#define V9938_WIDTH	(512 + 32)
/*TODO*///	#define V9938_BPP	(8)
/*TODO*///	#undef	V9938_BPP
/*TODO*///	#define V9938_BPP	(16)
/*TODO*///	#undef 	V9938_WIDTH
/*TODO*///	#define V9938_WIDTH	(256 + 16)
/*TODO*///	#undef	V9938_BPP
/*TODO*///	#define V9938_BPP	(8)
/*TODO*///	#undef	V9938_BPP
/*TODO*///	#undef 	V9938_WIDTH
/*TODO*///	
/*TODO*///	static void v9938_sprite_mode1 (int line, UINT8 *col)
/*TODO*///		{
/*TODO*///		UINT8	*attrtbl, *patterntbl, *patternptr;
/*TODO*///		int x, y, p, height, c, p2, i, n, pattern;
/*TODO*///	
/*TODO*///		memset (col, 0, 256);
/*TODO*///	
/*TODO*///		/* are sprites disabled? */
/*TODO*///		if (_vdp.contReg[8] & 0x02) return;
/*TODO*///	
/*TODO*///		attrtbl = _vdp.vram + (_vdp.contReg[5] << 7) + (_vdp.contReg[11] << 15);
/*TODO*///		patterntbl = _vdp.vram + (_vdp.contReg[6] << 11);
/*TODO*///	
/*TODO*///		/* 16x16 or 8x8 sprites */
/*TODO*///		height = (_vdp.contReg[1] & 2) ? 16 : 8;
/*TODO*///		/* magnified sprites (zoomed) */
/*TODO*///		if (_vdp.contReg[1] & 1) height *= 2;
/*TODO*///	
/*TODO*///		p2 = p = 0;
/*TODO*///		while (1)
/*TODO*///			{
/*TODO*///			y = attrtbl[0];
/*TODO*///			if (y == 208) break;
/*TODO*///			y = (y - _vdp.contReg[23]) & 255;
/*TODO*///			if (y > 208)
/*TODO*///				y = -(~y&255);
/*TODO*///			else
/*TODO*///				y++;
/*TODO*///	
/*TODO*///			/* if sprite in range, has to be drawn */
/*TODO*///			if ( (line >= y) && (line  < (y + height) ) )
/*TODO*///				{
/*TODO*///				if (p2 == 4)
/*TODO*///					{
/*TODO*///					/* max maximum sprites per line! */
/*TODO*///					if ( !(_vdp.statReg[0] & 0x40) )
/*TODO*///						_vdp.statReg[0] = (_vdp.statReg[0] & 0xa0) | 0x40 | p;
/*TODO*///	
/*TODO*///					if (_vdpsprite_limit) break;
/*TODO*///					}
/*TODO*///				/* get x */
/*TODO*///				x = attrtbl[1];
/*TODO*///				if (attrtbl[3] & 0x80) x -= 32;
/*TODO*///	
/*TODO*///				/* get pattern */
/*TODO*///				pattern = attrtbl[2];
/*TODO*///				if (_vdp.contReg[1] & 2)
/*TODO*///					pattern &= 0xfc;
/*TODO*///				n = line - y;
/*TODO*///				patternptr = patterntbl + pattern * 8 +
/*TODO*///					((_vdp.contReg[1] & 1) ? n/2  : n);
/*TODO*///				pattern = patternptr[0] << 8 | patternptr[16];
/*TODO*///	
/*TODO*///				/* get colour */
/*TODO*///				c = attrtbl[3] & 0x0f;
/*TODO*///	
/*TODO*///				/* draw left part */
/*TODO*///				n = 0;
/*TODO*///				while (1)
/*TODO*///					{
/*TODO*///					if (n == 0) pattern = patternptr[0];
/*TODO*///					else if ( (n == 1) && (_vdp.contReg[1] & 2) ) pattern = patternptr[16];
/*TODO*///					else break;
/*TODO*///	
/*TODO*///					n++;
/*TODO*///	
/*TODO*///					for (i=0;i<8;i++)
/*TODO*///						{
/*TODO*///						if (pattern & 0x80)
/*TODO*///							{
/*TODO*///							if ( (x >= 0) && (x < 256) )
/*TODO*///								{
/*TODO*///								if (col[x] & 0x40)
/*TODO*///									{
/*TODO*///									/* we have a collision! */
/*TODO*///									if (p2 < 4)
/*TODO*///										_vdp.statReg[0] |= 0x20;
/*TODO*///									}
/*TODO*///								if ( !(col[x] & 0x80) )
/*TODO*///									{
/*TODO*///									if (c || (_vdp.contReg[8] & 0x20) )
/*TODO*///										col[x] |= 0xc0 | c;
/*TODO*///									else
/*TODO*///										col[x] |= 0x40;
/*TODO*///									}
/*TODO*///	
/*TODO*///								/* if zoomed, draw another pixel */
/*TODO*///								if (_vdp.contReg[1] & 1)
/*TODO*///									{
/*TODO*///									if (col[x+1] & 0x40)
/*TODO*///	    	                        	{
/*TODO*///	       		                    	/* we have a collision! */
/*TODO*///										if (p2 < 4)
/*TODO*///											_vdp.statReg[0] |= 0x20;
/*TODO*///	                            		}
/*TODO*///	                        		if ( !(col[x+1] & 0x80) )
/*TODO*///		                            	{
/*TODO*///	   		                         	if (c || (_vdp.contReg[8] & 0x20) )
/*TODO*///											col[x+1] |= 0xc0 | c;
/*TODO*///										else
/*TODO*///											col[x+1] |= 0x80;
/*TODO*///	                            		}
/*TODO*///									}
/*TODO*///								}
/*TODO*///							}
/*TODO*///						if (_vdp.contReg[1] & 1) x += 2; else x++;
/*TODO*///						pattern <<= 1;
/*TODO*///						}
/*TODO*///					}
/*TODO*///	
/*TODO*///				p2++;
/*TODO*///				}
/*TODO*///	
/*TODO*///			if (p >= 31) break;
/*TODO*///			p++;
/*TODO*///			attrtbl += 4;
/*TODO*///			}
/*TODO*///	
/*TODO*///		if ( !(_vdp.statReg[0] & 0x40) )
/*TODO*///			_vdp.statReg[0] = (_vdp.statReg[0] & 0xa0) | p;
/*TODO*///		}
/*TODO*///	
/*TODO*///	static void v9938_sprite_mode2 (int line, UINT8 *col)
/*TODO*///		{
/*TODO*///		int attrtbl, patterntbl, patternptr, colourtbl;
/*TODO*///		int x, i, y, p, height, c, p2, n, pattern, colourmask, first_cc_seen;
/*TODO*///	
/*TODO*///		memset (col, 0, 256);
/*TODO*///	
/*TODO*///		/* are sprites disabled? */
/*TODO*///		if (_vdp.contReg[8] & 0x02) return;
/*TODO*///	
/*TODO*///		attrtbl = ( (_vdp.contReg[5] & 0xfc) << 7) + (_vdp.contReg[11] << 15);
/*TODO*///		colourtbl =  ( (_vdp.contReg[5] & 0xf8) << 7) + (_vdp.contReg[11] << 15);
/*TODO*///		patterntbl = (_vdp.contReg[6] << 11);
/*TODO*///		colourmask = ( (_vdp.contReg[5] & 3) << 3) | 0x7; /* check this! */
/*TODO*///	
/*TODO*///		/* 16x16 or 8x8 sprites */
/*TODO*///		height = (_vdp.contReg[1] & 2) ? 16 : 8;
/*TODO*///		/* magnified sprites (zoomed) */
/*TODO*///		if (_vdp.contReg[1] & 1) height *= 2;
/*TODO*///	
/*TODO*///		p2 = p = first_cc_seen = 0;
/*TODO*///		while (1)
/*TODO*///			{
/*TODO*///			y = v9938_vram_read (attrtbl);
/*TODO*///			if (y == 216) break;
/*TODO*///			y = (y - _vdp.contReg[23]) & 255;
/*TODO*///			if (y > 216)
/*TODO*///				y = -(~y&255);
/*TODO*///			else
/*TODO*///				y++;
/*TODO*///	
/*TODO*///			/* if sprite in range, has to be drawn */
/*TODO*///			if ( (line >= y) && (line  < (y + height) ) )
/*TODO*///				{
/*TODO*///				if (p2 == 8)
/*TODO*///					{
/*TODO*///					/* max maximum sprites per line! */
/*TODO*///					if ( !(_vdp.statReg[0] & 0x40) )
/*TODO*///						_vdp.statReg[0] = (_vdp.statReg[0] & 0xa0) | 0x40 | p;
/*TODO*///	
/*TODO*///					if (_vdpsprite_limit) break;
/*TODO*///					}
/*TODO*///	
/*TODO*///				n = line - y; if (_vdp.contReg[1] & 1) n /= 2;
/*TODO*///				/* get colour */
/*TODO*///				c = v9938_vram_read (colourtbl + (((p&colourmask)*16) + n));
/*TODO*///	
/*TODO*///				/* don't draw all sprite with CC set before any sprites 
/*TODO*///	               with CC = 0 are seen on this line */
/*TODO*///				if (c & 0x40)
/*TODO*///					{
/*TODO*///					if (first_cc_seen == 0)
/*TODO*///						goto skip_first_cc_set;
/*TODO*///					}
/*TODO*///				else
/*TODO*///					first_cc_seen = 1;
/*TODO*///	
/*TODO*///				/* get pattern */
/*TODO*///				pattern = v9938_vram_read (attrtbl + 2);
/*TODO*///				if (_vdp.contReg[1] & 2)
/*TODO*///					pattern &= 0xfc;
/*TODO*///				patternptr = patterntbl + pattern * 8 + n;
/*TODO*///				pattern = (v9938_vram_read (patternptr) << 8) |
/*TODO*///					v9938_vram_read (patternptr + 16);
/*TODO*///	
/*TODO*///				/* get x */
/*TODO*///				x = v9938_vram_read (attrtbl + 1);
/*TODO*///				if (c & 0x80) x -= 32;
/*TODO*///	
/*TODO*///				n = (_vdp.contReg[1] & 2) ? 16 : 8;
/*TODO*///				while (n--)
/*TODO*///					{
/*TODO*///					for (i=0;i<=(_vdp.contReg[1] & 1);i++)
/*TODO*///						{
/*TODO*///						if ( (x >= 0) && (x < 256) )
/*TODO*///							{
/*TODO*///							if ( (pattern & 0x8000) && !(col[x] & 0x10) )
/*TODO*///								{
/*TODO*///								if ( (c & 15) || (_vdp.contReg[8] & 0x20) ) 
/*TODO*///									{
/*TODO*///									if ( !(c & 0x40) )
/*TODO*///										{
/*TODO*///										if (col[x] & 0x20) col[x] |= 0x10;
/*TODO*///										else 
/*TODO*///											col[x] |= 0x20 | (c & 15);
/*TODO*///										}
/*TODO*///									else
/*TODO*///										col[x] |= c & 15;
/*TODO*///	
/*TODO*///									col[x] |= 0x80;
/*TODO*///									}
/*TODO*///								}
/*TODO*///							else
/*TODO*///								{
/*TODO*///								if ( !(c & 0x40) && (col[x] & 0x20) )
/*TODO*///									col[x] |= 0x10;
/*TODO*///								}
/*TODO*///	
/*TODO*///							if ( !(c & 0x60) && (pattern & 0x8000) )
/*TODO*///								{
/*TODO*///								if (col[x] & 0x40)
/*TODO*///									{
/*TODO*///									/* sprite collision! */
/*TODO*///									if (p2 < 8)
/*TODO*///										_vdp.statReg[0] |= 0x20;
/*TODO*///									}
/*TODO*///								else
/*TODO*///									col[x] |= 0x40;
/*TODO*///								}
/*TODO*///	
/*TODO*///							x++;
/*TODO*///							}
/*TODO*///						}
/*TODO*///	
/*TODO*///					pattern <<= 1;
/*TODO*///					}
/*TODO*///	
/*TODO*///	skip_first_cc_set:
/*TODO*///				p2++;
/*TODO*///				}
/*TODO*///	
/*TODO*///			if (p >= 31) break;
/*TODO*///			p++;
/*TODO*///			attrtbl += 4;
/*TODO*///			}
/*TODO*///	
/*TODO*///		if ( !(_vdp.statReg[0] & 0x40) )
/*TODO*///			_vdp.statReg[0] = (_vdp.statReg[0] & 0xa0) | p;
/*TODO*///		}
/*TODO*///	
/*TODO*///	typedef struct {
/*TODO*///		UINT8 m;
/*TODO*///		void (*visible_8)(UINT8*, int);
/*TODO*///		void (*visible_16)(UINT16*, int);
/*TODO*///		void (*visible_8s)(UINT8*, int);
/*TODO*///		void (*visible_16s)(UINT16*, int);
/*TODO*///		void (*border_8)(UINT8*);
/*TODO*///		void (*border_16)(UINT16*);
/*TODO*///		void (*border_8s)(UINT8*);
/*TODO*///		void (*border_16s)(UINT16*);
/*TODO*///		void (*sprites)(int, UINT8*);
/*TODO*///		void (*draw_sprite_8)(UINT8*, UINT8*);
/*TODO*///		void (*draw_sprite_16)(UINT16*, UINT8*);
/*TODO*///		void (*draw_sprite_8s)(UINT8*, UINT8*);
/*TODO*///		void (*draw_sprite_16s)(UINT16*, UINT8*);
/*TODO*///	} V9938_MODE;
/*TODO*///	
/*TODO*///	static const V9938_MODE modes[] = {
/*TODO*///		{ 0x02,
/*TODO*///			v9938_mode_text1_8, v9938_mode_text1_16,
/*TODO*///			v9938_mode_text1_8s, v9938_mode_text1_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			null, 
/*TODO*///			null, null,
/*TODO*///			null, null },
/*TODO*///		{ 0x01,
/*TODO*///			v9938_mode_multi_8, v9938_mode_multi_16,
/*TODO*///			v9938_mode_multi_8s, v9938_mode_multi_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			v9938_sprite_mode1, 
/*TODO*///			v9938_default_draw_sprite_8, v9938_default_draw_sprite_16,
/*TODO*///			v9938_default_draw_sprite_8s, v9938_default_draw_sprite_16s },
/*TODO*///		{ 0x00,
/*TODO*///			v9938_mode_graphic1_8, v9938_mode_graphic1_16,
/*TODO*///			v9938_mode_graphic1_8s, v9938_mode_graphic1_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			v9938_sprite_mode1, 
/*TODO*///			v9938_default_draw_sprite_8, v9938_default_draw_sprite_16,
/*TODO*///			v9938_default_draw_sprite_8s, v9938_default_draw_sprite_16s },
/*TODO*///		{ 0x04,
/*TODO*///			v9938_mode_graphic23_8, v9938_mode_graphic23_16,
/*TODO*///			v9938_mode_graphic23_8s, v9938_mode_graphic23_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			v9938_sprite_mode1, 
/*TODO*///			v9938_default_draw_sprite_8, v9938_default_draw_sprite_16,
/*TODO*///			v9938_default_draw_sprite_8s, v9938_default_draw_sprite_16s },
/*TODO*///		{ 0x08,
/*TODO*///			v9938_mode_graphic23_8, v9938_mode_graphic23_16,
/*TODO*///			v9938_mode_graphic23_8s, v9938_mode_graphic23_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			v9938_sprite_mode2, 
/*TODO*///			v9938_default_draw_sprite_8, v9938_default_draw_sprite_16,
/*TODO*///			v9938_default_draw_sprite_8s, v9938_default_draw_sprite_16s },
/*TODO*///		{ 0x0c,
/*TODO*///			v9938_mode_graphic4_8, v9938_mode_graphic4_16,
/*TODO*///			v9938_mode_graphic4_8s, v9938_mode_graphic4_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			v9938_sprite_mode2, 
/*TODO*///			v9938_default_draw_sprite_8, v9938_default_draw_sprite_16,
/*TODO*///			v9938_default_draw_sprite_8s, v9938_default_draw_sprite_16s },
/*TODO*///		{ 0x10,
/*TODO*///			v9938_mode_graphic5_8, v9938_mode_graphic5_16,
/*TODO*///			v9938_mode_graphic5_8s, v9938_mode_graphic5_16s,
/*TODO*///			v9938_graphic5_border_8, v9938_graphic5_border_16,
/*TODO*///			v9938_graphic5_border_8s, v9938_graphic5_border_16s,
/*TODO*///			v9938_sprite_mode2, 
/*TODO*///			v9938_graphic5_draw_sprite_8, v9938_graphic5_draw_sprite_16,
/*TODO*///			v9938_graphic5_draw_sprite_8s, v9938_graphic5_draw_sprite_16s },
/*TODO*///		{ 0x14,
/*TODO*///			v9938_mode_graphic6_8, v9938_mode_graphic6_16,
/*TODO*///			v9938_mode_graphic6_8s, v9938_mode_graphic6_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			v9938_sprite_mode2, 
/*TODO*///			v9938_default_draw_sprite_8, v9938_default_draw_sprite_16,
/*TODO*///			v9938_default_draw_sprite_8s, v9938_default_draw_sprite_16s },
/*TODO*///		{ 0x1c,
/*TODO*///			v9938_mode_graphic7_8, v9938_mode_graphic7_16,
/*TODO*///			v9938_mode_graphic7_8s, v9938_mode_graphic7_16s,
/*TODO*///			v9938_graphic7_border_8, v9938_graphic7_border_16,
/*TODO*///			v9938_graphic7_border_8s, v9938_graphic7_border_16s,
/*TODO*///			v9938_sprite_mode2, 
/*TODO*///			v9938_graphic7_draw_sprite_8, v9938_graphic7_draw_sprite_16,
/*TODO*///			v9938_graphic7_draw_sprite_8s, v9938_graphic7_draw_sprite_16s },
/*TODO*///		{ 0x0a,
/*TODO*///			v9938_mode_text2_8, v9938_mode_text2_16,
/*TODO*///			v9938_mode_text2_8s, v9938_mode_text2_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			null, 
/*TODO*///			null, null,
/*TODO*///			null, null },
/*TODO*///		{ 0xff,
/*TODO*///			v9938_mode_unknown_8, v9938_mode_unknown_16,
/*TODO*///			v9938_mode_unknown_8s, v9938_mode_unknown_16s,
/*TODO*///			v9938_default_border_8, v9938_default_border_16,
/*TODO*///			v9938_default_border_8s, v9938_default_border_16s,
/*TODO*///			null, 
/*TODO*///			null, null,
/*TODO*///			null, null },
/*TODO*///	};
	
	static void v9938_set_mode ()
		{
		int n,i;
	
		n = (((_vdp.contReg[0] & 0x0e) << 1) | ((_vdp.contReg[1] & 0x18) >> 3));
/*TODO*///		for (i=0;;i++)
/*TODO*///			{
/*TODO*///			if ( (modes[i].m == n) || (modes[i].m == 0xff) ) break;
/*TODO*///			}
/*TODO*///		_vdp.mode = i;
		}
	
/*TODO*///	static void v9938_refresh_8 (struct mame_bitmap *bmp, int line)
/*TODO*///		{
/*TODO*///		int i, double_lines;
/*TODO*///		UINT8 col[256], *ln, *ln2 = null;
/*TODO*///	
/*TODO*///		double_lines = 0;
/*TODO*///	
/*TODO*///		if (_vdp.size == RENDER_HIGH)
/*TODO*///			{
/*TODO*///			if (_vdp.contReg[9] & 0x08)
/*TODO*///				{
/*TODO*///				_vdp.size_now = RENDER_HIGH;
/*TODO*///				ln = bmp.line[line*2+((_vdp.statReg[2]>>1)&1)];
/*TODO*///				}
/*TODO*///			else
/*TODO*///				{
/*TODO*///				ln = bmp.line[line*2];
/*TODO*///				ln2 = bmp.line[line*2+1];
/*TODO*///				double_lines = 1;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		else
/*TODO*///			ln = bmp.line[line];
/*TODO*///	
/*TODO*///		if ( !(_vdp.contReg[1] & 0x40) || (_vdp.statReg[2] & 0x40) )
/*TODO*///			{
/*TODO*///			if (_vdp.size == RENDER_HIGH)
/*TODO*///				modes[_vdp.mode].border_8 (ln);
/*TODO*///			else
/*TODO*///				modes[_vdp.mode].border_8s (ln);
/*TODO*///			}
/*TODO*///		else
/*TODO*///			{
/*TODO*///			i = (line - _vdp.offset_y) & 255;
/*TODO*///			if (_vdp.size == RENDER_HIGH)
/*TODO*///				{
/*TODO*///				modes[_vdp.mode].visible_8 (ln, i);
/*TODO*///				if (modes[_vdp.mode].sprites)
/*TODO*///					{
/*TODO*///					modes[_vdp.mode].sprites (i, col);
/*TODO*///					modes[_vdp.mode].draw_sprite_8 (ln, col);
/*TODO*///					}
/*TODO*///				}
/*TODO*///			else
/*TODO*///				{
/*TODO*///				modes[_vdp.mode].visible_8s (ln, i);
/*TODO*///				if (modes[_vdp.mode].sprites)
/*TODO*///					{
/*TODO*///					modes[_vdp.mode].sprites (i, col);
/*TODO*///					modes[_vdp.mode].draw_sprite_8s (ln, col);
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///		if (double_lines)
/*TODO*///			memcpy (ln2, ln, (512 + 32) );
/*TODO*///		}
/*TODO*///	
/*TODO*///	static void v9938_refresh_16 (struct mame_bitmap *bmp, int line)
/*TODO*///		{
/*TODO*///		int i, double_lines;
/*TODO*///		UINT8 col[256];
/*TODO*///		UINT16 *ln, *ln2 = null;
/*TODO*///	
/*TODO*///		double_lines = 0;
/*TODO*///	
/*TODO*///		if (_vdp.size == RENDER_HIGH)
/*TODO*///			{
/*TODO*///			if (_vdp.contReg[9] & 0x08)
/*TODO*///				{
/*TODO*///				_vdp.size_now = RENDER_HIGH;
/*TODO*///				ln = (UINT16*)bmp.line[line*2+((_vdp.statReg[2]>>1)&1)];
/*TODO*///				}
/*TODO*///			else
/*TODO*///				{
/*TODO*///				ln = (UINT16*)bmp.line[line*2];
/*TODO*///				ln2 = (UINT16*)bmp.line[line*2+1];
/*TODO*///				double_lines = 1;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		else
/*TODO*///			ln = (UINT16*)bmp.line[line];
/*TODO*///	
/*TODO*///		if ( !(_vdp.contReg[1] & 0x40) || (_vdp.statReg[2] & 0x40) )
/*TODO*///			{
/*TODO*///			if (_vdp.size == RENDER_HIGH)
/*TODO*///				modes[_vdp.mode].border_16 (ln);
/*TODO*///			else
/*TODO*///				modes[_vdp.mode].border_16s (ln);
/*TODO*///			}
/*TODO*///		else
/*TODO*///			{
/*TODO*///			i = (line - _vdp.offset_y) & 255;
/*TODO*///			if (_vdp.size == RENDER_HIGH)
/*TODO*///				{
/*TODO*///				modes[_vdp.mode].visible_16 (ln, i);
/*TODO*///				if (modes[_vdp.mode].sprites)
/*TODO*///					{
/*TODO*///					modes[_vdp.mode].sprites (i, col);
/*TODO*///					modes[_vdp.mode].draw_sprite_16 (ln, col);
/*TODO*///					}
/*TODO*///				}
/*TODO*///			else
/*TODO*///				{
/*TODO*///				modes[_vdp.mode].visible_16s (ln, i);
/*TODO*///				if (modes[_vdp.mode].sprites)
/*TODO*///					{
/*TODO*///					modes[_vdp.mode].sprites (i, col);
/*TODO*///					modes[_vdp.mode].draw_sprite_16s (ln, col);
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///		if (double_lines != 0)
/*TODO*///			memcpy (ln2, ln, (512 + 32) * 2);
/*TODO*///		}
/*TODO*///	
/*TODO*///	static void v9938_refresh_line (mame_bitmap bmp, int line)
/*TODO*///		{
/*TODO*///		int ind16, ind256;
/*TODO*///	
/*TODO*///		ind16 = pal_ind16[0];
/*TODO*///		ind256 = pal_ind256[0];
/*TODO*///	
/*TODO*///		if ( !(_vdp.contReg[8] & 0x20) && (_vdp.mode != V9938_MODE_GRAPHIC5) )
/*TODO*///			{
/*TODO*///			pal_ind16[0] = pal_ind16[(_vdp.contReg[7] & 0x0f)];
/*TODO*///			pal_ind256[0] = pal_ind256[_vdp.contReg[7]];
/*TODO*///			}
/*TODO*///	
/*TODO*///		if (Machine.scrbitmap.depth == 8)
/*TODO*///			v9938_refresh_8 (bmp, line);
/*TODO*///		else
/*TODO*///			v9938_refresh_16 (bmp, line);
/*TODO*///	
/*TODO*///		if ( !(_vdp.contReg[8] & 0x20) && (_vdp.mode != V9938_MODE_GRAPHIC5) )
/*TODO*///			{
/*TODO*///			pal_ind16[0] = ind16;
/*TODO*///			pal_ind256[0] = ind256;
/*TODO*///			}
/*TODO*///		}
	
	public static VhUpdatePtr v9938_refresh = new VhUpdatePtr() {
            public void handler(mame_bitmap bitmap, int full_refresh) {
                /* already been rendered, since we're using scanline stuff */
            }
        };
		
/*TODO*///	/*
/*TODO*///	
/*TODO*///	From: awulms@inter.nl.net (Alex Wulms)
/*TODO*///	*** About the HR/VR topic: this is how it works according to me:
/*TODO*///	
/*TODO*///	*** HR:
/*TODO*///	HR is very straightforward:
/*TODO*///	-HR=1 during 'display time'
/*TODO*///	-HR=0 during 'horizontal border, horizontal retrace'
/*TODO*///	I have put 'display time' and 'horizontal border, horizontal retrace' between
/*TODO*///	quotes because HR does not only flip between 0 and 1 during the display of
/*TODO*///	the 192/212 display lines, but also during the vertical border and during the
/*TODO*///	vertical retrace.
/*TODO*///	
/*TODO*///	*** VR:
/*TODO*///	VR is a little bit tricky
/*TODO*///	-VR always gets set to 0 when the VDP starts with display line 0
/*TODO*///	-VR gets set to 1 when the VDP reaches display line (192 if LN=0) or (212 if
/*TODO*///	LN=1)
/*TODO*///	-The VDP displays contents of VRAM as long as VR=0
/*TODO*///	
/*TODO*///	As a consequence of this behaviour, it is possible to program the famous
/*TODO*///	overscan trick, where VRAM contents is shown in the borders:
/*TODO*///	Generate an interrupt at line 230 (or so) and on this interrupt: set LN=1
/*TODO*///	Generate an interrupt at line 200 (or so) and on this interrupt: set LN=0
/*TODO*///	Repeat the above two steps
/*TODO*///	
/*TODO*///	*** The top/bottom border contents during overscan:
/*TODO*///	On screen 0:
/*TODO*///	1) The VDP keeps increasing the name table address pointer during bottom
/*TODO*///	border, vertical retrace and top border
/*TODO*///	2) The VDP resets the name table address pointer when the first display line
/*TODO*///	is reached
/*TODO*///	
/*TODO*///	On the other screens:
/*TODO*///	1) The VDP keeps increasing the name table address pointer during the bottom
/*TODO*///	border
/*TODO*///	2) The VDP resets the name table address pointer such that the top border
/*TODO*///	contents connects up with the first display line. E.g., when the top border
/*TODO*///	is 26 lines high, the VDP will take:
/*TODO*///	'logical'      vram line
/*TODO*///	TOPB000  256-26
/*TODO*///	...
/*TODO*///	TOPB025  256-01
/*TODO*///	DISPL000 000
/*TODO*///	...
/*TODO*///	DISPL211 211
/*TODO*///	BOTB000  212
/*TODO*///	...
/*TODO*///	BOTB024  236
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	*** About the horizontal interrupt
/*TODO*///	
/*TODO*///	All relevant definitions on a row:
/*TODO*///	-FH: Bit 0 of status register 1
/*TODO*///	-IE1: Bit 4 of mode register 0
/*TODO*///	-IL: Line number in mode register 19
/*TODO*///	-DL: The line that the VDP is going to display (corrected for vertical scroll)
/*TODO*///	-IRQ: Interrupt request line of VDP to Z80
/*TODO*///	
/*TODO*///	At the *start* of every new line (display, bottom border, part of vertical
/*TODO*///	display), the VDP does:
/*TODO*///	-FH = (FH && IE1) || (IL==DL)
/*TODO*///	
/*TODO*///	After reading of status register 1 by the CPU, the VDP does:
/*TODO*///	-FH = 0
/*TODO*///	
/*TODO*///	Furthermore, the following is true all the time:
/*TODO*///	-IRQ = FH && IE1
/*TODO*///	
/*TODO*///	The resulting behaviour:
/*TODO*///	When IE1=0:
/*TODO*///	-FH will be set as soon as display of line IL starts
/*TODO*///	-FH will be reset as soon as status register 1 is read
/*TODO*///	-FH will be reset as soon as the next display line is reached
/*TODO*///	
/*TODO*///	When IE=1:
/*TODO*///	-FH and IRQ will be set as soon as display line IL is reached
/*TODO*///	-FH and IRQ will be reset as soon as status register 1 is read
/*TODO*///	
/*TODO*///	Another subtile result:
/*TODO*///	If, while FH and IRQ are set, IE1 gets reset, the next happens:
/*TODO*///	-IRQ is reset immediately (since IRQ is always FH && IE1)
/*TODO*///	-FH will be reset as soon as display of the next line starts (unless the next
/*TODO*///	line is line IL)
/*TODO*///	
/*TODO*///	
/*TODO*///	*** About the vertical interrupt:
/*TODO*///	Another relevant definition:
/*TODO*///	-FV: Bit 7 of status register 0
/*TODO*///	-IE0: Bit 5 of mode register 1
/*TODO*///	
/*TODO*///	I only know for sure the behaviour when IE0=1:
/*TODO*///	-FV and IRQ will be set as soon as VR changes from 0 to 1
/*TODO*///	-FV and IRQ will be reset as soon as status register 0 is read
/*TODO*///	
/*TODO*///	A consequence is that NO vertical interrupts will be generated during the
/*TODO*///	overscan trick, described in the VR section above.
/*TODO*///	
/*TODO*///	I do not know the behaviour of FV when IE0=0. That is the part that I still
/*TODO*///	have to test.
/*TODO*///	*/
/*TODO*///	
/*TODO*///	static void v9938_interrupt_start_vblank ()
/*TODO*///		{
/*TODO*///	/*TODO*///#if 0
/*TODO*///	/*TODO*///	if (keyboard_pressed (KEYCODE_D) )
/*TODO*///	/*TODO*///		{
/*TODO*///	/*TODO*///		FILE *fp;
/*TODO*///	/*TODO*///		int i;
/*TODO*///	/*TODO*///
/*TODO*///	/*TODO*///		fp = fopen ("vram.dmp", "wb");
/*TODO*///	/*TODO*///		if (fp)
/*TODO*///	/*TODO*///			{
/*TODO*///	/*TODO*///			fwrite (_vdp.vram, 0x10000, 1, fp);
/*TODO*///	/*TODO*///			fclose (fp);
/*TODO*///	/*TODO*///			usrintf_showmessage ("saved");
/*TODO*///	/*TODO*///			}
/*TODO*///	/*TODO*///
/*TODO*///	/*TODO*///		for (i=0;i<24;i++) printf ("R#%d = %02x\n", i, _vdp.contReg[i]);
/*TODO*///	/*TODO*///		}
/*TODO*///	/*TODO*///#endif
/*TODO*///	
/*TODO*///		/* at every frame, vdp switches fields */
/*TODO*///		_vdp.statReg[2] = (_vdp.statReg[2] & 0xfd) | (~_vdp.statReg[2] & 2);
/*TODO*///	
/*TODO*///		/* color blinking */
/*TODO*///		if ((_vdp.contReg[13] & 0xf0)==0)
/*TODO*///			_vdp.blink = 0;
/*TODO*///		else if (!(_vdp.contReg[13] & 0x0f))
/*TODO*///			_vdp.blink = 1;
/*TODO*///		else
/*TODO*///			{
/*TODO*///			/* both on and off counter are non-zero: timed blinking */
/*TODO*///			if (_vdp.blink_count)
/*TODO*///				_vdp.blink_count--;
/*TODO*///			if (!_vdp.blink_count)
/*TODO*///				{
/*TODO*///				_vdp.blink = vdp.blink!=0?0:1;
/*TODO*///				if (_vdp.blink != 0)
/*TODO*///					_vdp.blink_count = (_vdp.contReg[13] >> 4) * 10;
/*TODO*///				else
/*TODO*///					_vdp.blink_count = (_vdp.contReg[13] & 0x0f) * 10;
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///		/* check screen rendering size */
/*TODO*///		if (_vdp.size_auto && (_vdp.size_now >= 0) && (_vdp.size != _vdp.size_now) )
/*TODO*///			_vdp.size = _vdp.size_now;
/*TODO*///	
/*TODO*///		if (_vdp.size != _vdp.size_old)
/*TODO*///			{
/*TODO*///			if (_vdp.size == RENDER_HIGH)
/*TODO*///				osd_set_visible_area (0, 512 + 32 - 1, 0, 424 + 32 - 1);
/*TODO*///			else
/*TODO*///				osd_set_visible_area (0, 256 + 16 - 1, 0, 212 + 16 - 1);
/*TODO*///	
/*TODO*///			_vdp.size_old = _vdp.size;
/*TODO*///			}
/*TODO*///	
/*TODO*///		_vdp.size_now = -1;
/*TODO*///		}
/*TODO*///	
	public static int v9938_interrupt ()
		{
/*TODO*///		UINT8 col[256];
/*TODO*///		int scanline, max, pal, scanline_start;
/*TODO*///	
/*TODO*///		v9938_update_command ();
/*TODO*///	
/*TODO*///		pal = _vdp.contReg[9] & 2;
/*TODO*///		if (pal) scanline_start = 53; else scanline_start = 26;
/*TODO*///	
/*TODO*///		/* set flags */
/*TODO*///		if (_vdp.scanline == (_vdp.offset_y + scanline_start) )
/*TODO*///			{
/*TODO*///			_vdp.statReg[2] &= ~0x40;
/*TODO*///			}
/*TODO*///		else if (_vdp.scanline == (_vdp.offset_y + _vdp.visible_y + scanline_start) )
/*TODO*///			{
/*TODO*///			_vdp.statReg[2] |= 0x40;
/*TODO*///			_vdp.statReg[0] |= 0x80;
/*TODO*///			}
/*TODO*///	
/*TODO*///		max = (pal) ? 255 : (_vdp.contReg[9] & 0x80) ? 234 : 244;
/*TODO*///		scanline = (_vdp.scanline - scanline_start - _vdp.offset_y);
/*TODO*///		if ( (scanline >= 0) && (scanline <= max) &&
/*TODO*///		   ( ( (scanline + _vdp.contReg[23]) & 255) == _vdp.contReg[19]) )
/*TODO*///			{
/*TODO*///			_vdp.statReg[1] |= 1;
/*TODO*///			logerror ("V9938: scanline interrupt (%d)\n", scanline);
/*TODO*///			}
/*TODO*///		else
/*TODO*///			if ( !(_vdp.contReg[0] & 0x10) ) _vdp.statReg[1] &= 0xfe;
/*TODO*///	
/*TODO*///		v9938_check_int ();
/*TODO*///	
/*TODO*///		/* check for start of vblank */
/*TODO*///		if ((pal && (_vdp.scanline == 310)) ||
/*TODO*///			(!pal && (_vdp.scanline == 259)))
/*TODO*///			v9938_interrupt_start_vblank ();
/*TODO*///	
/*TODO*///		/* render the current line */
/*TODO*///		if ((_vdp.scanline >= scanline_start) && (_vdp.scanline < (212 + 16 + scanline_start)))
/*TODO*///			{
/*TODO*///			scanline = (_vdp.scanline - scanline_start) & 255;
/*TODO*///	
/*TODO*///			if (osd_skip_this_frame () != 0 )
/*TODO*///				{
/*TODO*///				if ( (_vdp.statReg[2] & 0x40)==0 && (modes[_vdp.mode].sprites)!=0 )
/*TODO*///					modes[_vdp.mode].sprites ( (scanline - _vdp.offset_y) & 255, col);
/*TODO*///				}
/*TODO*///			else
/*TODO*///				{
/*TODO*///				v9938_refresh_line (Machine.scrbitmap, scanline);
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///		max = (_vdp.contReg[9] & 2)!=0 ? 313 : 262;
/*TODO*///		if (++_vdp.scanline == max)
/*TODO*///			_vdp.scanline = 0;
/*TODO*///	
/*TODO*///		return _vdp.INT;
                    return 0;
		}
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///		Command unit
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	public static int VDP(){ return _vdp.contReg;}
/*TODO*///	public static int VDPStatus(){return  _vdp.statReg;}
/*TODO*///	#define VRAM _vdp.vram
/*TODO*///	public static int ScrMode(){return  _vdp.mode;}
/*TODO*///	
/*TODO*///	/*************************************************************/
/*TODO*///	/** Completely rewritten by Alex Wulms:                     **/
/*TODO*///	/**  - VDP Command execution 'in parallel' with CPU         **/
/*TODO*///	/**  - Corrected behaviour of VDP commands                  **/
/*TODO*///	/**  - Made it easier to implement correct S7/8 mapping     **/
/*TODO*///	/**    by concentrating VRAM access in one single place     **/
/*TODO*///	/**  - Made use of the 'in parallel' VDP command exec       **/
/*TODO*///	/**    and correct timing. You must call the function       **/
/*TODO*///	/**    LoopVDP() from LoopZ80 in MSX.c. You must call it    **/
/*TODO*///	/**    exactly 256 times per screen refresh.                **/
/*TODO*///	/** Started on       : 11-11-1999                           **/
/*TODO*///	/** Beta release 1 on:  9-12-1999                           **/
/*TODO*///	/** Beta release 2 on: 20-01-2000                           **/
/*TODO*///	/**  - Corrected behaviour of VRM <. Z80 transfer          **/
/*TODO*///	/**  - Improved performance of the code                     **/
/*TODO*///	/** Public release 1.0: 20-04-2000                          **/
/*TODO*///	/*************************************************************/
/*TODO*///	
/*TODO*///	#define VDP_VRMP5(X, Y) (VRAM + ((Y&1023)<<7) + ((X&255)>>1))
/*TODO*///	#define VDP_VRMP6(X, Y) (VRAM + ((Y&1023)<<7) + ((X&511)>>2))
/*TODO*///	//#define VDP_VRMP7(X, Y) (VRAM + ((Y&511)<<8) + ((X&511)>>1))
/*TODO*///	#define VDP_VRMP7(X, Y) (VRAM + ((X&2)<<15) + ((Y&511)<<7) + ((X&511)>>2))
/*TODO*///	//#define VDP_VRMP8(X, Y) (VRAM + ((Y&511)<<8) + (X&255))
/*TODO*///	#define VDP_VRMP8(X, Y) (VRAM + ((X&1)<<16) + ((Y&511)<<7) + ((X>>1)&127))
/*TODO*///	
/*TODO*///	#define VDP_VRMP(M, X, Y) VDPVRMP(M, X, Y)
/*TODO*///	#define VDP_POINT(M, X, Y) VDPpoint(M, X, Y)
/*TODO*///	#define VDP_PSET(M, X, Y, C, O) VDPpset(M, X, Y, C, O)
/*TODO*///	
/*TODO*///	#define CM_ABRT  0x0
/*TODO*///	#define CM_POINT 0x4
/*TODO*///	#define CM_PSET  0x5
/*TODO*///	#define CM_SRCH  0x6
/*TODO*///	#define CM_LINE  0x7
/*TODO*///	#define CM_LMMV  0x8
/*TODO*///	#define CM_LMMM  0x9
/*TODO*///	#define CM_LMCM  0xA
/*TODO*///	#define CM_LMMC  0xB
/*TODO*///	#define CM_HMMV  0xC
/*TODO*///	#define CM_HMMM  0xD
/*TODO*///	#define CM_YMMM  0xE
/*TODO*///	#define CM_HMMC  0xF
/*TODO*///	
/*TODO*///	/*************************************************************/
/*TODO*///	/* Many VDP commands are executed in some kind of loop but   */
/*TODO*///	/* essentially, there are only a few basic loop structures   */
/*TODO*///	/* that are re-used. We define the loop structures that are  */
/*TODO*///	/* re-used here so that they have to be entered only once    */
/*TODO*///	/*************************************************************/
/*TODO*///	#define pre_loop \
/*TODO*///	    while ((cnt-=delta) > 0) {
/*TODO*///	
/*TODO*///	
/*TODO*///	/* Loop over DX, DY */
/*TODO*///	#define post__x_y(MX) \
/*TODO*///	    if (!--ANX || ((ADX+=TX)&MX)) { \
/*TODO*///	      if (!(--NY&1023) || (DY+=TY)==-1) \
/*TODO*///	        break; \
/*TODO*///	      else { \
/*TODO*///	        ADX=DX; \
/*TODO*///	        ANX=NX; \
/*TODO*///	      } \
/*TODO*///	    } \
/*TODO*///	  }
/*TODO*///	
/*TODO*///	/* Loop over DX, SY, DY */
/*TODO*///	#define post__xyy(MX) \
/*TODO*///	    if ((ADX+=TX)&MX) { \
/*TODO*///	      if (!(--NY&1023) || (SY+=TY)==-1 || (DY+=TY)==-1) \
/*TODO*///	        break; \
/*TODO*///	      else \
/*TODO*///	        ADX=DX; \
/*TODO*///	    } \
/*TODO*///	  }
/*TODO*///	
/*TODO*///	/* Loop over SX, DX, SY, DY */
/*TODO*///	#define post_xxyy(MX) \
/*TODO*///	    if (!--ANX || ((ASX+=TX)&MX) || ((ADX+=TX)&MX)) { \
/*TODO*///	      if (!(--NY&1023) || (SY+=TY)==-1 || (DY+=TY)==-1) \
/*TODO*///	        break; \
/*TODO*///	      else { \
/*TODO*///	        ASX=SX; \
/*TODO*///	        ADX=DX; \
/*TODO*///	        ANX=NX; \
/*TODO*///	      } \
/*TODO*///	    } \
/*TODO*///	  }
/*TODO*///	
/*TODO*///	/*************************************************************/
/*TODO*///	/** Structures and stuff                                    **/
/*TODO*///	/*************************************************************/
/*TODO*///	public static class MMC {
/*TODO*///	  public int SX,SY;
/*TODO*///	  public int DX,DY;
/*TODO*///	  public int TX,TY;
/*TODO*///	  public int NX,NY;
/*TODO*///	  public int MX;
/*TODO*///	  public int ASX,ADX,ANX;
/*TODO*///	  public int CL;
/*TODO*///	  public int LO;
/*TODO*///	  public int CM;
/*TODO*///	};
/*TODO*///	
/*TODO*///	/*************************************************************/
/*TODO*///	/** Function prototypes                                     **/
/*TODO*///	/*************************************************************/
/*TODO*///	static UINT8 *VDPVRMP(register UINT8 M, register int X, register int Y);
/*TODO*///	
/*TODO*///	static UINT8 VDPpoint5(register int SX, register int SY);
/*TODO*///	static UINT8 VDPpoint6(register int SX, register int SY);
/*TODO*///	static UINT8 VDPpoint7(register int SX, register int SY);
/*TODO*///	static UINT8 VDPpoint8(register int SX, register int SY);
/*TODO*///	
/*TODO*///	static UINT8 VDPpoint(register UINT8 SM,
/*TODO*///	                     register int SX, register int SY);
/*TODO*///	
/*TODO*///	static void VDPpsetlowlevel(register UINT8 *P, register UINT8 CL,
/*TODO*///	                            register UINT8 M, register UINT8 OP);
/*TODO*///	
/*TODO*///	static void VDPpset5(register int DX, register int DY,
/*TODO*///	                     register UINT8 CL, register UINT8 OP);
/*TODO*///	static void VDPpset6(register int DX, register int DY,
/*TODO*///	                     register UINT8 CL, register UINT8 OP);
/*TODO*///	static void VDPpset7(register int DX, register int DY,
/*TODO*///	                     register UINT8 CL, register UINT8 OP);
/*TODO*///	static void VDPpset8(register int DX, register int DY,
/*TODO*///	                     register UINT8 CL, register UINT8 OP);
/*TODO*///	
/*TODO*///	static void VDPpset(register UINT8 SM,
/*TODO*///	                    register int DX, register int DY,
/*TODO*///	                    register UINT8 CL, register UINT8 OP);
/*TODO*///	
/*TODO*///	static int GetVdpTimingValue(register int *);
/*TODO*///	
/*TODO*///	static void SrchEngine(void);
/*TODO*///	static void LineEngine(void);
/*TODO*///	static void LmmvEngine(void);
/*TODO*///	static void LmmmEngine(void);
/*TODO*///	static void LmcmEngine(void);
/*TODO*///	static void LmmcEngine(void);
/*TODO*///	static void HmmvEngine(void);
/*TODO*///	static void HmmmEngine(void);
/*TODO*///	static void YmmmEngine(void);
/*TODO*///	static void HmmcEngine(void);
/*TODO*///	
/*TODO*///	static void ReportVdpCommand(register UINT8 Op);
/*TODO*///	
/*TODO*///	/*************************************************************/
/*TODO*///	/** Variables visible only in this module                   **/
/*TODO*///	/*************************************************************/
/*TODO*///	static int Mask[] = { 0x0F,0x03,0x0F,0xFF };
/*TODO*///	static int  PPB[]  = { 2,4,2,1 };
/*TODO*///	static int  PPL[]  = { 256,512,512,256 };
/*TODO*///	static int  VdpOpsCnt=1;
/*TODO*///	static void (*VdpEngine)(void)=0;
/*TODO*///	
/*TODO*///	                      /*  SprOn SprOn SprOf SprOf */
/*TODO*///	                      /*  ScrOf ScrOn ScrOf ScrOn */
/*TODO*///	static int srch_timing[8]={ 818, 1025,  818,  830,   /* ntsc */
/*TODO*///	                            696,  854,  696,  684 }; /* pal  */
/*TODO*///	static int line_timing[8]={ 1063, 1259, 1063, 1161,
/*TODO*///	                            904,  1026, 904,  953 };
/*TODO*///	static int hmmv_timing[8]={ 439,  549,  439,  531,
/*TODO*///	                            366,  439,  366,  427 };
/*TODO*///	static int lmmv_timing[8]={ 873,  1135, 873, 1056,
/*TODO*///	                            732,  909,  732,  854 };
/*TODO*///	static int ymmm_timing[8]={ 586,  952,  586,  610,
/*TODO*///	                            488,  720,  488,  500 };
/*TODO*///	static int hmmm_timing[8]={ 818,  1111, 818,  854,
/*TODO*///	                            684,  879,  684,  708 };
/*TODO*///	static int lmmm_timing[8]={ 1160, 1599, 1160, 1172,
/*TODO*///	                            964,  1257, 964,  977 };
/*TODO*///	
/*TODO*///	
/*TODO*///	/** VDPVRMP() **********************************************/
/*TODO*///	/** Calculate addr of a pixel in vram                       **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE UINT8 *VDPVRMP(UINT8 M,int X,int Y)
/*TODO*///	{
/*TODO*///	  switch(M)
/*TODO*///	  {
/*TODO*///	    case 0: return VDP_VRMP5(X,Y);
/*TODO*///	    case 1: return VDP_VRMP6(X,Y);
/*TODO*///	    case 2: return VDP_VRMP7(X,Y);
/*TODO*///	    case 3: return VDP_VRMP8(X,Y);
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  return(VRAM);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpoint5() ***********************************************/
/*TODO*///	/** Get a pixel on screen 5                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE UINT8 VDPpoint5(int SX, int SY)
/*TODO*///	{
/*TODO*///	  return (*VDP_VRMP5(SX, SY) >>
/*TODO*///	          (((~SX)&1)<<2)
/*TODO*///	         )&15;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpoint6() ***********************************************/
/*TODO*///	/** Get a pixel on screen 6                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE UINT8 VDPpoint6(int SX, int SY)
/*TODO*///	{
/*TODO*///	  return (*VDP_VRMP6(SX, SY) >>
/*TODO*///	          (((~SX)&3)<<1)
/*TODO*///	         )&3;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpoint7() ***********************************************/
/*TODO*///	/** Get a pixel on screen 7                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE UINT8 VDPpoint7(int SX, int SY)
/*TODO*///	{
/*TODO*///	  return (*VDP_VRMP7(SX, SY) >>
/*TODO*///	          (((~SX)&1)<<2)
/*TODO*///	         )&15;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpoint8() ***********************************************/
/*TODO*///	/** Get a pixel on screen 8                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE UINT8 VDPpoint8(int SX, int SY)
/*TODO*///	{
/*TODO*///	  return *VDP_VRMP8(SX, SY);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpoint() ************************************************/
/*TODO*///	/** Get a pixel on a screen                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE UINT8 VDPpoint(UINT8 SM, int SX, int SY)
/*TODO*///	{
/*TODO*///	  switch(SM)
/*TODO*///	  {
/*TODO*///	    case 0: return VDPpoint5(SX,SY);
/*TODO*///	    case 1: return VDPpoint6(SX,SY);
/*TODO*///	    case 2: return VDPpoint7(SX,SY);
/*TODO*///	    case 3: return VDPpoint8(SX,SY);
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  return(0);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpsetlowlevel() ****************************************/
/*TODO*///	/** Low level function to set a pixel on a screen           **/
/*TODO*///	/** Make it inline to make it fast                          **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE void VDPpsetlowlevel(UINT8 *P, UINT8 CL, UINT8 M, UINT8 OP)
/*TODO*///	{
/*TODO*///	  switch (OP)
/*TODO*///	  {
/*TODO*///	    case 0: *P = (*P & M) | CL; break;
/*TODO*///	    case 1: *P = *P & (CL | M); break;
/*TODO*///	    case 2: *P |= CL; break;
/*TODO*///	    case 3: *P ^= CL; break;
/*TODO*///	    case 4: *P = (*P & M) | ~(CL | M); break;
/*TODO*///	    case 8: if (CL) *P = (*P & M) | CL; break;
/*TODO*///	    case 9: if (CL) *P = *P & (CL | M); break;
/*TODO*///	    case 10: if (CL) *P |= CL; break;
/*TODO*///	    case 11:  if (CL) *P ^= CL; break;
/*TODO*///	    case 12:  if (CL) *P = (*P & M) | ~(CL|M); break;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpset5() ***********************************************/
/*TODO*///	/** Set a pixel on screen 5                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE void VDPpset5(int DX, int DY, UINT8 CL, UINT8 OP)
/*TODO*///	{
/*TODO*///	  register UINT8 SH = ((~DX)&1)<<2;
/*TODO*///	
/*TODO*///	  VDPpsetlowlevel(VDP_VRMP5(DX, DY),
/*TODO*///	                  CL << SH, ~(15<<SH), OP);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpset6() ***********************************************/
/*TODO*///	/** Set a pixel on screen 6                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE void VDPpset6(int DX, int DY, UINT8 CL, UINT8 OP)
/*TODO*///	{
/*TODO*///	  register UINT8 SH = ((~DX)&3)<<1;
/*TODO*///	
/*TODO*///	  VDPpsetlowlevel(VDP_VRMP6(DX, DY),
/*TODO*///	                  CL << SH, ~(3<<SH), OP);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpset7() ***********************************************/
/*TODO*///	/** Set a pixel on screen 7                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE void VDPpset7(int DX, int DY, UINT8 CL, UINT8 OP)
/*TODO*///	{
/*TODO*///	  register UINT8 SH = ((~DX)&1)<<2;
/*TODO*///	
/*TODO*///	  VDPpsetlowlevel(VDP_VRMP7(DX, DY),
/*TODO*///	                  CL << SH, ~(15<<SH), OP);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpset8() ***********************************************/
/*TODO*///	/** Set a pixel on screen 8                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE void VDPpset8(int DX, int DY, UINT8 CL, UINT8 OP)
/*TODO*///	{
/*TODO*///	  VDPpsetlowlevel(VDP_VRMP8(DX, DY),
/*TODO*///	                  CL, 0, OP);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPpset() ************************************************/
/*TODO*///	/** Set a pixel on a screen                                 **/
/*TODO*///	/*************************************************************/
/*TODO*///	INLINE void VDPpset(UINT8 SM, int DX, int DY, UINT8 CL, UINT8 OP)
/*TODO*///	{
/*TODO*///	  switch (SM) {
/*TODO*///	    case 0: VDPpset5(DX, DY, CL, OP); break;
/*TODO*///	    case 1: VDPpset6(DX, DY, CL, OP); break;
/*TODO*///	    case 2: VDPpset7(DX, DY, CL, OP); break;
/*TODO*///	    case 3: VDPpset8(DX, DY, CL, OP); break;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** GetVdpTimingValue() **************************************/
/*TODO*///	/** Get timing value for a certain VDP command              **/
/*TODO*///	/*************************************************************/
/*TODO*///	static int GetVdpTimingValue(register int *timing_values)
/*TODO*///	{
/*TODO*///	  return(timing_values[((VDP[1]>>6)&1)|(VDP[8]&2)|((VDP[9]<<1)&4)]);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** SrchEgine()** ********************************************/
/*TODO*///	/** Search a dot                                            **/
/*TODO*///	/*************************************************************/
/*TODO*///	void SrchEngine(void)
/*TODO*///	{
/*TODO*///	  register int SX=MMC.SX;
/*TODO*///	  register int SY=MMC.SY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int ANX=MMC.ANX;
/*TODO*///	  register UINT8 CL=MMC.CL;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(srch_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	#define pre_srch \
/*TODO*///	    pre_loop \
/*TODO*///	      if ((
/*TODO*///	#define post_srch(MX) \
/*TODO*///	           ==CL) ^ANX) { \
/*TODO*///	      VDPStatus[2]|=0x10; /* Border detected */ \
/*TODO*///	      break; \
/*TODO*///	    } \
/*TODO*///	    if ((SX+=TX) & MX) { \
/*TODO*///	      VDPStatus[2]&=0xEF; /* Border not detected */ \
/*TODO*///	      break; \
/*TODO*///	    } \
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  switch (ScrMode) {
/*TODO*///	    case 5: pre_srch VDPpoint5(SX, SY) post_srch(256)
/*TODO*///	            break;
/*TODO*///	    case 6: pre_srch VDPpoint6(SX, SY) post_srch(512)
/*TODO*///	            break;
/*TODO*///	    case 7: pre_srch VDPpoint7(SX, SY) post_srch(512)
/*TODO*///	            break;
/*TODO*///	    case 8: pre_srch VDPpoint8(SX, SY) post_srch(256)
/*TODO*///	            break;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    /* Update SX in VDP registers */
/*TODO*///	    VDPStatus[8]=SX&0xFF;
/*TODO*///	    VDPStatus[9]=(SX>>8)|0xFE;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.SX=SX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** LineEgine()** ********************************************/
/*TODO*///	/** Draw a line                                             **/
/*TODO*///	/*************************************************************/
/*TODO*///	void LineEngine(void)
/*TODO*///	{
/*TODO*///	  register int DX=MMC.DX;
/*TODO*///	  register int DY=MMC.DY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int TY=MMC.TY;
/*TODO*///	  register int NX=MMC.NX;
/*TODO*///	  register int NY=MMC.NY;
/*TODO*///	  register int ASX=MMC.ASX;
/*TODO*///	  register int ADX=MMC.ADX;
/*TODO*///	  register UINT8 CL=MMC.CL;
/*TODO*///	  register UINT8 LO=MMC.LO;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(line_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	#define post_linexmaj(MX) \
/*TODO*///	      DX+=TX; \
/*TODO*///	      if ((ASX-=NY)<0) { \
/*TODO*///	        ASX+=NX; \
/*TODO*///	        DY+=TY; \
/*TODO*///	      } \
/*TODO*///	      ASX&=1023; /* Mask to 10 bits range */ \
/*TODO*///	      if (ADX++==NX || (DX&MX)) \
/*TODO*///	        break; \
/*TODO*///	    }
/*TODO*///	#define post_lineymaj(MX) \
/*TODO*///	      DY+=TY; \
/*TODO*///	      if ((ASX-=NY)<0) { \
/*TODO*///	        ASX+=NX; \
/*TODO*///	        DX+=TX; \
/*TODO*///	      } \
/*TODO*///	      ASX&=1023; /* Mask to 10 bits range */ \
/*TODO*///	      if (ADX++==NX || (DX&MX)) \
/*TODO*///	        break; \
/*TODO*///	    }
/*TODO*///	
/*TODO*///	  if ((VDP[45]&0x01)==0)
/*TODO*///	    /* X-Axis is major direction */
/*TODO*///	    switch (ScrMode) {
/*TODO*///	      case 5: pre_loop VDPpset5(DX, DY, CL, LO); post_linexmaj(256)
/*TODO*///	              break;
/*TODO*///	      case 6: pre_loop VDPpset6(DX, DY, CL, LO); post_linexmaj(512)
/*TODO*///	              break;
/*TODO*///	      case 7: pre_loop VDPpset7(DX, DY, CL, LO); post_linexmaj(512)
/*TODO*///	              break;
/*TODO*///	      case 8: pre_loop VDPpset8(DX, DY, CL, LO); post_linexmaj(256)
/*TODO*///	              break;
/*TODO*///	    }
/*TODO*///	  else
/*TODO*///	    /* Y-Axis is major direction */
/*TODO*///	    switch (ScrMode) {
/*TODO*///	      case 5: pre_loop VDPpset5(DX, DY, CL, LO); post_lineymaj(256)
/*TODO*///	              break;
/*TODO*///	      case 6: pre_loop VDPpset6(DX, DY, CL, LO); post_lineymaj(512)
/*TODO*///	              break;
/*TODO*///	      case 7: pre_loop VDPpset7(DX, DY, CL, LO); post_lineymaj(512)
/*TODO*///	              break;
/*TODO*///	      case 8: pre_loop VDPpset8(DX, DY, CL, LO); post_lineymaj(256)
/*TODO*///	              break;
/*TODO*///	    }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    VDP[38]=DY & 0xFF;
/*TODO*///	    VDP[39]=(DY>>8) & 0x03;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.DX=DX;
/*TODO*///	    MMC.DY=DY;
/*TODO*///	    MMC.ASX=ASX;
/*TODO*///	    MMC.ADX=ADX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** LmmvEngine() *********************************************/
/*TODO*///	/** VDP . Vram                                             **/
/*TODO*///	/*************************************************************/
/*TODO*///	void LmmvEngine(void)
/*TODO*///	{
/*TODO*///	  register int DX=MMC.DX;
/*TODO*///	  register int DY=MMC.DY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int TY=MMC.TY;
/*TODO*///	  register int NX=MMC.NX;
/*TODO*///	  register int NY=MMC.NY;
/*TODO*///	  register int ADX=MMC.ADX;
/*TODO*///	  register int ANX=MMC.ANX;
/*TODO*///	  register UINT8 CL=MMC.CL;
/*TODO*///	  register UINT8 LO=MMC.LO;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(lmmv_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	  switch (ScrMode) {
/*TODO*///	    case 5: pre_loop VDPpset5(ADX, DY, CL, LO); post__x_y(256)
/*TODO*///	            break;
/*TODO*///	    case 6: pre_loop VDPpset6(ADX, DY, CL, LO); post__x_y(512)
/*TODO*///	            break;
/*TODO*///	    case 7: pre_loop VDPpset7(ADX, DY, CL, LO); post__x_y(512)
/*TODO*///	            break;
/*TODO*///	    case 8: pre_loop VDPpset8(ADX, DY, CL, LO); post__x_y(256)
/*TODO*///	            break;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    if (NY == 0)
/*TODO*///	      DY+=TY;
/*TODO*///	    VDP[38]=DY & 0xFF;
/*TODO*///	    VDP[39]=(DY>>8) & 0x03;
/*TODO*///	    VDP[42]=NY & 0xFF;
/*TODO*///	    VDP[43]=(NY>>8) & 0x03;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.DY=DY;
/*TODO*///	    MMC.NY=NY;
/*TODO*///	    MMC.ANX=ANX;
/*TODO*///	    MMC.ADX=ADX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** LmmmEngine() *********************************************/
/*TODO*///	/** Vram . Vram                                            **/
/*TODO*///	/*************************************************************/
/*TODO*///	void LmmmEngine(void)
/*TODO*///	{
/*TODO*///	  register int SX=MMC.SX;
/*TODO*///	  register int SY=MMC.SY;
/*TODO*///	  register int DX=MMC.DX;
/*TODO*///	  register int DY=MMC.DY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int TY=MMC.TY;
/*TODO*///	  register int NX=MMC.NX;
/*TODO*///	  register int NY=MMC.NY;
/*TODO*///	  register int ASX=MMC.ASX;
/*TODO*///	  register int ADX=MMC.ADX;
/*TODO*///	  register int ANX=MMC.ANX;
/*TODO*///	  register UINT8 LO=MMC.LO;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(lmmm_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	  switch (ScrMode) {
/*TODO*///	    case 5: pre_loop VDPpset5(ADX, DY, VDPpoint5(ASX, SY), LO); post_xxyy(256)
/*TODO*///	            break;
/*TODO*///	    case 6: pre_loop VDPpset6(ADX, DY, VDPpoint6(ASX, SY), LO); post_xxyy(512)
/*TODO*///	            break;
/*TODO*///	    case 7: pre_loop VDPpset7(ADX, DY, VDPpoint7(ASX, SY), LO); post_xxyy(512)
/*TODO*///	            break;
/*TODO*///	    case 8: pre_loop VDPpset8(ADX, DY, VDPpoint8(ASX, SY), LO); post_xxyy(256)
/*TODO*///	            break;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    if (NY == 0) {
/*TODO*///	      SY+=TY;
/*TODO*///	      DY+=TY;
/*TODO*///	    }
/*TODO*///	    else
/*TODO*///	      if (SY==-1)
/*TODO*///	        DY+=TY;
/*TODO*///	    VDP[42]=NY & 0xFF;
/*TODO*///	    VDP[43]=(NY>>8) & 0x03;
/*TODO*///	    VDP[34]=SY & 0xFF;
/*TODO*///	    VDP[35]=(SY>>8) & 0x03;
/*TODO*///	    VDP[38]=DY & 0xFF;
/*TODO*///	    VDP[39]=(DY>>8) & 0x03;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.SY=SY;
/*TODO*///	    MMC.DY=DY;
/*TODO*///	    MMC.NY=NY;
/*TODO*///	    MMC.ANX=ANX;
/*TODO*///	    MMC.ASX=ASX;
/*TODO*///	    MMC.ADX=ADX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** LmcmEngine() *********************************************/
/*TODO*///	/** Vram . CPU                                             **/
/*TODO*///	/*************************************************************/
/*TODO*///	void LmcmEngine()
/*TODO*///	{
/*TODO*///	  if ((VDPStatus[2]&0x80)!=0x80) {
/*TODO*///	
/*TODO*///	    VDPStatus[7]=VDP[44]=VDP_POINT(ScrMode-5, MMC.ASX, MMC.SY);
/*TODO*///	    VdpOpsCnt-=GetVdpTimingValue(lmmv_timing);
/*TODO*///	    VDPStatus[2]|=0x80;
/*TODO*///	
/*TODO*///	    if (!--MMC.ANX || ((MMC.ASX+=MMC.TX)&MMC.MX)) {
/*TODO*///	      if (!(--MMC.NY & 1023) || (MMC.SY+=MMC.TY)==-1) {
/*TODO*///	        VDPStatus[2]&=0xFE;
/*TODO*///	        VdpEngine=0;
/*TODO*///	        if (!MMC.NY)
/*TODO*///	          MMC.DY+=MMC.TY;
/*TODO*///	        VDP[42]=MMC.NY & 0xFF;
/*TODO*///	        VDP[43]=(MMC.NY>>8) & 0x03;
/*TODO*///	        VDP[34]=MMC.SY & 0xFF;
/*TODO*///	        VDP[35]=(MMC.SY>>8) & 0x03;
/*TODO*///	      }
/*TODO*///	      else {
/*TODO*///	        MMC.ASX=MMC.SX;
/*TODO*///	        MMC.ANX=MMC.NX;
/*TODO*///	      }
/*TODO*///	    }
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** LmmcEngine() *********************************************/
/*TODO*///	/** CPU . Vram                                             **/
/*TODO*///	/*************************************************************/
/*TODO*///	void LmmcEngine(void)
/*TODO*///	{
/*TODO*///	  if ((VDPStatus[2]&0x80)!=0x80) {
/*TODO*///	    register UINT8 SM=ScrMode-5;
/*TODO*///	
/*TODO*///	    VDPStatus[7]=VDP[44]&=Mask[SM];
/*TODO*///	    VDP_PSET(SM, MMC.ADX, MMC.DY, VDP[44], MMC.LO);
/*TODO*///	    VdpOpsCnt-=GetVdpTimingValue(lmmv_timing);
/*TODO*///	    VDPStatus[2]|=0x80;
/*TODO*///	
/*TODO*///	    if (!--MMC.ANX || ((MMC.ADX+=MMC.TX)&MMC.MX)) {
/*TODO*///	      if (!(--MMC.NY&1023) || (MMC.DY+=MMC.TY)==-1) {
/*TODO*///	        VDPStatus[2]&=0xFE;
/*TODO*///	        VdpEngine=0;
/*TODO*///	        if (!MMC.NY)
/*TODO*///	          MMC.DY+=MMC.TY;
/*TODO*///	        VDP[42]=MMC.NY & 0xFF;
/*TODO*///	        VDP[43]=(MMC.NY>>8) & 0x03;
/*TODO*///	        VDP[38]=MMC.DY & 0xFF;
/*TODO*///	        VDP[39]=(MMC.DY>>8) & 0x03;
/*TODO*///	      }
/*TODO*///	      else {
/*TODO*///	        MMC.ADX=MMC.DX;
/*TODO*///	        MMC.ANX=MMC.NX;
/*TODO*///	      }
/*TODO*///	    }
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** HmmvEngine() *********************************************/
/*TODO*///	/** VDP -. Vram                                            **/
/*TODO*///	/*************************************************************/
/*TODO*///	void HmmvEngine(void)
/*TODO*///	{
/*TODO*///	  register int DX=MMC.DX;
/*TODO*///	  register int DY=MMC.DY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int TY=MMC.TY;
/*TODO*///	  register int NX=MMC.NX;
/*TODO*///	  register int NY=MMC.NY;
/*TODO*///	  register int ADX=MMC.ADX;
/*TODO*///	  register int ANX=MMC.ANX;
/*TODO*///	  register UINT8 CL=MMC.CL;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(hmmv_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	  switch (ScrMode) {
/*TODO*///	    case 5: pre_loop *VDP_VRMP5(ADX, DY) = CL; post__x_y(256)
/*TODO*///	            break;
/*TODO*///	    case 6: pre_loop *VDP_VRMP6(ADX, DY) = CL; post__x_y(512)
/*TODO*///	            break;
/*TODO*///	    case 7: pre_loop *VDP_VRMP7(ADX, DY) = CL; post__x_y(512)
/*TODO*///	            break;
/*TODO*///	    case 8: pre_loop *VDP_VRMP8(ADX, DY) = CL; post__x_y(256)
/*TODO*///	            break;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    if (NY == 0)
/*TODO*///	      DY+=TY;
/*TODO*///	    VDP[42]=NY & 0xFF;
/*TODO*///	    VDP[43]=(NY>>8) & 0x03;
/*TODO*///	    VDP[38]=DY & 0xFF;
/*TODO*///	    VDP[39]=(DY>>8) & 0x03;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.DY=DY;
/*TODO*///	    MMC.NY=NY;
/*TODO*///	    MMC.ANX=ANX;
/*TODO*///	    MMC.ADX=ADX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** HmmmEngine() *********************************************/
/*TODO*///	/** Vram . Vram                                            **/
/*TODO*///	/*************************************************************/
/*TODO*///	void HmmmEngine(void)
/*TODO*///	{
/*TODO*///	  register int SX=MMC.SX;
/*TODO*///	  register int SY=MMC.SY;
/*TODO*///	  register int DX=MMC.DX;
/*TODO*///	  register int DY=MMC.DY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int TY=MMC.TY;
/*TODO*///	  register int NX=MMC.NX;
/*TODO*///	  register int NY=MMC.NY;
/*TODO*///	  register int ASX=MMC.ASX;
/*TODO*///	  register int ADX=MMC.ADX;
/*TODO*///	  register int ANX=MMC.ANX;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(hmmm_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	  switch (ScrMode) {
/*TODO*///	    case 5: pre_loop *VDP_VRMP5(ADX, DY) = *VDP_VRMP5(ASX, SY); post_xxyy(256)
/*TODO*///	            break;
/*TODO*///	    case 6: pre_loop *VDP_VRMP6(ADX, DY) = *VDP_VRMP6(ASX, SY); post_xxyy(512)
/*TODO*///	            break;
/*TODO*///	    case 7: pre_loop *VDP_VRMP7(ADX, DY) = *VDP_VRMP7(ASX, SY); post_xxyy(512)
/*TODO*///	            break;
/*TODO*///	    case 8: pre_loop *VDP_VRMP8(ADX, DY) = *VDP_VRMP8(ASX, SY); post_xxyy(256)
/*TODO*///	            break;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    if (NY == 0) {
/*TODO*///	      SY+=TY;
/*TODO*///	      DY+=TY;
/*TODO*///	    }
/*TODO*///	    else
/*TODO*///	      if (SY==-1)
/*TODO*///	        DY+=TY;
/*TODO*///	    VDP[42]=NY & 0xFF;
/*TODO*///	    VDP[43]=(NY>>8) & 0x03;
/*TODO*///	    VDP[34]=SY & 0xFF;
/*TODO*///	    VDP[35]=(SY>>8) & 0x03;
/*TODO*///	    VDP[38]=DY & 0xFF;
/*TODO*///	    VDP[39]=(DY>>8) & 0x03;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.SY=SY;
/*TODO*///	    MMC.DY=DY;
/*TODO*///	    MMC.NY=NY;
/*TODO*///	    MMC.ANX=ANX;
/*TODO*///	    MMC.ASX=ASX;
/*TODO*///	    MMC.ADX=ADX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** YmmmEngine() *********************************************/
/*TODO*///	/** Vram . Vram                                            **/
/*TODO*///	/*************************************************************/
/*TODO*///	void YmmmEngine(void)
/*TODO*///	{
/*TODO*///	  register int SY=MMC.SY;
/*TODO*///	  register int DX=MMC.DX;
/*TODO*///	  register int DY=MMC.DY;
/*TODO*///	  register int TX=MMC.TX;
/*TODO*///	  register int TY=MMC.TY;
/*TODO*///	  register int NY=MMC.NY;
/*TODO*///	  register int ADX=MMC.ADX;
/*TODO*///	  register int cnt;
/*TODO*///	  register int delta;
/*TODO*///	
/*TODO*///	  delta = GetVdpTimingValue(ymmm_timing);
/*TODO*///	  cnt = VdpOpsCnt;
/*TODO*///	
/*TODO*///	  switch (ScrMode) {
/*TODO*///	    case 5: pre_loop *VDP_VRMP5(ADX, DY) = *VDP_VRMP5(ADX, SY); post__xyy(256)
/*TODO*///	            break;
/*TODO*///	    case 6: pre_loop *VDP_VRMP6(ADX, DY) = *VDP_VRMP6(ADX, SY); post__xyy(512)
/*TODO*///	            break;
/*TODO*///	    case 7: pre_loop *VDP_VRMP7(ADX, DY) = *VDP_VRMP7(ADX, SY); post__xyy(512)
/*TODO*///	            break;
/*TODO*///	    case 8: pre_loop *VDP_VRMP8(ADX, DY) = *VDP_VRMP8(ADX, SY); post__xyy(256)
/*TODO*///	            break;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  if ((VdpOpsCnt=cnt)>0) {
/*TODO*///	    /* Command execution done */
/*TODO*///	    VDPStatus[2]&=0xFE;
/*TODO*///	    VdpEngine=0;
/*TODO*///	    if (NY == 0) {
/*TODO*///	      SY+=TY;
/*TODO*///	      DY+=TY;
/*TODO*///	    }
/*TODO*///	    else
/*TODO*///	      if (SY==-1)
/*TODO*///	        DY+=TY;
/*TODO*///	    VDP[42]=NY & 0xFF;
/*TODO*///	    VDP[43]=(NY>>8) & 0x03;
/*TODO*///	    VDP[34]=SY & 0xFF;
/*TODO*///	    VDP[35]=(SY>>8) & 0x03;
/*TODO*///	    VDP[38]=DY & 0xFF;
/*TODO*///	    VDP[39]=(DY>>8) & 0x03;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.SY=SY;
/*TODO*///	    MMC.DY=DY;
/*TODO*///	    MMC.NY=NY;
/*TODO*///	    MMC.ADX=ADX;
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** HmmcEngine() *********************************************/
/*TODO*///	/** CPU . Vram                                             **/
/*TODO*///	/*************************************************************/
/*TODO*///	void HmmcEngine(void)
/*TODO*///	{
/*TODO*///	  if ((VDPStatus[2]&0x80)!=0x80) {
/*TODO*///	
/*TODO*///	    *VDP_VRMP(ScrMode-5, MMC.ADX, MMC.DY)=VDP[44];
/*TODO*///	    VdpOpsCnt-=GetVdpTimingValue(hmmv_timing);
/*TODO*///	    VDPStatus[2]|=0x80;
/*TODO*///	
/*TODO*///	    if (!--MMC.ANX || ((MMC.ADX+=MMC.TX)&MMC.MX)) {
/*TODO*///	      if (!(--MMC.NY&1023) || (MMC.DY+=MMC.TY)==-1) {
/*TODO*///	        VDPStatus[2]&=0xFE;
/*TODO*///	        VdpEngine=0;
/*TODO*///	        if (!MMC.NY)
/*TODO*///	          MMC.DY+=MMC.TY;
/*TODO*///	        VDP[42]=MMC.NY & 0xFF;
/*TODO*///	        VDP[43]=(MMC.NY>>8) & 0x03;
/*TODO*///	        VDP[38]=MMC.DY & 0xFF;
/*TODO*///	        VDP[39]=(MMC.DY>>8) & 0x03;
/*TODO*///	      }
/*TODO*///	      else {
/*TODO*///	        MMC.ADX=MMC.DX;
/*TODO*///	        MMC.ANX=MMC.NX;
/*TODO*///	      }
/*TODO*///	    }
/*TODO*///	  }
/*TODO*///	}
/*TODO*///	
	/** VDPWrite() ***********************************************/
	/** Use this function to transfer pixel(s) from CPU to VDP. **/
	/*************************************************************/
	static void v9938_cpu_to_vdp (int V)
	{
/*TODO*///	  VDPStatus[2]&=0x7F;
/*TODO*///	  VDPStatus[7]=VDP[44]=V;
/*TODO*///	  if(VdpEngine&&(VdpOpsCnt>0)) VdpEngine();
	}
/*TODO*///	
/*TODO*///	/** VDPRead() ************************************************/
/*TODO*///	/** Use this function to transfer pixel(s) from VDP to CPU. **/
/*TODO*///	/*************************************************************/
/*TODO*///	static UINT8 v9938_vdp_to_cpu (void)
/*TODO*///	{
/*TODO*///	  VDPStatus[2]&=0x7F;
/*TODO*///	  if(VdpEngine&&(VdpOpsCnt>0)) VdpEngine();
/*TODO*///	  return(VDP[44]);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** ReportVdpCommand() ***************************************/
/*TODO*///	/** Report VDP Command to be executed                       **/
/*TODO*///	/*************************************************************/
/*TODO*///	static void ReportVdpCommand(register UINT8 Op)
/*TODO*///	{
/*TODO*///	  static char *Ops[16] =
/*TODO*///	  {
/*TODO*///	    "SET ","AND ","OR  ","XOR ","NOT ","NOP ","NOP ","NOP ",
/*TODO*///	    "TSET","TAND","TOR ","TXOR","TNOT","NOP ","NOP ","NOP "
/*TODO*///	  };
/*TODO*///	  static char *Commands[16] =
/*TODO*///	  {
/*TODO*///	    " ABRT"," ????"," ????"," ????","POINT"," PSET"," SRCH"," LINE",
/*TODO*///	    " LMMV"," LMMM"," LMCM"," LMMC"," HMMV"," HMMM"," YMMM"," HMMC"
/*TODO*///	  };
/*TODO*///	  register UINT8 CL, CM, LO;
/*TODO*///	  register int SX,SY, DX,DY, NX,NY;
/*TODO*///	
/*TODO*///	  /* Fetch arguments */
/*TODO*///	  CL = VDP[44];
/*TODO*///	  SX = (VDP[32]+((int)VDP[33]<<8)) & 511;
/*TODO*///	  SY = (VDP[34]+((int)VDP[35]<<8)) & 1023;
/*TODO*///	  DX = (VDP[36]+((int)VDP[37]<<8)) & 511;
/*TODO*///	  DY = (VDP[38]+((int)VDP[39]<<8)) & 1023;
/*TODO*///	  NX = (VDP[40]+((int)VDP[41]<<8)) & 1023;
/*TODO*///	  NY = (VDP[42]+((int)VDP[43]<<8)) & 1023;
/*TODO*///	  CM = Op>>4;
/*TODO*///	  LO = Op&0x0F;
/*TODO*///	
/*TODO*///	  logerror ("V9938: Opcode %02Xh %s-%s (%d,%d).(%d,%d),%d [%d,%d]%s\n",
/*TODO*///	         Op, Commands[CM], Ops[LO],
/*TODO*///	         SX,SY, DX,DY, CL, VDP[45]&0x04? -NX:NX,
/*TODO*///	         VDP[45]&0x08? -NY:NY,
/*TODO*///	         VDP[45]&0x70? " on ExtVRAM":""
/*TODO*///	        );
/*TODO*///	}
/*TODO*///	
/*TODO*///	/** VDPDraw() ************************************************/
/*TODO*///	/** Perform a given V9938 operation Op.                     **/
/*TODO*///	/*************************************************************/
	static int v9938_command_unit_w (int Op)
	{
/*TODO*///	  register int SM;
/*TODO*///	
/*TODO*///	  /* V9938 ops only work in SCREENs 5-8 */
/*TODO*///	  if (ScrMode<5)
/*TODO*///	    return(0);
/*TODO*///	
/*TODO*///	  SM = ScrMode-5;         /* Screen mode index 0..3  */
/*TODO*///	
/*TODO*///	  MMC.CM = Op>>4;
/*TODO*///	  if ((MMC.CM & 0x0C) != 0x0C && MMC.CM != 0)
/*TODO*///	    /* Dot operation: use only relevant bits of color */
/*TODO*///	    VDPStatus[7]=(VDP[44]&=Mask[SM]);
/*TODO*///	
/*TODO*///	/*  if(Verbose&0x02) */
/*TODO*///	    ReportVdpCommand(Op);
/*TODO*///	
/*TODO*///	  switch(Op>>4) {
/*TODO*///	    case CM_ABRT:
/*TODO*///	      VDPStatus[2]&=0xFE;
/*TODO*///	      VdpEngine=0;
/*TODO*///	      return 1;
/*TODO*///	    case CM_POINT:
/*TODO*///	      VDPStatus[2]&=0xFE;
/*TODO*///	      VdpEngine=0;
/*TODO*///	      VDPStatus[7]=VDP[44]=
/*TODO*///	                   VDP_POINT(SM, VDP[32]+((int)VDP[33]<<8),
/*TODO*///	                                 VDP[34]+((int)VDP[35]<<8));
/*TODO*///	      return 1;
/*TODO*///	    case CM_PSET:
/*TODO*///	      VDPStatus[2]&=0xFE;
/*TODO*///	      VdpEngine=0;
/*TODO*///	      VDP_PSET(SM,
/*TODO*///	               VDP[36]+((int)VDP[37]<<8),
/*TODO*///	               VDP[38]+((int)VDP[39]<<8),
/*TODO*///	               VDP[44],
/*TODO*///	               Op&0x0F);
/*TODO*///	      return 1;
/*TODO*///	    case CM_SRCH:
/*TODO*///	      VdpEngine=SrchEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_LINE:
/*TODO*///	      VdpEngine=LineEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_LMMV:
/*TODO*///	      VdpEngine=LmmvEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_LMMM:
/*TODO*///	      VdpEngine=LmmmEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_LMCM:
/*TODO*///	      VdpEngine=LmcmEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_LMMC:
/*TODO*///	      VdpEngine=LmmcEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_HMMV:
/*TODO*///	      VdpEngine=HmmvEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_HMMM:
/*TODO*///	      VdpEngine=HmmmEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_YMMM:
/*TODO*///	      VdpEngine=YmmmEngine;
/*TODO*///	      break;
/*TODO*///	    case CM_HMMC:
/*TODO*///	      VdpEngine=HmmcEngine;
/*TODO*///	      break;
/*TODO*///	    default:
/*TODO*///	      logerror("V9938: Unrecognized opcode %02Xh\n",Op);
/*TODO*///	        return(0);
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  /* Fetch unconditional arguments */
/*TODO*///	  MMC.SX = (VDP[32]+((int)VDP[33]<<8)) & 511;
/*TODO*///	  MMC.SY = (VDP[34]+((int)VDP[35]<<8)) & 1023;
/*TODO*///	  MMC.DX = (VDP[36]+((int)VDP[37]<<8)) & 511;
/*TODO*///	  MMC.DY = (VDP[38]+((int)VDP[39]<<8)) & 1023;
/*TODO*///	  MMC.NY = (VDP[42]+((int)VDP[43]<<8)) & 1023;
/*TODO*///	  MMC.TY = VDP[45]&0x08? -1:1;
/*TODO*///	  MMC.MX = PPL[SM];
/*TODO*///	  MMC.CL = VDP[44];
/*TODO*///	  MMC.LO = Op&0x0F;
/*TODO*///	
/*TODO*///	  /* Argument depends on UINT8 or dot operation */
/*TODO*///	  if ((MMC.CM & 0x0C) == 0x0C) {
/*TODO*///	    MMC.TX = VDP[45]&0x04? -PPB[SM]:PPB[SM];
/*TODO*///	    MMC.NX = ((VDP[40]+((int)VDP[41]<<8)) & 1023)/PPB[SM];
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.TX = VDP[45]&0x04? -1:1;
/*TODO*///	    MMC.NX = (VDP[40]+((int)VDP[41]<<8)) & 1023;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  /* X loop variables are treated specially for LINE command */
/*TODO*///	  if (MMC.CM == CM_LINE) {
/*TODO*///	    MMC.ASX=((MMC.NX-1)>>1);
/*TODO*///	    MMC.ADX=0;
/*TODO*///	  }
/*TODO*///	  else {
/*TODO*///	    MMC.ASX = MMC.SX;
/*TODO*///	    MMC.ADX = MMC.DX;
/*TODO*///	  }
/*TODO*///	
/*TODO*///	  /* NX loop variable is treated specially for SRCH command */
/*TODO*///	  if (MMC.CM == CM_SRCH)
/*TODO*///	    MMC.ANX=(VDP[45]&0x02)!=0; /* Do we look for "==" or "!="? */
/*TODO*///	  else
/*TODO*///	    MMC.ANX = MMC.NX;
/*TODO*///	
/*TODO*///	  /* Command execution started */
/*TODO*///	  VDPStatus[2]|=0x01;
/*TODO*///	
/*TODO*///	  /* Start execution if we still have time slices */
/*TODO*///	  if(VdpEngine&&(VdpOpsCnt>0)) VdpEngine();
	
	  /* Operation successfull initiated */
	  return(1);
	}
/*TODO*///	
/*TODO*///	/** LoopVDP() ************************************************/
/*TODO*///	/** Run X steps of active VDP command                       **/
/*TODO*///	/*************************************************************/
/*TODO*///	static void v9938_update_command (void)
/*TODO*///	{
/*TODO*///	  if(VdpOpsCnt<=0)
/*TODO*///	  {
/*TODO*///	    VdpOpsCnt+=13662;
/*TODO*///	    if(VdpEngine&&(VdpOpsCnt>0)) VdpEngine();
/*TODO*///	  }
/*TODO*///	  else
/*TODO*///	  {
/*TODO*///	    VdpOpsCnt=13662;
/*TODO*///	    if(VdpEngine) VdpEngine();
/*TODO*///	  }
/*TODO*///	}
	
}
