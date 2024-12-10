package com.yogeshj.autoform.user.userFormFragment.examApply

data class PaymentDataModel(
    val userId: String? = null,
    val examId:String?=null,
    val amount: Int = 0,
    val examName:String?=null,
    val examHostName:String?=null,
    val paymentStatus: String? = null,
    val email:String?=null
)