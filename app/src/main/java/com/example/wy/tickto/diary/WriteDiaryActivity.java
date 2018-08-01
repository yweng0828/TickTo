package com.example.wy.tickto.diary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wy.tickto.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.id;

public class WriteDiaryActivity extends AppCompatActivity {

    public TextView textView;
    public EditText title;
    public EditText body;
    public Button bt_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_diary);

        textView = (TextView)findViewById(R.id.tv_dairy_date);
        title = (EditText)findViewById(R.id.edit_title);
        body = (EditText)findViewById(R.id.edit_body);
        bt_save = (Button)findViewById(R.id.bt_savediary);

        final DiaryContent diaryContent = new DiaryContent();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        textView.setText(str.substring(0,11));
        diaryContent.date = str;

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(body.getText().toString().equals(" ")){
                    //内容为空
                    Toast.makeText(WriteDiaryActivity.this, "没有添加日记哦", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    diaryContent.setContent(body.getText().toString());
                    diaryContent.setTitle(title.getText().toString());
                    diaryContent.save();
                    Toast.makeText(WriteDiaryActivity.this, "添加日记成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }



}
