package uz.idea.mobio.ui.main.settingsScreen

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.mobio.R
import uz.idea.mobio.databinding.FragmentSettingsBinding
import uz.idea.mobio.databinding.LanguageDialogBinding
import uz.idea.mobio.models.userDataModel.UserDataModel
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant.EN
import uz.idea.mobio.utils.appConstant.AppConstant.RU
import uz.idea.mobio.utils.appConstant.AppConstant.UZB
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.language.Language
import uz.idea.mobio.utils.language.LocaleManager
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.mainVm.MainViewModel
import uz.idea.mobio.vm.settingsVm.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    // mainViewModel
    private val mainViewModel:MainViewModel by viewModels()
    // SettingsViewModel
    private val settingsViewModel:SettingsViewModel by viewModels()
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            if (!settingsViewModel.getMyShared().accessToken.isNotEmptyOrNull()){
                linearLogout.gone()
                binding.imageUser.visible()
                binding.imageUser.setImageResource(R.drawable.user_profile_image)
                binding.progress1.gone()
                binding.progressImage.gone()
                binding.userProfileName.text = getString(R.string.no_data)
                binding.userProfileName.visible()
                binding.email.visible()
                binding.email.text = getString(R.string.no_data)

                // user data
                linearUserInfo.gone()
            } else {
                // user data
                userData()
            }
            // logout click
            linearLogout.setOnClickListener {
                logOut()
            }

            // language
            language.setOnClickListener {
                languageData()
            }

            // theme
            switchApp.isChecked = settingsViewModel.getMyShared().theme?:false
            if (settingsViewModel.getMyShared().theme == true)   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            linearUiTheme.setOnClickListener {
                val checked = switchApp.isChecked
                switchApp.isChecked = !checked
                settingsViewModel.setTheme(switchApp.isChecked)
                if (switchApp.isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            switchApp.setOnCheckedChangeListener { buttonView, isChecked ->
                settingsViewModel.setTheme(isChecked)
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }


    private fun languageData(){
       val alertDialog = AlertDialog.Builder(requireContext())
        val create = alertDialog.create()
        val bindingDialog = LanguageDialogBinding.inflate(activityMain.layoutInflater)
        create.setView(bindingDialog.root)
        bindingDialog.apply {
            val language = LocaleManager.getLanguage(requireContext())
            when(language){
                UZB->{
                    uzbLang.isChecked = true
                }
                RU->{
                    ruLang.isChecked = true
                }
                EN->{
                    crlLang.isChecked = true
                }
            }
            checkLang.setOnClickListener {
                when(radioGroup.checkedRadioButtonId){
                    R.id.uzb_lang->{
                        LocaleManager.setNewLocale(requireContext(),Language.UZ.value)
                        activityMain.recreate()
                    }
                    R.id.ru_lang->{
                        LocaleManager.setNewLocale(requireContext(),Language.RU.value)
                        activityMain.recreate()
                    }
                    R.id.crl_lang->{
                        LocaleManager.setNewLocale(requireContext(),Language.EN.value)
                        activityMain.recreate()
                    }
                }
                create.dismiss()
            }
            cancel.setOnClickListener {
                create.dismiss()
            }
        }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        create.show()
    }

    private fun logOut(){
        activityMain.container?.otherDialog(0){ clickType ->
            if(clickType==1){
                settingsViewModel.logOut()
                lifecycleScope.launchWhenCreated {
                    settingsViewModel.logOut.collect { result->
                        when(result){
                            is ResponseState.Loading->{
                                activityMain.container?.loadingDialog(true)
                            }
                            is ResponseState.Success->{
                                activityMain.container?.loadingDialog(false)
                                settingsViewModel.clearShared()
                                requireActivity().startNewActivity(AuthActivity::class.java)
                                requireActivity().finish()
                            }
                            is ResponseState.Error->{
                                activityMain.container?.loadingDialog(false)
                                activityMain.errorDialog(result.errorCode,result.liveError){ clickType->
                                    if (clickType==1){
                                        logOut()
                                        settingsViewModel.clearErrorTable()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun userData(){
        binding.apply {
            mainViewModel.getUserData()
            lifecycleScope.launchWhenCreated {
                mainViewModel.userData.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                           userDataViewLoading()
                        }
                        is ResponseState.Success->{
                            userDataViewNotLoading()
                            val userDataModel = result.data?.parseClass(UserDataModel::class.java)
                            binding.imageUser.imageData(userDataModel?.user?.profile_photo_url.toString(),requireContext())
                            binding.userProfileName.text = userDataModel?.user?.name
                            binding.email.text = userDataModel?.user?.email
                        }
                        is ResponseState.Error->{
                            userDataViewNotLoading()
                            activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                                if (clickType==1) userData()
                                mainViewModel.clearErrorTable()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun userDataViewLoading(){
        binding.progressImage.visible()
        binding.imageUser.gone()

        binding.progress1.visible()
        binding.userProfileName.gone()
        binding.email.gone()
    }

    private fun userDataViewNotLoading(){
        binding.progressImage.gone()
        binding.imageUser.visible()

        binding.progress1.gone()
        binding.userProfileName.visible()
        binding.email.visible()
    }

    override fun start(savedInstanceState: Bundle?) {

    }

}