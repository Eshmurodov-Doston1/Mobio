package uz.idea.mobio.models.favoritesData.resSaveFavorite

data class Data(
    val created_at: String,
    val id: Int,
    val is_active: Int,
    val product_id: String,
    val updated_at: String,
    val user_id: Int
)