package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int Edit_NOTE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_button);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter noteAdapter = new NoteAdapter(new NoteAdapter.NoteDiff());
        recyclerView.setAdapter(noteAdapter);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update recycler view here
                noteAdapter.submitList(notes);
            }
        } );

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(noteAdapter.getNotes(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this,"Note deleted",Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerView);

        noteAdapter.setOnItemClickListener( new NoteAdapter.OnMyItemClickListener() {
            @Override
            public void onItemClick(Note note) {

                openAlertBox(note);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NoteActivity.class);
                startActivityForResult(intent,ADD_NOTE_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            String title = data.getStringExtra("extra_title");
            String description = data.getStringExtra("extra_desc");
            int priority = data.getIntExtra("extra_priority",1);

            Note note = new Note(title,description,priority);
            noteViewModel.insert(note);

            Toast.makeText(this,"Note has been saved",Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == Edit_NOTE_REQUEST && resultCode == RESULT_OK){
            int id = data.getIntExtra("extra_id",-1);
            if (id==-1)
            {
                Toast.makeText(this,"Note has not been updated",Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra("extra_title");
            String description = data.getStringExtra("extra_desc");
            int priority = data.getIntExtra("extra_priority",1);

            Note note = new Note(title,description,priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this,"Note has been updated",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,"Note not saved",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_all_notes:
                noteViewModel.deleteAll();
                Toast.makeText(this,"All Note has been deleted",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openAlertBox(Note note)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Enter PIN");
        builder1.setCancelable(true);
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder1.setView(input);

        builder1.setPositiveButton(
                "Verify",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sp = getSharedPreferences("credential",MODE_PRIVATE);
                        //to check if there is any key named "username" in the file if yes then setting data in to edit box
                        if (sp.contains("pin"))
                        {
                            if (input.getText().toString().equals(sp.getString("pin","")))
                            {
                                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                                intent.putExtra("extra_id",note.getId());
                                intent.putExtra("extra_title",note.getTitle());
                                intent.putExtra("extra_desc",note.getDescription());
                                intent.putExtra("extra_priority",note.getPriority());
                                startActivityForResult(intent,Edit_NOTE_REQUEST);
                            }
                        }
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Register",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sp = getSharedPreferences("credential",MODE_PRIVATE); //create file in private mode
                        SharedPreferences.Editor editor = sp.edit(); //because we are going to edit this file
                        editor.putString("pin",input.getText().toString()); //saving into file under a key
                        editor.commit(); //to save the information
                        editor.apply();
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}