# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Uncomment this to preserve the line number information for
# debugging stack traces.
# If you keep the line number information, uncomment this to
# hide the original source file name.


# Generic android
-dontwarn android.test.**
-dontwarn org.junit.**
-dontwarn android.support.**
-dontpreverify # preverification not needed for dex compiler/Dalvik VM


# Obfuscation level settings
-repackageclasses '' # aggressive obfuscation
-allowaccessmodification # aggressive obfuscation
-optimizations !code/simplification/arithmetic #Dalvik 1.0 and 1.5 can not handle this simplification

-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,SourceFile,LineNumberTable,*Annotation*

# Retain all android errorLogData attributes
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep native calls
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep enumeration classes
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# Handle Auto-Generate R classes and other resource files
-keepclassmembers class **.R$* {
    public static <fields>;
}

-adaptresourcefilenames    **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

-keep class android.support.v4.view.** { *; }

-keep class com.jht.serialport.** { *; }
-keep class com.jht.serialcommunication.** { *; }
-keep class com.jht.peripheral.** { *; }


-keep class mf.org.apache.** { *; }
-keep class mf.javax.xml.** { *; }
-keep class javax.xml.** { *; }
-keep class org.videolan.libvlc.** { *; }
