package org.net.gpstracer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.runner.RunWith;
import org.net.gpstracer.configuration.TestcontainersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@Import({TestcontainersConfig.class})
@Testcontainers
@Tag("integration")
public abstract class IntegrationTest {

    @Autowired
    private List<DynamoDbTable<?>> tables;


    @AfterEach
    void tearDown() {
        tables.forEach(IntegrationTest::deleteAllItems);
    }

    public static <T> void deleteAllItems(DynamoDbTable<T> table) {
        try {
            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();
            for (Page<T> page : table.scan(scanRequest)) {
                for (T item : page.items()) {
                    table.deleteItem(item);
                }
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to delete items: " + e.getMessage());
        }
    }
}
