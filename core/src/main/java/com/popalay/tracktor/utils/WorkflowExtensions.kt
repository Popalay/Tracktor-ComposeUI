package com.popalay.tracktor.utils

import com.squareup.workflow.Snapshot
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> T.toSnapshot(): Snapshot = Snapshot.EMPTY //TODO: revert when move to domain Snapshot.of(Json.encodeToString(this))

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> Snapshot.toData(): T? = Json.decodeFromString(bytes.utf8())