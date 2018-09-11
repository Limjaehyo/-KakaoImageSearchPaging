package com.example.limjaehyo.lezhinimageexample.model.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import io.reactivex.disposables.CompositeDisposable


class ImageQueryDataSourceFactory(private val compositeDisposable: CompositeDisposable, private val query: String, private val sort: String)
    : DataSource.Factory<Int, ImageQueryModel.Documents>() {

    val sourceFactoryLiveData = MutableLiveData<ImageQueryDataSource>()
    override fun create(): DataSource<Int, ImageQueryModel.Documents> {
        val usersDataSource = ImageQueryDataSource(compositeDisposable, query, sort)
        sourceFactoryLiveData.postValue(usersDataSource)
        return usersDataSource
    }


}