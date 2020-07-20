package com.popalay.tracktor.utils

import com.squareup.moshi.Moshi
import com.squareup.workflow.Snapshot
import okio.ByteString.Companion.toByteString

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> T.toSnapshot(moshi: Moshi): Snapshot = Snapshot.of {
    moshi.adapter(T::class.java).toJson(this).encodeToByteArray().toByteString()
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> Snapshot.toData(moshi: Moshi): T? = moshi.adapter(T::class.java).fromJson(bytes.toByteArray().decodeToString())