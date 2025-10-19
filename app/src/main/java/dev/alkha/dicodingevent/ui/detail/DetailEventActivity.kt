package dev.alkha.dicodingevent.ui.detail

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dev.alkha.dicodingevent.R
import dev.alkha.dicodingevent.data.Resource
import dev.alkha.dicodingevent.data.local.entity.EventEntity
import dev.alkha.dicodingevent.data.remote.response.EventItem
import dev.alkha.dicodingevent.databinding.ActivityDetailEventBinding
import dev.alkha.dicodingevent.ui.ViewModelFactory
import dev.alkha.dicodingevent.utils.DateFormatter
import kotlinx.coroutines.launch

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getIntExtra(EXTRA_ID, -1)

        if (eventId != -1) {
            lifecycleScope.launch {
                viewModel.getDetailEvent(eventId).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> showLoading(true)
                        is Resource.Success -> {
                            showLoading(false)
                            val event = resource.data.event
                            populateEventDetails(event)
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            showError()
                        }
                    }
                }
            }
        }
    }

    private fun populateEventDetails(event: EventItem) {
        binding.apply {
            Glide.with(this@DetailEventActivity)
                .load(event.mediaCover)
                .into(ivEventImage)
            btnRegister.visibility = View.VISIBLE
            btnRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, event.link.toUri())
                startActivity(intent)
            }
            fabFavorite.visibility = View.VISIBLE
            viewModel.getEventById(event.id).observe(this@DetailEventActivity) { favoriteEvent ->
                val isFavorite = favoriteEvent != null
                setFavoriteState(isFavorite)
                fabFavorite.setOnClickListener {
                    if (isFavorite) {
                        viewModel.removeFavoriteEvent(favoriteEvent)
                    } else {
                        val newFavorite = EventEntity(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,
                            isFavorite = true
                        )
                        viewModel.setFavoriteEvent(newFavorite)
                    }
                }
            }
        }
        binding.detail.apply {
            tvEventName.text = event.name
            tvEventOrganizer.text = getString(R.string.organizer, event.ownerName)
            tvEventTime.text = DateFormatter.formatDate(event.beginTime)
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

    private fun setFavoriteState(isFavorite: Boolean) {
        if (isFavorite) {
            binding.fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_favorite)
            )
        } else {
            binding.fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_favorite_border)
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError() {
        binding.tvError.visibility = View.VISIBLE
    }
}