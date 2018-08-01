package com.example.wy.tickto.diary;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 56989 on 2018/4/28.
 */

public class DiaryContent extends DataSupport implements Serializable{

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
    //内容
    public String content = new String();
    //标题
    public String title = new String();
    //日期
    public Date temp = new Date(System.currentTimeMillis());
    public String date = format.format(temp);

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String get_TitleAndDate(){
        return date + '\n' + title;
    }

    @Override
    public String toString() {
        return "日期： "+date + '\n' + "主题： " + title +"\n\n"+ "内容：\n"+content;
    }
}
