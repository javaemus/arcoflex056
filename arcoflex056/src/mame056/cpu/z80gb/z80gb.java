package mame056.cpu.z80gb;

import static mame056.cpu.z80gb.daa_tabH.DAATable;
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

    public static int INC_8BIT(int r) {
        r = (r + 1) & 0xFF;
        int f = (Regs.F & FLAG_C) & 0xFF;
        if (r == 0) {
            f |= FLAG_Z;
        }
        if ((r & 0xF) == 0) {
            f |= FLAG_H;
        }
        Regs.F = f & 0xFF;
        return r;
    }

    public static int DEC_8BIT(int r) {
        r = (r - 1) & 0xFF;
        int f = ((Regs.F & FLAG_C) | FLAG_N) & 0xFF;
        if (r == 0) {
            f |= FLAG_Z;
        }
        if ((r & 0xF) == 0) {
            f |= FLAG_H;
        }
        Regs.F = f & 0xFF;
        return r;
    }

    public static void OR_A_X(int value) {
        Regs.A = (Regs.A | value) & 0xff;
        if (Regs.A == 0) {
            Regs.F = FLAG_Z;
        } else {
            Regs.F = 0;
        }
    }

    public static void XOR_A_X(int value) {
        Regs.A = (Regs.A ^ value) & 0xff;
        if (Regs.A == 0) {
            Regs.F = FLAG_Z;
        } else {
            Regs.F = 0;
        }
    }

    public static void AND_A_X(int value) {
        Regs.A = (Regs.A & value) & 0xff;
        if (Regs.A == 0) {
            Regs.F = FLAG_H | FLAG_Z;
        } else {
            Regs.F = FLAG_H;
        }
    }

    public static void CP_A_X(int x) {
        int /*UINT16*/ r1, r2;
        int /*UINT8*/ f;
        r1 = ((Regs.A & 0xF) - ((x) & 0xF)) & 0xFFFF;
        r2 = (Regs.A - (x)) & 0xFFFF;
        if ((r2 & 0xFF) == 0) {
            f = FLAG_N | FLAG_Z;
        } else {
            f = FLAG_N;
        }
        if (r2 > 0xFF) {
            f |= FLAG_C;
        }
        if (r1 > 0xF) {
            f |= FLAG_H;
        }
        Regs.F = f & 0xFF;
    }

    public static void ADD_A_X(int x) {
        int /*UINT16*/ r1, r2;
        int /*UINT8*/ f;
        r1 = ((Regs.A & 0xF) + ((x) & 0xF)) & 0xFFFF;
        r2 = (Regs.A + (x)) & 0xFFFF;
        Regs.A = r2 & 0xFF;
        if ((r2 & 0xFF) == 0) {
            f = FLAG_Z;
        } else {
            f = 0;
        }
        if (r2 > 0xFF) {
            f |= FLAG_C;
        }
        if (r1 > 0xF) {
            f |= FLAG_H;
        }
        Regs.F = f & 0xFF;
    }

    public static void ADC_A_X(int x) {
        int /*UINT16*/ r1, r2;
        int /*UINT8*/ f;
        r1 = ((Regs.A & 0xF) + ((x) & 0xF) + ((Regs.F & FLAG_C) != 0 ? 1 : 0)) & 0xFFFF;
        r2 = (Regs.A + (x) + ((Regs.F & FLAG_C) != 0 ? 1 : 0)) & 0xFFFF;
        if ((Regs.A = (r2 & 0xFF)) == 0) {
            f = FLAG_Z;
        } else {
            f = 0;
        }
        if (r2 > 0xFF) {
            f |= FLAG_C;
        }
        if (r1 > 0xF) {
            f |= FLAG_H;
        }
        Regs.F = f & 0xFF;
    }

    public static void ADD_HL_RR(int x) {
        int /*UINT32*/ r1, r2;
        int /*UINT8*/ f;
        r1 = HL() + (x);
        r2 = (HL() & 0xFFF) + ((x) & 0xFFF);
        f = (Regs.F & FLAG_Z) & 0xFF;
        if (r1 > 0xFFFF) {
            f |= FLAG_C;
        }
        if (r2 > 0x0FFF) {
            f |= FLAG_H;
        }
        HL(r1 & 0xFFFF);
        Regs.F = f & 0xFF;
    }

    public static void SUB_A_X(int x) {
        int /*UINT16*/ r1, r2;
        int /*UINT8*/ f;
        r1 = ((Regs.A & 0xF) - ((x) & 0xF)) & 0xFFFF;
        r2 = (Regs.A - (x)) & 0xFFFF;
        Regs.A = r2 & 0xFF;
        if ((r2 & 0xFF) == 0) {
            f = FLAG_N | FLAG_Z;
        } else {
            f = FLAG_N;
        }
        if (r2 > 0xFF) {
            f |= FLAG_C;
        }
        if (r1 > 0xF) {
            f |= FLAG_H;
        }
        Regs.F = f & 0xFF;
    }

    public static int SRL_8BIT(int x) {
        int f;
        if (((x) & 1) != 0) {
            f = FLAG_C;
        } else {
            f = 0;
        }
        x = x >> 1 & 0xFF;
        if ((x) == 0) {
            f |= FLAG_Z;
        }
        Regs.F = f & 0xFF;
        return x;
    }

    public static int RR_8BIT(int x) {
        int /*UINT8*/ r;
        r = ((x) & 1) != 0 ? FLAG_C : 0;
        (x) = (((x) >> 1) | ((Regs.F & FLAG_C) != 0 ? 0x80 : 0)) & 0xFF;
        if ((x) == 0) {
            r |= FLAG_Z;
        }
        Regs.F = r & 0xFF;
        return x;
    }

    public static int SWAP_8BIT(int x) {
        (x) = (((x) >> 4) | ((x) << 4)) & 0xFF;
        if ((x) == 0) {
            Regs.F = FLAG_Z;
        } else {
            Regs.F = 0;
        }
        return x;
    }
    private static final int[] bitSet = {1, 2, 4, 8, 16, 32, 64, 128};           // lookup table for setting a bit of an 8-bit value using OR
    private static final int[] bitRes = {254, 253, 251, 247, 239, 223, 191, 127}; // lookup table for resetting a bit of an 8-bit value using AND

    public static int RES(int bitNumber, int value) {
        value = value & bitRes[bitNumber];
        return value;
    }

    public static int SET(int bitNumber, int value) {
        value = value | bitSet[bitNumber];
        return value;
    }

    public static int SLA_8BIT(int x) {
        int f;
        if (((x) & 0x80) != 0) {
            f = FLAG_C;
        } else {
            f = 0;
        }
        x = x << 1 & 0xFF;
        if ((x) == 0) {
            f |= FLAG_Z;
        }
        Regs.F = f & 0xFF;
        return x;
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
                case 0x00:
                    break;

                case 0x01:
                    BC(mem_ReadWord(Regs.PC));/*	   LD BC,n16 */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0x02:
                    mem_WriteByte(BC(), Regs.A);/*	   LD (BC),A */
                    break;
                case 0x03:
                    BC((BC() + 1) & 0xFFFF);/*	   INC BC */
                    break;
                case 0x04:
                    Regs.B = INC_8BIT(Regs.B);/*	   INC B */
                    break;
                case 0x05:
                    Regs.B = DEC_8BIT(Regs.B);/*	   DEC B */
                    break;
                case 0x06:
                    Regs.B = mem_ReadByte(Regs.PC);/*	   LD B,n8 */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                /*TODO*///case 0x07: /*	   RLCA */
/*TODO*///
/*TODO*///  Regs.b.A = (UINT8) ((Regs.b.A << 1) | (Regs.b.A >> 7));
/*TODO*///  if (Regs.b.A & 1)
/*TODO*///  {
/*TODO*///    Regs.b.F = FLAG_C;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.b.F = 0;
/*TODO*///  }
/*TODO*///  break;
                case 0x08:
                    mem_WriteWord(mem_ReadWord(Regs.PC), Regs.SP);/*	   LD (n16),SP */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0x09:
                    /*	   ADD HL,BC */
                    ADD_HL_RR(BC());
                    break;
                case 0x0A:
                    Regs.A = mem_ReadByte(BC());/*	   LD A,(BC) */
                    break;
                case 0x0B:
                    BC((BC() - 1) & 0xFFFF);/*	   DEC BC */
                    break;
                case 0x0C:
                    Regs.C = INC_8BIT(Regs.C);/*	   INC C */
                    break;
                case 0x0D:
                    Regs.C = DEC_8BIT(Regs.C);/*	   DEC C */
                    break;
                case 0x0E:
                    Regs.C = mem_ReadByte(Regs.PC);/*	   LD C,n8 */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                /*TODO*///case 0x0F: /*	   RRCA */
/*TODO*///
/*TODO*///  Regs.b.A = (UINT8) ((Regs.b.A >> 1) | (Regs.b.A << 7));
/*TODO*///  if (Regs.b.A & 0x80)
/*TODO*///  {
/*TODO*///    Regs.b.F |= FLAG_C;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.b.F = 0;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0x10: /*	   STOP */
/*TODO*///  break;
                case 0x11:
                    DE(mem_ReadWord(Regs.PC));/*	   LD DE,n16 */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0x12:
                    mem_WriteByte(DE(), Regs.A);/*	   LD (DE),A */
                    break;
                case 0x13:
                    DE((DE() + 1) & 0xFFFF);/*	   INC DE */
                    break;
                case 0x14:
                    Regs.D = INC_8BIT(Regs.D);/*	   INC D */
                    break;
                case 0x15:
                    /*	   DEC D */
                    Regs.D = DEC_8BIT(Regs.D);
                    break;
                case 0x16:
                    /*	   LD D,n8 */
                    Regs.D = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                /*TODO*///case 0x17: /*	   RLA */
/*TODO*///  
/*TODO*///  x = (Regs.b.A & 0x80) ? FLAG_C : 0;
/*TODO*///
/*TODO*///  Regs.b.A = (UINT8) ((Regs.b.A << 1) | ((Regs.b.F & FLAG_C) ? 1 : 0));
/*TODO*///  Regs.b.F = x;
/*TODO*///  break;
                case 0x18: /*	   JR	   n8 */ {
                    byte offset;
                    offset = (byte) mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    Regs.PC = (Regs.PC + offset) & 0xFFFF;
                }
                break;
                case 0x19:
                    /*	   ADD HL,DE */
                    ADD_HL_RR(DE());
                    break;
                case 0x1A:
                    Regs.A = mem_ReadByte(DE());/*	   LD A,(DE) */
                    break;
                /*TODO*///case 0x1B: /*	   DEC DE */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.D == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  Regs.w.DE -= 1;
/*TODO*///  break;
                case 0x1C:
                    Regs.E = INC_8BIT(Regs.E);/*	   INC E */
                    break;
                case 0x1D:
                    Regs.E = DEC_8BIT(Regs.E);/*	   DEC E */
                    break;
                /*TODO*///case 0x1E: /*	   LD E,n8 */
/*TODO*///
/*TODO*///  Regs.b.E = mem_ReadByte (Regs.w.PC++);
/*TODO*///  break;
                case 0x1F: /*	   RRA */ {
                    int x1 = (Regs.A & 1) != 0 ? FLAG_C : 0;
                    Regs.A = ((Regs.A >> 1) | ((Regs.F & FLAG_C) != 0 ? 0x80 : 0)) & 0xFF;
                    Regs.F = x1;
                }
                break;
                case 0x20:
                    if ((Regs.F & FLAG_Z) != 0) {/*	   JR NZ,n8 */
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    } else {
                        byte offset = (byte) mem_ReadByte(Regs.PC);
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                        Regs.PC = (Regs.PC + offset) & 0xFFFF;
                        ICycles += 4;
                    }
                    break;
                case 0x21:
                    HL(mem_ReadWord(Regs.PC));/*	   LD HL,n16 */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0x22:
                    mem_WriteByte(HL(), Regs.A);/*	   LD (HL+),A */
                    HL((HL() + 1) & 0xFFFF);
                    break;
                case 0x23:
                    HL((HL() + 1) & 0xFFFF);/*	   INC HL */
                    break;
                case 0x24:
                    Regs.H = INC_8BIT(Regs.H);/*	   INC H */
                    break;
                case 0x25:
                    Regs.H = DEC_8BIT(Regs.H);/*	   DEC H */
                    break;
                case 0x26:
                    Regs.H = mem_ReadByte(Regs.PC);/*	   LD H,n8 */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                case 0x27:
                    /*	   DAA */
                    AF(DAATable[(((Regs.F & (FLAG_N | FLAG_C | FLAG_H)) & 0xFFFF) << 4) | Regs.A]);
                    break;
                case 0x28:
                    if ((Regs.F & FLAG_Z) != 0) /*	   JR Z,n8 */ {
                        byte offset;

                        offset = (byte) mem_ReadByte(Regs.PC);
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                        Regs.PC = (Regs.PC + offset) & 0xFFFF;

                        ICycles += 4;
                    } else {
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    }
                    break;
                case 0x29:
                    ADD_HL_RR(HL());/*	   ADD HL,HL */
                    break;
                case 0x2A:
                    Regs.A = mem_ReadByte(HL());/*	   LD A,(HL+) */
                    HL((HL() + 1) & 0xFFFF);
                    break;
                /*TODO*///case 0x2B: /*	   DEC HL */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.H == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  Regs.w.HL -= 1;
/*TODO*///  break;
                case 0x2C:
                    Regs.L = INC_8BIT(Regs.L);/*	   INC L */
                    break;
                case 0x2D:
                    Regs.L = DEC_8BIT(Regs.L);/*	   DEC L */
                    break;
                case 0x2E:
                    /*	   LD L,n8 */
                    Regs.L = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                case 0x2F:
                    /*	   CPL */
                    Regs.A = (~Regs.A) & 0xFF;
                    Regs.F |= FLAG_N | FLAG_H;
                    //return 4;
                    break;
                case 0x30:
                    if ((Regs.F & FLAG_C) != 0)/*	   JR NC,n8 */ {
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    } else {
                        byte offset;
                        offset = (byte) mem_ReadByte(Regs.PC);
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                        Regs.PC = (Regs.PC + offset) & 0xFFFF;
                        ICycles += 4;
                    }
                    break;
                case 0x31:
                    Regs.SP = mem_ReadWord(Regs.PC);/*	   LD SP,n16 */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0x32:
                    mem_WriteByte(HL(), Regs.A);/*	   LD (HL-),A */
                    HL((HL() - 1) & 0xFFFF);
                    break;
                case 0x33:
                    /*	   INC SP */
                    Regs.SP = (Regs.SP + 1) & 0xFFFF;
                    break;
                case 0x34: /*	   INC (HL) */ {
                    int r, f;
                    f = (Regs.F & FLAG_C) & 0xFF;
                    r = (mem_ReadByte(HL()) + 1) & 0xFF;
                    mem_WriteByte(HL(), r);

                    if (r == 0) {
                        f |= FLAG_Z;
                    }

                    if ((r & 0xF) == 0) {
                        f |= FLAG_H;
                    }

                    Regs.F = f & 0xFF;
                }
                break;
                case 0x35: /*	   DEC (HL) */ {
                    int /*UINT8*/ r, f;
                    f = ((Regs.F & FLAG_C) | FLAG_N) & 0xFF;
                    r = (mem_ReadByte(HL()) - 1) & 0xFF;
                    mem_WriteByte(HL(), r);
                    if (r == 0) {
                        f |= FLAG_Z;
                    }

                    if ((r & 0xF) != 0xF) {
                        f |= FLAG_H;
                    }

                    Regs.F = f & 0xFF;
                }
                break;
                case 0x36:
                    /*	   LD (HL),n8 */
 /* FIXED / broken ? */
                    mem_WriteByte(HL(), mem_ReadByte(Regs.PC));
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                case 0x37:
                    /*	   SCF */

                    Regs.F = ((Regs.F & FLAG_Z) | FLAG_C) & 0xFF;
                    break;
                case 0x38:
                    /*	   JR C,n8 */

                    if ((Regs.F & FLAG_C) != 0) {
                        byte offset;

                        offset = (byte) mem_ReadByte(Regs.PC);
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                        Regs.PC = (Regs.PC + offset) & 0xFFFF;

                        ICycles += 4;
                    } else {
                        Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    }
                    break;
                case 0x39:
                    /*	   ADD HL,SP */
                    ADD_HL_RR(Regs.SP);
                    break;
                case 0x3A:
                    /*	   LD A,(HL-) */
                    Regs.A = mem_ReadByte(HL());
                    HL((HL() - 1) & 0xFFFF);
                    break;
                case 0x3B:
                    /*	   DEC SP */
                    Regs.SP = (Regs.SP - 1) & 0xFFFF;
                    break;
                case 0x3C:
                    Regs.A = INC_8BIT(Regs.A);/*	   INC	   A */
                    break;
                case 0x3D:
                    Regs.A = DEC_8BIT(Regs.A);/*	   DEC	   A */
                    break;
                case 0x3E:
                    Regs.A = mem_ReadByte(Regs.PC);/*	   LD A,n8 */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                /*TODO*///case 0x3F: /*	   CCF */
/*TODO*///
/*TODO*///  Regs.b.F = (UINT8) ((Regs.b.F & FLAG_Z) | ((Regs.b.F & FLAG_C) ? 0 : FLAG_C));
/*TODO*///  break;
                case 0x40:
                    /*	   LD B,B */
                    break;
                case 0x41:
                    /*	   LD B,C */
                    Regs.B = Regs.C;
                    break;
                case 0x42:
                    /*	   LD B,D */
                    Regs.B = Regs.D;
                    break;
                case 0x43:
                    /*	   LD B,E */
                    Regs.B = Regs.E;
                    break;
                case 0x44:
                    /*	   LD B,H */
                    Regs.B = Regs.H;
                    break;
                case 0x45:
                    /*	   LD B,L */
                    Regs.B = Regs.L;
                    break;
                case 0x46:
                    Regs.B = mem_ReadByte(HL());/*	   LD B,(HL) */
                    break;
                case 0x47:
                    Regs.B = Regs.A;/*	   LD B,A */
                    break;
                case 0x48:
                    /*	   LD C,B */
                    Regs.C = Regs.B;
                    break;
                case 0x49:
                    /*	   LD C,C */
                    break;
                case 0x4A:
                    /*	   LD C,D */
                    Regs.C = Regs.D;
                    break;
                case 0x4B:
                    /*	   LD C,E */
                    Regs.C = Regs.E;
                    break;
                case 0x4C:
                    /*	   LD C,H */
                    Regs.C = Regs.H;
                    break;
                case 0x4D:
                    /*	   LD C,L */
                    Regs.C = Regs.L;
                    break;
                case 0x4E:
                    Regs.C = mem_ReadByte(HL());/*	   LD C,(HL) */
                    break;
                case 0x4F:
                    /*	   LD C,A */
                    Regs.C = Regs.A;
                    break;
                case 0x50:
                    /*	   LD D,B */
                    Regs.D = Regs.B;
                    break;
                case 0x51:
                    /*	   LD D,C */
                    Regs.D = Regs.C;
                    break;
                case 0x52:
                    /*	   LD D,D */
                    break;
                case 0x53:
                    /*	   LD D,E */
                    Regs.D = Regs.E;
                    break;
                case 0x54:
                    /*	   LD D,H */
                    Regs.D = Regs.H;
                    break;
                case 0x55:
                    /*	   LD D,L */
                    Regs.D = Regs.L;
                    break;
                case 0x56:
                    Regs.D = mem_ReadByte(HL());/*	   LD D,(HL) */
                    break;
                case 0x57:
                    /*	   LD D,A */
                    Regs.D = Regs.A;
                    break;
                case 0x58:
                    /*	   LD E,B */
                    Regs.E = Regs.B;
                    break;
                case 0x59:
                    /*	   LD E,C */
                    Regs.E = Regs.C;
                    break;
                case 0x5A:
                    /*	   LD E,D */
                    Regs.E = Regs.D;
                    break;
                case 0x5B:
                    /*	   LD E,E */
                    break;
                case 0x5C:
                    /*	   LD E,H */
                    Regs.E = Regs.H;
                    break;
                case 0x5D:
                    /*	   LD E,L */
                    Regs.E = Regs.L;
                    break;
                case 0x5E:
                    Regs.E = mem_ReadByte(HL());/*	   LD E,(HL) */
                    break;
                case 0x5F:
                    /*	   LD E,A */
                    Regs.E = Regs.A;
                    break;
                case 0x60:
                    /*	   LD H,B */
                    Regs.H = Regs.B;
                    break;
                case 0x61:
                    /*	   LD H,C */
                    Regs.H = Regs.C;
                    break;
                case 0x62:
                    /*	   LD H,D */
                    Regs.H = Regs.D;
                    break;
                case 0x63:
                    /*	   LD H,E */
                    Regs.H = Regs.E;
                    break;
                case 0x64:
                    /*	   LD H,H */
                    break;
                case 0x65:
                    /*	   LD H,L */
                    Regs.H = Regs.L;
                    break;
                case 0x66:
                    Regs.H = mem_ReadByte(HL());/*	   LD H,(HL) */
                    break;
                case 0x67:
                    /*	   LD H,A */
                    Regs.H = Regs.A;
                    break;
                case 0x68:
                    /*	   LD L,B */
                    Regs.L = Regs.B;
                    break;
                case 0x69:
                    /*	   LD L,C */
                    Regs.L = Regs.C;
                    break;
                case 0x6A:
                    /*	   LD L,D */
                    Regs.L = Regs.D;
                    break;
                case 0x6B:
                    /*	   LD L,E */
                    Regs.L = Regs.E;
                    break;
                case 0x6C:
                    /*	   LD L,H */
                    Regs.L = Regs.H;
                    break;
                case 0x6D:
                    /*	   LD L,L */
                    break;
                case 0x6E:
                    /*	   LD L,(HL) */
                    Regs.L = mem_ReadByte(HL());
                    break;
                case 0x6F:
                    /*	   LD L,A */
                    Regs.L = Regs.A;
                    break;
                case 0x70:
                    /*	   LD (HL),B */
                    mem_WriteByte(HL(), Regs.B);
                    break;
                case 0x71:
                    /*	   LD (HL),C */
                    mem_WriteByte(HL(), Regs.C);
                    break;
                case 0x72:
                    /*	   LD (HL),D */
                    mem_WriteByte(HL(), Regs.D);
                    break;
                case 0x73:
                    /*	   LD (HL),E */
                    mem_WriteByte(HL(), Regs.E);
                    break;
                case 0x74:
                    /*	   LD (HL),H */
                    mem_WriteByte(HL(), Regs.H);
                    break;
                case 0x75:
                    /*	   LD (HL),L */
                    mem_WriteByte(HL(), Regs.L);
                    break;
                /*TODO*///case 0x76: /*	   HALT */
/*TODO*///  {
/*TODO*///	UINT32 skip_cycles;
/*TODO*///	Regs.w.enable |= HALTED;
/*TODO*///    CheckInterrupts = 1;
/*TODO*///    Regs.w.PC--;
/*TODO*///    
/*TODO*///    /* Calculate nr of cycles which can be skipped */
/*TODO*///	skip_cycles = (0x100 << gb_timer_shift) - gb_timer_count;
/*TODO*///	if (skip_cycles > z80gb_ICount) skip_cycles = z80gb_ICount;
/*TODO*///    
/*TODO*///    /* round cycles to multiple of 4 always round upwards */
/*TODO*///	skip_cycles = (skip_cycles+3) & ~3;
/*TODO*///	if (skip_cycles > ICycles) ICycles += skip_cycles - ICycles;
/*TODO*///  }
/*TODO*///  break;
                case 0x77:
                    mem_WriteByte(HL(), Regs.A);/*	   LD (HL),A */
                    break;
                case 0x78:
                    Regs.A = Regs.B;/*	   LD A,B */
                    break;
                case 0x79:
                    Regs.A = Regs.C;/*	   LD A,C */
                    break;
                case 0x7A:
                    Regs.A = Regs.D;/*	   LD A,D */
                    break;
                case 0x7B:
                    Regs.A = Regs.E;/*	   LD A,E */
                    break;
                case 0x7C:
                    Regs.A = Regs.H;/*	   LD A,H */
                    break;
                case 0x7D:
                    Regs.A = Regs.L;/*	   LD A,L */
                    break;
                case 0x7E:
                    /*	   LD A,(HL) */
                    Regs.A = mem_ReadByte(HL());
                    break;
                case 0x7F:
                    /*	   LD A,A */
                    break;
                case 0x80:
                    /*	   ADD A,B */
                    ADD_A_X(Regs.B);
                    break;
                case 0x81:
                    /*	   ADD A,C */
                    ADD_A_X(Regs.C);
                    break;
                case 0x82:
                    /*	   ADD A,D */
                    ADD_A_X(Regs.D);
                    break;
                case 0x83:
                    /*	   ADD A,E */
                    ADD_A_X(Regs.E);
                    break;
                case 0x84:
                    /*	   ADD A,H */
                    ADD_A_X(Regs.H);
                    break;
                case 0x85:
                    /*	   ADD A,L */
                    ADD_A_X(Regs.L);
                    break;
                case 0x86: /*	   ADD A,(HL) */ {
                    int x1 = mem_ReadByte(HL());
                    ADD_A_X(x1);
                }
                break;
                case 0x87:
                    /*	   ADD A,A */
                    ADD_A_X(Regs.A);
                    break;
                case 0x88:
                    /*	   ADC A,B */
                    ADC_A_X(Regs.B);
                    break;
                case 0x89:
                    /*	   ADC A,C */
                    ADC_A_X(Regs.C);
                    break;
                case 0x8A:
                    /*	   ADC A,D */
                    ADC_A_X(Regs.D);
                    break;
                case 0x8B:
                    /*	   ADC A,E */
                    ADC_A_X(Regs.E);
                    break;
                case 0x8C:
                    /*	   ADC A,H */
                    ADC_A_X(Regs.H);
                    break;
                case 0x8D:
                    /*	   ADC A,L */
                    ADC_A_X(Regs.L);
                    break;
                case 0x8E: /*	   ADC A,(HL) */ {
                    int x1 = mem_ReadByte(HL());
                    ADC_A_X(x1);
                }
                break;
                case 0x8F:
                    /*	   ADC A,A */
                    ADC_A_X(Regs.A);
                    break;
                case 0x90:
                    /*	   SUB A,B */
                    SUB_A_X(Regs.B);
                    break;
                case 0x91:
                    /*	   SUB A,C */
                    SUB_A_X(Regs.C);
                    break;
                case 0x92:
                    /*	   SUB A,D */
                    SUB_A_X(Regs.D);
                    break;
                case 0x93:
                    /*	   SUB A,E */
                    SUB_A_X(Regs.E);
                    break;
                case 0x94:
                    /*	   SUB A,H */
                    SUB_A_X(Regs.H);
                    break;
                case 0x95:
                    /*	   SUB A,L */
                    SUB_A_X(Regs.L);
                    break;
                case 0x96: /*	   SUB A,(HL) */ {
                    int x1 = mem_ReadByte(HL());
                    SUB_A_X(x1);
                }
                break;
                case 0x97:
                    /*	   SUB A,A */
                    SUB_A_X(Regs.A);
                    break;
                /*TODO*///case 0x98: /*	   SBC A,B */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x99: /*	   SBC A,C */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x9A: /*	   SBC A,D */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x9B: /*	   SBC A,E */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x9C: /*	   SBC A,H */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x9D: /*	   SBC A,L */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x9E: /*	   SBC A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  SBC_A_X (x)
/*TODO*///  break;
/*TODO*///case 0x9F: /*	   SBC A,A */
/*TODO*///
/*TODO*///  SBC_A_X (Regs.b.A)
/*TODO*///  break;
                case 0xA0:
                    /*	   AND A,B */
                    AND_A_X(Regs.B);
                    break;
                case 0xA1:
                    /*	   AND A,C */
                    AND_A_X(Regs.C);
                    break;
                case 0xA2:
                    /*	   AND A,D */
                    AND_A_X(Regs.D);
                    break;
                case 0xA3:
                    /*	   AND A,E */
                    AND_A_X(Regs.E);
                    break;
                case 0xA4:
                    /*	   AND A,H */
                    AND_A_X(Regs.H);
                    break;
                case 0xA5:
                    /*	   AND A,L */
                    AND_A_X(Regs.L);
                    break;
                case 0xA6: /*	   AND A,(HL) */ {
                    int x1 = mem_ReadByte(HL());

                    AND_A_X(x1);
                }
                break;
                case 0xA7:
                    /*	   AND A,A */
                    Regs.F = (Regs.A == 0) ? (FLAG_H | FLAG_Z) : FLAG_H;
                    break;
                case 0xA8:
                    XOR_A_X(Regs.B);/*	   XOR A,B */
                    break;
                case 0xA9:
                    XOR_A_X(Regs.C);/*	   XOR A,C */
                    break;
                case 0xAA:
                    XOR_A_X(Regs.D);/*	   XOR A,D */
                    break;
                case 0xAB:
                    XOR_A_X(Regs.E);/*	   XOR A,E */
                    break;
                case 0xAC:
                    XOR_A_X(Regs.H);/*	   XOR A,H */
                    break;
                case 0xAD:
                    XOR_A_X(Regs.L);/*	   XOR A,L */
                    break;
                case 0xAE: /*	   XOR A,(HL) */ {
                    int x1 = mem_ReadByte(HL());
                    XOR_A_X(x1);
                }
                break;
                case 0xAF:
                    XOR_A_X(Regs.A);/*	   XOR A,A */
                    break;
                case 0xB0:
                    OR_A_X(Regs.B);/*	   OR A,B */
                    break;
                case 0xB1:
                    OR_A_X(Regs.C);/*	   OR A,C */
                    break;
                case 0xB2:
                    OR_A_X(Regs.D);/*	   OR A,D */
                    break;
                case 0xB3:
                    OR_A_X(Regs.E);/*	   OR A,E */
                    break;
                case 0xB4:
                    OR_A_X(Regs.H);/*	   OR A,H */
                    break;
                case 0xB5:
                    OR_A_X(Regs.L);/*	   OR A,L */
                    break;
                case 0xB6: /*	   OR A,(HL) */ {
                    int x1 = mem_ReadByte(HL());
                    OR_A_X(x1);
                }
                break;
                case 0xB7:
                    OR_A_X(Regs.A);/*	   OR A,A */
                    break;
                case 0xB8:
                    /*	   CP A,B */
                    CP_A_X(Regs.B);
                    break;
                case 0xB9:
                    /*	   CP A,C */
                    CP_A_X(Regs.C);
                    break;
                case 0xBA:
                    /*	   CP A,D */
                    CP_A_X(Regs.D);
                    break;
                case 0xBB:
                    /*	   CP A,E */
                    CP_A_X(Regs.E);
                    break;
                case 0xBC:
                    /*	   CP A,H */
                    CP_A_X(Regs.H);
                    break;
                case 0xBD:
                    /*	   CP A,L */
                    CP_A_X(Regs.L);
                    break;
                case 0xBE: /*	   CP A,(HL) */ {
                    int x1 = mem_ReadByte(HL());
                    CP_A_X(x1);
                }
                break;
                case 0xBF:
                    /*	   CP A,A */
                    CP_A_X(Regs.A);
                    break;
                case 0xC0:
                    /*	   RET NZ */
                    if ((Regs.F & FLAG_Z) == 0) {
                        Regs.PC = mem_ReadWord(Regs.SP);
                        Regs.SP = (Regs.SP + 2) & 0xFFFF;
                        ICycles += 12;
                    }
                    break;
                case 0xC1:
                    BC(mem_ReadWord(Regs.SP));/*	   POP BC */
                    Regs.SP = (Regs.SP + 2) & 0xFFFF;
                    break;
                case 0xC2:
                    /*	   JP NZ,n16 */
                    if ((Regs.F & FLAG_Z) != 0) {
                        Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    } else {
                        Regs.PC = mem_ReadWord(Regs.PC);
                        ICycles += 4;
                    }
                    break;
                case 0xC3:
                    Regs.PC = mem_ReadWord(Regs.PC);/*	   JP n16 */
                    break;
                case 0xC4:
                    if ((Regs.F & FLAG_Z) != 0)/*	   CALL NZ,n16 */ {
                        Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    } else {
                        int PC;
                        PC = mem_ReadWord(Regs.PC);
                        Regs.PC = (Regs.PC + 2) & 0xFFFF;

                        Regs.SP = (Regs.SP - 2) & 0xFFFF;
                        mem_WriteWord(Regs.SP, Regs.PC);
                        Regs.PC = PC & 0xFFFF;
                        ICycles += 12;
                    }
                    break;
                case 0xC5:
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;/*	   PUSH BC */
                    mem_WriteWord(Regs.SP, BC());
                    break;
                case 0xC6: /*	   ADD A,n8 */ {
                    int x1 = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    ADD_A_X(x1);
                }
                break;
                case 0xC7: /*	   RST 0 */ {
                    int PC;
                    PC = Regs.PC;
                    Regs.PC = 0;
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, PC);
                }
                break;
                case 0xC8:
                    /*	   RET Z */
                    if ((Regs.F & FLAG_Z) != 0) {
                        Regs.PC = mem_ReadWord(Regs.SP);
                        Regs.SP = (Regs.SP + 2) & 0xFFFF;
                        ICycles += 12;
                    }
                    break;
                case 0xC9:
                    Regs.PC = mem_ReadWord(Regs.SP);/*	   RET */
                    Regs.SP = (Regs.SP + 2) & 0xFFFF;
                    break;
                case 0xCA:
                    /*	   JP Z,n16 */
                    if ((Regs.F & FLAG_Z) != 0) {
                        Regs.PC = mem_ReadWord(Regs.PC);
                        ICycles += 4;
                    } else {
                        Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    }
                    break;
                case 0xCB:
                    x = mem_ReadByte(Regs.PC);/*	   PREFIX! */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    ICycles += CyclesCB[x];
                    switch (x) {
                        /*TODO*///case 0x00:
/*TODO*///  /*      RLC B */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x01:
/*TODO*///  /*      RLC C */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x02:
/*TODO*///  /*      RLC D */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x03:
/*TODO*///  /*      RLC E */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x04:
/*TODO*///  /*      RLC H */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x05:
/*TODO*///  /*      RLC L */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x06:
/*TODO*///  /*      RLC (HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RLC_8BIT (x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x07:
/*TODO*///  /*      RLC A */
/*TODO*///
/*TODO*///  RLC_8BIT (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x08:
/*TODO*///  /*      RRC B */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x09:
/*TODO*///  /*      RRC C */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x0A:
/*TODO*///  /*      RRC D */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x0B:
/*TODO*///  /*      RRC E */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x0C:
/*TODO*///  /*      RRC H */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x0D:
/*TODO*///  /*      RRC L */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x0E:
/*TODO*///  /*      RRC (HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RRC_8BIT (x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x0F:
/*TODO*///  /*      RRC A */
/*TODO*///
/*TODO*///  RRC_8BIT (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x10:
/*TODO*///  /*      RL B */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x11:
/*TODO*///  /*      RL C */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x12:
/*TODO*///  /*      RL D */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x13:
/*TODO*///  /*      RL E */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x14:
/*TODO*///  /*      RL H */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x15:
/*TODO*///  /*      RL L */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x16:
/*TODO*///  /*      RL (HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RL_8BIT (x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x17:
/*TODO*///  /*      RL A */
/*TODO*///
/*TODO*///  RL_8BIT (Regs.b.A)
/*TODO*///  break;
                        case 0x18:
                            /*      RR B */
                            RR_8BIT(Regs.B);
                            break;
                        case 0x19:
                            Regs.C = RR_8BIT(Regs.C);/*      RR C */
                            break;
                        case 0x1A:
                            Regs.D = RR_8BIT(Regs.D);/*      RR D */
                            break;
                        case 0x1B:
                            Regs.E = RR_8BIT(Regs.E);/*      RR E */
                            break;
                        case 0x1C:
                            /*      RR H */
                            RR_8BIT(Regs.H);
                            break;
                        case 0x1D:
                            /*      RR L */
                            RR_8BIT(Regs.L);
                            break;
                        case 0x1E: /*      RR (HL) */ {
                            int x1 = mem_ReadByte(HL());
                            RR_8BIT(x1);
                            mem_WriteByte(HL(), x);
                        }
                        break;
                        case 0x1F:
                            /*      RR A */
                            RR_8BIT(Regs.A);
                            break;
                        /*TODO*///case 0x20:
/*TODO*///  /*      SLA B */
/*TODO*///
/*TODO*///  SLA_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x21:
/*TODO*///  /*      SLA C */
/*TODO*///
/*TODO*///  SLA_8BIT (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x22:
/*TODO*///  /*      SLA D */
/*TODO*///
/*TODO*///  SLA_8BIT (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x23:
/*TODO*///  /*      SLA E */
/*TODO*///
/*TODO*///  SLA_8BIT (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x24:
/*TODO*///  /*      SLA H */
/*TODO*///
/*TODO*///  SLA_8BIT (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x25:
/*TODO*///  /*      SLA L */
/*TODO*///
/*TODO*///  SLA_8BIT (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x26:
/*TODO*///  /*      SLA (HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SLA_8BIT (x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
                        case 0x27:
                            /*      SLA A */
                            Regs.A = SLA_8BIT(Regs.A);
                            break;
                        /*TODO*///case 0x28:
/*TODO*///  /*      SRA B */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x29:
/*TODO*///  /*      SRA C */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x2A:
/*TODO*///  /*      SRA D */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x2B:
/*TODO*///  /*      SRA E */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x2C:
/*TODO*///  /*      SRA H */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x2D:
/*TODO*///  /*      SRA L */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x2E:
/*TODO*///  /*      SRA (HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SRA_8BIT (x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x2F:
/*TODO*///  /*      SRA A */
/*TODO*///
/*TODO*///  SRA_8BIT (Regs.b.A)
/*TODO*///  break;
                        case 0x30:
                            /*      SWAP B */

                            SWAP_8BIT(Regs.B);
                            break;
                        case 0x31:
                            /*      SWAP C */

                            SWAP_8BIT(Regs.C);
                            break;
                        case 0x32:
                            /*      SWAP D */

                            SWAP_8BIT(Regs.D);
                            break;
                        case 0x33:
                            /*      SWAP E */

                            SWAP_8BIT(Regs.E);
                            break;
                        case 0x34:
                            /*      SWAP H */

                            SWAP_8BIT(Regs.H);
                            break;
                        case 0x35:
                            /*      SWAP L */
                            SWAP_8BIT(Regs.L);
                            break;
                        case 0x36: /*      SWAP (HL) */ {
                            int x1 = mem_ReadByte(HL());
                            SWAP_8BIT(x1);
                            mem_WriteByte(HL(), x);
                        }
                        break;
                        case 0x37:
                            /*      SWAP A */
                            Regs.A = SWAP_8BIT(Regs.A);
                            break;
                        case 0x38:
                            Regs.B = SRL_8BIT(Regs.B);/*      SRL B */
                            break;
                        case 0x39:
                            /*      SRL C */

                            SRL_8BIT(Regs.C);
                            break;
                        case 0x3A:
                            /*      SRL D */

                            SRL_8BIT(Regs.D);
                            break;
                        case 0x3B:
                            /*      SRL E */

                            SRL_8BIT(Regs.E);
                            break;
                        case 0x3C:
                            /*      SRL H */

                            SRL_8BIT(Regs.H);
                            break;
                        case 0x3D:
                            /*      SRL L */

                            SRL_8BIT(Regs.L);
                            break;
                        case 0x3E: /*      SRL (HL) */ {

                            int x1 = mem_ReadByte(HL());
                            SRL_8BIT(x1);
                            mem_WriteByte(HL(), x);
                        }
                        break;
                        case 0x3F:
                            /*      SRL A */

                            SRL_8BIT(Regs.A);
                            break;
                        /*TODO*///case 0x40:
/*TODO*///  /*      BIT 0,B */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x41:
/*TODO*///  /*      BIT 0,C */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x42:
/*TODO*///  /*      BIT 0,D */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x43:
/*TODO*///  /*      BIT 0,E */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x44:
/*TODO*///  /*      BIT 0,H */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x45:
/*TODO*///  /*      BIT 0,L */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x46:
/*TODO*///  /*      BIT 0,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (0, x)
/*TODO*///  break;
/*TODO*///case 0x47:
/*TODO*///  /*      BIT 0,A */
/*TODO*///
/*TODO*///  BIT_8BIT (0, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x48:
/*TODO*///  /*      BIT 1,B */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x49:
/*TODO*///  /*      BIT 1,C */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x4A:
/*TODO*///  /*      BIT 1,D */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x4B:
/*TODO*///  /*      BIT 1,E */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x4C:
/*TODO*///  /*      BIT 1,H */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x4D:
/*TODO*///  /*      BIT 1,L */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x4E:
/*TODO*///  /*      BIT 1,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (1, x)
/*TODO*///  break;
/*TODO*///case 0x4F:
/*TODO*///  /*      BIT 1,A */
/*TODO*///
/*TODO*///  BIT_8BIT (1, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x50:
/*TODO*///  /*      BIT 2,B */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x51:
/*TODO*///  /*      BIT 2,C */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x52:
/*TODO*///  /*      BIT 2,D */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x53:
/*TODO*///  /*      BIT 2,E */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x54:
/*TODO*///  /*      BIT 2,H */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x55:
/*TODO*///  /*      BIT 2,L */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x56:
/*TODO*///  /*      BIT 2,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (2, x)
/*TODO*///  break;
/*TODO*///case 0x57:
/*TODO*///  /*      BIT 2,A */
/*TODO*///
/*TODO*///  BIT_8BIT (2, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x58:
/*TODO*///  /*      BIT 3,B */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x59:
/*TODO*///  /*      BIT 3,C */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x5A:
/*TODO*///  /*      BIT 3,D */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x5B:
/*TODO*///  /*      BIT 3,E */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x5C:
/*TODO*///  /*      BIT 3,H */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x5D:
/*TODO*///  /*      BIT 3,L */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x5E:
/*TODO*///  /*      BIT 3,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (3, x)
/*TODO*///  break;
/*TODO*///case 0x5F:
/*TODO*///  /*      BIT 3,A */
/*TODO*///
/*TODO*///  BIT_8BIT (3, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x60:
/*TODO*///  /*      BIT 4,B */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x61:
/*TODO*///  /*      BIT 4,C */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x62:
/*TODO*///  /*      BIT 4,D */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x63:
/*TODO*///  /*      BIT 4,E */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x64:
/*TODO*///  /*      BIT 4,H */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x65:
/*TODO*///  /*      BIT 4,L */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x66:
/*TODO*///  /*      BIT 4,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (4, x)
/*TODO*///  break;
/*TODO*///case 0x67:
/*TODO*///  /*      BIT 4,A */
/*TODO*///
/*TODO*///  BIT_8BIT (4, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x68:
/*TODO*///  /*      BIT 5,B */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x69:
/*TODO*///  /*      BIT 5,C */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x6A:
/*TODO*///  /*      BIT 5,D */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x6B:
/*TODO*///  /*      BIT 5,E */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x6C:
/*TODO*///  /*      BIT 5,H */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x6D:
/*TODO*///  /*      BIT 5,L */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x6E:
/*TODO*///  /*      BIT 5,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (5, x)
/*TODO*///  break;
/*TODO*///case 0x6F:
/*TODO*///  /*      BIT 5,A */
/*TODO*///
/*TODO*///  BIT_8BIT (5, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x70:
/*TODO*///  /*      BIT 6,B */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x71:
/*TODO*///  /*      BIT 6,C */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x72:
/*TODO*///  /*      BIT 6,D */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x73:
/*TODO*///  /*      BIT 6,E */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x74:
/*TODO*///  /*      BIT 6,H */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x75:
/*TODO*///  /*      BIT 6,L */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x76:
/*TODO*///  /*      BIT 6,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (6, x)
/*TODO*///  break;
/*TODO*///case 0x77:
/*TODO*///  /*      BIT 6,A */
/*TODO*///
/*TODO*///  BIT_8BIT (6, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x78:
/*TODO*///  /*      BIT 7,B */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x79:
/*TODO*///  /*      BIT 7,C */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x7A:
/*TODO*///  /*      BIT 7,D */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x7B:
/*TODO*///  /*      BIT 7,E */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x7C:
/*TODO*///  /*      BIT 7,H */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x7D:
/*TODO*///  /*      BIT 7,L */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x7E:
/*TODO*///  /*      BIT 7,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  BIT_8BIT (7, x)
/*TODO*///  break;
/*TODO*///case 0x7F:
/*TODO*///  /*      BIT 7,A */
/*TODO*///
/*TODO*///  BIT_8BIT (7, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x80:
/*TODO*///  /*      RES 0,B */
/*TODO*///
/*TODO*///  RES_8BIT (0, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x81:
/*TODO*///  /*      RES 0,C */
/*TODO*///
/*TODO*///  RES_8BIT (0, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x82:
/*TODO*///  /*      RES 0,D */
/*TODO*///
/*TODO*///  RES_8BIT (0, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x83:
/*TODO*///  /*      RES 0,E */
/*TODO*///
/*TODO*///  RES_8BIT (0, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x84:
/*TODO*///  /*      RES 0,H */
/*TODO*///
/*TODO*///  RES_8BIT (0, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x85:
/*TODO*///  /*      RES 0,L */
/*TODO*///
/*TODO*///  RES_8BIT (0, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x86:
/*TODO*///  /*      RES 0,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (0, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
                        case 0x87:
                            /*      RES 0,A */
                            Regs.A = RES(0, Regs.A);
                            break;
                        case 0x88:
                            /*      RES 1,B */
                            Regs.B = RES(1, Regs.B);
                            break;
                        case 0x89:
                            /*      RES 1,C */
                            Regs.C = RES(1, Regs.C);
                            break;
                        case 0x8A:
                            /*      RES 1,D */
                            Regs.D = RES(1, Regs.D);
                            break;
                        case 0x8B:
                            /*      RES 1,E */
                            Regs.E = RES(1, Regs.E);
                            break;
                        case 0x8C:
                            /*      RES 1,H */
                            Regs.H = RES(1, Regs.H);
                            break;
                        case 0x8D:
                            /*      RES 1,L */
                            Regs.L = RES(1, Regs.L);
                            break;
                        /*TODO*///case 0x8E:
/*TODO*///  /*      RES 1,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (1, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x8F:
/*TODO*///  /*      RES 1,A */
/*TODO*///
/*TODO*///  RES_8BIT (1, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x90:
/*TODO*///  /*      RES 2,B */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x91:
/*TODO*///  /*      RES 2,C */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x92:
/*TODO*///  /*      RES 2,D */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x93:
/*TODO*///  /*      RES 2,E */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x94:
/*TODO*///  /*      RES 2,H */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x95:
/*TODO*///  /*      RES 2,L */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x96:
/*TODO*///  /*      RES 2,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (2, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x97:
/*TODO*///  /*      RES 2,A */
/*TODO*///
/*TODO*///  RES_8BIT (2, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x98:
/*TODO*///  /*      RES 3,B */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x99:
/*TODO*///  /*      RES 3,C */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x9A:
/*TODO*///  /*      RES 3,D */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x9B:
/*TODO*///  /*      RES 3,E */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x9C:
/*TODO*///  /*      RES 3,H */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x9D:
/*TODO*///  /*      RES 3,L */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x9E:
/*TODO*///  /*      RES 3,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (3, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0x9F:
/*TODO*///  /*      RES 3,A */
/*TODO*///
/*TODO*///  RES_8BIT (3, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xA0:
/*TODO*///  /*      RES 4,B */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xA1:
/*TODO*///  /*      RES 4,C */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xA2:
/*TODO*///  /*      RES 4,D */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xA3:
/*TODO*///  /*      RES 4,E */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xA4:
/*TODO*///  /*      RES 4,H */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xA5:
/*TODO*///  /*      RES 4,L */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xA6:
/*TODO*///  /*      RES 4,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (4, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xA7:
/*TODO*///  /*      RES 4,A */
/*TODO*///
/*TODO*///  RES_8BIT (4, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xA8:
/*TODO*///  /*      RES 5,B */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xA9:
/*TODO*///  /*      RES 5,C */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xAA:
/*TODO*///  /*      RES 5,D */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xAB:
/*TODO*///  /*      RES 5,E */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xAC:
/*TODO*///  /*      RES 5,H */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xAD:
/*TODO*///  /*      RES 5,L */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xAE:
/*TODO*///  /*      RES 5,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (5, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xAF:
/*TODO*///  /*      RES 5,A */
/*TODO*///
/*TODO*///  RES_8BIT (5, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xB0:
/*TODO*///  /*      RES 6,B */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xB1:
/*TODO*///  /*      RES 6,C */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xB2:
/*TODO*///  /*      RES 6,D */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xB3:
/*TODO*///  /*      RES 6,E */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xB4:
/*TODO*///  /*      RES 6,H */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xB5:
/*TODO*///  /*      RES 6,L */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xB6:
/*TODO*///  /*      RES 6,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (6, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xB7:
/*TODO*///  /*      RES 6,A */
/*TODO*///
/*TODO*///  RES_8BIT (6, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xB8:
/*TODO*///  /*      RES 7,B */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xB9:
/*TODO*///  /*      RES 7,C */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xBA:
/*TODO*///  /*      RES 7,D */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xBB:
/*TODO*///  /*      RES 7,E */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xBC:
/*TODO*///  /*      RES 7,H */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xBD:
/*TODO*///  /*      RES 7,L */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xBE:
/*TODO*///  /*      RES 7,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  RES_8BIT (7, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xBF:
/*TODO*///  /*      RES 7,A */
/*TODO*///
/*TODO*///  RES_8BIT (7, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xC0:
/*TODO*///  /*      SET 0,B */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xC1:
/*TODO*///  /*      SET 0,C */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xC2:
/*TODO*///  /*      SET 0,D */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xC3:
/*TODO*///  /*      SET 0,E */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xC4:
/*TODO*///  /*      SET 0,H */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xC5:
/*TODO*///  /*      SET 0,L */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xC6:
/*TODO*///  /*      SET 0,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (0, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xC7:
/*TODO*///  /*      SET 0,A */
/*TODO*///
/*TODO*///  SET_8BIT (0, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xC8:
/*TODO*///  /*      SET 1,B */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xC9:
/*TODO*///  /*      SET 1,C */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xCA:
/*TODO*///  /*      SET 1,D */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xCB:
/*TODO*///  /*      SET 1,E */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xCC:
/*TODO*///  /*      SET 1,H */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xCD:
/*TODO*///  /*      SET 1,L */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xCE:
/*TODO*///  /*      SET 1,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (1, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xCF:
/*TODO*///  /*      SET 1,A */
/*TODO*///
/*TODO*///  SET_8BIT (1, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xD0:
/*TODO*///  /*      SET 2,B */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xD1:
/*TODO*///  /*      SET 2,C */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xD2:
/*TODO*///  /*      SET 2,D */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xD3:
/*TODO*///  /*      SET 2,E */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xD4:
/*TODO*///  /*      SET 2,H */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xD5:
/*TODO*///  /*      SET 2,L */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xD6:
/*TODO*///  /*      SET 2,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (2, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xD7:
/*TODO*///  /*      SET 2,A */
/*TODO*///
/*TODO*///  SET_8BIT (2, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xD8:
/*TODO*///  /*      SET 3,B */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xD9:
/*TODO*///  /*      SET 3,C */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xDA:
/*TODO*///  /*      SET 3,D */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xDB:
/*TODO*///  /*      SET 3,E */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xDC:
/*TODO*///  /*      SET 3,H */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xDD:
/*TODO*///  /*      SET 3,L */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xDE:
/*TODO*///  /*      SET 3,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (3, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xDF:
/*TODO*///  /*      SET 3,A */
/*TODO*///
/*TODO*///  SET_8BIT (3, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xE0:
/*TODO*///  /*      SET 4,B */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xE1:
/*TODO*///  /*      SET 4,C */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xE2:
/*TODO*///  /*      SET 4,D */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xE3:
/*TODO*///  /*      SET 4,E */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xE4:
/*TODO*///  /*      SET 4,H */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xE5:
/*TODO*///  /*      SET 4,L */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xE6:
/*TODO*///  /*      SET 4,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (4, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xE7:
/*TODO*///  /*      SET 4,A */
/*TODO*///
/*TODO*///  SET_8BIT (4, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xE8:
/*TODO*///  /*      SET 5,B */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xE9:
/*TODO*///  /*      SET 5,C */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xEA:
/*TODO*///  /*      SET 5,D */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xEB:
/*TODO*///  /*      SET 5,E */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xEC:
/*TODO*///  /*      SET 5,H */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xED:
/*TODO*///  /*      SET 5,L */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xEE:
/*TODO*///  /*      SET 5,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (5, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xEF:
/*TODO*///  /*      SET 5,A */
/*TODO*///
/*TODO*///  SET_8BIT (5, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xF0:
/*TODO*///  /*      SET 6,B */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xF1:
/*TODO*///  /*      SET 6,C */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xF2:
/*TODO*///  /*      SET 6,D */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xF3:
/*TODO*///  /*      SET 6,E */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xF4:
/*TODO*///  /*      SET 6,H */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xF5:
/*TODO*///  /*      SET 6,L */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xF6:
/*TODO*///  /*      SET 6,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (6, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xF7:
/*TODO*///  /*      SET 6,A */
/*TODO*///
/*TODO*///  SET_8BIT (6, Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xF8:
/*TODO*///  /*      SET 7,B */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xF9:
/*TODO*///  /*      SET 7,C */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xFA:
/*TODO*///  /*      SET 7,D */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xFB:
/*TODO*///  /*      SET 7,E */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xFC:
/*TODO*///  /*      SET 7,H */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xFD:
/*TODO*///  /*      SET 7,L */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xFE:
/*TODO*///  /*      SET 7,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///  SET_8BIT (7, x)
/*TODO*///  mem_WriteByte (Regs.w.HL, x);
/*TODO*///  break;
/*TODO*///case 0xFF:
/*TODO*///  /*      SET 7,A */
/*TODO*///
/*TODO*///  SET_8BIT (7, Regs.b.A)
/*TODO*///  break;
                        default:
                            System.out.println("Unsupported CB 0x" + Integer.toHexString(x));
                            throw new UnsupportedOperationException("Unsupported");
                    }
                    break;
                /*TODO*///case 0xCC: /*	   CALL Z,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///	register UINT16 PC;
/*TODO*///    PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///
/*TODO*///    Regs.SP = (Regs.SP - 2) & 0xFFFF;
/*TODO*///    mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///    Regs.w.PC = PC;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  break;
                case 0xCD: /*	   CALL n16 */ {
                    int PC;
                    PC = mem_ReadWord(Regs.PC);
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = PC & 0xFFFF;
                }
                break;
                case 0xCE: /*	   ADC A,n8 */ {
                    int x1 = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    ADC_A_X(x1);
                }
                break;
                case 0xCF:
                    /*	   RST 8 */
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 8;
                    break;
                case 0xD0:
                    /*	   RET NC */
                    if ((Regs.F & FLAG_C) == 0) {
                        Regs.PC = mem_ReadWord(Regs.SP);
                        Regs.SP = (Regs.SP + 2) & 0xFFFF;
                        ICycles += 12;
                    }
                    break;
                case 0xD1:
                    /*	   POP DE */
                    DE(mem_ReadWord(Regs.SP));
                    Regs.SP = (Regs.SP + 2) & 0xFFFF;
                    break;
                /*TODO*///case 0xD2: /*	   JP NC,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xD3: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xD4: /*	   CALL NC,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///	register UINT16 PC;
/*TODO*///    PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///
/*TODO*///    Regs.SP = (Regs.SP - 2) & 0xFFFF;
/*TODO*///    mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///    Regs.w.PC = PC;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
                case 0xD5:
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;/*	   PUSH DE */
                    mem_WriteWord(Regs.SP, DE());
                    break;
                case 0xD6: {
                    int x1 = mem_ReadByte(Regs.PC);/*	   SUB A,n8 */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    SUB_A_X(x1);
                }
                break;
                case 0xD7:
                    /*	   RST	   $10 */
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 0x10;
                    break;
                case 0xD8:
                    /*	   RET C */
                    if ((Regs.F & FLAG_C) != 0) {
                        Regs.PC = mem_ReadWord(Regs.SP);
                        Regs.SP = (Regs.SP + 2) & 0xFFFF;
                        ICycles += 12;
                    }
                    break;
                case 0xD9:
                    /*	   RETI */
                    Regs.PC = mem_ReadWord(Regs.SP);
                    Regs.SP = (Regs.SP + 2) & 0xFFFF;
                    Regs.enable |= IME;
                    CheckInterrupts = 1;
                    break;
                /*TODO*///case 0xDA: /*	   JP C,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xDB: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xDC: /*	   CALL C,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///	register UINT16 PC;
/*TODO*///    PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///
/*TODO*///    Regs.SP = (Regs.SP - 2) & 0xFFFF;
/*TODO*///    mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///    Regs.w.PC = PC;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xDD: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xDE: /*	   SBC A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  SBC_A_X (x)
/*TODO*///  break;
                case 0xDF:
                    /*	   RST	   $18 */
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 0x18;
                    break;
                case 0xE0:
                    mem_WriteByte(mem_ReadByte(Regs.PC) + 0xFF00, Regs.A);/*	   LD	   ($FF00+n8),A */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                case 0xE1:
                    HL(mem_ReadWord(Regs.SP));/*	   POP HL */
                    Regs.SP = (Regs.SP + 2) & 0xFFFF;
                    break;
                case 0xE2:
                    /*	   LD ($FF00+C),A */

                    mem_WriteByte((0xFF00 + Regs.C) & 0xFFFF, Regs.A);
                    break;
                case 0xE3:
                    /*	   EH? */
                    break;
                case 0xE4:
                    /*	   EH? */
                    break;
                case 0xE5:
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;/*	   PUSH HL */
                    mem_WriteWord(Regs.SP, HL());
                    break;
                case 0xE6: /*	   AND A,n8 */ {
                    int x1 = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    AND_A_X(x1);
                }
                break;
                case 0xE7:
                    /*	   RST $20 */
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 0x20;
                    break;
                case 0xE8: /*	   ADD SP,n8 */ /*
 *	 Z - Reset.
 *	 N - Reset.
 *	 H - Set or reset according to operation.
 *	 C - Set or reset according to operation.
                 */ {
                    int n;
                    int /*UINT32*/ r1, r2;
                    int /*UINT8*/ f;

                    /* printf( "Hmmm.. ADD SP,n8\n" ); */
                    n = (int) ((byte) mem_ReadByte(Regs.PC));
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    r1 = Regs.SP + n;
                    r2 = (Regs.SP & 0xFFF) + (n & 0xFFF);

                    if (r1 > 0xFFFF) {
                        f = FLAG_C;
                    } else {
                        f = 0;
                    }

                    if (r2 > 0xFFF) {
                        f |= FLAG_H;
                    }

                    Regs.SP = r1 & 0xFFFF;
                    Regs.F = f & 0xFF;
                }
                break;
                case 0xE9:
                    Regs.PC = HL();/*	   JP (HL) */
                    break;
                case 0xEA:
                    mem_WriteByte(mem_ReadWord(Regs.PC), Regs.A);/*	   LD (n16),A */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0xEB:
                    /*	   EH? */
                    break;
                case 0xEC:
                    /*	   EH? */
                    break;
                case 0xED:
                    /*	   EH? */
                    break;
                case 0xEE: /*	   XOR A,n8 */ {
                    int x1 = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    XOR_A_X(x1);
                }
                break;
                case 0xEF:
                    /*	   RST $28 */
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 0x28;
                    break;
                case 0xF0:
                    Regs.A = mem_ReadByte(0xFF00 + mem_ReadByte(Regs.PC));/*	   LD A,($FF00+n8) */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                case 0xF1:
                    AF((mem_ReadWord(Regs.SP) & 0xFFF0));/*	   POP AF */
                    Regs.SP = (Regs.SP + 2) & 0xFFFF;
                    break;
                case 0xF2:
                    /*	   LD A,($FF00+C) */
                    Regs.A = mem_ReadByte((0xFF00 + Regs.C) & 0xFFFF);
                    break;
                case 0xF3:
                    Regs.enable &= ~IME;/*	   DI */
                    break;
                case 0xF4:
                    /*	   EH? */
                    break;
                case 0xF5:
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;/*	   PUSH AF */
                    mem_WriteWord(Regs.SP, (AF() & 0xFFF0) & 0xFFFF);
                    break;
                case 0xF6: /*	   OR A,n8 */ {
                    x = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    OR_A_X(x);
                }
                break;
                case 0xF7:
                    /*	   RST $30 */
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 0x30;
                    break;
                case 0xF8: /*	   LD HL,SP+n8 */ /*
 *	 n = one UINT8 signed immediate value.
 * Flags affected:
 *	 Z - Reset.
 *	 N - Reset.
 *	 H - Set or reset according to operation.
 *	 C - Set or reset according to operation.
 *
                 */ {
                    int n;
                    int /*UINT32*/ r1, r2;
                    int /*UINT8*/ f;

                    n = (int) ((byte) mem_ReadByte(Regs.PC));
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    r1 = Regs.SP + n;
                    r2 = (Regs.SP & 0xFFF) + (n & 0xFFF);

                    if (r1 > 0xFFFF) {
                        f = FLAG_C;
                    } else {
                        f = 0;
                    }

                    if (r2 > 0xFFF) {
                        f |= FLAG_H;
                    }

                    HL(r1 & 0xFFFF);
                    Regs.F = f & 0xFF;
                }
                break;
                case 0xF9:
                    /*	   LD SP,HL */
                    Regs.SP = HL();
                    break;
                case 0xFA:
                    Regs.A = mem_ReadByte(mem_ReadWord(Regs.PC));/*	   LD A,(n16) */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                case 0xFB:
                    /*	   EI */
                    Regs.enable |= IME;
                    CheckInterrupts = 1;
                    break;
                case 0xFC:
                    /*	   EH? */
                    break;
                case 0xFD:
                    /*	   EH? */
                    break;
                case 0xFE: /*	   CP A,n8 */ {
                    int x1 = mem_ReadByte(Regs.PC);
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    CP_A_X(x1);
                }
                break;
                case 0xFF:
                    Regs.SP = (Regs.SP - 2) & 0xFFFF;/*	   RST $38 */
                    mem_WriteWord(Regs.SP, Regs.PC);
                    Regs.PC = 0x38;
                    break;
                default: {
                    System.out.println("Unsupported 0x" + Integer.toHexString(x));
                    throw new UnsupportedOperationException("Unsupported");
                }
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
