package com.safeguardFamily.diabezone.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.adapter.*
import com.safeguardFamily.diabezone.base.BaseFragment
import com.safeguardFamily.diabezone.common.Bundle.KEY_DOCTOR
import com.safeguardFamily.diabezone.common.Bundle.KEY_TITLE
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_KEY
import com.safeguardFamily.diabezone.common.Bundle.KEY_WEB_URL
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormatTwo
import com.safeguardFamily.diabezone.common.DateUtils.displayingDateFormatTwoFromAPIDateTime
import com.safeguardFamily.diabezone.common.DateUtils.displayingTimeFormat
import com.safeguardFamily.diabezone.common.SharedPref
import com.safeguardFamily.diabezone.common.SharedPref.Pref.PrefHealthCoach
import com.safeguardFamily.diabezone.databinding.FragmentHealthVaultBinding
import com.safeguardFamily.diabezone.model.response.*
import com.safeguardFamily.diabezone.ui.activity.DoctorDetailsActivity
import com.safeguardFamily.diabezone.ui.activity.SubscriptionActivity
import com.safeguardFamily.diabezone.ui.activity.WebViewActivity
import com.safeguardFamily.diabezone.viewModel.HealthVaultViewModel

class HealthVaultFragment : BaseFragment<FragmentHealthVaultBinding, HealthVaultViewModel>(
    R.layout.fragment_health_vault,
    HealthVaultViewModel::class.java
) {

    override fun onceCreated() {
        mBinding.mViewModel = mViewModel

        mViewModel.isSample.observe(this) {
            mBinding.rlMemberContainer.visibility = if (it) View.VISIBLE else View.GONE
            mBinding.tvDesc.text = if (it) "Example HV PDF" else "HV PDF"
        }
        mViewModel.healthVault.observe(this) {
            loadTextAreas(
                it.message,
                it.patron,
                it.user,
                it.insurance,
                it.consolidatedPrescription,
                it.vitals
            )
            loadEmergencyContact(it.emergencyContacts, it.emergencyDetails)
            loadLabReport(it.labReports)
            loadAllergies(it.allergies)
            loadHistory(it.histories)
            loadDiagnosis(it.diagnosis)
            loadProcedure(it.procedures)
            loadHabit(it.personalHabits)
            loadBeneficiary(it.beneficiary)

//            loadEmergencyContact(arrayListOf(), it.emergencyDetails)
//            loadLabReport(arrayListOf())
//            loadAllergies(arrayListOf())
//            loadHistory(arrayListOf())
//            loadDiagnosis(arrayListOf())
//            loadProcedure(arrayListOf())
//            loadHabit(arrayListOf())
//            loadBeneficiary(arrayListOf())

            mBinding.tvDesc.setOnClickListener {
                if (mViewModel.pdfURL.length > 2) openWebView(mViewModel.pdfURL)
                else showToast("Health Vault PDF not available for now. Please contact your health coach")
            }
        }
    }

    private fun loadBeneficiary(beneficiary: List<Beneficiary>?) {

        if (beneficiary!!.isNotEmpty()) {
            mBinding.rlMemberContainer.visibility = View.VISIBLE
            mBinding.tvBeneficiary.visibility = View.GONE
            mBinding.llBeneficiary.visibility = View.VISIBLE
            mBinding.tvName.text = beneficiary[0].name
            mBinding.tvDetails.text =
                "${beneficiary[0].gender}, ${beneficiary[0].age}, ${beneficiary[0].occupation}"
            mBinding.tvEmail.text = beneficiary[0].email
            mBinding.tvMobile.text = beneficiary[0].mobile
            mBinding.tvLocation.text = beneficiary[0].address
            Glide.with(this).load(beneficiary[0].pic).placeholder(R.drawable.ic_profile_thumb)
                .into(mBinding.ivImages)
        } else {
            mBinding.rlMemberContainer.visibility = View.GONE
            mBinding.tvBeneficiary.visibility = View.VISIBLE
            mBinding.llBeneficiary.visibility = View.GONE
            mBinding.tvBeneficiary.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadHabit(habits: List<String>?) {
        if (habits!!.isNotEmpty()) {
            mBinding.rvHabits.visibility = View.VISIBLE
            mBinding.tvHabits.visibility = View.GONE
            mBinding.rvHabits.adapter = HabitsAdapter(habits)
            mBinding.rvHabits.layoutManager =
                GridLayoutManager(requireContext(), 5, RecyclerView.HORIZONTAL, false)
            mBinding.rvHabits.setHasFixedSize(false)
        } else {
            mBinding.rvHabits.visibility = View.GONE
            mBinding.tvHabits.visibility = View.VISIBLE
            mBinding.tvHabits.setOnClickListener { contactHealthCoach() }
        }

    }

    private fun loadProcedure(procedures: List<Procedure>?) {
        if (procedures!!.isNotEmpty()) {
            mBinding.rvProcedures.visibility = View.VISIBLE
            mBinding.tvProcedures.visibility = View.GONE
            mBinding.rvProcedures.adapter = ProcedureAdapter(procedures)
            mBinding.rvProcedures.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvProcedures.setHasFixedSize(false)
        } else {
            mBinding.rvProcedures.visibility = View.GONE
            mBinding.tvProcedures.visibility = View.VISIBLE
            mBinding.tvProcedures.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadDiagnosis(diagnosis: List<Diagnosi>?) {
        if (diagnosis!!.isNotEmpty()) {
            mBinding.rvDiagnosis.visibility = View.VISIBLE
            mBinding.tvDiagnosis.visibility = View.GONE
            mBinding.rvDiagnosis.adapter = DiagnosisAdapter(diagnosis)
            mBinding.rvDiagnosis.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvDiagnosis.setHasFixedSize(true)
        } else {
            mBinding.rvDiagnosis.visibility = View.GONE
            mBinding.tvDiagnosis.visibility = View.VISIBLE
            mBinding.tvDiagnosis.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadHistory(histories: List<History>?) {
        if (histories!!.isNotEmpty()) {
            mBinding.rvHistory.visibility = View.VISIBLE
            mBinding.tvHistory.visibility = View.GONE
            mBinding.rvHistory.adapter = HistoryAdapter(histories)
            mBinding.rvHistory.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvHistory.setHasFixedSize(false)
        } else {
            mBinding.rvHistory.visibility = View.GONE
            mBinding.tvHistory.visibility = View.VISIBLE
            mBinding.tvHistory.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadAllergies(allergies: List<Allergy>?) {
        if (allergies!!.isNotEmpty()) {
            mBinding.rvAllergies.visibility = View.VISIBLE
            mBinding.tvAllergies.visibility = View.GONE
            mBinding.rvAllergies.adapter = AllergyAdapter(allergies)
            mBinding.rvAllergies.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvAllergies.setHasFixedSize(false)
        } else {
            mBinding.rvAllergies.visibility = View.GONE
            mBinding.tvAllergies.visibility = View.VISIBLE
            mBinding.tvAllergies.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadLabReport(reports: List<LabReport>?) {
        if (reports!!.isNotEmpty()) {
            mBinding.rvLabReport.visibility = View.VISIBLE
            mBinding.tvLabReport.visibility = View.GONE
            mBinding.rvLabReport.adapter = LabReportAdapter(reports) {
                if (it.length > 2) openWebView(it)
                else showToast("Lab Report PDF not available for now. Please contact your health coach")
            }
            mBinding.rvLabReport.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvLabReport.setHasFixedSize(false)
        } else {
            mBinding.rvLabReport.visibility = View.GONE
            mBinding.tvLabReport.visibility = View.VISIBLE
            mBinding.tvLabReport.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadEmergencyContact(
        contacts: List<EmergencyContact>?,
        details: EmergencyDetails?
    ) {
        if (details != null) {
            mBinding.llEmergencyContainer.visibility = View.VISIBLE
            mBinding.view1.visibility = View.VISIBLE
            mBinding.tvEmergency.visibility = View.GONE
            mBinding.tvBloodGroup.text = details.bloodGroup
            mBinding.tvPrimaryDoctor.text = details.primaryDoctor
            mBinding.tvEmergencyContact.text = details.mobile
        } else {
            mBinding.llEmergencyContainer.visibility = View.GONE
            mBinding.view1.visibility = View.GONE
            mBinding.tvEmergency.visibility = View.VISIBLE
            mBinding.tvEmergency.setOnClickListener { contactHealthCoach() }
        }
        mBinding.rvEmergencyContact.adapter = EmergencyContactAdapter(contacts!!)
        mBinding.rvEmergencyContact.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvEmergencyContact.setHasFixedSize(false)
    }

    private fun loadTextAreas(
        message: String?,
        patron: Patron?,
        user: User?,
        insurance: Insurance?,
        prescription: ConsolidatedPrescription?,
        vitals: Vitals?
    ) {

        mBinding.tvHVDesc.text = message
        mBinding.tvTitle.text = user!!.name

        Glide.with(this).load(user.pic).placeholder(R.drawable.ic_profile_thumb)
            .into(mBinding.ivProfileImages)

        //      Patron details
        if (patron != null) {
            mBinding.cvPatron.visibility = View.VISIBLE
            mBinding.tvPatronName.text = patron.name
            mBinding.tvPatronEmail.text = patron.email
            mBinding.tvPatronContact.text = patron.mobile
            mBinding.tvPatronAddress.text = patron.address
        } else mBinding.cvPatron.visibility = View.GONE

        //      Insurance Details
        if (insurance != null) {
            mBinding.llInsurance.visibility = View.VISIBLE
            mBinding.tvInsurance.visibility = View.GONE
            mBinding.ivInsurancePdf.visibility = View.VISIBLE
            mBinding.tvInsurer.text = insurance.insurer
            mBinding.tvPolicyName.text = insurance.policyName
            mBinding.tvPolicyNo.text = insurance.policyNo
            mBinding.tvPeriod.text =
                "${displayingDateFormatTwo(insurance.startDate!!)} - ${
                    displayingDateFormatTwo(
                        insurance.endDate!!
                    )
                }"
            mBinding.tvAmount.text = "₹ ${insurance.sumAssured}"
            mBinding.tvPolicyHolder.text = insurance.tpa!!.name
            mBinding.tvPolicyEmail.text = insurance.tpa!!.email
            mBinding.tvPolicyContact.text = insurance.tpa!!.mobile

            mBinding.ivInsurancePdf.setOnClickListener {
                if (insurance.pdfUrl!!.length > 2) openWebView(insurance.pdfUrl!!)
                else showToast("Insurance PDF not available for now. Please contact your health coach")
            }
        } else {
            mBinding.llInsurance.visibility = View.GONE
            mBinding.tvInsurance.visibility = View.VISIBLE
            mBinding.ivInsurancePdf.visibility = View.INVISIBLE
            mBinding.tvInsurance.setOnClickListener { contactHealthCoach() }
        }

        //      Consolidated Prescription
        if (prescription != null) {
            mBinding.llPrescription.visibility = View.VISIBLE
            mBinding.tvPrescription.visibility = View.GONE
            mBinding.tvPreparedBy.text = prescription.preparedBy
            mBinding.tvHospital.text = prescription.hospital
            mBinding.tvDate.text = displayingDateFormatTwoFromAPIDateTime(prescription.reportDate!!)
            mBinding.tvTime.text = displayingTimeFormat(prescription.reportDate!!)
            mBinding.tvLifestyleDate.text =
                displayingDateFormatTwoFromAPIDateTime(prescription.lifestyleAdvice!!)
            mBinding.tvLifestyleTime.text = displayingTimeFormat(prescription.lifestyleAdvice!!)
            mBinding.tvRecommendationDate.text =
                displayingDateFormatTwoFromAPIDateTime(prescription.recommendations!!)
            mBinding.tvRecommendationTime.text =
                displayingTimeFormat(prescription.recommendations!!)
            mBinding.ivConsolidatedPdf.setOnClickListener {
                if (prescription.pdfUrl!!.length > 2) openWebView(prescription.pdfUrl!!)
                else showToast("Consolidated Prescription PDF not available for now. Please contact your health coach")
            }
        } else {
            mBinding.llPrescription.visibility = View.GONE
            mBinding.tvPrescription.visibility = View.VISIBLE
            mBinding.tvPrescription.setOnClickListener { contactHealthCoach() }
        }

        //      Vitals
        if (vitals != null) {
            mBinding.llVitals.visibility = View.VISIBLE
            mBinding.tvVitals.visibility = View.GONE
            mBinding.tvPulseRate.text = vitals.pulseRate
            mBinding.tvFastingBloodSugar.text = vitals.fastingBloodSugar
            mBinding.tvPostParandial.text = vitals.postPrandialBloodSugar
            mBinding.tvhba.text = vitals.hbA1C
            mBinding.tvSystolicBloodPressure.text = vitals.systolicBloodPressure
            mBinding.tvDiastolicBloodPressure.text = vitals.diastolicBloodPressure
            mBinding.tvTotalCholesterol.text = vitals.totalCholesterol
            mBinding.tvTriglyceride.text = vitals.triglyceride
            if (vitals.lastUpdatedDate != null)
                mBinding.tvVitalDate.text =
                    displayingDateFormatTwoFromAPIDateTime(vitals.lastUpdatedDate!!)
        } else {
            mBinding.llVitals.visibility = View.GONE
            mBinding.tvVitals.visibility = View.VISIBLE
            mBinding.tvVitals.setOnClickListener { contactHealthCoach() }
        }

    }

    private fun openWebView(url: String) {
        val mBundle = Bundle()
        mBundle.putString(KEY_WEB_KEY, "PDF")
        mBundle.putString(KEY_WEB_URL, url)
        navigateTo(WebViewActivity::class.java, mBundle)
    }

    private fun contactHealthCoach() {
        if (SharedPref.isMember()) {
            val bundle = Bundle()
            bundle.putString(KEY_DOCTOR, SharedPref.read(PrefHealthCoach, ""))
            bundle.putString(KEY_TITLE, "Health Coach")
            navigateTo(DoctorDetailsActivity::class.java, bundle)

        } else {
            showToast("Only members can contact their Health Coach")
            navigateTo(SubscriptionActivity::class.java)
        }
    }

}