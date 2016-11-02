package gridwatch.kplc;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import gridwatch.kplc.activities.billing.PostPaidActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by guoxinyi on 10/24/16.
 */
public class PostPaidActivityTest {

    private final static boolean FAILED = false;

    @Test
    public void check_sendMessage() {
        try {
            Method method = PostPaidActivity.class.getDeclaredMethod("sendMessage", null);
            method.setAccessible(true);
            Boolean result = (Boolean) method.invoke(PostPaidActivity.class.newInstance(), null);
            assertEquals(result, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            assertNotNull(e.getMessage(), null);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            assertNotNull(e.getMessage(), null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            assertNotNull(e.getMessage(), null);
        } catch (InstantiationException e) {
            e.printStackTrace();
            assertNotNull(e.getMessage(), null);
        }
    }

}