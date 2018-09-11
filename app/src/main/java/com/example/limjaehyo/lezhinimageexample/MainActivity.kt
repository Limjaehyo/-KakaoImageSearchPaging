package com.example.limjaehyo.lezhinimageexample

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.limjaehyo.lezhinimageexample.view.BaseViewModelActivity
import com.example.limjaehyo.lezhinimageexample.viewmodel.ImageQueryViewModel
import io.reactivex.disposables.Disposable

class MainActivity : BaseViewModelActivity<ImageQueryViewModel>(), ImageQueryViewModel.ImageQueryViewModelInterface {

    override fun viewModel(): ImageQueryViewModel {
        val factory = ImageQueryViewModel.ImageQueryViewModelFactory(application, this)
        return ViewModelProviders.of(this, factory)
                .get<ImageQueryViewModel>(ImageQueryViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mViewModel?.getQueryImages("소연")


    }

    override fun showMessageDialog(msg: String) {
        super.showCustomDialog(this, msg).show()
    }

    override fun putDisposableMap(tag: String, disposable: Disposable) {
        super.putDisposableMap(tag, disposable)
    }
}
