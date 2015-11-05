package com.temp.simplevoipdialer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.temp.simplevoipdialer.fragments.CallFragment;
import com.temp.simplevoipdialer.fragments.ContactFragment;
import com.temp.simplevoipdialer.fragments.DialFragment;
import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 31.08.2015.
 */
public class DialerMainActivity extends Activity implements DialFragment.PassData{

    private Button callButton;
    private Button contactsButton;
    private Button callsButton;
    private ImageButton menuButton;
    private String phoneNumber;
    private DialFragment dialFragment;
    private FragmentTransaction fragmentTransaction;
    private ContactFragment contactFragment;
    private CallFragment callFragment;
    public static TextView connectionText;
    public static RelativeLayout connectionLayout;
    public static Activity activity;
    private boolean isDialer;
    private boolean isContact;
    private boolean isHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        fragmentTransaction = getFragmentManager().beginTransaction();

        callButton = (Button) findViewById(R.id.callButton);
        addDialFragment();

        activity = this;
        connectionText = (TextView) findViewById(R.id.connectionText);
        connectionLayout = (RelativeLayout) findViewById(R.id.connectionLayout);
        if (MainService.registrationPass) {
            connectionText.setText("Registration pass");
            connectionLayout.setBackgroundColor(getResources().getColor(R.color.my_color_green));
        }
        if (!MainService.isServiceWorks()) {
            startService(new Intent(getApplicationContext(), MainService.class));
        }
//        new BalanceConnect().execute();

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDialer) {
                    if (phoneNumber!=null) {
                        if (isNetworkAvailable()&&!phoneNumber.equals("")) {
                            CallActivity.phoneNumber = phoneNumber;
                            startActivity(new Intent(getApplicationContext(), CallActivity.class));
                        }
                    }
                }else addDialFragment();
            }
        });

        contactsButton = (Button) findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactsFragment();
            }
        });

        callsButton = (Button) findViewById(R.id.historyButton);
        callsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCallsFragment();
            }
        });

        menuButton = (ImageButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(DialerMainActivity.this.getApplicationContext(), SettingsActivity.class));
                DialerMainActivity.this.openOptionsMenu();
            }
        });
    }

    private void addDialFragment() {
        if (!isDialer) {
            isDialer = true;
            isHistory = false;
            isContact = false;
            callButton.setBackgroundColor(getResources().getColor(R.color.my_color_green));
            callButton.setText("CALL");
            dialFragment = new DialFragment();
            getFragmentManager().beginTransaction().replace(R.id.main_frame_layout, dialFragment).commit();
        }
    }

    private void addContactsFragment() {
        if (!isContact) {
            callButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            callButton.setText("KEYPAD");
            isDialer = false;
            isHistory = false;
            isContact = true;
            contactFragment = new ContactFragment();
            getFragmentManager().beginTransaction().replace(R.id.main_frame_layout, contactFragment).commit();
        }
    }

    private void addCallsFragment() {
        if (!isHistory) {
            callButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            callButton.setText("KEYPAD");
            isDialer = false;
            isHistory = true;
            isContact = false;
            callFragment = new CallFragment();
            getFragmentManager().beginTransaction().replace(R.id.main_frame_layout, callFragment).commit();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onStart() {
        super.onStart();
//        initializeManager();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void passData(String data) {
        phoneNumber = data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(DialerMainActivity.this.getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.action_close:
                if (MainService.isServiceWorks()) {
                    stopService(new Intent(getApplicationContext(), MainService.class));
                }
                DialerMainActivity.this.finish();
                return true;
            case R.id.action_log_out:
                item.setTitle(getResources().getString(R.string.action_log_in));
                if (MainService.isServiceWorks()) {
                    stopService(new Intent(getApplicationContext(), MainService.class));
                }else startService(new Intent(getApplicationContext(), MainService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
