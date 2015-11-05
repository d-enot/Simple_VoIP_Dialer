package com.temp.simplevoipdialer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String PREF_NAME = "voIPDialerPref";
    public static final String PREF_USER = "user";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_BALANCE_LINK = "balanceLink";
    public static final String PREF_USER_NAME = "userName";
    public static final String PREF_USER_EMAIL = "userEmail";
    public static final String PREF_PORT = "port";
    private static EditText etUser;
    private static EditText etPassword;
//    private static EditText etDomain;
    private static EditText etBalanceLink;
    private static EditText etName;
    private static EditText etEmail;
    private String userName;
    private String userPassword;
    private String userLogName;
    private String userEmail;
    private String balanceLink;
    private static final String BFL_EMAIL = "info@brainfriendsltd.com";
    private RelativeLayout registerLayout;
    private LinearLayout regLayout;
    private Button createAccountButton;
    public static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_start);

        activity = this;

        SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (pref.contains(PREF_USER)) {
            userName = pref.getString(PREF_USER, null);
        }
//        if (pref.contains(PREF_PASSWORD)) {
//            userPassword = pref.getString(PREF_PASSWORD, null);
//        }
//        if (pref.contains(PREF_BALANCE_LINK)) {
//            balanceLink = pref.getString(PREF_BALANCE_LINK, null);
//        }

        if (userName != null) {
            startDialerMainActivity();
        }else {
            registerLayout = (RelativeLayout) findViewById(R.id.newUserLayout);
            regLayout = (LinearLayout) findViewById(R.id.defaultLayout);
            createAccountButton = (Button) findViewById(R.id.createAccountButton);
            etUser = (EditText) findViewById(R.id.userLog);
            etPassword = (EditText) findViewById(R.id.passwordLog);
            etBalanceLink = (EditText) findViewById(R.id.balanceLinkText);
            etName = (EditText) findViewById(R.id.userNameText);
            etEmail = (EditText) findViewById(R.id.userEmailText);

            registerLayout.setVisibility(View.VISIBLE);
            createAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userName = etUser.getText().toString();
                    userPassword = etPassword.getText().toString();
                    balanceLink = etBalanceLink.getText().toString();
                    userLogName = etName.getText().toString();
                    userEmail = etEmail.getText().toString();
                    if (userName != null&&userPassword!=null&&userLogName!=null&&userEmail!=null) {

                        registerLayout.setVisibility(View.GONE);
                        regLayout.setVisibility(View.VISIBLE);
                        SharedPreferences pref = MainActivity.this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(PREF_USER, userName);
                        editor.putString(PREF_PASSWORD, userPassword);
                        editor.putString(PREF_BALANCE_LINK, balanceLink);
                        editor.putString(PREF_USER_NAME, userLogName);
                        editor.putString(PREF_USER_EMAIL, userEmail);
                        editor.commit();
                        startDialerMainActivity();
                        sendMail();
                    }else
                        Toast.makeText(getApplicationContext(), "Fill all fields please", Toast.LENGTH_SHORT);
                }
            });
        }

    }

    private void getSharedPref() {

    }

    private void startDialerMainActivity() {
        Intent intent = new Intent(getApplicationContext(), DialerMainActivity.class);
//        intent.putExtra(PREF_USER, userName);
//        intent.putExtra(PREF_PASSWORD, userPassword);
//        intent.putExtra(PREF_BALANCE_LINK, balanceLink);
        startActivity(intent);
        finish();
    }

    private void sendMail() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("mailto:");
        buffer.append(BFL_EMAIL);
        buffer.append("?subject=");
        buffer.append("new user "+ userLogName);
        buffer.append("&body=");
        buffer.append("New User: Name " + userLogName + " email " + userEmail);

        String uriString = buffer.toString().replace(" ", "%20");

        startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString)), "Contact Developer"));
    }
}
