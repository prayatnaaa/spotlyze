package com.bangkit.spotlyze.ui.skincare.favourite

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bangkit.spotlyze.helper.customView.BoundEdgeEffect
import com.bangkit.spotlyze.ui.SkincareViewModelFactory
import com.bangkit.spotlyze.ui.home.HomeAdapter
import com.bangkit.spotlyze.ui.skincare.SkincareViewModel
import com.prayatna.spotlyze.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private var adapter: HomeAdapter? = null
    private val viewModel: SkincareViewModel by viewModels {
        SkincareViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAdapter()
    }

        private fun setupAdapter() {
            adapter = HomeAdapter()
            val layoutManager = GridLayoutManager(this, 2 )
            binding.recyclerView.adapter = adapter
            binding.recyclerView.edgeEffectFactory = BoundEdgeEffect(this)
            binding.recyclerView.layoutManager = layoutManager
        }

    private fun setupViewModel() {
        viewModel.getFavoriteSkincare().observe(this) { data ->
            adapter?.submitList(data)
        }
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }
}