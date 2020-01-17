public interface serialinter {

    boolean write(byte[] bytesCount, boolean flagCRC);

    byte[] read(int bytesCount, boolean flagCRC);

    boolean open();

    boolean close();

    void setParams(int baudRate, int dataBits, int stopBits, int parity);
}
