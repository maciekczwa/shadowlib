package pl.looksoft.shadowlib

import android.content.Context
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor


class ShadowsInterceptor(private val context: Context, private val logger: ShadowsLogger = EmptyLogger) : Interceptor {

    private val parametersParser = ParametersParser(context)

    override fun intercept(chain: Interceptor.Chain): InflateResult {
        logger.log("current view: ${chain.request().name()}")
        if (chain.request().name() == "pl.looksoft.shadowslib.ShadowLayout")
            logger.log("breakpoint")
        val parameters = parametersParser.pullShadowParameters(chain.request().attrs())
        if (parameters?.hasValue() == true) {
            logger.log("parameters: $parameters")
            val result = chain.proceed(chain.request())
            val inside = result.view()
            if (inside != null) {
                val sl = ShadowLayout(context, chain.request().attrs())
                sl.addView(inside)
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