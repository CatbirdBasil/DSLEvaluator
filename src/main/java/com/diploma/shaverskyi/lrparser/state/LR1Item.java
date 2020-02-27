package com.diploma.shaverskyi.lrparser.state;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class LR1Item {
    private String leftSide;
    private List<String> rightSide;
    private int dotPointer;
    private Set<String> lookahead;

    public LR1Item(LR1Item item) {
        this(item.leftSide, item.rightSide, item.dotPointer, item.lookahead);
    }

    public String getCurrent() {
        if (dotPointer == rightSide.size()) {
            return null;
        }

        return rightSide.get(dotPointer);
    }

    boolean goTo() {
        if (dotPointer >= rightSide.size()) {
            return false;
        }

        dotPointer++;
        return true;
    }

    //TODO Check
    public boolean equalLR0(LR1Item item) {
        return leftSide.equals(item.getLeftSide()) &&
                rightSide.equals(item.getRightSide()) &&
                dotPointer == item.getDotPointer();
    }

    @Override
    public String toString() {
        String str = leftSide + " -> ";
        for (int i = 0; i < rightSide.size(); i++) {
            if (i == dotPointer) {
                str += ".";
            }
            str += rightSide.get(i);
            if (i != rightSide.size() - 1) {
                str += " ";
            }
        }
        if (rightSide.size() == dotPointer) {
            str += ".";
        }
        str += " , " + lookahead;
        return str;
    }
}
