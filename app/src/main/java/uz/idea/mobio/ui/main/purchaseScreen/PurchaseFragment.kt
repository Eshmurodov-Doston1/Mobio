package uz.idea.mobio.ui.main.purchaseScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebViewClient
import uz.idea.mobio.databinding.FragmentPurchaseBinding
import uz.idea.mobio.ui.main.baseFragment.BaseFragment


class PurchaseFragment : BaseFragment<FragmentPurchaseBinding>() {
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentPurchaseBinding.inflate(inflater,container,false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun setup(savedInstanceState: Bundle?) {
        binding.apply {
            webView.loadUrl("https://kun.uz")
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
        }
    }

    override fun start(savedInstanceState: Bundle?) {

    }

}