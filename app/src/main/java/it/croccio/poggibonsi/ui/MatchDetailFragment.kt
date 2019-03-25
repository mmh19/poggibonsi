package it.croccio.poggibonsi.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.robertlevonyan.components.kex.startActivity
import android.view.ViewGroup
import com.google.gson.Gson
import com.rabtman.wsmanager.WsManager
import com.rabtman.wsmanager.listener.WsStatusListener
import it.croccio.poggibonsi.R
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.model.Match
import kotlinx.android.synthetic.main.fragment_match_detail.*
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.ByteString
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.TimeUnit
import retrofit2.Response as RResponse
import it.croccio.poggibonsi.*
import it.croccio.poggibonsi.controller.WSCallback
import it.croccio.poggibonsi.controller.WSController


class MatchDetailFragment : Fragment(), WSCallback {
    override fun match(match: Match) {
        this.match = match
        updateUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_detail, container, false)
    }

    var match: Match? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener {
            activity?.finish()
        }

        see.setOnClickListener {
            activity?.startActivity(LiveStreamingActivity::class.java)
        }

        stopButton.setOnClickListener {
            waitingMatch?.status = "FINISHED"
            RestClient.service.modifyMatch(waitingMatch!!, waitingMatch!!._id!!).enqueue(object :
                Callback<Match> {
                override fun onFailure(call: Call<Match>, t: Throwable) {
                    Log.e("#####", "")
                }

                override fun onResponse(call: Call<Match>, response: retrofit2.Response<Match>) {
                    Log.e("#####", "")
                }

            })
            activity?.finish()
        }

        RestClient.service.currentMatch().enqueue(object : Callback<Match> {
            override fun onFailure(call: Call<Match>, t: Throwable) {
                Log.e("#####", "")
            }

            override fun onResponse(call: Call<Match>, response: RResponse<Match>) {
                match = response.body()
                updateUI()
            }

        })


        WSController.callback.add(this)


    }

    override fun onResume() {
        super.onResume()

        closed = false

    }

    fun updateUI() {
        if (closed) {
            return
        }
        redPlayerGoalTextView?.text = "${match?.redScore}"
        redPlayerNameTextView?.text = "${match?.redPlayer?.firstName} ${match?.redPlayer?.lastName}"

        bluePlayerGoalTextView?.text = "${match?.blueScore}"
        bluePlayerNameTextView?.text = "${match?.bluePlayer?.firstName} ${match?.bluePlayer?.lastName}"
    }

    var closed = false

    override fun onPause() {
        closed = true
        //wsManager!!.stopConnect()
        closed = false
        super.onPause()
    }

    override fun onDetach() {
        WSController.callback.remove(this)
        super.onDetach()
    }


}
