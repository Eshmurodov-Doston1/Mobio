package uz.idea.mobio.ui.main.settingsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.idea.mobio.R
import uz.idea.mobio.databinding.FragmentSettingsBinding
import uz.idea.mobio.models.userDataModel.UserDataModel
import uz.idea.mobio.ui.auth.activity.AuthActivity
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.mainVm.MainViewModel
import uz.idea.mobio.vm.settingsVm.SettingsViewModel
import kotlin.math.log

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
            // user data
            userData()
            // logout click
            linearLogout.setOnClickListener {
                logOut()
            }
        }
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
                                    if (clickType==1) logOut()
                                    settingsViewModel.clearErrorTable()
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