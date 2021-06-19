package com.chenhaiteng.migowallet.ui.main

fun <S> createSingleton(volatileInst: S?, lock: Any, initBlock: ()->S ) = {
    volatileInst ?: {
        synchronized(lock) {
            volatileInst ?: initBlock()
        }
    }()
}()