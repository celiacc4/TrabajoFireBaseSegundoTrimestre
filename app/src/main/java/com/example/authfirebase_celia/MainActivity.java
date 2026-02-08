package com.example.authfirebase_celia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegister, buttonClose, buttonGoogleLogin;
    private TextView textViewResultado;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Enlazo los componentes con la vista
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonClose = findViewById(R.id.buttonClose);
        textViewResultado = findViewById(R.id.textViewResultado);
        buttonGoogleLogin = findViewById(R.id.buttonGoogleLogin);

        //Configuro Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Botones
        buttonRegister.setOnClickListener(v -> registrarUsuario());
        buttonLogin.setOnClickListener(v -> loginUsuario());
        buttonClose.setOnClickListener(v -> cerrarSesion());
        buttonGoogleLogin.setOnClickListener(v -> loginConGoogle());

        //Compruebo si ya hay usuario logueado
        actualizarUI(mAuth.getCurrentUser());
    }

    private void registrarUsuario() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validarCampos(email, password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            actualizarUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loginUsuario() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validarCampos(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                            actualizarUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loginConGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Fallo autenticación Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(this, "Login Google exitoso", Toast.LENGTH_SHORT).show();
                actualizarUI(user);
            } else {
                Toast.makeText(this, "Fallo autenticación Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cerrarSesion() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        actualizarUI(null);
    }

    private boolean validarCampos(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Ingresa un email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Ingresa una contraseña");
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Mínimo 6 caracteres");
            return false;
        }
        return true;
    }

    private void actualizarUI(FirebaseUser user) {
        if (user != null) {
            textViewResultado.setText("Usuario: " + user.getEmail());
            buttonLogin.setEnabled(false);
            buttonRegister.setEnabled(false);
            buttonClose.setEnabled(true);
            buttonGoogleLogin.setEnabled(false);
        } else {
            textViewResultado.setText("Resultado");
            buttonLogin.setEnabled(true);
            buttonRegister.setEnabled(true);
            buttonClose.setEnabled(false);
            buttonGoogleLogin.setEnabled(true);
        }
    }
}