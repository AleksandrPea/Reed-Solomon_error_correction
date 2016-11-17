package com.apea.rscodes;

import java.util.Arrays;

public class RSDecoder {

    private int h;
    private GaluaField field;

    public RSDecoder(int h, boolean[] simplePolynom) {
        this.h = h;
        field = new GaluaField(simplePolynom);
    }

    /** @return corrected info code */
    public int[] decode(int[] code) {
        int[] codecopy = Arrays.copyOf(code, code.length);
        fixErrors(codecopy);
        int[] info = new int[codecopy.length - 2*h];
        for (int i = 0; i < info.length; i++) {
            info[i] = codecopy[2*h+i];
        }
        return info;
    }

    /**
     * @return number of errors.
     * if there is more than h errors, returns -1
     */
    public int fixErrors(int[] code) {
        int[] syndromes = calcSyndromes(code);
        boolean hasErrors = false;
        int i = 0;
        while (i < syndromes.length && !hasErrors) {
            if (syndromes[i] != 0) {
                hasErrors = true;
            }
            i++;
        }
        int errosn = 0;
        if (hasErrors) {
            int[] positions = findErrorPositions(syndromes);
            if (positions[0] != -1) {
                errosn = positions.length;
                int[] errors = findErrorVectors(syndromes, positions);
                for (i = 0; i < positions.length; i++) {
                    code[positions[i]] = field.add(code[positions[i]], errors[i]);
                }

            } else {
                errosn = -1;
                System.out.println("There are more than " + h + " errors.");
            }
        }
        return errosn;
    }

    int[] calcSyndromes(int[] code) {
        int[] syndromes = new int[2*h];
        for (int i = 0; i < syndromes.length; i++) {
            syndromes[i] = 0;
            for (int j = 0; j < code.length; j++) {
                syndromes[i] = field.add(syndromes[i],
                        field.mul(field.getPower(code[j]), j*(i+1)));
            }
        }
        return syndromes;
    }

    int[] findErrorPositions(int[] syndromes) {
        int[][] matrix = new int[h][h+1];
        // fill matrix
        for (int i = 0; i < matrix.length; i++) {
            int j = 0;
            for (; j < matrix[i].length - 1; j++) {
                matrix[i][j] = syndromes[i + h - 1 - j];
            }
            matrix[i][j] = syndromes[h + i];
        }
        int[] sigmas = solveSystem(matrix);
        // finding positions
        int[] positions = new int[sigmas.length];
        positions[0] = -1;
        int k = 0;
        for (int i = 0; i < field.length && k < sigmas.length; i++) {
            int buf = 1;
            for (int j = 0; j < sigmas.length; j++) {
                buf = field.add(buf,
                        field.mul(field.getPower(sigmas[j]), (j+1)*i));
            }
            if (buf == 0) {
                if (i == 0) {
                    positions[k++] = 0;
                } else {
                    positions[k++] = field.length - i;   // чому саме field.length - i, а не i ??
                }
            }
        }
        return positions;
    }

    int[] findErrorVectors(int[] syndromes, int[] errorPositions) {
        int[][] matrix = new int[errorPositions.length][errorPositions.length+1];
        // fill matrix
        int j = 0;
        for (; j < matrix[0].length-1; j++) {
            matrix[0][j] = field.getCode(errorPositions[j]);
        }
        matrix[0][j] = syndromes[0];
        for (int i = 1; i < matrix.length; i++) {
            j = 0;
            for (; j < matrix[0].length-1; j++) {
                matrix[i][j] = field.mul(field.getPower(matrix[i-1][j]), errorPositions[j]);
            }
            matrix[i][j] = syndromes[i];
        }
        return solveSystem(matrix);
    }

    private int[] solveSystem(int[][] matrix) {
        boolean straightRunIsFinished = false;
        for (int k = 0; k < matrix.length && !straightRunIsFinished; k++) {
            if (matrix[k][k] == 0) {
                int q = k+1;
                while(q < matrix.length && matrix[q][k] == 0) {
                    q++;
                }
                if (q != matrix.length) {
                    int[] buf = matrix[q];
                    matrix[q] = matrix[k];
                    matrix[k] = buf;
                } else {
                    straightRunIsFinished = true;
                }
            }
            if (!straightRunIsFinished) {
                for (int j = matrix[k].length - 1; j >= k; j--) {
                    matrix[k][j] = field.divWithCodes(matrix[k][j], matrix[k][k]);
                }
                for (int i = k + 1; i < matrix.length; i++) {
                    for (int j = matrix[i].length - 1; j >= k; j--) {
                        matrix[i][j] = field.add(matrix[i][j],
                                field.mulWithCodes(matrix[i][k], matrix[k][j]));
                    }
                }
            }
        }
        //finding rank(solvesCount) of matrix
        int sovlesCount = matrix.length;
        int i = matrix.length - 1;
        boolean flag = true;
        while (flag) {
            if (matrix[i--][matrix.length] == 0) {
                sovlesCount--;
            } else {
                flag = false;
            }
        }
        int[] solves = new int[sovlesCount];
        for (i = solves.length-1; i >= 0; i--) {
            solves[i] = matrix[i][matrix.length];
            for (int j = solves.length-1; j > i; j--) {
                solves[i] = field.add(solves[i],
                        field.mulWithCodes(solves[j], matrix[i][j]));
            }
        }
        return solves;
    }
}
