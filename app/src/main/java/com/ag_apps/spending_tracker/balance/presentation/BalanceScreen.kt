package com.ag_apps.spending_tracker.balance.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ag_apps.spending_tracker.core.presentation.ui.theme.SpendingTrackerTheme
import com.ag_apps.spending_tracker.core.presentation.ui.theme.montserrat
import com.ag_apps.spending_tracker.core.presentation.util.Background
import com.ag_apps.spending_tracker.spending_details.presentation.SpendingDetailsAction
import com.ag_apps.spending_tracker.spending_details.presentation.SpendingDetailsEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun BalanceScreenCore(
    viewModel: BalanceViewModel = koinViewModel(),
    onSaveClick: () -> Unit
) {
    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                BalanceEvent.NavigateBack -> onSaveClick()
            }
        }
    }
    BalanceScreenCoreScreen(

        state = viewModel.state,
        onAction = viewModel::onAction,
        onSaveClick = {
            viewModel.onAction(BalanceAction.OnBalanceSaved)
            onSaveClick()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BalanceScreenCoreScreen(
    state: BalanceState,
    onAction: (BalanceAction) -> Unit,
    onSaveClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(start = 12.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 45.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Update Balance",
                            fontFamily = montserrat,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
                            .clickable {
                                onAction(BalanceAction.goBack)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Background()

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$${state.balance}",
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(38.dp))

            OutlinedTextField(
                value = state.balance.toString(),
                onValueChange = {
                    onAction(
                        BalanceAction.OnBalanceChanged(
                            it.toDoubleOrNull() ?: 0.0
                        )
                    )
                },
                label = {
                    Text(text = "Enter balance")
                },
                textStyle = TextStyle(
                    fontSize = 18.sp
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(38.dp))

            OutlinedButton(
                onClick = { onSaveClick() },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save balance",
                        modifier = Modifier.size(33.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Save Balance",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BalanceScreenCoreScreenPreview() {
    SpendingTrackerTheme {
        BalanceScreenCoreScreen(
            state = BalanceState(),
            onAction = {},
            onSaveClick = {}
        )
    }
}