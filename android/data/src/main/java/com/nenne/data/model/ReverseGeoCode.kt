package com.nenne.data.model

import kotlinx.serialization.Serializable


@Serializable
data class ReverseGeoCode(
    val results: List<Result>,
    val status : Status
)
@Serializable
data class Status(
    val code: Int,
    val message: String,
    val name: String
)

@Serializable
data class Result(
    val code: Code,
    val name: String,
    val region: Region
)

@Serializable
data class Code(
    val id: String,
    val mappingId: String,
    val type: String
)
@Serializable
data class Region(
    val area0: Area,
    val area1: Area,
    val area2: Area,
    val area3: Area,
    val area4: Area
)

@Serializable
data class Area(
    val name: String,
    val alias : String = "None"
)