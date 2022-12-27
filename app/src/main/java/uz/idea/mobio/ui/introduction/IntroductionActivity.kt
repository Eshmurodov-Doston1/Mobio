package uz.idea.mobio.ui.introduction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.mobio.databinding.ActivityIntroductionBinding
import uz.idea.mobio.databinding.ActivityMainBinding
import uz.idea.mobio.models.locale.LocalePager
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.utils.extension.gone
import uz.idea.mobio.utils.extension.startNewActivity
import uz.idea.mobio.utils.extension.visible
import uz.idea.mobio.vm.mainVm.MainViewModel
import java.util.LinkedList
@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {
    private val binding:ActivityIntroductionBinding by viewBinding()

    // container viewModel
    private val mainViewModel:MainViewModel by viewModels()

    private val genericRvAdapter:GenericRvAdapter<LocalePager> by lazy {
        GenericRvAdapter(R.layout.item_introduction){ data, position, clickType, viewBiding ->

        }
    }

    override fun onStart() {
        super.onStart()
        if (mainViewModel.getMySharedPreferences().isIntroduction==true){
            startNewActivity(MainActivity::class.java)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            viewPager2.adapter = genericRvAdapter
            genericRvAdapter.submitList(getListPager())
            window.statusBarColor = ContextCompat.getColor(this@IntroductionActivity,R.color.app_background_color)
            window.navigationBarColor = ContextCompat.getColor(this@IntroductionActivity,R.color.app_background_color)

            // right button click
            rightBtn.setOnClickListener {
                viewPager2.setCurrentItem(viewPager2.currentItem+1,true)
            }

            leftBtn.setOnClickListener {
                viewPager2.setCurrentItem(viewPager2.currentItem-1,true)
            }

            viewPager2.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    textCenter.text = getListPager()[position].cateGory
                    if (position == getListPager().size-1){
                        goShop.visible()
                        rightBtn.gone()
                    } else {
                        goShop.gone()
                        rightBtn.visible()
                    }
                    if(position>0){
                        leftBtn.visible()
                    } else {
                        leftBtn.gone()
                    }
                }
            })

            // go shop click
            goShop.setOnClickListener {
                mainViewModel.getMySharedPreferences().isIntroduction = true
                startNewActivity(MainActivity::class.java)
                finish()
            }
        }
    }


    fun getListPager():List<LocalePager>{
        val listPager = LinkedList<LocalePager>()
        listPager.add(LocalePager("Ilovaga xush kelibsiz bu ilova mobioning shaxsiy online do'koni",R.drawable.ic_auth_image,"Xush kelibsiz"))
        listPager.add(LocalePager("Ilova orqali tovar sotib olmoqchi bo'lsangiz olding ro'yxatdan o'tishingiz kerak so'ngra tovar xarid qilsangiz bo'ladi",R.drawable.ic_auth_image,"Ro'yxatdan o'tish"))
        listPager.add(LocalePager("Hozirda bizda faqatgina click to'lov tizimi mavjud va siz faqatgina click orqali to'lov qila olasiz",R.drawable.ic_auth_image,"To'lov turi"))
        return listPager
    }
}