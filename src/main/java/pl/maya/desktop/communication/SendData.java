package pl.maya.desktop.communication;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class SendData {
    public byte[] wyslijRequest(String request){
        try {
            //TODO ZMIENIC IMPLEMENTACJE
            TcpClient tcpClient = new TcpClient();
            byte[] bytes = tcpClient.wyslijRequest(request);

            System.out.println("Response: " + bytes.length);
            for(byte a: bytes){
                System.out.print("0x" + Integer.toHexString(a & 0xFF)+" ");
            }

            //blad
            if(bytes[0]== 0x30){
                throw new Exception("Blad");
            }
            return Arrays.copyOfRange(bytes,1,bytes.length);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
