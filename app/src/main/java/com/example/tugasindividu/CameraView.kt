package com.example.tugasindividu.Camera

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException


class CameraView(context: Context?, private val mCamera: Camera) : SurfaceView(context), SurfaceHolder.Callback{
    private val mHoldel: SurfaceHolder

    init {
        mCamera.setDisplayOrientation(90)
        mHoldel=holder
        mHoldel.addCallback(this)
        mHoldel.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        try{
            mCamera.setPreviewDisplay(mHoldel)
            mCamera.startPreview()
        } catch (e:IOException){
            Log.d("error", "Camera eror on SurfaceCreated" + e.message)
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        if(mHoldel.surface==null) return
        try {
            mCamera.setPreviewDisplay(mHoldel)
            mCamera.startPreview()
        }catch (e:IOException){
            Log.d("error", "Camera eror on SurfaceCreated" + e.message)
        }
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        mCamera.stopPreview()
        mCamera.release()
    }
}


