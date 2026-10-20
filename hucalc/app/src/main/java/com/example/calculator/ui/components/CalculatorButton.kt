package com.example.calculator.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCalculatorColors

enum class CalculatorButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    SCIENTIFIC,
    CLEAR,
    EQUALS,
    ACCENT_TOGGLE
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: CalculatorButtonType = CalculatorButtonType.NUMBER,
    secondaryText: String? = null,
    isSecondaryActive: Boolean = false,
    contentDesc: String = text,
    fontSize: TextUnit = 20.sp,
    isActiveToggle: Boolean = false,
    cornerRadius: Dp = 2.dp
) {
    val colors = LocalCalculatorColors.current
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth physical scissor/dome switch travel
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else 0.dp,
        animationSpec = tween(durationMillis = 40),
        label = "polished_metal_press"
    )

    // Keycap surfaces, chamfers, and depth shadows
    val (topColor, bottomColor, rimColor, shadowColor, textColor) = when (type) {
        CalculatorButtonType.NUMBER -> {
            Tuple5(colors.keyNumberTop, colors.keyNumberBottom, colors.keyNumberRim, colors.keyNumberShadow, colors.keyNumberText)
        }
        CalculatorButtonType.OPERATOR -> {
            Tuple5(colors.keyOperatorTop, colors.keyOperatorBottom, colors.keyOperatorRim, colors.keyOperatorShadow, colors.keyOperatorText)
        }
        CalculatorButtonType.FUNCTION -> {
            Tuple5(colors.keyFunctionTop, colors.keyFunctionBottom, colors.keyFunctionRim, colors.keyFunctionShadow, colors.keyFunctionText)
        }
        CalculatorButtonType.SCIENTIFIC -> {
            if (isActiveToggle) {
                Tuple5(colors.keyOperatorTop, colors.keyOperatorBottom, colors.keyOperatorRim, colors.keyOperatorShadow, colors.keyOperatorText)
            } else {
                Tuple5(colors.keyScientificTop, colors.keyScientificBottom, colors.keyScientificRim, colors.keyScientificShadow, colors.keyScientificText)
            }
        }
        CalculatorButtonType.CLEAR -> {
            Tuple5(colors.keyClearTop, colors.keyClearBottom, colors.keyClearRim, colors.keyClearShadow, Color.White)
        }
        CalculatorButtonType.EQUALS -> {
            Tuple5(colors.keyEqualsTop, colors.keyEqualsBottom, colors.keyEqualsRim, colors.keyEqualsShadow, Color.White)
        }
        CalculatorButtonType.ACCENT_TOGGLE -> {
            if (isActiveToggle) {
                Tuple5(colors.keyOperatorTop, colors.keyOperatorBottom, colors.keyOperatorRim, colors.keyOperatorShadow, colors.keyOperatorText)
            } else {
                Tuple5(colors.keyFunctionTop, colors.keyFunctionBottom, colors.keyFunctionRim, colors.keyFunctionShadow, colors.keyFunctionText)
            }
        }
    }

    val sanitizedTag = "btn_" + text.replace(" ", "_")
        .replace("(", "open_paren")
        .replace(")", "close_paren")
        .replace("÷", "div")
        .replace("×", "mul")
        .replace("−", "sub")
        .replace("+", "add")
        .replace("=", "equals")
        .replace(".", "dot")
        .replace("%", "percent")
        .replace("!", "fact")
        .replace("^", "pow")
        .replace("√", "sqrt")
        .replace("π", "pi")

    val keyBrush = Brush.verticalGradient(
        listOf(
            if (isPressed) bottomColor else topColor,
            bottomColor
        )
    )

    // Molded Aluminum Key Well
    Box(
        modifier = modifier
            .semantics { contentDescription = contentDesc }
            .testTag(sanitizedTag)
            .padding(horizontal = 1.5.dp, vertical = 1.5.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(shadowColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
    ) {
        // High-precision Machined Keycap with responsive constraints
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = pressOffset,
                    bottom = if (isPressed) 0.5.dp else 1.6.dp
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(keyBrush)
                .border(0.8.dp, rimColor.copy(alpha = if (isPressed) 0.4f else 0.85f), RoundedCornerShape(cornerRadius))
                .drawBehind {
                    // Specular reflection line along top polished chamfer
                    val highlightAlpha = if (isPressed) 0.15f else (if (colors.isDark) 0.35f else 0.7f)
                    drawLine(
                        color = Color.White.copy(alpha = highlightAlpha),
                        start = Offset(3f, 1.2f),
                        end = Offset(size.width - 3f, 1.2f),
                        strokeWidth = 1.6f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val isCompactHeight = maxHeight < 44.dp

            if (secondaryText != null) {
                val secFontSize = if (isCompactHeight) 8.sp else 9.sp
                val secLineHeight = if (isCompactHeight) 9.sp else 11.sp
                val primFontSize = if (isCompactHeight) {
                    (fontSize.value * 0.78f).coerceIn(10.5f, 16f).sp
                } else {
                    (fontSize.value * 0.88f).coerceIn(12f, 19f).sp
                }
                val primLineHeight = if (isCompactHeight) 12.sp else 16.sp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 1.dp, vertical = 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = secondaryText,
                        color = if (isSecondaryActive) colors.secondaryLabelGold else colors.secondaryLabelGold.copy(alpha = 0.9f),
                        fontSize = secFontSize,
                        lineHeight = secLineHeight,
                        fontWeight = if (isSecondaryActive) FontWeight.ExtraBold else FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        style = androidx.compose.ui.text.TextStyle(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = text,
                        color = textColor,
                        fontSize = primFontSize,
                        lineHeight = primLineHeight,
                        fontWeight = when (type) {
                            CalculatorButtonType.NUMBER, CalculatorButtonType.OPERATOR,
                            CalculatorButtonType.EQUALS, CalculatorButtonType.CLEAR -> FontWeight.Bold
                            else -> FontWeight.SemiBold
                        },
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        style = androidx.compose.ui.text.TextStyle(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            } else {
                val primFontSize = if (isCompactHeight && fontSize.value > 17f) {
                    (fontSize.value * 0.88f).sp
                } else {
                    fontSize
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = textColor,
                        fontSize = primFontSize,
                        lineHeight = primFontSize,
                        fontWeight = when (type) {
                            CalculatorButtonType.NUMBER, CalculatorButtonType.OPERATOR,
                            CalculatorButtonType.EQUALS, CalculatorButtonType.CLEAR -> FontWeight.Bold
                            else -> FontWeight.SemiBold
                        },
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        style = androidx.compose.ui.text.TextStyle(
                            platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
