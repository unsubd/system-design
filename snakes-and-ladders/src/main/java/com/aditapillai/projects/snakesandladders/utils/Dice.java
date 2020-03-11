package com.aditapillai.projects.snakesandladders.utils;

import java.util.Random;

public class Dice {

    public static final Random randomizer = new Random();
    public static final int DICE_MAX_VALUE = 6;

    public static int roll() {
        return randomizer.nextInt(DICE_MAX_VALUE) + 1;
    }
}
