package stllpt.com.flutchat

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class WebServiceCoordinator(private val context: Context, private val delegate: Listener) {

    fun fetchSessionConnectionData(sessionInfoUrlEndpoint: String) {

        val reqQueue = Volley.newRequestQueue(context)
        reqQueue.add(JsonObjectRequest(Request.Method.GET, sessionInfoUrlEndpoint,
                null, Response.Listener { response ->
            try {
                val apiKey = response.getString("apiKey")
                val sessionId = response.getString("sessionId")
                val token = response.getString("token")

                Log.i(LOG_TAG, "WebServiceCoordinator returned session information")

                delegate.onSessionConnectionDataReady(apiKey, sessionId, token)

            } catch (e: JSONException) {
                delegate.onWebServiceCoordinatorError(e)
            }
        }, Response.ErrorListener { error -> delegate.onWebServiceCoordinatorError(error) }))
    }

    interface Listener {

        fun onSessionConnectionDataReady(apiKey: String, sessionId: String, token: String)
        fun onWebServiceCoordinatorError(error: Exception)
    }

    companion object {

        private val LOG_TAG = WebServiceCoordinator::class.java.simpleName
    }
}

