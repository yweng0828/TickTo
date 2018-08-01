package com.example.wy.tickto.diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wy.tickto.MainActivity;
import com.example.wy.tickto.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ShowDiaryActivity extends AppCompatActivity {

    public final int WRITEDNEWDIART = 4;
    public final int SHOWDETAIL = 5;
    //只显示标题和日期
    public List<String> data = new ArrayList<>();
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView)findViewById(R.id.textview_showdiary);

        //将往期内容显示在listview中
        final List<DiaryContent> content = DataSupport.findAll(DiaryContent.class);
        for(int i=0; i<content.size(); i++){
            data.add(content.get(i).get_TitleAndDate());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowDiaryActivity.this,
            android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);

        //添加listview的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //显示内容
                //暂时默认是不可修改的
                DiaryContent diaryContent = content.get(position);
                Intent intent = new Intent(ShowDiaryActivity.this, DetailContentActivity.class);
                intent.putExtra("DiaryContent",diaryContent);
                startActivity(intent);
                Toast.makeText(ShowDiaryActivity.this,"显示日记",Toast.LENGTH_SHORT).show();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //添加新的日志
                Intent intent = new Intent(ShowDiaryActivity.this, WriteDiaryActivity.class);
                startActivityForResult(intent,WRITEDNEWDIART);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==WRITEDNEWDIART){
            recreate();
        }
    }
}
