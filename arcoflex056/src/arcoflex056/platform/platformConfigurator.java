/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arcoflex056.platform;

import static arcadeflex056.settings.*;
import mame056.inputH;
import mame056.inputH.KeyboardInfo;


/**
 *
 * @author chusogar
 */
public class platformConfigurator {
    
    public interface i_platform_configurator {
        
        public String getPlatformName();        
        public i_input_class get_input_class();
        public i_software_gfx_class get_software_gfx_class();
        public i_URLDownloadProgress_class get_URLDownloadProgress_class();
        public i_video_class get_video_class();
        public i_SoundPlayer_class get_SoundPlayer_class();
        public i_gamesxml_game_class get_gamesxml_game_class();
        public i_gamesxml_gameRoot_class get_gamesxml_gameRoot_class();
        public i_gamesxml_gamesXmlParser_class get_gamesxml_gamesXmlParser_class();
        public i_filemngr_class get_filemngr_class();
        
    };
    
    public interface i_input_class {
        
        public abstract KeyboardInfo[] osd_get_key_list();
        public abstract int osd_is_key_pressed(int keycode);
    };

    public interface i_software_gfx_class {

    };

    public interface i_URLDownloadProgress_class {
        public abstract void setVersion(String _version);
        public abstract void setVisible(boolean _visible);
        public abstract void setRomName(String _romName);
        public abstract void setFileName(String _fileName);
    };

    public interface i_video_class {

    };

    public interface i_SoundPlayer_class {

    };

    public interface i_gamesxml_game_class {

    };

    public interface i_gamesxml_gameRoot_class {

    };

    public interface i_gamesxml_gamesXmlParser_class {

    };

    public interface i_filemngr_class {

    };
    
    public static void ConfigurePlatform(i_platform_configurator platform){
        current_platform_configuration = platform;        
    };
    
}
