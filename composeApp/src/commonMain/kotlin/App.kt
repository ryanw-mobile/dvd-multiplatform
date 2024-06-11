import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dvdmultiplatform.composeapp.generated.resources.Res
import dvdmultiplatform.composeapp.generated.resources.dvd_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
@Preview
fun App() {
    MaterialTheme {
        val logoPainter: Painter =
            painterResource(resource = Res.drawable.dvd_logo) // Replace with your logo resource ID

        var position by remember { mutableStateOf(Offset(100f, 100f)) }
        var velocity by remember { mutableStateOf(Offset(5f, 5f)) }
        var color by remember { mutableStateOf(Color.Red) }

        val frameMillis = 16L

        var screenWidth by remember { mutableStateOf(0f) }
        var screenHeight by remember { mutableStateOf(0f) }
        val logoSize = 100.dp

        // Convert dp to px using LocalDensity
        val density = LocalDensity.current
        val logoSizePx = with(density) { logoSize.toPx() }

        // Measure the screen size
        LaunchedEffect(Unit) {
            while (true) {
                delay(frameMillis)

                position += velocity

                if (position.x <= 0 || position.x >= screenWidth - logoSizePx) {
                    velocity = Offset(-velocity.x, velocity.y)
                    color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
                }

                if (position.y <= 0 || position.y >= screenHeight - logoSizePx) {
                    velocity = Offset(velocity.x, -velocity.y)
                    color = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
                }
            }
        }

        // Draw the logo
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = Color.Black),
        ) {
            screenWidth = constraints.maxWidth.toFloat()
            screenHeight = constraints.maxHeight.toFloat()

            Image(
                painter = logoPainter,
                contentDescription = null,
                modifier =
                    Modifier
                        .graphicsLayer(
                            translationX = position.x,
                            translationY = position.y,
                        )
                        .size(logoSize),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color),
            )
        }
    }
}
