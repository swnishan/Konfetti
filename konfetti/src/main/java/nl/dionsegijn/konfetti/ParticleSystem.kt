package nl.dionsegijn.konfetti

import android.graphics.Canvas
import android.graphics.Color
import android.os.Handler
import android.support.annotation.ColorInt
import nl.dionsegijn.konfetti.models.Location
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import nl.dionsegijn.konfetti.models.Vector
import java.util.*

/**
 * Created by dionsegijn on 3/26/17.
 */
class ParticleSystem(val renderer: KonfettiView) {

    private var location = Location()
    private var gravity = Vector(0f, 0.01f)
    private var wind = Vector()
    private var colors = intArrayOf(Color.RED)
    private var sizes = arrayOf(Size.SMALL)
    private var shapes = arrayOf(Shape.RECT)
    private var acceleration = Vector()

    private var degrees = doubleArrayOf(0.0)
    private var speed: Int = 0

    private var spawnDelay = 40

    private val particles: MutableList<Confetti> = mutableListOf()

    fun addConfetti(location: Location) {
        particles.add(Confetti(
                location = Vector(location.x, location.y),
                radian = getAngleAsRadian(),
                speed = speed,
                size = sizes[Random().nextInt(sizes.size)],
                shape = shapes[Random().nextInt(shapes.size)],
                color = colors[Random().nextInt(colors.size)],
                acceleration = acceleration.copy())
        )
    }

    fun addColors(@ColorInt vararg colors: Int): ParticleSystem {
        this.colors = colors
        return this
    }

    fun addSizes(vararg possibleSizes: Size): ParticleSystem {
        this.sizes = possibleSizes.filterIsInstance<Size>().toTypedArray()
        return this
    }

    fun addShapes(vararg shapes: Shape): ParticleSystem {
        this.shapes = shapes.filterIsInstance<Shape>().toTypedArray()
        return this
    }

    /* Angle in degrees */
    fun setAngle(angle: Double): ParticleSystem {
        degrees = doubleArrayOf(angle)
        return this
    }

    /* Range in degrees */
    fun setAngle(startAngle: Double, endAngle: Double): ParticleSystem {
        degrees = doubleArrayOf(startAngle, endAngle)
        return this
    }

    private fun getAngleAsRadian(): Double {
        if (degrees.size == 1) {
            return Math.toRadians(degrees[0])
        }
        else {
            // Get random angle between end angle + min angle
            val degrees = Random().nextInt(degrees[1].toInt()) + degrees[0]
            return Math.toRadians(degrees)
        }
    }

    fun setSpeed(speed: Int): ParticleSystem {
        this.speed = speed
        return this
    }

    fun wind(x: Float, y: Float): ParticleSystem {
        wind = Vector(x, y)
        return this
    }

    fun fromPoint(x: Float, y: Float): ParticleSystem {
        location.setX(x)
        location.setY(y)
        return this
    }

    fun setSpawnDelay(delay: Int) {
        this.spawnDelay = delay
    }

    fun betweenPoints(x1: Float, x2: Float, y1: Float, y2: Float): ParticleSystem {
        location.betweenX(x1, x2)
        location.betweenY(y1, y2)
        return this
    }

    fun start() {
        renderer.start(this)
    }

    var handler: Handler = Handler()

    internal fun startTimer() {
        handler.postDelayed({
            addConfetti(location)
            startTimer()
        }, spawnDelay.toLong())
    }

    fun render(canvas: Canvas) {
        val it = particles.iterator()
        while (it.hasNext()) {
            val c = it.next()
            c.applyForce(gravity)
            if (c.location.y > canvas.height * 0.3) {
                c.applyForce(wind)
            }
            c.render(canvas)
            if (c.isDead()) it.remove()
        }
    }

}
