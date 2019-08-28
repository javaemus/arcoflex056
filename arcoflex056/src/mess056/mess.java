package mess056;
/*
This file is a set of function calls and defs required for MESS.
*/

/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 

import static mame056.version.build_version;
import static mess056.deviceH.*;
import static mess056.device.devices;
import static mess056.messH.*;
import static mess056.machine.flopdrv.*;
import static mess056.includes.flopdrvH.*;

import static mame056.driverH.*;

import static arcadeflex056.osdepend.logerror;
import static arcadeflex056.fileio.*;
import static common.libc.cstring.*;
import static common.libc.cstdio.*;
import static mame056.mame.*;

public class mess
{
	
/*TODO*///	extern struct GameOptions options;
/*TODO*///	extern const struct Devices devices[];
/*TODO*///	
/*TODO*///	/* CRC database file for this driver, supplied by the OS specific code */
/*TODO*///	extern const char *crcfile;
/*TODO*///	extern const char *pcrcfile;
/*TODO*///	
	
	/* Globals */
	static int mess_keep_going;
	static String renamed_image;
	
	public static class image_info {
		String name;
		int crc;
		int length;
		String longname;
		String manufacturer;
		String year;
		String playable;
		String extrainfo;
	};
	
	public static int MAX_INSTANCES = 5;
	public static image_info[][] images = new image_info[IO_COUNT][MAX_INSTANCES];
        public static int[] count = new int[IO_COUNT];
        
        static {
          for (int i=0 ; i<IO_COUNT ; i++)
              for (int j=0 ; j<MAX_INSTANCES ; j++)
                  images[i][j]=new image_info();
        };
	

		public static String dupe(String src)
		{
			/*TODO*///	if (src != 0)
			/*TODO*///	{
			/*TODO*///		char *dst = malloc(strlen(src) + 1);
			/*TODO*///		if (dst != 0)
			/*TODO*///			strcpy(dst,src);
			/*TODO*///		return dst;
			/*TODO*///	}
			/*TODO*///	return NULL;
		    return src;
		}


/*TODO*///	static char* stripspace(const char *src)
/*TODO*///	{
/*TODO*///		static char buff[512];
/*TODO*///		if( src )
/*TODO*///		{
/*TODO*///			char *dst;
/*TODO*///			while( *src && isspace(*src) )
/*TODO*///				src++;
/*TODO*///			strcpy(buff, src);
/*TODO*///			dst = buff + strlen(buff);
/*TODO*///			while( dst >= buff && isspace(*--dst) )
/*TODO*///				*dst = '\0';
/*TODO*///			return buff;
/*TODO*///		}
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///
/*TODO*///	int DECL_SPEC mess_printf(char *fmt, ...)	
	public static int mess_printf(String fmt, Object... arguments)
	{
		System.out.printf(fmt, arguments);
/*TODO*///		va_list arg;
		int length = 0;
/*TODO*///	
/*TODO*///		va_start(arg,fmt);
/*TODO*///	
/*TODO*///		if (options.mess_printf_output)
/*TODO*///			length = options.mess_printf_output(fmt, arg);
/*TODO*///		else if (!options.gui_host)
/*TODO*///			length = vprintf(fmt, arg);
/*TODO*///	
/*TODO*///		va_end(arg);
/*TODO*///	
		return length;
	}
	
/*TODO*///	static int read_crc_config (const char *file, struct image_info *img, const char* sysname)
/*TODO*///	{
/*TODO*///		int retval;
/*TODO*///		void *config = config_open (file);
/*TODO*///	
/*TODO*///		retval = 1;
/*TODO*///		if( config )
/*TODO*///		{
/*TODO*///			char line[1024];
/*TODO*///			char crc[9+1];
/*TODO*///	
/*TODO*///			sprintf(crc, "%08x", img->crc);
/*TODO*///			config_load_string(config,sysname,0,crc,line,sizeof(line));
/*TODO*///			if( line[0] )
/*TODO*///			{
/*TODO*///				logerror("found CRC %s= %s\n", crc, line);
/*TODO*///				img->longname = dupe(stripspace(strtok(line, "|")));
/*TODO*///				img->manufacturer = dupe(stripspace(strtok(NULL, "|")));
/*TODO*///				img->year = dupe(stripspace(strtok(NULL, "|")));
/*TODO*///				img->playable = dupe(stripspace(strtok(NULL, "|")));
/*TODO*///				img->extrainfo = dupe(stripspace(strtok(NULL, "|")));
/*TODO*///				retval = 0;
/*TODO*///			}
/*TODO*///			config_close(config);
/*TODO*///		}
/*TODO*///		return retval;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
	public static Object image_fopen(int type, int id, int filetype, int read_or_write)
    {
        //System.out.println("image_fopen");
        image_info img = images[type][id];
	    String sysname;
	    Object file;
	    int extnum;
	
	    if (type >= IO_COUNT) {
	        logerror("image_fopen: type out of range (%d)\n", type);
	        return null;
	    }
	
	    if (id >= count[type]) {
	        logerror("image_fopen: id out of range (%d)\n", id);
	        return null;
	    }
	
	    if (img.name == null) {
	        return null;
	    }
	
	    /* try the supported extensions */
	    extnum = 0;
	    for (;;) {
	        String ext;
	        String p;
	
	        sysname = Machine.gamedrv.name;
	        logerror("image_fopen: trying %s for system %s\n", img.name, sysname);
	
	        //System.out.println("image_fopen: trying %s for system %s\n");
	        //System.out.println(img.name);
	        //System.out.println(sysname);
	
	        file = osd_fopen(sysname, img.name, filetype, read_or_write);
	        /* file found, break out */
	        System.out.println("file==null " + file);
	        if (file != null) {
	            break;
	        }
	        /*TODO*///		if( Machine->gamedrv->clone_of &&
	/*TODO*///			Machine->gamedrv->clone_of != &driver_0 )
	/*TODO*///		{
	/*TODO*///			sysname = Machine->gamedrv->clone_of->name;
	/*TODO*///			logerror("image_fopen: now trying %s for system %s\n", img->name, sysname);
	/*TODO*///			file = osd_fopen(sysname, img->name, filetype, read_or_write);
	/*TODO*///		}
	/*TODO*///		if( file )
	/*TODO*///			break;
	/*TODO*///
	        ext = device_file_extension(type, extnum);
	        //System.out.println(ext);
	        extnum++;
	
	        /* no (more) extensions, break out */
	        if (ext == null) {
	            break;
	        }
	        p = strrchr(img.name, '.');
	        //System.out.println("p: " + p);
	        //System.out.println("img.name: " + img.name);
	        //System.out.println("ext: " + ext);
	
	        /* does the current name already have an extension? */
	        if (p == null) {
	            throw new UnsupportedOperationException("unimplemented");
	            /*TODO*///			++p; /* skip the dot */
	/*TODO*///			/* new extension won't fit? */
	/*TODO*///			if( strlen(p) < strlen(ext) )
	/*TODO*///			{
	/*TODO*///				img->name = realloc(img->name, l - strlen(p) + strlen(ext) + 1);
	/*TODO*///				if( !img->name )
	/*TODO*///				{
	/*TODO*///					logerror("image_fopen: realloc failed.. damn it!\n");
	/*TODO*///					return NULL;
	/*TODO*///				}
	/*TODO*///			}
	/*TODO*///			strcpy(p, ext);
	        } else {
	            img.name += sprintf(".%s", ext);
	        }
	    }
	    /*TODO*///
	/*TODO*///	if( file )
	/*TODO*///	{
	/*TODO*///		void *config;
	/*TODO*///		const struct IODevice *pc_dev = Machine->gamedrv->dev;
	/*TODO*///
	/*TODO*///		logerror("image_fopen: found image %s for system %s\n", img->name, sysname);
	/*TODO*///		img->length = osd_fsize(file);
	/*TODO*////* Cowering, partial crcs for NES/A7800/others */
	/*TODO*///		img->crc = 0;
	/*TODO*///		while( pc_dev && pc_dev->count && !img->crc)
	/*TODO*///		{
	/*TODO*///			logerror("partialcrc() -> %08lx\n",pc_dev->partialcrc);
	/*TODO*///			if( type == pc_dev->type && pc_dev->partialcrc )
	/*TODO*///			{
	/*TODO*///				unsigned char *pc_buf = (unsigned char *)malloc(img->length);
	/*TODO*///				if( pc_buf )
	/*TODO*///				{
	/*TODO*///					osd_fseek(file,0,SEEK_SET);
	/*TODO*///					osd_fread(file,pc_buf,img->length);
	/*TODO*///					osd_fseek(file,0,SEEK_SET);
	/*TODO*///					logerror("Calling partialcrc()\n");
	/*TODO*///					img->crc = (*pc_dev->partialcrc)(pc_buf,img->length);
	/*TODO*///					free(pc_buf);
	/*TODO*///				}
	/*TODO*///				else
	/*TODO*///				{
	/*TODO*///					logerror("failed to malloc(%d)\n", img->length);
	/*TODO*///				}
	/*TODO*///			}
	/*TODO*///			pc_dev++;
	/*TODO*///		}
	/*TODO*///
	/*TODO*///		if (!img->crc) img->crc = osd_fcrc(file);
	/*TODO*///		if( img->crc == 0 && img->length < 0x100000 )
	/*TODO*///		{
	/*TODO*///			logerror("image_fopen: calling osd_fchecksum() for %d bytes\n", img->length);
	/*TODO*///			osd_fchecksum(sysname, img->name, &img->length, &img->crc);
	/*TODO*///			logerror("image_fopen: CRC is %08x\n", img->crc);
	/*TODO*///		}
	/*TODO*///		free_image_info(img);
	/*TODO*///
	/*TODO*///		if (read_crc_config (crcfile, img, sysname) && Machine->gamedrv->clone_of->name)
	/*TODO*///			read_crc_config (pcrcfile, img, Machine->gamedrv->clone_of->name);
	/*TODO*///
	/*TODO*///		config = config_open(crcfile);
	/*TODO*///	}
	/*TODO*///
	    return file;
	}
	
/*TODO*///	void *image_fopen(int type, int id, int filetype, int read_or_write)
/*TODO*///	{
/*TODO*///		struct image_info *img = &images[type][id];
/*TODO*///		const char *sysname;
/*TODO*///		void *file;
/*TODO*///		int extnum;
/*TODO*///		int original_len;
/*TODO*///	
/*TODO*///		if( type >= IO_COUNT )
/*TODO*///		{
/*TODO*///			logerror("image_fopen: type out of range (%d)\n", type);
/*TODO*///			return NULL;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if( id >= count[type] )
/*TODO*///		{
/*TODO*///			logerror("image_fopen: id out of range (%d)\n", id);
/*TODO*///			return NULL;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if( img->name == NULL )
/*TODO*///			return NULL;
/*TODO*///	
/*TODO*///		/* try the supported extensions */
/*TODO*///		extnum = 0;
/*TODO*///	
/*TODO*///		/* remember original file name */
/*TODO*///		original_len = strlen(img->name);
/*TODO*///	
/*TODO*///		{
/*TODO*///			extern struct GameDriver driver_0;
/*TODO*///	
/*TODO*///			sysname = Machine->gamedrv->name;
/*TODO*///			logerror("image_fopen: trying %s for system %s\n", img->name, sysname);
/*TODO*///			file = osd_fopen(sysname, img->name, filetype, read_or_write);
/*TODO*///			/* file found, break out */
/*TODO*///			if (file == 0)
/*TODO*///			{
/*TODO*///				if( Machine->gamedrv->clone_of &&
/*TODO*///					Machine->gamedrv->clone_of != &driver_0 )
/*TODO*///				{	/* R Nabet : Shouldn't this be moved to osd code ??? Mac osd code does such a retry
/*TODO*///					whenever it makes sense, and I think this is the correct way. */
/*TODO*///					sysname = Machine->gamedrv->clone_of->name;
/*TODO*///					logerror("image_fopen: now trying %s for system %s\n", img->name, sysname);
/*TODO*///					file = osd_fopen(sysname, img->name, filetype, read_or_write);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (file)
/*TODO*///		{
/*TODO*///			void *config;
/*TODO*///			const struct IODevice *pc_dev = Machine->gamedrv->dev;
/*TODO*///	
/*TODO*///			/* did osd_fopen() rename the image? (yes, I know this is a hack) */
/*TODO*///			if (renamed_image)
/*TODO*///			{
/*TODO*///				free(img->name);
/*TODO*///				img->name = renamed_image;
/*TODO*///				renamed_image = NULL;
/*TODO*///			}
/*TODO*///	
/*TODO*///			logerror("image_fopen: found image %s for system %s\n", img->name, sysname);
/*TODO*///			img->length = osd_fsize(file);
/*TODO*///	/* Cowering, partial crcs for NES/A7800/others */
/*TODO*///			img->crc = 0;
/*TODO*///			while( pc_dev && pc_dev->count && !img->crc)
/*TODO*///			{
/*TODO*///				logerror("partialcrc() -> %08lx\n",pc_dev->partialcrc);
/*TODO*///				if( type == pc_dev->type && pc_dev->partialcrc )
/*TODO*///				{
/*TODO*///					unsigned char *pc_buf = (unsigned char *)malloc(img->length);
/*TODO*///					if( pc_buf )
/*TODO*///					{
/*TODO*///						osd_fseek(file,0,SEEK_SET);
/*TODO*///						osd_fread(file,pc_buf,img->length);
/*TODO*///						osd_fseek(file,0,SEEK_SET);
/*TODO*///						logerror("Calling partialcrc()\n");
/*TODO*///						img->crc = (*pc_dev->partialcrc)(pc_buf,img->length);
/*TODO*///						free(pc_buf);
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						logerror("failed to malloc(%d)\n", img->length);
/*TODO*///					}
/*TODO*///				}
/*TODO*///				pc_dev++;
/*TODO*///			}
/*TODO*///	
/*TODO*///			if (!img->crc) img->crc = osd_fcrc(file);
/*TODO*///			if( img->crc == 0 && img->length < 0x100000 )
/*TODO*///			{
/*TODO*///				logerror("image_fopen: calling osd_fchecksum() for %d bytes\n", img->length);
/*TODO*///				osd_fchecksum(sysname, img->name, &img->length, &img->crc);
/*TODO*///				logerror("image_fopen: CRC is %08x\n", img->crc);
/*TODO*///			}
/*TODO*///	
/*TODO*///			if (read_crc_config (crcfile, img, sysname) && Machine->gamedrv->clone_of->name)
/*TODO*///				read_crc_config (pcrcfile, img, Machine->gamedrv->clone_of->name);
/*TODO*///	
/*TODO*///			config = config_open(crcfile);
/*TODO*///		}
/*TODO*///	
/*TODO*///		return file;
/*TODO*///	}


	/* common init for all IO_FLOPPY devices */
	public static void	floppy_device_common_init(int id)
	{
		logerror("floppy device common init: id: %02x\n",id);
		/* disk inserted */
		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_DISK_INSERTED, 1);
		/* drive connected */
		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_CONNECTED, 1);
	}

/*TODO*///	/* common exit for all IO_FLOPPY devices */
/*TODO*///	static void floppy_device_common_exit(int id)
/*TODO*///	{
/*TODO*///		logerror("floppy device common exit: id: %02x\n",id);
/*TODO*///		/* disk removed */
/*TODO*///		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_DISK_INSERTED, 0);
/*TODO*///		/* drive disconnected */
/*TODO*///		floppy_drive_set_flag_state(id, FLOPPY_DRIVE_CONNECTED, 0);
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Does the system support cassette (for tapecontrol)
/*TODO*///	 * TRUE, FALSE return
/*TODO*///	 */
/*TODO*///	int system_supports_cassette_device (void)
/*TODO*///	{
/*TODO*///		const struct IODevice *dev = Machine->gamedrv->dev;
/*TODO*///	
/*TODO*///		/* Cycle through all devices for this system */
/*TODO*///		while(dev->type != IO_END)
/*TODO*///		{
/*TODO*///			if (dev->type == IO_CASSETTE)
/*TODO*///				return TRUE;
/*TODO*///			dev++;
/*TODO*///		}
/*TODO*///	
/*TODO*///		return FALSE;
/*TODO*///	}

	/*
	 * Return a name for the device type (to be used for UI functions)
	 */
	public static String device_typename(int type)
	{
		if (type < IO_COUNT)
			return devices[type].name;
		return "UNKNOWN";
	}

/*TODO*///	const char *device_brieftypename(int type)
/*TODO*///	{
/*TODO*///		if (type < IO_COUNT)
/*TODO*///			return devices[type].shortname;
/*TODO*///		return "UNKNOWN";
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* Return a name for a device of type 'type' with id 'id' */
/*TODO*///	const char *device_typename_id(int type, int id)
/*TODO*///	{
/*TODO*///		static char typename_id[40][31+1];
/*TODO*///		static int which = 0;
/*TODO*///		if (type < IO_COUNT)
/*TODO*///		{
/*TODO*///			which = (which + 1) % 40;
/*TODO*///			/* for the average user counting starts at #1 ;-) */
/*TODO*///			sprintf(typename_id[which], "%s #%d", devices[type].name, id+1);
/*TODO*///			return typename_id[which];
/*TODO*///		}
/*TODO*///		return "UNKNOWN";
/*TODO*///	}

		/*
		 * Return the number of filenames for a device of type 'type'.
		 */
		public static int device_count(int type)
		{
			if (type >= IO_COUNT)
				return 0;
			return count[type];
		}

	
		/*
	     * Return the 'id'th filename for a device of type 'type',
	     * NULL if not enough image names of that type are available.
	     */
	    public static String device_filename(int type, int id)
	    {
	        if (type >= IO_COUNT)
	            return null;
	        if (id < count[type])
	            return images[type][id].name;
	        return null;
	    }
	    
	    /*
         * Return the 'num'th file extension for a device of type 'type',
         * NULL if no file extensions of that type are available.
         */
        public static String device_file_extension(int type, int extnum)
        {
            IODevice[] dev = Machine.gamedrv.dev;
            int cont = 0;
            String ext=null;
            if (type >= IO_COUNT)
                return null;
            while( dev[cont].count != 0)
            {
                if( type == dev[cont].type )
                {
                    ext = dev[cont].file_extensions;
                    /*TODO*/////while( (ext && ext && extnum--) > 0 )
                    /*TODO*/////    ext = ext + (ext.length()) + 1;
                    /*TODO*/////if( ext && !ext )
                    /*TODO*/////    ext = null;
                    return ext;
                }
                /*TODO*/////dev++;
                cont++;
            }
            return null;
        }
	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th crc for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	unsigned int device_crc(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return 0;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].crc;
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Set the 'id'th crc for a device of type 'type',
/*TODO*///	 * this is to be used if only a 'partial crc' shall be used.
/*TODO*///	 */
/*TODO*///	void device_set_crc(int type, int id, UINT32 new_crc)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///		{
/*TODO*///			logerror("device_set_crc: type out of bounds (%d)\n", type);
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		if (id < count[type])
/*TODO*///		{
/*TODO*///			images[type][id].crc = new_crc;
/*TODO*///			logerror("device_set_crc: new_crc %08x\n", new_crc);
/*TODO*///		}
/*TODO*///		else
/*TODO*///			logerror("device_set_crc: id out of bounds (%d)\n", id);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th length for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	unsigned int device_length(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return 0;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].length;
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th long name for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	const char *device_longname(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return NULL;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].longname;
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th manufacturer name for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	const char *device_manufacturer(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return NULL;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].manufacturer;
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th release year for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	const char *device_year(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return NULL;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].year;
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th playable info for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	const char *device_playable(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return NULL;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].playable;
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * Return the 'id'th extrainfo info for a device of type 'type',
/*TODO*///	 * NULL if not enough image names of that type are available.
/*TODO*///	 */
/*TODO*///	const char *device_extrainfo(int type, int id)
/*TODO*///	{
/*TODO*///		if (type >= IO_COUNT)
/*TODO*///			return NULL;
/*TODO*///		if (id < count[type])
/*TODO*///			return images[type][id].extrainfo;
/*TODO*///		return NULL;
/*TODO*///	}
/*TODO*///	
        /*****************************************************************************
         *  --Distribute images to their respective Devices--
         *  Copy the Images specified at the CLI from options.image_files[] to the
         *  array of filenames we keep here, depending on the Device type identifier
         *  of each image.  Multiple instances of the same device are allowed
         *  RETURNS 0 on success, 1 if failed
         ****************************************************************************/
        public static int distribute_images()
        {
            int i,j;
	
            logerror("Distributing Images to Devices...\n");
            //System.out.println("Distributing Images to Devices...");
            
		/* Set names to NULL */
		for (i=0;i<IO_COUNT;i++)
			for (j=0;j<MAX_INSTANCES;j++)
				images[i][j].name = null;
	//System.out.println("options.image_count "+options.image_count);
		for( i = 0; i < options.image_count; i++ )
		{
			int type = options.image_files[i].type;
                        //System.out.println("i: "+i);
                        //System.out.println("count[type]: "+count[type]);
                        //System.out.println("OPTIONS NAME: "+options.image_files[i].name);
                        //System.out.println("OPTIONS TYPE: "+options.image_files[i].type);
			if (type < IO_COUNT)
			{
				/* Add a filename to the arrays of names */
				if( options.image_files[i].name != null)
				{
					images[type][count[type]].name = dupe(options.image_files[i].name);
                                        //System.out.println("IMAGES: "+images[type][count[type]].name);
					if( images[type][count[type]].name == null)
					{
						printf(" ERROR - dupe() failed\n");
						return 1;
					}
				}
				count[type]++;
			}
			else
			{
				printf(" Invalid Device type %d for %s\n", type, options.image_files[i].name);
				return 1;
			}
		}
	
		/* everything was fine */
		return 0;
    }


	/* Small check to see if system supports device */
	public static int supported_device(IODevice[] dev, int type)
	{
		int dev_ptr = 0;
		while(dev[dev_ptr].type!=IO_END)
		{
			if(dev[dev_ptr].type==type)
				return 1;	/* Return OK */
			dev_ptr++;
		}
		return 0;
	}

	/*****************************************************************************
	 *  --Initialise Devices--
	 *  Call the init() functions for all devices of a driver
	 *  ith all user specified image names.
	 ****************************************************************************/
	public static int init_devices(GameDriver game)
	{
		GameDriver gamedrv = game;
		IODevice[] dev = gamedrv.dev;
		int i,id;
		int dev_ptr = 0;
	
		logerror("Initialising Devices...\n");
	
		/* Check that the driver supports all devices requested (options struct)*/
		for( i = 0; i < options.image_count; i++ )
		{
			if (supported_device(dev, options.image_files[i].type)==0)
			{
				mess_printf(" ERROR: Device [%s] is not supported by this system\n",device_typename(options.image_files[i].type));
				return 1;
			}
		}
	
		/* Ok! All devices are supported.  Now distribute them to the appropriate device..... */
		if (distribute_images() == 1)
			return 1;
	
		/* Initialise all floppy drives here if the device is Setting can be overriden by the drivers and UI */
		floppy_drives_init();
	
		/* initialize --all-- devices */
		while( dev[dev_ptr].count != 0 )
		{
			/* all instances */
			for( id = 0; id < dev[dev_ptr].count; id++ )
			{
				mess_printf("Initialising %s device #%d\n",device_typename(dev[dev_ptr].type), id + 1);
				/********************************************************************
				 * CALL INITIALISE DEVICE
				 ********************************************************************/
				/* if this device supports initialize (it should!) */
				if( dev[dev_ptr].init != null )
				{
					int result;
	
					/* initialize */
					result = dev[dev_ptr].init.handler(id);
	
					if( result != INIT_PASS)
					{
						mess_printf("Driver Reports Initialisation [for %s device] failed\n",device_typename(dev[dev_ptr].type));
						mess_printf("Ensure image is valid and exists and (if needed) can be created\n");
						mess_printf("Also remember that some systems cannot boot without a valid image!\n");
						return 1;
					}
	
					/* init succeeded */
					/* if floppy, perform common init */
					if ((dev[dev_ptr].type == IO_FLOPPY) && (device_filename(dev[dev_ptr].type, id) != null))
					{
						floppy_device_common_init(id);
					}
				}
				else
				{
					mess_printf(" %s does not support init!\n", device_typename(dev[dev_ptr].type));
				}
			}
			dev_ptr++;
		}
		mess_printf("Device Initialision Complete!\n");
		return 0;
	}

	/*
	 * Call the exit() functions for all devices of a
	 * driver for all images.
	 */
	public static void exit_devices()
	{
/*TODO*///		const struct IODevice *dev = Machine->gamedrv->dev;
/*TODO*///		int id;
/*TODO*///		int type;
/*TODO*///	
/*TODO*///		/* shutdown all devices */
/*TODO*///		while( dev->count )
/*TODO*///		{
/*TODO*///			/* all instances */
/*TODO*///			if( dev->exit)
/*TODO*///			{
/*TODO*///				/* shutdown */
/*TODO*///				for( id = 0; id < device_count(dev->type); id++ )
/*TODO*///					(*dev->exit)(id);
/*TODO*///	
/*TODO*///			}
/*TODO*///			else
/*TODO*///			{
/*TODO*///				logerror("%s does not support exit!\n", device_typename(dev->type));
/*TODO*///			}
/*TODO*///	
/*TODO*///			/* The following is always executed for a IO_FLOPPY exit */
/*TODO*///			/* KT: if a image is removed:
/*TODO*///				1. Disconnect drive
/*TODO*///				2. Remove disk from drive */
/*TODO*///			/* This is done here, so if a device doesn't support exit, the status
/*TODO*///			will still be correct */
/*TODO*///			if (dev->type == IO_FLOPPY)
/*TODO*///			{
/*TODO*///				for (id = 0; id< device_count(dev->type); id++)
/*TODO*///				{
/*TODO*///					floppy_device_common_exit(id);
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///			dev++;
/*TODO*///		}
/*TODO*///	
/*TODO*///		for( type = 0; type < IO_COUNT; type++ )
/*TODO*///		{
/*TODO*///			if( images[type] )
/*TODO*///			{
/*TODO*///				for( id = 0; id < device_count(dev->type); id++ )
/*TODO*///				{
/*TODO*///					if( images[type][id].name )
/*TODO*///						free(images[type][id].name);
/*TODO*///					if( images[type][id].longname )
/*TODO*///						free(images[type][id].longname);
/*TODO*///					if( images[type][id].manufacturer )
/*TODO*///						free(images[type][id].manufacturer);
/*TODO*///					if( images[type][id].year )
/*TODO*///						free(images[type][id].year);
/*TODO*///					if( images[type][id].playable )
/*TODO*///						free(images[type][id].playable);
/*TODO*///					if( images[type][id].extrainfo )
/*TODO*///						free(images[type][id].extrainfo);
/*TODO*///				}
/*TODO*///			}
/*TODO*///			count[type] = 0;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* KT: clean up */
/*TODO*///		floppy_drives_exit();
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///		for( type = 0; type < IO_COUNT; type++ )
/*TODO*///		{
/*TODO*///			if (count[type])
/*TODO*///				mess_printf("OOPS!!!  Appears not all images free!\n");
/*TODO*///	
/*TODO*///		}
/*TODO*///	#endif
	
	}
	
/*TODO*///	/*
/*TODO*///	 * Change the associated image filename for a device.
/*TODO*///	 * Returns 0 if successful.
/*TODO*///	 */
/*TODO*///	int device_filename_change(int type, int id, const char *name)
/*TODO*///	{
/*TODO*///		const struct IODevice *dev = Machine->gamedrv->dev;
/*TODO*///		struct image_info *img = &images[type][id];
/*TODO*///	
/*TODO*///		if( type >= IO_COUNT )
/*TODO*///			return 1;
/*TODO*///	
/*TODO*///		while( dev->count && dev->type != type )
/*TODO*///			dev++;
/*TODO*///	
/*TODO*///		if( id >= dev->count )
/*TODO*///			return 1;
/*TODO*///	
/*TODO*///		if( dev->exit )
/*TODO*///			dev->exit(id);
/*TODO*///	
/*TODO*///		/* if floppy, perform common exit */
/*TODO*///		if (dev->type == IO_FLOPPY)
/*TODO*///		{
/*TODO*///			floppy_device_common_exit(id);
/*TODO*///		}
/*TODO*///	
/*TODO*///		if( dev->init )
/*TODO*///		{
/*TODO*///			int result;
/*TODO*///			/*
/*TODO*///			 * set the new filename and reset all addition info, it will
/*TODO*///			 * be inserted by osd_fopen() and the crc handling
/*TODO*///			 */
/*TODO*///			img->name = NULL;
/*TODO*///			img->length = 0;
/*TODO*///			img->crc = 0;
/*TODO*///			if( name )
/*TODO*///			{
/*TODO*///				img->name = dupe(name);
/*TODO*///				/* Check the name */
/*TODO*///				if( !img->name )
/*TODO*///					return 1;
/*TODO*///				/* check the count - if it was 0, add new! */
/*TODO*///				if (!device_count(type))
/*TODO*///					count[type]++;
/*TODO*///			}
/*TODO*///	
/*TODO*///			if( dev->reset_depth == IO_RESET_CPU )
/*TODO*///				machine_reset();
/*TODO*///			else
/*TODO*///			if( dev->reset_depth == IO_RESET_ALL )
/*TODO*///			{
/*TODO*///				mess_keep_going = 1;
/*TODO*///	
/*TODO*///			}
/*TODO*///	
/*TODO*///			result = (*dev->init)(id);
/*TODO*///			if( result != INIT_PASS)
/*TODO*///				return 1;
/*TODO*///	
/*TODO*///			/* init succeeded */
/*TODO*///			/* if floppy, perform common init */
/*TODO*///			if (dev->type == IO_FLOPPY)
/*TODO*///			{
/*TODO*///				floppy_device_common_init(id);
/*TODO*///			}
/*TODO*///	
/*TODO*///		}
/*TODO*///		return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	int messvaliditychecks(void)
/*TODO*///	{
/*TODO*///		int i;
/*TODO*///		int error = 0;
/*TODO*///	
/*TODO*///		/* Check the device struct array */
/*TODO*///		i=0;
/*TODO*///		while (devices[i].id != IO_COUNT)
/*TODO*///		{
/*TODO*///			if (devices[i].id != i)
/*TODO*///			{
/*TODO*///				mess_printf("MESS Validity Error - Device struct array order mismatch\n");
/*TODO*///				error = 1;
/*TODO*///			}
/*TODO*///			i++;
/*TODO*///		}
/*TODO*///		if (i < IO_COUNT)
/*TODO*///		{
/*TODO*///			mess_printf("MESS Validity Error - Device struct entry missing\n");
/*TODO*///			error = 1;
/*TODO*///		}
/*TODO*///		return error;
/*TODO*///	}
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	
/*TODO*///	int displayimageinfo(struct mame_bitmap *bitmap, int selected)
/*TODO*///	{
/*TODO*///		char buf[2048], *dst = buf;
/*TODO*///		int type, id, sel = selected - 1;
/*TODO*///	
/*TODO*///		dst += sprintf(dst,"%s\n\n",Machine->gamedrv->description);
/*TODO*///	
/*TODO*///		for (type = 0; type < IO_COUNT; type++)
/*TODO*///		{
/*TODO*///			for( id = 0; id < device_count(type); id++ )
/*TODO*///			{
/*TODO*///				const char *name = device_filename(type,id);
/*TODO*///				if( name )
/*TODO*///				{
/*TODO*///					const char *info;
/*TODO*///					char *filename;
/*TODO*///	
/*TODO*///					filename = (char *) device_filename(type, id);
/*TODO*///	
/*TODO*///					dst += sprintf(dst,"%s: %s\n", device_typename_id(type,id), osd_basename(filename));
/*TODO*///					info = device_longname(type,id);
/*TODO*///					if( info )
/*TODO*///						dst += sprintf(dst,"%s\n", info);
/*TODO*///					info = device_manufacturer(type,id);
/*TODO*///					if( info )
/*TODO*///					{
/*TODO*///						dst += sprintf(dst,"%s", info);
/*TODO*///						info = stripspace(device_year(type,id));
/*TODO*///						if( info && strlen(info))
/*TODO*///							dst += sprintf(dst,", %s", info);
/*TODO*///						dst += sprintf(dst,"\n");
/*TODO*///					}
/*TODO*///					info = device_playable(type,id);
/*TODO*///					if( info )
/*TODO*///						dst += sprintf(dst,"%s\n", info);
/*TODO*///	// why is extrainfo printed? only MSX and NES use it that i know of ... Cowering
/*TODO*///	//				info = device_extrainfo(type,id);
/*TODO*///	//				if( info )
/*TODO*///	//					dst += sprintf(dst,"%s\n", info);
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dst += sprintf(dst,"%s: ---\n", device_typename_id(type,id));
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (sel == -1)
/*TODO*///		{
/*TODO*///			/* startup info, print MAME version and ask for any key */
/*TODO*///	
/*TODO*///			strcat(buf,"\n\tPress any key to Begin");
/*TODO*///			ui_drawbox(bitmap,0,0,Machine->uiwidth,Machine->uiheight);
/*TODO*///			ui_displaymessagewindow(bitmap, buf);
/*TODO*///	
/*TODO*///			sel = 0;
/*TODO*///			if (code_read_async() != KEYCODE_NONE ||
/*TODO*///				code_read_async() != JOYCODE_NONE)
/*TODO*///				sel = -1;
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* menu system, use the normal menu keys */
/*TODO*///			strcat(buf,"\n\t\x1a Return to Main Menu \x1b");
/*TODO*///	
/*TODO*///			ui_displaymessagewindow(bitmap,buf);
/*TODO*///	
/*TODO*///			if (input_ui_pressed(IPT_UI_SELECT))
/*TODO*///				sel = -1;
/*TODO*///	
/*TODO*///			if (input_ui_pressed(IPT_UI_CANCEL))
/*TODO*///				sel = -1;
/*TODO*///	
/*TODO*///			if (input_ui_pressed(IPT_UI_CONFIGURE))
/*TODO*///				sel = -2;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (sel == -1 || sel == -2)
/*TODO*///		{
/*TODO*///			/* tell updatescreen() to clean after us */
/*TODO*///			schedule_full_refresh();
/*TODO*///		}
/*TODO*///	
/*TODO*///		return sel + 1;
/*TODO*///	}
/*TODO*///	
	
	public static void showmessdisclaimer()
	{
		mess_printf(
			"MESS is an emulator: it reproduces, more or less faithfully, the behaviour of\n"+
			"several computer and console systems. But hardware is useless without software\n"+
			"so a file dump of the BIOS, cartridges, discs, and cassettes which run on that\n"+
			"hardware is required. Such files, like any other commercial software, are\n"+
			"copyrighted material and it is therefore illegal to use them if you don't own\n"+
			"the original media from which the files are derived. Needless to say, these\n"+
			"files are not distributed together with MESS. Distribution of MESS together\n"+
			"with these files is a violation of copyright law and should be promptly\n"+
			"reported to the authors so that appropriate legal action can be taken.\n\n");
	}
	
	public static void showmessinfo()
	{
		mess_printf(
			"M.E.S.S. v%s\n"+
			"Multiple Emulation Super System - Copyright (C) 1997-2001 by the MESS Team\n"+
			"M.E.S.S. is based on the ever excellent M.A.M.E. Source code\n"+
			"Copyright (C) 1997-2001 by Nicola Salmoria and the MAME Team\n\n",
			build_version);
		showmessdisclaimer();
		mess_printf(
			"Usage:  MESS <system> <device> <software> <options>\n\n"+
			"        MESS -list        for a brief list of supported systems\n"+
			"        MESS -listdevices for a full list of supported devices\n"+
			"        MESS -showusage   to see usage instructions\n"+
			"See mess.txt for help, readme.txt for options.\n");
	
	}
	/*
	    * Copy the image names from options.image_files[] to
	    * the array of filenames we keep here, depending on the
	    * type identifier of each image.
	     */
	    public static int get_filenames() {
	        IODevice[] dev = Machine.gamedrv.dev;
	        int dev_ptr = 0;
	        int i;

	        for (i = 0; i < options.image_count; i++) {
	            int type = options.image_files[i].type;

	            if (type < IO_COUNT) {
	                /*TODO*///			/* Add a filename to the arrays of names */
	/*TODO*///			if( images[type] )
	/*TODO*///				images[type] = realloc(images[type],(count[type]+1)*sizeof(struct image_info));
	/*TODO*///			else
	/*TODO*///				images[type] = malloc(sizeof(struct image_info));
	/*TODO*///			if( !images[type] )
	/*TODO*///				return 1;
	/*TODO*///			memset(&images[type][count[type]], 0, sizeof(struct image_info));
	/*TODO*///			if( options.image_files[i].name )
	/*TODO*///			{
	/*TODO*///				images[type][count[type]].name = dupe(options.image_files[i].name);
	/*TODO*///				if( !images[type][count[type]].name )
	/*TODO*///					return 1;
	/*TODO*///			}
	/*TODO*///			logerror("%s #%d: %s\n", typename[type], count[type]+1, images[type][count[type]].name);
	/*TODO*///			count[type]++;
	//TODO below code needs to be tested
	                images[type][count[type]] = new image_info();
	                if (options.image_files[i].name != null) {
	                    images[type][count[type]].name = options.image_files[i].name;
	                    if (images[type][count[type]].name == null) {
	                        return 1;
	                    }
	                }
	                //logerror("%s #%d: %s\n", typename[type], count[type] + 1, images[type][count[type]].name);
	                count[type]++;
	            } else {
	                logerror("Invalid IO_ type %d for %s\n", type, options.image_files[i].name);
	                return 1;
	            }
	        }

	        /* Does the driver have any IODevices defined? */
	        if (dev != null) {
	            while (dev[dev_ptr].count != 0) {
	                int type = dev[dev_ptr].type;
	                /*TODO*///                while (count[type] < dev[dev_ptr].count) {
	/*TODO*///                    throw new UnsupportedOperationException("unimplemented");
	                /*TODO*///				/* Add an empty slot name the arrays of names */
	/*TODO*///				if( images[type] )
	/*TODO*///					images[type] = realloc(images[type],(count[type]+1)*sizeof(struct image_info));
	/*TODO*///				else
	/*TODO*///					images[type] = malloc(sizeof(struct image_info));
	/*TODO*///				if( !images[type] )
	/*TODO*///					return 1;
	/*TODO*///				memset(&images[type][count[type]], 0, sizeof(struct image_info));
	/*TODO*///				count[type]++;
	/*TODO*///                }
	                dev_ptr++;
	            }
	        }

	        /* everything was fine */
	        return 0;
	    }	
}
