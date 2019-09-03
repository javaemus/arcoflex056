/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mame056.cpu.z80;

import static mame056.cpuintrfH.*;
import static mame056.memoryH.*;

/**
 *
 * @author chusogar
 */
public class z80_MSX extends z80 {
    
    public z80_MSX() {
        super();
        cpu_num = CPU_Z80_MSX;
        
        isZ80_MSX = true;
    }
    
}
