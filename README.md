# Flutchat

This sample is to describe method channel feature in practical aspect. We will implement third party dependency provided by tokbox sdk and integrate it along with flutter. The main agenda for method channel is to use platform specific APIs in flutter.

## Getting Started

To integrate tokbox sdk in flutter we need to write platform specific code in android and IOS and then we will call that using respactive channel from Launcher activity as shown in following diagram.

![alt text](https://raw.githubusercontent.com/solutelabs/Flutchat/feature/add_assets/workflow.jpg)

### Android

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
