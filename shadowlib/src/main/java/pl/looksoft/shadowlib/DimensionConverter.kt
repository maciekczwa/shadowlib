package pl.looksoft.shadowlib

import android.util.DisplayMetrics
import android.util.TypedValue
import java.util.regex.Pattern

class DimensionConverter(private val metrics: DisplayMetrics) {
    // -- Initialize dimension string to constant lookup.

    companion object {
        private val DIMENSION_CONSTANT_LOOKUP = mapOf("px" to TypedValue.COMPLEX_UNIT_PX,
                "dip" to TypedValue.COMPLEX_UNIT_DIP,
                "dp" to TypedValue.COMPLEX_UNIT_DIP,
                "sp" to TypedValue.COMPLEX_UNIT_SP,
                "pt" to TypedValue.COMPLEX_UNIT_PT,
                "in" to TypedValue.COMPLEX_UNIT_IN,
                "mm" to TypedValue.COMPLEX_UNIT_MM)
        private val DIMENSION_PATTERN = Pattern.compile("^\\s*(-?\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$")
    }


    fun stringToDimensionPixelSize(dimension: String): Int {
        // -- Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
        val internalDimension = stringToInternalDimension(dimension)
        val value = internalDimension.value
        val f = TypedValue.applyDimension(internalDimension.unit, value, metrics)
        val res = (f + 0.5f).toInt()
        if (res != 0) return res
        if (value == 0f) return 0
        return if (value > 0) 1 else -1
    }

    fun stringToDimension(dimension: String): Float {
        // -- Mimics TypedValue.complexToDimension(int data, DisplayMetrics metrics).
        val internalDimension = stringToInternalDimension(dimension)
        return TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics)
    }

    private fun stringToInternalDimension(dimension: String): InternalDimension {
        // -- Match target against pattern.
        val matcher = DIMENSION_PATTERN.matcher(dimension)
        if (matcher.matches()) {
            // -- Match found.
            // -- Extract value.
            val value = java.lang.Float.valueOf(matcher.group(1)).toFloat()
            // -- Extract dimension units.
            val unit = matcher.group(3).toLowerCase()
            // -- Get Android dimension constant.
            val dimensionUnit = DIMENSION_CONSTANT_LOOKUP[unit]
            return if (dimensionUnit == null) {
                // -- Invalid format.
                throw NumberFormatException()
            } else {
                // -- Return valid dimension.
                InternalDimension(value, dimensionUnit)
            }
        } else {
            // -- Invalid format.
            throw NumberFormatException()
        }
    }

    private data class InternalDimension(internal var value: Float, internal var unit: Int)
}

