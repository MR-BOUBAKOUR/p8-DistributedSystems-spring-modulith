package com.redha.tourguide_modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {

    ApplicationModules modules = ApplicationModules.of(TourguideModulithApplication.class);

    @Test
    void verifyModularity() {
        System.out.println(modules.toString());
        modules.verify();
    }

    @Test
    void writeDocumentation() {
        new Documenter(modules)
                .writeDocumentation();
    }

}
