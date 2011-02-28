package com.dbdeploy;

import java.util.Scanner;

public class UserInputReader {
    public String read(String prompt) {
        System.out.print(prompt + ": ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
