package com.policyboss.policybosspro.view.others.incomePotential;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.policyboss.policybosspro.BaseActivity;
import com.policyboss.policybosspro.BaseJavaActivity;
import com.policyboss.policybosspro.R;

import java.text.DecimalFormat;

public class IncomePotentialdetailActivity extends BaseJavaActivity implements View.OnClickListener {

    TextView txtHeader, txtless50k, txt50k, txt1lac, txt3lac, txt10lac;
    TextView tvNoFamilyLess50k, tvEstFamilyIncomLess50k, tvAvgPremiumLess50k, tvPotConverionLess50k, tvPotPremiumLess50k, tvPotCommissionLess50k,
            tvNoFamily50k, tvEstFamilyIncom50k, tvAvgPremium50k, tvPotConverion50k, tvPotPremium50k, tvPotCommission50k,
            tvNoFamily1lac, tvEstFamilyIncom1lac, tvAvgPremium1lac, tvPotConverion1lac, tvPotPremium1lac, tvPotCommission1lac,
            tvNoFamily3lac, tvEstFamilyIncom3lac, tvAvgPremium3lac, tvPotConverion3lac, tvPotPremium3lac, tvPotCommission3lac,
            tvNoFamily10lac, tvEstFamilyIncom10lac, tvAvgPremium10lac, tvPotConverion10lac, tvPotPremium10lac, tvPotCommission10lac, txtSum;

    TextView lbtvPotPremiumLess50k, lbtvPotPremium50k, lbtvPotPremium1lac, lbtvPotPremium3lac, lbtvPotPremium10lac;

    TextView spnLoanEligiLess50, spnLoanEligAmtLess50;
    LinearLayout lvspnLoanEligi50k, lvAvgPremiumLess50k, lvInternationalLess50k;

    LinearLayout lvspnLoanEligiMore50, lvAvgPremiumMore50, lvInternationalmore50k;
    TextView spnLoanEligiMore50, spnLoanEligAmtMore50;

    LinearLayout lvspnLoanEligiMore1, lvAvgPremiumMore1, lvInternationalMore1;
    TextView spnLoanEligiMore1, spnLoanEligAmtMore1;

    LinearLayout lvspnLoanEligiMore3, lvAvgPremiumtMore3, lvInternationalMore3;
    TextView spnLoanEligiMore3, spnLoanEligAmtMore3;

    LinearLayout lvspnLoanEligiAbove10, lvAvgPremiumtAbove10, lvInternationalAbove10;
    TextView spnLoanEligiAbove10, spnLoanEligAmtAbove10;

    RelativeLayout rlLess50k, rl50k, rl1lac, rl3lac, rl10lac;
    LinearLayout llLess50k, ll50k, ll1lac, ll3lac, ll10lac;
    ImageView ivLess50, iv50k, iv1lac, iv3lac, iv10lac;

    double less50k, upto1lac, upto3lac, upto10lac, above10lac;
    String type, typeValue;
    int scenario;


    double TwoWheeler, PrivateCar, HealthInsurance, TravelInsurance, Home, PersonalLoan, Business;
    double TwoWheelerCR, PrivateCarCR, HealthInsuranceCR, TravelInsuranceCR, HomeCR, PersonalLoanCR, BusinessCR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_income_potentialdetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initialize();
        resetAll();
        getAllValues();
    }

    private void getAllValues() {
        type = getIntent().getStringExtra("type");
        typeValue = getIntent().getStringExtra("typeValue");
        less50k = getIntent().getDoubleExtra("less50k", 0.0);
        upto1lac = getIntent().getDoubleExtra("upto1lac", 0.0);
        upto3lac = getIntent().getDoubleExtra("upto3lac", 0.0);
        upto10lac = getIntent().getDoubleExtra("upto10lac", 0.0);
        above10lac = getIntent().getDoubleExtra("above10lac", 0.0);
        scenario = getIntent().getIntExtra("scenario", 0);

        txtHeader.setText(typeValue);

        showhide(type);
        CalculateIncomePotential();
    }

    private void showhide(String type) {

        //pt conversion
        if (type.toString().matches("Home") || type.toString().matches("Business") || type.toString().matches("Other")) {
            //less 50
            lvspnLoanEligi50k.setVisibility(View.VISIBLE);
            lvAvgPremiumLess50k.setVisibility(View.GONE);
            lvInternationalLess50k.setVisibility(View.GONE);
            //more 50

            lvspnLoanEligiMore50.setVisibility(View.VISIBLE);
            lvAvgPremiumMore50.setVisibility(View.GONE);
            lvInternationalmore50k.setVisibility(View.GONE);

            //more 1

            lvspnLoanEligiMore1.setVisibility(View.VISIBLE);
            lvAvgPremiumMore1.setVisibility(View.GONE);
            lvInternationalMore1.setVisibility(View.GONE);

            //more 3

            lvspnLoanEligiMore3.setVisibility(View.VISIBLE);
            lvAvgPremiumtMore3.setVisibility(View.GONE);
            lvInternationalMore3.setVisibility(View.GONE);

            //above 10

            lvspnLoanEligiAbove10.setVisibility(View.VISIBLE);
            lvAvgPremiumtAbove10.setVisibility(View.GONE);
            lvInternationalAbove10.setVisibility(View.GONE);

        } else if (type.toString().matches("Travel")) {
            //less 50
            lvspnLoanEligi50k.setVisibility(View.GONE);
            lvAvgPremiumLess50k.setVisibility(View.VISIBLE);
            lvInternationalLess50k.setVisibility(View.VISIBLE);
            tvAvgPremiumLess50k.setText("2000");

            //more 50
            lvspnLoanEligiMore50.setVisibility(View.GONE);
            lvAvgPremiumMore50.setVisibility(View.VISIBLE);
            lvInternationalmore50k.setVisibility(View.VISIBLE);
            tvAvgPremium50k.setText("2000");

            //more 1
            lvspnLoanEligiMore1.setVisibility(View.GONE);
            lvAvgPremiumMore1.setVisibility(View.VISIBLE);
            lvInternationalMore1.setVisibility(View.VISIBLE);
            tvAvgPremium1lac.setText("2000");

            //more 3
            lvspnLoanEligiMore3.setVisibility(View.GONE);
            lvAvgPremiumtMore3.setVisibility(View.VISIBLE);
            lvInternationalMore3.setVisibility(View.VISIBLE);
            tvAvgPremium3lac.setText("3000");

            //more 10
            lvspnLoanEligiAbove10.setVisibility(View.GONE);
            lvAvgPremiumtAbove10.setVisibility(View.VISIBLE);
            lvInternationalAbove10.setVisibility(View.VISIBLE);
            tvAvgPremium10lac.setText("3000");

        } else {
            //less 50
            lvspnLoanEligi50k.setVisibility(View.GONE);
            lvAvgPremiumLess50k.setVisibility(View.VISIBLE);
            lvInternationalLess50k.setVisibility(View.GONE);


            //more 50
            lvspnLoanEligiMore50.setVisibility(View.GONE);
            lvAvgPremiumMore50.setVisibility(View.VISIBLE);
            lvInternationalmore50k.setVisibility(View.GONE);


            //more 1
            lvspnLoanEligiMore1.setVisibility(View.GONE);
            lvAvgPremiumMore1.setVisibility(View.VISIBLE);
            lvInternationalMore1.setVisibility(View.GONE);


            //more 3
            lvspnLoanEligiMore3.setVisibility(View.GONE);
            lvAvgPremiumtMore3.setVisibility(View.VISIBLE);
            lvInternationalMore3.setVisibility(View.GONE);


            //more 10
            lvspnLoanEligiAbove10.setVisibility(View.GONE);
            lvAvgPremiumtAbove10.setVisibility(View.VISIBLE);
            lvInternationalAbove10.setVisibility(View.GONE);

            if (type.toString().matches("Health")) {
                tvAvgPremiumLess50k.setText("10000");
                tvAvgPremium50k.setText("20000");
                tvAvgPremium1lac.setText("50000");
                tvAvgPremium3lac.setText("100000");
                tvAvgPremium10lac.setText("200000");
            } else if (type.toString().matches("Motor")) {
                tvAvgPremiumLess50k.setText("1000");
                tvAvgPremium50k.setText("5000");
                tvAvgPremium1lac.setText("12000");
                tvAvgPremium3lac.setText("25000");
                tvAvgPremium10lac.setText("50000");
            }


        }
    }


    private void initialize() {
        txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtless50k = (TextView) findViewById(R.id.txtless50k);
        txt50k = (TextView) findViewById(R.id.txt50k);
        txt1lac = (TextView) findViewById(R.id.txt1lac);
        txt3lac = (TextView) findViewById(R.id.txt3lac);
        txt10lac = (TextView) findViewById(R.id.txt10lac);

        tvNoFamilyLess50k = (TextView) findViewById(R.id.tvNoFamilyLess50k);
        tvEstFamilyIncomLess50k = (TextView) findViewById(R.id.tvEstFamilyIncomLess50k);
        tvAvgPremiumLess50k = (TextView) findViewById(R.id.tvAvgPremiumLess50k);
        tvPotConverionLess50k = (TextView) findViewById(R.id.tvPotConverionLess50k);
        tvPotPremiumLess50k = (TextView) findViewById(R.id.tvPotPremiumLess50k);
        tvPotCommissionLess50k = (TextView) findViewById(R.id.tvPotCommissionLess50k);

        tvNoFamily50k = (TextView) findViewById(R.id.tvNoFamily50k);
        tvEstFamilyIncom50k = (TextView) findViewById(R.id.tvEstFamilyIncom50k);
        tvAvgPremium50k = (TextView) findViewById(R.id.tvAvgPremium50k);
        tvPotConverion50k = (TextView) findViewById(R.id.tvPotConverion50k);
        tvPotPremium50k = (TextView) findViewById(R.id.tvPotPremium50k);
        tvPotCommission50k = (TextView) findViewById(R.id.tvPotCommission50k);

        tvNoFamily1lac = (TextView) findViewById(R.id.tvNoFamily1lac);
        tvEstFamilyIncom1lac = (TextView) findViewById(R.id.tvEstFamilyIncom1lac);
        tvAvgPremium1lac = (TextView) findViewById(R.id.tvAvgPremium1lac);
        tvPotConverion1lac = (TextView) findViewById(R.id.tvPotConverion1lac);
        tvPotPremium1lac = (TextView) findViewById(R.id.tvPotPremium1lac);
        tvPotCommission1lac = (TextView) findViewById(R.id.tvPotCommission1lac);

        tvNoFamily3lac = (TextView) findViewById(R.id.tvNoFamily3lac);
        tvEstFamilyIncom3lac = (TextView) findViewById(R.id.tvEstFamilyIncom3lac);
        tvAvgPremium3lac = (TextView) findViewById(R.id.tvAvgPremium3lac);
        tvPotConverion3lac = (TextView) findViewById(R.id.tvPotConverion3lac);
        tvPotPremium3lac = (TextView) findViewById(R.id.tvPotPremium3lac);
        tvPotCommission3lac = (TextView) findViewById(R.id.tvPotCommission3lac);


        tvNoFamily10lac = (TextView) findViewById(R.id.tvNoFamily10lac);
        tvEstFamilyIncom10lac = (TextView) findViewById(R.id.tvEstFamilyIncom10lac);
        tvAvgPremium10lac = (TextView) findViewById(R.id.tvAvgPremium10lac);
        tvPotConverion10lac = (TextView) findViewById(R.id.tvPotConverion10lac);
        tvPotPremium10lac = (TextView) findViewById(R.id.tvPotPremium10lac);
        tvPotCommission10lac = (TextView) findViewById(R.id.tvPotCommission10lac);

        txtSum = (TextView) findViewById(R.id.txtSum);

        ivLess50 = (ImageView) findViewById(R.id.ivLess50);
        iv50k = (ImageView) findViewById(R.id.iv50k);
        iv1lac = (ImageView) findViewById(R.id.iv1lac);
        iv3lac = (ImageView) findViewById(R.id.iv3lac);
        iv10lac = (ImageView) findViewById(R.id.iv10lac);

        rlLess50k = (RelativeLayout) findViewById(R.id.rlLess50k);
        rl50k = (RelativeLayout) findViewById(R.id.rl50k);
        rl1lac = (RelativeLayout) findViewById(R.id.rl1lac);
        rl3lac = (RelativeLayout) findViewById(R.id.rl3lac);
        rl10lac = (RelativeLayout) findViewById(R.id.rl10lac);

        llLess50k = (LinearLayout) findViewById(R.id.llLess50k);
        ll50k = (LinearLayout) findViewById(R.id.ll50k);
        ll1lac = (LinearLayout) findViewById(R.id.ll1lac);
        ll3lac = (LinearLayout) findViewById(R.id.ll3lac);
        ll10lac = (LinearLayout) findViewById(R.id.ll10lac);


        lbtvPotPremiumLess50k = (TextView) findViewById(R.id.lbtvPotPremiumLess50k);
        lbtvPotPremium50k = (TextView) findViewById(R.id.lbtvPotPremium50k);
        lbtvPotPremium1lac = (TextView) findViewById(R.id.lbtvPotPremium1lac);
        lbtvPotPremium3lac = (TextView) findViewById(R.id.lbtvPotPremium3lac);
        lbtvPotPremium10lac = (TextView) findViewById(R.id.lbtvPotPremium10lac);

        spnLoanEligiLess50 = (TextView) findViewById(R.id.spnLoanEligiLess50);
        spnLoanEligAmtLess50 = (TextView) findViewById(R.id.spnLoanEligAmtLess50);

        spnLoanEligiMore50 = (TextView) findViewById(R.id.spnLoanEligiMore50);
        spnLoanEligAmtMore50 = (TextView) findViewById(R.id.spnLoanEligAmtMore50);

        spnLoanEligiMore1 = (TextView) findViewById(R.id.spnLoanEligiMore1);
        spnLoanEligAmtMore1 = (TextView) findViewById(R.id.spnLoanEligAmtMore1);

        spnLoanEligiMore3 = (TextView) findViewById(R.id.spnLoanEligiMore3);
        spnLoanEligAmtMore3 = (TextView) findViewById(R.id.spnLoanEligAmtMore3);

        spnLoanEligiAbove10 = (TextView) findViewById(R.id.spnLoanEligiAbove10);
        spnLoanEligAmtAbove10 = (TextView) findViewById(R.id.spnLoanEligAmtAbove10);

        lvspnLoanEligi50k = (LinearLayout) findViewById(R.id.lvspnLoanEligi50k);
        lvAvgPremiumLess50k = (LinearLayout) findViewById(R.id.lvAvgPremiumLess50k);
        lvInternationalLess50k = (LinearLayout) findViewById(R.id.lvInternationalLess50k);

        lvspnLoanEligiMore50 = (LinearLayout) findViewById(R.id.lvspnLoanEligiMore50);
        lvAvgPremiumMore50 = (LinearLayout) findViewById(R.id.lvAvgPremiumMore50);
        lvInternationalmore50k = (LinearLayout) findViewById(R.id.lvInternationalmore50k);

        lvspnLoanEligiMore1 = (LinearLayout) findViewById(R.id.lvspnLoanEligiMore1);
        lvAvgPremiumMore1 = (LinearLayout) findViewById(R.id.lvAvgPremiumMore1);
        lvInternationalMore1 = (LinearLayout) findViewById(R.id.lvInternationalMore1);


        lvspnLoanEligiMore3 = (LinearLayout) findViewById(R.id.lvspnLoanEligiMore3);
        lvAvgPremiumtMore3 = (LinearLayout) findViewById(R.id.lvAvgPremiumtMore3);
        lvInternationalMore3 = (LinearLayout) findViewById(R.id.lvInternationalMore3);

        lvspnLoanEligiAbove10 = (LinearLayout) findViewById(R.id.lvspnLoanEligiAbove10);
        lvAvgPremiumtAbove10 = (LinearLayout) findViewById(R.id.lvAvgPremiumtAbove10);
        lvInternationalAbove10 = (LinearLayout) findViewById(R.id.lvInternationalAbove10);


        rlLess50k.setOnClickListener(this);
        rl50k.setOnClickListener(this);
        rl1lac.setOnClickListener(this);
        rl3lac.setOnClickListener(this);
        rl10lac.setOnClickListener(this);

    }

    private void resetAll() {
        llLess50k.setVisibility(View.GONE);
        ll50k.setVisibility(View.GONE);
        ll1lac.setVisibility(View.GONE);
        ll3lac.setVisibility(View.GONE);
        ll10lac.setVisibility(View.GONE);
    }

    private void setLayout(int i) {
        switch (i) {
            case 1:  //  less than 50 k
            {
                if (llLess50k.getVisibility() == View.GONE) {
                    llLess50k.setVisibility(View.VISIBLE);
                    ivLess50.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));

                    iv50k.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv1lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv3lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv10lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                } else {
                    llLess50k.setVisibility(View.GONE);
                    ivLess50.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                }
                ll50k.setVisibility(View.GONE);
                ll1lac.setVisibility(View.GONE);
                ll3lac.setVisibility(View.GONE);
                ll10lac.setVisibility(View.GONE);
                break;
            }

            case 2:  // 50 k
            {
                if (ll50k.getVisibility() == View.GONE) {
                    ll50k.setVisibility(View.VISIBLE);
                    iv50k.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));

                    ivLess50.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv1lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv3lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv10lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                } else {
                    ll50k.setVisibility(View.GONE);
                    iv50k.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                }


                llLess50k.setVisibility(View.GONE);
                ll1lac.setVisibility(View.GONE);
                ll3lac.setVisibility(View.GONE);
                ll10lac.setVisibility(View.GONE);
                break;
            }
            case 3: // 1 lac
            {
                if (ll1lac.getVisibility() == View.GONE) {
                    ll1lac.setVisibility(View.VISIBLE);
                    iv1lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));

                    ivLess50.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv50k.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv3lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv10lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                } else {
                    ll1lac.setVisibility(View.GONE);
                    iv1lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                }


                ll50k.setVisibility(View.GONE);
                llLess50k.setVisibility(View.GONE);
                ll3lac.setVisibility(View.GONE);
                ll10lac.setVisibility(View.GONE);
                break;

            }
            case 4: // 3 lac
            {
                if (ll3lac.getVisibility() == View.GONE) {
                    ll3lac.setVisibility(View.VISIBLE);
                    iv3lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));

                    ivLess50.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv50k.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv1lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv10lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                } else {
                    ll3lac.setVisibility(View.GONE);
                    iv3lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                }


                ll50k.setVisibility(View.GONE);
                llLess50k.setVisibility(View.GONE);
                ll1lac.setVisibility(View.GONE);
                ll10lac.setVisibility(View.GONE);
                break;

            }
            case 5: // 10 lac
            {
                if (ll10lac.getVisibility() == View.GONE) {
                    ll10lac.setVisibility(View.VISIBLE);
                    iv10lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove));

                    ivLess50.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv50k.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv1lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    iv3lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                } else {
                    ll10lac.setVisibility(View.GONE);
                    iv10lac.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                }


                llLess50k.setVisibility(View.GONE);
                ll50k.setVisibility(View.GONE);
                ll1lac.setVisibility(View.GONE);
                ll3lac.setVisibility(View.GONE);
                break;


            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rlLess50k) {

            setLayout(1);


        } else if (v.getId() == R.id.rl50k) {
            setLayout(2);

        } else if (v.getId() == R.id.rl1lac) {
            setLayout(3);

        } else if (v.getId() == R.id.rl3lac) {
            setLayout(4);

        } else if (v.getId() == R.id.rl10lac) {
            setLayout(5);

        }

    }

    public void CalculateIncomePotential() {
        //txtvalue
        double txtLess50 = less50k;
        double txtMore50 = upto1lac;
        double txtMore1 = upto3lac;
        double txtMore3 = upto10lac;
        double txtAbove10 = above10lac;
//CR value
        TwoWheelerCR = .17;
        PrivateCarCR = .17;
        HealthInsuranceCR = .20;
        TravelInsuranceCR = .20;
        HomeCR = .0025;
        PersonalLoanCR = .0075;
        BusinessCR = .0075;
        //
        if (scenario == 1) {
            TwoWheeler = 0.15;
            PrivateCar = 0.15;
            HealthInsurance = 0.15;
            TravelInsurance = 0.15;
            Home = 0.15;
            PersonalLoan = 0.15;
            Business = 0.15;
        } else if (scenario == 2) {
            TwoWheeler = .20;
            PrivateCar = .20;
            HealthInsurance = .20;
            TravelInsurance = .20;
            Home = .20;
            PersonalLoan = .20;
            Business = .20;
        } else if (scenario == 3) {
            TwoWheeler = .30;
            PrivateCar = .30;
            HealthInsurance = .30;
            TravelInsurance = .30;
            Home = .30;
            PersonalLoan = .30;
            Business = .30;
        }

        double HealthInsu = 0;
        double MotorInsu = 0;
        double TravelInsu = 0;
        double HomeLoan = 0;
        double BusinessLoan = 0;
        double Other = 0;

        double Less50 = 0;
        double More50 = 0;
        double More1 = 0;
        double More3 = 0;
        double Above10 = 0;
        double Total = 0;

        double PotentialComm = 0;
        double PotentialPrem = 0;

        double EstIncomeLess50 = 500000;
        double EstIncomeMore50 = 1000000;
        double EstIncomeMore1 = 3000000;
        double EstIncomeMore3 = 5000000;
        double EstIncomeAbove10 = 10000000;

        //pt conversion
        if (type.toString().matches("Health")) {
            tvPotConverionLess50k.setText(HealthInsurance * 100 + "%");
            tvPotConverion50k.setText(HealthInsurance * 100 + "%");
            tvPotConverion1lac.setText(HealthInsurance * 100 + "%");
            tvPotConverion3lac.setText(HealthInsurance * 100 + "%");
            tvPotConverion10lac.setText(HealthInsurance * 100 + "%");

        } else if (type.toString().matches("Motor")) {
            tvPotConverionLess50k.setText(PrivateCar * 100 + "%");
            tvPotConverion50k.setText(PrivateCar * 100 + "%");
            tvPotConverion1lac.setText(PrivateCar * 100 + "%");
            tvPotConverion3lac.setText(PrivateCar * 100 + "%");
            tvPotConverion10lac.setText(PrivateCar * 100 + "%");
        } else if (type.toString().matches("Travel")) {
            tvPotConverionLess50k.setText(TravelInsurance * 100 + "%");
            tvPotConverion50k.setText(TravelInsurance * 100 + "%");
            tvPotConverion1lac.setText(TravelInsurance * 100 + "%");
            tvPotConverion3lac.setText(TravelInsurance * 100 + "%");
            tvPotConverion10lac.setText(TravelInsurance * 100 + "%");
        } else if (type.toString().matches("Home")) {
            tvPotConverionLess50k.setText(Home * 100 + "%");
            tvPotConverion50k.setText(Home * 100 + "%");
            tvPotConverion1lac.setText(Home * 100 + "%");
            tvPotConverion3lac.setText(Home * 100 + "%");
            tvPotConverion10lac.setText(Home * 100 + "%");
        } else if (type.toString().matches("Business")) {
            tvPotConverionLess50k.setText(Business * 100 + "%");
            tvPotConverion50k.setText(Business * 100 + "%");
            tvPotConverion1lac.setText(Business * 100 + "%");
            tvPotConverion3lac.setText(Business * 100 + "%");
            tvPotConverion10lac.setText(Business * 100 + "%");
        } else if (type.toString().matches("Other")) {
            tvPotConverionLess50k.setText(PersonalLoan * 100 + "%");
            tvPotConverion50k.setText(PersonalLoan * 100 + "%");
            tvPotConverion1lac.setText(PersonalLoan * 100 + "%");
            tvPotConverion3lac.setText(PersonalLoan * 100 + "%");
            tvPotConverion10lac.setText(PersonalLoan * 100 + "%");
        }
        // txt less 50
        String txtLess50s = String.valueOf(txtLess50);
        if (!txtLess50s.matches("")) {
            if (type.toString().matches("Health")) {
                PotentialComm = Math.round(txtLess50 * HealthInsurance * 10000 * HealthInsuranceCR);
                PotentialPrem = Math.round(txtLess50 * 10000 * HealthInsurance);
            } else if (type.toString().matches("Motor")) {
                PotentialComm = Math.round(txtLess50 * TwoWheeler * 1000 * TwoWheelerCR);
                PotentialPrem = Math.round(txtLess50 * 1000 * TwoWheeler);

                tvPotConverionLess50k.setText(TwoWheeler * 100 + "%");
                tvPotConverion50k.setText(TwoWheeler * 100 + "%");
                tvPotConverion1lac.setText(TwoWheeler * 100 + "%");
                tvPotConverion3lac.setText(TwoWheeler * 100 + "%");
                tvPotConverion10lac.setText(TwoWheeler * 100 + "%");

            } else if (type.toString().matches("Travel")) {
                double ITP = 0;
                double NoOfTraveller = txtLess50 * ITP;
                PotentialPrem = Math.round(NoOfTraveller * 2000 * TravelInsurance);
                PotentialComm = Math.round(PotentialPrem * TravelInsuranceCR);
            } else if (type.toString().matches("Home")) {
                double HomeLoanEligi = EstIncomeLess50 * 5;
                double HomeLoanSeekers = 0.05;//(5 / 100);
                double PotenHomeLoanThruMe = Math.round(txtLess50 * HomeLoanSeekers * Home);
                double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);

                PotentialPrem = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                PotentialComm = Math.round(PoteHomeLoanAmt * HomeCR);


                spnLoanEligiLess50.setText("Home Loan Eligibility");
                spnLoanEligAmtLess50.setText(getRemovedDecimal(HomeLoanEligi));

                lbtvPotPremiumLess50k.setText("Potential Home Loan Amount");

            } else if (type.toString().matches("Business")) {
                double BusiLoanNeed = EstIncomeLess50 * 0;
                double BusiLoanSeekers = 0;
                double PotenBusiLoanThruMe = Math.round((txtLess50) * BusiLoanSeekers * (Business));
                double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);

                PotentialPrem = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                PotentialComm = Math.round(PoteBusiLoanAmt * BusinessCR); //(((parseInt(txtLess50) * parseFloat(Business)) * 10000) * BusinessCR);


                spnLoanEligiLess50.setText("Business Loan Needs");
                spnLoanEligAmtLess50.setText(getRemovedDecimal(BusiLoanNeed));
                lbtvPotPremiumLess50k.setText("Potential Business Loan Amount");

            } else if (type.toString().matches("Other")) {
                double PersLoanEligi = (EstIncomeLess50 * 0.4);
                double PersLoanSeekers = 0.05;//(5 / 100);
                double PotenPersLoanThruMe = Math.round((txtLess50) * PersLoanSeekers * (PersonalLoan));
                double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);

                PotentialPrem = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                PotentialComm = Math.round(PotePersLoanAmt * PersonalLoanCR);//(((parseInt(txtLess50) * parseFloat(PersonalLoan)) * 10000) * PersonalLoanCR);


                spnLoanEligiLess50.setText("Personal Loan Eligibility");
                spnLoanEligAmtLess50.setText(getRemovedDecimal(PersLoanEligi));
                lbtvPotPremiumLess50k.setText("Potential Personal Loan Amount");
            }
            tvNoFamilyLess50k.setText(getRemovedDecimal(txtLess50));
            tvEstFamilyIncomLess50k.setText("500000");

            txtless50k.setText(getRemovedDecimal(PotentialComm));
            tvPotCommissionLess50k.setText(getRemovedDecimal(PotentialComm));

            tvPotPremiumLess50k.setText(getRemovedDecimal(PotentialPrem));
            Total = PotentialComm;
        }

        // txtMore50
        String txtMore50s = String.valueOf(txtMore50);
        if (!txtMore50s.matches("")) {
            if (type.toString().matches("Health")) {

                PotentialComm = Math.round(txtMore50 * HealthInsurance * 20000 * HealthInsuranceCR);
                PotentialPrem = Math.round(txtMore50 * 20000 * HealthInsurance);


            } else if (type.toString().matches("Motor")) {
                PotentialComm = Math.round(txtMore50 * PrivateCar * 5000 * PrivateCarCR);
                PotentialPrem = Math.round(txtMore50 * 5000 * PrivateCar);

            } else if (type.toString().matches("Travel")) {
                double ITP = 0.1;
                double NoOfTraveller = txtMore50 * ITP;
                PotentialPrem = Math.round(NoOfTraveller * 2000 * TravelInsurance);
                PotentialComm = Math.round(PotentialPrem * TravelInsuranceCR);

            } else if (type.toString().matches("Home")) {
                double HomeLoanEligi = EstIncomeMore50 * 5;
                double HomeLoanSeekers = 0.05;//(5 / 100);
                double PotenHomeLoanThruMe = Math.round(txtMore50 * HomeLoanSeekers * Home);
                double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);

                PotentialPrem = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                PotentialComm = Math.round(PoteHomeLoanAmt * HomeCR);


                spnLoanEligiMore50.setText("Home Loan Eligibility");
                spnLoanEligAmtMore50.setText(getRemovedDecimal(HomeLoanEligi));

                lbtvPotPremium50k.setText("Potential Home Loan Amount");


            } else if (type.toString().matches("Business")) {
                double BusiLoanNeed = EstIncomeMore50 * 0;
                double BusiLoanSeekers = 0;
                double PotenBusiLoanThruMe = Math.round((txtMore50) * BusiLoanSeekers * (Business));
                double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);

                PotentialPrem = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                PotentialComm = Math.round(PoteBusiLoanAmt * BusinessCR); //(((parseInt(txtLess50) * parseFloat(Business)) * 10000) * BusinessCR);


                spnLoanEligiMore50.setText("Business Loan Needs");
                spnLoanEligAmtMore50.setText(getRemovedDecimal(BusiLoanNeed));
                lbtvPotPremium50k.setText("Potential Business Loan Amount");

            } else if (type.toString().matches("Other")) {
                double PersLoanEligi = (EstIncomeMore50 * 0.4);
                double PersLoanSeekers = 0.05;//(5 / 100);
                double PotenPersLoanThruMe = Math.round((txtMore50) * PersLoanSeekers * (PersonalLoan));
                double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);

                PotentialPrem = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                PotentialComm = Math.round(PotePersLoanAmt * PersonalLoanCR);//(((parseInt(txtLess50) * parseFloat(PersonalLoan)) * 10000) * PersonalLoanCR);


                spnLoanEligiMore50.setText("Personal Loan Eligibility");
                spnLoanEligAmtMore50.setText(getRemovedDecimal(PersLoanEligi));
                lbtvPotPremium50k.setText("Potential Personal Loan Amount");
            }

            tvNoFamily50k.setText(getRemovedDecimal(txtMore50));
            tvEstFamilyIncom50k.setText("1000000");
            txt50k.setText(getRemovedDecimal(PotentialComm));
            tvPotCommission50k.setText(getRemovedDecimal(PotentialComm));

            tvPotPremium50k.setText(getRemovedDecimal(PotentialPrem));
            Total = Total + PotentialComm;
        }

        // txtMore1
        String txtMore1s = String.valueOf(txtMore1);
        if (!txtMore1s.matches("")) {
            if (type.toString().matches("Health")) {
                PotentialComm = Math.round(txtMore1 * HealthInsurance * 50000 * HealthInsuranceCR);
                PotentialPrem = Math.round(txtMore1 * 50000 * HealthInsurance);


            } else if (type.toString().matches("Motor")) {
                PotentialComm = Math.round(txtMore1 * PrivateCar * 12000 * PrivateCarCR);
                PotentialPrem = Math.round(txtMore1 * 12000 * PrivateCar);


            } else if (type.toString().matches("Travel")) {
                double ITP = 0.2;
                double NoOfTraveller = txtMore1 * ITP;
                PotentialPrem = Math.round(NoOfTraveller * 2000 * TravelInsurance);
                PotentialComm = Math.round(PotentialPrem * TravelInsuranceCR);
            } else if (type.toString().matches("Home")) {
                double HomeLoanEligi = EstIncomeMore1 * 5;
                double HomeLoanSeekers = 0.05;//(5 / 100);
                double PotenHomeLoanThruMe = Math.round(txtMore1 * HomeLoanSeekers * Home);
                double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);

                PotentialPrem = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                PotentialComm = Math.round(PoteHomeLoanAmt * HomeCR);


                spnLoanEligiMore1.setText("Home Loan Eligibility");
                spnLoanEligAmtMore1.setText(getRemovedDecimal(HomeLoanEligi));

                lbtvPotPremium1lac.setText("Potential Home Loan Amount");
            } else if (type.toString().matches("Business")) {
                double BusiLoanNeed = EstIncomeMore1 * 2;
                double BusiLoanSeekers = 0.05;
                double PotenBusiLoanThruMe = Math.round((txtMore1) * BusiLoanSeekers * (Business));
                double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);

                PotentialPrem = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                PotentialComm = Math.round(PoteBusiLoanAmt * BusinessCR); //(((parseInt(txtLess50) * parseFloat(Business)) * 10000) * BusinessCR);


                spnLoanEligiMore1.setText("Business Loan Needs");
                spnLoanEligAmtMore1.setText(getRemovedDecimal(BusiLoanNeed));
                lbtvPotPremium1lac.setText("Potential Business Loan Amount");
            } else if (type.toString().matches("Other")) {
                double PersLoanEligi = (EstIncomeMore1 * 0.4);
                double PersLoanSeekers = 0.05;//(5 / 100);
                double PotenPersLoanThruMe = Math.round((txtMore1) * PersLoanSeekers * (PersonalLoan));
                double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);

                PotentialPrem = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                PotentialComm = Math.round(PotePersLoanAmt * PersonalLoanCR);//(((parseInt(txtLess50) * parseFloat(PersonalLoan)) * 10000) * PersonalLoanCR);


                spnLoanEligiMore1.setText("Personal Loan Eligibility");
                spnLoanEligAmtMore1.setText(getRemovedDecimal(PersLoanEligi));
                lbtvPotPremium1lac.setText("Potential Personal Loan Amount");
            }

            tvNoFamily1lac.setText(getRemovedDecimal(txtMore1));
            tvEstFamilyIncom1lac.setText("3000000");
            txt1lac.setText(getRemovedDecimal(PotentialComm));
            tvPotCommission1lac.setText(getRemovedDecimal(PotentialComm));

            tvPotPremium1lac.setText(getRemovedDecimal(PotentialPrem));
            Total = Total + PotentialComm;
        }
        // txtMore3
        String txtMore3s = String.valueOf(txtMore3);
        if (!txtMore3s.matches("")) {
            if (type.toString().matches("Health")) {
                PotentialComm = Math.round(txtMore3 * HealthInsurance * 100000 * HealthInsuranceCR);
                PotentialPrem = Math.round(txtMore3 * 100000 * HealthInsurance);

            } else if (type.toString().matches("Motor")) {

                PotentialComm = Math.round(txtMore3 * PrivateCar * 25000 * PrivateCarCR);
                PotentialPrem = Math.round(txtMore3 * 25000 * PrivateCar);

            } else if (type.toString().matches("Travel")) {
                double ITP = 0.4;
                double NoOfTraveller = txtMore3 * ITP;
                PotentialPrem = Math.round(NoOfTraveller * 3000 * TravelInsurance);
                PotentialComm = Math.round(PotentialPrem * TravelInsuranceCR);
            } else if (type.toString().matches("Home")) {
                double HomeLoanEligi = EstIncomeMore3 * 5;
                double HomeLoanSeekers = 0.05;//(5 / 100);
                double PotenHomeLoanThruMe = Math.round(txtMore3 * HomeLoanSeekers * Home);
                double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);

                PotentialPrem = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                PotentialComm = Math.round(PoteHomeLoanAmt * HomeCR);


                spnLoanEligiMore3.setText("Home Loan Eligibility");
                spnLoanEligAmtMore3.setText(getRemovedDecimal(HomeLoanEligi));

                lbtvPotPremium3lac.setText("Potential Home Loan Amount");
            } else if (type.toString().matches("Business")) {
                double BusiLoanNeed = EstIncomeMore3 * 2;
                double BusiLoanSeekers = 0.05;
                double PotenBusiLoanThruMe = Math.round((txtMore3) * BusiLoanSeekers * (Business));
                double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);

                PotentialPrem = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                PotentialComm = Math.round(PoteBusiLoanAmt * BusinessCR); //(((parseInt(txtLess50) * parseFloat(Business)) * 10000) * BusinessCR);


                spnLoanEligiMore3.setText("Business Loan Needs");
                spnLoanEligAmtMore3.setText(getRemovedDecimal(BusiLoanNeed));
                lbtvPotPremium3lac.setText("Potential Business Loan Amount");
            } else if (type.toString().matches("Other")) {
                double PersLoanEligi = (EstIncomeMore3 * 0.4);
                double PersLoanSeekers = 0.05;//(5 / 100);
                double PotenPersLoanThruMe = Math.round((txtMore3) * PersLoanSeekers * (PersonalLoan));
                double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);

                PotentialPrem = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                PotentialComm = Math.round(PotePersLoanAmt * PersonalLoanCR);//(((parseInt(txtLess50) * parseFloat(PersonalLoan)) * 10000) * PersonalLoanCR);


                spnLoanEligiMore3.setText("Personal Loan Eligibility");
                spnLoanEligAmtMore3.setText(getRemovedDecimal(PersLoanEligi));
                lbtvPotPremium3lac.setText("Potential Personal Loan Amount");
            }

            tvNoFamily3lac.setText(getRemovedDecimal(txtMore3));
            tvEstFamilyIncom3lac.setText("5000000");
            txt3lac.setText(getRemovedDecimal(PotentialComm));
            tvPotCommission3lac.setText(getRemovedDecimal(PotentialComm));

            tvPotPremium3lac.setText(getRemovedDecimal(PotentialPrem));
            Total = Total + PotentialComm;
        }
        // txtAbove10
        String txtAbove10s = String.valueOf(txtAbove10);
        if (!txtAbove10s.matches("")) {
            if (type.toString().matches("Health")) {

                PotentialComm = Math.round(txtAbove10 * HealthInsurance * 200000 * HealthInsuranceCR);
                PotentialPrem = Math.round(txtAbove10 * 200000 * HealthInsurance);
            } else if (type.toString().matches("Motor")) {
                PotentialComm = Math.round(txtAbove10 * PrivateCar * 50000 * PrivateCarCR);
                PotentialPrem = Math.round(txtAbove10 * 50000 * PrivateCar);

            } else if (type.toString().matches("Travel")) {
                double ITP = 0.6;
                double NoOfTraveller = txtAbove10 * ITP;
                PotentialPrem = Math.round(NoOfTraveller * 3000 * TravelInsurance);
                PotentialComm = Math.round(PotentialPrem * TravelInsuranceCR);
            } else if (type.toString().matches("Home")) {

                double HomeLoanEligi = EstIncomeAbove10 * 5;
                double HomeLoanSeekers = 0.05;//(5 / 100);
                double PotenHomeLoanThruMe = Math.round(txtAbove10 * HomeLoanSeekers * Home);
                double PoteHomeLoanAmt = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);

                PotentialPrem = Math.round(PotenHomeLoanThruMe * HomeLoanEligi);
                PotentialComm = Math.round(PoteHomeLoanAmt * HomeCR);


                spnLoanEligiAbove10.setText("Home Loan Eligibility");
                spnLoanEligAmtAbove10.setText(getRemovedDecimal(HomeLoanEligi));

                lbtvPotPremium10lac.setText("Potential Home Loan Amount");
            } else if (type.toString().matches("Business")) {
                double BusiLoanNeed = EstIncomeAbove10 * 2;
                double BusiLoanSeekers = 0.05;
                double PotenBusiLoanThruMe = Math.round((txtAbove10) * BusiLoanSeekers * (Business));
                double PoteBusiLoanAmt = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);

                PotentialPrem = Math.round(PotenBusiLoanThruMe * BusiLoanNeed);
                PotentialComm = Math.round(PoteBusiLoanAmt * BusinessCR); //(((parseInt(txtLess50) * parseFloat(Business)) * 10000) * BusinessCR);


                spnLoanEligiAbove10.setText("Business Loan Needs");
                spnLoanEligAmtAbove10.setText(getRemovedDecimal(BusiLoanNeed));
                lbtvPotPremium10lac.setText("Potential Business Loan Amount");
            } else if (type.toString().matches("Other")) {
                double PersLoanEligi = (EstIncomeAbove10 * 0.4);
                double PersLoanSeekers = 0.05;//(5 / 100);
                double PotenPersLoanThruMe = Math.round((txtAbove10) * PersLoanSeekers * (PersonalLoan));
                double PotePersLoanAmt = Math.round(PotenPersLoanThruMe * PersLoanEligi);

                PotentialPrem = Math.round(PotenPersLoanThruMe * PersLoanEligi);
                PotentialComm = Math.round(PotePersLoanAmt * PersonalLoanCR);//(((parseInt(txtLess50) * parseFloat(PersonalLoan)) * 10000) * PersonalLoanCR);


                spnLoanEligiAbove10.setText("Personal Loan Eligibility");
                spnLoanEligAmtAbove10.setText(getRemovedDecimal(PersLoanEligi));
                lbtvPotPremium10lac.setText("Potential Personal Loan Amount");
            }

            tvNoFamily10lac.setText(getRemovedDecimal(txtAbove10));
            tvEstFamilyIncom10lac.setText("10000000");
            txt10lac.setText(getRemovedDecimal(PotentialComm));
            tvPotCommission10lac.setText(getRemovedDecimal(PotentialComm));

            tvPotPremium10lac.setText(getRemovedDecimal(PotentialPrem));
            Total = Total + PotentialComm;
        }

        txtSum.setText(getCommaNumber(Total));
    }

    public String getCommaNumber(double number) {
        DecimalFormat formatter = new DecimalFormat("##,##,##,##,###");
        return formatter.format(number);
    }

    public String getRemovedDecimal(double number) {
        DecimalFormat formatter = new DecimalFormat("#");
        return formatter.format(number);
    }
}