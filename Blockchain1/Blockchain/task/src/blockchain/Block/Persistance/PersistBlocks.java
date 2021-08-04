package blockchain.Block.Persistance;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public interface PersistBlocks extends Serializable {


    <Block> Stream<Block> persistBlockChain(List<Block> blockList);
}
