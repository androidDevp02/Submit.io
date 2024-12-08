package com.yogeshj.autoform.user.subscription

data class SubscriptionPaymentModel(
    val userId: String? = null,
    val userEmail:String?=null,
    val planType:String?=null,
    val startDate:Long?=null,
    val endDate:Long?=null,
    val paymentId:String?=null,
    val amountPaid:String?=null,
    val paymentStatus:String?=null,
    val paymentDate:String?=null
    )
