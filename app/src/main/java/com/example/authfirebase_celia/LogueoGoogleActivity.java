package com.example.authfirebase_celia;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthCredential;

public class LogueoGoogleActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No necesitamos setContentView si solo vamos a probar el login

        mAuth = FirebaseAuth.getInstance();

        // Aquí podrías llamar al método que maneje Google Sign-In
        // Por simplicidad, suponemos que ya obtuviste el ID Token
        String idToken = "TU_GOOGLE_ID_TOKEN";

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d("LogueoGoogle", "Usuario: " + user.getDisplayName() + " - " + user.getEmail());
            } else {
                Log.d("LogueoGoogle", "Fallo autenticación");
            }
        });
    }

    private void cerrarSesion() {
        mAuth.signOut();
        Log.d("LogueoGoogle", "Sesión cerrada");
    }
}