package nortti.ru.musicmedia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAudioQueryAdapter extends RecyclerView.Adapter<AudioViewHolder> {

    private List<String> mAudioPaths;
    private OnSetupViewListener mOnSetupViewListener;

    public FirebaseAudioQueryAdapter(List<String> audioPaths, OnSetupViewListener onSetupViewListener) {
        if (audioPaths == null || audioPaths.isEmpty()){
            mAudioPaths = new ArrayList<>();
        } else {
            mAudioPaths = audioPaths;
        }
        mOnSetupViewListener = onSetupViewListener;
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new AudioViewHolder(v);
    }

    public void setAudioPaths(List<String> audioPaths){
        mAudioPaths = audioPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        mAudioPaths.add(path);
        notifyItemInserted(mAudioPaths.size());
    }

    @Override
    public void onBindViewHolder(final AudioViewHolder holder, int position) {
        DatabaseReference ref = FirebaseUtil.getAudioRef().child(mAudioPaths.get(position));
        ValueEventListener audioListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Audio audio = dataSnapshot.getValue(Audio.class);
                mOnSetupViewListener.onSetupView(holder, audio, holder.getAdapterPosition(), dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//TODO Errors
            }
        };
        ref.addValueEventListener(audioListener);
        holder.mAudioRef = ref;
    }

    public void onViewRecycled(AudioViewHolder holder){
        super.onViewRecycled(holder);
        holder.mAudioRef.removeEventListener(holder.mValueEventListener);
    }

    @Override
    public int getItemCount() {
        return mAudioPaths.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(AudioViewHolder holder, Audio audio, int position, String postKey);
    }
}
