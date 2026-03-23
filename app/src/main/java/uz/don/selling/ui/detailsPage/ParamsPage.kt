package uz.don.selling.ui.detailsPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uz.don.selling.R
import uz.don.selling.data.remote.models.showProducts.PostDetailsData
import uz.don.selling.ui.main.add.TextMedium14
import uz.don.selling.ui.main.add.TextNormal12
import uz.don.selling.ui.main.add.TextNormal14
import uz.don.selling.ui.main.add.TextNormal16
import uz.don.selling.ui.theme.spacing

@Composable
fun PostParams(
    data: PostDetailsData,
    modifier: Modifier = Modifier
) {

    Column {
        TextNormal16(title = stringResource(id = R.string.characteristics))
        Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen8Dp))
        val newList = data.category.post_param.filter { it.type != "price" }
            .filter { it.type != "multichoice" }
        newList.forEach {
            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen2Dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextNormal14(title = "● ${it.label}")
                TextMedium14(title = " — ${it.post_value[0].label} ${it.param_unit?.label?:""}")
            }
        }
        val multiChoice = data.category.post_param
            .filter { it.type == "multichoice" }

        multiChoice.forEach {
            TextNormal14(title = "● ${it.label}")
            Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen2Dp))
            it.post_value.forEach { value ->
                Spacer(modifier = modifier.height(MaterialTheme.spacing.dimen1Dp))
                TextNormal12(title = "   — ${value.label}")
            }
        }
    }

}

@Composable
fun HoryzontalText() {

}