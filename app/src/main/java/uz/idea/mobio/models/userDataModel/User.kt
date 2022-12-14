package uz.idea.mobio.models.userDataModel

data class User(
    val created_at: String,
    val current_team_id: Any,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val name: String,
    val phone: String,
    val profile_photo_path: String,
    val profile_photo_url: String,
    val two_factor_confirmed_at: Any,
    val updated_at: String
)