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

-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.arthenica.mobileffmpeg.Config {
    native <methods>;
    void log(int, byte[]);
    void statistics(int, float, float, long , int, double, double);
}

-keep class com.arthenica.mobileffmpeg.AbiDetect {
    native <methods>;
}

-keepattributes *Annotation*
-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}
-optimizations !method/inlining/
-keepclasseswithmembers class * {
  public void onPayment*(...);
}
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule  For Dexguard Only

-dontwarn com.google.errorprone.annotations.** # Resolving Dagger 2 Proguard worning

##### Persisting cookies #########
#-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.** { *; }

#######  Remoiving acko classes from obsfocusion #########
-keep class com.acko.android.** { *; }

-keep class com.google.android.gms.ads.** { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class com.android.installreferrer.** { *; }


#-keep public class com.android.installreferrer.** { *; }

# add if wrapper warning
#-keep class com.appsflyer.** { *; }


-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}
-keep class com.google.protobuf.** { *; }
# Keep all generated protobuf classes and fields
-keep class  com.gamerboard.live.** { *; }

# Keep all enum values of protobuf classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep protobuf message classes used as Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep all generated protobuf classes and fields
-keep class com.gamerboard.live.** { *; }

# Keep all enum values of protobuf classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep protobuf message classes used as Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep protobuf generated metadata, such as field numbers
-keep class com.google.protobuf.** { *; }

# If you're using reflection with protobuf (e.g., parsing JSON with JSONPB), keep those classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.protobuf.** { *; }
-keep class com.fasterxml.jackson.databind.ObjectMapper { *; }
-keep class com.fasterxml.jackson.databind.** { *; }
-keep class com.google.firebase.perf.v1.** { *; }
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

# If you're using Android's Parcelable with protobuf, keep those implementations
-keep class * implements com.google.protobuf.MessageLite
-keep class * implements com.google.protobuf.GeneratedMessageLite
-keep class * implements com.google.protobuf.GeneratedMessageLite$Builder
-keep class * extends com.google.protobuf.GeneratedMessageLite$Builder


-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class
    com.google.android.gms.**,
    com.google.ads.**

-keepclassmembernames class
    com.google.android.gms.**,
    com.google.api.**,
    com.google.ads.** {
    @com.google.android.gms.common.annotation.KeepName *;
}

# Called by introspection
-keep class
    com.google.android.gms.**,
    com.google.ads.**
    extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}


# This keeps the class name as well as the creator field, because the
# "safe parcelable" can require them during unmarshalling.
-keepnames class
    com.google.android.gms.**,
    com.google.ads.**
    implements android.os.Parcelable {
    public static final ** CREATOR;
}

# com.google.android.gms.auth.api.signin.SignInApiOptions$Builder
# references these classes but no implementation is provided.
-dontnote com.facebook.Session
-dontnote com.facebook.FacebookSdk

# android.app.Notification.setLatestEventInfo() was removed in
# Marsmallow, but is still referenced (safely)
-dontwarn com.google.android.gms.common.GooglePlayServicesUtil
-dontwarn android.security.NetworkSecurityPolicy

-dontwarn com.github.siyamed.shapeimageview.path.parser.SvgToPath
-dontwarn com.google.android.gms.internal.zzhu
## okhttp progaurd rules
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**



#for crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
#

#for event bus
-keepattributes *Annotation*



-dontwarn com.google.android.gms.location.**
-dontwarn com.google.android.gms.gcm.**
-dontwarn com.google.android.gms.iid.**

-keep class com.google.android.gms.gcm.** { *; }
-keep class com.google.android.gms.iid.** { *; }
-keep class com.google.android.gms.location.** { *; }
-keep class com.acko.android.health.android.** { *; }
-keep class com.android.installreferrer.** { *; }

-keep class com.delight.**  { *; }
## for rx java
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}



# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Platform calls Class.forN
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

-keep class * extends android.webkit.WebChromeClient { *; }
-dontwarn im.delight.android.webview.**

-keep class android.support.v7.widget.** { *; }





-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class com.gamerboard.live.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.gamerboard.live.** { # <-- change package name to your app's
    *** Companion;
}
-keep,includedescriptorclasses class com.gamerboard.logger.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.gamerboard.logger.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.gamerboard.live.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class com.gamerboard.logger.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}

-keep public class com.gamerboard.live.gamestatemachine.stateMachine.State$* extends com.gamerboard.live.gamestatemachine.stateMachine.State
-keep public class com.gamerboard.live.gamestatemachine.stateMachine.VisionState$* extends com.gamerboard.live.gamestatemachine.stateMachine.VisionState

-keepclassmembers enum com.gamerboard.logger.LogCategory {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers enum com.gamerboard.logger.PlatformType {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class com.google.firebase.perf.v1.TraceMetric {
    boolean isAuto_;
}
-keepclassmembers class com.google.firebase.perf.v1.TraceMetric {
    long clientStartTimeUs_;
    com.google.protobuf.MapFieldLite counters_;
}
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }

-keep class com.google.api.** {
    *;
}
-keep class com.google.auth.oauth2.** {
    *;
}

-keepclassmembers class com.google.auth.oauth2.* {
   *;
}