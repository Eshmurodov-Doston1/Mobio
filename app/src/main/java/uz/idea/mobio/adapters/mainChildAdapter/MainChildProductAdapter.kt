package uz.idea.mobio.adapters.mainChildAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.idea.mobio.adapters.GenericDiffUtil
import uz.idea.mobio.databinding.ItemCategoryProduct1Binding
import uz.idea.mobio.models.mainChildModel.Data
import uz.idea.mobio.models.mainChildModel.Product
import java.util.*

class MainChildProductAdapter(
    val onItemClickListener: OnItemClickListener
):ListAdapter<Data, MainChildProductAdapter.Vh>(GenericDiffUtil<Data>()) {
    inner class Vh(val binding:ItemCategoryProduct1Binding):RecyclerView.ViewHolder(binding.root){
        fun onBind(data: Data,position: Int){
            binding.categoryTv.text = data.title
            val mainProductChildAdapter = MainChildProductChildAdapter(this@MainChildProductAdapter)
            val listProduct = LinkedList<Product>()
            data.products.onEach {  product->
                if (product?.productsss!=null && product.productsss.isNotEmpty()){
                    listProduct.add(product)
                }
            }
            mainProductChildAdapter.submitList(listProduct)
            binding.rvProduct.adapter = mainProductChildAdapter
            binding.rvProduct.setItemViewCacheSize(data.products.size)
            binding.linear.setOnClickListener {
                onItemClickListener.onItemClick(data,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemCategoryProduct1Binding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
      holder.onBind(getItem(position),position)
    }
    interface OnItemClickListener{
        fun onItemClick(data:Data,position:Int)
        fun onItemChildClick(product:Product,position:Int)
    }
}