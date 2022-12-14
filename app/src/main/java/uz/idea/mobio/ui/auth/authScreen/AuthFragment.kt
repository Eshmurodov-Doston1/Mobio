package uz.idea.mobio.ui.auth.authScreen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.mobio.R
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.databinding.DialogBinding
import uz.idea.mobio.databinding.FragmentAuthBinding
import uz.idea.mobio.models.auth.AuthReq
import uz.idea.mobio.models.auth.ResAuth
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.utils.appConstant.AppConstant
import uz.idea.mobio.utils.appConstant.AppConstant.LOGIN_ERROR
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.authVm.AuthViewModel


@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {
    private val binding:FragmentAuthBinding by viewBinding()
    private lateinit var authActivity: AuthActivity
    private var isPhone:Boolean?=false
    private var isPassword:Boolean?=false
    private val authViewModel:AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            phoneNumber.addTextChangedListener { text ->
                if (text.toString().isNotEmptyOrNull()){
                    isPhone = true
                    if (text.toString().trim().length==9){
                        password.requestFocus()
                    }
                } else {
                    isPhone = false
                }
                buttonCreate()
            }
            password.addTextChangedListener { passwordText->
                if (passwordText.toString().isNotEmptyOrNull()){
                    isPassword = passwordText.toString().trim().length>=6
                }
                buttonCreate()
            }

            // login button click
            login.setOnClickListener {
              loginMethode()
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun loginMethode(){
        binding.apply {
            val phone = "998${phoneNumber.text.toString().trim()}"
            val password = password.text.toString().trim()
            val authReq = AuthReq(password,phone)
            authViewModel.authMethode(authReq)
            lifecycleScope.launchWhenCreated {
                authViewModel.authData.collect { result->
                    when(result){
                        is ResponseState.Success->{
                            hideLoadingLoginBtn()
                            val resAuth = result.data?.parseClass(ResAuth::class.java)
                            authViewModel.saveSharedPref(resAuth)
                            requireActivity().startNewActivity(MainActivity::class.java)
                            requireActivity().finish()
                        }
                        is ResponseState.Error->{
                            hideLoadingLoginBtn()
                            if (result.errorCode == NO_INTERNET) {
                                authActivity.noInternetSnackBar()
                            } else {
                                errorData(result.liveError){ clickPos ->
                                    if (clickPos==1) {
                                        loginMethode()
                                    }
                                    result.liveError?.removeObservers(viewLifecycleOwner)
                                    authViewModel.clearErrorTable()
                                }
                            }

                        }
                        is ResponseState.Loading->{
                            loadingLoginBtn()
                        }
                    }
                }
            }
        }
    }


    private val create:AlertDialog by lazy { AlertDialog.Builder(requireContext()).create() }
    private fun errorData(liveError: LiveData<List<ErrorEntity>>?, onClick:(clickPos:Int)->Unit){
        val bindingDialog = DialogBinding.inflate(layoutInflater)
        create.setView(bindingDialog.root)

        var errorMessage = ""
        liveError?.observe(viewLifecycleOwner){ listError->
           try {
               listError?.onEach { errorEntity ->
                   if(errorEntity.error!=null){
                       val jsonObject = JsonParser.parseReader(errorEntity.error.reader()).asJsonObject
                       if (jsonObject.has(AppConstant.ERRORS)){
                           val jsonArray = jsonObject.get(AppConstant.ERRORS).asJsonArray
                           for (i in 0 until jsonArray.size()){
                               if (jsonArray.get(i).asJsonObject.has(AppConstant.MESSAGE)){
                                   errorMessage += jsonArray.get(i).asJsonObject.get(
                                       AppConstant.MESSAGE
                                   ).toString() + " \n"
                               }
                           }
                       }else if (jsonObject.has(AppConstant.PASSWORD)){
                           val asJsonArray = jsonObject.get(AppConstant.PASSWORD).asJsonArray
                           for (i in 0 until asJsonArray.size()){
                               errorMessage += asJsonArray.get(i).toString() + " \n"
                           }
                       }
                   }
               }
               bindingDialog.message.text = errorMessage
           }catch (e:Exception){
               e.printStackTrace()
           }
        }

        bindingDialog.cancel.setOnClickListener {
            create.dismiss()
            onClick.invoke(0)
        }
        bindingDialog.okBtn.setOnClickListener {
            create.dismiss()
            onClick.invoke(1)

        }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        create.setCancelable(false)
        create.show()
    }

    private fun loadingLoginBtn(){
        binding.apply {
            login.attachTextChangeAnimator()
            login.showProgress {
                buttonTextRes = R.string.loading
                progressColor = Color.WHITE
            }
            login.enabledFalse()
        }
    }

    private fun hideLoadingLoginBtn(){
        binding.apply {
            login.hideProgress(R.string.login_btn)
            login.enabled()
        }
    }

    private fun buttonCreate(){
        if (isPhone == true && isPassword == true){
            binding.login.setBackgroundResource(R.drawable.button_back)
            binding.login.enabled()
        } else {
            binding.login.setBackgroundResource(R.drawable.btn_no_enabled)
            binding.login.enabledFalse()
        }
    }


    override fun onResume() {
        super.onResume()
        binding.apply {
            if (authViewModel.mysharedPref.phone.isNotEmptyOrNull() && authViewModel.mysharedPref.password.isNotEmptyOrNull()){
                phoneNumber.setText(authViewModel.mysharedPref.phone?.substring(2))
                password.setText(authViewModel.mysharedPref.password)
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        authActivity = context as AuthActivity
    }
}