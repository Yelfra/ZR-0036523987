package com.franjelavic.zrtuner.tuning;

import static com.franjelavic.zrtuner.NoteName.A;
import static com.franjelavic.zrtuner.NoteName.B;
import static com.franjelavic.zrtuner.NoteName.D;
import static com.franjelavic.zrtuner.NoteName.E;
import static com.franjelavic.zrtuner.NoteName.G;

import com.franjelavic.zrtuner.Note;
import com.franjelavic.zrtuner.NoteName;
import com.franjelavic.zrtuner.Tuning;

public class StandardTuning implements Tuning {

    @Override
    public Note[] getNotes() {
        return Pitch.values();
    }

    @Override
    public Note findNote(String name) {
        return Pitch.valueOf(name);
    }

    public enum Pitch implements Note {

        E2(E, 2),
        A2(A, 2),
        D3(D, 3),
        G3(G, 3),
        B3(B, 3),
        E4(E, 4);

        private NoteName noteName;
        private final int octave;
        private final String sign;

        Pitch(NoteName noteName, int octave) {
            this.noteName = noteName;
            this.octave = octave;
            this.sign = "";
        }

        @Override
        public NoteName getName() {
            return noteName;
        }

        @Override
        public int getOctave() {
            return octave;
        }

        @Override
        public String getSign() {
            return sign;
        }
    }
}
