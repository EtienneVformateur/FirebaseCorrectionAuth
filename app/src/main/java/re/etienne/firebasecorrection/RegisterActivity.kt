package re.etienne.firebasecorrection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val etEmail = findViewById<EditText>(R.id.ETREmail)
        val etPassword1 = findViewById<EditText>(R.id.ETRPassword1)
        val etPassword2 = findViewById<EditText>(R.id.ETRPassword2)
        val btRegister = findViewById<Button>(R.id.BTRRegister)

        auth = Firebase.auth
        //Créer un compte utilisateur
        btRegister.setOnClickListener {
            val email  = etEmail.text.toString()
            val password = etPassword1.text.toString()
            val password2 = etPassword2.text.toString()
            if (password != password2) //and ()
            {
                Toast.makeText(
                    baseContext, "Les mots de passe ne correspondent pas",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 5) {
                Toast.makeText(
                    baseContext, "Entrez un mot de passe plus long",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            baseContext,
                                            "Un email de vérification vous a été envoyé",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            val ConnexionIntent = Intent(this,MainActivity::class.java)
                            startActivity(ConnexionIntent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext, "Erreur",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val AccueilIntent = Intent(this,AccueilActivity::class.java)
            startActivity(AccueilIntent)
        }
    }
}