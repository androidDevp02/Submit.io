package com.yogeshj.autoform.authentication

class User {
    var uid:String?=null
    var name:String?=null
    var email:String?=null
    var field1:String?=null
    var field2:String?=null
    var field3:String?=null
    var phone:String?=null
    var dob:String?=null
    var gender:String?=null
    var state:String?=null

    constructor()

    constructor(uid:String,name:String,email: String){
        this.uid=uid
        this.name=name
        this.email=email
    }






}