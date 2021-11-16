package pl.maya.desktop;

import pl.maya.desktop.communication.SendData;
import pl.maya.desktop.communication.TcpClient;
import pl.maya.desktop.util.DiodeE;
import pl.maya.desktop.util.Utils;


public class StartApplication {
    public static void main(String[]  args){
        String onlyDiode = "0";
        int integrationTime= 1000;
        String filter = "3";
        String diode = DiodeE.D240.getValue();

        byte[] bytes = new SendData().wyslijRequest(Utils.prepareFrame(onlyDiode,integrationTime,filter,diode));

        System.out.println("Response: " + bytes.length);
        for(byte a: bytes){
            System.out.print("0x" + Integer.toHexString(a & 0xFF)+" ");
        }
    }


}
