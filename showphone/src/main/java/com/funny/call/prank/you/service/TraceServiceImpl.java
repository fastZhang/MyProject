package com.funny.call.prank.you.service;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.xdandroid.hellodaemon.AbsWorkService;
import com.funny.call.prank.you.FakeRingerActivity;
import com.funny.call.prank.you.utils.CallSettingUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

import static com.funny.call.prank.you.ControlActivity.DURATION;
import static com.funny.call.prank.you.ControlActivity.HANA_UP_AFTER;

public class TraceServiceImpl extends AbsWorkService {

    Handler mHandler = new Handler();
    int modeTimes;

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
//        System.out.println("检查磁盘中是否有上次销毁时保存的数据");
//        if (sDisposable != null)
//            sDisposable.dispose();
//
////        sDisposable = Observable
////                .interval(3, TimeUnit.SECONDS)
////                //取消任务时取消定时唤醒
////                .doOnDispose(() -> {
////
////                    System.out.println("保存数据到磁盘。");
////                    cancelJobAlarmSub();
////
////                })
////                .subscribe(count -> {
////
////
////                    System.out.println("每 3 秒采集一次数据... count = " + count);
////                    if (count > 0 && count % 18 == 0)
////                        System.out.println("保存数据到磁盘。 saveCount = " + (count / 18 - 1));
////
////
////                });
////
//        sDisposable = Observable.never().retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
//            @Override
//            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
//
//                return Observable.just(throwable).delay(5, TimeUnit.SECONDS);
//            }
//        });
        if (intent == null) return;

        String contactImage = intent.getStringExtra("contactImage");
        String number = intent.getStringExtra("number");
        String name = intent.getStringExtra("name");

        String voice = intent.getStringExtra("voice");
        String ringUri = intent.getStringExtra("ringUri");
        String mode = intent.getStringExtra("mode");

        modeTimes = intent.getIntExtra("modeTimes", 0);


        String hangUpAfter = Integer.toString(HANA_UP_AFTER);
        String duration = Integer.toString(DURATION);

        int time = intent.getIntExtra("time", Integer.parseInt("3"));
        intent.getIntExtra("duration", Integer.parseInt(duration));
        intent.getIntExtra("hangUpAfter", Integer.parseInt(hangUpAfter));

        if (modeTimes == 0) return;

        mHandler.removeCallbacks(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {



                Intent intent = new Intent(getApplication(), CallSettingUtil.getSetThemeClass(getApplication()));

                intent.putExtra("contactImage", contactImage);
                intent.putExtra("number", number);
                intent.putExtra("name", name);

                intent.putExtra("voice", voice);
                intent.putExtra("ringUri", ringUri);
                intent.putExtra("mode", mode);


                intent.putExtra("modeTimes", modeTimes);


                intent.putExtra("duration", Integer.parseInt(duration));
                intent.putExtra("hangUpAfter", Integer.parseInt(hangUpAfter));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                modeTimes = 0;


            }
        }, time * 1000);


    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return false;
//        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
//        System.out.println("保存数据到磁盘。");
    }
}
