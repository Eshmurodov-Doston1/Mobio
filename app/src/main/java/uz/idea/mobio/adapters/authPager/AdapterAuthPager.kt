package uz.idea.mobio.adapters.authPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.idea.mobio.ui.auth.authScreen.AuthFragment
import uz.idea.mobio.ui.auth.registerScreen.RegisterFragment

class AdapterAuthPager(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
       return when(position){
            0->{
                AuthFragment()
            }
            1->{
                RegisterFragment()
            }
           else->{
               AuthFragment()
           }
        }
    }
}