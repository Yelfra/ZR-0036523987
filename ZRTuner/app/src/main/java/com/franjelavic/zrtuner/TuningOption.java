package com.franjelavic.zrtuner;

import com.franjelavic.zrtuner.tuning.ChromaticTuning;
import com.franjelavic.zrtuner.tuning.StandardTuning;
import com.franjelavic.zrtuner.tuning.DropDTuning;
import com.franjelavic.zrtuner.tuning.OpenGTuning;

public class TuningOption {

    private static final int CHROMATIC = 0;
    private static final int STANDARD = 1;
    private static final int DROP_D = 2;
    private static final int OPEN_G = 3;

    static Tuning getTuning(int tuningNumber) {
        switch (tuningNumber) {
            case CHROMATIC:
                return new ChromaticTuning();
            case STANDARD:
                return new StandardTuning();
            case DROP_D:
                return new DropDTuning();
            case OPEN_G:
                return new OpenGTuning();
            default: {
                return new ChromaticTuning();
            }
        }
    }
}
