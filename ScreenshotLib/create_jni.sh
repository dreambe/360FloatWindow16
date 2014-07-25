export ANDROID_PLATFOR=/home/frodo/dev/sdk/android-sdk-linux/platforms/android-8
cd bin/classes/
#javah -jni -classpath .;$ANDROID_PLATFOR/android_1 pkg.screenshot.Screenshot
javah -jni pkg.screenshot.Screenshot
mv pkg_screenshot_Screenshot.h ../../jni/screenshot.h
cd ../..

#javap -s -p pkg.screenshot.Screenshot
#create the java class sign
