package lock.lock;

public class SyncLockDemo {
    private int i = 0;
    private SyncLock syncLock = new SyncLock();
    public void add(){
        syncLock.lock();
        i++;
        syncLock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        SyncLockDemo syncLockDemo = new SyncLockDemo();
        for (int i = 0;i<1000;i++){
            new Thread(()->{
                for (int a = 0;a<100;a++){
                    syncLockDemo.add();
                }

            }).start();
        }

        Thread.sleep(5000);
        System.out.println(syncLockDemo.i);
    }
}
