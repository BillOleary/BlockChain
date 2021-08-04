package blockchain.Block.BlockProduct;

import blockchain.Block.GUI.BlockObserver;

/*
* Observer Pattern Used to Update the GUI data
* Use the Subject to register Observers to be updated on any changes to the
* state of the Concrete Subject.
* In our case the BlockManager class is the Subject to which the GUI will listen.
* The GUI will be an Observer and monitor the state of the BlockChain (the block list
* will be monitored).
 */
public interface Subject {

    void registerObservers(BlockObserver observer);
    void removeObservers(BlockObserver blockObserver);
    void notifyObservers();

}
