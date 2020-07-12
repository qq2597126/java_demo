package lock.cas;

import sun.misc.Unsafe;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

import java.lang.reflect.Field;

/**
 * CAS 介绍
 * 类： Unsafe
 *
 */
public class CAS {


    private static Unsafe unsafe;

    static {
        //此方法获取不到，行不通，报错，不允许使用。通过反射获取
        //Unsafe unsafe = Unsafe.getUnsafe();

        try {
            // 对象的获取
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 1.创建对象
       Persion persion = (Persion) unsafe.allocateInstance(Persion.class);
        // 2.操作属性
        Field ageField = Persion.class.getDeclaredField("age");
        long agetFieldOffset = unsafe.objectFieldOffset(ageField);

        // 3. 操作数组
        String str[] = new String[]{"1","2","3"};
        int indexScale = unsafe.arrayIndexScale(String[].class);
        // 4. 操作内存
        long allocateMemory = unsafe.allocateMemory(8);

        // 5 CAS 操作
        Persion persion1 = new Persion();
        persion1.setLeng(5000);
        Field ageField1 = Persion.class.getDeclaredField("age");
        long agetFieldOffset1 = unsafe.objectFieldOffset(ageField1);
        Field lengField = Persion.class.getDeclaredField("leng");
        long lengFieldOffset = unsafe.objectFieldOffset(lengField);
        //原子操作设置年龄
        System.out.println("原子操作 是否设置成功："+unsafe.compareAndSwapInt(persion1, agetFieldOffset1, 0, 1));
        System.out.println("设置后的值："+persion1.getAge());


        System.out.println("原子操作 是否设置成功："+unsafe.compareAndSwapObject(persion1,lengFieldOffset,persion1.getLeng(),new Integer(6000)));
        System.out.println("设置后的值："+persion1.getLeng());



    }
}
class Persion{
    private String name;
    private int age = 0;
    private Integer leng;

    public Integer getLeng() {
        return leng;
    }

    public void setLeng(Integer leng) {
        this.leng = leng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}


