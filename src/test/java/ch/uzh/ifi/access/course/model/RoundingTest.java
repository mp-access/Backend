package ch.uzh.ifi.access.course.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RoundingTest {

    @Test
    public void defaultRounding() {
        double val = 1.375412;
        Assertions.assertThat(Rounding.DEFAULT.round(val)).isEqualTo(1.5);

        val = 1.305412;
        Assertions.assertThat(Rounding.DEFAULT.round(val)).isEqualTo(1.25);
    }

    @Test
    public void roundUpStep2() {
        double val = 1.38412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 2).round(val)).isEqualTo(1.5);

        val = 1.235412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 2).round(val)).isEqualTo(1);
    }

    @Test
    public void roundUpStep3() {
        double val = 1.38412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 3).round(val)).isEqualTo(1.0 + 1d/3d);

        val = 1.125412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 3).round(val)).isEqualTo(1);
    }

    @Test
    public void roundUpStep4() {
        double val = 1.38412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 4).round(val)).isEqualTo(1.5);

        val = 1.126412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 4).round(val)).isEqualTo(1.25);

        val = 1.01412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 4).round(val)).isEqualTo(1);
    }

    @Test
    public void roundUpStep8() {
        double val = 1.46412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 8).round(val)).isEqualTo(1.5);

        val = 1.125412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 8).round(val)).isEqualTo(1.125);

        val = 1.01412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 8).round(val)).isEqualTo(1);
    }

    @Test
    public void roundUpStep10() {
        double val = 1.46412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 10).round(val)).isEqualTo(1.5);

        val = 1.125412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 10).round(val)).isEqualTo(1.1);

        val = 1.01412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.ROUND, 10).round(val)).isEqualTo(1);
    }

    @Test
    public void up() {
        double val = 1.375;
        Assertions.assertThat(new Rounding(Rounding.Strategy.CEILING, 4).round(val)).isEqualTo(1.5);

        val = 1.044;
        Assertions.assertThat(new Rounding(Rounding.Strategy.CEILING, 2).round(val)).isEqualTo(1.5);
    }

    @Test
    public void down() {
        double val = 1.375;
        Assertions.assertThat(new Rounding(Rounding.Strategy.FLOOR, 4).round(val)).isEqualTo(1.25);

        val = 1.244;
        Assertions.assertThat(new Rounding(Rounding.Strategy.FLOOR, 2).round(val)).isEqualTo(1);
    }

}