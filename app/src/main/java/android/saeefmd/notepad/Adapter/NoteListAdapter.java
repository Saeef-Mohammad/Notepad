package android.saeefmd.notepad.Adapter;

import android.content.Context;
import android.saeefmd.notepad.Database.Model.Note;
import android.saeefmd.notepad.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Note> noteList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView listTitleTv;
        TextView listdotTv;

        public MyViewHolder(View itemView) {
            super(itemView);

            listTitleTv = itemView.findViewById(R.id.list_title_tv);
            listdotTv = itemView.findViewById(R.id.list_dot_tv);
        }
    }

    public NoteListAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_list_item, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        Note note = noteList.get(i);

        ((MyViewHolder)viewHolder).listTitleTv.setText(note.getTitle());
        ((MyViewHolder)viewHolder).listdotTv.setText(Html.fromHtml("&#8226;"));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
