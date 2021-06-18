package com.chenhaiteng.migowallet.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chenhaiteng.migowallet.R
import com.chenhaiteng.migowallet.ui.main.placeholder.MyPassMockModel
import kotlinx.android.synthetic.main.my_pass_fragment.view.*

class MyPassFragment : Fragment() {
    private val myPass: MyPassMockModel by activityViewModels()
    inner class MyPassDatasource : GroupListDataSource {
        override fun numberOfGroups(groupList: GroupListAdapter): Int {
            return 2
        }

        override fun numberOfItemsIn(groupList: GroupListAdapter, group: Int): Int {
            return when(group) {
                0 -> {
                    myPass.numOfDayPass
                }
                1 -> {
                    myPass.numOfHourPass
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
                0 -> myPass.allDayPass()[pathIndex.second]
                1 -> myPass.allHourPass()[pathIndex.second]
                else -> null
            }
            return item?.let { pass ->

                val activate = { _: Pair<Int, Int> ->
                    pass.activate()
                    adapter.notifyDataSetChanged()
                }

                val showDetail = { _: Pair<Int, Int> ->

                }

                val action: ((Pair<Int,Int>) -> Unit)? = if (pass.isActivated()) {
                    if (pass.isExpired()) {
                        null
                    } else showDetail
                } else activate

                val listItem = GroupListItem(pathIndex, "${pass.title()}", action = action)
                listItem.description = "price \$${pass.price}"
                listItem.actionTitle = if(pass.isActivated()) R.string.detail_button else R.string.active_button
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
    }

    private val adapter = GroupListAdapter(MyPassDatasource(), null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.my_pass_fragment, container, false)
        root.my_pass_group_list.layoutManager = LinearLayoutManager(context)
        root.my_pass_group_list.adapter = adapter

        myPass.livedata.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })
        return root
    }

    companion object {
        fun newMyPasses() = MyPassFragment()
    }
}