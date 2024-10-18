package com.policyboss.policybosspro.core.viewModel.salesMaterialVM


import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.Event
import com.policyboss.policybosspro.core.repository.appRepository.AppRepository
import com.policyboss.policybosspro.core.response.salesMaterial.DocEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialProductDetailsResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.core.viewModel.SalesMaterialType
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utils.BitmapUtility
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SalesMaterialViewNodel @Inject constructor(

    @ApplicationContext val context: Context,
    private val appRepository: AppRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {

    //region Decleration of SalesMaterial State
    private val salesMaterialStateFlow : MutableStateFlow<Event<APIState<SalesMaterialResponse>>> = MutableStateFlow(
        Event(APIState.Empty())
    )
    val SalesMaterialResponse: StateFlow<Event<APIState<SalesMaterialResponse>>>
        get() = salesMaterialStateFlow



    private val salesMaterialDtlStateFlow : MutableStateFlow<Event<APIState<SalesMaterialProductDetailsResponse>>> = MutableStateFlow(
        Event(APIState.Empty())
    )
    val SalesMaterialDtlResponse: StateFlow<Event<APIState<SalesMaterialProductDetailsResponse>>>
        get() = salesMaterialDtlStateFlow





    //region Handling Posp,Fba Image
    //keyword means that the property pospBitmap can be read (accessed) publicly,
    // but it can only be modified (set) privately within the clas

    //Note: POSPBitmap, FBABitmap not save any data bec we used in dif Activity
    var POSPBitmap: Bitmap? = null
        private set

    var FBABitmap: Bitmap? = null
        private set

    //used for svaed data
    var combinedImage: Bitmap? = null
        private set

    var salesPhoto: Bitmap? = null
        private set

    //endregion




    suspend fun processPospBitmapFromUrl(
        salesType: SalesMaterialType,
        url: URL?,
        pospName: String,
        pospDesg: String,
        pospMob: String,
        pospEmail: String,
        textSize: Float = 25F,
        height: Int = 200,
        textMargin: Int = 10,
    ): Bitmap? {
        return withContext(Dispatchers.Default) {
            val startHeight: Float = (height - 4 * textSize - 3 * textMargin) / 2f  // Ensure float division

            val tempBitmap = Utility.downloadBitmapFromUrl(url)

            tempBitmap?.let {
                // Reusing common logic in a helper function
                return@withContext createBitmapForSalesType(
                    tempBitmap, salesType, pospName, pospDesg, pospMob, pospEmail, height, textSize, textMargin, startHeight
                )
            } ?: return@withContext null // If bitmap is null, return null
        }
    }

    private fun createBitmapForSalesType(
        bitmap: Bitmap,
        salesType: SalesMaterialType,
        pospName: String,
        pospDesg: String,
        pospMob: String,
        pospEmail: String,
        height: Int,
        textSize: Float,
        textMargin: Int,
        startHeight: Float
    ): Bitmap? {
        val resultBitmap = BitmapUtility.createBitmap(
            pospPhoto = bitmap,
            pospName = pospName,
            pospDesg = pospDesg,
            pospMob = pospMob,
            pospEmail = pospEmail,
            textSize = textSize,
            height = height,
            textMargin = textMargin,
            startHeight = startHeight
        )

        // Logging the size of the resulting bitmap
        resultBitmap?.let {
            Log.d(Constant.TAG, "Bitmap size for $salesType: ${it.byteCount} bytes")
        }

        // Store the resulting bitmap based on SalesMaterialType
        when (salesType) {
            SalesMaterialType.POSP -> POSPBitmap = resultBitmap
            SalesMaterialType.FBA -> FBABitmap = resultBitmap
        }

        return resultBitmap
    }


    /////For erging and Combine logic

    suspend fun mergeProductToFooter(docsEntity: DocEntity) {
        try {
            // Download the sales photo from the provided URL in the background
            val salesPhotoUrl = URL(docsEntity.image_path)
            val salesPhoto: Bitmap? =   BitmapUtility.downloadBitmapFromUrl(salesPhotoUrl)
            // If the sales photo is downloaded successfully, combine it with the existing combinedImage
            salesPhoto?.let {

                combinedImage = BitmapUtility.combineImages(it, combinedImage )
            }

            // If sales photo is null, just return the existing combined image
          //  combinedImage

        } catch (e: Exception) {
            e.printStackTrace()
            // Return the combined image even if there was an error downloading sales photo
           // combinedImage
        }
    }

    suspend fun getOnlyProductImage(docsEntity: DocEntity) {
         try {
            // Download the product image from the provided URL
            val salePhotoUrl = URL(docsEntity.image_path)
            val salesPhoto: Bitmap? = withContext(Dispatchers.IO) {
                BitmapUtility.downloadBitmapFromUrl(salePhotoUrl)
            }
            // If the sales photo is downloaded successfully, set it as the combinedImage
            if (salesPhoto != null) {
                combinedImage = salesPhoto
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    suspend fun retrieveSalesBitmap(salesProductId: Int, docsEntity: DocEntity,POSPBitmap : Bitmap ? = null, FBABitmap : Bitmap ? = null) {

        // region Add POSP or FBA Details on combinedImage
        ///****************************************************/
//        combinedImage = if (isSecondImageToShow) {
//            try {
//                when (salesProductId) {
//                    1, 2, 6, 8 -> POSPBitmap
//                    3, 4, 5 -> FBABitmap
//                    else -> null
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null  // Return null in case of an exception
//            }
//        } else {
//            null
//        }
        ///****************************************************/
        //endregion
        when (salesProductId) {
            1, 2, 6, 8 -> {
                // Download POSP details and merge with footer if needed
                POSPBitmap?.let {
                    combinedImage = BitmapUtility.combineImages(it, combinedImage)
                }
                mergeProductToFooter(docsEntity)
            }
            3, 4, 5 -> {
                // Download FBA details and merge with footer if needed
                FBABitmap?.let {
                    combinedImage = BitmapUtility.combineImages(it, combinedImage)
                }
                mergeProductToFooter(docsEntity)
            }
            else -> {
                // If not POSP or FBA, just get the product image
                getOnlyProductImage(docsEntity)
            }
        }
    }


    //region comment


//    suspend fun processPospBitmapFromUrl(
//        salesType: SalesMaterialType,
//        url: URL?,
//        pospName: String,
//        pospDesg: String,
//        pospMob: String,
//        pospEmail: String,
//        textSize: Float = 25F,
//        height: Int = 200,
//        textMargin: Int = 10,
//    ): Bitmap? {
//        return withContext(Dispatchers.Default) {
//            val startHeight: Float = (height - 4 * textSize - 3 * textMargin) / 2f  // Ensure float division
//
//            val tempBitmap = Utility.downloadBitmapFromUrl(url)
//            // Download and set the bitmap
//            when(salesType){
//                SalesMaterialType.POSP -> {
//
//                    if (tempBitmap != null) {
//                        pospDetails = BitmapUtility.createBitmap(
//                            pospPhoto = tempBitmap,
//                            pospName = pospName,
//                            pospDesg = pospDesg,
//                            pospMob = pospMob,
//                            pospEmail = pospEmail,
//                            height = height,
//                            textMargin = textMargin,
//                            startHeight = startHeight  // Make sure createBitmap accepts Float
//                        )
//
//                        // Log bitmap dimensions or size instead of using `toString()`
//                        pospDetails?.let {
//                            Log.d(Constant.TAG, "Bitmap size: ${it.byteCount} bytes")  // Logs bitmap size
//                        }
//
//                        return@withContext pospDetails
//                    } else {
//                        return@withContext null
//                    }
//                }
//                SalesMaterialType.FBA -> {
//
//                    if (tempBitmap != null) {
//                        fbaDetails = BitmapUtility.createBitmap(
//                            pospPhoto = tempBitmap,
//                            pospName = pospName,
//                            pospDesg = pospDesg,
//                            pospMob = pospMob,
//                            pospEmail = pospEmail,
//                            height = height,
//                            textMargin = textMargin,
//                            startHeight = startHeight  // Make sure createBitmap accepts Float
//                        )
//
//                        // Log bitmap dimensions or size instead of using `toString()`
//                        fbaDetails?.let {
//                            Log.d(Constant.TAG, "Bitmap size: ${it.byteCount} bytes")  // Logs bitmap size
//                        }
//
//                        return@withContext fbaDetails
//                    } else {
//                        return@withContext null
//                    }
//
//                }
//            }
//
//
//
//        }
//    }

    //endregion



    fun getSalesProducts() = viewModelScope.launch {


        var body = HashMap<String,String>()
        body.put("app_version",prefManager.getAppVersion())
        body.put("device_code",prefManager.getDeviceID())
        body.put("ssid",prefManager.getSSID())
        body.put("fbaid",prefManager.getFBAID())
        body.put("product_id", "")


        salesMaterialStateFlow.value =  Event(APIState.Loading())


        appRepository.getSalesProducts(body)
            .catch {
                salesMaterialStateFlow.value = Event(APIState.Failure(it.message ?: Constant.Fail))

            }.collect{ data ->

                if(data.isSuccessful){

                    if(data.body()?.StatusNo?:1 == 0){
                        salesMaterialStateFlow.value =Event( APIState.Success(data = data.body()))
                    }else{
                        salesMaterialStateFlow.value = Event(APIState.Failure(errorMessage = data.body()?.Message ?: Constant.ErrorMessage))
                    }

                }else{
                    salesMaterialStateFlow.value = Event(APIState.Failure(errorMessage = Constant.SeverUnavaiable))
                }

            }


    }


    fun getSalesProductDetail(prodID : String) = viewModelScope.launch {


        var body = HashMap<String,String>()
        body.put("app_version",prefManager.getAppVersion())
        body.put("device_code",prefManager.getDeviceID())
        body.put("ssid",prefManager.getSSID())
        body.put("fbaid",prefManager.getFBAID())
        body.put("product_id", prodID)


        salesMaterialDtlStateFlow.value =  Event(APIState.Loading())


        appRepository.getSalesProductDetail(body)
            .catch {
                salesMaterialStateFlow.value = Event(APIState.Failure(it.message ?: Constant.Fail))

            }.collect{ data ->

                if(data.isSuccessful){

                    if(data.body()?.StatusNo?:1 == 0){
                        salesMaterialDtlStateFlow.value = Event( APIState.Success(data = data.body()))

                    }else{
                        salesMaterialDtlStateFlow.value = Event(APIState.Failure(errorMessage = data.body()?.Message ?: Constant.ErrorMessage))
                    }

                }else{
                    salesMaterialDtlStateFlow.value = Event(APIState.Failure(errorMessage = Constant.SeverUnavaiable))
                }

            }


    }



    fun getSalesProductClick(product_id : String,product_name : String,content_url: String, content_source: String,  language : String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("app_version", prefManager.getAppVersion())
        body.put("product_id", product_id)
        body.put("product_name", product_name)
        body.put("device_code", prefManager.getDeviceID())

        body.put("fbaid", prefManager.getFBAID())
        body.put("ssid", prefManager.getSSID())
        body.put("type_of_content", content_source)
        body.put("content_url", content_url)

        body.put("language", language)
        body.put("content_source", content_source)
        body.put("fbaid", prefManager.getFBAID())





        try {
            // Concurrent API calls
            val saleProdDeferred = async { appRepository.getSalesProductClick(body) }

            val SaleProdResponse = saleProdDeferred.await()

            if (SaleProdResponse?.isSuccessful() == true) {

                Log.d(Constant.TAG, " Success: ${SaleProdResponse.message()}")
            }else{
                Log.d(Constant.TAG, "Error occurred att : ${SaleProdResponse?.message()}")
            }

        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
        }

    }





}