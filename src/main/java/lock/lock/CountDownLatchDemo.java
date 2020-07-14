package lock.lock;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lcy
 * @DESC: 倒计数器，
 *          await: 方法等待计数器变为0之前，线程进入等待状态
 *          countdown : 计数器数值减一，直到为0
 *
 *          1.场景
 *              1.统计线程的执行情况
 *              2.压力测试中，使用countDownLatch实现最大线程的并发处理,
 *              3.多个线程之间相互通信，比如线程异步调用接口，结果等通知。
 * @date 2020/7/14.
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        //new CountdownLatchTest1().countdownLatch();
        new CountDownLatchTest2().countDownLatch();
    }
}

class CountdownLatchTest1{
    private ExecutorService executorService = new ThreadPoolExecutor(10,10,1000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));

    private CountDownLatch countDownLatch = new CountDownLatch(5);

    public static AtomicInteger count = new AtomicInteger(0);

    public void  countdownLatch() throws InterruptedException {
        for (int x= 0 ;x<6;x++) {
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName()+" 进入当前线程");
                CountdownLatchTest1.count.addAndGet(1);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName()+" 线程开始一起运行");
                System.out.println(countDownLatch.getCount());
            });
        }
        System.out.println("准备好的线程："+count);
        countDownLatch.await();
        System.out.println("集体运行");
        executorService.shutdown();
    }
}

class CountDownLatchTest2{
    private ExecutorService executorService = new ThreadPoolExecutor(10,10,1000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
    final CountDownLatch cdOrder = new CountDownLatch(1);
    final CountDownLatch cdAnswer = new CountDownLatch(4);
    public void countDownLatch(){

        for (int i = 0; i < 4; i++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("选手" + Thread.currentThread().getName() + "正在等待裁判发布口令");
                        cdOrder.await();
                        System.out.println("选手" + Thread.currentThread().getName() + "已接受裁判口令");
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("选手" + Thread.currentThread().getName() + "到达终点");
                        cdAnswer.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            executorService.execute(runnable);
        }
        try {
            Thread.sleep((long) (Math.random() * 10000));
            System.out.println("裁判"+Thread.currentThread().getName()+"即将发布口令");
            cdOrder.countDown();
            System.out.println("裁判"+Thread.currentThread().getName()+"已发送口令，正在等待所有选手到达终点");
            cdAnswer.await();
            System.out.println("所有选手都到达终点");
            System.out.println("裁判"+Thread.currentThread().getName()+"汇总成绩排名");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
