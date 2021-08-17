package re.etienne.firebasecorrection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import re.etienne.firebasecorrection.databinding.ActivityQuizzBinding
import java.util.ArrayList

class QuizzActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityQuizzBinding
    var cpt = 0 // Compteur de bonne réponse
    var listQuestion = arrayListOf<Question>()
    var q_encours = 0 //Index de la question en cours dans listQuestion de 0 à listQuestion.size = au nombre de questions



    class Question(
        val Enonce: String,
        val Reponse: ArrayList<String>,
        var bonnereponse: Int,
    )

    class Score(
//        val username: String,
//        val userId: String, // Identifiant unique de l'user connecté
        val score: Int //Le compteur de bonne réponse va être stocker dans cette variable
    )

    fun toastcpt(reponse: Boolean){
        //Si c'est une bonne réponse alors j'incrémenteur le compteur de bonne réponse
        if(reponse){
            Toast.makeText(this,
                "Bonne reponse", Toast.LENGTH_SHORT).show()
            cpt ++
            binding.tvCpt.text = cpt.toString()
        }
        else{
            Toast.makeText(this,
                "Mauvaise reponse", Toast.LENGTH_SHORT).show()
        }
        //Je change de question si ce n'est pas la derniere question
        q_encours ++
        if (q_encours<listQuestion.size)
        {
            chargeQuestion(listQuestion[q_encours])
        }
        //Sinon si toutes les questions ont été posées j'affiche une alerte pour recommencer
        // Et je stocke le score en ligne
        else {
            val currentUser = auth.currentUser
            val userId = currentUser!!.uid
            val NewScore = Score(cpt)

            //ecriture des données en ligne
            val RefScore = database.child("Scores").child(userId)
            RefScore.child("score").get().addOnSuccessListener {
                if (it.value.toString().toInt() < cpt ){
                    RefScore.setValue(NewScore)
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Rejouer ? ! ")
            builder.setCancelable(false) // Oblige le clique sur un bouton
            builder.setPositiveButton("Oui") { dialog, which ->
                q_encours = 0
                cpt = 0
                binding.tvCpt.text = cpt.toString()
                chargeQuestion(listQuestion[q_encours])
            }
            builder.show()
        }
    }
    fun chargeQuestion(q:Question){
        binding.tvQuestion.text = q.Enonce
        binding.btR1.text = q.Reponse[0]
        binding.btR2.text = q.Reponse[1]
        binding.btR3.text = q.Reponse[2]
//        binding.btR4.text = q.Reponse[3]

        binding.btR1.setOnClickListener { toastcpt(binding.btR1.text == q.Reponse[q.bonnereponse] ) }
        binding.btR2.setOnClickListener { toastcpt(binding.btR2.text == q.Reponse[q.bonnereponse] ) }
        binding.btR3.setOnClickListener { toastcpt(binding.btR3.text == q.Reponse[q.bonnereponse] ) }
//        binding.btR4.setOnClickListener { toastcpt(binding.btR4.text == q.Reponse[q.bonnereponse] ) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizzBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val tvCpt = findViewById<TextView>(R.id.tvCpt)
        tvCpt.text = ""
        auth = Firebase.auth

        //Connexion à la base de donnée Firebase
        database = Firebase.database("https://fir-correction-default-rtdb.europe-west1.firebasedatabase.app/").reference
        //Chargement de la liste des questions en ligne
        val listeQuestionReference = database.child("ListeQuestion")
        listeQuestionReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(listequestion: DataSnapshot) {
                for (question in listequestion.children) {
                    var TableauReponse = arrayListOf<String>()
                    for (reponse in question.child("Reponse").children) {
                        TableauReponse.add(reponse.value.toString())
                    }
                    val Q=Question(question.child("Enonce").value.toString(),
                        TableauReponse,question.child("BonneReponse").value.toString().toInt())
                    listQuestion.add(Q)
                }
                chargeQuestion(listQuestion[q_encours])
                //Disparition écran de chargement et apparition des questions
                binding.LQLoading.visibility = View.GONE
                binding.LQQuestion.visibility = View.VISIBLE

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
            )

    }
}