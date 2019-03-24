package it.croccio.poggibonsi.controller

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.google.gson.Gson
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.model.Match
import it.croccio.poggibonsi.model.User
import okhttp3.OkHttpClient
import okio.ByteString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

object WSController {


    var callback = HashSet<WSCallback>()

    var wsManager: WsManager? = null

    fun initialize(context: Context) {
        val okHttpClient = OkHttpClient().newBuilder()
            .pingInterval(1, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
        wsManager = WsManager.Builder(context)
            .wsUrl("ws://192.168.3.242:8888/")
            .needReconnect(true)
            .client(okHttpClient)
            .build()

        wsManager?.startConnect()

        wsManager?.setWsStatusListener(object : WsStatusListener() {
            override fun onOpen(response: okhttp3.Response) {
                super.onOpen(response)
            }

            override fun onMessage(text: String?) {
                super.onMessage(text)

                callback.forEach {
                    it.match(Gson().fromJson(text, Match::class.java))
                }

            }

            override fun onMessage(bytes: ByteString?) {
                super.onMessage(bytes)
            }

            override fun onReconnect() {
                super.onReconnect()
            }

            override fun onClosing(code: Int, reason: String?) {
                super.onClosing(code, reason)
            }

            override fun onClosed(code: Int, reason: String?) {
                super.onClosed(code, reason)
            }

            override fun onFailure(t: Throwable?, response: okhttp3.Response?) {
                super.onFailure(t, response)
            }
        })
    }

}

interface WSCallback {
    fun match(users: Match)
}

