package uz.idea.mobio.models.categoryProductModel

data class DataX(
    val created_at: Any,
    val photo: Photo?=null,
    val productss: Productss?=null,
    val rate: List<Rate>?=null,
    val shop_category_id: Int,
    val shop_product_id: Int,
    val updated_at: Any
)