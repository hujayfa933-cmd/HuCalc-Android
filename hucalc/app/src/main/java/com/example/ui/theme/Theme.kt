package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    SILVER_ALUMINUM, // Classic Brushed Silver (Default)
    DARK_PLATINUM,   // Dark Silver Chrome
    SYSTEM
}

@Immutable
data class CalculatorColors(
    val chassisTop: Color,
    val chassisMid: Color,
    val chassisBottom: Color,
    val chassisHighlight: Color,
    val chassisShadow: Color,

    val screwHead: Color,
    val screwSlot: Color,
    val screwRim: Color,

    val lcdBezelOuter: Color,
    val lcdBezelInner: Color,
    val lcdBackground: Color,
    val lcdTextPrimary: Color,
    val lcdTextSecondary: Color,
    val lcdTextDim: Color,

    val keyNumberTop: Color,
    val keyNumberBottom: Color,
    val keyNumberRim: Color,
    val keyNumberShadow: Color,
    val keyNumberText: Color,

    val keyFunctionTop: Color,
    val keyFunctionBottom: Color,
    val keyFunctionRim: Color,
    val keyFunctionShadow: Color,
    val keyFunctionText: Color,

    val keyScientificTop: Color,
    val keyScientificBottom: Color,
    val keyScientificRim: Color,
    val keyScientificShadow: Color,
    val keyScientificText: Color,

    val keyOperatorTop: Color,
    val keyOperatorBottom: Color,
    val keyOperatorRim: Color,
    val keyOperatorShadow: Color,
    val keyOperatorText: Color,

    val keyClearTop: Color,
    val keyClearBottom: Color,
    val keyClearRim: Color,
    val keyClearShadow: Color,

    val keyEqualsTop: Color,
    val keyEqualsBottom: Color,
    val keyEqualsRim: Color,
    val keyEqualsShadow: Color,

    val secondaryLabelGold: Color,
    val isDark: Boolean
) {
    // Brushed silver chassis linear gradient
    val chassisBrush: Brush
        get() = Brush.verticalGradient(
            listOf(chassisTop, chassisMid, chassisBottom)
        )

    val bodyBackground: Color get() = chassisMid
    val bodyBevel: Color get() = chassisHighlight
    val background: Color get() = chassisMid
    val surface: Color get() = lcdBezelOuter
    val surfaceVariant: Color get() = chassisHighlight
    val textPrimary: Color get() = keyNumberText
    val textSecondary: Color get() = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    val keyNumber: Color get() = keyNumberBottom
    val keyFunction: Color get() = keyFunctionBottom
    val keyScientific: Color get() = keyScientificBottom
    val keyOperator: Color get() = keyOperatorTop
    val keyClear: Color get() = keyClearTop
    val keyEquals: Color get() = keyEqualsTop

    val buttonOperator: Color get() = keyOperatorTop
    val buttonOperatorText: Color get() = keyOperatorText
    val buttonEquals: Color get() = keyEqualsTop
    val buttonEqualsText: Color get() = Color.White
    val buttonNumber: Color get() = keyNumberBottom
    val buttonNumberText: Color get() = keyNumberText
    val buttonFunction: Color get() = keyFunctionBottom
    val buttonFunctionText: Color get() = keyFunctionText
    val buttonScientific: Color get() = keyScientificBottom
    val buttonScientificText: Color get() = keyScientificText
    val lcdFrame: Color get() = lcdBezelOuter
}

val LocalCalculatorColors = staticCompositionLocalOf {
    createSilverColors()
}

// Pure Brushed Silver Aluminum Theme
fun createSilverColors() = CalculatorColors(
    chassisTop = SilverChassisTop,
    chassisMid = SilverChassisMid,
    chassisBottom = SilverChassisBottom,
    chassisHighlight = SilverChassisHighlight,
    chassisShadow = SilverChassisShadow,

    screwHead = SilverScrewHead,
    screwSlot = SilverScrewSlot,
    screwRim = SilverScrewRim,

    lcdBezelOuter = SilverLcdBezelOuter,
    lcdBezelInner = SilverLcdBezelInner,
    lcdBackground = SilverLcdScreen,
    lcdTextPrimary = SilverLcdTextPrimary,
    lcdTextSecondary = SilverLcdTextSecondary,
    lcdTextDim = SilverLcdTextDim,

    keyNumberTop = SilverKeyNumberTop,
    keyNumberBottom = SilverKeyNumberBottom,
    keyNumberRim = SilverKeyNumberRim,
    keyNumberShadow = SilverKeyNumberShadow,
    keyNumberText = SilverKeyNumberText,

    keyFunctionTop = SilverKeyFunctionTop,
    keyFunctionBottom = SilverKeyFunctionBottom,
    keyFunctionRim = SilverKeyFunctionRim,
    keyFunctionShadow = SilverKeyFunctionShadow,
    keyFunctionText = SilverKeyFunctionText,

    keyScientificTop = SilverKeyScientificTop,
    keyScientificBottom = SilverKeyScientificBottom,
    keyScientificRim = SilverKeyScientificRim,
    keyScientificShadow = SilverKeyScientificShadow,
    keyScientificText = SilverKeyScientificText,

    keyOperatorTop = SilverKeyOperatorTop,
    keyOperatorBottom = SilverKeyOperatorBottom,
    keyOperatorRim = SilverKeyOperatorRim,
    keyOperatorShadow = SilverKeyOperatorShadow,
    keyOperatorText = Color.White,

    keyClearTop = SilverKeyClearTop,
    keyClearBottom = SilverKeyClearBottom,
    keyClearRim = SilverKeyClearRim,
    keyClearShadow = SilverKeyClearShadow,

    keyEqualsTop = SilverKeyEqualsTop,
    keyEqualsBottom = SilverKeyEqualsBottom,
    keyEqualsRim = SilverKeyEqualsRim,
    keyEqualsShadow = SilverKeyEqualsShadow,

    secondaryLabelGold = KeySecondaryLabelGold,
    isDark = false
)

// Dark Platinum / Gunmetal Silver Theme
fun createDarkSilverColors() = CalculatorColors(
    chassisTop = DarkSilverChassisTop,
    chassisMid = DarkSilverChassisMid,
    chassisBottom = DarkSilverChassisBottom,
    chassisHighlight = DarkSilverChassisHighlight,
    chassisShadow = DarkSilverChassisShadow,

    screwHead = DarkSilverScrewHead,
    screwSlot = DarkSilverScrewSlot,
    screwRim = DarkSilverScrewRim,

    lcdBezelOuter = DarkSilverLcdBezelOuter,
    lcdBezelInner = DarkSilverLcdBezelInner,
    lcdBackground = DarkSilverLcdScreen,
    lcdTextPrimary = DarkSilverLcdTextPrimary,
    lcdTextSecondary = DarkSilverLcdTextSecondary,
    lcdTextDim = DarkSilverLcdTextDim,

    keyNumberTop = DarkSilverKeyNumberTop,
    keyNumberBottom = DarkSilverKeyNumberBottom,
    keyNumberRim = DarkSilverKeyNumberRim,
    keyNumberShadow = DarkSilverKeyNumberShadow,
    keyNumberText = DarkSilverKeyNumberText,

    keyFunctionTop = DarkSilverKeyFunctionTop,
    keyFunctionBottom = DarkSilverKeyFunctionBottom,
    keyFunctionRim = DarkSilverKeyFunctionRim,
    keyFunctionShadow = DarkSilverKeyFunctionShadow,
    keyFunctionText = DarkSilverKeyFunctionText,

    keyScientificTop = DarkSilverKeyScientificTop,
    keyScientificBottom = DarkSilverKeyScientificBottom,
    keyScientificRim = DarkSilverKeyScientificRim,
    keyScientificShadow = DarkSilverKeyScientificShadow,
    keyScientificText = DarkSilverKeyScientificText,

    keyOperatorTop = SilverKeyOperatorTop,
    keyOperatorBottom = SilverKeyOperatorBottom,
    keyOperatorRim = SilverKeyOperatorRim,
    keyOperatorShadow = SilverKeyOperatorShadow,
    keyOperatorText = Color.White,

    keyClearTop = SilverKeyClearTop,
    keyClearBottom = SilverKeyClearBottom,
    keyClearRim = SilverKeyClearRim,
    keyClearShadow = SilverKeyClearShadow,

    keyEqualsTop = SilverKeyEqualsTop,
    keyEqualsBottom = SilverKeyEqualsBottom,
    keyEqualsRim = SilverKeyEqualsRim,
    keyEqualsShadow = SilverKeyEqualsShadow,

    secondaryLabelGold = KeySecondaryLabelGoldDark,
    isDark = true
)

private val SilverColorScheme = lightColorScheme(
    primary = SilverKeyEqualsTop,
    onPrimary = Color.White,
    secondary = SilverKeyOperatorTop,
    onSecondary = Color.White,
    background = SilverChassisMid,
    surface = SilverLcdBezelOuter,
    surfaceVariant = SilverChassisHighlight,
    onBackground = SilverKeyNumberText,
    onSurface = SilverKeyNumberText
)

private val DarkSilverColorScheme = darkColorScheme(
    primary = SilverKeyEqualsTop,
    onPrimary = Color.White,
    secondary = SilverKeyOperatorTop,
    onSecondary = Color.White,
    background = DarkSilverChassisMid,
    surface = DarkSilverLcdBezelOuter,
    surfaceVariant = DarkSilverChassisHighlight,
    onBackground = DarkSilverKeyNumberText,
    onSurface = DarkSilverKeyNumberText
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SILVER_ALUMINUM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SILVER_ALUMINUM -> false
        ThemeMode.DARK_PLATINUM -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val calculatorColors = if (isDark) createDarkSilverColors() else createSilverColors()
    val colorScheme = if (isDark) DarkSilverColorScheme else SilverColorScheme

    CompositionLocalProvider(LocalCalculatorColors provides calculatorColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
