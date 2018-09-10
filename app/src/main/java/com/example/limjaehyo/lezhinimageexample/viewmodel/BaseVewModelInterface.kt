package com.example.limjaehyo.lezhinimageexample.viewmodel

import io.reactivex.disposables.Disposable



interface BaseVewModelInterface {
    fun putDisposableMap(tag: String, disposable: Disposable)
    fun showMessageDialog(msg: String)
    fun removeDisposable(tag: String)

}