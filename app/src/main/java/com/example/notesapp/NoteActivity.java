package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {
    private EditText ed_title, ed_description;
    private NumberPicker priority_np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        ed_title = findViewById(R.id.edit_text_title);
        ed_description = findViewById(R.id.edit_text_description);
        priority_np = findViewById(R.id.number_picker_priority);

        priority_np.setMinValue(1);
        priority_np.setMaxValue(10);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        Intent intent = getIntent();
        if (intent.hasExtra("extra_id"))
        {
            setTitle("Edit Note");
            ed_title.setText(intent.getStringExtra("extra_title"));
            ed_description.setText(intent.getStringExtra("extra_desc"));
            priority_np.setValue(intent.getIntExtra("extra_priority",1));
        }

        else
            setTitle("Add Note");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_note_item:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void saveNote() {
        String title = ed_title.getText().toString();
        String description = ed_description.getText().toString();
        int priority = priority_np.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty())
        {
            Toast.makeText(this,"Enter Title and Description",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra("extra_title",title);
        data.putExtra("extra_desc",description);
        data.putExtra("extra_priority",priority);

        int id = getIntent().getIntExtra("extra_id",-1);
        if (id!=-1)
        {
            data.putExtra("extra_id",id);
        }
        setResult(RESULT_OK,data);
        finish();
    }
}