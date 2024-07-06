package io.github.itzispyder.impropers3dminimap.util.math;

public final class MathUtils {

    public static double round(double value, int nthPlace) {
        return Math.floor(value * nthPlace) / nthPlace;
    }

    public static int clamp(int val, int min, int max) {
        return Math.min(max, Math.max(min, val));
    }

    public static double clamp(double val, double min, double max) {
        return Math.min(max, Math.max(min, val));
    }

    public static double progress(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    public static double progressClamped(double start, double end, double progress) {
        return start + (end - start) * clamp(progress, 0, 1);
    }

    public static float[] toPolar(double x, double y, double z) {
        double pi2 = 2 * Math.PI;
        float pitch, yaw;

        if (x == 0 && z == 0) {
            pitch = y > 0 ? -90 : 90;
            return new float[] { pitch, 0.0F };
        }

        double theta = Math.atan2(-x, z);
        yaw = (float)Math.toDegrees((theta + pi2) % pi2);

        double xz = Math.sqrt(x * x + z * z);
        pitch = (float)Math.toDegrees(Math.atan(-y / xz));

        return new float[] { pitch, yaw };
    }

    public static boolean isWrapped(double deg) {
        double f = deg % 360.0;
        return f < 180 && f >= -180;
    }

    public static double wrapDegrees(double deg) {
        double f = deg % 360.0;

        if (f >= 180.0) {
            f -= 360.0;
        }
        if (f < -180.0) {
            f += 360.0;
        }

        return f;
    }

    public static double angleBetween(double deg1, double deg2) {
        double diff = deg2 - deg1;
        deg1 = wrapDegrees(deg1);
        deg2 = wrapDegrees(deg2);

        if ((deg1 < -90 && deg2 > 90) || (deg1 > 90 && deg2 < -90)) {
            return (180 - Math.abs(deg1)) + (180 - Math.abs(deg2));
        }
        if (diff >= 180 || diff < -180) {
            return Math.abs(deg2 - deg1);
        }
        return Math.abs(wrapDegrees(diff));
    }

    public static boolean isInRange(int val, int min, int max) {
        return val >= min && val <= max;
    }

    public static boolean isInRange(double val, double min, double max) {
        return val >= min && val <= max;
    }
}
