-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.popalay.datashared.**$$serializer { *; }
-keepclassmembers class com.popalay.datashared.** {
    *** Companion;
}
-keepclasseswithmembers class com.popalay.datashared.** {
    kotlinx.serialization.KSerializer serializer(...);
}