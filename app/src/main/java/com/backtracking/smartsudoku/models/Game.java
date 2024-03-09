package com.backtracking.smartsudoku.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Stack;


public class Game
{
    private Stack<ImmutableGrid> stateStack;
    private Stack<ImmutableGrid> redoStateStack;
    private ImmutableGrid solution;


    public Game() {
        this.stateStack = new Stack<>();
        this.stateStack.push(new ImmutableGrid());
        this.redoStateStack = new Stack<>();
        this.solution = new ImmutableGrid();
    }

    public Game(@NonNull ImmutableGrid grid, @NonNull ImmutableGrid solution) {
        this();
        this.stateStack.clear();
        this.stateStack.push(grid);
        this.solution = solution;
    }


    public void setNumber(int x, int y, int value) {
        ImmutableGrid baseGrid = stateStack.get(0);
        if (baseGrid.get(x,y) == 0) {
            stateStack.push(stateStack.peek().set(x, y, value));
            redoStateStack.clear();
        }
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
        return this.solution.equals(this.getGrid());
    }


    public ImmutableGrid getGrid() {
        return this.stateStack.peek();
    }


    public ImmutableGrid getBaseGrid() {
        return this.stateStack.get(0);
    }


    public ImmutableGrid getSolution() {
        return this.solution;
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
