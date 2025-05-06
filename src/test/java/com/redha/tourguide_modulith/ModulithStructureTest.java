package com.redha.tourguide_modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModulithStructureTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules modules = ApplicationModules.of(TourguideModulithApplication.class);
        modules.verify(); // Vérifie les cycles et les accès à des types non exposés
    }

}
