package com.appointment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "app.jwt.secret=dGVzdFNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3NlT25seQ==",
    "app.jwt.expiration-ms=86400000"
})
class PatientAppointmentSystemApplicationTests {

    @Test
    void contextLoads() {
    }
}
