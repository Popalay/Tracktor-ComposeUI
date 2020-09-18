package com.popalay.tracktor.domain.utils

fun <T : Any> List<T>.updateItem(item: T, newItem: T): List<T> =
    toMutableList().apply {
        set(indexOf(item), newItem)
    }.toList()