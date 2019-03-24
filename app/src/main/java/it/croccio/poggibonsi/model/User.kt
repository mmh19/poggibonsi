package it.croccio.poggibonsi.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import it.croccio.poggibonsi.controller.PreferencesController

var _loggedUser: User? = null
var loggedUser: User
    set(value) {
        _loggedUser = value
        PreferencesController.loggedUser = value
    }
    get() {
        return if (_loggedUser == null || _loggedUser!!._id?.length == 0) {
            _loggedUser = PreferencesController.loggedUser
            _loggedUser!!
        } else {
            _loggedUser!!
        }
    }

class User {

    var _id: String? = ""
    var firstName: String? = ""
    var lastName: String? = ""
    var email: String? = ""
    var available: Boolean? = false
    var ranking: Int = 0
    var stats: Stats? = null

}

class Stats {
    var matches = 0
    var goals = 0
    var ranking = 0.0f
}