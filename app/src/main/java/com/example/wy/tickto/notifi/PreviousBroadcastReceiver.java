package com.example.wy.tickto.notifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.wy.tickto.MainActivity;

/**
 * Created by 56989 on 2018/4/25.
 */

//上一曲
public class PreviousBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PreviousBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent){
        final MainActivity musicActivity = MainActivity.getInstance();
        musicActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.e(TAG, "run: "+"上一曲" );
                musicActivity.audioService.player.start();
            }
        });
    }
}