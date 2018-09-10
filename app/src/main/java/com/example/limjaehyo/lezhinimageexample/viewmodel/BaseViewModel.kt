package com.example.limjaehyo.lezhinimageexample.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import com.example.limjaehyo.lezhinimageexample.model.datasource.BaseModel
import io.reactivex.Single


abstract class BaseViewModel<T : BaseModel,I : BaseVewModelInterface ,A> (application : Application, var viewModelInterface: I):AndroidViewModel(application) {
    protected abstract fun getSingle(): Single<T>
    protected abstract fun getSingle(args: A): Single<T>
    val isProgress = ObservableField<Boolean>()

}