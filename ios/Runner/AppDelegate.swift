import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?
        ) -> Bool {
        
        let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
        let videoChatChannel = FlutterMethodChannel(name: "com.stl.flutchat/opentok",
                                                    binaryMessenger: controller)
        videoChatChannel.setMethodCallHandler({
            [weak self] (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            switch call.method {
            case "openVideoChat":
                self?.presentVideoChatScreen(result: result)
            default:
                result(FlutterMethodNotImplemented)
            }
        })
        
        GeneratedPluginRegistrant.register(with: self)
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    func presentVideoChatScreen(result: @escaping FlutterResult) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let identifier = "VideoChatNavigationViewController"
        let navVC = storyboard.instantiateViewController(withIdentifier: identifier) as! UINavigationController
        let videoChatVC = navVC.viewControllers.first as! VideoChatViewController
        videoChatVC.onCloseTap = { callDuration in
            result("\(callDuration) seconds")
        }
        window.rootViewController?.present(navVC, animated: true, completion: nil)
    }
    
}
