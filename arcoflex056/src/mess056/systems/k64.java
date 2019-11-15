
package mess056.systems;

import static arcadeflex056.fucPtr.*;
import static common.ptr.*;
import static common.libc.cstring.*;
import consoleflex056.funcPtr.StopMachinePtr;
import static mame056.common.*;
import static mame056.commonH.*;
import static mame056.cpuexecH.*;
import static mame056.cpuintrfH.*;
import static mame056.drawgfxH.*;
import static mame056.driverH.*;
import static mame056.inptportH.*;
import static mame056.inputH.*;
import static mame056.memoryH.*;
import static mame056.sndintrfH.*;
import static mess056.deviceH.*;
import static mess056.includes.vic6567H.VIC2_HRETRACERATE;
import static mess056.includes.vic6567H.VIC6567_CLOCK;
import static mess056.includes.vic6567H.VIC6567_VRETRACERATE;
import static mess056.messH.*;
import static mess056.vidhrdw.vic6567.vic2_palette;

/**
 *
 * @author jagsanchez
 */
public class k64 {
    
    public static boolean basicPaged = false;
    public static boolean kernalPaged = false;
    public static boolean charPaged = false;
    public static boolean hardwarePaged = false;

    public static UBytePtr basicMem = new UBytePtr( 8192 );
    public static UBytePtr kernalMem = new UBytePtr( 8192 );
    public static byte peekMem[] = new byte[ 65536 ];
    public static UBytePtr charMem = new UBytePtr( 4096 );
    public static byte hardwareMem[] = new byte[ 4096 ];
    public static byte regs[] = hardwareMem;
    public static int regsOff = 0;

    public static byte vicRegs[] = new byte[ 0x2F ];

    static boolean enable1a, enable1b, enable2a, enable2b, vic_den;
    static byte vic_ec, vic_b0c;

    static int vic_y, vic_vm, vic_cb, vic_bank;
    static int latch1a=0, latch2a=0, latch1b=0, latch2b=0;
    static byte cpuport, vic_yscrl, vic_xscrl, vic_b1c, vic_b2c, vic_b3c;
    static boolean vic_ecm, vic_bmm, vic_rsel, vic_mcm, vic_csel;
    static boolean irq = false;
    static byte icr1set=0, icr2set=0;
    static byte t1a, t2a, t1b, t2b;

    static int curr_loc;
    static boolean nmi = false;
    static int vic_rasirq = 0xFFFF;
    
    public static ReadHandlerPtr c64_r = new ReadHandlerPtr() {
            public int handler(int offset) {
            
                return 0xff;
            }
    };
    
    public static WriteHandlerPtr c64_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
        
        }
    };
    
    public static Memory_ReadAddress c64_readmem[]={
        new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
        new Memory_ReadAddress(0x0000, 0xffff, c64_r),
        new Memory_ReadAddress(MEMPORT_MARKER, 0)
    };

    public static Memory_WriteAddress c64_writemem[]={
        new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
        new Memory_WriteAddress(0x0000, 0xffff, c64_w),
        new Memory_WriteAddress(MEMPORT_MARKER, 0)
    };

    public static VhConvertColorPromPtr c64_init_palette = new VhConvertColorPromPtr() {
        public void handler(char[] sys_palette, char[] sys_colortable, UBytePtr color_prom) {
            memcpy (sys_palette, vic2_palette, vic2_palette.length);
        }
    };
    
    static RomLoadPtr rom_c64 = new RomLoadPtr() { 
        public void handler() {
            ROM_REGION (0x19400, REGION_CPU1, 0);
            ROM_LOAD ("901226.01", 0x10000, 0x2000, 0xf833d117);
            ROM_LOAD( "901227.03", 0x12000, 0x2000, 0xdbe3e7c7 );
            ROM_LOAD ("901225.01", 0x14000, 0x1000, 0xec4272ee);
            ROM_END(); 
        }
    };
    
    public static InitMachinePtr k64_init_machine = new InitMachinePtr() { 
        public void handler() 
	{
            basicMem = new UBytePtr(memory_region(REGION_CPU1), 0x10000);
            kernalMem = new UBytePtr(memory_region(REGION_CPU1), 0x12000);
            charMem = new UBytePtr(memory_region(REGION_CPU1), 0x14000);
            
            curr_loc = ( 0xFF & kernalMem.read( 0x1FFC ) ) + ( 0xFF00 & ( kernalMem.read( 0x1FFD ) << 8 ) );

            clearMem();
        }
    };
    
    public static void swapBasic()
    {
        for( int i=0; i<0x2000; i++ )
        {
                int temp =  basicMem.read( i )&0xFF;
                basicMem.write( i, peekMem[ 0xA000 + i ]);
                peekMem[ 0xA000 + i ]  = (byte)temp;
        }

        basicPaged = !basicPaged;
    }
    
    public static void swapKernal()
    {
            for( int i=0; i<0x2000; i++ )
            {
                    int temp =  kernalMem.read( i ) & 0xFF;
                    kernalMem.write( i, peekMem[ 0xE000 + i ]);
                    peekMem[ 0xE000 + i ]  = (byte)temp;
            }

            kernalPaged = !kernalPaged;
    }

    public static void swapChar()
    {
            for( int i=0; i<0x1000; i++ )
            {
                    int temp =  charMem.read( i ) & 0xFF;
                    charMem.write( i, peekMem[ 0xD000 + i ]);
                    peekMem[ 0xD000 + i ]  = (byte)temp;
            }

            charPaged = !charPaged;
    }

    public static void swapHardware()
    {
        for( int i=0; i<0x1000; i++ )
        {
                byte temp =  hardwareMem[ i ];
                hardwareMem[ i ] = peekMem[ 0xD000 + i ];
                peekMem[ 0xD000 + i ]  = temp;
        }

        hardwarePaged = !hardwarePaged;

        if( hardwarePaged )
        {
                regs = peekMem;
                regsOff = 0xD000;
        }
        else
        {
                regs = hardwareMem;
                regsOff = 0;
        }
    }
    
    public static void clearMem()
    {
            if( basicPaged ) swapBasic();
            if( kernalPaged ) swapKernal();
            if( charPaged ) swapChar();
            if( hardwarePaged ) swapHardware();

            for( int i=0; i<65535; i++ )
            {
                    if( ( i & 0x80 ) == 0 ) peekMem[ i ] = (byte)0x99;
                    else peekMem[ i ] = 0x66;
            }

            for( int i=0; i<0x2F; i++ ) vicRegs[ i ] = 0;

            peekMem[ 0 ] = 0x2F;
            peekMem[ 1 ] = 0x37;

            enable1a = enable1b = enable2a = enable2b = vic_den = false;
            vic_ec = vic_b0c = 0;

            for( int i=0; i<0x1000; i++ ) hardwareMem[ i ] = 0;
            hardwareMem[ 0x19 ] = 6;
            for( int i=0x2F; i<0x400; i++ ) hardwareMem[ i ] = -1;
            for( int i=0x041D; i<0x0800; i++ ) hardwareMem[ i ] = -1;
            for( int i=0x0BE8; i<0x0C00; i++ ) hardwareMem[ i ] = -1;
            for( int i=0x0E00; i<0x0FA0; i++ ) hardwareMem[ i ] = -1;

            swapBasic();
            swapKernal();
            swapHardware();
    }

    
    public static InterruptPtr k64_frame_interrupt = new InterruptPtr() { 
        public int handler() 
	{
            return 0xff;
        }
    };
    
    public static InterruptPtr kvic2_raster_irq = new InterruptPtr() {
        public int handler() {
            return 0xff;
        }
    };
    
    public static StopMachinePtr k64_shutdown_machine = new StopMachinePtr() {
        public void handler() {

        }
    };
    
    public static VhStartPtr kvic2_vh_start = new VhStartPtr() {
        public int handler() {
            return 0;
        }
    };
    
    public static VhStopPtr kvic2_vh_stop = new VhStopPtr() {
        public void handler() {
        
        }
    };
    
    public static VhUpdatePtr kvic2_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(mame_bitmap bitmap, int full_refresh) {

        }
    };
    
    static MachineDriver machine_driver_k64 = new MachineDriver
    (
        /* basic machine hardware */
        new MachineCPU[] {
                new MachineCPU(
                        CPU_M6510|CPU_16BIT_PORT,
                        VIC6567_CLOCK,
                        c64_readmem, c64_writemem,
                        null, null,
                        k64_frame_interrupt, 1,
                        kvic2_raster_irq, VIC2_HRETRACERATE
                )
        },
        VIC6567_VRETRACERATE, DEFAULT_REAL_60HZ_VBLANK_DURATION,	/* frames per second, vblank duration */
        0,
        k64_init_machine,
        k64_shutdown_machine,

        /* video hardware */
        336,							   /* screen width */
        216,							   /* screen height */
        new rectangle(0, 336 - 1, 0, 216 - 1),		   /* visible_area */
        null,								   /* graphics decode info */
        vic2_palette.length / 3,
        0,
        c64_init_palette,				   /* convert color prom */
        VIDEO_TYPE_RASTER,
        null,
        kvic2_vh_start,
        kvic2_vh_stop,
        kvic2_vh_screenrefresh,

        /* sound hardware */
        0, 0, 0, 0,
        /*TODO*///new MachineSound[] {
        /*TODO*///	new MachineSound(SOUND_CUSTOM, ntsc_sound_interface ),
        /*TODO*///	new MachineSound(SOUND_DAC, vc20tape_sound_interface)		
        /*TODO*///}

        null
    );
    
    static IODevice io_k64[] =
    {
        new IODevice(IO_END)
    };
    
    public static InitDriverPtr k64_driver_init = new InitDriverPtr() {
        public void handler() {

        }
    };
    
    static InputPortPtr input_ports_k64 = new InputPortPtr(){ 
        public void handler() {
        
        }
    };
    
    public static GameDriver driver_k64 = new GameDriver("1982", "k64", "k64.java", rom_c64, null, machine_driver_k64, input_ports_k64, k64_driver_init, io_k64, "Commodore Business Machines Co.", "Kommodore 64 (Chuso)");
}
