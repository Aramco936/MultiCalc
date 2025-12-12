package com.example.multicalc

data class BiseccionRow(
    val i: Int,
    val Xa: Double,
    val Xb: Double,
    val fXa: Double,
    val fXb: Double,
    val m: Double,
    val f_m: Double,
    val error: Double
)