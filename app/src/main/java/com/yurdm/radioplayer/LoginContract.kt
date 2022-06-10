package com.yurdm.radioplayer

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginContract : ActivityResultContract<GoogleSignInOptions, GoogleSignInAccount?>() {
    override fun createIntent(context: Context, input: GoogleSignInOptions): Intent {
        val client = GoogleSignIn.getClient(context, input)
        return client.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)
        val account: GoogleSignInAccount? = try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            null
        }

        return account
    }
}