package pkg.screenshot;

import java.io.DataOutputStream;
import java.io.File;

import android.content.Context;
import android.util.Log;

/**
 * some policy method need do before screen shot
 * 
 * @author idiottiger
 * 
 */
public class ScreenshotPolicy {

    static final String TAG = "ScreenshotPolicy";

    static final String LIB_PATH = "/data/data/%s/lib/libscreenshot.so";

    /**
     * command to can make the screen shot right
     */
    static final String COMMAND = "chmod 777 /dev/graphics/fb0";

    /**
     * run root command
     * 
     * @param command
     * @return root command execute successful return true, otherwise return
     *         false
     */
    static boolean runRootCommand(String command) {
        boolean result = true;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            result = (process.waitFor() == 0);
        } catch (Exception e) {
            result = false;
            Log.e(TAG, "EXCEPTION: run root command:" + command, e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * load the screen shot library, the path may be like
     * 
     * <pre>
     * /data/data/$package_name/lib/libscreenshot.so
     * </pre>
     * 
     * @deprecated the {@link Screenshot} will auto load the library
     * @param context
     */
    public static void loadScreenshotLibrary(Context context) {
        // String path = String.format(LIB_PATH, context.getPackageName());
        // System.load(path);
    }

    /**
     * run root command to acquire the root permission, and will callback the
     * {@link OnAcquireRootPermissionCallback} interface.
     * <p>
     * <b>get the root permission may occur the ANR, it will run in another
     * thread, so the callback will also run in non-ui thread.</b>
     * 
     * @param callback
     */
    public static void acquireRootPermission(final OnAcquireRootPermissionCallback callback) {
        new Thread("get_root") {
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                boolean result = runRootCommand(COMMAND);
                Log.i(TAG, "get root permission: " + result);
                if (callback != null) {
                    callback.onAcquireRootPermission(result);
                }
            };
        }.start();
    }

    /**
     * check current application get the root permission
     * 
     * @return
     */
    public static boolean isCurrentAppAcquiredRoot() {
        File dev = new File("/dev/graphics/fb0");
        return dev.canRead() && dev.canWrite();
    }

    /**
     * the get the root permission callback
     * 
     * @author idiottiger
     * 
     */
    public static interface OnAcquireRootPermissionCallback {
        /**
         * this method dont run in the ui thread
         * 
         * @param acquired
         */
        public void onAcquireRootPermission(boolean acquired);
    }

}
