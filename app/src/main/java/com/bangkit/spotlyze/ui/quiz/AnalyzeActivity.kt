package com.bangkit.spotlyze.ui.quiz

import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.spotlyze.data.source.RecommendationItem
import com.bangkit.spotlyze.data.source.Result
import com.bangkit.spotlyze.ui.SkinViewModelFactory
import com.bangkit.spotlyze.ui.adapter.RecommendationAdapter
import com.bangkit.spotlyze.utils.flattenRecommendations
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.prayatna.spotlyze.databinding.ActivityAnalyzeBinding
import com.prayatna.spotlyze.databinding.BottomSheetLayoutBinding

class AnalyzeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyzeBinding
    private val viewModel: QuizViewModel by viewModels {
        SkinViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyzeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
        setupAnimation()
    }

    private fun setupAnimation() {
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f)
        val animator = android.animation.ObjectAnimator.ofPropertyValuesHolder(
            binding.verifyImage,
            scaleX,
            scaleY
        ).apply {
            duration = 1000
            repeatCount = android.animation.ValueAnimator.INFINITE
            repeatMode = android.animation.ValueAnimator.REVERSE
        }
        animator.start()
    }

    private fun setupAction() {
        binding.btnAnalyze.setOnClickListener {
            classifySkin()
        }
    }

    private fun classifySkin() {
        val skinType = intent.getStringExtra(QuizActivity.SKIN_TYPE)
        val skinSensitivity = intent.getStringExtra(QuizActivity.SKIN_SENSITIVITY)
        val concerns = intent.getStringArrayListExtra(QuizActivity.CONCERN)
        val imageUri = intent.getStringExtra(QuizActivity.IMAGE_URI)
        if (skinType != null && skinSensitivity != null && concerns != null && imageUri != null) {
            viewModel.classifySkin(skinType, skinSensitivity, concerns, imageUri.toUri(), this)
        }
    }

    private fun setupViewModel() {
        viewModel.analyzeState.observe(this) { data ->
            when (data) {
                is Result.Error -> {
                    Log.e("analyzer", "error: ${data.error}")
                }

                Result.Loading -> {
                    binding.btnAnalyze.visibility = View.GONE
                }

                is Result.Success -> {
                    val recommend = flattenRecommendations(data.data.recommend!!)
                    Log.d("analyzer", "success: $recommend")
                    stopAnimation()
                    showResult(data.data.publicUrl)
                    binding.tvSkinType.text = data.data.predict
//                    showBottomSheetDialog(recommend)
                    setupAdapter(recommend)
                }
            }
        }
    }

    private fun showResult(publicUrl: String?) {
        Glide.with(binding.facePicture.context).load(publicUrl).into(binding.facePicture)
        binding.facePicture.visibility = View.VISIBLE
        binding.tvYeay.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.tvSkinType.visibility = View.VISIBLE
        showBackButton()
    }

    private fun showBackButton() {
        binding.toolBar.visibility = View.VISIBLE
        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAdapter(recommend: List<RecommendationItem>) {
        binding.recyclerView.visibility = View.VISIBLE
        val adapter = RecommendationAdapter(recommend)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AnalyzeActivity)
            this.adapter = adapter
        }
    }

    private fun stopAnimation() {
        binding.verifyImage.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                binding.verifyImage.visibility = View.GONE
            }

        binding.tvThanks.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                binding.tvThanks.visibility = View.GONE
            }
    }

    private fun showBottomSheetDialog(recommend: List<RecommendationItem>) {
        val dialog = BottomSheetDialog(this)
        val mBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(mBinding.root)
        val adapter = RecommendationAdapter(recommend)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AnalyzeActivity)
            this.adapter = adapter
        }
        dialog.show()
    }

    companion object {
        const val CLASSIFY_RESULT = "classify_result"
        const val EXTRA_RECOMMEND = "extra_recommend"
    }
}