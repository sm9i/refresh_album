package com.sm9i.refresh_album

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File


/** RefreshAlbumPlugin */
public class RefreshAlbumPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private var activity: Activity? = null
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val channel = MethodChannel(flutterPluginBinding.flutterEngine.dartExecutor, "refresh_album")
        channel.setMethodCallHandler(RefreshAlbumPlugin());
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "refresh_album")
            channel.setMethodCallHandler(RefreshAlbumPlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "refreshAlbum" -> {
                if (call.hasArgument("path")) {
                    val refPath = call.argument<String>("path")
                    val file = File(refPath)
                    if (file.exists()) {
                        val uri = Uri.fromFile(file)
                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
                        Log.d("DEBUG", "${activity == null}")
                        with(activity) {
                            this?.sendBroadcast(intent)
                        }
                        result.success("200")
                    }
                    result.success("404")
                }
                result.success("500")
            }
            "refreshAll" -> {
                Log.d("DEBUG", "${activity == null}")
                with(activity) {
                    this?.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
                }
            }
            "refreshInstall" -> {
                if (call.hasArgument("path")) {
                    val refPath = call.argument<String>("path")
                    val file = File(refPath)
                    if (file.exists()) {


                        MediaScannerConnection.scanFile(activity, arrayOf(refPath), arrayOf("png")) { p0, p1 ->
                            Log.d("DEBUG", "onScanCompleted")
                            Log.d("DEBUG", "$p0   $p1")
                        };
                        result.success("200")
                    }
                    result.success("404")
                }
                result.success("500")

            }
            else -> {
                result.notImplemented()
            }
        }

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        this.activity = null
    }

    override fun onDetachedFromActivity() {
        this.activity
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.activity = null
    }
}
