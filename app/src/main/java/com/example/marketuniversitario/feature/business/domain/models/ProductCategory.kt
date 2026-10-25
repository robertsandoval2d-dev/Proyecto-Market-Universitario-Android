package com.example.marketuniversitario.feature.business.domain.models

enum class ProductCategory(
    val id: String,
    val displayName: String,
    val tags: List<String>
) {
    ACADEMIC_MATERIAL(
        id = "MATERIAL_ACADEMICO",
        displayName = "Material Académico",
        tags = listOf("Libros", "Copias y Separatas", "Resúmenes", "Exámenes Pasados", "Guías de Laboratorio", "Diccionarios", "Obras Literarias")
    ),
    TECH_ELECTRONICS(
        id = "TECNOLOGIA_ELECTRONICA",
        displayName = "Tecnología y Electrónica",
        tags = listOf("Laptops", "Celulares", "Calculadoras Científicas", "Audífonos", "Cargadores", "Componentes (Arduino/ESP32)", "Accesorios PC")
    ),
    FOOD_BEVERAGES(
        id = "COMIDA_BEBIDAS",
        displayName = "Comida y Bebidas",
        tags = listOf("Menú", "Postres", "Snacks", "Bebidas", "Desayunos", "Comida Saludable/Vegana", "Dulces")
    ),
    CLOTHING_MERCH(
        id = "ROPA_MERCH",
        displayName = "Ropa y Merchandising",
        tags = listOf("Polos", "Casacas", "Buzos", "Zapatillas", "Merch Universitario (Pines/Tazas)", "Mochilas", "Gorras")
    ),
    SERVICES_TUTORING(
        id = "SERVICIOS_TUTORIAS",
        displayName = "Servicios y Tutorías",
        tags = listOf("Asesorías Académicas", "Reparación de PC/Laptops", "Impresiones y Anillados", "Traducciones", "Diseño Gráfico", "Formateo", "Clases Particulares")
    ),
    STATIONERY_SUPPLIES(
        id = "UTILES_MATERIALES",
        displayName = "Útiles y Materiales",
        tags = listOf("Cuadernos", "Lapiceros", "Material de Arquitectura/Arte", "Maquetas", "Tableros de Dibujo", "Agendas", "Reglas")
    ),
    HOUSING_LIVING(
        id = "ALOJAMIENTO_CUARTOS",
        displayName = "Alojamiento y Cuartos",
        tags = listOf("Alquiler de Cuarto", "Busco Roommate", "Muebles de Segunda", "Hervidores", "Microondas", "Colchones", "Limpieza")
    ),
    TICKETS_EVENTS(
        id = "ENTRADAS_EVENTOS",
        displayName = "Entradas y Eventos",
        tags = listOf("Fiestas de Facultad", "Rifas", "Seminarios", "Conferencias", "Deportes", "Teatro Universitario")
    ),
    SPORTS_FITNESS(
        id = "DEPORTES_FITNESS",
        displayName = "Deportes y Fitness",
        tags = listOf("Ropa Deportiva", "Suplementos", "Pelotas", "Raquetas", "Implementos de Gimnasio", "Bicicletas")
    ),
    OTHER(
        id = "OTROS",
        displayName = "Otros",
        tags = listOf("Regalos", "Artículos de Segunda Mano", "Juegos de Mesa", "Arte", "Música", "Varios")
    );

    companion object {
        fun fromId(id: String): ProductCategory {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: OTHER
        }
    }
}
