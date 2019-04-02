package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import android.view.View
import com.google.gson.JsonObject
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.BankAccount
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.database.entity.WalletEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.EmailVerifyBody
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.ErrorHelper
import com.kross.taxi_passenger.utils.debugLog
import com.kross.taxi_passenger.utils.errorLog
import okhttp3.ResponseBody
import org.jetbrains.anko.getStackTraceString
import retrofit2.Response

class CabinetViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val loadingViewModel = SingleLiveEvent<Int>()

    fun getPassenger(contentType: String, token: String, passengerId: Int): LiveData<PassengerEntity> {
        repository.getProfileInfo(contentType, token, passengerId).doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }.subscribe(
                        { passengerEntity: PassengerEntity? -> debugLog(this, "Obtain passenger entity") },
                        { throwable: Throwable? -> errorLog(this, "Error - ${throwable?.getStackTraceString()}") })
        return repository.getPassenger(passengerId)
    }

    fun getWallet(token: String, passengerId: Int): LiveData<WalletEntity> {
        repository.getWallet(token, passengerId).subscribe(
                { walletEntity: WalletEntity? -> debugLog(this, "Obtain wallet") },
                { throwable: Throwable? -> errorLog(this, "Error - ${throwable?.getStackTraceString()}") })
        return repository.getWallet(passengerId)
    }

    fun changePassword(contentType: String,
                       apiKey: String,
                       jsonObject: JsonObject, block: (Boolean, String?) -> Unit) {
        repository.changePassword(contentType, apiKey, jsonObject)
                .subscribe({
                    block.invoke(it.isSuccessful, it.message())
                }, {
                    print("error changing password")
                    block.invoke(false, null)
                })
    }

    fun shareReferralLink(context: Context, referral: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, referral)
        intent.type = "text/plain"
        val title = context.resources.getString(R.string.txt_cabinet_share_message)
        return Intent.createChooser(intent, title)
    }

    fun addBankAccount(token: String, account: BankAccount, handler: (Boolean, String?) -> Unit) {
        repository.addBankAccountToDB(account)
        val json = JsonObject()
        json.addProperty("bank_name", account.bankName)
        json.addProperty("bank_account", account.accountNumber)
        json.addProperty("account_name", account.accountName)
        repository.addBankAccount(token, json)
                .subscribe({
                    handler.invoke(true, null)
                }, {
                    handler.invoke(false, ErrorHelper.parseErrorAndGetString(getContext(), it))
                })
    }

    fun paymentWithDraw(token: String, jsonObject: JsonObject, handler: (Boolean, String?) -> Unit) {
        repository.paymentWithDraw(token, jsonObject)
                .subscribe({
                    handler.invoke(it.isSuccessful, it.message())
                }, {
                    handler.invoke(false, null)
                })
    }

    fun getBankAccount(): BankAccount? {
        return repository.getBankAccount()
    }

    private val liveDateBecomeOwner = SingleLiveEvent<Boolean>()
    private val liveDateVerify = SingleLiveEvent<Boolean>()


    @SuppressLint("CheckResult")
    fun verifyEmail(ccontentType: String, apiKey: String, authorization: EmailVerifyBody): LiveData<Boolean> {

        repository.emailVerify(contentType, apiKey, authorization)
                .doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe(
                        { response: Response<ResponseBody>? -> response?.let { liveDateVerify.value = it.isSuccessful } },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDateVerify
    }


    fun getLiveDataLoading(): LiveData<Int> = loadingViewModel


}