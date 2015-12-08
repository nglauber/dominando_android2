package dominando.android.handler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

public class MeuJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(this, "Job executado!", Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
