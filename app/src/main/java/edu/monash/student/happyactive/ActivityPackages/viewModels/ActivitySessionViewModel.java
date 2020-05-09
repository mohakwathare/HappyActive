package edu.monash.student.happyactive.ActivityPackages.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.monash.student.happyactive.ActivityPackages.PackageSessionManager;
import edu.monash.student.happyactive.ActivityPackages.Repositories.ActivityPackageRepository;
import edu.monash.student.happyactive.ActivityPackages.Repositories.ActivitySessionRepository;
import edu.monash.student.happyactive.data.enumerations.ActivityPackageStatus;
import edu.monash.student.happyactive.data.entities.ActivitySession;
import edu.monash.student.happyactive.data.entities.InteractivePrompt;
import edu.monash.student.happyactive.data.entities.Task;
import edu.monash.student.happyactive.data.relationships.ActivityPackageWithTasks;

public class ActivitySessionViewModel  extends AndroidViewModel {
    private ActivitySessionRepository activitySessionRepository;
    private ActivityPackageRepository activityPackageRepository;
    private ActivitySession activitySession;
    private List<InteractivePrompt> interactivePrompts;

    PackageSessionManager sessionManager;

    public ActivitySessionViewModel(@NonNull Application application, long activityPackageId) {
        super(application);
        activityPackageRepository = new ActivityPackageRepository(application);
        activitySessionRepository = new ActivitySessionRepository(application);
        activitySession = new ActivitySession(activityPackageId, 0, ActivityPackageStatus.STARTED);
        activitySession.setStepCount(0);
        interactivePrompts = new ArrayList<>();
        sessionManager = new PackageSessionManager(activityPackageId );
    }

    public void saveSessionAfterActivityIsCompleted(){
        activitySession.status = ActivityPackageStatus.COMPLETED;
        activitySession.completedDateTime = new Date();
        activitySession.currentTaskId = sessionManager.getLastTaskId();
        activitySessionRepository.updateSession(activitySession);
        activitySessionRepository.savePromptResults(interactivePrompts);
    }

    public LiveData<ActivityPackageWithTasks> getTasksOf(long packageId){
        return activityPackageRepository.getActivityWithTasks(packageId);
    }

    public Task completeCurrentTask() {
        Task nextTask = sessionManager.completeTaskInProgress();
        activitySession.currentTaskId = nextTask.id;
        activitySessionRepository.updateSession(activitySession);
        return nextTask;
    }

    public Task getTaskOnDisplay() {
        return sessionManager.getCurrentTaskOnDisplay();
    }

    public void setTaskInSessionManger(List<Task> tasksList) {
        sessionManager.setTasks(tasksList);
    }

    public boolean isActivityCompleted() {
        return sessionManager.isCompleted();
    }

    public void initSession(long activityPackageId) throws ExecutionException, InterruptedException {
        activitySession = new ActivitySession(activityPackageId, 0, ActivityPackageStatus.STARTED);
        activitySession.id = activitySessionRepository.insertNewSession(activitySession);
    }

    public void cancelSession() {
        activitySession.currentTaskId = sessionManager.completeTaskInProgress().id;
        activitySession.status = ActivityPackageStatus.CANCELED;
        activitySession.completedDateTime = new Date();
        activitySessionRepository.cancelSession(activitySession);
    }

    public void updateSteps(int numberOfSteps) {
        activitySession.stepCount = numberOfSteps;
    }

    public long getSessionId() {
        return activitySession.id;
    }

    public List<InteractivePrompt> getSessionWithPrompts() {
        if (interactivePrompts == null) {
            interactivePrompts = new ArrayList<>();
        }
        return interactivePrompts;
    }

    public ActivitySession checkInProgress(long activityId) throws ExecutionException, InterruptedException {
        return activitySessionRepository.checkInProgress(activityId);
    }

    public void setTaskInSessionManger(List<Task> tasksList, long currentTaskId) {
        sessionManager.setTasks(tasksList, currentTaskId);
    }

    public ActivitySession getActivitySession() {
        return activitySession;
    }

    public void setActivitySession(ActivitySession activitySession) {
        this.activitySession = activitySession;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application application;
        private final long activityPackage;

        public Factory(@NonNull Application application, long activityPackage){
            this.application = application;
            this.activityPackage = activityPackage;
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ActivitySessionViewModel(application, activityPackage);
        }
    }
}
