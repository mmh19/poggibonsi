package it.croccio.poggibonsi

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.florent37.expectanim.ExpectAnim
import com.github.florent37.expectanim.core.Expectations
import com.github.pwittchen.reactivebeacons.library.rx2.Beacon
import com.github.pwittchen.reactivebeacons.library.rx2.Proximity
import com.github.pwittchen.reactivebeacons.library.rx2.ReactiveBeacons
import com.robertlevonyan.components.kex.replaceFragment
import com.robertlevonyan.components.kex.startActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.croccio.poggibonsi.controller.MatchCallback
import it.croccio.poggibonsi.controller.MatchController
import it.croccio.poggibonsi.core.restclient.RestClient
import it.croccio.poggibonsi.extension.IntActivity
import it.croccio.poggibonsi.model.Match
import it.croccio.poggibonsi.model.User
import it.croccio.poggibonsi.model.loggedUser
import it.croccio.poggibonsi.ui.SplashScreenFragment
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var waitingMatch: Match? = null
var waitingToAcceptChallenge = false

class MainActivity : AppCompatActivity(), IntActivity, MatchCallback {

    var snackbarMatch: Snackbar? = null

    override fun onMatchAvailable() {
        if ((snackbarMatch == null || !snackbarMatch!!.isShown) && loggedUser._id!!.length > 0) {
            snackbarMatch = Snackbar.make(root, "C'è un match in corso! Guarda i dettagli!", Snackbar.LENGTH_INDEFINITE)
            snackbarMatch?.setAction("Guarda", {
                startActivity(MatchDetailsActivity::class.java)
            })
            snackbarMatch?.show()
        }
    }



    override fun onMatchWithMeAvailable(match: Match) {
        waitingMatch = match
        if (waitingToAcceptChallenge) {
            return
        }
        waitingToAcceptChallenge = true
        startActivity(ChallengeActivity::class.java)
    }

    override fun onMatchNotAvailable() {
        snackbarMatch?.dismiss()
        snackbarMatch = null
    }

    companion object {
        private val IS_AT_LEAST_ANDROID_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000
        private const val BEACON = "MAC: %s, RSSI: %d\ndistance: %.2fm, proximity: %s\n%s"
        private const val BLE_NOT_SUPPORTED = "BLE is not supported on this device";
    }


    private var reactiveBeacons: ReactiveBeacons? = null
    private var subscription: Disposable? = null
    private var beacons: MutableMap<String, Beacon> = HashMap()
    private var callingUpdateStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        replaceFragment(
            SplashScreenFragment(),
            root.id,
            inAnimationRes = R.anim.abc_fade_in,
            outAnimationRes = R.anim.abc_fade_out
        )

        MatchController.match(this)
        MatchController.pendingMatchWithMe(this)


    }

    override fun startLoad() {
        ExpectAnim()
            .expect(loadingView)
            .toBe(
                Expectations.alpha(1.0F)
            )
            .toAnimation()
            .addStartListener {
                loadingView.alpha = 0.0f
                loadingView.visibility = View.VISIBLE
            }
            .start()
    }

    override fun stopLoad() {
        ExpectAnim()
            .expect(loadingView)
            .toBe(
                Expectations.alpha(0.0F)
            )
            .toAnimation()
            .addEndListener {
                loadingView.alpha = 0.0f
                loadingView.visibility = View.GONE
            }
            .start()
    }

    override fun onResume() {
        super.onResume()
        reactiveBeacons = ReactiveBeacons(this)

        if (!canObserveBeacons()) {
            return
        }

        startSubscription()
    }

    @SuppressLint("MissingPermission") // permissions are requested in onResume()
    private fun startSubscription() {
        if (reactiveBeacons != null) {
            subscription = (reactiveBeacons as ReactiveBeacons).observe()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { beacon ->
                    Log.e("#####", "Beacon founded")
                    var newAvailability = true
                    if (beacon.proximity == Proximity.NEAR || beacon.proximity == Proximity.IMMEDIATE) {
                        beacons.put(beacon.device.address, beacon)
                    } else {
                        beacons.remove(beacon.device.address)
                    }

                    if (beacons.count() == 0) {
                        newAvailability = false
                    }

                    if (newAvailability != loggedUser.available && !callingUpdateStatus && loggedUser._id != null && loggedUser._id!!.count() > 0) {
                        callingUpdateStatus = true
                        Log.e("#####", "updat status ${loggedUser.available} -> ${newAvailability}")
                        loggedUser.available = newAvailability
                        RestClient.service.modifyPlayer(loggedUser, loggedUser._id!!).enqueue(object : Callback<User> {
                            override fun onFailure(call: Call<User>, t: Throwable) {
                                Log.e("#####", "modifyPlayer.failure")
                                callingUpdateStatus = false
                            }

                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                Log.e("#####", "modifyPlayer.success")
                                callingUpdateStatus = false
                            }

                        })
                    }

                }
        }
    }

    private fun canObserveBeacons(): Boolean {

        if (reactiveBeacons != null) {

            if (!(reactiveBeacons as ReactiveBeacons).isBleSupported) {
                Toast.makeText(this, BLE_NOT_SUPPORTED, Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            if (!(reactiveBeacons as ReactiveBeacons).isBluetoothEnabled) {
                (reactiveBeacons as ReactiveBeacons).requestBluetoothAccess(this)
                return false
            } else if (!(reactiveBeacons as ReactiveBeacons).isLocationEnabled(this)) {
                (reactiveBeacons as ReactiveBeacons).requestLocationAccess(this)
                return false
            } else if (!isFineOrCoarseLocationPermissionGranted() && IS_AT_LEAST_ANDROID_M) {
                requestCoarseLocationPermission()
                return false
            }

            return true
        }

        return false
    }

    override fun onPause() {
        safelyUnsubscribe(subscription)
        /*callingUpdateStatus = true
        loggedUser.available = false
        RestClient.service.modifyPlayer(loggedUser, loggedUser._id!!).enqueue(object: Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("#####", "modifyPlayer.failure")
                callingUpdateStatus = false
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.e("#####", "modifyPlayer.success")
                callingUpdateStatus = false
            }

        })*/
        super.onPause()
    }

    override fun onDestroy() {
        callingUpdateStatus = true
        loggedUser.available = false
        RestClient.service.modifyPlayer(loggedUser, loggedUser._id!!).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("#####", "modifyPlayer.failure")
                callingUpdateStatus = false
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.e("#####", "modifyPlayer.success")
                callingUpdateStatus = false
            }

        })
        super.onDestroy()
    }

    private fun safelyUnsubscribe(subscription: Disposable?) {
        //if (subscription != null && !subscription.isDisposed) {
        //    subscription!!.dispose()
        //}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val isCoarseLocation = requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
        val permissionGranted = grantResults[0] == PERMISSION_GRANTED

        if (isCoarseLocation && permissionGranted && subscription == null) {
            startSubscription()
        }
    }

    private fun requestCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf<String>(ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
            )
        }
    }

    private fun isFineOrCoarseLocationPermissionGranted(): Boolean {
        val isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        val isFineLocationPermissionGranted = isGranted(ACCESS_FINE_LOCATION)
        val isCoarseLocationPermissionGranted = isGranted(ACCESS_COARSE_LOCATION)

        return isAndroidMOrHigher && (isFineLocationPermissionGranted || isCoarseLocationPermissionGranted)
    }

    private fun isGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
    }
}
