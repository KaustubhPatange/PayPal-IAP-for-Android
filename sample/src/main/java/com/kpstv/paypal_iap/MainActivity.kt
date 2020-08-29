package com.kpstv.paypal_iap

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kpstv.library.Paypal

class MainActivity : AppCompatActivity() {

    private lateinit var paypal: Paypal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = Paypal.Options(
            paypalButtonId = "LE4DPD9LCBG3U",
            purchaseCompleteUrl = "https://kaustubhpatange.github.io/pay",
            isSandbox = true // Set this to false to accept live payments
        )
        paypal = Paypal.Builder(options)
            .setCallingContext(this)
    }

    fun checkout(view: View) {
        paypal.checkout(
            closeOnCheckout = false, // set this to true if you want to close the window after completing checkout.
            onCheckOutComplete = { details ->
                /**
                 * This will be called when checkout is complete.
                 *
                 * Note: During this, user might still be in Purchase screen. This means,
                 * you can't show any form of dialog or overlay from this activity.
                 *
                 * User have to manually go at the bottom of the page and click on
                 * "Return to Merchant" to complete the process. Only after then,
                 * [Paypal.isPurchaseComplete] will be called.
                 *
                 * If you don't want this behaviour and wan't to automatically close the window
                 * after checkout set [closeOnCheckout] to true. In such case [Paypal.isPurchaseComplete]
                 * will not called so it is redundant to write it.
                 */
                Toast.makeText(this, "Completed with ${details?.email}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (paypal.isPurchaseComplete(requestCode, resultCode)) {
            /**
             * Called when whole payment process is completed
             * This will called after onCheckOutComplete.
             *
             * You can also fetch purchase details from [Intent]
             */
            val details = data?.getSerializableExtra(Paypal.PURCHASE_DATA) as? Paypal.History
            AlertDialog.Builder(this)
                .setTitle("Purchase complete")
                .setMessage("Payment completed successfully for ${details?.email} for Id: ${paypal.options.paypalButtonId}")
                .setPositiveButton("OK", null)
                .show()

        } else if (paypal.isPurchaseCancelled(requestCode, resultCode)) {
            /**
             * Called when user cancels the payment or force close the
             *  purchase screen.
             */
            Toast.makeText(this, "Purchase cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}