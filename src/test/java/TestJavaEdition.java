import com.dansoftware.libraryapp.update.UpdateInformation;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestJavaEdition {

    @Test
    public void testVersion() {
        assertTrue(System.getProperty("java.version").startsWith("11"));
    }

    @Test
    public void testVendor() {
        assertTrue(System.getProperty("java.vendor").startsWith("Azul"));
    }
}
