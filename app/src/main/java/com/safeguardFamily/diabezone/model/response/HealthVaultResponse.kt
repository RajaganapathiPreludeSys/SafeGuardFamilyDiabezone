package com.safeguardFamily.diabezone.model.response
import com.google.gson.annotations.SerializedName

data class HealthVaultResponse(
    @SerializedName("is_member")
    var isMember: Boolean?,
    @SerializedName("is_sample")
    var isSample: Boolean?,
    @SerializedName("pdf_url")
    var pdfUrl: String?,
    var vault: Vault?
)

data class Vault(
    var allergies: List<Allergy>?,
    var beneficiary: List<Beneficiary>?,
    @SerializedName("consolidated_prescription")
    var consolidatedPrescription: ConsolidatedPrescription?,
    var diagnosis: List<String>?,
    @SerializedName("emergency_contacts")
    var emergencyContacts: List<EmergencyContact>?,
    @SerializedName("emergency_details")
    var emergencyDetails: EmergencyDetails?,
    var histories: List<History>?,
    var insurance: Insurance?,
    @SerializedName("lab_reports")
    var labReports: List<LabReport>?,
    var message: String?,
    var patron: Patron?,
    var user: User?,
    var vitals: Vitals?
)

data class Allergy(
    var hospital: String?,
    @SerializedName("is_alert")
    var isAlert: Boolean?,
    @SerializedName("prepared_by")
    var preparedBy: String?,
    var reaction: String?,
    @SerializedName("report_date")
    var reportDate: String?,
    var severity: String?,
    var title: String?
)

data class Beneficiary(
    var address: String?,
    var age: String?,
    var email: String?,
    var gender: String?,
    @SerializedName("is_member")
    var isMember: Boolean?,
    var mobile: String?,
    var name: String?,
    var occupation: String?,
    var pic: String?
)

data class ConsolidatedPrescription(
    var hospital: String?,
    @SerializedName("lifestyle_advice")
    var lifestyleAdvice: String?,
    @SerializedName("pdf_url")
    var pdfUrl: String?,
    @SerializedName("prepared_by")
    var preparedBy: String?,
    var recommendations: String?,
    @SerializedName("report_date")
    var reportDate: String?
)

data class EmergencyContact(
    var mobile1: String?,
    var mobile2: String?,
    var name: String?,
    var relation: String?
)

data class EmergencyDetails(
    @SerializedName("blood_group")
    var bloodGroup: String?,
    var mobile: String?,
    @SerializedName("primary_doctor")
    var primaryDoctor: String?
)

data class History(
    var condition: String?,
    var hospital: String?,
    @SerializedName("is_alert")
    var isAlert: Boolean?,
    @SerializedName("prepared_by")
    var preparedBy: String?,
    @SerializedName("report_date")
    var reportDate: String?,
    var title: String?
)

data class Insurance(
    @SerializedName("end_date")
    var endDate: String?,
    var insurer: String?,
    @SerializedName("pdf_url")
    var pdfUrl: String?,
    @SerializedName("policy_name")
    var policyName: String?,
    @SerializedName("policy_no")
    var policyNo: String?,
    @SerializedName("start_date")
    var startDate: String?,
    @SerializedName("sum_assured")
    var sumAssured: String?,
    var tpa: Tpa?
)

data class LabReport(
    var comment: String?,
    @SerializedName("is_alert")
    var isAlert: Boolean?,
    @SerializedName("pdf_url")
    var pdfUrl: String?,
    @SerializedName("prepared_by")
    var preparedBy: String?,
    @SerializedName("report_date")
    var reportDate: String?,
    var title: String?
)

data class Patron(
    var address: String?,
    var email: String?,
    var mobile: String?,
    var name: String?
)

data class Vitals(
    @SerializedName("BMI")
    var bMI: String?,
    @SerializedName("BP")
    var bP: String?,
    @SerializedName("Height")
    var height: String?,
    @SerializedName("Pulse Rate")
    var pulseRate: String?,
    @SerializedName("RBS")
    var rBS: String?,
    @SerializedName("SPO2")
    var sPO2: String?,
    @SerializedName("Temperature")
    var temperature: String?,
    @SerializedName("Weight")
    var weight: String?,
    @SerializedName("last_updated_date")
    var date: String?

)

data class Tpa(
    var email: String?,
    var mobile: String?,
    var name: String?
)