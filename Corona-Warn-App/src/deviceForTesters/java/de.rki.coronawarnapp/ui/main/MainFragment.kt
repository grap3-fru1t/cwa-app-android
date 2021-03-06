package de.rki.coronawarnapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import de.rki.coronawarnapp.R
import de.rki.coronawarnapp.databinding.FragmentMainBinding
import de.rki.coronawarnapp.timer.TimerHelper
import de.rki.coronawarnapp.ui.doNavigate
import de.rki.coronawarnapp.ui.viewmodel.SettingsViewModel
import de.rki.coronawarnapp.ui.viewmodel.SubmissionViewModel
import de.rki.coronawarnapp.ui.viewmodel.TracingViewModel
import de.rki.coronawarnapp.util.ExternalActionHelper

/**
 * After the user has finished the onboarding this fragment will be the heart of the application.
 * Three ViewModels are needed that this fragment shows all relevant information to the user.
 * Also the Menu is set here.
 *
 * @see tracingViewModel
 * @see settingsViewModel
 * @see submissionViewModel
 * @see PopupMenu
 */
class MainFragment : Fragment() {

    companion object {
        private val TAG: String? = MainFragment::class.simpleName
    }

    private val tracingViewModel: TracingViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val submissionViewModel: SubmissionViewModel by activityViewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater)
        binding.tracingViewModel = tracingViewModel
        binding.settingsViewModel = settingsViewModel
        binding.submissionViewModel = submissionViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonOnClickListener()
        setContentDescription()
    }

    override fun onResume() {
        super.onResume()
        // refresh required data
        tracingViewModel.refreshRiskLevel()
        tracingViewModel.refreshExposureSummary()
        tracingViewModel.refreshLastTimeDiagnosisKeysFetchedDate()
        tracingViewModel.refreshIsTracingEnabled()
        tracingViewModel.refreshActiveTracingDaysInRetentionPeriod()
        TimerHelper.checkManualKeyRetrievalTimer()
        submissionViewModel.refreshDeviceUIState()
        tracingViewModel.refreshLastSuccessfullyCalculatedScore()
        binding.mainScrollview.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }

    override fun onStart() {
        super.onStart()
        binding.mainScrollview.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }

    private fun setContentDescription() {
        val shareButtonString: String = getString(R.string.button_share)
        val menuButtonString: String = getString(R.string.button_menu)
        val mainCardString: String = getString(R.string.hint_external_webpage)
        binding.mainHeaderShare.buttonIcon.contentDescription = shareButtonString
        binding.mainHeaderOptionsMenu.buttonIcon.contentDescription = menuButtonString
        binding.mainAbout.mainCard.contentDescription = mainCardString
    }

    private fun setButtonOnClickListener() {
        binding.mainTestUnregistered.submissionStatusCardUnregistered.setOnClickListener {
            toSubmissionIntro()
        }
        binding.mainTestUnregistered.submissionStatusCardUnregisteredButton.setOnClickListener {
            toSubmissionIntro()
        }
        binding.mainTestDone.submissionStatusCardDone.setOnClickListener {
            findNavController().doNavigate(
                MainFragmentDirections.actionMainFragmentToSubmissionDoneFragment()
            )
        }
        binding.mainTestResult.submissionStatusCardContent.setOnClickListener {
            toSubmissionResult()
        }
        binding.mainTestResult.submissionStatusCardContentButton.setOnClickListener {
            toSubmissionResult()
        }
        binding.mainTestPositive.submissionStatusCardPositive.setOnClickListener {
            toSubmissionResult()
        }
        binding.mainTestPositive.submissionStatusCardPositiveButton.setOnClickListener {
            toSubmissionResult()
        }
        binding.mainTracing.setOnClickListener {
            findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToSettingsTracingFragment())
        }
        binding.mainRisk.riskCard.setOnClickListener {
            findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToRiskDetailsFragment())
        }
        binding.mainRisk.riskCardButtonUpdate.setOnClickListener {
            tracingViewModel.refreshDiagnosisKeys()
            settingsViewModel.updateManualKeyRetrievalEnabled(false)
        }
        binding.mainRisk.riskCardButtonEnableTracing.setOnClickListener {
            findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToSettingsTracingFragment())
        }
        binding.mainAbout.mainCard.setOnClickListener {
            ExternalActionHelper.openUrl(this, requireContext().getString(R.string.main_about_link))
        }
        binding.mainHeaderShare.buttonIcon.setOnClickListener {
            findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToMainSharingFragment())
        }
        binding.mainHeaderOptionsMenu.buttonIcon.setOnClickListener {
            showPopup(it)
        }
    }

    private fun toSubmissionResult() {
        findNavController().doNavigate(
            MainFragmentDirections.actionMainFragmentToSubmissionResultFragment()
        )
    }

    private fun toSubmissionIntro() {
        findNavController().doNavigate(
            MainFragmentDirections.actionMainFragmentToSubmissionIntroFragment()
        )
    }

    private fun showPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.menu_main)
        popup.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.menu_help -> {
                    findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToMainOverviewFragment())
                    true
                }
                R.id.menu_information -> {
                    findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToInformationFragment())
                    true
                }
                R.id.menu_settings -> {
                    findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
                    true
                }
                R.id.menu_test_api -> {
                    findNavController().doNavigate(MainFragmentDirections.actionMainFragmentToTestForAPIFragment())
                    true
                }
                R.id.menu_test_risk_level -> {
                    findNavController().doNavigate(
                        MainFragmentDirections.actionMainFragmentToTestRiskLevelCalculation()
                    )
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
        popup.show()
    }
}
