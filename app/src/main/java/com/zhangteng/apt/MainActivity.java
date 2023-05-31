package com.zhangteng.apt;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangteng.annotation.BindView;
import com.zhangteng.library.BindViewTool;

public class MainActivity extends AppCompatActivity {

    @BindView(value = "tv_hello")
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTool.bind(this);
        if (textView != null)
            textView.setText("Hello World");
    }
}