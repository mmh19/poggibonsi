package it.croccio.poggibonsi

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.robertlevonyan.components.kex.replaceFragment
import it.croccio.poggibonsi.ui.MatchDetailFragment
import it.croccio.poggibonsi.ui.UserDetailFragment
import it.croccio.poggibonsi.ui.detailsUser
import kotlinx.android.synthetic.main.activity_details.*

class MatchDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val det = MatchDetailFragment()
        replaceFragment(det, detailRoot.id, inAnimationRes = R.anim.abc_fade_in, outAnimationRes = R.anim.abc_fade_out)

    }
}
