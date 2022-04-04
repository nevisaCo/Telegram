package com.finalsoft.ui.profile;

import java.util.Random;


public class RandomHelper {
    public static int getRandomNumber(){
        final int min = 1;
        final int max = 99999;
         int random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }
}
