package com.example.notesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ListAdapter<Note,NoteAdapter.NoteViewHolder> {
    private OnMyItemClickListener listener;

    protected NoteAdapter(@NonNull DiffUtil.ItemCallback<Note> diffCallback) {
        super(diffCallback);
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.tv_title.setText(currentNote.getTitle());
        holder.tv_description.setText(currentNote.getDescription());
        holder.tv_priority.setText(Integer.toString(currentNote.getPriority()));

    }

    public Note getNotes(int position)
    {
        return getItem(position);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title;
        private TextView tv_description;
        private TextView tv_priority;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.textView_title);
            tv_description = itemView.findViewById(R.id.textView_description);
            tv_priority = itemView.findViewById(R.id.textView_priority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener!=null && position!=RecyclerView.NO_POSITION)
                        listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnMyItemClickListener{
         void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnMyItemClickListener listener)
    {
        this.listener = listener;

    }

    static class NoteDiff extends DiffUtil.ItemCallback<Note>
    {

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getPriority()==newItem.getPriority();
        }
    }
}
