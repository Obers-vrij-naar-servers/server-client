package afsp;

public enum ByteCode {
    SP(0x20),
    CR(0x0D),
    LF(0x0A),
    COL(0x3A);
    public int code;
    ByteCode(int _byte){
        this.code = _byte;
    }
}
