package unlockme;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author bbphuc
 */
public class BreathFirsrSearcher extends AbstractSearcher{

    private final Queue<Node> queue = new ArrayDeque<>();
    
    @Override
    public boolean search(State state) {
        Node head = new Node(0, state);
        return search_r(head);
    }
    
    protected boolean search_r(Node node){
        if (node.state.checkGoal()){
            this.last_node = node;
            // Success
            return true;
        }
            
        // Generate next states
        List<State> newstates = node.state.getNewState();
        newstates.stream().forEach((state) -> {
            Node new_node = new Node(node.level + 1, state);
            new_node.preNode = node;
            queue.offer(new_node);
        });
        
        // If there is not has any state stop algorithm
        if(!queue.isEmpty()){
            return search_r(queue.poll());
        }
        else
            return false;
    }
    
}
