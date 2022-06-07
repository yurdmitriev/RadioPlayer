package com.yurdm.radioplayer.model

data class Category(
    val id: Int,
    val title: String,
    val items: List<Radio>
)