package it.croccio.poggibonsi.controller

import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.model.User
import it.croccio.poggibonsi.model.loggedUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SignupController {

    fun signup(email: String, name: String, surname: String, image: String?, callback: SignupCallback) {
        var user = User()
        user.firstName = name
        user.lastName = surname
        user.email = email

        RestClient.service.signup(user).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.onSignupError()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                loggedUser = response.body()!!
                PreferencesController.token = response.body()!!._id
                callback.onSignupSuccess()
            }

        })
    }


}

interface SignupCallback {
    fun onSignupSuccess()
    fun onSignupError()
}