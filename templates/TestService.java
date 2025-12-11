package {{ module|lower }}.tests;

import {{ module|lower }}.service.{{ module }}Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class {{ module }}ServiceTest {

    @Autowired
    private {{ module }}Service service;

    @Test
    void testCreateAndGet() {
        // Add test logic here
    }
}
