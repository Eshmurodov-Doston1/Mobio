package uz.idea.mobio.utils.container

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.databinding.LoadingBinding
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.utils.dialogHelper.DialogHelper
import uz.idea.mobio.utils.screenNavigate.ScreenNavigate
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences

class Container(
    private val owner: LifecycleOwner,
    private val activity: MainActivity,
    val navController: NavController
) {
    private val dialogHelper by lazy {
        DialogHelper(activity)
    }

    fun errorDialog(
        errorCode:Int?,
        liveError:LiveData<List<ErrorEntity>>?,
        mySharedPreferences: MySharedPreferences,
        onClick:(clickType:Int)->Unit
    ){
        dialogHelper.errorDialog(errorCode,liveError,mySharedPreferences,onClick)
    }

    fun otherDialog(
        status:Int,
        onClick: (clickType: Int) -> Unit
    ){
        dialogHelper.otherDialog(status,onClick)
    }

    val screenNavigate by lazy {
        ScreenNavigate(navController)
    }
    private val alertDialog = AlertDialog.Builder(activity)
    private val create = alertDialog.create()
    fun loadingDialog(isLoading:Boolean){
        val bind = LoadingBinding.inflate(activity.layoutInflater)
        create.setView(bind.root)
        if (isLoading){
            create.show()
        }else{
            create.dismiss()
        }
        create.setCancelable(false)
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}