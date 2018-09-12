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
import android.view.View
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
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.CompletableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit


class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {


    private lateinit var adapter: ImageAdapter
    private lateinit var mViewBinding: ActivityMainBinding
    private lateinit var imm: InputMethodManager

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

        mViewModel?.dataLayoutSubject?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { visibility ->
                    if (visibility) {
                        mViewBinding.tvMsg.visibility = View.VISIBLE
                        mViewBinding.rvList.visibility = View.GONE
                    } else {
                        mViewBinding.tvMsg.visibility = View.GONE
                        mViewBinding.rvList.visibility = View.VISIBLE
                    }
                }
        getDisposable().add(RxTextView.textChanges(mViewBinding.etQuery)
                .throttleLast(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { text ->
                            Log.e("text",text.toString())
                            if (text.isNotEmpty()) {

                                removeDisposable("list")
                                mViewModel?.dataLayoutSubject?.onNext(mViewBinding.tvMsg.visibility != View.VISIBLE)
                                mViewModel?.getQueryImagesPaging(text.toString(), "recency")
                                mViewModel?.dataLayoutSubject?.onNext(false)
                            } else {
                                setMessageTextSetting("검색어를 입력해주세요")
                                mViewModel?.dataLayoutSubject?.onNext(true)
                            }
                        }
                ) { th -> run { Log.e("textChanges", th.message) } })

        mViewModel?.keybordSubject?.observeOn(AndroidSchedulers.mainThread())
                ?.throttleLast(1,TimeUnit.SECONDS)
                ?.subscribe { _ ->
                    imm.hideSoftInputFromWindow(mViewBinding.etQuery.windowToken, 0)
                }

    }



    private fun adapterInit() {
        adapter = ImageAdapter(this,mViewModel?.keybordSubject!!) {
            mViewModel?.retry()
        }
        setStaggeredSetting()

        mViewBinding.rvList.adapter = adapter
    }

    private fun setStaggeredSetting() {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, 1)
        staggeredGridLayoutManager.orientation = StaggeredGridLayoutManager.VERTICAL
        mViewBinding.rvList.layoutManager = staggeredGridLayoutManager
        mViewBinding.rvList.addItemDecoration(GridSpacingItemDecoration(2, 8, true))
    }

    override fun getQueryImages(items: LiveData<PagedList<ImageQueryModel.Documents>>) {
        if (::adapter.isInitialized) {

            mViewModel?.netWorkState?.observe(this, Observer { it ->
                run {
                    if (it?.status == Status.FAILED) {
                        setMessageTextSetting(it.message!!)
                        mViewModel?.dataLayoutSubject?.onNext(true)
                    } else {
//                        adapter.setNetworkState(it)

                    }
                }
            })
            mViewModel?.dataState?.observe(this, Observer { it ->
                run {
                    if (it == true) {
                        setMessageTextSetting("검색 결과가 없습니다.")
                        mViewModel?.dataLayoutSubject?.onNext(true)
                    }else{
                        /*putDisposableMap("keybord", Completable.create { emitter: CompletableEmitter -> emitter.onComplete() }
                                .delay(1200,TimeUnit.MILLISECONDS)
                                .subscribe({  imm.hideSoftInputFromWindow(mViewBinding.etQuery.windowToken, 0) }
                                        , { t: Throwable -> Log.e("keybord",t.message)  }))*/

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
        mViewBinding.tvMsg.text = msg
    }

    override fun showMessageDialog(msg: String) {
        super.showCustomDialog(this, msg).show()
    }

    override fun putDisposableMap(tag: String, disposable: Disposable) {
        super.putDisposableMap(tag, disposable)
    }


}
