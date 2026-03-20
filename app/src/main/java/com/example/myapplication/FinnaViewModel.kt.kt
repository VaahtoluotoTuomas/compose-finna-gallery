package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinnaUiState(
    val year: Int = (1880..2020).random(),
    val images: List<FinnaRecord> = emptyList(),
    val isLoading: Boolean = false,
    val page: Int = 1,
    val endReached: Boolean = false
)

class FinnaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FinnaUiState())

    val uiState: StateFlow<FinnaUiState> = _uiState.asStateFlow()

    init {
        fetchImages()
    }

    fun onYearChanged(newYear: Float) {
        _uiState.update { it.copy(year = newYear.toInt()) }
    }

    fun onYearSelected() {
        _uiState.update { it.copy(images = emptyList(), page = 1, endReached = false) }
        fetchImages()
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.endReached) return

        _uiState.update { it.copy(page = it.page + 1) }
        fetchImages()
    }

    private fun fetchImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val state = _uiState.value

                val searchQuery = "maisema ${state.year}"

                val filters = listOf(
                    "format:\"0/Image/\""
                )

                val response = FinnaNetwork.api.getLandscapes(
                    lookfor = searchQuery,
                    filters = filters,
                    page = state.page
                )

                val newRecords: List<FinnaRecord> = response.records ?: emptyList()

                Log.d("FINNA_TESTI", "Haulla '$searchQuery' saatiin ${newRecords.size} osumaa.")

                _uiState.update { currentState ->
                    currentState.copy(
                        images = currentState.images + newRecords,
                        isLoading = false,
                        endReached = newRecords.isEmpty()
                    )
                }
            } catch (e: Exception) {
                Log.e("FINNA_VIRHE", "Haku epäonnistui", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}