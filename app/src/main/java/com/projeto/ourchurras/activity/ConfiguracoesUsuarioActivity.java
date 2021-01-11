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
import com.projeto.ourchurras.model.Usuario;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioCidade, editUsuarioCpf,
            editUsuarioEndereco, editUsuarioTelefone, editUsuarioNumero, editUsuarioCep;
    private ImageView imagePerfilUsuario;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuario;
    private String urlImagem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //Configurações iniciais
        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        //Configurações Toolbar
       Toolbar toolbar = findViewById(R.id.toolbar);
       toolbar.setTitle("Configurações Usuario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
        /*Recuperar dados do usuario*/
        recuperarDadosUsuario();

    }
    private void recuperarDadosUsuario() {
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome() );
                    editUsuarioTelefone.setText(usuario.getTelefone() );
                    editUsuarioCpf.setText(usuario.getCpf() );
                    editUsuarioCidade.setText(usuario.getCidade() );
                    editUsuarioEndereco.setText(usuario.getEndereco() );
                    editUsuarioNumero.setText(usuario.getNumero() );
                    editUsuarioCep.setText(usuario.getCep() );

                    urlImagem = usuario.getUrlImagem();
                    if (!urlImagem.equals("") ) {
                        Picasso.get()
                                .load(urlImagem)
                                .into(imagePerfilUsuario);
                   }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void validarDadosUsuario(View view) {

        String nome = editUsuarioNome.getText().toString();
        String telefone = editUsuarioTelefone.getText().toString();
        String cpf = editUsuarioCpf.getText().toString();
        String cidade = editUsuarioCidade.getText().toString();
        String endereco = editUsuarioEndereco.getText().toString();
        String numero = editUsuarioNumero.getText().toString();
        String cep = editUsuarioCep.getText().toString();
        imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);


        if (!nome.isEmpty()) {
              if (!telefone.isEmpty()) {
                  if (!cpf.isEmpty()) {
                        if (!cidade.isEmpty()) {
                            if (!endereco.isEmpty()) {
                                if (!numero.isEmpty()) {
                                  if (!cep.isEmpty()) {


                                Usuario usuario = new Usuario();
                                usuario.setIdUsuario(idUsuario);
                                usuario.setNome(nome);
                                usuario.setTelefone(telefone);
                                usuario.setCpf(cpf);
                                usuario.setCidade(cidade);
                                usuario.setEndereco(endereco);
                                usuario.setNumero(numero);
                                usuario.setCep(cep);
                                usuario.setUrlImagem(urlImagem);
                                usuario.salvar();

                                exibirMensagem("Dados atualizados com sucesso");
                                finish();


                            }else{
                                exibirMensagem("Digite o CEP");
                            }
                        }else{
                            exibirMensagem("Digite o número da residência");
                        }
                    }else{
                        exibirMensagem("Digite seu endereço");
                    }
                }else{
                    exibirMensagem("Digite sua cidade");
                }
            }else{
                exibirMensagem("Digite o CPF");
            }
        }else{
            exibirMensagem("Digite um telefone de contato");
        }
     }else{
            exibirMensagem("Digite o nome de Usuario");
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

                    imagePerfilUsuario.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("usuarios")
                            .child(idUsuario + "jpeg");

                    //Retorna objeto que irá controlar o upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {


                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
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
                                    urlImagem = url.toString();
                                }
                            });
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
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
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioTelefone = findViewById(R.id.editUsuarioTelefone);
        editUsuarioCpf = findViewById(R.id.editUsuarioCpf);
        editUsuarioCidade = findViewById(R.id.editUsuarioCidade);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
        editUsuarioNumero = findViewById(R.id.editUsuarioNumero);
        editUsuarioCep = findViewById(R.id.editUsuarioCep);
        imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);

    }

}