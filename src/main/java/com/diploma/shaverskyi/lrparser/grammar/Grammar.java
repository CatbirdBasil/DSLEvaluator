package com.diploma.shaverskyi.lrparser.grammar;

import lombok.Getter;

import java.util.*;

import static com.diploma.shaverskyi.lrparser.grammar.Constants.BEGIN_SYMBOL;
import static com.diploma.shaverskyi.lrparser.grammar.Constants.END_SYMBOL;
import static com.diploma.shaverskyi.lrparser.grammar.Constants.EPSILON;

@Getter
public class Grammar {
    private List<Rule> rules;
    private Set<String> terminals;
    private Set<String> nonTerminals;
    private String startNonTerminal;
    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;

    // TODO Refactor
    public Grammar(String s) {
        rules = new ArrayList<>();
        terminals = new HashSet<>();
        nonTerminals = new HashSet<>();
        int line = 0;

        for (String st : s.split("\n")) {
            String[] sides = st.split("->");
            String leftSide = sides[0].trim();
            nonTerminals.add(leftSide);
            String[] rulesRightSide = sides[1].trim().split("\\|");

            for (String rule : rulesRightSide) {
                List<String> rightSide = Arrays.asList(rule.trim().split("\\s+"));
                terminals.addAll(rightSide);

                if (line == 0) {
                    startNonTerminal = leftSide;
                    rules.add(new Rule("S'", Collections.singletonList(startNonTerminal)));
                }

                rules.add(new Rule(leftSide, rightSide));
                line++;
            }
        }

        terminals.removeAll(nonTerminals);
        System.out.println("Rules: ");


        for (int i = 0; i < rules.size(); i++) {
            System.out.println(i + " : " + rules.get(i));
        }

        computeFirstSets();
        computeFollowSet();
    }

    private void computeFirstSets() {
        firstSets = new HashMap<>();

        for (String s : nonTerminals) {
            Set<String> temp = new HashSet<>();
            firstSets.put(s, temp);
        }

        while (true) {
            boolean hasChanged = false;

            for (String nonTerminal : nonTerminals) {
                Set<String> firstSet = new HashSet<>();

                Set<Rule> producedRules = getRulesByLeftNonTerminal(nonTerminal);
                for (Rule rule : producedRules) {
                    Set<String> firstTerminals = getFirstTerminalsByIndex(rule.getRightSide(), 0);
                    firstSet.addAll(firstTerminals);
                }

                if (!firstSets.get(nonTerminal).containsAll(firstSet)) {
                    firstSets.get(nonTerminal).addAll(firstSet);
                    hasChanged = true;
                }
            }

            // What?
            if (!hasChanged) {
                break;
            }
        }

        firstSets.put(BEGIN_SYMBOL, firstSets.get(startNonTerminal));
    }

    private void computeFollowSet() {
        followSets = new HashMap<>();

        for (String s : nonTerminals) {
            Set<String> temp = new HashSet<>();
            followSets.put(s, temp);
        }

        Set<String> start = new HashSet<>();
        start.add(END_SYMBOL);
        followSets.put(BEGIN_SYMBOL, start);

        while (true) {
            boolean hasChanged = false;

            for (String nonTerminal : nonTerminals) {
                for (Rule rule : rules) {
                    for (int i = 0; i < rule.getRightSide().size(); i++) {
                        if (rule.getRightSide().get(i).equals(nonTerminal)) {
                            if (i == rule.getRightSide().size() - 1) {
                                followSets.get(nonTerminal).addAll(followSets.get(rule.leftSide));
                            } else {
                                Set<String> firstTerminals = getFirstTerminalsByIndex(rule.getRightSide(), i + 1);

                                if (firstTerminals.contains(EPSILON)) {
                                    firstTerminals.remove(EPSILON);
                                    firstTerminals.addAll(followSets.get(rule.leftSide));
                                }

                                if (!followSets.get(nonTerminal).containsAll(firstTerminals)) {
                                    hasChanged = true;
                                    followSets.get(nonTerminal).addAll(firstTerminals);
                                }
                            }
                        }
                    }
                }
            }

            if (!hasChanged) {
                break;
            }
        }
    }

    // TODO Wut?
    public Set<String> getFirstTerminalsByIndex(List<String> possibleValues, int index) {
        Set<String> firstTerminals = new HashSet<>();

        if (index >= possibleValues.size()) {
            return firstTerminals;
        }

        String value = possibleValues.get(index);

        if (terminals.contains(value)) {
            firstTerminals.add(value);
            return firstTerminals;
        }

        if (nonTerminals.contains(value)) {
            firstTerminals.addAll(firstSets.get(value));
        }

        if (firstTerminals.contains(EPSILON) && index != possibleValues.size() - 1) {
            firstTerminals.addAll(getFirstTerminalsByIndex(possibleValues, index + 1));
            firstTerminals.remove(EPSILON);
        }

        return firstTerminals;
    }

    public Set<Rule> getRulesByLeftNonTerminal(String variable) {
        Set<Rule> result = new HashSet<>();

        if (variable == null) {
            return result;
        }

        for (Rule rule : rules) {
            if (variable.equals(rule.getLeftSide())) {
                result.add(rule);
            }
        }

        return result;
    }

    public int findRuleIndex(Rule rule) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).equals(rule)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasNonTerminal(String s) {
        return nonTerminals.contains(s);
    }
}

