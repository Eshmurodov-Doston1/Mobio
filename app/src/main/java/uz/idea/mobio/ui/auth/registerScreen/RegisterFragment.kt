package uz.idea.mobio.ui.auth.registerScreen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.idea.mobio.R
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.databinding.DialogBinding
import uz.idea.mobio.databinding.FragmentRegisterBinding
import uz.idea.mobio.models.errors.errorRegister.ErrorRegister
import uz.idea.mobio.models.register.RegisterReq
import uz.idea.mobio.models.register.RegisterRes
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.appConstant.AppConstant.REGISTER_ERROR
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.authVm.AuthViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val binding:FragmentRegisterBinding by viewBinding()
    private var isName: Boolean =false
    private var isPhone: Boolean =false
    private var isEmail: Boolean =false
    private var isPassword: Boolean =false
    private lateinit var activityAuth: AuthActivity
    private val authViewModel:AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            fullName.addTextChangedListener {  name->
                isName = name.toString().isNotEmptyOrNull()
                buttonCreate()
            }
            phoneNumber.addTextChangedListener { phone->
                if (phone.toString().trim().isNotEmptyOrNull()){
                    if (phone.toString().length==9){
                        email.requestFocus()
                        isPhone = true
                    }
                } else {
                   isPhone = false
                }
                buttonCreate()
            }
            email.addTextChangedListener { emailText->
                isEmail = emailText.toString().trim().isNotEmptyOrNull()
                buttonCreate()
            }
            password.addTextChangedListener { passwordText->
                isPassword = if (passwordText.toString().trim().isNotEmptyOrNull()){
                    passwordText.toString().length>=6
                }else{
                    false
                }
                buttonCreate()
            }

            // register button click
            register.setOnClickListener {
               registerUser()
            }
        }
    }


    private fun registerUser(){
        binding.apply {
            val fullName = fullName.text.toString().trim()
            val phoneNumber = "998${phoneNumber.text.toString().trim()}"
            val email = email.text.toString().trim()
            val password = password.text.toString().trim()
            val registerReq = RegisterReq(email,fullName,password,phoneNumber)
            authViewModel.registerUser(registerReq)
            lifecycleScope.launchWhenCreated {
                authViewModel.registerData.collect { result->
                    when(result){
                        is ResponseState.Success->{
                            hideLoadingLoginBtn()
                            LogData(result.data.toString())
                            val registerRes = result.data?.parseClass(RegisterRes::class.java)
                            authViewModel.saveData(password, registerRes?.user?.phone.toString())
                            activityAuth.binding.viewPager2.setCurrentItem(0,true)
                        }
                        is ResponseState.Error->{
                            hideLoadingLoginBtn()
                            if (result.errorCode==NO_INTERNET){
                                activityAuth.noInternetSnackBar()
                            }else{
                                errorRegister(result.liveError){ clickPos ->
                                    if (clickPos==1) registerUser()
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
    fun errorRegister(liveError: LiveData<List<ErrorEntity>>?, onClick:(clickPos:Int)->Unit){
        val bindingDialog = DialogBinding.inflate(layoutInflater)
        create.setView(bindingDialog.root)

        var errorMessageData = ""
        liveError?.observe(viewLifecycleOwner){ listError->
          try {
              listError.onEach { errorEntity ->
                  if (errorEntity.error!=null){
                      val jsonObject = JsonParser.parseReader(errorEntity.error.reader()).asJsonObject
                      val errorRegister = Gson().fromJson(jsonObject, ErrorRegister::class.java)
                      if (errorRegister.errors.name!=null){
                          errorMessageData += errorRegister.errors.name[0]
                      }
                      if (errorRegister.errors.email!=null){
                          errorMessageData += "\n" + errorRegister.errors.email[0]
                      }
                      if (errorRegister.errors.phone!=null){
                          errorMessageData += "\n" + errorRegister.errors.phone[0]
                      }

                      if (errorRegister.errors.password!=null){
                          errorMessageData += "\n" + errorRegister.errors.password[0]
                      }
                  }
              }
              bindingDialog.message.text = errorMessageData
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
            register.attachTextChangeAnimator()
            register.showProgress {
                buttonTextRes = R.string.loading
                progressColor = Color.WHITE
            }
            register.enabledFalse()
        }
    }

    private fun hideLoadingLoginBtn(){
        binding.apply {
            register.hideProgress(R.string.register_btn)
            register.enabled()
        }
    }

    private fun buttonCreate(){
        if (isName && isPhone && isEmail && isPassword){
            binding.register.enabled()
            binding.register.setBackgroundResource(R.drawable.button_back)
        }else{
            binding.register.enabledFalse()
            binding.register.setBackgroundResource(R.drawable.btn_no_enabled)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityAuth = context as AuthActivity
    }
}