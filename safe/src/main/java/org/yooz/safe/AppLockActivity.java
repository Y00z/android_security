package org.yooz.safe;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.yooz.fragment.LockFragment;
import org.yooz.fragment.UnLockFragment;

public class AppLockActivity extends ActionBarActivity implements View.OnClickListener {

    private TextView tv_unlock;
    private TextView tv_lock;
    private FrameLayout fl_content;
    private FragmentManager supportFragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initData() {

    }

    private void initUI() {
        setContentView(R.layout.activity_app_lock);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);

        tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
        tv_lock.setBackgroundResource(R.drawable.tab_right_default);

        //过去到Fragment管理者
        supportFragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = supportFragmentManager.beginTransaction();
        unLockFragment = new UnLockFragment();
        lockFragment = new LockFragment();
        //第一个参数表示要替换的fragment的id
        //第二个参数表示fragment对象
        mTransaction.replace(R.id.fl_content , unLockFragment).commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_unlock:
                tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_lock.setBackgroundResource(R.drawable.tab_right_default);
                //替换fragment
                fragmentTransaction.replace(R.id.fl_content, unLockFragment);
                System.out.println("未加锁");
                break;
            case R.id.tv_lock:
                tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                //替换fragment
                fragmentTransaction.replace(R.id.fl_content, lockFragment);
                System.out.println("加锁");
                break;
        }
        fragmentTransaction.commit();
    }
}
