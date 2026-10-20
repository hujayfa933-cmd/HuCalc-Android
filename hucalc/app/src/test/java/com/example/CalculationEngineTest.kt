package com.example

import com.example.calculator.engine.AngleMode
import com.example.calculator.engine.CalculationEngine
import com.example.calculator.engine.EvaluationResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculationEngineTest {

    private lateinit var engine: CalculationEngine

    @Before
    fun setUp() {
        engine = CalculationEngine()
    }

    private fun eval(expr: String, angleMode: AngleMode = AngleMode.DEG): EvaluationResult {
        return engine.evaluate(expr, angleMode)
    }

    private fun evalSuccess(expr: String, angleMode: AngleMode = AngleMode.DEG): Double {
        val res = eval(expr, angleMode)
        assertTrue("Expected success for '$expr', got: $res", res is EvaluationResult.Success)
        return (res as EvaluationResult.Success).value
    }

    @Test
    fun testBasicArithmeticAndPrecedence() {
        assertEquals(14.0, evalSuccess("2 + 3 × 4"), 1e-9)
        assertEquals(7.0, evalSuccess("10 − 6 ÷ 2"), 1e-9)
        assertEquals(20.0, evalSuccess("(2 + 3) × 4"), 1e-9)
    }

    @Test
    fun testNegativeNumbersAndDecimals() {
        assertEquals(-11.0, evalSuccess("-5.5 × 2"), 1e-9)
        assertEquals(0.3, evalSuccess("0.1 + 0.2"), 1e-9)
        assertEquals(-7.0, evalSuccess("3 + -10"), 1e-9)
    }

    @Test
    fun testNestedParentheses() {
        assertEquals(40.0, evalSuccess("(2 + 3) × (4 + (5 − 1))"), 1e-9)
        // Auto-close dangling open parentheses
        assertEquals(25.0, evalSuccess("(2 + 3) × (1 + 4"), 1e-9)
    }

    @Test
    fun testPercentages() {
        assertEquals(0.5, evalSuccess("50%"), 1e-9)
        assertEquals(20.0, evalSuccess("100 × 20%"), 1e-9)
    }

    @Test
    fun testPowersAndRoots() {
        assertEquals(8.0, evalSuccess("2^3"), 1e-9)
        assertEquals(4.0, evalSuccess("sqrt(16)"), 1e-9)
        assertEquals(3.0, evalSuccess("cbrt(27)"), 1e-9)
        assertEquals(2.0, evalSuccess("8^(1/3)"), 1e-9)
        assertEquals(512.0, evalSuccess("2^3^2"), 1e-9) // right-associative: 2^(3^2) = 2^9 = 512
    }

    @Test
    fun testLogarithms() {
        assertEquals(2.0, evalSuccess("log(100)"), 1e-9)
        assertEquals(1.0, evalSuccess("ln(e)"), 1e-9)
        val errLog = eval("log(0)")
        assertTrue("log(0) must fail with error", errLog is EvaluationResult.Error)
    }

    @Test
    fun testTrigonometryDegreeMode() {
        assertEquals(1.0, evalSuccess("sin(90)", AngleMode.DEG), 1e-9)
        assertEquals(0.0, evalSuccess("sin(180)", AngleMode.DEG), 1e-9)
        assertEquals(1.0, evalSuccess("cos(0)", AngleMode.DEG), 1e-9)
        assertEquals(0.0, evalSuccess("cos(90)", AngleMode.DEG), 1e-9)
        assertEquals(1.0, evalSuccess("tan(45)", AngleMode.DEG), 1e-9)
        assertEquals(90.0, evalSuccess("asin(1)", AngleMode.DEG), 1e-9)
        assertEquals(45.0, evalSuccess("atan(1)", AngleMode.DEG), 1e-9)

        val errTan = eval("tan(90)", AngleMode.DEG)
        assertTrue("tan(90°) must fail with domain error", errTan is EvaluationResult.Error)
    }

    @Test
    fun testTrigonometryRadianMode() {
        assertEquals(1.0, evalSuccess("sin(pi / 2)", AngleMode.RAD), 1e-9)
        assertEquals(1.0, evalSuccess("cos(0)", AngleMode.RAD), 1e-9)
    }

    @Test
    fun testFactorial() {
        assertEquals(120.0, evalSuccess("5!"), 1e-9)
        assertEquals(1.0, evalSuccess("0!"), 1e-9)
        assertEquals(1.0, evalSuccess("1!"), 1e-9)

        val errNegativeFact = eval("(-3)!")
        assertTrue("negative factorial must fail", errNegativeFact is EvaluationResult.Error)
    }

    @Test
    fun testScientificNotationExp() {
        assertEquals(2500.0, evalSuccess("2.5E3"), 1e-9)
        assertEquals(0.001, evalSuccess("1E-3"), 1e-9)
    }

    @Test
    fun testDivisionByZero() {
        val res = eval("10 ÷ 0")
        assertTrue(res is EvaluationResult.Error)
        assertEquals("Cannot divide by zero", (res as EvaluationResult.Error).message)
    }

    @Test
    fun testDomainErrors() {
        val sqrtNeg = eval("sqrt(-4)")
        assertTrue(sqrtNeg is EvaluationResult.Error)

        val asinInvalid = eval("asin(2)")
        assertTrue(asinInvalid is EvaluationResult.Error)
    }
}
