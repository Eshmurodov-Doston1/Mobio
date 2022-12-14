package uz.idea.mobio.adapters.genericPagingAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagingDataAdapter
import androidx.viewbinding.ViewBinding
import uz.idea.mobio.adapters.GenericDiffUtil

class PagingAdapter<T : Any>(
    @LayoutRes private val layoutRes:Int,
    private val onClick:(data:T?,position:Int,clickType:Int,viewBinding:ViewBinding)->Unit
):PagingDataAdapter<T,PagingViewHolder>(GenericDiffUtil()) {
    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        holder.onBind(getItem(position),position,layoutRes, onClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes,parent,false)
        return PagingViewHolder(view)
    }
}