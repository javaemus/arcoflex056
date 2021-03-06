/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056;

import static common.libc.cstdio.*;
import static mame056.commonH.*;
import static mame056.inptportH.*;
import static mame056.input.*;
import static mame056.mame.*;
import static mess056.device.*;
import static mess056.deviceH.*;
import static mess056.mess.*;
import static mame056.usrintrf.*;

public class tapectrl
{
	static int id = 0;
	
	public static int tapecontrol(mame_bitmap bitmap, int selected)
	{
            String timepos="";
            int t0, t1;
	    String[] menu_item = new String[40];
	    String[] menu_subitem = new String[40];
            char[] flag = new char[40];
	
	    int sel;
	    int total;
	    int arrowize;
            int status;
            
            if (device_count(IO_CASSETTE)==0) return 0;
	
	    total = 0;
	    sel = selected - 1;
	
	    menu_item[total] = device_typename_id(IO_CASSETTE,id);
            menu_subitem[total] = (device_filename(IO_CASSETTE,id) != null ? device_filename(IO_CASSETTE,id) : "---");
            flag[total] = 0;
            total++;

            t0 = device_tell(IO_CASSETTE,id);
            /* Using the following trick because device_length() is the file length,
             * and might not be valid */
            t1 = device_seek(IO_CASSETTE,id,0,SEEK_END);
            device_seek(IO_CASSETTE,id,t0,SEEK_SET);
            
            
            if( t1 != 0 )
                    sprintf(timepos, "%3d%%", t0*100/t1);
            else
                    sprintf(timepos, "%3d%%", 0);
            menu_item[total] = (device_status(IO_CASSETTE,id,-1) & 1)!=0 ? "playing" : "stopped";
            menu_subitem[total] = timepos;
	    flag[total] = 0;
            total++;
	
	    menu_item[total] = "Pause/Stop";
            menu_subitem[total] = "";
	    flag[total] = 0;
            total++;

            menu_item[total] = "Play";
            menu_subitem[total] = "";
	    flag[total] = 0;
            total++;

            menu_item[total] = "Rewind";
            menu_subitem[total] = "";
            flag[total] = 0;
	    total++;
	
            menu_item[total] = "Fast forward";
            menu_subitem[total] = "";
            flag[total] = 0;
	    total++;
	
	    menu_item[total] = "Return to Main Menu";
	    menu_subitem[total] = "";
	    flag[total] = 0;
	    total++;
	
	    menu_item[total] = "";   /* terminate array */
	    menu_subitem[total] = "";
	    flag[total] = 0;
	
	    arrowize = 0;
	    if (sel < total - 1)
	        arrowize = 2;
	
	    if (sel > 255)  /* are we waiting for a new key? */
	    {
	        /* display the menu */
			ui_displaymenu(bitmap, menu_item,menu_subitem,flag,sel & 0xff,3);
	        return sel + 1;
	    }
	
		ui_displaymenu(bitmap, menu_item,menu_subitem,flag,sel,arrowize);
	
	    if (input_ui_pressed_repeat(IPT_UI_DOWN,8) != 0)
	    {
	        if (sel < total - 1) sel++;
	        else sel = 0;
	    }
	
	    if (input_ui_pressed_repeat(IPT_UI_UP,8) != 0)
	    {
	        if (sel > 0) sel--;
	        else sel = total - 1;
	    }
	
	
		if (input_ui_pressed(IPT_UI_LEFT) != 0)
	    {
			switch (sel)
			{
			case 0:
				id--;
				if (id < 0) id = device_count(IO_CASSETTE)-1;
				break;
			}
			/* tell updatescreen() to clean after us (in case the window changes size) */
			schedule_full_refresh();
	    }
	
		if (input_ui_pressed(IPT_UI_RIGHT) != 0)
	    {
			switch (sel)
			{
			case 0:
				id++;
				if (id > device_count(IO_CASSETTE)-1) id = 0;
				break;
			}
			/* tell updatescreen() to clean after us (in case the window changes size) */
			schedule_full_refresh();
	    }
	
	    if (input_ui_pressed(IPT_UI_SELECT) != 0)
	    {
	        if (sel == total - 1){
	            sel = -1;
                    
                } else
	        {
                    
				status = device_status(IO_CASSETTE,id,-1);
                                
				switch (sel)
				{
				case 0:
                                        id = (id + 1) % device_count(IO_CASSETTE);
					break;
				case 2:
                                        if ((status & 1) == 0)
						device_seek(IO_CASSETTE,id,0,SEEK_SET);
					device_status(IO_CASSETTE,id,status & ~1);
					break;
				case 3:
                                    
					device_status(IO_CASSETTE,id,status | 1);
	                break;
				case 4:
                                    
					device_seek(IO_CASSETTE,id,-11025,SEEK_CUR);
					break;
				case 5:
                                    
					device_seek(IO_CASSETTE,id,+11025,SEEK_CUR);
					break;
	            }
                                
	            /* tell updatescreen() to clean after us (in case the window changes size) */
	            schedule_full_refresh();
	        }
	    }
            
            
	    if (input_ui_pressed(IPT_UI_CANCEL) != 0)
	        sel = -1;
	
	    if (input_ui_pressed(IPT_UI_CONFIGURE) != 0)
	        sel = -2;
	
	    if (sel == -1 || sel == -2)
	    {
	        /* tell updatescreen() to clean after us */
	        schedule_full_refresh();
	    }
            
            
	    return sel + 1;
	}
	
	
}
