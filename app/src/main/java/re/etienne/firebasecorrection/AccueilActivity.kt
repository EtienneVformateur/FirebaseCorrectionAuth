package re.etienne.firebasecorrection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class AccueilActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    fun demarreQuizz(view: View){
        val QuizzIntent = Intent(this,QuizzActivity::class.java)
        startActivity(QuizzIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)
        val btDeconnexion = findViewById<Button>(R.id.BTDeconnexion)
        val btProfile = findViewById<Button>(R.id.btProfile)
        val tvEmail = findViewById<TextView>(R.id.TVEmail)
        val tvVerifMail = findViewById<TextView>(R.id.TVVerifmail)
        val btVerifEmail = findViewById<Button>(R.id.BTVerifMail)

        auth = Firebase.auth
        val user = auth.currentUser
        if (user == null){
            val MainIntent = Intent(this,MainActivity::class.java)
            startActivity(MainIntent)
        }

        if(user!!.displayName.isNullOrEmpty()) {
            val ProfileIntent = Intent(this,ProfileActivity::class.java)
            startActivity(ProfileIntent)

        }
        else{
            tvEmail.text = user.displayName
        }


        if (user!!.isEmailVerified) {
            tvVerifMail.visibility = View.GONE
            btVerifEmail.visibility = View.GONE
        }

        btDeconnexion.setOnClickListener {
            auth.signOut()
            val ConnexionIntent = Intent(this,MainActivity::class.java)
            startActivity(ConnexionIntent)
        }

        btProfile.setOnClickListener {
            val ProfileIntent = Intent(this,ProfileActivity::class.java)
            startActivity(ProfileIntent)
        }
        btVerifEmail.setOnClickListener {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Un email de vérification vous a été envoyé",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }




    }
}