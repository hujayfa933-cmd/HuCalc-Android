package com.example.calculator.engine

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

sealed class EvaluationResult {
    data class Success(val value: Double, val formatted: String) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}

class CalculationEngine {

    companion object {
        const val PI_VALUE = Math.PI
        const val E_VALUE = Math.E
        private const val EPSILON = 1e-12
        private const val MAX_FACTORIAL = 170
    }

    /**
     * Evaluates a mathematical expression string given the specified AngleMode.
     */
    fun evaluate(expression: String, angleMode: AngleMode): EvaluationResult {
        val sanitized = sanitize(expression)
        if (sanitized.isBlank()) {
            return EvaluationResult.Error("Empty expression")
        }

        return try {
            val parser = ExpressionParser(sanitized, angleMode)
            val result = parser.parse()
            if (result.isNaN()) {
                EvaluationResult.Error("Result is undefined")
            } else if (result.isInfinite()) {
                EvaluationResult.Error(if (result > 0) "Infinity" else "-Infinity")
            } else {
                val cleaned = cleanDouble(result)
                val formatted = formatResult(cleaned)
                EvaluationResult.Success(cleaned, formatted)
            }
        } catch (e: ArithmeticException) {
            EvaluationResult.Error(e.message ?: "Arithmetic error")
        } catch (e: IllegalArgumentException) {
            EvaluationResult.Error(e.message ?: "Invalid expression")
        } catch (e: Exception) {
            EvaluationResult.Error("Malformed expression")
        }
    }

    private fun sanitize(expr: String): String {
        return expr
            .replace("×10^", "*10^")
            .replace("×10ˣ", "*10^")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("–", "-")
            .replace("π", "pi")
            .replace("EXP", "E")
            .replace("nPr", "P")
            .replace("nCr", "C")
            .replace(" ", "")
    }

    fun cleanDouble(value: Double): Double {
        if (value.isNaN() || value.isInfinite()) return value
        if (abs(value) < EPSILON) return 0.0

        return try {
            BigDecimal(value)
                .setScale(12, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toDouble()
        } catch (e: Exception) {
            value
        }
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Undefined"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        if (abs(value) < EPSILON) return "0"

        val absVal = abs(value)
        val symbols = DecimalFormatSymbols(Locale.US)

        return if (absVal >= 1e12 || (absVal > 0 && absVal < 1e-7)) {
            val df = DecimalFormat("0.######E0", symbols)
            df.format(value)
        } else {
            val df = DecimalFormat("#,##0.##########", symbols)
            df.isGroupingUsed = false
            val formatted = df.format(value)
            if (formatted == "-0") "0" else formatted
        }
    }

    data class FractionRepresentation(
        val improper: String,
        val mixed: String,
        val decimal: String
    )

    /**
     * Converts a decimal to exact fraction representations (Casio S<=>D feature: d/c, a b/c, and decimal)
     */
    fun toFractionRepresentation(value: Double): FractionRepresentation? {
        if (value.isNaN() || value.isInfinite() || abs(value) > 1e9) return null
        val absVal = abs(value)
        val sign = if (value < 0) "-" else ""
        val decStr = formatResult(value)

        // Integer values (e.g. 2 -> improper: 2/1, mixed: 2, decimal: 2)
        if (abs(absVal - round(absVal)) < EPSILON) {
            val longVal = round(absVal).toLong()
            return FractionRepresentation(
                improper = "$sign$longVal/1",
                mixed = "$sign$longVal",
                decimal = decStr
            )
        }

        val tolerance = 1e-6
        var h1 = 1L; var h2 = 0L
        var k1 = 0L; var k2 = 1L
        var b = absVal

        do {
            val a = floor(b).toLong()
            var aux = h1
            h1 = a * h1 + h2
            h2 = aux
            aux = k1
            k1 = a * k1 + k2
            k2 = aux
            if (abs(b - a) < 1e-12) break
            b = 1.0 / (b - a)
        } while (abs(absVal - h1.toDouble() / k1.toDouble()) > tolerance && k1 < 1000000L)

        if (k1 <= 1L) {
            return FractionRepresentation(
                improper = "$sign$h1/1",
                mixed = "$sign$h1",
                decimal = decStr
            )
        }

        val improperStr = "$sign$h1/$k1"
        val mixedStr = if (h1 > k1) {
            val whole = h1 / k1
            val rem = h1 % k1
            if (rem == 0L) "$sign$whole" else "$sign$whole $rem/$k1"
        } else {
            improperStr
        }

        return FractionRepresentation(
            improper = improperStr,
            mixed = mixedStr,
            decimal = decStr
        )
    }

    /**
     * Converts a decimal to exact fraction representation string (Casio S<=>D feature)
     */
    fun toFractionString(value: Double): String? {
        val rep = toFractionRepresentation(value) ?: return null
        return if (rep.improper != rep.decimal) rep.improper else null
    }

    /**
     * Parses a string representation of fraction or decimal to Double
     */
    fun parseFractionToDouble(str: String): Double? {
        val trimmed = str.trim()
        if (trimmed.isEmpty()) return null

        // Mixed fraction: "2 1/3" or "-2 1/3"
        if (trimmed.contains(' ') && trimmed.contains('/')) {
            val parts = trimmed.split(' ')
            if (parts.size == 2) {
                val whole = parts[0].toDoubleOrNull() ?: return null
                val fracParts = parts[1].split('/')
                if (fracParts.size == 2) {
                    val num = fracParts[0].toDoubleOrNull() ?: return null
                    val den = fracParts[1].toDoubleOrNull() ?: return null
                    if (den == 0.0) return null
                    val sign = if (parts[0].startsWith("-")) -1.0 else 1.0
                    return whole + sign * (num / den)
                }
            }
        }

        // Simple fraction: "5/2" or "-5/2"
        if (trimmed.contains('/')) {
            val parts = trimmed.split('/')
            if (parts.size == 2) {
                val num = parts[0].toDoubleOrNull() ?: return null
                val den = parts[1].toDoubleOrNull() ?: return null
                if (den == 0.0) return null
                return num / den
            }
        }

        return trimmed.toDoubleOrNull()
    }

    /**
     * Formats degrees into Degrees, Minutes, Seconds (Casio ° ' " feature)
     */
    fun toDmsString(degrees: Double): String {
        if (degrees.isNaN() || degrees.isInfinite()) return formatResult(degrees)
        val sign = if (degrees < 0) "-" else ""
        val absDeg = abs(degrees)
        val d = floor(absDeg).toInt()
        val mRemainder = (absDeg - d) * 60.0
        val m = floor(mRemainder).toInt()
        val s = (mRemainder - m) * 60.0
        val df = DecimalFormat("0.##", DecimalFormatSymbols(Locale.US))
        return "$sign${d}°${m}'${df.format(s)}\""
    }

    /**
     * Engineering Notation (Casio ENG feature)
     */
    fun toEngString(value: Double): String {
        if (value == 0.0 || value.isNaN() || value.isInfinite()) return formatResult(value)
        val exponent = floor(log10(abs(value))).toInt()
        val engExp = if (exponent >= 0) (exponent / 3) * 3 else ((exponent - 2) / 3) * 3
        val mantissa = value / 10.0.pow(engExp)
        val df = DecimalFormat("0.######", DecimalFormatSymbols(Locale.US))
        return if (engExp == 0) df.format(mantissa) else "${df.format(mantissa)}×10^$engExp"
    }

    // =========================================================================
    // Recursive Descent Parser
    // =========================================================================

    private class ExpressionParser(
        private val input: String,
        private val angleMode: AngleMode
    ) {
        private var pos = 0
        private val length = input.length

        private fun peek(): Char = if (pos < length) input[pos] else '\u0000'

        private fun advance(): Char {
            val c = peek()
            if (pos < length) pos++
            return c
        }

        private fun match(expected: Char): Boolean {
            if (peek() == expected) {
                pos++
                return true
            }
            return false
        }

        private fun match(str: String): Boolean {
            if (input.startsWith(str, pos)) {
                pos += str.length
                return true
            }
            return false
        }

        fun parse(): Double {
            val res = parseExpression()
            if (pos < length && peek() != ')' && peek() != ',') {
                throw IllegalArgumentException("Unexpected character '${peek()}'")
            }
            return res
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (pos < length) {
                when {
                    match('+') -> {
                        value += parseTerm()
                    }
                    match('-') -> {
                        value -= parseTerm()
                    }
                    else -> break
                }
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parsePower()
            while (pos < length) {
                when {
                    match('*') -> {
                        value *= parsePower()
                    }
                    match('/') -> {
                        val divisor = parsePower()
                        if (abs(divisor) < EPSILON) {
                            throw ArithmeticException("Cannot divide by zero")
                        }
                        value /= divisor
                    }
                    match("mod") -> {
                        val divisor = parsePower()
                        if (abs(divisor) < EPSILON) {
                            throw ArithmeticException("Cannot divide by zero")
                        }
                        value %= divisor
                    }
                    match('P') -> {
                        // nPr: Permutations
                        val r = parsePower()
                        value = computeNPr(value, r)
                    }
                    match('C') -> {
                        // nCr: Combinations
                        val r = parsePower()
                        value = computeNCr(value, r)
                    }
                    peek() == '(' || (peek().isLetter() && peek() != 'P' && peek() != 'C') -> {
                        value *= parsePower()
                    }
                    else -> break
                }
            }
            return value
        }

        private fun parsePower(): Double {
            var base = parsePostfix()
            if (match('^')) {
                val exponent = parsePower()
                if (base < 0 && (abs(exponent - round(exponent)) > EPSILON)) {
                    throw ArithmeticException("Domain error: negative base with fractional exponent")
                }
                base = base.pow(exponent)
            }
            return base
        }

        private fun parsePostfix(): Double {
            var value = parseUnary()
            while (pos < length) {
                when {
                    match('!') -> {
                        if (value < 0 || abs(value - round(value)) > EPSILON) {
                            throw ArithmeticException("Factorial requires non-negative integer")
                        }
                        if (value > MAX_FACTORIAL) {
                            throw ArithmeticException("Factorial overflow (> 170!)")
                        }
                        value = computeFactorial(round(value).toInt())
                    }
                    match('%') -> {
                        value /= 100.0
                    }
                    else -> break
                }
            }
            return value
        }

        private fun parseUnary(): Double {
            if (match('+')) return parseUnary()
            if (match('-')) return -parseUnary()
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            val c = peek()

            if (c == '(') {
                advance()
                val value = parseExpression()
                match(')')
                return value
            }

            if (c.isDigit() || c == '.') {
                return parseNumber()
            }

            if (c.isLetter()) {
                return parseWordOrFunction()
            }

            if (c == '\u0000') {
                throw IllegalArgumentException("Unexpected end of expression")
            }

            throw IllegalArgumentException("Unexpected character '$c'")
        }

        private fun parseNumber(): Double {
            val start = pos
            var hasDot = false
            while (pos < length) {
                val c = peek()
                if (c == '.') {
                    if (hasDot) break
                    hasDot = true
                    pos++
                } else if (c.isDigit()) {
                    pos++
                } else {
                    break
                }
            }

            if (pos < length && (peek() == 'E' || peek() == 'e')) {
                val ePos = pos
                pos++
                if (pos < length && (peek() == '+' || peek() == '-')) {
                    pos++
                }
                var hasExpDigits = false
                while (pos < length && peek().isDigit()) {
                    hasExpDigits = true
                    pos++
                }
                if (!hasExpDigits) {
                    pos = ePos
                }
            }

            val numStr = input.substring(start, pos)
            return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
        }

        private fun parseWordOrFunction(): Double {
            val start = pos
            while (pos < length && peek().isLetter()) {
                pos++
            }
            val word = input.substring(start, pos).lowercase()

            return when (word) {
                "pi" -> PI_VALUE
                "e" -> E_VALUE
                "sin" -> evalSin(parseArgument())
                "cos" -> evalCos(parseArgument())
                "tan" -> evalTan(parseArgument())
                "asin", "arcsin" -> evalAsin(parseArgument())
                "acos", "arccos" -> evalAcos(parseArgument())
                "atan", "arctan" -> evalAtan(parseArgument())
                "sinh" -> sinh(parseArgument())
                "cosh" -> cosh(parseArgument())
                "tanh" -> tanh(parseArgument())
                "asinh" -> {
                    val x = parseArgument()
                    ln(x + sqrt(x * x + 1.0))
                }
                "acosh" -> {
                    val x = parseArgument()
                    if (x < 1.0) throw ArithmeticException("Domain error: acosh requires x >= 1")
                    ln(x + sqrt(x * x - 1.0))
                }
                "atanh" -> {
                    val x = parseArgument()
                    if (abs(x) >= 1.0) throw ArithmeticException("Domain error: atanh requires |x| < 1")
                    0.5 * ln((1.0 + x) / (1.0 - x))
                }
                "log", "log10" -> {
                    val arg = parseArgument()
                    if (arg <= 0) throw ArithmeticException("Domain error: log of non-positive number")
                    log10(arg)
                }
                "ln" -> {
                    val arg = parseArgument()
                    if (arg <= 0) throw ArithmeticException("Domain error: ln of non-positive number")
                    ln(arg)
                }
                "sqrt", "√" -> {
                    val arg = parseArgument()
                    if (arg < 0) throw ArithmeticException("Domain error: square root of negative number")
                    sqrt(arg)
                }
                "cbrt", "∛" -> cbrt(parseArgument())
                "abs" -> abs(parseArgument())
                "exp" -> exp(parseArgument())
                "fact" -> {
                    val arg = parseArgument()
                    if (arg < 0 || abs(arg - round(arg)) > EPSILON) {
                        throw ArithmeticException("Factorial requires non-negative integer")
                    }
                    if (arg > MAX_FACTORIAL) {
                        throw ArithmeticException("Factorial overflow (> 170!)")
                    }
                    computeFactorial(round(arg).toInt())
                }
                "pol" -> {
                    // Pol(x, y) returns r = sqrt(x^2 + y^2)
                    val (x, y) = parseTwoArguments()
                    sqrt(x * x + y * y)
                }
                "rec" -> {
                    // Rec(r, theta) returns x = r * cos(theta)
                    val (r, theta) = parseTwoArguments()
                    r * evalCos(theta)
                }
                "ran" -> Math.random()
                else -> throw IllegalArgumentException("Unknown function or identifier: '$word'")
            }
        }

        private fun parseArgument(): Double {
            return if (peek() == '(') {
                advance()
                val arg = parseExpression()
                match(')')
                arg
            } else {
                parseUnary()
            }
        }

        private fun parseTwoArguments(): Pair<Double, Double> {
            if (peek() == '(') advance()
            val first = parseExpression()
            if (match(',')) {
                val second = parseExpression()
                match(')')
                return Pair(first, second)
            }
            match(')')
            return Pair(first, 0.0)
        }

        private fun evalSin(x: Double): Double {
            return if (angleMode == AngleMode.DEG) {
                val mod360 = ((x % 360.0) + 360.0) % 360.0
                when {
                    abs(mod360) < EPSILON || abs(mod360 - 180.0) < EPSILON || abs(mod360 - 360.0) < EPSILON -> 0.0
                    abs(mod360 - 90.0) < EPSILON -> 1.0
                    abs(mod360 - 270.0) < EPSILON -> -1.0
                    else -> sin(Math.toRadians(x))
                }
            } else {
                sin(x)
            }
        }

        private fun evalCos(x: Double): Double {
            return if (angleMode == AngleMode.DEG) {
                val mod360 = ((x % 360.0) + 360.0) % 360.0
                when {
                    abs(mod360 - 90.0) < EPSILON || abs(mod360 - 270.0) < EPSILON -> 0.0
                    abs(mod360) < EPSILON || abs(mod360 - 360.0) < EPSILON -> 1.0
                    abs(mod360 - 180.0) < EPSILON -> -1.0
                    else -> cos(Math.toRadians(x))
                }
            } else {
                cos(x)
            }
        }

        private fun evalTan(x: Double): Double {
            return if (angleMode == AngleMode.DEG) {
                val mod180 = ((x % 180.0) + 180.0) % 180.0
                if (abs(mod180 - 90.0) < EPSILON) {
                    throw ArithmeticException("Domain error: tan(90°) is undefined")
                }
                when {
                    abs(mod180) < EPSILON || abs(mod180 - 180.0) < EPSILON -> 0.0
                    abs(mod180 - 45.0) < EPSILON -> 1.0
                    abs(mod180 - 135.0) < EPSILON -> -1.0
                    else -> tan(Math.toRadians(x))
                }
            } else {
                val radModPi = ((x % PI) + PI) % PI
                if (abs(radModPi - (PI / 2.0)) < EPSILON) {
                    throw ArithmeticException("Domain error: tan(π/2) is undefined")
                }
                tan(x)
            }
        }

        private fun evalAsin(x: Double): Double {
            if (x < -1.0 - EPSILON || x > 1.0 + EPSILON) {
                throw ArithmeticException("Domain error: asin argument must be in [-1, 1]")
            }
            val clamped = x.coerceIn(-1.0, 1.0)
            val rad = asin(clamped)
            return if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
        }

        private fun evalAcos(x: Double): Double {
            if (x < -1.0 - EPSILON || x > 1.0 + EPSILON) {
                throw ArithmeticException("Domain error: acos argument must be in [-1, 1]")
            }
            val clamped = x.coerceIn(-1.0, 1.0)
            val rad = acos(clamped)
            return if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
        }

        private fun evalAtan(x: Double): Double {
            val rad = atan(x)
            return if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
        }

        private fun computeFactorial(n: Int): Double {
            if (n <= 1) return 1.0
            var res = 1.0
            for (i in 2..n) {
                res *= i
            }
            return res
        }

        private fun computeNPr(n: Double, r: Double): Double {
            if (n < 0 || r < 0 || r > n) throw ArithmeticException("Domain error: invalid nPr ($n, $r)")
            val ni = round(n).toInt()
            val ri = round(r).toInt()
            var res = 1.0
            for (i in (ni - ri + 1)..ni) {
                res *= i
            }
            return res
        }

        private fun computeNCr(n: Double, r: Double): Double {
            if (n < 0 || r < 0 || r > n) throw ArithmeticException("Domain error: invalid nCr ($n, $r)")
            val ni = round(n).toInt()
            val ri = round(r).toInt()
            val k = min(ri, ni - ri)
            var res = 1.0
            for (i in 1..k) {
                res = res * (ni - i + 1) / i
            }
            return res
        }
    }
}
