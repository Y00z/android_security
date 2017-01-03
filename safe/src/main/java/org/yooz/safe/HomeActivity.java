package org.yooz.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.yooz.MyApplication;
import org.yooz.receiver.AdminReceiver;
import org.yooz.service.LocationService;
import org.yooz.utils.MD5Utils;

public class HomeActivity extends Activity {

    private GridView gvHome;
    // 功能列表名称
    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    // 功能列表图标
    private int[] mIcon = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};

    private SharedPreferences mPref;
    private EditText et_password;
    private EditText et_password_confirm;
    private EditText et_input_password;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);

        if (!mDPM.isAdminActive(mDeviceAdminSample)) {
            tiveAdmin();
        }

        startService(new Intent(this, LocationService.class));

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 8:
                        setting();
                        break;
                    case 6:
                        startActivity(new Intent(HomeActivity.this, DataClearActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(HomeActivity.this, AtoolsActivity.class));
                        break;
                    case 0:
                        showInputPassword();
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this, CallSafeActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(HomeActivity.this, TaskManagerActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(HomeActivity.this, AntivirusActivity.class));
                        break;

                    default:
                        break;
                }
            }
        });

        MyApplication application = (MyApplication) getApplication();
    }

    //激活
    public void tiveAdmin() {
        Toast.makeText(this, "请先激活程序", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "哈哈哈, 我们有了超级设备管理器, 好NB!");
        startActivity(intent);
    }

    // 是否已经有密码
    public void showInputPassword() {
        String password = mPref.getString("password", null);
        if (TextUtils.isEmpty(password)) {
            setPassword();
        } else {
            InputPassword();
        }
    }

    // 输入密码
    public void InputPassword() {
        View view = View.inflate(this, R.layout.dialog_input_password, null);

        et_input_password = (EditText) view
                .findViewById(R.id.et_input_password);

        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        Builder builder = new Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        final String savePassword = mPref.getString("password", null);
        // 确定
        bt_ok.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                String password = et_input_password.getText().toString();
                password = MD5Utils.encode(password);
                if (password.equals(savePassword)) {
                    Toast.makeText(HomeActivity.this, "登录成功",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this,
                            LostFindActivity.class);
                    startActivity(intent);
                } else {
                    System.out.println(password + ":" + savePassword);
                    Toast.makeText(HomeActivity.this, "密码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 取消
        bt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // 设置密码
    protected void setPassword() {
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        et_password = (EditText) view.findViewById(R.id.et_password);
        et_password_confirm = (EditText) view
                .findViewById(R.id.et_password_confirm);

        Builder builder = new Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        // 确定
        bt_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString();
                String password_confirm = et_password_confirm.getText()
                        .toString();
                System.out.println(password + ":" + password_confirm);
                if (!TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(password_confirm)) {
                    if (password.equals(password_confirm)) {
                        password = MD5Utils.encode(password);
                        mPref.edit().putString("password", password).commit();
                        Toast.makeText(HomeActivity.this, "保存成功",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        String savePassword = mPref.getString("password", null);
                    } else {
                        Toast.makeText(HomeActivity.this, "两次输入不同",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框不能为空",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // 取消
        bt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,
                    R.layout.home_list_item, null);
            ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

            tv_item.setText(mItems[position]);
            iv_item.setImageResource(mIcon[position]);

            return view;
        }

    }

    // 跳转设置中心
    public void setting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
