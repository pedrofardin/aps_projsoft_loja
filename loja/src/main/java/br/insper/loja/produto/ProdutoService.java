package br.insper.loja.produto;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProdutoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://localhost:8080/api/produto";

    public Produto criarProduto(String nome, double preco) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);

        restTemplate.postForEntity(BASE_URL, produto, Produto.class);
        return produto;
    }

    public Produto buscarPorId(String id) {
        ResponseEntity<Produto> response = restTemplate.getForEntity(BASE_URL + "/" + id, Produto.class);
        return response.getBody();
    }

    public Produto[] listarTodos() {
        ResponseEntity<Produto[]> response = restTemplate.getForEntity(BASE_URL, Produto[].class);
        return response.getBody();
    }

    public Produto diminuirEstoque(String id, int quantidade) {
        String url = BASE_URL + "/" + id + "/diminuir?quantidade=" + quantidade;
        ResponseEntity<Produto> response = restTemplate.postForEntity(url, null, Produto.class);
        return response.getBody();
    }
}