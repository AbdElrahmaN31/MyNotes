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
                        long id = (long) noteRecyclerView.getChildViewHolder(child).itemView.getTag();
                        showActionsDialog((int)id);
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
//                noteAdapter.swapCursor(noteDBHelper.getAllNotes());
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
                    showNoteDialog(false, position);
                } else {
                    noteDBHelper.deleteNote((long) position);
                    noteAdapter.swapCursor(noteDBHelper.getAllNotes());
                }
            }
        });
        builder.show();
    }

    private void showNoteDialog(final boolean newNote, final int position) {
        // Put not_dialog layout in View.
        View view = LayoutInflater.from(this).inflate(R.layout.note_dialog, null);
        // Initialize dialog.
        AlertDialog.Builder noteDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        noteDialogBuilder.setView(view);// Set not_dialog layout as a dialog's view
        // Set title to the dialog
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!newNote ? getString(R.string.edit_note_title) : getString(R.string.new_note_title));

        final EditText title_et = view.findViewById(R.id.note_title_et);
        final EditText content_et = view.findViewById(R.id.note_content_et);
        //If the dialog for Update get the note data to update it.
        if (!newNote) {
            Note note = noteDBHelper.getNote(position);
            if (!(note == null)){
            String content = note.getContent();
            String title= note.getTitle();
            title_et.setText(title);
            content_et.setText(content);}
        }

        noteDialogBuilder
                .setCancelable(false)
                .setPositiveButton(!newNote ? "Update" : "Save", new DialogInterface.OnClickListener() {
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
        final AlertDialog alertDialog = noteDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print Error message if title_ed or content_ed is empty.
                FUtilsValidation.isEmpty(title_et, "Enter Title!");
                FUtilsValidation.isEmpty(content_et, "Note is Empty!");

                if (TextUtils.isEmpty(title_et.getText().toString()) ||
                        TextUtils.isEmpty(content_et.getText().toString())) {
                    return;
                }else {
                    alertDialog.dismiss();
                }

                    String noteTitle = title_et.getText().toString();
                    String noteContent = content_et.getText().toString();
                    Note note = new Note(noteTitle, noteContent);

                    if (newNote) {
                        long i = noteDBHelper.addNote(note);
                        noteAdapter.swapCursor(noteDBHelper.getAllNotes());
                        Log.i(LOG_TAG,String.valueOf(i));
                    }
                    if (!newNote) {
                        noteDBHelper.updateNote(note, position);
                        noteAdapter.swapCursor(noteDBHelper.getAllNotes());
                    }

                }
        });
    }



//    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
//        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);
//
//        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
//        alertDialogBuilderUserInput.setView(view);
//
//        final EditText inputNote = view.findViewById(R.id.note);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));
//
//        if (shouldUpdate && note != null) {
//            inputNote.setText(note.getNote());
//        }
//        alertDialogBuilderUserInput
//                .setCancelable(false)
//                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialogBox, int id) {
//
//                    }
//                })
//                .setNegativeButton("cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                dialogBox.cancel();
//                            }
//                        });
//
//        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
//        alertDialog.show();
//
//        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show toast message when no text is entered
//                if (TextUtils.isEmpty(inputNote.getText().toString())) {
//                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    alertDialog.dismiss();
//                }
//
//                // check if user updating note
//                if (shouldUpdate && note != null) {
//                    // update note by it's id
//                    updateNote(inputNote.getText().toString(), position);
//                } else {
//                    // create new note
//                    createNote(inputNote.getText().toString());
//                }
//            }
//        });
//    }


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
