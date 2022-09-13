package com.safeguardFamily.diabezone.common

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefHealthCoach
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefIsMember
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefMembership
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefUser
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefUserId
import com.safeguardFamily.diabezone.model.response.Membership
import com.safeguardFamily.diabezone.model.response.ProfileResponse
import com.safeguardFamily.diabezone.model.response.User

object SharedPref {

    private var mSharedPref: SharedPreferences? = null

    object Pref {
        const val PrefIsMember = "PrefIsMember"
        const val PrefUser = "PrefUser"
        const val PrefUserId = "PrefUserId"
        const val PrefMembership = "PrefMembership"
        const val PrefHealthCoach = "PrefHealthCoach"
    }

    fun init(context: Context) {
        if (mSharedPref == null) mSharedPref =
            context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
    }

    fun isMember() = read(PrefIsMember, false)

    fun getUserId() = read(PrefUserId, "")

    fun getUser(): User = Gson().fromJson(read(PrefUser, ""), User::class.java)


    fun putUser(user: User) {
        write(PrefUserId, user.uid)
        write(PrefUser, Gson().toJson(user))
    }

    fun putProfileDetails(profile: ProfileResponse) {
        putUser(profile.user!!)
        write(PrefMembership, Gson().toJson(profile.membership))
        write(PrefIsMember, profile.is_member!!)
        write(PrefHealthCoach, Gson().toJson(profile.health_coach))
    }

    fun getMembership(): List<Membership> =
        Gson().fromJson(read(PrefMembership, ""), Array<Membership>::class.java).toList()

    fun read(key: String, defValue: String?) = mSharedPref!!.getString(key, defValue)

    fun write(key: String, value: String) {
        mSharedPref!!.edit().putString(key, value).apply()
    }

    fun read(key: String, defValue: Boolean) = mSharedPref!!.getBoolean(key, defValue)

    fun write(key: String, value: Boolean) = mSharedPref!!.edit().putBoolean(key, value).apply()

    fun read(key: String, defValue: Int) = mSharedPref!!.getInt(key, defValue)

    fun write(key: String, value: Int) = mSharedPref!!.edit().putInt(key, value).apply()

    fun clearAll() {
        mSharedPref!!.edit().clear().apply()
    }
}