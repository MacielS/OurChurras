package com.projeto.ourchurras.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.projeto.ourchurras.R;
import com.projeto.ourchurras.adapter.AdapterProduto;
import com.projeto.ourchurras.helper.ConfiguracaoFirebase;
import com.projeto.ourchurras.model.Empresa;
import com.projeto.ourchurras.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private Empresa empresaSelecionada;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        //Configurações Iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Recupera empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            empresaSelecionada = (Empresa) bundle.getSerializable("churrasqueiros");

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome() );
            idEmpresa = empresaSelecionada.getIdUsuario();

            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCardapio);
        }

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Configurações recyclerView
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this) );
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        //Recuperar produtos para empresa
        recuperarProdutos();

    }

    private void recuperarProdutos() {
        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresa);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    produtos.add(ds.getValue(Produto.class) );

                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes() {

        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

    }
}