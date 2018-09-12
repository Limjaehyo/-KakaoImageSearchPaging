package com.example.limjaehyo.lezhinimageexample.view

import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BaseViewModelActivity<T : ViewModel> : AppCompatActivity() {
    private lateinit var observer: DisposableLifecycleObserver

    protected abstract fun viewModel(): T

    protected var mViewModel: T? = null
    private var materialDialog: MaterialDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lifecycle = this.lifecycle
        observer = DisposableLifecycleObserver(lifecycle)
        lifecycle.addObserver(observer)
        try {
            if (application != null) {
                mViewModel = viewModel()
            }
        } catch (e: Exception) {
            reStart()
        }

    }


    private fun reStart() {
        val intent = Intent(this, ImageQueryListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        if (::observer.isInitialized) {
            observer.enable()
        }

    }

    protected open fun putDisposableMap(tag: String, disposable: Disposable) {
        observer.putDisposableMap(tag, disposable)

    }

    override fun onStop() {
        super.onStop()
        if (materialDialog != null) {
            if (materialDialog!!.isShowing) {
                materialDialog!!.dismiss()
            }
        }

    }

    fun getDisposable(): CompositeDisposable {
        return observer.getDisposable()
    }

    fun getNetWorkDisposable(): CompositeDisposable {
        return observer.getNetWorkDisposable()
    }


    fun removeDisposable(tag: String) {
        observer.removeDisposable(tag)
    }


}