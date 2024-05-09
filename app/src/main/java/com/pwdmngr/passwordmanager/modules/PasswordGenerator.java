package com.pwdmngr.passwordmanager.modules;

import java.security.SecureRandom;

public class PasswordGenerator {
    public static String generate() {
        int pwdLength = 15, random;
        String pwd = new String();
        int count = 0;

        String numbers = "0123456789";
        String specials = "!@#$%^&*()_+=";
        String alphaLowercase = "abcdefghijklmnopqrstuvwxyz";
        String alphaUppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        int min = 2; // Minimum amount for each of char types

        SecureRandom rand = new SecureRandom();

        while (
                countChars(pwd, numbers) < min ||
                        countChars(pwd, specials) < min ||
                        countChars(pwd, alphaLowercase) < min ||
                        countChars(pwd, alphaUppercase) < min
        ) {
            pwd = "";
            for (int i = 0; i < pwdLength; i++) {
                random = rand.nextInt(4);
                switch(random) {
                    case 0:
                        pwd = pwd.concat(getChar(numbers));
                        break;
                    case 1:
                        pwd = pwd.concat(getChar(specials));
                        break;
                    case 2:
                        pwd = pwd.concat(getChar(alphaLowercase));
                        break;
                    case 3:
                        pwd = pwd.concat(getChar(alphaUppercase));
                        break;
                }
            }
        }
        return pwd;
    }

    // Get random char from string sequence
    private static String getChar(String s) {
        SecureRandom rand = new SecureRandom();
        int random = rand.nextInt(s.length());
        char c = s.charAt(random);

        return String.valueOf(c);
    }

    // Counts number of chars in a string that exist in another string
    private static int countChars(String str, String chars) {
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            if (chars.contains(String.valueOf(str.charAt(i)))) count++;
        }

        return count;
    }
}
