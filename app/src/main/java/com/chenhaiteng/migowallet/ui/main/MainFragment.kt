package com.chenhaiteng.migowallet.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.chenhaiteng.migowallet.R
import com.chenhaiteng.migowallet.ui.main.placeholder.MockShop
import kotlinx.android.synthetic.main.main_fragment.view.*

class MainFragment : Fragment() {
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
            true
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
        shop.fetchAvailablePasses()
    }

}