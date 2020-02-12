/***************************************************************************

  gb.c

  Video file to handle emulation of the Nintendo GameBoy.

  Original code                               Carsten Sorensen   1998
  Mess modifications, bug fixes and speedups  Hans de Goede      1998

***************************************************************************/
/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.vidhrdw;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static common.subArrays.*;
import static mame056.commonH.*;
import static mame056.drawgfx.*;
import static mame056.drawgfxH.*;
import static mame056.mame.*;
import static mame056.vidhrdw.generic.*;
import static mess056.machine.gb.*;
import static mess056.machine.gbH.*;

public class gb
{
	
	static char[] bg_zbuf = new char[160];
        /*static{
            for (int _i=0 ; _i<160 ; _i++)
                bg_zbuf[_i] = 0;
        }*/
	
	public static void gb_update_sprites ()
	{
		mame_bitmap bitmap = Machine.scrbitmap;
                int height, tilemask, line;
                UBytePtr oam;
                int i, yindex;

                //if ((LCDCONT() & 0x04) != 0)
                {
                        height = 16;
                        tilemask = 0xFE;
                }
                /*else*/
                /*{
                        height = 8;
                        tilemask = 0xFF;
                }*/

                yindex = CURLINE();
                line = CURLINE() + 16;

                oam = new UBytePtr(gb_ram, OAM + 39 * 4);
                for (i = 39; i >= 0; i--)
                {
                        /* if sprite is on current line && x-coordinate && x-coordinate is < 168 */
                        if (line >= oam.read(0) && line < (oam.read(0) + height) && oam.read(1)!=0 && oam.read(1) < 168)
                        {
                                int data;
                                int[] spal;
                                int bit;
                                int xindex;

                                spal = (oam.read(3) & 0x10)!=0 ? gb_spal1 : gb_spal0;
                                xindex = oam.read(1) - 8;
                                if ((oam.read(3) & 0x40)!=0)		   /* flip y ? */
                                {
                                        data = gb_ram.read(VRAM + (oam.read(2) & tilemask) * 16 + (height - 1 - line + oam.read(0)) * 2);
                                }
                                else
                                {
                                        data = gb_ram.read(VRAM + (oam.read(2) & tilemask) * 16 + (line - oam.read(0)) * 2);
                                }
/*TODO*///        #ifndef LSB_FIRST
                                data = (data << 8) | (data >> 8);
/*TODO*///        #endif

                                switch (oam.read(3) & 0xA0)
                                {
                                case 0xA0:
                                        for (bit = 0; bit < 8; bit++, xindex++)
                                        {
                                                int colour = ((data & 0x0100)!=0 ? 2 : 0) | ((data & 0x0001)!=0 ? 1 : 0);
                                                if (colour!=0 && bg_zbuf[xindex]==0)
                                                        plot_pixel.handler(bitmap, xindex, yindex, spal[colour]);
                                                data >>= 1;
                                        }
                                        break;
                                case 0x20:				   
                                        for (bit = 0; bit < 8; bit++, xindex++)
                                        {
                                                int colour = ((data & 0x0100)!=0 ? 2 : 0) | ((data & 0x0001)!=0 ? 1 : 0);
                            if (colour != 0)
                                                        plot_pixel.handler(bitmap, xindex, yindex, spal[colour]);
                                                data >>= 1;
                                        }
                                        break;
                                case 0x80:				   
                                        for (bit = 0; bit < 8; bit++, xindex++)
                                        {
                                                int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                                if (colour!=0 && bg_zbuf[xindex]==0)
                                                        plot_pixel.handler(bitmap, xindex, yindex, spal[colour]);
                                                data <<= 1;
                                        }
                                        break;
                                case 0x00:				   
                                        for (bit = 0; bit < 8; bit++, xindex++)
                                        {
                                                int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                                if (colour != 0)
                                                        plot_pixel.handler(bitmap, xindex, yindex, spal[colour]);
                                                data <<= 1;
                                        }
                                        break;
                                }
                                /*int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                                if (colour!=0)
                                                        plot_pixel.handler(bitmap, xindex, yindex, spal[colour]);
                                                data <<= 1;*/
                        }
                        oam.dec( 4 );
                }
	}
	
	public static class layer_struct
	{
		public int enabled;
                public UShortPtr bg_tiles;
                public UBytePtr bg_map;
                public char[] bg_pal;
                public int xindex;
                public int xshift;
                public int xstart;
                public int xend;
	};
        
        //static int last_line = -1;
	
	public static void gb_refresh_scanline ()
	{
		mame_bitmap bitmap = Machine.scrbitmap;
                //UBytePtr zbuf = new UBytePtr(bg_zbuf);
                
                int l = 0, yindex = CURLINE();

                /* layer info layer[0]=background, layer[1]=window */
                layer_struct[] layer = new layer_struct[2];
                
                for (int _i=0 ; _i<2 ; _i++)
                    layer[_i] = new layer_struct();

                /* if background or screen disabled clear line */
                if ((LCDCONT() & 0x81) != 0x81)
                {
                        rectangle r = new rectangle(Machine.visible_area);
                        r.min_y = r.max_y = yindex;
                        fillbitmap(bitmap, Machine.pens[0], r);
                }

                /* if lcd disabled return */
                if ((LCDCONT() & 0x80)==0)
                        return;

                /* Window is enabled if the hardware says so AND the current scanline is
                 * within the window AND the window X coordinate is <=166 */
                layer[1].enabled = ((LCDCONT() & 0x20)!=0 && CURLINE() >= WNDPOSY() && WNDPOSX() <= 166) ? 1 : 0;

                /* BG is enabled if the hardware says so AND (window_off OR (window_on
                 * AND window's X position is >=7 ) ) */
                layer[0].enabled = ((LCDCONT() & 0x01)!=0 && ((layer[1].enabled==0) || (layer[1].enabled!=0 && WNDPOSX() >= 7))) ? 1 : 0;

                if (layer[0].enabled != 0)
                {
                        int bgline;

                        bgline = (SCROLLY() + CURLINE()) & 0xFF;

                        layer[0].bg_map = new UBytePtr(gb_bgdtab);
                        /*if (layer[0].bg_map.offset>=layer[0].bg_map.memory.length)
                            layer[0].bg_map.offset = 0;*/
                        layer[0].bg_map.inc( (bgline << 2) & 0x3E0 );
                        layer[0].bg_tiles = new UShortPtr( gb_chrgen, (bgline & 7) );
                        layer[0].bg_pal = gb_bpal;
                        layer[0].xindex = SCROLLX()>> 3;
                        layer[0].xshift = SCROLLX() & 7;
                        layer[0].xstart = 0;
                        layer[0].xend = 160;
                }

                if (layer[1].enabled != 0)
                {
                        int bgline, xpos;

                        bgline = (CURLINE() - WNDPOSY()) & 0xFF;
                        xpos = WNDPOSX() - 7;		/* Window is offset by 7 pixels */
                        if (xpos < 0)
                                xpos = 0;

                        layer[1].bg_map = new UBytePtr(gb_wndtab);
                        layer[1].bg_map.inc( (bgline << 2) & 0x3E0 );
                        layer[1].bg_tiles = new UShortPtr(gb_chrgen, (bgline & 7));
                        layer[1].bg_pal = gb_wpal;
                        layer[1].xindex = 0;
                        layer[1].xshift = 0;
                        layer[1].xstart = xpos;
                        layer[1].xend = 160 - xpos;
                        layer[0].xend = xpos;
                }

                while (l < 2)
                {
                        /*
                         * BG display on
                         */
                        UBytePtr map;
                        int xidx, bit, i;
                        UShortPtr tiles;
                        int data;
                        int xindex;

                        if (layer[l].enabled == 0)
                        {
                                l++;
                                continue;
                        }

                        map = new UBytePtr(layer[l].bg_map);
                        //map.offset=0;
                        tiles = new UShortPtr(layer[l].bg_tiles);
                        xidx = layer[l].xindex;
                        bit = layer[l].xshift;
                        i = layer[l].xend;

                        data = (tiles.read(((map.read(xidx) ^ gb_tile_no_mod) * 8)&0xffff) << bit);

/*TODO*///        #ifndef LSB_FIRST
                        data = ((data << 8) | (data >> 8)) & 0xffff;
/*TODO*///        #endif
                        xindex = layer[l].xstart;
                        while (i != 0)
                        {
                                while ((bit < 8) && i!=0)
                                {
                                        int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                        plot_pixel.handler(bitmap, xindex, yindex, layer[l].bg_pal[colour]);
        /*				plot_pixel(bitmap, xindex, yindex, gb_bpal[colour]); */
                                        bg_zbuf[xindex] = (char) colour;
                                        xindex++;
                                        //zbuf.writeinc( colour );
                                        
                                        data <<= 1;
                                        bit++;
                                        i--;
                                }
                                xidx = (xidx + 1) & 31;
                                bit = 0;
                                data = tiles.read((map.read(xidx) ^ gb_tile_no_mod) * 8);
                        }
                        l++;
                }

                if ((LCDCONT() & 0x02) != 0)
                        gb_update_sprites();
	}
	
	public static VhStartPtr gb_vh_start = new VhStartPtr() {
            public int handler() {
                if( generic_bitmapped_vh_start.handler() != 0 )
			return 1;
		return 0;
            }
        };
	
	public static VhStopPtr gb_vh_stop = new VhStopPtr() {
            public void handler() {
                generic_vh_stop.handler();
            }
        };
	
	public static VhUpdatePtr gb_vh_screen_refresh = new VhUpdatePtr() {
            public void handler(mame_bitmap bitmap, int full_refresh) {
                // Nothing to do
            }
        };
	
}

