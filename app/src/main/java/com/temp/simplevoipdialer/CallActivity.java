package com.temp.simplevoipdialer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 02.09.2015.
 */
public class CallActivity extends Activity {

    public static SipManager sipManager;
    public static SipProfile sipProfile;
    public static String phoneNumber;
    public static String imageLink;
    private static SipAudioCall call;
    private TextView callInfo;
    private ImageView contactImage;
    private TextView name;
    private TextView callNumber;
    private Button callEnd;
    private long startTime;
    private long timeMilliseconds;
    private Handler handler;
    private int secs;
    private int mins;
    private int hrs;
    private TextView timer;
    private PowerManager powerManager;
    private WindowManager windowManager;
    private PowerManager.WakeLock wakeLock;
    private boolean isWakeLock;
    private static final String TAG = "DFLCallActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_layout);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay();

        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, getClass().getName());

        contactImage = (ImageView) findViewById(R.id.contactImage);
        MainService.setImage(contactImage, imageLink);
        sipManager = MainService.sipManager;
        sipProfile = MainService.sipProfile;
        callInfo = (TextView) findViewById(R.id.callInfoText);
        name = (TextView) findViewById(R.id.callNameText);
        callNumber = (TextView) findViewById(R.id.callNumberText);
        callEnd = (Button) findViewById(R.id.callEndButton);
        timer = (TextView) findViewById(R.id.timetText);
        handler = new Handler();
        setListeners();

        startCall();
    }

    private void setListeners() {
        callEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call != null) {
                    try {
                        call.endCall();
                    } catch (SipException e) {
                        e.printStackTrace();
                    }
                    call.close();
                    try {
                        updateCallStatus("Call end", getResources().getColor(R.color.my_color_white));
                        insertCall();
                        Thread.sleep(500);
                        CallActivity.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stopWakeLock();
                    CallActivity.this.finish();
                }else {
                    stopWakeLock();
                    CallActivity.this.finish();
                }
            }
        });
    }

    private void startCall() {
        SipAudioCall.Listener listener = new SipAudioCall.Listener(){
            @Override
            public void onCallEstablished(SipAudioCall call) {
                startTime = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimer, 0);
                call.startAudio();
//                call.
//                call.setSpeakerMode(true);
                if(call.isMuted()){
                    call.toggleMute();
                }
                updateCallStatus("Speaking", getResources().getColor(R.color.my_color_green));
            }

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                super.onRinging(call, caller);
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                stopWakeLock();
//                sensorManager.unregisterListener(mySensorListener);
                updateCallStatus("Error", getResources().getColor(R.color.my_color_red));
                showError(errorMessage);
                insertCall();
                try {
                    Thread.sleep(1000 * 5);
                    handler.removeCallbacks(updateTimer);
                    CallActivity.this.finish();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }

            @Override
            public void onCalling(SipAudioCall call) {
                wakeLock.acquire();
                isWakeLock = true;
                updateCallStatus("Calling", getResources().getColor(R.color.my_color_white));
                showCallNumber(phoneNumber);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                stopWakeLock();
//                sensorManager.unregisterListener(mySensorListener);
                updateCallStatus("Call end", getResources().getColor(R.color.my_color_white));
                insertCall();
                try {
                    Thread.sleep(1000);
                    CallActivity.this.finish();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }

            @Override
            public void onCallBusy(SipAudioCall call) {
//                super.onCallBusy(call);
                stopWakeLock();
                updateCallStatus("Busy", getResources().getColor(R.color.my_color_orange));
                insertCall();
                try {
                    Thread.sleep(1000);
                    call.close();
                    CallActivity.this.finish();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
            }
        };
        try {
            String sipCall = Uri.parse("sip:" + phoneNumber + "@" + MainService.userDomain + "").toString();
            call = sipManager.makeAudioCall(sipProfile.getUriString(), sipCall, listener, 30);
        } catch (SipException e) {
            try {
                sipManager.close(sipProfile.getUriString());
            } catch (Exception ee) {
            }
            if (call != null) {
                call.close();
            }
//            Toast.makeText(MainService.context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateCallStatus(final String text, final int color) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callInfo.setText(text);
                callInfo.setTextColor(color);
            }
        });
    }

    private void showCallNumber(final String text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callNumber.setText(text);
            }
        });
    }

    private void showError(final String text) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void insertCall() {
        ContentResolver contentResolver = MainService.context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, phoneNumber);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, timeMilliseconds/1000);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, "");
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        Log.d(TAG, "Inserting call log placeholder for " + phoneNumber);
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
    }

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            timeMilliseconds = SystemClock.uptimeMillis() - startTime;
            secs = (int) (timeMilliseconds / 1000);
            mins = secs / 60;
            hrs = mins / 60;
            secs = secs % 60;
            mins = mins % 60;
            timer.setText(""+ String.format("%02d", hrs) +":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }
    };

    private void stopWakeLock() {
        if (isWakeLock) {
            wakeLock.release();
            isWakeLock = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (call != null) {
            call.close();
        }
    }
}
