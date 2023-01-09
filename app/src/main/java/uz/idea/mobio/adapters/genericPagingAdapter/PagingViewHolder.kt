package uz.idea.mobio.adapters.genericPagingAdapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uz.idea.mobio.R
import uz.idea.mobio.databinding.*
import uz.idea.mobio.models.categoryModel.Data
import uz.idea.mobio.models.locale.LocaleCommentModel
import uz.idea.mobio.models.locale.LocalePager
import uz.idea.mobio.models.locale.user_image_url1
import uz.idea.mobio.models.newProductModel.DataX
import uz.idea.mobio.models.productModel.Photo
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_ADD
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_BASKET
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_FAVORITES
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_MINUS
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_CLICK
import uz.idea.mobio.utils.appConstant.AppConstant.IMAGE_URL
import uz.idea.mobio.utils.extension.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PagingViewHolder(var itemView:View):RecyclerView.ViewHolder(itemView),Holder {
    override fun <T> onBind(
        data: T,
        position: Int,
        layoutRes: Int,
        onClick: (data: T, position: Int, clickType:Int,viewBinding: ViewBinding) -> Unit
    ) {
       when(layoutRes){
           R.layout.item_category_main->{
               itemCategory(data,position,onClick)
           }
           R.layout.item_viewpager->{
               itemViewPager(data,position,onClick)
           }
           R.layout.item_product_category->{
               itemProductCategory(data,position,onClick)
           }
           R.layout.item_category->{
               itemCategoryMain(data,position,onClick)
           }
           R.layout.item_viewpager_product->{
               itemViewPagerProduct(data,position,onClick)
           }
           R.layout.basket_item->{
               itemBasket(data,position,onClick)
           }
           R.layout.item_comment->{
               itemComment(data,position,onClick)
           }
           R.layout.item_introduction->{
               itemIntroduction(data,position,onClick)
           }
       }
    }

    // item introduction
    private fun <T> itemIntroduction(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int, viewBinding:ViewBinding) -> Unit){
        val binding = ItemIntroductionBinding.bind(itemView)
        if (data is LocalePager){
            binding.textIntroduction.text = data.message
            binding.imageIntroduction.setImageResource(data.image)
        }
    }

    // item comment
    @SuppressLint("SimpleDateFormat")
    private fun <T> itemComment(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int, viewBinding:ViewBinding) -> Unit){
        val binding = ItemCommentBinding.bind(itemView)
        if (data is uz.idea.mobio.models.comment.commentList.Data){
            binding.userImage.imageData(user_image_url1,itemView.context)
            binding.nameUser.text = data.author
            binding.rate.setStar(data.rating.toFloat())
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date = format.parse(data.created_at)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:MM", Locale.getDefault())
            val dateFormat = simpleDateFormat.format(date)
             binding.timeComment.text = dateFormat
            binding.commentTv.text = data.comment
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK,binding)
            }
        }
    }



    // item basket
    @SuppressLint("SetTextI18n")
    private fun <T> itemBasket(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int,viewBinding:ViewBinding) -> Unit){
        val binding = BasketItemBinding.bind(itemView)
        if (data is uz.idea.mobio.models.basketModel.basketList.DataX){
            binding.imageProduct.imageData("$IMAGE_URL/${data.product.photo.id}/${data.product.photo.file_name}",itemView.context)
            binding.nameProduct.text = data.product.name
            binding.price.text = data.product.price + " ${itemView.context.getString(R.string.pay_type)}"
            val count = data.count
            if (count>100) binding.count.text = "99+" else binding.count.text = count.toString()
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK,binding)
            }
            binding.plus.setOnClickListener {
                onClick.invoke(data,position, CLICK_ADD,binding)
            }
            binding.minus.setOnClickListener {
                onClick.invoke(data,position, CLICK_MINUS,binding)
            }
        }
    }

    // item  Viewpager product
    private fun <T> itemViewPagerProduct(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int,viewBinding:ViewBinding) -> Unit){
        val binding = ItemViewpagerProductBinding.bind(itemView)
        if (data is Photo){
            binding.imageProduct.imageData("$IMAGE_URL/${data.id}/${data.file_name}",itemView.context)
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK,binding)
            }
        }
    }

    // item category
    private fun <T> itemCategoryMain(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int,viewBinding:ViewBinding) -> Unit){
        val binding = ItemCategoryBinding.bind(itemView)
        if (data is Data){
            val rnd = Random();
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            binding.cardCategory.setCardBackgroundColor(color)
            binding.titleText.text = data.name
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK,binding)
            }
        }
    }
    //itemProductCategory
    @SuppressLint("SetTextI18n")
    private fun <T> itemProductCategory(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int,viewBinding:ViewBinding) -> Unit){
        val binding = ItemProductCategoryBinding.bind(itemView)
        if (data is uz.idea.mobio.models.categoryProductModel.DataX){
            if (data.photo?.file_name?.substring(data.photo.file_name.length-5) == ".avif"){
                binding.imageProduct.gone()
                binding.shimmer.visible()
            }else{
                binding.imageProduct.visible()
                binding.shimmer.gone()
                if (data.photo!=null){
                    binding.imageProduct.imageData("$IMAGE_URL/${data.photo.id}/${data.photo.file_name}",itemView.context)
                }
            }
            binding.cardFavorite.setOnClickListener {
                onClick.invoke(data,position,CLICK_FAVORITES,binding)
            }
            binding.cardBasket.setOnClickListener {
                onClick.invoke(data,position, CLICK_BASKET,binding)
            }
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK,binding)
            }
            val price = data.productss?.price
            val oldPrice = data.productss?.old_price
            if (price!=null){
                binding.summa.text = price.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
            } else {
                binding.summa.gone()
            }
            if (oldPrice!=null){
                binding.realSumma.text = oldPrice.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
            }else{
                binding.realSumma.gone()
            }
            if (data.rate!=null && data.rate.isNotEmpty()){
                var rate = 0.0
                data.rate.onEach { rateData->
                    rate += rateData.rating
                }
                binding.rate.text = String.format("%.2f", (rate/(data.rate.size)))
            } else {
                binding.starCard.gone()
            }
            if (oldPrice!=null && price != null && oldPrice>price){
                val percent = 100 - ((price/oldPrice)*100)
                binding.discountText.text = "${String.format("%.2f", percent)} %"
            }else{
                binding.cardDiscount.gone()
                binding.realSumma.gone()
            }
            binding.nameProduct.text = data.productss?.name
        } else if(data is uz.idea.mobio.models.searchModel.DataX){
            if (data.photos!=null && data.photos.isNotEmpty()){
                if (data.photos[0]?.file_name?.substring((data.photos[0]?.file_name?.length?:0)-5) == ".avif"){
                    binding.imageProduct.gone()
                    binding.shimmer.visible()
                }else{
                    binding.imageProduct.visible()
                    binding.shimmer.gone()
                    if (data.photos[0]!=null){
                        binding.imageProduct.imageData("$IMAGE_URL/${data.photos[0]?.id}/${data.photos[0]?.file_name}",itemView.context)
                    }
                }
            }
            binding.cardFavorite.setOnClickListener {
                onClick.invoke(data,position,CLICK_FAVORITES,binding)
            }
            binding.cardBasket.setOnClickListener {
                onClick.invoke(data,position, CLICK_BASKET,binding)
            }
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK,binding)
            }
            val price = data?.price
            val oldPrice = data?.old_price
            if (price!=null){
                binding.summa.text = price.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
            } else {
                binding.summa.gone()
            }
            if (oldPrice!=null){
                binding.realSumma.text = oldPrice.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
            }else{
                binding.realSumma.gone()
            }

            binding.starCard.gone()

            if (oldPrice!=null && price != null && oldPrice>price){
                val percent = 100 - ((price/oldPrice)*100)
                binding.discountText.text = "${String.format("%.2f", percent)} %"
            }else{
                binding.cardDiscount.gone()
                binding.realSumma.gone()
            }
            binding.nameProduct.text = data.name
        }
    }

    // item Viewpager
    @SuppressLint("SetTextI18n")
    private fun <T> itemViewPager(data:T, position: Int, onClick: (data: T, position: Int, clickType:Int,viewBinding:ViewBinding) -> Unit){
        val binding = ItemViewpagerBinding.bind(itemView)
        if (data is DataX){
            val oldPrice = data.old_price
            val price = data.price
            if (oldPrice != null && oldPrice > (price ?: 0.0)){
                val percent = price?.div(oldPrice)
                binding.textPercent.text = ((percent?:0.0) * 100).numberFormatter() + "% ${itemView.context.getString(R.string.discount)}"
                binding.price.visible()
                binding.price.text = "${itemView.context.getString(R.string.summa)} " + price?.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
            }else{
                binding.price.gone()
                binding.textPercent.text = price?.numberFormatter() + " ${itemView.context.getString(R.string.pay_type)}"
            }
            binding.imageNewProduct.imageData("$IMAGE_URL/${data.photos[0].id}/${data.photos[0].file_name}",itemView.context)
        }
        itemView.setOnClickListener {
            onClick.invoke(data,position, DEFAULT_CLICK,binding)
        }

        binding.favorite.setOnClickListener {
         onClick.invoke(data,position,CLICK_FAVORITES,binding)
        }

        binding.buttonPay.setOnClickListener {
            onClick.invoke(data,position, DEFAULT_CLICK,binding)
        }
    }

    // item category
    private fun <T> itemCategory(data:T,position: Int, onClick: (data: T, position: Int, clickType:Int,viewBinding:ViewBinding) -> Unit){
        val binding = ItemCategoryMainBinding.bind(itemView)
        val rnd = Random();
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        binding.card.setCardBackgroundColor(color)
        if (data is Data){
            binding.titleCategory.text = data.name
        }
        itemView.setOnClickListener {
            onClick.invoke(data,position,DEFAULT_CLICK,binding)
        }
    }
}