package com.example.marketuniversitario.core.util

import android.util.Log
import com.example.marketuniversitario.feature.business.data.models.BusinessEntity
import com.example.marketuniversitario.feature.business.data.models.ProductEntity
import com.example.marketuniversitario.feature.business.domain.models.BusinessCategory
import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.ProductCategory
import com.example.marketuniversitario.feature.user.data.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class TestDataSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /** Plantilla de producto. Valida que los tags pertenezcan a la categoría. */
    private data class SeedItem(
        val name: String,
        val description: String,
        val price: Double,
        val category: ProductCategory,
        val tags: List<String>,
        val type: ItemType = ItemType.PRODUCT,
        val stock: Int? = null
    ) {
        init {
            require(category.tags.containsAll(tags)) {
                "Tags inválidos para '$name' en ${category.name}: ${tags - category.tags.toSet()}"
            }
        }
    }

    /** Catálogo para el usuario 1 (15 productos). */
    private val catalogUser1: List<SeedItem> by lazy {
        listOf(
            SeedItem("Menú Universitario del Día", "Almuerzo completo con entrada, segundo y refresco.", 12.0,
                ProductCategory.FOOD_BEVERAGES, listOf("Menú", "Bebidas"), stock = 50),
            SeedItem("Brownies Caseros (pack x4)", "Brownies de chocolate recién horneados, ideales para el break.", 8.0,
                ProductCategory.FOOD_BEVERAGES, listOf("Postres", "Dulces", "Snacks"), stock = 30),
            SeedItem("Bowl Vegano de Quinua", "Bowl de quinua con verduras salteadas y palta.", 14.0,
                ProductCategory.FOOD_BEVERAGES, listOf("Comida Saludable/Vegana", "Menú"), stock = 20),

            SeedItem("Módulo ESP32 DevKit", "Microcontrolador con WiFi y Bluetooth para proyectos de IoT.", 40.0,
                ProductCategory.TECH_ELECTRONICS, listOf("Componentes (Arduino/ESP32)"), stock = 15),
            SeedItem("Audífonos Bluetooth con Micrófono", "Inalámbricos, 20 horas de batería, ideales para clases virtuales.", 65.0,
                ProductCategory.TECH_ELECTRONICS, listOf("Audífonos", "Cargadores"), stock = 12),

            SeedItem("Separata de Cálculo II", "Separata completa con teoría y problemas resueltos del ciclo.", 15.0,
                ProductCategory.ACADEMIC_MATERIAL, listOf("Copias y Separatas", "Resúmenes"), stock = 40),
            SeedItem("Banco de Exámenes Pasados - Física I", "Parciales y finales de los últimos 4 ciclos.", 10.0,
                ProductCategory.ACADEMIC_MATERIAL, listOf("Exámenes Pasados"), stock = 25),

            SeedItem("Polo Oficial de Facultad", "Polo de algodón con el escudo de la facultad. Tallas S a XL.", 35.0,
                ProductCategory.CLOTHING_MERCH, listOf("Polos", "Merch Universitario (Pines/Tazas)"), stock = 60),
            SeedItem("Mochila Impermeable para Laptop", "Resistente al agua, con compartimiento acolchado para laptop de 15\".", 90.0,
                ProductCategory.CLOTHING_MERCH, listOf("Mochilas"), stock = 18),

            SeedItem("Asesoría de Programación (1 hora)", "Tutoría personalizada en Python, Java o Kotlin.", 30.0,
                ProductCategory.SERVICES_TUTORING, listOf("Asesorías Académicas", "Clases Particulares"), type = ItemType.SERVICE),
            SeedItem("Reparación y Formateo de Laptops", "Diagnóstico, formateo e instalación de programas. Entrega en 24 horas.", 50.0,
                ProductCategory.SERVICES_TUTORING, listOf("Reparación de PC/Laptops", "Formateo"), type = ItemType.SERVICE),

            SeedItem("Tablero de Dibujo A3 con Escuadras", "Tablero con juego de escuadras y reglas para arquitectura.", 55.0,
                ProductCategory.STATIONERY_SUPPLIES, listOf("Tableros de Dibujo", "Reglas"), stock = 10),
            SeedItem("Hervidor Eléctrico 1.7L", "Acero inoxidable, casi nuevo. Ideal para cuarto de estudiante.", 45.0,
                ProductCategory.HOUSING_LIVING, listOf("Hervidores"), stock = 5),
            SeedItem("Entrada Fiesta de Facultad", "Entrada general para la fiesta de integración.", 20.0,
                ProductCategory.TICKETS_EVENTS, listOf("Fiestas de Facultad"), stock = 100),
            SeedItem("Pelota de Vóley Oficial", "Tamaño oficial, usada solo una temporada.", 38.0,
                ProductCategory.SPORTS_FITNESS, listOf("Pelotas"), stock = 8)
        )
    }

    /** Catálogo para el usuario 2 (15 productos, todos distintos al anterior). */
    private val catalogUser2: List<SeedItem> by lazy {
        listOf(
            SeedItem("Desayuno Energético", "Sándwich de pollo, jugo de naranja y fruta picada.", 9.0,
                ProductCategory.FOOD_BEVERAGES, listOf("Desayunos", "Bebidas"), stock = 35),
            SeedItem("Pack de Snacks Saludables", "Mix de frutos secos, barras de cereal y chips de plátano.", 11.0,
                ProductCategory.FOOD_BEVERAGES, listOf("Snacks", "Comida Saludable/Vegana"), stock = 45),

            SeedItem("Calculadora Científica Casio fx-991", "Calculadora con más de 500 funciones, ideal para ingeniería.", 85.0,
                ProductCategory.TECH_ELECTRONICS, listOf("Calculadoras Científicas"), stock = 14),
            SeedItem("Laptop Core i5 Reacondicionada", "8 GB de RAM, SSD de 256 GB, batería en buen estado.", 1200.0,
                ProductCategory.TECH_ELECTRONICS, listOf("Laptops", "Accesorios PC"), stock = 2),
            SeedItem("Cargador Rápido USB-C 30W", "Cargador de pared compatible con celulares y tablets.", 35.0,
                ProductCategory.TECH_ELECTRONICS, listOf("Cargadores", "Celulares"), stock = 25),

            SeedItem("Diccionario Inglés-Español Oxford", "Edición actualizada, tapa dura, casi sin uso.", 40.0,
                ProductCategory.ACADEMIC_MATERIAL, listOf("Diccionarios", "Libros"), stock = 6),
            SeedItem("Guía de Laboratorio de Química General", "Guía impresa con prácticas y formatos de informe.", 12.0,
                ProductCategory.ACADEMIC_MATERIAL, listOf("Guías de Laboratorio", "Copias y Separatas"), stock = 30),

            SeedItem("Casaca Universitaria Bordada", "Casaca con cierre y logo bordado de la universidad.", 75.0,
                ProductCategory.CLOTHING_MERCH, listOf("Casacas", "Merch Universitario (Pines/Tazas)"), stock = 22),
            SeedItem("Zapatillas Urbanas Blancas", "Zapatillas cómodas para el día a día, tallas 38 a 43.", 120.0,
                ProductCategory.CLOTHING_MERCH, listOf("Zapatillas"), stock = 16),

            SeedItem("Impresiones y Anillados", "Impresión a color o blanco y negro, anillado en el día.", 5.0,
                ProductCategory.SERVICES_TUTORING, listOf("Impresiones y Anillados"), type = ItemType.SERVICE),
            SeedItem("Diseño de Presentaciones y Posters", "Diseño gráfico profesional para exposiciones y congresos.", 45.0,
                ProductCategory.SERVICES_TUTORING, listOf("Diseño Gráfico"), type = ItemType.SERVICE),

            SeedItem("Pack de Cuadernos y Lapiceros", "5 cuadernos cuadriculados y 10 lapiceros de colores.", 28.0,
                ProductCategory.STATIONERY_SUPPLIES, listOf("Cuadernos", "Lapiceros"), stock = 40),
            SeedItem("Microondas Compacto 20L", "Funciona perfecto, ideal para departamento de estudiantes.", 150.0,
                ProductCategory.HOUSING_LIVING, listOf("Microondas"), stock = 1),
            SeedItem("Entrada Seminario de Ingeniería", "Acceso al seminario anual con certificado de asistencia.", 25.0,
                ProductCategory.TICKETS_EVENTS, listOf("Seminarios", "Conferencias"), stock = 80),
            SeedItem("Bicicleta de Montaña Aro 26", "Bicicleta con 21 velocidades, revisada y lista para usar.", 380.0,
                ProductCategory.SPORTS_FITNESS, listOf("Bicicletas"), stock = 1)
        )
    }

    private fun seedProductId(userId: String, index: Int) =
        "seed_${userId}_${(index + 1).toString().padStart(2, '0')}"

    private suspend fun injectTestProductsForUser(
        userId: String,
        catalog: List<SeedItem>,
        force: Boolean
    ): Result<Unit> = runCatching {
        if (userId.isBlank() || userId.startsWith("PON_AQUI")) {
            Log.w(TAG, "ID de usuario no configurado. Omitiendo inyección.")
            return@runCatching
        }

        // Si ya fue sembrado, no hacemos nada (1 lectura en lugar de 15 escrituras)
        if (!force) {
            val alreadySeeded = firestore.collection("products")
                .document(seedProductId(userId, 0))
                .get().await().exists()
            if (alreadySeeded) {
                Log.d(TAG, "Productos de prueba ya existen para $userId. Omitiendo.")
                return@runCatching
            }
        }

        Log.d(TAG, "Iniciando inyección de datos para el usuario: $userId")
        val userRef = firestore.collection("users").document(userId)

        // 1. Obtener usuario
        val user = userRef.get().await().toObject(UserEntity::class.java)
            ?: throw IllegalStateException("Usuario con ID '$userId' no existe en la colección 'users'")

        // 2. Resolver/crear negocio
        val existingBusinessId = user.businessId
        val (currentBusinessId, businessName) =
            if (user.hasBusiness && !existingBusinessId.isNullOrEmpty()) {
                val name = firestore.collection("businesses").document(existingBusinessId)
                    .get().await().getString("name")
                    ?: "Negocio de Prueba de ${user.name}"
                existingBusinessId to name
            } else {
                val newBusinessRef = firestore.collection("businesses").document()
                val name = "Negocio de Prueba de ${user.name}"
                newBusinessRef.set(
                    BusinessEntity(
                        id = newBusinessRef.id,
                        ownerId = userId,
                        name = name,
                        description = "Negocio generado automáticamente para pruebas",
                        categories = BusinessCategory.entries.map { it.id }
                    )
                ).await()
                userRef.update("hasBusiness", true, "businessId", newBusinessRef.id).await()
                newBusinessRef.id to name
            }

        // 3. Construir productos con IDs determinísticos (re-ejecutar sobreescribe, no duplica)
        val products = catalog.mapIndexed { index, item ->
            val random = Random(seed = "$userId-$index".hashCode())
            ProductEntity(
                id = seedProductId(userId, index),
                businessId = currentBusinessId,
                businessName = businessName,
                name = item.name,
                description = item.description,
                price = item.price,
                type = item.type.name,
                category = item.category.id,
                tags = item.tags,
                stock = item.stock,
                isAvailable = true,
                views = random.nextInt(10, 500),
                salesCount = random.nextInt(0, 60)
            )
        }

        // 4. Escritura por lotes
        val batch = firestore.batch()
        products.forEach { batch.set(firestore.collection("products").document(it.id), it) }
        batch.commit().await()
        Log.d(TAG, "Inyectados ${products.size} productos para el usuario: $userId")
    }

    private val auth = FirebaseAuth.getInstance()

    // UIDs ya procesados en esta ejecución de la app (evita incluso la lectura de comprobación)
    private val processedUids = ConcurrentHashMap.newKeySet<String>()

    /** Cada UID de prueba recibe su propio catálogo. */
    private val catalogsByUid: Map<String, List<SeedItem>> by lazy {
        mapOf(
            SEED_UID_1 to catalogUser1,
            SEED_UID_2 to catalogUser2
        )
    }

    /** Emite el UID cada vez que cambia la sesión (null si no hay sesión). */
    private fun authUidFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    /**
     * Espera a que haya sesión y, si el UID es uno de los de prueba,
     * inyecta su catálogo (solo si no existe ya).
     */
    suspend fun seedOnLogin(force: Boolean = false) {
        authUidFlow().filterNotNull().collect { uid ->
            val catalog = catalogsByUid[uid] ?: return@collect   // no es cuenta de prueba
            if (!force && !processedUids.add(uid)) return@collect // ya revisado en esta ejecución

            injectTestProductsForUser(uid, catalog, force).onFailure {
                processedUids.remove(uid) // permite reintentar en el próximo login
                Log.e(TAG, "Falló la inyección para $uid", it)
            }
        }
    }

    companion object {
        private const val TAG = "TestDataSeeder"

        // Cuentas de prueba: cada una recibe su catálogo al iniciar sesión
        const val SEED_UID_1 = "zVWZ5ADAXhgyzx3oBB73wy6zIk32"
        const val SEED_UID_2 = "KIF9PyTx5XYokhfGTFZf0IH0A5f1"
    }
}