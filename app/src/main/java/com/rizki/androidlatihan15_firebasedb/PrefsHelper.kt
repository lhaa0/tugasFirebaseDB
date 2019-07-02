package com.rizki.androidlatihan15_firebasedb

import android.content.Context
import android.content.SharedPreferences

class PrefsHelper {

    val USER_ID = "uidx"
    val COUNTER_ID = "counter_id"

    val mContext : Context
    val sharedSet : SharedPreferences

    constructor(ctx : Context) {
        mContext = ctx
        sharedSet = mContext.getSharedPreferences("APLIKASITESTDB", Context.MODE_PRIVATE)
    }

    fun saveUID(uid : String) {
        val edit = sharedSet.edit()
        edit.putString(USER_ID, uid)
        edit.apply()
    }

    fun getUID() : String?{
        return sharedSet.getString(USER_ID, "")
    }

    fun saveCounterId(counter : Int) {
        val edit = sharedSet.edit()
        edit.putInt(COUNTER_ID, counter)
        edit.apply()
    }

    fun getCounterId() : Int{
        return sharedSet.getInt(COUNTER_ID, 1)
    }
}