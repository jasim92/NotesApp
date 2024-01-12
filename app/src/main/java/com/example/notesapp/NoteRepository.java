package com.example.notesapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;

    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getAllNotes()
    {
        return allNotes;
    }

    public void insert(Note note)
    {
        NoteDatabase.noteExecutors.execute(() ->{
            noteDao.insert(note);
        });
    }

    public void update(Note note)
    {
        NoteDatabase.noteExecutors.execute(() ->{
            noteDao.update(note);
        });
    }

    public void delete(Note note)
    {
        NoteDatabase.noteExecutors.execute(() ->{
            noteDao.delete(note);
        });
    }

    public void deleteAll()
    {
        NoteDatabase.noteExecutors.execute(() ->{
            noteDao.deleteAllNotes();
        });
    }
}
