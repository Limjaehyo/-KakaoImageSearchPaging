package com.example.limjaehyo.lezhinimageexample.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.databinding.ActivityMain2Binding
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.view.adapter.ImageAdapter
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {


    private lateinit var adapter: ImageAdapter
    lateinit var mViewBinding: ActivityMain2Binding
    lateinit var imm :InputMethodManager

    override fun viewModel(): ImageQueryViewModel {
        val factory = ImageQueryViewModel.ImageQueryViewModelFactory(application, this)
        return ViewModelProviders.of(this, factory)
                .get<ImageQueryViewModel>(ImageQueryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        Fresco.initialize(this)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        adapter = ImageAdapter {
            mViewModel?.retry()
        }

        mViewBinding.rvList.adapter = adapter

        getDisposable().add(RxTextView.textChanges(mViewBinding.etQuery)
                .observeOn(AndroidSchedulers.mainThread())
                .throttleLast(1, TimeUnit.SECONDS)
                .filter { t: CharSequence -> t.isNotEmpty() }
                .subscribe(
                        { text -> mViewModel?.getQueryImagesPaging(text.toString(), "recency") }
                ) { _ -> run {} })




    }

    override fun getQueryImages(items: LiveData<PagedList<ImageQueryModel.Documents>>) {
        if (::adapter.isInitialized) {
            imm.hideSoftInputFromWindow(mViewBinding.etQuery.windowToken,0)
            mViewModel?.netWorkState?.observe(this, Observer { adapter.setNetworkState(it) })

            items.observe(this, Observer { adapter.submitList(it) })
        } else {
            Log.e("adapter", "no Init")

        }
    }

    override fun showMessageDialog(msg: String) {
        super.showCustomDialog(this, msg).show()
    }

    override fun putDisposableMap(tag: String, disposable: Disposable) {
        super.putDisposableMap(tag, disposable)
    }
}
