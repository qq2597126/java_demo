package thread;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/26.
 */
public class GuavaDemo {

    /**
     * 核心线程数10   最大线程数20 超时时间1000秒  消息队列 100个
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(10,20,1000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));

    /**
     * Guava线程池
     */
    private static ListeningExecutorService gPool = MoreExecutors.listeningDecorator(executorService);

}
