/*
 * Copyright (c) 2015 Mark Platvoet<mplatvoet@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.komponents.kovenant.incubating

import nl.komponents.kovenant.*
import java.util.*


/**
 * Undocumented API. Added as a public testable experimental feature. Implementation and signature might change.
 */
fun <V, R> Sequence<V>.mapEach(context: Context = Kovenant.context, bind: (V) -> R): Promise<List<R>, Exception> {
    val deferred = deferred<List<R>, Exception>(context)
    context.workerContext.offer {
        //TODO ArrayList is jvm only
        val promises = ArrayList<Promise<R, Exception>>()
        forEach {
            value ->
            promises.add(task(context) { bind(value) })
        }
        val masterPromise = all(promises)
        masterPromise success {
            deferred resolve it
        }
        masterPromise fail {
            deferred reject it
        }
    }

    return deferred.promise
}

