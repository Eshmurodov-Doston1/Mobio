package uz.idea.mobio.adapters.mainProductAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.idea.mobio.R
import uz.idea.mobio.adapters.GenericDiffUtil
import uz.idea.mobio.databinding.ItemChildProductCategoryBinding
import uz.idea.mobio.models.mainProduct.Product
import uz.idea.mobio.utils.appConstant.AppConstant.IMAGE_URL
import uz.idea.mobio.utils.extension.*

class MainProductChildAdapter(private val mainProductAdapter: MainProductAdapter):ListAdapter<Product, MainProductChildAdapter.Vh>(GenericDiffUtil<Product>()) {
    inner class Vh(var binding:ItemChildProductCategoryBinding):RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(product: Product, position:Int){
            itemView.setOnClickListener {
                mainProductAdapter.onItemClickListener.onItemChildClick(product,position)
            }
            binding.cardFavorite.setOnClickListener {
                mainProductAdapter.onItemClickListener.onItemChildClickFavorites(product,position)
            }
            binding.cardBasket.setOnClickListener {
                mainProductAdapter.onItemClickListener.onItemChildClickBasket(product,position,binding)
            }
            if (product.photo!=null){
                if (product.photo.file_name.substring(product.photo.file_name.length-5) == ".avif"){
                    binding.imageProduct.visibility = View.INVISIBLE
                    binding.shimmer.visible()
                } else {
                    binding.imageProduct.imageData("$IMAGE_URL/${product.photo.id}/${product.photo.file_name}",itemView.context)
                }
            } else {
                binding.imageProduct.visibility = View.INVISIBLE
                binding.shimmer.visible()
            }
            if (product.productsss!=null && product.productsss.isNotEmpty()){
                binding.dataLinear.visible()
                binding.shimmer1.gone()
                val price = product.productsss[0].price
                val oldPrice = product.productsss[0].old_price
                if (price!=null){
                    binding.summa.text = price.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
                }
                if (oldPrice != null){
                    binding.realSumma.text = oldPrice.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
                }
                binding.nameProduct.text = product.productsss[0].name
                var rateStar = 0.0
                if (product.rate!=null && product.rate.isNotEmpty()){
                    binding.starCard.visible()
                    product.rate.onEach { rate->
                        rateStar += rate.rating
                    }
                    binding.rate.text = (rateStar/product.rate.size.toDouble()).toString()
                } else {
                    binding.starCard.gone()
                }
                if (price!=null && oldPrice!=null && oldPrice>price){
                    val percent = 100 - ((price/oldPrice)*100)

                    binding.discountText.text = "${String.format("%.2f", percent)} %"
                    binding.cardDiscount.visible()
                } else {
                    binding.cardDiscount.gone()
                }
            }else {
                binding.dataLinear.gone()
                binding.shimmer1.visible()
                binding.realSumma.gone()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemChildProductCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position),position)
    }
}