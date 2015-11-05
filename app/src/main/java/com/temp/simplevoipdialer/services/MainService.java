package com.temp.simplevoipdialer.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.temp.simplevoipdialer.DialerMainActivity;
import com.temp.simplevoipdialer.MainActivity;
import com.temp.simplevoipdialer.R;
import com.temp.simplevoipdialer.fragments.CallFragment;
import com.temp.simplevoipdialer.items.Call;
import com.temp.simplevoipdialer.items.Contact;
import com.temp.simplevoipdialer.myContentObserver.CallLogChangeObserver;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Created by klim-mobile on 02.09.2015.
 */
public class MainService extends Service {

    public static String userName;
    public static String userPassword;
//    public static String userDomain = "198.50.224.106";
    public static String userDomain = "198.50.158.153";
    public static int defaultPort = 5060;
    public static int port;
    private String balanceLink;
    public static SipManager sipManager = null;
    public static SipProfile sipProfile = null;
    public static Context context;
    public static List<Contact> contacts;
    public static List<Call> calls;
    public static boolean registrationPass;
    private static MainService instance;
    static Contact contactTemp;
    CallLogChangeObserver callLogChangeObserver;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences pref = getSharedPreferences(MainActivity.PREF_NAME, MODE_PRIVATE);

        context = getApplicationContext();

        startObserver();

        if (pref.contains(MainActivity.PREF_USER)) {
            userName = pref.getString(MainActivity.PREF_USER, null);
        }
        if (pref.contains(MainActivity.PREF_PASSWORD)) {
            userPassword = pref.getString(MainActivity.PREF_PASSWORD, null);
        }
        if (pref.contains(MainActivity.PREF_BALANCE_LINK)) {
            balanceLink = pref.getString(MainActivity.PREF_BALANCE_LINK, null);
        }
        if (pref.contains(MainActivity.PREF_PORT)) {
            String sPort = pref.getString(MainActivity.PREF_PORT, null);
            if (sPort == null||sPort.equals("")) {
                port = defaultPort;
            }else port = Integer.parseInt(sPort);
        }else port = defaultPort;
        initializeManager();

        if (contacts == null) {
            contacts = new ArrayList<>();
        }
        if (calls == null) {
            calls = new ArrayList<>();
        }
        getContacts();
        getCallHistory();

        return Service.START_NOT_STICKY;
    }

    private void initializeManager() {
        if (sipManager == null) {
            sipManager = sipManager.newInstance(this);
        }
        initializeProfile();
    }

    private void initializeProfile() {
        if (sipManager == null) {
            return;
        }
        if (sipProfile != null) {
            closeLocalProfile();
        }

        if (userName.length() == 0 || userDomain.length() == 0 || userPassword.length() == 0) {
            return;
        }
        try {
            SipProfile.Builder builder = new SipProfile.Builder(userName, userDomain);
            builder.setPort(port);
            builder.setPassword(userPassword);
            sipProfile = builder.build();

            Intent intentSIP = new Intent();
            intentSIP.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intentSIP, Intent.FILL_IN_DATA);

            sipManager.open(sipProfile, pendingIntent, null);
            sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
                public void onRegistering(String localProfileUri) {
//                    updateConnectInfo("Start registration");
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateConnectInfo("Registration pass");
                    registrationPass = true;
                    updateConnectColor(context.getResources().getColor(R.color.my_color_green));
                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    updateConnectInfo("Registration failed");
                    registrationPass = false;
                    updateConnectColor(context.getResources().getColor(R.color.my_color_red));
//                    showError(errorMessage);
                }
            });
        } catch (ParseException pe) {
        } catch (SipException se) {
        }
    }

    private void closeLocalProfile() {
        if (sipManager == null) {
            return;
        }
        try {
            if (sipManager != null) {
                sipManager.close(sipProfile.getUriString());
            }
        } catch (Exception ee) {
        }
    }

    public void updateConnectInfo(final String text) {
        DialerMainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialerMainActivity.connectionText.setText(text);
            }
        });
    }

    public void updateConnectColor(final int color) {
        DialerMainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialerMainActivity.connectionLayout.setBackgroundColor(color);
            }
        });
    }

    private void showError(final String text) {
        DialerMainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getContacts() {

        ContentResolver cr = getContentResolver();
        String[] query = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, query, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                Contact contact = new Contact();

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.setName(name);
                contact.setId(id);
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    // get the phone number
                    Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (phoneCur.moveToNext()) {
                        String phone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int phoneTypes = phoneCur.getInt(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phTypes = "";
                        switch (phoneTypes) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                phTypes = "Home";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                phTypes = "Mobile";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                phTypes = "Work";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                                phTypes = "Main";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                phTypes = "Other";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                                phTypes = "Fax home";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                phTypes = "Fax work";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                                phTypes = "Custom";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                                phTypes = "Pager";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                                phTypes = "Assistant";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                                phTypes = "Callback";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                                phTypes = "Car";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                                phTypes = "Company main";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                                phTypes = "ISDN";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                                phTypes = "MMS";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                                phTypes = "Other fax";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                                phTypes = "Radio";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                                phTypes = "Telex";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                                phTypes = "TTY TDD";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                                phTypes = "Work mobile";
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                                phTypes = "Work pager";
                                break;
                        }
                        contact.setPhoneType(phTypes);
                        contact.setPhoneNumbers(phone.replace(" ", "").replace("-", ""));
                    }
                    phoneCur.close();

                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
                    Uri imageUti = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                    contact.setImageLink(imageUti.toString());

                    contacts.add(contact);
                }


            }
        }
    }

    public static void updateCalls() {
        getCallHistory();
        if (CallFragment.callFragment!=null) {
            CallFragment.updateAdapter();
        }
    }

    private static void getCallHistory() {
        StringBuffer stringBuffer = new StringBuffer();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            Contact callContact = getContactNameByPhoneNumber(phNumber);
            String callDateInfo = getCallDate(callDayTime);
            Call call = null;
            if (callContact!=null) {
                call = new Call(callContact.getName(), phNumber, dir, callDateInfo, callDuration);
                call.setImageLink(callContact.getImageLink());
                call.setId(callContact.getId());
            }else {
                call = new Call(phNumber, "", dir, callDateInfo, callDuration);
            }
            calls.add(call);
        }
        cursor.close();
    }

    public static String getCallDate(Date date) {

        DateFormat df = new SimpleDateFormat("dd MMM yy, HH:mm");

        return  df.format(date);
    }

    public static Contact getContactNameByPhoneNumber(final String phNumber) {

        if (contactTemp == null) {
            contactTemp = new Contact();
            contactTemp.setName("");
            contactTemp.setId("");
        }
        contactTemp.cleanNumbers();
        contactTemp.setPhoneNumbers(phNumber);

        Contact requiredContact = null;
        try {
            requiredContact = getContactFromList(contactTemp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (requiredContact!=null) {
            return requiredContact;
        }


        return null;
    }

    private static Contact getContactFromList(Contact item) {
        List<Contact> lContacts = contacts;
        Contact nContact = null;
        if (!contacts.isEmpty()) {
            int n = contacts.indexOf(item);
            if (!(n == -1)) {
                nContact = contacts.get(n);
            }
        }
        return nContact;
    }

    public static boolean isServiceWorks() {
        if (instance != null) {
            return true;
        }
        return false;
    }

    public static void setImage(ImageView image, String imageLink) {
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setBackground(null);
        Bitmap bImage = null;
        if (imageLink!=null) {
            try {
                bImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imageLink));
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        if (bImage==null) {
            bImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_face);
        }
        image.setImageBitmap(bImage);
    }

    private void startObserver() {
        callLogChangeObserver = new CallLogChangeObserver(new Handler(), DialerMainActivity.activity);
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogChangeObserver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        closeLocalProfile();
    }

//    public class SipLayer implements SipListener{
//
//    }

}
