package io.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author lcy
 * @DESC:
 * @date 2020/7/30.
 */
public class BufferDemo {
    public static void main(String[] args) {
        BufferDemo bufferDemo = new BufferDemo();
        bufferDemo.demo1();
    }

    public void demo1(){
        //分配buffer内存空间

        //  capacity容量10,postition 偏移量：0 ,限制大小limit:10
        ByteBuffer allocate = ByteBuffer.allocate(10); // 堆内存 分配空间 8字节
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(10); //堆外（操作系统内核）

        System.out.println(String.format("初始化相关值： capacity容量%s,postition 偏移量：%s ,限制大小limit:%s ",allocate.capacity(),allocate.position(),allocate.limit()));

        //capacity容量10,postition 偏移量：3 ,限制大小limit:10
        //写入字符
        allocate.put((byte)1);
        allocate.put((byte)2);
        allocate.put((byte)3);

        System.out.println(String.format("初始化相关值： capacity容量%s,postition 偏移量：%s ,限制大小limit:%s ",allocate.capacity(),allocate.position(),allocate.limit()));

        //读取字符
        // capacity容量10,postition 偏移量：0 ,限制大小limit:3
        allocate.flip();
        System.out.println(String.format("初始化相关值： capacity容量%s,postition 偏移量：%s ,限制大小limit:%s ",allocate.capacity(),allocate.position(),allocate.limit()));

        //初始化相关值： capacity容量10,postition 偏移量：3 ,限制大小limit:3
        byte b1 = allocate.get();
        System.out.println(b1);
        byte b2 = allocate.get();
        System.out.println(b2);
        byte b3 = allocate.get();
        System.out.println(b3);

        System.out.println(String.format("初始化相关值： capacity容量%s,postition 偏移量：%s ,限制大小limit:%s ",allocate.capacity(),allocate.position(),allocate.limit()));


        allocate.clear();
        System.out.println(String.format("初始化相关值： capacity容量%s,postition 偏移量：%s ,限制大小limit:%s ",allocate.capacity(),allocate.position(),allocate.limit()));


    }
}
