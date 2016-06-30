package nortti.ru.musicmedia;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nortti.ru.musicmedia.item.EntryAdapter;
import nortti.ru.musicmedia.item.EntryItem;
import nortti.ru.musicmedia.item.Item;
import nortti.ru.musicmedia.item.SectionItem;

public class MainActivity extends Fragment {
    public static final String TAG = "PostsFragment";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private static final String KEY_TYPE = "type";

    private int mRecyclerViewPosition = 0;
    private OnAudioSelectedListener mListener;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<AudioViewHolder> mAdapter;

    public MainActivity(){

    }

    public static MainActivity newInstance(int type){
        MainActivity activity = new MainActivity();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        activity.setArguments(args);
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
            // TODO: RecyclerView only restores position properly for some tabs.
        }

                Log.d(TAG, "Restoring recycler view position (following): " + mRecyclerViewPosition);

                FirebaseUtil.getAudioRef().addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot followedUserSnapshot, String s) {
                        String followedUserId = followedUserSnapshot.getKey();
                        String lastKey = "";
                        if (followedUserSnapshot.getValue() instanceof String) {
                            lastKey = followedUserSnapshot.getValue().toString();
                        }
                        Log.d(TAG, "followed user id: " + followedUserId);
                        Log.d(TAG, "last key: " + lastKey);
                        FirebaseUtil.getAudioRef()
                                .orderByKey().startAt(lastKey).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(final DataSnapshot postSnapshot, String s) {
                                HashMap<String, Object> addedPost = new HashMap<String, Object>();
                                addedPost.put(postSnapshot.getKey(), true);
                                FirebaseUtil.getAudioRef()
                                        .updateChildren(addedPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseUtil.getAudioRef()
                                                .setValue(postSnapshot.getKey());
                                    }
                                });
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FirebaseUtil.getAudioRef()
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final List<String> postPaths = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "adding post key: " + snapshot.getKey());
                                    postPaths.add(snapshot.getKey());
                                }
                                mAdapter = new FirebaseAudioQueryAdapter(postPaths,
                                        new FirebaseAudioQueryAdapter.OnSetupViewListener() {
                                            @Override
                                            public void onSetupView(AudioViewHolder holder, Audio audio, int position, String postKey) {
                                                setupAudio(holder, audio, position, postKey);
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

                            }
                        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private FirebaseRecyclerAdapter<Audio, AudioViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Audio, AudioViewHolder>(
                Audio.class, R.layout.item, AudioViewHolder.class, query) {
            @Override
            public void populateViewHolder(final AudioViewHolder audioViewHolder,
                                           final Audio audio, final int position) {
                setupAudio(audioViewHolder, audio, position, null);
            }

            @Override
            public void onViewRecycled(AudioViewHolder holder) {
                super.onViewRecycled(holder);
//                FirebaseUtil.getLikesRef().child(holder.mPostKey).removeEventListener(holder.mLikeListener);
            }
        };
    }

    private void setupAudio(final AudioViewHolder audioViewHolder, final Audio audio, final int position, final String inPostKey) {

        audioViewHolder.setText(audio.getName());
        audioViewHolder.setDescription(audio.getUrl());

        final String postKey;
        if (mAdapter instanceof FirebaseRecyclerAdapter) {
            postKey = ((FirebaseRecyclerAdapter) mAdapter).getRef(position).getKey();
        } else {
            postKey = inPostKey;
        }



        ValueEventListener likeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        audioViewHolder.setAudioClickListener(new AudioViewHolder.AudioClickListner() {
            public void showComments() {
                Log.d(TAG, "Comment position: " + position);
                mListener.onPostComment(postKey);
            }

            public void toggleLike() {
                Log.d(TAG, "Like position: " + position);
                mListener.onPostLike(postKey);
            }
        });
    }

    public interface OnAudioSelectedListener {
        void onPostComment(String postKey);
        void onPostLike(String postKey);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAudioSelectedListener) {
            mListener = (OnAudioSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
