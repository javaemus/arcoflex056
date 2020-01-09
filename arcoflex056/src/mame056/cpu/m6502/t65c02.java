/*****************************************************************************
 *
 *	 tbl65c02.c
 *	 65c02 opcode functions and function pointer table
 *
 *	 Copyright (c) 1998,1999,2000 Juergen Buchmueller, all rights reserved.
 *
 *	 - This source code is released as freeware for non-commercial purposes.
 *	 - You are free to use and redistribute this code in modified or
 *	   unmodified form, provided you list me in the credits.
 *	 - If you modify this source code, you must add a notice to each modified
 *	   source file that it has been changed.  If you're a nice person, you
 *	   will clearly mark each change too.  :)
 *	 - If you wish to use this for commercial purposes, please contact me at
 *	   pullmoll@t-online.de
 *	 - The author of this copywritten work reserves the right to change the
 *	   terms of its usage and license at any time, including retroactively
 *	 - This entire notice must remain in the source code.
 *
 *****************************************************************************/

package mame056.cpu.m6502;

import static mame056.cpu.m6502.m6502.*;
import static mame056.cpu.m6502.ops02H.AND;
import static mame056.cpu.m6502.ops02H.BIT;
import static mame056.cpu.m6502.ops02H.BRA;
import static mame056.cpu.m6502.ops02H.CMP;
import static mame056.cpu.m6502.ops02H.EOR;
import static mame056.cpu.m6502.ops02H.JMP;
import static mame056.cpu.m6502.ops02H.LDA;
import static mame056.cpu.m6502.ops02H.ORA;
import static mame056.cpu.m6502.ops02H.RD_ABS;
import static mame056.cpu.m6502.ops02H.RD_ABX;
import static mame056.cpu.m6502.ops02H.RD_IMM;
import static mame056.cpu.m6502.ops02H.RD_ZPG;
import static mame056.cpu.m6502.ops02H.RD_ZPX;
import static mame056.cpu.m6502.ops02H.STA;
import static mame056.cpu.m6502.ops02H.WB_EA;
import static mame056.cpu.m6502.ops02H.WR_ABS;
import static mame056.cpu.m6502.ops02H.WR_ABX;
import static mame056.cpu.m6502.ops02H.WR_ZPG;
import static mame056.cpu.m6502.ops02H.WR_ZPX;
import static mame056.cpu.m6502.opsc02H.*;
import static mame056.cpu.m6502.ill02H.*;
import static mame056.cpu.m6502.t6502.*;

/**
 * ported to v0.56
 */
public class t65c02 {
/*****************************************************************************
 *****************************************************************************
 *
 *	 overrides for 65C02 opcodes
 *
 *****************************************************************************
 * op	 temp	  cycles			 rdmem	 opc  wrmem   ********************/
    //OP(00)
    static opcode m65c02_00 = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 7;
            BRK();
        }
    };/* 7 BRK */

    static opcode m65c02_20 = m6502_20;									/* 6 JSR ABS */
    static opcode m65c02_40 = m6502_40;									/* 6 RTI */
    static opcode m65c02_60 = m6502_60;									/* 6 RTS */
    
    static opcode m65c02_80 = new opcode() {
        public void handler() {
            int tmp=0;
            BRA(true);
        }
    }; /* 2 BRA */  

    static opcode m65c02_a0 = m6502_a0;									/* 2 LDY IMM */
    static opcode m65c02_c0 = m6502_c0;									/* 2 CPY IMM */
    static opcode m65c02_e0 = m6502_e0;									/* 2 CPX IMM */

static opcode m65c02_10 = m6502_10;									/* 2 BPL */
static opcode m65c02_30 = m6502_30;									/* 2 BMI */
static opcode m65c02_50 = m6502_50;									/* 2 BVC */
static opcode m65c02_70 = m6502_70;									/* 2 BVS */
static opcode m65c02_90 = m6502_90;									/* 2 BCC */
static opcode m65c02_b0 = m6502_b0;									/* 2 BCS */
static opcode m65c02_d0 = m6502_d0;									/* 2 BNE */
static opcode m65c02_f0 = m6502_f0;									/* 2 BEQ */

static opcode m65c02_01 = m6502_01;									/* 6 ORA IDX */
static opcode m65c02_21 = m6502_21;									/* 6 AND IDX */
static opcode m65c02_41 = m6502_41;									/* 6 EOR IDX */
static opcode m65c02_61 = m6502_61;									/* 6 ADC IDX */
static opcode m65c02_81 = m6502_81;									/* 6 STA IDX */
static opcode m65c02_a1 = m6502_a1;									/* 6 LDA IDX */
static opcode m65c02_c1 = m6502_c1;									/* 6 CMP IDX */
static opcode m65c02_e1 = m6502_e1;									/* 6 SBC IDX */

static opcode m65c02_11 = m6502_11;									/* 5 ORA IDY; */
static opcode m65c02_31 = m6502_31;									/* 5 AND IDY; */
static opcode m65c02_51 = m6502_51;									/* 5 EOR IDY; */
static opcode m65c02_71 = m6502_71;									/* 5 ADC IDY; */
static opcode m65c02_91 = m6502_91;									/* 6 STA IDY; */
static opcode m65c02_b1 = m6502_b1;									/* 5 LDA IDY; */
static opcode m65c02_d1 = m6502_d1;									/* 5 CMP IDY; */
static opcode m65c02_f1 = m6502_f1;									/* 5 SBC IDY; */

static opcode m65c02_02 = m6502_02;									/* 2 ILL */
static opcode m65c02_22 = m6502_22;									/* 2 ILL */
static opcode m65c02_42 = m6502_42;									/* 2 ILL */
static opcode m65c02_62 = m6502_62;									/* 2 ILL */
static opcode m65c02_82 = m6502_82;									/* 2 ILL */
static opcode m65c02_a2 = m6502_a2;									/* 2 LDX IMM */
static opcode m65c02_c2 = m6502_c2;									/* 2 ILL */
static opcode m65c02_e2 = m6502_e2;									/* 2 ILL */

/*TODO*///#ifndef CORE_M65CE02
    static opcode m65c02_12 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            ORA(tmp);
        }
    }; /* 3 ORA ZPI */

    static opcode m65c02_32 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            AND(tmp);
        } 
    }; /* 3 AND ZPI */

    static opcode m65c02_52 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            EOR(tmp);
        } 
    }; /* 3 EOR ZPI */

    static opcode m65c02_72 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            ADC(tmp);
        } 
    }; /* 3 ADC ZPI */

    static opcode m65c02_92 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 4;
            tmp=STA(); 
            WR_ZPI(tmp); 
        } 
    }; /* 3 STA ZPI */

    static opcode m65c02_b2 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            LDA(tmp);
        } 
    }; /* 3 LDA ZPI */

    static opcode m65c02_d2 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            CMP(tmp);
        } 
    }; /* 3 CMP ZPI */

    static opcode m65c02_f2 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            RD_ZPI(tmp); 
            SBC(tmp);
        } 
    }; /* 3 SBC ZPI */

/*TODO*///#endif

static opcode m65c02_03 = m6502_03;									/* 2 ILL */
static opcode m65c02_23 = m6502_23;									/* 2 ILL */
static opcode m65c02_43 = m6502_43;									/* 2 ILL */
static opcode m65c02_63 = m6502_63;									/* 2 ILL */
static opcode m65c02_83 = m6502_83;									/* 2 ILL */
static opcode m65c02_a3 = m6502_a3;									/* 2 ILL */
static opcode m65c02_c3 = m6502_c3;									/* 2 ILL */
static opcode m65c02_e3 = m6502_e3;									/* 2 ILL */

static opcode m65c02_13 = m6502_13;									/* 2 ILL */
static opcode m65c02_33 = m6502_33;									/* 2 ILL */
static opcode m65c02_53 = m6502_53;									/* 2 ILL */
static opcode m65c02_73 = m6502_73;									/* 2 ILL */
static opcode m65c02_93 = m6502_93;									/* 2 ILL */
static opcode m65c02_b3 = m6502_b3;									/* 2 ILL */
static opcode m65c02_d3 = m6502_d3;									/* 2 ILL */
static opcode m65c02_f3 = m6502_f3;									/* 2 ILL */

    static opcode m65c02_04 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            tmp=RD_ZPG(); 
            TSB(tmp); 
            WB_EA(tmp);
        } 
    }; /* 3 TSB ZPG */

static opcode m65c02_24 = m6502_24;									/* 3 BIT ZPG */
static opcode m65c02_44 = m6502_44;									/* 2 ILL */

    static opcode m65c02_64 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 2;
            STZ(tmp); 
            WR_ZPG(tmp);
        } 
    }; /* 3 STZ ZPG */

static opcode m65c02_84 = m6502_84;									/* 3 STY ZPG */
static opcode m65c02_a4 = m6502_a4;									/* 3 LDY ZPG */
static opcode m65c02_c4 = m6502_c4;									/* 3 CPY ZPG */
static opcode m65c02_e4 = m6502_e4;									/* 3 CPX ZPG */

    static opcode m65c02_14 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 3; 
            tmp=RD_ZPG(); 
            TRB(tmp); 
            WB_EA(tmp);  
        } 
    }; /* 3 TRB ZPG */

    static opcode m65c02_34 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 4; 
            tmp=RD_ZPX(); 
            BIT(tmp);
        } 
    }; /* 4 BIT ZPX */

static opcode m65c02_54 = m6502_54;									/* 2 ILL */

    static opcode m65c02_74 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 4;
            STZ(tmp);
            WR_ZPX(tmp); 
        } 
    }; /* 4 STZ ZPX */

static opcode m65c02_94 = m6502_94;									/* 4 STY ZPX */
static opcode m65c02_b4 = m6502_b4;									/* 4 LDY ZPX */
static opcode m65c02_d4 = m6502_d4;									/* 2 ILL */
static opcode m65c02_f4 = m6502_f4;									/* 2 ILL */

static opcode m65c02_05 = m6502_05;									/* 3 ORA ZPG */
static opcode m65c02_25 = m6502_25;									/* 3 AND ZPG */
static opcode m65c02_45 = m6502_45;									/* 3 EOR ZPG */
static opcode m65c02_65 = m6502_65;									/* 3 ADC ZPG */
static opcode m65c02_85 = m6502_85;									/* 3 STA ZPG */
static opcode m65c02_a5 = m6502_a5;									/* 3 LDA ZPG */
static opcode m65c02_c5 = m6502_c5;									/* 3 CMP ZPG */
static opcode m65c02_e5 = m6502_e5;									/* 3 SBC ZPG */

static opcode m65c02_15 = m6502_15;									/* 4 ORA ZPX */
static opcode m65c02_35 = m6502_35;									/* 4 AND ZPX */
static opcode m65c02_55 = m6502_55;									/* 4 EOR ZPX */
static opcode m65c02_75 = m6502_75;									/* 4 ADC ZPX */
static opcode m65c02_95 = m6502_95;									/* 4 STA ZPX */
static opcode m65c02_b5 = m6502_b5;									/* 4 LDA ZPX */
static opcode m65c02_d5 = m6502_d5;									/* 4 CMP ZPX */
static opcode m65c02_f5 = m6502_f5;									/* 4 SBC ZPX */

static opcode m65c02_06 = m6502_06;									/* 5 ASL ZPG */
static opcode m65c02_26 = m6502_26;									/* 5 ROL ZPG */
static opcode m65c02_46 = m6502_46;									/* 5 LSR ZPG */
static opcode m65c02_66 = m6502_66;									/* 5 ROR ZPG */
static opcode m65c02_86 = m6502_86;									/* 3 STX ZPG */
static opcode m65c02_a6 = m6502_a6;									/* 3 LDX ZPG */
static opcode m65c02_c6 = m6502_c6;									/* 5 DEC ZPG */
static opcode m65c02_e6 = m6502_e6;									/* 5 INC ZPG */

static opcode m65c02_16 = m6502_16;									/* 6 ASL ZPX */
static opcode m65c02_36 = m6502_36;									/* 6 ROL ZPX */
static opcode m65c02_56 = m6502_56;									/* 6 LSR ZPX */
static opcode m65c02_76 = m6502_76;									/* 6 ROR ZPX */
static opcode m65c02_96 = m6502_96;									/* 4 STX ZPY */
static opcode m65c02_b6 = m6502_b6;									/* 4 LDX ZPY */
static opcode m65c02_d6 = m6502_d6;									/* 6 DEC ZPX */
static opcode m65c02_f6 = m6502_f6;									/* 6 INC ZPX */

    static opcode m65c02_07 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 0);
            WB_EA(tmp);
        } 
    }; /* 5 RMB0 ZPG */

    static opcode m65c02_27 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 2);
            WB_EA(tmp);
        } 
    }; /* 5 RMB2 ZPG */

    static opcode m65c02_47 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 4);
            WB_EA(tmp);
        } 
    }; /* 5 RMB4 ZPG */

    static opcode m65c02_67 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 6);
            WB_EA(tmp);
        } 
    }; /* 5 RMB6 ZPG */

    static opcode m65c02_87 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 0);
            WB_EA(tmp);
        } 
    }; /* 5 SMB0 ZPG */

    static opcode m65c02_a7 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 2);
            WB_EA(tmp);
        } 
    }; /* 5 SMB2 ZPG */

    static opcode m65c02_c7 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 4);
            WB_EA(tmp);
        } 
    }; /* 5 SMB4 ZPG */

    static opcode m65c02_e7 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 6);
            WB_EA(tmp);
        } 
    }; /* 5 SMB6 ZPG */


    static opcode m65c02_17 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 1);
            WB_EA(tmp);
        } 
    }; /* 5 RMB1 ZPG */

    static opcode m65c02_37 = new opcode() {
        public void handler() {int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 3);
            WB_EA(tmp);
        } 
    }; /* 5 RMB3 ZPG */
    
    static opcode m65c02_57 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 5);
            WB_EA(tmp);
        } 
    }; /* 5 RMB5 ZPG */
    
    static opcode m65c02_77 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            RMB(tmp, 7);
            WB_EA(tmp);
        } 
    }; /* 5 RMB7 ZPG */
    
    static opcode m65c02_97 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 1);
            WB_EA(tmp);
        } 
    }; /* 5 SMB1 ZPG */
    
    static opcode m65c02_b7 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 3);
            WB_EA(tmp);
        } 
    }; /* 5 SMB3 ZPG */
    
    static opcode m65c02_d7 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 5);
            WB_EA(tmp);
        } 
    }; /* 5 SMB5 ZPG */
    
    static opcode m65c02_f7 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            SMB(tmp, 7);
            WB_EA(tmp);
        } 
    }; /* 5 SMB7 ZPG */
    

static opcode m65c02_08 = m6502_08;									/* 3 PHP */
static opcode m65c02_28 = m6502_28;									/* 4 PLP */
static opcode m65c02_48 = m6502_48;									/* 3 PHA */
static opcode m65c02_68 = m6502_68;									/* 4 PLA */
static opcode m65c02_88 = m6502_88;									/* 2 DEY */
static opcode m65c02_a8 = m6502_a8;									/* 2 TAY */
static opcode m65c02_c8 = m6502_c8;									/* 2 INY */
static opcode m65c02_e8 = m6502_e8;									/* 2 INX */

static opcode m65c02_18 = m6502_18;									/* 2 CLC */
static opcode m65c02_38 = m6502_38;									/* 2 SEC */
static opcode m65c02_58 = m6502_58;									/* 2 CLI */
static opcode m65c02_78 = m6502_78;									/* 2 SEI */
static opcode m65c02_98 = m6502_98;									/* 2 TYA */
static opcode m65c02_b8 = m6502_b8;									/* 2 CLV */
static opcode m65c02_d8 = m6502_d8;									/* 2 CLD */
static opcode m65c02_f8 = m6502_f8;									/* 2 SED */

static opcode m65c02_09 = m6502_09;									/* 2 ORA IMM */
static opcode m65c02_29 = m6502_29;									/* 2 AND IMM */
static opcode m65c02_49 = m6502_49;									/* 2 EOR IMM */
static opcode m65c02_69 = m6502_69;									/* 2 ADC IMM */

    static opcode m65c02_89 = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 2; 
            tmp=RD_IMM(); 
            BIT(tmp);		  
        } 
    }; /* 2 BIT IMM */
    
static opcode m65c02_a9 = m6502_a9;									/* 2 LDA IMM */
static opcode m65c02_c9 = m6502_c9;									/* 2 CMP IMM */
static opcode m65c02_e9 = m6502_e9;									/* 2 SBC IMM */

static opcode m65c02_19 = m6502_19;									/* 4 ORA ABY */
static opcode m65c02_39 = m6502_39;									/* 4 AND ABY */
static opcode m65c02_59 = m6502_59;									/* 4 EOR ABY */
static opcode m65c02_79 = m6502_79;									/* 4 ADC ABY */
static opcode m65c02_99 = m6502_99;									/* 5 STA ABY */
static opcode m65c02_b9 = m6502_b9;									/* 4 LDA ABY */
static opcode m65c02_d9 = m6502_d9;									/* 4 CMP ABY */
static opcode m65c02_f9 = m6502_f9;									/* 4 SBC ABY */

static opcode m65c02_0a = m6502_0a;									/* 2 ASL */
static opcode m65c02_2a = m6502_2a;									/* 2 ROL */
static opcode m65c02_4a = m6502_4a;									/* 2 LSR */
static opcode m65c02_6a = m6502_6a;									/* 2 ROR */
static opcode m65c02_8a = m6502_8a;									/* 2 TXA */
static opcode m65c02_aa = m6502_aa;									/* 2 TAX */
static opcode m65c02_ca = m6502_ca;									/* 2 DEX */
static opcode m65c02_ea = m6502_ea;									/* 2 NOP */

    static opcode m65c02_1a = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 2;
            INA();
        } 
    }; /* 2 INA */

    static opcode m65c02_3a = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 2;
            DEA();
        } 
    }; /* 2 DEA */

    static opcode m65c02_5a = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 3;
            PHY();
        } 
    }; /* 3 PHY */

    static opcode m65c02_7a = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 4;
            PLY();
        } 
    }; /* 4 PLY */

static opcode m65c02_9a = m6502_9a;									/* 2 TXS */
static opcode m65c02_ba = m6502_ba;									/* 2 TSX */

    static opcode m65c02_da = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 3;
            PHX();
        } 
    }; /* 3 PHX */

    static opcode m65c02_fa = new opcode() {
        public void handler() {
            m6502_ICount[0] -= 4;
            PLX();
        } 
    }; /* 4 PLX */

static opcode m65c02_0b = m6502_0b;									/* 2 ILL */
static opcode m65c02_2b = m6502_2b;									/* 2 ILL */
static opcode m65c02_4b = m6502_4b;									/* 2 ILL */
static opcode m65c02_6b = m6502_6b;									/* 2 ILL */
static opcode m65c02_8b = m6502_8b;									/* 2 ILL */
static opcode m65c02_ab = m6502_ab;									/* 2 ILL */
static opcode m65c02_cb = m6502_cb;									/* 2 ILL */
static opcode m65c02_eb = m6502_eb;									/* 2 ILL */

static opcode m65c02_1b = m6502_1b;									/* 2 ILL */
static opcode m65c02_3b = m6502_3b;									/* 2 ILL */
static opcode m65c02_5b = m6502_5b;									/* 2 ILL */
static opcode m65c02_7b = m6502_7b;									/* 2 ILL */
static opcode m65c02_9b = m6502_9b;									/* 2 ILL */
static opcode m65c02_bb = m6502_bb;									/* 2 ILL */
static opcode m65c02_db = m6502_db;									/* 2 ILL */
static opcode m65c02_fb = m6502_fb;									/* 2 ILL */

    static opcode m65c02_0c = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 2; 
            tmp=RD_ABS(); 
            TSB(tmp); 
            WB_EA(tmp);
        } 
    }; /* 4 TSB ABS */

static opcode m65c02_2c = m6502_2c;									/* 4 BIT ABS */
static opcode m65c02_4c = m6502_4c;									/* 3 JMP ABS */

    static opcode m65c02_6c = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            EA_IND(); 
            JMP();
        } 
    }; /* 5 JMP IND */

static opcode m65c02_8c = m6502_8c;									/* 4 STY ABS */
static opcode m65c02_ac = m6502_ac;									/* 4 LDY ABS */
static opcode m65c02_cc = m6502_cc;									/* 4 CPY ABS */
static opcode m65c02_ec = m6502_ec;									/* 4 CPX ABS */

    static opcode m65c02_1c = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 4; 
            tmp=RD_ABS(); 
            TRB(tmp); 
            WB_EA(tmp);  
        } 
    }; /* 4 TRB ABS */

    static opcode m65c02_3c = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 4; 
            tmp=RD_ABX(); 
            BIT(tmp);
        } 
    }; /* 4 BIT ABX */

static opcode m65c02_5c = m6502_5c;									/* 2 ILL */

    static opcode m65c02_7c = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 2; 
            EA_IAX(tmp); 
            JMP();
        } 
    }; /* 6 JMP IAX */

    static opcode m65c02_9c = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 4;
            STZ(tmp); 
            WR_ABS(tmp); 
        } 
    }; /* 4 STZ ABS */
    
static opcode m65c02_bc = m6502_bc;									/* 4 LDY ABX */
static opcode m65c02_dc = m6502_dc;									/* 2 ILL */
static opcode m65c02_fc = m6502_fc;									/* 2 ILL */

static opcode m65c02_0d = m6502_0d;									/* 4 ORA ABS */
static opcode m65c02_2d = m6502_2d;									/* 4 AND ABS */
static opcode m65c02_4d = m6502_4d;									/* 4 EOR ABS */
static opcode m65c02_6d = m6502_6d;									/* 4 ADC ABS */
static opcode m65c02_8d = m6502_8d;									/* 4 STA ABS */
static opcode m65c02_ad = m6502_ad;									/* 4 LDA ABS */
static opcode m65c02_cd = m6502_cd;									/* 4 CMP ABS */
static opcode m65c02_ed = m6502_ed;									/* 4 SBC ABS */

static opcode m65c02_1d = m6502_1d;									/* 4 ORA ABX */
static opcode m65c02_3d = m6502_3d;									/* 4 AND ABX */
static opcode m65c02_5d = m6502_5d;									/* 4 EOR ABX */
static opcode m65c02_7d = m6502_7d;									/* 4 ADC ABX */
static opcode m65c02_9d = m6502_9d;									/* 5 STA ABX */
static opcode m65c02_bd = m6502_bd;									/* 4 LDA ABX */
static opcode m65c02_dd = m6502_dd;									/* 4 CMP ABX */
static opcode m65c02_fd = m6502_fd;									/* 4 SBC ABX */

static opcode m65c02_0e = m6502_0e;									/* 6 ASL ABS */
static opcode m65c02_2e = m6502_2e;									/* 6 ROL ABS */
static opcode m65c02_4e = m6502_4e;									/* 6 LSR ABS */
static opcode m65c02_6e = m6502_6e;									/* 6 ROR ABS */
static opcode m65c02_8e = m6502_8e;									/* 4 STX ABS */
static opcode m65c02_ae = m6502_ae;									/* 4 LDX ABS */
static opcode m65c02_ce = m6502_ce;									/* 6 DEC ABS */
static opcode m65c02_ee = m6502_ee;									/* 6 INC ABS */

static opcode m65c02_1e = m6502_1e;									/* 7 ASL ABX */
static opcode m65c02_3e = m6502_3e;									/* 7 ROL ABX */
static opcode m65c02_5e = m6502_5e;									/* 7 LSR ABX */
static opcode m65c02_7e = m6502_7e;									/* 7 ROR ABX */

    static opcode m65c02_9e = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5;
            STZ(tmp); 
            WR_ABX(tmp); 
        } 
    }; /* 5 STZ ABX */
    
static opcode m65c02_be = m6502_be;									/* 4 LDX ABY */
static opcode m65c02_de = m6502_de;									/* 7 DEC ABX */
static opcode m65c02_fe = m6502_fe;									/* 7 INC ABX */

    static opcode m65c02_0f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 0);
        } 
    }; /* 5 BBR0 ZPG */
    
    static opcode m65c02_2f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 2);	  
        } 
    }; /* 5 BBR2 ZPG */
    
    static opcode m65c02_4f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 4);
        } 
    }; /* 5 BBR4 ZPG */
    
    static opcode m65c02_6f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 6);
        } 
    }; /* 5 BBR6 ZPG */
    
    static opcode m65c02_8f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 0);
        } 
    }; /* 5 BBS0 ZPG */
    
    static opcode m65c02_af = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 2);
        } 
    }; /* 5 BBS2 ZPG */
    
    static opcode m65c02_cf = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 4);
        } 
    }; /* 5 BBS4 ZPG */
    
    static opcode m65c02_ef = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 6);
        } 
    }; /* 5 BBS6 ZPG */
    
    static opcode m65c02_1f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 1);
        } 
    }; /* 5 BBR1 ZPG */
    
    static opcode m65c02_3f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 3);
        } 
    }; /* 5 BBR3 ZPG */
    
    static opcode m65c02_5f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 5);
        } 
    }; /* 5 BBR5 ZPG */
    
    static opcode m65c02_7f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBR(tmp, 7);
        } 
    }; /* 5 BBR7 ZPG */
    
    static opcode m65c02_9f = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 1);
        } 
    }; /* 5 BBS1 ZPG */
    
    static opcode m65c02_bf = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 3);
        } 
    }; /* 5 BBS3 ZPG */
    
    static opcode m65c02_df = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5; 
            tmp=RD_ZPG(); 
            BBS(tmp, 5);
        } 
    }; /* 5 BBS5 ZPG */
    
    static opcode m65c02_ff = new opcode() {
        public void handler() {
            int tmp=0;
            m6502_ICount[0] -= 5;
            tmp=RD_ZPG();
            BBS(tmp, 7);
        } 
    }; /* 5 BBS7 ZPG */

    public static opcode[] insn65c02 = {
	m65c02_00,m65c02_01,m65c02_02,m65c02_03,m65c02_04,m65c02_05,m65c02_06,m65c02_07,
	m65c02_08,m65c02_09,m65c02_0a,m65c02_0b,m65c02_0c,m65c02_0d,m65c02_0e,m65c02_0f,
	m65c02_10,m65c02_11,m65c02_12,m65c02_13,m65c02_14,m65c02_15,m65c02_16,m65c02_17,
	m65c02_18,m65c02_19,m65c02_1a,m65c02_1b,m65c02_1c,m65c02_1d,m65c02_1e,m65c02_1f,
	m65c02_20,m65c02_21,m65c02_22,m65c02_23,m65c02_24,m65c02_25,m65c02_26,m65c02_27,
	m65c02_28,m65c02_29,m65c02_2a,m65c02_2b,m65c02_2c,m65c02_2d,m65c02_2e,m65c02_2f,
	m65c02_30,m65c02_31,m65c02_32,m65c02_33,m65c02_34,m65c02_35,m65c02_36,m65c02_37,
	m65c02_38,m65c02_39,m65c02_3a,m65c02_3b,m65c02_3c,m65c02_3d,m65c02_3e,m65c02_3f,
	m65c02_40,m65c02_41,m65c02_42,m65c02_43,m65c02_44,m65c02_45,m65c02_46,m65c02_47,
	m65c02_48,m65c02_49,m65c02_4a,m65c02_4b,m65c02_4c,m65c02_4d,m65c02_4e,m65c02_4f,
	m65c02_50,m65c02_51,m65c02_52,m65c02_53,m65c02_54,m65c02_55,m65c02_56,m65c02_57,
	m65c02_58,m65c02_59,m65c02_5a,m65c02_5b,m65c02_5c,m65c02_5d,m65c02_5e,m65c02_5f,
	m65c02_60,m65c02_61,m65c02_62,m65c02_63,m65c02_64,m65c02_65,m65c02_66,m65c02_67,
	m65c02_68,m65c02_69,m65c02_6a,m65c02_6b,m65c02_6c,m65c02_6d,m65c02_6e,m65c02_6f,
	m65c02_70,m65c02_71,m65c02_72,m65c02_73,m65c02_74,m65c02_75,m65c02_76,m65c02_77,
	m65c02_78,m65c02_79,m65c02_7a,m65c02_7b,m65c02_7c,m65c02_7d,m65c02_7e,m65c02_7f,
	m65c02_80,m65c02_81,m65c02_82,m65c02_83,m65c02_84,m65c02_85,m65c02_86,m65c02_87,
	m65c02_88,m65c02_89,m65c02_8a,m65c02_8b,m65c02_8c,m65c02_8d,m65c02_8e,m65c02_8f,
	m65c02_90,m65c02_91,m65c02_92,m65c02_93,m65c02_94,m65c02_95,m65c02_96,m65c02_97,
	m65c02_98,m65c02_99,m65c02_9a,m65c02_9b,m65c02_9c,m65c02_9d,m65c02_9e,m65c02_9f,
	m65c02_a0,m65c02_a1,m65c02_a2,m65c02_a3,m65c02_a4,m65c02_a5,m65c02_a6,m65c02_a7,
	m65c02_a8,m65c02_a9,m65c02_aa,m65c02_ab,m65c02_ac,m65c02_ad,m65c02_ae,m65c02_af,
	m65c02_b0,m65c02_b1,m65c02_b2,m65c02_b3,m65c02_b4,m65c02_b5,m65c02_b6,m65c02_b7,
	m65c02_b8,m65c02_b9,m65c02_ba,m65c02_bb,m65c02_bc,m65c02_bd,m65c02_be,m65c02_bf,
	m65c02_c0,m65c02_c1,m65c02_c2,m65c02_c3,m65c02_c4,m65c02_c5,m65c02_c6,m65c02_c7,
	m65c02_c8,m65c02_c9,m65c02_ca,m65c02_cb,m65c02_cc,m65c02_cd,m65c02_ce,m65c02_cf,
	m65c02_d0,m65c02_d1,m65c02_d2,m65c02_d3,m65c02_d4,m65c02_d5,m65c02_d6,m65c02_d7,
	m65c02_d8,m65c02_d9,m65c02_da,m65c02_db,m65c02_dc,m65c02_dd,m65c02_de,m65c02_df,
	m65c02_e0,m65c02_e1,m65c02_e2,m65c02_e3,m65c02_e4,m65c02_e5,m65c02_e6,m65c02_e7,
	m65c02_e8,m65c02_e9,m65c02_ea,m65c02_eb,m65c02_ec,m65c02_ed,m65c02_ee,m65c02_ef,
	m65c02_f0,m65c02_f1,m65c02_f2,m65c02_f3,m65c02_f4,m65c02_f5,m65c02_f6,m65c02_f7,
	m65c02_f8,m65c02_f9,m65c02_fa,m65c02_fb,m65c02_fc,m65c02_fd,m65c02_fe,m65c02_ff
    };
    
    /*public abstract interface opcode {
        public abstract void handler();
    }*/
    
}
