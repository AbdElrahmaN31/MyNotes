package com.example.abdelrahman.mynotes;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.abdelrahman.mynotes.Adapter.NoteAdapter;
import com.example.abdelrahman.mynotes.Data.Models.Note;
import com.example.abdelrahman.mynotes.Data.SQLite.NoteHelper;
import com.example.abdelrahman.mynotes.Utils.NoteRecyclerTouchListener;
import com.fourhcode.forhutils.FUtilsValidation;


public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    RecyclerView noteRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    NoteAdapter noteAdapter;
    NoteHelper noteDBHelper;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteDBHelper = new NoteHelper(this);
        sqLiteDatabase = noteDBHelper.getReadableDatabase();
        Cursor cursor = noteDBHelper.getAllNotes();
        noteAdapter = new NoteAdapter(this, cursor);

        noteRecyclerView = findViewById(R.id.note_rv);
        layoutManager = new LinearLayoutManager(this);
        noteRecyclerView.setLayoutManager(layoutManager);
        noteRecyclerView.setAdapter(noteAdapter);


        FloatingActionButton fab = findViewById(R.id.add_note_bt);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(true, -1);
            }
        });

        noteRecyclerView.addOnItemTouchListener(new NoteRecyclerTouchListener(this, noteRecyclerView, new
                NoteRecyclerTouchListener.ClickListener() {
                    @Override
                    public void onLongClick(View child, int childPosition) {
                        showActionsDialog(childPosition);
                    }

                    @Override
                    public void onClick(View child, int childPosition) {
                    }
                }));

        //ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
        //An item touch helper to handle swiping items off the list
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Get the viewHolder's itemView's tag and store in a long variable id
                //Get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                // RemoveNote and pass through that id
                //remove from DB
                noteDBHelper.deleteNote(id);
                // COMPLETED (10) call swapCursor on mAdapter passing in getAllGuests() as the argument
                //update the list
                noteAdapter.swapCursor(noteDBHelper.getAllNotes());
            }

            // Attach the ItemTouchHelper to the noteRecyclerView
        }).attachToRecyclerView(noteRecyclerView);
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, position);
                } else {
                    noteDBHelper.deleteNote(position);
                }
            }
        });
        builder.show();
    }

    private void showNoteDialog(final boolean newNote, final int position) {
        // Put not_dialog layout in View.
        View view = LayoutInflater.from(this).inflate(R.layout.note_dialog, null);
        final EditText title_et = view.findViewById(R.id.note_title_et);
        final EditText content_et = view.findViewById(R.id.note_content_et);
        // Initialize dialog.
        AlertDialog.Builder noteDialogBuilder = new AlertDialog.Builder(this);
        noteDialogBuilder.setView(view);// Set not_dialog layout as a dialog's view
        // Set title to the dialog
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(newNote ? getString(R.string.new_note_title) : getString(R.string.edit_note_title));
        //If the dialog for Update get the note data to update it.
        if (!newNote) {
            Note note = noteDBHelper.getNote(position);
            title_et.setText(note.getTitle());
            content_et.setText(note.getContent());
        }

        noteDialogBuilder.setCancelable(false)
                .setPositiveButton(newNote ? "Save" : "Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = noteDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print Error message if title_ed or content_ed is empty.
                FUtilsValidation.isEmpty(title_et, "Enter Title!");
                FUtilsValidation.isEmpty(content_et, "Note is Empty!");

                if (!TextUtils.isEmpty(title_et.getText().toString()) ||
                        !TextUtils.isEmpty(content_et.getText().toString())) {

                    String noteTitle = title_et.getText().toString();
                    String noteContent = content_et.getText().toString();

                    Note note = new Note(noteTitle, noteContent);
                    if (newNote) {
                        long i = noteDBHelper.addNote(note);
                        Log.i(LOG_TAG,String.valueOf(i));
                    }
                    if (!newNote) noteDBHelper.updateNote(note, position);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
