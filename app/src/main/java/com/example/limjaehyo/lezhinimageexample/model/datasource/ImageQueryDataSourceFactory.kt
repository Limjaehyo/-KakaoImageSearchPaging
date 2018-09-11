package com.example.limjaehyo.lezhinimageexample.model.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import io.reactivex.disposables.CompositeDisposable


class ImageQueryDataSourceFactory(private val compositeDisposable: CompositeDisposable, private val query: String, private val sort: String , private  val viewModelInterface: ImageQueryViewModel.ImageQueryViewModelInterface)
    : DataSource.Factory<Int, ImageQueryModel.Documents>() {

    val sourceFactoryLiveData = MutableLiveData<ImageQueryDataSource>()
    override fun create(): DataSource<Int, ImageQueryModel.Documents> {
        val usersDataSource = ImageQueryDataSource(compositeDisposable, query, sort,viewModelInterface)
        sourceFactoryLiveData.postValue(usersDataSource)
        return usersDataSource
    }


}