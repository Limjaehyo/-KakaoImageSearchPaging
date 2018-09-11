package com.example.limjaehyo.lezhinimageexample.view

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
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
            /*어플리케이션을 강제 종료하거나 메모리에서 삭제될경우 메소드 를 찾지못하는 어레발생 어리케이션을 찾지못할경우 catch 문으로 이동하고 앱을 재시작한다.*/
            if (application != null) {
                mViewModel = viewModel()
            }
        } catch (e: Exception) {
            /*리스타트할 처음 화면으로 로딩*/
            reStart()
        }

    }


    private fun reStart() {
        val intent = Intent(this, MainActivity::class.java)
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


    fun showCustomDialog(context: Context, msg: String): MaterialDialog {
        this.materialDialog = MaterialDialog.Builder(context).build()
        materialDialog?.setTitle("알림")
        materialDialog?.setContent(msg)
        if (materialDialog?.window != null) {
            materialDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
            materialDialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }
        materialDialog?.setCanceledOnTouchOutside(false)

        return materialDialog!!
    }

    fun removeDisposable(tag: String) {
        observer.removeDisposable(tag)
    }


}