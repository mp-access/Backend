package ch.uzh.ifi.access.course.model;

import lombok.Data;

@Data
public class Rounding {

    public static final Rounding DEFAULT = new Rounding(Rounding.Strategy.QUARTER_UP, 2);

    private Strategy strategy;
    private int precision;

    public Rounding() { }

    public Rounding(Strategy strategy, int precision) {
        this.strategy = strategy;
        this.precision = precision;
    }

    public double round(double value){
        return strategy.round(value, precision);
    }

    public enum Strategy {
        UP, DOWN, HALP_UP, HALF_DOWN, QUARTER_UP, QUARTER_DOWN;

         double round(double unroundedValue, int precision) {
            return 2.0d;
        }

    }

}
