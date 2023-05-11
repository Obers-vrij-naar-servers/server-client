import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class InputStreamMocker {

    public InputStreamReader getReader(String response){
        ByteBuffer buffer = createByteBufferFromString(response);
        InputStream inputStream = new ByteArrayInputStream(buffer.array());
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    private ByteBuffer createByteBufferFromString(String str) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());
        buffer.flip();
        return buffer;
    }
}
