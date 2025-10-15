package dev.alkha.dicodingevent.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.alkha.dicodingevent.R
import dev.alkha.dicodingevent.databinding.FragmentFinishedEventBinding
import dev.alkha.dicodingevent.ui.EventAdapter
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.launch

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FinishedEventViewModel
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

        viewModel = ViewModelProvider(this)[FinishedEventViewModel::class.java]

        adapter = EventAdapter()
        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvent.adapter = adapter
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                if (searchView.text.isNotEmpty()) {
                    viewModel.searchEvents(searchView.text.toString())
                } else {
                    viewModel.getFinishedEvents()
                }
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> {
                        showLoading(true)
                        showError(false)
                    }

                    is UiState.Success -> {
                        showLoading(false)
                        showError(false)
                        adapter.submitList(uiState.data)
                        if (uiState.data.isEmpty()) {
                            showError(true)
                            binding.tvError.text = getString(R.string.empty_data)
                        }
                    }

                    is UiState.Error -> {
                        showLoading(false)
                        showError(true)
                    }
                }
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