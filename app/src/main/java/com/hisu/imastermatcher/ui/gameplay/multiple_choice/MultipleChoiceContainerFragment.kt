package com.hisu.imastermatcher.ui.gameplay.multiple_choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hisu.imastermatcher.databinding.FragmentMultipleChoiceContainerBinding
import com.hisu.imastermatcher.model.multiple_choice.MultipleChoiceModel
import com.hisu.imastermatcher.model.multiple_choice.MultipleChoicesResponse

class MultipleChoiceContainerFragment : Fragment() {

    private var _binding: FragmentMultipleChoiceContainerBinding? = null
    private val binding get() = _binding!!
    val questions = MultipleChoicesResponse()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMultipleChoiceContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
//        binding.vpMultipleQuestion.registerOnPageChangeCallback(onPageChangeCallback)
    }

    private fun setUpViewPager() = binding.vpMultipleQuestion.apply {
        val questionAdapter = MultipleChoiceViewPagerAdapter(
            requireActivity(), ::handleItemClick, ::handleWrongAnswer
        )

        questions.add(
            MultipleChoiceModel(
                1,
                "What is number one?",
                listOf("mot", "hai", "ba", "bon"),
                ""
            )
        )
        questions.add(
            MultipleChoiceModel(
                2,
                "What is number two?",
                listOf("mot 1", "hai 2", "ba 3", "bon 4"),
                ""
            )
        )
        questions.add(
            MultipleChoiceModel(
                3,
                "What is number three?",
                listOf("mot 1", "hai 2", "ba 3", "bon 4"),
                ""
            )
        )

        questionAdapter.questions = questions

        adapter = questionAdapter
//        isUserInputEnabled = false
    }

    private fun handleItemClick() {

    }

    private fun handleWrongAnswer() {

    }

    private var onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

        }

        override fun onPageSelected(position: Int) {
//            binding.tvCurrentProgress.text = String.format(
//                requireContext().getString(R.string.question_progress_pattern),
//                position + 1,
//                questions.size
//            )
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding.vpMultipleQuestion.unregisterOnPageChangeCallback(onPageChangeCallback)
        _binding = null
    }
}