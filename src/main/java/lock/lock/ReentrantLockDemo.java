package lock.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    /**
     *独享锁，支持公平和非公平两种模式，可重入
     */
    public static Lock lock = new ReentrantLock();

    public void addLock(){
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+"-获取锁");
            try {
                //addLock2();
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally {
            lock.unlock();
            lock.unlock();
            System.out.println(Thread.currentThread().getName()+"-释放锁");
        }
    }

    public void  addLock2(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+"-获取锁");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            new ReentrantLockDemo().addLock();
        }).start();

        Thread.sleep(200);
        lock.lock();
        System.out.println(Thread.currentThread().getName()+"-获取锁");
    }

}
