package com.example.wy.tickto.diary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.wy.tickto.R;

public class DetailContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_content);

        Intent intent1 = getIntent();
        DiaryContent info = (DiaryContent) intent1.getSerializableExtra("DiaryContent");

        TextView textView = (TextView)findViewById(R.id.textview_detail);
        textView.setText(info.toString());
    }
}
