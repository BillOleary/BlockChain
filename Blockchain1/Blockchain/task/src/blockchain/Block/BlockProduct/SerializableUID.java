package blockchain.Block.BlockProduct;

enum SerializableUID {
    UID(1L);

    long serialUID;
    SerializableUID(long l) {
        this.serialUID = l;
    }

    public long getSerialUID() {
        return serialUID;
    }

    public void setSerialUID(long serialUID) {
        this.serialUID = serialUID;
    }
}
