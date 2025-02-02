package com.projeto.ourchurras.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.projeto.ourchurras.R;
import com.projeto.ourchurras.helper.ConfiguracaoFirebase;
import com.projeto.ourchurras.helper.UsuarioFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);


        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signOut();

        //Verifica usuario logado
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){//Empresa
                linearTipoUsuario.setVisibility(View.VISIBLE);
            }else {//Usuario
                linearTipoUsuario.setVisibility(View.GONE);
            }

        });
        //linearTipoUsuario.setVisibility(View.VISIBLE);

        botaoAcessar.setOnClickListener(v -> {

            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if(!email.isEmpty() ) {
                if(!senha.isEmpty() ) {

                    //Verifica estado do switch
                    if(tipoAcesso.isChecked()) {
                        autenticacao.createUserWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener((Task<AuthResult> task) -> {
                            if(task.isSuccessful() ) {
                                Toast.makeText(AutenticacaoActivity.this,
                                        "Cadastro realizado com sucesso",
                                        Toast.LENGTH_SHORT).show();

                                        String tipoUsuario = getTipoUsuario();
                                UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                abrirTelaPrincipal(tipoUsuario);

                            }else {
                                String erroExcecao = "";
                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthWeakPasswordException e) {
                                    erroExcecao = "Digite uma senha mais forte";
                                }catch (FirebaseAuthInvalidCredentialsException e) {
                                    erroExcecao = "Digite um e-mail válido";
                                }catch (FirebaseAuthUserCollisionException e) {
                                    erroExcecao = "Conta já existente";
                                }catch (Exception e) {
                                    erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                        e.printStackTrace();

                                }
                                Toast.makeText(AutenticacaoActivity.this,
                                        "Erro: " + erroExcecao,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {//Login
                        autenticacao.signInWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener((Task<AuthResult> task) -> {
                            if (task.isSuccessful() ) {

                                Toast.makeText(AutenticacaoActivity.this,
                                        "Logado com sucesso",
                                        Toast.LENGTH_SHORT).show();
                                String tipoUsuario = task.getResult().getUser().getDisplayName();
                                abrirTelaPrincipal(tipoUsuario);

                            }else{
                                Toast.makeText(AutenticacaoActivity.this,
                                        "Erro ao fazer login: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha a Senhal", Toast.LENGTH_SHORT);
                }

            }else {
                Toast.makeText(AutenticacaoActivity.this,
                        "Preencha o E-mail", Toast.LENGTH_SHORT);
            }
        });

    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null) {
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario() {
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaPrincipal(String tipoUsuario) {
        if(tipoUsuario !=null && tipoUsuario.equals("E")) {//Empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class) );
        }else {//Usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class) );
        }
    }

    private void inicializaComponentes () {
        campoEmail = findViewById(R.id.editProdutoNome);
        campoSenha = findViewById(R.id.editProdutoDescricao);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);

    }
}