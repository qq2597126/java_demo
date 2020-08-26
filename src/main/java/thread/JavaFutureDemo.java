package thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/25.
 */
public class JavaFutureDemo {
    public static  final int SLEEP_GAP = 500;

    public static String getCurThreadName(){
        return Thread.currentThread().getName();
    }
    static class HotWarterJob implements Callable<Boolean>{

        @Override
        public Boolean call() throws Exception {
            try {
                System.out.println("洗好水壶");
                System.out.println("灌上凉水");
                System.out.println("放在火上");
                //线程睡眠一段时间，代表烧水中
                Thread.sleep(SLEEP_GAP);
                System.out.println("水开了");
            } catch (InterruptedException e) {
                System.out.println(" 发生异常被中断.");
                return false;
            }
            System.out.println(" 运行结束.");
            return true;
        }
    }

    static class WashJob implements Callable<Boolean>{

        @Override
        public Boolean call() throws Exception {
            try {
                System.out.println("洗茶壶");
                System.out.println("洗茶杯");
                System.out.println("拿茶叶");
                //线程睡眠一段时间，代表清洗中
                Thread.sleep(SLEEP_GAP);
                System.out.println("洗完了");
            } catch (InterruptedException e) {
                System.out.println(" 清洗工作发生异常被中断.");
                return false;
            }
            System.out.println(" 清洗工作运行结束.");
            return true;
        }
    }
    public static void drinkTea(boolean warterOk, boolean cupOk) {
        if (warterOk&&cupOk) {
            System.out.println("泡茶喝");
        } else if (!warterOk) {
            System.out.println("烧水失败，没有茶喝了");
        } else if (!cupOk) {
            System.out.println("杯子洗不了，没有茶喝了");
        }
    }
    public static void main(String args[]) {
        Callable<Boolean>hJob = new HotWarterJob();
        FutureTask<Boolean> hTask = new FutureTask<>(hJob);
        Thread hThread = new Thread(hTask, "** 烧水-Thread");
        Callable<Boolean> wJob = new WashJob();
        FutureTask<Boolean>wTask = new FutureTask<>(wJob);
        Thread wThread = new Thread(wTask, "$$ 清洗-Thread");
        hThread.start();
        wThread.start();
        Thread.currentThread().setName("主线程");
        try {
            boolean warterOk = hTask.get();
            boolean cupOk = wTask.get();
            drinkTea(warterOk, cupOk);
        } catch (InterruptedException e) {
            System.out.println(getCurThreadName() + "发生异常被中断.");
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(getCurThreadName() + " 运行结束.");
    }
}
