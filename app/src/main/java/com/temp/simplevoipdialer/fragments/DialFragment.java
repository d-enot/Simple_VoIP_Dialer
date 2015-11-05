package com.temp.simplevoipdialer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.temp.simplevoipdialer.R;
import com.temp.simplevoipdialer.listeners.DialerButtonsListener;

/**
 * Created by klim-mobile on 29.08.2015.
 */
public class DialFragment extends Fragment {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button button10;
    private Button button11;
    private Button button12;
    private ImageButton delButton;
    public EditText phoneNumber;
    private PassData dataPass;

    public interface PassData {
        public void passData(String data);
    }

    private void setButtonListener(final View view) {
        button1 = (Button) view.findViewById(R.id.button);
        button1.setOnClickListener(new DialerButtonsListener(view));
        button2 = (Button) view.findViewById(R.id.button2);
        button2.setOnClickListener(new DialerButtonsListener(view));
        button3 = (Button) view.findViewById(R.id.button3);
        button3.setOnClickListener(new DialerButtonsListener(view));
        button4 = (Button) view.findViewById(R.id.button4);
        button4.setOnClickListener(new DialerButtonsListener(view));
        button5 = (Button) view.findViewById(R.id.button5);
        button5.setOnClickListener(new DialerButtonsListener(view));
        button6 = (Button) view.findViewById(R.id.button6);
        button6.setOnClickListener(new DialerButtonsListener(view));
        button7 = (Button) view.findViewById(R.id.button7);
        button7.setOnClickListener(new DialerButtonsListener(view));
        button8 = (Button) view.findViewById(R.id.button8);
        button8.setOnClickListener(new DialerButtonsListener(view));
        button9 = (Button) view.findViewById(R.id.button9);
        button9.setOnClickListener(new DialerButtonsListener(view));
        button10 = (Button) view.findViewById(R.id.button10);
        button10.setOnClickListener(new DialerButtonsListener(view));
        button11 = (Button) view.findViewById(R.id.button11);
        button11.setOnClickListener(new DialerButtonsListener(view));
        button11.setOnLongClickListener(new DialerButtonsListener(view));
        button12 = (Button) view.findViewById(R.id.button12);
        button12.setOnClickListener(new DialerButtonsListener(view));
        delButton = (ImageButton) view.findViewById(R.id.delButton);
        delButton.setOnClickListener(new DialerButtonsListener(view));
        delButton.setOnLongClickListener(new DialerButtonsListener(view));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialer_layout, container, false);
        setButtonListener(view);
        phoneNumber = (EditText) view.findViewById(R.id.mPhoneNumber);
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataPass.passData(s.toString());
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dataPass = (PassData) activity;
    }
}
