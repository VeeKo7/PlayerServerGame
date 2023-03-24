package com.yolo.playerservergame.player;

/**
 * This class represents a player/game round with bet and playerNumber
 * and getServerNumber() that generates random number from 1 to 100
 * */

import java.util.Random;

public class Player {
    private final double bet;
    private final int playerNumber;

    public Player(double bet, int playerNumber) {
        this.bet = bet;
        this.playerNumber = playerNumber;
    }

    public double getBet() {
        return bet;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * @return a randomly generated number
     * from 1 to 100
     * */
    public int generateServerNumber () {
        Random random = new Random();
        return random.nextInt(100) + 1;
    }
}
