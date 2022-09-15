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
                it.message!!,
                it.beneficiary!![0],
                it.patron!!,
                it.user!!,
                it.insurance!!,
                it.consolidatedPrescription!!,
                it.vitals!!
            )
            loadEmergencyContact(it.emergencyContacts!!, it.emergencyDetails!!)
            loadLabReport(it.labReports!!)
            loadAllergies(it.allergies!!)
            loadHistory(it.histories!!)
            loadDiagnosis(it.diagnosis!!)
//
//            loadLabReport(arrayListOf())
//            loadAllergies(arrayListOf())
//            loadHistory(arrayListOf())
//            loadDiagnosis(arrayListOf())

            mBinding.tvDesc.setOnClickListener {
                if (mViewModel.pdfURL.length > 2) openWebView(mViewModel.pdfURL)
                else showToast("Health Vault PDF not available for now. Please contact your health coach")
            }
        }
    }

    private fun loadDiagnosis(diagnosis: List<String>) {
        if (diagnosis.isNotEmpty()) {
            mBinding.rvDiagnosis.visibility = View.VISIBLE
            mBinding.tvDiagnosis.visibility = View.GONE
            mBinding.rvDiagnosis.adapter = DiagnosisAdapter(diagnosis)
            mBinding.rvDiagnosis.layoutManager =
                GridLayoutManager(requireContext(), 5, RecyclerView.HORIZONTAL, false)
            mBinding.rvDiagnosis.setHasFixedSize(true)
        } else {
            mBinding.rvDiagnosis.visibility = View.GONE
            mBinding.tvDiagnosis.visibility = View.VISIBLE
            mBinding.tvDiagnosis.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadHistory(histories: List<History>) {
        if (histories.isNotEmpty()) {
            mBinding.rvHistory.visibility = View.VISIBLE
            mBinding.tvHistory.visibility = View.GONE
            mBinding.rvHistory.adapter = HistoryAdapter(histories)
            mBinding.rvHistory.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvHistory.setHasFixedSize(true)
        } else {
            mBinding.rvHistory.visibility = View.GONE
            mBinding.tvHistory.visibility = View.VISIBLE
            mBinding.tvHistory.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadAllergies(allergies: List<Allergy>) {
        if (allergies.isNotEmpty()) {
            mBinding.rvAllergies.visibility = View.VISIBLE
            mBinding.tvAllergies.visibility = View.GONE
            mBinding.rvAllergies.adapter = AllergyAdapter(allergies)
            mBinding.rvAllergies.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvAllergies.setHasFixedSize(true)
        } else {
            mBinding.rvAllergies.visibility = View.GONE
            mBinding.tvAllergies.visibility = View.VISIBLE
            mBinding.tvAllergies.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadLabReport(reports: List<LabReport>) {
        if (reports.isNotEmpty()) {
            mBinding.rvLabReport.visibility = View.VISIBLE
            mBinding.tvLabReport.visibility = View.GONE
            mBinding.rvLabReport.adapter = LabReportAdapter(reports) {
                if (it.length > 2) openWebView(it)
                else showToast("Lab Report PDF not available for now. Please contact your health coach")
            }
            mBinding.rvLabReport.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.HORIZONTAL, false)
            mBinding.rvLabReport.setHasFixedSize(true)
        } else {
            mBinding.rvLabReport.visibility = View.GONE
            mBinding.tvLabReport.visibility = View.VISIBLE
            mBinding.tvLabReport.setOnClickListener { contactHealthCoach() }
        }
    }

    private fun loadEmergencyContact(contacts: List<EmergencyContact>, details: EmergencyDetails) {
        mBinding.tvBloodGroup.text = details.bloodGroup
        mBinding.tvPrimaryDoctor.text = details.primaryDoctor
        mBinding.tvEmergencyContact.text = details.mobile
        mBinding.rvEmergencyContact.adapter = EmergencyContactAdapter(contacts)
        mBinding.rvEmergencyContact.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mBinding.rvEmergencyContact.setHasFixedSize(true)
    }

    private fun loadTextAreas(
        message: String,
        beneficiary: Beneficiary,
        patron: Patron,
        user: User,
        insurance: Insurance,
        prescription: ConsolidatedPrescription,
        vitals: Vitals
    ) {

        mBinding.tvHVDesc.text = message
        mBinding.tvTitle.text = user.name

        Glide.with(this).load(user.pic).placeholder(R.drawable.ic_profile_thumb)
            .into(mBinding.ivProfileImages)

        //      Beneficiary
        mBinding.tvName.text = beneficiary.name
        mBinding.tvDetails.text =
            "${beneficiary.gender}, ${beneficiary.age}, ${beneficiary.occupation}"
        mBinding.tvEmail.text = beneficiary.email
        mBinding.tvMobile.text = beneficiary.mobile
        mBinding.tvLocation.text = beneficiary.address
        Glide.with(this).load(beneficiary.pic).placeholder(R.drawable.ic_profile_thumb)
            .into(mBinding.ivImages)

        //      Patron details
        mBinding.tvPatronName.text = patron.name
        mBinding.tvPatronEmail.text = patron.email
        mBinding.tvPatronContact.text = patron.mobile
        mBinding.tvPatronAddress.text = patron.address

        //      Insurance Details
        mBinding.tvInsurer.text = insurance.insurer
        mBinding.tvPolicyName.text = insurance.policyName
        mBinding.tvPolicyNo.text = insurance.policyNo
        mBinding.tvPeriod.text =
            "${displayingDateFormatTwo(insurance.startDate!!)} - ${displayingDateFormatTwo(insurance.endDate!!)}"
        mBinding.tvAmount.text = "₹ ${insurance.sumAssured}"
        mBinding.tvPolicyHolder.text = insurance.tpa!!.name
        mBinding.tvPolicyEmail.text = insurance.tpa!!.email
        mBinding.tvPolicyContact.text = insurance.tpa!!.mobile

        mBinding.ivInsurancePdf.setOnClickListener {
            if (insurance.pdfUrl!!.length > 2) openWebView(insurance.pdfUrl!!)
            else showToast("Insurance PDF not available for now. Please contact your health coach")
        }

        //      Consolidated Prescription
        mBinding.tvPreparedBy.text = prescription.preparedBy
        mBinding.tvHospital.text = prescription.hospital
        mBinding.tvDate.text = displayingDateFormatTwoFromAPIDateTime(prescription.reportDate!!)
        mBinding.tvTime.text = displayingTimeFormat(prescription.reportDate!!)
        mBinding.tvLifestyleDate.text =
            displayingDateFormatTwoFromAPIDateTime(prescription.lifestyleAdvice!!)
        mBinding.tvLifestyleTime.text = displayingTimeFormat(prescription.lifestyleAdvice!!)
        mBinding.tvRecommendationDate.text =
            displayingDateFormatTwoFromAPIDateTime(prescription.recommendations!!)
        mBinding.tvRecommendationTime.text = displayingTimeFormat(prescription.recommendations!!)
        mBinding.ivConsolidatedPdf.setOnClickListener {
            if (prescription.pdfUrl!!.length > 2) openWebView(prescription.pdfUrl!!)
            else showToast("Consolidated Prescription PDF not available for now. Please contact your health coach")
        }

        //      Vitals
        mBinding.tvWeight.text = vitals.weight
        mBinding.tvHeight.text = vitals.height
        mBinding.tvBMI.text = vitals.bMI
        mBinding.tvBP.text = vitals.bP
        mBinding.tvRBS.text = vitals.rBS
        mBinding.tvSPO.text = vitals.sPO2
        mBinding.tvTemperature.text = vitals.temperature
        mBinding.tvPulse.text = vitals.pulseRate
        mBinding.tvVitalDate.text = displayingDateFormatTwoFromAPIDateTime(vitals.date!!)

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