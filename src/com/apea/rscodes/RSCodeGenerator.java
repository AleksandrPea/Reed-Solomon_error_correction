package com.apea.rscodes;

public class RSCodeGenerator {

    private int h;
    private GaluaField field;
    private int[] g;

    public RSCodeGenerator(int h, boolean[] simplePolynom) {
        this.h = h;
        field = new GaluaField(simplePolynom);
        makeGenPolynom();
    }

    public int[] encode(int[] info) {
        int[] code;
        if (info.length <= field.length - 2*h) {
            code = new int[info.length + 2*h];
            for (int i = 0; i < info.length; i++) {
                code[i+2*h] = info[i];
            }
            // ділення info / g
            for (int i = code.length - 1; i >= g.length-1; i--) {
                if (code[i] != 0) {
                    int alpa = code[i];
                    int k = i;
                    for (int j = g.length - 1; j >= 0; j--, k--) {
                        code[k] = code[k] ^ field.mulWithCodes(alpa, g[j]);
                    }
                }
            }
            // додаємо до знайденого поліному інформаційний
            for (int i = 0; i < info.length; i++) {
                code[i+2*h] = info[i];
            }
        } else {
            System.out.println("There are too many info symbols for this generator.");
            code = null;
        }
        return code;
    }

    private void makeGenPolynom() {
        g = new int[2 * h + 1];
        int[] gCopy = new int[2*h + 1];
        g[0] = field.getCode(1);
        g[1] = 1;
        for (int i = 2; i <= 2 * h; i++) {
            for (int j = 0; j < i; j++) {
                //shift + copy
                gCopy[j+1] = g[j];
                //multiply by alpa^i
                g[j] = field.mul(field.getPower(g[j]), i);
            }
            //adding gCopy + g
            for (int j = 1; j <= i; j++) {
                g[j] = field.add(g[j], gCopy[j]);
            }
        }
    }
}
