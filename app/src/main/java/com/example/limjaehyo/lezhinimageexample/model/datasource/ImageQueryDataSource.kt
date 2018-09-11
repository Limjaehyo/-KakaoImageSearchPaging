package com.example.limjaehyo.lezhinimageexample.model.datasource

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.util.Log
import com.example.limjaehyo.lezhinimageexample.repository.ImageQueryRepository
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers


class ImageQueryDataSource(private val compositeDisposable: CompositeDisposable, private val query: String, private val sort: String,private  val viewModelInterface: ImageQueryViewModel.ImageQueryViewModelInterface)
    : PageKeyedDataSource<Int, ImageQueryModel.Documents>() {

    var networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()
    val initialLoad = MutableLiveData<NetworkState>()
    val isData = MutableLiveData<Boolean>()

    private var retryCompletable: Completable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ImageQueryModel.Documents>) {
        networkStateLiveData.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        compositeDisposable.add(ImageQueryRepository.instance.getResponse(query, sort, 1, 80)
                .subscribeOn(Schedulers.io())
                .subscribe({ imageQueryList ->
                    setRetry(null)
                    initialLoad.postValue(NetworkState.LOADED)
                    networkStateLiveData.postValue(NetworkState.LOADED)
                    isData.postValue(imageQueryList.documents.isEmpty())
                    callback.onResult(imageQueryList.documents, null, 2)

                }) { throwable ->
                    viewModelInterface.showMessageDialog(throwable?.message?:"")
                    setRetry(Action { loadInitial(params, callback) })
                    networkStateLiveData.postValue(NetworkState.error(throwable.message))
                    initialLoad.postValue(NetworkState.error(throwable.message))
                })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageQueryModel.Documents>) {
        networkStateLiveData.postValue(NetworkState.LOADING)
        compositeDisposable.add(ImageQueryRepository.instance.getResponse(query, "recency", params.key + 1, 80)
                .subscribeOn(Schedulers.io())
                .subscribe({ imageQueryList ->
                    networkStateLiveData.postValue(NetworkState.LOADED)
                    setRetry(null)
                    val nextKey = (if (imageQueryList.meta.is_end) null else params.key + 1)
                    callback.onResult(imageQueryList.documents, nextKey)


                }, { throwable ->
                    Log.e("loadAfter", throwable.message);
                    setRetry(Action { loadAfter(params, callback) })
                    networkStateLiveData.postValue(NetworkState.error(throwable.message))
                }))

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageQueryModel.Documents>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                    }, { throwable -> Log.e("retry", throwable.message) }))
        }
    }
}

