package com.yolo.playerservergame.gameServer;

import com.yolo.playerservergame.player.Player;

import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

/**
 * Responsible for setting up a WebSocket server that
 * receives messages and responds to player.
 * Generation of random number 1 to 100
 * Implementation of logic to calculate the win based
 * on the chance formula
 * and sending result back to player.
 * */

/*
* @ServerEndpoint defines WebSocket endpoint on the server side.
* When player initiates connection, a server will create
* a new instance of a class with @ServerEndpoint annotation
* to handle the connection.
* */
@ServerEndpoint("/game")
public class GameServer {

    /**
     * onMessage() method handles incoming messages from player(client) in a WebSocket.
     * This method is called whenever a new message is received from a player.
     * @param session represents current WebSocket instance
     * @param message received from player
     * @throws IOException - when error occurs (connection problem, server unable to
     *  write data to the socket) while sending result back to player
     * */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        /*
        * parsing incoming message from player,
        * splitting message into parts where ever we have ","
        * assuming message: "bet, playerNumber"
        * */
        String [] parts = message.split(",");
        //converting string representation of floating-point number into double value
        double bet = Double.parseDouble(parts[0]);
        //converting string representation of decimal number into int value
        int playerNumber = Integer.parseInt(parts[1]);
        //creating a new Player instance with player's bet and playerNumber
        Player player = new Player(bet, playerNumber);
        //generating random number for game round
        int serverNumber = player.generateServerNumber();
        //calculated win based on bet and playerNumber
        double win = calculateWin(bet, playerNumber, serverNumber);
        /*
        * sending result back to client and converting win back to String
        * getBasicRemote() obtains RemoteEndpoint.Basic object that allows
        * a WebSocket endpoint to send messages synchronously to the
        * connected client
        * */
        session.getBasicRemote().sendText(Double.toString(win));
    }

    /**
     * calculating win based on chance formula
     * @param bet received from player
     * @param playerNumber received from player
     * @param serverNumber compared with playerNumber
     *
     * @return win depending on comparison of playerNumber and serverNumber
     * */
    public double calculateWin(double bet, int playerNumber, int serverNumber) {
        int chance = 99 / (100 - playerNumber);
        double win =  bet * chance;

        /*
        * comparing player's number with server's number and adjusting win,
          if playerNumber is greater than serverNumber, player wins, winnings double,
          if playerNumber equals serverNumber, win equals to bet
          and if playerNumber is less than serverNumber, winnings are lost
          and equal to 0.
        * */
        if (playerNumber > serverNumber) {
            return win *= 2;
        } else if (playerNumber == serverNumber) {
            return win = bet;
        } else
            return win = 0;
    }
}


