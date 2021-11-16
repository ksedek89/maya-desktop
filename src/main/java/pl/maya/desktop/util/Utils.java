package pl.maya.desktop.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String prepareFrame(String onlyDiode, int integrationTime, String filter, String diode){
        SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm:ss");
        String time = dt1.format(new Date());
        return time+","+onlyDiode+","+integrationTime+","+filter+","+diode+","+"md5,";
    }

}
