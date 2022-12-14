package uz.idea.mobio.models.mainChildModel

data class Productss(
    val backorder: Boolean,
    val barcode: String,
    val cost: String,
    val created_at: String,
    val depth_unit: String,
    val depth_value: String,
    val description: String,
    val featured: Boolean,
    val height_unit: String,
    val height_value: String,
    val id: Int,
    val is_visible: Boolean,
    val name: String,
    val old_price: Double?=null,
    val price: Double?=null,
    val published_at: String,
    val qty: Int,
    val requires_shipping: Boolean,
    val security_stock: Int,
    val seo_description: Any,
    val seo_title: Any,
    val shop_brand_id: Int,
    val sku: String,
    val slug: String,
    val type: Any,
    val updated_at: String,
    val volume_unit: String,
    val volume_value: String,
    val weight_unit: String,
    val weight_value: String,
    val width_unit: String,
    val width_value: String
)