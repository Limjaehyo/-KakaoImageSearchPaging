package com.example.limjaehyo.lezhinimageexample.model.datasource

import com.google.gson.annotations.SerializedName

class ImageQueryModel(@SerializedName("meta") var meta: Meta, @SerializedName("documents") var documents: ArrayList<Documents>) : BaseModel() {


    class Meta {
        @SerializedName("total_count")
        var total_count: Int = 0
        @SerializedName("pageable_count")
        var pageable_count: Int = 0
        @SerializedName("is_end")
        var is_end: Boolean = false

    }

    class Documents {
        @SerializedName("collection")
        var collection: String = ""
        @SerializedName("thumbnail_url")
        var thumbnail_url: String = ""
        @SerializedName("image_url")
        var image_url: String = ""
        @SerializedName("width")
        var width: String = ""
        @SerializedName("height")
        var height: String = ""
        @SerializedName("display_sitename")
        var display_sitename: String = ""
        @SerializedName("doc_url")
        var doc_url: String = ""
        @SerializedName("datetime")
        var datetime: String = ""

    }
}
