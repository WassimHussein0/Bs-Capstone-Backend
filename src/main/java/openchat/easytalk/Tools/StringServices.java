package openchat.easytalk.Tools;

public class StringServices {


    public static String combinedInOrder(String s1, String s2) {
        return s1.compareTo(s2) < 0 ? new String(s1 + s2)
                : new String(s2 + s1);
    }

    public static String combinedInOrder(Long s1, Long s2) {

        return s1.toString().compareTo(s2.toString()) < 0 ? new String(s1 + "_" + s2)
                : new String(s2 + "_" + s1);
    }

}
