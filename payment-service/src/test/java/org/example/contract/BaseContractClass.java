package org.example.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.PaymentServiceApplication;
import org.example.intergation.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PaymentServiceApplication.class)
@AutoConfigureMessageVerifier
@DirtiesContext
public class BaseContractClass extends BaseIntegrationTest {
    @Autowired
    protected WebApplicationContext context;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.webAppContextSetup(context);
    }
}