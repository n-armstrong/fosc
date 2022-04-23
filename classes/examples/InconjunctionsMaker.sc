/* ------------------------------------------------------------------------------------------------------------
• InconjunctionsMaker

• Example 1

(
~maker = InconjunctionsMaker();

~selections = ~maker.(
    durations: #[[4,8]],
    // divisions: the rhythmic ratio/s that embed/s into durations
    divisions: #[3,2,1,2].wrapExtend(24),
    // groupSizes: per voice segmentations
    groupSizes: #[
        [5,5,5,5,4],
        [6,6,6,6],
        [5,6,7,6],
        [3,4,5,6,6]
    ],
    // pitches: per voice ordered pitch sequences that repeat cyclically at each segment
    pitches: #[
        "gqf'' bf'' cqf''' d''' ef'''",
        "d' g' bqs' aqf'' bf'' bqf''",
        "aqf d' af' bqs' gqf'' af'' bqf''",
        "gqs aqf d' g' af' bqs'"
    ],
    // hairpins: per voice, repeating cyclically at each segment
    hairpin: 'fff > f',
    // articulations: per voice, applied cyclically to the first event in each segment
    articulations: #['>'],
    // finalize: ad hoc per voice formatting
    finalize: { |sel, i|
        // override default spelling of tuplet ratio
        sel.selectComponents(FoscTuplet).do { |tuplet|
            tuplet.denominator = 4;
            tuplet.forceFraction = true;
        };

        #[
            "\\accidentalStyle dodecaphonic",
            "\\override Stem.direction = #DOWN",
            "\\override TupletBracket.direction = #UP"
        ].do { |str| sel[0].attach(FoscLilypondLiteral(str)) };
    };
);

// collect selections of music into a score and add final details
~score = FoscScore(~selections.collect { |sel| FoscStaff([FoscVoice(sel)]) });
~score.selectComponents(FoscStaff).do { |staff, i| set(staff).instrumentName = "Vln. %".format(i + 1) };
~score.leafAt(0).attach(FoscTimeSignature(#[4,8]));
~score.leafAt(0).attach(FoscMetronomeMark(#[1,8], 62));
~score.show(staffSize: 14);
)
------------------------------------------------------------------------------------------------------------ */
InconjunctionsMaker {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |durations, divisions, groupSizes, pitches, hairpin, articulations, finalize|
        var maker, selections, selection;

        maker = FoscMusicMaker();
        selections = [];

        groupSizes.do { |sizes, i|
            selection = maker.(durations: durations, divisions: divisions);
            
            selection.logicalTies.groupBySizes(sizes).do { |group, j|
                if (group.size > 1) {
                    group.beam;
                    if (hairpin.notNil) { group.hairpin(hairpin) };
                };

                if (articulations.notNil) {
                    articulations.do { |each| group.leafAt(0).attach(FoscArticulation(each)) };
                };

                if (pitches.notNil) { mutate(group).rewritePitches(pitches.wrapAt(i)) };
            };

            selections = selections.add(selection);
        };

        selections.do { |each, i| finalize.(each, i) };

        ^selections;
    }
}
