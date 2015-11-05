package com.temp.simplevoipdialer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 09.09.2015.
 */
public class SettingsActivity extends Activity {

    private String userName;
    private String userPassword;
    private EditText mUserName;
    private EditText mUserPass;
    private EditText mPort;
    private Button goPayPal;
    private Button goPayPal2;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mUserName = (EditText) findViewById(R.id.userId);
        mUserPass = (EditText) findViewById(R.id.userPass);
        mPort = (EditText) findViewById(R.id.serverPort);

        SharedPreferences pref = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        if (pref.contains(MainActivity.PREF_USER)) {
            userName = pref.getString(MainActivity.PREF_USER, null);
        }
        if (pref.contains(MainActivity.PREF_PASSWORD)) {
            userPassword = pref.getString(MainActivity.PREF_PASSWORD, null);
        }

        goPayPal = (Button) findViewById(R.id.goPayPal);
        goPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this.getApplicationContext(), CheckBalance.class));
            }
        });

        goPayPal2 = (Button) findViewById(R.id.payPal2);
        goPayPal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this.getApplicationContext(), PayActivity2.class));
            }
        });

        mUserName.setText(userName);
        mUserPass.setText(userPassword);
        String sPort = "";
        if (MainService.port == 0) {
            sPort = String.valueOf(MainService.defaultPort);
        }else sPort = String.valueOf(MainService.port);
        mPort.setText(sPort);

        saveButton = (Button) findViewById(R.id.saveChangeButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        SharedPreferences pref = SettingsActivity.this.getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(MainActivity.PREF_USER, mUserName.getText().toString());
        editor.putString(MainActivity.PREF_PASSWORD, mUserPass.getText().toString());
        editor.putString(MainActivity.PREF_PORT, mPort.getText().toString());
        editor.commit();
        stopService(new Intent(this, MainService.class));
        startService(new Intent(this, MainService.class));
        SettingsActivity.this.finish();
    }
}
