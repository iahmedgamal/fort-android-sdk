package com.payfort.fortapisimulator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.payfort.fortapisimulator.activities.CustomUiDialog
import com.payfort.fortapisimulator.activities.ResponseActivity
import com.payfort.fortpaymentsdk.callbacks.PayFortCallback
import com.payfort.fortpaymentsdk.domain.model.FortRequest
import com.payfort.fortpaymentsdk.utils.gone
import com.payfort.fortpaymentsdk.utils.visible
import com.payfort.fortpaymentsdk.views.model.PayComponents
import com.payfort.fortapisimulator.databinding.CustomUiBinding

class CustomUISample : AppCompatActivity(), PayFortCallback {
    private lateinit var binding: CustomUiBinding
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CustomUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val fortRequest: FortRequest = intent.getSerializableExtra("fortRequest") as FortRequest
        val environment = intent.getStringExtra("env")

        val payComponents = PayComponents(binding.etCardNumberView, cvvView = binding.etCardCvv, binding.etCardExpiry, holderNameView = binding.cardHolderNameView)

        binding.btnPay.setup(environment!!, fortRequest, payComponents, this)
        binding.btnDirectPay.setup(environment, fortRequest, this)
        binding.rememberMeTB.setOnCheckedChangeListener { _, isChecked ->
            binding.btnPay.isRememberMeEnabled(isChecked)
        }
    }

    override fun startLoading() {
        Log.e("startLoading", "startLoading")
        binding.progressContainer.visible()
        enableFields(false)
    }

    override fun onSuccess(requestParamsMap: Map<String, Any>, fortResponseMap: Map<String, Any>) {
        stopLoading()
        openResponsePage(gson.toJson(fortResponseMap))
    }

    override fun onFailure(requestParamsMap: Map<String, Any>, fortResponseMap: Map<String, Any>) {
        stopLoading()
        openResponsePage(gson.toJson(fortResponseMap))
    }

    private fun openResponsePage(responseString: String) {
        Log.e("Error", responseString)
        stopLoading()
        val openResponseActivityIntent = Intent(this, ResponseActivity::class.java)
        openResponseActivityIntent.putExtra("responseString", responseString)
        startActivity(openResponseActivityIntent)
    }

    private fun stopLoading() {
        Log.e("startLoading", "startLoading")
        binding.progressContainer.gone()
        enableFields(true)
    }

    private fun enableFields(enableFields: Boolean) {
        binding.cardHolderNameView.isEnabled = enableFields
        binding.etCardNumberView.isEnabled = enableFields
        binding.etCardExpiry.isEnabled = enableFields
        binding.etCardCvv.isEnabled = enableFields
        binding.btnPay.isEnabled = enableFields
    }
}
