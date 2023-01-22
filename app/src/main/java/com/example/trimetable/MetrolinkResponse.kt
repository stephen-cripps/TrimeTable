package com.example.trimetable

data class MetrolinkResponse(
    val value: List<MetrolinkResponseValue>
)

data class MetrolinkResponseValue(
    val AtcoCode: String,
    val Carriages0: String,
    val Carriages1: String,
    val Carriages2: String,
    val Carriages3: String,
    val Dest0: String,
    val Dest1: String,
    val Dest2: String,
    val Dest3: String,
    val Direction: String,
    val Id: Int,
    val LastUpdated: String,
    val Line: String,
    val MessageBoard: String,
    val PIDREF: String,
    val StationLocation: String,
    val Status0: String,
    val Status1: String,
    val Status2: String,
    val Status3: String,
    val TLAREF: String,
    val Wait0: String,
    val Wait1: String,
    val Wait2: String,
    val Wait3: String
)