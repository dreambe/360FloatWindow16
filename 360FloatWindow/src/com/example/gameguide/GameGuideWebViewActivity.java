
package com.example.gameguide;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.floatwindow.R;

public class GameGuideWebViewActivity extends Activity {
    public static final String EXTRA_STRING_URL = "url";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_guide_webview);
        TextView title = (TextView) findViewById(R.id.title_text);
        title.setText("游戏攻略");
        View titleCloseBtn = findViewById(R.id.title_close_btn);
        titleCloseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView = (WebView) findViewById(R.id.webview);
        String url = getIntent().getStringExtra(EXTRA_STRING_URL);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
