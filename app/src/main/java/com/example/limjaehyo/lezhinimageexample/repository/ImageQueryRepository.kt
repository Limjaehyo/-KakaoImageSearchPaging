package com.example.limjaehyo.lezhinimageexample.repository

import com.example.limjaehyo.lezhinimageexample.model.datasource.BaseModel
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.util.CommonUtil
import io.reactivex.Single
import java.util.LinkedHashMap

class ImageQueryRepository : BaseRepository<ImageQueryModel>() {
    var query: String =""
    var sort: String = "recency"
    var page : Int = 1
    var size : Int = 80

    companion object {
        private var ourInstance: ImageQueryRepository? = null
        @JvmStatic
        val instance: ImageQueryRepository
            get() {
                if (ourInstance == null) {
                    synchronized(ImageQueryRepository::class) {
                        if (ourInstance == null) {
                            ourInstance = ImageQueryRepository()
                        }
                    }
                }
                return ourInstance!!
            }
    }


    override fun getResponse(): Single<BaseModel> {
            return  null!!
    }

    fun getResponse(query: String, sort: String = "recency", page : Int = 1,  size : Int = 80 ) : Single<ImageQueryModel>{
        this.query = query
        this.sort  = sort
        this.page = page
        this.size = size

        return CommonUtil.getRetrofitAPI()
                .getSearchImage(getQuery())
    }

    override fun queryValuesMap(): LinkedHashMap<String, String> {
        val valuesMap = getValuesMap()
        valuesMap["query"] = query
        valuesMap["sort"] = sort
        valuesMap["page"] = page.toString()
        valuesMap["size"] = size.toString()
        return valuesMap
    }

}