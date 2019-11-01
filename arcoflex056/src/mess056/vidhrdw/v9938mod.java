/*
 * ported to v0.56
 * using automatic conversion tool v0.01
 */ 
package mess056.vidhrdw;

import static common.ptr.*;
import static common.subArrays.*;
import static mame056.mame.Machine;
import static mess056.vidhrdw.v9938H.*;
import static mess056.vidhrdw.v9938.V9938_WIDTH;
import static mess056.vidhrdw.v9938._vdp;
import static mess056.vidhrdw.v9938.pal_ind16;

public class v9938mod {
    
    public static abstract interface ModeVisible_8_HandlersPtr {
        public abstract void handler(UBytePtr col, int line);
    }
    
    public static abstract interface ModeVisible_16_HandlersPtr {
        public abstract void handler(UShortPtr col, int line);
    }
    
    public static abstract interface ModeBorder_8_HandlersPtr {
        public abstract void handler(UBytePtr col);
    }
    
    public static abstract interface ModeBorder_16_HandlersPtr {
        public abstract void handler(UShortPtr col);
    }
    
    public static abstract interface ModeSprites_HandlersPtr {
        public abstract void handler(int line, UBytePtr col);
    }
    
    public static abstract interface ModeDraw_Sprites_HandlersPtr {
        public abstract void handler(UShortPtr ln, UBytePtr col);
    }
    
    public static ModeVisible_8_HandlersPtr v9938_mode_text1_8 = new ModeVisible_8_HandlersPtr() {
        public void handler(UBytePtr ln, int line) {
            int pattern, x, xx, name, xxx;	
            int fg, bg, pen;	
            UBytePtr nametbl, patterntbl;	

            patterntbl = new UBytePtr(_vdp.vram, (_vdp.contReg[4] << 11));
            nametbl = new UBytePtr(_vdp.vram, (_vdp.contReg[2] << 10));

        fg = Machine.pens[pal_ind16[_vdp.contReg[7] >> 4]];	
        bg = Machine.pens[pal_ind16[_vdp.contReg[7] & 15]];	

            name = (line/8)*40;	

            pen = Machine.pens[pal_ind16[(_vdp.contReg[7]&0x0f)]];	

            xxx = _vdp.offset_x + 8;	
    if (V9938_WIDTH > 512){
            xxx *= 2;
    }
            while (xxx-- !=0) {ln.writeinc( pen );}

            for (x=0;x<40;x++)	
                    {	
                    pattern = patterntbl.read((nametbl.read(name) * 8) + 	
                            ((line + _vdp.contReg[23]) & 7));	
                    for (xx=0;xx<6;xx++)	
                            {	
                            ln.writeinc( (pattern & 0x80)!=0 ? fg : bg );
    if (V9938_WIDTH > 512){
                            ln.writeinc( (pattern & 0x80)!=0 ? fg : bg );
    }
                            pattern <<= 1;	
                            }	
                    /* width height 212, characters start repeating at the bottom */ 
                    name = (name + 1) & 0x3ff;	
                    }	

            xxx = (16 - _vdp.offset_x) + 8;	
    if (V9938_WIDTH > 512){
            xxx *= 2;
    }
            while (xxx-- !=0) ln.writeinc( pen );
            if (_vdp.size_now != RENDER_HIGH) _vdp.size_now = RENDER_LOW;
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_text1_16 = new ModeVisible_16_HandlersPtr() {
        public void handler(UShortPtr ln, int line) {
            int pattern, x, xx, name, xxx;	
            int fg, bg, pen;	
            UBytePtr nametbl, patterntbl;	

            patterntbl = new UBytePtr(_vdp.vram, (_vdp.contReg[4] << 11) );
            nametbl = new UBytePtr(_vdp.vram, (_vdp.contReg[2] << 10) );
	
            fg = Machine.pens[pal_ind16[_vdp.contReg[7] >> 4]];
            bg = Machine.pens[pal_ind16[_vdp.contReg[7] & 15]];
	
            name = (line/8)*40;
	
            pen = Machine.pens[pal_ind16[(_vdp.contReg[7]&0x0f)]];	
	
            xxx = _vdp.offset_x + 8;	
            if (V9938_WIDTH > 512){
                    xxx *= 2;
            }
            while (xxx-- != 0){
                ln.write((char) pen);
                ln.inc();
            }	

            for (x=0;x<40;x++)	
            {	
                    pattern = patterntbl.read((nametbl.read(name) * 8) + 	
                            ((line + _vdp.contReg[23]) & 7));	
                    for (xx=0;xx<6;xx++)	
                    {	
                            ln.write((char) ((pattern & 0x80)!=0 ? fg : bg));
                            ln.inc();
                            if (V9938_WIDTH > 512){
                                ln.write((char) ((pattern & 0x80)!=0 ? fg : bg));
                                ln.inc();
                            }
                            pattern <<= 1;	
                    }	
                    /* width height 212, characters start repeating at the bottom */ 
                    name = (name + 1) & 0x3ff;	
            }	

            xxx = (16 - _vdp.offset_x) + 8;	
            if (V9938_WIDTH > 512){
                    xxx *= 2;
            }
            while (xxx-- != 0){
                ln.write((char) pen);
                ln.inc();
            }
            
            if (_vdp.size_now != RENDER_HIGH) _vdp.size_now = RENDER_LOW;
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_text1_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
            
    public static ModeVisible_16_HandlersPtr v9938_mode_text1_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
            
    public static ModeBorder_8_HandlersPtr v9938_default_border_8 = new ModeBorder_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_16_HandlersPtr v9938_default_border_16 = new ModeBorder_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln) {
           int pen;
            int	i;

            pen = Machine.pens[pal_ind16[(_vdp.contReg[7]&0x0f)]];
            i = V9938_WIDTH;
            while (i-- != 0) {
                ln.write((char) pen);
                ln.inc();
            }

            if (_vdp.size_now != RENDER_HIGH) _vdp.size_now = RENDER_LOW;
        }
    };
    
    public static ModeBorder_8_HandlersPtr v9938_default_border_8s = new ModeBorder_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_16_HandlersPtr v9938_default_border_16s = new ModeBorder_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_multi_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_multi_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_multi_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_multi_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_default_draw_sprite_8 = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_default_draw_sprite_8s = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_default_draw_sprite_16 = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_default_draw_sprite_16s = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic1_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic1_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic1_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic1_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic23_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic23_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic23_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic23_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic4_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic4_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic4_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic4_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic5_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic5_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic5_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic5_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_8_HandlersPtr v9938_graphic5_border_8 = new ModeBorder_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_16_HandlersPtr v9938_graphic5_border_16 = new ModeBorder_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_8_HandlersPtr v9938_graphic5_border_8s = new ModeBorder_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_16_HandlersPtr v9938_graphic5_border_16s = new ModeBorder_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
   
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic5_draw_sprite_8 = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic5_draw_sprite_16 = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic5_draw_sprite_8s = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic5_draw_sprite_16s = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic6_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic6_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic6_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic6_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic7_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic7_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_graphic7_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_graphic7_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_8_HandlersPtr v9938_graphic7_border_8 = new ModeBorder_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_16_HandlersPtr v9938_graphic7_border_16 = new ModeBorder_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_8_HandlersPtr v9938_graphic7_border_8s = new ModeBorder_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeBorder_16_HandlersPtr v9938_graphic7_border_16s = new ModeBorder_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic7_draw_sprite_8 = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic7_draw_sprite_16 = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic7_draw_sprite_8s = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeDraw_Sprites_HandlersPtr v9938_graphic7_draw_sprite_16s = new ModeDraw_Sprites_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, UBytePtr col) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_text2_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_text2_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_text2_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_text2_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_unknown_8 = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_unknown_16 = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr ln, int line) {
            int fg, bg;
            int x;
	
            fg = Machine.pens[pal_ind16[_vdp.contReg[7] >> 4]];
            bg = Machine.pens[pal_ind16[_vdp.contReg[7] & 15]];	
	
            if (V9938_WIDTH < 512){
                    x = _vdp.offset_x;
                    while (x-- != 0){
                        ln.write((char) bg);
                        ln.inc();
                    }	

                    x = 256;
                    while (x-- != 0){
                        ln.write((char) fg);
                        ln.inc();
                    }	

                    x = 16 - _vdp.offset_x;
                    while (x-- != 0){
                        ln.write((char) bg);
                        ln.inc();
                    }
            } else {
                x = _vdp.offset_x * 2;
                while (x-- != 0){
                    ln.write((char) bg);
                    ln.inc();
                }	

                x = 512;
                while (x-- != 0){
                    ln.write((char) fg);
                    ln.inc();
                }	

                x = (16 - _vdp.offset_x) * 2;
                while (x-- != 0){
                    ln.write((char) bg);
                    ln.inc();
                }
            }
            
            if (_vdp.size_now != RENDER_HIGH) _vdp.size_now = RENDER_LOW;
        }
    };
    
    public static ModeVisible_8_HandlersPtr v9938_mode_unknown_8s = new ModeVisible_8_HandlersPtr() {
        @Override
        public void handler(UBytePtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public static ModeVisible_16_HandlersPtr v9938_mode_unknown_16s = new ModeVisible_16_HandlersPtr() {
        @Override
        public void handler(UShortPtr col, int line) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
}
