package com.example.limjaehyo.lezhinimageexample.view

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView

class ImageDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_image_detail)
        val bundle = intent.getBundleExtra("bundle")
        val parcelable = bundle.getParcelable<ImageQueryModel.Documents>("item")

        val simpleDraweeView = findViewById<SimpleDraweeView>(R.id.my_image_view)
        val uri = Uri.parse(parcelable.image_url)
        simpleDraweeView.setImageURI(uri)
        val height = (parcelable.height.toFloat() * getWidth().toFloat() / parcelable.width.toFloat()).toInt()
        val layoutParams = simpleDraweeView.layoutParams
        layoutParams.width = getWidth()
        layoutParams.height = height
        simpleDraweeView.layoutParams = layoutParams
    }
    private fun getWidth(): Int {
        val dm = applicationContext.resources.displayMetrics
        return dm.widthPixels
    }
    override fun onDestroy() {
        super.onDestroy()
        if (intent != null) {
            intent.removeExtra("bundle")
            intent.removeExtra("item")
        }
    }
}
