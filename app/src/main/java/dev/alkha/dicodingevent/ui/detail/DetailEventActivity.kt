package dev.alkha.dicodingevent.ui.detail

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dev.alkha.dicodingevent.R
import dev.alkha.dicodingevent.data.remote.response.EventItem
import dev.alkha.dicodingevent.databinding.ActivityDetailEventBinding
import dev.alkha.dicodingevent.ui.UiState
import dev.alkha.dicodingevent.utils.DateFormatter.formatDate
import kotlinx.coroutines.launch

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private lateinit var viewModel: DetailEventViewModel

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[DetailEventViewModel::class.java]

        val eventId = intent.getIntExtra(EXTRA_ID, -1)
        if (eventId != -1) {
            viewModel.getDetailEvent(eventId)
        }

        lifecycleScope.launch {
            viewModel.uiState.collect {
                when (it) {
                    is UiState.Loading -> {
                        showLoading(true)
                        showError(false)
                    }

                    is UiState.Success -> {
                        showLoading(false)
                        showError(false)
                        populateEventDetails(it.data)
                    }

                    is UiState.Error -> {
                        showLoading(false)
                        showError(true)
                    }
                }
            }
        }
    }

    private fun populateEventDetails(event: EventItem) {
        binding.apply {
            val imageUrl = event.mediaCover
            Glide.with(this@DetailEventActivity)
                .load(imageUrl)
                .into(ivEventImage)
            btnRegister.visibility = View.VISIBLE
            btnRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, event.link.toUri())
                startActivity(intent)
            }
        }
        binding.detail.apply {
            tvEventName.text = event.name
            tvEventOrganizer.text = getString(R.string.organizer, event.ownerName)
            tvEventTime.text = formatDate(event.beginTime)
            val remainingQuota = event.quota - event.registrants
            val resources: Resources = this@DetailEventActivity.resources
            tvEventQuota.text = resources.getQuantityString(
                R.plurals.quota_remaining,
                remainingQuota,
                remainingQuota
            )
            tvEventDescription.text =
                HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(isError: Boolean) {
        binding.tvError.visibility = if (isError) View.VISIBLE else View.GONE
    }
}