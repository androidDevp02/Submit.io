package com.yogeshj.autoform.uploadForm.uploadNewFormFragment

class FormDetails {

    var uid:String?=null
    var icon:String?=null
    var examName:String?=null
    var examHostName:String?=null
    var category:String?=null
    var examDate:String?=null
    var deadline:String?=null
    var examDescription:String?=null
    var eligibility:String?=null
    private var importantDetails:String?=null
    private var paymentNumber:String?=null
    var fees:Int=0
    var status:String?=null

    constructor()

    constructor(uid:String,examName:String,examHostName:String,icon:String,category:String,examDate:String,deadline:String,examDescription:String,eligibility:String,importantDetails:String,paymentNumber:String,fees:Int,status:String){
        this.uid=uid
        this.examName=examName
        this.examHostName=examHostName
        this.icon=icon
        this.category=category
        this.examDate=examDate
        this.deadline=deadline
        this.examDescription=examDescription
        this.eligibility=eligibility
        this.importantDetails=importantDetails
        this.paymentNumber=paymentNumber
        this.fees=fees
        this.status=status
    }
}