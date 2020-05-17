/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mame056.cpu.konami;

public class konamiH
{
	
        public static final int KONAMI_INT_NONE  =0;   /* No interrupt required */
        public static final int KONAMI_INT_IRQ	 =1;	/* Standard IRQ interrupt */
        public static final int KONAMI_INT_FIRQ  =2;	/* Fast IRQ */
        public static final int KONAMI_INT_NMI   =4;	/* NMI */	/* NS 970909 */
        
	public static final int KONAMI_PC           = 1;
        public static final int KONAMI_S            = 2;
        public static final int KONAMI_CC           = 3;
        public static final int KONAMI_A            = 4;
        public static final int KONAMI_B            = 5;
        public static final int KONAMI_U            = 6;
        public static final int KONAMI_X            = 7;
        public static final int KONAMI_Y            = 8;
	public static final int KONAMI_DP           = 9;
        public static final int KONAMI_NMI_STATE    = 10;
        public static final int KONAMI_IRQ_STATE    = 11;
        public static final int KONAMI_FIRQ_STATE   = 12;
	
	public static final int KONAMI_IRQ_LINE     = 0;	/* IRQ line number */
	public static final int KONAMI_FIRQ_LINE    = 1;   /* FIRQ line number */
	
}
