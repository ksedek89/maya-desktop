package pl.maya.desktop.chart;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartUtils {
    public static XYDataset getDataset(byte [] b, String nazwaPomiaru){
        final XYSeries series = new XYSeries(nazwaPomiaru);
        int counter = 0;
        if(b.length>4136){
            for(int i=0;i<4135;i=i+2){
                int a= b[i+1];
                a = a<<8;
                series.add(counter, a+b[i]);
                counter++;
            }
        }
        return new XYSeriesCollection(series);
    }

}
