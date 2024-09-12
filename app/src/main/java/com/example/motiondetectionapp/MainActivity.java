package com.example.motiondetectionapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "MotionDetection";
    private SurfaceView cameraPreview;
    private Camera camera;
    private TextView motionMessage;
    private TextToSpeech textToSpeech;
    private boolean motionDetected = false;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private Handler handler = new Handler();
    private Runnable motionCheckRunnable;
    private byte[] previousFrameData = null;
    private byte[] currentFrameData = null;
    private static final int CHECK_INTERVAL = 300; // 0.3 seconden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraPreview = findViewById(R.id.camera_preview);
        motionMessage = findViewById(R.id.motion_message);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }

            }
        });

        // Initialize motion check
        motionCheckRunnable = new Runnable() {
            @Override
            public void run() {
                checkMotion();
                handler.postDelayed(this, CHECK_INTERVAL); // Herhaal elke 0.3 seconden
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCameraPreview();
        }
    }

    private void startCameraPreview() {
        SurfaceHolder holder = cameraPreview.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
            camera.startPreview();
            handler.post(motionCheckRunnable); // Start motion check runnable
            Log.d(TAG, "Camera preview started and motion check runnable initiated.");
        } catch (Exception e) {
            Log.e(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (currentFrameData == null) {
            currentFrameData = data.clone();
            return;
        }

        // Bewaar de huidige frame data voor de volgende vergelijking
        if (previousFrameData != null) {
            currentFrameData = data.clone();
        } else {
            previousFrameData = data.clone();
        }
    }

    private void checkMotion() {
        if (previousFrameData != null && currentFrameData != null) {
            // Vergelijk de laatste twee frames voor beweging
            int movement = calculateMovement(previousFrameData, currentFrameData);
            Log.d(TAG, "Movement calculated: " + movement);
            int digitCount = String.valueOf(movement).length();
            Log.d(TAG, "Movement digit count: " + digitCount);

            if (digitCount >= 8) { // Meer dan 7 cijfers betekent beweging
                if (!motionDetected) {
                    motionDetected = true;
                    displayMotionMessage();
                    Log.d(TAG, "Motion detected.");
                }
            } else {
                if (motionDetected) {
                    motionDetected = false;
                    motionMessage.setVisibility(TextView.GONE); // Verberg tekst bij geen beweging
                    Log.d(TAG, "Motion ended. Text hidden.");
                }
            }

            // Update previous frame data
            previousFrameData = currentFrameData.clone();
        }
    }

    private int calculateMovement(byte[] previousData, byte[] currentData) {
        int movement = 0;
        // Vergelijk pixelwaarden tussen de twee frames
        for (int i = 0; i < previousData.length; i++) {
            movement += Math.abs(currentData[i] - previousData[i]);
        }
        return movement;
    }

    private void displayMotionMessage() {
        motionMessage.setVisibility(TextView.VISIBLE);
        String message = "Beweging gedetecteerd!";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        Log.d(TAG, "Motion detected and message displayed.");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            Log.d(TAG, "Camera stopped and released.");
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        handler.removeCallbacks(motionCheckRunnable); // Stop motion check runnable
        Log.d(TAG, "Motion check runnable stopped.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
