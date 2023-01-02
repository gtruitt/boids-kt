import org.openrndr.math.Vector2
import java.util.*
import kotlin.random.Random

data class Boid(
    val location: Vector2,
    val heading: Double,
    val speed: Double,
    val id: UUID = UUID.randomUUID()
) {
    companion object {
        fun randomBoid() = Boid(
            location = Vector2(
                Random.nextDouble(Config.SCREEN_SIZE_X.value.toDouble()),
                Random.nextDouble(Config.SCREEN_SIZE_Y.value.toDouble())
            ),
            heading = Random.nextDouble(TWO_PI),
            speed = Random.nextDouble(
                Config.BOID_MIN_SPEED.value.toDouble(),
                Config.BOID_MAX_SPEED.value.toDouble()
            )
        )
    }
}
