package com.bangkit.spotlyze.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.spotlyze.helper.Message
import com.bangkit.spotlyze.ui.UserViewModelFactory
import com.prayatna.spotlyze.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        UserViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupAction()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.getUser().observe(requireActivity()) { user ->
            binding.tvUserName.text = user.username
            binding.tvEmail.text = String.format(user.id.toString())
        }
    }

    private fun setupAction() {
        goToDetailProfile()
        logout()
    }

    private fun logout() {
        binding.btnLogout.setOnClickListener {
            Message.alertDialog(
                requireContext(),
                "Logout",
                "Are you sure want to logout?"
            ) {
                viewModel.logOut()
            }
        }
    }

    private fun goToDetailProfile() {
        binding.btnProfileDetail.setOnClickListener {
            val intent = Intent(requireContext(), DetailProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        binding.actionBar.btnSearch.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}