package com.rtnfacelandmarker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp  

@Composable
fun FaceLandmarkerText() {
    Text(
        text = "Hello from Compose",
        style = TextStyle(
            color = Color.Red,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

