package com.example.logger

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.tencent.mars.BuildConfig
import com.tencent.mars.xlog.Xlog
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class LoggerLifecycleCallbacks(): Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivityResumed")
//        com.tencent.mars.xlog.Log.appenderFlushSync(true)
    }

    override fun onActivityPaused(activity: Activity) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivityPaused")
//        com.tencent.mars.xlog.Log.appenderFlushSync(true)
    }

    override fun onActivityStopped(activity: Activity) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
//         Log.v(LoggerPlugin.TAG, "xx------------onActivityDestroyed")
        com.tencent.mars.xlog.Log.appenderFlushSync(true)
        com.tencent.mars.xlog.Log.appenderClose()
    }
}

/** LoggerPlugin */
class LoggerPlugin() : FlutterPlugin, MethodCallHandler {

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private  lateinit var lifeCallback: LoggerLifecycleCallbacks

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {

//         Log.v(TAG, "xx------------onAttachedToEngine")

        lifeCallback = LoggerLifecycleCallbacks()
        // setup xlog
        val logDir = flutterPluginBinding.applicationContext.getExternalFilesDir("logs")!!.path

        if (BuildConfig.DEBUG) {
            com.tencent.mars.xlog.Log.setConsoleLogOpen(true)
        } else {
            com.tencent.mars.xlog.Log.setConsoleLogOpen(false)
        }
        Xlog.open(true, Xlog.LEVEL_ALL, Xlog.AppednerModeAsync, "", logDir, "GS", "")
        com.tencent.mars.xlog.Log.setLogImp(Xlog())

        val app = flutterPluginBinding.applicationContext as Application
        app.registerActivityLifecycleCallbacks(lifeCallback)

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            X_LOG -> {
                if (call.argument<Int>("type") == 4) { // err
                    com.tencent.mars.xlog.Log.e(TAG, call.argument<String>("msg")!!)
                } else {
                    com.tencent.mars.xlog.Log.i(TAG, call.argument<String>("msg")!!)
                }
                com.tencent.mars.xlog.Log.appenderFlushSync(true)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
//         Log.v(TAG, "xx------------onDetachedFromEngine")
        channel.setMethodCallHandler(null)
        val app = binding.applicationContext as Application
        app.unregisterActivityLifecycleCallbacks(lifeCallback)

        com.tencent.mars.xlog.Log.appenderFlushSync(true)
        com.tencent.mars.xlog.Log.appenderClose()
    }

    companion object {
        const val TAG = "XlogPlugins"
        const val CHANNEL = "com.gaussian.gsbot/log"
        const val X_LOG = "xlog"
    }
}
