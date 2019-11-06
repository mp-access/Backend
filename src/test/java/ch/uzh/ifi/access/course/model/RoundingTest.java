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
    public void halfUp() {
        double val = 1.375412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.HALP_UP, 2).round(val)).isEqualTo(1.5);

        val = 1.245412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.HALP_UP, 2).round(val)).isEqualTo(1.);
    }

    @Test
    public void halfDown() {
        double val = 1.375412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.HALF_DOWN, 2).round(val)).isEqualTo(1.);

        val = 1.245412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.HALF_DOWN, 2).round(val)).isEqualTo(1.);
    }

    @Test
    public void quarterUp() {
        double val = 1.375412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.QUARTER_UP, 2).round(val)).isEqualTo(1.5);

        val = 1.245412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.QUARTER_UP, 2).round(val)).isEqualTo(1.25);
    }

    @Test
    public void quarterDown() {
        double val = 1.375412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.QUARTER_DOWN, 2).round(val)).isEqualTo(1.25);

        val = 1.245412;
        Assertions.assertThat(new Rounding(Rounding.Strategy.QUARTER_DOWN, 2).round(val)).isEqualTo(1.);
    }

    @Test
    public void up() {
        double val = 1.375;
        Assertions.assertThat(new Rounding(Rounding.Strategy.UP, 2).round(val)).isEqualTo(1.38);

        val = 1.3751;
        Assertions.assertThat(new Rounding(Rounding.Strategy.UP, 2).round(val)).isEqualTo(1.38);

        val = 1.244;
        Assertions.assertThat(new Rounding(Rounding.Strategy.UP, 2).round(val)).isEqualTo(1.24);
    }

    @Test
    public void down() {
        double val = 1.375;
        Assertions.assertThat(new Rounding(Rounding.Strategy.DOWN, 2).round(val)).isEqualTo(1.37);

        val = 1.3751;
        Assertions.assertThat(new Rounding(Rounding.Strategy.DOWN, 2).round(val)).isEqualTo(1.38);

        val = 1.244;
        Assertions.assertThat(new Rounding(Rounding.Strategy.DOWN, 2).round(val)).isEqualTo(1.24);
    }

    @Test
    public void higherPrecision() {
        double val = 1.37517;
        Assertions.assertThat(new Rounding(Rounding.Strategy.UP, 4).round(val)).isEqualTo(1.3752);

        val = 1.3751;
        Assertions.assertThat(new Rounding(Rounding.Strategy.UP, 4).round(val)).isEqualTo(val);
    }
}