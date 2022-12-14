package uz.idea.mobio.utils.appConstant

object AppConstant {
    val EMPTY_MAP = HashMap<String,String>()
    const val DATABASE_NAME = "Mobio.db"

    // TODO: Http methode
    const val API = "api"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CATEGORY = "category"
    const val NEW_PRODUCT = "new-product"
    const val MAIN_PRODUCT_PATH = "products/main"
    const val MAIN_PRODUCT_CHILD_PATH = "products/mainChild"
    const val CATEGORY_PRODUCT_PATH = "productbycategory"
    const val PRODUCT_ID_PATH = "product"
    const val BASKET_LIST_PATH = "cart/list"
    const val UPDATE_BASKET = "cart/update"
    const val DELETE_BASKET = "cart/delete"
    const val ADD_BASKET = "cart/add"
    const val USER_DATA = "user/get"
    const val LOGOUT = "logout"
    const val SEARCH_PATH = "search"
    const val IMAGE_URL = "https://dev.mobio.uz/storage"
    const val DEFAULT_PAGE_SIZE = 10

    // TODO: SharedPreference
    const val COMPANY_NAME = "Mobio"
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val TOKEN_TYPE = "token_type"
    const val EMPTY = ""
    const val LANG = "language"
    const val PHONE = "phone"
    const val THEME = "theme"
    const val UZB ="uz"
    const val RU ="ru"
    const val EN ="en"

    // TODO: Locale Error

    const val LOGIN_ERROR = -1
    const val NO_INTERNET = -2
    const val REGISTER_ERROR = -3
    const val ERRORS = "errors"
    const val APPLICATION_JSON = "application/json"

    const val PASSWORD = "password"
    const val MESSAGE = "message"

    // TODO: Locale click
    const val DEFAULT_CLICK = 1
    const val CLICK_FAVORITES = 2
    const val CLICK_BASKET = 3
    const val CLICK_ADD = 4
    const val CLICK_MINUS = 5

    // TODO: Locale Bundle
    const val PRODUCT_ID = "product_id"
    const val TITLE_NAME = "title_name"
}