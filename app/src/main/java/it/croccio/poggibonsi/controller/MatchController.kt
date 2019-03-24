package it.croccio.poggibonsi.controller

import android.util.Log
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.model.Match
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

object MatchController {

    fun match(callback: MatchCallback) {

        val timer = Timer()
        Log.e("#####", "UserController.users.call")

        timer.schedule(object : TimerTask() {
            override fun run() {

                RestClient.service.currentMatch().enqueue(object : Callback<Match> {
                    override fun onFailure(call: Call<Match>, t: Throwable) {
                        Log.e("#####", "MatchController.match.error")
                        callback.onMatchNotAvailable()
                    }

                    override fun onResponse(call: Call<Match>, response: Response<Match>) {

                        Log.e("#####", "MatchController.match.call")
                        if (response.body() == null) {
                            callback.onMatchNotAvailable()
                        } else if (response.body()?.isActive!!) {
                            callback.onMatchAvailable()
                        } else {
                            callback.onMatchNotAvailable()
                        }
                    }

                })
            }

        }, 0, 5000)

    }

    fun pendingMatchWithMe(callback: MatchCallback) {

        val timer = Timer()
        Log.e("#####", "UserController.users.call")

        timer.schedule(object : TimerTask() {
            override fun run() {

                RestClient.service.pendingMatch().enqueue(object : Callback<Match> {
                    override fun onFailure(call: Call<Match>, t: Throwable) {
                        Log.e("#####", "MatchController.match.error")
                        //callback.onMatchNotAvailable()
                    }

                    override fun onResponse(call: Call<Match>, response: Response<Match>) {

                        Log.e("#####", "MatchController.match.call")
                        if (response.body() == null) {
                            //callback.onMatchNotAvailable()
                        } else if (response.body()?.isPendingWithMe!!) {
                            callback.onMatchWithMeAvailable(response.body()!!)
                        } else {
                            //callback.onMatchNotAvailable()
                        }
                    }

                })
            }

        }, 0, 5000)

    }


}

interface MatchCallback {
    fun onMatchAvailable()
    fun onMatchWithMeAvailable(match: Match)
    fun onMatchNotAvailable()
}