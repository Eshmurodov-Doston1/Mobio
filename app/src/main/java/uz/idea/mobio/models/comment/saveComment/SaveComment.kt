package uz.idea.mobio.models.comment.saveComment

data class SaveComment(
    val comment: String,
    val email: String?,
    val model_id: String,
    val model_type: String?,
    val name: String,
    val rating: Double
)