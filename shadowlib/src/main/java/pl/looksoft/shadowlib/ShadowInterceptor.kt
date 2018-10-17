package pl.looksoft.shadowlib

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor


class ShadowsInterceptor(private val context: Context, private val logger: ShadowsLogger = EmptyLogger) : Interceptor {

    init {
        LOGGER = logger
    }

    private val parametersParser = ParametersParser(context)

    override fun intercept(chain: Interceptor.Chain): InflateResult {
        logger.log("current view: ${chain.request().name()}")
        val parameters = parametersParser.pullShadowParameters(chain.request().attrs())
        if (parameters?.hasValue() == true) {
            logger.log("parameters: $parameters")
            val result = chain.proceed(chain.request())
            val inside = result.view()
            if (inside != null) {
                val sl = ShadowLayout(context, chain.request().attrs())
                val parent = chain.request().parent()
                val insideLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                if(parent is ViewGroup) {
                    var params = parent.generateLayoutParams(chain.request().attrs())
                    if (params.width == 0) {
                        logger.log("width is 0 setting inside to MATCH_PARENT")
                        insideLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    } else if (params.width != ViewGroup.LayoutParams.MATCH_PARENT) {
                        logger.log("width is not MATCH_PARENT setting inside to ${params.width}, setting outside to WRAP_CONTENT")
                        insideLayoutParams.width = params.width
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    if (params.height == 0) {
                        logger.log("height is 0 setting inside to MATCH_PARENT")
                        insideLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    } else if (params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
                        logger.log("height is not MATCH_PARENT setting inside to ${params.width}, setting outside to WRAP_CONTENT")
                        insideLayoutParams.height = params.height
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                    logger.log("sl params: $params ${params.width} ${params.height}")
                    sl.layoutParams = params
                    // the layout params will be changed to the original one by enclosing layout, we need to make sure that we keep our LayoutParams
                    sl.blockLayoutParams = true
                }
                sl.background = null
                sl.addView(inside, insideLayoutParams)
                sl.shadowDx = (parameters.shadowX ?: 0.0f)
                sl.shadowDy = (parameters.shadowY ?: 0.0f)
                sl.shadowColor = parameters.shadowColor ?: 0
                sl.shadowSpread = (parameters.shadowSpread ?: 0.0f)
                sl.shadowBlur = (parameters.shadowBlur ?: 0.0f)
                return InflateResult.builder().attrs(chain.request().attrs())
                        .context(context)
                        .name(sl.javaClass.name)
                        .view(sl)
                        .build()
            } else {
                return result
            }
        } else {
            return chain.proceed(chain.request())
        }

    }
}

var LOGGER: ShadowsLogger = object : ShadowsLogger {
    override fun log(text: String) {

    }

}