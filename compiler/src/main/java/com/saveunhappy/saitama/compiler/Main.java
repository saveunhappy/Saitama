package com.saveunhappy.saitama.compiler;

public class Main {
    public static void main(String[] args) {
        int expected = 8;
        int actual = sum(3,5);
        if(expected == actual){
            System.out.println("test passed");
        }else{
            System.out.println("test failed");
        }
    }

    private static int sum(int i, int i1) {
        return i + i1;
    }
}
