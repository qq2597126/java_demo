package lock.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 共享锁
 * 1.
 */
public class ReentrantReadWriteLockDemo {
    private  final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private   final Map<String,String> dataMap =  new HashMap<String,String>();

    public String get(String key){
        readLock.lock();
        try {
            Thread.sleep(1000);
             return dataMap.get(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public Set<String> getKeys(){
        readLock.lock();
        try{
            return  dataMap.keySet();
        }finally {
            readLock.unlock();
        }
    }

    public void set(String key,String value){
        writeLock.lock();
        System.out.println("获取锁成功");
        try {
            try {
                dataMap.put(key,value);
                readLock.lock();
            }finally {
                writeLock.unlock();
            }
            // 后续其他操作
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

    }


    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLockDemo demo2 = new ReentrantReadWriteLockDemo();

        // 写 ->读->读->写->读->写
        new Thread(()->{
            demo2.set("name","lcy");
        }).start();

        Thread.sleep(200);


        new Thread(()->{
            System.out.println(demo2.get("name"));
        }).start();


        Thread.sleep(200);


        new Thread(()->{
            System.out.println(demo2.get("name"));
        }).start();

        Thread.sleep(200);



        new Thread(()->{
            demo2.set("name","lcy2");
        }).start();
        Thread.sleep(200);

        new Thread(()->{

            System.out.println(demo2.get("name"));
        }).start();


    }
}
