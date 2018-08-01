package com.example.wy.tickto.timingpush;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import com.example.wy.tickto.MainActivity;
import com.example.wy.tickto.R;

import java.util.Calendar;


/**
 * Created by 56989 on 2018/4/28.
 */

public class AlertService extends Service {
    private Context mContext;
    private NotificationManager notificationManager;
    private Notification.Builder mBuilder;
    private Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(mContext);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intent2=new Intent();
        intent2.setClass(this, MainActivity.class);//点击通知需要跳转的activity
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,0, intent2,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification = mBuilder.setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("contentText"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
        return START_REDELIVER_INTENT;
    }
}