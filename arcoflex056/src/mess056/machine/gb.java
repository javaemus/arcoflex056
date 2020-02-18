/***************************************************************************

  gb.c

  Machine file to handle emulation of the Nintendo GameBoy.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

import static arcadeflex036.libc_old.strcpy;
import static arcadeflex036.libc_old.strncpy;
import static arcadeflex056.fileio.*;
import static arcadeflex056.fucPtr.*;
import static arcadeflex056.osdepend.logerror;
import static common.ptr.*;
import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.memory.*;
import static mame056.memoryH.*;
import static mame056.cpuexec.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrf.*;
import static mame056.cpuintrfH.*;
import static mame056.inptport.*;
import static mame056.inptportH.*;
import static common.libc.cstdio.*;
import static common.libc.cstring.*;
import static consoleflex056.funcPtr.*;
import static mame056.mame.*;
import static mame056.osdependH.*;
import static mame056.timer.*;
import static mess056.deviceH.*;

import static mess056.machine.gbH.*;
import static mess056.mess.*;
import static mess056.messH.*;
import static mess056.sndhrdw.gb.*;
import static mess056.vidhrdw.gb.*;

public class gb
{
	
	//public static int TCount, TStep;		   /* Timer counter and increment            */
	//#define SOUND =0
	
        public static int MBCType;				   /* MBC type: 0 for none                        */
        public static int CartType;				   /* Cart Type (battery, ram, timer etc)         */
        public static UBytePtr[] ROMMap = new UBytePtr[512];       /* Addresses of ROM banks                      */
        public static int ROMBank;				   /* Number of ROM bank currently used           */
	public static int ROMMask;				   /* Mask for the ROM bank number                */
	public static int ROMBanks;				   /* Total number of ROM banks                   */
	public static UBytePtr[] RAMMap = new UBytePtr[256];	   /* Addresses of RAM banks                      */
        public static int RAMBank;				   /* Number of RAM bank currently used           */
	public static int RAMMask;				   /* Mask for the RAM bank number                */
	public static int RAMBanks;				   /* Total number of RAM banks                   */
        public static int SIOCount;				   /* Serial I/O counter                          */
        public static int/*UINT8*/ MBC1Mode;                       /* MBC1 ROM/RAM mode                           */
        public static UBytePtr[]/*UINT8*/ MBC3RTCMap = new UBytePtr[5];      /* MBC3 Real-Time-Clock banks                  */
        public static int/*UINT8*/ MBC3RTCBank;			   /* Number of RTC bank for MBC3                 */
        public static char[] nvram_name = new char[1024];//char nvram_name[1024];/* Name to store NVRAM under                   */
        public static UBytePtr gb_ram = new UBytePtr();

        public static int Verbose = 0x00;
	public static int SGB = 0;
	public static int CheckCRC = 1;
	public static int LineDelay = 0;
	public static int IFreq = 60;
        
	public static InitMachinePtr gb_init_machine = new InitMachinePtr() { public void handler() 
	{
		gb_ram = new UBytePtr(memory_region (REGION_CPU1));

                ROMBank = 1;
                RAMBank = 0;
                cpu_setbank (1, ROMMap[ROMBank]!=null ? ROMMap[ROMBank] : new UBytePtr(gb_ram, 0x4000));
                cpu_setbank (2, RAMMap[RAMBank]!=null ? RAMMap[RAMBank] : new UBytePtr(gb_ram, 0xA000));
                MBC1Mode = 0;
                MBC3RTCBank = 0;

                /* Initialise the registers */
                LCDSTAT( 0x00 );
                CURLINE( 0x00 );
                CMPLINE( 0x00 );
                IFLAGS( 0x00 );
                ISWITCH( 0x00 );
                SIODATA( 0x00 );
                SIOCONT( 0x7E );
                SCROLLX( 0x00 );
                SCROLLY( 0x00 );
                WNDPOSX( 0x00 );
                WNDPOSY( 0x00 );
                gb_w_io.handler(0x05, 0x00 );	/* TIMECNT */
                gb_w_io.handler(0x06, 0x00 );	/* TIMEMOD */
                gb_w_io.handler(0x07, 0x00 );	/* TIMEFRQ */
                gb_w_io.handler(0x40, 0x91 );	/* LCDCONT */
                gb_w_io.handler(0x47, 0xFC );	/* BGRDPAL */
                gb_w_io.handler(0x48, 0xFC );	/* SPR0PAL */
                gb_w_io.handler(0x49, 0xFC );	/* SPR1PAL */

                /* Initialise the Sound Registers */
                gameboy_sound_w.handler(0xFF26,0xF1); /*Gameboy, F0 for SGB*/ /* set this first */
                gameboy_sound_w.handler(0xFF10,0x80);
                gameboy_sound_w.handler(0xFF11,0xBF);
                gameboy_sound_w.handler(0xFF12,0xF3);
                gameboy_sound_w.handler(0xFF14,0x3F); /* NOTE: Should be 0xBF but it causes a tone at startup */
                gameboy_sound_w.handler(0xFF16,0x3F);
                gameboy_sound_w.handler(0xFF17,0x00);
                gameboy_sound_w.handler(0xFF19,0xBF);
                gameboy_sound_w.handler(0xFF1A,0x7F);
                gameboy_sound_w.handler(0xFF1B,0xFF);
                gameboy_sound_w.handler(0xFF1C,0x9F);
                gameboy_sound_w.handler(0xFF1E,0xBF);
                gameboy_sound_w.handler(0xFF20,0xFF);
                gameboy_sound_w.handler(0xFF21,0x00);
                gameboy_sound_w.handler(0xFF22,0x00);
                gameboy_sound_w.handler(0xFF23,0xBF);
                gameboy_sound_w.handler(0xFF24,0x77);
                gameboy_sound_w.handler(0xFF25,0xF3);
            }
        };
        
        public static StopMachinePtr gb_shutdown_machine = new StopMachinePtr() {
            public void handler() {
                int I;
                UBytePtr battery_ram, ptr;

                /* Don't save if there was no battery */
                if( (CartType & BATTERY)==0 )
                        return;

                /* NOTE: The reason we save the carts RAM this way instead of using MAME's
                   built in macros is because they force the filename to be the name of
                   the machine.  We need to have a separate name for each game. */
                battery_ram = new UBytePtr( RAMBanks * 0x2000 );
                if( battery_ram != null )
                {
                        ptr = battery_ram;
                        for( I = 0; I < RAMBanks; I++ )
                        {
                                memcpy( ptr, RAMMap[I], 0x2000 );
                                ptr.inc( 0x2000 );
                        }
                        battery_save( new String(nvram_name), battery_ram, RAMBanks * 0x2000 );

                        battery_ram = null;
                }

                /* We should releas memory here, but this function is called upon reset
                   and we don't reload the rom, so we're going to have to leak for now.
                */
        /*	for( I = 0; I < RAMBanks; I++ )
                {
                        free( RAMMap[I] );
                }
                for( I = 0; I < ROMBanks; I++ )
                {
                        free( ROMMap[I] );
                }*/
            }
        };
	
	public static WriteHandlerPtr gb_rom_bank_select = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                /* No need to bank switch if there is no controller */
                if( MBCType == 0 )
                        return;

                data &= ROMMask;

                /* Selecting bank 0 == selecting bank 1 except with an MBC5 */
                if( data==0 && MBCType != MBC5 )
                        data = 1;

                if( ROMMask != 0 )
                {
                        switch( MBCType )
                        {
                        case MBC1:
                                ROMBank = data & 0x1F; /* Only uses lower 5 bits */
                                break;
                        case MBC2:
                                /* The least significant bit of the upper address byte must be 1 */
                                if(( offset & 0x0100 ) != 0)
                                        ROMBank = data;
                                break;
                        case MBC3:
                        case HUC1:	/* Probably wrong */
                        case HUC3:	/* Probably wrong */
                                ROMBank = data;
                                break;
                        case MBC5:
                                /* MBC5 has a 9 bit bank select */
                                if( offset < 0x1000 )
                                {
                                        ROMBank = (ROMBank & 0x100 ) | data;
                                }
                                else
                                {
                                        ROMBank = (ROMBank & 0xFF ) | ((data & 0x1) << 8);
                                }
                                break;
                        }

                        /* Switch banks */
                        cpu_setbank (1, ROMMap[ROMBank]!=null ? ROMMap[ROMBank] : new UBytePtr(gb_ram, 0x4000));
                        if ((Verbose & 0x08) != 0)
                                printf ("ROM: Bank %d selected\n", ROMBank);
                }
            }
        };
	
	public static WriteHandlerPtr gb_ram_bank_select = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
		/* No need to bank switch if no controller or MBC2 */
                if( MBCType==0 || MBCType == MBC2 )
                        return;

                data &= RAMMask;
                if( RAMMask != 0 )
                {
                        switch( MBCType )
                        {
                        case MBC1:
                                data &= 0x3; /* Only uses the lower 2 bits */
                                if( MBC1Mode != 0 )
                                {
                                        /* Select the upper bits of the ROMMask */
                                        ROMBank |= data << 5;
                                        cpu_setbank (1, ROMMap[ROMBank]!=null ? ROMMap[ROMBank] : new UBytePtr(gb_ram, 0x4000));
                                        return;
                                }
                                else
                                {
                                        RAMBank = data;
                                }
                                break;
                        case MBC3:
                                if(( data & 0x3 ) != 0)
                                {
                                        RAMBank = data & 0x3;
                                }
                                if(( data & 0x8 ) != 0)
                                {
                                        MBC3RTCBank = data & 0x3;
                                        cpu_setbank (2, MBC3RTCMap[MBC3RTCBank]);
                                        return;
                                }
                                break;
                        case MBC5:
                                if(( CartType & RUMBLE ) != 0)
                                {
                                        logerror( "Rumble motor: %s\n", (data & 0x8)!=0?"On":"Off" );
                                        data &= 0x7;
                                }
                                RAMBank = data;
                                break;
                        }

                        /* Switch banks */
                        cpu_setbank (2, RAMMap[RAMBank]!=null ? RAMMap[RAMBank] : new UBytePtr(gb_ram, 0xA000));
                        if ((Verbose & 0x08) != 0)
                                printf ("RAM: Bank %d selected\n", RAMBank);
                }
            }
        };
        
        public static WriteHandlerPtr gb_mem_mode_select = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                switch( MBCType )
                {
                        case MBC1:
                                MBC1Mode = data & 0x1;
                                break;
                        case MBC3:
                                if(( CartType & TIMER ) != 0)
                                {
                                        /* RTC Latch goes here */
                                }
                                break;
                }
            }
        };
        
        static int timer_shifts[] =
		{10, 4, 6, 8};
        
        
        static int bit_count = 0, byte_count = 0, start = 0, rest = 0;
	static int[] sgb_data = new int[16];
	static int controller_no = 0, controller_mode = 0;
	
	public static WriteHandlerPtr gb_w_io = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                UBytePtr P;
                
                offset += 0xFF00;

                switch (offset)
                {
                case 0xFF00:
                        if (SGB != 0)
                        {
                                switch (data & 0x30)
                                {
                                case 0x00:				   /* start condition */
                                        if (start != 0)
                                                printf ("SGB: Start condition before end of transfer ??");
                                        bit_count = 0;
                                        byte_count = 0;
                                        start = 1;
                                        rest = 0;
                                        JOYPAD( 0x0F & ((readinputport (0) >> 4) | readinputport (0) | 0xF0));
                                        break;
                                case 0x10:				   /* data true */
                                        if (rest != 0)
                                        {
                                                if (byte_count == 16)
                                                {
                                                        printf ("SGB: end of block is not zero!");
                                                        start = 0;
                                                }
                                                sgb_data[byte_count] >>= 1;
                                                sgb_data[byte_count] |= 0x80;
                                                bit_count++;
                                                if (bit_count == 8)
                                                {
                                                        bit_count = 0;
                                                        byte_count++;
                                                }
                                                rest = 0;
                                        }
                                        JOYPAD( 0x1F & ((readinputport (0) >> 4) | 0xF0) );
                                        break;
                                case 0x20:				   /* data false */
                                        if (rest != 0)
                                        {
                                                if (byte_count == 16)
                                                {
                                                        printf
                                                                        ("SGB: command: %02X packets: %d data: %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X\n",
                                                                        sgb_data[0] >> 3, sgb_data[0] & 0x07, sgb_data[1], sgb_data[2], sgb_data[3],
                                                                        sgb_data[4], sgb_data[5], sgb_data[6], sgb_data[7],
                                                                        sgb_data[8], sgb_data[9], sgb_data[10], sgb_data[11],
                                                                        sgb_data[12], sgb_data[13], sgb_data[14], sgb_data[15]);
                                                        if ((sgb_data[0] >> 3) == 0x11)
                                                        {
                                                                printf ("multicontroller command, data= %02X\n", sgb_data[1]);
                                                                if (sgb_data[1] == 0x00)
                                                                        controller_mode = 0;
                                                                else if (sgb_data[1] == 0x01)
                                                                        controller_mode = 2;
                                                        }
                                                        start = 0;
        /*						Trace=1; */
                                                }
                                                sgb_data[byte_count] >>= 1;
                                                bit_count++;
                                                if (bit_count == 8)
                                                {
                                                        bit_count = 0;
                                                        byte_count++;
                                                }
                                                rest = 0;
                                        }
                                        JOYPAD( 0x2F & (readinputport (0) | 0xF0) );
                                        break;
                                case 0x30:				   /* rest condition */
                                        if (start != 0)
                                                rest = 1;
                                        if (controller_mode != 0)
                                        {
                                                controller_no++;
                                                if (controller_no == controller_mode)
                                                        controller_no = 0;
                                                JOYPAD( 0x3F - controller_no );
                                        }
                                        else
                                                JOYPAD( 0x3F );
                                        break;
                                }
        /*                   printf("%d%d\n", (data&0x10)? 1:0, (data&0x20)? 1:0); */
                        }
                        else
                        {
                                JOYPAD( 0xCF | data );
                                if ((data & 0x20)==0)
                                        JOYPAD( JOYPAD() & (readinputport (0) >> 4) | 0xF0);
                                if ((data & 0x10)==0)
                                        JOYPAD( JOYPAD() & readinputport (0) | 0xF0 );
                        }
                        return;
                case 0xFF01:						/* SB - Serial transfer data */
                        break;
                case 0xFF02:						/* SC - SIO control */
                        if ((data & 0x81) == 0x81)		/* internal clock && enable */
                        {
                                SIODATA( 0xFF );
                                SIOCount = 8;
                        }
                        else							/* external clock || disable */
                                SIOCount = 0;
                        break;
                case 0xFF04:						/* DIV - Divider register */
                        gb_divcount = 0;
                        return;
                case 0xFF05:						/* TIMA - Timer counter */
                        gb_timer_count = data << gb_timer_shift;
                        break;
                case 0xFF07:						/* TAC - Timer control */
                        gb_timer_shift = timer_shifts[data & 0x03];
                        data |= 0xF8;
                        break;
                case 0xFF0F:						/* IF - Interrupt flag */
                        data &= 0x1F;
                        break;
                case 0xFF40:						/* LCDC - LCD Control */
                        gb_chrgen = new UBytePtr(gb_ram, ((data & 0x10)!=0 ? 0x8000 : 0x8800));
                        gb_tile_no_mod = ((data & 0x10)!=0 ? 0x00 : 0x80);
                        gb_bgdtab = new UBytePtr(gb_ram, ((data & 0x08)!=0 ? 0x9C00 : 0x9800));
                        gb_wndtab = new UBytePtr(gb_ram, ((data & 0x40)!=0 ? 0x9C00 : 0x9800));
                        break;
                case 0xFF41:						/* STAT - LCD Status */
                        data = (data & 0xF8) | (LCDSTAT() & 0x07);
                        break;
                case 0xFF44:						/* LY - LCD Y-coordinate */
                        data = 0;
                        break;
                case 0xFF46:						/* DMA - DMA Transfer and Start Address */
                        P = new UBytePtr(gb_ram, 0xFE00);
                        offset = (data << 8)&0xffff;
                        for (data = 0; data < 0xA0; data++)
                                P.writeinc( cpu_readmem16 (offset++) );
                        return;
                case 0xFF47:						/* BGP - Background Palette */
                        gb_bpal[0] = (char) Machine.remapped_colortable.read((data & 0x03));
                        gb_bpal[1] = (char) Machine.remapped_colortable.read((data & 0x0C) >> 2);
                        gb_bpal[2] = (char) Machine.remapped_colortable.read((data & 0x30) >> 4);
                        gb_bpal[3] = (char) Machine.remapped_colortable.read((data & 0xC0) >> 6);
                        /* This is so we can assign different colours to window tiles,
                           even though the window shares the same palette data as the
                           background */
                        gb_wpal[0] = (char) Machine.remapped_colortable.read((data & 0x03) + 12);
                        gb_wpal[1] = (char) Machine.remapped_colortable.read(((data & 0x0C) >> 2) + 12);
                        gb_wpal[2] = (char) Machine.remapped_colortable.read(((data & 0x30) >> 4) + 12);
                        gb_wpal[3] = (char) Machine.remapped_colortable.read(((data & 0xC0) >> 6) + 12);
                        break;
                case 0xFF48:						/* OBP0 - Object Palette 0 */
                        gb_spal0[0] = Machine.remapped_colortable.read((data & 0x03) + 4);
                        gb_spal0[1] = Machine.remapped_colortable.read(((data & 0x0C) >> 2) + 4);
                        gb_spal0[2] = Machine.remapped_colortable.read(((data & 0x30) >> 4) + 4);
                        gb_spal0[3] = Machine.remapped_colortable.read(((data & 0xC0) >> 6) + 4);
                        break;
                case 0xFF49:						/* OBP1 - Object Palette 1 */
                        gb_spal1[0] = Machine.remapped_colortable.read((data & 0x03) + 8);
                        gb_spal1[1] = Machine.remapped_colortable.read(((data & 0x0C) >> 2) + 8);
                        gb_spal1[2] = Machine.remapped_colortable.read(((data & 0x30) >> 4) + 8);
                        gb_spal1[3] = Machine.remapped_colortable.read(((data & 0xC0) >> 6) + 8);
                        break;
                default:
                        /* Sound Registers */
                        if ((offset >= 0xFF10) && (offset <= 0xFF26))
                        {
                                gameboy_sound_w.handler(offset, data);
                                return;
                        }
                }
                gb_ram.memory[offset] = (char) (data & 0xff);
            }
        };
        
        public static WriteHandlerPtr gb_w_ie = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                gb_ram.write(0xFFFF, data & 0x1F);
            }
        };
        
        public static ReadHandlerPtr gb_r_io = new ReadHandlerPtr() {
            public int handler(int offset) {
                offset += 0xFF00;

                switch(offset)
                {
                        case 0xFF04:
                                return ((gb_divcount >> 8) & 0xFF);
                        case 0xFF05:
                                return (gb_timer_count >> gb_timer_shift);
                        case 0xFF00:
                        case 0xFF01:
                        case 0xFF02:
                        case 0xFF03:
                        case 0xFF06:
                        case 0xFF07:
                        case 0xFF0F:
                        case 0xFF10:
                        case 0xFF11:
                        case 0xFF12:
                        case 0xFF13:
                        case 0xFF14:
                        case 0xFF16:
                        case 0xFF17:
                        case 0xFF18:
                        case 0xFF19:
                        case 0xFF1A:
                        case 0xFF1B:
                        case 0xFF1C:
                        case 0xFF1D:
                        case 0xFF1E:
                        case 0xFF20:
                        case 0xFF21:
                        case 0xFF22:
                        case 0xFF23:
                        case 0xFF24:
                        case 0xFF25:
                        case 0xFF26:
                        case 0xFF40:
                        case 0xFF41:
                        case 0xFF42:
                        case 0xFF43:
                        case 0xFF44:
                        case 0xFF45:
                        case 0xFF47:
                        case 0xFF48:
                        case 0xFF49:
                        case 0xFF4A:
                        case 0xFF4B:
                                return gb_ram.memory[offset];
                        default:
                                /* It seems unsupported registers return 0xFF */
                                return 0xFF;
                }
            }
        };
        
        static String CartTypes[] =
	{
		"ROM ONLY",
		"ROM+MBC1",
		"ROM+MBC1+RAM",
		"ROM+MBC1+RAM+BATTERY",
                "UNKNOWN",
		"ROM+MBC2",
		"ROM+MBC2+BATTERY",
                "UNKNOWN",
		"ROM+RAM",
		"ROM+RAM+BATTERY",
                "UNKNOWN",
		"ROM+MMM01",
		"ROM+MMM01+SRAM",
		"ROM+MMM01+SRAM+BATTERY",
                "UNKNOWN",
		"ROM+MBC3+TIMER+BATTERY",
		"ROM+MBC3+TIMER+RAM+BATTERY",
		"ROM+MBC3",
		"ROM+MBC3+RAM",
		"ROM+MBC3+RAM+BATTERY",
                "UNKNOWN",
                "UNKNOWN",
                "UNKNOWN",
                "UNKNOWN",
                "UNKNOWN",
		"ROM+MBC5",
		"ROM+MBC5+RAM",
		"ROM+MBC5+RAM+BATTERY",
		"ROM+MBC5+RUMBLE",
		"ROM+MBC5+RUMBLE+SRAM",
		"ROM+MBC5+RUMBLE+SRAM+BATTERY",
		"Pocket Camera",
		"Bandai TAMA5",
		/* Need heaps of unknowns here */
		"Hudson HuC-3",
		"Hudson HuC-1"
	};
        
        /*** Following are some known manufacturer codes *************************/
	static class _Companies
	{
		public int Code;
		public String Name;
                
                public _Companies(int val, String desc){
                    Code = val;
                    Name = desc;
                }
	};
        
	static _Companies Companies[] =
	{
		new _Companies(0x3301, "Nintendo"),
		new _Companies(0x7901, "Accolade"),
		new _Companies(0xA400, "Konami"),
		new _Companies(0x6701, "Ocean"),
		new _Companies(0x5601, "LJN"),
		new _Companies(0x9900, "ARC?"),
		new _Companies(0x0101, "Nintendo"),
		new _Companies(0x0801, "Capcom"),
		new _Companies(0x0100, "Nintendo"),
		new _Companies(0xBB01, "SunSoft"),
		new _Companies(0xA401, "Konami"),
		new _Companies(0xAF01, "Namcot?"),
		new _Companies(0x4901, "Irem"),
		new _Companies(0x9C01, "Imagineer"),
		new _Companies(0xA600, "Kawada?"),
		new _Companies(0xB101, "Nexoft"),
		new _Companies(0x5101, "Acclaim"),
		new _Companies(0x6001, "Titus"),
		new _Companies(0xB601, "HAL"),
		new _Companies(0x3300, "Nintendo"),
		new _Companies(0x0B00, "Coconuts?"),
		new _Companies(0x5401, "Gametek"),
		new _Companies(0x7F01, "Kemco?"),
		new _Companies(0xC001, "Taito"),
		new _Companies(0xEB01, "Atlus"),
		new _Companies(0xE800, "Asmik?"),
		new _Companies(0xDA00, "Tomy?"),
		new _Companies(0xB100, "ASCII?"),
		new _Companies(0xEB00, "Atlus"),
		new _Companies(0xC000, "Taito"),
		new _Companies(0x9C00, "Imagineer"),
		new _Companies(0xC201, "Kemco?"),
		new _Companies(0xD101, "Sofel?"),
		new _Companies(0x6101, "Virgin"),
		new _Companies(0xBB00, "SunSoft"),
		new _Companies(0xCE01, "FCI?"),
		new _Companies(0xB400, "Enix?"),
		new _Companies(0xBD01, "Imagesoft"),
		new _Companies(0x0A01, "Jaleco?"),
		new _Companies(0xDF00, "Altron?"),
		new _Companies(0xA700, "Takara?"),
		new _Companies(0xEE00, "IGS?"),
		new _Companies(0x8300, "Lozc?"),
		new _Companies(0x5001, "Absolute?"),
		new _Companies(0xDD00, "NCS?"),
		new _Companies(0xE500, "Epoch?"),
		new _Companies(0xCB00, "VAP?"),
		new _Companies(0x8C00, "Vic Tokai"),
		new _Companies(0xC200, "Kemco?"),
		new _Companies(0xBF00, "Sammy?"),
		new _Companies(0x1800, "Hudson Soft"),
		new _Companies(0xCA01, "Palcom/Ultra"),
		new _Companies(0xCA00, "Palcom/Ultra"),
		new _Companies(0xC500, "Data East?"),
		new _Companies(0xA900, "Technos Japan?"),
		new _Companies(0xD900, "Banpresto?"),
		new _Companies(0x7201, "Broderbund?"),
		new _Companies(0x7A01, "Triffix Entertainment?"),
		new _Companies(0xE100, "Towachiki?"),
		new _Companies(0x9300, "Tsuburava?"),
		new _Companies(0xC600, "Tonkin House?"),
		new _Companies(0xCE00, "Pony Canyon"),
		new _Companies(0x7001, "Infogrames?"),
		new _Companies(0x8B01, "Bullet-Proof Software?"),
		new _Companies(0x5501, "Park Place?"),
		new _Companies(0xEA00, "King Records?"),
		new _Companies(0x5D01, "Tradewest?"),
		new _Companies(0x6F01, "ElectroBrain?"),
		new _Companies(0xAA01, "Broderbund?"),
		new _Companies(0xC301, "SquareSoft"),
		new _Companies(0x5201, "Activision?"),
		new _Companies(0x5A01, "Bitmap Brothers/Mindscape"),
		new _Companies(0x5301, "American Sammy"),
		new _Companies(0x4701, "Spectrum Holobyte"),
		new _Companies(0x1801, "Hudson Soft"),
		new _Companies(0x0000, null)
	};
	
        public static io_initPtr gb_load_rom = new io_initPtr() {
            public int handler(int id) {
                UBytePtr ROM = new UBytePtr(memory_region(REGION_CPU1));

                int Checksum, I, J;
                String P;
                char[] S = new char[50];
                Object F;
                int rambanks[] = {0, 1, 1, 4, 16};

                for (I = 0; I < 256; I++)
                        RAMMap[I] = ROMMap[I] = null;

                if(device_filename(IO_CARTSLOT,id)==null)
                {
                        printf("Cartridge name required!\n");
                        return INIT_FAIL;
                }

                /* Grabbed this from the NES driver */
                strcpy (nvram_name, device_filename(IO_CARTSLOT,id));
                /* Strip off file extension if it exists */
                for (I = strlen(new String(nvram_name)) - 1; I > 0; I--)
                {
                        /* If we found a period, terminate the string here and jump out */
                        if (nvram_name[I] == '.')
                        {
                                nvram_name[I] = 0x00;
                                break;
                        }
                }

                if( new_memory_region(REGION_CPU1, 0x10000,0) != 0 )
                {
                        logerror("Memory allocation failed reading roms!\n");
                        return INIT_FAIL;
                }

                ROM = gb_ram = memory_region(REGION_CPU1);
                memset (ROM, 0, 0x10000);

                /* FIXME should check first if a file is given, should give a more clear error */
                if ((F = image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_READ))==null)
                {
                        logerror("image_fopen failed in gb_load_rom.\n");
                        return INIT_FAIL;
                }

            /* some tricks since we don't have a lseek, the filesize can't
               be determined easily. So we just keep reading into the same buffer untill
               the reads fails and then check if we have 512 bytes too much, so its a file
               with header or not */

            for (J = 0x4000; J == 0x4000;)
                        J = osd_fread (F, gb_ram, 0x4000);

                osd_fclose (F);

                /* FIXME should check first if a file is given, should give a more clear error */
                if ((F = image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_READ))==null)
                {
                        logerror("image_fopen failed in gb_load_rom.\n");
                        return INIT_FAIL;
                }

                if (J == 512)
                {
                        logerror("ROM-header found skipping\n");
                        osd_fread (F, gb_ram, 512);
                }

                if (osd_fread (F, gb_ram, 0x4000) != 0x4000)
                {
                        logerror("Error while reading from file: %s\n", device_filename(IO_CARTSLOT,id));
                        osd_fclose (F);
                        return INIT_FAIL;
                }

                ROMMap[0] = gb_ram;
                ROMBanks = 2 << gb_ram.read(0x0148);
                RAMBanks = rambanks[gb_ram.read(0x0149) & 3];
                Checksum = ( gb_ram.read(0x014E) << 8) + gb_ram.read(0x014F);
                /* File in our cart details */
                switch( gb_ram.read(0x0147) )
                {
                        case 0x00:
                                MBCType = 0;
                                CartType = 0;
                                break;
                        case 0x01:
                                MBCType = MBC1;
                                CartType = 0;
                                break;
                        case 0x02:
                                MBCType = MBC1;
                                CartType = RAM;
                                break;
                        case 0x03:
                                MBCType = MBC1;
                                CartType = RAM | BATTERY;
                                break;
                        case 0x05:
                                MBCType = MBC2;
                                CartType = 0;
                                break;
                        case 0x06:
                                MBCType = MBC2;
                                CartType = BATTERY;
                                break;
                        case 0x08:
                                MBCType = 0;
                                CartType = RAM;
                        case 0x09:
                                MBCType = 0;
                                CartType = RAM | BATTERY;
                                break;
                        case 0x0F:
                                MBCType = MBC3;
                                CartType = TIMER | BATTERY;
                                break;
                        case 0x10:
                                MBCType = MBC3;
                                CartType = TIMER | RAM | BATTERY;
                                break;
                        case 0x11:
                                MBCType = MBC3;
                                CartType = 0;
                                break;
                        case 0x12:
                                MBCType = MBC3;
                                CartType = RAM;
                                break;
                        case 0x13:
                                MBCType = MBC3;
                                CartType = RAM | BATTERY;
                                break;
                        case 0x19:
                                MBCType = MBC5;
                                CartType = 0;
                                break;
                        case 0x1A:
                                MBCType = MBC5;
                                CartType = RAM;
                                break;
                        case 0x1B:
                                MBCType = MBC5;
                                CartType = RAM | BATTERY;
                                break;
                        case 0x1C:
                                MBCType = MBC5;
                                CartType = RUMBLE;
                                break;
                        case 0x1D:
                                MBCType = MBC5;
                                CartType = RUMBLE | SRAM;
                                break;
                        case 0x1E:
                                MBCType = MBC5;
                                CartType = RUMBLE | SRAM | BATTERY;
                                break;
                        case 0xFE:
                                MBCType = HUC3;
                                CartType = 0;
                                break;
                        case 0xFF:
                                MBCType = HUC1;
                                CartType = 0;
                                break;
                        default:
                                MBCType = 0;
                                CartType = UNKNOWN;
                }

                if (( CartType & UNKNOWN ) != 0)
                {
                        logerror("Error loading cartridge: Unknown ROM type\n");
                        osd_fclose (F);
                        return INIT_FAIL;
                }

                if (Verbose != 0)
                {
                        strncpy (S, new UBytePtr(gb_ram, 0x0134), 16);
                        S[16] = '\0';
                        logerror("OK\n  Name: %s\n", S);
                        logerror("  Type: %s [%Xh]\n", CartTypes[gb_ram.read(0x0147)], gb_ram.read(0x0147) );
                        logerror("  Color GB: %s [%Xh]\n", (gb_ram.read(0x0143) == 0x80 || gb_ram.read(0x0143) == 0xc0) ? "Yes" : "No", gb_ram.read(0x0143) );
                        logerror("  Super GB: %s [%Xh]\n", (gb_ram.read(0x0146) == 0x03) ? "Yes" : "No", gb_ram.read(0x0146) );
                        logerror("  ROM Size: %d 16kB Banks [%X]\n", ROMBanks, gb_ram.read(0x0148));
                        J = (gb_ram.read(0x0149) & 0x03) * 2;
                        J = J!=0 ? (1 << (J - 1)) : 0;
                        logerror("  RAM Size: %d kB [%X]\n", J, gb_ram.read(0x0149));
                        logerror("  License code %X%Xh\n", gb_ram.read(0x0145), gb_ram.read(0x0144) );
                        J = (gb_ram.read(0x014B) << 8) + gb_ram.read(0x014A);
                        for (I = 0, P = null; P==null && Companies[I].Name!=null; I++)
                                if (J == Companies[I].Code)
                                        P = Companies[I].Name;
                        logerror("  Manufacturer ID: %Xh", J);
                        logerror(" [%s]\n", P!=null ? P : "?");

                        logerror("  Version Number: %Xh\n", gb_ram.read(0x014C));
                        logerror("  Complement Check: %Xh\n", gb_ram.read(0x014D));
                        logerror("  Checksum: %Xh\n", Checksum);
                        J = (gb_ram.read(0x0103) << 8) + gb_ram.read(0x0102);
                        logerror("  Start Address: %Xh\n", J);
                }

                Checksum += gb_ram.read(0x014E) + gb_ram.read(0x014F);
                for (I = 0; I < 0x4000; I++)
                        Checksum -= gb_ram.read(I);

                if (Verbose != 0)
                        logerror("Loading %dx16kB ROM banks:.", ROMBanks);
                for (I = 1; I < ROMBanks; I++)
                {
                        if ((ROMMap[I] = new UBytePtr(0x4000)) != null)
                        {
                                if (osd_fread (F, ROMMap[I], 0x4000) == 0x4000)
                                {
                                        for (J = 0; J < 0x4000; J++)
                                                Checksum -= ROMMap[I].read(J);
                                        if (Verbose != 0)
                                                printf (".");
                                }
                                else
                                {
                                        logerror("Error while reading from file: %s\n", device_filename(IO_CARTSLOT,id));
                                        break;
                                }
                        }
                        else
                        {
                                logerror("Error allocating memory\n");
                                break;
                        }
                }

                osd_fclose (F);
                if (I < ROMBanks)
                        return INIT_FAIL;

                if (CheckCRC!=0 && (Checksum & 0xFFFF)!=0)
                {
                        logerror("Error loading cartridge: Checksum is wrong");
                        return INIT_FAIL;
                }

                /* MBC2 has 512 * 4bits (8kb) internal RAM */
                if( MBCType == MBC2 )
                        RAMBanks = 1;

                if (RAMBanks!=0 && MBCType!=0)
                {
                        for (I = 0; I < RAMBanks; I++)
                        {
                                if ((RAMMap[I] = new UBytePtr(0x2000)) != null)
                                        memset (RAMMap[I], 0, 0x2000);
                                else
                                {
                                        logerror("Error alocating memory\n");
                                        return INIT_FAIL;
                                }
                        }
                }

                /* Load the saved RAM if this cart has a battery */
                if(( CartType & BATTERY ) != 0)
                {
                        UBytePtr battery_ram, ptr;
                        battery_ram = new UBytePtr( RAMBanks * 0x2000 );
                        if( battery_ram != null )
                        {
                                battery_load( new String(nvram_name), battery_ram, RAMBanks * 0x2000 );
                                ptr = battery_ram;
                                for( I = 0; I < RAMBanks; I++ )
                                {
                                        memcpy( RAMMap[I], ptr, 0x2000 );
                                        ptr.inc(0x2000);
                                }

                                battery_ram = null;
                        }
                }

                if (ROMBanks < 3)
                        ROMMask = 0;
                else
                {
                        for (I = 1; I < ROMBanks; I <<= 1) ;
                        ROMMask = I - 1;
                }
                if (RAMMap[0]==null)
                        RAMMask = 0;
                else
                {
                        for (I = 1; I < RAMBanks; I <<= 1) ;
                        RAMMask = I - 1;
                }

                return INIT_PASS;
            }
        };
        
    //static int count = 0;

    public static InterruptPtr gb_scanline_interrupt = new InterruptPtr() {
        public int handler() {
            /* This is a little dodgy, but it works... mostly */
	
            /*count = (count + 1) % 3;
            if ( count != 0 )
                    return ignore_interrupt.handler();*/

            /* First let's draw the current scanline */
            if (CURLINE() < 144)
                    gb_refresh_scanline ();

            /* The rest only makes sense if the display is enabled */
            if ((LCDCONT() & 0x80) != 0)
            {
                    if (CURLINE() == CMPLINE())
                    {
                            LCDSTAT(LCDSTAT() | 0x04);
                            /* Generate lcd interrupt if requested */
                            if(( LCDSTAT() & 0x40 ) != 0)
                                    cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
                    }
                    else
                            LCDSTAT( LCDSTAT() & 0xFB );

                    if (CURLINE() < 144)
                    {
                            /* Set Mode 2 lcdstate */
                            LCDSTAT( (LCDSTAT() & 0xFC) | 0x02 );
                            /* Generate lcd interrupt if requested */
                            if ((LCDSTAT() & 0x20) != 0)
                                    cpu_set_irq_line(0, LCD_INT, HOLD_LINE);

                            /* First  lcdstate change after aprox 19 uS */
                            timer_set (19.0 / 1000000.0, 0, gb_scanline_interrupt_set_mode3);
                            /* Second lcdstate change after aprox 60 uS */
                            timer_set (60.0 / 1000000.0, 0, gb_scanline_interrupt_set_mode0);
                    }
                    else
                    {
                            /* Generate VBlank interrupt */
                            if (CURLINE() == 144)
                            {
                                    /* Cause VBlank interrupt */
                                    cpu_set_irq_line(0, VBL_INT, HOLD_LINE);
                                    /* Set VBlank lcdstate */
                                    LCDSTAT( (LCDSTAT() & 0xFC) | 0x01 );
                                    /* Generate lcd interrupt if requested */
                                    if(( LCDSTAT() & 0x10 ) != 0)
                                            cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
                            }
                    }

                    CURLINE( (CURLINE() + 1) % 154 );
            }

            /* Generate serial IO interrupt */
            if (SIOCount != 0)
            {
                    SIODATA( (SIODATA() << 1) | 0x01 );
                    if (--SIOCount == 0)
                    {
                            SIOCONT( SIOCONT() & 0x7F );
                            cpu_set_irq_line(0, SIO_INT, HOLD_LINE);
                    }
            }

            /* Return No interrupt, we cause them ourselves since multiple int's can
             * occur at the same time */
            return ignore_interrupt.handler();
        }
    };

    public static timer_callback gb_scanline_interrupt_set_mode0 = new timer_callback() {
        public void handler(int param) {
            /* Set Mode 0 lcdstate */
            LCDSTAT( LCDSTAT() & 0xFC );
            /* Generate lcd interrupt if requested */
            if(( LCDSTAT() & 0x08 ) != 0)
                    cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
        }
    };

    public static timer_callback gb_scanline_interrupt_set_mode3 = new timer_callback() {
        public void handler(int param) {
            /* Set Mode 3 lcdstate */
            LCDSTAT( (LCDSTAT() & 0xFC) | 0x03 );
        }
    };
    
}
