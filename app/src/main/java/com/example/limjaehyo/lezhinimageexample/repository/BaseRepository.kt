package com.example.limjaehyo.lezhinimageexample.repository

import com.example.limjaehyo.lezhinimageexample.model.datasource.BaseModel


abstract class BaseRepository<T> : getResponseInterFace<BaseModel> {



    protected fun getQuery( ): Map<String, String> {
        val map = getDefaultMap()

        for (key in queryValuesMap().keys) {
            if (!queryValuesMap().isEmpty()) {
                map[key] = queryValuesMap()[key]!!
            }
        }
        return map
    }


    private fun getDefaultMap( ): LinkedHashMap<String, String> {
        val map = LinkedHashMap<String,String>()
        return map
    }

    protected  fun getValuesMap(): LinkedHashMap<String, String> {
        return LinkedHashMap()
    }
}