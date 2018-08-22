/*
 * Copyright (c) 2018 Orkhan Gasimli - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This file is proprietary and confidential.
 * Written by Orkhan Gasimli orkhan.gasimli@gmail.com in 2018.
 */

package app.ogasimli.remoter.helper.rx

import androidx.appcompat.widget.SearchView
import app.ogasimli.remoter.ui.custom.RemoterSearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Custom Observable for emitting search query
 *
 * @author Orkhan Gasimli on 21.08.2018.
 */
object RxSearchObservable {

    // Keep last query to avoid onNext call on first time initialization
    var lastQuery: String? = null

    fun fromView(searchView: RemoterSearchView): Observable<String> {

        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (lastQuery != null) {
                    subject.onNext(newText)
                }
                lastQuery = newText
                return true
            }
        })

        return subject
    }
}