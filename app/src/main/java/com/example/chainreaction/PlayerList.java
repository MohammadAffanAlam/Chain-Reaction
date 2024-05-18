package com.example.chainreaction;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Random;

public class PlayerList implements Serializable {
    // Circular linked list
    class PlayerNode implements Serializable { // PlayerNode class
        private String playerName;
        private int playerColor;
        private PlayerNode next; // Next player in the list
        private int pos; // Player index
        private int cellCount; // Number of cells occupied by the player (0 means the player has lost)

        public PlayerNode(String playerName, int playerColor, PlayerNode next, int pos) {
            this.playerName = playerName;
            this.playerColor = playerColor;
            this.next = next;
            this.pos = pos;
            cellCount = 0;
        }
        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }
        public void setPlayerColor(int playerColor) {
            this.playerColor = playerColor;
        }
        public String getPlayerName() {
            return playerName;
        }
        public int getPlayerColor() {
            return playerColor;
        }
        public int getPos() {
            return pos;
        }
        public int getCellCount() {
            return cellCount;
        }
        public void increaseCellCount() {
            cellCount++;
        }
        public void decreaseCellCount() {
            cellCount--;
        }
    }

    private PlayerNode head = null;
    private PlayerNode turnPointer = null; // PlayerNode object that points to the player whose turn it is

    public Boolean isEmpty() {
        if (head == null) {
            return true;
        }
        else {
            return false;
        }
    }

    public int size() {
        if (isEmpty()) {
            return 0;
        }

        int x = 1;
        PlayerNode runner = head;
        while (runner.next != head) {
            runner = runner.next;
            x++;
        }

        return x;
    }

    // Adds a PlayerNode to the end of the linked list with a name generated based on its position and a random color
    public void addPlayerNode() {
        Random random = new Random();
        String playerName = "Player" + (size() + 1);
        int playerColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        PlayerNode newNode = new PlayerNode(playerName, playerColor, null, 0);

        if (head == null) {
            head = newNode;
            head.next = head;
            turnPointer = head;
        }
        else {
            PlayerNode runner = head;
            while (runner.next != head) {
                runner = runner.next;
            }

            newNode.next = runner.next;
            runner.next = newNode;
            newNode.pos = size() - 1;
        }
    }

    // Adds a PlayerNode to the end of the linked list with a set name and color
    public void addPlayerNode(String playerName, int playerColor) {
        PlayerNode newNode = new PlayerNode(playerName, playerColor, null, 0);

        if (head == null) {
            head = newNode;
            head.next = head;
            turnPointer = head;
        }
        else {
            PlayerNode runner = head;
            while (runner.next != head) {
                runner = runner.next;
            }

            newNode.next = runner.next;
            runner.next = newNode;
            newNode.pos = size() - 1;
        }
    }

    public void removePlayerNode(int pos) {
        if (!isEmpty()) { // Error check for empty linked list
            if (pos < 0) { // Error check for negative positions
                return;
            }
            if (pos >= size()) { // Error check for out of bounds
                return;
            }

            if (pos == 0) {
                if (head.next == head) {
                    head = null;
                }
                else {
                    PlayerNode runner = head.next;
                    while (runner.next != head) {
                        runner = runner.next;
                    }
                    runner.next = head.next;
                    head = head.next;
                }
            }
            else {
                int x = pos;
                PlayerNode runner = head;

                while (x > 1) {
                    runner = runner.next;
                    x--;
                }

                runner.next = runner.next.next;
            }

            PlayerNode runner = head;
            for (int i = 0; i < size(); i++) {
                runner.pos = i;
                runner = runner.next;
            }
        }
    }

    public PlayerNode getPlayerNode(int pos) {
        if (isEmpty()) { // Error check for empty linked list
            return null;
        }
        if (pos < 0) { // Error check for negative positions
            return null;
        }
        if (pos >= size()) { // Error check for out of bounds
            return null;
        }

        int x = 0;
        PlayerNode runner = head;

        while (x != pos) {
            runner = runner.next;
            x++;
        }

        return runner;
    }

    public void rotateTurn() {
        turnPointer = turnPointer.next;
    }

    public void reset() { // Sets the turnPointer back to the head and sets the cellCount of all players back to 0
        turnPointer = head;

        for (int i = 0; i < size(); i++) {
            getPlayerNode(i).cellCount = 0;
        }
    }

    public PlayerNode getPointer() {
        return turnPointer;
    }
}