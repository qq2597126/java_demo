package utils;

import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lcy
 * @DESC:
 * @date 2020/7/31.
 */
public class CollectionUtils {

    public static <T> void addListToArray(List<T> list, T[] toArray ){
        if(list == null){
            list = new ArrayList<T>();
        }
        for (int index = 0; index < toArray.length;index++){
            list.add(toArray[index]);
        }
    }

    public static Byte[] toPrimitive(final byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Byte[0];
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Byte.valueOf(array[i]);
        }
        return result;
    }


}
