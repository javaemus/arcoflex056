/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.vidhrdw;

import static arcadeflex056.fucPtr.*;
import static arcadeflex056.osdepend.logerror;
import static common.ptr.*;
import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.drawgfx.*;
import static mame056.drawgfxH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mame056.mame.Machine;
import static mame056.palette.*;
import static mame056.tilemapH.*;
import static mame056.tilemapC.*;
import static mame056.vidhrdw.generic.*;

public class spdodgeb
{
	
	
	public static UBytePtr spdodgeb_videoram = new UBytePtr();
	
	static int tile_palbank;
	static int sprite_palbank;
	static int[] scrollx = new int[30];
	
	static struct_tilemap bg_tilemap;
	
	
	
	public static VhConvertColorPromPtr spdodgeb_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
                int _palette = 0;
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3;
	
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			/* green component */
			bit0 = (color_prom.read(0)>> 4) & 0x01;
			bit1 = (color_prom.read(0)>> 5) & 0x01;
			bit2 = (color_prom.read(0)>> 6) & 0x01;
			bit3 = (color_prom.read(0)>> 7) & 0x01;
			palette[_palette++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
			/* blue component */
			bit0 = (color_prom.read(Machine.drv.total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(Machine.drv.total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(Machine.drv.total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(Machine.drv.total_colors)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
	
			color_prom.inc();
		}
	} };
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static GetMemoryOffsetPtr background_scan = new GetMemoryOffsetPtr() {
            public int handler(int col, int row, int num_cols, int num_rows) {
                /* logical (col,row) . memory offset */
		return (col & 0x1f) + ((row & 0x1f) << 5) + ((col & 0x20) << 5);
            }
        };
	
	static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
            public void handler(int tile_index) {
                
		int code = spdodgeb_videoram.read(tile_index);
		int attr = spdodgeb_videoram.read(tile_index + 0x800);
		SET_TILE_INFO(
				0,
				code + ((attr & 0x1f) << 8),
				((attr & 0xe0) >> 5) + 8 * tile_palbank,
				0);
            }
        };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr spdodgeb_vh_start = new VhStartPtr() { public int handler() 
	{
		bg_tilemap = tilemap_create(get_bg_tile_info,background_scan,TILEMAP_OPAQUE,8,8,64,32);
	
		if (bg_tilemap == null)
			return 1;
	
		tilemap_set_scroll_rows(bg_tilemap,32);
	
		return 0;
	} };
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	static int lastscroll;
	
	public static InterruptPtr spdodgeb_interrupt = new InterruptPtr() { public int handler() 
	{
		int line = 33 - cpu_getiloops();
	
		if (line < 30)
		{
			scrollx[line] = lastscroll;
			return M6502_INT_IRQ;
		}
		else if (line == 30)	/* vblank */
			return M6502_INT_NMI;
		else 	/* skip 31 32 33 to allow vblank to finish */
			return ignore_interrupt.handler();
	} };
	
	public static WriteHandlerPtr spdodgeb_scrollx_lo_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		lastscroll = (lastscroll & 0x100) | data;
	} };
	
	public static WriteHandlerPtr spdodgeb_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UBytePtr rom = new UBytePtr(memory_region(REGION_CPU1));
	
		/* bit 0 = flip screen */
		flip_screen_set(data & 0x01);
	
		/* bit 1 = ROM bank switch */
		cpu_setbank(1, new UBytePtr(rom, 0x10000 + 0x4000 * ((~data & 0x02) >> 1)));
	
		/* bit 2 = scroll high bit */
		lastscroll = (lastscroll & 0x0ff) | ((data & 0x04) << 6);
	
		/* bit 3 = to mcu?? */
	
		/* bits 4-7 = palette bank select */
		if (tile_palbank != ((data & 0x30) >> 4))
		{
			tile_palbank = ((data & 0x30) >> 4);
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		}
		sprite_palbank = (data & 0xc0) >> 6;
	} };
	
	public static WriteHandlerPtr spdodgeb_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (spdodgeb_videoram.read(offset) != data)
		{
			spdodgeb_videoram.write(offset, data);
			tilemap_mark_tile_dirty(bg_tilemap,offset & 0x7ff);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	public static void DRAW_SPRITE( int order, int sx, int sy, mame_bitmap bitmap, GfxElement gfx, int which, int color, int flipx, int flipy, rectangle clip ){
            drawgfx( bitmap, gfx,
                    (which+order),color+ 8 * sprite_palbank,flipx,flipy,sx,sy,
						clip,TRANSPARENCY_PEN,0);
        }
	
	static void draw_sprites( mame_bitmap bitmap )
	{
		rectangle clip = Machine.visible_area;
		GfxElement gfx = Machine.gfx[1];
		UBytePtr src;
		int i;
	
		src = new UBytePtr(spriteram);
	
	/*	240-Y    S|X|CLR|WCH WHICH    240-X
		xxxxxxxx x|x|xxx|xxx xxxxxxxx xxxxxxxx
	*/
	
	
		for (i = 0;i < spriteram_size[0];i += 4)
		{
			int attr = src.read(i+1);
			int which = src.read(i+2)+((attr & 0x07)<<8);
			int sx = ((src.read(i+3) + 8) & 0xff) - 8;
			int sy = 240 - src.read(i);
			int size = (attr & 0x80) >> 7;
			int color = (attr & 0x38) >> 3;
			int flipx = ~attr & 0x40;
			int flipy = 0;
			int dy = -16;
	
			if (flip_screen() != 0)
			{
				sx = 240 - sx;
				sy = 240 - sy;
				flipx = flipx!=0?0:1;
				flipy = flipy!=0?0:1;
				dy = -dy;
			}
	
			switch (size)
			{
				case 0: /* normal */
				DRAW_SPRITE(0,sx,sy, bitmap, gfx, which, color, flipx, flipy, clip );
				break;
	
				case 1: /* double y */
				DRAW_SPRITE(0,sx,sy + dy, bitmap, gfx, which, color, flipx, flipy, clip );
				DRAW_SPRITE(1,sx,sy, bitmap, gfx, which, color, flipx, flipy, clip );
				break;
			}
		}
	}
	
	
	public static VhUpdatePtr spdodgeb_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
		int i;
	
	
		if (flip_screen() != 0)
		{
			for (i = 0;i < 30;i++)
				tilemap_set_scrollx(bg_tilemap,i+1,scrollx[29 - i]+5);
		}
		else
		{
			for (i = 0;i < 30;i++)
				tilemap_set_scrollx(bg_tilemap,i+1,scrollx[i]+5);
		}
	
		tilemap_draw(bitmap,bg_tilemap,0,0);
		draw_sprites(bitmap);
	} };
}