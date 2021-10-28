package com.firstapp.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.solbios.other.Constants.email
import com.solbios.other.Constants.mobileNumber

class SessionManagement(context: Context) {

     private var sharedPreference: SharedPreferences?= null


    init {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context, // fileName,
            "",
            mainKey, // masterKeyAlias
            // context
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ).also { sharedPreference = it }
    }

    fun setUserEmail(email: String?){
        setSharedPreference("email",email.toString())
    }
    fun getUserEmail():String?{
        return getSharedPreference("email")
    }

    fun setUserName(name:String?){
        setSharedPreference("name",name.toString())
    }
    fun getUserName():String?{
        return getSharedPreference("name")
    }
    fun setUserMobileNumber(mobileNumber:String?){
        setSharedPreference("mobileNumber",mobileNumber.toString())
    }
    fun getUserMobileNumber():String?{
        return getSharedPreference("mobileNumber")
    }

    fun setUserPassword(password:String){
        setSharedPreference("Password",password)
    }

    fun getPassword(): String?{
        return getSharedPreference("Password")
    }
    fun setUserId(userId:String?){
        setSharedPreference("userId",userId.toString())
    }
    fun getUserId():String?{
        return getSharedPreference("userId")
    }
    fun setToken(token:String?){
        setSharedPreference("token",token.toString())
    }
    fun getToken():String?{
        return  getSharedPreference("token")

    }
    fun setStatus(status:String?){
        setSharedPreference("status",status.toString())
    }
    fun getStatus():String?{
      return getSharedPreference("status")
    }

    fun setIsMobileVerify(mobileVerify:String?){
        setSharedPreference("mobileVerify",mobileVerify.toString())
    }
    fun getIsMobileVerify():String?{
        return getSharedPreference("mobileVerify")
    }

    private fun getSharedPreference(key: String): String?{
        return sharedPreference?.getString(key,null)
    }


     private fun setSharedPreference(key:String, value:String) {
         val editor = sharedPreference?.edit()
         editor?.putString(key,value)
             editor?.apply()

     }

    fun clearSharedPreference(){
        val editor = sharedPreference?.edit()
        editor?.clear()
            editor?.apply()

    }
}