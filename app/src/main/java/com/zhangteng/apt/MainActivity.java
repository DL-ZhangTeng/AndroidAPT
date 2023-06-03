package com.zhangteng.apt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangteng.annotation.BindView;
import com.zhangteng.annotation.OnClick;
import com.zhangteng.library.BindViewTool;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.tv_hello1)
    TextView textView1;
    @SuppressLint("NonConstantResourceId")
    @BindView(value = R.id.tv_hello2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BindViewTool.bind(this);

        textView1.setText("Hello World");
        textView2.setText("Hello World");
    }

    @OnClick({R.id.tv_hello1, R.id.tv_hello2})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_hello1) {
            Toast.makeText(this, "tv_hello1", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.tv_hello2) {
            Toast.makeText(this, "tv_hello2", Toast.LENGTH_SHORT).show();
        }
    }
}