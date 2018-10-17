package pl.looksoft.shadows

import android.app.Application
import android.util.Log
import io.github.inflationx.viewpump.ViewPump
import pl.looksoft.shadowlib.ShadowsInterceptor
import pl.looksoft.shadowlib.ShadowsLogger


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ViewPump.init(ViewPump.builder()
                .addInterceptor(ShadowsInterceptor(this, object : ShadowsLogger {
                    override fun log(text: String) {
                        Log.d("SHADOWS", text)
                    }
                }))
                .build())
    }
}