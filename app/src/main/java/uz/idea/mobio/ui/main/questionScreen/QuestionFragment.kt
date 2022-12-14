package uz.idea.mobio.ui.main.questionScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.idea.mobio.R
import uz.idea.mobio.databinding.FragmentQuestionBinding
import uz.idea.mobio.ui.main.baseFragment.BaseFragment

class QuestionFragment : BaseFragment<FragmentQuestionBinding>() {
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentQuestionBinding.inflate(layoutInflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {

        }
    }

    override fun start(savedInstanceState: Bundle?) {

    }

}