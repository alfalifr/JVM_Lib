import java.nio.charset.StandardCharsets;

public class StdTestJv {
    public static void main(String[] args) {
        String toPrint = "";
        String string100 = "";
        int max = Integer.MAX_VALUE -100;
        for (int i = 0; i < 100; i += 10) {
            string100 += "1234567ñ90";
            System.out.println(string100);
        }
        System.out.println("============ +100 =============");
        for (int i = 0; i < max; i += 100) {
            toPrint += string100;
            System.out.println(toPrint);
        }
        System.out.println("String complete!");
        byte[] byteArray = toPrint.getBytes(StandardCharsets.UTF_8);
        System.out.println(byteArray.length);
        System.exit(0);
    }
}
