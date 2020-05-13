/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.vidhrdw;

import static WIP.mame056.vidhrdw.konamiic.*;
import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static mame056.tilemapH.*;
import static mame056.tilemapC.*;
import static mame056.cpuintrfH.*;
import static mame056.cpuintrf.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.drawgfxH.*;
import static mame056.drawgfx.*;
import static mame056.mame.*;
import static mame056.vidhrdw.generic.*;
import static mame056.palette.*;
import static mame056.memory.*;
import static mame056.memoryH.*;

public class mainevt
{
	
	
	static int[] layer_colorbase=new int[3];
        static int sprite_colorbase=0;
	
	
	
	/***************************************************************************
	
	  Callbacks for the K052109
	
	***************************************************************************/
	
	static K007342_callback mainevt_tile_callback = new K007342_callback() {
            public void handler(int layer, int bank, UBytePtr code, UBytePtr color) {
            
		tile_info.flags = (color.read() & 0x02) != 0 ? TILE_FLIPX : 0;
	
		/* priority relative to HALF priority sprites */
		if (layer == 2) tile_info.priority = (color.read() & 0x20) >> 5;
		else tile_info.priority = 0;
	
		code.write( code.read() | ((color.read() & 0x01) << 8) | ((color.read() & 0x1c) << 7));
		color.write( layer_colorbase[layer] + ((color.read() & 0xc0) >> 6));
            }
        };
	
	static K007342_callback dv_tile_callback = new K007342_callback() {
            public void handler(int layer, int bank, UBytePtr code, UBytePtr color) {
		/* (color & 0x02) is flip y handled internally by the 052109 */
		code.write( code.read() | ((color.read() & 0x01) << 8) | ((color.read() & 0x3c) << 7));
		color.write( layer_colorbase[layer] + ((color.read() & 0xc0) >> 6));
            }
        };
	
	
	/***************************************************************************
	
	  Callbacks for the K051960
	
	***************************************************************************/
	
	static sprite_callback mainevt_sprite_callback = new sprite_callback() {
            public void handler(UBytePtr code, UBytePtr color, UBytePtr priority_mask, UBytePtr shadow) {
            
		/* bit 5 = priority over layer B (has precedence) */
		/* bit 6 = HALF priority over layer B (used for crowd when you get out of the ring) */
		if ((color.read() & 0x20) != 0)		priority_mask.write( 0xff00 );
		else if ((color.read() & 0x40) != 0)	priority_mask.write( 0xff00|0xf0f0 );
		else					priority_mask.write( 0xff00|0xf0f0|0xcccc );
		/* bit 7 is shadow, not used */
	
		color.write( sprite_colorbase + (color.read() & 0x03) );
            }
        };
	
	static sprite_callback dv_sprite_callback = new sprite_callback() {
            public void handler(UBytePtr code, UBytePtr color, UBytePtr priority_mask, UBytePtr shadow) {
		/* TODO: the priority/shadow handling (bits 5-7) seems to be quite complex (see PROM) */
		color.write( sprite_colorbase + (color.read() & 0x07) );
            }
        };
	
	
	/*****************************************************************************/
	
	public static VhStartPtr mainevt_vh_start = new VhStartPtr() { public int handler() 
	{
		layer_colorbase[0] = 0;
		layer_colorbase[1] = 8;
		layer_colorbase[2] = 4;
		sprite_colorbase = 12;
	
		if (K052109_vh_start(REGION_GFX1, 0, 1, 2, 3, mainevt_tile_callback) != 0)
			return 1;
		if (K051960_vh_start(REGION_GFX2, 0, 1, 2, 3,mainevt_sprite_callback) != 0)
		{
			K052109_vh_stop.handler();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStartPtr dv_vh_start = new VhStartPtr() { public int handler() 
	{
		layer_colorbase[0] = 0;
		layer_colorbase[1] = 0;
		layer_colorbase[2] = 4;
		sprite_colorbase = 8;
	
		if (K052109_vh_start(REGION_GFX1,0,1,2,3,dv_tile_callback) != 0)
			return 1;
		if (K051960_vh_start(REGION_GFX2,0,1,2,3,dv_sprite_callback) != 0)
		{
			K052109_vh_stop.handler();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStopPtr mainevt_vh_stop = new VhStopPtr() { public void handler() 
	{
		K052109_vh_stop.handler();
		K051960_vh_stop.handler();
	} };
	
	/*****************************************************************************/
	
	public static VhUpdatePtr mainevt_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
	
		fillbitmap(priority_bitmap,0,null);
		K052109_tilemap_draw(bitmap,1,TILEMAP_IGNORE_TRANSPARENCY,1);
		K052109_tilemap_draw(bitmap,2,1,2);	/* low priority part of layer */
		K052109_tilemap_draw(bitmap,2,0,4);	/* high priority part of layer */
		K052109_tilemap_draw(bitmap,0,0,8);
	
		K051960_sprites_draw(bitmap,-1,-1);
	} };
	
	public static VhUpdatePtr dv_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
	
		K052109_tilemap_draw(bitmap,1,TILEMAP_IGNORE_TRANSPARENCY,0);
		K052109_tilemap_draw(bitmap,2,0,0);
		K051960_sprites_draw(bitmap,0,0);
		K052109_tilemap_draw(bitmap,0,0,0);
	} };
}
