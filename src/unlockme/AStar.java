/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unlockme;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/**
 *
 * @author NhatNam
 */
public class AStar extends AbstractSearcher {
    HashSet<State> visitedState = new HashSet<>();
    PriorityQueue<Node> openSet = new PriorityQueue<>();
    @Override
    public boolean search(State state){
        State.setAllowCacheValue(true);
        Node start = new Node(0,state,null);
        openSet.add(start);
        
        while(!openSet.isEmpty()){
            //State cur = openSet.
            Node cur = openSet.poll();
            // check goal
            if(cur.state.checkGoal()){
                last_node = cur;
                return true;
            }
            
            visitedState.add(cur.state);
            List<State> newStates = cur.state.getNewState();
            newStates.stream().map((nstate) -> new Node(cur.level + 1, nstate, cur)).forEach((childNode) -> {
                if(!visitedState.contains(childNode.state)){
                    if (!openSet.contains(childNode)){
                        openSet.add(childNode);
                    }
                }
            });
        }
        
        return false;
        //return false;
    }
}