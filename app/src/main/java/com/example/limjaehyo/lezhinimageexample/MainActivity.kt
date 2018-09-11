package com.example.limjaehyo.lezhinimageexample

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.example.limjaehyo.lezhinimageexample.databinding.ActivityMain2Binding
import com.example.limjaehyo.lezhinimageexample.view.BaseViewModelActivity
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {
    lateinit var mViewBinding: ActivityMain2Binding

    override fun viewModel(): ImageQueryViewModel {
        val factory = ImageQueryViewModel.ImageQueryViewModelFactory(application, this)
        return ViewModelProviders.of(this, factory)
                .get<ImageQueryViewModel>(ImageQueryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)


        getDisposable().add(RxTextView.textChanges(mViewBinding.etQuery)
                .observeOn(AndroidSchedulers.mainThread())
                .throttleLast(1, TimeUnit.SECONDS)
                .subscribe(
                        { text ->
                            mViewModel?.getQueryImages(text.toString())
                        }
                ) { _ -> run {} })

        mViewModel?.getQueryImages("소연")


    }

    override fun showMessageDialog(msg: String) {
        super.showCustomDialog(this, msg).show()
    }

    override fun putDisposableMap(tag: String, disposable: Disposable) {
        super.putDisposableMap(tag, disposable)
    }
}
