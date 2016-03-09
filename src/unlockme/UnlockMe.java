/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unlockme;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bbphuc
 */
public class UnlockMe {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        // TODO code application logic here
        State s = State.loadFromFile("F:\\m.txt");
       System.out.println(s.toString());
        List<State> newstate = s.getNewState();
        //System.out.println(" n = " + newstate.size());
        newstate.stream().forEach((object) -> {
            System.out.println(object.toString());
        });
    }
    
}
