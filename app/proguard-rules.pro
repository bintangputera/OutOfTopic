# ===== General =====
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ===== Kotlin =====
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# ===== Kotlin Coroutines =====
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ===== Kotlin Serialization =====
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.poetralabs.outoftopic.**$$serializer { *; }
-keepclassmembers class com.poetralabs.outoftopic.** {
    *** Companion;
}
-keepclasseswithmembers class com.poetralabs.outoftopic.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# Keep @Serializable annotated classes
-keep @kotlinx.serialization.Serializable class * { *; }

# ===== Room =====
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.**

# ===== Ktor =====
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn io.ktor.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ===== Koin =====
-keep class org.koin.** { *; }
-keepnames class * extends org.koin.core.module.Module
-dontwarn org.koin.**

# ===== Firebase =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ===== App models (Room entities, remote responses, nav routes) =====
-keep class com.poetralabs.outoftopic.core.data.local.entity.** { *; }
-keep class com.poetralabs.outoftopic.core.data.remote.response.** { *; }
-keep class com.poetralabs.outoftopic.core.navigation.** { *; }

# ===== ViewModels =====
-keep class * extends androidx.lifecycle.ViewModel { *; }
