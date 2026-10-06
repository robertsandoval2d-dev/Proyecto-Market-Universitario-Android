package com.example.marketuniversitario.feature.orders.data.datasources

import android.net.Uri
import com.example.marketuniversitario.feature.orders.data.models.MessageEntity
import com.example.marketuniversitario.feature.orders.domain.models.MessageType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getMessages(orderId: String): Flow<List<MessageEntity>> = callbackFlow {
        val listener = firestore.collection("orders")
            .document(orderId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Firebase lee los documentos y los convierte directamente a Entidades
                    val messageEntities = snapshot.toObjects(MessageEntity::class.java)
                    trySend(messageEntities).isSuccess
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun sendTextMessage(messageEntity: MessageEntity) {
        // Navegamos hasta la ruta exacta de la subcolección
        firestore.collection("orders")
            .document(messageEntity.orderId)
            .collection("messages")
            .document(messageEntity.id)
            .set(messageEntity)
            .await()
    }

    suspend fun sendVoiceMessage(messageEntity: MessageEntity, localAudioUri: Uri) {
        // 1. Crear la ruta en Storage: "chat_audios/{orderId}/{messageId}.m4a"
        val audioRef = storage.reference
            .child("chat_audios")
            .child(messageEntity.orderId)
            .child("${messageEntity.id}.m4a")

        // 2. Subir el archivo físico desde el celular a Firebase Storage
        audioRef.putFile(localAudioUri).await()

        // 3. Obtener la URL pública de descarga
        val downloadUrl = audioRef.downloadUrl.await().toString()

        // 4. Actualizar la entidad con la URL y el tipo de mensaje correcto
        val finalEntity = messageEntity.copy(
            audioUrl = downloadUrl,
            type = MessageType.AUDIO.name
        )

        // 5. Guardar la burbuja de chat en Firestore (igual que el texto)
        val messageRef = firestore.collection("orders")
            .document(finalEntity.orderId)
            .collection("messages")
            .document(finalEntity.id)

        messageRef.set(finalEntity).await()
    }
}