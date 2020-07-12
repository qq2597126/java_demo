package lock.lock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;


public class SyncLock implements Lock {

    /**
     * 当前获取锁的线程
     */
    private volatile AtomicReference<Thread> lockThread = new AtomicReference();
    /**
     * 等待队列
     */
    private volatile LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<Thread>();

    public void lock() {

        boolean addQ = true;
        while (!tryLock()){
            if(addQ){
                waiters.add(Thread.currentThread());
                addQ=false;
            }
            LockSupport.park();
        }
        waiters.remove(Thread.currentThread());
    }


    public void lockInterruptibly() throws InterruptedException {

    }


    public boolean tryLock() {
        return lockThread.compareAndSet(null,Thread.currentThread());
    }


    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    public void unlock() {
        if(lockThread.compareAndSet(Thread.currentThread(),null)){
            Thread  firstThread  = waiters.peek();
            LockSupport.unpark(firstThread);
        }
    }


    public Condition newCondition() {
        return null;
    }
}
