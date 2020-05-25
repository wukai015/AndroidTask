package com.example.mybrowser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_go;
    private EditText et_url;
    private String urlHead="https://";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_url = (EditText) findViewById(R.id.et_url);
        btn_go = (Button) findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建Intent
                Intent intent = new Intent();
                // 为Intent设置Action属性
                intent.setAction(Intent.ACTION_VIEW);
                // 设置Data属性
                intent.setData(Uri.parse(urlHead+et_url.getText().toString()));
                startActivity(intent);
            }
        });

    }


}

