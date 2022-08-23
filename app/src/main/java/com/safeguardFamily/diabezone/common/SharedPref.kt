package com.safeguardFamily.diabezone.common

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefMembership
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefUser
import com.safeguardFamily.diabezone.model.response.Membership
import com.safeguardFamily.diabezone.model.response.User

object SharedPref {

    private var mSharedPref: SharedPreferences? = null

    object Pref {
        const val PrefIsMember = "PrefIsMember"
        const val PrefUser = "PrefUser"
        const val PrefMembership = "PrefMembership"
    }

    fun init(context: Context) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun getUser(): User = Gson().fromJson(read(PrefUser, ""), User::class.java)

    fun putUser(user: User) = write(PrefUser, Gson().toJson(user))

    fun getMembership(): List<Membership> =
        Gson().fromJson(read(PrefMembership, ""), Array<Membership>::class.java).toList()

    fun putMembership(membership: List<Membership>) =
        write(PrefMembership, Gson().toJson(membership))

    fun read(key: String, defValue: String?) = mSharedPref!!.getString(key, defValue)

    fun write(key: String, value: String) {
        mSharedPref!!.edit().putString(key, value).apply()
    }

    fun read(key: String, defValue: Boolean) = mSharedPref!!.getBoolean(key, defValue)

    fun write(key: String, value: Boolean) = mSharedPref!!.edit().putBoolean(key, value).apply()

    fun read(key: String, defValue: Int) = mSharedPref!!.getInt(key, defValue)

    fun write(key: String, value: Int) {
        mSharedPref!!.edit().putInt(key, value).apply()
    }

    fun clearAll() {
        mSharedPref!!.edit().clear().apply()
    }
}