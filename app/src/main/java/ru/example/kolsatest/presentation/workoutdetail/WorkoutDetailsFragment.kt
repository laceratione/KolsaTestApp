package ru.example.kolsatest.presentation.workoutdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.example.kolsatest.R
import ru.example.kolsatest.databinding.FragmentWorkoutDetailsBinding
import ru.example.kolsatest.di.NetworkModule
import ru.example.kolsatest.domain.model.Video
import ru.example.kolsatest.domain.model.WorkoutType

private const val TAG = "WorkoutDetailsFragment"

@AndroidEntryPoint
class WorkoutDetailsFragment : Fragment() {
    private val workoutDetailsVM: WorkoutDetailsViewModel by viewModels()
    private val args: WorkoutDetailsFragmentArgs by navArgs()
    private var binding: FragmentWorkoutDetailsBinding? = null

    private var playOrPauseBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutDetailsVM.setWorkout(args.workout)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        playOrPauseBtn = binding?.playerView?.findViewById(com.google.android.exoplayer2.ui.R.id.exo_play_pause)
        playOrPauseBtn?.setOnClickListener { view ->
            workoutDetailsVM.playOrPause()
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workoutDetailsVM.initVideoPlayer(requireContext().applicationContext)
        workoutDetailsVM.loadVideoByWorkoutId()
        setupView()
        setupPlayer()
        observeVideo()
    }

    private fun setupView() {
        try {
            binding?.apply {
                workoutDetailsVM.workout?.let {
                    tvTitle.setText(it.title)
                    tvDuration.setText(it.duration)

                    val workoutType = WorkoutType.fromId(it.type)
                    val nameType = workoutType?.idRes?.let { context?.getString(it) }
                    tvType.setText(nameType)
                    tvDescription.setText(it.description ?: getString(R.string.no_description))
                }

                speedButton.setOnClickListener { showSpeedSelectionDialog() }
                qualityButton.setOnClickListener { showQualitySelectionDialog() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupView", e)
        }
    }

    private fun setupPlayer() {
        binding?.playerView?.player = workoutDetailsVM.getPlayer()
    }

    private fun observeVideo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                workoutDetailsVM.video.collect { state ->
                    when (state) {
                        is VideoState.Success -> showVideo(state.video)
                        is VideoState.Error -> showError(state.message)
                        is VideoState.Loading -> {}
                    }
                }
            }
        }
    }

    private fun showVideo(video: Video) {
        if (workoutDetailsVM.currentUrl.isEmpty()) {
            val url = NetworkModule.BASE_URL + video.link.drop(1)
            workoutDetailsVM.setVideoUrl(url)
        }
    }

    private fun showError(message: String?) {
        message?.let { Log.e(TAG, "Video data load error: $it") }
        showToast(getString(R.string.error_load_msg))
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSpeedSelectionDialog() {
        val speeds = resources.getStringArray(R.array.speeds)
        val speedValues = workoutDetailsVM.speedsValue

        val currentSpeed = workoutDetailsVM.getCurrentSpeed()
        var checkedItem = speedValues.indexOfFirst { it == currentSpeed }
        if (checkedItem == -1) checkedItem = 2

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.speed_dialog_title))
            .setSingleChoiceItems(speeds, checkedItem) { dialog, which ->
                workoutDetailsVM.setPlaybackSpeed(speedValues[which])
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showQualitySelectionDialog() {
        val player = workoutDetailsVM.getPlayer()

        val qualityDialogBuilder = player?.let {
            TrackSelectionDialogBuilder(
                requireContext(),
                getString(R.string.quality_dialog_title),
                it,
                C.TRACK_TYPE_VIDEO,
            )
        }

        qualityDialogBuilder
            ?.setTrackNameProvider { format ->
                when {
                    format.height > 0 -> "${format.height}p"
                    format.bitrate > 0 -> "${format.bitrate / 1000} kbps"
                    else -> "Unknown"
                }
            }
            ?.build()
            ?.show()
            ?: Log.w(TAG, "qualityDialogBuilder null")
    }

    override fun onResume() {
        super.onResume()
        workoutDetailsVM.play()
    }

    override fun onStop() {
        super.onStop()
        if (!requireActivity().isChangingConfigurations)
            workoutDetailsVM.pauseOnHideApp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.playerView?.player = null
        binding = null
    }
}