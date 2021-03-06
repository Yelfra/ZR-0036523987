package com.franjelavic.zrtuner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

class Sampler {

    static PitchDifference calculateAverageDifference(List<PitchDifference> samples) {
        Note mostFrequentNote = extractMostFrequentNote(samples);
        List<PitchDifference> filteredSamples = filterByNote(samples, mostFrequentNote);

        double deviationSum = 0;
        int sameNoteCount = 0;
        for (PitchDifference pitchDifference : filteredSamples) {
            deviationSum += pitchDifference.deviation;
            sameNoteCount++;
        }

        if (sameNoteCount > 0) {
            double averageDeviation = deviationSum / sameNoteCount;

            return new PitchDifference(mostFrequentNote, averageDeviation);
        }

        return null;
    }

    static Note extractMostFrequentNote(List<PitchDifference> samples) {
        Map<Note, Integer> noteFrequencies = new HashMap<>();

        for (PitchDifference pitchDifference : samples) {
            Note closest = pitchDifference.closest;
            if (noteFrequencies.containsKey(closest)) {
                int count = noteFrequencies.get(closest);
                noteFrequencies.put(closest, count + 1);
            } else {
                noteFrequencies.put(closest, 1);
            }
        }

        Note mostFrequentNote = null;
        int mostOccurrences = 0;
        for (Note note : noteFrequencies.keySet()) {
            int occurrences = noteFrequencies.get(note);
            if (occurrences > mostOccurrences) {
                mostFrequentNote = note;
                mostOccurrences = occurrences;
            }
        }

        return mostFrequentNote;
    }

    static List<PitchDifference> filterByNote(List<PitchDifference> samples, Note note) {
        List<PitchDifference> filteredSamples = new ArrayList<>();

        for (PitchDifference sample : samples) {
            if (sample.closest == note) {
                filteredSamples.add(sample);
            }
        }

        return filteredSamples;
    }
}