package android.saeefmd.notepad.View;

import android.content.Intent;
import android.saeefmd.notepad.Database.DatabaseHelper;
import android.saeefmd.notepad.Database.Model.Note;
import android.saeefmd.notepad.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private EditText titleEt;
    private EditText noteEt;

    private DatabaseHelper databaseHelper;

    private long noteId;
    private Note mNote;

    private int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        titleEt = findViewById(R.id.title_et);
        noteEt = findViewById(R.id.note_et);

        databaseHelper = new DatabaseHelper(EditorActivity.this);

        Intent intent = getIntent();
        noteId = intent.getLongExtra("noteId", 0);
        adapterPosition = intent.getIntExtra("adapterPosition", -1);

        Log.i("noteId: ", String.valueOf(noteId));

        if (noteId != 0 && adapterPosition != -1) {

            mNote = databaseHelper.getNote(noteId);

            titleEt.setText(String.valueOf(mNote.getTitle()));
            noteEt.setText(String.valueOf(mNote.getNote()));
        }
    }

    private void createNote (String title, String note) {

        // Inserting title and note into table
        long id = databaseHelper.insertNote(title, note);

        Intent intent = new Intent();
        intent.putExtra(getString(R.string.note_id), id);
        intent.putExtra(getString(R.string.new_note_flag), true);
        setResult(1001, intent);
        finish();
    }

    private void updateNote(String title, String note) {

        mNote.setTitle(title);
        mNote.setNote(note);

        // updating note in db
        databaseHelper.updateNote(mNote);

        Intent intent = new Intent();
        intent.putExtra(getString(R.string.update_note_flag), true);
        setResult(1002, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_save:
                if (noteEt.getText().toString().equals("")) {
                    Toast.makeText(this, "Insert Note!", Toast.LENGTH_SHORT).show();
                    break;
                } else {

                    if (titleEt.getText().toString().equals("")) {
                        titleEt.setText("Untitled");
                    }
                }

                if (noteId == 0 && adapterPosition == -1) {
                    createNote(titleEt.getText().toString(), noteEt.getText().toString());
                } else {
                    updateNote(titleEt.getText().toString(), noteEt.getText().toString());
                }

                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
