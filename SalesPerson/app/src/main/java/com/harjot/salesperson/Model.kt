package com.harjot.salesperson

import com.google.firestore.admin.v1.Index.IndexField.Order

data class Model(
    var id:String?=null,
    var vendorName:String?=null,
    var inTime:String?=null,
    var outTime:String?=null,
    var inLocation:String?=null,
    var outLocation:String?=null,
    var inLat:String?=null,
    var inLong:String?=null,
    var outLat:String?=null,
    var outLong:String?=null,
    var order: String?=null,
    var productName: String?=null,
    var quantity: String?=null,
    var date:String?=null
)
