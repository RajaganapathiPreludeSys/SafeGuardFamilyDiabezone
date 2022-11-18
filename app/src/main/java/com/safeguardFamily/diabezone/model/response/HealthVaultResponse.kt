package com.safeguardFamily.diabezone.model.response

import com.google.gson.annotations.SerializedName

data class HealthVaultResponse(
    @SerializedName("is_member") var isMember: Boolean?,
    @SerializedName("is_sample") var isSample: Boolean?,
    @SerializedName("pdf_url") var pdfUrl: String?,
    @SerializedName("vault") var vault: Vault?
)

data class Vault(
    @SerializedName("allergies") var allergies: List<Allergy>?,
    @SerializedName("beneficiary") var beneficiary: List<Beneficiary>?,
    @SerializedName("consolidated_prescription") var consolidatedPrescription: ConsolidatedPrescription?,
    @SerializedName("diagnosis") var diagnosis: List<Diagnosi>?,
    @SerializedName("emergency_contacts") var emergencyContacts: List<EmergencyContact>?,
    @SerializedName("emergency_details") var emergencyDetails: EmergencyDetails?,
    @SerializedName("histories") var histories: List<History>?,
    @SerializedName("insurance") var insurance: Insurance?,
    @SerializedName("lab_reports") var labReports: List<LabReport>?,
    @SerializedName("message") var message: String?,
    @SerializedName("patron") var patron: Patron?,
    @SerializedName("personal_habits") var personalHabits: List<History>?,
    @SerializedName("procedures") var procedures: List<Procedure>?,
    @SerializedName("user") var user: User?,
    @SerializedName("vitals") var vitals: Vitals?
)

data class Allergy(
    @SerializedName("hospital") var hospital: String?,
    @SerializedName("is_alert") var isAlert: Boolean?,
    @SerializedName("prepared_by") var preparedBy: String?,
    @SerializedName("reaction") var reaction: String?,
    @SerializedName("report_date") var reportDate: String?,
    @SerializedName("severity") var severity: String?,
    @SerializedName("title") var title: String?
)

data class Beneficiary(
    @SerializedName("address") var address: String?,
    @SerializedName("age") var age: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("gender") var gender: String?,
    @SerializedName("is_member") var isMember: Boolean?,
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("occupation") var occupation: String?,
    @SerializedName("pic") var pic: String?
)

data class ConsolidatedPrescription(
    @SerializedName("hospital") var hospital: String?,
    @SerializedName("lifestyle_advice") var lifestyleAdvice: String?,
    @SerializedName("pdf_url") var pdfUrl: String?,
    @SerializedName("prepared_by") var preparedBy: String?,
    @SerializedName("recommendations") var recommendations: String?,
    @SerializedName("report_date") var reportDate: String?
)

data class Diagnosi(
    @SerializedName("duration") var duration: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("is_alert") var isAlert: Boolean?,
    @SerializedName("title") var title: String?
)

data class EmergencyContact(
    @SerializedName("mobile1") var mobile1: String?,
    @SerializedName("mobile2") var mobile2: String?,
    @SerializedName("name") var name: String?,
    @SerializedName("relation") var relation: String?
)

data class EmergencyDetails(
    @SerializedName("blood_group") var bloodGroup: String?,
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("primary_doctor") var primaryDoctor: String?
)

data class History(
    @SerializedName("comment") var comment: String?,
    @SerializedName("hospital") var hospital: String?,
    @SerializedName("is_alert") var isAlert: Boolean?,
    @SerializedName("prepared_by") var preparedBy: String?,
    @SerializedName("report_date") var reportDate: String?,
    @SerializedName("status") var status: String?,
    @SerializedName("title") var title: String?
)

data class Insurance(
    @SerializedName("end_date") var endDate: String?,
    @SerializedName("insurer") var insurer: String?,
    @SerializedName("pdf_url") var pdfUrl: String?,
    @SerializedName("policy_name") var policyName: String?,
    @SerializedName("policy_no") var policyNo: String?,
    @SerializedName("start_date") var startDate: String?,
    @SerializedName("sum_assured") var sumAssured: String?,
    @SerializedName("tpa") var tpa: Tpa?
)

data class LabReport(
    @SerializedName("comment") var comment: String?,
    @SerializedName("is_alert") var isAlert: Boolean?,
    @SerializedName("pdf_url") var pdfUrl: String?,
    @SerializedName("prepared_by") var preparedBy: String?,
    @SerializedName("report_date") var reportDate: String?,
    @SerializedName("title") var title: String?
)

data class Patron(
    @SerializedName("address") var address: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("name") var name: String?
)

data class Procedure(
    @SerializedName("procedure_date") var procedureDate: String?,
    @SerializedName("title") var title: String?,
    @SerializedName("comment") var comment: String?
)

data class Vitals(
    @SerializedName("list") var list: List<Vital>?,
    @SerializedName("report_date") var reportDate: String?
)

data class Tpa(
    @SerializedName("email") var email: String?,
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("name") var name: String?
)

data class Vital(
    @SerializedName("name") var name: String?,
    @SerializedName("value") var value: String?
)