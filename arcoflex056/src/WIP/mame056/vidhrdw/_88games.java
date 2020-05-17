/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.vidhrdw;

import static WIP.mame056.vidhrdw.konamiic.*;
import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static mame056.commonH.*;
import static mame056.tilemapC.*;
import static mame056.tilemapH.*;

public class _88games
{
	
	
	public static int k88games_priority;
	
	static int[] layer_colorbase=new int[3];
        static int sprite_colorbase,zoom_colorbase;
	
	
	
	/***************************************************************************
	
	  Callbacks for the K052109
	
	***************************************************************************/
	
	static K007342_callback tile_callback = new K007342_callback() {
            public void handler(int layer, int bank, UBytePtr code, UBytePtr color) {
		code.write( code.read() | ((color.read() & 0x0f) << 8) | (bank << 12));
		color.write( layer_colorbase[layer] + ((color.read() & 0xf0) >> 4) );
            }
        };
	
	/***************************************************************************
	
	  Callbacks for the K051960
	
	***************************************************************************/
	
	static sprite_callback _sprite_callback = new sprite_callback() {
            public void handler(UBytePtr code, UBytePtr color, UBytePtr priority, UBytePtr shadow) {
		priority.write( (color.read() & 0x20) >> 5);	/* ??? */
		color.write( sprite_colorbase + (color.read() & 0x0f) );
            }
        };
	
	
	/***************************************************************************
	
	  Callbacks for the K051316
	
	***************************************************************************/
	
	static sprite_callback zoom_callback = new sprite_callback() {
            public void handler(UBytePtr code, UBytePtr color, UBytePtr _pri, UBytePtr _shadow) {
            
		tile_info.flags = (color.read() & 0x40)!=0 ? TILE_FLIPX : 0;
		code.write( code.read() | ((color.read() & 0x07) << 8) );
		color.write( zoom_colorbase + ((color.read() & 0x38) >> 3) + ((color.read() & 0x80) >> 4) );
            }
        };
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr k88games_vh_start = new VhStartPtr() { public int handler() 
	{
		layer_colorbase[0] = 64;
		layer_colorbase[1] = 0;
		layer_colorbase[2] = 16;
		sprite_colorbase = 32;
		zoom_colorbase = 48;
		if (K052109_vh_start(REGION_GFX1,0, 1, 2, 3,tile_callback) != 0)
		{
			return 1;
		}
		if (K051960_vh_start(REGION_GFX2,0, 1, 2, 3,_sprite_callback) != 0)
		{
			K052109_vh_stop.handler();
			return 1;
		}
		if (K051316_vh_start_0(REGION_GFX3,4,TILEMAP_TRANSPARENT,0,zoom_callback) != 0)
		{
			K052109_vh_stop.handler();
			K051960_vh_stop.handler();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStopPtr k88games_vh_stop = new VhStopPtr() { public void handler() 
	{
		K052109_vh_stop.handler();
		K051960_vh_stop.handler();
		K051316_vh_stop_0.handler();
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	public static VhUpdatePtr k88games_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
	
		if (k88games_priority != 0)
		{
			K052109_tilemap_draw(bitmap,0,TILEMAP_IGNORE_TRANSPARENCY,0);
			K051960_sprites_draw(bitmap,1,1);
			K052109_tilemap_draw(bitmap,2,0,0);
			K052109_tilemap_draw(bitmap,1,0,0);
			K051960_sprites_draw(bitmap,0,0);
			K051316_zoom_draw_0(bitmap,0,0);
		}
		else
		{
			K052109_tilemap_draw(bitmap,2,TILEMAP_IGNORE_TRANSPARENCY,0);
			K051316_zoom_draw_0(bitmap,0,0);
			K051960_sprites_draw(bitmap,0,0);
			K052109_tilemap_draw(bitmap,1,0,0);
			K051960_sprites_draw(bitmap,1,1);
			K052109_tilemap_draw(bitmap,0,0,0);
		}
	} };
}
