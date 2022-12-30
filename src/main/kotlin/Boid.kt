import java.util.UUID
import kotlin.random.Random

data class Boid(
    val x: Int,
    val y: Int,
    val heading: Double,
    val speed: Double,
    val id: UUID = UUID.randomUUID()
) {
    companion object {
        fun randomBoid() = Boid(
            x = Random.nextInt(Config.SCREEN_SIZE_X.value.inc()),
            y = Random.nextInt(Config.SCREEN_SIZE_Y.value.inc()),
            heading = Random.nextDouble(Constants.TWO_PI),
            speed = Random.nextDouble(
                Config.BOID_MIN_SPEED.value.toDouble(),
                Config.BOID_MAX_SPEED.value.toDouble())
        )
    }
}
