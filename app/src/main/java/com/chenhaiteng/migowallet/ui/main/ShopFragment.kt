package com.chenhaiteng.migowallet.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chenhaiteng.migowallet.R
import com.chenhaiteng.migowallet.ui.main.placeholder.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.shop_fragment.view.*
import javax.inject.Inject

@AndroidEntryPoint
class ShopFragment : Fragment() {
    @MockLocalPass @Inject lateinit var myPass: LocalPassModel
    private val shop: MockShop by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.shop_fragment, container, false)
        root.group_list.layoutManager = LinearLayoutManager(context)
        root.group_list.adapter = GroupListAdapter(object: GroupListDataSource {
            override fun numberOfGroups(groupList: GroupListAdapter): Int {
                return 2
            }

            override fun numberOfItemsIn(groupList: GroupListAdapter, group: Int): Int {
                return when(group) {
                    0 -> {
                        shop.numOfDayPass
                    }
                    1 -> {
                        shop.numOfHourPass
                    }
                    else -> {
                        0
                    }
                }
            }

            fun getHeaderItem(headerIndex: Pair<Int, Int>) : GroupListItem = GroupListItem(headerIndex, when(headerIndex.first) {
                0 -> "Day Pass".toUpperCase()
                1 -> "Hour Pass".toUpperCase()
                else -> "!!!Incorrect Header!!!"
            })

            fun getPassItem(pathIndex: Pair<Int, Int>) : GroupListItem {
                val item = when(pathIndex.first) {
                    0 -> shop.availableDayPass()[pathIndex.second]
                    1 -> shop.availableHourPass()[pathIndex.second]
                    else -> null
                }
                return item?.let { pass ->
                    val listItem = GroupListItem(pathIndex, "${pass.title()}") {
                        myPass.addPass(pass)
                    }
                    listItem.description = "price \$${pass.price}"
                    listItem.actionTitle = R.string.buy_button
                    listItem
                } ?: run {
                    GroupListItem(pathIndex, "Unknown Pass")
                }
            }

            override fun getItem(
                groupList: GroupListAdapter,
                atIndex: Pair<Int, Int>
            ): GroupListItem {
                return when(atIndex.second) {
                    -1 -> getHeaderItem(atIndex)
                    else -> getPassItem(atIndex)
                }
            }
        }, null)
        return root
    }

    companion object {
        fun newShop() = ShopFragment()
    }
}