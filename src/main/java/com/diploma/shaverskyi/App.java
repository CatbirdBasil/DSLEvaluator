package com.diploma.shaverskyi;

import com.diploma.shaverskyi.lrparser.grammar.Grammar;
import com.diploma.shaverskyi.lrparser.state.LR1Parser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        LR1Parser parser = new LR1Parser(new Grammar(
                        "S -> A A \n" +
                        "A -> a A | b"
        ));
        parser.parseCLR1();
        System.out.println(parser.canonicalCollectionStr());
    }
}
