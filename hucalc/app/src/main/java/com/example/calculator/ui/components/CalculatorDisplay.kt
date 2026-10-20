package com.example.calculator.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.engine.AngleMode
import com.example.calculator.ui.CalculatorMode
import com.example.ui.theme.ChromeMirrorHighlight
import com.example.ui.theme.GlassGlareHighlight
import com.example.ui.theme.LocalCalculatorColors
import com.example.ui.theme.SolarCellBase
import com.example.ui.theme.SolarCellGrid
import com.example.ui.theme.SolarCellHighlight

@Composable
fun CalculatorDisplay(
    displayValue: String,
    expression: String,
    previousExpression: String,
    mode: CalculatorMode,
    angleMode: AngleMode,
    isInverse: Boolean,
    isAlpha: Boolean = false,
    isError: Boolean,
    onToggleAngleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCalculatorColors.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val mainScrollState = rememberScrollState()
    val secondaryScrollState = rememberScrollState()

    LaunchedEffect(displayValue) {
        mainScrollState.scrollTo(mainScrollState.maxValue)
    }
    LaunchedEffect(expression) {
        secondaryScrollState.scrollTo(secondaryScrollState.maxValue)
    }

    val copyToClipboard = {
        val textToCopy = if (isError || displayValue.isBlank()) expression else displayValue
        if (textToCopy.isNotBlank()) {
            clipboardManager.setText(AnnotatedString(textToCopy))
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    val polishedBezelBrush = Brush.verticalGradient(
        listOf(
            ChromeMirrorHighlight.copy(alpha = 0.85f),
            colors.lcdBezelOuter,
            colors.chassisMid,
            colors.lcdBezelInner
        )
    )

    // Mirror-Polished Machined Aluminum Outer Frame
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(polishedBezelBrush)
            .border(1.2.dp, colors.chassisHighlight.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Consolidated Compact Header: CASIO fx-991ES PLUS / HuCalc DESK + V.P.A.M. + Solar Cell Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    MachinedScrewRivet()
                    Text(
                        text = "CASIO",
                        color = colors.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = if (mode == CalculatorMode.SCIENTIFIC) "fx-991ES PLUS" else "HuCalc DESK",
                        color = colors.textPrimary.copy(alpha = 0.9f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.SansSerif
                    )
                    if (mode == CalculatorMode.SCIENTIFIC) {
                        Text(
                            text = "NATURAL-V.P.A.M.",
                            color = Color(0xFF2563EB),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    SolarCellStrip()
                    MachinedScrewRivet()
                }
            }

            // Recessed Mineral Glass LCD Window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(9.dp))
                    .background(colors.lcdBackground)
                    .border(1.dp, colors.lcdBezelInner, RoundedCornerShape(9.dp))
                    .drawBehind {
                        // Inset drop shadow from bezel edges
                        drawLine(
                            color = Color.Black.copy(alpha = 0.38f),
                            start = Offset(0f, 1f),
                            end = Offset(size.width, 1f),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.Black.copy(alpha = 0.18f),
                            start = Offset(1f, 0f),
                            end = Offset(1f, size.height),
                            strokeWidth = 2f
                        )
                        // Diagonal glass lens reflection glare
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    GlassGlareHighlight,
                                    Color.Transparent,
                                    GlassGlareHighlight.copy(alpha = 0.04f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, size.height * 0.8f)
                            )
                        )
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // fx-991ES PLUS LCD Annunciator Row: [ S ] [ A ] [ M ] [ D / R ] [ Math ]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // [ S ] Shift annunciator
                            Text(
                                text = "S",
                                color = if (isInverse) colors.lcdTextPrimary else colors.lcdTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )

                            // [ A ] Alpha annunciator
                            Text(
                                text = "A",
                                color = if (isAlpha) colors.lcdTextPrimary else colors.lcdTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )

                            // [ M ] Memory annunciator
                            Text(
                                text = "M",
                                color = colors.lcdTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            // [ D ] Degrees indicator
                            Text(
                                text = "D",
                                color = if (angleMode == AngleMode.DEG) colors.lcdTextPrimary else colors.lcdTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable(onClick = onToggleAngleMode)
                                    .testTag("badge_angle_mode")
                            )

                            // [ R ] Radians indicator
                            Text(
                                text = "R",
                                color = if (angleMode == AngleMode.RAD) colors.lcdTextPrimary else colors.lcdTextDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.clickable(onClick = onToggleAngleMode)
                            )

                            // [ Math ] Natural-V.P.A.M. indicator
                            Text(
                                text = "Math",
                                color = colors.lcdTextPrimary.copy(alpha = 0.85f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        IconButton(
                            onClick = copyToClipboard,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("button_copy_result")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy result",
                                tint = colors.lcdTextSecondary.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Natural Display Line 1: Formula Expression
                    val subText = when {
                        previousExpression.isNotBlank() -> previousExpression
                        expression.isNotBlank() && expression != displayValue -> expression
                        else -> ""
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(secondaryScrollState),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = if (subText.isNotBlank()) subText else " ",
                            color = colors.lcdTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            textAlign = TextAlign.End,
                            style = androidx.compose.ui.text.TextStyle(
                                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            modifier = Modifier.testTag("calculator_secondary_display")
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Natural Display Line 2: Large Calculation Result
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(mainScrollState)
                            .clickable(onClick = copyToClipboard),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        val textSize = when {
                            displayValue.length > 15 -> 22.sp
                            displayValue.length > 11 -> 26.sp
                            else -> 32.sp
                        }

                        Text(
                            text = displayValue,
                            color = if (isError) MaterialTheme.colorScheme.error else colors.lcdTextPrimary,
                            fontSize = textSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.8.sp,
                            maxLines = 1,
                            textAlign = TextAlign.End,
                            style = androidx.compose.ui.text.TextStyle(
                                platformStyle = androidx.compose.ui.text.PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            modifier = Modifier.testTag("calculator_display")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Precision mirror-polished screw rivet with recessed cross slot
 */
@Composable
fun MachinedScrewRivet(modifier: Modifier = Modifier) {
    val colors = LocalCalculatorColors.current

    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        colors.screwHead,
                        colors.chassisShadow
                    )
                )
            )
            .border(0.6.dp, colors.screwRim, CircleShape)
            .drawBehind {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val slotLen = size.width * 0.36f
                drawLine(
                    color = colors.screwSlot,
                    start = Offset(cx - slotLen, cy),
                    end = Offset(cx + slotLen, cy),
                    strokeWidth = 1.2f
                )
                drawLine(
                    color = colors.screwSlot,
                    start = Offset(cx, cy - slotLen),
                    end = Offset(cx, cy + slotLen),
                    strokeWidth = 1.2f
                )
            }
    )
}

@Composable
private fun SolarCellStrip() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(SolarCellBase)
            .border(0.8.dp, SolarCellHighlight, RoundedCornerShape(3.dp))
            .padding(horizontal = 3.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) {
            Box(
                modifier = Modifier
                    .size(width = 11.dp, height = 11.dp)
                    .background(SolarCellGrid)
            )
        }
    }
}
