package com.example.chao4_wang.html5test;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NativeActivity extends Activity {
    private Button mBtnTestClick = null;
    private Button mBtnTestClick2 = null;
    private String text = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        Intent intent = getIntent();
        text = intent.getStringExtra("WV");

        mBtnTestClick = (Button) this.findViewById(R.id.btn_test_click);
        mBtnTestClick2 = (Button) this.findViewById(R.id.btn_test_click2);
        mBtnTestClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToDo
                Log.e("CollinWang", "Click have run:" + text);
            }
        });

        mBtnTestClick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToDo
                Log.e("CollinWang", "Click2 have run");
                Intent i = new Intent();
                i.putExtra("param", "test1111111111");
                setResult(111, i);
                finish();
            }
        });
    }
}
