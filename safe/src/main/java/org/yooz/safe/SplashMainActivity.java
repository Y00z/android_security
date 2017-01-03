package org.yooz.safe;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.yooz.bean.AntivirusJsonInfo;
import org.yooz.dao.AntivirusDao;
import org.yooz.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashMainActivity extends ActionBarActivity {

    protected static final int CODE_UPDATE_DIALOG = 0;
    protected static final int CODE_URL_DIALOG = 1;
    protected static final int CODE_NET_DiALOG = 2;
    protected static final int CODE_JSON_DIALOG = 3;
    protected static final int CODE_ENTER_HOME = 4;
    private TextView tvVersion;
    private String mVersionName;
    private int mVersionCode;
    private String mDescrition;
    private String mDownloadUrl;

    private SharedPreferences mPre;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_DIALOG:
                    Toast.makeText(SplashMainActivity.this, "url错误",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_DiALOG:
                    Toast.makeText(SplashMainActivity.this, "网络错误aa",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_DIALOG:
                    Toast.makeText(SplashMainActivity.this, "解析错误",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RelativeLayout rl_root = (RelativeLayout) findViewById(R.id.rl_root);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        // 动态设置版本号
        tvVersion.setText("版本号：" + getVersionName());
        mPre = getSharedPreferences("config", MODE_PRIVATE);
        boolean flag = mPre.getBoolean("auto_update", true);
        if (flag) {
            checkVersion();
        } else {
            handler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
        }

        copyDB("address.db");

        copyDB("antivirus.db");

        upDataVirus();

        createShortcut();


        AlphaAnimation anima = new AlphaAnimation(0.5f, 1f);
        anima.setDuration(2000);
        rl_root.startAnimation(anima);
    }

    //更新病毒数据库
    private void upDataVirus() {
        HttpUtils httpUtils = new HttpUtils();
        String url = "";
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
//				JSONObject jsonObject = new JSONObject(responseInfo.result);
                Gson gson = new Gson();
                AntivirusJsonInfo antivirusJsonInfo = gson.fromJson(responseInfo.result, AntivirusJsonInfo.class);
//					String md5 = jsonObject.getString("md5");
//					String desc = jsonObject.getString("desc");
                AntivirusDao.addVirus(antivirusJsonInfo.md5, antivirusJsonInfo.desc);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    //快捷图标
    private void createShortcut() {
        Intent intent = new Intent();
        //你想干什么
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //表示是否可以创建重复的快捷方式
        intent.putExtra("duplicate", false);
        //你长什么样
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, R.drawable.ic_launcher);
        //你叫什么名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "我是快捷方式");

        Intent shortcut_intent = new Intent(this, SplashMainActivity.class);
        shortcut_intent.setAction("home_shortcut");
        shortcut_intent.addCategory(Intent.CATEGORY_DEFAULT);
        //点了你之后能干嘛
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);
        sendBroadcast(intent);
    }

    // 获取版本号
    public int getVersionCode() {
        // 获取版本包管理器
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {// 获取版本信息，参数传递包名
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionCode = packageInfo.versionCode;
        // 返回版本号
        return versionCode;
    }

    // 获取版本名
    public String getVersionName() {
        // 获取版本包管理器
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {// 获取版本信息，参数传递包名
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = packageInfo.versionName;
        return versionName;
    }

    // 访问应用更新服务器并解析
    public void checkVersion() {
        final long startTime = System.currentTimeMillis();
        new Thread() {
            public void run() {

                Message msg = handler.obtainMessage();
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://192.168.1.189:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(2000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        // 获取到json数据
                        String result = StreamUtils.readFormInputStream(conn
                                .getInputStream());
                        // 解析json
                        JSONObject json = new JSONObject(result);
                        mVersionName = json.getString("versionName");
                        mVersionCode = json.getInt("versionCode");
                        mDescrition = json.getString("descrition");
                        mDownloadUrl = json.getString("downloadUrl");

                        // 如果服务器版本大于本应用版本
                        if (mVersionCode > getVersionCode()) {
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            // 没有更新版本,强制休眠一段时间，至少2秒钟
                            long endTime = System.currentTimeMillis();
                            long UserTime = endTime - startTime;
                            if (UserTime < 2000) {
                                try {
                                    Thread.sleep(2000 - UserTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            msg.what = CODE_ENTER_HOME;
                        }
                    }

                } catch (MalformedURLException e) {
                    // url错误异常
                    msg.what = CODE_URL_DIALOG;
                    e.printStackTrace();
                } catch (IOException e) {
                    // 网络错误异常
                    msg.what = CODE_NET_DiALOG;
                    e.printStackTrace();
                } catch (JSONException e) {
                    // json解析异常
                    msg.what = CODE_JSON_DIALOG;
                    e.printStackTrace();
                } finally {
                    // 发送消息
                    handler.sendMessageDelayed(msg, 2000);
                    if (conn != null) {
                        // 关闭连接
                        conn.disconnect();
                    }
                }
            }
        }.start();

    }

    // 跳转到主界面
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // 升级对话框
    protected void showUpdateDialog() {
        Builder builder = new Builder(this);
        builder.setTitle("发现新版本" + mVersionName);
        builder.setMessage(mDescrition);
        // builder.setCancelable(false);
        builder.setPositiveButton("立即更新", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Download();
            }
        });

        builder.setNegativeButton("劳资不更新", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SplashMainActivity.this, "不更新玩你麻痹",
                        Toast.LENGTH_SHORT).show();
                enterHome();
            }
        });

        // 如果用户点的不更新，就跳进主界面。
        builder.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });

        builder.show();
    }

    // 更新下载apk
    protected void Download() {
        // 是否有sd卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String target = Environment.getExternalStorageDirectory()
                    + "/update.apk";
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    Toast.makeText(SplashMainActivity.this, "下载完成",
                            Toast.LENGTH_SHORT).show();
                    // 下载成功后自动跳到安装页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(arg0.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 8);
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Toast.makeText(SplashMainActivity.this, "下载失败",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);

                    // 使用进度条对话框显示下载进度
                    ProgressDialog pd = new ProgressDialog(
                            SplashMainActivity.this);
                    pd.setTitle("正在下载");
                    pd.setMax((int) total);
                    pd.incrementProgressBy((int) current);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.show();

                }
            });
        } else {
            Toast.makeText(SplashMainActivity.this, "下载失败，没有找到sd卡",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 如果用户点击了取消安装，就跳转到主页面
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        System.out.println(requestCode);
        System.out.println(responseCode);
        if (requestCode == 8) {
            enterHome();
        }
    }

    // 初始化归属地数据库
    public void copyDB(String dbName) {
        FileOutputStream out = null;
        InputStream in = null;
        File file = new File(getFilesDir(), dbName);
        if (file.exists()) {
            System.out.println("归属地数据库:" + dbName + "已经存在");
            return;
        }
        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(file);
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
