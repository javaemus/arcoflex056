/***************************************************************************

  gb.c

  Machine file to handle emulation of the Nintendo GameBoy.

***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

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
	
	public static int MBCType;				   /* MBC type: 1 for MBC2, 0 for MBC1            */
	public static UBytePtr[] ROMMap = new UBytePtr[256];	   /* Addresses of ROM banks                      */
	public static int ROMBank;				   /* Number of ROM bank currently used           */
	public static int ROMMask;				   /* Mask for the ROM bank number                */
	public static int ROMBanks;				   /* Total number of ROM banks                   */
	public static UBytePtr[] RAMMap = new UBytePtr[256];	   /* Addresses of RAM banks                      */
	public static int RAMBank;				   /* Number of RAM bank currently used           */
	public static int RAMMask;				   /* Mask for the RAM bank number                */
	public static int RAMBanks;				   /* Total number of RAM banks                   */
	public static int TCount, TStep;		   /* Timer counter and increment            */
	public static int SIOCount;				   /* Serial I/O counter                     */
	
	public static int Verbose = 0x00;
	public static int SGB = 0;
	public static int CheckCRC = 1;
	public static int LineDelay = 0;
	public static int IFreq = 60;
	//#define SOUND =0
	
	public static UBytePtr gb_ram = new UBytePtr();
	
	public static InitMachinePtr gb_init_machine = new InitMachinePtr() { public void handler() 
	{
		int I;
	
		gb_ram = new UBytePtr(memory_region (REGION_CPU1));
	
		cpu_setbank (1, ROMMap[1]!=null ? new UBytePtr(ROMMap[1]) : new UBytePtr(gb_ram, 0x4000));
		cpu_setbank (2, RAMMap[0]!=null ? new UBytePtr(RAMMap[0]) : new UBytePtr(gb_ram, 0xA000));
	
		TStep = 32768;
		TCount = 0;
	
		gb_chrgen = new UBytePtr(gb_ram, 0x8800);
		gb_bgdtab = new UBytePtr(gb_ram, 0x9800);
                gb_wndtab = new UBytePtr(gb_ram, 0x9800);
		LCDCONT( 0x81 );
		LCDSTAT( 0x00 );
		CURLINE( 0x00 );
		CMPLINE( 0x00 );
		IFLAGS ( 0x00 );
                ISWITCH( 0x00 );
		TIMECNT( 0x01 );
                TIMEMOD( 0x01 );
		TIMEFRQ( 0xF8 );
		SIODATA( 0x00 );
		SIOCONT( 0x7E );
	
		for (I = 0; I < 4; I++)
		{
			gb_spal0[I] = gb_bpal[I] = I;
			gb_spal1[I] = I + 4;
		}
	
		BGRDPAL( 0xE4 );
                SPR0PAL( 0xE4 );
                SPR1PAL( 0xE4 );
	
		/* Initialise the timer */
		gb_w_io.handler(0x07, gb_ram.read(0xFF07));
	
		/* Initialise the Sound Registers */
                gameboy_sound_w.handler(0xFF10,0x80);
                gameboy_sound_w.handler(0xFF11,0xBF);
                gameboy_sound_w.handler(0xFF12,0xBF);
                gameboy_sound_w.handler(0xFF14,0xBF);
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
                gameboy_sound_w.handler(0xFF26,0xF1); /*Gameboy, F0 for SGB*/
	
            }
        };
	
	public static WriteHandlerPtr gb_rom_bank_select = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
                if (MBCType!=0 && ((offset & 0x0100) == 0))
			return;
		data &= ROMMask;
		if (data == 0)
			data = 1;
		if (ROMMask!=0 && (data != ROMBank))
		{
			ROMBank = data;
			cpu_setbank (1, ROMMap[data]!=null ? ROMMap[data] : new UBytePtr(gb_ram, 0x4000));
			if ((Verbose & 0x08) != 0)
				printf ("ROM: Bank %d selected\n", data);
		}
            }
        };
	
	public static WriteHandlerPtr gb_ram_bank_select = new WriteHandlerPtr() {
            public void handler(int offset, int data) {
		data &= RAMMask;
		if (RAMMask!=0 && MBCType==0 && (RAMBank != data))
		{
			RAMBank = data;
			cpu_setbank (2, RAMMap[data]!=null ? RAMMap[data] : new UBytePtr(gb_ram, 0xA000));
			if ((Verbose & 0x08) != 0)
				printf ("RAM: Bank %d selected\n", data);
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
	
		/*logerror("Hardware hit %04x, %02x\n", offset, data);*/
		switch (offset)
		{
		case 0xFF00:
			if (SGB != 0)
			{
				switch (data & 0x30)
				{
				case 0x00:				   // start condition
					/*TODO*///if (start != 0)
					/*TODO*///	puts ("SGB: Start condition before end of transfer ??");
					bit_count = 0;
					byte_count = 0;
					start = 1;
					rest = 0;
					JOYPAD( 0x0F & ((readinputport (0) >> 4) | readinputport (0) | 0xF0) );
					break;
				case 0x10:				   // data true
					if (rest != 0)
					{
						if (byte_count == 16)
						{
							/*TODO*///puts ("SGB: end of block is not zero!");
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
				case 0x20:				   // data false
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
				case 0x30:				   // rest condition
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
				if ((data & 0x20) == 0)
					JOYPAD( JOYPAD() & (readinputport (0) >> 4) | 0xF0 );
				if ((data & 0x10) == 0)
					JOYPAD( JOYPAD() & readinputport (0) | 0xF0 );
			}
			return;
		case 0xFF01:
			break;
		case 0xFF02:
			if ((data & 0x81) == 0x81)	   /* internal clock && enable */
				SIOCount = 8;
			else						   /* external clock || disable */
				SIOCount = 0;
			data |= 0x7E;
			break;
		case 0xFF04:
			gb_divcount = 0;
			return;
		case 0xFF05:
			gb_timer_count = data << gb_timer_shift;
		case 0xFF07:
			gb_timer_shift = timer_shifts[data & 0x03];
			data |= 0xF8;
			break;
		case 0xFF0F:
			data &= 0x1F;
			break;
		case 0xFFFF:
			data &= 0x1F;
			break;
		case 0xFF46:
			P = new UBytePtr(gb_ram, 0xFE00);
			offset = data << 8;
			for (data = 0; data < 0xA0; data++)
				P.writeinc( cpu_readmem16 (offset++) );
			return;
		case 0xFF41:
			data = (data & 0xF8) | (LCDSTAT() & 0x07);
			break;
		case 0xFF40:
			gb_chrgen = new UBytePtr(gb_ram, ((data & 0x10)!=0 ? 0x8000 : 0x8800));
			gb_tile_no_mod = (data & 0x10)!=0 ? 0x00 : 0x80;
			gb_bgdtab = new UBytePtr(gb_ram, ((data & 0x08)!=0 ? 0x9C00 : 0x9800));
			gb_wndtab = new UBytePtr(gb_ram, ((data & 0x40)!=0 ? 0x9C00 : 0x9800));
	
			break;
		case 0xFF44:
			data = 0;
			break;
		case 0xFF47:
			gb_bpal[0] = Machine.pens[(data & 0x03)];
			gb_bpal[1] = Machine.pens[(data & 0x0C) >> 2];
			gb_bpal[2] = Machine.pens[(data & 0x30) >> 4];
			gb_bpal[3] = Machine.pens[(data & 0xC0) >> 6];
			break;
	/*TODO*///#if 0
	/*TODO*///		// only 4 colors in Machine.pens allocated!
	/*TODO*///	case 0xFF48:
	/*TODO*///		gb_spal0[0] = Machine.pens[(data & 0x03) + 4];
	/*TODO*///		gb_spal0[1] = Machine.pens[((data & 0x0C) >> 2) + 4];
	/*TODO*///		gb_spal0[2] = Machine.pens[((data & 0x30) >> 4) + 4];
	/*TODO*///		gb_spal0[3] = Machine.pens[((data & 0xC0) >> 6) + 4];
	/*TODO*///		break;
	/*TODO*///	case 0xFF49:
	/*TODO*///		gb_spal1[0] = Machine.pens[(data & 0x03) + 8];
	/*TODO*///		gb_spal1[1] = Machine.pens[((data & 0x0C) >> 2) + 8];
	/*TODO*///		gb_spal1[2] = Machine.pens[((data & 0x30) >> 4) + 8];
	/*TODO*///		gb_spal1[3] = Machine.pens[((data & 0xC0) >> 6) + 8];
	/*TODO*///		break;
	/*TODO*///#else
		case 0xFF48:
			gb_spal0[0] = Machine.pens[(data & 0x03) ];
			gb_spal0[1] = Machine.pens[((data & 0x0C) >> 2) ];
			gb_spal0[2] = Machine.pens[((data & 0x30) >> 4) ];
			gb_spal0[3] = Machine.pens[((data & 0xC0) >> 6) ];
			break;
		case 0xFF49:
			gb_spal1[0] = Machine.pens[(data & 0x03) ];
			gb_spal1[1] = Machine.pens[((data & 0x0C) >> 2) ];
			gb_spal1[2] = Machine.pens[((data & 0x30) >> 4) ];
			gb_spal1[3] = Machine.pens[((data & 0xC0) >> 6) ];
			break;
	/*TODO*///#endif
		default:
	
			/* Sound Registers */
			if ((offset >= 0xFF10) && (offset <= 0xFF26))
			{
                            /*logerror("SOUND WRITE offset: %x  data: %x\n",offset,data);*/
                            gameboy_sound_w.handler(offset,data);
                            gb_ram.write(offset, data);
                            return;
			}
	
			/*Pre defined Waveform Area */
			if ((offset >= 0xFF30) && (offset <= 0xFF3F))
			{
				gb_ram.write(offset, data);
				return;
			}
	
			if (offset == 0xFF26)
			{
				if ((data & 0x80) != 0)
					gb_ram.write(0xFF26, 0xFF);
				else
					gb_ram.write(0xFF26, 0);
				return;
			}
	
		}
		gb_ram.write(offset, data);
            }
        };
	
	public static ReadHandlerPtr gb_ser_regs = new ReadHandlerPtr() {
            public int handler(int offset) {
                offset += 0xFF00;
	
		switch(offset)
		{
			case 0xFF00:
							/*logerror("Location read 0xff00\n");*/
							break;
			case 0xFF01:
							/*logerror("Location read 0xff01\n");*/
							break;
			case 0xFF02:
							/*logerror("Location read 0xff02\n");*/
							break;
			case 0xFF03:
							/*logerror("Location read 0xff03\n");*/
							break;
		}
	
		return gb_ram.read(offset);
            }
        };
	
	public static ReadHandlerPtr gb_r_divreg = new ReadHandlerPtr() {
            public int handler(int offset) {
		return ((gb_divcount >> 8) & 0xFF);
            }
        };
	
	public static ReadHandlerPtr gb_r_timer_cnt = new ReadHandlerPtr() {
            public int handler(int offset) {
		return (gb_timer_count >> gb_timer_shift);
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
                "ROM+MBC2+BATTERY"
        };
        
        public static class _COMPANIES
        {
                public int Code;
                public String Name;
                
                public _COMPANIES(int Code, String Name){
                    this.Code = Code;
                    this.Name = Name;
                }
        }
	
	public static io_initPtr gb_load_rom = new io_initPtr() {
            public int handler(int id) {
                UBytePtr ROM = new UBytePtr(memory_region(REGION_CPU1));
		
	
	  /*** Following are some known manufacturer codes *************************/
		
		_COMPANIES Companies[] =
		{
			new _COMPANIES(0x3301, "Nintendo"),
			new _COMPANIES(0x7901, "Accolade"),
			new _COMPANIES(0xA400, "Konami"),
			new _COMPANIES(0x6701, "Ocean"),
			new _COMPANIES(0x5601, "LJN"),
			new _COMPANIES(0x9900, "ARC?"),
			new _COMPANIES(0x0101, "Nintendo"),
			new _COMPANIES(0x0801, "Capcom"),
			new _COMPANIES(0x0100, "Nintendo"),
			new _COMPANIES(0xBB01, "SunSoft"),
			new _COMPANIES(0xA401, "Konami"),
			new _COMPANIES(0xAF01, "Namcot?"),
			new _COMPANIES(0x4901, "Irem"),
			new _COMPANIES(0x9C01, "Imagineer"),
			new _COMPANIES(0xA600, "Kawada?"),
			new _COMPANIES(0xB101, "Nexoft"),
			new _COMPANIES(0x5101, "Acclaim"),
			new _COMPANIES(0x6001, "Titus"),
			new _COMPANIES(0xB601, "HAL"),
			new _COMPANIES(0x3300, "Nintendo"),
			new _COMPANIES(0x0B00, "Coconuts?"),
			new _COMPANIES(0x5401, "Gametek"),
			new _COMPANIES(0x7F01, "Kemco?"),
			new _COMPANIES(0xC001, "Taito"),
			new _COMPANIES(0xEB01, "Atlus"),
			new _COMPANIES(0xE800, "Asmik?"),
			new _COMPANIES(0xDA00, "Tomy?"),
			new _COMPANIES(0xB100, "ASCII?"),
			new _COMPANIES(0xEB00, "Atlus"),
			new _COMPANIES(0xC000, "Taito"),
			new _COMPANIES(0x9C00, "Imagineer"),
			new _COMPANIES(0xC201, "Kemco?"),
			new _COMPANIES(0xD101, "Sofel?"),
			new _COMPANIES(0x6101, "Virgin"),
			new _COMPANIES(0xBB00, "SunSoft"),
			new _COMPANIES(0xCE01, "FCI?"),
			new _COMPANIES(0xB400, "Enix?"),
			new _COMPANIES(0xBD01, "Imagesoft"),
			new _COMPANIES(0x0A01, "Jaleco?"),
			new _COMPANIES(0xDF00, "Altron?"),
			new _COMPANIES(0xA700, "Takara?"),
			new _COMPANIES(0xEE00, "IGS?"),
			new _COMPANIES(0x8300, "Lozc?"),
			new _COMPANIES(0x5001, "Absolute?"),
			new _COMPANIES(0xDD00, "NCS?"),
			new _COMPANIES(0xE500, "Epoch?"),
			new _COMPANIES(0xCB00, "VAP?"),
			new _COMPANIES(0x8C00, "Vic Tokai"),
			new _COMPANIES(0xC200, "Kemco?"),
			new _COMPANIES(0xBF00, "Sammy?"),
			new _COMPANIES(0x1800, "Hudson Soft"),
			new _COMPANIES(0xCA01, "Palcom/Ultra"),
			new _COMPANIES(0xCA00, "Palcom/Ultra"),
			new _COMPANIES(0xC500, "Data East?"),
			new _COMPANIES(0xA900, "Technos Japan?"),
			new _COMPANIES(0xD900, "Banpresto?"),
			new _COMPANIES(0x7201, "Broderbund?"),
			new _COMPANIES(0x7A01, "Triffix Entertainment?"),
			new _COMPANIES(0xE100, "Towachiki?"),
			new _COMPANIES(0x9300, "Tsuburava?"),
			new _COMPANIES(0xC600, "Tonkin House?"),
			new _COMPANIES(0xCE00, "Pony Canyon"),
			new _COMPANIES(0x7001, "Infogrames?"),
			new _COMPANIES(0x8B01, "Bullet-Proof Software?"),
			new _COMPANIES(0x5501, "Park Place?"),
			new _COMPANIES(0xEA00, "King Records?"),
			new _COMPANIES(0x5D01, "Tradewest?"),
			new _COMPANIES(0x6F01, "ElectroBrain?"),
			new _COMPANIES(0xAA01, "Broderbund?"),
			new _COMPANIES(0xC301, "SquareSoft"),
			new _COMPANIES(0x5201, "Activision?"),
			new _COMPANIES(0x5A01, "Bitmap Brothers/Mindscape"),
			new _COMPANIES(0x5301, "American Sammy"),
			new _COMPANIES(0x4701, "Spectrum Holobyte"),
			new _COMPANIES(0x1801, "Hudson Soft"),
			new _COMPANIES(0x0000, null)
		};
	
		int Checksum, I, J;
		/*TODO*///char *P, S[50];
		Object F;
		int rambanks[] =
		{0, 1, 1, 4};
	
		for (I = 0; I < 256; I++)
			RAMMap[I] = ROMMap[I] = null;
	
		if(device_filename(IO_CARTSLOT,id)==null)
		{
			printf("Cartridge name not specified!\n");
			return INIT_FAIL;
		}
		if( new_memory_region(REGION_CPU1, 0x10000,0) != 0)
		{
			logerror("Memory allocation failed reading roms!\n");
	        return 1;
	    }
	
		ROM = new UBytePtr(memory_region(REGION_CPU1));
                gb_ram = new UBytePtr(memory_region(REGION_CPU1));
		memset (ROM, 0, 0x10000);
	
		/* FIXME should check first if a file is given, should give a more clear error */
		if ((F = image_fopen (IO_CARTSLOT, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_READ))==null)
		{
			logerror("image_fopen failed in gb_load_rom.\n");
			return 1;
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
	        return 1;
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
			return 1;
		}
	
		ROMMap[0] = gb_ram;
		ROMBanks = 2 << gb_ram.read(0x0148);
		RAMBanks = rambanks[gb_ram.read(0x0149) & 3];
		Checksum = (gb_ram.read(0x014E) << 8) + gb_ram.read(0x014F);
		MBCType = gb_ram.read(0x0147) > 3 ? 1 : 0;
	
		if ((gb_ram.read(0x0147) == 4) || (gb_ram.read(0x0147) > 6))
		{
			logerror("Error loading cartridge: Unknown ROM type");
			osd_fclose (F);
			return 1;
		}
	
		if (Verbose != 0)
		{
		/*TODO*///	strncpy (S, (char *)&gb_ram[0x0134], 16);
		/*TODO*///	S[16] = '\0';
		/*TODO*///	logerror("OK\n  Name: %s\n", S);
		/*TODO*///	logerror("  Type: %s\n", CartTypes[gb_ram[0x0147]]);
		/*TODO*///	logerror("  ROM Size: %d 16kB Banks\n", ROMBanks);
		/*TODO*///	J = (gb_ram[0x0149] & 0x03) * 2;
		/*TODO*///	J = J ? (1 << (J - 1)) : 0;
		/*TODO*///	logerror("  RAM Size: %d kB\n", J);
	
		/*TODO*///	J = ((UINT16) gb_ram[0x014B] << 8) + gb_ram[0x014A];
		/*TODO*///	for (I = 0, P = null; !P && Companies[I].Name; I++)
		/*TODO*///		if (J == Companies[I].Code)
		/*TODO*///			P = Companies[I].Name;
		/*TODO*///	logerror("  Manufacturer ID: %Xh", J);
		/*TODO*///	logerror(" [%s]\n", P ? P : "?");
	
		/*TODO*///	logerror("  Version Number: %Xh\n", gb_ram[0x014C]);
		/*TODO*///	logerror("  Complement Check: %Xh\n", gb_ram[0x014D]);
		/*TODO*///	logerror("  Checksum: %Xh\n", Checksum);
		/*TODO*///	J = ((UINT16) gb_ram[0x0103] << 8) + gb_ram[0x0102];
		/*TODO*///	logerror("  Start Address: %Xh\n", J);
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
					/*TODO*///if (Verbose != 0)
					/*TODO*///	putchar ('.');
				}
				else
				{
					logerror("Error while reading from file: %s\n", device_filename(IO_CARTSLOT,id));
					break;
				}
			}
			else
			{
				logerror("Error alocating memory\n");
				break;
			}
		}
	
		osd_fclose (F);
		if (I < ROMBanks)
			return 1;
	
		if (CheckCRC!=0 && (Checksum & 0xFFFF)!=0)
		{
			logerror("Error loading cartridge: Checksum is wrong");
			return 1;
		}
	
		if (RAMBanks!=0 && MBCType==0)
		{
			for (I = 0; I < RAMBanks; I++)
			{
				if ((RAMMap[I] = new UBytePtr(0x2000)) != null)
					memset (RAMMap[I], 0, 0x2000);
				else
				{
					logerror("Error alocating memory\n");
					return 1;
				}
			}
		}
	
	
	/*TODO*///#if 0	/* FIXME */
	/*TODO*///	if ((gb_ram[0x0147] == 3) || (gb_ram[0x0147] == 6))
	/*TODO*///	{
	/*TODO*///		strcpy (TempFileName, BaseCartName);
	/*TODO*///		strcat (TempFileName, ".sav");
	/*TODO*///		if (Verbose)
	/*TODO*///			logerror("Opening %s...", TempFileName);
	/*TODO*///		if (F = fopen (TempFileName, "rb"))
	/*TODO*///		{
	/*TODO*///			if (Verbose)
	/*TODO*///				logerror("reading...");
	/*TODO*///			if (gb_ram[0x0147] == 3)
	/*TODO*///			{
	/*TODO*///				J = 0;
	/*TODO*///				for (I = 0; I < RAMBanks; I++)
	/*TODO*///				{
	/*TODO*///					J += fread (RAMMap[I], 1, 0x2000, F);
	/*TODO*///				}
	/*TODO*///				if (Verbose)
	/*TODO*///					puts ((J == RAMBanks * 0x2000) ? "OK" : "FAILED");
	/*TODO*///			}
	/*TODO*///			else
	/*TODO*///			{
	/*TODO*///				J = 0x0200;
	/*TODO*///				J = (fread (Page[5], 1, J, F) == J);
	/*TODO*///				if (Verbose)
	/*TODO*///					puts (J ? "OK" : "FAILED");
	/*TODO*///			}
	/*TODO*///			fclose (F);
	/*TODO*///		}
	/*TODO*///		else if (Verbose)
	/*TODO*///			puts ("FAILED");
	/*TODO*///	}
	/*TODO*///#endif
	
		if (ROMBanks < 3)
			ROMMask = 0;
		else
		{
			for (I = 1; I < ROMBanks; I <<= 1) ;
			ROMMask = I - 1;
			ROMBank = 1;
		}
		if (RAMMap[0]==null)
			RAMMask = 0;
		else
		{
			for (I = 1; I < RAMBanks; I <<= 1) ;
			RAMMask = I - 1;
			RAMBank = 0;
		}
	
		return 0;
            }
        };
	
        static int count = 0;
	
	public static InterruptPtr gb_scanline_interrupt = new InterruptPtr() { public int handler() 
	{
		/* test ! */
		
	
		count = (count + 1) % 3;
		switch (count)
		{
		case 0:
			/* continue */
			break;
		case 1:
			gb_scanline_interrupt_set_mode2.handler(0);
			return ignore_interrupt.handler();
		case 2:
			gb_scanline_interrupt_set_mode3.handler(0);
			return ignore_interrupt.handler();
		}
	
		/* first lett's draw the current scanline */
		if (CURLINE() < 144)
			gb_refresh_scanline ();
	
		/* the rest only makes sense if the display is enabled */
		if ((LCDCONT() & 0x80) != 0)
		{
			if (CURLINE() == CMPLINE())
			{
				LCDSTAT( LCDSTAT() | 0x04 );
				/* generate lcd interrupt if requested */
				if(( LCDSTAT() & 0x40 ) != 0)
					cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
			}
			else
				LCDSTAT( LCDSTAT() & 0xFB );
	
			CURLINE( (CURLINE() + 1) % 154 );
	
			//gb_ram[0xFF44] = CURLINE;
	
			if (CURLINE() < 144)
			{
				/* first  lcdstate change after aprox 49 uS */
				timer_set (49.0 / 1000000.0, 0, gb_scanline_interrupt_set_mode2);
	
				/* second lcdstate change after aprox 69 uS */
				timer_set (69.0 / 1000000.0, 0, gb_scanline_interrupt_set_mode3);
	
				/* modify lcdstate */
				LCDSTAT( LCDSTAT() & 0xFC );
	
				/* generate lcd interrupt if requested */
				if(( LCDSTAT() & 0x08 ) != 0)
					{
						/*logerror("generating lcd interrupt\n");*/
						cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
					}
			}
			else
			{
				/* generate VBlank interrupt */
				if (CURLINE() == 144)
				{
					/* cause VBlank interrupt */
					cpu_set_irq_line(0, VBL_INT, HOLD_LINE);
					/* Set VBlank lcdstate */
					LCDSTAT( (LCDSTAT() & 0xFC) | 0x01 );
					/* generate lcd interrupt if requested */
					if(( LCDSTAT() & 0x10 ) != 0)
						cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
				}
			}
	
			/* Generate serial IO interrupt */
			if (SIOCount != 0)
			{
				SIODATA( (SIODATA() << 1) | 0x01 );
				if (--SIOCount == 0)
				{
					SIOCONT( SIOCONT() & 0x7F );
					if(( LCDSTAT() & 0x10 ) != 0)
						cpu_set_irq_line(0, SIO_INT, HOLD_LINE);
				}
			}
		}
	
		/* Return No interrupt, we cause them ourselves since multiple int's can
		 * occur at the same time */
		return ignore_interrupt.handler();
            }
        };
	
	public static timer_callback gb_scanline_interrupt_set_mode2 = new timer_callback() {
            public void handler(int param) {
                /* modify lcdstate */
		LCDSTAT( (LCDSTAT() & 0xFC) | 0x02 );
		/* generate lcd interrupt if requested */
		if ((LCDSTAT() & 0x20) != 0)
			cpu_set_irq_line(0, LCD_INT, HOLD_LINE);
            }
        };
	
	public static timer_callback gb_scanline_interrupt_set_mode3 = new timer_callback() {
            public void handler(int param) {
		/* modify lcdstate */
		LCDSTAT( (LCDSTAT() & 0xFC) | 0x03 );
            }
        };
}
