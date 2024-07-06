package io.github.itzispyder.impropers3dminimap.render.animation;

public final class Animations {

    // f(x) = x
    public static final AnimationController LINEAR = x -> x;

    // f(x) = 1.2sin(2.1555x)
    public static final AnimationController UPWARDS_BOUNCE = x -> 1.2 * Math.sin(2.1555 * x);

    // f(x) = 2sin(2.6x)
    public static final AnimationController UPWARDS_BOUNCE_HEAVY = x -> 2 * Math.sin(2.6 * x);

    // f(x) = 1.1sin(2x)
    public static final AnimationController UPWARDS_BOUNCE_LIGHT = x -> 1.1 * Math.sin(2 * x);

    // f(x) = 0.8sin(5x-2.5)+0.5
    public static final AnimationController ELASTIC_BOUNCE = x -> 0.8 * Math.sin(5 * x - 2.5) + 0.5;

    // f(x) = 1 / [1 + e^(8-16x)]
    public static final AnimationController FADE_IN_AND_OUT = x -> 1 / (1 + Math.exp(8 - 16 * x));

    @FunctionalInterface
    public interface AnimationController {

        /**
         * Average f(x) math pun
         */
        double f(double x);
    }
}
