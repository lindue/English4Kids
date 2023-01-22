package com.hisu.imastermatcher.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: String,
    val imagePath: Int,
    var visible: Boolean
): Parcelable