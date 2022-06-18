package com.theappschef.boxnbiz

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Profile(val company: String? = null, val name: String? = null,val phone: String? = null,val promo: String? = null,val pass:String?=null,val suffix:String?=null) {

}