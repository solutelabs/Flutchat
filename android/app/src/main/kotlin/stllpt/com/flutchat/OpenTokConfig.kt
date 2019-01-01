package stllpt.com.flutchat

import android.webkit.URLUtil

object OpenTokConfig {
    // *** Fill the following variables using your own Project info from the OpenTok dashboard  ***
    // ***                      https://dashboard.tokbox.com/projects                           ***

    // Replace with your OpenTok API key
    const val API_KEY = "46242132"
    // Replace with a generated Session ID
    const val SESSION_ID = "1_MX40NjI0MjEzMn5-MTU0NTg4NjE5NzkwOX5qeFdDdUMzbVRuMmtwakFGNFBPYnBDcW9-fg"
    // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
    const val TOKEN = "T1==cGFydG5lcl9pZD00NjI0MjEzMiZzaWc9OTcwMTc1Y2MwYjk2MjM4YWRjYzZkN2JmMjg5MzgyZDcxZTMwNmM4ZjpzZXNzaW9uX2lkPTFfTVg0ME5qSTBNakV6TW41LU1UVTBOVGc0TmpFNU56a3dPWDVxZUZkRGRVTXpiVlJ1TW10d2FrRkdORkJQWW5CRGNXOS1mZyZjcmVhdGVfdGltZT0xNTQ1ODg2MjUzJm5vbmNlPTAuNDA3NDIwNjI3NDIwOTU5NSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTQ2NDkxMDUzJmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9"

    /*                           ***** OPTIONAL *****
     If you have set up a server to provide session information replace the null value
     in CHAT_SERVER_URL with it.

     For example: "https://yoursubdomain.com"
    */
    val CHAT_SERVER_URL: String? = null
    val SESSION_INFO_ENDPOINT = CHAT_SERVER_URL?:"" + "/session"


    // *** The code below is to validate this configuration file. You do not need to modify it  ***

    lateinit var webServerConfigErrorMessage: String
    lateinit var hardCodedConfigErrorMessage: String

    val isWebServerConfigUrlValid: Boolean
        get() {
            if (OpenTokConfig.CHAT_SERVER_URL == null || OpenTokConfig.CHAT_SERVER_URL.isEmpty()) {
                webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must not be null or empty"
                return false
            } else if (!(URLUtil.isHttpsUrl(OpenTokConfig.CHAT_SERVER_URL) || URLUtil.isHttpUrl(OpenTokConfig.CHAT_SERVER_URL))) {
                webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must be specified as either http or https"
                return false
            } else if (!URLUtil.isValidUrl(OpenTokConfig.CHAT_SERVER_URL)) {
                webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java is not a valid URL"
                return false
            } else {
                return true
            }
        }

    fun areHardCodedConfigsValid(): Boolean {
        if (OpenTokConfig.API_KEY != null && !OpenTokConfig.API_KEY.isEmpty()
                && OpenTokConfig.SESSION_ID != null && !OpenTokConfig.SESSION_ID.isEmpty()
                && OpenTokConfig.TOKEN != null && !OpenTokConfig.TOKEN.isEmpty()) {
            return true
        } else {
            hardCodedConfigErrorMessage = "API KEY, SESSION ID and TOKEN in OpenTokConfig.java cannot be null or empty."
            return false
        }
    }
}
