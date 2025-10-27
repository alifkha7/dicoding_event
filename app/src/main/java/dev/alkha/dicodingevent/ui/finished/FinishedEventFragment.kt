package dev.alkha.dicodingevent.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.alkha.dicodingevent.R
import dev.alkha.dicodingevent.data.Resource
import dev.alkha.dicodingevent.data.remote.response.EventResponse
import dev.alkha.dicodingevent.databinding.FragmentFinishedEventBinding
import dev.alkha.dicodingevent.ui.ViewModelFactory
import dev.alkha.dicodingevent.ui.event.EventAdapter
import dev.alkha.dicodingevent.ui.event.EventViewModel
import kotlinx.coroutines.launch

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
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
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter()
        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvent.adapter = adapter
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                lifecycleScope.launch {
                    if (searchView.text.isNotEmpty()) {
                        viewModel.searchEvents(searchView.text.toString()).collect {
                            handleUiState(it)
                        }
                    } else {
                        viewModel.getEvents(0).collect {
                            handleUiState(it)
                        }
                    }
                }
                false
            }
        }

        lifecycleScope.launch {
            viewModel.getEvents(0).collect {
                handleUiState(it)
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
                if (uiState.data.listEvents.isEmpty()) {
                    showError(true)
                    binding.tvError.text = getString(R.string.empty_data)
                }
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
