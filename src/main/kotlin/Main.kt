import org.openrndr.application
import org.openrndr.color.ColorRGBa
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun differenceSquared(a: Int, b: Int) = (a - b).let { it * it }

fun isWithinDistance(boid: Boid, otherBoid: Boid, distance: Int): Boolean {
    val xDiffSquared = differenceSquared(boid.x, otherBoid.x).toDouble()
    val yDiffSquared = differenceSquared(boid.y, otherBoid.y).toDouble()
    return sqrt(xDiffSquared + yDiffSquared).roundToInt() <= distance
}

fun isBoidVisible(boid: Boid, otherBoid: Boid) =
    isWithinDistance(boid, otherBoid, Config.BOID_PERCEPTION_RADIUS.value)

fun List<Boid>.visibleBoids(boid: Boid) = filter { isBoidVisible(boid, it) }

fun isBoidClose(boid: Boid, otherBoid: Boid) =
    isWithinDistance(boid, otherBoid, Config.BOID_CROWDING_RADIUS.value)

fun List<Boid>.closeBoids(boid: Boid) = filter { isBoidClose(boid, it) }

/*
(defn center-of
  [boids]
  (let [boid-count (count boids)]
    {:x (/ (reduce + (map :x boids)) boid-count)
     :y (/ (reduce + (map :y boids)) boid-count)}))
*/

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
                drawer.circle(
                    it.x.toDouble(),
                    it.y.toDouble(),
                    Config.BOID_SIZE.value.toDouble())
            }
        }
    }
}
