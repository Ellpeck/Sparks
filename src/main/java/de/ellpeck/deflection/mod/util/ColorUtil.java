package de.ellpeck.deflection.mod.util;

public final class ColorUtil{

    public static float[] getWheelColor(float pos){
        if(pos < 85F){
            return new float[]{pos*3F, 255F-pos*3F, 0F};
        }
        if(pos < 170F){
            return new float[]{255F-(pos -= 85F)*3F, 0F, pos*3F};
        }
        return new float[]{0F, (pos -= 170F)*3F, 255F-pos*3F};
    }
}
