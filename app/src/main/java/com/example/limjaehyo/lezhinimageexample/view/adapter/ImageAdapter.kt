package com.example.limjaehyo.lezhinimageexample.view.adapter

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.model.datasource.NetworkState
import com.example.limjaehyo.lezhinimageexample.model.datasource.Status
import com.example.limjaehyo.lezhinimageexample.util.CommonUtil
import com.example.limjaehyo.lezhinimageexample.view.ImageDetailActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import kotlinx.android.synthetic.main.item_network_state.view.*


class ImageAdapter(private val context: Context,private val retryCallback: () -> Unit) : PagedListAdapter<ImageQueryModel.Documents, RecyclerView.ViewHolder>(ImageDiffCallback) {
    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_image -> ImageItemViewHolder.create(parent)
            R.layout.item_network_state -> NetworkStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_image -> (holder as ImageItemViewHolder).bindTo(context, getItem(position))
            R.layout.item_network_state -> (holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_network_state
        } else {
            R.layout.item_image
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        if (currentList != null) {
            if (currentList!!.size != 0) {
                val previousState = this.networkState
                val hadExtraRow = hasExtraRow()
                this.networkState = newNetworkState
                val hasExtraRow = hasExtraRow()
                if (hadExtraRow != hasExtraRow) {
                    if (hadExtraRow) {
                        notifyItemRemoved(super.getItemCount())
                    } else {
                        notifyItemInserted(super.getItemCount())
                    }
                } else if (hasExtraRow && previousState !== newNetworkState) {
                    notifyItemChanged(itemCount - 1)
                }
            }
        }
    }



    companion object {
        val ImageDiffCallback = object : DiffUtil.ItemCallback<ImageQueryModel.Documents>() {

            override fun areItemsTheSame(oldItem: ImageQueryModel.Documents, newItem: ImageQueryModel.Documents): Boolean {
                return oldItem.datetime == newItem.datetime
            }

            override fun areContentsTheSame(oldItem: ImageQueryModel.Documents, newItem: ImageQueryModel.Documents): Boolean {
                return oldItem == newItem
            }
        }
    }

    class NetworkStateViewHolder(val view: View, private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(view) {

        init {
            itemView.retryLoadingButton.setOnClickListener { retryCallback() }
        }

        fun bindTo(networkState: NetworkState?) {
            itemView.errorMessageTextView.visibility = if (networkState?.message != null) View.VISIBLE else View.GONE
            if (networkState?.message != null) {
                itemView.errorMessageTextView.text = networkState.message
            }

            itemView.retryLoadingButton.visibility = if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
            itemView.loadingProgressBar.visibility = if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE
        }

        companion object {
            fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_network_state, parent, false)
                return NetworkStateViewHolder(view, retryCallback)
            }
        }

    }

    class ImageItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindTo(context: Context, imageDoc: ImageQueryModel.Documents?) {
            val simpleDraweeView = itemView.findViewById<SimpleDraweeView>(R.id.my_image_view)
            val controllerBuilder = Fresco.newDraweeControllerBuilder()

            controllerBuilder.setUri(imageDoc?.image_url)
            controllerBuilder.oldController = simpleDraweeView.controller
            val ratioHeight = CommonUtil.getRatioHeight(context, imageDoc?.height?.toInt()
                    ?: 0, imageDoc?.width?.toInt() ?: 0)
            val draweeViewLayoutParams = simpleDraweeView.layoutParams
            val progressBarView = itemView.findViewById<ContentLoadingProgressBar>(R.id.progress_view)
            draweeViewLayoutParams.width = CommonUtil.getWidth(context)
            draweeViewLayoutParams.height = ratioHeight / 2
            simpleDraweeView.layoutParams = draweeViewLayoutParams

            controllerBuilder.controllerListener = object : BaseControllerListener<ImageInfo>() {
                override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                    super.onFinalImageSet(id, imageInfo, animatable)
                    if (imageInfo == null) {
                        return
                    }
                    progressBarView.visibility = View.GONE
                }
            }
            simpleDraweeView.controller = controllerBuilder.build()

            simpleDraweeView.setOnClickListener { _: View? -> context.startActivity( Intent(context, ImageDetailActivity::class.java).putExtra("item", imageDoc)) }

        }

        companion object {
            fun create(parent: ViewGroup): ImageItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_image, parent, false)
                return ImageItemViewHolder(view)
            }
        }

    }


}