package com.projeto.ourchurras.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projeto.ourchurras.R;
import com.projeto.ourchurras.model.Empresa;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

    private List<Empresa> empresas;

    public AdapterEmpresa(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Empresa empresa = empresas.get(i);
        holder.nomeChurrasqueiro.setText(empresa.getNome());
        holder.categoria.setText(empresa.getServicos() + "");
        holder.telefone.setText(empresa.getTelefone() + "");

        //Carregar imagem
        String urlImagem = empresa.getUrlImagem();
        if (urlImagem != null && !urlImagem.isEmpty() ){
            Picasso.get().load( urlImagem ).into(holder.imagemChurrasqueiro);

        }

    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemChurrasqueiro;
        TextView nomeChurrasqueiro;
        TextView categoria;
        TextView telefone;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeChurrasqueiro = itemView.findViewById(R.id.textNomeChurrasqueiroCardapio);
            categoria = itemView.findViewById(R.id.textCategoriaChurrasqueiro);
            telefone = itemView.findViewById(R.id.textTelefoneChurrasqueiro);
            imagemChurrasqueiro = itemView.findViewById(R.id.imageChurrasqueiroCardapio);
        }
    }
}
