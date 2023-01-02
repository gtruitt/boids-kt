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

fun alignmentHeading(boids: List<Boid>) = boids.map { it.heading }.averageHeading()

fun Boid.cohesionHeading(others: List<Boid>) = location.headingToward(others.centerOf())

fun Boid.findNewHeading(others: List<Boid>) =
    others.visibleTo(this).let { neighbors ->
        (listOf(
            separationHeading(neighbors.closeTo(this)),
            alignmentHeading(neighbors),
            cohesionHeading(neighbors),
        ) + Collections.nCopies(Config.BOID_INERTIA.value, heading)
        ).averageHeading()
    }

fun wrapToBounds(value: Double, floor: Int, ceiling: Int) =
    when {
        value > ceiling -> value - ceiling
        value < 0 -> value + ceiling
        else -> value
    }

fun Boid.advance(others: List<Boid>, fieldSizeX: Int, fieldSizeY: Int) =
    findNewHeading(others).let { newHeading ->
        Boid(
            Vector2(
                wrapToBounds(
                    location.x + (speed * cos(newHeading)),
                    0,
                    fieldSizeX
                ),
                wrapToBounds(
                    location.y + (speed * sin(newHeading)),
                    0,
                    fieldSizeY
                )
            ),
            newHeading,
            speed,
            id
        )
    }

fun Drawer.configure() = also {
    it.clear(ColorRGBa.PINK)
    it.fill = ColorRGBa.WHITE
    it.stroke = ColorRGBa.BLACK
    it.strokeWeight = 1.0
}

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
        height = 633
        windowResizable = true
        title = "BoidsKt"
    }
    program {
        var boids = List(Config.NUM_BOIDS.value) { Boid.randomBoid() }
        extend {
            drawer.configure()
            boids = boids.map { it.advance(boids, width, height) }
            boids.drawEach(drawer)
        }
    }
}
