package org.yooz.safe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.yooz.bean.TaskInfo;
import org.yooz.utils.SystemInfoUtils;
import org.yooz.utils.TaskInfoParser;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends Activity {

    private TextView tv_task_process;
    private TextView tv_task_running;
    private TaskManagerAdapter adapter;
    private List<TaskInfo> taskInfos;
    private ListView list_view;
    private List<TaskInfo> userTaskInfo;
    private List<TaskInfo> systemTaskInfo;
    private int processCount;
    private long availMem;
    private long totalMem;
    private SharedPreferences mPre;
    private LinearLayout ll_progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new TaskManagerAdapter();
            list_view.setAdapter(adapter);
        }
    };


    //全选
    public void taskAll(View v) {
        for (TaskInfo taskInfo : userTaskInfo) {
            taskInfo.setChecked(true);
            //跳过本程序
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
        }
        for (TaskInfo taskInfo : systemTaskInfo) {
            taskInfo.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    //反选
    public void taskInvert(View v) {
        for (TaskInfo taskInfo : userTaskInfo) {
            taskInfo.setChecked(!taskInfo.isChecked());
            //跳过本程序
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
        }
        for (TaskInfo taskInfo : systemTaskInfo) {
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    public void taskEnd(View v) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<TaskInfo> arraylist = new ArrayList<TaskInfo>();
        int totalCount = 0;
        int killMem = 0;
        //集合正在遍历的时候，不能更改集合的大小
        for (TaskInfo taskInfo : userTaskInfo) {
            if (taskInfo.isChecked()) {
                arraylist.add(taskInfo);
//                userTaskInfo.remove(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
//                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }
        for (TaskInfo taskInfo : systemTaskInfo) {
            if (taskInfo.isChecked()) {
                arraylist.add(taskInfo);
//                systemTaskInfo.remove(taskInfo);
//                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }
        if(totalCount == 0 && killMem == 0 ){
            Toast.makeText(this, "请勾选需要清理的应用", Toast.LENGTH_SHORT).show();
            return ;
        }
        //把需要清理的程序都放到一个另一个集合中，然后这个集合里面的元素就是要清理的元素
        for (TaskInfo taskInfo : arraylist) {
            if (taskInfo.isUserApp()) {
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                userTaskInfo.remove(taskInfo);
            } else {
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                systemTaskInfo.remove(taskInfo);
            }
        }

        Toast.makeText(this, "共清理了" + totalCount + "个进程，" + "释放了" + Formatter.formatFileSize(this, killMem) + "内存", Toast.LENGTH_SHORT).show();
        processCount -= totalCount;
        tv_task_process.setText("进程:(" + processCount + ")个");
        tv_task_running.setText("剩余/总:" + Formatter.formatFileSize(this,availMem + killMem) +
                "/" + Formatter.formatFileSize(this,totalMem));
        adapter.notifyDataSetChanged();
    }

    public void taskSetting(View v) {
        startActivity(new Intent(this,TaskManagerSettingActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        if(adapter !=null) {
            System.out.println("adapter!=null");
            adapter.notifyDataSetChanged();
        }
    }

    class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            boolean system_task = mPre.getBoolean("system_task", false);
            if(system_task) {
                return userTaskInfo.size() + 1 + systemTaskInfo.size() + 1;
            }else {
                return userTaskInfo.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfo.size() + 1) {
                return null;
            }
            TaskInfo taskInfo;
            if (position < userTaskInfo.size() + 1) {
                taskInfo = userTaskInfo.get(position - 1);
            } else {
                int location = userTaskInfo.size() + 2;
                taskInfo = systemTaskInfo.get(position - location);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //添加两个特殊条目，一个用户程序条目，一个系统程序条目
            if (position == 0) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userTaskInfo.size() + ")");
                return textView;
            } else if (position == userTaskInfo.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemTaskInfo.size() + ")");
                return textView;
            }
            ViewHolder holder;
            View view;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder = new ViewHolder();
                holder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tv_task_title = (TextView) view.findViewById(R.id.tv_task_title);
                holder.tv_task_process = (TextView) view.findViewById(R.id.tv_task_process);
                holder.cb_task_process = (CheckBox) view.findViewById(R.id.cb_task_process);
                view.setTag(holder);
            }
            TaskInfo taskInfo;
            //把两个特殊的条目从appInfo中减去
            if (position < userTaskInfo.size() + 1) {
                taskInfo = userTaskInfo.get(position - 1);
            } else {
                int location = userTaskInfo.size() + 2;
                taskInfo = systemTaskInfo.get(position - location);
            }
            holder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_task_title.setText(taskInfo.getAppName());
            holder.tv_task_process.setText(Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));
            if (taskInfo.isChecked()) {
                holder.cb_task_process.setChecked(true);
            } else {
                holder.cb_task_process.setChecked(false);
            }
            //判断当前展示的item是否是自己的程序。如果是。就把程序给隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                //隐藏
                holder.cb_task_process.setVisibility(View.INVISIBLE);
            } else {
                //显示
                holder.cb_task_process.setVisibility(View.VISIBLE);
            }

            ll_progress.setVisibility(View.GONE);
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_task_icon;
        TextView tv_task_title;
        TextView tv_task_process;
        CheckBox cb_task_process;
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                userTaskInfo = new ArrayList<TaskInfo>();
                systemTaskInfo = new ArrayList<TaskInfo>();
                for (TaskInfo taskInfo : taskInfos) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfo.add(taskInfo);
                    } else {
                        systemTaskInfo.add(taskInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        tv_task_process = (TextView) findViewById(R.id.tv_task_process);
        tv_task_running = (TextView) findViewById(R.id.tv_task_running);
        list_view = (ListView) findViewById(R.id.list_view);
        mPre = getSharedPreferences("config", MODE_PRIVATE);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);

        processCount = SystemInfoUtils.getProcessCount(this);
        tv_task_process.setText("进程:(" + processCount + ")个");
        availMem = SystemInfoUtils.getAvailMem(this);
        totalMem = SystemInfoUtils.getTotalMem(this);

        tv_task_running.setText("剩余/总:" + Formatter.formatFileSize(this, availMem)
                + "/" + Formatter.formatFileSize(this, totalMem));

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到点击listview的条目对象
                Object itemAtPosition = list_view.getItemAtPosition(position);
                if (itemAtPosition != null && itemAtPosition instanceof TaskInfo) {
                    TaskInfo taskInfo = (TaskInfo) itemAtPosition;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    //让本程序勾选不了。
                    if (taskInfo.getPackageName().equals(getPackageName())) {
                        return;
                    }
                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.cb_task_process.setChecked(false);
                        System.out.println("false");
                    } else {
                        taskInfo.setChecked(true);
                        holder.cb_task_process.setChecked(true);
                        System.out.println("true");
                    }
                }
            }
        });
    }

}
