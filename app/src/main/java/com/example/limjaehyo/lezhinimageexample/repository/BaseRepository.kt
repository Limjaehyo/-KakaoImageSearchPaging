package com.example.limjaehyo.lezhinimageexample.repository

import com.example.limjaehyo.lezhinimageexample.model.datasource.BaseModel


abstract class BaseRepository<T> : getResponseInterFace<BaseModel> {
 val REST_API  : String = "76b08acf0079a56ec963bd146ea42e58"

    /**
     * 기본적인 커리를 완성하여 리턴하며 추가적으로 Map 형태의 Values 를 받아 세팅한다.
     * 기본적인 값 추가
     *
     * @param type   Query 형태 타입을 받는다
     * @param apiKey //apiKey를 받아 세팅한다.
     * @return 기본 QueryMap + Values Map
     */
    protected fun getQuery( ): Map<String, String> {
        val map = getDefaultMap()


        //추가적으로 자식에서 구현받은 map key Values 룰 저장한다. key 값으로 받아온 values 값이 빈경우 넣지 않는ㄴ다.
        for (key in queryValuesMap().keys) {
            if (!queryValuesMap().isEmpty()) {
                map[key] = queryValuesMap()[key]!!
            }
        }
        return map
    }


    private fun getDefaultMap( ): LinkedHashMap<String, String> {
        val map = LinkedHashMap<String,String>()
        //기본적으로 들어갈 키
//        map["apiKey"] = apiKey

        return map
    }

    protected  fun getValuesMap(): LinkedHashMap<String, String> {
        return LinkedHashMap()
    }
}