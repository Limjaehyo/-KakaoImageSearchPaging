package com.example.limjaehyo.lezhinimageexample.model.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.util.Log
import com.example.limjaehyo.lezhinimageexample.repository.ImageQueryRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ImageQueryDataSource(private val compositeDisposable: CompositeDisposable, private val query: String,private  val sort : String,
                           private val liveData: MutableLiveData<MutableList<ImageQueryModel.Documents>>)
    : PageKeyedDataSource<Int, ImageQueryModel.Documents>() {

    var netWorkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ImageQueryModel.Documents>) {
        netWorkStateLiveData.postValue(NetworkState.LOADING)
        compositeDisposable.add(ImageQueryRepository.instance.getResponse(query, sort, 1, 80)
                .subscribeOn(Schedulers.io())
                .subscribe({ imageQueryList ->
                    netWorkStateLiveData.postValue(NetworkState.LOADED)
                    callback.onResult(imageQueryList.documents, null, 2)

                }) { throwable ->
                    netWorkStateLiveData.postValue(NetworkState.FAILED)
                    Log.e("loadInitial_throwable3", throwable.localizedMessage)
                })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageQueryModel.Documents>) {
        netWorkStateLiveData.postValue(NetworkState.LOADING)
        compositeDisposable.add(ImageQueryRepository.instance.getResponse(query, "recency", params.key + 1, 80)
                .subscribeOn(Schedulers.io())
                .subscribe({ imageQueryList ->
                    netWorkStateLiveData.postValue(NetworkState.LOADED)

                        val nextKey = (if (imageQueryList.meta.is_end) null else params.key + 1)
                    callback.onResult(imageQueryList.documents, nextKey)


                }, { throwable ->
                    netWorkStateLiveData.postValue(NetworkState.FAILED)
                    Log.e("loadAfter_throwable", throwable.message)
                }))

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageQueryModel.Documents>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

