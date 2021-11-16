package sample;
import javafx.scene.text.Font;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.controlsfx.control.CheckComboBox;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pl.maya.desktop.chart.ChartUtils;
import pl.maya.desktop.communication.SendData;
import pl.maya.desktop.util.Utils;

import java.util.Map;

public class Controller {
    private final static String WYBIERZ_WSZYSTKIE = "Wybierz wszystkie";
    private final static int INTEGRATION_TIME = 1000;
    private final String[] tablicaFiltrow = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};
    private final String[] tablicaDiod = {"240","255","270","285"};

    private Map<String, Dataset> pomiaryDiod;
    private Map<String, Dataset> pelnePomiary;
    private Map<String, Dataset> roznicowePomiary;


    //POMIAR - ODNALEZIONE PRÓBKI
    @FXML
    private StackPane odnalezioneProbkiStack;
    @FXML
    private CheckBox automatycznaAnaliza;
    @FXML
    private ListView<String> odnalezioneProbki;
    @FXML
    private Button wczytaj;


    @FXML
    private BorderPane root;


    @FXML
    private TextField nazwaPortu;

    @FXML
    private CheckComboBox typPomiaru;

    @FXML
    private Button pomiarDiod;
    @FXML
    private Button pelenPomiar;

    @FXML
    private ProgressBar pomiarDiodProgress;

    @FXML
    private Label wykonanoPomiarow;

    private ChartViewer viewer;
    private JFreeChart jFreeChart;
    private XYPlot plot;


    //WYKRES - ZAKRES
    @FXML
    private TextField xmin;
    @FXML
    private TextField ymin;
    @FXML
    private TextField xmax;
    @FXML
    private TextField ymax;
    @FXML
    private Button wyczysc;
    @FXML
    private Button ustaw;


    public void initialize(){
        wykonanoPomiarow.setText("Wykonano " + 0 + " z " + tablicaFiltrow.length*tablicaDiod.length + " pomiarów");

        odnalezioneProbkiStack.setVisible(false);

        initChart();
        initTypPomiaru();
        initPomiarDiod();
        initPelenPomiar();

        initWykresZakres();
    }

    public void  initWykresZakres(){
        ustaw.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    plot.getRangeAxis().setRange(Double.valueOf(ymin.getText()), Double.valueOf(ymax.getText()));
                    plot.getDomainAxis().setRange(Double.valueOf(xmin.getText()), Double.valueOf(xmax.getText()));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        wyczysc.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                xmin.setText(null);
                ymin.setText(null);
                xmax.setText(null);
                ymax.setText(null);
            }
        });
    }

    public XYSeries createSeries(int ii ) {
        XYSeries series = new XYSeries(Integer.valueOf(ii));
        for (int i = 0; i < 100; i++) {
            series.add(i, Math.random());
        }
        return series;
    }

    public XYDataset createDataset(int ii ) {
        XYSeries series = new XYSeries(Integer.valueOf(ii));
        for (int i = 0; i < 100; i++) {
            series.add(i, Math.random());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    public void initChart(){
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart("Spektrometr", "Długość fali", "Wartość", dataset);
        viewer = new ChartViewer(chart,true);
        jFreeChart = viewer.getChart();
        plot = jFreeChart.getXYPlot();
        viewer.setOnContextMenuRequested(null);

        root.setCenter(viewer);


    }


    public void initPelenPomiar() {
        pelenPomiar.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Task<Integer> task = new Task<Integer>() {
                    protected Integer call(){
                        wykonajZbiorPomiarow("1");
                        if(automatycznaAnaliza.isSelected()){
                            analizuj();
                        }
                        return 1;
                    }
                };
                Thread th = new Thread(task);
                th.start();
            }
        });
    }

    public void analizuj(){
        System.out.println("analizuję");
        //TODO przepisać na algorytm
        odnalezioneProbkiStack.setVisible(true);
        odnalezioneProbki.getItems().add("Próbka nr 1");
        odnalezioneProbki.getItems().add("Próbka nr 2");
        odnalezioneProbki.getItems().add("Próbka nr 3");

    }

    public void initPomiarDiod() {
        pomiarDiod.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                        Task<Integer> task = new Task<Integer>() {
                            protected Integer call(){
                                return wykonajZbiorPomiarow("0");
                            }
                        };
                        Thread th = new Thread(task);
                        th.start();
                }
            });
    }

    public int wykonajZbiorPomiarow(String onlyDiode){
        int odpytanie = 0;
        for (int i = 1; i <=  tablicaFiltrow.length*tablicaDiod.length; i++) {
            final int index = i;
            pomiarDiod.setDisable(true);

            String filter;
            String diode;
                if (!typPomiaru.getCheckModel().isChecked(i)) {
                    continue;
                } else {
                    diode = ((String) (typPomiaru.getItems().get(i))).split("-")[1];
                    filter = ((String) (typPomiaru.getItems().get(i))).split("-")[0];
                }

            odpytanie++;
            byte [] pomiar = new SendData().wyslijRequest(Utils.prepareFrame(onlyDiode,INTEGRATION_TIME,filter,diode));
            System.out.println("Pomiar: " + pomiar.length);
            for(byte a: pomiar){
                System.out.print("0x" + Integer.toHexString(a & 0xFF)+" ");
            }
            System.out.println(pomiar.length);


            final int  liczbaWykonanychPomiarow = odpytanie;
            Platform.runLater(() -> {
                plot.setRenderer(index, new StandardXYItemRenderer());
                plot.setDataset(index, ChartUtils.getDataset(pomiar, (String)typPomiaru.getItems().get(index)));
//                Dataset xy = ChartUtils.getDataset(pomiar, (String)typPomiaru.getItems().get(index));
//                System.out.println(xy.getY(0,0));
//                System.out.println(xy.getY(0,1));

                pomiarDiodProgress.setProgress((double)liczbaWykonanychPomiarow / ((double)typPomiaru.getCheckModel().getCheckedIndices().size()-1));
                wykonanoPomiarow.setText("Wykonano " + liczbaWykonanychPomiarow + " z " + (typPomiaru.getCheckModel().getCheckedIndices().size()-1) + " pomiarów");
            });
        }



        pomiarDiod.setDisable(false);
        return 1;
    };


    public void initTypPomiaru(){
        typPomiaru.getItems().add(WYBIERZ_WSZYSTKIE);
        for(String a: tablicaFiltrow){
            for(String b: tablicaDiod){
                typPomiaru.getItems().add(a+"-"+b);
            }
        }
        for(int i=0;i<typPomiaru.getItems().size();i++) {
            typPomiaru.getCheckModel().checkIndices(i);
        }

        typPomiaru.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                if(c.next()){
                    if(c.getAddedSubList()!= null && c.getAddedSubList().size()>0 && c.getAddedSubList().get(0).equalsIgnoreCase(WYBIERZ_WSZYSTKIE)){
                        for(int i=1;i<typPomiaru.getItems().size();i++) {
                            typPomiaru.getCheckModel().checkIndices(i);
                        }
                    }
                    if(c.getRemoved()!= null && c.getRemoved().size()>0 && c.getRemoved().get(0).equalsIgnoreCase(WYBIERZ_WSZYSTKIE)){
                        for(int i=1;i<typPomiaru.getItems().size();i++) {
                            typPomiaru.getCheckModel().clearCheck(i);
                        }
                    }
                }
            }
        });
    }



}
