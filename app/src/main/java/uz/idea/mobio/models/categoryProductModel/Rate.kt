package uz.idea.mobio.models.categoryProductModel

data class Rate(
    val author: String,
    val comment: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val model_id: Int,
    val model_type: String,
    val rating: Double,
    val updated_at: String
)