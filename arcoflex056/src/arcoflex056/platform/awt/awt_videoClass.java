/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arcoflex056.platform.awt;

import static arcoflex056.platform.platformConfigurator.*;

import java.awt.Toolkit;

/**
 *
 * @author chusogar
 */
public class awt_videoClass implements i_video_class {

    @Override
    public double getWidth() {
        java.awt.Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        
        return d.getWidth();
    }

    @Override
    public double getHeight() {
        java.awt.Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        
        return d.getHeight();
    }
    
        
}
