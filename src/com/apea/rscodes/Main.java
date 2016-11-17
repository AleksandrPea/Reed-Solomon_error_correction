package com.apea.rscodes;

import java.util.Arrays;

public class Main {
    private static int m = 6;
    private static int h = 3;
    private static int maxInfoLen = (int)Math.pow(2, m) - 2*h;
    private static boolean[] simplePolynom = {true, false, false,false,false, true};

    public static void main(String[] args) {
        //boolean[] simplePolynom = {true, false, true};
        RSCodeGenerator rscg = new RSCodeGenerator(h, simplePolynom);
        RSDecoder rsdc = new RSDecoder(h, simplePolynom);
       // int[] info = {0b010,0b011,0b111};
        int[] info = {0b111, 0b0001};
        int[] code = rscg.encode(info);
        System.out.println("Згенерований код:");
        printInBinary(code, true);
        // вносимо помилки
        code[0] = code[0] ^ 0b100;
        code[4] = code[4] ^ 0b101;
        code[2] = code[2] ^ 0b111;
        //code[2] = code[2] ^ 0b001;
        System.out.println("Спотворений код:");
        printInBinary(code, true);
        int[] syndromes = rsdc.calcSyndromes(code);
        System.out.println("Синдроми:");
        printInBinary(syndromes, false);
        int[] positions = rsdc.findErrorPositions(syndromes);
        if (positions[0] != -1) {
            System.out.println("Позиції помилок та відповідні вектори помилок:");
            System.out.println(Arrays.toString(positions));
            int[] errors = rsdc.findErrorVectors(syndromes, positions);
            printInBinary(errors, false);
            rsdc.fixErrors(code);
            System.out.println("Виправлений код:");
            printInBinary(code, true);
        } else {
            System.out.println("Внесено помилок більше ніж " + h);
        }
        //test();
    }

    private static void printInBinary(int[] arr, boolean reverse) {
        if (reverse) {
            for (int i = arr.length-1; i >= 0; i--) {
                System.out.print(String.format("%" + m + "s",
                        Integer.toBinaryString(arr[i])).replace(' ', '0') + " ");
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                System.out.print(String.format("%" + m + "s",
                        Integer.toBinaryString(arr[i])).replace(' ', '0') + " ");
            }
        }
        System.out.println();
    }

    private static void test() {
        RSCodeGenerator rscg = new RSCodeGenerator(h, simplePolynom);
        RSDecoder rsdc = new RSDecoder(h, simplePolynom);
        int N = 1000;
        int q = 0;
        for (; q < N; q++) {
            int infolen = (int)(maxInfoLen*Math.random());
            int[] info = new int[infolen];
            int buf = (int)Math.pow(2, m);
            for (int i = 0; i < info.length; i++) {
                info[i] = (int)(buf*Math.random());
            }
            int[] code = rscg.encode(info);
            //System.out.print("Test " + q+": ");
            //printInBinary(code, true);
            int[] codeCopy = Arrays.copyOf(code, code.length);
            for (int i = 0; i < h; i++) {
                int index = (int)(codeCopy.length*Math.random());
                codeCopy[index] = (int)(buf*Math.random());
            }
            rsdc.fixErrors(codeCopy);
            boolean flag = true;
            for (int i = 0; i < code.length && flag; i++) {
                flag = code[i] == codeCopy[i];
            }
            if (!flag) {
                System.err.println("The test failed.");
                q = N+1;
            }
        }
        if (q == N) {
            System.out.println("The test is successful.");
        }
    }
}
