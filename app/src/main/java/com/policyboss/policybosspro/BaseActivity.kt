package com.policyboss.policybosspro

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.policyboss.policybosspro.databinding.ProgressdialogLoadingBinding
import java.util.regex.Pattern

open class BaseActivity : AppCompatActivity() {

    private lateinit var dialog: Dialog
    private var dialogNoInterNet: AlertDialog? = null
   // private var netWorkErrorLayoutBinding: NetworkErrorLayoutBinding? = null

   // private lateinit var connectivityObserver: ConnectivityObserver
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    //region progress dialog

    open fun displayLoadingWithText(
        text: String? = "Loading...",

        cancelable: Boolean? = false,
    ) { // function -- context(parent (reference))

        var loadingLayout: ProgressdialogLoadingBinding? = null
        try {
            if (!this::dialog.isInitialized) {
                dialog = Dialog(this.applicationContext)
                val requestWindowFeature = dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                if (dialog.window != null) {

                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))

                }
                loadingLayout = ProgressdialogLoadingBinding.inflate(layoutInflater)
                dialog.setContentView(loadingLayout.root)
                // dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(cancelable ?: false)

            }

            loadingLayout?.txtMessage?.text = text


            //hide keyboard
            //view.context.hideKeyboard(view)

            dialog.let {
                if (!it.isShowing) {
                    it.show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideLoading() {
        try {
            if (this.dialog != null) {
                if(dialog.isShowing){
                    dialog.dismiss()
                }

            }
        } catch (e: Exception) {
        }
    }

    //endregion
}