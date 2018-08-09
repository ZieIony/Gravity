package tk.zielony.gravity.game

import android.content.res.Resources
import android.graphics.*
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import tk.zielony.gravity.R
import java.util.*

class SpaceShip {
    var x: Float
    var y: Float
    var vel: PointF
    var color: Int
    var colorFilter: LightingColorFilter

    constructor(x: Float, y: Float, velx: Float, vely: Float, color: Int, resources: Resources) {
        this.x = x
        this.y = y
        this.vel = PointF()
        this.color = color

        val array = FloatArray(3)
        Color.colorToHSV(color, array)
        array[2] *= 0.5f

        this.colorFilter = LightingColorFilter(0, Color.HSVToColor(1, array));
        vel.x = Math.max(-2f, Math.min(velx, 2f))
        vel.y = Math.max(-2f, Math.min(vely, 2f))
        try {
            if (ufo1 == null) {
                val width = (100 * resources.displayMetrics.density).toInt()
                val height = (100 * resources.displayMetrics.density).toInt()

                var svg = SVG.getFromResource(resources, R.raw.ufo1)
                svg.renderDPI = resources.displayMetrics.densityDpi.toFloat()
                ufo1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                var canvas = Canvas(ufo1!!)
                svg.renderToCanvas(canvas)

                svg = SVG.getFromResource(resources, R.raw.ufo2)
                svg.renderDPI = resources.displayMetrics.densityDpi.toFloat()
                ufo2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                canvas = Canvas(ufo2)
                svg.renderToCanvas(canvas)
            }
        } catch (e: SVGParseException) {
        }
    }

    fun animate() {
        vel.x += (Math.random() * 2 - 1).toFloat()
        vel.x = Math.max(-2f, Math.min(vel.x, 2f))
        vel.y += (Math.random() * 2 - 1).toFloat()
        vel.y = Math.max(-2f, Math.min(vel.y, 2f))

        x += vel.x
        y += vel.y
    }

    fun draw(canvas: Canvas) {
        canvas.save()
        canvas.translate(x, y)
        canvas.scale(0.2f, 0.2f)
        canvas.translate((-ufo1!!.width / 2).toFloat(), (-ufo1!!.height / 2).toFloat())
        paint.colorFilter = colorFilter
        canvas.drawBitmap(ufo1!!, 0f, 0f, paint)
        paint.colorFilter = null
        canvas.drawBitmap(ufo2, 0f, 0f, paint)
        canvas.restore()
    }

    companion object {
        internal var paint = Paint()
        var random = Random()
        var toRemove = ArrayList<SpaceShip>()

        var ufo1: Bitmap? = null
        var ufo2: Bitmap? = null
    }
}
