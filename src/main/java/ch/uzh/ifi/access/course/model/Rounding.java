package ch.uzh.ifi.access.course.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

@Data
public class Rounding implements Serializable {

    public static final Rounding DEFAULT = new Rounding(Strategy.ROUND, 4);

    private Strategy strategy;
    private int steps;
    private int precision;

    public Rounding() {
    }

    public Rounding(Strategy strategy, int steps) {
        this.strategy = strategy;
        this.steps = steps;
        this.precision = calcPrecision(steps);
    }

    public double round(double value) {
        return strategy.round(value, steps, precision);
    }

    public int calcPrecision(Integer steps) {
        if(steps == 1 ) {
            return 0;
        }

        String strinRep = Double.toString( 1/steps);
        return strinRep.length() - strinRep.indexOf('.') - 1;
    }

    public enum Strategy {
        CEILING(Strategy::ceil),
        FLOOR(Strategy::floor),
        ROUND(Strategy::roundUp);

        private TriFunction<Double, Integer, Integer, Double> algorithm;

        Strategy(TriFunction<Double, Integer, Integer, Double> algorithm) {
            this.algorithm = algorithm;
        }

        double round(double unroundedValue, int steps, int precision) {
            return algorithm.apply(unroundedValue, steps, precision);
        }

        static double ceil(Double unroundedValue, Integer steps, Integer precision) {
          //  return new BigDecimal(unroundedValue*steps).setScale(precision, RoundingMode.CEILING).doubleValue()/steps;
            return Math.ceil( unroundedValue*steps)/steps;

        }

        static double floor(Double unroundedValue, Integer steps, Integer precision) {
            return Math.floor(unroundedValue * steps)/steps;
        }

        static double roundUp(double unroundedValue, Integer steps, Integer precision) {
            return (double) Math.round(unroundedValue * steps) / steps;
        }

    }

    @FunctionalInterface
    public interface TriFunction<F, S, T, R> {
        R apply(F f, S s, T t);
    }

}
