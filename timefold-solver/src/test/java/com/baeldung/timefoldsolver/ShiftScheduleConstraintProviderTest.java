package com.baeldung.timefoldsolver;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;

class ShiftScheduleConstraintProviderTest {

    private static final LocalDate MONDAY = LocalDate.of(2030, 4, 1);
    private static final LocalDate TUESDAY = LocalDate.of(2030, 4, 2);

    ConstraintVerifier<ShiftScheduleConstraintProvider, ShiftSchedule> constraintVerifier = ConstraintVerifier.build(new ShiftScheduleConstraintProvider(),
        ShiftSchedule.class, Shift.class);

    @Test
    void whenTwoShiftsOnOneDay_thenPenalize() {
        Employee ann = new Employee("Ann", null);
        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::atMostOneShiftPerDay)
            .given(ann, new Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), null, ann), new Shift(MONDAY.atTime(14, 0), MONDAY.atTime(22, 0), null, ann))
            // Penalizes by 2 because both {shiftA, shiftB} and {shiftB, shiftA} match.
            // To avoid that, use forEachUniquePair() in the constraint instead.
            .penalizesBy(2);
    }
    @Test
    void whenTwoShiftsOnDifferentDays_thenDoNotPenalize() {
        Employee ann = new Employee("Ann", null);
        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::atMostOneShiftPerDay)
            .given(ann, new Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), null, ann), new Shift(TUESDAY.atTime(14, 0), TUESDAY.atTime(22, 0), null, ann))
            .penalizesBy(0);
    }

    @Test
    void whenEmployeeLacksRequiredSkill_thenPenalize() {
        Employee ann = new Employee("Ann", Set.of("Waiter"));
        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::requiredSkill)
            .given(ann, new Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), "Cook", ann))
            .penalizesBy(1);
    }

    @Test
    void whenEmployeeHasRequiredSkill_thenDoNotPenalize() {
        Employee ann = new Employee("Ann", Set.of("Waiter"));
        constraintVerifier.verifyThat(ShiftScheduleConstraintProvider::requiredSkill)
            .given(ann, new Shift(MONDAY.atTime(6, 0), MONDAY.atTime(14, 0), "Waiter", ann))
            .penalizesBy(0);
    }

}
