package com.wegielek.feature.fxRatesConverter.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wegielek.feature.fxRatesConverter.presentation.viewmodel.CurrencyExchangeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesModalSheet(viewModel: CurrencyExchangeViewModel = koinViewModel()) {
    val scope = rememberCoroutineScope()

    val currencies = listOf("PLN", "UAH", "GBP", "EUR")
    val sheetState = rememberModalBottomSheetState()
    val searchField by viewModel.searchField.collectAsState()

    val chooseFromCurrencyModalSheet by viewModel.chooseFromCurrencyModalSheet.collectAsState()

    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val toCurrency by viewModel.toCurrency.collectAsState()

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
                modifier = Modifier.fillMaxWidth().testTag("sendingHeader"),
            )
            OutlinedTextField(
                value = searchField,
                onValueChange = {
                    viewModel.updateSearchField(it)
                },
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
                        if (chooseFromCurrencyModalSheet) it != fromCurrency else it != toCurrency
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
                                    Text(
                                        country.currencyName + " • " + currency,
                                        color = MaterialTheme.colorScheme.tertiary,
                                    )
                                }
                            }
                        }
                        HorizontalDivider(
                            Modifier.padding(horizontal = 16.dp),
                            DividerDefaults.Thickness,
                            MaterialTheme.colorScheme.tertiaryContainer,
                        )
                    }
            }
        }
    }
}
