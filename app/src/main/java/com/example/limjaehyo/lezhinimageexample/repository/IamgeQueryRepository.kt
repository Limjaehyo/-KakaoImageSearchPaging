package com.example.limjaehyo.lezhinimageexample.repository

import com.example.limjaehyo.lezhinimageexample.model.datasource.BaseModel
import io.reactivex.Single
import java.util.LinkedHashMap

class IamgeQueryRepository : BaseRepository<BaseModel>() {


    override fun getResponse(): Single<BaseModel> {
            return  null!!
    }

    override fun queryValuesMap(): LinkedHashMap<String, String> {
        val valuesMap = getValuesMap()
        return valuesMap
    }

}