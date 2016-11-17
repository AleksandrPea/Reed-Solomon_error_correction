package com.apea.rscodes;

public class GaluaField {

    private boolean[] simplePolynom;
    private int polynomNumber;
    private int[] field;
    // fieldMap[code] = power
    private int[] fieldMap;
    public final int length;

    public GaluaField(boolean[] simplePolynom) {
        this.simplePolynom = simplePolynom;
        for (int i = simplePolynom.length-2; i >= 0; i--) {
            if (simplePolynom[i]) {
                polynomNumber++;
            }
            polynomNumber = polynomNumber << 1;
        }
        polynomNumber++;
        buildField();
        length = field.length;
    }

    public int add(int code1, int code2) {
        return code1 ^ code2;
    }

    /** @return result code **/
    public int mulWithCodes(int code1, int code2) {
        int result = 0;
        if (code1 != 0 && code2 != 0) {
            result = field[(fieldMap[code1] + fieldMap[code2]) % field.length];
        }
        return result;
    }

    /** @return result code **/
    public int mul(int power1, int power2) {
        int result = 0;
        if (power1 != -1 && power2 != -1) {
            result = field[(power1 + power2) % field.length];
        }
        return result;
    }

    public int divWithCodes(int code1, int code2) {
        int result = 0;
        if (code1 !=0 && code2 != 0) {
            int index = fieldMap[code1] - fieldMap[code2];
            if (index < 0) {
                index += field.length;
            }
            result = field[index];
        } else if (code2 == 0) {
            throw new ArithmeticException("Dividing by zero");
        }
        return result;
    }

    public int getCode(int power) {
        return field[power];
    }

    public int getPower(int code) {
        return fieldMap[code];
    }

    private void buildField() {
        field = new int[(int)Math.pow(2, simplePolynom.length)-1];
        int test;
        int mask = field.length+1;
        field[0] = 1;
        for (int i = 1; i < field.length; i++) {
            field[i] = field[i-1] << 1;
            test = field[i] & mask;
            if (test != 0) {
                field[i] = field[i] & ~mask;
                field[i] = field[i] ^ polynomNumber;
            }
        }
        fieldMap = new int[field.length+1];
        fieldMap[0] = -1;
        for (int i = 0; i < field.length; i++) {
            fieldMap[field[i]] = i;
        }
    }
}
