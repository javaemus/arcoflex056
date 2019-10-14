/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

import static mess056.includes.cbmdriveH.*;
import static mess056.machine.cbmserb.*;
import static mame056.timer.*;
import static arcadeflex056.osdepend.logerror;
import static common.libc.cstdio.printf;

public class cbmdrive
{
	/*
	#define VERBOSE_DBG 1				   /* general debug messages */
	
	
	/* tracks 1 to 35
	 * sectors number from 0
	 * each sector holds 256 data bytes
	 * directory and Bitmap Allocation Memory in track 18
	 * sector 0:
	 * 0: track# of directory begin (this linkage of sector often used)
	 * 1: sector# of directory begin
	 *
	 * BAM entries (one per track)
	 * offset 0: # of free sectors
	 * offset 1: sector 0 (lsb) free to sector 7
	 * offset 2: sector 8 to 15
	 * offset 3: sector 16 to whatever the number to sectors in track is
	 *
	 * directory sector:
	 * 0,1: track sector of next directory sector
	 * 2, 34, 66, ... : 8 directory entries
	 *
	 * directory entry:
	 * 0: file type
	 * (0x = scratched/splat, 8x = alive, Cx = locked
	 * where x: 0=DEL, 1=SEQ, 2=PRG, 3=USR, 4=REL)
	 * 1,2: track and sector of file
	 * 3..18: file name padded with a0
	 * 19,20: REL files side sector
	 * 21: REL files record length
	 * 28,29: number of blocks in file
	 * ended with illegal track and sector numbers
	 */
	
	public static int D64_MAX_TRACKS = 35;
	
	static int d64_sectors_per_track[] =
	{
		21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
		19, 19, 19, 19, 19, 19, 19,
		18, 18, 18, 18, 18, 18,
		17, 17, 17, 17, 17
	};
	
	public static int[] d64_offset = new int[D64_MAX_TRACKS];		   /* offset of begin of track in d64 file */
	
	/* must be called before other functions */
	public static void cbm_drive_open_helper ()
	{
		int i;
	
		d64_offset[0] = 0;
		for (i = 1; i <= 35; i++)
			d64_offset[i] = d64_offset[i - 1] + d64_sectors_per_track[i - 1] * 256;
	}
	
	/* calculates offset to beginning of d64 file for sector beginning */
	public static int d64_tracksector2offset (int track, int sector)
	{
		return d64_offset[track - 1] + sector * 256;
	}
	
	static int cbm_compareNames (char[] left, char[] right)
	{
		int i;
	
		for (i = 0; i < 16; i++)
		{
			if ((left[i] == '*') || (right[i] == '*'))
				return 1;
			if (left[i] == right[i])
				continue;
			if ((left[i] == 0xa0) && (right[i] == 0))
				return 1;
			if ((right[i] == 0xa0) && (left[i] == 0))
				return 1;
			return 0;
		}
		return 1;
	}
	
	/* searches program with given name in directory
	 * delivers -1 if not found
	 * or pos in image of directory node */
	static int d64_find (CBM_Drive drive, String name)
	{
		int pos, track, sector, i;
	
		pos = d64_tracksector2offset (18, 0);
		track = drive.d.d64.image.read(pos);
		sector = drive.d.d64.image.read(pos + 1);
	
		while ((track >= 1) && (track <= 35))
		{
			pos = d64_tracksector2offset (track, sector);
			for (i = 2; i < 256; i += 32)
			{
				if ((drive.d.d64.image.read(pos + i) & 0x80) != 0)
				{
					if (name.compareTo("*") == 0)
						return pos + i;
		/*TODO*///			if (cbm_compareNames (name, drive.d.d64.image + pos + i + 3))
		/*TODO*///				return pos + i;
				}
			}
			track = drive.d.d64.image.read(pos);
			sector = drive.d.d64.image.read(pos + 1);
		}
		return -1;
	}
	
	/* reads file into buffer */
	static void d64_readprg (CBM_Drive c1551, int pos)
	{
		int i;
	
	/*TODO*///	for (i = 0; i < 16; i++)
	/*TODO*///		c1551.d.d64.filename[i] = toupper (c1551.d.d64.image[pos + i + 3]);
	
	/*TODO*///	c1551.d.d64.filename[i] = 0;
	
	/*TODO*///	pos = d64_tracksector2offset (c1551.d.d64.image[pos + 1], c1551.d.d64.image[pos + 2]);
	
		i = pos;
		c1551.size = 0;
	/*TODO*///	while (c1551.d.d64.image[i] != 0)
	/*TODO*///	{
	/*TODO*///		c1551.size += 254;
	/*TODO*///		i = d64_tracksector2offset (c1551.d.d64.image[i], c1551.d.d64.image[i + 1]);
	/*TODO*///	}
	/*TODO*///	c1551.size += c1551.d.d64.image[i + 1];
	
		/*TODO*///DBG_LOG (3, "d64 readprg", ("size %d\n", c1551.size));
	
	/*TODO*///	c1551.buffer = (UINT8*)realloc (c1551.buffer, c1551.size);
	/*TODO*///	if (!c1551.buffer) {
	/*TODO*///		logerror("out of memory %s %d\n",
	/*TODO*///				__FILE__, __LINE__);
	/*TODO*///		osd_exit();
	/*TODO*///		exit(1);
	/*TODO*///	}
	
		c1551.size--;
	
		/*TODO*///DBG_LOG (3, "d64 readprg", ("track: %d sector: %d\n",
		/*TODO*///							c1551.d.d64.image[pos + 1],
		/*TODO*///							c1551.d.d64.image[pos + 2]));
	
		for (i = 0; i < c1551.size; i += 254)
		{
			if (i + 254 < c1551.size)
			{							   /* not last sector */
	/*TODO*///			memcpy (c1551.buffer + i, c1551.d.d64.image + pos + 2, 254);
	/*TODO*///			pos = d64_tracksector2offset (c1551.d.d64.image[pos + 0],
	/*TODO*///									  c1551.d.d64.image[pos + 1]);
				/*TODO*///DBG_LOG (3, "d64 readprg", ("track: %d sector: %d\n",
				/*TODO*///							c1551.d.d64.image[pos],
				/*TODO*///							c1551.d.d64.image[pos + 1]));
			}
			else
			{
	/*TODO*///			memcpy (c1551.buffer + i, c1551.d.d64.image + pos + 2, c1551.size - i);
			}
		}
	}
	
	/* reads sector into buffer */
	static void d64_read_sector (CBM_Drive c1551, int track, int sector)
	{
		int pos;
	
		printf (c1551.d.d64.filename, c1551.d.d64.filename.length(),
				  "track %d sector %d", track, sector);
	
		pos = d64_tracksector2offset (track, sector);
	
		/*TODO*///c1551.buffer = (UINT8*)realloc (c1551.buffer,256);
		/*TODO*///if (!c1551.buffer) {
		/*TODO*///	logerror("out of memory %s %d\n",__FILE__, __LINE__);
		/*TODO*///	osd_exit();
		/*TODO*///	exit(1);
		/*TODO*///}
	
		logerror("d64 read track %d sector %d\n", track, sector);
	
		/*TODO*///memcpy (c1551.buffer, c1551.d.d64.image + pos, 256);
		c1551.size = 256;
		c1551.pos = 0;
	}
	
	/* reads directory into buffer */
	static void d64_read_directory (CBM_Drive c1551)
	{
		int pos, track, sector, i, j, blocksfree, addr = 0x0101/*0x1001*/;
	
	/*TODO*///	c1551.buffer = (UINT8*)realloc (c1551.buffer, 8 * 18 * 25);
	/*TODO*///	if (!c1551.buffer) {
	/*TODO*///		logerror("out of memory %s %d\n",
	/*TODO*///				__FILE__, __LINE__);
	/*TODO*///		osd_exit();
	/*TODO*///		exit(1);
	/*TODO*///	}
	
		c1551.size = 0;
	
		pos = d64_tracksector2offset (18, 0);
	/*TODO*///	track = c1551.d.d64.image[pos];
	/*TODO*///	sector = c1551.d.d64.image[pos + 1];
	
		blocksfree = 0;
		for (j = 1, i = 4; j <= 35; j++, i += 4)
		{
	/*TODO*///		blocksfree += c1551.d.d64.image[pos + i];
		}
	/*TODO*///	c1551.buffer[c1551.size++] = addr & 0xff;
	/*TODO*///	c1551.buffer[c1551.size++] = addr >> 8;
		addr += 29;
	/*TODO*///	c1551.buffer[c1551.size++] = addr & 0xff;
	/*TODO*///	c1551.buffer[c1551.size++] = addr >> 8;
	/*TODO*///	c1551.buffer[c1551.size++] = 0;
	/*TODO*///	c1551.buffer[c1551.size++] = 0;
	/*TODO*///	c1551.buffer[c1551.size++] = '\"';
	/*TODO*///	for (j = 0; j < 16; j++)
	/*TODO*///		c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + 0x90 + j];
	/*memcpy(c1551.buffer+c1551.size,c1551.image+pos+0x90, 16);c1551.size+=16; */
	/*TODO*///	c1551.buffer[c1551.size++] = '\"';
	/*TODO*///	c1551.buffer[c1551.size++] = ' ';
	/*TODO*///	c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + 162];
	/*TODO*///	c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + 163];
	/*TODO*///	c1551.buffer[c1551.size++] = ' ';
	/*TODO*///	c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + 165];
	/*TODO*///	c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + 166];
	/*TODO*///	c1551.buffer[c1551.size++] = 0;
	/*TODO*///
	/*TODO*///	while ((track >= 1) && (track <= 35))
	/*TODO*///	{
	/*TODO*///		pos = d64_tracksector2offset (track, sector);
	/*TODO*///		for (i = 2; i < 256; i += 32)
	/*TODO*///		{
	/*TODO*///			if (c1551.d.d64.image[pos + i] & 0x80)
	/*TODO*///			{
	/*TODO*///				int len, blocks = c1551.d.d64.image[pos + i + 28]
	/*TODO*///				+ 256 * c1551.d.d64.image[pos + i + 29];
	/*TODO*///				char dummy[10];
	/*TODO*///
	/*TODO*///				sprintf (dummy, "%d", blocks);
	/*TODO*///				len = strlen (dummy);
	/*TODO*///				addr += 29 - len;
	/*TODO*///				c1551.buffer[c1551.size++] = addr & 0xff;
	/*TODO*///				c1551.buffer[c1551.size++] = addr >> 8;
	/*TODO*///				c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + i + 28];
	/*TODO*///				c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + i + 29];
	/*TODO*///				for (j = 4; j > len; j--)
	/*TODO*///					c1551.buffer[c1551.size++] = ' ';
	/*TODO*///				c1551.buffer[c1551.size++] = '\"';
	/*TODO*///				for (j = 0; j < 16; j++)
	/*TODO*///					c1551.buffer[c1551.size++] = c1551.d.d64.image[pos + i + 3 + j];
	/*TODO*///				c1551.buffer[c1551.size++] = '\"';
	/*TODO*///				c1551.buffer[c1551.size++] = ' ';
	/*TODO*///				switch (c1551.d.d64.image[pos + i] & 0x3f)
	/*TODO*///				{
	/*TODO*///				case 0:
	/*TODO*///					c1551.buffer[c1551.size++] = 'D';
	/*TODO*///					c1551.buffer[c1551.size++] = 'E';
	/*TODO*///					c1551.buffer[c1551.size++] = 'L';
	/*TODO*///					break;
	/*TODO*///				case 1:
	/*TODO*///					c1551.buffer[c1551.size++] = 'S';
	/*TODO*///					c1551.buffer[c1551.size++] = 'E';
	/*TODO*///					c1551.buffer[c1551.size++] = 'Q';
	/*TODO*///					break;
	/*TODO*///				case 2:
	/*TODO*///					c1551.buffer[c1551.size++] = 'P';
/*TODO*///						c1551.buffer[c1551.size++] = 'R';
/*TODO*///						c1551.buffer[c1551.size++] = 'G';
/*TODO*///						break;
/*TODO*///					case 3:
/*TODO*///						c1551.buffer[c1551.size++] = 'U';
/*TODO*///						c1551.buffer[c1551.size++] = 'S';
/*TODO*///						c1551.buffer[c1551.size++] = 'R';
/*TODO*///						break;
/*TODO*///					case 4:
/*TODO*///						c1551.buffer[c1551.size++] = 'R';
/*TODO*///						c1551.buffer[c1551.size++] = 'E';
/*TODO*///						c1551.buffer[c1551.size++] = 'L';
/*TODO*///						break;
/*TODO*///					}
/*TODO*///					c1551.buffer[c1551.size++] = 0;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			track = c1551.d.d64.image[pos];
/*TODO*///			sector = c1551.d.d64.image[pos + 1];
/*TODO*///		}
/*TODO*///		addr += 14;
/*TODO*///		c1551.buffer[c1551.size++] = addr & 0xff;
/*TODO*///		c1551.buffer[c1551.size++] = addr >> 8;
/*TODO*///		c1551.buffer[c1551.size++] = blocksfree & 0xff;
/*TODO*///		c1551.buffer[c1551.size++] = blocksfree >> 8;
/*TODO*///		memcpy (c1551.buffer + c1551.size, "BLOCKS FREE", 11);
/*TODO*///		c1551.size += 11;
/*TODO*///		c1551.buffer[c1551.size++] = 0;
/*TODO*///	
/*TODO*///		strcpy (c1551.d.d64.filename, "$");
	}
	
	static int c1551_d64_command (CBM_Drive c1551, String name)
	{
		int pos;
	
		/* name eventuell mit 0xa0 auffuellen */
	
		/*TODO*///if (stricmp ((char *) name, (char *) "$") == 0)
		/*TODO*///{
		/*TODO*///	d64_read_directory (c1551);
		/*TODO*///}
		/*TODO*///else
		/*TODO*///{
		/*TODO*///	if ((pos = d64_find (c1551, name)) == -1)
		/*TODO*///	{
		/*TODO*///		return 1;
		/*TODO*///	}
		/*TODO*///	d64_readprg (c1551, pos);
		/*TODO*///}
		return 0;
	}
	
	static int c1551_fs_command (CBM_Drive c1551, String name)
	{
	/*TODO*///	FILE *fp;
/*TODO*///		int type=0;
/*TODO*///		int read;
/*TODO*///		int i;
/*TODO*///		char n[32];
/*TODO*///	
/*TODO*///		strcpy(n,(char*)name);
/*TODO*///		fp = (FILE*)osd_fopen (Machine.gamedrv.name, n, OSD_FILETYPE_IMAGE, 0);
/*TODO*///	
/*TODO*///		if (fp == 0)
/*TODO*///		{
/*TODO*///			for (i = 0; n[i] != 0; i++)
/*TODO*///				n[i] = tolower (n[i]);
/*TODO*///			fp = (FILE*)osd_fopen (Machine.gamedrv.name, n, OSD_FILETYPE_IMAGE, 0);
/*TODO*///		}
/*TODO*///		if (fp == 0)
/*TODO*///		{
/*TODO*///			strcpy(n, (char*)name);
/*TODO*///			strcat ((char *) n, ".prg");
/*TODO*///	
/*TODO*///			fp = (FILE*)osd_fopen (Machine.gamedrv.name, n, OSD_FILETYPE_IMAGE, 0);
/*TODO*///		}
/*TODO*///		if (fp == 0)
/*TODO*///		{
/*TODO*///			for (i = 0; n[i] != 0; i++)
/*TODO*///				n[i] = tolower (n[i]);
/*TODO*///			fp = (FILE*)osd_fopen (Machine.gamedrv.name, n, OSD_FILETYPE_IMAGE, 0);
/*TODO*///		}
/*TODO*///		if (fp == 0)
/*TODO*///		{
/*TODO*///			type=1;
/*TODO*///			strcpy(n,(char*)name);
/*TODO*///			strcat(n,".p00");
/*TODO*///			fp = (FILE*)osd_fopen (Machine.gamedrv.name, n, OSD_FILETYPE_IMAGE, 0);
/*TODO*///		}
/*TODO*///		if (fp == 0)
/*TODO*///		{
/*TODO*///			for (i = 0; n[i] != 0; i++)
/*TODO*///				n[i] = tolower (n[i]);
/*TODO*///			fp = (FILE*)osd_fopen (Machine.gamedrv.name, n, OSD_FILETYPE_IMAGE, 0);
/*TODO*///		}
/*TODO*///		if (fp)
/*TODO*///		{
/*TODO*///			if (type==1)
/*TODO*///			{
/*TODO*///				c1551.size = osd_fsize (fp);
/*TODO*///				c1551.buffer = (UINT8*)realloc (c1551.buffer, c1551.size);
/*TODO*///				if (!c1551.buffer) {
/*TODO*///					logerror("out of memory %s %d\n",__FILE__, __LINE__);
/*TODO*///					osd_exit();
/*TODO*///					exit(1);
/*TODO*///				}
/*TODO*///	
/*TODO*///				read = osd_fread (fp, c1551.buffer, 26);
/*TODO*///				strncpy (c1551.d.fs.filename, (char *) c1551.buffer + 8, 16);
/*TODO*///				c1551.size -= 26;
/*TODO*///				read = osd_fread (fp, c1551.buffer, c1551.size);
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				c1551.size = osd_fsize (fp);
/*TODO*///				c1551.buffer = (UINT8*)realloc (c1551.buffer,c1551.size);
/*TODO*///				if (!c1551.buffer) {
/*TODO*///					logerror("out of memory %s %d\n",__FILE__, __LINE__);
/*TODO*///					osd_exit();
/*TODO*///					exit(1);
/*TODO*///				}
/*TODO*///	
/*TODO*///				read = osd_fread (fp, c1551.buffer, c1551.size);
/*TODO*///				osd_fclose (fp);
/*TODO*///				logerror("loading file %s\n", name);
/*TODO*///				strcpy (c1551.d.fs.filename, (char *) name);
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			logerror("file %s not found\n", name);
/*TODO*///			return 1;
/*TODO*///		}
		return 0;
	}
	
	/**
	  7.1 Serial bus
	
	   CBM Serial Bus Control Codes
	
		20	Talk
		3F	Untalk
		40	Listen
		5F	Unlisten
		60	Open Channel
		70	-
		80	-
		90	-
		A0	-
		B0	-
		C0	-
		D0	-
		E0	Close
		F0	Open
	
	
	
		 How the C1541 is called by the C64:
	
			read (drive 8)
			/28 /f0 filename /3f
			/48 /60 read data /5f
			/28 /e0 /3f
	
			write (drive 8)
			/28 /f0 filename /3f
			/28 /60 send data /3f
			/28 /e0 /3f
	
		 I used '/' to denote bytes sent under Attention (ATN low).
	
		28 == LISTEN command + device number 8
		f0 == secondary addres for OPEN file on channel 0
	
	  Note that there's no acknowledge bit, but timeout/EOI handshake for each
	  byte. Check the C64 Kernel for exact description...
	
	 computer master
	
	 c16 called
	 dload
	  20 f0 30 3a name 3f
	  40 60 listening 5f
	  20 e0 3f
	
	 load
	  20 f0 name 3f
	 */
	static void cbm_command (CBM_Drive drive)
	{
/*TODO*///		unsigned char name[20], type = 'P', mode = 0;
/*TODO*///		int channel, head, track, sector;
/*TODO*///		int j, i, rc;
/*TODO*///	
/*TODO*///		if ((drive.cmdpos == 4)
/*TODO*///			&& ((drive.cmdbuffer[0] & 0xf0) == 0x20)
/*TODO*///			&& ((drive.cmdbuffer[1] & 0xf0) == 0xf0)
/*TODO*///			&& (drive.cmdbuffer[2] == '#')
/*TODO*///			&& (drive.cmdbuffer[3] == 0x3f))
/*TODO*///		{
/*TODO*///			logerror("floppy direct access channel %d opened\n",
/*TODO*///						 (unsigned) drive.cmdbuffer[1] & 0xf);
/*TODO*///		}
/*TODO*///		else if ((drive.cmdpos >= 4)
/*TODO*///				 && (((unsigned) drive.cmdbuffer[0] & 0xf0) == 0x20)
/*TODO*///				 && (((unsigned) drive.cmdbuffer[1] & 0xf0) == 0xf0)
/*TODO*///				 && (drive.cmdbuffer[drive.cmdpos - 1] == 0x3f))
/*TODO*///		{
/*TODO*///			if (drive.cmdbuffer[3] == ':')
/*TODO*///				j = 4;
/*TODO*///			else
/*TODO*///				j = 2;
/*TODO*///	
/*TODO*///			for (i = 0; (j < sizeof (name)) && (drive.cmdbuffer[j] != 0x3f)
/*TODO*///				 && (drive.cmdbuffer[j] != ',');
/*TODO*///				 i++, j++)
/*TODO*///				name[i] = drive.cmdbuffer[j];
/*TODO*///			name[i] = 0;
/*TODO*///	
/*TODO*///			if (drive.cmdbuffer[j] == ',')
/*TODO*///			{
/*TODO*///				j++;
/*TODO*///				if (j < drive.cmdpos)
/*TODO*///				{
/*TODO*///					type = drive.cmdbuffer[j];
/*TODO*///					j++;
/*TODO*///					if ((j < drive.cmdpos) && (drive.cmdbuffer[j] == 'j'))
/*TODO*///					{
/*TODO*///						j++;
/*TODO*///						if (j < drive.cmdpos)
/*TODO*///							mode = drive.cmdbuffer[j];
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///			rc = 1;
/*TODO*///			if (drive.drive == D64_IMAGE)
/*TODO*///			{
/*TODO*///				if ((type == 'P') || (type == 'S'))
/*TODO*///					rc = c1551_d64_command (drive, name);
/*TODO*///			}
/*TODO*///			else if (drive.drive == FILESYSTEM)
/*TODO*///			{
/*TODO*///				if (type == 'P')
/*TODO*///					rc = c1551_fs_command (drive, name);
/*TODO*///			}
/*TODO*///			if (rc == 0)
/*TODO*///			{
/*TODO*///				drive.state = OPEN;
/*TODO*///				drive.pos = 0;
/*TODO*///			}
/*TODO*///			/*TODO*///DBG_LOG (1, "cbm_open", ("%s %s type:%c %c\n", name,
/*TODO*///									 rc ? "failed" : "success", type, mode ? mode : ' '));
/*TODO*///		}
/*TODO*///		else if ((drive.cmdpos == 1) && (drive.cmdbuffer[0] == 0x5f))
/*TODO*///		{
/*TODO*///			drive.state = OPEN;
/*TODO*///		}
/*TODO*///		else if ((drive.cmdpos == 3) && ((drive.cmdbuffer[0] & 0xf0) == 0x20)
/*TODO*///				 && ((drive.cmdbuffer[1] & 0xf0) == 0xe0)
/*TODO*///				 && (drive.cmdbuffer[2] == 0x3f))
/*TODO*///		{
/*TODO*///	/*    if (drive.buffer) free(drive.buffer);drive.buffer=0; */
/*TODO*///			drive.state = 0;
/*TODO*///		}
/*TODO*///		else if ((drive.cmdpos == 2) && ((drive.cmdbuffer[0] & 0xf0) == 0x40)
/*TODO*///				 && ((drive.cmdbuffer[1] & 0xf0) == 0x60))
/*TODO*///		{
/*TODO*///			if (drive.state == OPEN)
/*TODO*///			{
/*TODO*///				drive.state = READING;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else if ((drive.cmdpos == 2) && ((drive.cmdbuffer[0] & 0xf0) == 0x20)
/*TODO*///				 && ((drive.cmdbuffer[1] & 0xf0) == 0x60))
/*TODO*///		{
/*TODO*///			drive.state = WRITING;
/*TODO*///		}
/*TODO*///		else if ((drive.cmdpos == 1) && (drive.cmdbuffer[0] == 0x3f))
/*TODO*///		{
/*TODO*///			drive.state = OPEN;
/*TODO*///		}
/*TODO*///		else if ((drive.drive == D64_IMAGE)
/*TODO*///				 && (4 == sscanf ((char *) drive.cmdbuffer, "U1: %d %d %d %d\x0d",
/*TODO*///								  &channel, &head, &track, &sector)))
/*TODO*///		{
/*TODO*///			d64_read_sector (drive, track, sector);
/*TODO*///			drive.state = OPEN;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///	
/*TODO*///			logerror("unknown floppycommand(size:%d):", drive.cmdpos);
/*TODO*///			for (i = 0; i < drive.cmdpos; i++)
/*TODO*///				logerror("%.2x", drive.cmdbuffer[i]);
/*TODO*///			logerror(" ");
/*TODO*///			for (i = 0; i < drive.cmdpos; i++)
/*TODO*///				logerror("%c", drive.cmdbuffer[i]);
/*TODO*///			logerror("\n");
/*TODO*///	
/*TODO*///			drive.state = 0;
/*TODO*///		}
/*TODO*///		drive.cmdpos = 0;
	}
	
	 /*
	  * 0x55 begin of command
	  *
	  * frame
	  * selector
	  * 0x81 device id
	  * 0x82 command
	  * 0x83 data
	  * 0x84 read byte
	  * handshake low
	  *
	  * byte (like in serial bus!)
	  *
	  * floppy drive delivers
	  * status 3 for file not found
	  * or filedata ended with status 3
	  */
	public static void c1551_state (CBM_Drive c1551)
	{
	/*TODO*///#if VERBOSE_DBG
	/*TODO*///	static int oldstate;
	/*TODO*///
	/*TODO*///	oldstate = c1551.i.iec.state;
	/*TODO*///#endif
	
		switch (c1551.i.iec.state)
		{
		case -1:						   /* currently neccessary for correct init */
			if (c1551.i.iec.handshakein != 0)
			{
				c1551.i.iec.state++;
			}
			break;
		case 0:
			if (c1551.i.iec.datain == 0x55)
			{
				c1551.i.iec.status = 0;
				c1551.i.iec.state++;
			}
			break;
		case 1:
			if (c1551.i.iec.datain != 0x55)
			{
				c1551.i.iec.state = 10;
			}
			break;
		case 10:
			if (c1551.i.iec.datain != 0)
			{
				c1551.i.iec.handshakeout = 0;
				if (c1551.i.iec.datain == 0x84)
				{
					c1551.i.iec.state = 20;
					if (c1551.pos + 1 == c1551.size)
						c1551.i.iec.status = 3;
				}
				else if (c1551.i.iec.datain == 0x83)
				{
					c1551.i.iec.state = 40;
				}
				else
				{
					c1551.i.iec.status = 0;
					c1551.i.iec.state++;
				}
			}
			break;
		case 11:
			if (c1551.i.iec.handshakein==0)
			{
				c1551.i.iec.state++;
				/*TODO*///DBG_LOG(1,"c1551",("taken data %.2x\n",c1551.i.iec.datain));
				/*TODO*///if (c1551.cmdpos < sizeof (c1551.cmdbuffer))
				/*TODO*///	c1551.cmdbuffer[c1551.cmdpos++] = c1551.i.iec.datain;
				if ((c1551.i.iec.datain == 0x3f) || (c1551.i.iec.datain == 0x5f))
				{
					cbm_command (c1551);
					c1551.i.iec.state = 30;
				}
				else if (((c1551.i.iec.datain & 0xf0) == 0x60))
				{
					cbm_command (c1551);
					if (c1551.state == READING)
					{
					}
					else if (c1551.state == WRITING)
					{
					}
					else
						c1551.i.iec.status = 3;
				}
				c1551.i.iec.handshakeout = 1;
			}
			break;
		case 12:
			if (c1551.i.iec.datain == 0)
			{
				c1551.i.iec.state++;
			}
			break;
		case 13:
			if (c1551.i.iec.handshakein != 0)
			{
				c1551.i.iec.state = 10;
			}
			break;
	
		case 20:						   /* reading data */
			if (c1551.i.iec.handshakein == 0)
			{
				c1551.i.iec.handshakeout = 1;
				/*TODO*///if (c1551.state == READING)
				/*TODO*///	c1551.i.iec.dataout = c1551.buffer[c1551.pos++];
				c1551.i.iec.state++;
			}
			break;
		case 21:						   /* reading data */
			if (c1551.i.iec.handshakein != 0)
			{
				c1551.i.iec.handshakeout = 0;
				c1551.i.iec.state++;
			}
			break;
		case 22:
			if (c1551.i.iec.datain == 0)
			{
				c1551.i.iec.state++;
			}
			break;
		case 23:
			if (c1551.i.iec.handshakein == 0)
			{
				c1551.i.iec.handshakeout = 1;
				if (c1551.state == READING)
					c1551.i.iec.state = 10;
				else
					c1551.i.iec.state = 0;
			}
			break;
	
		case 30:						   /* end of command */
			if (c1551.i.iec.datain == 0)
			{
				c1551.i.iec.state++;
			}
			break;
		case 31:
			if (c1551.i.iec.handshakein != 0)
			{
				c1551.i.iec.state = 0;
			}
			break;
	
		case 40:						   /* simple write */
			if (c1551.i.iec.handshakein == 0)
			{
				c1551.i.iec.state++;
				if ((c1551.state == 0) || (c1551.state == OPEN))
				{
					/*TODO*///DBG_LOG (1, "c1551", ("taken data %.2x\n",
					/*TODO*///					  c1551.i.iec.datain));
					/*TODO*///if (c1551.cmdpos < sizeof (c1551.cmdbuffer))
					/*TODO*///	c1551.cmdbuffer[c1551.cmdpos++] = c1551.i.iec.datain;
				}
				else if (c1551.state == WRITING)
				{
					/*TODO*///DBG_LOG (1, "c1551", ("written data %.2x\n", c1551.i.iec.datain));
				}
				c1551.i.iec.handshakeout = 1;
			}
			break;
		case 41:
			if (c1551.i.iec.datain == 0)
			{
				c1551.i.iec.state++;
			}
			break;
		case 42:
			if (c1551.i.iec.handshakein != 0)
			{
				c1551.i.iec.state = 10;
			}
			break;
		}
	/*TODO*///#if VERBOSE_DBG
	/*TODO*///	if (oldstate != c1551.i.iec.state)
	/*TODO*///		logerror ("state %d.%d %d\n", oldstate, c1551.i.iec.state, c1551.state);
	/*TODO*///#endif
	}
	
	public static void vc1541_state (CBM_Drive vc1541)
	{
	/*TODO*///#if VERBOSE_DBG
	/*TODO*///	int oldstate = vc1541.i.serial.state;
	/*TODO*///
	/*TODO*///#endif
	
		switch (vc1541.i.serial.state)
		{
		case 0:
			if (cbm_serial.atn[0]==0 && cbm_serial.clock[0]==0)
			{
				vc1541.i.serial.data = 0;
				vc1541.i.serial.state = 2;
				break;
			}
			if (cbm_serial.clock[0]==0 && vc1541.i.serial.forme!=0)
			{
				vc1541.i.serial.data = 0;
				vc1541.i.serial.state++;
				break;
			}
			break;
		case 1:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state++;
				break;
			}
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.broadcast = 0;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.state = 100;
				vc1541.i.serial.last = 0;
				vc1541.i.serial.value = 0;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 2:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.broadcast = 1;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.state = 100;
				vc1541.i.serial.last = 0;
				vc1541.i.serial.value = 0;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
			/* bits to byte fitting */
		case 100:
			if (cbm_serial.clock[0] == 0)
			{
				vc1541.i.serial.state++;
				break;
			}
			break;
		case 102:
		case 104:
		case 106:
		case 108:
		case 110:
		case 112:
		case 114:
			if (cbm_serial.clock[0] == 0)
				vc1541.i.serial.state++;
			break;
		case 101:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!=0 ? 1 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 103:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!=0 ? 2 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 105:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!=0 ? 4 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 107:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!= 0 ? 8 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 109:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!= 0 ? 0x10 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 111:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!= 0 ? 0x20 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 113:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!= 0 ? 0x40 : 0;
				vc1541.i.serial.state++;
			}
			break;
		case 115:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.value |= cbm_serial.data[0]!= 0 ? 0x80 : 0;
				if (vc1541.i.serial.broadcast != 0
					&& (((vc1541.i.serial.value & 0xf0) == 0x20)
						|| ((vc1541.i.serial.value & 0xf0) == 0x40)))
				{
					vc1541.i.serial.forme = (vc1541.i.serial.value & 0xf)
						== vc1541.i.serial.device ? 1 : 0;
					if (vc1541.i.serial.forme==0)
					{
						vc1541.i.serial.state = 160;
						break;
					}
				}
				if (vc1541.i.serial.forme != 0)
				{
					/*TODO*///if (vc1541.cmdpos < sizeof (vc1541.cmdbuffer))
					/*TODO*///	vc1541.cmdbuffer[vc1541.cmdpos++] = vc1541.i.serial.value;
					/*TODO*///DBG_LOG (1, "serial read", ("%s %s %.2x\n",
					/*TODO*///			vc1541.i.serial.broadcast ? "broad" : "",
					/*TODO*///			vc1541.i.serial.last ? "last" : "",
					/*TODO*///							vc1541.i.serial.value));
				}
				vc1541.i.serial.state++;
			}
			break;
		case 116:
			if (cbm_serial.clock[0]==0)
			{
				if (vc1541.i.serial.last != 0)
					vc1541.i.serial.state = 130;
				else
					vc1541.i.serial.state++;
				if (vc1541.i.serial.broadcast != 0 &&
					((vc1541.i.serial.value == 0x3f) || (vc1541.i.serial.value == 0x5f)
					 || ((vc1541.i.serial.value & 0xf0) == 0x60)))
				{
					cbm_command (vc1541);
				}
				vc1541.i.serial.time = timer_get_time ();
				vc1541.i.serial.data = 0;
				break;
			}
			break;
		case 117:
			if (vc1541.i.serial.forme != 0 && ((vc1541.i.serial.value & 0xf0) == 0x60)
				&& vc1541.i.serial.broadcast != 0 && cbm_serial.atn[0] != 0)
			{
				if (vc1541.state == READING)
				{
					vc1541.i.serial.state = 200;
					break;
				}
				else if (vc1541.state != WRITING)
				{
					vc1541.i.serial.state = 150;
					break;
				}
			}
			if (((vc1541.i.serial.value == 0x3f)
				 || (vc1541.i.serial.value == 0x5f))
				&& vc1541.i.serial.broadcast != 0 && cbm_serial.atn[0] != 0)
			{
				vc1541.i.serial.data = 1;
				vc1541.i.serial.state = 140;
				break;
			}
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.time = timer_get_time ();
				vc1541.i.serial.broadcast = cbm_serial.atn[0]!=0?0:1;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.value = 0;
				vc1541.i.serial.state++;
				break;
			}
			break;
			/* if computer lowers clk not in 200micros (last byte following)
			 * negativ pulse on data by listener */
		case 118:
			if (cbm_serial.clock[0]==0)
			{
				vc1541.i.serial.value = 0;
				vc1541.i.serial.state = 101;
				vc1541.i.serial.data = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time >= 200e-6)
			{
				vc1541.i.serial.data = 0;
				vc1541.i.serial.last = 1;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 119:
			if (timer_get_time () - vc1541.i.serial.time >= 60e-6)
			{
				vc1541.i.serial.value = 0;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.state = 100;
				break;
			}
			break;
	
		case 130:						   /* last byte of talk */
			if (timer_get_time () - vc1541.i.serial.time >= 60e-6)
			{
				vc1541.i.serial.data = 1;
				vc1541.i.serial.state = 0;
				break;
			}
			break;
	
		case 131:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.state = 0;
			}
			break;
	
		case 140:						   /* end of talk */
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.state = 0;
			}
			break;
	
		case 150:						   /* file not found */
			if (cbm_serial.atn[0] != 0)
			{
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 151:
			if (timer_get_time () - vc1541.i.serial.time > 1000e-6)
			{
				vc1541.i.serial.state++;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 152:
			if (timer_get_time () - vc1541.i.serial.time > 50e-6)
			{
				vc1541.i.serial.state++;
				vc1541.i.serial.clock = 1;
				break;
			}
			break;
		case 153:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.state = 0;
			}
			break;
	
		case 160:						   /* not for me */
			if (cbm_serial.atn[0] != 0)
			{
				vc1541.i.serial.state = 0;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
	
		case 200:
			if (cbm_serial.clock[0] != 0)
			{
				vc1541.i.serial.state++;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 201:
			if (timer_get_time () - vc1541.i.serial.time > 80e-6)
			{
				vc1541.i.serial.clock = 1;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.state = 300;
				break;
			}
			break;
	
		case 300:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (cbm_serial.data[0] != 0)
			{
				/*TODO*///vc1541.i.serial.value = vc1541.buffer[vc1541.pos];
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.data = (vc1541.i.serial.value & 1) != 0 ? 1 : 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 301:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 40e-6)
			{
				vc1541.i.serial.clock = 1;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 303:
		case 305:
		case 307:
		case 309:
		case 311:
		case 313:
		case 315:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.clock = 1;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 302:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 2) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 304:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 4) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 306:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 8) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 308:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 0x10) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 310:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 0x20) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 312:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 0x40) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 314:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				vc1541.i.serial.data = (vc1541.i.serial.value & 0x80) != 0 ? 1 : 0;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 316:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 20e-6)
			{
				/*TODO*///DBG_LOG (1, "vc1541", ("%.2x written\n", vc1541.i.serial.value));
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 0;
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 317:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (cbm_serial.data[0]==0)
			{
				vc1541.i.serial.state++;
				vc1541.i.serial.time = timer_get_time ();
				break;
			}
			break;
		case 318:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (vc1541.pos + 1 == vc1541.size)
			{
				vc1541.i.serial.clock = 1;
				vc1541.i.serial.state = 0;
				break;
			}
			if (vc1541.pos + 2 == vc1541.size)
			{
				vc1541.pos++;
				vc1541.i.serial.state = 320;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 100e-6)
			{
				vc1541.pos++;
				vc1541.i.serial.clock = 1;
				vc1541.i.serial.state = 300;
				break;
			}
			break;
		case 320:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (timer_get_time () - vc1541.i.serial.time > 100e-6)
			{
				vc1541.i.serial.clock = 1;
				vc1541.i.serial.state++;
				break;
			}
			break;
		case 321:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (cbm_serial.data[0] != 0)
			{
				vc1541.i.serial.state++;
			}
			break;
		case 322:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (cbm_serial.data[0]==0)
			{
				vc1541.i.serial.state++;
			}
			break;
		case 323:
			if (cbm_serial.atn[0]==0)
			{
				vc1541.i.serial.state = 330;
				vc1541.i.serial.data = 1;
				vc1541.i.serial.clock = 1;
				break;
			}
			if (cbm_serial.data[0] != 0)
			{
				vc1541.i.serial.state = 300;
			}
			break;
		case 330:						   /* computer breaks receiving */
			vc1541.i.serial.state = 0;
			break;
		}
	/*TODO*///#if VERBOSE_DBG
	/*TODO*///	if (oldstate != vc1541.i.serial.state)
	/*TODO*///		logerror ("%d state %d.%d %d %s %s %s\n",
	/*TODO*///				 vc1541.i.serial.device,
	/*TODO*///				 oldstate,
	/*TODO*///				 vc1541.i.serial.state, vc1541.state,
	/*TODO*///				 cbm_serial.atn[0] ? "ATN" : "atn",
	/*TODO*///				 cbm_serial.clock[0] ? "CLOCK" : "clock",
	/*TODO*///				 cbm_serial.data[0] ? "DATA" : "data");
	/*TODO*///#endif
	}
	
	/* difference between vic20 and pet (first series)
	   pet lowers atn and wants a reaction on ndac */
	
	public static void c2031_state(CBM_Drive drive)
	{
	/*TODO*///#if VERBOSE_DBG
	/*TODO*///	int oldstate = drive.i.ieee.state;
	/*TODO*///#endif
		int data;
	
		switch (drive.i.ieee.state)
		{
		case 0:
			/*TODO*///if (cbm_ieee_dav_r()==0) {
			/*TODO*///	drive.i.ieee.state=10;
			/*TODO*///} else if (cbm_ieee_atn_r()==0) {
			/*TODO*///	drive.i.ieee.state=11;
			/*TODO*///	cbm_ieee_ndac_w(1,0);
			/*TODO*///	logerror("arsch\n");
			/*TODO*///}
			break;
		case 1:
			break;
		case 10:
			/*TODO*///if (cbm_ieee_dav_r()!=0) {
			/*TODO*///	drive.i.ieee.state++;
			/*TODO*///	cbm_ieee_nrfd_w(1,1);
			/*TODO*///	cbm_ieee_ndac_w(1,0);
			/*TODO*///}
			break;
		case 11:
			/*TODO*///if (cbm_ieee_dav_r()==0) {
			/*TODO*///	cbm_ieee_nrfd_w(1,0);
			/*TODO*///	data=cbm_ieee_data_r()^0xff;
			/*TODO*///	cbm_ieee_ndac_w(1,1);
			/*TODO*///	logerror("byte received %.2x\n",data);
			/*TODO*///	if (cbm_ieee_atn_r()==0&&((data&0x0f)==drive.i.ieee.device) ) {
			/*TODO*///		if ((data&0xf0)==0x40)
			/*TODO*///			drive.i.ieee.state=30;
			/*TODO*///		else
			/*TODO*///			drive.i.ieee.state=20;
			/*TODO*///		if (drive.cmdpos < sizeof (drive.cmdbuffer))
			/*TODO*///			drive.cmdbuffer[drive.cmdpos++] = data;
			/*TODO*///	} else if ((data&0xf)==0xf) {
			/*TODO*///		drive.i.ieee.state--;
			/*TODO*///	} else {
			/*TODO*///		drive.i.ieee.state++;
			/*TODO*///	}
			/*TODO*///}
			break;
			/* wait until atn is released */
		case 12:
			/*TODO*///if (cbm_ieee_atn_r()!=0) {
			/*TODO*///	drive.i.ieee.state++;
			/*TODO*///	cbm_ieee_nrfd_w(1,0);
			/*TODO*///}
			break;
		case 13:
			/*TODO*///if (cbm_ieee_atn_r()==0) {
			/*TODO*///	drive.i.ieee.state=10;
	/*			cbm_ieee_nrfd_w(1,0); */
			/*TODO*///}
			break;
	
			/* receiving rest of command */
		case 20:
			/*TODO*///if (cbm_ieee_dav_r()!=0) {
			/*TODO*///	drive.i.ieee.state++;
			/*TODO*///	cbm_ieee_nrfd_w(1,1);
			/*TODO*///	cbm_ieee_ndac_w(1,0);
			/*TODO*///}
			break;
		case 21:
			/*TODO*///if (cbm_ieee_dav_r()==0) {
			/*TODO*///	cbm_ieee_nrfd_w(1,0);
			/*TODO*///	data=cbm_ieee_data_r()^0xff;
			/*TODO*///	logerror("byte received %.2x\n",data);
			/*TODO*///	if (drive.cmdpos < sizeof (drive.cmdbuffer))
			/*TODO*///		drive.cmdbuffer[drive.cmdpos++] = data;
			/*TODO*///	if (cbm_ieee_atn_r()==0&&((data&0xf)==0xf)) {
			/*TODO*///		cbm_command(drive);
			/*TODO*///		drive.i.ieee.state=10;
			/*TODO*///	} else
			/*TODO*///		drive.i.ieee.state=20;
			/*TODO*///	cbm_ieee_ndac_w(1,1);
			/*TODO*///}
			break;
	
			/* read command */
		case 30:
			/*TODO*///if (cbm_ieee_dav_r()!=0) {
			/*TODO*///	drive.i.ieee.state++;
			/*TODO*///	cbm_ieee_nrfd_w(1,1);
			/*TODO*///	cbm_ieee_ndac_w(1,0);
			/*TODO*///}
			break;
		case 31:
			/*TODO*///if (cbm_ieee_dav_r()==0) {
			/*TODO*///	cbm_ieee_nrfd_w(1,0);
			/*TODO*///	data=cbm_ieee_data_r()^0xff;
			/*TODO*///	logerror("byte received %.2x\n",data);
			/*TODO*///	if (drive.cmdpos < sizeof (drive.cmdbuffer))
			/*TODO*///		drive.cmdbuffer[drive.cmdpos++] = data;
			/*TODO*///	cbm_command(drive);
			/*TODO*///	if (drive.state==READING)
			/*TODO*///		drive.i.ieee.state++;
			/*TODO*///	else
			/*TODO*///		drive.i.ieee.state=10;
			/*TODO*///	cbm_ieee_ndac_w(1,1);
			/*TODO*///}
			break;
		case 32:
			/*TODO*///if (cbm_ieee_dav_r()!=0) {
			/*TODO*///	cbm_ieee_nrfd_w(1,1);
			/*TODO*///	drive.i.ieee.state=40;
			/*TODO*///}
			break;
		case 40:
			/*TODO*///if (cbm_ieee_ndac_r()==0) {
			/*TODO*///	cbm_ieee_data_w(1,drive.buffer[drive.pos++]^0xff);
			/*TODO*///	if (drive.pos>=drive.size)
			/*TODO*///		cbm_ieee_eoi_w(1,0);
			/*TODO*///	cbm_ieee_dav_w(1,0);
			/*TODO*///	drive.i.ieee.state++;
			/*TODO*///}
			break;
		case 41:
			/*TODO*///if (cbm_ieee_nrfd_r()==0) {
			/*TODO*///	drive.i.ieee.state++;
			/*TODO*///}
			break;
		case 42:
			/*TODO*///if (cbm_ieee_ndac_r()!=0) {
			/*TODO*///	if (cbm_ieee_eoi_r())
			/*TODO*///		drive.i.ieee.state=40;
			/*TODO*///	else {
			/*TODO*///		cbm_ieee_data_w(1,0xff);
			/*TODO*///		cbm_ieee_ndac_w(1,0);
			/*TODO*///		cbm_ieee_nrfd_w(1,0);
			/*TODO*///		cbm_ieee_eoi_w(1,1);
			/*TODO*///		drive.i.ieee.state=10;
			/*TODO*///	}
			/*TODO*///	cbm_ieee_dav_w(1,1);
			/*TODO*///}
			break;
		}
	
	/*TODO*///#if VERBOSE_DBG
	/*TODO*///	if (oldstate != drive.i.ieee.state)
	/*TODO*///		logerror("%d state %d.%d %d\n",
	/*TODO*///				 drive.i.ieee.device,
	/*TODO*///				 oldstate,
	/*TODO*///				 drive.i.ieee.state, drive.state
	/*TODO*///				 );
	/*TODO*///#endif
	}
}
