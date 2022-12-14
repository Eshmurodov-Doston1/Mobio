package uz.idea.mobio.adapters.genericPagingAdapter

import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

interface Holder {
    fun <T> onBind(
        data:T,
        position:Int,
        @LayoutRes layoutRes:Int,
        onClick:(data:T,position:Int,clickType:Int,viewBinding:ViewBinding)->Unit
    )
}