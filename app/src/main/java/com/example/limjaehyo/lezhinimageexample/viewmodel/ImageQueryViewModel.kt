package com.example.limjaehyo.lezhinimageexample.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryDataSource
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryDataSourceFactory
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.model.datasource.NetworkState
import com.example.limjaehyo.lezhinimageexample.repository.ImageQueryRepository
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class ImageQueryViewModel(application: Application, viewModelInterface: ImageQueryViewModelInterface) : BaseViewModel<ImageQueryModel, ImageQueryViewModel.ImageQueryViewModelInterface, String>(application, viewModelInterface) {
    private val executor: Executor
    var netWorkState: LiveData<NetworkState>? = null
    var refreshState: LiveData<NetworkState>? = null
    var dataState: LiveData<Boolean>? = null
    lateinit var userList: LiveData<PagedList<ImageQueryModel.Documents>>
    lateinit var imageListDataSource: ImageQueryDataSourceFactory
    var dataLayoutSubject: PublishSubject<Boolean>

    init {
        executor = Executors.newFixedThreadPool(5)
        dataLayoutSubject = PublishSubject.create()
    }


    override fun getSingle(): Single<ImageQueryModel> {
        return null!!
    }

    override fun getSingle(args: String): Single<ImageQueryModel> {

        return ImageQueryRepository.instance.getResponse(args)
    }

    fun getQueryImagesPaging(paging: String, sort: String) {

        imageListDataSource = ImageQueryDataSourceFactory(compositeDisposable, paging, sort,viewModelInterface)
        netWorkState = Transformations.switchMap<ImageQueryDataSource, NetworkState>(imageListDataSource.sourceFactoryLiveData) { it.networkStateLiveData }
        refreshState = Transformations.switchMap<ImageQueryDataSource, NetworkState>(imageListDataSource.sourceFactoryLiveData) { it.initialLoad }
        dataState = Transformations.switchMap<ImageQueryDataSource, Boolean>(imageListDataSource.sourceFactoryLiveData) { it.isData }
        userList = LivePagedListBuilder(imageListDataSource, 20)
                .setFetchExecutor(executor)
                .build()


        viewModelInterface.getQueryImages(userList)


    }

    //새로고침 호츨 메소드
    fun retry() {
        imageListDataSource.sourceFactoryLiveData.value!!.retry()
    }


    class ImageQueryViewModelFactory(private val mApplication: Application, private val viewModelInterface: ImageQueryViewModelInterface) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ImageQueryViewModel::class.java!!)) {
                ImageQueryViewModel(mApplication, viewModelInterface) as T

            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

    interface ImageQueryViewModelInterface : BaseVewModelInterface {
        fun getQueryImages(items: LiveData<PagedList<ImageQueryModel.Documents>>)
    }
}