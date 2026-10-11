import java.util.Locale;

/** Неизменяемое комплексное число. */
public final class Complex {
    public static final Complex ZERO = new Complex(0.0, 0.0);
    public static final Complex ONE = new Complex(1.0, 0.0);

    private final double real;
    private final double imaginary;

    public Complex(double real, double imaginary) {
        if (!Double.isFinite(real) || !Double.isFinite(imaginary)) {
            throw new IllegalArgumentException("Действительная и мнимая части должны быть конечными числами");
        }
        this.real = real;
        this.imaginary = imaginary;
    }

    public double real() {
        return real;
    }

    public double imaginary() {
        return imaginary;
    }

    public Complex add(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }

    public Complex subtract(Complex other) {
        return new Complex(real - other.real, imaginary - other.imaginary);
    }

    public Complex multiply(Complex other) {
        return new Complex(
                real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real);
    }

    public Complex divide(Complex other) {
        // Масштабирование предотвращает переполнение знаменателя при больших компонентах.
        double scale = Math.max(Math.abs(other.real), Math.abs(other.imaginary));
        if (scale == 0.0) {
            throw new ArithmeticException("Деление на нулевое комплексное число");
        }
        double r = other.real / scale;
        double i = other.imaginary / scale;
        double denominator = r * r + i * i;
        return new Complex((real * r + imaginary * i) / denominator / scale,
                (imaginary * r - real * i) / denominator / scale);
    }

    public double abs() {
        return Math.hypot(real, imaginary);
    }

    @Override
    public String toString() {
        double shownReal = Math.abs(real) < 0.00005 ? 0.0 : real;
        double shownImaginary = Math.abs(imaginary) < 0.00005 ? 0.0 : imaginary;
        return String.format(Locale.US, "%.4f%+.4fi", shownReal, shownImaginary);
    }
}
