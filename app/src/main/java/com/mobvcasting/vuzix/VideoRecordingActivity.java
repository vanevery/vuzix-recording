package com.mobvcasting.vuzix;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.IOException;

import com.vuzix.hud.actionmenu.ActionMenuActivity;

public class VideoRecordingActivity extends ActionMenuActivity implements SurfaceHolder.Callback {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);

        setContentView(R.layout.main);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

//        cameraView.setClickable(true);
//        cameraView.setOnClickListener(this);
    }


    public static final String LOGTAG = "VIDEOCAPTURE";

    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;

    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;

    private void prepareRecorder() {
        recorder = new MediaRecorder();
        recorder.setPreviewDisplay(holder.getSurface());

        if (usecamera) {
            camera.unlock();
            recorder.setCamera(camera);
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setProfile(camcorderProfile);

        // This is all very sloppy
        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
            try {
                File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/videocapture" + ".mp4");
                Log.v(LOGTAG, newFile.getAbsolutePath());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (Exception e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            try {
                File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/videocapture" + ".mp4");
                Log.v(LOGTAG, newFile.getAbsolutePath());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (Exception e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        } else {
            try {
                File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/videocapture" + ".mp4");
                Log.v(LOGTAG, newFile.getAbsolutePath());
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (Exception e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }

        }
        //recorder.setMaxDuration(50000); // 50 seconds
        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

//    public void onClick(View v) {
//        if (recording) {
//            recorder.stop();
//            if (usecamera) {
//                try {
//                    camera.reconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            // recorder.release();
//            recording = false;
//            Log.v(LOGTAG, "Recording Stopped");
//            // Let's prepareRecorder so we can record again
//            prepareRecorder();
//        } else {
//            recording = true;
//            recorder.start();
//            Log.v(LOGTAG, "Recording Started");
//        }
//    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceCreated");

        if (usecamera) {
            camera = Camera.open();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }
        }

    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOGTAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning){
                camera.stopPreview();
            }

            try {
                Camera.Parameters p = camera.getParameters();

                p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

                camera.setParameters(p);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }

            prepareRecorder();
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        if (usecamera) {
            previewRunning = false;
            //camera.lock();
            camera.release();
        }
        finish();
    }

    /*
        Utilize this method to get keyDown events that the application can override.
        The Keycode can be used in a switch case statement to identify the required and desire events.
        https://developer.android.com/training/keyboard-input/commands
        https://developer.android.com/guide/topics/media-apps/mediabuttons
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // You always want to return the super event to have the system actually handle the event the
        // way they want. This is just for showing the available events.
//        return super.onKeyDown(keyCode, event);

        Log.d(LOGTAG, "Key Code: " + String.valueOf(keyCode) );
        Log.d(LOGTAG,"Key Event: " + event.toString());

        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if (keyCode == 4) {
                if (recording) {
                    recorder.stop();
                    if (usecamera) {
                        try {
                            camera.reconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // recorder.release();
                    recording = false;
                    Log.v(LOGTAG, "Recording Stopped");
                    // Let's prepareRecorder so we can record again
                    prepareRecorder();
                } else {
                    recording = true;
                    recorder.start();
                    Log.v(LOGTAG, "Recording Started");
                }
            }
//            logArea.setText("Key Code: " + String.valueOf(keyCode));
//            logArea.append("\n Key Event: " + event.toString());
//            showToast("Key Code: " + String.valueOf(keyCode) +
//                    " \n Shortcut Key Event: " + event.toString());
        }


        return false;
    }

}