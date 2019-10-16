/***************************************************************************

	Video Interface Chip 6567 (6566 6569 and some other)
	PeT mess@utanet.at

***************************************************************************/
/* mos videochips
  vic (6560 NTSC, 6561 PAL)
  used in commodore vic20

  vic II
   6566 NTSC
    no dram refresh?
   6567 NTSC
   6569 PAL-B
   6572 PAL-N
   6573 PAL-M
   8562 NTSC
   8565 PAL
  used in commodore c64
  complete different to vic

  ted
   7360/8360 (NTSC-M, PAL-B by same chip ?)
   8365 PAL-N
   8366 PAL-M
  used in c16 c116 c232 c264 plus4 c364
  based on the vic II
  but no sprites and not register compatible
  includes timers, input port, sound generators
  memory interface, dram refresh, clock generation

  vic IIe
   8564 NTSC-M
   8566 PAL-B
   8569 PAL-N
  used in commodore c128
  vic II with some additional features
   3 programmable output pins k0 k1 k2

  vic III
   4567
  used in commodore c65 prototype
  vic II compatible (mode only?)
  several additional features
   different resolutions, more colors, ...
   (maybe like in the amiga graphic chip docu)

  vdc
   8563
   8568 (composite video and composite sync)
  second graphic chip in c128
  complete different to above chips
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.vidhrdw;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static mame056.commonH.*;
import static mame056.cpuexec.*;
import static mame056.mame.*;

public class vic6567
{
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	#define VERBOSE_DBG 1
/*TODO*///	
/*TODO*///	/* lightpen values */
/*TODO*///	
/*TODO*///	
/*TODO*///	/*#define GFX */
/*TODO*///	
/*TODO*///	#define VREFRESHINLINES 28
/*TODO*///	
/*TODO*///	#define VIC2_YPOS 50
/*TODO*///	#define RASTERLINE_2_C64(a) (a)
/*TODO*///	#define C64_2_RASTERLINE(a) (a)
/*TODO*///	#define XPOS 8
/*TODO*///	#define YPOS 8
/*TODO*///	
/*TODO*///	/* lightpen delivers values from internal counters
/*TODO*///	 * they do not start with the visual area or frame area */
/*TODO*///	#define VIC2_MAME_XPOS 0
/*TODO*///	#define VIC2_MAME_YPOS 0
/*TODO*///	#define VIC6567_X_BEGIN 38
/*TODO*///	#define VIC6567_Y_BEGIN -6			   /* first 6 lines after retrace not for lightpen! */
/*TODO*///	#define VIC6569_X_BEGIN 38
/*TODO*///	#define VIC6569_Y_BEGIN -6
/*TODO*///	#define VIC2_X_BEGIN (vic2.pal?VIC6569_X_BEGIN:VIC6567_X_BEGIN)
/*TODO*///	#define VIC2_Y_BEGIN (vic2.pal?VIC6569_Y_BEGIN:VIC6567_Y_BEGIN)
/*TODO*///	#define VIC2_X_VALUE ((LIGHTPEN_X_VALUE+VIC2_X_BEGIN \
/*TODO*///	                          +VIC2_MAME_XPOS)/2)
/*TODO*///	#define VIC2_Y_VALUE ((LIGHTPEN_Y_VALUE+VIC2_Y_BEGIN \
/*TODO*///	                          +VIC2_MAME_YPOS))
/*TODO*///	
/*TODO*///	#define VIC2E_K0_LEVEL (vic2.reg[0x2f]&1)
/*TODO*///	#define VIC2E_K1_LEVEL (vic2.reg[0x2f]&2)
/*TODO*///	#define VIC2E_K2_LEVEL (vic2.reg[0x2f]&4)
/*TODO*///	
/*TODO*///	/*#define VIC3_P5_LEVEL (vic2.reg[0x30]&0x20) */
/*TODO*///	#define VIC3_BITPLANES (vic2.reg[0x31]&0x10)
/*TODO*///	#define VIC3_80COLUMNS (vic2.reg[0x31]&0x80)
/*TODO*///	#define VIC3_LINES	   ((vic2.reg[0x31]&0x19)==0x19?400:200)
/*TODO*///	#define VIC3_BITPLANES_WIDTH (vic2.reg[0x31]&0x80?640:320)
/*TODO*///	
/*TODO*///		 /*#define VIC2E_TEST (vic2[0x30]&2) */
/*TODO*///	#define DOUBLE_CLOCK (vic2.reg[0x30]&1)
/*TODO*///	
/*TODO*///	/* sprites 0 .. 7 */
/*TODO*///	#define SPRITE_BASE_X_SIZE 24
/*TODO*///	#define SPRITE_BASE_Y_SIZE 21
/*TODO*///	#define SPRITEON(nr) (vic2.reg[0x15]&(1<<nr))
/*TODO*///	#define SPRITE_Y_EXPAND(nr) (vic2.reg[0x17]&(1<<nr))
/*TODO*///	#define SPRITE_Y_SIZE(nr) (SPRITE_Y_EXPAND(nr)?2*21:21)
/*TODO*///	#define SPRITE_X_EXPAND(nr) (vic2.reg[0x1d]&(1<<nr))
/*TODO*///	#define SPRITE_X_SIZE(nr) (SPRITE_X_EXPAND(nr)?2*24:24)
/*TODO*///	#define SPRITE_X_POS(nr) ( (vic2.reg[(nr)*2]|(vic2.reg[0x10]&(1<<(nr))?0x100:0))-24+XPOS )
/*TODO*///	#define SPRITE_Y_POS(nr) (vic2.reg[1+2*(nr)]-50+YPOS)
/*TODO*///	#define SPRITE_MULTICOLOR(nr) (vic2.reg[0x1c]&(1<<nr))
/*TODO*///	#define SPRITE_PRIORITY(nr) (vic2.reg[0x1b]&(1<<nr))
/*TODO*///	#define SPRITE_MULTICOLOR1 (vic2.reg[0x25]&0xf)
/*TODO*///	#define SPRITE_MULTICOLOR2 (vic2.reg[0x26]&0xf)
/*TODO*///	#define SPRITE_COLOR(nr) (vic2.reg[0x27+nr]&0xf)
/*TODO*///	#define SPRITE_ADDR(nr) (vic2.videoaddr+0x3f8+nr)
/*TODO*///	#define SPRITE_BG_COLLISION(nr) (vic2.reg[0x1f]&(1<<nr))
/*TODO*///	#define SPRITE_COLLISION(nr) (vic2.reg[0x1e]&(1<<nr))
/*TODO*///	#define SPRITE_SET_BG_COLLISION(nr) (vic2.reg[0x1f]|=(1<<nr))
/*TODO*///	#define SPRITE_SET_COLLISION(nr) (vic2.reg[0x1e]|=(1<<nr))
/*TODO*///	
/*TODO*///	#define SCREENON (vic2.reg[0x11]&0x10)
/*TODO*///	#define VERTICALPOS (vic2.reg[0x11]&7)
/*TODO*///	#define HORICONTALPOS (vic2.reg[0x16]&7)
/*TODO*///	#define ECMON (vic2.reg[0x11]&0x40)
/*TODO*///	#define HIRESON (vic2.reg[0x11]&0x20)
/*TODO*///	#define MULTICOLORON (vic2.reg[0x16]&0x10)
/*TODO*///	#define LINES25 (vic2.reg[0x11]&8)		   /* else 24 Lines */
/*TODO*///	#define LINES (LINES25?25:24)
/*TODO*///	#define YSIZE (LINES*8)
/*TODO*///	#define COLUMNS40 (vic2.reg[0x16]&8)	   /* else 38 Columns */
/*TODO*///	#define COLUMNS (COLUMNS40?40:38)
/*TODO*///	#define XSIZE (COLUMNS*8)
/*TODO*///	
/*TODO*///	#define VIDEOADDR ( (vic2.reg[0x18]&0xf0)<<(10-4) )
/*TODO*///	#define CHARGENADDR ((vic2.reg[0x18]&0xe)<<10)
/*TODO*///	
/*TODO*///	#define RASTERLINE ( ((vic2.reg[0x11]&0x80)<<1)|vic2.reg[0x12])
/*TODO*///	
/*TODO*///	#define BACKGROUNDCOLOR (vic2.reg[0x21]&0xf)
/*TODO*///	#define MULTICOLOR1 (vic2.reg[0x22]&0xf)
/*TODO*///	#define MULTICOLOR2 (vic2.reg[0x23]&0xf)
/*TODO*///	#define FOREGROUNDCOLOR (vic2.reg[0x24]&0xf)
/*TODO*///	#define FRAMECOLOR (vic2.reg[0x20]&0xf)
	
	public static char vic2_palette[] =
	{
	/* black, white, red, cyan */
	/* purple, green, blue, yellow */
	/* orange, brown, light red, dark gray, */
	/* medium gray, light green, light blue, light gray */
	/* taken from the vice emulator */
		0x00, 0x00, 0x00,  0xfd, 0xfe, 0xfc,  0xbe, 0x1a, 0x24,  0x30, 0xe6, 0xc6,
		0xb4, 0x1a, 0xe2,  0x1f, 0xd2, 0x1e,  0x21, 0x1b, 0xae,  0xdf, 0xf6, 0x0a,
		0xb8, 0x41, 0x04,  0x6a, 0x33, 0x04,  0xfe, 0x4a, 0x57,  0x42, 0x45, 0x40,
		0x70, 0x74, 0x6f,  0x59, 0xfe, 0x59,  0x5f, 0x53, 0xfe,  0xa4, 0xa7, 0xa2
	};
	
	public static class _vic2 {
/*TODO*///		UINT8 reg[0x80];
/*TODO*///		bool pal;
/*TODO*///		bool vic2e;		     /* version with some port lines */
		public boolean vic3 = false;
/*TODO*///		bool on; /* rastering of the screen */
/*TODO*///	
/*TODO*///		int (*dma_read) (int);
/*TODO*///		int (*dma_read_color) (int);
/*TODO*///		void (*interrupt) (int);
/*TODO*///		void (*port_changed)(int);
/*TODO*///	
/*TODO*///		int lines;
/*TODO*///		void *lightpentimer;
/*TODO*///	
/*TODO*///		int chargenaddr, videoaddr;
/*TODO*///	
		public mame_bitmap bitmap;		   /* Machine->scrbitmap for speedup */
/*TODO*///		int x_begin, x_end;
/*TODO*///		int y_begin, y_end;
/*TODO*///	
/*TODO*///		UINT16 c64_bitmap[2], bitmapmulti[4], mono[2],
/*TODO*///			multi[4], ecmcolor[2], colors[4], spritemulti[4];
/*TODO*///	
/*TODO*///		int lastline, rasterline;
	
		/* background/foreground for sprite collision */
		public UBytePtr[] screen = new UBytePtr[216], shift = new UBytePtr[216];
	
		/* convert multicolor byte to background/foreground for sprite collision */
		public int[] foreground = new int[256];
		public int[] expandx = new int[256];
                public int[] expandx_multi = new int[256];
	
		/* converts sprite multicolor info to info for background collision checking */
		public int[] multi_collision = new int[256];
	
/*TODO*///		struct {
/*TODO*///			int on, x, y, xexpand, yexpand;
/*TODO*///	
/*TODO*///			int repeat;						   /* y expand, line once drawn */
/*TODO*///			int line;						   /* 0 not painting, else painting */
/*TODO*///	
/*TODO*///			/* buffer for currently painted line */
/*TODO*///			int paintedline[8];
/*TODO*///		    UINT8 bitmap[8][SPRITE_BASE_X_SIZE * 2 / 8 + 1/*for simplier sprite collision detection*/];
/*TODO*///		}
/*TODO*///		sprites[8];
	};
        
        public static _vic2 vic2 = new _vic2();
	
/*TODO*///	INLINE int vic2_getforeground (register int y, register int x)
/*TODO*///	{
/*TODO*///	    return ((vic2.screen[y][x >> 3] << 8)
/*TODO*///		    | (vic2.screen[y][(x >> 3) + 1])) >> (8 - (x & 7));
/*TODO*///	}
/*TODO*///	
/*TODO*///	INLINE int vic2_getforeground16 (register int y, register int x)
/*TODO*///	{
/*TODO*///		return ((vic2.screen[y][x >> 3] << 16)
/*TODO*///				| (vic2.screen[y][(x >> 3) + 1] << 8)
/*TODO*///				| (vic2.screen[y][(x >> 3) + 2])) >> (8 - (x & 7));
/*TODO*///	}
/*TODO*///	
/*TODO*///	#if 0
/*TODO*///	static void vic2_setforeground (int y, int x, int value)
/*TODO*///	{
/*TODO*///		vic2.screen[y][x >> 3] = value;
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	static void vic2_drawlines (int first, int last);
/*TODO*///	
/*TODO*///	void vic6567_init (int chip_vic2e, int pal,
/*TODO*///					   int (*dma_read) (int), int (*dma_read_color) (int),
/*TODO*///					   void (*irq) (int))
/*TODO*///	{
/*TODO*///		memset(&vic2, 0, sizeof(vic2));
/*TODO*///	
/*TODO*///		vic2.lines = VIC2_LINES;
/*TODO*///	
/*TODO*///		vic2.dma_read = dma_read;
/*TODO*///		vic2.dma_read_color = dma_read_color;
/*TODO*///		vic2.interrupt = irq;
/*TODO*///		vic2.vic2e = chip_vic2e;
/*TODO*///		vic2.pal = pal;
/*TODO*///		vic2.on = true;
/*TODO*///	}
/*TODO*///	
/*TODO*///	void vic2_set_rastering(int onoff)
/*TODO*///	{
/*TODO*///		vic2.on=onoff;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int vic2e_k0_r (void)
/*TODO*///	{
/*TODO*///		return VIC2E_K0_LEVEL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int vic2e_k1_r (void)
/*TODO*///	{
/*TODO*///		return VIC2E_K1_LEVEL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int vic2e_k2_r (void)
/*TODO*///	{
/*TODO*///		return VIC2E_K2_LEVEL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	#if 0
/*TODO*///	int vic3_p5_r (void)
/*TODO*///	{
/*TODO*///		return VIC3_P5_LEVEL;
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	static void vic2_set_interrupt (int mask)
/*TODO*///	{
/*TODO*///		if (((vic2.reg[0x19] ^ mask) & vic2.reg[0x1a] & 0xf))
/*TODO*///		{
/*TODO*///			if (!(vic2.reg[0x19] & 0x80))
/*TODO*///			{
/*TODO*///				DBG_LOG (2, "vic2", ("irq start %.2x\n", mask));
/*TODO*///				vic2.reg[0x19] |= 0x80;
/*TODO*///				vic2.interrupt (1);
/*TODO*///			}
/*TODO*///		}
/*TODO*///		vic2.reg[0x19] |= mask;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_clear_interrupt (int mask)
/*TODO*///	{
/*TODO*///		vic2.reg[0x19] &= ~mask;
/*TODO*///		if ((vic2.reg[0x19] & 0x80) && !(vic2.reg[0x19] & vic2.reg[0x1a] & 0xf))
/*TODO*///		{
/*TODO*///			DBG_LOG (2, "vic2", ("irq end %.2x\n", mask));
/*TODO*///			vic2.reg[0x19] &= ~0x80;
/*TODO*///			vic2.interrupt (0);
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	void vic2_lightpen_write (int level)
/*TODO*///	{
/*TODO*///		/* calculate current position, write it and raise interrupt */
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_timer_timeout (int which)
/*TODO*///	{
/*TODO*///		DBG_LOG (3, "vic2 ", ("timer %d timeout\n", which));
/*TODO*///		switch (which)
/*TODO*///		{
/*TODO*///		case 1:						   /* light pen */
/*TODO*///			/* and diode must recognize light */
/*TODO*///			if (1)
/*TODO*///			{
/*TODO*///				vic2.reg[0x13] = VIC2_X_VALUE;
/*TODO*///				vic2.reg[0x14] = VIC2_Y_VALUE;
/*TODO*///			}
/*TODO*///			vic2_set_interrupt (8);
/*TODO*///			break;
/*TODO*///		}
/*TODO*///	}
	
	public static int vic2_frame_interrupt ()
	{
		return ignore_interrupt.handler();
	}
	
/*TODO*///	WRITE_HANDLER ( vic2_port_w )
/*TODO*///	{
/*TODO*///		DBG_LOG (2, "vic write", ("%.2x:%.2x\n", offset, data));
/*TODO*///		offset &= 0x3f;
/*TODO*///		switch (offset)
/*TODO*///		{
/*TODO*///		case 1:
/*TODO*///		case 3:
/*TODO*///		case 5:
/*TODO*///		case 7:
/*TODO*///		case 9:
/*TODO*///		case 0xb:
/*TODO*///		case 0xd:
/*TODO*///		case 0xf:
/*TODO*///			/* sprite y positions */
/*TODO*///			if (vic2.reg[offset] != data) {
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.sprites[offset/2].y=SPRITE_Y_POS(offset/2);
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0:
/*TODO*///		case 2:
/*TODO*///		case 4:
/*TODO*///		case 6:
/*TODO*///		case 8:
/*TODO*///		case 0xa:
/*TODO*///		case 0xc:
/*TODO*///		case 0xe:
/*TODO*///			/* sprite x positions */
/*TODO*///			if (vic2.reg[offset] != data) {
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.sprites[offset/2].x=SPRITE_X_POS(offset/2);
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x10:
/*TODO*///			/* sprite x positions */
/*TODO*///			if (vic2.reg[offset] != data) {
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.sprites[0].x=SPRITE_X_POS(0);
/*TODO*///				vic2.sprites[1].x=SPRITE_X_POS(1);
/*TODO*///				vic2.sprites[2].x=SPRITE_X_POS(2);
/*TODO*///				vic2.sprites[3].x=SPRITE_X_POS(3);
/*TODO*///				vic2.sprites[4].x=SPRITE_X_POS(4);
/*TODO*///				vic2.sprites[5].x=SPRITE_X_POS(5);
/*TODO*///				vic2.sprites[6].x=SPRITE_X_POS(6);
/*TODO*///				vic2.sprites[7].x=SPRITE_X_POS(7);
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x17:						   /* sprite y size */
/*TODO*///		case 0x1d:						   /* sprite x size */
/*TODO*///		case 0x1b:						   /* sprite background priority */
/*TODO*///		case 0x1c:						   /* sprite multicolor mode select */
/*TODO*///		case 0x27:
/*TODO*///		case 0x28:						   /* sprite colors */
/*TODO*///		case 0x29:
/*TODO*///		case 0x2a:
/*TODO*///		case 0x2b:
/*TODO*///		case 0x2c:
/*TODO*///		case 0x2d:
/*TODO*///		case 0x2e:
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x25:
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.spritemulti[1] = Machine->pens[SPRITE_MULTICOLOR1];
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x26:
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.spritemulti[3] = Machine->pens[SPRITE_MULTICOLOR2];
/*TODO*///				if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x19:
/*TODO*///			vic2_clear_interrupt (data & 0xf);
/*TODO*///			break;
/*TODO*///		case 0x1a:						   /* irq mask */
/*TODO*///			vic2.reg[offset] = data;
/*TODO*///			vic2_set_interrupt(0); //beamrider needs this
/*TODO*///			break;
/*TODO*///		case 0x11:
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				if (LINES25)
/*TODO*///				{
/*TODO*///					vic2.y_begin = 0;
/*TODO*///					vic2.y_end = vic2.y_begin + 200;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.y_begin = 4;
/*TODO*///					vic2.y_end = vic2.y_begin + 192;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x12:
/*TODO*///			if (data != vic2.reg[offset])
/*TODO*///			{
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x16:
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				if (COLUMNS40)
/*TODO*///				{
/*TODO*///					vic2.x_begin = 0;
/*TODO*///					vic2.x_end = vic2.x_begin + 320;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.x_begin = HORICONTALPOS;
/*TODO*///					vic2.x_end = vic2.x_begin + 320;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x18:
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.videoaddr = VIDEOADDR;
/*TODO*///				vic2.chargenaddr = CHARGENADDR;
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x21:						   /* backgroundcolor */
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.mono[0] = vic2.bitmapmulti[0] = vic2.multi[0] =
/*TODO*///					vic2.colors[0] = Machine->pens[BACKGROUNDCOLOR];
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x22:						   /* background color 1 */
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.multi[1] = vic2.colors[1] = Machine->pens[MULTICOLOR1];
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x23:						   /* background color 2 */
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.multi[2] = vic2.colors[2] = Machine->pens[MULTICOLOR2];
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x24:						   /* background color 3 */
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///				vic2.colors[3] = Machine->pens[FOREGROUNDCOLOR];
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x20:						   /* framecolor */
/*TODO*///			if (vic2.reg[offset] != data)
/*TODO*///			{
/*TODO*///				if (vic2.on)
/*TODO*///					vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x2f:
/*TODO*///			if (vic2.vic2e)
/*TODO*///			{
/*TODO*///				DBG_LOG (2, "vic write", ("%.2x:%.2x\n", offset, data));
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x30:
/*TODO*///			if (vic2.vic2e)
/*TODO*///			{
/*TODO*///				vic2.reg[offset] = data;
/*TODO*///			}
/*TODO*///			break;
/*TODO*///		case 0x31:
/*TODO*///		case 0x32:
/*TODO*///		case 0x33:
/*TODO*///		case 0x34:
/*TODO*///		case 0x35:
/*TODO*///		case 0x36:
/*TODO*///		case 0x37:
/*TODO*///		case 0x38:
/*TODO*///		case 0x39:
/*TODO*///		case 0x3a:
/*TODO*///		case 0x3b:
/*TODO*///		case 0x3c:
/*TODO*///		case 0x3d:
/*TODO*///		case 0x3e:
/*TODO*///		case 0x3f:
/*TODO*///			vic2.reg[offset] = data;
/*TODO*///			DBG_LOG (2, "vic write", ("%.2x:%.2x\n", offset, data));
/*TODO*///			break;
/*TODO*///		default:
/*TODO*///			vic2.reg[offset] = data;
/*TODO*///			break;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	READ_HANDLER ( vic2_port_r )
/*TODO*///	{
/*TODO*///		int val = 0;
/*TODO*///		offset &= 0x3f;
/*TODO*///		switch (offset)
/*TODO*///		{
/*TODO*///		case 0x11:
/*TODO*///			val = (vic2.reg[offset] & ~0x80) | ((vic2.rasterline & 0x100) >> 1);
/*TODO*///			if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///			break;
/*TODO*///		case 0x12:
/*TODO*///			val = vic2.rasterline & 0xff;
/*TODO*///			if (vic2.on) vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///			break;
/*TODO*///		case 0x16:
/*TODO*///			val = vic2.reg[offset] | 0xc0;
/*TODO*///			break;
/*TODO*///		case 0x18:
/*TODO*///			val = vic2.reg[offset] | 1;
/*TODO*///			break;
/*TODO*///		case 0x19:						   /* interrupt flag register */
/*TODO*///			/*    vic2_clear_interrupt(0xf); */
/*TODO*///			val = vic2.reg[offset] | 0x70;
/*TODO*///			break;
/*TODO*///		case 0x1a:
/*TODO*///			val = vic2.reg[offset] | 0xf0;
/*TODO*///			break;
/*TODO*///		case 0x1e:						   /* sprite to sprite collision detect */
/*TODO*///			val = vic2.reg[offset];
/*TODO*///			vic2.reg[offset] = 0;
/*TODO*///			vic2_clear_interrupt (4);
/*TODO*///			break;
/*TODO*///		case 0x1f:						   /* sprite to background collision detect */
/*TODO*///			val = vic2.reg[offset];
/*TODO*///			vic2.reg[offset] = 0;
/*TODO*///			vic2_clear_interrupt (2);
/*TODO*///			break;
/*TODO*///		case 0x20:
/*TODO*///		case 0x21:
/*TODO*///		case 0x22:
/*TODO*///		case 0x23:
/*TODO*///		case 0x24:
/*TODO*///		case 0x25:
/*TODO*///		case 0x26:
/*TODO*///		case 0x27:
/*TODO*///		case 0x28:
/*TODO*///		case 0x29:
/*TODO*///		case 0x2a:
/*TODO*///		case 0x2b:
/*TODO*///		case 0x2c:
/*TODO*///		case 0x2d:
/*TODO*///		case 0x2e:
/*TODO*///			val = vic2.reg[offset];
/*TODO*///			break;
/*TODO*///		case 0x2f:
/*TODO*///		case 0x30:
/*TODO*///			if (vic2.vic2e) {
/*TODO*///				val = vic2.reg[offset];
/*TODO*///				DBG_LOG (2, "vic read", ("%.2x:%.2x\n", offset, val));
/*TODO*///			} else
/*TODO*///				val = 0xff;
/*TODO*///			break;
/*TODO*///		case 0x31:
/*TODO*///		case 0x32:
/*TODO*///		case 0x33:
/*TODO*///		case 0x34:
/*TODO*///		case 0x35:
/*TODO*///		case 0x36:
/*TODO*///		case 0x37:
/*TODO*///		case 0x38:
/*TODO*///		case 0x39:
/*TODO*///		case 0x3a:
/*TODO*///		case 0x3b:
/*TODO*///		case 0x3c:
/*TODO*///		case 0x3d:
/*TODO*///		case 0x3e:
/*TODO*///		case 0x3f:						   /* not used */
/*TODO*///			val = vic2.reg[offset];
/*TODO*///			DBG_LOG (2, "vic read", ("%.2x:%.2x\n", offset, val));
/*TODO*///			break;
/*TODO*///		default:
/*TODO*///			val = vic2.reg[offset];
/*TODO*///		}
/*TODO*///		if ((offset != 0x11) && (offset != 0x12))
/*TODO*///			DBG_LOG (2, "vic read", ("%.2x:%.2x\n", offset, val));
/*TODO*///		return val;
/*TODO*///	}
/*TODO*///	
	public static VhStartPtr vic2_vh_start = new VhStartPtr() {
            public int handler() {
		int i;
	
		vic2.bitmap = Machine.scrbitmap;
	
		if (vic2.vic3) {
			vic2.screen[0] = new UBytePtr(216 * 656 / 8);
	
			if (vic2.screen[0]==null)
				return 1;
			for (i = 1; i < 216; i++)
				vic2.screen[i] = new UBytePtr(vic2.screen[i - 1], 656 / 8);
		} else {
			vic2.screen[0] = new UBytePtr(216 * 336 / 8);
	
			if (vic2.screen[0]==null)
				return 1;
			for (i = 1; i < 216; i++)
				vic2.screen[i] = new UBytePtr(vic2.screen[i - 1], 336 / 8);
		}
	
		for (i = 0; i < 256; i++)
		{
			vic2.foreground[i] = 0;
			if ((i & 3) > 1)
				vic2.foreground[i] |= 0x3;
			if ((i & 0xc) > 0x4)
				vic2.foreground[i] |= 0xc;
			if ((i & 0x30) > 0x10)
				vic2.foreground[i] |= 0x30;
			if ((i & 0xc0) > 0x40)
				vic2.foreground[i] |= 0xc0;
		}
		for (i = 0; i < 256; i++)
		{
			vic2.expandx[i] = 0;
			if ((i & 1) != 0)
				vic2.expandx[i] |= 3;
			if ((i & 2) != 0)
				vic2.expandx[i] |= 0xc;
			if ((i & 4) != 0)
				vic2.expandx[i] |= 0x30;
			if ((i & 8) != 0)
				vic2.expandx[i] |= 0xc0;
			if ((i & 0x10) != 0)
				vic2.expandx[i] |= 0x300;
			if ((i & 0x20) != 0)
				vic2.expandx[i] |= 0xc00;
			if ((i & 0x40) != 0)
				vic2.expandx[i] |= 0x3000;
			if ((i & 0x80) != 0)
				vic2.expandx[i] |= 0xc000;
		}
		for (i = 0; i < 256; i++)
		{
			vic2.expandx_multi[i] = 0;
			if ((i & 1) != 0)
				vic2.expandx_multi[i] |= 5;
			if ((i & 2) != 0)
				vic2.expandx_multi[i] |= 0xa;
			if ((i & 4) != 0)
				vic2.expandx_multi[i] |= 0x50;
			if ((i & 8) != 0)
				vic2.expandx_multi[i] |= 0xa0;
			if ((i & 0x10) != 0)
				vic2.expandx_multi[i] |= 0x500;
			if ((i & 0x20) != 0)
				vic2.expandx_multi[i] |= 0xa00;
			if ((i & 0x40) != 0)
				vic2.expandx_multi[i] |= 0x5000;
			if ((i & 0x80) != 0)
				vic2.expandx_multi[i] |= 0xa000;
		}
		for (i = 0; i < 256; i++)
		{
			vic2.multi_collision[i] = 0;
			if ((i & 3) != 0)
				vic2.multi_collision[i] |= 3;
			if ((i & 0xc) != 0)
				vic2.multi_collision[i] |= 0xc;
			if ((i & 0x30) != 0)
				vic2.multi_collision[i] |= 0x30;
			if ((i & 0xc0) != 0)
				vic2.multi_collision[i] |= 0xc0;
		}
		return 0;
            }
        };
	
	public static VhStopPtr vic2_vh_stop = new VhStopPtr() {
            public void handler() {
                vic2.screen[0] = null;
            }
        };
	
/*TODO*///	static void vic2_draw_character (int ybegin, int yend, int ch,
/*TODO*///									 int yoff, int xoff, UINT16 *color)
/*TODO*///	{
/*TODO*///		int y, code;
/*TODO*///	
/*TODO*///	/*	if (Machine->color_depth == 8)
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read (vic2.chargenaddr + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = code;
/*TODO*///				vic2.bitmap->line[y + yoff][xoff] = color[code >> 7];
/*TODO*///				vic2.bitmap->line[y + yoff][1 + xoff] = color[(code >> 6) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][2 + xoff] = color[(code >> 5) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][3 + xoff] = color[(code >> 4) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][4 + xoff] = color[(code >> 3) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][5 + xoff] = color[(code >> 2) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][6 + xoff] = color[(code >> 1) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][7 + xoff] = color[code & 1];
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///	*/	{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read (vic2.chargenaddr + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = code;
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff) = color[code >> 7];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 1 + xoff) = color[(code >> 6) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 2 + xoff) = color[(code >> 5) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 3 + xoff) = color[(code >> 4) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 4 + xoff) = color[(code >> 3) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 5 + xoff) = color[(code >> 2) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 6 + xoff) = color[(code >> 1) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 7 + xoff) = color[code & 1];
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_character_multi (int ybegin, int yend, int ch,
/*TODO*///										   int yoff, int xoff)
/*TODO*///	{
/*TODO*///		int y, code;
/*TODO*///	
/*TODO*///	/*	if (Machine->color_depth == 8)
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read (vic2.chargenaddr + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = vic2.foreground[code];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 1] = vic2.multi[code >> 6];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff + 2] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 3] = vic2.multi[(code >> 4) & 3];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff + 4] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 5] = vic2.multi[(code >> 2) & 3];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff + 6] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 7] = vic2.multi[code & 3];
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///	*/	{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read (vic2.chargenaddr + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = vic2.foreground[code];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 1) = vic2.multi[code >> 6];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff + 2) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 3) = vic2.multi[(code >> 4) & 3];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff + 4) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 5) = vic2.multi[(code >> 2) & 3];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff + 6) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 7) = vic2.multi[code & 3];
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_bitmap (int ybegin, int yend,
/*TODO*///								  int ch, int yoff, int xoff)
/*TODO*///	{
/*TODO*///		int y, code;
/*TODO*///	
/*TODO*///	/*	if (Machine->color_depth == 8)
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read ((vic2.chargenaddr&0x2000) + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = code;
/*TODO*///				vic2.bitmap->line[y + yoff][xoff] = vic2.c64_bitmap[code >> 7];
/*TODO*///				vic2.bitmap->line[y + yoff][1 + xoff] = vic2.c64_bitmap[(code >> 6) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][2 + xoff] = vic2.c64_bitmap[(code >> 5) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][3 + xoff] = vic2.c64_bitmap[(code >> 4) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][4 + xoff] = vic2.c64_bitmap[(code >> 3) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][5 + xoff] = vic2.c64_bitmap[(code >> 2) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][6 + xoff] = vic2.c64_bitmap[(code >> 1) & 1];
/*TODO*///				vic2.bitmap->line[y + yoff][7 + xoff] = vic2.c64_bitmap[code & 1];
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///	*/	{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read ((vic2.chargenaddr&0x2000) + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = code;
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff) = vic2.c64_bitmap[code >> 7];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 1 + xoff) = vic2.c64_bitmap[(code >> 6) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 2 + xoff) = vic2.c64_bitmap[(code >> 5) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 3 + xoff) = vic2.c64_bitmap[(code >> 4) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 4 + xoff) = vic2.c64_bitmap[(code >> 3) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 5 + xoff) = vic2.c64_bitmap[(code >> 2) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 6 + xoff) = vic2.c64_bitmap[(code >> 1) & 1];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + 7 + xoff) = vic2.c64_bitmap[code & 1];
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_bitmap_multi (int ybegin, int yend,
/*TODO*///										int ch, int yoff, int xoff)
/*TODO*///	{
/*TODO*///		int y, code;
/*TODO*///	
/*TODO*///	/*	if (Machine->color_depth == 8)
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read ((vic2.chargenaddr&0x2000)+ ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = vic2.foreground[code];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 1] = vic2.bitmapmulti[code >> 6];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff + 2] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 3] = vic2.bitmapmulti[(code >> 4) & 3];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff + 4] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 5] = vic2.bitmapmulti[(code >> 2) & 3];
/*TODO*///				vic2.bitmap->line[y + yoff][xoff + 6] =
/*TODO*///					vic2.bitmap->line[y + yoff][xoff + 7] = vic2.bitmapmulti[code & 3];
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///	*/	{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				code = vic2.dma_read ((vic2.chargenaddr&0x2000) + ch * 8 + y);
/*TODO*///				vic2.screen[y + yoff][xoff >> 3] = vic2.foreground[code];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 1) = vic2.bitmapmulti[code >> 6];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff + 2) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 3) = vic2.bitmapmulti[(code >> 4) & 3];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff + 4) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 5) = vic2.bitmapmulti[(code >> 2) & 3];
/*TODO*///				*((short *) vic2.bitmap->line[y + yoff] + xoff + 6) =
/*TODO*///					*((short *) vic2.bitmap->line[y + yoff] + xoff + 7) = vic2.bitmapmulti[code & 3];
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_sprite_code_multi (int y, int xbegin,
/*TODO*///											 int code, int prior)
/*TODO*///	{
/*TODO*///		register int x, mask, shift;
/*TODO*///	
/*TODO*///		if ((y < YPOS) || (y >= 208) || (xbegin <= 1) || (xbegin >= 328))
/*TODO*///			return;
/*TODO*///	/*	if (Machine->color_depth == 8)
/*TODO*///		{
/*TODO*///			for (x = 0, mask = 0xc0, shift = 6; x < 8; x += 2, mask >>= 2, shift -= 2)
/*TODO*///			{
/*TODO*///				if (code & mask)
/*TODO*///				{
/*TODO*///					switch ((prior & mask) >> shift)
/*TODO*///					{
/*TODO*///					case 1:
/*TODO*///						vic2.bitmap->line[y][xbegin + x + 1] =
/*TODO*///							vic2.spritemulti[(code >> shift) & 3];
/*TODO*///						break;
/*TODO*///					case 2:
/*TODO*///						vic2.bitmap->line[y][xbegin + x] =
/*TODO*///							vic2.spritemulti[(code >> shift) & 3];
/*TODO*///						break;
/*TODO*///					case 3:
/*TODO*///						vic2.bitmap->line[y][xbegin + x] =
/*TODO*///							vic2.bitmap->line[y][xbegin + x + 1] =
/*TODO*///							vic2.spritemulti[(code >> shift) & 3];
/*TODO*///						break;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///	*/	{
/*TODO*///			for (x = 0, mask = 0xc0, shift = 6; x < 8; x += 2, mask >>= 2, shift -= 2)
/*TODO*///			{
/*TODO*///				if (code & mask)
/*TODO*///				{
/*TODO*///					switch ((prior & mask) >> shift)
/*TODO*///					{
/*TODO*///					case 1:
/*TODO*///						((short *) vic2.bitmap->line[y])[xbegin + x + 1] =
/*TODO*///							vic2.spritemulti[(code >> shift) & 3];
/*TODO*///						break;
/*TODO*///					case 2:
/*TODO*///						((short *) vic2.bitmap->line[y])[xbegin + x] =
/*TODO*///							vic2.spritemulti[(code >> shift) & 3];
/*TODO*///						break;
/*TODO*///					case 3:
/*TODO*///						((short *) vic2.bitmap->line[y])[xbegin + x] =
/*TODO*///							((short *) vic2.bitmap->line[y])[xbegin + x + 1] =
/*TODO*///							vic2.spritemulti[(code >> shift) & 3];
/*TODO*///						break;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_sprite_code (int y, int xbegin, int code, int color)
/*TODO*///	{
/*TODO*///		register int mask, x;
/*TODO*///	
/*TODO*///		if ((y < YPOS) || (y >= 208) || (xbegin <= 1) || (xbegin >= 328))
/*TODO*///			return;
/*TODO*///	/*	if (Machine->color_depth == 8)
/*TODO*///		{
/*TODO*///			for (x = 0, mask = 0x80; x < 8; x++, mask >>= 1)
/*TODO*///			{
/*TODO*///				if (code & mask)
/*TODO*///				{
/*TODO*///					vic2.bitmap->line[y][xbegin + x] = color;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///	*/	{
/*TODO*///			for (x = 0, mask = 0x80; x < 8; x++, mask >>= 1)
/*TODO*///			{
/*TODO*///				if (code & mask)
/*TODO*///				{
/*TODO*///					((short *) vic2.bitmap->line[y])[xbegin + x] = color;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_sprite_collision (int nr, int y, int x, int mask)
/*TODO*///	{
/*TODO*///		int i, value, xdiff;
/*TODO*///	
/*TODO*///		for (i = 7; i > nr; i--)
/*TODO*///		{
/*TODO*///			if (!SPRITEON (i)
/*TODO*///				|| !vic2.sprites[i].paintedline[y]
/*TODO*///				|| (SPRITE_COLLISION (i) && SPRITE_COLLISION (nr)))
/*TODO*///				continue;
/*TODO*///			if ((x + 7 < SPRITE_X_POS (i))
/*TODO*///				|| (x >= SPRITE_X_POS (i) + SPRITE_X_SIZE (i)))
/*TODO*///				continue;
/*TODO*///			xdiff = x - SPRITE_X_POS (i);
/*TODO*///			if ((x & 7) == (SPRITE_X_POS (i) & 7))
/*TODO*///				value = vic2.sprites[i].bitmap[y][xdiff >> 3];
/*TODO*///			else if (xdiff < 0)
/*TODO*///				value = vic2.sprites[i].bitmap[y][0] >> (-xdiff);
/*TODO*///			else {
/*TODO*///				UINT8 *vp = vic2.sprites[i].bitmap[y]+(xdiff>>3);
/*TODO*///				value = ((vp[1] | (*vp << 8)) >> (8 - (xdiff&7) )) & 0xff;
/*TODO*///			}
/*TODO*///			if (value & mask)
/*TODO*///			{
/*TODO*///				SPRITE_SET_COLLISION (i);
/*TODO*///				SPRITE_SET_COLLISION (nr);
/*TODO*///				vic2_set_interrupt (4);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_sprite_multi (int nr, int yoff, int ybegin, int yend)
/*TODO*///	{
/*TODO*///		int y, i, prior, addr, xbegin, collision;
/*TODO*///		int value, value2, value3 = 0, bg, color[2];
/*TODO*///	
/*TODO*///		xbegin = SPRITE_X_POS (nr);
/*TODO*///		addr = vic2.dma_read (SPRITE_ADDR (nr)) << 6;
/*TODO*///		vic2.spritemulti[2] = Machine->pens[SPRITE_COLOR (nr)];
/*TODO*///		prior = SPRITE_PRIORITY (nr);
/*TODO*///		collision = SPRITE_BG_COLLISION (nr);
/*TODO*///		color[0] = Machine->pens[0];
/*TODO*///		color[1] = Machine->pens[1];
/*TODO*///	
/*TODO*///		if (SPRITE_X_EXPAND (nr))
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				vic2.sprites[nr].paintedline[y] = 1;
/*TODO*///				for (i = 0; i < 3; i++)
/*TODO*///				{
/*TODO*///					value = vic2.expandx_multi[bg = vic2.dma_read (addr + vic2.sprites[nr].line * 3 + i)];
/*TODO*///					value2 = vic2.expandx[vic2.multi_collision[bg]];
/*TODO*///					vic2.sprites[nr].bitmap[y][i*2] = value2>>8;
/*TODO*///					vic2.sprites[nr].bitmap[y][i*2+1] = value2&0xff;
/*TODO*///					vic2_sprite_collision (nr, y, xbegin + i * 16, value2 >> 8);
/*TODO*///					vic2_sprite_collision (nr, y, xbegin + i * 16 + 8, value2 & 0xff);
/*TODO*///					if (prior || !collision)
/*TODO*///					{
/*TODO*///						value3 = vic2_getforeground16 (yoff + y, xbegin + i * 16);
/*TODO*///					}
/*TODO*///					if (!collision && (value2 & value3))
/*TODO*///					{
/*TODO*///						collision = 1;
/*TODO*///						SPRITE_SET_BG_COLLISION (nr);
/*TODO*///						vic2_set_interrupt (2);
/*TODO*///					}
/*TODO*///					if (prior)
/*TODO*///					{
/*TODO*///						vic2_draw_sprite_code_multi (yoff + y, xbegin + i * 16, value >> 8,
/*TODO*///													 (value3 >> 8) ^ 0xff);
/*TODO*///						vic2_draw_sprite_code_multi (yoff + y, xbegin + i * 16 + 8, value & 0xff,
/*TODO*///													 (value3 & 0xff) ^ 0xff);
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						vic2_draw_sprite_code_multi (yoff + y, xbegin + i * 16, value >> 8, 0xff);
/*TODO*///						vic2_draw_sprite_code_multi (yoff + y, xbegin + i * 16 + 8, value & 0xff, 0xff);
/*TODO*///					}
/*TODO*///				}
/*TODO*///				vic2.sprites[nr].bitmap[y][i*2]=0; //easier sprite collision detection
/*TODO*///				if (SPRITE_Y_EXPAND (nr))
/*TODO*///				{
/*TODO*///					if (vic2.sprites[nr].repeat)
/*TODO*///					{
/*TODO*///						vic2.sprites[nr].line++;
/*TODO*///						vic2.sprites[nr].repeat = 0;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						vic2.sprites[nr].repeat = 1;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.sprites[nr].line++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				vic2.sprites[nr].paintedline[y] = 1;
/*TODO*///				for (i = 0; i < 3; i++)
/*TODO*///				{
/*TODO*///					value = vic2.dma_read (addr + vic2.sprites[nr].line * 3 + i);
/*TODO*///					vic2.sprites[nr].bitmap[y][i] =
/*TODO*///						value2 = vic2.multi_collision[value];
/*TODO*///					vic2_sprite_collision (nr, y, xbegin + i * 8, value2);
/*TODO*///					if (prior || !collision)
/*TODO*///					{
/*TODO*///						value3 = vic2_getforeground (yoff + y, xbegin + i * 8);
/*TODO*///					}
/*TODO*///					if (!collision && (value2 & value3))
/*TODO*///					{
/*TODO*///						collision = 1;
/*TODO*///						SPRITE_SET_BG_COLLISION (nr);
/*TODO*///						vic2_set_interrupt (2);
/*TODO*///					}
/*TODO*///					if (prior)
/*TODO*///					{
/*TODO*///						vic2_draw_sprite_code_multi (yoff + y, xbegin + i * 8, value, value3 ^ 0xff);
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						vic2_draw_sprite_code_multi (yoff + y, xbegin + i * 8, value, 0xff);
/*TODO*///					}
/*TODO*///				}
/*TODO*///				vic2.sprites[nr].bitmap[y][i]=0; //easier sprite collision detection
/*TODO*///				if (SPRITE_Y_EXPAND (nr))
/*TODO*///				{
/*TODO*///					if (vic2.sprites[nr].repeat)
/*TODO*///					{
/*TODO*///						vic2.sprites[nr].line++;
/*TODO*///						vic2.sprites[nr].repeat = 0;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						vic2.sprites[nr].repeat = 1;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.sprites[nr].line++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic2_draw_sprite (int nr, int yoff, int ybegin, int yend)
/*TODO*///	{
/*TODO*///		int y, i, addr, xbegin, color, prior, collision;
/*TODO*///		int value, value3 = 0;
/*TODO*///	
/*TODO*///		xbegin = SPRITE_X_POS (nr);
/*TODO*///		addr = vic2.dma_read (SPRITE_ADDR (nr)) << 6;
/*TODO*///		color = Machine->pens[SPRITE_COLOR (nr)];
/*TODO*///		prior = SPRITE_PRIORITY (nr);
/*TODO*///		collision = SPRITE_BG_COLLISION (nr);
/*TODO*///	
/*TODO*///		if (SPRITE_X_EXPAND (nr))
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				vic2.sprites[nr].paintedline[y] = 1;
/*TODO*///				for (i = 0; i < 3; i++)
/*TODO*///				{
/*TODO*///					value = vic2.expandx[vic2.dma_read (addr + vic2.sprites[nr].line * 3 + i)];
/*TODO*///					vic2.sprites[nr].bitmap[y][i*2] = value>>8;
/*TODO*///					vic2.sprites[nr].bitmap[y][i*2+1] = value&0xff;
/*TODO*///					vic2_sprite_collision (nr, y, xbegin + i * 16, value >> 8);
/*TODO*///					vic2_sprite_collision (nr, y, xbegin + i * 16 + 8, value & 0xff);
/*TODO*///					if (prior || !collision)
/*TODO*///						value3 = vic2_getforeground16 (yoff + y, xbegin + i * 16);
/*TODO*///					if (!collision && (value & value3))
/*TODO*///					{
/*TODO*///						collision = 1;
/*TODO*///						SPRITE_SET_BG_COLLISION (nr);
/*TODO*///						vic2_set_interrupt (2);
/*TODO*///					}
/*TODO*///					if (prior)
/*TODO*///						value &= ~value3;
/*TODO*///					vic2_draw_sprite_code (yoff + y, xbegin + i * 16, value >> 8, color);
/*TODO*///					vic2_draw_sprite_code (yoff + y, xbegin + i * 16 + 8, value & 0xff, color);
/*TODO*///				}
/*TODO*///				vic2.sprites[nr].bitmap[y][i*2]=0; //easier sprite collision detection
/*TODO*///				if (SPRITE_Y_EXPAND (nr))
/*TODO*///				{
/*TODO*///					if (vic2.sprites[nr].repeat)
/*TODO*///					{
/*TODO*///						vic2.sprites[nr].line++;
/*TODO*///						vic2.sprites[nr].repeat = 0;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						vic2.sprites[nr].repeat = 1;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.sprites[nr].line++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			for (y = ybegin; y <= yend; y++)
/*TODO*///			{
/*TODO*///				vic2.sprites[nr].paintedline[y] = 1;
/*TODO*///				for (i = 0; i < 3; i++)
/*TODO*///				{
/*TODO*///					value = vic2.dma_read (addr + vic2.sprites[nr].line * 3 + i);
/*TODO*///					vic2.sprites[nr].bitmap[y][i] = value;
/*TODO*///					vic2_sprite_collision (nr, y, xbegin + i * 8, value);
/*TODO*///					if (prior || !collision)
/*TODO*///						value3 = vic2_getforeground (yoff + y, xbegin + i * 8);
/*TODO*///					if (!collision && (value & value3))
/*TODO*///					{
/*TODO*///						collision = 1;
/*TODO*///						SPRITE_SET_BG_COLLISION (nr);
/*TODO*///						vic2_set_interrupt (2);
/*TODO*///					}
/*TODO*///					if (prior)
/*TODO*///						value &= ~value3;
/*TODO*///					vic2_draw_sprite_code (yoff + y, xbegin + i * 8, value, color);
/*TODO*///				}
/*TODO*///				vic2.sprites[nr].bitmap[y][i]=0; //easier sprite collision detection
/*TODO*///				if (SPRITE_Y_EXPAND (nr))
/*TODO*///				{
/*TODO*///					if (vic2.sprites[nr].repeat)
/*TODO*///					{
/*TODO*///						vic2.sprites[nr].line++;
/*TODO*///						vic2.sprites[nr].repeat = 0;
/*TODO*///					}
/*TODO*///					else
/*TODO*///						vic2.sprites[nr].repeat = 1;
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.sprites[nr].line++;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void vic3_drawlines (int first, int last);
/*TODO*///	
/*TODO*///	static void vic2_drawlines (int first, int last)
/*TODO*///	{
/*TODO*///		int line, vline, end;
/*TODO*///		int attr, ch, ecm;
/*TODO*///		int syend;
/*TODO*///		int offs, yoff, xoff, ybegin, yend, xbegin, xend;
/*TODO*///		int i, j;
/*TODO*///	
/*TODO*///		if (vic2.vic3 && VIC3_BITPLANES) return ;
/*TODO*///		/* temporary allowing vic3 displaying 80 columns */
/*TODO*///		if (vic2.vic3&&(vic2.reg[0x31]&0x80)) {
/*TODO*///			vic3_drawlines(first,last);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (first == last)
/*TODO*///			return;
/*TODO*///		vic2.lastline = last;
/*TODO*///		if (osd_skip_this_frame ())
/*TODO*///			return;
/*TODO*///	
/*TODO*///	
/*TODO*///		/* top part of display not rastered */
/*TODO*///		first -= VIC2_YPOS - YPOS;
/*TODO*///		last -= VIC2_YPOS - YPOS;
/*TODO*///		if ((first >= last) || (last <= 0))
/*TODO*///		{
/*TODO*///			for (i = 0; i < 8; i++)
/*TODO*///				vic2.sprites[i].repeat = vic2.sprites[i].line = 0;
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		if (first < 0)
/*TODO*///			first = 0;
/*TODO*///	
/*TODO*///		if (SCREENON == 0)
/*TODO*///		{
/*TODO*///	/*		if (Machine->color_depth == 8)
/*TODO*///			{
/*TODO*///				for (line = first; (line < last) && (line < vic2.bitmap->height); line++)
/*TODO*///					memset (vic2.bitmap->line[line], Machine->pens[0], vic2.bitmap->width);
/*TODO*///			}
/*TODO*///			else
/*TODO*///	*/		{
/*TODO*///				for (line = first; (line < last) && (line < vic2.bitmap->height); line++)
/*TODO*///					memset16 (vic2.bitmap->line[line], Machine->pens[0], vic2.bitmap->width);
/*TODO*///			}
/*TODO*///			return;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (COLUMNS40)
/*TODO*///			xbegin = XPOS, xend = xbegin + 320;
/*TODO*///		else
/*TODO*///			xbegin = XPOS + 7, xend = xbegin + 304;
/*TODO*///	
/*TODO*///		if (last < vic2.y_begin)
/*TODO*///			end = last;
/*TODO*///		else
/*TODO*///			end = vic2.y_begin + YPOS;
/*TODO*///	
/*TODO*///		line=first;
/*TODO*///		if (line<end) {
/*TODO*///			plot_box(vic2.bitmap, 0, line, vic2.bitmap->width, end-line,
/*TODO*///					 Machine->pens[FRAMECOLOR]);
/*TODO*///			line=end;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (LINES25)
/*TODO*///		{
/*TODO*///			vline = line - vic2.y_begin - YPOS;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			vline = line - vic2.y_begin - YPOS + 8 - VERTICALPOS;
/*TODO*///		}
/*TODO*///		if (last < vic2.y_end + YPOS)
/*TODO*///			end = last;
/*TODO*///		else
/*TODO*///			end = vic2.y_end + YPOS;
/*TODO*///	
/*TODO*///		for (; line < end; vline = (vline + 8) & ~7, line = line + 1 + yend - ybegin)
/*TODO*///		{
/*TODO*///			offs = (vline >> 3) * 40;
/*TODO*///			ybegin = vline & 7;
/*TODO*///			yoff = line - ybegin;
/*TODO*///			yend = (yoff + 7 < end) ? 7 : (end - yoff - 1);
/*TODO*///			/* rendering 39 characters */
/*TODO*///			/* left and right borders are overwritten later */
/*TODO*///			vic2.shift[line] = HORICONTALPOS;
/*TODO*///	
/*TODO*///			for (xoff = vic2.x_begin + XPOS; xoff < vic2.x_end + XPOS; xoff += 8, offs++)
/*TODO*///			{
/*TODO*///				ch = vic2.dma_read (vic2.videoaddr + offs);
/*TODO*///	#if 0
/*TODO*///				attr = vic2.dma_read_color (vic2.videoaddr + offs);
/*TODO*///	#else
/*TODO*///				/* temporaery until vic3 finished */
/*TODO*///				attr = vic2.dma_read_color ((vic2.videoaddr + offs)&0x3ff)&0x0f;
/*TODO*///	#endif
/*TODO*///				if (HIRESON)
/*TODO*///				{
/*TODO*///					vic2.bitmapmulti[1] = vic2.c64_bitmap[1] = Machine->pens[ch >> 4];
/*TODO*///					vic2.bitmapmulti[2] = vic2.c64_bitmap[0] = Machine->pens[ch & 0xf];
/*TODO*///					if (MULTICOLORON)
/*TODO*///					{
/*TODO*///					    vic2.bitmapmulti[3] = Machine->pens[attr];
/*TODO*///						vic2_draw_bitmap_multi (ybegin, yend, offs, yoff, xoff);
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						vic2_draw_bitmap (ybegin, yend, offs, yoff, xoff);
/*TODO*///					}
/*TODO*///				}
/*TODO*///				else if (ECMON)
/*TODO*///				{
/*TODO*///					ecm = ch >> 6;
/*TODO*///					vic2.ecmcolor[0] = vic2.colors[ecm];
/*TODO*///					vic2.ecmcolor[1] = Machine->pens[attr];
/*TODO*///					vic2_draw_character (ybegin, yend, ch & ~0xC0, yoff, xoff, vic2.ecmcolor);
/*TODO*///				}
/*TODO*///				else if (MULTICOLORON && (attr & 8))
/*TODO*///				{
/*TODO*///					vic2.multi[3] = Machine->pens[attr & 7];
/*TODO*///					vic2_draw_character_multi (ybegin, yend, ch, yoff, xoff);
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					vic2.mono[1] = Machine->pens[attr];
/*TODO*///					vic2_draw_character (ybegin, yend, ch, yoff, xoff, vic2.mono);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			/* sprite priority, sprite overwrites lowerprior pixels */
/*TODO*///			for (i = 7; i >= 0; i--)
/*TODO*///			{
/*TODO*///				if (vic2.sprites[i].line || vic2.sprites[i].repeat)
/*TODO*///				{
/*TODO*///					syend = yend;
/*TODO*///					if (SPRITE_Y_EXPAND (i))
/*TODO*///					{
/*TODO*///						if ((21 - vic2.sprites[i].line) * 2 - vic2.sprites[i].repeat < yend - ybegin + 1)
/*TODO*///							syend = ybegin + (21 - vic2.sprites[i].line) * 2 - vic2.sprites[i].repeat - 1;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						if (vic2.sprites[i].line + yend - ybegin + 1 > 20)
/*TODO*///							syend = ybegin + 20 - vic2.sprites[i].line;
/*TODO*///					}
/*TODO*///					if (yoff + syend > YPOS + 200)
/*TODO*///						syend = YPOS + 200 - yoff - 1;
/*TODO*///					if (SPRITE_MULTICOLOR (i))
/*TODO*///						vic2_draw_sprite_multi (i, yoff, ybegin, syend);
/*TODO*///					else
/*TODO*///						vic2_draw_sprite (i, yoff, ybegin, syend);
/*TODO*///					if ((syend != yend) || (vic2.sprites[i].line > 20))
/*TODO*///					{
/*TODO*///						vic2.sprites[i].line = vic2.sprites[i].repeat = 0;
/*TODO*///						for (j = syend; j <= yend; j++)
/*TODO*///							vic2.sprites[i].paintedline[j] = 0;
/*TODO*///					}
/*TODO*///				}
/*TODO*///				else if (SPRITEON (i) && (yoff + ybegin <= SPRITE_Y_POS (i))
/*TODO*///						 && (yoff + yend >= SPRITE_Y_POS (i)))
/*TODO*///				{
/*TODO*///					syend = yend;
/*TODO*///					if (SPRITE_Y_EXPAND (i))
/*TODO*///					{
/*TODO*///						if (21 * 2 < yend - ybegin + 1)
/*TODO*///							syend = ybegin + 21 * 2 - 1;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						if (yend - ybegin + 1 > 21)
/*TODO*///							syend = ybegin + 21 - 1;
/*TODO*///					}
/*TODO*///					if (yoff + syend >= YPOS + 200)
/*TODO*///						syend = YPOS + 200 - yoff - 1;
/*TODO*///					for (j = 0; j < SPRITE_Y_POS (i) - yoff; j++)
/*TODO*///						vic2.sprites[i].paintedline[j] = 0;
/*TODO*///					if (SPRITE_MULTICOLOR (i))
/*TODO*///						vic2_draw_sprite_multi (i, yoff, SPRITE_Y_POS (i) - yoff, syend);
/*TODO*///					else
/*TODO*///						vic2_draw_sprite (i, yoff, SPRITE_Y_POS (i) - yoff, syend);
/*TODO*///					if ((syend != yend) || (vic2.sprites[i].line > 20))
/*TODO*///					{
/*TODO*///						for (j = syend; j <= yend; j++)
/*TODO*///							vic2.sprites[i].paintedline[j] = 0;
/*TODO*///						vic2.sprites[i].line = vic2.sprites[i].repeat = 0;
/*TODO*///					}
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					memset (vic2.sprites[i].paintedline, 0, sizeof (vic2.sprites[i].paintedline));
/*TODO*///				}
/*TODO*///			}
/*TODO*///			plot_box(vic2.bitmap, 0, yoff+ybegin, xbegin, yend-ybegin+1,
/*TODO*///					 Machine->pens[FRAMECOLOR]);
/*TODO*///			plot_box(vic2.bitmap, xend, yoff+ybegin, vic2.bitmap->width - xend, yend-ybegin+1,
/*TODO*///					 Machine->pens[FRAMECOLOR]);
/*TODO*///		}
/*TODO*///		if (last < vic2.bitmap->height)
/*TODO*///			end = last;
/*TODO*///		else
/*TODO*///			end = vic2.bitmap->height;
/*TODO*///		if (line<end) {
/*TODO*///			plot_box(vic2.bitmap, 0, line, vic2.bitmap->width, end-line,
/*TODO*///					 Machine->pens[FRAMECOLOR]);
/*TODO*///			line=end;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
	public static InterruptPtr vic2_raster_irq = new InterruptPtr() {
            @Override
            public int handler() {
/*TODO*///		int i;
/*TODO*///	
/*TODO*///		vic2.rasterline++;
/*TODO*///		if (vic2.rasterline >= vic2.lines)
/*TODO*///		{
/*TODO*///			vic2.rasterline = 0;
/*TODO*///			if (vic2.on) vic2_drawlines (vic2.lastline, vic2.lines);
/*TODO*///	
/*TODO*///			for (i = 0; i < 8; i++)
/*TODO*///				vic2.sprites[i].repeat = vic2.sprites[i].line = 0;
/*TODO*///			vic2.lastline = 0;
/*TODO*///			if (LIGHTPEN_BUTTON)
/*TODO*///			{
/*TODO*///				double tme = 0.0;
/*TODO*///	
/*TODO*///				/* lightpen timer starten */
/*TODO*///				vic2.lightpentimer = timer_set (tme, 1, vic2_timer_timeout);
/*TODO*///			}
/*TODO*///			//state_display(vic2.bitmap);
/*TODO*///		}
/*TODO*///		if (vic2.rasterline == C64_2_RASTERLINE (RASTERLINE))
/*TODO*///		{
/*TODO*///			if (vic2.on)
/*TODO*///				vic2_drawlines (vic2.lastline, vic2.rasterline);
/*TODO*///			vic2_set_interrupt (1);
/*TODO*///		}
		return ignore_interrupt.handler();
            }
        };
	
	public static VhUpdatePtr vic2_vh_screenrefresh = new VhUpdatePtr() {
            public void handler(mame_bitmap bitmap, int full_refresh) {
/*TODO*///	#if 0
/*TODO*///	    char text[40];
/*TODO*///	    int i, y;
/*TODO*///	    for (y=0, i=0; i<8; i++) {
/*TODO*///		if (SPRITEON(i)) {
/*TODO*///		    sprintf(text,"%d x:%d y:%d",i,
/*TODO*///			    SPRITE_X_POS(i), SPRITE_Y_POS(i) );
/*TODO*///	
/*TODO*///		    ui_text(bitmap,text,0,y);
/*TODO*///		    y+=8;
/*TODO*///		}
/*TODO*///	    }
/*TODO*///	#endif

/*TODO*///		state_display(bitmap);
            }
        };
	
	
}
