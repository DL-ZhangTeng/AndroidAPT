package com.zhangteng.apt;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhangteng.annotation.BindView;
import com.zhangteng.annotation.OnClick;
import com.zhangteng.library.BindViewTool;

public class MainActivity extends AppCompatActivity {

    @BindView(value = "tv_hello1")
    TextView textView1;
    @BindView(value = "tv_hello2")
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTool.bind(this);
        if (textView1 != null)
            textView1.setText("Hello World");
        if (textView2 != null)
            textView2.setText("Hello World");
    }

    @OnClick({"tv_hello1", "tv_hello2"})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_hello1) {
            Toast.makeText(this, "tv_hello1", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.tv_hello2) {
            Toast.makeText(this, "tv_hello2", Toast.LENGTH_SHORT).show();
        }
    }
}