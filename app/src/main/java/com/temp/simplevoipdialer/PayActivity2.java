package com.temp.simplevoipdialer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

/**
 * Created by klim-mobile on 11.09.2015.
 */
public class PayActivity2 extends Activity {

    private static final String TAG = "paymentExample";
    private static final int REQUEST_CODE_PAYMENT = 1;
//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
//    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "ActEIUZNMZcRwwnsf-KcafZI9pB0d1VdyLIPJVHgl2xPYujObunVDSzWkuFEcNxzLFHchUkgFXZBDwrB";
//    private static final String CONFIG_CLIENT_ID = "Ac6mr1yPfXd-_VNRXoyPY6mrsdmWF-xA7dgAglo_NgOcMfCsm2JLFYA4eZVrmw97zNow_bU0pECWOlrL";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    private EditText amount;
    private String mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_layout2);

        amount = (EditText) findViewById(R.id.amount);
        amount.setText("1");


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */

        mAmount = amount.getText().toString();

        if (mAmount == null || mAmount.equals("")) {
            Toast.makeText(getApplicationContext(), "Payment amount 0", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(PayActivity2.this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {

        return new PayPalPayment(new BigDecimal(mAmount), "USD", "VoIp payment",
                paymentIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        Toast.makeText(
                                getApplicationContext(),
                                "PaymentConfirmation info received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }
}
