package org.yooz.safe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import org.yooz.service.KillTaskService;

public class TaskManagerSettingActivity extends ActionBarActivity {

    private CheckBox cb_task_show;
    private SharedPreferences mPre;
    private CheckBox cb_task_time_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPre = getSharedPreferences("config", MODE_PRIVATE);
        initUI();
        initData();
    }

    private void initData() {

    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager_setting);
        /**
         * 显示系统应用监听
         */
        cb_task_show = (CheckBox) findViewById(R.id.cb_task_show);
        boolean system_task = mPre.getBoolean("system_task", false);
        if(system_task) {
            cb_task_show.setChecked(true);
        } else {
            cb_task_show.setChecked(false);
        }
        RelativeLayout rl_show= (RelativeLayout) findViewById(R.id.rl_show);
        rl_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_task_show.isChecked()) {
                    cb_task_show.setChecked(false);
                    mPre.edit().putBoolean("system_task", false).commit();
                } else {
                    cb_task_show.setChecked(true);
                    mPre.edit().putBoolean("system_task", true).commit();
                }
            }
        });

        /**
         * 是否定时清理应用
         */
        cb_task_time_show = (CheckBox) findViewById(R.id.cb_task_time_show);
        RelativeLayout rl_time_kill = (RelativeLayout)  findViewById(R.id.rl_time_kill);
        boolean time_kill = mPre.getBoolean("time_kill", false);
        if(time_kill) {
            cb_task_time_show.setChecked(true);
        } else {
            cb_task_time_show.setChecked(false);
        }
        rl_time_kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_task_time_show.isChecked()) {
                    cb_task_time_show.setChecked(false);
                    mPre.edit().putBoolean("time_kill", false).commit();
                    stopService(new Intent(TaskManagerSettingActivity.this, KillTaskService.class));
                } else {
                    cb_task_time_show.setChecked(true);
                    mPre.edit().putBoolean("time_kill", true).commit();
                    startService(new Intent(TaskManagerSettingActivity.this , KillTaskService.class));
                }
            }
        });
    }


}
