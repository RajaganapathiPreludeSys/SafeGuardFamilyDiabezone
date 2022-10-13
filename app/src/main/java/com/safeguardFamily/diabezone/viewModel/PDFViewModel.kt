package com.safeguardFamily.diabezone.viewModel

import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import java.io.*

class PDFViewModel : BaseViewModel() {

    private var inProgress = false

    val PDF_CACHED_FILE_NAME = "mypdf.pdf"
    val pdfPath: MutableLiveData<Uri> = MutableLiveData<Uri>()

     fun loadPdf(cacheDir: File, url: String) {
        if (inProgress) return
        inProgress = true

        val pdf = File(cacheDir, PDF_CACHED_FILE_NAME)
        if (pdf.exists() && pdf.canRead()) {
            pdfPath.value = pdf.toUri()
            inProgress = false
            return
        }

//		If your pdf may be changed and backend controling it - http mechanics are recommeneded for caching
//		This will cause additional network traffic and additional alignment with backend reqired to make sure proper http headers setup on a backend for OkHTTP cache to work properly
//		Then always get response from OkHttp after initialization
        // 10Mb - make sure PDf will fit
//		val cacheSize = 10L * 1024 * 1024
        // probably don't use default cache folder to not mix this cache with your usual REST responses cache - this file may not be requestet often, but it's big
//		val cacheDirectory = File(cacheDir.toURI())
//		val cache = Cache(cacheDirectory, cacheSize)
//		val client = OkHttpClient.Builder()
//				.cache(cache)
//				.build()

        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //show error state and send analytics report?
                Handler(Looper.getMainLooper()).post {
                    inProgress = false
                }

            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    //show error state and send analytics report?
                    Handler(Looper.getMainLooper()).post {
                        inProgress = false
                    }
                    return
                }
                val result = File(cacheDir, PDF_CACHED_FILE_NAME)
                val body = response.body!!
                val inputStream = body.byteStream()
                val input = BufferedInputStream(inputStream)
                val output: OutputStream = FileOutputStream(result)
                input.copyTo(output)
                output.flush();
                output.close();
                input.close(); //will closing just body is enough??
                body.close();

                //Update
                Handler(Looper.getMainLooper()).post {
                    pdfPath.value = result.toUri()
                    inProgress = false
                }
            }
        })
    }
}