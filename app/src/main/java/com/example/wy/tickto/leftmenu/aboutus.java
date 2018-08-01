package com.example.wy.tickto.leftmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.wy.tickto.R;

public class aboutus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        TextView textView = (TextView)findViewById(R.id.textview_in_aboutus);
        String aboutme = new String("\n华南理工大学\n计算机科学与工程学院\n2016级计科二班\n翁跃");
        textView.setText(aboutme);
    }
}
