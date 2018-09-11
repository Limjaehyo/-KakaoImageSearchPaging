package com.example.limjaehyo.lezhinimageexample.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.databinding.ActivityMainBinding
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.model.datasource.Status
import com.example.limjaehyo.lezhinimageexample.view.adapter.GridSpacingItemDecoration
import com.example.limjaehyo.lezhinimageexample.view.adapter.ImageAdapter
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import com.facebook.drawee.backends.pipeline.Fresco
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {


    private lateinit var adapter: ImageAdapter
    private lateinit var mViewBinding: ActivityMainBinding
    private lateinit var imm: InputMethodManager
    private  var isFirst   = true

    override fun viewModel(): ImageQueryViewModel {
        val factory = ImageQueryViewModel.ImageQueryViewModelFactory(application, this)
        return ViewModelProviders.of(this, factory)
                .get<ImageQueryViewModel>(ImageQueryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Fresco.initialize(this)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        adapterInit()

        getDisposable().add(RxTextView.textChanges(mViewBinding.etQuery)
                .observeOn(AndroidSchedulers.mainThread())
                .throttleLast(1, TimeUnit.SECONDS)

                .subscribe(
                        { text ->
                            if (text.isNotEmpty()) {
                                mViewModel?.getQueryImagesPaging(text.toString(), "recency")
                            }else{
                                if (isFirst) {
                                    isFirst = false
                                }else{
//                                    mViewModel?.userList?.value?.dataSource?.invalidate()
                                    adapter.setDataReset()

                                }
                            }
                        }
                ) { _ -> run {} })


    }

    private fun adapterInit() {
        adapter = ImageAdapter(this,getWidth()) {
            mViewModel?.retry()
        }
        setStaggeredSetting()

        mViewBinding.rvList.adapter = adapter
    }

    private  fun setLiniManager(){
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mViewBinding.rvList.layoutManager = linearLayoutManager
    }
private  fun setStaggeredSetting(){
    val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, 1)
    staggeredGridLayoutManager.orientation = StaggeredGridLayoutManager.VERTICAL
    mViewBinding.rvList.layoutManager = staggeredGridLayoutManager
    mViewBinding.rvList.addItemDecoration(GridSpacingItemDecoration(2, 8, true))
}
    override fun getQueryImages(items: LiveData<PagedList<ImageQueryModel.Documents>>) {
        if (::adapter.isInitialized) {
            imm.hideSoftInputFromWindow(mViewBinding.etQuery.windowToken, 0)
            mViewModel?.netWorkState?.observe(this, Observer { it ->
                run {
                            if (it?.status == Status.FAILED) {
                                setLiniManager()
                            }else{
                                adapter.setNetworkState(it)

                        }
                }
            })

            items.observe(this, Observer { it -> run {
                setStaggeredSetting()
                adapter.submitList(it)
            }})
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

    private fun getWidth(): Int {
        val dm = applicationContext.resources.displayMetrics
        return dm.widthPixels
    }
}
