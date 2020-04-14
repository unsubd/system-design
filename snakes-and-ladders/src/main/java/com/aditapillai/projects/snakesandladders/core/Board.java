package com.aditapillai.projects.snakesandladders.core;

import com.aditapillai.projects.snakesandladders.actors.Player;
import com.aditapillai.projects.snakesandladders.actors.Snake;
import com.aditapillai.projects.snakesandladders.models.Ladder;
import com.aditapillai.projects.snakesandladders.models.Square;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Board {
    private final Square[] board;

    public int place(Player player, int roll) {
        int currentPosition = player.getCurrentPosition();
        int finalPosition = currentPosition + roll;
        if (finalPosition <= this.board.length - 1) {
            Square square = board[finalPosition];
            Snake snake = square.getSnake();
            Ladder ladder = square.getLadder();
            player.setCurrentPosition(finalPosition);
            if (snake != null) {
                System.out.println(String.format("%s :: Snake bites!", player));
                snake.bite(player);
            } else if (ladder != null) {
                System.out.println(String.format("%s :: Climbs ladder!", player));
                player.climb(ladder);
            }

            board[currentPosition].removePlayer(player);
            board[player.getCurrentPosition()].addPlayer(player);
        }
        return player.getCurrentPosition();
    }

    public boolean isLastOccupied() {
        return this.board[this.board.length - 1].isOccupied();
    }

    public Player getWinner() {
        return this.board[this.board.length - 1].getPlayers()
                                                .stream()
                                                .findFirst()
                                                .orElse(null);
    }

    public Square[] getCurrentBoardClone() {
        return Arrays.stream(this.board)
                     .map(Square::clone)
                     .toArray(Square[]::new);
    }

}
