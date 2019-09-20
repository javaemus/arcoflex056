/***************************************************************************

  dsk.c

 CPCEMU standard and extended disk image support.
 Used on Amstrad CPC and Spectrum +3 drivers.

 KT - 27/2/00 - Moved Disk Image handling code into this file
							- Fixed a few bugs
							- Cleaned code up a bit
***************************************************************************/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.machine;

import static arcadeflex056.fileio.*;
import static common.ptr.*;
import static common.libc.cstdio.*;
import static common.libc.cstring.*;
import static consoleflex056.funcPtr.*;
import static mame056.osdependH.*;
import static mess056.deviceH.*;
import static mess056.includes.flopdrvH.*;
import static mess056.machine.flopdrv.*;
import static mess056.mess.*;
import static mess056.messH.*;

public class dsk
{
	/* disk image and extended disk image support code */
	/* supports up to 84 tracks and 2 sides */
	
	public static int dsk_MAX_TRACKS        = 84;
	public static int dsk_MAX_SIDES         = 2;
	public static int dsk_NUM_DRIVES        = 4;
	public static int dsk_SECTORS_PER_TRACK = 20;
	
	public static class dsk_drive
	{
		public char[] data = null; /* the whole image data */
		public int[] track_offsets = new int[dsk_MAX_TRACKS*dsk_MAX_SIDES]; /* offset within data for each track */
		public int[] sector_offsets = new int[dsk_SECTORS_PER_TRACK]; /* offset within current track for sector data */
		public int current_track;		/* current track */
		public int disk_image_type;  /* image type: standard or extended */
	};
	
	public static floppy_interface dsk_floppy_interface= new floppy_interface() {
            @Override
            public void seek_callback(int drive, int physical_track) {
                dsk_seek_callback(drive, physical_track);
            }

            @Override
            public int get_sectors_per_track(int drive, int physical_side) {
                return dsk_get_sectors_per_track(drive, physical_side);
            }

            @Override
            public void get_id_callback(int drive, chrn_id id_chrn, int id_index, int physical_side) {
                dsk_get_id_callback(drive, id_chrn, id_index, physical_side);
            }

            @Override
            public void read_sector_data_into_buffer(int drive, int side, int data_id, char[] buf, int length) {
                dsk_read_sector_data_into_buffer(drive, side, data_id, new UBytePtr(buf), length);
            }

            @Override
            public void write_sector_data_from_buffer(int drive, int side, int data_id, char[] buf, int length, int ddam) {
                dsk_write_sector_data_from_buffer(drive, side, data_id, buf, length, ddam);
            }

            @Override
            public void read_track_data_info_buffer(int drive, int side, char[] ptr, int length) {
                // Nothing to do
            }

            @Override
            public void format_sector(int drive, int side, int sector_index, int c, int h, int r, int n, int filler) {
                // Nothing to do
            }
        };
               
        /*
	{
		dsk_seek_callback,
		dsk_get_sectors_per_track,
		dsk_get_id_callback,
		dsk_read_sector_data_into_buffer,
		dsk_write_sector_data_from_buffer,
		null,
		null
	};
        */
	
	public static dsk_drive[] drives = new dsk_drive[dsk_NUM_DRIVES]; /* the drives */
        static{
            for (int i = 0 ; i<dsk_NUM_DRIVES ; i++)
                drives[i] = new dsk_drive();
        }
	
	/* load image */
	public static int dsk_load(int type, int id)
	{
            //System.out.println("dsk_load!!!! "+id);
		Object file;
	
		file = image_fopen(type, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_READ);
	
		if (file != null)
		{
                    //System.out.println("no es nulo");
			int datasize;
			//UBytePtr data = new UBytePtr();
	
			/* get file size */
			datasize = osd_fsize(file);
                        
                        //System.out.println(datasize);
	
			if (datasize!=0)
			{
				/* malloc memory for this data */
				ptr2 = new UBytePtr(datasize);
	
				if (ptr2!=null)
				{
					/* read whole file */
					osd_fread(file, ptr2, datasize);
	
					//ptr = new UBytePtr(data);
                                        //ptr = data;
                                        /*System.out.println("Leido");
                                        System.out.println(data.memory);
                                        System.out.println("Leido 2");
                                        System.out.println(ptr.memory);*/
                                        
                                        //System.out.println(ptr.memory);
	
					/* close file */
					osd_fclose(file);
	
					/* ok! */
					return 1;
				}
				osd_fclose(file);
	
			}
		}
	
		return 0;
	}
	
	public static int dsk_floppy_verify(char[] diskimage_data)
	{
		if ( (memcmp(diskimage_data, "MV - CPC".toCharArray(), 8)==0) || 	/* standard disk image? */
			 (memcmp(diskimage_data, "EXTENDED".toCharArray(), 8)==0))	/* extended disk image? */
		{
			return IMAGE_VERIFY_PASS;
		}
		return IMAGE_VERIFY_FAIL;
	}
	
	static UBytePtr ptr2 = null;
        
	/* load floppy */
	public static io_initPtr dsk_floppy_load = new io_initPtr() {
            public int handler(int id) {
                dsk_drive thedrive = drives[id];
                
	
		/* load disk image */
		if (dsk_load(IO_FLOPPY,id) != 0)
		{
                    //System.out.println("Paso2");
                    //System.out.println(ptr);
                    //System.out.println(ptr.memory);
			if (ptr2 != null)
			{
                            //System.out.println("Paso3");
                            drives[id].data = new UBytePtr(ptr2).memory;
				dsk_disk_image_init(thedrive); /* initialise dsk */
                                drives[id] = thedrive;
                                //System.out.println("Paso4");
                                floppy_drive_set_disk_image_interface(id,dsk_floppy_interface);
                                //System.out.println("Paso5");
                                if(dsk_floppy_verify(drives[id].data) == IMAGE_VERIFY_PASS){
                                    //System.out.println("Paso6");
                                    drives[id] = thedrive;
                                    return INIT_PASS;
                                }else{
                                    drives[id] = thedrive;
                                    //System.out.println("Paso7");
                                    return INIT_PASS;
                                }
			}
		}
                
                drives[id] = thedrive;
	
		return INIT_PASS;
            }
        };
	
	public static int dsk_save(int type, int id, char[] ptr)
	{
		Object file;
	
		file = image_fopen(type, id, OSD_FILETYPE_IMAGE_R, OSD_FOPEN_RW);
	
		if (file != null)
		{
			int datasize;
			UBytePtr data = null;
	
			/* get file size */
			datasize = osd_fsize(file);
	
			if (datasize!=0)
			{
				data = new UBytePtr(ptr);
				if (data!=null)
				{
					osd_fwrite(file, data, datasize);
	
					/* close file */
					osd_fclose(file);
	
					/* ok! */
					return 1;
				}
				osd_fclose(file);
	
			}
		};
	
		return 0;
	}
	
	
	public static io_exitPtr dsk_floppy_exit = new io_exitPtr() {
            public int handler(int id) {
                //dsk_drive *thedrive = &drives[id];
	
		if (drives[id].data!=null)
		{
			dsk_save(IO_FLOPPY,id,drives[id].data);
			drives[id].data = null;
		}
		drives[id].data = null;
                
                return 1;
            }
        };
		
	
	public static void dsk_dsk_init_track_offsets(dsk_drive thedrive)
	{
		int track_offset;
		int i;
		int track_size;
		int tracks, sides;
		int skip, length,offs;
		UBytePtr file_loaded = new UBytePtr(thedrive.data);
	
	
		/* get size of each track from main header. Size of each
		track includes a 0x0100 byte header, and the actual sector data for
		all sectors on the track */
		track_size = file_loaded.read(0x032) | (file_loaded.read(0x033)<<8);
	
		/* main header is 0x0100 in size */
		track_offset = 0x0100;
	
		sides = file_loaded.read(0x031);
		tracks = file_loaded.read(0x030);
                
                System.out.println("Sides: "+sides);
		System.out.println("Tracks: "+tracks);
	
	
		/* single sided? */
		if (sides==1)
		{
			skip = 2;
			length = tracks;
		}
		else
		{
			skip = 1;
			length = tracks*sides;
		}
	
		offs = 0;
		for (i=0; i<length; i++)
		{
			thedrive.track_offsets[offs] = track_offset;
			track_offset+=track_size;
			offs+=skip;
		}
	
	}
	
	public static void dsk_dsk_init_sector_offsets(dsk_drive thedrive,int track,int side)
	{
		int track_offset;
	
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = (int) thedrive.track_offsets[(track<<1) + side];
                
                
	
		if (track_offset!=0)
		{
			int spt;
			int sector_offset;
			int sector_size;
			int i;
	
			UBytePtr track_header = new UBytePtr();
	
			track_header= new UBytePtr(thedrive.data, track_offset);
	
			/* sectors per track as specified in nec765 format command */
			/* sectors on this track */
			spt = track_header.read(0x015);
	
			sector_size = (1<<(track_header.read(0x014)+7));
	
			/* track header is 0x0100 bytes in size */
			sector_offset = 0x0100;
	
			for (i=0; i<spt; i++)
			{
				thedrive.sector_offsets[i] = sector_offset;
				sector_offset+=sector_size;
			}
		}
	}
	
	public static void dsk_extended_dsk_init_track_offsets(dsk_drive thedrive)
	{
		int track_offset;
		int i;
		int track_size;
		int tracks, sides;
		int offs, skip, length;
		UBytePtr file_loaded = new UBytePtr(thedrive.data, 0);
	
		sides = file_loaded.read(0x031);
                System.out.println("Sides: "+sides);
		tracks = file_loaded.read(0x030);
                System.out.println("Tracks: "+tracks);
	
		if (sides==1)
		{
			skip = 2;
			length = tracks;
		}
		else
		{
			skip = 1;
			length = tracks*sides;
		}
	
		/* main header is 0x0100 in size */
		track_offset = 0x0100;
		offs = 0;
		for (i=0; i<length; i++)
		{
			int track_size_high_byte;
	
			/* track size is specified as a byte, and is multiplied
			by 256 to get size in bytes. If 0, track doesn't exist and
			is unformatted, otherwise it exists. Track size includes 0x0100
			header */
			track_size_high_byte = file_loaded.read(0x034 + i);
	
			if (track_size_high_byte != 0)
			{
				/* formatted track */
				track_size = track_size_high_byte<<8;
	
				thedrive.track_offsets[offs] = track_offset;
                                System.out.println("track_offsets["+offs+"]="+track_offset);
				track_offset+=track_size;
			}
	
			offs+=skip;
		}
	}
	
	
	public static void dsk_extended_dsk_init_sector_offsets(dsk_drive thedrive,int track,int side)
	{
		int track_offset;
	
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = (int) thedrive.track_offsets[(track<<1) + side];
                System.out.println("Trrack_offset en init: "+track_offset);
	
		if (track_offset!=0)
		{
			int spt;
			int sector_offset;
			int sector_size;
			int i;
			UBytePtr id_info = new UBytePtr();
			UBytePtr track_header = new UBytePtr();
                        
                        //thedrive.data.offset = 0;
                        
                        //System.out.println("track_offset!!!!="+track_offset);
	
			track_header= new UBytePtr(thedrive.data, track_offset);
                        
                        //thedrive.data.offset = track_offset + 0x018;
	
			/* sectors per track as specified in nec765 format command */
			/* sectors on this track */
			spt = track_header.read(0x015);
	
			id_info = new UBytePtr(track_header, 0x018);
	
			/* track header is 0x0100 bytes in size */
			sector_offset = 0x0100;
	
			for (i=0; i<spt; i++)
			{
	                        sector_size = id_info.read((i<<3) + 6) + (id_info.read((i<<3) + 7)<<8);
	
				thedrive.sector_offsets[i] = sector_offset;
				sector_offset+=sector_size;
			}
                        
                        //thedrive.data.offset = track_offset + 0x018;
                        //curr_track_offset = thedrive.data.offset;
		}
	}
	
	
	
	public static void dsk_disk_image_init(dsk_drive thedrive)
	{
            //System.out.println(thedrive.data.memory);
		/*-----------------27/02/00 11:26-------------------
		 clear offsets
		--------------------------------------------------*/
		memset(thedrive.track_offsets, 0, dsk_MAX_TRACKS*dsk_MAX_SIDES);
		memset(thedrive.sector_offsets, 0, 20);
                
                //System.out.println(thedrive.data);
                //System.out.println(thedrive.data.memory);
	
		if (memcmp(thedrive.data,"MV - CPC".toCharArray(),8)==0)
		{
                    System.out.println("MV - CPC");
			thedrive.disk_image_type = 0;
	
			/* standard disk image */
			dsk_dsk_init_track_offsets(thedrive);
	
		}
		else
		if (memcmp(thedrive.data,"EXTENDED".toCharArray(),8)==0)
		{
                    System.out.println("EXTENDED");
			thedrive.disk_image_type = 0;
	
			/* extended disk image */
			dsk_extended_dsk_init_track_offsets(thedrive);
                        
		}
	}
	
	
	public static void dsk_seek_callback(int drive, int physical_track)
	{
		drive = drive & 0x03;
		drives[drive].current_track = physical_track;
	}
	
	static int get_track_offset(int drive, int side)
	{
		dsk_drive thedrive;
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		thedrive = drives[drive];
                
                //System.out.println("Current Track A "+thedrive.current_track);
                //System.out.println("Current Track B "+(thedrive.current_track<<1));
                //System.out.println("Traxk Offset "+thedrive.track_offsets[(thedrive.current_track<<1) + side]);
	
		return (int) thedrive.track_offsets[(thedrive.current_track<<1) + side];
	}
	
	/*static UBytePtr get_floppy_data(int drive, int offset)
	{
		drive = drive & 0x03;
                drives[drive].data.offset = offset;
		return new UBytePtr(drives[drive].data);
	}*/
        
        //static int curr_track_offset = 0;
	
	public static void dsk_get_id_callback(int drive, chrn_id id, int id_index, int side)
	{
		int id_offset;
		int track_offset;
		UBytePtr track_header=new UBytePtr();
		UBytePtr data=new UBytePtr();
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = get_track_offset(drive, side);
                //System.out.println("track_offset="+track_offset);
                
                //curr_track_offset = track_offset;
	
		/* track exists? */
		if (track_offset==0)
			return;
	
		/* yes */
		data = new UBytePtr(drives[drive].data);
	
		if (data==null)
			return;
	
		track_header = new UBytePtr(data, track_offset);
	
		id_offset = 0x018 + (id_index<<3);
	
		id.C = track_header.read(id_offset + 0);
		id.H = track_header.read(id_offset + 1);
		id.R = track_header.read(id_offset + 2);
		id.N = track_header.read(id_offset + 3);
                //System.out.println("id.C: "+id.C);
                //System.out.println("id.H: "+id.H);
                //System.out.println("id.R: "+id.R);
                //System.out.println("id.N: "+id.N);
		id.flags = 0;
		id.data_id = id_index;
                //System.out.println("id.flags: "+id.flags);
                //System.out.println("id.data_id: "+id.data_id);
	
		if ((track_header.read(id_offset + 5) & 0x040) != 0)
		{
			id.flags |= ID_FLAG_DELETED_DATA;
		}
	
	
	
	
	//	id.ST0 = track_header[id_offset + 4];
	//	id.ST1 = track_header[id_offset + 5];
	
	}
	
	
	static void dsk_set_ddam(int drive, int id_index, int side, int ddam)
	{
            System.out.println("dsk_set_ddam");
		int id_offset;
		int track_offset;
		UBytePtr track_header = new UBytePtr();
		UBytePtr data = new UBytePtr();
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = get_track_offset(drive, side);
	
		/* track exists? */
		if (track_offset==0)
			return;
	
		/* yes */
		data = new UBytePtr(drives[drive].data);
	
		if (data==null)
			return;
	
		track_header = new UBytePtr(data, track_offset);
	
		id_offset = 0x018 + (id_index<<3);
	
		track_header.write(id_offset + 5, track_header.read(id_offset + 5) & ~0x040);
                //kk
	
		if (ddam != 0)
		{
			track_header.write(id_offset + 5, track_header.read(id_offset + 5) | 0x040);
		}
	}
	
	public static void printSector(UBytePtr bSector){
            int longo = bSector.memory.length - bSector.offset;
            int iniPos = bSector.offset;
            
            System.out.println("----printSectorA----");
            
            for (int _i = iniPos; _i<256 ; _i++)
                System.out.print(bSector.read(_i));
        }
        
        public static void printSector(char[] bSector){
            //int longo = bSector.length - bSector.offset;
            int iniPos = 0;
            
            System.out.println("----printSector----");
            
            for (int _i = iniPos; _i<256 ; _i++){
                System.out.print(bSector[_i]);
            }
            
            System.out.println("----end printSector----");
        }
        
	public static UBytePtr dsk_get_sector_ptr_callback(int drive, int sector_index, int side)
	{
		int track_offset;
		int sector_offset;
		int track;
		//dsk_drive thedrive;
		UBytePtr data = new UBytePtr();
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		//thedrive = &drives[drive];
                
                //HACK
                //drives[drive].current_track = 1;
	
		track = drives[drive].current_track;
	
		/* offset to track header in image */
		track_offset = get_track_offset(drive, side);
                
                System.out.println("Track: "+track+" track_offset: "+track_offset);
	
		/* track exists? */
		if (track_offset==0)
			return null;
	
	
		/* setup sector offsets */
		switch (drives[drive].disk_image_type)
		{
		case 0:
			dsk_dsk_init_sector_offsets(drives[drive],track, side);
			break;
	
	
		case 1:
			dsk_extended_dsk_init_sector_offsets(drives[drive], track, side);
			break;
	
		default:
			break;
		}
	
		sector_offset = (int) drives[drive].sector_offsets[sector_index];
	
		data = new UBytePtr(drives[drive].data);
	
		if (data==null)
			return null;
                //System.out.println("track_offset="+track_offset+", sector_offset="+sector_offset);
                
                //curr_track_offset = track_offset + sector_offset;
                	
		return new UBytePtr(data, track_offset + sector_offset);
	}
	
	public static void dsk_write_sector_data_from_buffer(int drive, int side, int index1, char[] ptr, int length, int ddam)
	{
		UBytePtr pSectorData = new UBytePtr();
	
		pSectorData = new UBytePtr(dsk_get_sector_ptr_callback(drive, index1, side));
	
		if (pSectorData!=null)
		{
			memcpy(pSectorData.memory, ptr, length);
                        pSectorData.offset = 0;
		}
	
		/* set ddam */
		dsk_set_ddam(drive, index1, side,ddam);
	}
	
	public static void dsk_read_sector_data_into_buffer(int drive, int side, int index1, UBytePtr ptr, int length)
	{
		UBytePtr pSectorData;
                
                //System.out.println("dsk_read_sector_data_into_buffer!!!!"+curr_track_offset);
                //System.out.println("dsk_read_sector_data_into_buffer!!!!");
	
		pSectorData = new UBytePtr(dsk_get_sector_ptr_callback(drive, index1, side));
	
		if (pSectorData!=null)
		{
			//memcpy(ptr, 0, pSectorData.memory, curr_track_offset, length);
                        memcpy(ptr, pSectorData, length);
                        //System.out.println("not null "+curr_track_offset);
                        //System.out.println("not null ");
                        //System.out.println(pSectorData.memory);
                        //printSector(ptr);
                        ptr.offset = 0;
	
		} else {
                    System.out.println("es null");
                }                
                
	}
	
	public static int dsk_get_sectors_per_track(int drive, int side)
	{
		int track_offset;
		UBytePtr track_header = new UBytePtr();
		UBytePtr data = new UBytePtr();
	
		drive = drive & 0x03;
		side = side & 0x01;
	
		/* get offset to track header in image */
		track_offset = get_track_offset(drive, side);
	
		/* track exists? */
		if (track_offset==0)
			return 0;
	
		data = new UBytePtr(drives[drive].data);
	
		if (data==null)
			return 0;
	
		/* yes, get sectors per track */
		track_header = new UBytePtr(data, track_offset);
                
                //System.out.println("track_offset: "+track_offset);
                //System.out.println("Sectores por pista: "+(int)(track_header.read(0x015)));
	
		return track_header.read(0x015);
	}
	
}
