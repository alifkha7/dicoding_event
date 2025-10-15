package dev.alkha.dicodingevent.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.alkha.dicodingevent.data.Resource
import dev.alkha.dicodingevent.data.remote.response.EventResponse
import dev.alkha.dicodingevent.databinding.FragmentHomeBinding
import dev.alkha.dicodingevent.ui.ViewModelFactory
import dev.alkha.dicodingevent.ui.event.EventAdapter
import dev.alkha.dicodingevent.ui.event.EventViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
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
        lifecycleScope.launch {
            viewModel.getEvents(1).collect { uiState ->
                handleUiState(uiState, true)
            }
        }

        lifecycleScope.launch {
            viewModel.getEvents(0).collect { uiState ->
                handleUiState(uiState, false)
            }
        }
    }

    private fun handleUiState(uiState: Resource<EventResponse>, isUpcoming: Boolean) {
        when (uiState) {
            is Resource.Loading -> {
                showLoading(true)
                showError(false)
            }

            is Resource.Success -> {
                showLoading(false)
                showError(false)
                if (isUpcoming) {
                    binding.tvUpcomingEventsTitle.visibility = View.VISIBLE
                    upcomingEventAdapter.submitList(uiState.data.listEvents.take(5))
                } else {
                    binding.tvFinishedEventsTitle.visibility = View.VISIBLE
                    finishedEventAdapter.submitList(uiState.data.listEvents.take(5))
                }
            }

            is Resource.Error -> {
                showLoading(false)
                showError(true)
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