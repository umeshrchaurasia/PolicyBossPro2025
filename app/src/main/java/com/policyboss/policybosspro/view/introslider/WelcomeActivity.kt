package com.policyboss.policybosspro.view.introslider

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.ActivityWelcomeBinding
import com.policyboss.policybosspro.utils.CoroutineHelper
import com.webengage.sdk.android.WebEngage

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    private lateinit var layouts: IntArray
    private var current = 0

    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("Welcome Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarInsetsPadding()


        initWidgets()
        setListener()
        CoroutineHelper.saveDeviceDetails(this@WelcomeActivity, "0", "Install")
    }

    private fun setListener() {
        myViewPagerAdapter = MyViewPagerAdapter(layouts)
        binding.viewPager.adapter = myViewPagerAdapter

        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        binding.btnNext.setOnClickListener(this)
        binding.btnSkip.setOnClickListener(this)
    }

    private fun initWidgets() {
        layouts = intArrayOf(
            R.layout.welcome_slide1,
            R.layout.welcome_slide2,
            R.layout.welcome_slide3,
            R.layout.welcome_slide4,
            R.layout.welcome_slide5
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> {
                current++
                if (current < layouts.size) {
                    binding.viewPager.currentItem = current
                } else {
                    startActivity(Intent(this, EulaActivity::class.java))
                }
            }
            R.id.btn_skip -> startActivity(Intent(this, EulaActivity::class.java))
        }
    }

    //  ViewPager2 change listener
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            current = position
            setSelectedDot(position + 1)

            if (position == layouts.size - 1) {
                binding.btnNext.text = getString(R.string.get_started)
                binding.btnSkip.visibility = View.VISIBLE
            } else {
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
        }
    }

    private fun setSelectedDot(current: Int) {
        // Reset all dots to unselected
        binding.dot1.setImageDrawable(getDrawable(R.drawable.unselected_dot))
        binding.dot2.setImageDrawable(getDrawable(R.drawable.unselected_dot))
        binding.dot3.setImageDrawable(getDrawable(R.drawable.unselected_dot))
        binding.dot4.setImageDrawable(getDrawable(R.drawable.unselected_dot))
        binding.dot5.setImageDrawable(getDrawable(R.drawable.unselected_dot))

        // Set the selected dot based on position
        when (current) {
            1 -> binding.dot1.setImageDrawable(getDrawable(R.drawable.selected_dot))
            2 -> binding.dot2.setImageDrawable(getDrawable(R.drawable.selected_dot))
            3 -> binding.dot3.setImageDrawable(getDrawable(R.drawable.selected_dot))
            4 -> binding.dot4.setImageDrawable(getDrawable(R.drawable.selected_dot))
            5 -> binding.dot5.setImageDrawable(getDrawable(R.drawable.selected_dot))
        }
    }

    // ViewPager2 Adapter using RecyclerView.Adapter
    inner class MyViewPagerAdapter(private val layouts: IntArray) :
        RecyclerView.Adapter<MyViewPagerAdapter.ViewHolder>() {

        inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(layoutRes: Int) {
                // No need for additional binding in this case, view is inflated in onCreateViewHolder
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Inflate the specific layout for the position
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Not much binding needed for static layouts
            holder.bind(layouts[position])
        }

        override fun getItemCount(): Int {
            return layouts.size
        }

        override fun getItemViewType(position: Int): Int {
            // Return the specific layout for this position
            return layouts[position]
        }
    }

    override fun onDestroy() {
        super.onDestroy()


    }
}