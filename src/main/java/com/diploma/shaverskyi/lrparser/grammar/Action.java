package com.diploma.shaverskyi.lrparser.grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Action {
    private ActionType type;
    private int operand;

    @Override
    public String toString() {
        return type + " " + (type == ActionType.ACCEPT ? "" : operand);
    }
}
