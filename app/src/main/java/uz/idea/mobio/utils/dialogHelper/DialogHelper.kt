package uz.idea.mobio.utils.dialogHelper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import uz.idea.mobio.R
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.databinding.DialogBinding
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.extension.startNewActivity
import uz.idea.mobio.utils.sharedPreferences.MySharedPreferences

class DialogHelper(
    private val activity: MainActivity
) {
    private val alertDialog = AlertDialog.Builder(activity)
    private val create = alertDialog.create()

    fun errorDialog(
        errorCode:Int?,
        liveError:LiveData<List<ErrorEntity>>?,
        mySharedPreferences: MySharedPreferences,
        onClick:(clickType:Int)->Unit
    ){
        val binding = DialogBinding.inflate(activity.layoutInflater)
        create.setView(binding.root)
        when(errorCode){
            in 400..499->{
                liveError?.observe(activity){ listError->
                    var errorMessage = ""
                    listError.onEach {  errorEntity ->
                        if (errorEntity.errorCode==401){
                            mySharedPreferences.clearToken()
                            activity.startNewActivity(AuthActivity::class.java)
                            activity.finish()
                        }else{
                            errorMessage += "${errorEntity.error} \n"
                        }
                    }
                    binding.message.text = errorMessage
                }
            }
            in 500..599->{
                binding.message.text = activity.getString(R.string.server_error)
            }
            else->{
                liveError?.observe(activity){ listError->
                    var errorMessage = ""
                    listError.onEach {  errorEntity ->
                        errorMessage += "${errorEntity.error} \n"
                    }
                    binding.message.text = errorMessage
                }
            }
        }

        binding.okBtn.setOnClickListener {
            onClick.invoke(1)
            create.dismiss()
        }
        binding.cancel.setOnClickListener {
            onClick.invoke(0)
            create.dismiss()
        }
        create.setCancelable(false)
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        create.show()
    }

    fun otherDialog(
        status:Int,
        onClick: (clickType: Int) -> Unit
    ){
        val alertDialog1 = AlertDialog.Builder(activity)
        val create1 = alertDialog1.create()
        val binding = DialogBinding.inflate(activity.layoutInflater)
        create1.setView(binding.root)
        when(status){
            0->{
                binding.lottie.setAnimation(R.raw.log_out)
                binding.message.text = activity.getString(R.string.logout)
                binding.okBtn.text = activity.getString(R.string.accepted)
            }
        }
        binding.okBtn.setOnClickListener {
            onClick.invoke(1)
            create1.dismiss()
        }
        binding.cancel.setOnClickListener {
            onClick.invoke(1)
            create1.dismiss()
        }
        create1.setCancelable(false)
        create1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        create1.show()
    }
}