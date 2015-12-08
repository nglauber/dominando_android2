package dominando.android.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int CONTADOR = 1;
    private static final int ACABOU = 2;
    TextView mTextView;
    Button mButton;
    MeuHandler mHandler;
    MinhaThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MeuHandler();
        mTextView = (TextView)findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread = new MinhaThread();
                mThread.start();
                mButton.setEnabled(false);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agendarAlarme();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agendarJob();
            }
        });
    }

    private void agendarJob() {
        ComponentName componentName = new ComponentName(this, MeuJob.class);
        JobInfo uploadTask = new JobInfo.Builder(1, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(TimeUnit.SECONDS.toMillis(10))
                .build();
        JobScheduler jobScheduler = (JobScheduler)
                getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(uploadTask);

    }

    private void agendarAlarme(){
        TimePickerDialog.OnTimeSetListener tratador =
                new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Intent it = new Intent(MainActivity.this, AlarmeReceiver.class);

                PendingIntent pit = PendingIntent.getBroadcast(
                        MainActivity.this, 0, it, 0);

                AlarmManager alarmManager = (AlarmManager)
                        getSystemService(Context.ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pit);
            }
        };
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                tratador,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        if (mThread != null) mThread.interrupt();
    }

    class MeuHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CONTADOR){
                mTextView.setText("Contador: " + msg.arg1);

            } else if (msg.what == ACABOU){
                mTextView.setText("Acabou!");
                mButton.setEnabled(true);
            }
        }
    }

    class MinhaThread extends Thread {
        @Override
        public void run() {
            super.run();
            int graus = 0;
            while (graus < 10){
                graus ++;
                Message message = new Message();
                message.what = CONTADOR;
                message.arg1 = graus;
                mHandler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mHandler.sendEmptyMessage(ACABOU);
        }
    }
}
