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

        imageListDataSource = ImageQueryDataSourceFactory(compositeDisposable, paging, sort)
        netWorkState = Transformations.switchMap<ImageQueryDataSource, NetworkState>(imageListDataSource.sourceFactoryLiveData) { it.networkStateLiveData }
        refreshState = Transformations.switchMap<ImageQueryDataSource, NetworkState>(imageListDataSource.sourceFactoryLiveData) { it.initialLoad }
        dataState = Transformations.switchMap<ImageQueryDataSource, Boolean>(imageListDataSource.sourceFactoryLiveData) { it.isData }
        userList = LivePagedListBuilder(imageListDataSource, 20)
                .setFetchExecutor(executor)
                .build()


        viewModelInterface.getQueryImages(userList)


    }

/*    fun getQueryImages1(query: String) {
        viewModelInterface.putDisposableMap("list",
                getSingle(query).subscribeOn(Schedulers.io())
                        .doOnSubscribe { isProgress.set(true) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ item: ImageQueryModel ->
                            run {
                                Log.e("dd", item.documents[0].image_url)
                            }
                        }, { throwable: Throwable ->
                            run {
                                throwable.message?.let { viewModelInterface.showMessageDialog(it) }
                            }
                        })
        )

    }*/

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