/***************************************************************************

 Pang Video Hardware

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package WIP.mame056.vidhrdw;

import static arcadeflex056.fucPtr.*;

import static common.ptr.*;
import static common.subArrays.*;
import static common.libc.cstring.*;
import static common.libc.cstdio.*;

import static mame056.common.*;
import static mame056.palette.*;
import static mame056.drawgfx.*;
import static mame056.mame.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrf.*;
import static mame056.cpuintrfH.*;
import static mame056.commonH.*;
import static mame056.drawgfxH.*;
import static mame056.inptport.*;
import static mame056.sound.mixer.*;
import static mame056.vidhrdw.generic.*;

import static mame056.timer.*;
import static mame056.timerH.*;

import static arcadeflex056.osdepend.logerror;
import static mame056.tilemapH.*;
import static mame056.tilemapC.*;
import static mame056.sound.oki6295.*;
import static mame056.sound.oki6295H.*;

public class mitchell
{
	
	
	/* Globals */
	public static int[] pang_videoram_size = new int[2];
	public static UBytePtr pang_videoram = new UBytePtr();
	public static UBytePtr pang_colorram = new UBytePtr();
	
	/* Private */
	public static UBytePtr pang_objram = new UBytePtr();           /* Sprite RAM */
	
	static struct_tilemap bg_tilemap;
	static int flipscreen;
	
	/* Declarations */
	
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoPtr get_tile_info = new GetTileInfoPtr() {
            public void handler(int tile_index) {
                int attr = pang_colorram.read(tile_index);
		int code = pang_videoram.read(2*tile_index) + (pang_videoram.read(2*tile_index+1) << 8);
		SET_TILE_INFO(
				0,
				code,
				attr & 0x7f,
				(attr & 0x80)!=0 ? TILE_FLIPX : 0);
            }
        };
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr pang_vh_start = new VhStartPtr() { public int handler() 
	{
		pang_objram=null;
		paletteram=null;
	
	
		bg_tilemap = tilemap_create(get_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,8,8,64,32);
	
		if (bg_tilemap == null)
			return 1;
	
		tilemap_set_transparent_pen(bg_tilemap,15);
	
		/*
			OBJ RAM
		*/
		pang_objram=new UBytePtr(pang_videoram_size[0]);
		if (pang_objram == null)
		{
			pang_vh_stop.handler();
			return 1;
		}
		memset(pang_objram, 0, pang_videoram_size[0]);
	
		/*
			Palette RAM
		*/
		paletteram = new UBytePtr(2*Machine.drv.total_colors);
		if (paletteram == null)
		{
			pang_vh_stop.handler();
			return 1;
		}
		memset(paletteram, 0, 2*Machine.drv.total_colors);
	
		return 0;
	} };
	
	public static VhStopPtr pang_vh_stop = new VhStopPtr() { public void handler() 
	{
		pang_objram = null;
		paletteram = null;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	/***************************************************************************
	  OBJ / CHAR RAM HANDLERS (BANK 0 = CHAR, BANK 1=OBJ)
	***************************************************************************/
	
	static int video_bank;
	
	public static WriteHandlerPtr pang_video_bank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		/* Bank handler (sets base pointers for video write) (doesn't apply to mgakuen) */
		video_bank = data;
	} };
	
	public static WriteHandlerPtr mgakuen_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (pang_videoram.read(offset) != data)
		{
			pang_videoram.write(offset, data);
			tilemap_mark_tile_dirty(bg_tilemap,offset/2);
		}
	} };
	
	public static ReadHandlerPtr mgakuen_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return pang_videoram.read(offset);
	} };
	
	public static WriteHandlerPtr mgakuen_objram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		pang_objram.write(offset, data);
	} };
	
	public static ReadHandlerPtr mgakuen_objram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return pang_objram.read(offset);
	} };
	
	public static WriteHandlerPtr pang_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (video_bank != 0) mgakuen_objram_w.handler(offset,data);
		else mgakuen_videoram_w.handler(offset,data);
	} };
	
	public static ReadHandlerPtr pang_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (video_bank != 0) return mgakuen_objram_r.handler(offset);
		else return mgakuen_videoram_r.handler(offset);
	} };
	
	/***************************************************************************
	  COLOUR RAM
	****************************************************************************/
	
	public static WriteHandlerPtr pang_colorram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (pang_colorram.read(offset) != data)
		{
			pang_colorram.write(offset, data);
			tilemap_mark_tile_dirty(bg_tilemap,offset);
		}
	} };
	
	public static ReadHandlerPtr pang_colorram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return pang_colorram.read(offset);
	} };
	
	/***************************************************************************
	  PALETTE HANDLERS (COLOURS: BANK 0 = 0x00-0x3f BANK 1=0x40-0xff)
	****************************************************************************/
	
	static int paletteram_bank;
	
	public static WriteHandlerPtr pang_gfxctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	logerror("PC %04x: pang_gfxctrl_w %02x\n",cpu_get_pc(),data);
	{
		String baf;
		baf = sprintf("%02x",data);
	//	usrintf_showmessage(baf);
	}
	
		/* bit 0 is unknown (used, maybe back color enable?) */
	
		/* bit 1 is coin counter */
		coin_counter_w.handler(0,data & 2);
	
		/* bit 2 is flip screen */
		if (flipscreen != (data & 0x04))
		{
			flipscreen = data & 0x04;
			tilemap_set_flip(ALL_TILEMAPS,flipscreen!=0 ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);
		}
	
		/* bit 3 is unknown (used, e.g. marukin pulses it on the title screen) */
	
		/* bit 4 selects OKI M6295 bank */
		OKIM6295_set_bank_base(0, (data & 0x10)!=0 ? 0x40000 : 0x00000);
	
		/* bit 5 is palette RAM bank selector (doesn't apply to mgakuen) */
		paletteram_bank = data & 0x20;
	
		/* bits 6 and 7 are unknown, used in several places. At first I thought */
		/* they were bg and sprites enable, but this screws up spang (screen flickers */
		/* every time you pop a bubble). However, not using them as enable bits screws */
		/* up marukin - you can see partially built up screens during attract mode. */
	} };
	
	public static WriteHandlerPtr pang_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (paletteram_bank != 0) paletteram_xxxxRRRRGGGGBBBB_w.handler(offset + 0x800,data);
		else paletteram_xxxxRRRRGGGGBBBB_w.handler(offset,data);
	} };
	
	public static ReadHandlerPtr pang_paletteram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		if (paletteram_bank != 0) return paletteram_r.handler(offset + 0x800);
		return paletteram_r.handler(offset);
	} };
	
	public static WriteHandlerPtr mgakuen_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		paletteram_xxxxRRRRGGGGBBBB_w.handler(offset,data);
	} };
	
	public static ReadHandlerPtr mgakuen_paletteram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return paletteram_r.handler(offset);
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(mame_bitmap bitmap)
	{
		int offs,sx,sy;
	
		/* the last entry is not a sprite, we skip it otherwise spang shows a bubble */
		/* moving diagonally across the screen */
		for (offs = 0x1000-0x40;offs >= 0;offs -= 0x20)
		{
			int code = pang_objram.read(offs);
			int attr = pang_objram.read(offs+1);
			int color = attr & 0x0f;
			sx = pang_objram.read(offs+3) + ((attr & 0x10) << 4);
			sy = ((pang_objram.read(offs+2) + 8) & 0xff) - 8;
			code += (attr & 0xe0) << 3;
			if (flipscreen != 0)
			{
				sx = 496 - sx;
				sy = 240 - sy;
			}
			drawgfx(bitmap,Machine.gfx[1],
					 code,
					 color,
					 flipscreen,flipscreen,
					 sx,sy,
					 Machine.visible_area,TRANSPARENCY_PEN,15);
		}
	}
	
	public static VhUpdatePtr pang_vh_screenrefresh = new VhUpdatePtr() { public void handler(mame_bitmap bitmap,int full_refresh) 
	{
		fillbitmap(bitmap,Machine.pens[0],Machine.visible_area);
		tilemap_draw(bitmap,bg_tilemap,0,0);
                draw_sprites(bitmap);
	} };
}
