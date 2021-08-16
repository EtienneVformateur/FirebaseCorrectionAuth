package re.etienne.firebasecorrection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var binding: ActivityQuizzBinding
    var cpt = 0
    var listQuestion = arrayListOf<Question>()
    var q_encours = 0

    class Question(
        val Enonce: String,
        val Reponse: ArrayList<String>,
        var bonnereponse: Int,
    )
    fun toastcpt(reponse: Boolean){
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
        q_encours ++
        if (q_encours<listQuestion.size)
        {
            chargeQuestion(listQuestion[q_encours])
        }
        else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Rejouer ? ! ")
            builder.setCancelable(false) // Oblige le clique sur un bouton
            builder.setPositiveButton("Oui") { dialog, which ->
                q_encours = 0
                cpt = 0
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
        database = Firebase.database("https://fir-correction-default-rtdb.europe-west1.firebasedatabase.app/").reference





        tvCpt.text = ""
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

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
            )

    }
}