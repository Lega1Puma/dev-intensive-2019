package ru.skillbranch.devintensive.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity: AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        initViews()
        initViewModel()

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun initViews() {
        TODO("Not yet implemented")
    }

    private fun initToolbar() {
        TODO("Not yet implemented")
    }

}