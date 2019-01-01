import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class IntentApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Welcome to Flutter',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Scaffold(
        appBar: AppBar(
          centerTitle: true,
          title: Text("FlutChat"),
        ),
        body: Center(
          child: VideoCallScreen(),
        ),
      ),
      debugShowCheckedModeBanner: false,
    );
  }
}

class VideoCallScreen extends StatefulWidget {
  @override
  _VideoCallScreenState createState() => _VideoCallScreenState();
}

class _VideoCallScreenState extends State<VideoCallScreen> {
  static const platform = const MethodChannel("com.stl.flutchat/opentok");
  var _callDuration;

  void _openVideoCallScreen() async {
    print("item clicked");
    var callDuration = "Unknown call duration.";
    try {
      callDuration = await platform.invokeMethod("openVideoChat");
    } on PlatformException catch (e) {
      callDuration = "Failed to get call duration.";
    }
    setState(() {
      _callDuration = callDuration;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: <Widget>[
        _info(),
        RaisedButton(
          child: Text("Start Video Call"),
          onPressed: () {
            _openVideoCallScreen();
          },
          textTheme: ButtonTextTheme.accent,
        ),
        _duration()
      ],
    );
  }

  Widget _duration() {
    if (_callDuration != null) {
      return Text("Last call duration : $_callDuration");
    } else {
      return Container();
    }
  }

  Widget _info() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Center(
        child: Text(
          "This is Sample Flutter App that uses Android Native Tokbox SDK for Video calls.",
          textAlign: TextAlign.center,
        ),
      ),
    );
  }
}
