
package com.example.gameguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.floatwindow.R;

public class ViewGameGuideActivity extends Activity implements OnItemClickListener {
    public static final String EXTRA_KEY_STRING_TARGET_PKG_NAME = "target_pkg";

    private static final String TAG = "ViewGameGuideActivity";

    private ImageView mAppIconImageView;

    private ListView mListView;

    private BaseAdapter mAdapter;

    private final class GuideItem {
        String guideTip;

        String url;
    }

    private ArrayList<GuideItem> mGuideLists = new ArrayList<GuideItem>();

    private List<HashMap<String, String>> mGuideListItemMap = new ArrayList<HashMap<String, String>>();

    private AsyncTask<String, Void, Void> mLoadDataTask;

    private ImageView mAppIconImage;

    private AsyncTask<String, Void, Drawable> mLoadIconTask;

    private Animation mExpandAlphaInAnim;

    private String EXTRA_STRING_TARGET_PKG_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game_guide);
        TextView title = (TextView) findViewById(R.id.title_text);
        title.setText("游戏攻略");
        View titleClose = findViewById(R.id.title_close_btn);
        titleClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAppIconImage = (ImageView) findViewById(R.id.app_icon);
        mExpandAlphaInAnim = AnimationUtils.loadAnimation(this, R.anim.expand_alpha_in);
        mListView = (ListView) findViewById(R.id.guide_list);
        mAdapter = new SimpleAdapter(this, mGuideListItemMap, R.layout.guide_list_item, new String[] {
            "GUIDE_TEXT"
        }, new int[] {
            R.id.text
        });
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);

        loadData();
        mLoadIconTask = new AsyncTask<String, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(String... arg0) {
                PackageManager pm = getPackageManager();
                String pkgName = arg0[0];
                try {
                    Drawable icon = pm.getApplicationIcon(pkgName);
                    return icon;
                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Drawable result) {
                if (isFinishing()) {
                    return;
                }
                if (result == null) {
                    mAppIconImage.setImageResource(R.drawable.icon_other_app_default);
                } else {
                    mAppIconImage.setImageDrawable(result);
                }
                mAppIconImage.startAnimation(mExpandAlphaInAnim);
                mLoadIconTask = null;
            }
        };
        mLoadIconTask.execute(getIntent().getStringExtra(EXTRA_STRING_TARGET_PKG_NAME));
    }

    private void loadData() {
        if (mLoadDataTask == null) {
            mLoadDataTask = new AsyncTask<String, Void, Void>() {

                @Override
                protected Void doInBackground(String... arg0) {
                    String pkgName = arg0[0];
                    // TODO: 添加真实获取数据的流程 

                    for (int i = 0; i < 3; ++i) {
                        GuideItem item = new GuideItem();
                        item.guideTip = "游戏攻略条目" + i;
                        item.url = "http://360.cn";
                        mGuideLists.add(item);
                    }
                    // 转化为SimpleAdapter可用的数据
                    for (GuideItem item : mGuideLists) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("GUIDE_TEXT", item.guideTip);
                        mGuideListItemMap.add(map);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (isFinishing()) {
                        return;
                    }
                    mAdapter.notifyDataSetChanged();
                    mLoadDataTask = null;
                }

            };
            mLoadDataTask.execute(getIntent().getStringExtra(EXTRA_KEY_STRING_TARGET_PKG_NAME));
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
        if (mLoadIconTask != null) {
            mLoadIconTask.cancel(true);
            mLoadIconTask = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (mGuideLists.size() > arg2) {
            GuideItem item = mGuideLists.get(arg2);
            Log.d(TAG, "item.guideTip:" + item.guideTip);
            Log.d(TAG, "item.url:" + item.url);
        }
    }

}
