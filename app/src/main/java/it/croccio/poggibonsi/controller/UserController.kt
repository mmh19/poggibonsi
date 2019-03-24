package it.croccio.poggibonsi.controller

import android.os.CountDownTimer
import android.util.Log
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

object UserController {

    fun users(callback: UserCallback) {

        val timer = Timer()
        Log.e("#####", "UserController.users.call")

        timer.schedule(object : TimerTask() {
            override fun run() {

                RestClient.service.players().enqueue(object: Callback<List<User>> {
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        Log.e("#####", "UserController.users.error")
                    }

                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {

                        Log.e("#####", "UserController.users.call")
                        callback.onUserList(response.body()!!)}

                })
            }

        }, 0, 5000)

    }


}

interface UserCallback {
    fun onUserList(users: List<User>)
}

class AbsUserCallback: UserCallback {
    override fun onUserList(users: List<User>) {

    }

}