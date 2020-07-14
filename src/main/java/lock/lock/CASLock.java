package lock.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lcy
 * @DESC:
 * @date 2020/6/18.
 */
public class CASLock {
    volatile  int i =1;

    //java.lang.ExceptionInInitializerError   Unsafe.getUnsafe(); 不能直接使用
    static Unsafe unsafe  ; //直接操作内存，修改对象，数组...API
    public static  Long valueOffset;
    //相关的操作是基于Unsafe的cas
    public static AtomicBoolean atomicBoolean = new AtomicBoolean();

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            valueOffset=unsafe.objectFieldOffset(CASLock.class.getDeclaredField("i"));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void add(){
        //i++;


        // 1. CSA 循环重置机制
        //直接操作内存
        int current;
        do {
            //此处会耗时操作，那么线程就会占中大量的CPU,某一个内存地址。某一个变量。
            current = unsafe.getIntVolatile(this, valueOffset);
        }while (!unsafe.compareAndSwapInt(this,valueOffset,current,current+1)); // 进行原子操作，局限只能是一条语句

        //2.使用锁。



    }

    public static void main(String[] args) throws InterruptedException {
        CASLock casLock = new CASLock();
        for (int x=0;x<2;x++){
            new Thread(()->{
                for (int y=0;y<10000;y++){
                    casLock.add();
                }
            }).start();
        }

        TimeUnit.SECONDS.sleep(1);
        System.out.println(casLock.i);
    }
}
