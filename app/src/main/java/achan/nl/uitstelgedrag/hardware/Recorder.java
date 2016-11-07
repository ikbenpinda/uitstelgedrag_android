package achan.nl.uitstelgedrag.hardware;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import achan.nl.uitstelgedrag.domain.models.Timestamp;

/**
 * A simple microphone/recorder implementation.
 *
 * Created by Etienne on 7-11-2016.
 */
public class Recorder {

    public boolean isRecording = false;
    public String filename;

    Context       context;
    MediaRecorder recorder;

    public Recorder(Context context){
        this.context = context;
    }

    /**
     * Stops recording and returns the path of the audio file.
     */
    public String stopRecording() {

        recorder.stop();
        recorder.release();
        recorder = null;

        isRecording = false;
        Log.w("Recorder", "Recording saved to " + filename);
        return filename;
    }

    /**
     * Starts recording and outputs the recording to the default app directory.
     */
    public void startRecording() {
        // important - special characters in filename can cause state errors / "IllegalStateException(4)".
        filename = context.getFilesDir().getAbsolutePath() + String.format(Locale.ENGLISH, "recording_%s", 4, Timestamp.formatDate(new Date()));
        record();
    }

    /**
     * Starts recording and outputs the file to the specified location.
     * Additional availability checks are not made.
     *
     * @param filename
     */
    public void startRecording(String filename){
        this.filename = filename;
        record();
    }

    private void record(){
        recorder = new MediaRecorder();
        //            recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filename);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Recorder", "prepare() failed: " + e.getMessage());
        }

        recorder.start();
        isRecording = true;
        Log.w("Recorder", "Recording message.");
    }
}
