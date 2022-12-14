package uz.idea.mobio.ui.main.categoryScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericPagingAdapter.PagingAdapter
import uz.idea.mobio.databinding.FragmentCategoryBinding
import uz.idea.mobio.models.categoryModel.Data
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.extension.gone
import uz.idea.mobio.utils.extension.visible
import uz.idea.mobio.vm.homeVm.HomeViewModel

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()

    //category adapter
    private val categoryPagingAdapter: PagingAdapter<Data> by lazy {
        PagingAdapter(R.layout.item_category){ data, position, clickType,viewBinding ->
            activityMain.container?.screenNavigate?.createCategoryProductInOtherFragments(data?.id?:0,data?.name.toString())
        }
    }
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCategoryBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            getCategory()
            binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.circle_progress_color))
            binding.swipeRefresh.setOnRefreshListener {
                getCategory()
            }
        }
    }


    // category data
    private fun getCategory(){
        loadState()
        lifecycleScope.launchWhenStarted {
            homeViewModel.getCategory().collect { pagingData->
                categoryPagingAdapter.submitData(pagingData)
            }
        }
        binding.rvCategory.adapter = categoryPagingAdapter
    }


    override fun start(savedInstanceState: Bundle?) {

    }


    private fun loadState(){
        lifecycleScope.launchWhenCreated {
            categoryPagingAdapter.loadStateFlow.collectLatest { loadStates->
                binding.includeShimmer.shimmerCons.isVisible = loadStates.refresh is LoadState.Loading
                binding.rvCategory.isVisible = loadStates.refresh !is LoadState.Loading
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
    }
}