package com.example.wy.tickto.leftmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.wy.tickto.R;
import com.example.wy.tickto.entity.ThingInfo;

public class AddThing extends AppCompatActivity {

    private static final String TAG = "AddThing";
    public ThingInfo info;
    public boolean isupdate = false;

    public RadioButton rb1 = null;
    public RadioButton rb2 = null;
    public RadioButton rb3 = null;
    public EditText whatthing = null;
    public EditText note = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_thing);

        Intent intent1 = getIntent();
        info = (ThingInfo) intent1.getSerializableExtra("showThingInfo");

        if(info == null) {
            isupdate = false;
        }
        else {
            isupdate = true;
        }

        rb1 = (RadioButton)findViewById(R.id.rb_important);
        rb2 = (RadioButton)findViewById(R.id.rb_medium);
        rb3 = (RadioButton)findViewById(R.id.rb_easy);
        whatthing = (EditText)findViewById(R.id.edit_thing);
        note = (EditText)findViewById(R.id.edit_note);

        if(isupdate){
            //修改原有的事件
            Log.e(TAG, "onClick: 进入设置界面");
            whatthing.setText(info.getWhatthing());
            note.setText(info.getNote());
            int mark = info.getHowimportant();
            if(mark==1){
                rb1.setChecked(true);
            }else if(mark==2){
                rb2.setChecked(true);
            }else{
                rb3.setChecked(true);
            }
        }else{
            info = new ThingInfo();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addThing);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                //修改或者添加
                info.setWhatthing(whatthing.getText().toString());
                info.setNote(note.getText().toString());
                if(!isupdate)
                    info.setDone(false);

                if(rb1.isChecked()) {
                    info.setHowimportant(1);
                }else if(rb2.isChecked()){
                    info.setHowimportant(2);
                }else if (rb3.isChecked()){
                    info.setHowimportant(3);
                }



                if(isupdate) Toast.makeText(AddThing.this, "更新成功",Toast.LENGTH_SHORT).show();
                else Toast.makeText(AddThing.this, "添加成功",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("addThing",info);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           finish();
        }

        return super.onKeyDown(keyCode, event);

    }
}
