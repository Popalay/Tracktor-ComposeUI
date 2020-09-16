-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.popalay.domain.**$$serializer { *; }
-keepclassmembers class com.popalay.domain.** {
    *** Companion;
}
-keepclasseswithmembers class com.popalay.domain.** {
    kotlinx.serialization.KSerializer serializer(...);
}