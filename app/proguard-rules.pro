# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# ========================================
# Prody Application Classes
# ========================================

# Keep all Prody app classes
-keep class com.prody.prashant.** { *; }

# Keep data models
-keep class com.prody.prashant.data.model.** { *; }
-keep class com.prody.prashant.domain.model.** { *; }
-keep class com.prody.prashant.domain.identity.** { *; }
-keep class com.prody.prashant.data.local.entity.** { *; }

# Keep data classes members
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# ========================================
# Kotlin
# ========================================

-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# ========================================
# Room Database
# ========================================

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.paging.**

# Keep Room generated code
-keep class * extends androidx.room.RoomDatabase$Callback

# ========================================
# Kotlin Serialization
# ========================================

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.prody.prashant.**$$serializer { *; }
-keepclassmembers class com.prody.prashant.** {
    *** Companion;
}
-keepclasseswithmembers class com.prody.prashant.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ========================================
# Hilt Dependency Injection
# ========================================

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-dontwarn dagger.hilt.internal.aggregatedroot.codegen.**
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}

# ========================================
# Jetpack Compose
# ========================================

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ========================================
# Coroutines
# ========================================

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ========================================
# Retrofit (if used)
# ========================================

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# ========================================
# DataStore
# ========================================

-keep class androidx.datastore.** { *; }

# ========================================
# WorkManager
# ========================================

-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# ========================================
# Parcelable
# ========================================

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ========================================
# Enums
# ========================================

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ========================================
# Generative AI (Gemini)
# ========================================

-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# ========================================
# Glance App Widgets
# ========================================

-keep class androidx.glance.** { *; }
-dontwarn androidx.glance.**
