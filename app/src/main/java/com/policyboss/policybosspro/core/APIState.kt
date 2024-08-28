package com.policyboss.policybosspro.core


sealed class APIState<T> ( val data: T? = null,
                           val errorMessage: String? = null){
    class Loading<T> : APIState<T>()
    class Success<T>(data: T? = null) : APIState<T>(data = data)
    class Failure<T>( errorMessage: String) : APIState<T>(errorMessage = errorMessage)
    class Empty<T>: APIState<T>()
}

