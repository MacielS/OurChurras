package com.projeto.ourchurras.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projeto.ourchurras.R;
import com.projeto.ourchurras.helper.ConfiguracaoFirebase;
import com.projeto.ourchurras.helper.UsuarioFirebase;
import com.projeto.ourchurras.model.Empresa;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaServicos,
                     editEmpresaTempo, editEmpresaValor;
    private ImageView imagePerfilEmpresa;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        //Configurações Iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        /*Recuperar dados da empresa*/
        recuperarDadosEmpresa();

    }
    private void recuperarDadosEmpresa() {
       DatabaseReference empresaRef = firebaseRef
               .child("empresas")
               .child(idUsuarioLogado);
       empresaRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               if (dataSnapshot.getValue() != null) {
                   Empresa empresa = dataSnapshot.getValue(Empresa.class);
                   editEmpresaNome.setText(empresa.getNome() );
                   editEmpresaServicos.setText(empresa.getServicos() );
                   editEmpresaValor.setText(empresa.getPrecoServico().toString() );
                   editEmpresaTempo.setText(empresa.getTempo() );

                   urlImagemSelecionada = empresa.getUrlImagem();
                   if (!urlImagemSelecionada.equals("") ) {
                       Picasso.get()
                               .load(urlImagemSelecionada)
                               .into(imagePerfilEmpresa);
                   }
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }

    public void validarDadosEmpresa(View view) {

        //Valida se os campos foram preenchidos
        String nome = editEmpresaNome.getText().toString();
        String valor = editEmpresaValor.getText().toString();
        String servicos = editEmpresaServicos.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();

        if (!nome.isEmpty()) {
            if (!valor.isEmpty()) {
                if (!servicos.isEmpty()) {
                    if (!tempo.isEmpty()) {

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setPrecoServico(Double.parseDouble(valor));
                        empresa.setServicos(servicos);
                        empresa.setTempo(tempo);
                        empresa.setUrlImagem(urlImagemSelecionada);
                        empresa.salvar();
                        finish();

                    }else{
                        exibirMensagem("Digite o tempo de serviço");
                    }
                }else{
                    exibirMensagem("Digite os serviços prestados");
                }
            }else{
                exibirMensagem("Digite um valor a ser cobrado");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa");
        }

    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        assert data != null;
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), localImagem);
                        break;
                }

                if (imagem != null) {

                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    //Retorna objeto que irá controlar o upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {


                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                            "Upload da imagem falhou",
                                            Toast.LENGTH_LONG).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    urlImagemSelecionada = url.toString();
                                }
                            });
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void inicializarComponentes() {
        editEmpresaNome = findViewById(R.id.editProdutoNome);
        editEmpresaServicos = findViewById(R.id.editProdutoDescricao);
        editEmpresaValor = findViewById(R.id.editProdutoPreco);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);

    }

}