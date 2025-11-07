package com.wegielek.feature.fxRatesConverter.utils

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

object ScreenUtils {
    fun dpToPx(
        density: Density,
        dp: Dp,
    ): Float = with(density) { dp.toPx() }

    fun pxToDp(
        density: Density,
        px: Float,
    ): Dp = with(density) { px.toDp() }
}
