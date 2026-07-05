package com.example.pharmax.viewmodel

import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.repo.NotificationRepo
import com.example.pharmax.repo.PrescriptionRepo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class PrescriptionViewModelTest {

    private lateinit var repo: PrescriptionRepo
    private lateinit var notificationRepo: NotificationRepo
    private lateinit var viewModel: PrescriptionViewModel

    private fun validModel() = PrescriptionModel(
        name = "My Blood Pressure Rx",
        imageUrl = "https://example.com/rx.jpg"
    )

    @Before
    fun setUp() {
        repo = mock()
        notificationRepo = mock()
        viewModel = PrescriptionViewModel(repo, notificationRepo)
    }

    @Test
    fun `addPrescription blocks when name is blank`() {
        viewModel.addPrescription(validModel().copy(name = "")) {}

        assertEquals("Please give this prescription a name", viewModel.message.value)
        verify(repo, never()).addPrescription(any(), any())
    }

    @Test
    fun `addPrescription blocks when image is missing`() {
        viewModel.addPrescription(validModel().copy(imageUrl = "")) {}

        assertEquals("Please upload a prescription image", viewModel.message.value)
        verify(repo, never()).addPrescription(any(), any())
    }

    @Test
    fun `addPrescription succeeds and notifies admin`() {
        val saved = validModel().copy(prescriptionId = "rx1")
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, PrescriptionModel) -> Unit>(1)
            callback(true, "Prescription submitted", saved)
            null
        }.`when`(repo).addPrescription(any(), any())

        var successResult: PrescriptionModel? = null
        viewModel.addPrescription(validModel()) { successResult = it }

        assertEquals(saved, successResult)
        assertEquals("Prescription submitted", viewModel.message.value)
        verify(notificationRepo).addNotification(any(), any())
    }
}
