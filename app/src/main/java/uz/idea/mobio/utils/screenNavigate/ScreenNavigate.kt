package uz.idea.mobio.utils.screenNavigate

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import uz.idea.mobio.R
import uz.idea.mobio.utils.appConstant.AppConstant.PRODUCT_ID
import uz.idea.mobio.utils.appConstant.AppConstant.TITLE_NAME

class ScreenNavigate(
    private val navController: NavController
) {
    fun createCategoryProduct(productId:Int,title:String){
        val bundle = Bundle()
        bundle.putSerializable(PRODUCT_ID,productId)
        bundle.putSerializable(TITLE_NAME,title)
        navController.navigate(R.id.action_nav_home_to_categoryProductFragment,bundle,animationViewCreateRight())
    }

    fun createCategoryProductInOtherFragments(productId:Int,title:String){
        val bundle = Bundle()
        bundle.putSerializable(PRODUCT_ID,productId)
        bundle.putSerializable(TITLE_NAME,title)
        navController.navigate(R.id.categoryProductFragment,bundle,animationViewCreateRight())
    }

    fun createProductInfoScreen(productId: Int){
        val bundle = Bundle()
        bundle.putInt(PRODUCT_ID,productId)
        navController.navigate(R.id.productInfoFragment,bundle,animationViewCreateRight())
    }


    fun createCategoryScreen(){
        navController.navigate(R.id.category,Bundle(),animationViewCreateRight())
    }

    private fun animationViewCreateRight(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.enter)
            .setExitAnim(R.anim.exit)
            .setPopEnterAnim(R.anim.pop_enter)
            .setPopExitAnim(R.anim.pop_exit)
            .build()
    }
}