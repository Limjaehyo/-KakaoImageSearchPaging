package com.example.limjaehyo.lezhinimageexample.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.model.datasource.Status
import com.example.limjaehyo.lezhinimageexample.view.adapter.GridSpacingItemDecoration
import com.example.limjaehyo.lezhinimageexample.view.adapter.ImageAdapter
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {


    private lateinit var adapter: ImageAdapter

    override fun viewModel(): ImageQueryViewModel {
        val factory = ImageQueryViewModel.ImageQueryViewModelFactory(application, this)
        return ViewModelProviders.of(this, factory)
                .get<ImageQueryViewModel>(ImageQueryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fresco.initialize(this)
        setMessageTextSetting(getString(R.string.input_text_plz))
        adapterInit()


        mViewModel?.dataLayoutSubject?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { visibility ->
                    if (visibility) {
                        tv_msg.visibility = View.VISIBLE
                        rv_list.visibility = View.GONE
                    } else {
                        tv_msg.visibility = View.GONE
                        rv_list.visibility = View.VISIBLE
                    }
                }

        RxTextView.textChanges(et_query)
                .throttleLast(1, TimeUnit.SECONDS,AndroidSchedulers.mainThread())
                .subscribe(
                        { text ->
                           if (text.isNotEmpty()) {
                                removeDisposable("list")
                                mViewModel?.getQueryImagesPaging(text.toString(), "recency")
                                mViewModel?.dataLayoutSubject?.onNext(false)
                            } else {
                                setMessageTextSetting(getString(R.string.input_text_plz))
                                mViewModel?.dataLayoutSubject?.onNext(true)
                            }
                        }
                ) { th -> run { Log.e("textChanges", th.message) } }


    }


    private fun adapterInit() {
        adapter = ImageAdapter(this) {
            mViewModel?.retry()
        }
        setStaggeredSetting()

        rv_list.adapter = adapter
    }

    private fun setStaggeredSetting() {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, 1)
        staggeredGridLayoutManager.orientation = StaggeredGridLayoutManager.VERTICAL
        rv_list.layoutManager = staggeredGridLayoutManager
        rv_list.addItemDecoration(GridSpacingItemDecoration(2, 8, true))
    }

    override fun getQueryImages(items: LiveData<PagedList<ImageQueryModel.Documents>>) {
        if (::adapter.isInitialized) {
            mViewModel?.netWorkState?.observe(this, Observer { it ->
                run {
                    if (it?.status == Status.FAILED) {
                        setMessageTextSetting(it.message!!)
                        mViewModel?.dataLayoutSubject?.onNext(true)
                    }
                }
            })

            mViewModel?.dataState?.observe(this, Observer { it ->
                run {
                    if (it == true) {
                        setMessageTextSetting(getString(R.string.no_result))
                        mViewModel?.dataLayoutSubject?.onNext(true)
                    }
                }
            })
            items.observe(this, Observer { it ->
                run {
                    adapter.submitList(it)
                    mViewModel?.dataLayoutSubject?.onNext(false)

                }
            })


        }
    }

    private fun setMessageTextSetting(msg: String) {
        tv_msg.text = msg
    }


    override fun putDisposableMap(tag: String, disposable: Disposable) {
        super.putDisposableMap(tag, disposable)
    }


}
