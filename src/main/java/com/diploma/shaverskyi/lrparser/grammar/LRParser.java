package com.diploma.shaverskyi.lrparser.grammar;

import lombok.Getter;

import java.util.*;

@Getter
public abstract class LRParser {
    protected List<Map<String, Integer>> goToTable;
    protected List<Map<String, Action>> actionTable;
    protected Grammar grammar;

    public LRParser(Grammar grammar) {
        this.grammar = grammar;
    }

    protected abstract void createGoToTable();

    public boolean accept(List<String> inputs) {
        inputs.add(Constants.BEGIN_SYMBOL);
        Deque<String> stack = new ArrayDeque<>();
        stack.add(Constants.ZERO);

        int index = 0;
        while (index < inputs.size()) {
            String stackTop = stack.peek();

            if (stackTop == null) {
                // TODO Throw valid exception
                throw new RuntimeException("Stack is empty");
            }

            int state = Integer.parseInt(stackTop);
            String nextInput = inputs.get(index);
            Action action = actionTable.get(state).get(nextInput);

            if (action == null) {
                return false;
            } else {
                switch (action.getType()) {
                    case SHIFT: {
                        stack.push(nextInput);
                        stack.push(Objects.toString(action.getOperand()));
                        index++;
                        break;
                    }

                    case REDUCE: {
                        int ruleIndex = action.getOperand();
                        Rule rule = grammar.getRules().get(ruleIndex);

                        int rightSideLength = rule.getRightSide().size();
                        for (int i = 0; i < 2 * rightSideLength; i++) {
                            stack.pop();
                        }

                        nextInput = stack.peek();
                        if (nextInput == null) {
                            // TODO Throw valid exception
                            throw new RuntimeException("Stack is empty");
                        }

                        String leftSide = rule.getLeftSide();
                        int nextState = Integer.parseInt(nextInput);
                        int nonTerminalState = goToTable.get(nextState).get(leftSide);

                        stack.push(leftSide);
                        stack.push(Objects.toString(nonTerminalState));
                        break;
                    }

                    case ACCEPT: {
                        return true;
                    }

                    default: {
                        // TODO Exception
                        System.out.println("Action type out of bounds??");
                    }
                }
            }
        }
        return false;
    }

    //TODO KEK
    public String goToTableStr() {
        String str = "Go TO Table : \n";
        str += "          ";
        for (String variable : grammar.getNonTerminals()) {
            str += String.format("%-6s", variable);
        }
        str += "\n";

        for (int i = 0; i < goToTable.size(); i++) {
            for (int j = 0; j < (grammar.getNonTerminals().size() + 1) * 6 + 2; j++) {
                str += "-";
            }
            str += "\n";
            str += String.format("|%-6s|", i);
            for (String variable : grammar.getNonTerminals()) {
                str += String.format("%6s", (goToTable.get(i).get(variable) == null ? "|" : goToTable.get(i).get(variable) + "|"));
            }
            str += "\n";
        }
        for (int j = 0; j < (grammar.getNonTerminals().size() + 1) * 6 + 2; j++) {
            str += "-";
        }
        return str;
    }

    // TODO kek
    public String actionTableStr() {
        String str = "Action Table : \n";
        HashSet<String> terminals = new HashSet<>(grammar.getTerminals());
        terminals.add("$");
        str += "                ";
        for (String terminal : terminals) {
            str += String.format("%-10s", terminal);
        }
        str += "\n";

        for (int i = 0; i < actionTable.size(); i++) {
            for (int j = 0; j < (terminals.size() + 1) * 10 + 2; j++) {
                str += "-";
            }
            str += "\n";
            str += String.format("|%-10s|", i);
            for (String terminal : terminals) {
                str += String.format("%10s", (actionTable.get(i).get(terminal) == null ? "|" : actionTable.get(i).get(terminal) + "|"));
            }
            str += "\n";
        }
        for (int j = 0; j < (terminals.size() + 1) * 10 + 2; j++) {
            str += "-";
        }
        return str;
    }
}
