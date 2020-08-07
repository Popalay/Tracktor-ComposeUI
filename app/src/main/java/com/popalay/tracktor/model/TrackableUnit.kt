package com.popalay.tracktor.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackableUnit(
    val name: String,
    val symbol: String,
    val valueType: UnitValueType
) {
    val displayName: String get() = listOfNotNull(name, symbol.ifBlank { null }).joinToString()

    companion object {
        val None = TrackableUnit(name = "", symbol = "", UnitValueType.NONE)
        val Quantity = TrackableUnit(name = "Quantity", symbol = "", UnitValueType.INTEGER)
        val Time = TrackableUnit(name = "Time", symbol = "s", UnitValueType.DOUBLE)
        val Weight = TrackableUnit(name = "Weight", symbol = "kg", UnitValueType.DOUBLE)
        val Length = TrackableUnit(name = "Length", symbol = "m", UnitValueType.DOUBLE)
        val Temperature = TrackableUnit(name = "Temperature", symbol = "°C", UnitValueType.DOUBLE)
        val Speed = TrackableUnit(name = "Speed", symbol = "km/h", UnitValueType.DOUBLE)
        val Energy = TrackableUnit(name = "Energy", symbol = "kcal", UnitValueType.DOUBLE)
        val Word = TrackableUnit(name = "Word", symbol = "", UnitValueType.TEXT)
    }
}

enum class UnitValueType(val displayName: String) {
    NONE(""),
    TEXT("Text"),
    DOUBLE("Double"),
    INTEGER("Integer")
}

enum class ProgressDirection(val displayName: String) {
    ASCENDING("Ascending"),
    DESCENDING("Descending")
}