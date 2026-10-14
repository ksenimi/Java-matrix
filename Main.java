import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/** Консольный интерфейс для матриц A, B и последнего матричного результата R. */
public final class Main {
    private static final BufferedReader INPUT = new BufferedReader(new InputStreamReader(System.in));
    private static ComplexMatrix a;
    private static ComplexMatrix b;
    private static ComplexMatrix result;
    private static Complex lastDeterminant;
    private static String lastDeterminantSource;

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.out.println("Матрицы комплексных чисел. Числа вводите через точку, например: 2.5 -3");
        while (true) {
            System.out.println("\n1 — создать A     2 — создать B     3 — показать A, B и результаты");
            System.out.println("4 — A + B         5 — A × B         6 — A / B");
            System.out.println("7 — Aᵀ            8 — Bᵀ            9 — det(A)");
            System.out.println("10 — det(B)       11 — R → A         12 — R → B");
            System.out.println("0 — выход");
            String choice = readLine("Операция: ");
            if (choice == null || choice.equals("0")) {
                return;
            }
            try {
                switch (choice) {
                    case "1":
                        a = readMatrix("A");
                        System.out.println("Матрица A создана.\n" + a);
                        break;
                    case "2":
                        b = readMatrix("B");
                        System.out.println("Матрица B создана.\n" + b);
                        break;
                    case "3":
                        printMatrix("A", a);
                        printMatrix("B", b);
                        printMatrix("R (последний матричный результат)", result);
                        if (lastDeterminant != null) {
                            System.out.println("Последний определитель det(" + lastDeterminantSource
                                    + ") = " + lastDeterminant);
                        }
                        break;
                    case "4":
                        requireBoth();
                        saveMatrixResult("A + B", a.add(b));
                        break;
                    case "5":
                        requireBoth();
                        saveMatrixResult("A × B", a.multiply(b));
                        break;
                    case "6":
                        requireBoth();
                        saveMatrixResult("A / B = A × B⁻¹", a.divide(b));
                        break;
                    case "7":
                        saveMatrixResult("Aᵀ", requireMatrix(a, "A").transpose());
                        break;
                    case "8":
                        saveMatrixResult("Bᵀ", requireMatrix(b, "B").transpose());
                        break;
                    case "9":
                        saveDeterminant("A", requireMatrix(a, "A").determinant());
                        break;
                    case "10":
                        saveDeterminant("B", requireMatrix(b, "B").determinant());
                        break;
                    case "11":
                        a = requireResult();
                        printMatrix("A (скопирована из R)", a);
                        break;
                    case "12":
                        b = requireResult();
                        printMatrix("B (скопирована из R)", b);
                        break;
                    default:
                        System.out.println("Неизвестная операция.");
                }
            } catch (IllegalArgumentException | ArithmeticException exception) {
                System.out.println("Операция невозможна: " + exception.getMessage());
            }
        }
    }

    private static ComplexMatrix readMatrix(String name) throws IOException {
        int rows = readPositiveInt("Число строк " + name + ": ");
        int columns = readPositiveInt("Число столбцов " + name + ": ");
        Complex[][] values = new Complex[rows][columns];
        System.out.println("Для каждого элемента введите действительную и мнимую части через пробел.");
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                while (true) {
                    String line = readLine(name + "[" + (row + 1) + "][" + (column + 1) + "]: ");
                    if (line == null) {
                        throw new IOException("Ввод завершён до окончания матрицы");
                    }
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length != 2) {
                        System.out.println("Нужно два числа, например: 2 -3");
                        continue;
                    }
                    try {
                        values[row][column] = new Complex(
                                Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                        break;
                    } catch (NumberFormatException exception) {
                        System.out.println("Используйте числовой формат с точкой, например: 2.5 -3");
                    } catch (IllegalArgumentException exception) {
                        System.out.println("Части числа должны быть конечными.");
                    }
                }
            }
        }
        return new ComplexMatrix(values);
    }

    private static int readPositiveInt(String prompt) throws IOException {
        while (true) {
            String line = readLine(prompt);
            if (line == null) {
                throw new IOException("Ввод завершён до окончания матрицы");
            }
            try {
                int value = Integer.parseInt(line.trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // При неверном вводе повторяем запрос.
            }
            System.out.println("Введите положительное целое число.");
        }
    }

    private static String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return INPUT.readLine();
    }

    private static ComplexMatrix requireMatrix(ComplexMatrix matrix, String name) {
        if (matrix == null) {
            throw new IllegalArgumentException("сначала создайте матрицу " + name);
        }
        return matrix;
    }

    private static void requireBoth() {
        requireMatrix(a, "A");
        requireMatrix(b, "B");
    }

    private static ComplexMatrix requireResult() {
        if (result == null) {
            throw new IllegalArgumentException("сначала выполните операцию с матрицей");
        }
        return result;
    }

    private static void saveMatrixResult(String operation, ComplexMatrix matrix) {
        result = matrix;
        printMatrix("R = " + operation, result);
    }

    private static void saveDeterminant(String source, Complex determinant) {
        lastDeterminant = determinant;
        lastDeterminantSource = source;
        System.out.println("det(" + source + ") = " + determinant);
    }

    private static void printMatrix(String name, ComplexMatrix matrix) {
        if (matrix == null) {
            System.out.println(name + ": ещё не создана");
        } else {
            System.out.println(name + " (" + matrix.rows() + " × " + matrix.columns() + "):\n" + matrix);
        }
    }
}
