package com.example.thingapp

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.thingapp.databinding.ActivityMainBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.awaitAll
import android.app.Activity
import android.widget.Toast
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var selectedImages = mutableListOf<Uri>()
    private val selectedColors = mutableListOf<Int>()
    private val firestore = Firebase.firestore
    private val productsStorage = Firebase.storage.reference
    

    // Initialize UI, status bar color, and click listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        
        window.statusBarColor = android.graphics.Color.parseColor("#3700B3")

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Product color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }
                })
                .setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }
                .show()
        }

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    //Multiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }
                    updateImages()
                }
            }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }
    }

    // Inflate the toolbar menu with the save icon
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Handle clicks on toolbar menu items (like Save button)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.saveProduct) {
            val productValidation = validateInformation()
            if (!productValidation) {
                Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProduct()
        }
        return super.onOptionsItemSelected(item)
    }

    // Main function to save the product to Firebase
    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizesList(binding.edSizes.text.toString().trim())
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showLoading()
            }
            try {
                val imagesByteArrays = getImagesByteArrays()
                val images = imagesByteArrays.map {
                    async {
                        val id = java.util.UUID.randomUUID().toString()
                        val imageStorage = productsStorage.child("products/images/$id")
                        val result = imageStorage.putBytes(it).await()
                        val downloadUrl = result.storage.downloadUrl.await().toString()
                        downloadUrl
                    }
                }.awaitAll()

                val product = com.example.thingapp.data.Product(
                    java.util.UUID.randomUUID().toString(),
                    name,
                    category,
                    price.toFloat(),
                    if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                    if (description.isEmpty()) null else description,
                    if (selectedColors.isEmpty()) null else selectedColors,
                    sizes,
                    images
                )

                firestore.collection("Products").add(product).addOnSuccessListener {
                    hideLoading()
                }.addOnFailureListener {
                    hideLoading()
                    android.util.Log.e("Error", it.message.toString())
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    hideLoading()
                }
            }
        }
    }

    // Show the progress bar when loading starts
    private fun showLoading() {
    }

    // Hide the progress bar when loading ends
    private fun hideLoading() {
    }

    // Convert selected image URIs to ByteArrays for Firebase upload
    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = java.io.ByteArrayOutputStream()
            val imageBmp = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, stream)) {
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray
    }

    
    // Update the UI text for selected images count
    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    // Update the UI text for selected colors list
    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }

    // Parse the sizes string into a formatted List
    private fun getSizesList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty())
            return null
        val sizesList = sizesStr.split(",")
        return sizesList
    }

    // Check if the required fields are properly filled
    private fun validateInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false

        if (binding.edName.text.toString().trim().isEmpty())
            return false

        if (binding.edCategory.text.toString().trim().isEmpty())
            return false

        if (selectedImages.isEmpty())
            return false

        return true
    }
}
