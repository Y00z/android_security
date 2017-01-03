package org.yooz.safe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LockPwdActivity extends ActionBarActivity implements View.OnClickListener {

    private Button bt_0;
    private Button bt_1;
    private Button bt_2;
    private Button bt_3;
    private Button bt_4;
    private Button bt_5;
    private Button bt_6;
    private Button bt_7;
    private Button bt_8;
    private Button bt_9;
    private Button bt_clean_all;
    private Button bt_delete;
    private Button bt_ok;
    private EditText et_pwd;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_pwd);
        initUI();
    }



    private void initUI() {

        Intent intent = getIntent();
        if (intent != null) {
            packageName = intent.getStringExtra("packageName");
        }

        bt_0 = (Button) findViewById(R.id.bt_0);
        bt_1 = (Button) findViewById(R.id.bt_1);
        bt_2 = (Button) findViewById(R.id.bt_2);
        bt_3 = (Button) findViewById(R.id.bt_3);
        bt_4 = (Button) findViewById(R.id.bt_4);
        bt_5 = (Button) findViewById(R.id.bt_5);
        bt_6 = (Button) findViewById(R.id.bt_6);
        bt_7 = (Button) findViewById(R.id.bt_7);
        bt_8 = (Button) findViewById(R.id.bt_8);
        bt_9 = (Button) findViewById(R.id.bt_9);
        bt_clean_all = (Button) findViewById(R.id.bt_clean_all);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        //隐藏键盘
        et_pwd.setInputType(InputType.TYPE_NULL);

        bt_ok.setOnClickListener(this);


        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                if (str.length() == 0) {
                    return;
                }
                et_pwd.setText(str.substring(0, str.length() - 1));
            }
        });

        bt_clean_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_pwd.setText("");
            }
        });

        bt_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_0.getText().toString());
            }
        });

        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_1.getText().toString());
            }
        });

        bt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_2.getText().toString());
            }
        });

        bt_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_3.getText().toString());
            }
        });

        bt_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_4.getText().toString());
            }
        });

        bt_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_5.getText().toString());
            }
        });

        bt_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_6.getText().toString());
            }
        });

        bt_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_7.getText().toString());
            }
        });

        bt_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_8.getText().toString());
            }
        });

        bt_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = et_pwd.getText().toString();
                et_pwd.setText(str + bt_9.getText().toString());
            }
        });
    }



    //用户按返回的时候跳入桌面
    @Override
    public void onBackPressed() {
        // 当用户输入后退健 的时候。我们进入到桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ok:
                String str = et_pwd.getText().toString();
                if (str.equals("135")) {
                    //发送广播，携带停止保护的Action,以及当前应用的包名
                    Intent intent1 = new Intent();
                    intent1.setAction("org.yooz.mobile.stopWatchDog");
                    intent1.putExtra("packageName", packageName);
                    sendBroadcast(intent1);
                    finish();
                } else {
                    Toast.makeText(LockPwdActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }
}
