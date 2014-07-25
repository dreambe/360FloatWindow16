package com.example.capture;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class ScreenshotUtils {

    static final String TAG = "ScreenshotUtils";

    /** use to save the bitmap compress to jpg */
    static ByteArrayOutputStream mBitmapBytesStream = new ByteArrayOutputStream(100 * 1024);

    /** the AUTH_END */
    static final String AUTH_END = UUID.randomUUID().toString();

    static final int AUTH_HEAD_BYTES_SIZE = 16, AUTH_END_BYTES_SIZE = AUTH_END.getBytes().length;

    /** the default jpg compress quality */
    static final int DEFAULT_COMPRESS_QUALITY = 100;

    /** the default bitmap json key */
    public static final String DEFAULT_GET_BITMAP_JSON_KEY = "GetJsonImageResult", DEFAULT_UPLOAD_BITMAP_JSON_KEY = "UploadJsonImage";

    /** header bytes */
    public static final byte[] mHeaderBytes = new byte[AUTH_HEAD_BYTES_SIZE];

    private static void initHeaderBytes(int offset, int head) {
        mHeaderBytes[offset] = (byte) (head >> 24);
        mHeaderBytes[offset + 1] = (byte) (head >> 16);
        mHeaderBytes[offset + 2] = (byte) (head >> 8);
        mHeaderBytes[offset + 3] = (byte) (head);
    }

    /** use 0xcafe to init the header */
    static {
        initHeaderBytes(0, 'c');
        initHeaderBytes(4, 'a');
        initHeaderBytes(8, 'f');
        initHeaderBytes(12, 'e');
    }

    /**
     * convert the bitmap to string based on Base64, and aslo write the header
     * and end
     * 
     * @param map
     * @return
     * @throws IOException
     */
    public static String convertBitmap2AuthBase64String(Bitmap map) throws IOException {
        String result = null;
        mBitmapBytesStream.reset();
        writeAuthHeader();
        boolean compress = map.compress(Bitmap.CompressFormat.JPEG, DEFAULT_COMPRESS_QUALITY, mBitmapBytesStream);
        if (compress) {
            writeAuthEnd();
            final byte[] bytes = mBitmapBytesStream.toByteArray();
            result = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return result;
    }

    /**
     * convert the bitmap to json string, the return like:
     * 
     * <pre>
     * {"key":"value"}
     * </pre>
     * 
     * @param map
     * @param key
     * @return
     * @throws IOException
     */
    public static String convertBitmap2JsonString(Bitmap map, String key) throws IOException {
        final StringBuilder builder = new StringBuilder();
        String result = convertBitmap2AuthBase64String(map);
        if (result != null) {
            builder.append("{\"");
            builder.append(key);
            builder.append("\":\"");
            builder.append(result);
            result = null;
            builder.append("\"}");
        }
        return builder.toString();
    }

    /**
     * save bitmap to file
     * 
     * @param map
     * @param file
     * @throws IOException
     */
    public static void saveBitmap2File(Bitmap map, String file) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 16 * 1024);
        map.compress(Bitmap.CompressFormat.PNG, DEFAULT_COMPRESS_QUALITY, bos);
        bos.flush();
        bos.close();
        bos = null;
    }

    /**
     * save json string to file
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public static void saveJsonString2File(String json, String path) throws IOException {
        byte[] jsonBytes = json.getBytes();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path), 16 * 1024);
        bos.write(jsonBytes);
        bos.flush();
        bos.close();
        jsonBytes = null;
    }

    /**
     * covert the json file to bitmap
     * 
     * @param jsonFile
     * @param key
     * @return
     * @throws IOException
     * @throws OutOfMemoryError
     */
    public static Bitmap convertJsonFile2Bitmap(String jsonFile, String key) throws IOException, OutOfMemoryError {
        File file = new File(jsonFile);
        if (file.exists() && key != null) {
            int size = (int) file.length();
            if (size > 0) {
                Log.i(TAG, "size:" + size);
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), 16 * 1024);
                byte[] bytes = new byte[size];
                int offset = 0, readed = 0;
                while ((readed = bis.read(bytes, offset, size - offset)) != -1 && readed != size && offset != size) {
                    offset += readed;
                    Log.i(TAG, "offset:" + offset + ",readed:" + readed);
                }
                Log.i(TAG, "end offset:" + offset + ",readed:" + readed);
                bis.close();
                bis = null;
                return convertJsonStringBytes2Bitmap(bytes, key);
            }
        }
        return null;
    }

    /**
     * convert the json string to bitmap
     * 
     * @param jsonString
     * @param key
     * @return
     * @throws IOException
     * @throws OutOfMemoryError
     */
    public static Bitmap convertJsonString2Bitmap(String jsonString, String key) throws IOException, OutOfMemoryError {
        if (jsonString != null && key != null) {
            return convertJsonStringBytes2Bitmap(jsonString.getBytes(), key);
        }
        return null;
    }

    private static Bitmap convertJsonStringBytes2Bitmap(byte[] bytes, String key) throws IOException, OutOfMemoryError {
        int size = bytes.length;
        // reduce json header and ender size
        // json header: {"key":"
        // json ender: "}
        String jHeader = "{\"" + key + "\":\"";
        int start = jHeader.getBytes().length;
        String jEnd = "\"}";
        int length = size - start - jEnd.getBytes().length;
        // Log.i(TAG, "start:" + start + ",length:" + length);

        if (start >= size - 1 || length < 0) {
            return null;
        }
        bytes = Base64.decode(bytes, start, length, Base64.DEFAULT);

        // reduce auth header and ender
        size = bytes.length;
        start = AUTH_HEAD_BYTES_SIZE;
        length = size - start - AUTH_END_BYTES_SIZE;

        // Log.i(TAG, "bitmap start:" + start + ",length:" + length);
        if (start >= size - 1 || length < 0) {
            return null;
        }
        Bitmap map = BitmapFactory.decodeByteArray(bytes, start, length);
        return map;
    }

    /**
     * write four int header, right now we use 0xcafe
     * 
     * @throws IOException
     */
    private static void writeAuthHeader() throws IOException {
        mBitmapBytesStream.write(mHeaderBytes);
    }

    /**
     * write end, right now use random uuid
     * 
     * @throws IOException
     */
    private static void writeAuthEnd() throws IOException {
        mBitmapBytesStream.write(AUTH_END.getBytes());
    }

}
