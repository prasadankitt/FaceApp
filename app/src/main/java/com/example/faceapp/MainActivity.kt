package com.example.faceapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.OnSwipe
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        viewModel = ProfileViewModel(applicationContext)
        adapter = ProfileAdapter(this,this)

        recyclerView = findViewById(R.id.rV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //observing our empty data state
        empty = findViewById(R.id.empty_data_parent)
        val emptyDataObserver = EmptyDataObserver(recyclerView, empty)
        adapter.registerAdapterDataObserver(emptyDataObserver)

        //updating data in UI as we get any submission in our database
        viewModel.allUser.observe(this, Observer{ it ->
            it?.let{adapter.updateList(it) }
        })
        //if we don't have any profile added
        viewModel.allUser.observe(this, Observer{ it ->
            it?.let{countIs = adapter.getItemCount()
                if(countIs == 0)
                {
                    Toast.makeText(this,"No Items",Toast.LENGTH_SHORT).show()
                }}
        })

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
                    adapter.allUser.add(position,user)
                    viewModel.insert(user)
                    adapter.notifyItemInserted(position)
                }.show()
            }
        }).attachToRecyclerView(recyclerView)


        deleteAllButton = findViewById(R.id.deleteButton)
        deleteAllButton.setOnClickListener{
            viewModel.deleteAll()
        }

        addButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    override fun onContentChanged() {
//        super.onContentChanged()
//
//        empty = findViewById(R.id.empty)
//        recyclerView.
//
//    }

    override fun onItemClicked(user: Profile) {
        viewModel.delete(user)
        Toast.makeText(this,"Profile Deleted", Toast.LENGTH_LONG).show()
    }

}
