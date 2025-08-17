package com.example.mypiggybank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mypiggybank.ui.theme.MyPiggyBankTheme
import com.example.mypiggybank.ui.components.ProgressRing
import com.example.mypiggybank.viewmodel.SavingsViewModel
import com.example.mypiggybank.viewmodel.SavingsViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPiggyBankTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val app = LocalContext.current.applicationContext as MyPiggyBankApp
                    val vm: SavingsViewModel = viewModel(factory = SavingsViewModelFactory(app.repository))
                    MainScreen(vm)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: SavingsViewModel) {
    val ui by vm.uiState.collectAsState()
    val progressAnim by animateFloatAsState(targetValue = ui.progress.coerceIn(0f, 1f), label = "progress")

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(ui.goal == 0L) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        topBar = {
            TopAppBar(title = { Text("Моя Копилка") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(6.dp)) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Цель", style = MaterialTheme.typography.labelLarge)
                        Text(text = money(ui.goal), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        ProgressRing(progress = progressAnim)
                        Spacer(Modifier.height(12.dp))
                        Text(text = "Накоплено: ${money(ui.current)}")
                        Text(text = "Осталось: ${money(ui.remaining)}")
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            listOf(100L, 200L, 1000L).forEach { amt ->
                                AssistChip(onClick = { vm.addDeposit(amt) }, label = { Text(money(amt)) })
                            }
                            OutlinedButton(onClick = { showGoalDialog = true }) {
                                Text("Изм. цель")
                            }
                        }
                    }
                }
            }
            item {
                Text("История операций", style = MaterialTheme.typography.titleMedium)
            }
            items(ui.history) { dep ->
                ListItem(
                    headlineContent = { Text(money(dep.amount)) },
                    supportingContent = {
                        val df = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
                        Text(df.format(Date(dep.timestamp)))
                    }
                )
                HorizontalDivider() // Заменили Divider() на HorizontalDivider()
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        AmountDialog(
            title = "Пополнить копилку",
            onDismiss = { showAddDialog = false },
            onConfirm = { amount -> vm.addDeposit(amount); showAddDialog = false }
        )
    }

    if (showGoalDialog) {
        AmountDialog(
            title = "Установить цель",
            onDismiss = { showGoalDialog = false },
            onConfirm = { amount -> vm.setGoal(amount); showGoalDialog = false },
            initial = if (ui.goal > 0) ui.goal else null
        )
    }
}

@Composable
fun AmountDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
    initial: Long? = null
) {
    var text by remember { mutableStateOf(initial?.toString() ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { txt -> if (txt.all { it.isDigit() }) text = txt },
                placeholder = { Text("Введите сумму") },
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val value = text.toLongOrNull() ?: 0L
                    if (value > 0) onConfirm(value)
                }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}

private fun money(v: Long): String = NumberFormat.getCurrencyInstance().format(v)
