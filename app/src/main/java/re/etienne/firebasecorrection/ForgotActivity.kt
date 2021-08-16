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

class ForgotActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        auth = Firebase.auth
        val btForgot = findViewById<Button>(R.id.BTFForgot)
        val etEmail = findViewById<EditText>(R.id.ETFMail)

        btForgot.setOnClickListener {
            val email = etEmail.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Un email de réinitialisation de mot de passe vous a été envoyé",
                            Toast.LENGTH_SHORT
                        ).show()
                        val ConnexionIntent = Intent(this,MainActivity::class.java)
                        startActivity(ConnexionIntent)
                    }
                }
        }


    }
}