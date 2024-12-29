package com.ag_apps.spending_tracker.spending_overview.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ag_apps.spending_tracker.core.domain.Spending
import com.ag_apps.spending_tracker.core.presentation.ui.theme.SpendingTrackerTheme
import com.ag_apps.spending_tracker.core.presentation.ui.theme.montserrat
import com.ag_apps.spending_tracker.core.presentation.util.Background
import com.ag_apps.spending_tracker.core.presentation.util.TopBarBackground
import com.ag_apps.spending_tracker.spending_overview.presentation.util.formatDate
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Composable

fun SpendingOverviewScreenCore(
    viewModel: SpendingOverviewViewModel = koinViewModel(),
    onBalanceClick: () -> Unit,
    onAddSpendingClick: () -> Unit,
) {

    LaunchedEffect(true) {
        viewModel.onAction(
            SpendingOverviewAction.LoadSpendingOverviewAndBalance
        )
    }

    SpendingOverviewScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
        onBalanceClick = onBalanceClick,
        onAddSpendingClick = onAddSpendingClick,
        onDeleteSpendingClick = {
            viewModel.onAction(SpendingOverviewAction.OnDeleteSpending(it))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpendingOverviewScreen(
    state: SpendingOverviewState,
    onAction: (SpendingOverviewAction) -> Unit,
    onBalanceClick: () -> Unit,
    onAddSpendingClick: () -> Unit,
    onDeleteSpendingClick: (Int) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )

    Scaffold(
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { onAddSpendingClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Spending"
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2E5DA8).copy(alpha = 0.35f))
            ) {
                SpendingOverviewTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    scrollBehavior = scrollBehavior,
                    balance = state.balance,
                    onBalanceClick = onBalanceClick
                )

                Spacer(modifier = Modifier.height(8.dp))

            }
        },


    ) { paddingValues ->
        Background()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                SpendingCalendarGrid(
                    spendingList = state.spendingList,
                    month = state.month,
                    year = state.year,
                    monthlySpendingList = state.monthlySpendingList,
                    onAction = onAction
                )
            }
            SpendingList(
                spendingList = state.spendingList,
                onDeleteSpending = onDeleteSpendingClick
            )

        }


    }

}
@Composable
fun SpendingCalendarGrid(
    modifier: Modifier = Modifier,
    spendingList: List<Spending>,
    monthlySpendingList : List<Double>,
    month: Int,
    year: Int,
    onAction: (SpendingOverviewAction) -> Unit
) {


    val totalPrice = monthlySpendingList.sumOf { it }

    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7) // Adjust for Sunday -> Saturday
    val totalCells = firstDayOfWeek + daysInMonth
    val columns = 7 // Days in a week (Sunday -> Saturday)


        // Days of week header row

    Column(modifier = Modifier.fillMaxWidth()) {
        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onAction(SpendingOverviewAction.OnMonthChange(month - 1)) },
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(0.6f),
                        shape = RoundedCornerShape(13.dp)
                    )


            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Month",
                )
            }
            Text(
                text = LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontSize = 25.sp,
                maxLines = 1,
                fontFamily = montserrat,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            )
            IconButton(
                onClick = { onAction(SpendingOverviewAction.OnMonthChange(month + 1)) },
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(0.6f),
                        shape = RoundedCornerShape(13.dp)
                    )

                ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Month"
                )
            }
        }
        // Calendar grid row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)) {
            for (dayIndex in 0 until columns) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val dayName = when (dayIndex) {
                        0 -> "Sun"
                        1 -> "Mon"
                        2 -> "Tue"
                        3 -> "Wed"
                        4 -> "Thu"
                        5 -> "Fri"
                        else -> "Sat"
                    }

                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Generate dates for this column
                        for (weekIndex in 0 until ceil(totalCells / 7.0).toInt()) {
                            val cellIndex = weekIndex * 7 + dayIndex
                            val date =
                                if (cellIndex >= firstDayOfWeek && cellIndex < firstDayOfWeek + daysInMonth) {
                                    firstDayOfMonth.plusDays((cellIndex - firstDayOfWeek).toLong())
                                } else null

                            if (date != null) {
                                val dayOfMonthIndex = date.dayOfMonth - 1
                                println(monthlySpendingList)
                                CalendarDay(
                                    date = date,
                                    spending = if (dayOfMonthIndex in monthlySpendingList.indices) {
                                        monthlySpendingList[dayOfMonthIndex]
                                    } else {
                                        0.0 // Default value when the list is empty or index is out of bounds
                                    },
                                    totalSpending = totalPrice,
                                    onAction = onAction
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun CalendarDay(
    date: LocalDate,
    spending: Double,
    totalSpending: Double,
    onAction: (SpendingOverviewAction) -> Unit,
) {
    println("Clicked date: $totalSpending $spending")
    val colorIntensity = if (totalSpending > 0) (spending/ totalSpending).coerceIn(0.0, 1.0) else 0.0
    println("Clicked date: $colorIntensity")
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (spending == 0.0) {
                    androidx.compose.ui.graphics.Color(0xFFB0B0B0) // Example gray color

                } else {
                    androidx.compose.ui.graphics.Color(
                        red = 0f,
                        green = (1 - colorIntensity.toFloat()).coerceIn(0.5f, 1f),
                        blue = 0f,
                        alpha = 1f
                    )
                }
            )
            .clickable(onClick = {
                onAction(SpendingOverviewAction.OnDateChange(date.atStartOfDay(ZoneId.systemDefault())))
            })
    )
}




@Composable
fun SpendingList(
    modifier: Modifier = Modifier,
    spendingList: List<Spending>,
    onDeleteSpending: (Int) -> Unit,
) {
    if (spendingList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Spending",
                fontSize = 15.sp,
                maxLines = 1,
                fontFamily = montserrat,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 20.dp, bottom = 80.dp
            )
        ) {
            itemsIndexed(spendingList) { index, spending ->
                SpendingItem(
                    spending = spending,
                    onDeleteSpending = { onDeleteSpending(spending.spendingId ?: -1) },
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpendingItem(
    modifier: Modifier = Modifier,
    spending: Spending,
    onDeleteSpending: () -> Unit
) {
    var isDeleteShowing by rememberSaveable {
        mutableStateOf(false)
    }

    Box {
        ElevatedCard(
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp
            ),
            modifier = modifier
                .height(150.dp)
                .padding(horizontal = 16.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        isDeleteShowing = !isDeleteShowing
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color(spending.color))
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = spending.name,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(1.dp))

                SpendingInfo(
                    name = "Price",
                    value = "$${spending.price}"
                )
                SpendingInfo(
                    name = "Kilograms",
                    value = "${spending.kilograms}"
                )
                SpendingInfo(
                    name = "Quantity",
                    value = "${spending.quantity}"
                )
            }
        }

        DropdownMenu(
            expanded = isDeleteShowing,
            onDismissRequest = { isDeleteShowing = false },
            offset = DpOffset(30.dp, 0.dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete Spending",
                        fontFamily = montserrat
                    )
                },
                onClick = {
                    isDeleteShowing = false
                    onDeleteSpending()
                }
            )
        }
    }
}

@Composable
fun SpendingInfo(
    name: String,
    value: String
) {
    Row {
        Text(
            text = "$name:  ",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(0.8f)
        )

        Text(
            text = value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingOverviewTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    balance: Double,
    onBalanceClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        scrollBehavior = scrollBehavior,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Overview",
                    fontSize = 25.sp,
                    maxLines = 1,
                    fontFamily = montserrat,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
            }
        },
        actions = {
            IconButton(
                onClick = {},

                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(0.6f),
                        shape = RoundedCornerShape(13.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle, // Replace with your desired profile icon
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = modifier.padding(end = 16.dp, start = 12.dp)
    )
}

@Composable
fun DatePickerDropDownMenu(
    modifier: Modifier = Modifier,
    state: SpendingOverviewState,
    onItemClick: (Int) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(
                elevation = 0.5.dp,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            offset = DpOffset(10.dp, 0.dp),
            modifier = Modifier.heightIn(max = 440.dp)
        ) {

            state.dateList.forEachIndexed { index, zonedDateTime ->
                if (index == 0) {
                    HorizontalDivider()
                }

                Text(
                    text = zonedDateTime.formatDate(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Normal,
                    fontFamily = montserrat,

                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            isExpanded = false
                            onItemClick(index)
                        }

                )

                HorizontalDivider()
            }

        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { isExpanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Text(
                text = state.pickedDate.formatDate(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = "Pick a date"
            )

        }
    }

}



@Preview
@Composable
private fun SpendingOverviewScreenPreview() {
    SpendingTrackerTheme {
        SpendingOverviewScreen(state = SpendingOverviewState(),
            onAction = {},
            onBalanceClick = {},
            onAddSpendingClick = {},
            onDeleteSpendingClick = {})
    }
}


