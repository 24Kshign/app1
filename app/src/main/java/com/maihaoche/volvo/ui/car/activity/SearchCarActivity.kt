package com.maihaoche.volvo.ui.car.activity

import com.maihaoche.commonbiz.module.ui.BaseFragmentActivity
import com.maihaoche.volvo.R
import com.maihaoche.volvo.ui.car.fragment.SearchCarFragment
import kotlinx.android.synthetic.main.activity_search_car.*


/**
 * Created by manji
 * Date：2018/12/4 9:53 AM
 * Desc：
 */
class SearchCarActivity : BaseFragmentActivity() {

    private lateinit var mSearchCarFragment: SearchCarFragment

    override fun bindLayoutRes(): Int {
        return R.layout.activity_search_car
    }

    override fun initView() {
        super.initView()
        ascIvBack.setOnClickListener { finish() }
        mSearchCarFragment = SearchCarFragment.create()
        val fragmentTransaction = supportFragmentManager.beginTransaction()

//        fragmentTransaction.add(R.id.ascFlContainer, CheckCarFragment.create(CheckCarFragment.TYPE_CHECK_CAR_TODAY))
        fragmentTransaction.add(R.id.ascFlContainer, mSearchCarFragment)
        fragmentTransaction.commit()
    }
}