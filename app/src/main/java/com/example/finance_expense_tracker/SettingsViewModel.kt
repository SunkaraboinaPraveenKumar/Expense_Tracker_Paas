import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance_expense_tracker.AppDatabase
import com.example.finance_expense_tracker.SettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class SettingsViewModel(context: Context) : ViewModel() {
    private val settingsDao = AppDatabase.getDatabase(context).settingsDao()

    var selectedCurrency by mutableStateOf("INR")
        private set

    var selectedUiMode by mutableStateOf(UiMode.SystemDefault)
        private set

    init {
        viewModelScope.launch {
            val settings = withContext(Dispatchers.IO) { settingsDao.getSettings() }
            settings?.let {
                selectedCurrency = it.currencySymbol
                selectedUiMode = UiMode.valueOf(it.uiMode)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun putSelectedCurrency(currency: String) {
        selectedCurrency = currency
        viewModelScope.launch {
            val settings = SettingsEntity(1, currency, selectedUiMode.name)
            withContext(Dispatchers.IO) { settingsDao.insertSettings(settings) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun putSelectedUiMode(uiMode: UiMode) {
        selectedUiMode = uiMode
        viewModelScope.launch {
            val settings = SettingsEntity(1, selectedCurrency, uiMode.name)
            withContext(Dispatchers.IO) { settingsDao.insertSettings(settings) }
        }
    }

    fun getCurrencySymbol(): String {
        return when (selectedCurrency) {
            "USD" -> "$"
            "INR" -> "₹"
            "GBP" -> "£"
            "EUR" -> "€"
            "JPY" -> "¥"
            else -> "$"
        }
    }
}

enum class UiMode {
    Light, Dark, SystemDefault
}

