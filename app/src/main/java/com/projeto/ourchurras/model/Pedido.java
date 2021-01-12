package com.projeto.ourchurras.model;

import com.google.firebase.database.DatabaseReference;
import com.projeto.ourchurras.helper.ConfiguracaoFirebase;

import java.util.List;

public class Pedido {

    private String idUsuario;
    private String idChurrasqueiro;
    private String idPedido;
    private String nome;
    private String endereco;
    private List <ItemPedido> itens;
    private Double total;
    private String status = "Pendente";
    private int metodoPagamento;
    private String observacao;

    public Pedido() {
    }

    public Pedido(String idUsu, String idChurras) {
        setIdUsuario(idUsu);
        setIdChurrasqueiro(idChurras);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idChurras)
                .child(idUsu);
        setIdPedido(pedidoRef.push().getKey() );

    }

    public void salvar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(getIdChurrasqueiro())
                .child(getIdUsuario());
        pedidoRef.setValue(this);
    }

    public void remover() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(getIdChurrasqueiro())
                .child(getIdUsuario());
        pedidoRef.removeValue();
    }

    public void confirmar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(getIdChurrasqueiro())
                .child(getIdPedido());
        pedidoRef.setValue(this);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdChurrasqueiro() {
        return idChurrasqueiro;
    }

    public void setIdChurrasqueiro(String idChurrasqueiro) {
        this.idChurrasqueiro = idChurrasqueiro;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
