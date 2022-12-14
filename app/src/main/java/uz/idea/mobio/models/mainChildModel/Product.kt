package uz.idea.mobio.models.mainChildModel

data class Product(
    val created_at: Any,
    val photo: Photo?=null,
    val productsss: List<Productss>?=null,
    val rate: List<Rate>?=null,
    val shop_category_id: Int,
    val shop_product_id: Int,
    val updated_at: Any
)