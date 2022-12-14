package uz.idea.mobio.ui.main.baseFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log
import uz.idea.mobio.ui.main.activity.MainActivity
import uz.idea.mobio.utils.extension.LogData

abstract class BaseFragment<VB: ViewBinding>: Fragment() {
    private var _binding : VB? = null
    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    val activityMain:MainActivity get() = (activity as MainActivity)
    @Suppress("UNCHECKED_CAST")
    protected val binding :VB get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (_binding == null){
            _binding = inflateViewBinding(inflater,container)
            _binding?.root
        } else {
            binding.root
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup(savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
       // _binding=null
    }
    abstract fun setup(savedInstanceState: Bundle?)
    abstract fun start(savedInstanceState: Bundle?)
}