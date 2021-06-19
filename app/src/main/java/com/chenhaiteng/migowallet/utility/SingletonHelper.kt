package com.chenhaiteng.migowallet.utility

fun <S> createSingleton(volatileInst: S?, lock: Any, initBlock: ()->S ) =
    volatileInst ?: synchronized(lock) {
        volatileInst ?: initBlock()
    }