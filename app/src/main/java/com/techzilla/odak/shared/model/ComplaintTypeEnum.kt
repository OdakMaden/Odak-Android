package com.techzilla.odak.shared.model

enum class ComplaintTypeEnum(val value: String){
    Complaint("Complaint"),
    Suggestion("Suggestion"),
    PaymentProblem("PaymentProblem"),
    SellerProblem("SellerProblem"),
    RefundRequest("RefundRequest")
}