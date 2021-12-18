package com.asyncdevs.devforum

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast

class CreateTeam : AppCompatActivity() {
    private var selectedPhotoUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_team)


        val teamSize = arrayOf("1","2", "3", "4","5")
        val teamSizeAdapter = ArrayAdapter(this,R.layout.dropdown_menu_popup_item,teamSize)
        val designationTextFilledExposedDropdown = findViewById<AutoCompleteTextView>(R.id.createTeam_filled_exposed_dropdown)
        designationTextFilledExposedDropdown.setAdapter(teamSizeAdapter)

        designationTextFilledExposedDropdown.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                Toast.makeText(applicationContext,s.toString(), Toast.LENGTH_SHORT).show()
            }
        })
        val teamLogo = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.team_logo)

        teamLogo.setOnClickListener {
//            Toast.makeText(requireContext(), "Select photo", Toast.LENGTH_SHORT).show()
            val i = Intent(Intent.ACTION_PICK)
            i.type= "image/*"
            startActivityForResult(i, 0)
        }

        findViewById<Button>(R.id.btn_createNewTeam).setOnClickListener {
            val intent = Intent(this,CreatedTeamInfo::class.java)
            startActivity(intent)
            Log.i("TEAM_ID",getRandomString(7))
        }




    }
    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!=null){
            Log.i("info", "Photo Selected")
            selectedPhotoUri = data.data
//                RegisterScreen.dataContains!!.put("ImageURL", selectedPhotoUri.toString())
            Log.i("USERIMAGE",selectedPhotoUri.toString())
            Log.e("SRI1711", "$selectedPhotoUri")
            val userImage = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.team_logo)
            val bitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                selectedPhotoUri
            )
            userImage?.setImageBitmap(bitmap)
        }
    }
}