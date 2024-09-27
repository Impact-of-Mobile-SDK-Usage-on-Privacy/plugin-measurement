# Mea Culpa PI Assistance Plugin

This plugin assists the PI to conduct traffic collection with our proof of concept apps.

## Build

Just go into the root folder of the project and run `sbt package`.
This should result in a `.jar` in `target/scala-2.13/` move this jar into the plugin folder of the `scala-appanalyzer`.

## Run

Please follow the `README.md` of the `scala-appanlyzer` to configure your phone and computer.
We assume for the remainder of the tutorial that you have done so successfully.


### iOS

To run your iOS app you first have to prepare the folder in which it resides.
Add a `manifest.json` or if it already exists extend it to contain an entry for the iOS app you want to test.

```manifest.json
{
  ....
  "/path/to/App.ipa": {
    "id": "app.identifier",
    "os": "ios",
    "path": "/path/to/App.ipa",
    "version": "NA"
  }
}
```

Next we can already start the analysis using `./aa.sh run ios /path/to/app/folder/ plugin MeaCulpa` but we also need to provide some
essential parameters.

```
   -r <id> if we want to resume a previous experiment, e.g., to keep all analysis for the same study together
   -p <parameters>  the parameters we want to provide the plugin 
```

`-r` is optional but `-p` is essential as we need to provide the order in which the app goes through our four phases.
We do not want to run appium on iOS and thus need to provide `appium=false` and to provide the order of the phases we need
to set `actions=nothing;create;consent;init;basic`.
The `nothing` action allows us to start the experiment on our terms and wait until we have orientated ourselves in the app interface.

The final command to start the traffic interception looks like this then

```
$> ./aa.sh run ios /path/to/app/folder/ plugin MeaCulpa -p "appium=false,actions=nothing;create;consent;init;basic"
``` 

The output looks like this and will guide you through the process. I.e.,it tells you which action you are (supposed to)
currently look at and also tells you when to continue.

```
MLog initialization issue: slf4j found no binding or threatened to use its (dangerously silent) NOPLogger. We consider the slf4j library not found.
Feb 20, 2024 8:53:33 PM com.mchange.v2.log.MLog 
INFO: MLog clients using java 1.4+ standard logging.
2024-02-20 20:53:33.555+0100  info [AppAnalyzer] running   - (AppAnalyzer.scala:301)
2024-02-20 20:53:33.741+0100  info [AppAnalyzer] detected app manifest  - (AppAnalyzer.scala:147)
2024-02-20 20:53:33.775+0100  info [AppAnalyzer] we have 1 app to analyze  - (AppAnalyzer.scala:312)
2024-02-20 20:53:33.783+0100  info [Analysis] running analysis for de.tubs.cs.ias.VungleTestbed:NA@ios  - (Analysis.scala:382)
2024-02-20 20:53:34.095+0100  info [iOSDevice] installing de.tubs.cs.ias.VungleTestbed:NA@ios  - (iOSDevice.scala:103)
WARNING: could not locate iTunesMetadata.plist in archive!
WARNING: could not locate Payload/VungleTestbed.app/SC_Info/VungleTestbed.sinf in archive!
2024-02-20 20:53:41.200+0100  info [iOSDevice] opening com.apple.Preferences  - (iOSDevice.scala:157)
id.of.app
undefined
2024-02-20 20:53:47.636+0100  info [Analysis] setting up analysis Analyzing SDK for app de.tubs.cs.ias.VungleTestbed:NA@ios  - (Analysis.scala:396)
2024-02-20 20:53:47.646+0100  info [Analysis] inserting analysis run into database  - (Analysis.scala:196)
2024-02-20 20:53:47.648+0100  warn [NoAppium] created no appium API - no App interaction/screenshotting possible  - (NoAppium.scala:11)
2024-02-20 20:53:47.649+0100  info [Analysis] starting app de.tubs.cs.ias.VungleTestbed for interface analysis  - (Analysis.scala:200)
2024-02-20 20:53:47.652+0100  info [Analysis] starting traffic collection  - (Analysis.scala:88)
2024-02-20 20:53:58.495+0100  info [Analysis] extracting start interface  - (Analysis.scala:206)
2024-02-20 20:53:58.498+0100  info [Interface] inserting interface initial interface  - (Interface.scala:39)
2024-02-20 20:53:58.548+0100  info [Analysis] calling on actor to perform his magic  - (Analysis.scala:213)
2024-02-20 20:53:58.915+0100  info [MeaCulpa] we are currently looking at NOTHING and will continue in 60000ms  - (MeaCulpa.scala:114)
2024-02-20 20:54:58.916+0100  info [Analysis] stopping traffic collection  - (Analysis.scala:109)
2024-02-20 20:54:58.921+0100  info [Analysis] starting traffic collection  - (Analysis.scala:88)
2024-02-20 20:54:58.926+0100  info [MeaCulpa] please continue in the app and then insert any key to continue the measurement  - (MeaCulpa.scala:127)
Press ENTER to continue ...
2024-02-20 20:55:07.659+0100  info [MeaCulpa] we are currently looking at CREATE_OBJECT and will continue in 60000ms  - (MeaCulpa.scala:114)
2024-02-20 20:56:07.660+0100  info [Analysis] stopping traffic collection  - (Analysis.scala:109)
2024-02-20 20:56:07.666+0100  info [Analysis] starting traffic collection  - (Analysis.scala:88)
2024-02-20 20:56:07.691+0100  info [MeaCulpa] please continue in the app and then insert any key to continue the measurement  - (MeaCulpa.scala:127)
Press ENTER to continue ...
2024-02-20 20:56:11.254+0100  info [MeaCulpa] we are currently looking at CONSENT and will continue in 60000ms  - (MeaCulpa.scala:114)
2024-02-20 20:57:11.255+0100  info [Analysis] stopping traffic collection  - (Analysis.scala:109)
2024-02-20 20:57:11.262+0100  info [Analysis] starting traffic collection  - (Analysis.scala:88)
2024-02-20 20:57:11.274+0100  info [MeaCulpa] please continue in the app and then insert any key to continue the measurement  - (MeaCulpa.scala:127)
Press ENTER to continue ...
2024-02-20 20:57:15.408+0100  info [MeaCulpa] we are currently looking at INIT and will continue in 60000ms  - (MeaCulpa.scala:114)
2024-02-20 20:58:15.409+0100  info [Analysis] stopping traffic collection  - (Analysis.scala:109)
2024-02-20 20:58:15.414+0100  info [Analysis] starting traffic collection  - (Analysis.scala:88)
2024-02-20 20:58:15.422+0100  info [MeaCulpa] please continue in the app and then insert any key to continue the measurement  - (MeaCulpa.scala:127)
Press ENTER to continue ...
2024-02-20 20:58:21.492+0100  info [MeaCulpa] we are currently looking at BASIC_FUNC and will continue in 60000ms  - (MeaCulpa.scala:114)
2024-02-20 20:59:21.493+0100  info [Analysis] stopping traffic collection  - (Analysis.scala:109)
2024-02-20 20:59:21.500+0100  info [Analysis] actor indicates that he done  - (Analysis.scala:228)
2024-02-20 20:59:21.511+0100  info [iOSDevice] uninstall de.tubs.cs.ias.VungleTestbed  - (iOSDevice.scala:116)
2024-02-20 20:59:22.622+0100  info [Analysis] analysis of app de.tubs.cs.ias.VungleTestbed:NA@ios is done  - (Analysis.scala:468)
2024-02-20 20:59:22.623+0100  info [AppAnalyzer] experiment 110 is done  - (AppAnalyzer.scala:332)
```

After the program finishes (sometimes it hangs, just press `ctr+c` after you are told it is done) you should check the
database for any traffic. If no traffic was collected this is a sign that something with your app or your configuration
is amiss.

### Android

Android is near equivalent to iOS except for a small change in the final command

```
./aa.sh run android_device /path/to/app/folder/ plugin MeaCulpa -p "appium=false,actions=nothing;create;init;consent;basic"
```