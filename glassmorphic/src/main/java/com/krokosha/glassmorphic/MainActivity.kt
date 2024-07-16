package com.krokosha.glassmorphic

import android.graphics.BlurMaskFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.krokosha.glassmorphic.ui.theme.Test_delete_itTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Test_delete_itTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GlassmorphismExample()
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                }
            }
        }
    }
}

@Composable
fun GlassmorphismBox(modifier: Modifier = Modifier) {
    val blurMask = BlurMaskFilter(
        30f,
        BlurMaskFilter.Blur.NORMAL
    )

    val paint = Paint().apply {

        color = Color.White.copy(alpha = 0.2f)

//        maskFilter = BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL)
    }

//    val paint = Paint().asFrameworkPaint().apply {
////        maskFilter = blurMask
//        color = android.graphics.Color.WHITE
//    }.asComposePaint()

//    val blurMask = BlurMaskFilter(
//        15f,
//        BlurMaskFilter.Blur.NORMAL
//    )
//    val radialGradient = android.graphics.RadialGradient(
//        100f, 100f, 50f,
//        intArrayOf(android.graphics.Color.WHITE, android.graphics.Color.BLACK),
//        floatArrayOf(0f, 0.9f), android.graphics.Shader.TileMode.CLAMP
//    )
//    val paint = Paint().asFrameworkPaint().apply {
//        shader = radialGradient
//        maskFilter = blurMask
//        color = android.graphics.Color.WHITE
//    }
//    drawCircle(100f, 100f, 50f, paint)

    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    canvas.drawRoundRect(0f, 0f, size.width, size.height, 16.dp.toPx(), 16.dp.toPx(), paint)
//                    canvas.drawCircle(Offset(100f), 100f, paint)
                }
            }
            .shadow(10.dp, shape = MaterialTheme.shapes.medium)
    )
}

@Composable
fun GlassmorphismExample() {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(50) { index ->
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.LightGray)
                        .padding(32.dp)
                ) {
                    androidx.compose.material3.Text(text = "Item #$index")
                }
            }
        }
        GlassmorphismBox(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize(0.5f)
        )
    }
}