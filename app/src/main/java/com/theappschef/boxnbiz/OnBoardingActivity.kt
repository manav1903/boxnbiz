package com.theappschef.boxnbiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.jama.carouselview.CarouselView
import com.jama.carouselview.enums.IndicatorAnimationType
import com.jama.carouselview.enums.OffsetType

class OnBoardingActivity : AppCompatActivity() {
    var images = intArrayOf(
        R.drawable.onboarding_first,
        R.drawable.onboarding_second,
        R.drawable.onboarding_third,
        R.drawable.onboarding_four
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        val tv:TextView
        tv=findViewById(R.id.started)
        tv.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }
        val c: CarouselView
        c = findViewById(R.id.carouselView)



        c.apply {
            size = images.size
            resource = R.layout.image_carousel_item
            autoPlay = true
            indicatorAnimationType = IndicatorAnimationType.THIN_WORM
            carouselOffset = OffsetType.CENTER
            setCarouselViewListener { view, position ->
                // Example here is setting up a full image carousel
                val imageView = view.findViewById<ImageView>(R.id.image)
                imageView.setImageDrawable(resources.getDrawable(images[position]))
            }
            // After you finish setting up, show the CarouselView
            show()
        }
    }
}