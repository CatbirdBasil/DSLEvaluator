package com.diploma.shaverskyi.lrparser.state;

import com.diploma.shaverskyi.lrparser.grammar.*;

import java.util.*;

public class LR1Parser extends LRParser {

    private List<LR1State> canonicalCollection;

    public LR1Parser(Grammar grammar) {
        super(grammar);
    }

    protected void createStatesForCLR1() {
        canonicalCollection = new ArrayList<>();
        Set<LR1Item> start = new HashSet<>();
        Rule startRule = grammar.getRules().get(0);
        Set<String> startLookahead = new HashSet<>();
        startLookahead.add(Constants.END_SYMBOL);
        start.add(new LR1Item(startRule.getLeftSide(), startRule.getRightSide(), 0, startLookahead));

        LR1State startState = new LR1State(grammar, start);
        canonicalCollection.add(startState);
//TODO WHY DO WE NEED FOR???
        for (int i = 0; i < canonicalCollection.size(); i++) {
            Set<String> afterDotValues = new HashSet<>();

            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                if (item.getCurrent() != null) {
                    afterDotValues.add(item.getCurrent());
                }
            }

            for (String afterDotValue : afterDotValues) {
                Set<LR1Item> nextStateItems = new HashSet<>();

                for (LR1Item item : canonicalCollection.get(i).getItems()) {
                    if (item.getCurrent() != null && item.getCurrent().equals(afterDotValue)) {
                        LR1Item temp = new LR1Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer() + 1, item.getLookahead());
                        nextStateItems.add(temp);
                    }
                }

                LR1State nextState = new LR1State(grammar, nextStateItems);
                boolean isExist = false;

                for (LR1State previousState: canonicalCollection) {
                    if (previousState.getItems().containsAll(nextState.getItems())
                            && nextState.getItems().containsAll(previousState.getItems())) {
                        isExist = true;
                        canonicalCollection.get(i).getTransition().put(afterDotValue, previousState);
                    }
                }

                if (!isExist) {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(i).getTransition().put(afterDotValue, nextState);
                }
            }
        }

    }

    public boolean parseCLR1() {
        createStatesForCLR1();
        createGoToTable();
        return createActionTable();
    }

//    public boolean parseLALR1() {
//        createStatesForLALR1();
//        createGoToTable();
//        return createActionTable();
//    }
//
//    public void createStatesForLALR1() {
//        createStatesForCLR1();
//
//        List<LR1State> temp = new ArrayList<>();
//
//        for (int i = 0; i < canonicalCollection.size(); i++) {
//            Set<String> lookahead = new HashSet<>();
//
//            HashSet<LR0Item> itemsi = new HashSet<>();
//            for (LR1Item item : canonicalCollection.get(i).getItems()) {
//                itemsi.add(new LR0Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer()));
//            }
//            for (int j = i + 1; j < canonicalCollection.size(); j++) {
//                HashSet<LR0Item> itemsj = new HashSet<>();
//                for (LR1Item item : canonicalCollection.get(j).getItems()) {
//                    itemsj.add(new LR0Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer()));
//                }
//                if (itemsi.containsAll(itemsj) && itemsj.containsAll(itemsi)) {
//                    for (LR1Item itemi : canonicalCollection.get(i).getItems()) {
//                        for (LR1Item itemj : canonicalCollection.get(j).getItems()) {
//                            if (itemi.equalLR0(itemj)) {
//                                itemi.getLookahead().addAll(itemj.getLookahead());
//                                break;
//                            }
//                        }
//                    }
//                    for (int k = 0; k < canonicalCollection.size(); k++) {
//                        for (String s : canonicalCollection.get(k).getTransition().keySet()) {
//                            if (canonicalCollection.get(k).getTransition().get(s).getItems().containsAll(canonicalCollection.get(j).getItems()) &&
//                                    canonicalCollection.get(j).getItems().containsAll(canonicalCollection.get(k).getTransition().get(s).getItems())) {
//                                canonicalCollection.get(k).getTransition().put(s, canonicalCollection.get(i));
//                            }
//                        }
//                    }
//                    canonicalCollection.remove(j);
//                    j--;
//
//                }
//            }
//            temp.add(canonicalCollection.get(i));
//        }
//        canonicalCollection = temp;
//    }

    protected void createGoToTable() {
        goToTable = new ArrayList<>();

        for (int i = 0; i < canonicalCollection.size(); i++) {
            goToTable.add(new HashMap<String, Integer>());
        }

        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String transition : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.hasNonTerminal(transition)) {
                    goToTable.get(i).put(transition,
                            findStateIndex(canonicalCollection.get(i).getTransition().get(transition)));
                }
            }
        }
    }

    private int findStateIndex(LR1State state) {
        for (int i = 0; i < canonicalCollection.size(); i++) {
            if (canonicalCollection.get(i).equals(state)) {
                return i;
            }
        }

        return -1;
    }

    private boolean createActionTable() {
        actionTable = new ArrayList<>();

        for (int i = 0; i < canonicalCollection.size(); i++) {
            actionTable.add(new HashMap<String, Action>());
        }

        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String transition : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getTerminals().contains(transition)) {
                    actionTable.get(i).put(transition, new Action(ActionType.SHIFT,
                            findStateIndex(canonicalCollection.get(i).getTransition().get(transition))));
                }
            }
        }

        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().size()) {
                    if (item.getLeftSide().equals(Constants.BEGIN_SYMBOL)) {
                        actionTable.get(i).put(Constants.END_SYMBOL, new Action(ActionType.ACCEPT, 0));
                    } else {
                        Rule rule = new Rule(item.getLeftSide(), item.getRightSide());
                        int index = grammar.findRuleIndex(rule);
                        Action action = new Action(ActionType.REDUCE, index);
                        for (String lookahead : item.getLookahead()) {
                            if (actionTable.get(i).get(lookahead) != null) {
                                System.out.println("it has a REDUCE-" + actionTable.get(i).get(lookahead).getType() + " confilct in state " + i);
                                return false;
                            } else {
                                actionTable.get(i).put(lookahead, action);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public String canonicalCollectionStr() {
        String str = "Canonical Collection : \n";
        for (int i = 0; i < canonicalCollection.size(); i++) {
            str += "State " + i + " : \n";
            str += canonicalCollection.get(i) + "\n";
        }
        return str;
    }

}
