package uz.idea.mobio.ui.main.privacyScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import uz.idea.mobio.databinding.FragmentPrivacyBinding
import uz.idea.mobio.ui.main.baseFragment.BaseFragment


class PrivacyFragment : BaseFragment<FragmentPrivacyBinding>() {
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentPrivacyBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {

        }
    }

    override fun start(savedInstanceState: Bundle?) {

    }

}