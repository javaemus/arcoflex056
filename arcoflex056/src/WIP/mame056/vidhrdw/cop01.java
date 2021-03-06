/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.vidhrdw;

import static arcadeflex056.fucPtr.*;

import static common.libc.cstring.*;
import static common.ptr.*;
import static common.libc.expressions.*;

import static mame056.drawgfx.*;
import static mame056.drawgfxH.*;
import static mame056.mame.*;
import static mame056.commonH.*;
import static mame056.common.*;
import static mame056.palette.*;
import static mame056.timer.*;
import static mame056.timerH.*;

import static mame056.vidhrdw.generic.*;
import static mame056.usrintrf.usrintf_showmessage;

import static arcadeflex056.osdepend.logerror;
import static mame056.cpuintrfH.ASSERT_LINE;
import static mame056.cpuintrfH.CLEAR_LINE;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.inptport.*;

import static mame056.tilemapC.*;
import static mame056.tilemapH.*;

public class cop01
{
	
	public static UBytePtr cop01_bgvideoram=new UBytePtr(), cop01_fgvideoram=new UBytePtr();
	
	static int[] mightguy_vreg = new int[4];
	static struct_tilemap bg_tilemap, fg_tilemap;
        
        public static int TOTAL_COLORS(int gfxn){
            return (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity);
        }
	
        public static void COLOR(char []colortable, int gfxn, int offs,int  value){ colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs] = (char) value; }
	
	public static VhConvertColorPromPtr cop01_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
                int _palette=0;
		
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3;
	
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom.read(Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(Machine.drv.total_colors)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			bit0 = (color_prom.read(2*Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(2*Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(2*Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(2*Machine.drv.total_colors)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
	
			color_prom.inc();
		}
	
		color_prom.inc( 2*Machine.drv.total_colors );
		/* color_prom now points to the beginning of the lookup tables */
	
		/* characters use colors 0-15 (or 0-127, but the eight rows are identical) */
		for (i = 0;i < TOTAL_COLORS(0);i++)
			COLOR(colortable, 0,i, i);
	
		/* background tiles use colors 192-255 */
		/* I don't know how much of the lookup table PROM is hooked up, */
		/* I'm only using the first 32 bytes because the rest is empty. */
		for (i = 0;i < TOTAL_COLORS(1);i++)
			COLOR(colortable, 1,i, 0xc0 + (i & 0x30) + (color_prom.read(((i & 0x40) >> 2) + (i & 0x0f))& 0x0f));
		color_prom.inc( 256 );
	
		/* sprites use colors 128-143 (or 128-191, but the four rows are identical) */
		for (i = 0;i < TOTAL_COLORS(2);i++)
			COLOR(colortable, 2,i, 0x80 + (color_prom.readinc() & 0x0f));
	} };
	
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
            public void handler(int tile_index) {
                int tile = cop01_bgvideoram.read(tile_index);
		int attr = cop01_bgvideoram.read(tile_index+0x800);
		int pri  = (attr & 0x80) >> 7;
	
		/* kludge: priority is not actually pen based, but color based. Since the
		 * game uses a lookup table, the two are not the same thing.
		 * Palette entries with bits 2&3 set have priority over sprites.
		 * tilemap.c can't handle that yet, so I'm cheating, because I know that
		 * color codes using the second row of the lookup table don't use palette
		 * entries 12-15.
		 * The only place where this has any effect is the beach at the bottom of
		 * the screen right at the beginning of mightguy. cop01 doesn't seem to
		 * use priority at all.
		 */
		if ((attr & 0x10) != 0) pri = 0;
	
		SET_TILE_INFO(1,tile + ((attr & 0x03) << 8),(attr & 0x1c) >> 2,TILE_SPLIT(pri));
            }
        };
	
	static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
            public void handler(int tile_index) {
		int tile = cop01_fgvideoram.read(tile_index);
		SET_TILE_INFO(0,tile,0,0);
            }
        };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr cop01_vh_start = new VhStartPtr() { public int handler() 
	{
		bg_tilemap = tilemap_create(get_bg_tile_info,tilemap_scan_rows,TILEMAP_SPLIT,      8,8,64,32);
		fg_tilemap = tilemap_create(get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,32,32);
	
		if (bg_tilemap==null || fg_tilemap==null)
			return 1;
	
		tilemap_set_transparent_pen(fg_tilemap,15);
	
		/* priority doesn't exactly work this way, see above */
		tilemap_set_transmask(bg_tilemap,0,0xffff,0x0000); /* split type 0 is totally transparent in front half */
		tilemap_set_transmask(bg_tilemap,1,0x0fff,0xf000); /* split type 1 has pens 0-11 transparent in front half */
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr cop01_background_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (cop01_bgvideoram.read(offset) != data)
		{
			cop01_bgvideoram.write(offset, data);
			tilemap_mark_tile_dirty(bg_tilemap,offset & 0x7ff);
		}
	} };
	
	public static WriteHandlerPtr cop01_foreground_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (cop01_fgvideoram.read(offset) != data)
		{
			cop01_fgvideoram.write(offset, data);
			tilemap_mark_tile_dirty(fg_tilemap,offset);
		}
	} };
	
	public static WriteHandlerPtr cop01_vreg_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/*	0x40: --xx---- sprite bank, coin counters, flip screen
		 *	      -----x-- flip screen
		 *	      ------xx coin counters
		 *	0x41: xxxxxxxx xscroll
		 *	0x42: ---xx--- ? matches the bg tile color most of the time, but not
		 *                 during level transitions. Maybe sprite palette bank?
		 *                 (the four banks in the PROM are identical)
		 *	      ------x- unused (xscroll overflow)
		 *	      -------x msb xscroll
		 *	0x43: xxxxxxxx yscroll
		 */
		mightguy_vreg[offset] = data;
	
		if (offset == 0)
		{
			coin_counter_w.handler(0,data & 1);
			coin_counter_w.handler(1,data & 2);
			flip_screen_set(data & 4);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites( mame_bitmap bitmap )
	{
		int offs,code,attr,sx,sy,flipx,flipy,color;
	
		for (offs = 0;offs < spriteram_size[0];offs += 4)
		{
			code = spriteram.read(offs+1);
			attr = spriteram.read(offs+2);
			/* xxxx----	color
			 * ----xx--	flipy,flipx
			 * -------x msbx
			 */
			color = attr>>4;
			flipx = attr & 0x04;
			flipy = attr & 0x08;
	
			sx = (spriteram.read(offs+3)- 0x80) + 256 * (attr & 0x01);
			sy = 240 - spriteram.read(offs);
	
			if (flip_screen() != 0)
			{
				sx = 240 - sx;
				sy = 240 - sy;
				flipx = flipx!=0?0:1;
				flipy = flipy!=0?0:1;
			}
	
			if ((code&0x80) != 0)
				code += (mightguy_vreg[0]&0x30)<<3;
	
			drawgfx(bitmap,Machine.gfx[2],
				code,
				color,
				flipx,flipy,
				sx,sy,
				Machine.visible_area,TRANSPARENCY_PEN,0 );
		}
	}
	
	
	public static VhUpdatePtr cop01_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
		tilemap_set_scrollx(bg_tilemap,0,mightguy_vreg[1] + 256 * (mightguy_vreg[2] & 1));
		tilemap_set_scrolly(bg_tilemap,0,mightguy_vreg[3]);
	
		tilemap_draw(bitmap,bg_tilemap,TILEMAP_BACK,0);
		draw_sprites(bitmap);
		tilemap_draw(bitmap,bg_tilemap,TILEMAP_FRONT,0);
		tilemap_draw(bitmap,fg_tilemap,0,0 );
	} };
}
