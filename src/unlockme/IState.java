/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unlockme;

import java.util.List;

/**
 *
 * @author bbphuc
 */
interface IState {
    public boolean checkGoal();
    public List<State> getNewState();
    public double evaluationFunction();
}
