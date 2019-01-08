package com.example.urvish.mlkitdemo

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.camerakit.CameraKitView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabel
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!askPermission()) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
        btnTakePhoto.setOnClickListener {
            camera.captureImage { cameraKitView: CameraKitView?, bytes: ByteArray? ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)
                getLable(bitmap)
            }
        }
    }

    private fun getLable(bitmap: Bitmap?) {
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
        val detector: FirebaseVisionLabelDetector = FirebaseVision.getInstance().visionLabelDetector
        detector.detectInImage(image).addOnSuccessListener {
            val msg: StringBuilder = java.lang.StringBuilder()
            for (firebaseVision: FirebaseVisionLabel in it) {
                Log.d(
                    "RESULT",
                    "Item name ${firebaseVision.label} ${firebaseVision.confidence} ${firebaseVision.entityId}"
                )
                msg.append("Item Name: ${firebaseVision.label}  Acc:${firebaseVision.confidence * 100}\n")

            }


            val dailog = this.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setMessage(msg)
                    setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                }
                builder.create()
            }
            dailog.show()
        }.addOnFailureListener {
            Log.e("TAG", "" + it.toString())
        }
    }

    private fun askPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        camera.onStart()
    }

    override fun onStop() {
        super.onStop()
        camera.onStop()
    }

    override fun onPause() {
        super.onPause()
        camera.onPause()
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
