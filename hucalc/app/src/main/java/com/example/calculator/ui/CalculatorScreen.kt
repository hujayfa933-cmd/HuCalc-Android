package com.example.calculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calculator.ui.components.AboutDialog
import com.example.calculator.ui.components.CalculatorDisplay
import com.example.calculator.ui.components.CalculatorKeypad
import com.example.calculator.ui.components.HistoryBottomSheet
import com.example.calculator.ui.components.MachinedScrewRivet
import com.example.calculator.ui.components.ScientificConstantsDialog
import com.example.calculator.ui.components.UnitConversionDialog
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    val colors = LocalCalculatorColors.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showHistory by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showConstantsDialog by remember { mutableStateOf(false) }
    var showConversionsDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.chassisBrush),
        containerColor = Color.Transparent,
        topBar = {
            PolishedMetalTopBar(
                currentMode = uiState.mode,
                themeMode = uiState.themeMode,
                historyCount = historyList.size,
                onSwitchMode = { viewModel.switchMode(it) },
                onCycleTheme = { viewModel.cycleThemeMode() },
                onOpenHistory = { showHistory = true },
                onOpenAbout = { showAbout = true }
            )
        }
    ) { innerPadding ->
        // Main Metal Chassis Container with Chamfer Rim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalculatorDisplay(
                        displayValue = uiState.displayValue,
                        expression = uiState.expression,
                        previousExpression = uiState.previousExpression,
                        mode = uiState.mode,
                        angleMode = uiState.angleMode,
                        isInverse = uiState.isInverse,
                        isAlpha = uiState.isAlpha,
                        isError = uiState.errorMessage != null,
                        onToggleAngleMode = { viewModel.toggleAngleMode() },
                        modifier = Modifier
                            .weight(0.42f)
                            .fillMaxHeight()
                    )

                    CalculatorKeypad(
                        uiState = uiState,
                        onDigit = viewModel::onDigit,
                        onOperator = viewModel::onOperator,
                        onDecimal = viewModel::onDecimal,
                        onToggleSign = viewModel::onToggleSign,
                        onClear = viewModel::onClear,
                        onDelete = viewModel::onDelete,
                        onEquals = viewModel::onEquals,
                        onPercentage = viewModel::onPercentage,
                        onFunction = viewModel::onFunction,
                        onSquare = viewModel::onSquare,
                        onCube = viewModel::onCube,
                        onPowerY = viewModel::onPowerY,
                        onNthRoot = viewModel::onNthRoot,
                        onReciprocal = viewModel::onReciprocal,
                        onAbsoluteValue = viewModel::onAbsoluteValue,
                        onFactorial = viewModel::onFactorial,
                        onConstant = viewModel::onConstant,
                        onParenthesis = viewModel::onParenthesis,
                        onExp = viewModel::onExp,
                        onToggleAngleMode = viewModel::toggleAngleMode,
                        onToggleInverse = viewModel::toggleInverse,
                        onToggleAlpha = viewModel::toggleAlpha,
                        onAns = viewModel::onAns,
                        onReplay = viewModel::onReplayPrevious,
                        onFractionToggle = viewModel::onFractionToggle,
                        onEng = viewModel::onEng,
                        onDms = viewModel::onDms,
                        onMemoryAdd = viewModel::onMemoryAdd,
                        onMemorySubtract = viewModel::onMemorySubtract,
                        onMemoryRecall = viewModel::onMemoryRecall,
                        onMemoryClear = viewModel::onMemoryClear,
                        onPermutation = viewModel::onPermutation,
                        onCombination = viewModel::onCombination,
                        onRandomNumber = viewModel::onRandomNumber,
                        onOpenConstants = { showConstantsDialog = true },
                        onOpenConversions = { showConversionsDialog = true },
                        modifier = Modifier
                            .weight(0.58f)
                            .fillMaxHeight()
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    CalculatorDisplay(
                        displayValue = uiState.displayValue,
                        expression = uiState.expression,
                        previousExpression = uiState.previousExpression,
                        mode = uiState.mode,
                        angleMode = uiState.angleMode,
                        isInverse = uiState.isInverse,
                        isAlpha = uiState.isAlpha,
                        isError = uiState.errorMessage != null,
                        onToggleAngleMode = { viewModel.toggleAngleMode() },
                        modifier = Modifier.wrapContentHeight()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    CalculatorKeypad(
                        uiState = uiState,
                        onDigit = viewModel::onDigit,
                        onOperator = viewModel::onOperator,
                        onDecimal = viewModel::onDecimal,
                        onToggleSign = viewModel::onToggleSign,
                        onClear = viewModel::onClear,
                        onDelete = viewModel::onDelete,
                        onEquals = viewModel::onEquals,
                        onPercentage = viewModel::onPercentage,
                        onFunction = viewModel::onFunction,
                        onSquare = viewModel::onSquare,
                        onCube = viewModel::onCube,
                        onPowerY = viewModel::onPowerY,
                        onNthRoot = viewModel::onNthRoot,
                        onReciprocal = viewModel::onReciprocal,
                        onAbsoluteValue = viewModel::onAbsoluteValue,
                        onFactorial = viewModel::onFactorial,
                        onConstant = viewModel::onConstant,
                        onParenthesis = viewModel::onParenthesis,
                        onExp = viewModel::onExp,
                        onToggleAngleMode = viewModel::toggleAngleMode,
                        onToggleInverse = viewModel::toggleInverse,
                        onToggleAlpha = viewModel::toggleAlpha,
                        onAns = viewModel::onAns,
                        onReplay = viewModel::onReplayPrevious,
                        onFractionToggle = viewModel::onFractionToggle,
                        onEng = viewModel::onEng,
                        onDms = viewModel::onDms,
                        onMemoryAdd = viewModel::onMemoryAdd,
                        onMemorySubtract = viewModel::onMemorySubtract,
                        onMemoryRecall = viewModel::onMemoryRecall,
                        onMemoryClear = viewModel::onMemoryClear,
                        onPermutation = viewModel::onPermutation,
                        onCombination = viewModel::onCombination,
                        onRandomNumber = viewModel::onRandomNumber,
                        onOpenConstants = { showConstantsDialog = true },
                        onOpenConversions = { showConversionsDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }

        if (showHistory) {
            HistoryBottomSheet(
                historyList = historyList,
                onDismiss = { showHistory = false },
                onSelectItem = { item, useResult -> viewModel.onHistoryItemClick(item, useResult) },
                onClearAll = { viewModel.clearHistory() },
                onDeleteItem = { id -> viewModel.deleteHistoryItem(id) }
            )
        }

        if (showAbout) {
            AboutDialog(
                onDismiss = { showAbout = false }
            )
        }

        if (showConstantsDialog) {
            ScientificConstantsDialog(
                onDismiss = { showConstantsDialog = false },
                onSelectConstant = { constant ->
                    viewModel.onInsertConstant(constant.symbol, constant.value)
                    showConstantsDialog = false
                }
            )
        }

        if (showConversionsDialog) {
            UnitConversionDialog(
                onDismiss = { showConversionsDialog = false },
                onSelectConversion = { conv ->
                    viewModel.onApplyConversion(conv)
                    showConversionsDialog = false
                }
            )
        }
    }
}

@Composable
private fun PolishedMetalTopBar(
    currentMode: CalculatorMode,
    themeMode: ThemeMode,
    historyCount: Int,
    onSwitchMode: (CalculatorMode) -> Unit,
    onCycleTheme: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val colors = LocalCalculatorColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Machined Aluminum Slider Switch with Tactile Grip Grooves
        Surface(
            shape = RoundedCornerShape(2.dp),
            color = colors.lcdBezelInner,
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.chassisHighlight.copy(alpha = 0.8f)),
            modifier = Modifier.testTag("mode_selector_pill")
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isGeneral = currentMode == CalculatorMode.GENERAL
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (isGeneral) colors.keyOperatorTop else colors.lcdBezelInner,
                    border = if (isGeneral) androidx.compose.foundation.BorderStroke(0.8.dp, colors.keyOperatorRim) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .clickable { onSwitchMode(CalculatorMode.GENERAL) }
                        .testTag("tab_general")
                ) {
                    Text(
                        text = "GENERAL",
                        fontSize = 9.sp,
                        fontWeight = if (isGeneral) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.2.sp,
                        color = if (isGeneral) Color.White else colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                val isScientific = currentMode == CalculatorMode.SCIENTIFIC
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (isScientific) colors.keyOperatorTop else colors.lcdBezelInner,
                    border = if (isScientific) androidx.compose.foundation.BorderStroke(0.8.dp, colors.keyOperatorRim) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .clickable { onSwitchMode(CalculatorMode.SCIENTIFIC) }
                        .testTag("tab_scientific")
                ) {
                    Text(
                        text = "SCIENTIFIC",
                        fontSize = 10.sp,
                        fontWeight = if (isScientific) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.4.sp,
                        color = if (isScientific) Color.White else colors.textSecondary,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Precision Machined Tool Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onCycleTheme,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_theme_toggle")
            ) {
                val themeIcon = when (themeMode) {
                    ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                    ThemeMode.DARK_PLATINUM -> Icons.Default.DarkMode
                    ThemeMode.SILVER_ALUMINUM -> Icons.Default.LightMode
                }
                Icon(
                    imageVector = themeIcon,
                    contentDescription = "Theme: ${themeMode.name}",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }

            BadgedBox(
                badge = {
                    if (historyCount > 0) {
                        Badge(
                            containerColor = colors.keyClearTop,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (historyCount > 99) "99+" else historyCount.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                onClick = onOpenAbout,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("btn_about")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
