package com.example.faceapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faceapp.R
import com.example.faceapp.data.Profile
import com.example.faceapp.utils.EmptyDataObserver
import com.example.faceapp.viewmodel.ProfileViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() , deleteListener{

    private lateinit var viewModel: ProfileViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : ProfileAdapter
    private lateinit var addButton : FloatingActionButton
    private lateinit var deleteAllButton : FloatingActionButton
    private lateinit var empty : View
    var countIs:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        adapter = ProfileAdapter(this,this)

        recyclerView = findViewById(R.id.rV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        // Performance optimizations
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.setDrawingCacheEnabled(true)
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)

        //observing our empty data state
        empty = findViewById(R.id.empty_data_parent)
        val emptyDataObserver = EmptyDataObserver(recyclerView, empty)
        adapter.registerAdapterDataObserver(emptyDataObserver)

        //updating data in UI as we get any submission in our database
        viewModel.allUser.observe(this) { profiles ->
            profiles?.let { 
                adapter.updateList(it)
                countIs = adapter.itemCount
                if(countIs == 0) {
                    Toast.makeText(this,"No Items",Toast.LENGTH_SHORT).show()
                }
            }
        }

        //For swipe Delete
        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val user = adapter.getUserPosition(position)
                viewModel.delete(user)
                Snackbar.make(recyclerView,"Profile Deleted"+ user.name,2000).setAction("UNDO"){
                    viewModel.insert(user)
                }.show()
            }
        }).attachToRecyclerView(recyclerView)


        deleteAllButton = findViewById(R.id.deleteButton)
        deleteAllButton.setOnClickListener{
            viewModel.deleteAll()
        }

        addButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
            // Don't finish() here - let the user return to this activity
        }
    }
    override fun onItemClicked(user: Profile) {
        viewModel.delete(user)
        Toast.makeText(this,"Profile Deleted", Toast.LENGTH_LONG).show()
    }
}
