package uz.idea.mobio.adapters.genericRvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import uz.idea.mobio.adapters.GenericDiffUtil
import uz.idea.mobio.adapters.genericPagingAdapter.PagingViewHolder

class GenericRvAdapter<T : Any>(
    @LayoutRes private val layoutRes:Int,
    private val onCLick:(data:T,position:Int,clickType:Int,viewBiding:ViewBinding)->Unit
):ListAdapter<T,PagingViewHolder>(GenericDiffUtil<T>()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutRes,parent,false)
        return PagingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        holder.onBind(getItem(position),position,layoutRes,onCLick)
    }
}