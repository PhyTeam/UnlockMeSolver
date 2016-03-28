package unlockme;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author bbphuc
 */
public class BreathFirsrSearcher extends AbstractSearcher{

    private final Queue<Node> queue = new LinkedList<>();
    private final HashSet<State> visitedState = new HashSet<>();
    @Override
    public boolean search(State state) {
        State.setAllowCacheValue(false);
        Node head = new Node(0, state, null);
        // Add initial state
        visitedState.add(state);
        queue.add(head);
        while(!queue.isEmpty()) {
            final Node node = queue.poll();
            if (node.state.checkGoal()){
                this.last_node = node;
                // Success
                return true;
            }
            // Mark has been visited this state
            visitedState.add(node.state);
            
            // Generate next states
            List<State> newstates = node.state.getNewState();
            newstates.stream().map((nstate) -> new Node(node.level + 1, nstate, node)).forEach((childNode) -> {
                if(!visitedState.contains(childNode.state)){
                    visitedState.add(childNode.state);
                    queue.offer(childNode);
                }
                
            });
        }
        return false;
    }
    
}
