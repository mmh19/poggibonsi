package it.croccio.poggibonsi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.robertlevonyan.components.kex.replaceFragment
import com.robertlevonyan.components.kex.startActivity
import it.croccio.poggibonsi.model.Match
import it.croccio.poggibonsi.ui.UserDetailFragment
import it.croccio.poggibonsi.ui.detailsUser
import kotlinx.android.synthetic.main.activity_challenge.*
import kotlinx.android.synthetic.main.activity_details.*
import it.croccio.poggibonsi.core.restclient.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge)

        description.text = "${waitingMatch?.redPlayer?.firstName} ti ha sfidato. Accetti la sfida?"

        accept.setOnClickListener {
            waitingMatch?.status = "ACTIVE"
            RestClient.service.modifyMatch(waitingMatch!!, waitingMatch!!._id!!).enqueue(object :
                Callback<Match> {
                override fun onFailure(call: Call<Match>, t: Throwable) {
                    startActivity(MatchDetailsActivity::class.java)
                }

                override fun onResponse(call: Call<Match>, response: Response<Match>) {
                    startActivity(MatchDetailsActivity::class.java)
                }

            })
            waitingToAcceptChallenge = false

            finish()
        }

        reject.setOnClickListener {
            waitingMatch?.status = "REJECTED"
            RestClient.service.modifyMatch(waitingMatch!!, waitingMatch!!._id!!).enqueue(object :
                Callback<Match> {
                override fun onFailure(call: Call<Match>, t: Throwable) {
                    Log.e("#####", "")
                }

                override fun onResponse(call: Call<Match>, response: Response<Match>) {
                    Log.e("#####", "")
                }

            })
            waitingToAcceptChallenge = false
            finish()
        }

    }
}
