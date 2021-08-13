package blockchain.Block.Persistance;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

public class ToFile implements PersistBlocks {

    @Override
    public <Block> Stream<Block> persistBlockChain(List<Block> blockList) {
        OutputStream is = null;
        ObjectOutputStream oos = null;
        try {
            is = new FileOutputStream("out.bin");

            oos = new ObjectOutputStream(is);
            oos.writeObject(blockList);

        } catch (IOException ioXception) {
            ioXception.printStackTrace();
        } finally {
            try {
                assert oos != null;
                oos.close();
                is.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }
        return null;
    }

}
