package com.example.wy.tickto;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wy.tickto.adapter.MainAdapter;
import com.example.wy.tickto.diary.ShowDiaryActivity;
import com.example.wy.tickto.entity.Operation4DB;
import com.example.wy.tickto.entity.ThingInfo;
import com.example.wy.tickto.itemhandle.MyItemTochHelper;
import com.example.wy.tickto.itemhandle.MyItemTouchHelpCallback;
import com.example.wy.tickto.leftmenu.AddThing;
import com.example.wy.tickto.leftmenu.AudioService;
import com.example.wy.tickto.leftmenu.SettingActivity;
import com.example.wy.tickto.leftmenu.aboutus;
import com.example.wy.tickto.timingpush.AlarmReceiver;
import com.example.wy.tickto.timingpush.AlertService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import java.util.List;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final int DB_NOTADD = 1;
    public final int DB_ADD = 2;
    public final int SET_WRITEDIARYTIME = 3;

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;

    //数据源
    //ThingsInfoList用来存储主界面显示的数据
    public List<ThingInfo> ThingsInfoList = new ArrayList<>();

    //数据适配器
    private MainAdapter mainAdapter;

    //滑动拖拽的帮助类
    private MyItemTochHelper itemTouchHelper;

    //设置夜间模式等
    private int mode;

    private int updatePosition;

    //音乐播放所需变量
    private static final int NOTIFICATION_ID = 1; // 如果id设置为0,会导致不能设置为前台service
    public static NotificationManager manager;
    RemoteViews remoteViews;
    Notification notification;
    private Button btn;
    private TextView tv;
    static MainActivity appCompatActivity;
    public static int state = 0;
    //白噪音播放
    public static AudioService audioService;
    //记录写日记时间
    public static int writeDiaryTime_Hour = 0;
    public static int writeDiaryTime_Minute = 0;

    //退出程序的计时变量
    public long time = 0;
    public Calendar calendar=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(appCompatActivity == null){
            appCompatActivity = this;
        }

        //获取系统当前时间
        final Calendar mCalendar=Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mHour=mCalendar.get(Calendar.HOUR);
        int mMinuts=mCalendar.get(Calendar.MINUTE);

        if(mHour*100+mMinuts > writeDiaryTime_Hour*100+writeDiaryTime_Minute){
            //当前时间比设置时间晚，显示提醒
            long start = System.currentTimeMillis() + 10 * 1000;
            Intent intent = new Intent(this, AlertService.class);
            intent.putExtra("id", 10);
            intent.putExtra("title", "日记提醒");
            intent.putExtra("contentText", "别忘了记下每天的点滴哦~");
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, start , pendingIntent);
        }


        //设置初始主题
        //默认白天
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        //获得数据库
        Operation4DB.connect();
        //Operation4DB.deleteall();

        //侧边菜单代码
        //开始
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //自带小按钮
        //添加事件按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, AddThing.class);
                ThingInfo temp = null;
                intent.putExtra("showThingInfo",temp);
                startActivityForResult(intent,DB_NOTADD);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_main);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //必须要设置一个布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mainAdapter = new MainAdapter(ThingsInfoList);
        mainAdapter.setOnItemClickListener(onItemClickListener);
        mainAdapter.setOnCheckedChangeListener(onCheckedChangeListener);
        recyclerView.setAdapter(mainAdapter);

        //加载数据
        //默认加载当天日程
        loadTodayData();

        mainAdapter.notifyDataSetChanged(ThingsInfoList);

        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new MyItemTochHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mainAdapter.setItemTouchHelper(itemTouchHelper);

        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwipeEnable(true);

    }

    /**
     * 显示列表点击监听操作
     * onSwiped 删除操作
     * onMove 交换操作
     * */
    private MyItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener
            = new MyItemTouchHelpCallback.OnItemTouchCallbackListener(){
        //删除
        @Override
        public void onSwiped(int adapterPosition) {
            if(ThingsInfoList != null){
                Operation4DB.deleteone(ThingsInfoList.get(adapterPosition).getId());
                ThingsInfoList.remove(adapterPosition);
                mainAdapter.notifyItemRemoved(adapterPosition);
            }
        }

        //交换
        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if(ThingsInfoList != null){
                // 更换数据源中的数据Item的位置
                Collections.swap(ThingsInfoList, srcPosition,targetPosition);

                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mainAdapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }
    };

    /**
     * RecyclerView的Item点击监听事件
     */
    private MainAdapter.OnItemClickListener onItemClickListener = new MainAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ThingInfo temp = ThingsInfoList.get(position);
            updatePosition = position;
            Intent intent = new Intent(MainActivity.this, AddThing.class);
            intent.putExtra("showThingInfo",temp);
            startActivityForResult(intent,DB_ADD);

        }
    };

    /*
    CheckBox 点击监听操作
     */
    private MainAdapter.OnCheckedChangeListener onCheckedChangeListener = new MainAdapter.OnCheckedChangeListener() {
        @Override
        public void onItemCheckedChange(CompoundButton view, int position, boolean checked) {

            //更新数据库
            ThingInfo click = Operation4DB.find(ThingsInfoList.get(position).getId());  //被点击的事情
            click.setDone(checked);
            click.save();

            //更新数组内容
            ThingsInfoList.get(position).setDone(checked);
            mainAdapter.notifyItemChanged(position);

            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            Toast.makeText(MainActivity.this, checked ? "完成任务" : "还需加油", Toast.LENGTH_SHORT).show();
        }
    };

    //不同活动间的交互
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==DB_NOTADD){
            if(data==null)
            {
                Toast.makeText(MainActivity.this,"暂无添加",Toast.LENGTH_SHORT).show();
                return ;
            }
            ThingInfo newThinginfo = (ThingInfo) data.getSerializableExtra("addThing");

            Operation4DB.add(newThinginfo);
            Log.e(TAG, "测试返回结果 "+newThinginfo.toString());

            ThingsInfoList.add(newThinginfo);
            mainAdapter.notifyDataSetChanged(ThingsInfoList);
        }
        else if(requestCode==DB_ADD){
            if(data==null) {
                Toast.makeText(MainActivity.this,"暂无添加",Toast.LENGTH_SHORT).show();
                return ;
            }
            ThingInfo newThinginfo = (ThingInfo) data.getSerializableExtra("addThing");
            if(newThinginfo==null) {
                Toast.makeText(MainActivity.this,"暂无添加",Toast.LENGTH_SHORT).show();
                return ;
            }
            ThingInfo updateinfo = Operation4DB.find(newThinginfo.getId());

            updateinfo.setWhatthing(newThinginfo.getWhatthing());
            updateinfo.setDone(newThinginfo.isDone());
            updateinfo.setHowimportant(newThinginfo.getHowimportant());
            updateinfo.setNote(newThinginfo.getNote());
            updateinfo.save();

            ThingsInfoList.set(updatePosition,updateinfo);
            mainAdapter.notifyDataSetChanged(ThingsInfoList);
        }
        else if(requestCode==SET_WRITEDIARYTIME){
            //设置通知写日记时间
        }
    }


    //使用ServiceConnection来监听Service状态的变化
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            audioService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //这里我们实例化audioService,通过binder来实现
            audioService = ((AudioService.AudioBinder)binder).getService();

        }
    };

    /*
     侧边菜单
     不做修改
    */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    侧边菜单
    不做修改
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //加载数据到当前面板
    //默认加载当天
    public void loadTodayData(){
        ThingsInfoList.clear();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(new java.util.Date());
        ThingsInfoList = Operation4DB.findtoday(today);
        mainAdapter.notifyDataSetChanged(ThingsInfoList);
    }


    /*
    右上角设置按钮
    开启夜间模式
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_openday) {
            //开启白天模式
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        }
        else if (id == R.id.action_opennight){
            //开启夜间模式
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            recreate();
        }

        return super.onOptionsItemSelected(item);
    }

    //侧边栏菜单点击事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.nav_todaytodo) {
           //加载当天记录
           ThingsInfoList.clear();
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
           String today = format.format(new java.util.Date());
           ThingsInfoList = Operation4DB.findtoday(today);
           mainAdapter.notifyDataSetChanged(ThingsInfoList);

       } else if (id == R.id.nav_beforethings) {
            //加载之前记录
           ThingsInfoList.clear();
           SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
           String today = format.format(new java.util.Date());
           ThingsInfoList = Operation4DB.findbefore(today);
           mainAdapter.notifyDataSetChanged(ThingsInfoList);

       } else if (id == R.id.nav_diary) {
            Intent intent = new Intent(MainActivity.this, ShowDiaryActivity.class);
            startActivity(intent);

       } else if (id == R.id.nav_studymodel) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_menu_gallery);
            builder.setTitle("提示");
            builder.setMessage("是否打开白噪音?");
            //确定按钮
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createNotifcation();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, AudioService.class);
                    startService(intent);
                    bindService(intent,conn,Context.BIND_AUTO_CREATE);
                }
            });

            //取消按钮
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            //显示出对话框
            builder.show();


       }else if (id == R.id.nav_out) {
           //如果在两秒大于2秒
           if (System.currentTimeMillis() - time > 2000) {
               //获得当前的时间
               time = System.currentTimeMillis();
               Toast.makeText(MainActivity.this,"再点击一次退出应用程序",Toast.LENGTH_SHORT).show();
           } else {
                //点击在两秒以内
               finish();
               System.exit(0);
           }

       }else if(id == R.id.nav_setting1){
           Intent intent = new Intent(MainActivity.this, SettingActivity.class);
           startActivityForResult(intent,SET_WRITEDIARYTIME);

       } else if (id == R.id.nav_share) {
           Toast.makeText(MainActivity.this, "快去我的GitHub主页下载吧~~", Toast.LENGTH_SHORT).show();

       }else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent(MainActivity.this, aboutus.class);
            startActivity(intent);
       }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static MainActivity getInstance(){
        return appCompatActivity;
    }

    //设置通知栏
    public void createNotifcation(){
        notification = new Notification();
        notification.icon =R.drawable.musicfile;
        notification.tickerText = "title";
        notification.when = System.currentTimeMillis();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        remoteViews = new RemoteViews(getPackageName(),
                R.layout.widget);
        remoteViews.setTextViewText(R.id.wt_title, "title");
        remoteViews.setImageViewResource(R.id.icon1, R.drawable.musicfile);
        if(state == 0){
            remoteViews.setImageViewResource(R.id.wt_play,R.drawable.pause);
        }else {
            remoteViews.setImageViewResource(R.id.wt_play,R.drawable.play);
        }

        Intent previous=new Intent("com.example.wy.tickto.broadcasttest.PREVIOUS");
        PendingIntent pi_previous = PendingIntent.getBroadcast(MainActivity.this,0,
                previous,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wt_previous,pi_previous);


        Intent play=new Intent("com.example.wy.tickto.broadcasttest.PLAY");
        PendingIntent pi_play = PendingIntent.getBroadcast(MainActivity.this,0,
                play,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wt_play,pi_play);

        Intent next=new Intent("com.example.wy.tickto.broadcasttest.NEXT");
        PendingIntent pi_next = PendingIntent.getBroadcast(MainActivity.this,0,
                next,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wt_next,pi_next);

        Intent clear=new Intent("com.example.wy.tickto.broadcasttest.CLEAR");
        PendingIntent pi_clear = PendingIntent.getBroadcast(MainActivity.this,0,
                clear,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.wt_clear,pi_clear);

        notification.contentView = remoteViews;
        manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID,notification);
    }

    //避免按返回键时直接杀掉程序
    //按两次才退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {

            if((System.currentTimeMillis()-time) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
            }
            else
            {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //设置提醒写日记
    private void setReminder(boolean b) {

        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);

        // 创建将执行广播的PendingIntent
        PendingIntent pi= PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(this,AlarmReceiver.class), 0);
        if(b){

            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }

    }

}
