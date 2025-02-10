package com.ssafy.firstproject.ui.check

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentCheckBinding

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

                        extractTextByImage(it)
                    }
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
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

        // EditText 터치시 Scrollview 스크롤 무시
        binding.tieExtractTextInput.setOnTouchListener { _, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
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
                binding.tilExtractTextInput.visibility = View.VISIBLE
                binding.tilUrlInput.visibility = View.GONE
                binding.tilContentInput.visibility = View.GONE
                binding.ivCamera.visibility = View.GONE
            }

            getString(R.string.type_url) -> {
                binding.groupAddImg.visibility = View.GONE
                binding.tilExtractTextInput.visibility = View.GONE
                binding.tilUrlInput.visibility = View.VISIBLE
                binding.tilContentInput.visibility = View.GONE
                binding.ivCamera.visibility = View.GONE
            }

            getString(R.string.type_content) -> {
                binding.groupAddImg.visibility = View.GONE
                binding.tilExtractTextInput.visibility = View.GONE
                binding.tilUrlInput.visibility = View.GONE
                binding.tilContentInput.visibility = View.VISIBLE
                binding.ivCamera.visibility = View.VISIBLE
            }
        }
    }

    private fun extractTextByImage(img: Uri) {
        runCatching {
            // URI로부터 InputImage 객체 생성
            val image = InputImage.fromFilePath(requireContext(), img)

            // 한글 텍스트 인식기 초기화
            val recognizer =
                TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

            // 이미지 처리 시작
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    binding.tieExtractTextInput.setText(visionText.text)
                }
                .addOnFailureListener { exception ->
                    binding.tieExtractTextInput.setText(exception.message)
                }
        }.onFailure { exception ->
            exception.printStackTrace()
        }
    }
}