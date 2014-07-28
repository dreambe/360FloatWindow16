
package com.example.gameguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
	
    public static final String EXTRA_KEY_STRING_TARGET_PKG_NAME = "target_pkgname";

    private static final String TAG = "ViewGameGuideActivity";

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

    //private String EXTRA_STRING_TARGET_PKG_NAME;

    private TextView mEmptyTextView;

    private ViewGroup mEmtpyView;
    
    private String jsonStr = null;

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
        
        mEmtpyView = (ViewGroup) findViewById(R.id.empty_view);
        mEmptyTextView = (TextView) findViewById(R.id.empty_text_view);
        mEmptyTextView.setText("正在加载..."); //没有加载完成前， 替代ListView显示的EmptyView文案是这个
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmtpyView);

        loadData();
        
        mLoadIconTask = new AsyncTask<String, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(String... arg0) {
                PackageManager pm = getPackageManager();
                String pkgName = arg0[0];
                Drawable icon = null;
                try {
                	icon = pm.getApplicationIcon(pkgName);
                    return icon;
                } catch (NameNotFoundException e) {
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
        mLoadIconTask.execute(getIntent().getStringExtra(EXTRA_KEY_STRING_TARGET_PKG_NAME));
    }

    private void loadData() {
        if (mLoadDataTask == null) {
            mLoadDataTask = new AsyncTask<String, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    mEmptyTextView.setText("正在加载...");
                }

                @Override
                protected Void doInBackground(String... arg0) {
                    String packageName = arg0[0];
                    //String packageName = "com.gameloft.android.ANMP.GloftDMHM";
                    
                    if(jsonStr == null)
                    {
                    	jsonStr = WinFloatUtils.download(packageName);
                    }
                    List<Guide> guides = WinFloatUtils.getGuide(jsonStr);
                    
                    if(guides == null || guides.size() <= 0)
                    {
                    	return null;
                    }
                    for (int i = 0; i < guides.size(); ++i) {
                        GuideItem item = new GuideItem();
                        
                        item.guideTip = guides.get(i).getGuideTitle();
                        item.url = guides.get(i).getUrl();
                        
                        //item.guideTip = "游戏攻略条目" + i;
                        //item.url = "http://www.so.com";
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
                    mEmptyTextView.setText("哎哟，介个游戏暂时还没有提供攻略呢...");
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
            Intent intent = new Intent(this, GameGuideWebViewActivity.class);
            intent.putExtra(GameGuideWebViewActivity.EXTRA_STRING_URL, item.url);
            startActivity(intent);
        }
    }
}
