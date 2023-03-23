package afsp.util;

import java.nio.file.FileSystems;

public enum ByteCode {
    SP(0x20),
    CR(0x0D),
    LF(0x0A),
    COL(0x3A),
    ESC(0x5C);
    public int code;
    ByteCode(int _byte){
        this.code = _byte;
    }
}
