import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import org.openrndr.math.mod
import java.util.*
import kotlin.math.*

const val TWO_PI = PI * 2

fun differenceSquared(a: Double, b: Double) = (a - b).let { it * it }

fun isWithinDistance(locA: Vector2, locB: Vector2, distance: Int): Boolean {
    val xDiffSquared = differenceSquared(locA.x, locB.x)
    val yDiffSquared = differenceSquared(locA.y, locB.y)
    return sqrt(xDiffSquared + yDiffSquared) <= distance
}

fun Boid.canSee(other: Boid) =
    isWithinDistance(location, other.location, Config.BOID_PERCEPTION_RADIUS.value)

fun List<Boid>.visibleTo(boid: Boid) = filter { boid.canSee(it) }

fun Boid.isCloseTo(other: Boid) =
    isWithinDistance(location, other.location, Config.BOID_CROWDING_RADIUS.value)

fun List<Boid>.closeTo(boid: Boid) = filter { boid.isCloseTo(it) }

fun List<Boid>.centerOf() = Vector2(
    sumOf { it.location.x } / size,
    sumOf { it.location.y } / size
)

fun Vector2.headingToward(target: Vector2) =
    (target - this).let {
        val angle = atan2(it.y, it.x)
        if (angle < 0) angle + TWO_PI else angle
    }

fun Vector2.headingAwayFrom(target: Vector2) = mod((PI + this.headingToward(target)), TWO_PI)

fun List<Double>.averageHeading() = atan2(sumOf { sin(it) }, sumOf { cos(it) })

fun Boid.separationHeading(others: List<Boid>) = location.headingAwayFrom(others.centerOf())

fun Boid.alignmentHeading(others: List<Boid>) = others.map { it.heading }.averageHeading()

fun Boid.cohesionHeading(others: List<Boid>) = location.headingToward(others.centerOf())

fun Boid.findNewHeading(otherBoids: List<Boid>) =
    otherBoids.visibleTo(this).let { visible ->
        (Collections.nCopies(Config.BOID_INERTIA.value, heading) +
         listOf(
             separationHeading(visible.closeTo(this)),
             alignmentHeading(visible),
             cohesionHeading(visible),
         )).averageHeading()
    }

/*
(defn wrap-value
  [value min-value max-value]
  (cond (> value max-value) (- value max-value)
        (< value min-value) (+ value max-value)
        :else value))

(defn move-boid // "Boid.advance()"
  [state boid]
  (let [new-heading (boid-heading state boid)
        new-x (+ (:x boid) (* (:speed boid) (quil/cos new-heading)))
        new-y (+ (:y boid) (* (:speed boid) (quil/sin new-heading)))]
    (merge boid
           {:x (wrap-value new-x 0 (quil/width))
            :y (wrap-value new-y 0 (quil/height))
            :heading new-heading})))
*/

fun List<Boid>.drawEach(drawer: Drawer) = forEach {
    drawer.circle(
        it.location.x,
        it.location.y,
        Config.BOID_SIZE.value.toDouble()
    )
}

fun main() = application {
    configure {
        width = 1024
        height = 632
        windowResizable = true
        title = "BoidsKt"
    }

    program {
        drawer.fill = ColorRGBa.PINK

        var boids = List(Config.NUM_BOIDS.value) { Boid.randomBoid() }

        extend {
            boids.drawEach(drawer)
        }
    }
}
