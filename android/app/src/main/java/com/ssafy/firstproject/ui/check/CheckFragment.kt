package com.ssafy.firstproject.ui.check

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentCheckBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class CheckFragment : BaseFragment<FragmentCheckBinding>(
    FragmentCheckBinding::bind,
    R.layout.fragment_check
) {

    private val args by navArgs<CheckFragmentArgs>()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 갤러리에서 이미지를 가져오는 ActivityResultLauncher 초기화
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri: Uri? = result.data?.data
                    imageUri?.let {
                        binding.ivAddImagePlus.visibility = View.GONE
                        binding.ivAddImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        binding.ivAddImage.setImageURI(it) // 이미지 설정

                        //multipart 형식으로 변환
                        val multipartBody = uriToMultipart(it, requireContext())
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 드롭다운 항목을 선택할 때의 이벤트 처리
        binding.actvCheckType.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            showBySelectedItem(selectedItem)
        }

        binding.btnCheck.setOnClickListener {
            findNavController().navigate(R.id.dest_check_detail)
        }

        // 이미지 추가 버튼 클릭 시 갤러리 실행
        binding.ivAddImagePlus.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.ivCamera.setOnClickListener {
            findNavController().navigate(R.id.dest_camera)
        }

        if (args.recognizedText.isNotEmpty()) {
            binding.actvCheckType.setText(getString(R.string.type_content))
            binding.tieContentInput.setText(args.recognizedText)
        }
    }

    override fun onResume() {
        super.onResume()

        showBySelectedItem(binding.actvCheckType.text.toString())

        val typeList = resources.getStringArray(R.array.check_types)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            typeList
        )

        binding.actvCheckType.setAdapter(adapter)
    }

    private fun showBySelectedItem(selectedItem: String) {
        when (selectedItem) {
            getString(R.string.type_img) -> {
                binding.groupAddImg.visibility = View.VISIBLE
                binding.tilUrlInput.visibility = View.GONE
                binding.tilContentInput.visibility = View.GONE
                binding.ivCamera.visibility = View.GONE
            }

            getString(R.string.type_url) -> {
                binding.groupAddImg.visibility = View.GONE
                binding.tilUrlInput.visibility = View.VISIBLE
                binding.tilContentInput.visibility = View.GONE
                binding.ivCamera.visibility = View.GONE
            }

            getString(R.string.type_content) -> {
                binding.groupAddImg.visibility = View.GONE
                binding.tilUrlInput.visibility = View.GONE
                binding.tilContentInput.visibility = View.VISIBLE
                binding.ivCamera.visibility = View.VISIBLE
            }
        }
    }

    private fun uriToMultipart(
        uri: Uri,
        context: Context,
        paramName: String = "image"
    ): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "upload_image.jpg")

        return runCatching {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(paramName, file.name, requestFile)
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }
}