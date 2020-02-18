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

                if ((LCDCONT() & 0x04) != 0)
                {
                        height = 16;
                        tilemask = 0xFE;
                }
                else
                {
                        height = 8;
                        tilemask = 0xFF;
                }

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
                                    //System.out.println("Posi: "+(VRAM + (oam.read(2) & tilemask) * 16 + (line - oam.read(0)) * 2));
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
        
        public static final int SC_Y = 0xff42;
        public static final int SC_X = 0xff43;
        public static final int LCDC_CONTROL = 0xff40;
        public static final int WINDOW_DISPLAY_ENABLE = 5;
        public static final int SPRITE_ENABLE = 1;
        public static final int BACKGROUND_ENABLE = 0;
        
        public static final int W_Y = 0xff4a;
        public static final int W_X = 0xff4b;
        
        //sprite flags
        private static final int PALETTE_NUM = 4;
        private static final int HORIZ_FLIP = 5;
        private static final int VERT_FLIP = 6;
        private static final int PRIORITY = 7;
        private static final int SPRITE_HEIGHT = 2;
        
        public static void gb_refresh_scanline ()
	{
	
            mame_bitmap bitmap = Machine.scrbitmap;
            int currentScanLine = CURLINE();
            
            int lcdc = gb_ram.read(LCDC_CONTROL) & 0xff;
            if (isSet(lcdc, BACKGROUND_ENABLE))
                drawBackground(bitmap, currentScanLine);
            if (isSet(lcdc, WINDOW_DISPLAY_ENABLE))
                drawWindow(bitmap, currentScanLine);
            if (isSet(lcdc, SPRITE_ENABLE))
                drawSprites(bitmap, currentScanLine);
        }
        
        /**
        * draws the window onto the LCD display screen
        * as specified by wX and wY registers
        */
       private static void drawWindow(mame_bitmap bitmap, int scanLine) {
           int lcdc = gb_ram.read(LCDC_CONTROL)&0xff;
           int wX = gb_ram.read(W_X)&0xff;
           int wY = gb_ram.read(W_Y)&0xff;
           int tileMapAddress = isSet(lcdc, 6) ? 0x9c00 : 0x9800;
           int tileDataAddress = isSet(lcdc, 4) ? 0x8000 : 0x9000;
           boolean signedIndex = !isSet(lcdc, 4);
           int bgTileInfo;


           if (wY > 143 || wX > 166 || wY > scanLine) {
               return; //window not on screen or on this line
           }
           //draw tiles on current scanLine
           int yOffset = ((scanLine - wY) / 8) * 32;
           int tileLine = (scanLine - wY) % 8;

           for (int xTile = 0; xTile <= 20; ++xTile) {
               int tileIndex = gb_ram.read(tileMapAddress + yOffset + xTile)&0xff;
               //if (gbcMode)
               //    bgTileInfo = memory.readVram(tileMapAddress + yOffset + xTile, 1);
               //else
                   bgTileInfo = 0;

               int tileAddress = signedIndex ? (tileIndex * 16) + tileDataAddress
                                             : (Byte.toUnsignedInt((byte) tileIndex) * 16) + tileDataAddress;

               int xCoord = (wX - 7) + (xTile * 8);
               drawWindowTile(bitmap, tileAddress, tileLine, xCoord, scanLine, bgTileInfo);
           }
       }
       
     /**
     * Draws one window tile line at specified position
     * Window doesn't wrap
     *
     * @param tileAddress of the tile
     * @param line of the tile to draw (0 - 7)
     * @param xPos on screen to draw top left of line
     * @param yPos on screen to draw top left of line
     */
    private static void drawWindowTile(mame_bitmap bitmap, int tileAddress, int line, int xPos, int yPos, int bgTileInfo) {
        int paletteAddress = 0xff47;

        int pixByteA = gb_ram.read(tileAddress + (2 * line))&0xff;
        int pixByteB = gb_ram.read(tileAddress + (2 * line) + 1)&0xff;

        /*if (gbcMode) {
            //set correct GBC tile info
            paletteAddress = bgTileInfo & 0x7;
            int bankNum = isSet(bgTileInfo, 3) ? 1 : 0;
            pixByteA = memory.readVram(tileAddress + (2 * line), bankNum);
            pixByteB = memory.readVram(tileAddress + (2 * line) + 1, bankNum);
        }*/

        //draw each pixel in the line
        for (int pixel = 0; pixel <= 7; ++pixel) {
            int colorNum = getPixelColorNum(pixByteA, pixByteB, pixel);
            int xCoord = (xPos + pixel);
            if (xCoord < 160 && yPos < 144 && xCoord >= 0 && yPos >= 0) {
                //TODO is this bg palette?
                plot_pixel.handler(bitmap, xCoord, yPos, colorNum);
            }
        }
    }
        
        private static void drawSprites(mame_bitmap bitmap, int scanline) {
            int lcdc = gb_ram.read(LCDC_CONTROL) & 0xff;
            int height = isSet(lcdc, SPRITE_HEIGHT) ? 16 : 8;

            //TODO for GBC mainly the same
            // VRAM Bank #, different palette number

            for (int i = 0; i < 40; ++i){
                int offset = (39 - i) * 4;
                int y = gb_ram.read(0xfe00 + offset)&0xff;
                int x = gb_ram.read(0xfe00 + offset + 1)&0xff;
                int tileNum = gb_ram.read(0xfe00 + offset + 2)&0xff;
                int flags = gb_ram.read(0xfe00 + offset + 3)&0xff;
                if (height == 16) {
                    tileNum &= 0xfe;
                }
                if ((scanline >= (y - 16)) && ((y - 17) + height >= scanline)) {
                    int address = (tileNum * 16) + 0x8000;
                    draw_sprite_line(bitmap, x - 8, y -16, address,
                                    flags, height, scanline);
                }
            }
        }


    /**
     * draws the sprite for current scanline at address
     * to the screen at xPos, yPos (top left corner
     * @param x of LCD screen to display top left
     * @param y of LCD screen to display top left
     * @param address of first byte of sprite data
     * @param height of the sprite (8 or 16)
     * @param flags associated with the sprite
     *
     * TODO scanline LIMIT
     */
    private static void draw_sprite_line(mame_bitmap bitmap, int x, int y, int address, int flags, int height, int scanline) {
        boolean horizFlip = isSet(flags, HORIZ_FLIP);
        boolean hasPriority = !isSet(flags, PRIORITY);
        int paletteAddress = isSet(flags, PALETTE_NUM) ? 0xff49 : 0xff48;
        int sprite_line = height - (scanline - y);

        int offset;
        if (!isSet(flags, VERT_FLIP)) {
            offset = 2 * (scanline - y);
        } else {
            offset = 2 * (sprite_line - 1);
        }
        int pixDataA = gb_ram.read(address + offset)&0xff;
        int pixDataB = gb_ram.read(address + offset + 1)&0xff;

        /*if (gbcMode) {
            paletteAddress = flags & 0x7;
            int bankNum = isSet(flags, 3) ? 1 : 0;
            pixDataA = memory.readVram(address + offset, bankNum);
            pixDataB = memory.readVram(address + offset + 1, bankNum);
        }*/

        for (int pix = 0; pix < 8; ++pix) {
            int col_index = horizFlip ? 7 - pix : pix;
            int color_num = getPixelColorNum(pixDataA, pixDataB, col_index);
            int color = color_num;
            if ((x + pix < 160) && (x + pix >= 0) && color_num != 0) {
                if (hasPriority) {
                    plot_pixel.handler(bitmap, x + pix, scanline, color);
                } else {
                    //if (bufferToColor(x + pix, scanline) == getColor(0, 0xff47, false)) {
                        plot_pixel.handler(bitmap, x + pix, scanline, color);
                    //}
                }
            }
        }
    }
        
        public static void drawBackground(mame_bitmap bitmap, int scanLine){
            
                int lcdc = gb_ram.read(LCDC_CONTROL) & 0xFF;
                int tileMapAddress = isSet(lcdc, 3) ? 0x9c00 : 0x9800;
                int scY = gb_ram.read(SC_Y) & 0xFF;
                int scX = gb_ram.read(SC_X) & 0xFF;
                int tileDataAddress = isSet(lcdc, 4) ? 0x8000 : 0x9000;
                boolean signedIndex = !isSet(lcdc, 4);


                // for GBC TODO
                // Look up additional info in VRAM bank 1 (same index #)
                // This gives palette num, bank num, horiz/vert/priority
                // index should be the same
                // Calculate the address, vram bank, and draw the actual background tile
                // need to add (vram bank, palette address to drawTile)
                // TODO HORIZ/VERT FLIP/ PRIORITY

                //draw tiles on current scanLine
                
                int yOffset = (((scY + scanLine) / 8) % 32) * 32;
                int tileLine = (scY + scanLine) % 8;

                for (int xTile = 0; xTile <= 20; ++xTile) {
                    int xOffset = (xTile + (scX / 8)) % 32;
                    //byte tileIndex = (byte)memory.readVram(tileMapAddress + yOffset + xOffset, 0);
                    int tileIndex = (gb_ram.read(tileMapAddress + yOffset + xOffset))&0xFF;
                    int bgTileInfo;
                    /*if (gbcMode)
                        bgTileInfo = memory.readVram(tileMapAddress + yOffset + xOffset, 1);
                    else*/
                        bgTileInfo = 0;
                    int tileAddress = signedIndex ? (tileIndex * 16) + tileDataAddress
                                                  : (Byte.toUnsignedInt((byte) tileIndex) * 16) + tileDataAddress;


                    //calculate correct shift from scX
                    int xShift = scX % 8;
                    if (scanLine == 0) {
                        if (tileAddress == 0x9000) {
                            tileAddress = 0x9010;
                        }
                    }
                    if (xTile == 0 && xShift != 0) {
                        drawTile(bitmap, tileAddress, tileLine, 0, scanLine, xShift, 7, bgTileInfo);
                    } else if (xTile == 20 && xShift != 0) {
                        drawTile(bitmap, tileAddress, tileLine, 160 - xShift, scanLine, 0, xShift - 1, bgTileInfo);
                    } else if (xTile != 20) {
                        drawTile(bitmap, tileAddress, tileLine, (xTile * 8) - xShift, scanLine, 0, 7, bgTileInfo);
                    }
                }
        }
        
        public static void drawTile(mame_bitmap bitmap, int tileAddress, int line, int xPos, int yPos, int pixStart, int pixEnd, int bgTileInfo) {
            int paletteAddress = 0xff47;
            
            int wX = (gb_ram.read(W_X) & 0xff) - 7;
            int wY = gb_ram.read(W_Y) & 0xff;
            boolean windowDrawn = isSet(gb_ram.read(LCDC_CONTROL)&0xff, WINDOW_DISPLAY_ENABLE);
            int pixByteA = gb_ram.read(tileAddress + (2 * line))&0xff;
            int pixByteB = gb_ram.read(tileAddress + (2 * line) + 1)&0xff;

            /*if (gbcMode) {
                //set correct GBC tile info
                //vertical flip
                if (isSet(bgTileInfo, 6)) {
                    line = 7 - line;
                }
                paletteAddress = bgTileInfo & 0x7;
                int bankNum = isSet(bgTileInfo, 3) ? 1 : 0;
                pixByteA = memory.readVram(tileAddress + (2 * line), bankNum);
                pixByteB = memory.readVram(tileAddress + (2 * line) + 1, bankNum);
            }*/

            //draw each pixel in the line
            for (int pixel = pixStart; pixel <= pixEnd; ++pixel) {
                int pix = pixel;
                // Horizontal flip
                /*if (gbcMode && isSet(bgTileInfo, 5)) {
                    pix = 7 - pix;
                }*/
                int colorNum = getPixelColorNum(pixByteA, pixByteB, pix);
                int xCoord = (xPos + pix - pixStart) % 160;

                if (wY <= yPos && xCoord >= wX && windowDrawn) {
                    break; //window will be drawn at this position
                } else {
                    //drawToBuffer(xCoord, yPos, getColor(colorNum, paletteAddress, true));
                    plot_pixel.handler(bitmap, xCoord, yPos, colorNum);
                }
            }
        }

        private static int getPixelColorNum(int pixDataA, int pixDataB, int pixIndex) {
            int colorNum = isSet(pixDataB, 7 - pixIndex) ? 1 : 0;
            colorNum = (colorNum << 1) | (isSet(pixDataA, 7 - pixIndex) ? 1 : 0);

            return colorNum;
        }
        
        private static boolean isSet(int num, int bitNum) {
            return (((num >> bitNum) & 0x1) == 1);
        }
	
	public static void gb_refresh_scanline_old ()
	{
		mame_bitmap bitmap = Machine.scrbitmap;
                UBytePtr zbuf = new UBytePtr(bg_zbuf);
                
                int l = 0, yindex = CURLINE();

                /* layer info layer[0]=background, layer[1]=window */
                layer_struct[] layer = new layer_struct[2];
                
                for (int _h=0 ; _h<2 ; _h++)
                    layer[_h]=new layer_struct();

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
                        System.out.println("bgline: "+bgline);

                        layer[0].bg_map = new UBytePtr(gb_bgdtab, 0);
                        //layer[0].bg_map = new UBytePtr(gb_ram, 0x9800);
                        layer[0].bg_map.inc( (bgline << 2) & 0x3E0 );
                        //layer[0].bg_map.inc( (bgline *32));
                        layer[0].bg_tiles = new UShortPtr( gb_chrgen, (bgline & 7));
                        layer[0].bg_pal = gb_bpal;
                        layer[0].xindex = SCROLLX() >> 3;
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
                        layer[1].bg_tiles = new UShortPtr( gb_chrgen, (bgline & 7));
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
                        int xidx;
                        //int bit;
                        int i;
                        UShortPtr tiles;
                        int data;
                        //int xindex;

                        if (layer[l].enabled==0)
                        {
                                l++;
                                continue;
                        }

                        map = (layer[l].bg_map);
                        tiles = (layer[l].bg_tiles);
                        xidx = layer[l].xindex;
                        //bit = layer[l].xshift;
                        
                        i = layer[l].xend;

                        data = (tiles.read((map.read(xidx) ^ gb_tile_no_mod) * 8) << layer[l].xshift)&0xff;
/*TODO*////*TODO*///        #ifndef LSB_FIRST
                        data = ((data << 8) | (data >> 8)) & 0xffff;
/*TODO*////*TODO*///        #endif
                        //xindex = layer[l].xstart;
                        while (i != 0)
                        {
                                while ((layer[l].xshift < 8) && i!=0)
                                {
                                        int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                        plot_pixel.handler(bitmap, layer[l].xstart, yindex, layer[l].bg_pal[colour]);
        /*				plot_pixel(bitmap, xindex, yindex, gb_bpal[colour]); */
                                        layer[l].xstart++;
                                        zbuf.writeinc( colour );
                                        data <<= 1;
                                        data = data & 0xffff;
                                        layer[l].xshift++;
                                        i--;
                                }
                                xidx = (xidx + 1) & 31;
                                layer[l].xshift = 0;
                                data = (tiles.read((map.read(xidx) ^ gb_tile_no_mod) * 8)) & 0xff;
                                data = ((data << 8) | (data >> 8)) & 0xffff;
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
	private static int[] getGradients(int val) {

		val = val & 0xff;
		int[] gradients = new int[4];

		gradients[0] = (val & 0xC0) >> 6;
		gradients[1] = (val & 0x30) >> 4;
		gradients[2] = (val & 0xC) >> 2;
		gradients[3] = (val & 0x3);

		return gradients;
	}
        
        private static void drawBackgroundx(mame_bitmap bitmap) {
            int bgTileDataStart = 0x9800;

            for(int _rowi = 0 ; _rowi <= 100 ; _rowi++) {
/*TODO*///                UBytePtr zbuf = new UBytePtr(bg_zbuf);
/*TODO*///                
/*TODO*///                int l = 0, yindex = CURLINE();
/*TODO*///
                int l = 0;
                int val = gb_ram.read(_rowi+bgTileDataStart);
                int yindex = _rowi;
                
                /* layer info layer[0]=background, layer[1]=window */
                layer_struct[] layer = new layer_struct[2];
                
                for (int _h=0 ; _h<2 ; _h++)
                    layer[_h]=new layer_struct();

                /* if background or screen disabled clear line */
                if ((LCDCONT() & 0x81) != 0x81)
                {
                        rectangle r = new rectangle(Machine.visible_area);
                        r.min_y = r.max_y = _rowi;
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
                //layer[0].enabled = 1;

                if (layer[0].enabled != 0)
                {
                        int bgline;

                        bgline = (SCROLLY() + CURLINE()) & 0xFF;

                        layer[0].bg_map = new UBytePtr(gb_bgdtab);
                        layer[0].bg_map.inc( (bgline << 2) & 0x3E0 );
                        layer[0].bg_tiles = new UShortPtr( gb_chrgen, (bgline & 7));
                        layer[0].bg_pal = gb_bpal;
                        layer[0].xindex = SCROLLX() >> 3;
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
                        layer[1].bg_tiles = new UShortPtr( gb_chrgen, (bgline & 7));
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

                        if (layer[l].enabled==0)
                        {
                                l++;
                                continue;
                        }

                        map = new UBytePtr(layer[l].bg_map);
                        tiles = new UShortPtr(layer[l].bg_tiles);
                        xidx = layer[l].xindex;
                        bit = layer[l].xshift;
                        i = layer[l].xend;

/*TODO*///                        data = (tiles.read((map.read(xidx) ^ gb_tile_no_mod) * 8) << bit)&0xffff;
/*TODO*////*TODO*///        #ifndef LSB_FIRST
/*TODO*///                        data = ((data << 8) | (data >> 8)) & 0xffff;
/*TODO*////*TODO*///        #endif
                        
                        data = gb_ram.read(_rowi+bgTileDataStart);
                        data = ((data << 8) | (data >> 8)) & 0xffff;
                        
                        xindex = layer[l].xstart;
                        while (i != 0)
                        {
                                while ((bit < 8) && i!=0)
                                {
                                        int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                        plot_pixel.handler(bitmap, xindex, _rowi, layer[l].bg_pal[colour]);
        /*				plot_pixel(bitmap, xindex, yindex, gb_bpal[colour]); */
                                        xindex++;
/*TODO*///                                        zbuf.writeinc( colour );
                                        data <<= 1;
                                        bit++;
                                        i--;
                                }
                                xidx++; // = (xidx + 1) & 31;
                                bit = 0;
                                //data = (tiles.read((map.read(xidx) ^ gb_tile_no_mod) * 8)) & 0xffff;
                                data = val = gb_ram.read(_rowi+bgTileDataStart+xidx);
                                data = ((data << 8) | (data >> 8)) & 0xffff;
                        }
                        l++;
                }

/*TODO*///                if ((LCDCONT() & 0x02) != 0)
/*TODO*///                        gb_update_sprites();
            }
        }
        
        private static void drawBackground(mame_bitmap bitmap) {
            int bgTileDataStart = 0x9800;
            
            for (int _i=0 ; _i<0x3ff ; _i++){
                int val = gb_ram.read(_i);
            }
            
            plot_box.handler(bitmap, 0, 0, 160, 1, 0);
        }
        
        private static void drawBackgroundB(mame_bitmap bitmap) {
            int xVal = 0;
		int yVal = 0;

		int bgTileDataStart = 0x9800;
                int xindex = 0;

		for(int i = bgTileDataStart ; i <= (bgTileDataStart + 0x3ff) ; i++) {

			int val = gb_ram.read(i);

			int[] gradients = getGradients(val);

			/*for(int grad : gradients) {
				drawPX(xVal - scrollX, yVal - scrollY, grad);
				drawPX(bitmap, xVal, yVal, grad);
			}*/
                        int bit=0;
                        int data = val;
                        
                        while ((bit < 8) && i!=0)
                                {
                                        int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                        plot_pixel.handler(bitmap, xVal+bit, yVal, colour);
        /*				plot_pixel(bitmap, xindex, yindex, gb_bpal[colour]); */
                                        xindex++;
                                        //zbuf.writeinc( colour );
                                        data <<= 1;
                                        bit++;
                                        i--;
                                }

			xVal++;

			if(xVal >= 32) {
				xVal = 0;
				yVal++;
			}

		}
        }
        
        private static void drawPX(mame_bitmap bitmap, int x, int y, int val) {

		//if(x >= screen.length || x < 0 || y >= screen[x].length || y < 0) {
			
		    
		//} else {
//			System.out.println(x+": "+y+" : "+val);
			//screen[x][y] = (byte)val;
                        plot_box.handler(bitmap, x*2, y*2, 2, 2, val);
		//}

	}

	private static void drawBackgroundY(mame_bitmap bitmap) {

		int xVal = 0;
		int yVal = 0;
                
                int bgTileDataStart = 0x9800;

		for(int i = bgTileDataStart ; i <= (bgTileDataStart + 0x3ff) ; i++) {

			int val = gb_ram.read(i);
                        int data = val;

			int[] gradients = getGradients(val);
                        int bit=0;
                        int xindex=0;
                        
                        while ((bit < 8) && i!=0)
                                {
                                        //int colour = ((data & 0x8000)!=0 ? 2 : 0) | ((data & 0x0080)!=0 ? 1 : 0);
                                        //plot_pixel.handler(bitmap, xindex, _rowi, layer[l].bg_pal[colour]);
                                    for(int grad : gradients) {
                            //System.out.println(SCROLLX());
                            //System.out.println(SCROLLY());
//				drawPX(xVal - scrollX, yVal - scrollY, grad);
				drawPX2(bitmap, xVal-SCROLLX(), yVal-SCROLLY(), grad, val);
			}
        /*				plot_pixel(bitmap, xindex, yindex, gb_bpal[colour]); */
                                        xindex++;
/*TODO*///                                        zbuf.writeinc( colour );
                                        data <<= 1;
                                        bit++;
                                        i--;
                                }

			

			xVal++;

			if(xVal >= 32) {
				xVal = 0;
				yVal++;
			}

		}

	}

	private static void drawPX2(mame_bitmap bitmap, int x, int y, int grad, int val) {

		if(x >= 160 || x < 0 || y >= 144 || y < 0) {
                    
                } else {
                int xCalc, yCalc;
                int sizePixel = 1;
                
                //yCalc = y * sizePixel;
                //xCalc = x * sizePixel;
                int _posBit = 0x8;
                
                
                //for (int _i = 0 ; _i<sizePixel ; _i++)
		    
		//} else 
                {
//			System.out.println(x+": "+y+" : "+val);
			//screen[x][y] = (byte)val;
                        //if ((val & _posBit) != 0){
                            //plot_box.handler(bitmap, xCalc+(_i*sizePixel), yCalc, sizePixel, sizePixel, val);
                            if ((bitmap!=null)&&(bitmap.line!=null)&&(bitmap.line[y]!=null))
                                plot_pixel.handler(bitmap, x, y, grad);
                                //plot_box.handler(bitmap, xCalc, yCalc, sizePixel, sizePixel, grad);
                        /*} else {
                            if ((bitmap!=null)&&(bitmap.line!=null)&&(bitmap.line[yCalc]!=null))
                                plot_pixel.handler(bitmap, xCalc, yCalc, 0);
                        }
                        
                        _posBit = _posBit >> 1;*/
                        //xCalc*=sizePixel;
                         
		}
                }

	}
	public static VhUpdatePtr gb_vh_screen_refresh = new VhUpdatePtr() {
            public void handler(mame_bitmap bitmap, int full_refresh) {
                // Nothing to do
                //drawBackground(bitmap);
            }
        };
	
}

