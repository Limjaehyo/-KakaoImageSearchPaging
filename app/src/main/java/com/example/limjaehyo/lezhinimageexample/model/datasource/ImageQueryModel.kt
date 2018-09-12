package com.example.limjaehyo.lezhinimageexample.model.datasource

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class ImageQueryModel(@SerializedName("meta") var meta: Meta, @SerializedName("documents") var documents: ArrayList<Documents>) : BaseModel() {


    class Meta {
        //검색어에 검색된 문서수
        @SerializedName("total_count")
        var total_count: Int = 0

        //노출가능 문서수
        @SerializedName("pageable_count")
        var pageable_count: Int = 0

        //다음페이지 여부
        @SerializedName("is_end")
        var is_end: Boolean = false

    }


    @Parcelize
    class Documents(@SerializedName("collection")
                    var collection: String = "", @SerializedName("thumbnail_url")
                    var thumbnail_url: String = "", @SerializedName("image_url")
                    var image_url: String = "", @SerializedName("width")
                    var width: String = "", @SerializedName("height")
                    var height: String = "", @SerializedName("display_sitename")
                    var display_sitename: String = "", @SerializedName("doc_url")
                    var doc_url: String = "", @SerializedName("datetime")
                    var datetime: String = "") : Parcelable {
        /* //컬렉션
         @SerializedName("collection")
         var collection: String = ""

         //섬네일 이미
         @SerializedName("thumbnail_url")
         var thumbnail_url: String = ""

         //	이미지 URL
         @SerializedName("image_url")
         var image_url: String = ""

         //	이미지 가로크기
         @SerializedName("width")
         var width: String = ""

         //	이미지 세로크기
         @SerializedName("height")
         var height: String = ""

         //출처
         @SerializedName("display_sitename")
         var display_sitename: String = ""

         //	문서 URL
         @SerializedName("doc_url")
         var doc_url: String = ""

         //문서 작성시간. ISO 8601. [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz]
         @SerializedName("datetime")
         var datetime: String = ""
 */


    }
}
