package android.saeefmd.notepad.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.saeefmd.notepad.Adapter.NoteListAdapter;
import android.saeefmd.notepad.Database.DatabaseHelper;
import android.saeefmd.notepad.Database.Model.Note;
import android.saeefmd.notepad.R;
import android.saeefmd.notepad.Utilities.RecyclerTouchListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView noteListRv;
    private FloatingActionButton addNoteFab;
    private TextView noNoteTv;

    private NoteListAdapter noteListAdapter;
    private List<Note> notesList = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    private int adapterPosition;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteListRv = findViewById(R.id.note_list_rv);
        addNoteFab = findViewById(R.id.add_note_fab);
        noNoteTv = findViewById(R.id.no_note_tv);

        databaseHelper = new DatabaseHelper(this);

        noteListAdapter = new NoteListAdapter(this, notesList);
        noteListAdapter.notifyDataSetChanged();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        noteListRv.setLayoutManager(mLayoutManager);
        noteListRv.setItemAnimator(new DefaultItemAnimator());
        noteListRv.setAdapter(noteListAdapter);

        toggleEmptyNotes();

        notesList.addAll(databaseHelper.getAllNotes());

        addNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        noteListRv.addOnItemTouchListener(new RecyclerTouchListener(this, noteListRv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                adapterPosition = position;

                noteId = notesList.get(position).getId();

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("noteId", noteId);
                intent.putExtra("adapterPosition", position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteNote(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        }));
    }

    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (databaseHelper.getNotesCount() > 0) {
            noNoteTv.setVisibility(View.GONE);
        } else {
            noNoteTv.setVisibility(View.VISIBLE);
        }
    }

    private void deleteNote (int position) {

        // deleting the note from db
        databaseHelper.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        noteListAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();

    }

    private void updateAdapter(int position, long id) {

        Note note = databaseHelper.getNote(id);

        notesList.set(position, note);

        noteListAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    private void addNewNote (long id) {

        Note note = databaseHelper.getNote(id);

        notesList.add(0, note);

        noteListAdapter.notifyDataSetChanged();

        toggleEmptyNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreference = this
                .getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();

        boolean newNote = sharedPreference.getBoolean(getString(R.string.new_note_flag), false);
        boolean updateNote = sharedPreference.getBoolean(getString(R.string.update_note_flag), false);

        if (updateNote) {

            updateAdapter(adapterPosition, noteId);
            editor.putBoolean(getString(R.string.update_note_flag), false).apply();
        } else if (newNote){

            long id = sharedPreference.getLong(getString(R.string.note_id), 0);

            if (id != 0) {

                addNewNote(id);
                editor.putBoolean(getString(R.string.new_note_flag), false).apply();
            }
        } else {

            toggleEmptyNotes();
        }
    }
}
