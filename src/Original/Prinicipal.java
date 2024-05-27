package Original;

import java.io.IOException;

public class Prinicipal {
    public static void main(String[] args) throws IOException, InterruptedException {
        Conversor conversor = new Conversor();
        try {
            conversor.exibirMenuConversao();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
