package com.example.limjaehyo.lezhinimageexample.viewmodel

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.repository.IamgeQueryRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ImageQueryViewModel(application: Application, viewModelInterface: ImageQueryViewModelInterface) : BaseViewModel<ImageQueryModel, ImageQueryViewModel.ImageQueryViewModelInterface, String>(application, viewModelInterface) {

    override fun getSingle(): Single<ImageQueryModel> {
        return  null!!
    }

    override fun getSingle(query: String): Single<ImageQueryModel> {

        return IamgeQueryRepository.instance.getResponse(query)
    }

    fun getQueryImages(query: String) {
        viewModelInterface.putDisposableMap("list",
                getSingle(query).subscribeOn(Schedulers.io())
                        .doOnSubscribe { isProgress.set(true) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ item: ImageQueryModel ->
                            run {
                                Log.e("dd", item.documents[0].image_url)
                            }
                        }, {
                            throwable : Throwable-> run {
                            throwable.message?.let { viewModelInterface.showMessageDialog(it) }
                        }
                        })
        )

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

    }
}