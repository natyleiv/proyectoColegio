package cl.duoc.bitacora.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Un solo lugar donde se configuran las conexiones a los microservicios.
// 10.0.2.2 es la IP con la que el emulador llega al "localhost" del computador.
object RetrofitClient {

    private const val URL_BITACORAS = "http://10.0.2.2:8082/"
    // ms-usuarios (8081): se agrega en el paso del login, junto con UsuariosApi

    private fun crearRetrofit(urlBase: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val bitacorasApi: BitacorasApi by lazy {
        crearRetrofit(URL_BITACORAS).create(BitacorasApi::class.java)
    }
}