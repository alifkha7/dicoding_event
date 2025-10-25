package dev.alkha.dicodingevent.ui.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import dev.alkha.dicodingevent.R
import dev.alkha.dicodingevent.databinding.FragmentSettingBinding
import dev.alkha.dicodingevent.ui.ViewModelFactory
import dev.alkha.dicodingevent.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableDailyReminder()
        } else {
            binding.switchDailyReminder.isChecked = false
            Snackbar.make(
                binding.root,
                getString(R.string.notification_permission_is_required_for_this_feature),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                viewModel.saveThemeSetting(isChecked)
            }
        }

        viewModel.getDailyReminderSetting()
            .observe(viewLifecycleOwner) { isDailyReminderActive: Boolean ->
                binding.switchDailyReminder.isChecked = isDailyReminderActive
            }

        binding.switchDailyReminder.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) {
                return@setOnCheckedChangeListener
            }

            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    enableDailyReminder()
                }
            } else {
                disableDailyReminder()
            }
        }
    }

    private fun enableDailyReminder() {
        viewModel.saveDailyReminderSetting(true)
        val workManager = WorkManager.getInstance(requireContext())
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS).build()
        workManager.enqueueUniquePeriodicWork(
            DailyReminderWorker.WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        Snackbar.make(
            binding.root,
            getString(R.string.daily_reminder_is_active), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun disableDailyReminder() {
        viewModel.saveDailyReminderSetting(false)
        WorkManager.getInstance(requireContext()).cancelUniqueWork(DailyReminderWorker.WORK_TAG)
        Snackbar.make(
            binding.root,
            getString(R.string.daily_reminder_is_not_active), Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}