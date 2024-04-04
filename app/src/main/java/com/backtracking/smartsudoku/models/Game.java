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
    private long timer;

    public Game() {
        this.stateStack = new Stack<>();
        this.stateStack.push(new ImmutableGrid());
        this.redoStateStack = new Stack<>();
        this.solution = new ImmutableGrid();
        this.timer = 0;
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


    public long getTimer() {
        return this.timer;
    }

    public void setTimer(long seconds) {
        this.timer = seconds;
    }


    /*
     * Serialization
     */

    /**
     * Serialized format:
     *      "undoStackGrid1;undoStackGrid2;...;;redoStackGrid1;redoStackGrid2;...;;elapsedTime"
     *
     *  A grid stack is serialized like this;
     *      "grid1;grid2;grid3;" etc.
     *
     *  Example (with abridged grids):
     *        data: "601000090252;62100090252;;611000090252;;124"
     *               ^undoStack                ^redoStack    ^seconds"
     *
     * @return game state serialized as string.
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        if (!stateStack.isEmpty()) {
            sb.append(Game.serializeState(stateStack));
            sb.append(";");
        } else {
            sb.append(";;");
        }
        if (!redoStateStack.isEmpty()) {
            sb.append(Game.serializeState(redoStateStack));
            sb.append(";");
        } else {
            sb.append(";;");
        }
        sb.append(timer);
        return sb.toString();
    }


    public static Game deserialize(final String string) {
        String[] strings = string.split(";;");
        if (strings.length != 3) {
            return null;
        }
        Game game = new Game();
        game.stateStack = Game.deserializeState(strings[0]);
        game.redoStateStack = Game.deserializeState(strings[1]);
        game.timer = Long.parseLong(strings[2]);
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
