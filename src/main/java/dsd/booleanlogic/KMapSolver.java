package dsd.booleanlogic;

import java.util.List;
import java.util.Set;

public class KMapSolver {
    private final int numVariables;
    private final int rows;
    private final int cols;
    private final int[][] grid;

    // Gray-code sequence for rows/columns: 00, 01, 11, 10
    private static final int[] GRAY_2BIT = {0, 1, 3, 2};
    private static final int[] GRAY_1BIT = {0, 1};

    public KMapSolver(int numVariables) {
        if (numVariables < 2 || numVariables > 4) {
            throw new IllegalArgumentException("K-Map supports 2, 3, or 4 variables.");
        }
        this.numVariables = numVariables;

        if (numVariables == 2) {
            this.rows = 2; // A: 0, 1
            this.cols = 2; // B: 0, 1
        } else if (numVariables == 3) {
            this.rows = 2; // A: 0, 1
            this.cols = 4; // BC: 00, 01, 11, 10
        } else {
            this.rows = 4; // AB: 00, 01, 11, 10
            this.cols = 4; // CD: 00, 01, 11, 10
        }

        this.grid = new int[rows][cols];
    }

    /**
     * Populates the K-Map matrix with 1s at minterm positions.
     */
    public void populateMinterms(Set<Integer> minterms) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int mintermIndex = calculateMintermIndex(r, c);
                grid[r][c] = minterms.contains(mintermIndex) ? 1 : 0;
            }
        }
    }

    private int calculateMintermIndex(int r, int c) {
        if (numVariables == 2) {
            return (GRAY_1BIT[r] << 1) | GRAY_1BIT[c];
        } else if (numVariables == 3) {
            return (GRAY_1BIT[r] << 2) | GRAY_2BIT[c];
        } else {
            return (GRAY_2BIT[r] << 2) | GRAY_2BIT[c];
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public void printGrid() {
        System.out.println("K-Map Grid (" + numVariables + " Variables):");
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }
}