package dev.alkha.dicodingevent.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.alkha.dicodingevent.databinding.FragmentFavoriteBinding
import dev.alkha.dicodingevent.ui.ViewModelFactory
import dev.alkha.dicodingevent.ui.detail.DetailEventActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteEventAdapter: FavoriteEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        favoriteEventAdapter = FavoriteEventAdapter {
            val intent = Intent(requireActivity(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_ID, it.id)
            startActivity(intent)
        }

        binding.rvFavoriteEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoriteEventAdapter
        }

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) {
            favoriteEventAdapter.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}