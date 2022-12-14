package uz.idea.mobio.models.auth

data class AuthReq(
    val password: String,
    val phone: String
)