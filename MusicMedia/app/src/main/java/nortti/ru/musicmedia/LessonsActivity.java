package nortti.ru.musicmedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LessonsActivity extends AppCompatActivity implements MainActivity.OnAudioSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        LessonNewTask lessonNewTask = new LessonNewTask();
        lessonNewTask.uploadLesson("Lesson1","Colors");

    }
    @Override
    public void onPostComment(String postKey) {

    }

    @Override
    public void onPostLike(String postKey) {

    }
}
