package uz.idea.mobio.models.errors.errorRegister

data class Errors(
    val name: List<String>?=null,
    val password: List<String>?=null,
    val phone: List<String>?=null,
    val email: List<String>?=null
)