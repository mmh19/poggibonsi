package it.croccio.poggibonsi.ui


import android.net.Uri
import android.os.Bundle
import it.croccio.poggibonsi.model.*
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.robertlevonyan.components.kex.startActivity
import com.robertlevonyan.components.kex.create
import it.croccio.poggibonsi.model.*
import it.croccio.poggibonsi.core.restclient.*
import com.robertlevonyan.components.kex.replaceFragment
import it.croccio.poggibonsi.*
import es.dmoral.prefs.Prefs
import it.croccio.poggibonsi.DetailsActivity
import it.croccio.poggibonsi.R
import it.croccio.poggibonsi.controller.UserCallback
import it.croccio.poggibonsi.controller.UserController
import it.croccio.poggibonsi.extension.intActivity
import it.croccio.poggibonsi.model.Match
import it.croccio.poggibonsi.model.User
import it.croccio.poggibonsi.model.loggedUser
import it.croccio.poggibonsi.waitingMatch
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import kotlinx.android.synthetic.main.fragment_user_list.*
import kotlinx.android.synthetic.main.fragment_user_list_item.*
import kotlinx.android.synthetic.main.fragment_user_list_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

var detailsUser: User? = null

class UserListFragment : Fragment(), UserCallback {

    override fun onUserList(use: List<User>) {
        if (usersRecycleView == null){
            return
        }

        val users = ArrayList<User>()

        use.forEach{
            if (it._id != loggedUser._id) {
                users.add(it)
            }
        }

        usersRecycleView.create(
            R.layout.fragment_user_list_item,
            users.toTypedArray(),
            LinearLayoutManager(context!!),
            creator = { user, position ->
                nameTextView.text = "${user.firstName} ${user.lastName}"
                availableImageView.visibility = if (user.available!!) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                levelTextView.text = String.format("Livello: %.0f", user.stats?.ranking)

                val isVersus = waitingMatch?.bluePlayer?._id == user._id
                challengeButton.isEnabled = !isVersus

                challengeButton.setOnClickListener{
                    RestClient.service.createMatch(
                        CreateMatch(
                            loggedUser._id!!,
                            user?._id!!
                        )
                    ).enqueue(object: Callback<Match> {
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

            },
            itemClick = { user, position ->
                detailsUser = user
                activity?.startActivity(DetailsActivity::class.java)
            }
        )
        intActivity().stopLoad()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNameTextView.text = "${loggedUser.firstName} ${loggedUser.lastName}"
        userLevelTextView.text = String.format("Livello: %d ", loggedUser.ranking)


        val options = RequestOptions().centerCrop()
            .placeholder(R.drawable.plus)
            .error(R.drawable.__picker_ic_broken_image_black_48dp)

        Glide.with(context!!)
            .load(Uri.fromFile(File(Prefs.with(context!!).read("profilePic"))))
            .apply(options)
            .thumbnail(0.1f)
            .into(uuserImageView)

        intActivity().startLoad()
        UserController.users(this)

    }


}
