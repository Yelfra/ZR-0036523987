package com.franjelavic.zrtuner;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;

public class ListenerFragment extends Fragment {

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final int OVERLAP = 768 * 4;
    private static final int MIN_PITCH_COUNT = 8;
    static boolean IS_RECORDING;
    private static List<PitchDifference> pitchDifferences = new ArrayList<>();
    private static TaskCallbacks taskCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        taskCallbacks = (TaskCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PitchListener pitchListener = new PitchListener();
        pitchListener.execute();
    }

    private static class PitchListener extends AsyncTask<Void, PitchDifference, Void> {

        private AudioDispatcher audioDispatcher;

        @Override
        protected Void doInBackground(Void... params) {
            PitchDetectionHandler pitchDetectionHandler = (pitchDetectionResult, audioEvent) -> {

                if (isCancelled()) {
                    stopAudioDispatcher();
                    return;
                }

                if (!IS_RECORDING) {
                    IS_RECORDING = true;
                    publishProgress();
                }

                float pitch = pitchDetectionResult.getPitch();

                if (pitch != -1) {
                    PitchDifference pitchDifference = PitchComparator.retrieveNote(pitch);

                    pitchDifferences.add(pitchDifference);

                    if (pitchDifferences.size() >= MIN_PITCH_COUNT) {
                        PitchDifference average = Sampler.calculateAverageDifference(pitchDifferences);

                        publishProgress(average);

                        pitchDifferences.clear();
                    }
                }
            };

            PitchProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                    SAMPLE_RATE,
                    BUFFER_SIZE, pitchDetectionHandler);

            audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE,
                    BUFFER_SIZE, OVERLAP);

            audioDispatcher.addAudioProcessor(pitchProcessor);

            audioDispatcher.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(PitchDifference... pitchDifference) {
            if (taskCallbacks != null) {
                if (pitchDifference.length > 0) {
                    taskCallbacks.onProgressUpdate(pitchDifference[0]);
                } else {
                    taskCallbacks.onProgressUpdate(null);
                }
            }
        }

        private void stopAudioDispatcher() {
            if (audioDispatcher != null && !audioDispatcher.isStopped()) {
                audioDispatcher.stop();
                IS_RECORDING = false;
            }
        }
    }

    interface TaskCallbacks {
        void onProgressUpdate(PitchDifference percent);
    }
}
