package it.croccio.poggibonsi.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robertlevonyan.components.kex.replaceFragment

import it.croccio.poggibonsi.R
import it.croccio.poggibonsi.controller.PreferencesController
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.extension.intActivity
import it.croccio.poggibonsi.model.User
import it.croccio.poggibonsi.model.loggedUser
import it.croccio.poggibonsi.ui.signup.SignupFragment
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RestClient.service.playerById(loggedUser._id!!).enqueue(object: Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("#####", t.localizedMessage)
                activity?.replaceFragment(
                    if (PreferencesController.token == null) {
                        SignupFragment()
                    } else {
                        UserListFragment()
                    },
                    activity!!.root.id,
                    inAnimationRes = R.anim.abc_fade_in,
                    outAnimationRes = R.anim.abc_fade_out
                )
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                loggedUser = response.body()!!

                activity?.replaceFragment(
                    if (PreferencesController.token == null) {
                        SignupFragment()
                    } else {
                        UserListFragment()
                    },
                    activity!!.root.id,
                    inAnimationRes = R.anim.abc_fade_in,
                    outAnimationRes = R.anim.abc_fade_out
                )
            }

        })
    }

}
