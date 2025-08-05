package com.policyboss.policybosspro.view.others.incomePotential;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.policyboss.policybosspro.BaseActivity;
import com.policyboss.policybosspro.BaseJavaActivity;
import com.policyboss.policybosspro.R;
import com.policyboss.policybosspro.utils.Constant;
import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.WebEngage;

public class IncomePotentialActivity extends BaseJavaActivity implements View.OnClickListener {


    // region Declare variable
    ImageView ivQr;
    TextView btnCalculate;
    EditText etLess50k, etUpto1lac, etUpto3Lac, etUpto10lac, etAbove10lac;
    double less50k, upto1lac, upto3lac, upto10lac, above10lac;
    TextView tvOptimistic, tvModerate, tvPermistic;

    AppBarLayout appBarLayout ;
    ScrollView scrollView ; // the content container
    View rootLayout ; // or your outermost layout


    double TwoWheeler, PrivateCar, HealthInsurance, TravelInsurance, Home, PersonalLoan, Business;
    double TwoWheelerCR, PrivateCarCR, HealthInsuranceCR, TravelInsuranceCR, HomeCR, PersonalLoanCR, BusinessCR;
    double HealthInsu = 0;
    double MotorInsu = 0;
    double TravelInsu = 0;
    double HomeLoan = 0;
    double BusinessLoan = 0;
    double Other = 0;

    double EstIncomeLess50 = 500000;
    double EstIncomeMore50 = 1000000;
    double EstIncomeMore1 = 3000000;
    double EstIncomeMore3 = 5000000;
    double EstIncomeAbove10 = 10000000;
    int scenario = 0;



    //endregion

    @Override
    protected void onStart() {
        super.onStart();
        Analytics weAnalytics = WebEngage.get().analytics();
        weAnalytics.screenNavigated("IncomePotential Screen");
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_income_potential);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("INCOME CALCULATOR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initWidgets();

       // Handling Top and bottom safe Area
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Status bar padding for toolbar/appbar
            appBarLayout.setPadding(
                    appBarLayout.getPaddingLeft(),
                    systemBars.top,
                    appBarLayout.getPaddingRight(),
                    appBarLayout.getPaddingBottom()
            );

            // Navigation bar padding for ScrollView (bottom)
            scrollView.setPadding(
                    scrollView.getPaddingLeft(),
                    scrollView.getPaddingTop(),
                    scrollView.getPaddingRight(),
                    systemBars.bottom
            );

            return insets;
        });
        TwoWheelerCR = .17;
        PrivateCarCR = .17;
        HealthInsuranceCR = .20;
        TravelInsuranceCR = .20;
        HomeCR = .0025;
        PersonalLoanCR = .0075;
        BusinessCR = .0075;
        tvOptimistic.performClick();

    }
    private void initWidgets() {

         appBarLayout = findViewById(R.id.appbar);
         scrollView = findViewById(R.id.scroll_view); // the content container
        rootLayout = findViewById(R.id.root_layout);

        ivQr = (ImageView) findViewById(R.id.ivQr);
        btnCalculate = (TextView) findViewById(R.id.btnCalculate);
        etLess50k = (EditText) findViewById(R.id.etLess50k);
        etUpto1lac = (EditText) findViewById(R.id.etUpto1lac);
        etUpto3Lac = (EditText) findViewById(R.id.etUpto3Lac);
        etUpto10lac = (EditText) findViewById(R.id.etUpto10lac);
        etAbove10lac = (EditText) findViewById(R.id.etAbove10lac);

        tvOptimistic = (TextView) findViewById(R.id.tvOptimistic);
        tvModerate = (TextView) findViewById(R.id.tvModerate);
        tvPermistic = (TextView) findViewById(R.id.tvPermistic);
        btnCalculate.setOnClickListener(this);
        tvOptimistic.setOnClickListener(this);
        tvModerate.setOnClickListener(this);
        tvPermistic.setOnClickListener(this);
        ivQr.setOnClickListener(this);
    }

    private void setBacground(View view) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.grey_border));
        } else {
            view.setBackground(getResources().getDrawable(R.drawable.grey_border));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivQr:
                //  startActivityForResult(new Intent(this, SimpleScannerActivity.class), 111);
                break;
            case R.id.tvPermistic:
                tvPermistic.setBackgroundColor(getResources().getColor(R.color.selcted_blue));
                tvModerate.setTextColor(getResources().getColor(R.color.black));
                scenario = 1;
                tvPermistic.setTextColor(getResources().getColor(R.color.white));
                tvOptimistic.setTextColor(getResources().getColor(R.color.black));
                setBacground(tvModerate);
                setBacground(tvOptimistic);


                TwoWheeler = 0.15;
                PrivateCar = 0.15;
                HealthInsurance = 0.15;
                TravelInsurance = 0.15;
                Home = 0.15;
                PersonalLoan = 0.15;
                Business = 0.15;
                break;
            case R.id.tvModerate:
                scenario = 2;
                tvModerate.setBackgroundColor(getResources().getColor(R.color.selcted_blue));
                tvModerate.setTextColor(getResources().getColor(R.color.white));
                tvPermistic.setTextColor(getResources().getColor(R.color.black));
                tvOptimistic.setTextColor(getResources().getColor(R.color.black));
                setBacground(tvPermistic);
                setBacground(tvOptimistic);

                TwoWheeler = .20;
                PrivateCar = .20;
                HealthInsurance = .20;
                TravelInsurance = .20;
                Home = .20;
                PersonalLoan = .20;
                Business = .20;
                break;
            case R.id.tvOptimistic:
                scenario = 3;
                tvOptimistic.setBackgroundColor(getResources().getColor(R.color.selcted_blue));
                tvModerate.setTextColor(getResources().getColor(R.color.black));
                tvPermistic.setTextColor(getResources().getColor(R.color.black));
                tvOptimistic.setTextColor(getResources().getColor(R.color.white));
                setBacground(tvPermistic);
                setBacground(tvModerate);

                TwoWheeler = .30;
                PrivateCar = .30;
                HealthInsurance = .30;
                TravelInsurance = .30;
                Home = .30;
                PersonalLoan = .30;
                Business = .30;
                break;
            case R.id.btnCalculate:

                if (!etLess50k.getText().toString().equals("")) {
                    less50k = Double.parseDouble(etLess50k.getText().toString());

                    HealthInsu = Math.round(less50k * HealthInsurance * 10000 * HealthInsuranceCR);
                    MotorInsu = Math.round(less50k * TwoWheeler * 1000 * TwoWheelerCR);


                    double ITP = 0;
                    double NoOfTraveller = less50k * ITP;
                    double PotentialPrem = Math.round(NoOfTraveller * 2000 * TravelInsurance);
                    TravelInsu = Math.round(PotentialPrem * TravelInsuranceCR);//((((less50k) * (TravelInsurance)) * 2000) * TravelInsuranceCR);

                    double HomeLoanEligi = EstIncomeLess50 * 5;
                    double HomeLoanSeekers = (.05);
                    double PotenHomeLoanThruMe = Math.round(less50k * HomeLoanSeekers * Home);
                    double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                    HomeLoan = Math.round(PoteHomeLoanAmt * HomeCR);//HomeLoan = ((((less50k) * (Home)) * 10000) * HomeCR);
                    //console.log(HomeLoanEligi, PotenHomeLoanThruMe, PoteHomeLoanAmt, Math.round(PoteHomeLoanAmt * HomeCR));

                    double BusiLoanNeed = EstIncomeLess50 * 0;
                    double BusiLoanSeekers = 0;
                    double PotenBusiLoanThruMe = Math.round(less50k * BusiLoanSeekers * Business);
                    double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                    BusinessLoan = Math.round(PoteBusiLoanAmt * BusinessCR);//BusinessLoan = ((((less50k) * (Business)) * 10000) * BusinessCR);

                    double PersLoanEligi = (EstIncomeLess50 * 0.4);
                    double PersLoanSeekers = (.05);
                    double PotenPersLoanThruMe = Math.round(less50k * PersLoanSeekers * PersonalLoan);
                    double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                    Other = Math.round(PotePersLoanAmt * PersonalLoanCR); //Other = ((((less50k) * (PersonalLoan)) * 10000) * PersonalLoanCR);
                    //console.log(PersLoanEligi, PotenPersLoanThruMe, PotePersLoanAmt, Math.round(PotePersLoanAmt * PersonalLoanCR));
                }


                if (!etUpto1lac.getText().toString().equals("")) {
                    upto1lac = Double.parseDouble(etUpto1lac.getText().toString());
                    HealthInsu = HealthInsu + Math.round((((upto1lac) * (HealthInsurance)) * 20000) * HealthInsuranceCR);
                    MotorInsu = MotorInsu + Math.round((((upto1lac) * (PrivateCar)) * 5000) * PrivateCarCR);

                    double ITP = (0.10);
                    double NoOfTraveller = (upto1lac) * ITP;
                    double PotentialPrem = Math.round((NoOfTraveller * (2000)) * (TravelInsurance));
                    TravelInsu = TravelInsu + Math.round(PotentialPrem * TravelInsuranceCR);//TravelInsu + ((((upto1lac) * (TravelInsurance)) * 2000) * TravelInsuranceCR);

                    double HomeLoanEligi = EstIncomeMore50 * 5;
                    double HomeLoanSeekers = (.05);
                    double PotenHomeLoanThruMe = Math.round((upto1lac) * HomeLoanSeekers * (Home));
                    double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                    HomeLoan = HomeLoan + Math.round(PoteHomeLoanAmt * HomeCR);//HomeLoan = HomeLoan + ((((upto1lac) * (Home)) * 20000) * HomeCR);
                    //console.log(HomeLoanEligi, PotenHomeLoanThruMe, PoteHomeLoanAmt, Math.round(PoteHomeLoanAmt * HomeCR));

                    double BusiLoanNeed = EstIncomeMore50 * 0;
                    double BusiLoanSeekers = 0;
                    double PotenBusiLoanThruMe = Math.round((upto1lac) * BusiLoanSeekers * (Business));
                    double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                    BusinessLoan = BusinessLoan + Math.round(PoteBusiLoanAmt * BusinessCR);//BusinessLoan = BusinessLoan + ((((upto1lac) * (Business)) * 20000) * BusinessCR);

                    double PersLoanEligi = (EstIncomeMore50 * 0.4);
                    double PersLoanSeekers = (.05);
                    double PotenPersLoanThruMe = Math.round((upto1lac) * PersLoanSeekers * (PersonalLoan));
                    double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                    Other = Other + Math.round(PotePersLoanAmt * PersonalLoanCR); //Other = Other + ((((upto1lac) * (PersonalLoan)) * 20000) * PersonalLoanCR);
                    //console.log(PersLoanEligi, PotenPersLoanThruMe, PotePersLoanAmt, Math.round(PotePersLoanAmt * PersonalLoanCR));

                    //console.log(HealthInsu, MotorInsu, TravelInsu, HomeLoan, BusinessLoan, Other);
                }

                if (!etUpto3Lac.getText().toString().equals("")) {
                    upto3lac = Double.parseDouble(etUpto3Lac.getText().toString());
                    HealthInsu = HealthInsu + Math.round((((upto3lac) * (HealthInsurance)) * 50000) * HealthInsuranceCR);
                    MotorInsu = MotorInsu + Math.round((((upto3lac) * (PrivateCar)) * 12000) * PrivateCarCR);

                    double ITP = (.20);
                    double NoOfTraveller = (upto3lac) * ITP;
                    double PotentialPrem = Math.round((NoOfTraveller * (2000)) * (TravelInsurance));
                    TravelInsu = TravelInsu + Math.round(PotentialPrem * TravelInsuranceCR);//TravelInsu + ((((upto3lac) * (TravelInsurance)) * 2000) * TravelInsuranceCR);

                    double HomeLoanEligi = EstIncomeMore1 * 5;
                    double HomeLoanSeekers = (.05);
                    double PotenHomeLoanThruMe = Math.round((upto3lac) * HomeLoanSeekers * (Home));
                    double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                    HomeLoan = HomeLoan + Math.round(PoteHomeLoanAmt * HomeCR); //HomeLoan = HomeLoan + ((((upto3lac) * (Home)) * 30000) * HomeCR);
                    //console.log(HomeLoanEligi, PotenHomeLoanThruMe, PoteHomeLoanAmt, Math.round(PoteHomeLoanAmt * HomeCR));

                    double BusiLoanNeed = EstIncomeMore1 * 2;
                    double BusiLoanSeekers = (.05);
                    double PotenBusiLoanThruMe = Math.round((upto3lac) * BusiLoanSeekers * (Business));
                    double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                    BusinessLoan = BusinessLoan + Math.round(PoteBusiLoanAmt * BusinessCR); //BusinessLoan = BusinessLoan + ((((upto3lac) * (Business)) * 30000) * BusinessCR);

                    double PersLoanEligi = (EstIncomeMore1 * 0.4);
                    double PersLoanSeekers = (.05);
                    double PotenPersLoanThruMe = Math.round((upto3lac) * PersLoanSeekers * (PersonalLoan));
                    double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                    Other = Other + Math.round(PotePersLoanAmt * PersonalLoanCR); //Other = Other + ((((txtMore1) * (PersonalLoan)) * 30000) * PersonalLoanCR);
                    //console.log(PersLoanEligi, PotenPersLoanThruMe, PotePersLoanAmt, Math.round(PotePersLoanAmt * PersonalLoanCR));
                }

                if (!etUpto10lac.getText().toString().equals("")) {
                    upto10lac = Double.parseDouble(etUpto10lac.getText().toString());
                    HealthInsu = HealthInsu + Math.round((((upto10lac) * (HealthInsurance)) * 100000) * HealthInsuranceCR);
                    MotorInsu = MotorInsu + Math.round((((upto10lac) * (PrivateCar)) * 25000) * PrivateCarCR);

                    double ITP = (.40);
                    double NoOfTraveller = (upto10lac) * ITP;
                    double PotentialPrem = Math.round((NoOfTraveller * (3000)) * (TravelInsurance));
                    TravelInsu = TravelInsu + Math.round(PotentialPrem * TravelInsuranceCR);//TravelInsu + ((((upto10lac) * (TravelInsurance)) * 3000) * TravelInsuranceCR);

                    double HomeLoanEligi = EstIncomeMore3 * 5;
                    double HomeLoanSeekers = (.05);
                    double PotenHomeLoanThruMe = Math.round((upto10lac) * HomeLoanSeekers * (Home));
                    double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                    HomeLoan = HomeLoan + Math.round(PoteHomeLoanAmt * HomeCR);//HomeLoan = HomeLoan + ((((upto10lac) * (Home)) * 40000) * HomeCR);
                    //console.log(HomeLoanEligi, PotenHomeLoanThruMe, PoteHomeLoanAmt, Math.round(PoteHomeLoanAmt * HomeCR));

                    double BusiLoanNeed = EstIncomeMore3 * 2;
                    double BusiLoanSeekers = (.05);
                    double PotenBusiLoanThruMe = Math.round((upto10lac) * BusiLoanSeekers * (Business));
                    double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                    BusinessLoan = BusinessLoan + Math.round(PoteBusiLoanAmt * BusinessCR); //BusinessLoan = BusinessLoan + ((((upto10lac) * (Business)) * 40000) * BusinessCR);

                    double PersLoanEligi = (EstIncomeMore3 * 0.4);
                    double PersLoanSeekers = (.05);
                    double PotenPersLoanThruMe = Math.round((upto10lac) * PersLoanSeekers * (PersonalLoan));
                    double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                    Other = Other + Math.round(PotePersLoanAmt * PersonalLoanCR); //Other = Other + ((((upto10lac) * (PersonalLoan)) * 40000) * PersonalLoanCR);
                    //console.log(PersLoanEligi, PotenPersLoanThruMe, PotePersLoanAmt, Math.round(PotePersLoanAmt * PersonalLoanCR));
                }

                if (!etAbove10lac.getText().toString().equals("")) {
                    above10lac = Double.parseDouble(etAbove10lac.getText().toString());
                    HealthInsu = HealthInsu + Math.round((((above10lac) * (HealthInsurance)) * 200000) * HealthInsuranceCR);
                    MotorInsu = MotorInsu + Math.round((((above10lac) * (PrivateCar)) * 50000) * PrivateCarCR);

                    double ITP = (.60);
                    double NoOfTraveller = (above10lac) * ITP;
                    double PotentialPrem = Math.round((NoOfTraveller * (3000)) * (TravelInsurance));
                    TravelInsu = TravelInsu + Math.round(PotentialPrem * TravelInsuranceCR);//TravelInsu + ((((above10lac) * (TravelInsurance)) * 3000) * TravelInsuranceCR);

                    double HomeLoanEligi = EstIncomeAbove10 * 5;
                    double HomeLoanSeekers = (.05);
                    double PotenHomeLoanThruMe = Math.round((above10lac) * HomeLoanSeekers * (Home));
                    double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                    HomeLoan = HomeLoan + Math.round(PoteHomeLoanAmt * HomeCR); //HomeLoan = HomeLoan + ((((above10lac) * (Home)) * 50000) * HomeCR);
                    //console.log(HomeLoanEligi, PotenHomeLoanThruMe, PoteHomeLoanAmt, Math.round(PoteHomeLoanAmt * HomeCR));

                    double BusiLoanNeed = EstIncomeAbove10 * 2;
                    double BusiLoanSeekers = (.05);
                    double PotenBusiLoanThruMe = Math.round((above10lac) * BusiLoanSeekers * (Business));
                    double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                    BusinessLoan = BusinessLoan + Math.round(PoteBusiLoanAmt * BusinessCR); //BusinessLoan = BusinessLoan + ((((above10lac) * (Business)) * 50000) * BusinessCR);

                    double PersLoanEligi = (EstIncomeAbove10 * 0.4);
                    double PersLoanSeekers = (.05);
                    double PotenPersLoanThruMe = Math.round((above10lac) * PersLoanSeekers * (PersonalLoan));
                    double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                    Other = Other + Math.round(PotePersLoanAmt * PersonalLoanCR);//Other = Other + ((((above10lac) * (PersonalLoan)) * 50000) * PersonalLoanCR);
                    //console.log(PersLoanEligi, PotenPersLoanThruMe, PotePersLoanAmt, Math.round(PotePersLoanAmt * PersonalLoanCR));
                }
                double total = HealthInsu + MotorInsu + TravelInsu + HomeLoan + BusinessLoan + Other;
                Log.d("Total", "" + total);
                Log.d("All", "HealthInsu-" + HealthInsu + " MotorInsu-" + MotorInsu + " TravelInsu - " + TravelInsu
                        + " HomeLoan - " + HomeLoan + " BusinessLoan - " + BusinessLoan + " Other - " + Other);


                double PessimisticRate = 11.59374246;
                double ModerateRate = 13.04555774;
                double OptimisticRate = 15.19173642;

                double spnHealthInsu = HealthInsu;
                double spnMotorInsu = MotorInsu;
                double spnTravelInsu = TravelInsu;
                double spnHomeLoan = HomeLoan;
                double spnBusinessLoan = BusinessLoan;
                double spnOther = Other;

                double ProHealthInsu = 0;
                double ProMotorInsu = 0;
                double ProTravelInsu = 0;
                double ProHomeLoan = 0;
                double ProBusinessLoan = 0;
                double ProOther = 0;

                if (TwoWheeler == 0.15) {
                    ProHealthInsu = Math.round((spnHealthInsu) * PessimisticRate);
                    ProMotorInsu = Math.round((spnMotorInsu) * PessimisticRate);
                    ProTravelInsu = Math.round((spnTravelInsu) * PessimisticRate);
                    ProHomeLoan = Math.round((spnHomeLoan) * PessimisticRate);
                    ProBusinessLoan = Math.round((spnBusinessLoan) * PessimisticRate);
                    ProOther = Math.round((spnOther) * PessimisticRate);
                } else if (TwoWheeler == 0.20) {
                    ProHealthInsu = Math.round((spnHealthInsu) * ModerateRate);
                    ProMotorInsu = Math.round((spnMotorInsu) * ModerateRate);
                    ProTravelInsu = Math.round((spnTravelInsu) * ModerateRate);
                    ProHomeLoan = Math.round((spnHomeLoan) * ModerateRate);
                    ProBusinessLoan = Math.round((spnBusinessLoan) * ModerateRate);
                    ProOther = Math.round((spnOther) * ModerateRate);
                } else if (TwoWheeler == 0.30) {
                    ProHealthInsu = Math.round((spnHealthInsu) * OptimisticRate);
                    ProMotorInsu = Math.round((spnMotorInsu) * OptimisticRate);
                    ProTravelInsu = Math.round((spnTravelInsu) * OptimisticRate);
                    ProHomeLoan = Math.round((spnHomeLoan) * OptimisticRate);
                    ProBusinessLoan = Math.round((spnBusinessLoan) * OptimisticRate);
                    ProOther = Math.round((spnOther) * OptimisticRate);
                }

                double ProTotal = ProHealthInsu + ProMotorInsu + ProTravelInsu + ProHomeLoan + ProBusinessLoan + ProOther;

                Log.d("ProTotal", "" + ProTotal);
                Log.d("ProAll", "HealthInsu-" + ProHealthInsu + " MotorInsu-" + ProMotorInsu + " TravelInsu - " + ProTravelInsu
                        + " HomeLoan - " + ProHomeLoan + " BusinessLoan - " + ProBusinessLoan + " Other - " + ProOther);

                startActivity(new Intent(this, IncomePotentialDialogActivity.class)
                        .putExtra("ProTotal", ProTotal)
                        .putExtra("ProHealthInsu", ProHealthInsu)
                        .putExtra("ProMotorInsu", ProMotorInsu)
                        .putExtra("ProTravelInsu", ProTravelInsu)
                        .putExtra("ProHomeLoan", ProHomeLoan)
                        .putExtra("ProBusinessLoan", ProBusinessLoan)
                        .putExtra("ProOther", ProOther)

                        .putExtra("total", total)
                        .putExtra("HealthInsu", HealthInsu)
                        .putExtra("MotorInsu", MotorInsu)
                        .putExtra("TravelInsu", TravelInsu)
                        .putExtra("HomeLoan", HomeLoan)
                        .putExtra("BusinessLoan", BusinessLoan)
                        .putExtra("Other", Other)

                        .putExtra("scenario", scenario)

                        .putExtra("less50k", less50k)
                        .putExtra("upto1lac", upto1lac)
                        .putExtra("upto3lac", upto3lac)
                        .putExtra("upto10lac", upto10lac)
                        .putExtra("above10lac", above10lac));

                if(scenario==1) {
                    trackcalcEvent("Pessimistic");
                }else if(scenario==2)
                {
                    trackcalcEvent("Moderate");
                }
                else {
                    trackcalcEvent("Optimistic");
                }


                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                String rawText = (String) data.getExtras().getString("RAWRESULT");
                String type = (String) data.getExtras().getString("TYPE");
                Log.d("QRSCANNED", rawText);
                try {


                    String[] textArray = rawText.split("\\|");
                    etLess50k.setText(textArray[0]);
                    etUpto1lac.setText(textArray[1]);
                    etUpto3Lac.setText(textArray[2]);
                    etUpto10lac.setText(textArray[3]);
                    etAbove10lac.setText(textArray[4]);
                    btnCalculate.performClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finish the activity when the Up button is pressed
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void trackcalcEvent(String status) {
        // Create event attributes
        //  Map<String, Object> eventAttributes = new HashMap<>();
        //  eventAttributes.put("Status",status); // Add any relevant attributes

        // Track the login event using WebEngageHelper
        //   WebEngageAnalytics.getInstance().trackEvent("Scenario", eventAttributes);
    }
}