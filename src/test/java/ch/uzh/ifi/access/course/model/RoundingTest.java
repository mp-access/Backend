package ch.uzh.ifi.access.course.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoundingTest {

    @Test
    public void defaultRounding() {
        double val = 1.375412;
        Assertions.assertEquals(1.5, Rounding.DEFAULT.round(val));

        val = 1.305412;
        Assertions.assertEquals(1.25, Rounding.DEFAULT.round(val));
    }

    @Test
    public void roundUpStep2() {
        double val = 1.38412;
        Assertions.assertEquals(1.5, new Rounding(Rounding.Strategy.ROUND, 2).round(val));

        val = 1.235412;
        Assertions.assertEquals(1, new Rounding(Rounding.Strategy.ROUND, 2).round(val));
    }

    @Test
    public void roundUpStep3() {
        double val = 1.38412;
        Assertions.assertEquals(1.0 + 1d/3d, new Rounding(Rounding.Strategy.ROUND, 3).round(val));

        val = 1.125412;
        Assertions.assertEquals(1, new Rounding(Rounding.Strategy.ROUND, 3).round(val));
    }

    @Test
    public void roundUpStep4() {
        double val = 1.38412;
        Assertions.assertEquals(1.5, new Rounding(Rounding.Strategy.ROUND, 4).round(val));

        val = 1.126412;
        Assertions.assertEquals(1.25, new Rounding(Rounding.Strategy.ROUND, 4).round(val));

        val = 1.01412;
        Assertions.assertEquals(1, new Rounding(Rounding.Strategy.ROUND, 4).round(val));
    }

    @Test
    public void roundUpStep8() {
        double val = 1.46412;
        Assertions.assertEquals(1.5, new Rounding(Rounding.Strategy.ROUND, 8).round(val));

        val = 1.125412;
        Assertions.assertEquals(1.125, new Rounding(Rounding.Strategy.ROUND, 8).round(val));

        val = 1.01412;
        Assertions.assertEquals(1, new Rounding(Rounding.Strategy.ROUND, 8).round(val));
    }

    @Test
    public void roundUpStep10() {
        double val = 1.46412;
        Assertions.assertEquals(1.5, new Rounding(Rounding.Strategy.ROUND, 10).round(val));

        val = 1.125412;
        Assertions.assertEquals(1.1, new Rounding(Rounding.Strategy.ROUND, 10).round(val));

        val = 1.01412;
        Assertions.assertEquals(1, new Rounding(Rounding.Strategy.ROUND, 10).round(val));
    }

    @Test
    public void up() {
        double val = 1.375;
        Assertions.assertEquals(1.5, new Rounding(Rounding.Strategy.CEILING, 4).round(val));

        val = 1.044;
        Assertions.assertEquals(1.5, new Rounding(Rounding.Strategy.CEILING, 2).round(val));
    }

    @Test
    public void down() {
        double val = 1.375;
        Assertions.assertEquals(1.25, new Rounding(Rounding.Strategy.FLOOR, 4).round(val));

        val = 1.244;
        Assertions.assertEquals(1, new Rounding(Rounding.Strategy.FLOOR, 2).round(val));
    }

}