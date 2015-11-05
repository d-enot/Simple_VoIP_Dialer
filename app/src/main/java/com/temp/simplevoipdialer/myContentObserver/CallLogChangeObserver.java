package com.temp.simplevoipdialer.myContentObserver;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Handler;

import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 06.07.2015.
 */
public class CallLogChangeObserver extends ContentObserver {

    private Activity mActivity;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public CallLogChangeObserver(Handler handler, Activity activity) {
        super(handler);
        mActivity = activity;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        MainService.calls.clear();
        MainService.updateCalls();

    }
}
