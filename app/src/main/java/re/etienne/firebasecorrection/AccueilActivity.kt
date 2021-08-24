package re.etienne.firebasecorrection

import android.app.Activity
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AccueilActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    val REQUEST_CODE = 100

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
        val btUpdateImage = findViewById<Button>(R.id.BTUpdateImage)
        storage = Firebase.storage
        val storageRef = storage.reference
        val pathReference = storageRef.child("images/2813393")
        val imageView = findViewById<ImageView>(R.id.IVAvatar)
        Glide.with(this /* context */)
            .load(pathReference)
            .sizeMultiplier(0.5F)
            .into(imageView)


        btUpdateImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE)
        }

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


        if (user.isEmailVerified) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            val uriImage = data?.data
            val storageRef = storage.reference
            val uploadTask = storageRef.child("images/${uriImage?.lastPathSegment}")
                .putFile(uriImage!!)
            uploadTask.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext, "Connexion réussite",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext, "Erreur de connexion",
                        Toast.LENGTH_SHORT
                    ).show()
                }
//            ivAvatar.setImageURI(data?.data) // handle chosen image --> URI De l'image dans data?.data
            }
        }
    }

}