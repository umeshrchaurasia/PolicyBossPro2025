package com.policyboss.policybosspro.view.others.feedback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.google.android.material.appbar.AppBarLayout;
import com.policyboss.policybosspro.R;
import com.policyboss.policybosspro.analytics.WebEngageAnalytics;
import com.policyboss.policybosspro.view.others.contactUS.ContactUsActivity;
import com.policyboss.policybosspro.webview.CommonWebViewActivity;
import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.WebEngage;

import java.util.HashMap;
import java.util.Map;

public class HelpFeedBackActivity extends AppCompatActivity implements View.OnClickListener {

    CardView cvContactUs, cvRaiseTicket, cvAboutUs, cvDisclosure ,cvChat;

    AppBarLayout appBarLayout ;

    View includedRoot;
    View rootLayout ; // or your outermost layout
    @Override
    protected void onStart() {
        super.onStart();

        Analytics weAnalytics = WebEngage.get().analytics();
        weAnalytics.screenNavigated("HelpFeedBack Screen");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feed_back);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("HELP &  FEEDBACK");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();

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
            includedRoot.setPadding(
                    includedRoot.getPaddingLeft(),
                    includedRoot.getPaddingTop(),
                    includedRoot.getPaddingRight(),
                    systemBars.bottom
            );

            return insets;
        });
        setListener();
    }

    private void setListener() {
        cvContactUs.setOnClickListener(this);
        cvRaiseTicket.setOnClickListener(this);
        cvAboutUs.setOnClickListener(this);
        cvDisclosure.setOnClickListener(this);
        cvChat.setOnClickListener(this);
    }

    private void initWidgets() {

        appBarLayout = findViewById(R.id.appbar);
        rootLayout = findViewById(R.id.root_layout);

        // ✅ reference the include root
        includedRoot = findViewById(R.id.include_help_feed_back);

        cvContactUs = (CardView) findViewById(R.id.cvContactUs);
        cvRaiseTicket = (CardView) findViewById(R.id.cvRaiseTicket);
        cvAboutUs = (CardView) findViewById(R.id.cvAboutUs);
        cvDisclosure = (CardView) findViewById(R.id.cvDisclosure);
        cvChat  = (CardView) findViewById(R.id.cvChat);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvChat:
                //    startActivity(new Intent(this, InsertContactActivity.class));
                break;
            case R.id.cvContactUs:
                startActivity(new Intent(this, ContactUsActivity.class));
                tracktContactFeedbackEvent();
                break;
//            case R.id.cvRaiseTicket:
//                startActivity(new Intent(this, RaiseTicketActivity.class));
//                break;
//            case R.id.cvAboutUs:
//                startActivity(new Intent(this, AboutUsActivity.class));
//                break;
            case R.id.cvDisclosure:
                startActivity(new Intent(this, CommonWebViewActivity.class)
                        .putExtra("URL", "file:///android_asset/Disclosure.html")
                        .putExtra("NAME", "DISCLOSURE")
                        .putExtra("TITLE", "DISCLOSURE"));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        supportFinishAfterTransition();
        super.onBackPressed();
    }

    private void tracktContactFeedbackEvent( ) {
        // Create event attributes
        Map<String, Object> eventAttributes = new HashMap<>();
        // Track the login event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Clicked on Contact Us in Help & Feedback" , eventAttributes);


    }

}