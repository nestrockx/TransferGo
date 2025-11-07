package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CurrencyExchangeScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(16.dp),
                ).padding(16.dp),
        ) {
            Column(Modifier.align(Alignment.CenterStart), verticalArrangement = Arrangement.Center) {
                Text("Sending from")
                Row {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Flag",
                    )
                    Text("PLN")
                }
            }
            Text(
                "100.00",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}
