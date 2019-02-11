# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/user/tools/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembernames class android.support.v7.widget.** { *; }

-keep class com.ontbee.legacyforks.cn.pedant.SweetAlert.** {*;}
 -keepclassmembers class com.ontbee.legacyforks.cn.pedant.SweetAlert.** {*;}
 -dontwarn org.apache.commons.**
 -keep class org.apache.http.** { *; }
 -dontwarn org.apache.http.**
 -dontwarn okio.**
 -dontwarn com.mapbox.mapboxsdk.plugins.locationlayer.**
 -dontwarn okhttp3.internal.platform.ConscryptPlatform.**
 # Retrofit 2
 # Platform calls Class.forName on types which do not exist on Android to determine platform.
 -dontnote retrofit2.Platform
 # Platform used when running on RoboVM on iOS. Will not be used at runtime.
 -dontnote retrofit2.Platform$IOS$MainThreadExecutor
 # Platform used when running on Java 8 VMs. Will not be used at runtime.
 -dontwarn retrofit2.Platform$Java8
 # Retain generic type information for use by reflection by converters and adapters.
 -keepattributes Signature
 # Retain declared checked exceptions for use by a Proxy instance.
 -keepattributes Exceptions

 # For using GSON @Expose annotation
 -keepattributes *Annotation*
 # Gson specific classes
 -dontwarn sun.misc.**

 -dontwarn okio.**
 -dontwarn okhttp3.**
 -keep class retrofit.**
 -keep class retrofit.** { *; }
 -keepclasseswithmembers class * {
     @retrofit.http.* <methods>;
 }
 -dontwarn retrofit.**

 # Picasso
 -dontwarn com.squareup.okhttp.**

 -dontwarn android.support.**
 -dontwarn java.lang.**
 -dontwarn org.codehaus.**
 -dontwarn com.google.**
 -dontwarn java.nio.**
 -dontwarn javax.annotation.**
 #//////////////////////// cardslider////////////
 -keep public class * extends com.ramotion.cardslider
 -keep class ViewUpdaterir.appsoar.teknesian.cards.CardsUpdater

 #/////////////maps.ir

 -keep class ir.map.sdk_map
 -keep class ir.appsoar.teknesian.Fonts

 #////// Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}