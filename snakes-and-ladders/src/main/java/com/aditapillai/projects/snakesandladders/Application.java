package com.aditapillai.projects.snakesandladders;

import com.aditapillai.projects.snakesandladders.core.Game;
import com.aditapillai.projects.snakesandladders.utils.GameUtils;

public class Application {
    public static void main(String[] args) {
        Game game = GameUtils.createStaticGame();
        game.start();
    }
}
