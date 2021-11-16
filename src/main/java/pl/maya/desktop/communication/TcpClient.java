package pl.maya.desktop.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class TcpClient {
    public byte[] wyslijRequest(String request){
        try {
            Socket socket = new Socket("localhost", 4444);
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            InputStream in = socket.getInputStream();
            outToServer.write(request.getBytes());
            byte[] bytes = new byte[4609];
            in.read(bytes);
            socket.close();
            return bytes;
        }catch(Exception e){
            e.printStackTrace();
        }
            return null;
    }
}
