/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */
package mess056.includes;

import static arcadeflex056.fucPtr.*;

public class cia6526H {
/**********************************************************************
	Metal Oxid Semiconductor / Commodore Business Machines
        Complex Interface Adapter 6526

	based on 6522via emulation
**********************************************************************/

    public static int MAX_CIA = 8;

    public static class cia6526_interface
    {
            public ReadHandlerPtr in_a_func;
            public ReadHandlerPtr in_b_func;
            public WriteHandlerPtr out_a_func;
            public WriteHandlerPtr out_b_func;
            public WriteHandlerPtr out_pc_func;
            public ReadHandlerPtr in_sp_func;
            public ReadHandlerPtr out_sp_func;
            public ReadHandlerPtr in_cnt_func;
            public ReadHandlerPtr out_cnt_func;
            public ReadHandlerPtr irq_func;
            public int a_pullup, b_pullup;
            public int todin50hz;
    };
    
}
