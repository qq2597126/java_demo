package lock.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author lcy
 * @DESC:   信号量 用于 Java限流处理,不可重入。
 * @date 2020/7/10.
 */
public class SemaphoreDemo {


    public void service(String name,Semaphore semaphore) throws InterruptedException {
        System.out.println(name+"-业务执行开始");
        semaphore.acquire();
        Thread.sleep(30000);
        semaphore.release();
        System.out.println(name+"-执行结束");
    }

    public static void main(String[] args) throws InterruptedException {
        SemaphoreDemo semaphoreDemo = new SemaphoreDemo();

        Semaphore semaphore = new Semaphore(5);

        int num = 5;

        for (int x=0;x<num;x++){
            String vipName = x+"";
            new Thread(()->{
                try {
                    semaphore.acquire();

                    semaphoreDemo.service(vipName,semaphore);

                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }).start();
        }
        Thread.sleep(1000);
        System.out.println(semaphore.getQueueLength());


    }
}
