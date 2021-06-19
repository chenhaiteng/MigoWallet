package com.chenhaiteng.migowallet.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.chenhaiteng.migowallet.R
import com.chenhaiteng.migowallet.databinding.PassDetailBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PassDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PassDetailFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var pass: Pass? = null

    private lateinit var binding: PassDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pass_detail, container, false)
        binding.pass = pass
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param pass Pass.
         * @return A new instance of fragment PassDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(pass: Pass) = when(pass.type) {
            PassType.Day -> newInstance(pass as DayPass)
            PassType.Hour -> newInstance(pass as HourPass)
        }
        @JvmStatic
        fun newInstance(pass: DayPass) =
            PassDetailFragment().apply {
                this.pass = pass
            }

        @JvmStatic
        fun newInstance(pass: HourPass) =
            PassDetailFragment().apply {
                this.pass = pass
            }
    }
}