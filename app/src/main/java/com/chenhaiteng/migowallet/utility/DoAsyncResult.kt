package com.chenhaiteng.migowallet.utility

import android.os.AsyncTask
import com.chenhaiteng.migowallet.ui.main.MainFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

//TODO: Temporary implementation, should replace related code with Kotlin Coroutines.

open class DoAsyncResult<Params, Progress, Result>(private val task: TASK<Params, Progress, Result>,
                                                   private val completion: RESULT<Params, Progress, Result>?) : AsyncTask<Params, Progress, Result>() {
    override fun doInBackground(vararg params: Params): Result {
        return task(this, params)
    }

    override fun onPostExecute(result: Result) {
        completion?.invoke(this, result)
    }
}

open class DoAsync<Params>(task: TASK<Params, Unit, Unit>) : DoAsyncResult<Params, Unit, Unit>(task, null)

typealias TASK<Params, Progress, Result> = DoAsyncResult<Params, Progress, Result>.(Array<out Params>)->Result
typealias RESULT<Params, Progress, Result> = DoAsyncResult<Params, Progress, Result>.(Result)->Unit


//fun doAsync(block : (suspend CoroutineScope.() -> Unit)) = GlobalScope.launch(block = block)

private val asyncScope = object : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default
}

fun doAsync(block: (suspend CoroutineScope.() -> Unit)) = asyncScope.launch(block = block)

@Module
@InstallIn(SingletonComponent::class)
object Async {
    private val internalScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Default
    }
    @Provides
    fun doJob(block: (suspend CoroutineScope.() -> Unit)) = internalScope.launch(block = block)
}