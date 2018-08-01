package com.example.wy.tickto.notifi;

/**
 * Created by 56989 on 2018/4/25.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.wy.tickto.MainActivity;

//关闭
public class ClearBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ClearBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent){
        final MainActivity musicActivity = MainActivity.getInstance();
        musicActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: "+"关闭");
                musicActivity.audioService.onDestroy();
            }
        });

    }


}

