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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import com.wegielek.fx_rates_converter.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

sealed class Country(
    @param:DrawableRes val flagResId: Int,
    val countryName: String,
    val currencyName: String,
) {
    data object England : Country(R.drawable.england_big, "Great Britain", "British Pound")

    data object Poland : Country(R.drawable.poland_big, "Poland", "Złoty")

    data object Ukraine : Country(R.drawable.ukraine_big, "Ukraine", "Hrivna")

    data object Germany : Country(R.drawable.germany_big, "Germany", "Euro")
}

fun countryFromCurrency(currency: String): Country? =
    when (currency) {
        "GBP" -> Country.England
        "PLN" -> Country.Poland
        "UAH" -> Country.Ukraine
        "EUR" -> Country.Germany
        else -> null
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeScreen(
    viewModel: CurrencyExchangeViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()

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

    val chooseFromCurrencyModalSheet by viewModel.chooseFromCurrencyModalSheet.collectAsState()
    val chooseToCurrencyModalSheet by viewModel.chooseToCurrencyModalSheet.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    val searchField by viewModel.searchField.collectAsState()

    val amountFormat: DecimalFormat =
        run {
            val symbols =
                DecimalFormatSymbols().apply {
                    decimalSeparator = '.'
                    groupingSeparator = ' '
                }
            DecimalFormat("#,##0.00", symbols)
        }

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
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text("Receiver gets", color = MaterialTheme.colorScheme.tertiary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val country = countryFromCurrency(toCurrency)
                            country?.let {
                                Image(
                                    painter = painterResource(it.flagResId),
                                    contentDescription = "Flag",
                                )
                            }
                            Spacer(Modifier.padding(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        viewModel.showChooseReceivingCurrencySheet()
                                    },
                            ) {
                                Text(toCurrency, fontWeight = FontWeight.Bold)
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                    }
                    TextField(
                        value = amountFormat.format(toAmount),
//                            toAmount
//                                .setScale(2, RoundingMode.HALF_UP)
//                                .toPlainString(),
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
                            val country = countryFromCurrency(fromCurrency)
                            country?.let {
                                Image(
                                    painter = painterResource(it.flagResId),
                                    contentDescription = "Flag",
                                )
                            }
                            Spacer(Modifier.padding(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier =
                                    Modifier.clickable {
                                        viewModel.showChooseSendingCurrencySheet()
                                    },
                            ) {
                                Text(
                                    fromCurrency,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                    }
                    TextField(
                        value = amountFormat.format(fromAmount),
//                            fromAmount
//                                .setScale(2, RoundingMode.HALF_UP)
//                                .toPlainString(),
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
        Row(
            Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable {},
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Request",
                    modifier =
                        Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable {
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Send",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.background,
                )
            }
        }
        ConnectionErrorPopup(
            modifier =
                Modifier
                    .align(Alignment.TopCenter),
        )
        if (chooseFromCurrencyModalSheet || chooseToCurrencyModalSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.hideChooseFromCurrencySheet()
                    viewModel.hideChooseToCurrencySheet()
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        if (chooseFromCurrencyModalSheet) "Sending from" else "Sending to",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = searchField,
                        onValueChange = { viewModel.updateSearchField(it) },
                        label = {
                            Text(
                                "Search",
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        keyboardOptions =
                            KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search,
                            ),
                    )
                    Text(
                        "All countries",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Column(Modifier.fillMaxWidth()) {
                        val filteredCurrencies =
                            currencies.filter {
                                if (chooseFromCurrencyModalSheet) it != toCurrency else it != fromCurrency
                            }
                        filteredCurrencies
                            .forEach { currency ->
                                val country = countryFromCurrency(currency)
                                val search = searchField.lowercase()
                                if (searchField.isNotEmpty()) {
                                    country?.let {
                                        if (!currency.lowercase().contains(search) &&
                                            !it.countryName.lowercase().contains(search) &&
                                            !it.currencyName.lowercase().contains(search)
                                        ) {
                                            return@forEach
                                        }
                                    }
                                }
                                Row(
                                    Modifier.fillMaxWidth().clickable {
                                        if (chooseFromCurrencyModalSheet) {
                                            viewModel.onFromCurrencySelected(currency)
                                            scope.launch(Dispatchers.Main) {
                                                sheetState.hide()
                                                viewModel.hideChooseFromCurrencySheet()
                                            }
                                        } else {
                                            viewModel.onToCurrencySelected(currency)
                                            scope.launch(Dispatchers.Main) {
                                                sheetState.hide()
                                                viewModel.hideChooseToCurrencySheet()
                                            }
                                        }
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    country?.let {
                                        Box(
                                            Modifier
                                                .padding(vertical = 8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.tertiaryContainer,
                                                ).padding(8.dp),
                                        ) {
                                            Image(
                                                painter = painterResource(it.flagResId),
                                                contentDescription = "Flag",
                                            )
                                        }
                                        Spacer(Modifier.padding(8.dp))
                                        Column {
                                            Text(
                                                it.countryName,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                            Text(country.currencyName + " " + currency)
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }
}
