package io.github.itzispyder.impropers3dminimap.render.animation;

import static java.lang.Math.*;

/**
 * Desmos is actually goated
 */
public final class Animations {

    // f(x) = x
    public static final AnimationController LINEAR = x -> x;

    // f(x) = 1.2sin(2.1555x)
    public static final AnimationController UPWARDS_BOUNCE = x -> 1.2 * sin(2.1555 * x);

    // f(x) = 2sin(2.6x)
    public static final AnimationController UPWARDS_BOUNCE_HEAVY = x -> 2 * sin(2.6 * x);

    // f(x) = 1.1sin(2x)
    public static final AnimationController UPWARDS_BOUNCE_LIGHT = x -> 1.1 * sin(2 * x);

    // f(x) = 0.8sin(5x-2.5)+0.5
    public static final AnimationController ELASTIC_BOUNCE = x -> 0.8 * sin(5 * x - 2.5) + 0.5;

    // f(x) = 0.323333arctan(50(x-0.5))+0.5
    public static final AnimationController FADE_IN_AND_OUT = x -> 0.36 * Math.atan(10 * (x - 0.5)) + 0.5;

    // f(x) = 0.5sin(2π(x-0.25))+0.5
    public static final AnimationController HARMONIC = x -> 0.5 * sin(2 * PI * (x - 0.25)) + 0.5;

    // f(x) = 1 / [(6.9(x - 0.5))^2 + 1]^2
    public static final AnimationController SPIKED_HARMONIC = x -> 1 / Math.pow(Math.pow(6.9 * (x - 0.5), 2) + 1, 2);

    // f(x) = 0.3(sin^{2}6.1(x + 0.0635) - cos6.1(x + 0.0635) - sin6.1(x + 0.0635)) + 0.35
    public static final AnimationController INTERRUPTED_HARMONIC = x -> 0.3 * (pow(sin(6.1 * (x + 0.0635)), 2) - cos(6.1 * (x + 0.0635)) - sin(6.1 * (x + 0.0635))) + 0.35;

    @FunctionalInterface
    public interface AnimationController {

        /**
         * Average f(x) math pun
         */
        double f(double x);
    }
}