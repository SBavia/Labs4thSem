import org.apache.commons.lang.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;


public class ByteStuffing {

    //first and last bytes are
    public final static byte F_END_SYMBOL = 0x7E; //126 ~, флаг начала пакета
    public final static byte F_ESC_SYMBOL = 0x7D; //125 }, ESC-символ
    public final static byte T_END_SYMBOL = 0x5E; //94, код замены начала пакета
    public final static byte T_ESC_SYMBOL = 0x5D; //93, END-символ


    public static byte[] doStuffing(byte[] bytes) {

        ArrayList<Byte> list = new ArrayList<>
                (Arrays.asList
                        (ArrayUtils.toObject(bytes)));

        list.add(0, F_END_SYMBOL); //устанавливаем флаг начала пакета

        for (int i = 1; i < list.size(); i++) {
            list.trimToSize();
            if (list.get(i) == F_ESC_SYMBOL) { //если найден ESC-символ,
                list.add(i+1, T_ESC_SYMBOL); //то следующим за ним ставим END-символ
                //i++;
            }
            else if (list.get(i) == F_END_SYMBOL) { //если найден символ, совпадающий с флагом начала,
                list.set(i, F_ESC_SYMBOL); //то заменяем его на ESC-символ
                list.add(i+1, T_END_SYMBOL); //и добавляем после него код замены
                //i++;
            }
        }

        Byte[] returnBytes = new Byte[list.size()];
        list.toArray(returnBytes);

        System.out.println(ArrayUtils.toPrimitive(returnBytes));

        return ArrayUtils.toPrimitive(returnBytes);
    }

    public static byte[] inject(byte[] bytes) {
        ArrayList<Byte> list = new ArrayList<>
                (Arrays.asList
                        (ArrayUtils.toObject(bytes)));
        list.remove(0);

        for (int i = 0; i < list.size(); i++) {
            list.trimToSize();
            if (list.get(i) == F_ESC_SYMBOL) { //если найден ESC-символ
                if (list.get(i + 1) == T_END_SYMBOL) { //и за ним следует код замены,
                    list.set(i, F_END_SYMBOL); //то заменяем ESC-символ на флаг начала
                    list.remove(i+1); //и удаляем код замены
                }
                else if (list.get(i+1) == T_ESC_SYMBOL) { //если найден END-символ
                    list.remove(i + 1);//то удаляем его
                    //i++;
                }
            }
        }

        Byte[] returnBytes = new Byte[list.size()];
        list.toArray(returnBytes);
        return ArrayUtils.toPrimitive(returnBytes);
    }
}
