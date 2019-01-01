import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

/// Home widget to display video chat option.
class VideoChatApp extends StatelessWidget {
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
          child: HomeWidget(),
        ),
      ),
      debugShowCheckedModeBanner: false,
    );
  }
}

/// Widget to display start video call layout.
class HomeWidget extends StatefulWidget {
  @override
  _HomeWidgetState createState() => _HomeWidgetState();
}

class _HomeWidgetState extends State<HomeWidget> {
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
        InfoTitle(),
        RaisedButton(
          child: Text("Start Video Call"),
          onPressed: _openVideoCallScreen,
          textTheme: ButtonTextTheme.accent,
        ),
        ShowDuration(
          callDuration: _callDuration,
        )
      ],
    );
  }
}

/// Widget to display start video call title.
class InfoTitle extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
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

/// Widget to display last call duration.
class ShowDuration extends StatelessWidget {
  final double callDuration;

  const ShowDuration({Key key, this.callDuration}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (callDuration != null) {
      return Text("Last call duration : $callDuration");
    } else {
      return Container();
    }
  }
}
