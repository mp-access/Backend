package ch.uzh.ifi.access.course.model;

import lombok.Data;

import java.io.Serializable;
import java.util.function.BiFunction;

@Data
public class Rounding implements Serializable {

    public static final Rounding DEFAULT = new Rounding(Strategy.ROUND, 4);

    private Strategy strategy;
    private int steps;

    public Rounding() {
    }

    public Rounding(Strategy strategy, int steps) {
        this.strategy = strategy;
        this.steps = steps;
    }

    public double round(double value) {
        return strategy.round(value, steps);
    }

    public enum Strategy {

        CEILING(Strategy::ceil),
        FLOOR(Strategy::floor),
        ROUND(Strategy::roundUp);

        private BiFunction<Double, Integer, Double> algorithm;

        Strategy(BiFunction<Double, Integer, Double> algorithm) {
            this.algorithm = algorithm;
        }

        double round(double unroundedValue, int steps) {
            return algorithm.apply(unroundedValue, steps);
        }

        static double ceil(Double unroundedValue, Integer steps) {
            return Math.ceil( unroundedValue*steps)/steps;
        }

        static double floor(Double unroundedValue, Integer steps) {
            return Math.floor(unroundedValue * steps)/steps;
        }

        static double roundUp(double unroundedValue, Integer steps) {
            return (double) Math.round(unroundedValue * steps) / steps;
        }

    }

}
