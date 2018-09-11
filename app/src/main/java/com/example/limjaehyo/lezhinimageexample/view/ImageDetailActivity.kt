package com.example.limjaehyo.lezhinimageexample.view

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.util.CommonUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import java.util.*

class ImageDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_image_detail)
        val parcelable = intent.getParcelableExtra<ImageQueryModel.Documents>("item")

        val simpleDraweeView = findViewById<SimpleDraweeView>(R.id.my_image_view)
        val uri = Uri.parse(parcelable.image_url)
        simpleDraweeView.setImageURI(uri)

        val ratioHeight = CommonUtil.getRatioHeight(this,parcelable.height.toInt(),parcelable.width.toInt())
        val layoutParams = simpleDraweeView.layoutParams
        layoutParams.width = CommonUtil.getWidth(this)
        layoutParams.height = ratioHeight
        simpleDraweeView.layoutParams = layoutParams


        simpleDraweeView.setOnClickListener { view: View? ->  }
        findViewById<TextView>(R.id.tv_departure).text = String.format(Locale.KOREA,"출저: %s",parcelable.display_sitename)
        findViewById<TextView>(R.id.tv_doc_url).text =String.format(Locale.KOREA,"문서 URL: %s",parcelable.doc_url)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (intent != null) {
            intent.removeExtra("item")
        }
    }
}
