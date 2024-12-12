package com.bangkit.spotlyze.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bangkit.spotlyze.data.local.database.entity.SkincareEntity
import com.bangkit.spotlyze.data.source.Result
import com.bangkit.spotlyze.data.source.SortType
import com.bangkit.spotlyze.helper.Message
import com.bangkit.spotlyze.helper.customView.BoundEdgeEffect
import com.bangkit.spotlyze.ui.SkincareViewModelFactory
import com.bangkit.spotlyze.ui.adapter.SkincareAdapter
import com.bangkit.spotlyze.ui.auth.login.LoginActivity
import com.bangkit.spotlyze.ui.skincare.SkincareActivity
import com.prayatna.spotlyze.R
import com.prayatna.spotlyze.databinding.FragmentHomeBinding
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapter: SkincareAdapter? = null

    private val viewModel: HomeViewModel by viewModels {
        SkincareViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var dataList: ArrayList<SkincareEntity>
    private lateinit var searchList: ArrayList<SkincareEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupViewModel()
        setupMenu()
    }

    private fun setupMenu() {
        binding.btnFilter.setOnClickListener {
            val intent = Intent(requireActivity(), SkincareActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showFilterMenu(view: View) {
        val popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_sort, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menu ->
            handleFilterSelection(menu)
            popupMenu.dismiss()
            true
        }
        popupMenu.show()
    }

    private fun handleFilterSelection(menu: MenuItem) {
        when (menu.itemId) {
            R.id.sort_random -> {
                viewModel.changeSortType(SortType.RANDOM)
            }

            R.id.sort_desc -> {
                viewModel.changeSortType(SortType.DESCENDING)
            }

            R.id.sort_asc -> {
                viewModel.changeSortType(SortType.ASCENDING)
            }
        }
    }

    private fun setupSearchView(data: List<SkincareEntity>) {
        searchList = ArrayList(data)
        dataList = ArrayList(data)

        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {

                if (newText.isNullOrEmpty()) {
                    binding.recyclerView.scrollToPosition(0)
                }

                searchList.clear()
                val searchText = newText?.lowercase(Locale.getDefault()) ?: ""
                if (searchText.isNotEmpty()) {
                    dataList.forEach {
                        if (it.name?.lowercase(Locale.getDefault())?.contains(searchText) == true) {
                            searchList.add(it)
                        }
                    }
                } else {
                    searchList.addAll(dataList)
                }

                if (searchList.isNotEmpty()) {
                    adapter?.submitList(ArrayList(searchList))
                } else {
                    adapter?.submitList(emptyList())
                }
                return false
            }
        })
    }


    private fun setupViewModel() {
        viewModel.getAllSkincare().observe(viewLifecycleOwner) { data ->
            when (data) {
                is Result.Loading -> {
                    Log.d("okhttp", "loading")
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Message.offlineDialog(requireActivity())
                    binding.progressBar.visibility = View.GONE
                    if (data.error == "Invalid token") {
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    Message.toast(requireActivity(), data.error)
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val skincare = data.data.take(10)
                    setupSearchView(skincare)
                    adapter?.submitList(skincare)
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = SkincareAdapter()
        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.edgeEffectFactory = BoundEdgeEffect(requireActivity())
        binding.recyclerView.layoutManager = layoutManager
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }
}
