package com.example.abdelrahman.mynotes.Data.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abdelrahman.mynotes.Data.Models.Note;

import static com.example.abdelrahman.mynotes.Data.SQLite.NoteContract.NoteEntry.CREATE_NOTES_TABLE;
import static com.example.abdelrahman.mynotes.Data.SQLite.NoteContract.NoteEntry.NOTE_COLUMN_NOTE;
import static com.example.abdelrahman.mynotes.Data.SQLite.NoteContract.NoteEntry.NOTE_COLUMN_TITLE;
import static com.example.abdelrahman.mynotes.Data.SQLite.NoteContract.NoteEntry.NOTE_TABLE_NAME;

public class NoteHelper extends SQLiteOpenHelper {

    // Data Base Name.
    private static final String DATABASE_NAME = "Notes";
    // NoteContract version.
    private static final int DATABASE_VERSION = 1;


    public NoteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + String.valueOf(CREATE_NOTES_TABLE));
        onCreate(sqLiteDatabase);// reCreate Notes Database
    }

    public long addNote(Note note){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_COLUMN_TITLE,note.getTitle());
        contentValues.put(NOTE_COLUMN_NOTE,note.getContent());

        long i = sqLiteDatabase.insert(NOTE_TABLE_NAME,null,contentValues);

        sqLiteDatabase.close();
        return i;
    }

    public void updateNote(Note note,int id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTE_COLUMN_TITLE,note.getTitle());
        contentValues.put(NOTE_COLUMN_NOTE,note.getContent());

        sqLiteDatabase.update(NOTE_TABLE_NAME,
                contentValues,
                NoteContract.NoteEntry._ID + "=" + id,
                null);
    }

    public Note getNote(int id){
        String select = "SELECT * FROM pets WHERE​ _id = " + id;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(select,null);
        String noteTilte = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_TITLE));
        String noteContent = cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_NOTE));
        sqLiteDatabase.close();
        cursor.close();
        return new Note(noteTilte,noteContent);
    }

    public Cursor getAllNotes(){
        SQLiteDatabase sqLiteDatabase  = this.getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + NoteContract.NoteEntry.NOTE_TABLE_NAME;
//       Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
//       sqLiteDatabase.close();
//       return cursor;
        return sqLiteDatabase.query(
                NOTE_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public void deleteNote(long id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(NOTE_TABLE_NAME, NoteContract.NoteEntry._ID + "=" + id,null);
        sqLiteDatabase.close();
    }

}
