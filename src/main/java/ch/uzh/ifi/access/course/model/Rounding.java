package ch.uzh.ifi.access.course.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

@Data
public class Rounding implements Serializable {

    public static final Rounding DEFAULT = new Rounding(Strategy.QUARTER_UP, 0);

    private Strategy strategy;
    private int precision;

    public Rounding() {
    }

    public Rounding(Strategy strategy, int precision) {
        this.strategy = strategy;
        this.precision = precision;
    }

    public double round(double value) {
        return strategy.round(value, precision);
    }

    public enum Strategy {
        UP(Strategy::up),
        DOWN(Strategy::down),
        HALP_UP(Strategy::halfUp),
        HALF_DOWN(Strategy::halfDown),
        QUARTER_UP(Strategy::quarterUp),
        QUARTER_DOWN(Strategy::quarterDown);

        private BiFunction<Double, Integer, Double> algorithm;

        Strategy(BiFunction<Double, Integer, Double> algorithm) {
            this.algorithm = algorithm;
        }

        double round(double unroundedValue, int precision) {
            return algorithm.apply(unroundedValue, precision);
        }

        static double up(Double unroundedValue, Integer precision) {
            return new BigDecimal(unroundedValue).setScale(precision, RoundingMode.HALF_UP).doubleValue();
        }

        static double down(Double unroundedValue, Integer precision) {
            return new BigDecimal(unroundedValue).setScale(precision, RoundingMode.HALF_DOWN).doubleValue();
        }

        static double halfUp(double unroundedValue, Integer precision) {
            return (double) Math.round(unroundedValue * 2) / 2;
        }

        static double halfDown(double unroundedValue, Integer precision) {
            return Math.floor(unroundedValue * 2) / 2;
        }

        static double quarterUp(double unroundedValue, Integer precision) {
            return (double) Math.round(unroundedValue * 4) / 4;
        }

        static double quarterDown(double unroundedValue, Integer precision) {
            return Math.floor(unroundedValue * 4) / 4;
        }
    }

}
