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
# GENERAL ATTRIBUTES (REQUIRED)
####################################
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

####################################
# GSON / RETROFIT (API MODELS)
####################################
# Keep fields for JSON parsing
-keep class com.policyboss.policybosspro.core.model.** { <fields>; }
-keep class com.policyboss.policybosspro.core.response.** { <fields>; }

# Prevent Gson warnings
-dontwarn sun.misc.**

####################################
# RAZORPAY SDK (MANDATORY)
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
# KOTLIN SAFETY (RECOMMENDED)
####################################
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }

####################################
# REMOVE LOGS (OPTIONAL BUT GOOD)
####################################
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
