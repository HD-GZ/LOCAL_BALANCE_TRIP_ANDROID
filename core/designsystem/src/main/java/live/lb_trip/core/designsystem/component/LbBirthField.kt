package live.lb_trip.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import live.lb_trip.core.designsystem.LbColors
import live.lb_trip.core.designsystem.R

@Composable
fun LbBirthField(
    year: String,
    month: Int,
    day: String,
    onYearChange: (String) -> Unit,
    onMonthChange: (Int) -> Unit,
    onDayChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    yearFocusRequester: FocusRequester? = null,
) {
    var monthMenuExpanded by remember { mutableStateOf(false) }
    val dayFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Text(
            text = if (required) {
                buildAnnotatedString {
                    append(label)
                    withStyle(SpanStyle(color = LbColors.RequiredMark, fontWeight = FontWeight.SemiBold)) {
                        append(" *")
                    }
                }
            } else {
                buildAnnotatedString { append(label) }
            },
            color = LbColors.Ink2,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = if (required) (-0.065).sp else TextUnit.Unspecified,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicTextField(
                value = year,
                onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) onYearChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { dayFocusRequester.requestFocus() }),
                singleLine = true,
                cursorBrush = SolidColor(LbColors.Green),
                textStyle = TextStyle(color = LbColors.Ink, fontSize = 15.5.sp),
                modifier = if (yearFocusRequester != null) {
                    Modifier.weight(2.2f).focusRequester(yearFocusRequester)
                } else {
                    Modifier.weight(2.2f)
                },
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (year.isEmpty()) {
                            Text(text = "YYYY", color = LbColors.Ink4, fontSize = 15.5.sp)
                        }
                        innerTextField()
                    }
                },
            )

            Box(modifier = Modifier.weight(1.5f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { monthMenuExpanded = true }
                        .padding(horizontal = 15.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = if (month == 0) {
                            stringResource(R.string.core_birth_field_month_placeholder)
                        } else {
                            stringResource(R.string.core_birth_field_month_value_template, month)
                        },
                        color = if (month == 0) LbColors.Ink4 else LbColors.Ink,
                        fontSize = 15.5.sp,
                    )
                }
                DropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false },
                ) {
                    for (selectedMonth in 1..12) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.core_birth_field_month_value_template, selectedMonth)) },
                            onClick = {
                                onMonthChange(selectedMonth)
                                monthMenuExpanded = false
                            },
                        )
                    }
                }
            }

            BasicTextField(
                value = day,
                onValueChange = { if (it.length <= 2 && it.all { char -> char.isDigit() }) onDayChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                cursorBrush = SolidColor(LbColors.Green),
                textStyle = TextStyle(color = LbColors.Ink, fontSize = 15.5.sp),
                modifier = Modifier.weight(1.2f).focusRequester(dayFocusRequester),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .border(1.dp, LbColors.Line, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (day.isEmpty()) {
                            Text(text = stringResource(R.string.core_birth_field_day_placeholder), color = LbColors.Ink4, fontSize = 15.5.sp)
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}
