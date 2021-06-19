package com.chenhaiteng.migowallet.ui.main.placeholder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chenhaiteng.migowallet.ui.main.Pass
import com.chenhaiteng.migowallet.ui.main.PassType
import java.time.LocalDateTime

class MyPassMockModel : ViewModel() {

    private val items = mutableListOf<Pass>()
    val livedata = MutableLiveData(items)

    fun loadMyPass() {
        // TODO: This is a mock implementation, try link to Room in future
    }

    private val _dayPasses
        get() = items.filter { it.type == PassType.Day }

    private val _hourPasses
        get() = items.filter { it.type == PassType.Hour }

    val numOfDayPass: Int
        get() = _dayPasses.count()

    val numOfHourPass: Int
        get() = _hourPasses.count()

    fun allDayPass() = _dayPasses

    fun allHourPass() = _hourPasses

    fun addPass(pass: Pass) {
        val copy = pass.copyTo().apply {
            insertDate = LocalDateTime.now()
        }
        items.add(copy)
        livedata.value = items
    }
}