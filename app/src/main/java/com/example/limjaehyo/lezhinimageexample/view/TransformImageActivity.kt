package com.example.limjaehyo.lezhinimageexample.view

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.util.CommonUtil
import me.relex.photodraweeview.PhotoDraweeView

class TransformImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transform_image)
        val imageUrl = intent.getStringExtra("image_url")
        val photoDraweeView = findViewById<PhotoDraweeView>(R.id.my_image_view)
        photoDraweeView.setPhotoUri(Uri.parse(imageUrl))

        val layoutParams = photoDraweeView.layoutParams
        layoutParams.width = CommonUtil.getWidth(this)
        layoutParams.height = CommonUtil.getHeight(this)
        photoDraweeView.layoutParams = layoutParams
    }
    override fun onDestroy() {
        super.onDestroy()
        if (intent != null) {
            intent.removeExtra("image_url")
        }
    }
}
