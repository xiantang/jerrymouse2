package info.xiantang.jerrymouse2.core.handler;

import info.xiantang.jerrymouse2.core.server.MultiReactor;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static info.xiantang.jerrymouse2.core.server.Constants.CLOSED;
import static info.xiantang.jerrymouse2.core.server.Constants.SENDING;

public class SampleBaseHandler extends BaseHandler {

    public SampleBaseHandler(MultiReactor reactor, SocketChannel channel) throws IOException {
        super(reactor, channel);
    }

    @Override
    public boolean inputIsComplete(int bytes) throws IOException {
        if (bytes > 0) {
            input.flip();
            while (input.hasRemaining()) {
                byte ch = input.get();

                if (ch == 3) {
                    setState(CLOSED);
                    return true;
                } else if (ch == '\r') {
                } else if (ch == '\n') {
                    setState(SENDING);
                    return true;
                } else {
                    request.append((char) ch);
                }
            }
        } else if (bytes == -1) {
            throw new EOFException();
        }

        return false;
    }

    @Override
    public void process(ByteBuffer output) throws EOFException{
        int state = getState();
        if (state == CLOSED) {
            throw new EOFException();
        } else if (state == SENDING) {
            String requestContent = request.toString();
            byte[] response = requestContent.getBytes(StandardCharsets.UTF_8);
            output.put(response);
        }
    }


}
