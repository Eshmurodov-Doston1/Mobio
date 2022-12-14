package uz.idea.mobio.models.basketModel.addBasket.addBasketRes

data class Data(
    val amount: String,
    val count: Int,
    val created_at: String,
    val id: Int,
    val is_active: Int,
    val is_being: Int,
    val is_delivered: Int,
    val is_drive: Int,
    val product_id: String,
    val updated_at: String,
    val user_id: Int
)