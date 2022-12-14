package uz.idea.mobio.ui.auth.activity

import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.mobio.R
import uz.idea.mobio.adapters.authPager.AdapterAuthPager
import uz.idea.mobio.databinding.ActivityAuthBinding
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.utils.extension.isNotEmptyOrNull
import uz.idea.mobio.utils.extension.startNewActivity
import uz.idea.mobio.vm.authVm.AuthViewModel
import java.util.*

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    val binding:ActivityAuthBinding by viewBinding()
    private val adapterAuthPager:AdapterAuthPager by lazy{AdapterAuthPager(this)}
    private val gson:Gson by lazy { Gson() }
    private val authViewModel:AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Mobio)
        setContentView(binding.root)
        authViewModel.clearErrorTable()
        binding.apply {
            if (authViewModel.mysharedPref.accessToken.isNotEmptyOrNull()){
                startNewActivity(MainActivity::class.java)
                finish()
            }

            viewPager2.adapter = adapterAuthPager
            TabLayoutMediator(tabLayout,viewPager2){ tab,position->
                tab.text = getCategoryData()[position]
            }.attach()
        }
    }

    private fun getCategoryData():List<String>{
        val listCategory = LinkedList<String>()
        listCategory.add(getString(R.string.login))
        listCategory.add(getString(R.string.register))
        return listCategory
    }







    override fun onPause() {
        authViewModel.clearErrorTable()
        super.onPause()
    }

    override fun onDestroy() {
        authViewModel.clearErrorTable()
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        authViewModel.clearErrorTable()
    }
    fun noInternetSnackBar(){
        val snackBar = Snackbar.make(binding.root, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
        snackBar.show()
    }
}