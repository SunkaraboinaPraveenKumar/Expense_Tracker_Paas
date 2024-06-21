package com.example.financemanagementapp

class UserRepository(private val dao: LoginRegisterDao) {
    suspend fun registerUser(user: RegisterEntity) {
        dao.insert(user)
    }

    suspend fun getUserByUsername(username: String): RegisterEntity? {
        return dao.getUserByUsername(username)
    }

    suspend fun authenticateUser(username: String, password: String): RegisterEntity? {
        return dao.authenticate(username, password)
    }
}
