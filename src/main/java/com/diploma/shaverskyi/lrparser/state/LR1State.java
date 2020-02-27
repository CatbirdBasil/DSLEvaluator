package com.diploma.shaverskyi.lrparser.state;

import com.diploma.shaverskyi.lrparser.grammar.Constants;
import com.diploma.shaverskyi.lrparser.grammar.Grammar;
import com.diploma.shaverskyi.lrparser.grammar.Rule;
import lombok.Getter;

import java.util.*;

@Getter
public class LR1State {
    private Set<LR1Item> items;
    private Map<String, LR1State> transition;

    public LR1State(Grammar grammar, Set<LR1Item> coreItems) {
        items = new LinkedHashSet<>(coreItems);
        transition = new HashMap<>();

        closure(grammar);
    }

    private void closure(Grammar grammar) {
        boolean changeFlag;

        do {
            changeFlag = false;
            Set<LR1Item> temp = new HashSet<>();

            for (LR1Item item : items) {
                if (item.getDotPointer() != item.getRightSide().size() && grammar.hasNonTerminal(item.getCurrent())) {
                    Set<String> lookahead = new HashSet<>();

                    if (item.getDotPointer() == item.getRightSide().size() - 1) {
                        lookahead.addAll(item.getLookahead());
                    } else {
                        Set<String> firstTerminals = grammar.
                                getFirstTerminalsByIndex(item.getRightSide(), item.getDotPointer() + 1);

                        if (firstTerminals.contains(Constants.EPSILON)) {
                            firstTerminals.remove(Constants.EPSILON);
                            firstTerminals.addAll(item.getLookahead());
                        }

                        lookahead.addAll(firstTerminals);
                    }

                    Set<Rule> rules = grammar.getRulesByLeftNonTerminal(item.getCurrent());
                    for (Rule rule : rules) {
                        temp.add(new LR1Item(rule.getLeftSide(), rule.getRightSide(), 0, lookahead));
                    }
                }
            }

            if (!items.containsAll(temp)) {
                items.addAll(temp);
                changeFlag = true;
            }
        } while (changeFlag);

    }

    @Override
    public String toString() {
        String s = "";
        for (LR1Item item : items) {
            s += item + "\n";
        }
        return s;
    }

}
