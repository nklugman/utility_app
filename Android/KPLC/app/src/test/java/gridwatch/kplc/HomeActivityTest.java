package gridwatch.kplc;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import gridwatch.kplc.activities.HomeActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HomeActivityTest {

    private final static boolean FAILED = false;

    @Test
    public void check_random_fail() {
        try {
            Method method = HomeActivity.class.getDeclaredMethod("randomly_fail", null);
            method.setAccessible(true);
            Boolean result = (Boolean) method.invoke(HomeActivity.class.newInstance(), null);
            assertEquals(result, FAILED);
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