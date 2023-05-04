package com.hisu.english4kids.data.model.game_play

import com.hisu.english4kids.data.model.card.Card

class GameStyleOne(
    playType: Int,
    score: Int,
    var allowedMoves: Int,
    var totalPairs: Int,
    var cards: List<Card>
) : BaseGamePlay(playType, score)