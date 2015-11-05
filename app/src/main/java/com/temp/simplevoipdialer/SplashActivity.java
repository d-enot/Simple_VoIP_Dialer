package com.temp.simplevoipdialer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;

import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 07.10.2015.
 */
public class SplashActivity extends Activity {

    private static int SPLASH_DURATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        if (MainService.isServiceWorks()) {
            SPLASH_DURATION = 100;
        }else SPLASH_DURATION = 250;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();

                overridePendingTransition(R.anim.activityfadein,
                        R.anim.splashfadeout);
            }
        }, SPLASH_DURATION);
    }
}
