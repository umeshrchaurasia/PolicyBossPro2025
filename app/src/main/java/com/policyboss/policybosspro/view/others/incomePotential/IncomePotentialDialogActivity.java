package com.policyboss.policybosspro.view.others.incomePotential;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.policyboss.policybosspro.BaseActivity;
import com.policyboss.policybosspro.BaseJavaActivity;
import com.policyboss.policybosspro.R;
import com.policyboss.policybosspro.utils.animation.Animate;

import java.text.DecimalFormat;

public class IncomePotentialDialogActivity extends BaseJavaActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    LinearLayout llPerAnnum, llNext10Years;
    RelativeLayout rlPerAnnum, rlNext10Years;
    ImageView ivPerAnnum, ivAfter10Years;

    TextView tvPerAnnumToatal, tvHealthPerAnnum, tvMotorPerAnnum, tvTravelPerAnnum, tvHomeLoanPerAnnum, tvBusinessPerAnnum,
            tvHomeNext10, tvBusinessNext10, tvOtherNext10, tvOtherPerAnnum, tvTotalNext10, tvHealthNext10, tvMotorNext10, tvTravelNext10;
    CheckBox chHealthPerAnnum, chMotorPerAnnum, chTravelPerAnnum, chHomePerAnnum, chBusinessNext10,
            chBusinessPerAnnum, chOtherPerAnnum, chOtherNext10, chHealthNext10, chMotorNext10, chTravelNext10, chHomeNext10;


    double HealthInsu = 0;
    double MotorInsu = 0;
    double TravelInsu = 0;
    double HomeLoan = 0;
    double BusinessLoan = 0;
    double Other = 0;
    double total = 0;
    int scenario = 0;
    double ProHealthInsu = 0;
    double ProMotorInsu = 0;
    double ProTravelInsu = 0;
    double ProHomeLoan = 0;
    double ProBusinessLoan = 0;
    double ProOther = 0;
    double ProTotal = 0;
    double less50k, upto1lac, upto3lac, upto10lac, above10lac;
    double TwoWheeler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_potential_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFinishOnTouchOutside(false);
        init_widgets();
        getValuesFromPrevActivity();
        setListeners();
        bindData();
    }

    public String getCommaNumber(double number) {
        DecimalFormat formatter = new DecimalFormat("##,##,##,##,###");
        return formatter.format(number);
    }

    private void bindData() {
        tvTotalNext10.setText("" + getCommaNumber(ProTotal));
        tvPerAnnumToatal.setText("" + getCommaNumber(total));


        tvHealthPerAnnum.setText("" + getCommaNumber(HealthInsu));
        tvMotorPerAnnum.setText("" + getCommaNumber(MotorInsu));
        tvTravelPerAnnum.setText("" + getCommaNumber(TravelInsu));
        tvHomeLoanPerAnnum.setText("" + getCommaNumber(HomeLoan));
        tvBusinessPerAnnum.setText("" + getCommaNumber(BusinessLoan));
        tvOtherPerAnnum.setText("" + getCommaNumber(Other));

        tvHealthPerAnnum.setPaintFlags(tvHealthPerAnnum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvMotorPerAnnum.setPaintFlags(tvMotorPerAnnum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvTravelPerAnnum.setPaintFlags(tvTravelPerAnnum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvHomeLoanPerAnnum.setPaintFlags(tvHomeLoanPerAnnum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvBusinessPerAnnum.setPaintFlags(tvBusinessPerAnnum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvOtherPerAnnum.setPaintFlags(tvOtherPerAnnum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        tvHealthNext10.setText("" + getCommaNumber(ProHealthInsu));
        tvMotorNext10.setText("" + getCommaNumber(ProMotorInsu));
        tvTravelNext10.setText("" + getCommaNumber(ProTravelInsu));
        tvHomeNext10.setText("" + getCommaNumber(ProHomeLoan));
        tvBusinessNext10.setText("" + getCommaNumber(ProBusinessLoan));
        tvOtherNext10.setText("" + getCommaNumber(ProOther));
    }

    private void setListeners() {
        tvPerAnnumToatal.setOnClickListener(this);
        tvHealthPerAnnum.setOnClickListener(this);
        tvMotorPerAnnum.setOnClickListener(this);
        tvTravelPerAnnum.setOnClickListener(this);
        tvHomeLoanPerAnnum.setOnClickListener(this);
        tvBusinessPerAnnum.setOnClickListener(this);
        tvHomeNext10.setOnClickListener(this);
        tvBusinessNext10.setOnClickListener(this);
        tvOtherNext10.setOnClickListener(this);
        tvOtherPerAnnum.setOnClickListener(this);
        tvTotalNext10.setOnClickListener(this);
        tvHealthNext10.setOnClickListener(this);
        tvMotorNext10.setOnClickListener(this);
        tvTravelNext10.setOnClickListener(this);
        chHealthPerAnnum.setOnCheckedChangeListener(this);
        chMotorPerAnnum.setOnCheckedChangeListener(this);
        chTravelPerAnnum.setOnCheckedChangeListener(this);
        chHomePerAnnum.setOnCheckedChangeListener(this);
        chBusinessPerAnnum.setOnCheckedChangeListener(this);
        chOtherPerAnnum.setOnCheckedChangeListener(this);
        chHealthNext10.setOnCheckedChangeListener(this);
        chMotorNext10.setOnCheckedChangeListener(this);
        chTravelNext10.setOnCheckedChangeListener(this);
        chHomeNext10.setOnCheckedChangeListener(this);
        chBusinessNext10.setOnCheckedChangeListener(this);
        chOtherNext10.setOnCheckedChangeListener(this);
    }

    private void getValuesFromPrevActivity() {
        ProTotal = getIntent().getDoubleExtra("ProTotal", 0.0);
        total = getIntent().getDoubleExtra("total", 0.0);
        HealthInsu = getIntent().getDoubleExtra("HealthInsu", 0.0);
        MotorInsu = getIntent().getDoubleExtra("MotorInsu", 0.0);
        TravelInsu = getIntent().getDoubleExtra("TravelInsu", 0.0);
        HomeLoan = getIntent().getDoubleExtra("HomeLoan", 0.0);
        BusinessLoan = getIntent().getDoubleExtra("BusinessLoan", 0.0);
        Other = getIntent().getDoubleExtra("Other", 0.0);
        ProHealthInsu = getIntent().getDoubleExtra("ProHealthInsu", 0.0);
        ProMotorInsu = getIntent().getDoubleExtra("ProMotorInsu", 0.0);
        ProTravelInsu = getIntent().getDoubleExtra("ProTravelInsu", 0.0);
        ProHomeLoan = getIntent().getDoubleExtra("ProHomeLoan", 0.0);
        ProBusinessLoan = getIntent().getDoubleExtra("ProBusinessLoan", 0.0);
        ProOther = getIntent().getDoubleExtra("ProOther", 0.0);
        less50k = getIntent().getDoubleExtra("less50k", 0.0);
        upto1lac = getIntent().getDoubleExtra("upto1lac", 0.0);
        upto3lac = getIntent().getDoubleExtra("upto3lac", 0.0);
        upto10lac = getIntent().getDoubleExtra("upto10lac", 0.0);
        above10lac = getIntent().getDoubleExtra("above10lac", 0.0);
        scenario = getIntent().getIntExtra("scenario", 0);
    }

    private void init_widgets() {
        llPerAnnum = (LinearLayout) findViewById(R.id.llPerAnnum);
        llNext10Years = (LinearLayout) findViewById(R.id.llNext10Years);
        rlPerAnnum = (RelativeLayout) findViewById(R.id.rlPerAnnum);
        rlNext10Years = (RelativeLayout) findViewById(R.id.rlNext10Years);
        ivAfter10Years = (ImageView) findViewById(R.id.ivAfter10Years);
        ivPerAnnum = (ImageView) findViewById(R.id.ivPerAnnum);


        tvPerAnnumToatal = (TextView) findViewById(R.id.tvPerAnnumToatal);
        tvHealthPerAnnum = (TextView) findViewById(R.id.tvHealthPerAnnum);
        tvMotorPerAnnum = (TextView) findViewById(R.id.tvMotorPerAnnum);
        tvTravelPerAnnum = (TextView) findViewById(R.id.tvTravelPerAnnum);
        tvHomeLoanPerAnnum = (TextView) findViewById(R.id.tvHomeLoanPerAnnum);
        tvBusinessPerAnnum = (TextView) findViewById(R.id.tvBusinessPerAnnum);
        tvHomeNext10 = (TextView) findViewById(R.id.tvHomeNext10);
        tvBusinessNext10 = (TextView) findViewById(R.id.tvBusinessNext10);
        tvOtherNext10 = (TextView) findViewById(R.id.tvOtherNext10);
        tvOtherPerAnnum = (TextView) findViewById(R.id.tvOtherPerAnnum);
        tvTotalNext10 = (TextView) findViewById(R.id.tvTotalNext10);
        tvHealthNext10 = (TextView) findViewById(R.id.tvHealthNext10);
        tvMotorNext10 = (TextView) findViewById(R.id.tvMotorNext10);
        tvTravelNext10 = (TextView) findViewById(R.id.tvTravelNext10);


        chHealthPerAnnum = (CheckBox) findViewById(R.id.chHealthPerAnnum);
        chMotorPerAnnum = (CheckBox) findViewById(R.id.chMotorPerAnnum);
        chTravelPerAnnum = (CheckBox) findViewById(R.id.chTravelPerAnnum);
        chHomePerAnnum = (CheckBox) findViewById(R.id.chHomePerAnnum);
        chBusinessNext10 = (CheckBox) findViewById(R.id.chBusinessNext10);
        chBusinessPerAnnum = (CheckBox) findViewById(R.id.chBusinessPerAnnum);
        chOtherPerAnnum = (CheckBox) findViewById(R.id.chOtherPerAnnum);
        chOtherNext10 = (CheckBox) findViewById(R.id.chOtherNext10);
        chHealthNext10 = (CheckBox) findViewById(R.id.chHealthNext10);
        chMotorNext10 = (CheckBox) findViewById(R.id.chMotorNext10);
        chTravelNext10 = (CheckBox) findViewById(R.id.chTravelNext10);
        chHomeNext10 = (CheckBox) findViewById(R.id.chHomeNext10);


        llPerAnnum.setVisibility(View.GONE);
        llNext10Years.setVisibility(View.GONE);
        rlPerAnnum.setOnClickListener(this);
        rlNext10Years.setOnClickListener(this);
        tvHealthPerAnnum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPerAnnum:
            case R.id.tvPerAnnumToatal:
                if (llPerAnnum.getVisibility() == View.VISIBLE) {
                    Animate.SlideUpAnimation(llPerAnnum);
                } else {
                    llPerAnnum.setVisibility(View.VISIBLE);
                    Animate.SlideDownAnimation(llPerAnnum);

                    ivPerAnnum.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.up2));
                }
                if (llNext10Years.getVisibility() == View.VISIBLE) {
                    llNext10Years.setVisibility(View.GONE);

                    ivAfter10Years.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.down2));
                }
                break;

            case R.id.rlNext10Years:
            case R.id.tvTotalNext10:
                if (llNext10Years.getVisibility() == View.VISIBLE) {
                    //Animate.SlideUpAnimation(llNext10Years);
                    Animate.SlideUpAnimation(llNext10Years);
                } else {
                    llNext10Years.setVisibility(View.VISIBLE);
                    Animate.SlideDownAnimation(llNext10Years);
                    ivAfter10Years.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.up2));
                }
                if (llPerAnnum.getVisibility() == View.VISIBLE) {
                    llPerAnnum.setVisibility(View.GONE);
                    ivPerAnnum.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.down2));
                }
                break;
            case R.id.tvMotorPerAnnum:
                if (chMotorPerAnnum.isChecked()) {
                    startActivity(new Intent(this, IncomePotentialdetailActivity.class)
                            .putExtra("type", "Motor")
                            .putExtra("typeValue", "MOTOR INSURANCE COMMISSION")
                            .putExtra("scenario", scenario)
                            .putExtra("less50k", less50k)
                            .putExtra("upto1lac", upto1lac)
                            .putExtra("upto3lac", upto3lac)
                            .putExtra("upto10lac", upto10lac)
                            .putExtra("above10lac", above10lac));
                }
                break;

            case R.id.tvHealthPerAnnum:
                if (chHealthPerAnnum.isChecked()) {
                    startActivity(new Intent(this, IncomePotentialdetailActivity.class)
                            .putExtra("type", "Health")
                            .putExtra("typeValue", "HEALTH INSURANCE COMMISSION")
                            .putExtra("scenario", scenario)
                            .putExtra("less50k", less50k)
                            .putExtra("upto1lac", upto1lac)
                            .putExtra("upto3lac", upto3lac)
                            .putExtra("upto10lac", upto10lac)
                            .putExtra("above10lac", above10lac));
                }
                break;
            case R.id.tvTravelPerAnnum:
                if (chTravelPerAnnum.isChecked()) {
                    startActivity(new Intent(this, IncomePotentialdetailActivity.class)
                            .putExtra("type", "Travel")
                            .putExtra("typeValue", "TRAVEL INSURANCE COMMISSION")
                            .putExtra("scenario", scenario)
                            .putExtra("less50k", less50k)
                            .putExtra("upto1lac", upto1lac)
                            .putExtra("upto3lac", upto3lac)
                            .putExtra("upto10lac", upto10lac)
                            .putExtra("above10lac", above10lac));
                }
                break;
            case R.id.tvHomeLoanPerAnnum:
                if (chHomePerAnnum.isChecked()) {
                    startActivity(new Intent(this, IncomePotentialdetailActivity.class)
                            .putExtra("type", "Home")
                            .putExtra("typeValue", "HOME LOAN COMMISSION")
                            .putExtra("scenario", scenario)
                            .putExtra("less50k", less50k)
                            .putExtra("upto1lac", upto1lac)
                            .putExtra("upto3lac", upto3lac)
                            .putExtra("upto10lac", upto10lac)
                            .putExtra("above10lac", above10lac));
                }
                break;
            case R.id.tvBusinessPerAnnum:
                if (chBusinessPerAnnum.isChecked()) {
                    startActivity(new Intent(this, IncomePotentialdetailActivity.class)
                            .putExtra("type", "Business")
                            .putExtra("typeValue", "BUSINESS LOAN COMMISSION")
                            .putExtra("scenario", scenario)
                            .putExtra("less50k", less50k)
                            .putExtra("upto1lac", upto1lac)
                            .putExtra("upto3lac", upto3lac)
                            .putExtra("upto10lac", upto10lac)
                            .putExtra("above10lac", above10lac));
                }
                break;
            case R.id.tvOtherPerAnnum:
                if (chOtherPerAnnum.isChecked()) {
                    startActivity(new Intent(this, IncomePotentialdetailActivity.class)
                            .putExtra("type", "Other")
                            .putExtra("typeValue", "OTHER LOAN COMMISSION")
                            .putExtra("scenario", scenario)
                            .putExtra("less50k", less50k)
                            .putExtra("upto1lac", upto1lac)
                            .putExtra("upto3lac", upto3lac)
                            .putExtra("upto10lac", upto10lac)
                            .putExtra("above10lac", above10lac));
                }
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chHealthPerAnnum:

                if (isChecked) {
                    total = total + HealthInsu;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                } else {
                    total = total - HealthInsu;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                }
                changeNext10YearCheckBox();
                break;
            case R.id.chMotorPerAnnum:
                if (isChecked) {
                    total = total + MotorInsu;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                } else {
                    total = total - MotorInsu;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                }
                changeNext10YearCheckBox();
                break;
            case R.id.chTravelPerAnnum:
                if (isChecked) {
                    total = total + TravelInsu;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                } else {
                    total = total - TravelInsu;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                }
                changeNext10YearCheckBox();
                break;
            case R.id.chHomePerAnnum:
                if (isChecked) {
                    total = total + HomeLoan;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                } else {
                    total = total - HomeLoan;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                }
                changeNext10YearCheckBox();
                break;
            case R.id.chBusinessPerAnnum:
                if (isChecked) {
                    total = total + BusinessLoan;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                } else {
                    total = total - BusinessLoan;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                }
                changeNext10YearCheckBox();
                break;
            case R.id.chOtherPerAnnum:
                if (isChecked) {
                    total = total + Other;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                } else {
                    total = total - Other;
                    tvPerAnnumToatal.setText("" + getCommaNumber(total));
                }
                changeNext10YearCheckBox();
                break;


            case R.id.chHealthNext10:
                if (isChecked) {
                    ProTotal = ProTotal + ProHealthInsu;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                } else {
                    ProTotal = ProTotal - ProHealthInsu;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                }
                break;
            case R.id.chMotorNext10:
                if (isChecked) {
                    ProTotal = ProTotal + ProMotorInsu;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                } else {
                    ProTotal = ProTotal - ProMotorInsu;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                }
                break;
            case R.id.chTravelNext10:
                if (isChecked) {
                    ProTotal = ProTotal + ProTravelInsu;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                } else {
                    ProTotal = ProTotal - ProTravelInsu;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                }
                break;
            case R.id.chHomeNext10:
                if (isChecked) {
                    ProTotal = ProTotal + ProHomeLoan;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                } else {
                    ProTotal = ProTotal - ProHomeLoan;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                }
                break;
            case R.id.chBusinessNext10:
                if (isChecked) {
                    ProTotal = ProTotal + ProBusinessLoan;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                } else {
                    ProTotal = ProTotal - ProBusinessLoan;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                }
                break;
            case R.id.chOtherNext10:
                if (isChecked) {
                    ProTotal = ProTotal + ProOther;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                } else {
                    ProTotal = ProTotal - ProOther;
                    tvTotalNext10.setText("" + getCommaNumber(ProTotal));
                }
                break;

        }
    }

    private void changeNext10YearCheckBox() {

        if (chHealthPerAnnum.isChecked()) {
            chHealthNext10.setChecked(true);
        } else {
            chHealthNext10.setChecked(false);
        }

        if (chMotorPerAnnum.isChecked()) {
            chMotorNext10.setChecked(true);
        } else {
            chMotorNext10.setChecked(false);
        }

        if (chTravelPerAnnum.isChecked()) {
            chTravelNext10.setChecked(true);
        } else {
            chTravelNext10.setChecked(false);
        }

        if (chHomePerAnnum.isChecked()) {
            chHomeNext10.setChecked(true);
        } else {
            chHomeNext10.setChecked(false);
        }

        if (chBusinessPerAnnum.isChecked()) {
            chBusinessNext10.setChecked(true);
        } else {
            chBusinessNext10.setChecked(false);
        }

        if (chOtherPerAnnum.isChecked()) {
            chOtherNext10.setChecked(true);
        } else {
            chOtherNext10.setChecked(false);
        }
    }
}