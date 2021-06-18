package com.chenhaiteng.migowallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.chenhaiteng.migowallet.ui.main.MainFragment
import com.chenhaiteng.migowallet.ui.main.placeholder.MockShop
import com.chenhaiteng.migowallet.ui.main.placeholder.MyPassMockModel

class MainActivity : AppCompatActivity() {

    private val shop: MockShop by viewModels()
    private val myPass: MyPassMockModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}