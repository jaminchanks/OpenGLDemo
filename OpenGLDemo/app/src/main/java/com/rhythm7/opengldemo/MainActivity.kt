package com.rhythm7.opengldemo

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    private var mGLSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGLSurfaceView = GLSurfaceView(this)

        if (isSupportES2()) {
            mGLSurfaceView?.setEGLContextClientVersion(2)
            mGLSurfaceView?.setRenderer(TextureRender(this))
        } else {
            toast("This device does not support OpenGL ES 2.0.")
        }

        addContentView(mGLSurfaceView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mGLSurfaceView = null
    }

}
