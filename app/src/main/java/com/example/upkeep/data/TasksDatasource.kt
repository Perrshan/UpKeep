package com.example.upkeep.data

import com.example.upkeep.task.Task
import com.example.upkeep.R

class TasksDatasource() {
    fun loadTasks(): List<Task> {
        return listOf<Task>(
            Task(R.string.laundry, R.string.laundry_desc, R.string.daily, R.drawable.laundry),
            Task(R.string.sweep, R.string.sweep_desc, R.string.daily, R.drawable.sweep),
            Task(R.string.dishes, R.string.dishes_desc, R.string.daily, R.drawable.dishes),
            Task(R.string.washing_counters, R.string.washing_counters_desc, R.string.daily, R.drawable.counters),

            Task(R.string.vacuum, R.string.vacuum_desc, R.string.weekly, R.drawable.vacuum),
            Task(R.string.bed_sheets, R.string.bed_sheets_desc, R.string.weekly, R.drawable.bed_sheets),
            Task(R.string.mop, R.string.mop_desc, R.string.weekly, R.drawable.mop),
            Task(R.string.mirrors, R.string.mirrors_desc, R.string.weekly, R.drawable.mirrors),
            Task(R.string.microwave, R.string.microwave_desc, R.string.weekly, R.drawable.microwave),

            Task(R.string.oven, R.string.oven_desc, R.string.monthly, R.drawable.oven),
            Task(R.string.car_lights, R.string.car_lights_desc, R.string.monthly, R.drawable.car_lights),

        )
    }
}