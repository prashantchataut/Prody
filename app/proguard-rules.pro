# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep data classes
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin Serialization
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

# Hilt
-dontwarn dagger.hilt.internal.aggregatedroot.codegen.**
-keepclasseswithmembers class * {
    @dagger.hilt.* <methods>;
}

# Compose
-dontwarn androidx.compose.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Keep data models
-keep class com.prody.prashant.data.model.** { *; }
-keep class com.prody.prashant.domain.model.** { *; }
-keep class com.prody.prashant.data.local.entity.** { *; }
