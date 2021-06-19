package com.chenhaiteng.migowallet.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.chenhaiteng.migowallet.R
import com.chenhaiteng.migowallet.ui.main.placeholder.MockShop
import com.chenhaiteng.migowallet.utility.NetworkInfo
import com.chenhaiteng.migowallet.utility.doAsync
import kotlinx.android.synthetic.main.main_fragment.view.*
import okhttp3.OkHttpClient
import java.lang.Exception
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory

class MainFragment : Fragment(), LifecycleObserver {
    companion object {
        fun newInstance() = MainFragment()
    }
    private val shop: MockShop by activityViewModels()

    private val shopFragment: ShopFragment by lazy { ShopFragment.newShop() }
    private val myPassFragment: MyPassFragment by lazy { MyPassFragment.newMyPasses() }

    private lateinit var currentFragment: Fragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        childFragmentManager.beginTransaction().add(R.id.container, shopFragment).commitNowAllowingStateLoss()
        childFragmentManager.beginTransaction().add(R.id.container, myPassFragment).commitNowAllowingStateLoss()
        childFragmentManager.beginTransaction().hide(myPassFragment).show(shopFragment).commitNowAllowingStateLoss()
        currentFragment = shopFragment
        root.bottom_bar.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.shop -> {
                    childFragmentManager.beginTransaction().hide(currentFragment).show(shopFragment).commitNowAllowingStateLoss()
                    currentFragment = shopFragment
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.mypass -> {
                    childFragmentManager.beginTransaction().hide(currentFragment).show(myPassFragment).commitNowAllowingStateLoss()
                    currentFragment = myPassFragment
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        return root
    }

    private fun showMigoCodeTestResult(result: String) {
        activity?.runOnUiThread {
            view?.fetch_status?.let { view ->
                view.text = result
            }
        }
    }

    private fun fetchMigoCodeTest() {
        val netInfo = NetworkInfo.standard(context)
        if (netInfo.isConnected) {
            if (netInfo.isWifi && netInfo.isCellular) {
                migoCodeTestFetchPrivate()
            } else {
                migoCodeTestFetchPublic()
            }
        } else {
            showMigoCodeTestResult("No network!")
        }
    }

    private fun migoCodeTestFetchPrivate() {
        //Try connect to Private
        doAsync {
            val url = URL(getString(R.string.code_test_url_private))
            val client = OkHttpClient.Builder()
                .socketFactory(PrivateSocketFactory.wifi() ?: SocketFactory.getDefault())
                .readTimeout(2, TimeUnit.SECONDS)
                .build()
            val request = okhttp3.Request.Builder()
                .url(url)
                .get()
                .build()
            try {
                val response = client.newCall(request).execute()
                when (response.code()) {
                    200 -> {
                        response.body()?.string()?.let {
                            showMigoCodeTestResult("private: $it")
                        }
                    }
                    else -> {
                        response.body()?.string()?.let {
                            showMigoCodeTestResult("private: $it")
                        }
                    }
                }
            } catch (e: Exception) {
                showMigoCodeTestResult(e.message ?: "$e")
            }
        }
    }

    fun migoCodeTestFetchPublic() {
        doAsync {
            val url = URL(getString(R.string.code_test_url))
            val client = OkHttpClient.Builder()
                .build()
            val request = okhttp3.Request.Builder()
                .url(url)
                .get()
                .build()
            try {
                val response = client.newCall(request).execute()
                when (response.code()) {
                    200 -> {
                        activity?.runOnUiThread {
                            response.body()?.string()?.let {
                                showMigoCodeTestResult("public: $it")
                            }
                        }
                    }
                    else -> {
                        activity?.runOnUiThread {
                            response.body()?.string()?.let {
                                showMigoCodeTestResult("public: $it")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                showMigoCodeTestResult(e.message ?: "$e")
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreated(){
        activity?.lifecycle?.removeObserver(this)
        shop.fetchAvailablePasses()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchMigoCodeTest()
    }

}