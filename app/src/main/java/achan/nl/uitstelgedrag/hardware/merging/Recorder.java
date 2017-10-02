package achan.nl.uitstelgedrag.hardware.merging;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * MediaRecorder wrapper to make recording media easier.
 *
 * Created by Etienne on 13/11/16.
 */
public class Recorder {

    public String filename;
    public boolean isRecording = false;

    private Context context;
    private MediaRecorder recorder;

    public Recorder(Context context) {
        this.context = context;
        this.recorder = new MediaRecorder();
    }

    public void startRecording(){

        SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy_HH-MM");
        filename = context.getFilesDir().getAbsolutePath() + String.format(Locale.ENGLISH, "/recording_%s", format.format(new Date()));

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filename);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Recorder", "prepare() failed!");
        }

        recorder.start();
        isRecording = true;
        Log.w("Recorder", "Recording message.");
    }

    public String stopRecording(){
        recorder.stop();
        recorder.release();
        recorder = null;

        isRecording = false;
        Log.w("Recorder", "Recording saved to " + filename);
        return filename;
    }
}
