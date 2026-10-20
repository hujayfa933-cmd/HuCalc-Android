package com.example.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.engine.AngleMode
import com.example.calculator.ui.CalculatorMode
import com.example.calculator.ui.CalculatorUiState
import com.example.ui.theme.LocalCalculatorColors

@Composable
fun CalculatorKeypad(
    uiState: CalculatorUiState,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onToggleSign: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEquals: () -> Unit,
    onPercentage: () -> Unit,
    onFunction: (String) -> Unit,
    onSquare: () -> Unit,
    onCube: () -> Unit,
    onPowerY: () -> Unit,
    onNthRoot: () -> Unit,
    onReciprocal: () -> Unit,
    onAbsoluteValue: () -> Unit,
    onFactorial: () -> Unit,
    onConstant: (String) -> Unit,
    onParenthesis: (String) -> Unit,
    onExp: () -> Unit,
    onToggleAngleMode: () -> Unit,
    onToggleInverse: () -> Unit,
    onToggleAlpha: () -> Unit = {},
    onAns: () -> Unit = {},
    onReplay: () -> Unit = {},
    onFractionToggle: () -> Unit = {},
    onEng: () -> Unit = {},
    onDms: () -> Unit = {},
    onMemoryAdd: () -> Unit = {},
    onMemorySubtract: () -> Unit = {},
    onMemoryRecall: () -> Unit = {},
    onMemoryClear: () -> Unit = {},
    onPermutation: () -> Unit = {},
    onCombination: () -> Unit = {},
    onRandomNumber: () -> Unit = {},
    onOpenConstants: () -> Unit = {},
    onOpenConversions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (uiState.mode == CalculatorMode.GENERAL) {
        GeneralKeypad(
            onDigit = onDigit,
            onOperator = onOperator,
            onDecimal = onDecimal,
            onToggleSign = onToggleSign,
            onClear = onClear,
            onDelete = onDelete,
            onEquals = onEquals,
            onPercentage = onPercentage,
            onFractionToggle = onFractionToggle,
            onOpenConstants = onOpenConstants,
            onOpenConversions = onOpenConversions,
            modifier = modifier
        )
    } else {
        Fx991EsScientificKeypad(
            uiState = uiState,
            onDigit = onDigit,
            onOperator = onOperator,
            onDecimal = onDecimal,
            onToggleSign = onToggleSign,
            onClear = onClear,
            onDelete = onDelete,
            onEquals = onEquals,
            onPercentage = onPercentage,
            onFunction = onFunction,
            onSquare = onSquare,
            onCube = onCube,
            onPowerY = onPowerY,
            onNthRoot = onNthRoot,
            onReciprocal = onReciprocal,
            onAbsoluteValue = onAbsoluteValue,
            onFactorial = onFactorial,
            onConstant = onConstant,
            onParenthesis = onParenthesis,
            onExp = onExp,
            onToggleAngleMode = onToggleAngleMode,
            onToggleInverse = onToggleInverse,
            onToggleAlpha = onToggleAlpha,
            onAns = onAns,
            onReplay = onReplay,
            onFractionToggle = onFractionToggle,
            onEng = onEng,
            onDms = onDms,
            onMemoryAdd = onMemoryAdd,
            onMemorySubtract = onMemorySubtract,
            onMemoryRecall = onMemoryRecall,
            onMemoryClear = onMemoryClear,
            onPermutation = onPermutation,
            onCombination = onCombination,
            onRandomNumber = onRandomNumber,
            onOpenConstants = onOpenConstants,
            onOpenConversions = onOpenConversions,
            modifier = modifier
        )
    }
}

@Composable
private fun GeneralKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onToggleSign: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEquals: () -> Unit,
    onPercentage: () -> Unit,
    onFractionToggle: () -> Unit,
    onOpenConstants: () -> Unit,
    onOpenConversions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Access Row: CONST, CONV, S<=>D
            Row(
                modifier = Modifier.fillMaxWidth().height(42.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(
                    text = "CONST",
                    secondaryText = "40 Constants",
                    type = CalculatorButtonType.SCIENTIFIC,
                    fontSize = 11.sp,
                    cornerRadius = 2.dp,
                    onClick = onOpenConstants,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                CalculatorButton(
                    text = "CONV",
                    secondaryText = "40 Conversions",
                    type = CalculatorButtonType.SCIENTIFIC,
                    fontSize = 11.sp,
                    cornerRadius = 2.dp,
                    onClick = onOpenConversions,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                CalculatorButton(
                    text = "S<=>D",
                    secondaryText = "Fraction",
                    type = CalculatorButtonType.SCIENTIFIC,
                    fontSize = 11.sp,
                    cornerRadius = 2.dp,
                    onClick = onFractionToggle,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(text = "AC", type = CalculatorButtonType.CLEAR, contentDesc = "All Clear", fontSize = 18.sp, cornerRadius = 2.dp, onClick = onClear, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "⌫", type = CalculatorButtonType.FUNCTION, contentDesc = "Delete", fontSize = 18.sp, cornerRadius = 2.dp, onClick = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "%", type = CalculatorButtonType.FUNCTION, contentDesc = "Percent", fontSize = 18.sp, cornerRadius = 2.dp, onClick = onPercentage, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "÷", type = CalculatorButtonType.OPERATOR, contentDesc = "Divide", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onOperator("÷") }, modifier = Modifier.weight(1f).fillMaxHeight())
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(text = "7", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("7") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "8", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("8") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "9", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("9") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "×", type = CalculatorButtonType.OPERATOR, contentDesc = "Multiply", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onOperator("×") }, modifier = Modifier.weight(1f).fillMaxHeight())
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(text = "4", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("4") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "5", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("5") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "6", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("6") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "−", type = CalculatorButtonType.OPERATOR, contentDesc = "Subtract", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onOperator("−") }, modifier = Modifier.weight(1f).fillMaxHeight())
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(text = "1", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("1") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "2", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("2") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "3", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("3") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "+", type = CalculatorButtonType.OPERATOR, contentDesc = "Add", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onOperator("+") }, modifier = Modifier.weight(1f).fillMaxHeight())
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CalculatorButton(text = "±", type = CalculatorButtonType.NUMBER, contentDesc = "Toggle Sign", fontSize = 18.sp, cornerRadius = 2.dp, onClick = onToggleSign, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "0", fontSize = 20.sp, cornerRadius = 2.dp, onClick = { onDigit("0") }, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = ".", fontSize = 20.sp, cornerRadius = 2.dp, onClick = onDecimal, modifier = Modifier.weight(1f).fillMaxHeight())
                CalculatorButton(text = "=", type = CalculatorButtonType.EQUALS, contentDesc = "Equals", fontSize = 22.sp, cornerRadius = 2.dp, onClick = onEquals, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

/**
 * Full fx-991ES PLUS Keypad with 417-function architecture
 */
@Composable
private fun Fx991EsScientificKeypad(
    uiState: CalculatorUiState,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onToggleSign: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEquals: () -> Unit,
    onPercentage: () -> Unit,
    onFunction: (String) -> Unit,
    onSquare: () -> Unit,
    onCube: () -> Unit,
    onPowerY: () -> Unit,
    onNthRoot: () -> Unit,
    onReciprocal: () -> Unit,
    onAbsoluteValue: () -> Unit,
    onFactorial: () -> Unit,
    onConstant: (String) -> Unit,
    onParenthesis: (String) -> Unit,
    onExp: () -> Unit,
    onToggleAngleMode: () -> Unit,
    onToggleInverse: () -> Unit,
    onToggleAlpha: () -> Unit,
    onAns: () -> Unit,
    onReplay: () -> Unit,
    onFractionToggle: () -> Unit,
    onEng: () -> Unit,
    onDms: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryClear: () -> Unit,
    onPermutation: () -> Unit,
    onCombination: () -> Unit,
    onRandomNumber: () -> Unit,
    onOpenConstants: () -> Unit,
    onOpenConversions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isShift = uiState.isInverse
    val isAlpha = uiState.isAlpha

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // =====================================================================
        // ROW 0: Control Cluster with Iconic REPLAY D-Pad
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(0.95f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalculatorButton(
                text = "SHIFT",
                type = CalculatorButtonType.SCIENTIFIC,
                isActiveToggle = isShift,
                fontSize = 11.sp,
                cornerRadius = 2.dp,
                onClick = onToggleInverse,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "ALPHA",
                type = CalculatorButtonType.SCIENTIFIC,
                isActiveToggle = isAlpha,
                fontSize = 11.sp,
                cornerRadius = 2.dp,
                onClick = onToggleAlpha,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            Fx991EsReplayPad(
                onReplayClick = onReplay,
                modifier = Modifier.weight(1.8f).fillMaxHeight()
            )

            CalculatorButton(
                text = if (uiState.angleMode == AngleMode.DEG) "D/R" else "R/D",
                secondaryText = "SETUP",
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 11.sp,
                cornerRadius = 2.dp,
                onClick = onToggleAngleMode,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "ON",
                type = CalculatorButtonType.CLEAR,
                fontSize = 11.sp,
                cornerRadius = 2.dp,
                onClick = onClear,
                modifier = Modifier.weight(0.9f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 1: ■/□, √x (∛), x² (x³), xʸ (ⁿ√), log (10ˣ), ln (eˣ)
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(
                text = "■/□",
                secondaryText = "d/c",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = onFractionToggle,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "√x",
                secondaryText = "∛x",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { onFunction(if (isShift) "cbrt" else "sqrt") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "x²",
                secondaryText = "x³",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onCube() else onSquare() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "xʸ",
                secondaryText = "ⁿ√x",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onNthRoot() else onPowerY() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "log",
                secondaryText = "10ˣ",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { onFunction(if (isShift) "10^" else "log") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "ln",
                secondaryText = "eˣ",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { onFunction(if (isShift) "exp" else "ln") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 2: (-) [sign], ° ' " [DMS], hyp, sin, cos, tan
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(
                text = "(-)",
                secondaryText = "x⁻¹",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onReciprocal() else onToggleSign() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "° ' \"",
                secondaryText = "x!",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onFactorial() else onDms() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "hyp",
                secondaryText = if (isShift) "Abs" else "sinh",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 12.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onAbsoluteValue() else onFunction("sinh") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "sin",
                secondaryText = "sin⁻¹",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { onFunction(if (isShift) "asin" else "sin") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "cos",
                secondaryText = "cos⁻¹",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { onFunction(if (isShift) "acos" else "cos") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "tan",
                secondaryText = "tan⁻¹",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { onFunction(if (isShift) "atan" else "tan") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 3: RCL (STO), ENG (←), ( (CONST), ) (CONV), S<=>D (a b/c), M+ (M-)
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(
                text = "RCL",
                secondaryText = "STO",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 12.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onMemoryClear() else onMemoryRecall() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "ENG",
                secondaryText = "←",
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 12.sp,
                cornerRadius = 2.dp,
                onClick = onEng,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "(",
                secondaryText = "CONST",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.FUNCTION,
                fontSize = 15.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onOpenConstants() else onParenthesis("(") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = ")",
                secondaryText = "CONV",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.FUNCTION,
                fontSize = 15.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onOpenConversions() else onParenthesis(")") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "S<=>D",
                secondaryText = "a b/c ⇔ d/c",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 11.sp,
                cornerRadius = 2.dp,
                onClick = onFractionToggle,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "M+",
                secondaryText = "M-",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.SCIENTIFIC,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onMemorySubtract() else onMemoryAdd() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 4: 7 (CONST), 8 (CONV), 9 (CLR), DEL (INS), AC (OFF)
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1.05f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(
                text = "7",
                secondaryText = "CONST",
                isSecondaryActive = isShift,
                fontSize = 20.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onOpenConstants() else onDigit("7") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "8",
                secondaryText = "CONV",
                isSecondaryActive = isShift,
                fontSize = 20.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onOpenConversions() else onDigit("8") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "9",
                secondaryText = "CLR",
                isSecondaryActive = isShift,
                fontSize = 20.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onClear() else onDigit("9") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "DEL",
                secondaryText = "INS",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.CLEAR,
                fontSize = 14.sp,
                cornerRadius = 2.dp,
                onClick = onDelete,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "AC",
                secondaryText = "OFF",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.CLEAR,
                fontSize = 15.sp,
                cornerRadius = 2.dp,
                onClick = onClear,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 5: 4, 5, 6, × (nPr), ÷ (nCr)
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1.05f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(text = "4", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onDigit("4") }, modifier = Modifier.weight(1f).fillMaxHeight())
            CalculatorButton(text = "5", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onDigit("5") }, modifier = Modifier.weight(1f).fillMaxHeight())
            CalculatorButton(text = "6", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onDigit("6") }, modifier = Modifier.weight(1f).fillMaxHeight())
            CalculatorButton(
                text = "×",
                secondaryText = "nPr",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.OPERATOR,
                fontSize = 22.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onPermutation() else onOperator("×") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "÷",
                secondaryText = "nCr",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.OPERATOR,
                fontSize = 22.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onCombination() else onOperator("÷") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 6: 1, 2, 3, + (Pol), − (Rec)
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1.05f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(text = "1", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onDigit("1") }, modifier = Modifier.weight(1f).fillMaxHeight())
            CalculatorButton(text = "2", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onDigit("2") }, modifier = Modifier.weight(1f).fillMaxHeight())
            CalculatorButton(text = "3", fontSize = 21.sp, cornerRadius = 2.dp, onClick = { onDigit("3") }, modifier = Modifier.weight(1f).fillMaxHeight())
            CalculatorButton(
                text = "+",
                secondaryText = "Pol",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.OPERATOR,
                fontSize = 22.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onFunction("pol") else onOperator("+") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "−",
                secondaryText = "Rec",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.OPERATOR,
                fontSize = 22.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onFunction("rec") else onOperator("−") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // =====================================================================
        // ROW 7: 0 (Ran#), . (RanInt), ×10ˣ (π), Ans (%), =
        // =====================================================================
        Row(
            modifier = Modifier.fillMaxWidth().weight(1.05f),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            CalculatorButton(
                text = "0",
                secondaryText = "Ran#",
                isSecondaryActive = isShift,
                fontSize = 21.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onRandomNumber() else onDigit("0") },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = ".",
                fontSize = 22.sp,
                cornerRadius = 2.dp,
                onClick = onDecimal,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "×10ˣ",
                secondaryText = "π",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.FUNCTION,
                fontSize = 13.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onConstant("pi") else onExp() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "Ans",
                secondaryText = "%",
                isSecondaryActive = isShift,
                type = CalculatorButtonType.FUNCTION,
                fontSize = 14.sp,
                cornerRadius = 2.dp,
                onClick = { if (isShift) onPercentage() else onAns() },
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            CalculatorButton(
                text = "=",
                type = CalculatorButtonType.EQUALS,
                fontSize = 26.sp,
                cornerRadius = 2.dp,
                onClick = onEquals,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

/**
 * Iconic fx-991ES Silver Curved Oval REPLAY Pad
 */
@Composable
private fun Fx991EsReplayPad(
    onReplayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        colors.chassisHighlight.copy(alpha = 0.95f),
                        colors.lcdBezelOuter,
                        colors.chassisShadow
                    )
                )
            )
            .border(1.dp, colors.chassisHighlight, RoundedCornerShape(2.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onReplayClick()
                }
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Replay Left",
                tint = colors.textPrimary,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = "REPLAY",
                color = colors.textPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Replay Right",
                tint = colors.textPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
