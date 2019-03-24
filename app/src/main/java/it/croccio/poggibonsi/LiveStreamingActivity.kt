package it.croccio.poggibonsi

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_live_streaming.*
import android.view.SurfaceHolder
import android.media.MediaPlayer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.pedro.vlc.VlcListener
import java.util.Arrays.asList
import com.pedro.vlc.VlcVideoLibrary
import android.widget.Toast
import com.google.gson.Gson
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import it.croccio.poggibonsi.controller.WSCallback
import it.croccio.poggibonsi.controller.WSController
import it.croccio.poggibonsi.model.Match
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import java.util.concurrent.TimeUnit


class LiveStreamingActivity : AppCompatActivity(), VlcListener, WSCallback {
    override fun match(match: Match) {
        this.match = match
        updateUI()
    }

    val options = arrayListOf(":fullscreen")
    var vlcVideoLibrary: VlcVideoLibrary? = null

    var match: Match? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_streaming)

        vlcVideoLibrary = VlcVideoLibrary(this@LiveStreamingActivity, this@LiveStreamingActivity, videoSurface)

        vlcVideoLibrary?.setOptions(options)

        vlcVideoLibrary?.play("rtsp://192.168.3.23/onvif1")



        WSController.callback.add(this)
    }

    fun updateUI() {
        if (closed) {
            return
        }
        bluescore?.text = "${match?.blueScore}"

        redscore?.text = "${match?.redScore}"
    }

    var closed = false

    override fun onDestroy() {
        closed = true
        vlcVideoLibrary?.stop()
        super.onDestroy()
    }


    override fun onStop() {
        super.onStop()
    }

    override fun onComplete() {
    }

    override fun onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show()
        vlcVideoLibrary?.stop()
    }

    override fun onBackPressed() {
        WSController.callback.remove(this)
        super.onBackPressed()
    }

}
