package stllpt.com.flutchat

import android.app.Activity
import android.content.Intent
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.Toast

import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.Subscriber
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.OpentokError
import com.opentok.android.SubscriberKit

import java.util.Date

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * Activity for video chat for maximum 2 participants.
 */
class VideoActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, WebServiceCoordinator.Listener, Session.SessionListener, PublisherKit.PublisherListener, SubscriberKit.SubscriberListener {

    // Suppressing this warning. mWebServiceCoordinator will get GarbageCollected if it is local.
    private var mWebServiceCoordinator: WebServiceCoordinator? = null

    private var mSession: Session? = null
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null

    private var btnEndCall: Button? = null

    private var mPublisherViewContainer: FrameLayout? = null
    private var mSubscriberViewContainer: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(LOG_TAG, "onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // initialize view objects from your layout
        mPublisherViewContainer = findViewById<View>(R.id.publisher_container) as FrameLayout
        mSubscriberViewContainer = findViewById<View>(R.id.subscriber_container) as FrameLayout
        btnEndCall = findViewById<View>(R.id.btnEndCall) as Button
        btnEndCall!!.setOnClickListener {
            if (mSession != null) {
                val date = mSession!!.connection.creationTime
                Log.e("SessionCall", "Start time is : " + date.time)
                mSession!!.disconnect()
                val intent = intent
                intent.putExtra("callStartTime", date)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        requestPermissions()
    }

    /* Activity lifecycle methods */

    override fun onPause() {

        Log.d(LOG_TAG, "onPause")

        super.onPause()

        if (mSession != null) {
            mSession!!.onPause()
        }

    }

    override fun onResume() {

        Log.d(LOG_TAG, "onResume")

        super.onResume()

        if (mSession != null) {
            mSession!!.onResume()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

        Log.d(LOG_TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

        Log.d(LOG_TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show()
        }
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {

        val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // if there is no server URL set
            if (OpenTokConfig.CHAT_SERVER_URL == null) {
                // use hard coded session values
                if (OpenTokConfig.areHardCodedConfigsValid()) {
                    initializeSession(OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID, OpenTokConfig.TOKEN)
                } else {
                    showConfigError("Configuration Error", OpenTokConfig.hardCodedConfigErrorMessage)
                }
            } else {
                // otherwise initialize WebServiceCoordinator and kick off request for session data
                // session initialization occurs once data is returned, in onSessionConnectionDataReady
                if (OpenTokConfig.isWebServerConfigUrlValid) {
                    mWebServiceCoordinator = WebServiceCoordinator(this, this)
                    mWebServiceCoordinator!!.fetchSessionConnectionData(OpenTokConfig.SESSION_INFO_ENDPOINT)
                } else {
                    showConfigError("Configuration Error", OpenTokConfig.webServerConfigErrorMessage)
                }
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_video_app), RC_VIDEO_APP_PERM, *perms)
        }
    }

    private fun initializeSession(apiKey: String, sessionId: String, token: String) {

        mSession = Session.Builder(this, apiKey, sessionId).build()
        mSession!!.setSessionListener(this)
        mSession!!.connect(token)
    }

    /* Web Service Coordinator delegate methods */

    override fun onSessionConnectionDataReady(apiKey: String, sessionId: String, token: String) {

        Log.d(LOG_TAG, "ApiKey: $apiKey SessionId: $sessionId Token: $token")
        initializeSession(apiKey, sessionId, token)
    }

    override fun onWebServiceCoordinatorError(error: Exception) {

        Log.e(LOG_TAG, "Web Service error: " + error.message)
        Toast.makeText(this, "Web Service error: " + error.message, Toast.LENGTH_LONG).show()
        finish()

    }

    /* Session Listener methods */

    override fun onConnected(session: Session) {

        Log.d(LOG_TAG, "onConnected: Connected to session: " + session.sessionId)

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = Publisher.Builder(this).build()
        mPublisher!!.setPublisherListener(this)

        // set publisher video style to fill view
        mPublisher!!.renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL)
        mPublisherViewContainer!!.addView(mPublisher!!.view)
        if (mPublisher!!.view is GLSurfaceView) {
            (mPublisher!!.view as GLSurfaceView).setZOrderOnTop(true)
        }

        mSession!!.publish(mPublisher)
    }

    override fun onDisconnected(session: Session) {

        Log.d(LOG_TAG, "onDisconnected: Disconnected from session: " + session.sessionId)
    }

    override fun onStreamReceived(session: Session, stream: Stream) {

        Log.d(LOG_TAG, "onStreamReceived: New Stream Received " + stream.streamId + " in session: " + session.sessionId)
        findViewById<View>(R.id.tvWaiting).visibility = View.GONE
        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSubscriber!!.renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            mSubscriber!!.setSubscriberListener(this)
            mSession!!.subscribe(mSubscriber!!)
            mSubscriberViewContainer!!.addView(mSubscriber!!.view)
        }
    }

    override fun onStreamDropped(session: Session, stream: Stream) {

        Log.d(LOG_TAG, "onStreamDropped: Stream Dropped: " + stream.streamId + " in session: " + session.sessionId)

        if (mSubscriber != null) {
            mSubscriber = null
            mSubscriberViewContainer!!.removeAllViews()
        }
    }

    override fun onError(session: Session, opentokError: OpentokError) {
        Log.e(LOG_TAG, "onError: " + opentokError.errorDomain + " : " +
                opentokError.errorCode + " - " + opentokError.message + " in session: " + session.sessionId)

        showOpenTokError(opentokError)
    }

    /* Publisher Listener methods */

    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {

        Log.d(LOG_TAG, "onStreamCreated: Publisher Stream Created. Own stream " + stream.streamId)

    }

    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {

        Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream " + stream.streamId)
    }

    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {

        Log.e(LOG_TAG, "onError: " + opentokError.errorDomain + " : " +
                opentokError.errorCode + " - " + opentokError.message)

        showOpenTokError(opentokError)
    }

    override fun onConnected(subscriberKit: SubscriberKit) {

        Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: " + subscriberKit.stream.streamId)
    }

    override fun onDisconnected(subscriberKit: SubscriberKit) {

        Log.d(LOG_TAG, "onDisconnected: Subscriber disconnected. Stream: " + subscriberKit.stream.streamId)
    }

    override fun onError(subscriberKit: SubscriberKit, opentokError: OpentokError) {

        Log.e(LOG_TAG, "onError: " + opentokError.errorDomain + " : " +
                opentokError.errorCode + " - " + opentokError.message)

        showOpenTokError(opentokError)
    }

    private fun showOpenTokError(opentokError: OpentokError) {

        Toast.makeText(this, opentokError.errorDomain.name + ": " + opentokError.message + " Please, see the logcat.", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showConfigError(alertTitle: String, errorMessage: String) {
        Log.e(LOG_TAG, "Error $alertTitle: $errorMessage")
        AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("ok") { dialog, which -> this@VideoActivity.finish() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    companion object {

        private val LOG_TAG = VideoActivity::class.java.simpleName
        private val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_VIDEO_APP_PERM = 124
    }
}
