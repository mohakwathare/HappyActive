package edu.monash.student.happyactive.Reports.PostActivityStats;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import edu.monash.student.happyactive.Reports.ReportsRepositories.PostActivityStatsRepository;
import edu.monash.student.happyactive.data.entities.ActivitySession;

/**
 * View Model class for Post Activity Stats Screen.
 */
public class PostActivityStatsViewModel extends AndroidViewModel {

    private PostActivityStatsRepository postActivityStatsRepository;

    public PostActivityStatsViewModel(@NonNull Application application) {
        super(application);
        postActivityStatsRepository = new PostActivityStatsRepository(application);
    }

    /**
     * Method for fetching current activity.
     * @param currentId
     * @return
     */
    public LiveData<ActivitySession> getDataForCurrentSession(Integer currentId) {
        return postActivityStatsRepository.getDataForCurrentSession(currentId);
    }

    /**
     * Method for updating the activity session status to completed.
     * @param activitySession
     */
    public void setStatusCompletedPostActivity(ActivitySession activitySession) {
        postActivityStatsRepository.setStatusCompletedPostActivity(activitySession);
    }

    public void updateUserScore(ActivitySession currentActivitySession) {
        postActivityStatsRepository.updateUserScore(currentActivitySession);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application application;

        public Factory(@NonNull Application application){
            this.application = application;
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PostActivityStatsViewModel(application);
        }
    }
}
