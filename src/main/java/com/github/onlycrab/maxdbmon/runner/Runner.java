package com.github.onlycrab.maxdbmon.runner;

/**
 * @author Roman Rynkovich
 * @version 0.9
 */
public class Runner {
    public static void main(String[] args) {
        try {
            System.out.println(new Executor().execute(args));
        } catch (Exception e) {
            System.out.println(Executor.printError(e.getMessage()));
        }
    }
}
