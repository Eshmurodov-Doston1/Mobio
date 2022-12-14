package uz.idea.mobio.models.auth

data class ResAuth(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val token_type: String
)