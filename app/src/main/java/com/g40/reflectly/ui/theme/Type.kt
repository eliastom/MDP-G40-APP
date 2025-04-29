package com.g40.reflectly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import com.g40.reflectly.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.inter_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.inter_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

val InterDisplay = FontFamily(
    Font(R.font.interdisplay_regular, FontWeight.Normal),
    Font(R.font.interdisplay_medium, FontWeight.Medium),
    Font(R.font.interdisplay_bold, FontWeight.Bold),
    Font(R.font.interdisplay_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.interdisplay_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.interdisplay_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

val Typography = Typography(
    displayLarge = TextStyle( // Big splash titles
        fontFamily = InterDisplay,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp
    ),
    headlineSmall = TextStyle( // Section headers
        fontFamily = InterDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    )
)
