import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.util.Random;

public class CollisionController {
    private static final int MAX_ATTEMPTS = 10;
    private static final char COLLISION_SYMBOL = '~';
    private static final int COLLISION_DURATION = 30;
    private static final int SLOT_TIME = 35;


    private byte buffer = 0;

    @FXML private Button sendButton;
    @FXML private Button clearButton;
    @FXML private TextArea inputZone;
    @FXML private TextArea outputZone;
    @FXML private TextArea debugZone;

    private void send(byte data) {
        this.buffer = data;
    }

    private boolean isCollision() {
        return (System.currentTimeMillis() % 2) == 1;
    }

    boolean isPackageMode() {
        return false;
    }

    private boolean isChannelFree() {
        return (System.currentTimeMillis() % 2) == 1;
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runOnUIThread(Runnable task) {
        if(task == null) throw new NullPointerException("Task can't be null.");
        if(Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }

    private class SendTask extends Task<Void> {

        int i = 1;

        @Override
        protected Void call() {
            runOnUIThread(() -> {
                inputZone.setEditable(false);
                sendButton.setDisable(true);
                clearButton.setDisable(true);
            });

            byte[] line = inputZone.getText().getBytes();

            for (byte symbol: line) {
                StringBuilder collisions = new StringBuilder();
                int attempts = 0;                                //обнулить счетчик попыток
                boolean sending = true;
                if (!isPackageMode()) {                          //пакетный режим?
                    while (sending) {                            //пока кол-во попыток <10
                        if (isChannelFree()) {                   //канал свободен?
                            send(symbol);                            //передать кадр
                            sleep(COLLISION_DURATION);               //выждать окно коллизий

                            if (isCollision()) {                       //коллизия обнаружена?
                                collisions.append(COLLISION_SYMBOL); //отправить джем-сигнал
                                attempts += 1;                       //инкрементировать счетчик попыток

                                if (attempts >= MAX_ATTEMPTS) {       //попыток слишком много?
                                sending = false;
                                } else {
                                    Random rand = new Random();
                                    int k = Math.min(attempts, MAX_ATTEMPTS);
                                    int r = rand.nextInt((int) Math.pow(2, k));
                                    sleep(r * SLOT_TIME);          //вычислить и выждать случайную задержку
                                    }
                            } else {
                                runOnUIThread(() -> {
                                    outputZone.appendText((char) symbol + "");
                                    debugZone.appendText("Line" + i++ + " ");
                                    //i++;
                                    debugZone.appendText(collisions + "\n");
                                });
                                sending = false;
                            }
                        }
                    }
                }
            }
            runOnUIThread(() -> {
                inputZone.setEditable(true);
                sendButton.setDisable(false);
                clearButton.setDisable(false);
            });
            inputZone.setText("");
            outputZone.appendText("\n");
            return null;
        }
    }

    @FXML protected void onClear(ActionEvent event) {
        inputZone.setText("");
        outputZone.setText("");
        debugZone.setText("");
    }

    @FXML protected void onSend(ActionEvent event) { new Thread(new SendTask()).start(); }
}