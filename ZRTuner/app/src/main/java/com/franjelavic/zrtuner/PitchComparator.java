package com.franjelavic.zrtuner;

import java.util.Arrays;
import java.util.Comparator;

class PitchComparator {

    public static PitchDifference retrieveNote(float pitch) {

        Tuning tuning = MainActivity.getCurrentTuning();
        Note[] tuningNotes = tuning.getNotes();

        int referencePitch = MainActivity.getReferencePitch();
        NoteFrequencyCalculator noteFrequencyCalculator = new NoteFrequencyCalculator(referencePitch);

        Arrays.sort(tuningNotes, Comparator.comparingDouble(noteFrequencyCalculator::getFrequency));

        double minCentDifference = Float.POSITIVE_INFINITY;
        Note closestNote = tuningNotes[0];
        for (Note note : tuningNotes) {
            double frequency = noteFrequencyCalculator.getFrequency(note);
            double centDifference = 1200d * log2(pitch / frequency);

            if (Math.abs(centDifference) < Math.abs(minCentDifference)) {
                minCentDifference = centDifference;
                closestNote = note;
            }
        }

        return new PitchDifference(closestNote, minCentDifference);
    }

    private static double log2(double number) {
        return Math.log(number) / Math.log(2);
    }
}
