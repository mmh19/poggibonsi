package it.croccio.poggibonsi.ui.signup


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.robertlevonyan.components.kex.replaceFragment
import it.croccio.poggibonsi.R
import it.croccio.poggibonsi.controller.SignupCallback
import me.iwf.photopicker.PhotoPicker
import java.io.File
import it.croccio.poggibonsi.controller.SignupController
import it.croccio.poggibonsi.extension.intActivity
import it.croccio.poggibonsi.ui.UserListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_signup.*
import java.io.ByteArrayOutputStream
import android.provider.MediaStore
import es.dmoral.prefs.Prefs


class SignupFragment : Fragment(), SignupCallback {

    var base64ProfileImage: String? = null

    override fun onSignupSuccess() {
        intActivity().stopLoad()
        activity?.replaceFragment(UserListFragment(), activity!!.root.id, inAnimationRes = R.anim.abc_fade_in, outAnimationRes = R.anim.abc_fade_out)
    }

    override fun onSignupError() {
        intActivity().stopLoad()
        AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.thersAProblem))
            .setMessage(getString(R.string.signupErrorMessage))
            .setPositiveButton(getString(R.string.ok), { dialog, which ->  dialog.dismiss() })
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(it.croccio.poggibonsi.R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImage.setOnClickListener {
            PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(false)
                .start(view.context, this@SignupFragment)
        }

        continueButton.setOnClickListener {
            //fixme: usare libreria per la validazione dei campi
            if (nameEditText.text?.length == 0) {
                nameEdit.error = "Inserisci il nome"
                return@setOnClickListener
            } else {
                nameEdit.error = null
            }

            if (surnameEditText.text?.length == 0) {
                surnameEdit.error = "Inserisci il cognome"
                return@setOnClickListener
            } else {
                surnameEdit.error = null
            }


            if (emailEditText.text?.length == 0) {
                emailEdit.error = "Inserisci la tua email"
                return@setOnClickListener
            } else {
                emailEdit.error = null
            }

            intActivity().startLoad()

            SignupController.signup(
                emailEditText.text.toString(),
                nameEditText.text.toString(),
                surnameEditText.text.toString(),
                base64ProfileImage,
                this)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                val photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                val uri = Uri.fromFile(File(photos.get(0)))

                val options = RequestOptions().centerCrop()
                    .placeholder(R.drawable.plus)
                    .error(R.drawable.__picker_ic_broken_image_black_48dp)
                Glide.with(context!!)
                    .load(uri)
                    .apply(options)
                    .thumbnail(0.1f)
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Prefs.with(context!!).write("profilePic", photos.get(0))
                            return false
                        }

                    })
                    .into(profileImage)



            }
        }
    }

}
