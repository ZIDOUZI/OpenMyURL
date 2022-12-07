package zdz.openmyurl

import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    
    val list = mutableListOf<String>()
    
    val contracts = ActivityResultContracts.StartIntentSenderForResult()
    
}