package uz.idea.mobio.models.categoryModel

data class Data(
    val created_at: String,
    val description: Any,
    val id: Int,
    val is_visible: Boolean,
    val name: String,
    val parent_id: Int,
    val position: Int,
    val seo_description: Any,
    val seo_title: Any,
    val slug: String,
    val updated_at: String
)