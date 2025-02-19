package com.ssafy.firstproject.ui.profileedit

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.firstproject.R
import com.ssafy.firstproject.base.BaseFragment
import com.ssafy.firstproject.databinding.FragmentProfileEditBinding
import com.ssafy.firstproject.ui.profileedit.viewmodel.ProfileEditViewModel
import com.ssafy.firstproject.util.setOnSingleClickListener


private const val TAG = "MyPageEditFragment"

class ProfileEditFragment : BaseFragment<FragmentProfileEditBinding>(
    FragmentProfileEditBinding::bind,
    R.layout.fragment_profile_edit
) {

    private val viewModel: ProfileEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeValidation()
        initEvent()
    }

    private fun initEvent() {
        binding.ivEditClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tieEditPwInput.addTextChangedListener {
            viewModel.checkPasswordValidation(binding.tieEditPwInput.text.toString())
        }

        binding.tieEditPwCheckInput.addTextChangedListener {
            viewModel.checkPasswordMatch(
                binding.tieEditPwInput.text.toString(),
                binding.tieEditPwCheckInput.text.toString()
            )
        }

        binding.tieEditNicknameInput.addTextChangedListener {
            viewModel.setNicknameDuplicateFalse()
            viewModel.checkNickNameValidation(binding.tieEditNicknameInput.text.toString())
        }

        binding.btnEditCheckNickname.setOnSingleClickListener {
            viewModel.checkNicknameDuplicate(binding.tieEditNicknameInput.text.toString())
        }
    }

    private fun observeValidation() {
        viewModel.isPasswordValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvEditPwMetaInfo.visibility = View.GONE
                viewModel.checkPasswordMatch(
                    binding.tieEditPwInput.text.toString(),
                    binding.tieEditPwCheckInput.text.toString()
                )
            } else {
                binding.tvEditPwMetaInfo.visibility = View.VISIBLE
                binding.tvEditPwMetaInfo.text = getString(R.string.user_limit_message)
                binding.tvEditPwMetaInfo.setTextColor(
                    getColor(
                        requireContext(),
                        R.color.maximumRed
                    )
                )
            }
            viewModel.checkAllValidation()
        }

        viewModel.isPasswordMatch.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.isPasswordValid.value == true) {
                    binding.tvEditPwMetaInfo.visibility = View.VISIBLE
                    binding.tvEditPwMetaInfo.text = getString(R.string.user_password_message)
                    binding.tvEditPwMetaInfo.setTextColor(Color.BLUE)
                }
            } else {
                binding.tvEditPwMetaInfo.visibility = View.VISIBLE
                binding.tvEditPwMetaInfo.text = getString(R.string.user_not_correct_message)
                binding.tvEditPwMetaInfo.setTextColor(
                    getColor(
                        requireContext(),
                        R.color.maximumRed
                    )
                )
            }
            viewModel.checkAllValidation()
        }

        viewModel.isNicknameValid.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvEditNicknameMetaInfo.visibility = View.GONE
            } else {
                binding.tvEditNicknameMetaInfo.visibility = View.VISIBLE
                binding.tvEditNicknameMetaInfo.text = getString(R.string.nickname_limit_message)
                binding.tvEditNicknameMetaInfo.setTextColor(
                    getColor(
                        requireContext(),
                        R.color.maximumRed
                    )
                )
            }

            binding.btnEditCheckNickname.isEnabled = it
            viewModel.checkAllValidation()
        }

        viewModel.isNicknameDuplicate.observe(viewLifecycleOwner) {
            if (it.available) {
                binding.tvEditNicknameMetaInfo.visibility = View.VISIBLE
                binding.tvEditNicknameMetaInfo.text = it.message
                binding.tvEditNicknameMetaInfo.setTextColor(Color.BLUE)
            } else {
                binding.tvEditNicknameMetaInfo.visibility = View.VISIBLE
                binding.tvEditNicknameMetaInfo.text = it.message
                binding.tvEditNicknameMetaInfo.setTextColor(
                    getColor(
                        requireContext(),
                        R.color.maximumRed
                    )
                )
            }
            viewModel.checkAllValidation()
        }

        viewModel.isAllValid.observe(viewLifecycleOwner) {
            binding.btnEdit.isEnabled = it
        }
    }
}


