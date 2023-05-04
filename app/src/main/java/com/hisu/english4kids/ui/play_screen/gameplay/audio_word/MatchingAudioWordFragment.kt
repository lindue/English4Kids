package com.hisu.english4kids.ui.play_screen.gameplay.audio_word

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hisu.english4kids.R
import com.hisu.english4kids.data.model.game_play.GameStyleFive
import com.hisu.english4kids.databinding.FragmentMatchingAudioWordBinding

class MatchingAudioWordFragment(
    private val itemTapListener: () -> Unit,
    private val wrongAnswerListener: () -> Unit,
    private val correctAnswerListener: (score: Int) -> Unit,
    private val gameStyleFive: GameStyleFive
) : Fragment() {

    private var _binding: FragmentMatchingAudioWordBinding? = null
    private val binding get() = _binding!!
    private val _result = MutableLiveData<Boolean>()
    private val result: LiveData<Boolean> = _result
    private lateinit var answer: String

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingAudioWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val audioImageAdapter = MatchingAudioWordAdapter(requireContext()) {
            answer = it.cardId

            if (!binding.btnCheck.btnNextRound.isEnabled) {
                binding.btnCheck.btnNextRound.isEnabled = true
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.check)
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.classic))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
            }
        }

        audioImageAdapter.pairs = gameStyleFive.cards

        binding.rvPickAnswer.adapter = audioImageAdapter

        result.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.GONE
                binding.btnCheck.tvCorrect.visibility = View.VISIBLE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.correct))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_correct))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))

                audioImageAdapter.isLockView = true
                correctAnswerListener.invoke(gameStyleFive.score)
            } else {
                binding.btnCheck.btnNextRound.text = requireContext().getString(R.string.next)
                binding.btnCheck.containerWrong.visibility = View.VISIBLE
                binding.btnCheck.tvCorrect.visibility = View.GONE
                binding.btnCheck.containerNextRound.setBackgroundColor(requireContext().getColor(R.color.incorrect))
                binding.btnCheck.btnNextRound.setBackgroundColor(requireContext().getColor(R.color.text_incorrect))
                binding.btnCheck.btnNextRound.setTextColor(requireContext().getColor(R.color.white))
                audioImageAdapter.isLockView = true

                wrongAnswerListener.invoke()
            }
        }

        checkAnswer()
        playAudio()
    }

    private fun checkAnswer() = binding.btnCheck.btnNextRound.setOnClickListener {
        if (binding.btnCheck.btnNextRound.text == requireContext().getString(R.string.check)) {
            if (answer == gameStyleFive.correctAns) {
                _result.postValue(true)
            } else {
                _result.postValue(false)

                val correctAnswer = gameStyleFive.cards.first {
                    it.cardId == gameStyleFive.correctAns
                }

                binding.btnCheck.tvCorrectAnswer.text = correctAnswer.word
            }
        } else {
            itemTapListener.invoke()
        }
    }

    private fun playAudio() = binding.ibtnAudioQuestion.setOnClickListener {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(gameStyleFive.question)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}