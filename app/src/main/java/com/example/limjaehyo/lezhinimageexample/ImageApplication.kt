package com.example.limjaehyo.lezhinimageexample

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco


class ImageApplication : Application() {

    override fun onCreate() {
        super.onCreate()
         Fresco.initialize(this)
    }
}