package dev.alkha.dicodingevent.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.alkha.dicodingevent.ui.EventAdapter
import dev.alkha.dicodingevent.databinding.FragmentUpcomingEventBinding
import dev.alkha.dicodingevent.ui.UiState
import kotlinx.coroutines.launch

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UpcomingEventViewModel
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

        viewModel = ViewModelProvider(this)[UpcomingEventViewModel::class.java]

        adapter = EventAdapter()
        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvent.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                when (it) {
                    is UiState.Loading -> {
                        showLoading(true)
                        showError(false)
                    }
                    is UiState.Success -> {
                        showLoading(false)
                        showError(false)
                        adapter.submitList(it.data)
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