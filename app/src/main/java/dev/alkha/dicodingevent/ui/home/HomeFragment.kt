package dev.alkha.dicodingevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.alkha.dicodingevent.databinding.FragmentHomeBinding
import dev.alkha.dicodingevent.ui.EventAdapter
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var upcomingEventAdapter: UpcomingEventAdapter
    private lateinit var finishedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        upcomingEventAdapter = UpcomingEventAdapter()
        binding.rvUpcomingEvents.adapter = upcomingEventAdapter

        finishedEventAdapter = EventAdapter()
        binding.rvFinishedEvents.adapter = finishedEventAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.upcomingEventsState.collect {
                when (it) {
                    is UiState.Loading -> {
                        showLoading(true)
                        showError(false)
                    }

                    is UiState.Success -> {
                        showLoading(false)
                        showError(false)
                        binding.tvUpcomingEventsTitle.visibility = View.VISIBLE
                        upcomingEventAdapter.submitList(it.data)
                    }

                    is UiState.Error -> {
                        showLoading(false)
                        showError(true)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.finishedEventsState.collect {
                when (it) {
                    is UiState.Loading -> {
                        showLoading(true)
                        showError(false)
                    }

                    is UiState.Success -> {
                        showLoading(false)
                        showError(false)
                        binding.tvFinishedEventsTitle.visibility = View.VISIBLE
                        finishedEventAdapter.submitList(it.data)
                    }

                    is UiState.Error -> {
                        showLoading(false)
                        showError(true)
                    }
                }
            }
        }
    }

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun showError(isError: Boolean) {
        binding.tvError.visibility = if (isError) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}