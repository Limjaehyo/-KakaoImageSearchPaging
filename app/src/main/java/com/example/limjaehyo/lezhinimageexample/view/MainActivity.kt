package com.example.limjaehyo.lezhinimageexample.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.databinding.ActivityMain2Binding
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.view.adapter.IamgeAdpter
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {


    private lateinit var adapter: IamgeAdpter
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

//        mViewModel?.getQueryImages("소연")

        mViewBinding.rvList.adapter = IamgeAdpter {
            mViewModel?.retry()
        }

    }

    override fun getqueryImages(items: LiveData<PagedList<ImageQueryModel.Documents>>) {

        mViewModel?.netWorkState?.observe(this, Observer { adapter.setNetworkState(it) })
        items.observe(this, Observer { adapter.submitList(it) })
    }

    override fun showMessageDialog(msg: String) {
        super.showCustomDialog(this, msg).show()
    }

    override fun putDisposableMap(tag: String, disposable: Disposable) {
        super.putDisposableMap(tag, disposable)
    }
}
