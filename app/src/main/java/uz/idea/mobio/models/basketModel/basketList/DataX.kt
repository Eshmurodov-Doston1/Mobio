package uz.idea.mobio.models.basketModel.basketList

data class DataX(
    val amount: Int,
    val count: Int,
    val created_at: String,
    val id: Int,
    val is_active: Int,
    val is_being: Int,
    val is_delivered: Int,
    val is_drive: Int,
    val order_token: Any,
    val product: Product,
    val product_id: Int,
    val token: String,
    val type_pay: Any,
    val updated_at: String,
    val user_id: Int
)