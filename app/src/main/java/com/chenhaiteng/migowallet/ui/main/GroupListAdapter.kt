package com.chenhaiteng.migowallet.ui.main

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenhaiteng.migowallet.R
import kotlinx.android.synthetic.main.group_list_header.view.*
import kotlinx.android.synthetic.main.group_list_item.view.*

class GroupListItem(val index : Pair<Int, Int>, var name: String = "")

interface GroupListDataSource {
    fun numberOfGroups(groupList: GroupListAdapter): Int
    fun numberOfItemsIn(groupList: GroupListAdapter, group: Int) : Int
    fun getItem(groupList: GroupListAdapter, atIndex: Pair<Int, Int>) : GroupListItem
}

interface GroupListDelegate {
    fun onClickItem(groupList: GroupListAdapter, at: Pair<Int, Int>?)
}

class GroupListAdapter(
        private var mDataSource: GroupListDataSource,
        private var mDelegate: GroupListDelegate?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    val mSelections = mutableSetOf<Pair<Int, Int>>()

    enum class SelectionType {
        Single, //TODO: Need implement single selection behaviour
        GroupSingle,
        Multiple // TODO: Need to implement multiple selection behaviour
    }

    private var selectionType: SelectionType = SelectionType.GroupSingle

    init {
        mOnClickListener = View.OnClickListener { v ->
            val clickedIndex = v.tag as? Pair<Int, Int>
            var needUpdate = clickedIndex?.let {
                select(it.second, it.first)
            } ?: false

            mDelegate?.onClickItem(this, clickedIndex)
            if(needUpdate) notifyDataSetChanged()
        }
    }

    private fun Int.toGroupIndex() : Pair<Int, Int> {
        var total = 0
        var group = 0
        var position = 0
        for(_group in 0 until mDataSource.numberOfGroups(this@GroupListAdapter)) {
            group = _group
            if(total == this) {
                position = -1
                break
            }
            val _count = mDataSource.numberOfItemsIn(this@GroupListAdapter, _group)
            total += _count + 1
            if(this < total) {
                position = _count - (total - this)
                break
            }
        }
        return Pair(group, position)
    }


    fun select(item: Int, atGroup: Int) : Boolean {
        val target = Pair(atGroup, item)
        var success = false
        when(selectionType) {
            SelectionType.GroupSingle -> {
                if(!mSelections.contains(target)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mSelections.removeIf {
                            it.first == target.first
                        }
                    } else {
                        val iter = mSelections.iterator()
                        while (iter.hasNext()) {
                            if(target.first == iter.next().first) {
                                iter.remove()
                            }
                        }
                    }
                    mSelections.add(target)
                    success = true
                }
            }
            else -> {
                //TODO: Need process other selection types.
            }
        }
        return success
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when(viewType) {
            HeaderType -> {
                GroupHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.group_list_header, parent, false))
            }
            ItemType -> {
                ItemHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.group_list_item, parent, false))
            } else -> {
                Log.i(Tag, "No such type $viewType")
                ItemHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.group_list_item, parent, false))
            }
        }

        return holder
    }

    private fun RecyclerView.ViewHolder.bind(item: GroupListItem) {
        when(this) {
            is GroupListAdapter.ItemHolder -> {
                this.mIdView.text = item.name
                this.mCheckMark.visibility = if(mSelections.contains(item.index)) View.VISIBLE else View.INVISIBLE
            }
            is GroupListAdapter.GroupHolder -> {
                this.mHeader.text = item.name
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val groupIndex = position.toGroupIndex()
        holder.bind(mDataSource.getItem(this, groupIndex))

        if (holder is GroupListAdapter.ItemHolder) {
            with(holder.itemView) {
                this.tag = groupIndex
                setOnClickListener(mOnClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        for(group in 0 until mDataSource.numberOfGroups(this)) {
            count += mDataSource.numberOfItemsIn(this, group) + 1
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        val (_, item) = position.toGroupIndex()
        return if(item == -1) HeaderType else ItemType
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mIdView: TextView = itemView.item_name
        val mCheckMark: ImageView = itemView.check_mark
        override fun toString(): String {
            return super.toString() + " '${mIdView.text}'"
        }
    }

    inner class GroupHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mHeader : TextView = itemView.header_name
        override fun toString(): String {
            return super.toString() + "Group '${mHeader.text}'"
        }
    }

    companion object {
        const val HeaderType = 0
        const val ItemType = 1

        const val Tag = "CLOUD"

    }
}
