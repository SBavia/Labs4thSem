import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private static final String MARKER = "*";
    private Timer timer;
    private StationModel stationModel;
    private byte[] addresses = { 3 , 9, 27 };


    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage stage)  {
        ArrayList<StationForm> stationForms = new ArrayList<>();
        stationForms.add(new StationForm(addresses[0],addresses[1],(byte)1));   //source - 3 | dest - 9
        stationForms.add(new StationForm(addresses[1],addresses[2], (byte)0));  //source - 9 | dest - 27
        stationForms.add(new StationForm(addresses[2], addresses[0], (byte)0)); //source - 27 | dest - 3  объявление окон

        stage = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();     //создание сцен

        stage.setX(440);
        stage.setY(320);
        stage2.setX(790);
        stage2.setY(320);
        stage3.setX(1140);
        stage3.setY(320);       //расставляем окна

        stage.setTitle("1st station");
        stage2.setTitle("2nd station");
        stage3.setTitle("3rd station");     //выставляем заголовки

        stationForms.get(0).start(stage);
        stationForms.get(1).start(stage2);
        stationForms.get(2).start(stage3);      //высставляем окна на сцены

        timer = new Timer();

        stationForms.get(0).getStartButton().setOnAction((event) -> {   //высставляем действие на кнопку старт
            stationModel = new StationModel();
            for (int i = 1; i <= 3; i++) {
                int currentStation = i;

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {         //начать выполнять задачу с интервалом
                        System.out.println(currentStation);         //выводим в консоль на какой станции сейчас маркер
                        try {
                            stationRoutine(stationForms.get(currentStation - 1), stationModel); //
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },1000, 1000);

                stationForms.get(0).getStartButton().setDisable(true);
            }
        });

        stationForms.get(0).getTokenText().textProperty().addListener(observable -> sendPackage(stationForms.get(0)));//установить слушатель, на тот случай
        stationForms.get(1).getTokenText().textProperty().addListener(observable -> sendPackage(stationForms.get(1)));//если на нас висит маркер
        stationForms.get(2).getTokenText().textProperty().addListener(observable -> sendPackage(stationForms.get(2)));//и в поле ввода есть текст для отправки
    }

    public static void stationRoutine(StationForm stationForm, StationModel myStationModal) throws InterruptedException {
        if (myStationModal.getControl() == 0) {
            myStationModal.setSource(stationForm.getSourceAddress());                                    //выставляем параметры для станции
            myStationModal.setDestination(stationForm.getDestinationAddress());                          //и сидим здесь, пока нет текста
            myStationModal.setMonitor(stationForm.getMainStation());    //установить управляющую станцию
        } else {                         //как только появился текст
            runOnUIThread(() -> {
                if (myStationModal.getDestination() == stationForm.getSourceAddress()) {    //если есть еще символы для отправки
                    String data = "";
                    data += (char) myStationModal.getData();    //получаем символ, который мы получили из ф-ии sendPackage
                    myStationModal.setStatus((byte) 1);             //выставляем когда уже получили символ
                    stationForm.getOutputArea().appendText(data);   //отображаем полученный символ на станции-приемнике
                }
                if (myStationModal.getStatus() == 1 && stationForm.getMainStation() == 1) { //заходим сюда, если до этого мы отправили символ
                    myStationModal.setControl((byte) 0);    //выставляем на случай, если символ был последним
                    myStationModal.freeData();              //очищаем поле data для последующих данных
                    myStationModal.setStatus((byte) 0);     //
                }
            });
        }
        runOnUIThread(() -> stationForm.getTokenText().setText(MARKER));
        Thread.sleep(1000);
        runOnUIThread(() -> stationForm.getTokenText().setText(""));
    }

    public void sendPackage(StationForm stationForm) {
        if (!stationForm.getInputArea().getText().equals("")    //если в поле ввода текста не пусто
                && !stationForm.getDestination().equals("")     //и у нас есть адрес куда нужно отправить
                && stationModel.getControl() == 0               //и мы не заняты(т.е. не отправляем в текущий момент)
                && stationModel != null) {                      //и станции созданы
            if (stationForm.getTokenText().getText().equals("*")) {     //если маркер у нас
                if (stationForm.getDestination().equals("3")) {
                    stationForm.setDestinationAddress(addresses[0]);
                }                                                       //выставляем адрес отправки
                if (stationForm.getDestination().equals("9")) {
                    stationForm.setDestinationAddress(addresses[1]);
                }                                                       //выставляем адрес отправки
                if (stationForm.getDestination().equals("27")) {
                    stationForm.setDestinationAddress(addresses[2]);
                }                                                       //выставляем адрес отправки
                stationModel.setControl((byte) 1);                      //показываем что теперь занято(чтобы другие не могли отправлять одновременно с нами)
                stationModel.setDestination(stationForm.getDestinationAddress());//выставляем адрес приемника
                stationModel.setSource(stationForm.getSourceAddress());             //выставляем адрес источника
                stationModel.setMonitor(stationForm.getMainStation());      //выставляем станцию-монитор

                String reduced = stationForm.getInputArea().getText().substring(1); //получаем новую подстроку(без 1-го символа) на станции-источнике
                stationModel.setData(stationForm.getInputArea().getText().getBytes()[0]);//отправляем 1ый символ в data на станцию-приемник
                stationForm.getInputArea().setText(reduced);                        //выставляем новую подстроку(без 1-го символа) на станции-источнике
            }
        }
    }

    private static void runOnUIThread(Runnable task) {
        if(task == null) throw new NullPointerException("Task can't be null");
        if(Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }
}