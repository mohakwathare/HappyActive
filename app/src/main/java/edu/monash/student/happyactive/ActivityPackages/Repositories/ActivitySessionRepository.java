package edu.monash.student.happyactive.ActivityPackages.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.monash.student.happyactive.data.ActivityPackageDatabase;
import edu.monash.student.happyactive.data.ActivitySessionDao;
import edu.monash.student.happyactive.data.entities.ActivitySession;
import edu.monash.student.happyactive.data.entities.InteractivePrompt;

public class ActivitySessionRepository {
    private ActivitySessionDao activitySessionDao;

    public ActivitySessionRepository(Application application) {
        ActivityPackageDatabase db = ActivityPackageDatabase.getDatabase(application);
        activitySessionDao = db.activitySessionDao();
    }

    public void updateSession(ActivitySession session) {
         new updateAsyncTask(activitySessionDao).execute(session);
    }

    public void cancelSession(ActivitySession session) {
        new cancelAsyncTask(activitySessionDao).execute(session);
    }

    public LiveData<List<ActivitySession>> getAllSessions() {
        return activitySessionDao.getAllSessionsRecords();
    }

    public long insertNewSession(ActivitySession session) throws ExecutionException, InterruptedException {
        return new insertAsyncTask(activitySessionDao).execute(session).get();
    }

    public ActivitySession findSessionByActivityId(long activityId, long id) {
        return activitySessionDao.findSessionByActivityId(activityId, id);
    }

    public void savePromptResults(List<InteractivePrompt> interactivePrompts) {
        new savePromptsAsyncTask(activitySessionDao).execute(interactivePrompts);
    }

    private static class updateAsyncTask extends AsyncTask<ActivitySession, Void, Void> {
        private ActivitySessionDao mSessionDao;

        public updateAsyncTask(ActivitySessionDao mSessionDao) {
            this.mSessionDao = mSessionDao;
        }

        @Override
        protected Void doInBackground(ActivitySession... activitySessions) {
             mSessionDao.updateSession(activitySessions[0]);
             return null;
        }

    }

    private static class savePromptsAsyncTask extends AsyncTask<List<InteractivePrompt>, Void, Void> {
        private ActivitySessionDao mSessionDao;

        public savePromptsAsyncTask(ActivitySessionDao mSessionDao) {
            this.mSessionDao = mSessionDao;
        }

        @Override
        protected Void doInBackground(List<InteractivePrompt>... lists) {
            mSessionDao.insertPrompts(lists[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<ActivitySession, Void, Long> {
        private ActivitySessionDao mSessionDao;

        public insertAsyncTask(ActivitySessionDao mSessionDao) {
            this.mSessionDao = mSessionDao;
        }

        @Override
        protected Long doInBackground(ActivitySession... activitySessions) {
            return mSessionDao.insertSession(activitySessions[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }
    }

    private static class cancelAsyncTask extends AsyncTask<ActivitySession, Void, Void> {
        private ActivitySessionDao mSessionDao;

        public cancelAsyncTask(ActivitySessionDao mSessionDao) {
            this.mSessionDao = mSessionDao;
        }

        @Override
        protected Void doInBackground(ActivitySession... activitySessions) {
            mSessionDao.cancelSession(activitySessions[0]);
            return null;
        }

    }
}
