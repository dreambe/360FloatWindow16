package com.example.capture;

import java.io.File;

import com.example.floatwindow.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CapturedImageActivity extends Activity{
	
	static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/screens_shot";
	
	private static Bitmap mBitmap;
	
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
	
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private MyRequestListener requestListener = new MyRequestListener();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_image);
        
        // 创建微博实例
        // mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
              
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        imgView.setImageBitmap(mBitmap);

        ImageButton imgBtn = (ImageButton)findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent=new Intent(Intent.ACTION_SEND);   
                intent.setType("image/*");   
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share");  
                File file = new File(CaptureService.imageFile); 
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
                
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
                startActivity(Intent.createChooser(intent, getTitle()));  
//                mSsoHandler = new SsoHandler(CapturedImageActivity.this, mWeiboAuth);
//                mSsoHandler.authorize(new AuthListener());	
			}
		});
    }
    
    public static void setBitmap(Bitmap bitmap)
    {
    	mBitmap = bitmap;
    }
    
    
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
    	
    	
        
        @Override
        public void onComplete(Bundle values) {
        	Log.e("onComplete","onComplete");
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                //updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
               // AccessTokenKeeper.writeAccessToken(CapturedImageActivity.this, mAccessToken);
                
               
//                WeiboParameters params = new WeiboParameters();
//                params.add("access_token", mAccessToken);
//                params.add("status", "");
//                params.add("pic", mBitmap);
//                AsyncWeiboRunner.requestAsync("https://upload.api.weibo.com/2/statuses/upload.json", params, "POST", requestListener);
               
                WeiboParameters params = new WeiboParameters();
                params.add("access_token", mAccessToken);
                params.add("status", "i love you");
                AsyncWeiboRunner.requestAsync("https://api.weibo.com/2/statuses/update.json", params, "POST", requestListener);
                
                
                
//                Toast.makeText(CapturedImageActivity.this, 
//                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                String code = values.getString("code");
//                //String message = getString(R.string.weibosdk_demo_toast_auth_failed);
//                if (!TextUtils.isEmpty(code)) {
//                    message = message + "\nObtained the code: " + code;
//                }
//                Toast.makeText(CapturedImageActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
//            Toast.makeText(CapturedImageActivity.this, 
//                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(CapturedImageActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    class MyRequestListener implements RequestListener{

		@Override
		public void onComplete(String arg0) {
			// TODO 自动生成的方法存根
			Log.i("weibo","onComplete");
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO 自动生成的方法存根
			Log.i("weibo","onWeiboException:"+arg0.getLocalizedMessage());
		}
    	
    }
    


}
