import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.tint
import kotlin.math.cos
import kotlin.math.sin

fun main() = application {
    configure {
        width = 1024
        height = 632
        windowResizable = true
        title = "BoidsKt"
    }

    program {
        var boids = List(Config.NUM_BOIDS.value) { Boid.randomBoid() }

        extend {
            drawer.fill = ColorRGBa.PINK

            boids.forEach {
                drawer.circle(it.x.toDouble(), it.y.toDouble(), Config.BOID_SIZE.value.toDouble())
            }
        }
    }
}
