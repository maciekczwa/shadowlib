package pl.looksoft.shadows

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
