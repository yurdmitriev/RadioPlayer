package com.yurdm.radioplayer.model

data class Radio (
    val id: Int,
    val title: String,
    val logo: String,
    val stream: String,
) {
    companion object {
    }
}