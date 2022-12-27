package uz.idea.mobio.models.comment

data class Data(
    val author: String,
    val comment: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val model_id: Int,
    val model_type: String,
    val rating: Int,
    val updated_at: String
)