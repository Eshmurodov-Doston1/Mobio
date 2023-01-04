package uz.idea.mobio.adapters.mainChildAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.idea.mobio.R
import uz.idea.mobio.adapters.GenericDiffUtil
import uz.idea.mobio.databinding.ItemChildProductCategory1Binding
import uz.idea.mobio.models.mainChildModel.Product
import uz.idea.mobio.utils.appConstant.AppConstant.IMAGE_URL
import uz.idea.mobio.utils.extension.*

class MainChildProductChildAdapter(private val mainProductAdapter: MainChildProductAdapter):ListAdapter<Product, MainChildProductChildAdapter.Vh>(GenericDiffUtil<Product>()) {
    inner class Vh(var binding:ItemChildProductCategory1Binding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(product: Product, position:Int){
            itemView.setOnClickListener {
                mainProductAdapter.onItemClickListener.onItemChildClick(product,position)
            }

            if (product.photo!=null){
                if (product.photo.file_name.substring(product.photo.file_name.length-5) == ".avif"){
                    binding.imageProduct.visibility = View.INVISIBLE
                    binding.shimmer.visible()
                    binding.imageProduct.gone()
                } else {
                    binding.imageProduct.imageData("$IMAGE_URL/${product.photo.id}/${product.photo.file_name}",itemView.context)
                }
            } else {
                binding.shimmer.visible()
                binding.imageProduct.gone()
            }

            if (product.productsss!=null && product.productsss.isNotEmpty()){
                binding.dataLinear.visible()
                binding.shimmer1.gone()
                val price = product.productsss[0].price
                if (price!=null){
                    binding.summa.text = price.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
                }
                binding.productName.text = product.productsss[0].name
                var rateStar = 0.0
                if (product.rate!=null && product.rate.isNotEmpty()){
                    binding.iconStar.visible()
                    binding.textRate.visible()
                    product.rate.onEach { rate->
                        rateStar += rate.rating.toFloat()
                    }
                    binding.textRate.text = (rateStar/product.rate.size.toDouble()).toString()
                } else {
                    binding.iconStar.gone()
                    binding.textRate.gone()
                }
                LogData(product.rate.toString())
                var commentCount = 0
                product.rate?.onEach { rate->
                    if (rate.comment.isNotEmptyOrNull()) {
                        commentCount += 1
                    }
                }
                if (commentCount!=0){
                    binding.commentTv.text = "$commentCount ${itemView.context.getString(R.string.comment_text)}"
                }else{
                    binding.commentTv.text = itemView.context.getString(R.string.no_comment)
                }
            }else {
                binding.dataLinear.gone()
                binding.shimmer1.visible()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemChildProductCategory1Binding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position),position)
    }
}