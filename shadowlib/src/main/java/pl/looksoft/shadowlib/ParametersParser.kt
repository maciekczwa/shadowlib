package pl.looksoft.shadowlib

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet

class ParametersParser(private val context: Context) {

    private val dimensionConverter = DimensionConverter(context.resources.displayMetrics)

    fun pullShadowParameters(attrs: AttributeSet?): ShadowParameters? {
        if (attrs != null) {
            return ShadowParameters(
                    readDimension(attrs, R.attr.shadowLibDx),
                    readDimension(attrs, R.attr.shadowLibDy),
                    readDimension(attrs, R.attr.shadowLibBlur),
                    readDimension(attrs, R.attr.shadowLibSpread),
                    readColor(attrs, R.attr.shadowLibColor))
        }
        return null
    }

    private fun pullValueFromElement(attrs: AttributeSet?, id: Int): String? {
        return context.resources.getResourceEntryName(id)?.let {
            attrs?.getAttributeValue(null, it)
        }
    }

    private fun pullValueFromStyle(attrs: AttributeSet?, id: Int): String? {
        if (attrs == null)
            return null
        val typedArray = context.obtainStyledAttributes(attrs, intArrayOf(id))
        if (typedArray != null) {
            try {
                // First defined attribute
                val value = typedArray.getString(0)
                if (!value.isNullOrEmpty()) {
                    return value
                }
            } catch (ignore: Exception) {
                // Failed for some reason.
            } finally {
                typedArray.recycle()
            }
        }
        return null
    }

    private fun readColor(attrs: AttributeSet?, id: Int): Int? {
        var value: String? = pullValueFromElement(attrs, id)
        if (value == null) {
            value = pullValueFromStyle(attrs, id)
        }
        return value?.let { value ->
            when {
                value.startsWith("#") -> Color.parseColor(value)
                value.startsWith("@") -> ContextCompat.getColor(context, value.substring(1).toInt())
                else -> null
            }
        }
    }

    private fun readDimension(attrs: AttributeSet?, id: Int): Float? {
        var value: String? = pullValueFromElement(attrs, id)
        if (value == null) {
            value = pullValueFromStyle(attrs, id)
        }
        return value?.let { value ->
            when {
                value.startsWith("@") -> context.resources.getDimension(value.substring(1).toInt())
                else -> dimensionConverter.stringToDimension(value)
            }
        }
    }
}

data class ShadowParameters(val shadowX: Float?, val shadowY: Float?, val shadowBlur: Float?, val shadowSpread: Float?, val shadowColor: Int?) {
    fun hasValue() = shadowX != null || shadowY != null || shadowBlur != null || shadowSpread != null || shadowColor != null
}