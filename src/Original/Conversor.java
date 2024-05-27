package Original;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Conversor {
    private static final String API_URL_TEMPLATE = "https://v6.exchangerate-api.com/v6/eb30d5f36896ba5bd0df1e9f/latest/";
    private String moeda;
    private String converterMoeda;
    private String valor;

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public String getConverterMoeda() {
        return converterMoeda;
    }

    public void setConverterMoeda(String converterMoeda) {
        this.converterMoeda = converterMoeda;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public void exibirMenuConversao() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Seja bem-vindo ao conversor de moeda\n" +
                    "Digite - 1 - para começar\n" +
                    "Digite - 0 - para sair\n");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 1) {
                coletarDados(scanner);
                realizarConversao();
            } else if (opcao == 0) {
                System.out.println("Obrigado, Até mais.");
                break;
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }


    //pegando info dos cliente para saber qual moeda ele tem e para qual quer converter
    private void coletarDados(Scanner scanner) {
        System.out.println("Digite a sigla de qual moeda você está usando, por favor!");
        setMoeda(scanner.nextLine().trim());
        validarEntrada(getMoeda(), "A moeda não pode ser vazia");

        System.out.println("Digite a sigla para qual moeda deseja converter, por favor!");
        setConverterMoeda(scanner.nextLine().trim());
        validarEntrada(getConverterMoeda(), "A moeda para converter não pode ser vazia");

        System.out.println("Digite o valor que deseja converter, por favor!");
        setValor(scanner.nextLine().trim());
        validarEntrada(getValor(), "O valor não pode ser vazio");
    }

    //verificando se as entradas nao estao nulas
    private void validarEntrada(String entrada, String mensagemErro) {
        if (entrada.isEmpty()) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private void realizarConversao() throws IOException, InterruptedException {
        try {
            String endereco = API_URL_TEMPLATE + getMoeda();
            // criando o cliente
            HttpClient client = HttpClient.newHttpClient();
            //fazendo a requisição
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endereco))
                    .build();
            // gerando resposta
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                processarRespostaApi(response.body());
            } else {
                System.out.println("Erro ao acessar a API, Código de status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
    }

    //metodo para verificar a resposta da api e pegar a taxa de conversao e calculcar o valor final
    //utliliza aqui tambem a verificaçoes par ver se a resposta nao esta nula e utiliza o gson para converter a api response
    private void processarRespostaApi(String apiResponse) {
        if (apiResponse != null && !apiResponse.isEmpty()) {
            JsonObject jsonResposta = JsonParser.parseString(apiResponse).getAsJsonObject();

            if (jsonResposta.has("conversion_rates")) {
                JsonObject conversionRates = jsonResposta.getAsJsonObject("conversion_rates");
                // pegando taxa de conversao
                if (conversionRates.has(getConverterMoeda())) {
                    double taxaConversao = conversionRates.get(getConverterMoeda()).getAsDouble();
                    System.out.println("Taxa de conversão para " + getConverterMoeda() + ": " + taxaConversao);
                    // tranformando de styring para double
                    double valorOriginal = Double.parseDouble(getValor());
                    double valorConvertido = valorOriginal * taxaConversao;
                    System.out.println("Valor Convertido: " + valorConvertido);
                } else {
                    System.out.println("Erro: Moeda de conversão não encontrada na resposta da API.");
                }
            } else {
                System.out.println("Erro: Resposta da API não contém 'conversion_rates'.");
            }
        } else {
            System.out.println("Erro: A resposta da API está vazia ou é nula.");
        }
    }
}
