package uz.idea.mobio.adapters.mainProductAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uz.idea.mobio.adapters.GenericDiffUtil
import uz.idea.mobio.databinding.ItemCategoryProductBinding
import uz.idea.mobio.models.mainProduct.Data
import uz.idea.mobio.models.mainProduct.Product

class MainProductAdapter(
    val onItemClickListener: OnItemClickListener
):ListAdapter<Data, MainProductAdapter.Vh>(GenericDiffUtil<Data>()) {
    inner class Vh(val binding:ItemCategoryProductBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(data: Data,position: Int){
            binding.categoryTv.text = data.title
            val mainProductChildAdapter = MainProductChildAdapter(this@MainProductAdapter)
            mainProductChildAdapter.submitList(data.products)
            binding.rvProduct.adapter = mainProductChildAdapter
            binding.rvProduct.setItemViewCacheSize(data.products.size)
            binding.linear.setOnClickListener {
                onItemClickListener.onItemClick(data,position,binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemCategoryProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
      holder.onBind(getItem(position),position)
    }
    interface OnItemClickListener{
        fun onItemClick(data:Data,position:Int,viewBinding: ViewBinding)
        fun onItemChildClick(product:Product,position:Int,viewBinding: ViewBinding)
        fun onItemChildClickFavorites(product:Product,position:Int,viewBinding: ViewBinding)
        fun onItemChildClickBasket(product:Product,position:Int,viewBinding:ViewBinding)
    }
}