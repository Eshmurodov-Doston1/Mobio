package uz.idea.mobio.models.register

data class User(
    val created_at: String,
    val email: String,
    val id: Int,
    val name: String,
    val phone: String,
    val profile_photo_path: String,
    val profile_photo_url: String,
    val updated_at: String
)