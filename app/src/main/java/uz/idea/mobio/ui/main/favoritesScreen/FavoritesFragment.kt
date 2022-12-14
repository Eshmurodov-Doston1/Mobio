package uz.idea.mobio.ui.main.favoritesScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.idea.mobio.databinding.FragmentFavoritesBinding
import uz.idea.mobio.ui.main.baseFragment.BaseFragment


class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentFavoritesBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {

    }

    override fun start(savedInstanceState: Bundle?) {

    }

}