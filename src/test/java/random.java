import java.util.Random;

public class random {
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Random r = new Random();
            int radomNum = r.nextInt(300)%(300-170+1) + 170;
            System.out.println(radomNum);
        }
    }
}
