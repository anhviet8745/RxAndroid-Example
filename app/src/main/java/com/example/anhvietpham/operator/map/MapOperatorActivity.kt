package com.example.anhvietpham.operator.map

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MapOperatorActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUsersObservable()
            .map { user ->
                user.email = String.format("%s@rxjava.wtf", user.email)
                user.name = user.name.toUpperCase()
                return@map user
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getUserObserver())
        
    }

    private fun getUserObserver() : Observer<User>{
        return object : Observer<User> {
            override fun onComplete() {
                Log.e("MapOperatorActivity", "All users emitted!")
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onNext(t: User) {
                Log.e("MapOperatorActivity", "onNext: " + t.name + ", " + t.gender)
            }

            override fun onError(e: Throwable) {
                Log.e("MapOperatorActivity", "onError: " + e.message)
            }

        }
    }

    private fun getUsersObservable() : Observable<User>{
        val names = arrayOf("mark", "john", "trump", "obama")
        val users = mutableListOf<User>()
        for (name in names) {
            val user = User()
            user.name = name
            user.gender = "male"
            users.add(user)
        }
        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->
                for (user in users) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(user)
                    }
                }

                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            }).subscribeOn(Schedulers.io())
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    class User(
        var name: String = "",
        var email: String = "",
        var gender: String = "",
        var address: Address? = null
    )
    class Address(var city: String = "")
}