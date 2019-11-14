# Flutchat

This sample is to describe method channel feature in practical aspect. (Complete steps in [this blog](https://medium.com/@milindmevada/integrating-third-party-native-sdks-in-flutter-df418829dcf7)) We will implement third party dependency provided by TokBox sdk and integrate it along with flutter. The main agenda for method channel is to use platform specific APIs in flutter.

## Getting Started

### Android

#### Requirements 
- You need to replace you api key, session id and token with yours [here](https://github.com/solutelabs/Flutchat/blob/master/android/app/src/main/kotlin/stllpt/com/flutchat/OpenTokConfig.kt) which you can get from [OpenTok dashboard](https://tokbox.com/account/) project section

#### Implementation
- To integrate platform channel in android we need to initiate MehtodChannel in onCreate method in LauncherActivity (Here we will use FirstActivity as Launcher).
```
        MethodChannel(flutterView, "channel_id")
                .setMethodCallHandler { methodCall, result ->
                    methodCall.method?.let {
                        if (it.contentEquals("method")) {
                          // use result object to pass success or error event to main.dart
                          // the datatype of result will be MethodChannel.Result
                        }
                    }
                }

```

- Then we need to invoke method from method channel from main.dart on respective events using following code snippet.
```
var output = await platform.invokeMethod("method");
```
Here output variable will get output of method call. It will wait for method execution.

- To get acomplish method invocation call success or error method from MethodChannel.Result object which we retrive during method channel initialization.

- We can get output value using result object which we get from MethodCallHandler provided in MethodChannel initialization in Launcher Activity. 
```
result.success(any object);
```
For more information regarding platform channel refer
[online documentation](https://flutter.io/docs/development/platform-integration/platform-channels), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## More reference 
- Blog Post explaining complete steps: https://medium.com/@milindmevada/integrating-third-party-native-sdks-in-flutter-df418829dcf7
- Tokbox android integration : https://tokbox.com/developer/tutorials/android/basic-video-chat/
- OpenTok android sdk sample : https://github.com/opentok/opentok-android-sdk-samples
- Flutter method channel : https://flutter.io/docs/development/platform-integration/platform-channels
