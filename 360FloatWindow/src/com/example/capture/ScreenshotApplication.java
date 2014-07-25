package com.example.capture;

import pkg.screenshot.ScreenshotPolicy;
import android.app.Application;

public class ScreenshotApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ScreenshotPolicy.loadScreenshotLibrary(this);
        
        //progress: A->B
        //1.A send request
        //2.B accept the requst;
        //3.A begin take sreen shot and send to B
        //4.B receive the take screen and show
    }
}
