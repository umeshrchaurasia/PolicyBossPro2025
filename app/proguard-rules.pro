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


####################################
# GENERAL ATTRIBUTES
####################################
-keepattributes Signature,*Annotation*,EnclosingMethod,InnerClasses

####################################
# GSON / RETROFIT
####################################
-keep class com.policyboss.policybosspro.core.model.** { *; }
-keep class com.policyboss.policybosspro.core.response.** { *; }

-dontwarn sun.misc.**

####################################
# RAZORPAY SDK
####################################
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**

####################################
# WEBVIEW JAVASCRIPT INTERFACE
####################################
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

####################################
# KOTLIN
####################################
-keep class kotlin.Metadata { *; }

####################################
# REMOVE LOGS
####################################
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

####################################
# TARGETED R8 WARNING BYPASS (EXACT MATCH)
####################################
-dontwarn com.google.protobuf.java_com_google_android_gmscore_sdk_target_granule__proguard_group_gtm_N1281923064GeneratedExtensionRegistryLite$Loader