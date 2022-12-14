package uz.idea.mobio.models.register

data class RegisterReq(
    val email: String,
    val name: String,
    val password: String,
    val phone: String
)