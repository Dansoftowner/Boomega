import org.junit.*;

public class TestJavaEdition {

    @Test
    public void testVersion() {
        Assert.assertTrue(System.getProperty("java.version").startsWith("11"));
    }

    @Test
    public void testVendor() {
        Assert.assertTrue(System.getProperty("java.vendor").startsWith("Azul"));
    }
}
