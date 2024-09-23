package com.example.opsc7312_poe

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FirestoreDatabase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Method to add a user to Firestore
    fun addUser(userId: String, userData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Method to get user data from Firestore
    fun getUser(userId: String, onSuccess: (DocumentSnapshot) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document)
                } else {
                    onFailure(FirebaseFirestoreException("No such document", FirebaseFirestoreException.Code.NOT_FOUND))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Method to update user data in Firestore
    fun updateUser(userId: String, updatedData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Method to delete a user from Firestore
    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Method to get all users (or apply any Firestore query)
    fun getAllUsers(onSuccess: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener { result ->
                onSuccess(result)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}