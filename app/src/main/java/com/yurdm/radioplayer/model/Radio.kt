package com.yurdm.radioplayer.model

import java.io.Serializable

data class Radio (
    val id: Int,
    val title: String,
    val logo: String,
    val stream: String,
) : Serializable {
    companion object {
    }
}