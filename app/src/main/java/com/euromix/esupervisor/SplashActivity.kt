package com.euromix.esupervisor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.euromix.esupervisor.app.Singletons
import com.yariksoffice.lingver.Lingver
import java.util.*


/**
 * Entry point of the app.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Singletons.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

}