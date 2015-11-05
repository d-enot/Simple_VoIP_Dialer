package com.temp.simplevoipdialer.listeners;

import android.view.View;
import android.widget.EditText;

import com.temp.simplevoipdialer.R;

/**
 * Created by klim-mobile on 31.08.2015.
 */
public class DialerButtonsListener implements View.OnClickListener, View.OnLongClickListener{

    private static EditText phoneN;
    private View mView;

    public DialerButtonsListener(View view) {
        mView = view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            setTextInPhoneNumber("1", false);
        }else if (v.getId() == R.id.button2) {
            setTextInPhoneNumber("2", false);
        } else if (v.getId() == R.id.button3) {
            setTextInPhoneNumber("3", false);
        }else if (v.getId() == R.id.button4) {
            setTextInPhoneNumber("4", false);
        }else if (v.getId() == R.id.button5) {
            setTextInPhoneNumber("5", false);
        }else if (v.getId() == R.id.button6) {
            setTextInPhoneNumber("6", false);
        }else if (v.getId() == R.id.button7) {
            setTextInPhoneNumber("7", false);
        }else if (v.getId() == R.id.button8) {
            setTextInPhoneNumber("8", false);
        }else if (v.getId() == R.id.button9) {
            setTextInPhoneNumber("9", false);
        }else if (v.getId() == R.id.button10) {
            setTextInPhoneNumber("*", false);
        }else if (v.getId() == R.id.button11) {
            setTextInPhoneNumber("0", false);
        }else if (v.getId() == R.id.button12) {
            setTextInPhoneNumber("#", false);
        }else if (v.getId() == R.id.delButton) {
            delitTextInPhoneNumber();
        }
    }



    private void setTextInPhoneNumber(String text, boolean del) {
        phoneN = (EditText) mView.findViewById(R.id.mPhoneNumber);
        if (!del) {
            int start = phoneN.getSelectionStart();
            phoneN.getText().insert(start, text);
        }else phoneN.setText(text);

    }

    private void delitTextInPhoneNumber() {
        phoneN = (EditText) mView.findViewById(R.id.mPhoneNumber);
        String dialled_nos = phoneN.getText().toString();
        int remove_index_position = phoneN.getSelectionStart()-1;
        StringBuilder dialled_nos_builder = new StringBuilder(dialled_nos);
        if(remove_index_position>=0) {
            dialled_nos_builder.deleteCharAt(remove_index_position);
            phoneN.setText(dialled_nos_builder.toString());
            phoneN.setSelection(remove_index_position);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.delButton) {
            setTextInPhoneNumber("", true);
            return true;
        }else if (v.getId() == R.id.button11) {
            setTextInPhoneNumber("+", false);
            return true;
        }
        return false;
    }
}
