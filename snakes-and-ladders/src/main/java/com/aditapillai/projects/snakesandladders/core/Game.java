package com.aditapillai.projects.snakesandladders.core;

import com.aditapillai.projects.snakesandladders.actors.Player;
import com.aditapillai.projects.snakesandladders.constants.GlobalConstants;
import com.aditapillai.projects.snakesandladders.constants.Status;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
public class Game {
    private Set<Player> players;
    @Getter
    private Board board;
    @Getter
    private Status status;
    @Getter
    private Player winner;

    public void start() {
        this.status = Status.ACTIVE;
        outer:
        while (true) {
            for (Player player : players) {
                int roll = player.rollDice();
                int count = GlobalConstants.MAX_ALLOWED_CHANCES;

                do {
                    if (player.isActive()) {
                        this.move(player, roll);
                    } else {
                        System.out.println(String.format("%s :: INACTIVE", player));
                        System.out.println(String.format("%s :: rolls %d", player, roll));
                        if (roll == GlobalConstants.ACTIVATION_VALUE) {
                            this.activate(player);
                            System.out.println(String.format("%s :: ACTIVE", player));
                            roll = player.rollDice();
                            this.move(player, roll);
                        }
                    }
                    if (board.isLastOccupied()) {
                        break outer;
                    }

                } while ((roll == GlobalConstants.EXTRA_CHANCE_VALUE) && --count > 0);
            }
        }
        this.status = Status.COMPLETED;
        this.winner = board.getWinner();
        System.out.println(String.format("%s WINS!!", this.winner));
    }

    private void move(Player player, int roll) {
        System.out.println(String.format("%s :: rolls %d", player, roll));
        board.place(player, roll);
        System.out.println(String.format("%s :: CURRENT_STATUS", player));
    }

    private void activate(Player player) {
        player.setActive(true);
    }

}
