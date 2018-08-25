package com.example.abdelrahman.mynotes.Data.SQLite;

import android.provider.BaseColumns;

public class NoteContract {

    public final class NoteEntry implements BaseColumns {
        // Notes Table.
        public static final String NOTE_TABLE_NAME = "NotesTable";
        // Note Title.
        public static final String NOTE_COLUMN_TITLE = "Title";
        // Note Content.
        public static final String NOTE_COLUMN_NOTE = "Note";
        // Note Type.
        public static final String NOTE_COLUMN_TYPE = "Type";
        // Note Time.
        public static final String NOTE_COLUMN_TIME = "Time";

        // Create Note table.
        public static final String CREATE_NOTES_TABLE = "CREATE TABLE " + NOTE_TABLE_NAME + "(" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NOTE_COLUMN_TITLE + " TEXT," +
                NOTE_COLUMN_NOTE + " TEXT NOT NULL," +
                NOTE_COLUMN_TYPE + " TEXT," +
                NOTE_COLUMN_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";

    }
}
