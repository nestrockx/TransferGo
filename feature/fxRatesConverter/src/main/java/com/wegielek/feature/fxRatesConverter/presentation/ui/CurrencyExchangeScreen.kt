package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import com.wegielek.fx_rates_converter.R
import org.koin.androidx.compose.koinViewModel

sealed class CountryFlag(
    @param:DrawableRes val resId: Int,
) {
    data object England : CountryFlag(R.drawable.england_big)

    data object Poland : CountryFlag(R.drawable.poland_big)

    data object Ukraine : CountryFlag(R.drawable.ukraine_big)

    data object Germany : CountryFlag(R.drawable.germany_big)
}

fun flagFromString(currency: String): CountryFlag? =
    when (currency) {
        "GBP" -> CountryFlag.England
        "PLN" -> CountryFlag.Poland
        "UAH" -> CountryFlag.Ukraine
        "EUR" -> CountryFlag.Germany
        else -> null
    }

@Composable
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val exchange by viewModel.exchangeResult.collectAsState()

    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val toCurrency by viewModel.toCurrency.collectAsState()
    val fromAmount by viewModel.fromAmount.collectAsState()
    val toAmount by viewModel.toAmount.collectAsState()

    val fromAmountExceeded by viewModel.fromAmountExceeded.collectAsState()

    var swapCurrencyRotated by remember { mutableStateOf(false) }
    val swapCurrencyRotation by animateFloatAsState(
        targetValue = if (swapCurrencyRotated) 180f else 0f,
        label = "swap rotation",
    )

    val currencies = listOf("PLN", "UAH", "GBP", "EUR")
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(fromCurrency, toCurrency) {
        viewModel.getExchangeRate()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.padding(8.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Box(
                Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text("Receiver gets", color = MaterialTheme.colorScheme.tertiary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val flag = flagFromString(toCurrency)
                            flag?.let {
                                Image(
                                    painter = painterResource(it.resId),
                                    contentDescription = "Flag",
                                )
                            }
                            Spacer(Modifier.padding(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        toExpanded = true
                                    },
                            ) {
                                Text(toCurrency, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                )
                            }
                            DropdownMenu(
                                expanded = toExpanded,
                                onDismissRequest = { toExpanded = false },
                                containerColor = MaterialTheme.colorScheme.background,
                            ) {
                                currencies.filter { it != fromCurrency }.forEach { currency ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                currency,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        },
                                        onClick = {
                                            viewModel.onToCurrencySelected(currency)
                                            toExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                    TextField(
                        value = "%.2f".format(toAmount),
                        onValueChange = {
                            viewModel.updateToAmount(it)
                        },
                        singleLine = true,
                        textStyle =
                            LocalTextStyle.current.copy(
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                            ),
                        colors =
                            TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                            ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.TopCenter)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            clip = false,
                        ).border(
                            width = 2.dp,
                            color =
                                if (!fromAmountExceeded) {
                                    Color.Transparent
                                } else {
                                    MaterialTheme.colorScheme.errorContainer
                                },
                            shape = RoundedCornerShape(16.dp),
                        ).clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text("Sending from", color = MaterialTheme.colorScheme.tertiary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val flag = flagFromString(fromCurrency)
                            flag?.let {
                                Image(
                                    painter = painterResource(it.resId),
                                    contentDescription = "Flag",
                                )
                            }
                            Spacer(Modifier.padding(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        fromExpanded = true
                                    },
                            ) {
                                Text(
                                    fromCurrency,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                )
                            }
                            DropdownMenu(
                                expanded = fromExpanded,
                                onDismissRequest = { fromExpanded = false },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            ) {
                                currencies.filter { it != toCurrency }.forEach { currency ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                currency,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        },
                                        onClick = {
                                            viewModel.onFromCurrencySelected(currency)
                                            fromExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                    TextField(
                        value = "%.2f".format(fromAmount),
                        onValueChange = {
                            viewModel.updateFromAmount(it)
                        },
                        singleLine = true,
                        textStyle =
                            LocalTextStyle.current.copy(
                                fontSize = 32.sp,
                                color =
                                    if (!fromAmountExceeded) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    },
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                            ),
                        colors =
                            TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                            ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(onClick = {
                        swapCurrencyRotated = !swapCurrencyRotated
                        viewModel.swapCurrency()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.reverse),
                            contentDescription = "Swap",
                            tint = MaterialTheme.colorScheme.background,
                            modifier =
                                Modifier
                                    .graphicsLayer(rotationZ = swapCurrencyRotation)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(8.dp),
                        )
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        exchange?.let {
                            Text(
                                text = "1 $fromCurrency = %.2f $toCurrency".format(it.rate),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )
                        }
                    }
                    Spacer(Modifier.padding(24.dp))
                }
            }
            if (fromAmountExceeded) {
                Spacer(Modifier.padding(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
                        .padding(8.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.errorContainer,
                        )
                        Spacer(Modifier.padding(4.dp))
                        Text(
                            "Maximum sending amount: 20000 $fromCurrency",
                            color = MaterialTheme.colorScheme.errorContainer,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
        ConnectionErrorPopup(
            modifier =
                Modifier
                    .align(Alignment.TopCenter),
        )
    }
}
