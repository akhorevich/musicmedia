package nortti.ru.musicmedia;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AudioViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private AudioClickListner mListener;
    public DatabaseReference mAudioRef;
    public ValueEventListener mValueEventListener;

    private TextView mName;
    private TextView mDescription;

    public AudioViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mName = (TextView)itemView.findViewById(R.id.list_item_entry_title);
        mDescription = (TextView)itemView.findViewById(R.id.list_item_entry_summary);


    }

    public void setText(final String text){
        mName.setText(text);
    }

    public void setDescription(final String text){
        mDescription.setText(text);
    }

    public void setAudioClickListener(AudioClickListner listener) {
        mListener = listener;
    }

    public interface AudioClickListner {


    }
}
