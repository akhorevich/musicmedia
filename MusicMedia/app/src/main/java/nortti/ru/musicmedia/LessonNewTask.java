package nortti.ru.musicmedia;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class LessonNewTask{
    private static final String TAG = "LessonTaskFragment";
    private Context mContext;


    public interface TaskCallbacks {
        void onPostUploaded(String error);
    }

    private TaskCallbacks mCallbacks;
    public LessonNewTask() {
    }

    public static LessonNewTask newInstance() {
        return new LessonNewTask();
    }

    public void uploadLesson(){

    }


    class UploadLessonTask extends AsyncTask<Void, Void, Void>{

        private String name;
        private String path;

        public UploadLessonTask(String inName, String inPath){
            name = inName;
            path = inPath;
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference audioRef = storage.getReferenceFromUrl("gs://musicmedia-f3b8d.appspot.com");

            StorageReference lessonRef = audioRef.child("audio").child(name + ".wav");
            ByteArrayOutputStream fullSizeStream = new ByteArrayOutputStream();
            byte[] bytes = fullSizeStream.toByteArray();
            lessonRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri Url = taskSnapshot.getDownloadUrl();

                    final DatabaseReference ref = FirebaseUtil.getBaseRef();
                    DatabaseReference audioRef = FirebaseUtil.getAudioRef();
                    final String newAudioKey = audioRef.push().getKey();

                    Audio newAudio = new Audio(name,path);
                    Map<String, Object> updateAudioData = new HashMap<String, Object>();
                    updateAudioData.put(FirebaseUtil.getAudioRef() + newAudioKey, new ObjectMapper().convertValue(newAudio,Map.class));
                    ref.updateChildren(updateAudioData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){
                                mCallbacks.onPostUploaded(null);
                            } else {
                                FirebaseCrash.report(databaseError.toException());
                                mCallbacks.onPostUploaded("Couldn't create upload task.");
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "Failed to upload post to database.");
                    FirebaseCrash.report(e);
                    mCallbacks.onPostUploaded(mContext.getString(
                            R.string.error_upload_task_create));
                }
            });

            return null;
        }
    }
}
