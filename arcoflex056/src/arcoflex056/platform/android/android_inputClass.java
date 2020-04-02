/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arcoflex056.platform.android;

import arcoflex056.platform.platformConfigurator;
import mame056.inputH;

/**
 *
 * @author jagsanchez
 */
class android_inputClass implements platformConfigurator.i_input_class{

    @Override
    public inputH.KeyboardInfo[] osd_get_key_list() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int osd_is_key_pressed(int keycode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
