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

                /*TODO*///case 0x01: /*	   LD BC,n16 */
/*TODO*///  Regs.w.BC = mem_ReadWord (Regs.w.PC);
/*TODO*///  Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  break;
/*TODO*///case 0x02: /*	   LD (BC),A */
/*TODO*///  mem_WriteByte (Regs.w.BC, Regs.b.A);
/*TODO*///  break;
/*TODO*///case 0x03: /*	   INC BC */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.B == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///  Regs.w.BC += 1;
/*TODO*///  break;
/*TODO*///case 0x04: /*	   INC B */
/*TODO*///
/*TODO*///  INC_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x05: /*	   DEC B */
/*TODO*///
/*TODO*///  DEC_8BIT (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x06: /*	   LD B,n8 */
/*TODO*///
/*TODO*///  Regs.b.B = mem_ReadByte (Regs.w.PC++);
/*TODO*///  break;
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
/*TODO*///case 0x08: /*	   LD (n16),SP */
/*TODO*///
/*TODO*///  mem_WriteWord (mem_ReadWord (Regs.w.PC), Regs.w.SP);
/*TODO*///  Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  break;
/*TODO*///case 0x09: /*	   ADD HL,BC */
/*TODO*///
/*TODO*///  ADD_HL_RR (Regs.w.BC)
/*TODO*///  break;
/*TODO*///case 0x0A: /*	   LD A,(BC) */
/*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte (Regs.w.BC);
/*TODO*///  break;
/*TODO*///case 0x0B: /*	   DEC BC */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.B == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  Regs.w.BC -= 1;
/*TODO*///  break;
/*TODO*///case 0x0C: /*	   INC C */
/*TODO*///
/*TODO*///  INC_8BIT (Regs.b.C)
/*TODO*///  break;
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
                /*TODO*///case 0x13: /*	   INC DE */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.D == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  Regs.w.DE += 1;
/*TODO*///  break;
                case 0x14:
                    Regs.D = INC_8BIT(Regs.D);/*	   INC D */
                    break;
                /*TODO*///case 0x15: /*	   DEC D */
/*TODO*///
/*TODO*///  DEC_8BIT (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x16: /*	   LD D,n8 */
/*TODO*///
/*TODO*///  Regs.b.D = mem_ReadByte (Regs.w.PC++);
/*TODO*///  break;
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
                /*TODO*///case 0x19: /*	   ADD HL,DE */
/*TODO*///
/*TODO*///  ADD_HL_RR (Regs.w.DE)
/*TODO*///  break;
/*TODO*///case 0x1A: /*	   LD A,(DE) */
/*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte (Regs.w.DE);
/*TODO*///  break;
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
                /*TODO*///case 0x1D: /*	   DEC E */
/*TODO*///
/*TODO*///  DEC_8BIT (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x1E: /*	   LD E,n8 */
/*TODO*///
/*TODO*///  Regs.b.E = mem_ReadByte (Regs.w.PC++);
/*TODO*///  break;
/*TODO*///case 0x1F: /*	   RRA */
/*TODO*///  
/*TODO*///  x = (Regs.b.A & 1) ? FLAG_C : 0;
/*TODO*///
/*TODO*///  Regs.b.A = (UINT8) ((Regs.b.A >> 1) | ((Regs.b.F & FLAG_C) ? 0x80 : 0));
/*TODO*///  Regs.b.F = x;
/*TODO*///  break;
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
                /*TODO*///case 0x22: /*	   LD (HL+),A */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.H == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.A);
/*TODO*///  Regs.w.HL += 1;
/*TODO*///  break;
/*TODO*///case 0x23: /*	   INC HL */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.H == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  Regs.w.HL += 1;
/*TODO*///  break;
/*TODO*///case 0x24: /*	   INC H */
/*TODO*///
/*TODO*///  INC_8BIT (Regs.b.H);
/*TODO*///  break;
/*TODO*///case 0x25: /*	   DEC H */
/*TODO*///
/*TODO*///  DEC_8BIT (Regs.b.H);
/*TODO*///  break;
/*TODO*///case 0x26: /*	   LD H,n8 */
/*TODO*///
/*TODO*///  Regs.b.H = mem_ReadByte (Regs.w.PC++);
/*TODO*///  break;
/*TODO*///case 0x27: /*	   DAA */
/*TODO*///
/*TODO*///  Regs.w.AF = DAATable[(((UINT16) (Regs.b.F & (FLAG_N | FLAG_C | FLAG_H))) << 4) | Regs.b.A];
/*TODO*///  break;
/*TODO*///case 0x28: /*	   JR Z,n8 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///	INT8 offset;
/*TODO*///
/*TODO*///    offset = mem_ReadByte (Regs.w.PC++);
/*TODO*///    Regs.w.PC += offset;
/*TODO*///
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.w.PC += 1;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0x29: /*	   ADD HL,HL */
/*TODO*///
/*TODO*///  ADD_HL_RR (Regs.w.HL)
/*TODO*///  break;
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
/*TODO*///case 0x2C: /*	   INC L */
/*TODO*///
/*TODO*///  INC_8BIT (Regs.b.L);
/*TODO*///  break;
/*TODO*///case 0x2D: /*	   DEC L */
/*TODO*///
/*TODO*///  DEC_8BIT (Regs.b.L);
/*TODO*///  break;
/*TODO*///case 0x2E: /*	   LD L,n8 */
/*TODO*///
/*TODO*///  Regs.b.L = mem_ReadByte (Regs.w.PC++);
/*TODO*///  break;
/*TODO*///case 0x2F: /*	   CPL */
/*TODO*///
/*TODO*///  Regs.b.A = ~Regs.b.A;
/*TODO*///  Regs.b.F |= FLAG_N | FLAG_H;
/*TODO*///  return 4;
/*TODO*///  break;
/*TODO*///case 0x30: /*	   JR NC,n8 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///    Regs.w.PC += 1;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///	INT8 offset;
/*TODO*///
/*TODO*///    offset = mem_ReadByte (Regs.w.PC++);
/*TODO*///    Regs.w.PC += offset;
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  break;
                case 0x31:
                    Regs.SP = mem_ReadWord(Regs.PC);/*	   LD SP,n16 */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                /*TODO*///case 0x32: /*	   LD (HL-),A */
/*TODO*///
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.H == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.A);
/*TODO*///  Regs.w.HL -= 1;
/*TODO*///  break;
/*TODO*///case 0x33: /*	   INC SP */
/*TODO*///
/*TODO*///  Regs.w.SP += 1;
/*TODO*///  break;
/*TODO*///case 0x34: /*	   INC (HL) */
/*TODO*///  
/*TODO*///  {
/*TODO*///	register UINT8 r, f;
/*TODO*///
/*TODO*///	f = (UINT8) (Regs.b.F & FLAG_C);
/*TODO*///	r = (UINT8) (mem_ReadByte (Regs.w.HL) + 1);
/*TODO*///    mem_WriteByte (Regs.w.HL, r);
/*TODO*///
/*TODO*///    if (r == 0)
/*TODO*///      f |= FLAG_Z;
/*TODO*///
/*TODO*///    if ((r & 0xF) == 0)
/*TODO*///      f |= FLAG_H;
/*TODO*///
/*TODO*///    Regs.b.F = f;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0x35: /*	   DEC (HL) */
/*TODO*///  
/*TODO*///  {
/*TODO*///	register UINT8 r, f;
/*TODO*///
/*TODO*///	f = (UINT8) ((Regs.b.F & FLAG_C) | FLAG_N);
/*TODO*///	r = (UINT8) (mem_ReadByte (Regs.w.HL) - 1);
/*TODO*///    mem_WriteByte (Regs.w.HL, r);
/*TODO*///
/*TODO*///    if (r == 0)
/*TODO*///      f |= FLAG_Z;
/*TODO*///
/*TODO*///    if ((r & 0xF) != 0xF)
/*TODO*///      f |= FLAG_H;
/*TODO*///
/*TODO*///    Regs.b.F = f;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0x36: /*	   LD (HL),n8 */
/*TODO*///  /* FIXED / broken ? */
/*TODO*///  mem_WriteByte (Regs.w.HL, mem_ReadByte (Regs.w.PC++));
/*TODO*///  break;
/*TODO*///case 0x37: /*	   SCF */
/*TODO*///
/*TODO*///  Regs.b.F = (UINT8) ((Regs.b.F & FLAG_Z) | FLAG_C);
/*TODO*///  break;
/*TODO*///case 0x38: /*	   JR C,n8 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///	INT8 offset;
/*TODO*///
/*TODO*///    offset = mem_ReadByte (Regs.w.PC++);
/*TODO*///    Regs.w.PC += offset;
/*TODO*///
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.w.PC += 1;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0x39: /*	   ADD HL,SP */
/*TODO*///  ADD_HL_RR (Regs.w.SP)
/*TODO*///  break;
/*TODO*///case 0x3A: /*	   LD A,(HL-) */
/*TODO*///#if 0				/* FIXME ?? do we want to support this? (bug emulation) */
/*TODO*///  if (Regs.b.H == 0xFE)
/*TODO*///  {
/*TODO*///    trash_sprites (state);
/*TODO*///  }
/*TODO*///#endif
/*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte (Regs.w.HL);
/*TODO*///  Regs.w.HL -= 1;
/*TODO*///  break;
/*TODO*///case 0x3B: /*	   DEC SP */
/*TODO*///
/*TODO*///  Regs.w.SP -= 1;
/*TODO*///  break;
/*TODO*///case 0x3C: /*	   INC	   A */
/*TODO*///
/*TODO*///  INC_8BIT (Regs.b.A);
/*TODO*///  break;
/*TODO*///case 0x3D: /*	   DEC	   A */
/*TODO*///
/*TODO*///  DEC_8BIT (Regs.b.A);
/*TODO*///  break;
                case 0x3E:
                    Regs.A = mem_ReadByte(Regs.PC);/*	   LD A,n8 */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                /*TODO*///case 0x3F: /*	   CCF */
/*TODO*///
/*TODO*///  Regs.b.F = (UINT8) ((Regs.b.F & FLAG_Z) | ((Regs.b.F & FLAG_C) ? 0 : FLAG_C));
/*TODO*///  break;
/*TODO*///case 0x40: /*	   LD B,B */
/*TODO*///  break;
/*TODO*///case 0x41: /*	   LD B,C */
/*TODO*///
/*TODO*///  Regs.b.B = Regs.b.C;
/*TODO*///  break;
/*TODO*///case 0x42: /*	   LD B,D */
/*TODO*///
/*TODO*///  Regs.b.B = Regs.b.D;
/*TODO*///  break;
/*TODO*///case 0x43: /*	   LD B,E */
/*TODO*///
/*TODO*///  Regs.b.B = Regs.b.E;
/*TODO*///  break;
/*TODO*///case 0x44: /*	   LD B,H */
/*TODO*///
/*TODO*///  Regs.b.B = Regs.b.H;
/*TODO*///  break;
/*TODO*///case 0x45: /*	   LD B,L */
/*TODO*///
/*TODO*///  Regs.b.B = Regs.b.L;
/*TODO*///  break;
/*TODO*///case 0x46: /*	   LD B,(HL) */
/*TODO*///
/*TODO*///  Regs.b.B = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
                case 0x47:
                    Regs.B = Regs.A;/*	   LD B,A */
                    break;
                /*TODO*///case 0x48: /*	   LD C,B */
/*TODO*///
/*TODO*///  Regs.b.C = Regs.b.B;
/*TODO*///  break;
/*TODO*///case 0x49: /*	   LD C,C */
/*TODO*///  break;
/*TODO*///case 0x4A: /*	   LD C,D */
/*TODO*///
/*TODO*///  Regs.b.C = Regs.b.D;
/*TODO*///  break;
/*TODO*///case 0x4B: /*	   LD C,E */
/*TODO*///
/*TODO*///  Regs.b.C = Regs.b.E;
/*TODO*///  break;
/*TODO*///case 0x4C: /*	   LD C,H */
/*TODO*///
/*TODO*///  Regs.b.C = Regs.b.H;
/*TODO*///  break;
/*TODO*///case 0x4D: /*	   LD C,L */
/*TODO*///
/*TODO*///  Regs.b.C = Regs.b.L;
/*TODO*///  break;
/*TODO*///case 0x4E: /*	   LD C,(HL) */
/*TODO*///
/*TODO*///  Regs.b.C = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0x4F: /*	   LD C,A */
/*TODO*///
/*TODO*///  Regs.b.C = Regs.b.A;
/*TODO*///  break;
/*TODO*///case 0x50: /*	   LD D,B */
/*TODO*///
/*TODO*///  Regs.b.D = Regs.b.B;
/*TODO*///  break;
/*TODO*///case 0x51: /*	   LD D,C */
/*TODO*///
/*TODO*///  Regs.b.D = Regs.b.C;
/*TODO*///  break;
/*TODO*///case 0x52: /*	   LD D,D */
/*TODO*///  break;
/*TODO*///case 0x53: /*	   LD D,E */
/*TODO*///
/*TODO*///  Regs.b.D = Regs.b.E;
/*TODO*///  break;
/*TODO*///case 0x54: /*	   LD D,H */
/*TODO*///
/*TODO*///  Regs.b.D = Regs.b.H;
/*TODO*///  break;
/*TODO*///case 0x55: /*	   LD D,L */
/*TODO*///
/*TODO*///  Regs.b.D = Regs.b.L;
/*TODO*///  break;
/*TODO*///case 0x56: /*	   LD D,(HL) */
/*TODO*///
/*TODO*///  Regs.b.D = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0x57: /*	   LD D,A */
/*TODO*///
/*TODO*///  Regs.b.D = Regs.b.A;
/*TODO*///  break;
/*TODO*///case 0x58: /*	   LD E,B */
/*TODO*///
/*TODO*///  Regs.b.E = Regs.b.B;
/*TODO*///  break;
/*TODO*///case 0x59: /*	   LD E,C */
/*TODO*///
/*TODO*///  Regs.b.E = Regs.b.C;
/*TODO*///  break;
/*TODO*///case 0x5A: /*	   LD E,D */
/*TODO*///
/*TODO*///  Regs.b.E = Regs.b.D;
/*TODO*///  break;
/*TODO*///case 0x5B: /*	   LD E,E */
/*TODO*///  break;
/*TODO*///case 0x5C: /*	   LD E,H */
/*TODO*///
/*TODO*///  Regs.b.E = Regs.b.H;
/*TODO*///  break;
/*TODO*///case 0x5D: /*	   LD E,L */
/*TODO*///
/*TODO*///  Regs.b.E = Regs.b.L;
/*TODO*///  break;
/*TODO*///case 0x5E: /*	   LD E,(HL) */
/*TODO*///
/*TODO*///  Regs.b.E = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0x5F: /*	   LD E,A */
/*TODO*///
/*TODO*///  Regs.b.E = Regs.b.A;
/*TODO*///  break;
/*TODO*///case 0x60: /*	   LD H,B */
/*TODO*///
/*TODO*///  Regs.b.H = Regs.b.B;
/*TODO*///  break;
/*TODO*///case 0x61: /*	   LD H,C */
/*TODO*///
/*TODO*///  Regs.b.H = Regs.b.C;
/*TODO*///  break;
/*TODO*///case 0x62: /*	   LD H,D */
/*TODO*///
/*TODO*///  Regs.b.H = Regs.b.D;
/*TODO*///  break;
/*TODO*///case 0x63: /*	   LD H,E */
/*TODO*///
/*TODO*///  Regs.b.H = Regs.b.E;
/*TODO*///  break;
/*TODO*///case 0x64: /*	   LD H,H */
/*TODO*///  break;
/*TODO*///case 0x65: /*	   LD H,L */
/*TODO*///
/*TODO*///  Regs.b.H = Regs.b.L;
/*TODO*///  break;
/*TODO*///case 0x66: /*	   LD H,(HL) */
/*TODO*///
/*TODO*///  Regs.b.H = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0x67: /*	   LD H,A */
/*TODO*///
/*TODO*///  Regs.b.H = Regs.b.A;
/*TODO*///  break;
/*TODO*///case 0x68: /*	   LD L,B */
/*TODO*///
/*TODO*///  Regs.b.L = Regs.b.B;
/*TODO*///  break;
/*TODO*///case 0x69: /*	   LD L,C */
/*TODO*///
/*TODO*///  Regs.b.L = Regs.b.C;
/*TODO*///  break;
/*TODO*///case 0x6A: /*	   LD L,D */
/*TODO*///  Regs.b.L = Regs.b.D;
/*TODO*///  break;
/*TODO*///case 0x6B: /*	   LD L,E */
/*TODO*///
/*TODO*///  Regs.b.L = Regs.b.E;
/*TODO*///  break;
/*TODO*///case 0x6C: /*	   LD L,H */
/*TODO*///
/*TODO*///  Regs.b.L = Regs.b.H;
/*TODO*///  break;
/*TODO*///case 0x6D: /*	   LD L,L */
/*TODO*///  break;
/*TODO*///case 0x6E: /*	   LD L,(HL) */
/*TODO*///
/*TODO*///  Regs.b.L = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0x6F: /*	   LD L,A */
/*TODO*///
/*TODO*///  Regs.b.L = Regs.b.A;
/*TODO*///  break;
/*TODO*///case 0x70: /*	   LD (HL),B */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.B);
/*TODO*///  break;
/*TODO*///case 0x71: /*	   LD (HL),C */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.C);
/*TODO*///  break;
/*TODO*///case 0x72: /*	   LD (HL),D */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.D);
/*TODO*///  break;
/*TODO*///case 0x73: /*	   LD (HL),E */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.E);
/*TODO*///  break;
/*TODO*///case 0x74: /*	   LD (HL),H */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.H);
/*TODO*///  break;
/*TODO*///case 0x75: /*	   LD (HL),L */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.L);
/*TODO*///  break;
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
/*TODO*///case 0x77: /*	   LD (HL),A */
/*TODO*///
/*TODO*///  mem_WriteByte (Regs.w.HL, Regs.b.A);
/*TODO*///  break;
                case 0x78:
                    Regs.A = Regs.B;/*	   LD A,B */
                    break;
                /*TODO*///case 0x79: /*	   LD A,C */
/*TODO*///
/*TODO*///  Regs.b.A = Regs.b.C;
/*TODO*///  break;
/*TODO*///case 0x7A: /*	   LD A,D */
/*TODO*///
/*TODO*///  Regs.b.A = Regs.b.D;
/*TODO*///  break;
/*TODO*///case 0x7B: /*	   LD A,E */
/*TODO*///
/*TODO*///  Regs.b.A = Regs.b.E;
/*TODO*///  break;
                case 0x7C:
                    Regs.A = Regs.H;/*	   LD A,H */
                    break;
                case 0x7D:
                    Regs.A = Regs.L;/*	   LD A,L */
                    break;
                /*TODO*///               case 0x7E:
                /*	   LD A,(HL) */
 /*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte (Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0x7F: /*	   LD A,A */
/*TODO*///  break;
/*TODO*///case 0x80: /*	   ADD A,B */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x81: /*	   ADD A,C */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x82: /*	   ADD A,D */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x83: /*	   ADD A,E */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x84: /*	   ADD A,H */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x85: /*	   ADD A,L */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x86: /*	   ADD A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  ADD_A_X (x)
/*TODO*///  break;
/*TODO*///case 0x87: /*	   ADD A,A */
/*TODO*///
/*TODO*///  ADD_A_X (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x88: /*	   ADC A,B */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x89: /*	   ADC A,C */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x8A: /*	   ADC A,D */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x8B: /*	   ADC A,E */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x8C: /*	   ADC A,H */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x8D: /*	   ADC A,L */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x8E: /*	   ADC A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  ADC_A_X (x)
/*TODO*///  break;
/*TODO*///case 0x8F: /*	   ADC A,A */
/*TODO*///
/*TODO*///  ADC_A_X (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0x90: /*	   SUB A,B */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0x91: /*	   SUB A,C */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0x92: /*	   SUB A,D */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0x93: /*	   SUB A,E */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0x94: /*	   SUB A,H */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0x95: /*	   SUB A,L */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0x96: /*	   SUB A,(HL) */
/*TODO*///
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  SUB_A_X (x)
/*TODO*///  break;
/*TODO*///case 0x97: /*	   SUB A,A */
/*TODO*///
/*TODO*///  SUB_A_X (Regs.b.A)
/*TODO*///  break;
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
/*TODO*///case 0xA0: /*	   AND A,B */
/*TODO*///
/*TODO*///  AND_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xA1: /*	   AND A,C */
/*TODO*///
/*TODO*///  AND_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xA2: /*	   AND A,D */
/*TODO*///
/*TODO*///  AND_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xA3: /*	   AND A,E */
/*TODO*///
/*TODO*///  AND_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xA4: /*	   AND A,H */
/*TODO*///
/*TODO*///  AND_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xA5: /*	   AND A,L */
/*TODO*///
/*TODO*///  AND_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xA6: /*	   AND A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  AND_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xA7: /*	   AND A,A */
/*TODO*///
/*TODO*///  Regs.b.F = (Regs.b.A == 0) ? (FLAG_H | FLAG_Z) : FLAG_H;
/*TODO*///  break;
/*TODO*///case 0xA8: /*	   XOR A,B */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xA9: /*	   XOR A,C */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xAA: /*	   XOR A,D */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xAB: /*	   XOR A,E */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xAC: /*	   XOR A,H */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xAD: /*	   XOR A,L */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xAE: /*	   XOR A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  XOR_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xAF: /*	   XOR A,A */
/*TODO*///
/*TODO*///  XOR_A_X (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xB0: /*	   OR A,B */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xB1: /*	   OR A,C */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xB2: /*	   OR A,D */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xB3: /*	   OR A,E */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xB4: /*	   OR A,H */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xB5: /*	   OR A,L */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xB6: /*	   OR A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  OR_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xB7: /*	   OR A,A */
/*TODO*///
/*TODO*///  OR_A_X (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xB8: /*	   CP A,B */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.B)
/*TODO*///  break;
/*TODO*///case 0xB9: /*	   CP A,C */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.C)
/*TODO*///  break;
/*TODO*///case 0xBA: /*	   CP A,D */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.D)
/*TODO*///  break;
/*TODO*///case 0xBB: /*	   CP A,E */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.E)
/*TODO*///  break;
/*TODO*///case 0xBC: /*	   CP A,H */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.H)
/*TODO*///  break;
/*TODO*///case 0xBD: /*	   CP A,L */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.L)
/*TODO*///  break;
/*TODO*///case 0xBE: /*	   CP A,(HL) */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.HL);
/*TODO*///
/*TODO*///  CP_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xBF: /*	   CP A,A */
/*TODO*///
/*TODO*///  CP_A_X (Regs.b.A)
/*TODO*///  break;
/*TODO*///case 0xC0: /*	   RET NZ */
/*TODO*///
/*TODO*///  if (!(Regs.b.F & FLAG_Z))
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.SP);
/*TODO*///    Regs.w.SP += 2;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xC1: /*	   POP BC */
/*TODO*///
/*TODO*///  Regs.w.BC = mem_ReadWord (Regs.w.SP);
/*TODO*///  Regs.w.SP += 2;
/*TODO*///  break;
/*TODO*///case 0xC2: /*	   JP NZ,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  break;
                case 0xC3:
                    Regs.PC = mem_ReadWord(Regs.PC);/*	   JP n16 */
                    break;
                /*TODO*///case 0xC4: /*	   CALL NZ,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///	register UINT16 PC;
/*TODO*///    PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///
/*TODO*///    Regs.w.SP -= 2;
/*TODO*///    mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///    Regs.w.PC = PC;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xC5: /*	   PUSH BC */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.BC);
/*TODO*///  break;
/*TODO*///case 0xC6: /*	   ADD A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  ADD_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xC7: /*	   RST 0 */
/*TODO*///  
/*TODO*///  {
/*TODO*///	register UINT16 PC;
/*TODO*///    PC = Regs.w.PC;
/*TODO*///    Regs.w.PC = 0;
/*TODO*///
/*TODO*///    Regs.w.SP -= 2;
/*TODO*///    mem_WriteWord (Regs.w.SP, PC);
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xC8: /*	   RET Z */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.SP);
/*TODO*///    Regs.w.SP += 2;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xC9: /*	   RET */
/*TODO*///
/*TODO*///  Regs.w.PC = mem_ReadWord (Regs.w.SP);
/*TODO*///  Regs.w.SP += 2;
/*TODO*///  break;
/*TODO*///case 0xCA: /*	   JP Z,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    ICycles += 4;
/*TODO*///  }
/*TODO*///  else
/*TODO*///  {
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xCB: /*	   PREFIX! */
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  ICycles += CyclesCB[x];
/*TODO*///  switch (x)
/*TODO*///  {
/*TODO*///    #include "opc_cb.h"
/*TODO*///  }  
/*TODO*///  break;
/*TODO*///case 0xCC: /*	   CALL Z,n16 */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_Z)
/*TODO*///  {
/*TODO*///	register UINT16 PC;
/*TODO*///    PC = mem_ReadWord (Regs.w.PC);
/*TODO*///    Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///
/*TODO*///    Regs.w.SP -= 2;
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
                /*TODO*///case 0xCE: /*	   ADC A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  ADC_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xCF: /*	   RST 8 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 8;
/*TODO*///  break;
/*TODO*///case 0xD0: /*	   RET NC */
/*TODO*///
/*TODO*///  if (!(Regs.b.F & FLAG_C))
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.SP);
/*TODO*///    Regs.w.SP += 2;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xD1: /*	   POP DE */
/*TODO*///
/*TODO*///  Regs.w.DE = mem_ReadWord (Regs.w.SP);
/*TODO*///  Regs.w.SP += 2;
/*TODO*///  break;
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
/*TODO*///    Regs.w.SP -= 2;
/*TODO*///    mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///    Regs.w.PC = PC;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xD5: /*	   PUSH DE */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.DE);
/*TODO*///  break;
/*TODO*///case 0xD6: /*	   SUB A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  SUB_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xD7: /*	   RST	   $10 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 0x10;
/*TODO*///  break;
/*TODO*///case 0xD8: /*	   RET C */
/*TODO*///
/*TODO*///  if (Regs.b.F & FLAG_C)
/*TODO*///  {
/*TODO*///    Regs.w.PC = mem_ReadWord (Regs.w.SP);
/*TODO*///    Regs.w.SP += 2;
/*TODO*///    ICycles += 12;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xD9: /*	   RETI */
/*TODO*///
/*TODO*///  Regs.w.PC = mem_ReadWord (Regs.w.SP);
/*TODO*///  Regs.w.SP += 2;
/*TODO*///  Regs.w.enable |= IME;
/*TODO*///  CheckInterrupts = 1;
/*TODO*///  break;
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
/*TODO*///    Regs.w.SP -= 2;
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
/*TODO*///case 0xDF: /*	   RST	   $18 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 0x18;
/*TODO*///  break;
                case 0xE0:
                    mem_WriteByte(mem_ReadByte(Regs.PC) + 0xFF00, Regs.A);/*	   LD	   ($FF00+n8),A */
                    Regs.PC = (Regs.PC + 1) & 0xFFFF;
                    break;
                /*TODO*///                case 0xE1:
                /*	   POP HL */

 /*TODO*///  Regs.w.HL = mem_ReadWord (Regs.w.SP);
/*TODO*///  Regs.w.SP += 2;
/*TODO*///  break;
/*TODO*///case 0xE2: /*	   LD ($FF00+C),A */
/*TODO*///
/*TODO*///  mem_WriteByte ((UINT16) (0xFF00 + Regs.b.C), Regs.b.A);
/*TODO*///  break;
/*TODO*///case 0xE3: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xE4: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xE5: /*	   PUSH HL */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.HL);
/*TODO*///  break;
/*TODO*///case 0xE6: /*	   AND A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  AND_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xE7: /*	   RST $20 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 0x20;
/*TODO*///  break;
/*TODO*///case 0xE8: /*	   ADD SP,n8 */
/*TODO*////*
/*TODO*/// *	 Z - Reset.
/*TODO*/// *	 N - Reset.
/*TODO*/// *	 H - Set or reset according to operation.
/*TODO*/// *	 C - Set or reset according to operation.
/*TODO*/// */
/*TODO*///
/*TODO*///  {
/*TODO*///	register INT32 n;
/*TODO*///	register UINT32 r1, r2;
/*TODO*///	register UINT8 f;
/*TODO*///
/*TODO*///    /* printf( "Hmmm.. ADD SP,n8\n" ); */
/*TODO*///
/*TODO*///	n = (INT32) ((INT8) mem_ReadByte (Regs.w.PC++));
/*TODO*///    r1 = Regs.w.SP + n;
/*TODO*///    r2 = (Regs.w.SP & 0xFFF) + (n & 0xFFF);
/*TODO*///
/*TODO*///    if (r1 > 0xFFFF)
/*TODO*///    {
/*TODO*///      f = FLAG_C;
/*TODO*///    }
/*TODO*///    else
/*TODO*///    {
/*TODO*///      f = 0;
/*TODO*///    }
/*TODO*///
/*TODO*///    if (r2 > 0xFFF)
/*TODO*///    {
/*TODO*///      f |= FLAG_H;
/*TODO*///    }
/*TODO*///
/*TODO*///	Regs.w.SP = (UINT16) r1;
/*TODO*///    Regs.b.F = f;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xE9: /*	   JP (HL) */
/*TODO*///
/*TODO*///  Regs.w.PC = Regs.w.HL;
/*TODO*///  break;
                case 0xEA:
                    mem_WriteByte(mem_ReadWord(Regs.PC), Regs.A);/*	   LD (n16),A */
                    Regs.PC = (Regs.PC + 2) & 0xFFFF;
                    break;
                /*TODO*///case 0xEB: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xEC: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xED: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xEE: /*	   XOR A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  XOR_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xEF: /*	   RST $28 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 0x28;
/*TODO*///  break;
/*TODO*///case 0xF0: /*	   LD A,($FF00+n8) */
/*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte (0xFF00 + mem_ReadByte (Regs.w.PC++));
/*TODO*///  break;
/*TODO*///case 0xF1: /*	   POP AF */
/*TODO*///
/*TODO*///  Regs.w.AF = (UINT16) (mem_ReadWord (Regs.w.SP) & 0xFFF0);
/*TODO*///  Regs.w.SP += 2;
/*TODO*///  break;
/*TODO*///case 0xF2: /*	   LD A,($FF00+C) */
/*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte ((UINT16) (0xFF00 + Regs.b.C));
/*TODO*///  break;
                case 0xF3:
                    Regs.enable &= ~IME;
                    /*	   DI */
                    break;
                /*TODO*///case 0xF4: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xF5: /*	   PUSH AF */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, (UINT16) (Regs.w.AF & 0xFFF0));
/*TODO*///  break;
/*TODO*///case 0xF6: /*	   OR A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  OR_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xF7: /*	   RST $30 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 0x30;
/*TODO*///  break;
/*TODO*///case 0xF8: /*	   LD HL,SP+n8 */
/*TODO*////*
/*TODO*/// *	 n = one UINT8 signed immediate value.
/*TODO*/// * Flags affected:
/*TODO*/// *	 Z - Reset.
/*TODO*/// *	 N - Reset.
/*TODO*/// *	 H - Set or reset according to operation.
/*TODO*/// *	 C - Set or reset according to operation.
/*TODO*/// *
/*TODO*/// */
/*TODO*///
/*TODO*///  {
/*TODO*///	register INT32 n;
/*TODO*///	register UINT32 r1, r2;
/*TODO*///	register UINT8 f;
/*TODO*///
/*TODO*///	n = (INT32) ((INT8) mem_ReadByte (Regs.w.PC++));
/*TODO*///    r1 = Regs.w.SP + n;
/*TODO*///    r2 = (Regs.w.SP & 0xFFF) + (n & 0xFFF);
/*TODO*///
/*TODO*///    if (r1 > 0xFFFF)
/*TODO*///    {
/*TODO*///      f = FLAG_C;
/*TODO*///    }
/*TODO*///    else
/*TODO*///    {
/*TODO*///      f = 0;
/*TODO*///    }
/*TODO*///
/*TODO*///    if (r2 > 0xFFF)
/*TODO*///    {
/*TODO*///      f |= FLAG_H;
/*TODO*///    }
/*TODO*///
/*TODO*///	Regs.w.HL = (UINT16) r1;
/*TODO*///    Regs.b.F = f;
/*TODO*///  }
/*TODO*///  break;
/*TODO*///case 0xF9: /*	   LD SP,HL */
/*TODO*///
/*TODO*///  Regs.w.SP = Regs.w.HL;
/*TODO*///  break;
/*TODO*///case 0xFA: /*	   LD A,(n16) */
/*TODO*///
/*TODO*///  Regs.b.A = mem_ReadByte (mem_ReadWord (Regs.w.PC));
/*TODO*///  Regs.PC = (Regs.PC + 2) & 0xFFFF;
/*TODO*///  break;
/*TODO*///case 0xFB: /*	   EI */
/*TODO*///
/*TODO*///  Regs.w.enable |= IME;
/*TODO*///  CheckInterrupts = 1;
/*TODO*///  break;
/*TODO*///case 0xFC: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xFD: /*	   EH? */
/*TODO*///  break;
/*TODO*///case 0xFE: /*	   CP A,n8 */
/*TODO*///
/*TODO*///  x = mem_ReadByte (Regs.w.PC++);
/*TODO*///  CP_A_X (x)
/*TODO*///  break;
/*TODO*///case 0xFF: /*	   RST $38 */
/*TODO*///
/*TODO*///  Regs.w.SP -= 2;
/*TODO*///  mem_WriteWord (Regs.w.SP, Regs.w.PC);
/*TODO*///  Regs.w.PC = 0x38;
/*TODO*///  break;
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
