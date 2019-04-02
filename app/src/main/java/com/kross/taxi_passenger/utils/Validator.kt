package com.kross.taxi_passenger.utils

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import com.kross.taxi_passenger.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

open class BaseErrorsString(val context: Context) {
    open fun isShortCustomHint(): String = context.resources.getString(R.string.validator_error_txt_shot_custom_hint)
    open fun isShort(): String = context.resources.getString(R.string.validator_error_txt_shot)
    open fun isShortField(): String = context.resources.getString(R.string.validator_error_txt_shot_field)
    open fun isLong(): String = context.resources.getString(R.string.validator_error_txt_long)
    open fun withoutDot(): String = context.resources.getString(R.string.validator_error_txt_withoutDot)
    open fun withoutAtSymbol(): String = context.resources.getString(R.string.validator_error_txt_withoutAtSymbol)
    open fun notOnlyLatin(): String = context.resources.getString(R.string.validator_error_txt_notOnlyLatin)
    open fun isEmpty(): String = context.resources.getString(R.string.validator_error_txt_isEmpty)
    open fun containsSpace(): String = context.resources.getString(R.string.validator_error_txt_containsSpace)
    open fun incorrect(): String = context.resources.getString(R.string.validator_error_txt_incorrect)
    open fun notOnlyDigits(): String = context.resources.getString(R.string.validator_error_txt_notOnlyDigits)
    open fun notFitCustomRegex(): String = context.resources.getString(R.string.validator_error_txt_notFitCustomRegex)
    open fun isConstLength(): String = context.resources.getString(R.string.txt_registration_third_validation_error)
    open fun mustContainsSpace(): String = context.resources.getString(R.string.txt_registration_step_account_name_validation)
}

class Validation(val context: Context) {
    private val EMAIL_REGEX = ".+@.+\\..+".toRegex()
    private val ALL_NON_LATIN_REGEX = "[^a-zA-Z]".toRegex()
    private val ALL_LATIN_REGEX = "([a-zA-Z])+?".toRegex()
    private val ALL_DIGITS_REGEX = "([0-9])+?".toRegex()

    private var errorsString: BaseErrorsString = BaseErrorsString(context)
    private var onlyDigits: Boolean? = null //- латинкские буквы и цифры
    private var minLengthWithCustomHint: Int? = null //- минимальная длина
    private var minLength: Int? = null //- минимальная длина
    private var minLengthField: Int? = null //- минимальная длина
    private var maxLength: Int? = null //- максимальная длина
    private var constLength: Int? = null //- const length
    private var onlyLatin: Boolean? = null //- латинкские буквы и цифры
    private var isNotEmpty: Boolean? = null //- пустота
    private var canContainSpaces: Boolean? = null //- пробелы
    private var mustContainSpaces: Boolean? = null //- пробелы
    private var checkForDot: Boolean? = null //- на точку
    private var checkForAtSymbol: Boolean? = null //- на @
    private var useEmailRegex: Boolean? = null //- регулярка
    private var customRegex: Regex? = null //- регулярка

    fun customErrors(errorsStrings: BaseErrorsString) = consume { errorsString = errorsStrings }
    fun setMinLength(int: Int) = consume { minLength = int }
    fun setMinLengthField(int: Int) = consume { minLengthField = int }
    fun setMinLengthWithCustomHint(int: Int) = consume { minLengthWithCustomHint = int }
    fun setMaxLength(int: Int) = consume { maxLength = int }
    fun setConstLengthWithSpace(int: Int) = consume { constLength = int }
    fun hasDot() = consume { checkForDot = true }
    fun hasAtSymbol() = consume { checkForAtSymbol = true }
    fun onlyLatin() = consume { onlyLatin = true }
    fun notEmpty() = consume { isNotEmpty = true }
    fun canContainSpaces(boolean: Boolean) = consume { canContainSpaces = boolean }
    fun mustContainSpaces(boolean: Boolean) = consume { mustContainSpaces = boolean }
    fun useEmailRegex() = consume { useEmailRegex = true }
    fun useOnlyDigits() = consume { onlyDigits = true }
    fun useCustomRegex(regex: Regex) = consume { customRegex = regex }

    fun getAllConditions(string: String): MutableList<Pair<Boolean, String>> {
        val conditionsList: MutableList<Pair<Boolean, String>> = ArrayList()
        with(conditionsList) {
            minLengthWithCustomHint?.let { addCustom(string.length >= it) { isShortCustomHint() } }
            minLengthField?.let { addCustom(string.length >= minLengthField!!) { isShortField() } }
            minLength?.let { addCustom(string.length >= minLength!!) { isShort() } }
            maxLength?.let { addCustom(string.length <= maxLength!!) { isLong() } }
            constLength?.let { addCustom(getLengthWithoutSpace(string, it)) {isConstLength()} }
            onlyDigits?.let { addCustom(string.matches(ALL_DIGITS_REGEX)) { notOnlyDigits() } }
            checkForDot?.let { addCustom(string.contains(".")) { withoutDot() } }
            checkForAtSymbol?.let { addCustom(string.contains("@")) { withoutAtSymbol() } }
            customRegex?.let { addCustom(string.matches(customRegex!!)) { notFitCustomRegex() } }
            useEmailRegex?.let { addCustom(string.matches(EMAIL_REGEX)) { incorrect() } }
            canContainSpaces?.let { addCustom(if (!canContainSpaces!!) !string.contains(" ") else true) { containsSpace() } }
            mustContainSpaces?.let { addCustom(if (mustContainSpaces!!) string.contains(" ") else true) { mustContainsSpace() } }
            onlyLatin?.let { addCustom(string.replace(ALL_NON_LATIN_REGEX, "").matches(ALL_LATIN_REGEX)) { notOnlyLatin() } }
            isNotEmpty?.let { addCustom(string.isNotEmpty()) { isEmpty() } }
        }
        return conditionsList
    }

    private fun getLengthWithoutSpace(text: String, count: Int): Boolean{
        var length = text.length
        if(text.contains(" ")){ length = text.replace(" ", "").length }
        return length == count
    }

    private inline fun consume(f: () -> Unit): Validation {
        f.invoke()
        return this
    }

    private fun MutableList<Pair<Boolean, String>>.addCustom(condition: Boolean, errorString: BaseErrorsString.() -> String): Boolean = this.add(Pair(condition, errorString.invoke(errorsString)))
}

fun TextInputLayout.isFieldValid(validation: Validation): Boolean {
    editText?.apply {
        val valid = validation.getAllConditions(text.toString())
                .filterNot { it.first }
                .map { buildError(it.second) }
                .all { it }
        return valid
    }
    return false
}

fun TextInputLayout.isFieldValidWithoutError(validation: Validation): Boolean {
    editText?.apply {
        val valid = validation.getAllConditions(text.toString())
                .filterNot { it.first }
                .all { it.first }
        return valid
    }
    return false
}

fun TextInputEditText.isFieldValid(validation: Validation): Boolean {
    val valid = validation.getAllConditions(text.toString())
            .filterNot { it.first }
            .all { it.first }
    return valid
}

private fun TextInputLayout.buildError(errorString: String?): Boolean {
    error = errorString
    setErrorTextAppearance(R.style.StyleInputLayoutError)
    return errorString.isNullOrEmpty()
}