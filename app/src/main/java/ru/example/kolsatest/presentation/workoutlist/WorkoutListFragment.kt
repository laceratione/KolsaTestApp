package ru.example.kolsatest.presentation.workoutlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.example.kolsatest.R
import ru.example.kolsatest.databinding.FragmentWorkoutListBinding
import ru.example.kolsatest.domain.model.Workout
import ru.example.kolsatest.domain.model.WorkoutType

private const val TAG = "WorkoutListFragment"

@AndroidEntryPoint
class WorkoutListFragment : Fragment() {
    private val workoutListVM: WorkoutListViewModel by viewModels()
    private var binding: FragmentWorkoutListBinding? = null
    private var adapter: WorkoutAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchEditText()
        setupRecyclerView()
        observeWorkouts()
    }

    private fun setupSearchEditText() {
        binding?.apply {
            etSearch.setOnEditorActionListener { view, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val textSearch = etSearch.text.toString()
                    if (textSearch.isNotEmpty()) {
                        workoutListVM.search(textSearch)
                        hideKeyboard(etSearch)
                    } else
                        showToast(getString(R.string.empty_input_search))
                    true
                } else {
                    false
                }
            }
            textInputLayoutSearch.setEndIconOnClickListener {
                etSearch.setText("")
                etSearch.clearFocus()
                workoutListVM.clearTextSearch()
            }

            btnFilter.setOnClickListener { showFiltersDialog() }
        }
    }

    private fun hideKeyboard(etSearch: TextInputEditText) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    private fun setupRecyclerView() {
        adapter = WorkoutAdapter(onItemClick = { workout ->
            val action =
                WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutDetailsFragment(
                    workout
                )
            findNavController().navigate(action)
        })
        binding?.rvWorkout?.adapter = adapter
    }

    private fun observeWorkouts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                workoutListVM.workouts.collect { state ->
                    when (state) {
                        is WorkoutState.Success -> showWorkouts(state.workouts)
                        is WorkoutState.Error -> showError(state.message)
                        is WorkoutState.Loading -> showLoading()
                        is WorkoutState.Empty -> showEmptyData()
                    }
                }
            }
        }
    }

    private fun showFiltersDialog() {
        val workoutTypes = WorkoutType.entries
        val items = workoutTypes.map { context?.getString(it.idRes) }.toTypedArray()
        val defaultType = workoutListVM.currentFilter?.let { workoutTypes.indexOf(it) } ?: -1

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.filters_dialog_title))
            .setSingleChoiceItems(items, defaultType) { dialog, which ->
                val selectedType = workoutTypes[which]
                workoutListVM.filtering(selectedType)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setNeutralButton(getString(R.string.reset), { dilog, _ ->
                workoutListVM.resetFilters()
                dilog.dismiss()
            })
            .show()
    }

    private fun showWorkouts(workouts: List<Workout>) {
        binding?.progressBar?.visibility = View.GONE
        adapter?.submitList(workouts)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String?) {
        binding?.progressBar?.visibility = View.GONE
        message?.let { Log.e(TAG, "Workouts list load error: $it") }
        showToast(getString(R.string.error_load_msg))
    }

    private fun showLoading() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    private fun showEmptyData() {
        adapter?.submitList(emptyList())
        showToast(getString(R.string.no_results_search))
    }

    companion object {
        fun newInstance() = WorkoutListFragment()
    }
}