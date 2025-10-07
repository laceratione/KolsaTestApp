package ru.example.kolsatest.presentation.workoutlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.example.kolsatest.R
import ru.example.kolsatest.domain.model.Workout
import ru.example.kolsatest.domain.model.WorkoutType

private const val TAG = "WorkoutAdapter"

class WorkoutAdapter(
    private val onItemClick: (Workout) -> Unit,
): ListAdapter<Workout, WorkoutAdapter.WorkoutHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutAdapter.WorkoutHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_workout, parent, false)
        return WorkoutHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutAdapter.WorkoutHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class WorkoutHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val context = itemView.context
        private val titleTextView: TextView = view.findViewById(R.id.tv_title)
        private val descriptionTextView: TextView = view.findViewById(R.id.tv_description)
        private val typeTextView: TextView = view.findViewById(R.id.tv_type)
        private val durationTextView: TextView = view.findViewById(R.id.tv_duration)

        fun bind(workout: Workout) {
            try {
                titleTextView.setText(workout.title)
                descriptionTextView.setText(
                    workout.description ?: context.getString(R.string.no_description)
                )

                val workoutType = WorkoutType.fromId(workout.type)
                val nameType = workoutType?.idRes?.let { context.getString(it) }
                typeTextView.setText(nameType)

                val dur = workout.duration.toIntOrNull() ?: workout.duration
                val textDur = if (dur is Int) context.getString(R.string.duration_min, dur) else dur.toString()
                durationTextView.setText(textDur)

                itemView.setOnClickListener {
                    onItemClick(workout)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in bind", e)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem == newItem
        }
    }
}