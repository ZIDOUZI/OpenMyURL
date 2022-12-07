package zdz.openmyurl

import android.content.Intent
import android.content.Intent.EXTRA_PROCESS_TEXT
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zdz.openmyurl.ui.theme.OpenMyURLTheme
import java.net.URL

class MainActivity : ComponentActivity() {
    
    private val vm: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OpenMyURLTheme {
                // A surface container using the 'background' color from the theme
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    items(vm.list.size) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openURL(vm.list[it]) }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                IconButton(onClick = { openURL(vm.list[it]) }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_open_in_new_24),
                                        contentDescription = "open in new"
                                    )
                                }
                                Text(
                                    text = vm.list[it],
                                    fontSize = 22.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
        
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") intent.getStringExtra(Intent.EXTRA_TEXT)
                    ?.let { s ->
                        vm.list.addAll(urlReg.findAll(s).map { it.groupValues[0] })
                    }
            }
            
            Intent.ACTION_PROCESS_TEXT -> {
                intent.getStringExtra(EXTRA_PROCESS_TEXT)?.let { s ->
                    vm.list.addAll(urlReg.findAll(s).map { it.groupValues[0] })
                }
            }
        }
        
        if (vm.list.size == 1) openURL(vm.list.first())
    }
    
    private fun openURL(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = uri
        vm.viewModelScope.launch {
            startActivity(intent)
            delay(1000)
            finishAndRemoveTask()
        }
    }
    
    companion object {
        private val urlReg = Regex(
            "(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]",
            RegexOption.IGNORE_CASE
        )
    }
}