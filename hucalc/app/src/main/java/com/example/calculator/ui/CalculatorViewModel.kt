package com.example.calculator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.data.AppDatabase
import com.example.calculator.data.CalculationHistoryEntity
import com.example.calculator.engine.AngleMode
import com.example.calculator.engine.CalculationEngine
import com.example.calculator.engine.EvaluationResult
import com.example.calculator.engine.UnitConversion
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

enum class CalculatorMode {
    GENERAL,
    SCIENTIFIC
}

data class CalculatorUiState(
    val mode: CalculatorMode = CalculatorMode.SCIENTIFIC,
    val angleMode: AngleMode = AngleMode.DEG,
    val isInverse: Boolean = false, // SHIFT
    val isAlpha: Boolean = false,
    val hasMemory: Boolean = false,
    val memoryValue: Double = 0.0,
    val ansValue: String = "0",
    val displayValue: String = "0",
    val expression: String = "",
    val previousExpression: String = "",
    val isResultState: Boolean = false,
    val errorMessage: String? = null,
    val isFractionDisplay: Boolean = false,
    val numericResultValue: Double? = null,
    val themeMode: ThemeMode = ThemeMode.SILVER_ALUMINUM
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = CalculationEngine()
    private val database = AppDatabase.getInstance(application)
    private val historyDao = database.calculationHistoryDao()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    val historyList: StateFlow<List<CalculationHistoryEntity>> = historyDao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun switchMode(mode: CalculatorMode) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun toggleAngleMode() {
        val next = if (_uiState.value.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        _uiState.value = _uiState.value.copy(angleMode = next)
        if (_uiState.value.expression.isNotBlank() && !_uiState.value.isResultState) {
            updateLivePreview()
        }
    }

    fun toggleInverse() {
        _uiState.value = _uiState.value.copy(isInverse = !_uiState.value.isInverse)
    }

    fun toggleAlpha() {
        _uiState.value = _uiState.value.copy(isAlpha = !_uiState.value.isAlpha)
    }

    fun onAns() {
        val state = _uiState.value
        val ans = state.ansValue
        val baseExpr = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val newExpr = baseExpr + ans
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
        updateLivePreview()
    }

    /**
     * Casio S<=>D (Standard <=> Decimal) button:
     * Converts between exact fraction (d/c improper, a b/c mixed) and decimal representation.
     */
    fun onFractionToggle() {
        val state = _uiState.value
        var num = state.numericResultValue
        if (num == null) {
            num = engine.parseFractionToDouble(state.displayValue)
        }
        if (num == null && state.expression.isNotBlank() && state.errorMessage == null) {
            val eval = engine.evaluate(state.expression, state.angleMode)
            if (eval is EvaluationResult.Success) {
                num = eval.value
            }
        }
        if (num == null) return

        val rep = engine.toFractionRepresentation(num) ?: return
        val currentDisplay = state.displayValue.trim()

        val nextDisplay: String
        val isFrac: Boolean

        if (currentDisplay == rep.decimal) {
            // Decimal -> Improper fraction (or mixed)
            nextDisplay = rep.improper
            isFrac = true
        } else if (currentDisplay == rep.improper && rep.improper != rep.mixed) {
            // Improper fraction -> Mixed fraction
            nextDisplay = rep.mixed
            isFrac = true
        } else {
            // Fraction -> Decimal
            nextDisplay = rep.decimal
            isFrac = false
        }

        _uiState.value = state.copy(
            displayValue = nextDisplay,
            numericResultValue = num,
            isFractionDisplay = isFrac,
            isResultState = true
        )
    }

    /**
     * Casio ENG button: Formats result into engineering powers of ten
     */
    fun onEng() {
        val state = _uiState.value
        val num = state.numericResultValue ?: state.displayValue.toDoubleOrNull() ?: return
        val eng = engine.toEngString(num)
        _uiState.value = state.copy(displayValue = eng)
    }

    /**
     * Casio ° ' " button: Converts decimal degrees to DMS
     */
    fun onDms() {
        val state = _uiState.value
        val num = state.numericResultValue ?: state.displayValue.toDoubleOrNull() ?: return
        val dms = engine.toDmsString(num)
        _uiState.value = state.copy(displayValue = dms)
    }

    /**
     * Casio M+ button: Adds to memory
     */
    fun onMemoryAdd() {
        val state = _uiState.value
        val num = state.numericResultValue ?: state.displayValue.toDoubleOrNull() ?: 0.0
        val newMem = state.memoryValue + num
        _uiState.value = state.copy(
            memoryValue = newMem,
            hasMemory = abs(newMem) > 1e-12
        )
    }

    /**
     * Casio M- button: Subtracts from memory
     */
    fun onMemorySubtract() {
        val state = _uiState.value
        val num = state.numericResultValue ?: state.displayValue.toDoubleOrNull() ?: 0.0
        val newMem = state.memoryValue - num
        _uiState.value = state.copy(
            memoryValue = newMem,
            hasMemory = abs(newMem) > 1e-12
        )
    }

    /**
     * Casio RCL M / MR button: Recalls memory value into calculation
     */
    fun onMemoryRecall() {
        val state = _uiState.value
        if (!state.hasMemory) return
        val memStr = engine.formatResult(state.memoryValue)
        val baseExpr = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val newExpr = baseExpr + memStr
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
        updateLivePreview()
    }

    /**
     * Casio MC button: Clears memory
     */
    fun onMemoryClear() {
        _uiState.value = _uiState.value.copy(
            memoryValue = 0.0,
            hasMemory = false
        )
    }

    /**
     * Casio nPr button (Permutations)
     */
    fun onPermutation() {
        onOperator("P")
    }

    /**
     * Casio nCr button (Combinations)
     */
    fun onCombination() {
        onOperator("C")
    }

    /**
     * Casio Ran# button (Random number between 0 and 1)
     */
    fun onRandomNumber() {
        val ran = Random.nextDouble()
        val formatted = engine.formatResult(ran)
        val state = _uiState.value
        val baseExpr = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val newExpr = baseExpr + formatted
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
        updateLivePreview()
    }

    /**
     * Casio CONST button: Inserts one of the 40 scientific constants
     */
    fun onInsertConstant(symbol: String, value: Double) {
        val valStr = engine.formatResult(value)
        val state = _uiState.value
        val baseExpr = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val newExpr = baseExpr + valStr
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
        updateLivePreview()
    }

    /**
     * Casio CONV button: Applies unit conversion
     */
    fun onApplyConversion(conversion: UnitConversion) {
        val state = _uiState.value
        val num = state.numericResultValue ?: state.displayValue.toDoubleOrNull() ?: return
        val converted = conversion.convert(num)
        val formatted = engine.formatResult(converted)
        _uiState.value = state.copy(
            previousExpression = "${engine.formatResult(num)} ${conversion.fromUnit} ➔ ${conversion.toUnit}",
            displayValue = formatted,
            numericResultValue = converted,
            isResultState = true
        )
    }

    fun onReplayPrevious() {
        val history = historyList.value
        if (history.isNotEmpty()) {
            val item = history.first()
            _uiState.value = _uiState.value.copy(
                expression = item.expression,
                displayValue = item.result,
                previousExpression = "${item.expression} =",
                isResultState = true,
                errorMessage = null
            )
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun cycleThemeMode() {
        val next = when (_uiState.value.themeMode) {
            ThemeMode.SILVER_ALUMINUM -> ThemeMode.DARK_PLATINUM
            ThemeMode.DARK_PLATINUM -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.SILVER_ALUMINUM
        }
        _uiState.value = _uiState.value.copy(themeMode = next)
    }

    fun onDigit(digit: String) {
        val state = _uiState.value
        val newExpr = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            digit
        } else {
            state.expression + digit
        }
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
        updateLivePreview()
    }

    fun onDecimal() {
        val state = _uiState.value
        if (state.isResultState || state.errorMessage != null) {
            _uiState.value = state.copy(
                expression = "0.",
                displayValue = "0.",
                isResultState = false,
                errorMessage = null
            )
            return
        }

        val expr = state.expression
        if (expr.isEmpty()) {
            _uiState.value = state.copy(expression = "0.", displayValue = "0.")
            return
        }

        val lastOpIndex = expr.lastIndexOfAny(charArrayOf('+', '−', '-', '×', '*', '÷', '/', '%', '^', '(', ')', 'P', 'C'))
        val currentNumber = if (lastOpIndex >= 0) expr.substring(lastOpIndex + 1) else expr

        if (!currentNumber.contains(".")) {
            val toAppend = if (currentNumber.isEmpty()) "0." else "."
            val updated = expr + toAppend
            _uiState.value = state.copy(expression = updated, displayValue = updated)
        }
    }

    fun onOperator(op: String) {
        val state = _uiState.value
        val displayOp = when (op) {
            "*" -> "×"
            "/" -> "÷"
            "-" -> "−"
            else -> op
        }

        if (state.errorMessage != null) {
            _uiState.value = state.copy(errorMessage = null)
        }

        val expr = state.expression
        if (expr.isEmpty()) {
            if (displayOp == "−") {
                _uiState.value = state.copy(expression = "−", displayValue = "−", isResultState = false)
            }
            return
        }

        val lastChar = expr.last().toString()
        val isOperator = lastChar in listOf("+", "−", "-", "×", "*", "÷", "/", "^", "%", "P", "C")

        val newExpr = if (isOperator) {
            expr.dropLast(1) + displayOp
        } else {
            expr + displayOp
        }

        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false
        )
    }

    fun onFunction(functionName: String) {
        val state = _uiState.value
        val actualFunc = if (state.isInverse) {
            when (functionName) {
                "sin" -> "asin"
                "cos" -> "acos"
                "tan" -> "atan"
                "sinh" -> "asinh"
                "cosh" -> "acosh"
                "tanh" -> "atanh"
                "ln" -> "exp"
                "log" -> "10^"
                "sqrt" -> "cbrt"
                else -> functionName
            }
        } else {
            functionName
        }

        val appendStr = when (actualFunc) {
            "10^" -> "10^("
            "exp" -> "e^("
            "cbrt" -> "cbrt("
            else -> "$actualFunc("
        }

        val baseExpr = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }

        val newExpr = baseExpr + appendStr
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null,
            isInverse = false // Reset shift after function use
        )
    }

    fun onSquare() {
        val state = _uiState.value
        if (state.expression.isNotEmpty()) {
            val newExpr = state.expression + "^2"
            _uiState.value = state.copy(
                expression = newExpr,
                displayValue = newExpr,
                isResultState = false
            )
            updateLivePreview()
        }
    }

    fun onCube() {
        val state = _uiState.value
        if (state.expression.isNotEmpty()) {
            val newExpr = state.expression + "^3"
            _uiState.value = state.copy(
                expression = newExpr,
                displayValue = newExpr,
                isResultState = false
            )
            updateLivePreview()
        }
    }

    fun onPowerY() {
        onOperator("^")
    }

    fun onNthRoot() {
        val state = _uiState.value
        val newExpr = if (state.isResultState) {
            state.expression + "^(1/"
        } else if (state.expression.isNotEmpty()) {
            state.expression + "^(1/"
        } else {
            "^(1/"
        }
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false
        )
    }

    fun onReciprocal() {
        val state = _uiState.value
        val expr = state.expression
        val newExpr = if (expr.isEmpty() || state.isResultState) {
            "1/(" + (if (expr.isEmpty()) "" else expr)
        } else {
            "1/($expr)"
        }
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false
        )
        updateLivePreview()
    }

    fun onConstant(constant: String) {
        val state = _uiState.value
        val base = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val symbol = when (constant) {
            "pi" -> "π"
            "e" -> "e"
            else -> constant
        }
        val newExpr = base + symbol
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
        updateLivePreview()
    }

    fun onParenthesis(paren: String) {
        val state = _uiState.value
        val base = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val newExpr = base + paren
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
    }

    fun onFactorial() {
        val state = _uiState.value
        if (state.expression.isNotEmpty()) {
            val newExpr = state.expression + "!"
            _uiState.value = state.copy(
                expression = newExpr,
                displayValue = newExpr,
                isResultState = false
            )
            updateLivePreview()
        }
    }

    fun onAbsoluteValue() {
        val state = _uiState.value
        val base = if (state.isResultState || state.errorMessage != null || state.displayValue == "0") {
            ""
        } else {
            state.expression
        }
        val newExpr = base + "abs("
        _uiState.value = state.copy(
            expression = newExpr,
            displayValue = newExpr,
            isResultState = false,
            errorMessage = null
        )
    }

    fun onExp() {
        val state = _uiState.value
        if (state.expression.isNotEmpty()) {
            val lastChar = state.expression.last()
            if (lastChar.isDigit()) {
                val newExpr = state.expression + "E"
                _uiState.value = state.copy(
                    expression = newExpr,
                    displayValue = newExpr,
                    isResultState = false
                )
            }
        }
    }

    fun onPercentage() {
        val state = _uiState.value
        if (state.expression.isNotEmpty()) {
            val newExpr = state.expression + "%"
            _uiState.value = state.copy(
                expression = newExpr,
                displayValue = newExpr,
                isResultState = false
            )
            updateLivePreview()
        }
    }

    fun onToggleSign() {
        val state = _uiState.value
        val expr = state.expression

        if (expr.isEmpty() || expr == "0") {
            _uiState.value = state.copy(expression = "−", displayValue = "−")
            return
        }

        if (state.isResultState) {
            val toggled = if (expr.startsWith("−") || expr.startsWith("-")) {
                expr.substring(1)
            } else {
                "−$expr"
            }
            _uiState.value = state.copy(
                expression = toggled,
                displayValue = toggled
            )
            return
        }

        val lastOpIndex = expr.lastIndexOfAny(charArrayOf('+', '−', '-', '×', '*', '÷', '/', '(', '^', 'P', 'C'))
        if (lastOpIndex == -1) {
            val toggled = if (expr.startsWith("−") || expr.startsWith("-")) {
                expr.substring(1)
            } else {
                "−$expr"
            }
            _uiState.value = state.copy(expression = toggled, displayValue = toggled)
        } else {
            val prefix = expr.substring(0, lastOpIndex)
            val op = expr[lastOpIndex]
            val suffix = expr.substring(lastOpIndex + 1)

            if (op == '−' || op == '-') {
                val newExpr = if (lastOpIndex == 0 || expr[lastOpIndex - 1] in "+−-*÷/(") {
                    prefix + suffix
                } else {
                    "$prefix+$suffix"
                }
                _uiState.value = state.copy(expression = newExpr, displayValue = newExpr)
            } else if (op == '+') {
                val newExpr = "$prefix−$suffix"
                _uiState.value = state.copy(expression = newExpr, displayValue = newExpr)
            } else {
                val newExpr = "$prefix$op(−$suffix"
                _uiState.value = state.copy(expression = newExpr, displayValue = newExpr)
            }
        }
    }

    fun onClear() {
        _uiState.value = _uiState.value.copy(
            displayValue = "0",
            expression = "",
            previousExpression = "",
            isResultState = false,
            errorMessage = null,
            numericResultValue = null,
            isFractionDisplay = false
        )
    }

    fun onDelete() {
        val state = _uiState.value
        if (state.isResultState || state.errorMessage != null) {
            onClear()
            return
        }

        val expr = state.expression
        if (expr.isEmpty() || expr == "0") {
            _uiState.value = state.copy(displayValue = "0", expression = "")
            return
        }

        val funcs = listOf("asin(", "acos(", "atan(", "asinh(", "acosh(", "atanh(", "sinh(", "cosh(", "tanh(", "log(", "cbrt(", "abs(", "sin(", "cos(", "tan(", "ln(", "10^(", "e^(", "^(1/")
        var dropped = false
        for (f in funcs) {
            if (expr.endsWith(f)) {
                val updated = expr.dropLast(f.length)
                _uiState.value = state.copy(
                    expression = updated,
                    displayValue = if (updated.isEmpty()) "0" else updated
                )
                dropped = true
                break
            }
        }

        if (!dropped) {
            val updated = expr.dropLast(1)
            _uiState.value = state.copy(
                expression = updated,
                displayValue = if (updated.isEmpty()) "0" else updated
            )
        }
        updateLivePreview()
    }

    fun onEquals() {
        val state = _uiState.value
        val expr = state.expression
        if (expr.isBlank()) return

        when (val result = engine.evaluate(expr, state.angleMode)) {
            is EvaluationResult.Success -> {
                val formatted = result.formatted
                val modeStr = if (state.mode == CalculatorMode.GENERAL) "General" else "Scientific"

                viewModelScope.launch {
                    historyDao.insert(
                        CalculationHistoryEntity(
                            expression = expr,
                            result = formatted,
                            mode = modeStr
                        )
                    )
                }

                _uiState.value = state.copy(
                    previousExpression = "$expr =",
                    displayValue = formatted,
                    expression = formatted,
                    ansValue = formatted,
                    numericResultValue = result.value,
                    isResultState = true,
                    isFractionDisplay = false,
                    errorMessage = null
                )
            }
            is EvaluationResult.Error -> {
                _uiState.value = state.copy(
                    previousExpression = "$expr =",
                    displayValue = result.message,
                    errorMessage = result.message,
                    numericResultValue = null,
                    isResultState = true
                )
            }
        }
    }

    private fun updateLivePreview() {
        val state = _uiState.value
        val expr = state.expression
        if (expr.length < 2 || state.isResultState) return

        when (val result = engine.evaluate(expr, state.angleMode)) {
            is EvaluationResult.Success -> {
                _uiState.value = state.copy(previousExpression = result.formatted)
            }
            is EvaluationResult.Error -> {}
        }
    }

    fun onHistoryItemClick(item: CalculationHistoryEntity, useResult: Boolean) {
        val state = _uiState.value
        if (useResult) {
            _uiState.value = state.copy(
                expression = item.result,
                displayValue = item.result,
                previousExpression = "${item.expression} =",
                ansValue = item.result,
                numericResultValue = item.result.toDoubleOrNull(),
                isResultState = true,
                errorMessage = null
            )
        } else {
            _uiState.value = state.copy(
                expression = item.expression,
                displayValue = item.expression,
                previousExpression = "",
                isResultState = false,
                errorMessage = null
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyDao.clearAll()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyDao.deleteById(id)
        }
    }
}
