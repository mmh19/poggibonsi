package it.croccio.poggibonsi.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robertlevonyan.components.kex.startActivity
import it.croccio.poggibonsi.R
import it.croccio.poggibonsi.model.User
import kotlinx.android.synthetic.main.fragment_user_detail.*
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.model.CreateMatch
import it.croccio.poggibonsi.model.Match
import it.croccio.poggibonsi.model.loggedUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import it.croccio.poggibonsi.*


class UserDetailFragment : Fragment() {

    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goalTextView.text = "${user?.stats?.goals}"
        matchesTextView.text =  "${user?.stats?.matches}"
        rankingTextView.text = String.format("%.0f", user?.stats?.ranking)
        userLevelTextView.text = String.format("%.0f", user?.stats?.ranking)
        userNameTextView.text = "${user?.firstName} ${user?.lastName}"
        //imageView

        backButton.setOnClickListener {
            activity?.finish()
        }

        challengeButton.setOnClickListener {
            RestClient.service.createMatch(CreateMatch(loggedUser._id!!, user?._id!!)).enqueue(object: Callback<Match> {
                override fun onFailure(call: Call<Match>, t: Throwable) {

                    AlertDialog.Builder(context!!)
                        .setTitle(getString(R.string.thersAProblem))
                        .setMessage(t.localizedMessage)
                        .setPositiveButton(getString(R.string.ok), { dialog, which ->  dialog.dismiss() })
                        .show()
                }

                override fun onResponse(call: Call<Match>, response: Response<Match>) {

                    if (response.code() == 200) {
                        AlertDialog.Builder(context!!)
                            .setTitle("Che vinca il migliore!!")
                            .setMessage("Hai sfidato ${user?.firstName} ${user?.lastName}")
                            .setPositiveButton(getString(R.string.ok), { dialog, which -> dialog.dismiss() })
                            .show()
                    } else {
                        AlertDialog.Builder(context!!)
                            .setTitle("C'è già una sfida in corso")
                            .setMessage("Ops. C'è già una partita in corso. Vuoi vederla?")
                            .setPositiveButton(getString(R.string.ok), { dialog, which ->
                                activity?.startActivity(MatchDetailsActivity::class.java)
                                dialog.dismiss() })
                            .setNegativeButton("No", { dialog, which -> dialog.dismiss() })
                            .show()
                    }
                }

            })
        }

    }


}
