package uz.idea.mobio.ui.main.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.muddassir.connection_checker.ConnectionState
import com.muddassir.connection_checker.ConnectivityListener
import com.muddassir.connection_checker.checkConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import uz.idea.mobio.BuildConfig.BASE_URL
import uz.idea.mobio.R
import uz.idea.mobio.database.daoAndEntity.error.ErrorEntity
import uz.idea.mobio.databinding.ActivityMainBinding
import uz.idea.mobio.databinding.DialogBinding
import uz.idea.mobio.databinding.NavHeaderMainBinding
import uz.idea.mobio.models.userDataModel.UserDataModel
import uz.idea.mobio.utils.appConstant.AppConstant
import uz.idea.mobio.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.mobio.utils.container.Container
import uz.idea.mobio.utils.containerui.UiController
import uz.idea.mobio.utils.extension.*
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.mainVm.MainViewModel
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),UiController, ConnectivityListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    val binding: ActivityMainBinding by viewBinding()
    var container:Container?=null
    val mainViewModel:MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Mobio)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        if (mainViewModel.getMyShared().theme == true)   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            //check Network
        checkConnection(this,BASE_URL,Lifecycle.State.STARTED)

        // navView drawer
        var widthOfNav: Double
        if (AppConstant.width>720 && AppConstant.width>1300)  widthOfNav = (AppConstant.width) * 0.5 else  widthOfNav = (AppConstant.width) * 0.7
        binding.navView.layoutParams.width = widthOfNav.toInt()
        binding.navView.requestLayout()

        // container object
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        container = Container(this,this,navHostFragment.navController)

        // fab icon tint
        binding.appBarMain.fabHome.setColorFilter(ContextCompat.getColor(this,R.color.icon_color_navigation))

        //home create
        binding.appBarMain.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true


        binding.drawerLayout.addDrawerListener(object:DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                userDataMethode()
            }

            override fun onDrawerClosed(drawerView: View) {
               // userDataMethode()
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })


        //fab setOnClick
        binding.appBarMain.fabHome.setOnClickListener {
            if (navHostFragment.navController.currentDestination?.id!=R.id.nav_home){
                navHostFragment.navController.popBackStack()
                binding.appBarMain.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
            }
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.category,
            R.id.favorites, R.id.questions,
            R.id.privacy, R.id.settings), drawerLayout)
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        binding.appBarMain.bottomNavigation.setupWithNavController(navHostFragment.navController)
        navView.setupWithNavController(navHostFragment.navController)
        navUiController()
    }

    @SuppressLint("SetTextI18n")
    private fun userDataMethode() {
        // header date
        val headerBinding = binding.navView.getHeaderView(0)
        // nav_header.xml is headerLayout
        val navViewHeaderBinding : NavHeaderMainBinding = NavHeaderMainBinding.bind(headerBinding)
        if (mainViewModel.getMyShared().accessToken.isNotEmptyOrNull()){
            mainViewModel.getUserData()
            lifecycleScope.launchWhenCreated {
                mainViewModel.userData.collect { result->
                    when(result){
                        is ResponseState.Success->{
                            LogData(result.data.toString())
                            val userData = result.data?.parseClass(UserDataModel::class.java)
                            navViewHeaderBinding.imageUser.imageData(userData?.user?.profile_photo_url.toString(),this@MainActivity)
                            navViewHeaderBinding.userName.text = userData?.user?.name
                            navViewHeaderBinding.phoneNumber.text = "+${userData?.user?.phone}"
                        }
                        is ResponseState.Loading->{

                        }
                        is ResponseState.Error->{

                        }
                    }
                }
            }
        }
    }


    private fun navUiController(){
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.appBarMain.apply {
                if (controller.currentDestination?.id == R.id.nav_home ||
                    controller.currentDestination?.id == R.id.search ||
                    controller.currentDestination?.id == R.id.basket){
                    if (!bottomAppbar.isVisible && !fabHome.isVisible){
                        slideUp(bottomAppbar)
                        slideUp(fabHome)
                    }
                }else{
                    if (bottomAppbar.isVisible && fabHome.isVisible){
                        slideDown(bottomAppbar)
                        slideDown(fabHome)
                    }
                }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun bottomBarView(isVisible: Boolean) {
        binding.appBarMain.apply {
            if (!bottomAppbar.isVisible && !fabHome.isVisible){
                if (isVisible){
                    slideUp(bottomAppbar)
                    slideUp(fabHome)
                }
            } else {
                if (!isVisible) {
                    slideDown(bottomAppbar)
                    slideDown(fabHome)
                }
            }
        }
    }

    private val create by lazy { AlertDialog.Builder(this).create() }
    override fun errorDialog(
        errorCode: Int?,
        liveError: LiveData<List<ErrorEntity>>?,
        onClick: (clickType: Int) -> Unit
    ) {
        if (errorCode == NO_INTERNET){
            val binding = DialogBinding.inflate(layoutInflater)
            create.setView(binding.root)
            binding.lottie.setAnimation(R.raw.no_internet)
            binding.message.text = getString(R.string.no_internet)
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
        }else {
            container!!.errorDialog(errorCode, liveError,mainViewModel.getMySharedPreferences(),onClick)
        }

    }


    fun toolbarView(isVisible:Boolean){
       if (isVisible) binding.appBarMain.toolbar.gone()
        else binding.appBarMain.toolbar.visible()
    }

    override fun onConnectionState(state: ConnectionState) {
        when (state) {
            ConnectionState.CONNECTED -> {
                binding.appBarMain.innerInclude.noInternetTv.gone()
            }
            ConnectionState.SLOW -> {
                binding.appBarMain.innerInclude.noInternetTv.visible()
                binding.appBarMain.innerInclude.noInternetTv.text = getString(R.string.slow_internet)
            }
            else -> {
                binding.appBarMain.innerInclude.noInternetTv.visible()
            }
        }
    }

    fun scrollFabVisibleOrGone(isVisible: Boolean){
        if (isVisible){
            if (!binding.appBarMain.fabScroll.isVisible){
                slideUp(binding.appBarMain.fabScroll)
            }
        } else {
            if (binding.appBarMain.fabScroll.isVisible) {
                slideDown(binding.appBarMain.fabScroll)
            }
        }
    }



   fun motionAnimation(status:String?,message:String?){
       MotionToast.createColorToast(this, message = message.toString(),
           style = if (status != "error") MotionToastStyle.SUCCESS else MotionToastStyle.ERROR,
           position = MotionToast.GRAVITY_TOP,
           duration = MotionToast.LONG_DURATION,
           font =  ResourcesCompat.getFont(this,R.font.inter_medium)
       )
   }
}