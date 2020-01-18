package mame056.cpu.z80gb;

import static mame056.cpu.z80gb.z80gbH.*;
import mame056.cpuintrfH;
import static mame056.cpuintrfH.*;
import mame056.cpuintrfH.irqcallbacksPtr;
import static mame056.memory.cpu_readmem16;
import static mame056.memory.cpu_setOPbase16;
import static mame056.memory.cpu_writemem16;
import static mame056.memoryH.change_pc16;
import static mess056.machine.gbH.IFLAGS;
import static mess056.machine.gbH.ISWITCH;
import static mess056.machine.gbH.TIMEFRQ;
import static mess056.machine.gbH.TIMEMOD;
import static mess056.machine.gbH.TIM_IFLAG;
import static mess056.machine.gbH.gb_divcount;
import static mess056.machine.gbH.gb_timer_count;
import static mess056.machine.gbH.gb_timer_shift;

public class z80gb extends cpuintrfH.cpu_interface {

    public static int[] z80gb_ICount = new int[1];

    public z80gb() {
        cpu_num = CPU_Z80GB;
        num_irqs = 5;
        default_vector = 255;
        icount = z80gb_ICount;
        overclock = 1.00;
        irq_int = 0;
        databus_width = 8;
        pgm_memory_base = 0;
        address_shift = 0;
        address_bits = 16;
        endianess = CPU_IS_LE;
        align_unit = 1;
        max_inst_len = 4;

    }

    public static class Z80GB_Regs {

        public int PC, SP, A, F, B, C, D, E, H, L;
        public int irq_state;/* irq line state */
        public int enable;
        public irqcallbacksPtr irq_callback;
    }

    public static int FLAG_Z = 0x80;
    public static int FLAG_N = 0x40;
    public static int FLAG_H = 0x20;
    public static int FLAG_C = 0x10;

    static Z80GB_Regs Regs = new Z80GB_Regs();
    public static int /*UINT8*/ ICycles;
    public static int/*UINT8*/ CheckInterrupts;
    public static int IME = 0x01;
    public static int HALTED = 0x02;

    private static int AF() {
        return ((Regs.A << 8) | Regs.F) & 0xFFFF;
    }

    public static int BC() {
        return ((Regs.B << 8) | Regs.C) & 0xFFFF;
    }

    public static int DE() {
        return ((Regs.D << 8) | Regs.E) & 0xFFFF;
    }

    public static int HL() {
        return ((Regs.H << 8) | Regs.L) & 0xFFFF;
    }

    private static void AF(int nn) {
        Regs.A = (nn >> 8) & 0xff;
        Regs.F = nn & 0xff;
    }

    private static void BC(int nn) {
        Regs.B = (nn >> 8) & 0xff;
        Regs.C = nn & 0xff;
    }

    private static void DE(int nn) {
        Regs.D = (nn >> 8) & 0xff;
        Regs.E = nn & 0xff;
    }

    private static void HL(int nn) {
        Regs.H = (nn >> 8) & 0xff;
        Regs.L = nn & 0xff;
    }
    static int Cycles[]
            = {
                4, 12, 8, 8, 4, 4, 8, 4, 20, 8, 8, 8, 4, 4, 8, 4,
                4, 12, 8, 8, 4, 4, 8, 4, 8, 8, 8, 8, 4, 4, 8, 4,
                8, 12, 8, 8, 4, 4, 8, 4, 8, 8, 8, 8, 4, 4, 8, 4,
                8, 12, 8, 8, 12, 12, 12, 4, 8, 8, 8, 8, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                8, 8, 8, 8, 8, 8, 4, 8, 4, 4, 4, 4, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                4, 4, 4, 4, 4, 4, 8, 4, 4, 4, 4, 4, 4, 4, 8, 4,
                8, 12, 12, 12, 12, 16, 8, 16, 8, 8, 12, 0, 12, 24, 8, 16,
                8, 12, 12, 4, 12, 16, 8, 16, 8, 16, 12, 4, 12, 4, 8, 16,
                12, 12, 8, 4, 4, 16, 8, 16, 16, 4, 16, 4, 4, 4, 8, 16,
                12, 12, 8, 4, 4, 16, 8, 16, 12, 8, 16, 4, 4, 4, 8, 16
            };

    static int CyclesCB[]
            = {
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 12, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8,
                8, 8, 8, 8, 8, 8, 16, 8, 8, 8, 8, 8, 8, 8, 16, 8
            };

    @Override
    public void init() {
        //not needed
    }

    @Override
    public void reset(Object param) {
        Regs.PC = 0;
        Regs.SP = 0;
        Regs.A = 0;
        Regs.F = 0;
        Regs.B = 0;
        Regs.C = 0;
        Regs.D = 0;
        Regs.E = 0;
        Regs.H = 0;
        Regs.L = 0;
        Regs.irq_callback = null;
        Regs.enable = 0;
        Regs.irq_state = 0;
        //gb intialaze
        AF(0x01B0);
        BC(0x0013);
        DE(0x00D8);
        HL(0x014D);
        Regs.SP = 0xFFFE;
        Regs.PC = 0x0100;
        Regs.enable &= ~IME;

        CheckInterrupts = 0;
    }

    public static void z80gb_ProcessInterrupts() {

        if (CheckInterrupts != 0 && ((Regs.enable & IME)) != 0) {
            int /*UINT8*/ irq;

            CheckInterrupts = 0;

            irq = (ISWITCH() & IFLAGS()) & 0xFF;

            /*
                            logerror("Attempting to process Z80GB Interrupt IRQ $%02X\n", irq);
                            logerror("Attempting to process Z80GB Interrupt ISWITCH $%02X\n", ISWITCH);
                            logerror("Attempting to process Z80GB Interrupt IFLAGS $%02X\n", IFLAGS);
             */
            if (irq != 0) {
                int irqline = 0;
                /*
                            logerror("Z80GB Interrupt IRQ $%02X\n", irq);
                 */

                while (irqline < 5) {
                    if ((irq & (1 << irqline)) != 0) {
                        if (Regs.irq_callback != null) {
                            (Regs.irq_callback).handler(irqline);
                        }
                        if ((Regs.enable & HALTED) != 0) {
                            Regs.enable &= ~HALTED;
                            Regs.PC = (Regs.PC + 1) & 0xFFFF;
                        }
                        Regs.enable &= ~IME;
                        IFLAGS(IFLAGS() & ~(1 << irqline));
                        ICycles += 20;
                        Regs.SP = (Regs.SP - 2) & 0xFFFF;
                        mem_WriteWord(Regs.SP, Regs.PC);
                        Regs.PC = (0x40 + irqline * 8) & 0xFFFF;
                        /*logerror("Z80GB Interrupt PC $%04X\n", Regs.w.PC );*/
                        return;
                    }
                    irqline++;
                }
            }
        }
    }

    @Override
    public void exit() {
        //no functionality expected
    }

    /**
     * *
     *
     * arcadeflex functions
     */
    @Override
    public Object init_context() {
        Object reg = new Z80GB_Regs();
        return reg;
    }

    @Override
    public void set_op_base(int pc) {
        cpu_setOPbase16.handler(pc);
    }

    @Override
    public int memory_read(int offset) {
        return cpu_readmem16(offset);
    }

    @Override
    public void memory_write(int offset, int data) {
        cpu_writemem16(offset, data);
    }

    @Override
    public int internal_read(int offset) {
        return 0; //doesn't exist in z80 cpu
    }

    @Override
    public void internal_write(int offset, int data) {
        //doesesn't exist in z80 cpu
    }

    @Override
    public int mem_address_bits_of_cpu() {
        return 16;
    }

    @Override
    public Object get_context() {
        Z80GB_Regs r = new Z80GB_Regs();
        r.PC = Regs.PC;
        r.SP = Regs.SP;
        r.A = Regs.A;
        r.F = Regs.F;
        r.B = Regs.B;
        r.C = Regs.C;
        r.D = Regs.D;
        r.E = Regs.E;
        r.H = Regs.H;
        r.L = Regs.L;
        r.irq_state = Regs.irq_state;
        r.enable = Regs.enable;
        r.irq_callback = Regs.irq_callback;
        return r;
    }

    @Override
    public void set_context(Object reg) {
        Z80GB_Regs r = (Z80GB_Regs) reg;
        Regs.PC = r.PC;
        Regs.SP = r.SP;
        Regs.A = r.A;
        Regs.F = r.F;
        Regs.B = r.B;
        Regs.C = r.C;
        Regs.D = r.D;
        Regs.E = r.E;
        Regs.H = r.H;
        Regs.L = r.L;
        Regs.irq_state = r.irq_state;
        Regs.enable = r.enable;
        Regs.irq_callback = r.irq_callback;
        change_pc16(Regs.PC);
    }

    @Override
    public int[] get_cycle_table(int which) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_cycle_table(int which, int[] new_table) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_reg(int regnum) {
        switch (regnum) {
            case Z80GB_PC:
                return Regs.PC & 0xFFFF;
            case Z80GB_SP:
                return Regs.SP & 0xFFFF;
            case Z80GB_AF:
                return AF();
            case Z80GB_BC:
                return BC();
            case Z80GB_DE:
                return DE();
            case Z80GB_HL:
                return HL();
        }
        return 0;
    }

    @Override
    public void set_reg(int regnum, int val) {
        switch (regnum) {
            case Z80GB_PC:
                Regs.PC = val & 0xFFFF;
            case Z80GB_SP:
                Regs.SP = val & 0xFFFF;
            case Z80GB_AF:
                AF(val);
            case Z80GB_BC:
                BC(val);
            case Z80GB_DE:
                DE(val);
            case Z80GB_HL:
                HL(val);
        }
    }

    @Override
    public void set_irq_line(int irqline, int state) {
        /*logerror("setting irq line 0x%02x state 0x%08x\n", irqline, state);*/
        //if( Regs.w.irq_state == state )
        //	return;

        Regs.irq_state = state;
        if (state == ASSERT_LINE) {

            IFLAGS(IFLAGS() | (0x01 << irqline));
            CheckInterrupts = 1;
            /*logerror("Z80GB assert irq line %d ($%02X)\n", irqline, IFLAGS);*/

        } else {

            IFLAGS(IFLAGS() & ~(0x01 << irqline));
            if (IFLAGS() == 0) {
                CheckInterrupts = 0;
            }
            /*logerror("Z80GB clear irq line %d ($%02X)\n", irqline, IFLAGS);*/
        }
    }

    @Override
    public void set_irq_callback(cpuintrfH.irqcallbacksPtr callback) {
        Regs.irq_callback = callback;
    }

    @Override
    public String cpu_info(Object context, int regnum) {
        /*TODO*///	static char buffer[8][47+1];
/*TODO*///	static int which = 0;
/*TODO*///	z80gb_regs *r = context;
/*TODO*///
/*TODO*///	which = (which + 1) % 8;
/*TODO*///    buffer[which][0] = '\0';
/*TODO*///	if( !context )
/*TODO*///		r = &Regs;
/*TODO*///
        switch (regnum) {
            /*TODO*///		case CPU_INFO_REG+Z80GB_PC: sprintf(buffer[which], "PC:%04X", r->w.PC); break;
/*TODO*///		case CPU_INFO_REG+Z80GB_SP: sprintf(buffer[which], "SP:%04X", r->w.SP); break;
/*TODO*///		case CPU_INFO_REG+Z80GB_AF: sprintf(buffer[which], "AF:%04X", r->w.AF); break;
/*TODO*///		case CPU_INFO_REG+Z80GB_BC: sprintf(buffer[which], "BC:%04X", r->w.BC); break;
/*TODO*///		case CPU_INFO_REG+Z80GB_DE: sprintf(buffer[which], "DE:%04X", r->w.DE); break;
/*TODO*///		case CPU_INFO_REG+Z80GB_HL: sprintf(buffer[which], "HL:%04X", r->w.HL); break;
/*TODO*///		case CPU_INFO_REG+Z80GB_IRQ_STATE: sprintf(buffer[which], "IRQ:%X", r->w.irq_state); break;
/*TODO*///        case CPU_INFO_FLAGS:
/*TODO*///			sprintf(buffer[which], "%c%c%c%c%c%c%c%c",
/*TODO*///				r->b.F & 0x80 ? 'Z':'.',
/*TODO*///				r->b.F & 0x40 ? 'N':'.',
/*TODO*///				r->b.F & 0x20 ? 'H':'.',
/*TODO*///				r->b.F & 0x10 ? 'C':'.',
/*TODO*///				r->b.F & 0x08 ? '3':'.',
/*TODO*///				r->b.F & 0x04 ? '2':'.',
/*TODO*///				r->b.F & 0x02 ? '1':'.',
/*TODO*///				r->b.F & 0x01 ? '0':'.');
/*TODO*///			break;
            case CPU_INFO_NAME:
                return "Z80GB";
            case CPU_INFO_FAMILY:
                return "Nintendo Z80";
            case CPU_INFO_VERSION:
                return "1.0";
            case CPU_INFO_FILE:
                return "z80gb.java";
            case CPU_INFO_CREDITS:
                return "Copyright (C) 2000 by The MESS Team.";
            /*TODO*///		case CPU_INFO_REG_LAYOUT: return (const char *)z80gb_reg_layout;
/*TODO*///		case CPU_INFO_WIN_LAYOUT: return (const char *)z80gb_win_layout;
        }
        throw new UnsupportedOperationException("Not supported yet.");
        /*TODO*///	return buffer[which];
    }

    @Override
    public String cpu_dasm(String buffer, int pc) {
        return null;//no functionality
    }

    @Override
    public int execute(int cycles) {
        int/*UINT8*/ x;

        z80gb_ICount[0] = cycles;

        do {
            ICycles = 0;
            z80gb_ProcessInterrupts();
            x = mem_ReadByte(Regs.PC);
            Regs.PC = (Regs.PC + 1) & 0xFFFF;
            ICycles += Cycles[x];
            switch (x) {
                default:
                    System.out.println("0x" + Integer.toHexString(x));
            }
            z80gb_ICount[0] -= ICycles;
            gb_divcount += ICycles;
            if ((TIMEFRQ() & 0x04) != 0) {
                gb_timer_count += ICycles;
                if ((gb_timer_count & (0xFF00 << gb_timer_shift)) != 0) {
                    gb_timer_count = TIMEMOD() << gb_timer_shift;
                    IFLAGS(IFLAGS() | TIM_IFLAG);
                    CheckInterrupts = 1;
                }
            }
        } while (z80gb_ICount[0] > 0);

        return cycles - z80gb_ICount[0];
    }
}
