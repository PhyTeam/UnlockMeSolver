/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unlockme;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javafx.util.Pair;

/**
 *
 * @author bbphuc
 */
public class Searcher {
    private class Tuple{
        public int key;
        public Tuple preNode = null;
        public State state;

        public Tuple(int key, State state) {
            this.key = key;
            this.state = state;
        }
        
        
        
    }
    Queue<Tuple> q = new ArrayDeque<>();
    List<State> solution = new ArrayList<>();
    List<Tuple> history = new ArrayList<>();
    public List<State> getSolution(){
        return solution;
    }
    int k = 1;
    Tuple laststate = null;
    
    public boolean path(State state){
        return path(new Tuple(1, state));
    }
    
    public void print(){
        print(laststate);
    }
    
    private void print(Tuple t){
        if(t != null){
            System.out.println(t.state);
            print(t.preNode);
        }
    }
    
    private boolean path(Tuple t){
        // Ok! end now
        
        if (t.state.checkGoal()){
            laststate = t;
            return true;
        }
            
        // Generate next states
        List<State> newstates = t.state.getNewState();
        System.out.println(" STEP  = " + k++);
        newstates.stream().forEach((n) -> {
            Tuple tp = new Tuple(t.key + 1, n);
            tp.preNode = t;
            history.add(tp);
            q.offer(tp);
        });
        
        if(!q.isEmpty()){
            return path(q.poll());
        }
        else
            return false;
        
        /*
        q.addAll(newstates);
        // If queue is empty
        if(q.isEmpty())
            return false;
        
        // Call next in queue
        State next = q.poll();
        //System.out.println(next);
        boolean result =  path(next);
        if(result)
            this.solution.add(next);
        
        return result;*/
    }
}
