package com.example.map;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ScheduleJob {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void GetMapService(Context context) {
        JobScheduler js =
                (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo job = new JobInfo.Builder(
                0,
                new ComponentName(context, GetMapService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        assert js != null;
        js.schedule(job);
    }
}
