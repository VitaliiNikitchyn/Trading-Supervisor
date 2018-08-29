package com.tradingsupervisor.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class LocationViewModel extends AndroidViewModel {

    private LocationLiveData location;

    //extends AndroidViewModel; if Context needed, it can be acquired: getApplication()
    public LocationViewModel(@NonNull Application application) {
        super(application);

        location = new LocationLiveData(getApplication());
    }

    public LocationLiveData getLocation() {
        return location;
    }
}
