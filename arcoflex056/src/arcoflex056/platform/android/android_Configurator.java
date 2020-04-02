/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arcoflex056.platform.android;

import static arcoflex056.platform.platformConfigurator.*;

/**
 *
 * @author chusogar
 */
public class android_Configurator  implements i_platform_configurator {
    
    public android_inputClass current_android_input_class = new android_inputClass();
    public android_softwareGFXClass current_android_softwareGFX_class = new android_softwareGFXClass();
    public android_urlDownloadProgress current_android_urlDownloadProgress_class = new android_urlDownloadProgress();
    public android_videoClass current_android_video_class = new android_videoClass();
    public android_SoundPlayerClass current_android_SoundPlayer_class = new android_SoundPlayerClass();
    public android_filemngrClass current_filemngr_class = new android_filemngrClass();
    
    @Override
    public String getPlatformName() {
        return "Android";
    }

    @Override
    public i_input_class get_input_class() {
        return current_android_input_class;
    }

    @Override
    public i_software_gfx_class get_software_gfx_class() {
        return current_android_softwareGFX_class;
    }

    @Override
    public i_URLDownloadProgress_class get_URLDownloadProgress_class() {
        return current_android_urlDownloadProgress_class;
    }

    @Override
    public i_video_class get_video_class() {
        return current_android_video_class;
    }

    @Override
    public i_SoundPlayer_class get_SoundPlayer_class() {
        return current_android_SoundPlayer_class;
    }

    @Override
    public i_filemngr_class get_filemngr_class() {
        return current_filemngr_class;
    }

        
}

