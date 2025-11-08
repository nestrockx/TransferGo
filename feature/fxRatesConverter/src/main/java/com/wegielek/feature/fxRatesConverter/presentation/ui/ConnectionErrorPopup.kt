package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.ConnectionErrorPopupViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConnectionErrorPopup(
    modifier: Modifier,
    viewModel: ConnectionErrorPopupViewModel = koinViewModel(),
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val showInternetConnectionError by viewModel.showInternetConnectionError.collectAsState()

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            viewModel.showInternetConnectionError()
        } else {
            viewModel.hideInternetConnectionError()
        }
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = showInternetConnectionError,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false,
                    ).clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background),
        ) {
            Row(Modifier.weight(1f).padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Internet Connection Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(Modifier.padding(8.dp))
                Column {
                    Text(
                        "No network",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        "Check your internet connection",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            IconButton(onClick = {
                viewModel.hideInternetConnectionError()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
