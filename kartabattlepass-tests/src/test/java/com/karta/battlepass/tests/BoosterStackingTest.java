package com.karta.battlepass.tests;

import com.karta.battlepass.api.data.booster.ActiveBooster;
import com.karta.battlepass.api.data.booster.Booster;
import com.karta.battlepass.api.data.booster.BoosterScope;
import com.karta.battlepass.api.data.booster.BoosterStackingStrategy;
import com.karta.battlepass.api.data.booster.BoosterType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoosterStackingTest {

    private static final double DELTA = 0.001;

    @Test
    void testSumStacking() {
        List<ActiveBooster> boosters = List.of(
                createBooster("b1", 1.5, BoosterStackingStrategy.SUM),
                createBooster("b2", 1.5, BoosterStackingStrategy.SUM)
        );
        double multiplier = calculateMultiplier(boosters, BoosterType.POINTS);
        // SUM strategy: 1.0 (base) + (1.5 - 1.0) + (1.5 - 1.0) = 2.0
        assertEquals(2.0, multiplier, DELTA);
    }

    @Test
    void testSumDifferentStacking() {
        List<ActiveBooster> boosters = List.of(
                createBooster("b1", 1.5, BoosterStackingStrategy.SUM_DIFFERENT),
                createBooster("b1", 2.0, BoosterStackingStrategy.SUM_DIFFERENT), // Should be ignored
                createBooster("b2", 1.2, BoosterStackingStrategy.SUM_DIFFERENT)
        );
        double multiplier = calculateMultiplier(boosters, BoosterType.POINTS);
        // SUM_DIFFERENT: 1.0 + (2.0 - 1.0) [highest of b1] + (1.2 - 1.0) [b2] = 2.2
        assertEquals(2.2, multiplier, DELTA);
    }

    @Test
    void testHighestStacking() {
        List<ActiveBooster> boosters = List.of(
                createBooster("b1", 1.5, BoosterStackingStrategy.HIGHEST),
                createBooster("b2", 2.0, BoosterStackingStrategy.HIGHEST),
                createBooster("b3", 1.8, BoosterStackingStrategy.HIGHEST)
        );
        double multiplier = calculateMultiplier(boosters, BoosterType.POINTS);
        // HIGHEST: The highest multiplier is 2.0
        assertEquals(2.0, multiplier, DELTA);
    }

    private ActiveBooster createBooster(String id, double multiplier, BoosterStackingStrategy strategy) {
        Booster booster = new Booster(id, "Test", BoosterType.POINTS, multiplier, Duration.ofHours(1), BoosterScope.GLOBAL, strategy);
        return new ActiveBooster(booster, null, Instant.now(), Instant.now().plus(Duration.ofHours(1)));
    }

    // This is the logic that would live inside BoosterServiceImpl
    private double calculateMultiplier(List<ActiveBooster> activeBoosters, BoosterType type) {
        // This is a simplified version for testing purposes.
        // A real implementation would handle the global default strategy.
        Map<BoosterStackingStrategy, List<ActiveBooster>> byStrategy = activeBoosters.stream()
                .filter(b -> b.booster().type() == type)
                .collect(Collectors.groupingBy(b -> b.booster().stackingStrategy()));

        double sumMultiplier = 1.0;
        List<ActiveBooster> sumGroup = byStrategy.get(BoosterStackingStrategy.SUM);
        if (sumGroup != null) {
            for (ActiveBooster b : sumGroup) {
                sumMultiplier += (b.booster().multiplier() - 1.0);
            }
        }

        double sumDifferentMultiplier = 1.0;
        List<ActiveBooster> sumDifferentGroup = byStrategy.get(BoosterStackingStrategy.SUM_DIFFERENT);
        if (sumDifferentGroup != null) {
            sumDifferentGroup.stream()
                    .collect(Collectors.groupingBy(b -> b.booster().id())) // Group by booster ID
                    .values().stream()
                    .map(group -> group.stream().max(Comparator.comparingDouble(b -> b.booster().multiplier())).orElse(null)) // Find the best booster in each group
                    .filter(java.util.Objects::nonNull)
                    .forEach(b -> sumDifferentMultiplier += (b.booster().multiplier() - 1.0));
        }

        double highestMultiplier = 1.0;
        List<ActiveBooster> highestGroup = byStrategy.get(BoosterStackingStrategy.HIGHEST);
        if (highestGroup != null) {
            highestMultiplier = highestGroup.stream()
                    .mapToDouble(b -> b.booster().multiplier())
                    .max()
                    .orElse(1.0);
        }

        return Math.max(Math.max(sumMultiplier, sumDifferentMultiplier), highestMultiplier);
    }
}
