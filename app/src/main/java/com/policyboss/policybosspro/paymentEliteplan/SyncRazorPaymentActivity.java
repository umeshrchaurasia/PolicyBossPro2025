package com.policyboss.policybosspro.paymentEliteplan;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.policyboss.policybosspro.BaseJavaActivity;
import com.policyboss.policybosspro.R;
import com.policyboss.policybosspro.core.model.paymentElite.PaymentEliteEntity;
import com.policyboss.policybosspro.core.model.sysncContact.POSPHorizonEntity;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.response.APIResponse;
import com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse.synctransactionDetailEntity;
import com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse.synctransactionDetailReponse;
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager;
import com.policyboss.policybosspro.view.home.HomeActivity;
import com.policyboss.policybosspro.webview.SyncWebViewActivity;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SyncRazorPaymentActivity extends BaseJavaActivity implements PaymentResultListener, View.OnClickListener, IResponseSubcriber {


    private static final String TAG = "RAZOR_PAYMENT";
    Button btnBuy, btnCancel, btnContinue, btnHomeContinue;
    CardView cvBuy, cvSuccess, cvFailure;
    TextView txtCustomerName, txtProdName, txtDisplayAmount, txtSuccessMessage, txtSuccessTitle, txtFailureMessage,
            txtpaymentstatus,txtfailcustid;

    @Inject
    PolicyBossPrefsManager prefManager;

    synctransactionDetailEntity synctransactionEntity;

    POSPHorizonEntity posphorizonEntity;

    PaymentEliteEntity paymentEliteEntity;
    String transactionId="";
    String  payment_type ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_razor_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initialize();
        setListner();

        Checkout.preload(getApplicationContext());

        // Log.d(TAG, "transactionId :   " + "" + transactionId);

        synctransactionEntity = getIntent().getExtras().getParcelable("SYNC_TRANSACTION");

        posphorizonEntity = getIntent().getExtras().getParcelable("posphorizon_TRANSACTION");

        payment_type  = getIntent().getStringExtra("payment_type");

        if (payment_type !=null) {
            if (posphorizonEntity != null) {
                startPayment_posp();
            }
        }
        else
        {
            if (synctransactionEntity != null) {
                startPayment();
            }
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(SyncRazorPaymentActivity.this, HomeActivity.class));
                finish();
            }
        });

    }

    private void initialize() {


        cvBuy = (CardView) findViewById(R.id.cvBuy);
        cvSuccess = (CardView) findViewById(R.id.cvSuccess);
        cvFailure = (CardView) findViewById(R.id.cvFailure);

        btnBuy = (Button) findViewById(R.id.btnBuy);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnHomeContinue = (Button) findViewById(R.id.btnHomeContinue);
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtProdName = (TextView) findViewById(R.id.txtProdName);
        txtDisplayAmount = (TextView) findViewById(R.id.txtDisplayAmount);
        txtSuccessMessage = (TextView) findViewById(R.id.txtSuccessMessage);
        txtSuccessTitle = (TextView) findViewById(R.id.txtSuccessTitle);
        txtFailureMessage = (TextView) findViewById(R.id.txtFailureMessage);

        txtpaymentstatus = (TextView) findViewById(R.id.txtpaymentstatus);
        txtfailcustid = (TextView) findViewById(R.id.txtfailcustid);

    }

    private void setListner() {
        btnBuy.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnHomeContinue.setOnClickListener(this);
    }


    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", synctransactionEntity.getName());
            // options.put("description", synctransactionEntity.getPlan());
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", paymentEliteEntity.getImage());
            options.put("currency", "INR");

            String finalamount = String.valueOf(synctransactionEntity.getTotal_Premium() * 100);
            options.put("amount", finalamount);//paymentEliteEntity.getAmount());
            //options.put("amount", 100);
            JSONObject preFill = new JSONObject();
            preFill.put("email", synctransactionEntity.getEmail());
            preFill.put("contact", synctransactionEntity.getMobile());


            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    public void startPayment_posp() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();

            String Emp_name = "";
            Emp_name = posphorizonEntity.getFirst_Name()+" "+ posphorizonEntity.getMiddle_Name()+" "+ posphorizonEntity.getLast_Name();
            options.put("name", Emp_name);
            // options.put("description", synctransactionEntity.getPlan());
            //You can omit the image option to fetch the image from dashboard
            //options.put("image", paymentEliteEntity.getImage());
            options.put("currency", "INR");

            String finalamount = String.valueOf(posphorizonEntity.getRegAmount() * 100);
            options.put("amount", finalamount);//paymentEliteEntity.getAmount());
            //options.put("amount", 100);
            JSONObject preFill = new JSONObject();
            preFill.put("email", posphorizonEntity.getEmail_Id());
            preFill.put("contact", posphorizonEntity.getMobile_No());


            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    private void setPaymentDetail(PaymentEliteEntity paymentEliteEntity) {

        String custname = paymentEliteEntity.getFullname() + " - " + paymentEliteEntity.getCustID();
        txtCustomerName.setText(custname);
        txtProdName.setText(paymentEliteEntity.getPlanname() + " - " + paymentEliteEntity.getSubplan());
        txtDisplayAmount.setText("\u20B9" + " " + paymentEliteEntity.getDisplayamount());
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // Toast.makeText(this, "Payment Successfully Done : " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Pyment Success with RazorPaymentID  " + razorpayPaymentID);

        this.finish();

        if (payment_type !=null) {
            if (posphorizonEntity != null) {

                String suceessurl = "https://horizon.policyboss.com/razorpay-transaction-status/" + String.valueOf(posphorizonEntity.getSs_Id())+"/Success/"+razorpayPaymentID+"/POSP_ONBOARD";
                startActivity(new Intent(SyncRazorPaymentActivity.this, SyncWebViewActivity.class)
                        .putExtra("URL",suceessurl)
                        .putExtra("NAME", "Razor Payment")
                        .putExtra("TITLE", "Razor Payment"));
            }
        }else
        {
            if (synctransactionEntity != null) {
                String suceessurl = "https://horizon.policyboss.com/razorpay-transaction-status/" + String.valueOf(synctransactionEntity.getTransaction_Id())+"/Success/"+razorpayPaymentID;
                startActivity(new Intent(SyncRazorPaymentActivity.this, SyncWebViewActivity.class)
                        .putExtra("URL",suceessurl)
                        .putExtra("NAME", "Razor Payment")
                        .putExtra("TITLE", "Razor Payment"));
            }
        }


    }

    @Override
    public void onPaymentError(int code, String response) {

        try {
            Log.d(TAG, "Payment failed: " + code + " " + response);
            this.finish();

            if (payment_type !=null) {
                if (posphorizonEntity != null) {



                    String suceessurl = "https://horizon.policyboss.com/razorpay-transaction-status/" + String.valueOf(posphorizonEntity.getSs_Id())+"/Cancle/NA/POSP_ONBOARD";
                    startActivity(new Intent(SyncRazorPaymentActivity.this, SyncWebViewActivity.class)
                            .putExtra("URL",suceessurl)
                            .putExtra("NAME", "Razor Payment")
                            .putExtra("TITLE", "Razor Payment"));
                }


            }else {
                if (synctransactionEntity != null) {
                    String suceessurl = "https://horizon.policyboss.com/razorpay-transaction-status/" + String.valueOf(synctransactionEntity.getTransaction_Id())+"/Cancle";
                    startActivity(new Intent(SyncRazorPaymentActivity.this, SyncWebViewActivity.class)
                            .putExtra("URL",suceessurl)
                            .putExtra("NAME", "Razor Payment")
                            .putExtra("TITLE", "Razor Payment"));
                }
            }




        } catch (Exception e) {
            Log.d(TAG, "Exception in onPaymentError " + e.toString());
        }
    }

    @Override
    public void OnSuccess(APIResponse response, String message) {

        //  cancelDialog();

        // use by synctransactionEntity
        if (response instanceof synctransactionDetailReponse) {

            if (((synctransactionDetailReponse) response).getStatus().equals("success")) {
                synctransactionEntity = ((synctransactionDetailReponse) response).getMasterData();


                if (synctransactionEntity != null) {
                    startPayment();
                }


            }

        }

    }

    @Override
    public void OnFailure(Throwable t) {

        cancelDialog();

        Log.d(TAG, "Failure :   " + "" + t.getMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}