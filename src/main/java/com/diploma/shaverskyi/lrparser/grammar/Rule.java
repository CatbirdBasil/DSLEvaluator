package com.diploma.shaverskyi.lrparser.grammar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class Rule {
    // TODO Do we need protected?
    protected String leftSide;
    protected List<String> rightSide;

    public Rule(String leftSide, List<String> rightSide) {
        this.leftSide = leftSide;
        this.rightSide = new ArrayList<>(rightSide);
    }

    public Rule(Rule rule) {
        this.leftSide = rule.leftSide;
        this.rightSide = new ArrayList<>(rule.rightSide);
    }
}

