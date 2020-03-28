package pl.edu.agh.sm.picalculator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.jetbrains.annotations.NotNull;

public class LogicService extends Service {
    private final IBinder mBinder = new LocalBinder();

    @NotNull
    public String multiply(double first, double second) {
        return String.valueOf(first * second);
    }

    @NotNull
    public String add(double first, double second) {
        return String.valueOf(first + second);
    }

    @NotNull
    public String subtract(double first, double second) {
        return String.valueOf(first - second);
    }

    @NotNull
    public String divide(double first, double second) {
        return String.valueOf(first / second);
    }

    public class LocalBinder extends Binder {
        LogicService getService() {
            return LogicService.this;
        }
    }
    public LogicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
