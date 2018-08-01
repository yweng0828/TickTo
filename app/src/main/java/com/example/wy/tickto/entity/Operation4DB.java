package com.example.wy.tickto.entity;

import android.util.Log;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 56989 on 2018/4/23.
 */

public class Operation4DB {

    public static long StaticId = 0;
    public static List<ThingInfo> tempList = new ArrayList<>();
    private static final String TAG = "Operation4DB";

    //连接
    public static void connect() {
        Connector.getDatabase();
    }

    //添加
    public static boolean add(ThingInfo thingInfo){
        thingInfo.setId(StaticId++);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        thingInfo.date = format.format(new java.util.Date());
        return thingInfo.save();
    }

    //更新操作
    public static boolean update(long id){
        return true;
    }

    //删除
    public static boolean deleteone(long id){
        DataSupport.delete(ThingInfo.class, id);
        return true;
    }

    public static boolean deleteall(){
        DataSupport.deleteAll(ThingInfo.class);
        return true;
    }

    //查询
    public static ThingInfo find(long id){
        return DataSupport.find(ThingInfo.class,id);
    }

    //查询全部
    public static List<ThingInfo> findAll(){
        return DataSupport.findAll(ThingInfo.class);
    }

    //查询今天的记录
    public static List<ThingInfo> findtoday(String today){
        return DataSupport.where("date=?",today).find(ThingInfo.class);
    }

    //查询不是今天，之前的记录
    public static List<ThingInfo> findbefore(String today){
        return DataSupport.where("date!=?",today).find(ThingInfo.class);
    }
}
