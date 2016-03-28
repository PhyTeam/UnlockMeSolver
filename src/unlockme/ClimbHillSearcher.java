package unlockme;

import java.util.HashSet;
import java.util.List;

/**
 *
 * @author bbphuc
 */
public class ClimbHillSearcher extends AbstractSearcher {

    @Override
    public boolean search(State state) {
        Node head = new Node(0, state, null);
        HashSet<State> closed = new HashSet<>();
        Node current = head;
        while(true){
            closed.add(current.state);
            if(current.state.checkGoal()){
                last_node = current;
                return true;
            }
            
            List<State> newstate = current.state.getNewState();
            double best_heuristic_value = current.state.getEvaluationValueFromCache();
            State best_state = null;
            
            for(State s : newstate){
                if(closed.contains(s)) continue;
                double h = s.getEvaluationValueFromCache();
                if(best_heuristic_value >= h){
                    best_state = s;
                    best_heuristic_value = h;
                }
            }
            if(best_state == null) return false;
            current = new Node(current.level + 1, best_state, current);
        }
    }
    
}
