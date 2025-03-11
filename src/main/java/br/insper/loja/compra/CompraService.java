package br.insper.loja.compra;

import br.insper.loja.evento.EventoService;
import br.insper.loja.produto.Produto;
import br.insper.loja.produto.ProdutoService;
import br.insper.loja.usuario.Usuario;
import br.insper.loja.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ProdutoService produtoService;

    public Compra salvarCompra(Compra compra) {
        Usuario usuario = usuarioService.getUsuario(compra.getUsuario());
        compra.setNome(usuario.getNome());
        compra.setDataCompra(LocalDateTime.now());

        double precoTotal = 0.0;

        for (String produtoId : compra.getProdutos()) {
            Produto produto = produtoService.buscarPorId(produtoId);

            if (produto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + produtoId);
            }

            if (produto.getQuantidadeEstoque() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto sem estoque: " + produto.getNome());
            }

            // Decrementa o estoque
            produtoService.diminuirEstoque(produtoId, 1);

            // Adiciona o preço ao total
            precoTotal += produto.getPreco();
        }

        compra.setPrecoTotal(precoTotal);

        eventoService.salvarEvento(usuario.getEmail(), "Compra realizada no valor de R$ " + precoTotal);
        return compraRepository.save(compra);
    }

    public List<Compra> getCompras() {
        return compraRepository.findAll();
    }
}