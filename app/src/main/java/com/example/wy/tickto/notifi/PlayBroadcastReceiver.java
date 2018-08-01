package com.example.wy.tickto.notifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.wy.tickto.MainActivity;

/**
 * Created by 56989 on 2018/4/25.
 */

//开始播放或者停止播放
public class PlayBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PlayBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent){
        final MainActivity musicActivity = MainActivity.getInstance();
//        musicActivity.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Log.e(TAG, "run: "+"开始或者停止" );
//                if(!musicActivity.audioService.player.isPlaying()){
//                    musicActivity.audioService.player.start();
//                    MainActivity.state = 1;
//                }
//
//                else {
//                    musicActivity.audioService.player.stop();
//                    MainActivity.state = 0;
//                }
//
//            }
//        });
        Log.e(TAG, "run: "+"开始或者暂停" );
        if(MainActivity.state == 0){
            MainActivity.state = 1;
            MainActivity.audioService.player.start();
        }else {
            MainActivity.state = 0;
            MainActivity.audioService.player.pause();
            //MainActivity.audioService.player.seekTo(0);
        }
        musicActivity.createNotifcation();
    }

}

