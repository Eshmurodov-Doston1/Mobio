package uz.idea.mobio.ui.main.basketScreen

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import uz.idea.mobio.R
import uz.idea.mobio.adapters.genericPagingAdapter.PagingAdapter
import uz.idea.mobio.databinding.FragmentBasketBinding
import uz.idea.mobio.models.basketModel.basketList.DataX
import uz.idea.mobio.models.basketModel.deleteBasket.reqDeleteBasket.DeleteReqModel
import uz.idea.mobio.models.basketModel.udateBasket.UpdateBasketModel
import uz.idea.mobio.ui.main.baseFragment.BaseFragment
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_ADD
import uz.idea.mobio.utils.appConstant.AppConstant.CLICK_MINUS
import uz.idea.mobio.utils.appConstant.AppConstant.DEFAULT_CLICK
import uz.idea.mobio.utils.resPonseState.ResponseState
import uz.idea.mobio.vm.basketVm.BasketViewModel


@AndroidEntryPoint
class BasketFragment : BaseFragment<FragmentBasketBinding>() {
    private val basketViewModel:BasketViewModel by viewModels()
    private val basketAdapter:PagingAdapter<DataX> by lazy {
        PagingAdapter(R.layout.basket_item){ data, position, clickType,viewBinding ->
            when(clickType){
                DEFAULT_CLICK->{
                    activityMain.container?.screenNavigate?.createProductInfoScreen(data?.product_id?:0)
                }
                CLICK_ADD->{
                    val count = data?.count?.plus(1)
                    val updateBasketModel = UpdateBasketModel(count.toString(),data?.id.toString())
                    updateBasket(updateBasketModel)
                }
                CLICK_MINUS->{
                    if (data?.count==1 || data?.count!! <= 1){
                        val deleteReqModel = DeleteReqModel(data.id.toString())
                        deleteBasket(deleteReqModel)
                    } else {
                        val count = data.count.minus(1)
                        val updateBasketModel = UpdateBasketModel(count.toString(),data.id.toString())
                        updateBasket(updateBasketModel)
                    }
                }
            }
        }
    }

    private fun deleteBasket(deleteReqModel: DeleteReqModel){
        basketViewModel.deleteBasket(deleteReqModel)
        lifecycleScope.launchWhenCreated {
            basketViewModel.deleteBasket.collect { result->
                when(result){
                    is ResponseState.Success->{
                        basketListRv()
                    }
                    is ResponseState.Error->{
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) deleteBasket(deleteReqModel)
                            basketViewModel.clearErrorTable()
                        }
                    }
                    else->{}
                }
            }
        }
    }


    private fun updateBasket(updateBasketModel: UpdateBasketModel){
        basketViewModel.updateBasket(updateBasketModel)
        lifecycleScope.launchWhenCreated {
            basketViewModel.updateBasket.collect { result->
                when(result){
                    is ResponseState.Success->{
                        basketListRv()
                    }
                    is ResponseState.Error->{
                        activityMain.errorDialog(result.errorCode,result.liveError){ clickType ->
                            if (clickType==1) updateBasket(updateBasketModel)
                            basketViewModel.clearErrorTable()
                        }
                    }
                    else->{}
                }
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBasketBinding.inflate(inflater,container,false)

    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {

            // menu host
            menuMethode()

            // paging state
            loadStat()
            // basket list
            basketListRv()

            // swipe refresh
            swipeRefresh.setOnRefreshListener {
                // paging state
                loadStat()
                // basket list
                basketListRv()
            }

            binding.rvBasket.adapter = basketAdapter
            // swipe refresh color
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.circle_progress_color))


            val layoutManager = LinearLayoutManager(requireContext())
            rvBasket.layoutManager = layoutManager

            rvBasket.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (basketAdapter.itemCount>7){
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == basketAdapter.itemCount-1){
                            activityMain.bottomBarView(false)
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) {
                        activityMain.bottomBarView(true)
                    }
                }
            })
        }
    }

    private fun menuMethode() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object:MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.buy_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
              return  when(menuItem.itemId){
                    R.id.buy_products->{
                        true
                    }
                  else-> false
                }
            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)
    }

    private fun basketListRv(){
        lifecycleScope.launchWhenCreated {
            basketViewModel.getBasketList().collect { pagingData->
                basketAdapter.submitData(pagingData)
            }
        }
    }

    override fun start(savedInstanceState: Bundle?) {

    }


    private fun loadStat(){
        lifecycleScope.launchWhenStarted {
            basketAdapter.loadStateFlow.collectLatest { loadStates->
                binding.progress.isVisible = loadStates.refresh is LoadState.Loading
                binding.rvBasket.isVisible = loadStates.refresh !is LoadState.Loading
                binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
    }



}