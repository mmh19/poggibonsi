package it.croccio.poggibonsi.controller

import android.content.Context
import es.dmoral.prefs.Prefs
import it.croccio.poggibonsi.model.User

object PreferencesController {

    //fixme: remove context reference in singleton object
    var applicationContext: Context? = null

    var token: String?
        get() = Prefs.with(applicationContext!!).read("context", null)
        set(value) = Prefs.with(applicationContext!!).write("context", value)


    var loggedUser: User
        get() {
            val user = User()
            user._id = Prefs.with(applicationContext!!).read("_id")
            user.lastName = Prefs.with(applicationContext!!).read("lastName")
            user.email = Prefs.with(applicationContext!!).read("email")
            user.firstName = Prefs.with(applicationContext!!).read("firstName")
            user.available = Prefs.with(applicationContext!!).readBoolean("available", false)
            user.ranking = Prefs.with(applicationContext!!).readInt("ranking")

            return user
        }
        set(value) {
            Prefs.with(applicationContext!!).write("_id", value._id)
            Prefs.with(applicationContext!!).write("email", value.email)
            Prefs.with(applicationContext!!).write("firstName", value.firstName)
            Prefs.with(applicationContext!!).write("lastName", value.lastName)
            Prefs.with(applicationContext!!).writeBoolean("available", value.available!!)
            Prefs.with(applicationContext!!).writeInt("ranking", value.ranking!!)
        }




}