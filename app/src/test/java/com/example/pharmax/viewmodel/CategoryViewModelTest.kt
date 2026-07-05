package com.example.pharmax.viewmodel

import com.example.pharmax.repo.CategoryRepo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class CategoryViewModelTest {

    private lateinit var repo: CategoryRepo
    private lateinit var viewModel: CategoryViewModel

    @Before
    fun setUp() {
        repo = mock()
        viewModel = CategoryViewModel(repo)
    }

    @Test
    fun `addCategory blocks when name is blank`() {
        viewModel.addCategory("", "Some description", "💊", true) {}

        assertEquals("Category name is required", viewModel.message.value)
        verify(repo, never()).checkCategoryNameExists(any(), any(), any())
    }

    @Test
    fun `addCategory blocks when name already exists`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean) -> Unit>(2)
            callback(true)
            null
        }.`when`(repo).checkCategoryNameExists(eq("Pain Relief"), any(), any())

        viewModel.addCategory("Pain Relief", "desc", "💊", true) {}

        assertEquals("A category with this name already exists", viewModel.message.value)
        verify(repo, never()).addCategory(any(), any())
    }

    @Test
    fun `addCategory succeeds when name is valid and unique`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean) -> Unit>(2)
            callback(false)
            null
        }.`when`(repo).checkCategoryNameExists(eq("Vitamins"), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Category added successfully")
            null
        }.`when`(repo).addCategory(any(), any())

        var successCalled = false
        viewModel.addCategory("Vitamins", "desc", "🌿", true) { successCalled = true }

        assertEquals(true, successCalled)
        assertEquals("Category added successfully", viewModel.message.value)
        verify(repo).addCategory(any(), any())
    }
}
