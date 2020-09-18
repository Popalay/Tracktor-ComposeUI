-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.popalay.data.**$$serializer { *; }
-keepclassmembers class com.popalay.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.popalay.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}