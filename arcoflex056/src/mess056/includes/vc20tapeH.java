/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.includes;

import static mess056.deviceH.*;
import static mess056.machine.vc20tape.vc20_tape_attach_image;
import static mess056.machine.vc20tape.vc20_tape_detach_image;
import static mess056.messH.*;

public class vc20tapeH {
    public static IODevice IODEVICE_VC20TAPE = 
      new IODevice(      
        IO_CASSETTE,        /* type */
        1,                  /* count */
        "wav\0",    /* TAP, LNX and T64(maybe) later file extensions */
        IO_RESET_NONE,      /* reset if file changed */
        null,               /* id */
        vc20_tape_attach_image,	/* init */
        vc20_tape_detach_image,	/* exit */
        null,               /* info */
        null,               /* open */
        null,               /* close */
        null,               /* status */
        null,               /* seek */
        null,               /* tell */
        null,               /* input */
        null,               /* output */
        null,               /* input_chunk */
        null                /* output_chunk */
      );

}
