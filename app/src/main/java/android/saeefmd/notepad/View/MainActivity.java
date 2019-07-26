package android.saeefmd.notepad.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.saeefmd.notepad.Adapter.NoteListAdapter;
import android.saeefmd.notepad.Database.DatabaseHelper;
import android.saeefmd.notepad.Database.Model.Note;
import android.saeefmd.notepad.R;
import android.saeefmd.notepad.Utilities.RecyclerTouchListener;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    private boolean newNote = false;
    private boolean updateNote = false;

    private long newNoteId;

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

        // Setting the recycler view to display notes
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
                startActivityForResult(intent, 1001);
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
                startActivityForResult(intent, 1002);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.main_menu_delete_all:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Delete all notes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteAllNotes();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
        }

        return super.onOptionsItemSelected(item);
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

    private void deleteAllNotes() {

        databaseHelper.deleteAllNotes();

        // Clear data from the notelist
        notesList.clear();
        noteListAdapter.notifyDataSetChanged();

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1001) {

            newNoteId = data.getLongExtra(getString(R.string.note_id), 0);
            newNote = data.getBooleanExtra(getString(R.string.new_note_flag), false);
        }

        if (resultCode == 1002) {

            updateNote = data.getBooleanExtra(getString(R.string.update_note_flag), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (updateNote) {

            updateAdapter(adapterPosition, noteId);
            updateNote = false;
        } else if (newNote){

            if (newNoteId != 0) {

                addNewNote(newNoteId);
                newNote = false;
            }
        } else {

            toggleEmptyNotes();
        }
    }
}
