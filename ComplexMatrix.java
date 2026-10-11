/** Матрица комплексных чисел. Каждая операция возвращает новую матрицу. */
public final class ComplexMatrix {
    private final Complex[][] values;
    private final int rows;
    private final int columns;

    public ComplexMatrix(Complex[][] source) {
        if (source == null || source.length == 0 || source[0] == null || source[0].length == 0) {
            throw new IllegalArgumentException("Матрица должна содержать хотя бы одну строку и один столбец");
        }
        rows = source.length;
        columns = source[0].length;
        values = new Complex[rows][columns];
        for (int row = 0; row < rows; row++) {
            if (source[row] == null || source[row].length != columns) {
                throw new IllegalArgumentException("Все строки матрицы должны иметь одинаковую длину");
            }
            for (int column = 0; column < columns; column++) {
                if (source[row][column] == null) {
                    throw new IllegalArgumentException("Каждый элемент матрицы должен быть задан");
                }
                values[row][column] = source[row][column];
            }
        }
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }

    public Complex get(int row, int column) {
        return values[row][column];
    }

    public ComplexMatrix add(ComplexMatrix other) {
        if (rows != other.rows || columns != other.columns) {
            throw new IllegalArgumentException("Для сложения размеры матриц должны совпадать");
        }
        Complex[][] result = new Complex[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                result[row][column] = values[row][column].add(other.values[row][column]);
            }
        }
        return new ComplexMatrix(result);
    }

    public ComplexMatrix multiply(ComplexMatrix other) {
        if (columns != other.rows) {
            throw new IllegalArgumentException("Для умножения число столбцов первой матрицы должно совпадать с числом строк второй");
        }
        Complex[][] result = new Complex[rows][other.columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < other.columns; column++) {
                Complex sum = Complex.ZERO;
                for (int k = 0; k < columns; k++) {
                    sum = sum.add(values[row][k].multiply(other.values[k][column]));
                }
                result[row][column] = sum;
            }
        }
        return new ComplexMatrix(result);
    }

    /** Деление справа: A / B означает A, умноженную на обратную матрицу B. */
    public ComplexMatrix divide(ComplexMatrix other) {
        return multiply(other.inverse());
    }

    /** Обычное транспонирование без комплексного сопряжения. */
    public ComplexMatrix transpose() {
        Complex[][] result = new Complex[columns][rows];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                result[column][row] = values[row][column];
            }
        }
        return new ComplexMatrix(result);
    }

    /** Метод Гаусса с выбором ведущего элемента по модулю. */
    public Complex determinant() {
        requireSquare();
        Complex[][] work = copyValues();
        Complex product = Complex.ONE;
        for (int pivot = 0; pivot < rows; pivot++) {
            int best = pivotRow(work, pivot);
            if (work[best][pivot].abs() == 0.0) {
                return Complex.ZERO;
            }
            if (best != pivot) {
                swapRows(work, best, pivot);
                product = product.multiply(new Complex(-1.0, 0.0));
            }
            Complex leading = work[pivot][pivot];
            product = product.multiply(leading);
            for (int row = pivot + 1; row < rows; row++) {
                Complex factor = work[row][pivot].divide(leading);
                for (int column = pivot + 1; column < columns; column++) {
                    work[row][column] = work[row][column]
                            .subtract(factor.multiply(work[pivot][column]));
                }
                work[row][pivot] = Complex.ZERO;
            }
        }
        return product;
    }

    /** Метод Гаусса — Жордана с выбором ведущего элемента по модулю. */
    public ComplexMatrix inverse() {
        requireSquare();
        Complex[][] left = copyValues();
        Complex[][] right = new Complex[rows][rows];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < rows; column++) {
                right[row][column] = row == column ? Complex.ONE : Complex.ZERO;
            }
        }

        for (int pivot = 0; pivot < rows; pivot++) {
            int best = pivotRow(left, pivot);
            if (left[best][pivot].abs() == 0.0) {
                throw new ArithmeticException("Матрица-делитель вырождена");
            }
            swapRows(left, best, pivot);
            swapRows(right, best, pivot);

            Complex leading = left[pivot][pivot];
            for (int column = 0; column < rows; column++) {
                left[pivot][column] = left[pivot][column].divide(leading);
                right[pivot][column] = right[pivot][column].divide(leading);
            }
            for (int row = 0; row < rows; row++) {
                if (row == pivot) {
                    continue;
                }
                Complex factor = left[row][pivot];
                for (int column = 0; column < rows; column++) {
                    left[row][column] = left[row][column]
                            .subtract(factor.multiply(left[pivot][column]));
                    right[row][column] = right[row][column]
                            .subtract(factor.multiply(right[pivot][column]));
                }
            }
        }
        return new ComplexMatrix(right);
    }

    private void requireSquare() {
        if (rows != columns) {
            throw new IllegalArgumentException("Операция требует квадратную матрицу");
        }
    }

    private Complex[][] copyValues() {
        Complex[][] copy = new Complex[rows][columns];
        for (int row = 0; row < rows; row++) {
            System.arraycopy(values[row], 0, copy[row], 0, columns);
        }
        return copy;
    }

    private static int pivotRow(Complex[][] matrix, int column) {
        int best = column;
        for (int row = column + 1; row < matrix.length; row++) {
            if (matrix[row][column].abs() > matrix[best][column].abs()) {
                best = row;
            }
        }
        return best;
    }

    private static void swapRows(Complex[][] matrix, int first, int second) {
        Complex[] temporary = matrix[first];
        matrix[first] = matrix[second];
        matrix[second] = temporary;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            result.append("[ ");
            for (int column = 0; column < columns; column++) {
                if (column > 0) {
                    result.append("  ");
                }
                result.append(values[row][column]);
            }
            result.append(" ]");
            if (row + 1 < rows) {
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }
}
