package com.andreast.taskstodo.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.andreast.taskstodo.R

private val RobotoRegular = Font(R.font.roboto_regular, FontWeight.Normal)
private val RobotoItalic = Font(R.font.roboto_italic, FontWeight.Normal)
private val RobotoBold = Font(R.font.roboto_bold, FontWeight.Bold)
private val RobotoBoldItalic = Font(R.font.roboto_bold_italic, FontWeight.Bold)

private val RobotoFontFamily = FontFamily(RobotoRegular, RobotoItalic, RobotoBold, RobotoBoldItalic)

private val DefaultTextStyle = TextStyle.Default.copy(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    ),
)

val Typography = Typography(
    displayLarge = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 57.sp,
        lineHeight = 64.0.sp,
        letterSpacing = (-0.2).sp,
    ),
    displayMedium = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 45.sp,
        lineHeight = 52.0.sp,
        letterSpacing = 0.0.sp,
    ),
    displaySmall = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 36.sp,
        lineHeight = 44.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineLarge = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineMedium = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 28.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineSmall = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 24.sp,
        lineHeight = 32.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleLarge = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 22.sp,
        lineHeight = 28.0.sp,
        letterSpacing = 0.3.sp,
    ),
    titleMedium = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 18.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.2.sp,
    ),
    titleSmall = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 15.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.2.sp,
    ),
    bodyLarge = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.2.sp
    ),
    bodyMedium = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.2.sp,
    ),
    bodySmall = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.2.sp,
    ),
    labelLarge = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = DefaultTextStyle.copy(
        fontFamily = RobotoFontFamily,
        fontWeight = RobotoRegular.weight,
        fontSize = 11.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.5.sp,
    )
)