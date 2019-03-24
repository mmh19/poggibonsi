package it.croccio.poggibonsi.core

import android.app.Application
import it.croccio.poggibonsi.controller.PreferencesController
import it.croccio.poggibonsi.controller.WSController

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesController.applicationContext = this
        WSController.initialize(this)
    }

}