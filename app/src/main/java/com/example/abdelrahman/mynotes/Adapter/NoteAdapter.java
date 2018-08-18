package com.example.abdelrahman.mynotes.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abdelrahman.mynotes.Data.SQLite.NoteContract;
import com.example.abdelrahman.mynotes.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private Cursor cursor;
    private String LOG_TAG = NoteAdapter.class.getSimpleName();

    public NoteAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        Log.i(LOG_TAG, "test");
        return new NoteViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if (!cursor.moveToPosition(position))
            return; // bail if returned null

        long id = cursor.getLong(cursor.getColumnIndex(NoteContract.NoteEntry._ID));

        String noteTitle = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.NOTE_COLUMN_TITLE));
        String noteContent = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.NOTE_COLUMN_NOTE));
        holder.title.setText(noteTitle);
        holder.content.setText(noteContent);
        holder.itemView.setTag(id);
    }

    /**
     * Swaps the Cursor currently held in the adapter with a new one
     * and triggers a UI refresh
     *
     * @param newCursor the new cursor that will replace the existing one
     */
    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView title, content;

        private NoteViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title_tv);
            content = itemView.findViewById(R.id.note_content_tv);
        }
    }
}
