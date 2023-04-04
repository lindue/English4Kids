package com.hisu.imastermatcher.ui.play_style.match_pairs

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.imastermatcher.R
import com.hisu.imastermatcher.databinding.FragmentMatchingAudioImagePairsBinding
import com.hisu.imastermatcher.databinding.FragmentMatchingWordPairsBinding
import com.hisu.imastermatcher.model.Card
import com.hisu.imastermatcher.model.PairMatchingModel
import com.hisu.imastermatcher.model.PairMatchingResponse
import com.hisu.imastermatcher.ui.play_style.audio_image.MatchingAudioImageAdapter
import com.makeramen.roundedimageview.RoundedImageView
import es.dmoral.toasty.Toasty
import java.util.*

class MatchingWordPairsFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit
) : Fragment() {

    private var _binding: FragmentMatchingWordPairsBinding? = null
    private val binding get() = _binding!!

    private lateinit var wordPairsAdapter: MatchingWordPairsAdapter
    private lateinit var wordPairsResponse: PairMatchingResponse
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result

    private var prev: PairMatchingModel? = null
    private var prevPos: Int = -1
    private var counter = 0
//    private var wrongMoves = 5


    private val correctMsgs = listOf<String>(
        "Tuyệt vời ông mặt trời!",
        "Chính xác!",
        "Hay quá bạn ơi!",
        "Quá xuất sắc!",
        "10 điểm!"
    )

    private val wrongMsgs = listOf<String>(
        "Úi sai rồi!",
        "Chúc bạn may mắn lần sau",
        "Hic! Dịch sai rồi",
        "Học bài kỹ lại nhaa",
        "0 điểm về chỗ!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchingWordPairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo: later will add audio - word pairs
        wordPairsResponse = PairMatchingResponse(
            listOf(
                PairMatchingModel(1, 1, -1, "Màu đen", isAudioWordPairs = false),
                PairMatchingModel(2, 2, -1, "grampa", isAudioWordPairs = true),

                PairMatchingModel(3, 3, -1, "Bóng chày", isAudioWordPairs = false),
                PairMatchingModel(4, 4, -1, "old", isAudioWordPairs = true),

                PairMatchingModel(5, 2, -1, "Ông", isAudioWordPairs = false),
                PairMatchingModel(6, 5, -1, "fish", isAudioWordPairs = true),

                PairMatchingModel(7, 4, -1, "Cũ", isAudioWordPairs = false),
                PairMatchingModel(8, 3, -1, "baseball", isAudioWordPairs = true),

                PairMatchingModel(9, 5, -1, "Con cá", isAudioWordPairs = false),
                PairMatchingModel(10, 1, -1, "black", isAudioWordPairs = true),
            ),
            "",
            "",
            allowedMoves = 5
        )

        binding.tvModeLevel.text = requireContext().getString(R.string.tap_connect_pairs)

        wordPairsAdapter = MatchingWordPairsAdapter(requireContext(), ::handle)

        wordPairsAdapter.pairs = wordPairsResponse.images

        binding.rvMatchingPairs.adapter = wordPairsAdapter

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            } else {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
//                wrongAnswerListener.invoke()
            }
        }

        checkAnswer()
    }

    private fun handle(item: PairMatchingModel, position: Int) {
        if (prev != null) {

            //ensure self select
            if (prev?.id == item.id) return

            //in audio - word gameplay
            //if previous item & current item is audio, then reset select color of prev item
            //todo: play audio later
            if(prev?.isAudioWordPairs == true && item.isAudioWordPairs == true) {
                wordPairsAdapter.resetPairsSelected(prevPos)
                prev = item
                prevPos = position
                return
            }

            if (item.pairId == prev?.pairId) {
                prev = null

                counter++

                Toasty.success(
                    requireContext(),
                    correctMsgs[Random().nextInt(correctMsgs.size)],
                    Toast.LENGTH_SHORT,
                    true
                ).show()

                wordPairsAdapter.correctPairsSelected(position)
                if (prevPos != -1)
                    wordPairsAdapter.correctPairsSelected(prevPos)

                prevPos = -1

                if (counter == wordPairsResponse.allowedMoves) {
                    _result.postValue(true)
                }

            } else {
                prev = null

                Toasty.error(
                    requireContext(),
                    wrongMsgs[Random().nextInt(wrongMsgs.size)],
                    Toast.LENGTH_SHORT,
                    true
                ).show();

                Handler(requireActivity().mainLooper).postDelayed({
                    wordPairsAdapter.resetPairsSelected(position)
                    wordPairsAdapter.resetPairsSelected(prevPos)
                    prevPos = -1
                }, 500)
            }
        } else {
            prev = item
            prevPos = position
        }
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        itemTapListener.invoke()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}