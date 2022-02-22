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
        Log.d(LoggerPlugin.TAG, "xx------------onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(LoggerPlugin.TAG, "xx------------onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(LoggerPlugin.TAG, "xx------------onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(LoggerPlugin.TAG, "xx------------onActivityPaused")
        com.tencent.mars.xlog.Log.appenderFlushSync(true)
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(LoggerPlugin.TAG, "xx------------onActivityStopped")
        com.tencent.mars.xlog.Log.appenderFlushSync(true)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.d(LoggerPlugin.TAG, "xx------------onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(LoggerPlugin.TAG, "xx------------onActivityDestroyed")
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

        Log.d(TAG, "xx------------onAttachedToEngine")

        lifeCallback = LoggerLifecycleCallbacks()
        // setup xlog
        val logDir = flutterPluginBinding.applicationContext.getExternalFilesDir("logs")!!.path
        Log.d(TAG, "xx------------log path-$logDir")
        if (BuildConfig.DEBUG) {
            Xlog.open(true, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, "", logDir, "GS", "")
            com.tencent.mars.xlog.Log.setConsoleLogOpen(true)
        } else {
            Xlog.open(true, Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logDir, "GS", "")
            com.tencent.mars.xlog.Log.setConsoleLogOpen(false)
        }
        com.tencent.mars.xlog.Log.setLogImp(Xlog())

        val app = flutterPluginBinding.applicationContext as Application
        app.registerActivityLifecycleCallbacks(lifeCallback)

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            X_LOG -> {
                println("------------------------------------${call.argument<String>("msg")!!}")

                if (call.argument<Int>("type") == 4) { // err
                    com.tencent.mars.xlog.Log.e(TAG, call.argument<String>("msg")!!)
                } else {
                    com.tencent.mars.xlog.Log.i(TAG, call.argument<String>("msg")!!)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(TAG, "xx------------onDetachedFromEngine")
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
