package com.redha.tourguide_modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTest {

    ApplicationModules modules = ApplicationModules.of(TourguideModulithApplication.class);

    @Test
    void verifyModularity() {

        System.out.println(modules.toString());

        modules.verify();

    }

}
