package com.example.limjaehyo.lezhinimageexample.view.adapter

import android.arch.paging.PagedListAdapter
import android.net.Uri
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.limjaehyo.lezhinimageexample.R
import com.example.limjaehyo.lezhinimageexample.model.datasource.ImageQueryModel
import com.example.limjaehyo.lezhinimageexample.model.datasource.NetworkState
import com.example.limjaehyo.lezhinimageexample.model.datasource.Status
import kotlinx.android.synthetic.main.item_network_state.view.*
import com.facebook.drawee.view.SimpleDraweeView




class ImageAdapter(private val retryCallback: () -> Unit) : PagedListAdapter<ImageQueryModel.Documents,RecyclerView.ViewHolder>(ImageDiffCallback){
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
            R.layout.item_image -> (holder as ImageItemViewHolder).bindTo(getItem(position))
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
            //error message
            itemView.errorMessageTextView.visibility = if (networkState?.message != null) View.VISIBLE else View.GONE
            if (networkState?.message != null) {
                itemView.errorMessageTextView.text = networkState.message
            }

            //loading and retry
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

        fun bindTo(imageDoc: ImageQueryModel.Documents?) {
            val uri = Uri.parse(imageDoc?.thumbnail_url)
            Log.e("aa","${imageDoc?.thumbnail_url}")
            val simpleDraweeView = itemView.findViewById(R.id.my_image_view) as SimpleDraweeView
            simpleDraweeView.setImageURI(uri)
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