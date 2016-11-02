package gridwatch.kplc;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import gridwatch.kplc.activities.social_media.Twitter.TwitterTimeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class TwitterUnitTest{
    private final static boolean TestPassed = true;

    @Test
    public void check_random_fail() {

        try {
            Method method = TwitterTimeline.class.getDeclaredMethod("checkTarget", null);
            method.setAccessible(true);
            Boolean result = (Boolean) method.invoke(TwitterTimeline.class.newInstance(), null);
            assertEquals(result, TestPassed);
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


