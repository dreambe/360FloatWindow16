package pkg.screenshot;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * screen shot main class</br> useage: </p> 1.load the native library:
 * 
 * <pre>
 * System.load(&quot;/data/data/package_name/lib/lib.so&quot;);
 * </pre>
 * 
 * 2.invoke {@link #takeScreenshot()} method to take screenshot, this method is
 * native method, DONT invoke in main thread, it may be block the main thread,
 * and you <b>MUST</b> check the method return value</p> 3.when the
 * {@link #takeScreenshot()} return the {@link #SCREENSHOT_OK}, you can use
 * {@link #generateScreenshotBitmap()} to gernate the bitmap or
 * {@link #getScreensshotBuffer()} to get the screen shot content
 * 
 * </p> 4.when dont need take screenshot, invoke {@link #release()} to release
 * memory and resource </p><b>requirements: need the root permisson, if no root
 * permission, the {@link #takeScreenshot()} will return the
 * {@link #SCREENSHOT_ERROR_PERMISSION} </b>
 * 
 * @author idiottiger
 * 
 */
public class Screenshot {

    public static final String TAG = "Screenshot";

    static {
        System.loadLibrary("screenshot");
    }

    /**
     * take screen successful
     */
    public static final int SCREENSHOT_OK = 0;
    /**
     * when take need root permission
     */
    public static final int SCREENSHOT_ERROR_PERMISSION = 1;
    /**
     * when take throw OutOfMemoryError
     */
    public static final int SCREENSHOT_ERROR_OUTOFMEMORY = 2;
    /**
     * another error make the take screen shot error
     */
    public static final int SCREENSHOT_ERROR_UNKNOWN = 3;

    /**
     * the screen shot twice are same
     */
    public static final int SCREENSHOT_ARE_SAME = 4;

    /**
     * the RGB_565 bitmap config, see {@link Bitmap.Config#RGB_565}
     */
    public static final int CONFIG_RGB_565 = 16;

    /**
     * the ARGB_8888 bitmap config, see {@link Bitmap.Config#ARGB_8888}
     */
    public static final int CONFIG_ARGB_8888 = 32;

    static Screenshot mInstance;

    ByteBuffer mScreenshotBuffer = null;

    Bitmap mBitmap = null;

    int width;

    int height;

    int config;

    private Screenshot() {

    }

    /**
     * only method to create the instance
     * 
     * @return
     */
    public static Screenshot getInstance() {
        if (mInstance == null) {
            mInstance = new Screenshot();
        }
        return mInstance;
    }

    /**
     * set the screen shot header information
     * 
     * @param w
     *            width
     * @param h
     *            height
     * @param c
     *            config
     */
    @CalledByNative
    void setHeader(int w, int h, int c) {
        width = w;
        height = h;
        config = c;
    }

    /**
     * copy the src bytes to the buffer
     * 
     * @param src
     */
    @CalledByNative
    boolean copyBuffer(byte[] src) {
        try {
            if (mScreenshotBuffer == null) {
                mScreenshotBuffer = ByteBuffer.allocate(width * height * config >> 3);
            }
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "allocate byte buffer out of memory", e);
            return false;
        }
        mScreenshotBuffer.rewind();
        mScreenshotBuffer.put(src);
        mScreenshotBuffer.position(0);
        return true;
    }

    /**
     * take screen shot, this is a native method, when finish this method,if the
     * return is {@link #SCREENSHOT_OK} you can invoke
     * {@link #generateScreenshotBitmap()} to gernate the screen shot bitmap or
     * {@link #getScreensshotBuffer()} to get the screen shot content</br>
     * <b>you must check the return value</b>
     * 
     * @return screen shot result code, must be one of {@link #SCREENSHOT_OK},
     *         {@link #SCREENSHOT_ERROR_PERMISSION},
     *         {@link #SCREENSHOT_ERROR_OUTOFMEMORY},
     *         {@link #SCREENSHOT_ERROR_UNKNOWN}, {@link #SCREENSHOT_ARE_SAME}
     *         (this value need when build the lib, add the flag:
     *         -DAVOID_SAME_SCREENSHOT)
     */
    public native int takeScreenshot();

    /**
     * free some native memory and resouce
     */
    public native void free();

    /**
     * release all memory
     */
    public void release() {
        free();
        if (mScreenshotBuffer != null) {
            mScreenshotBuffer.clear();
            mScreenshotBuffer = null;
        }
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mInstance != null) {
            mInstance = null;
        }
    }

    /**
     * create the screen shot bitmap, for the performance, dont recycle
     * 
     * @return screen shot bitmap
     * @throws OutOfMemoryError
     *             when create the bitmap fail because the memory low will throw
     *             it
     */
    public Bitmap generateScreenshotBitmap() throws OutOfMemoryError {
        if (mScreenshotBuffer != null) {
            if (mBitmap == null || mBitmap.isRecycled()) {
                Bitmap.Config bconfig = (config == CONFIG_RGB_565) ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
                try {
                    mBitmap = Bitmap.createBitmap(width, height, bconfig);
                } catch (OutOfMemoryError e) {
                    Log.e(TAG, "create bitmap out of memory", e);
                    throw e;
                }
            }
            mBitmap.copyPixelsFromBuffer(mScreenshotBuffer);
        }
        return mBitmap;
    }

    /**
     * get the screenshot buffer, also you need {@link #takeScreenshot()} first,
     * and invoke this to get the screen shot content, if no
     * {@link #takeScreenshot()} invoke, the buffer may be null
     * 
     * @return
     */
    public ByteBuffer getScreensshotBuffer() {
        return mScreenshotBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getConfig() {
        return config;
    }

}
