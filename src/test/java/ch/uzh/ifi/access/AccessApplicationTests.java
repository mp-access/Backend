package ch.uzh.ifi.access;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.data.mongodb.database=testing"})
public class AccessApplicationTests {

    @Test
    public void contextLoads() {
    }

}
