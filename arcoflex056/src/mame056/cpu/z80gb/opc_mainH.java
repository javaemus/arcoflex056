package mame056.cpu.z80gb;

import static mame056.cpu.z80gb.daa_tabH.DAATable;
import static mame056.cpu.z80gb.z80gbH.*;
import static mame056.cpu.z80gb.z80gb.*;
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

public class opc_mainH {
    
    public static void SBC_A_X(int x)
    {
      int r1,r2;
      int f;
      r1=((Regs.A&0xF)-((x)&0xF)-((Regs.F&FLAG_C)!=0?1:0))&0xffff;
      r2=(Regs.A-(x)-((Regs.F&FLAG_C)!=0?1:0))&0xffff;
      Regs.A=r2&0xff;
      if( (r2&0xff)==0 ) f=FLAG_N|FLAG_Z;
        else f=FLAG_N;
      if( r2>0xFF ) f|=FLAG_C;
      if( r1>0xF )  f|=FLAG_H;
      Regs.F=f;
    }
    
}
