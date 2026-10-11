package com.example.marketuniversitario.feature.orders.data.datasources

import com.example.marketuniversitario.feature.orders.data.models.OrderRoomEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderLocalDataSource @Inject constructor(
    private val orderDao: OrderDao
) {
    fun getOrdersByBuyer(buyerId: String): Flow<List<OrderRoomEntity>> {
        return orderDao.getOrdersByBuyer(buyerId)
    }

    fun getOrdersBySeller(sellerId: String): Flow<List<OrderRoomEntity>> {
        return orderDao.getOrdersBySeller(sellerId)
    }

    suspend fun getOrderById(orderId: String): OrderRoomEntity? {
        return orderDao.getOrderById(orderId)
    }

    suspend fun getUnsyncedOrders(): List<OrderRoomEntity> {
        return orderDao.getUnsyncedOrders()
    }

    suspend fun markAsSynced(orderId: String) {
        orderDao.markAsSynced(orderId)
    }

    suspend fun insertOrders(orders: List<OrderRoomEntity>) {
        orderDao.insertOrders(orders)
    }

    suspend fun insertOrder(order: OrderRoomEntity) {
        orderDao.insertOrder(order)
    }
}
