package dev.alkha.dicodingevent.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.alkha.dicodingevent.data.Resource
import dev.alkha.dicodingevent.data.remote.response.EventResponse
import dev.alkha.dicodingevent.databinding.FragmentUpcomingEventBinding
import dev.alkha.dicodingevent.ui.ViewModelFactory
import dev.alkha.dicodingevent.ui.event.EventAdapter
import dev.alkha.dicodingevent.ui.event.EventViewModel
import kotlinx.coroutines.launch

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter()
        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvent.adapter = adapter

        lifecycleScope.launch {
            viewModel.getEvents(1).collect { uiState ->
                handleUiState(uiState)
            }
        }
    }

    private fun handleUiState(uiState: Resource<EventResponse>) {
        when (uiState) {
            is Resource.Loading -> {
                showLoading(true)
                showError(false)
            }

            is Resource.Success -> {
                showLoading(false)
                showError(false)
                adapter.submitList(uiState.data.listEvents)
            }

            is Resource.Error -> {
                showLoading(false)
                showError(true)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: Boolean) {
        binding.tvError.visibility = if (isError) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}