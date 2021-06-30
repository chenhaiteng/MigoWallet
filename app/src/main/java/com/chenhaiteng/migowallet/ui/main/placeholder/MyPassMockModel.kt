package com.chenhaiteng.migowallet.ui.main.placeholder

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.chenhaiteng.migowallet.ui.main.Pass
import com.chenhaiteng.migowallet.ui.main.PassType
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton


interface LocalPassModel {
    fun loadPasses()
    val numOfDayPass: Int
    val numOfHourPass: Int
    fun addPass(newPass: Pass)
    operator fun get(type:PassType, index: Int): Pass
    operator fun set(index: Int, value: Pass)
    @MainThread
    fun observe(owner: LifecycleOwner, observer: Observer<MutableList<Pass>>)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MockLocalPass

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalPass

//@HiltViewModel
class MyPassMockModel @Inject constructor() : LocalPassModel, ViewModel() {

    private val items = mutableListOf<Pass>()
    private val livedata = MutableLiveData(items)

    override fun loadPasses() {
        // TODO: This is a mock implementation, try link to Room in future
    }

    private val _dayPasses
        get() = items.filter { it.type == PassType.Day }

    private val _hourPasses
        get() = items.filter { it.type == PassType.Hour }

    override val numOfDayPass: Int
        get() = _dayPasses.count()

    override val numOfHourPass: Int
        get() = _hourPasses.count()

    override fun addPass(pass: Pass) {
        val copy = pass.copyTo().apply {
            insertDate = LocalDateTime.now()
        }
        items.add(copy)
        livedata.value = items
    }

    override fun get(type:PassType, index: Int): Pass =
        when(type) {
            PassType.Day -> {
                _dayPasses[index]
            }
            PassType.Hour -> {
                _hourPasses[index]
            }
        }

    override fun set(index: Int, value: Pass) {
        TODO("Not yet implemented")
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<MutableList<Pass>>) {
        livedata.observe(owner, observer)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalPassModule {

    @MockLocalPass
    @Singleton
    @Binds
    abstract fun bindMock(mockImpl: MyPassMockModel): LocalPassModel
}