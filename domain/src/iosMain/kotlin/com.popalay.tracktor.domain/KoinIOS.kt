package com.popalay.tracktor.domain

import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.KoinApplication
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object KoinIOS {
    private lateinit var koin: KoinApplication

    fun initialize() {
        koin = initKoin {}
    }

    fun get(objCClass: ObjCClass, qualifier: Qualifier? = null, parameter: Any? = null): Any? {
        val kClazz = getOriginalKotlinClass(objCClass)!!
        return koin.koin.get(kClazz, qualifier) { parametersOf(parameter) }
    }
}