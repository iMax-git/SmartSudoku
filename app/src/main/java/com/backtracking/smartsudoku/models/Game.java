package com.backtracking.smartsudoku.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Stack;


public class Game
{
    private Stack<ImmutableGrid> stateStack;
    private Stack<ImmutableGrid> redoStateStack;
    private boolean isWon;


    public Game() {
        this.stateStack = new Stack<>();
        this.redoStateStack = new Stack<>();
        this.isWon = false;
    }

    public Game(@NonNull ImmutableGrid grid) {
        this();
        this.stateStack.push(grid);
    }


    public void setNumber(int x, int y, int value) {
        stateStack.push(stateStack.peek().set(x, y, value));
        redoStateStack.clear();
    }


    public void undo() {
        redoStateStack.push(stateStack.pop());
    }


    public void redo() {
        stateStack.push(redoStateStack.pop());
    }


    public boolean isUndoable() {
        return this.stateStack.size() > 1;
    }


    public boolean isRedoable() {
        return !this.redoStateStack.isEmpty();
    }


    public boolean isWon() {
        return this.isWon;
    }


    public ImmutableGrid getGrid() {
        return this.stateStack.peek();
    }


    /*
     * Serialization
     */

    public String serialize() {
        return Game.serializeState(this.stateStack) +
                ";" +                                           // -> ";;" between the two strings
                Game.serializeState(this.redoStateStack);
    }


    public static Game deserialize(final String string) {
        Game game = new Game();
        String[] stackStrings = string.split(";;");
        if (stackStrings.length > 0) {
            game.stateStack = Game.deserializeState(stackStrings[0]);
            if (stackStrings.length > 1) {
                game.redoStateStack = Game.deserializeState(stackStrings[1]);
            }
        }
        return game;
    }


    protected static String serializeState(final Stack<ImmutableGrid> states) {
        StringBuilder sb = new StringBuilder();
        states.forEach(state -> {
            sb.append(state.toString());
            sb.append(';');
        });
        return sb.toString();
    }


    protected static Stack<ImmutableGrid> deserializeState(String data) {
        Stack<ImmutableGrid> states = new Stack<>();
        Arrays.stream(TextUtils.split(data, ";"))
                .forEach(state -> {
                    if (state.length() == 81) {
                        states.push(ImmutableGrid.fromString(state));
                    }
                });
        return states;
    }
}
