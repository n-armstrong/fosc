/* ------------------------------------------------------------------------------------------------------------
• FoscMusicMaker


!!!TODO:
• add initialOffset argument
• add padRight argument
• add specifiers: rewriteMeter, accidentalSpelling, beaming, etc.
• rename 'pitches' as 'writtenPitches'


• Example 1

var t, r, tempo, rhythmMaker;

t = #[[4,8],[3,8],[4,8],[4,8],[3,8],[4,8]];
r = (22 * 4).partitionByRatio([1,1,1,1,1,1,1,1,1]);
tempo = FoscMetronomeMark(#[1,8], 66);
x = FoscScoreSegment(Sev, 'D', t, tempo);

rhythmMaker = FoscRhythmMaker(
    tupletSpecifier: FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true)
);

m = FoscMusicMaker(segment: x, rhythmMaker: rhythmMaker);

m.(
    contextName: 'vc0',
    divisions: 1/8 ! 22,
    ratios: Ptenney(#[[3,3,2,2],[2,3,3,2],[3,2,2,3],[3,2,3,2],[2,3,2,3]], alpha: 0.7),
    masks: FoscSilenceMask(FoscPattern.indices(r.offsets[1..] - 1)),
    pitches: Ploop((60..84), Pseq(r - 1, inf)),
    seed: 7255389
);

m.music.doRuns { |run| if (run.size > 1) { run.slur } };

m.show;


// add to segment and show
x['vc0'].addAll(m.music)
x.show;
------------------------------------------------------------------------------------------------------------ */
FoscMusicMaker : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <segment, <rhythmMaker;
    var <music, <player;
    *new { |segment, rhythmMaker|
        ^super.new.init(segment, rhythmMaker);
    }
    init { |argSegment, argRhythmMaker|
        segment = argSegment;
        rhythmMaker = argRhythmMaker;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |contextName, divisions, ratios, masks, pitches, seed|
        var selections;

        //!!!TODO: check if contextName is in segment

        //if (timeSignatures.notNil) { segment.addTimeSignatures };
        if (seed.notNil) { thisThread.randSeed_(seed) };
        
        divisions = divisions ?? { segment.timeSignatures };
        ratios = ratios ?? { #[[1]]};
        selections = rhythmMaker.(divisions: divisions, ratios: ratios, masks: masks);
        //music = FoscVoice(selections, lilypondType: lilypondType);
        music = FoscVoice(selections);
        //!!! this.prCheckDuration; // NOT WORKING
        if (pitches.notNil) { mutate(music).rewritePitches(pitches) };

        //!!! TODO: check current contents of voice, emptyContents if necessary
        if (segment[contextName ? 0].isEmpty) { segment[contextName].addAll(music) };

        //this.decorate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • play

    m.play;
    -------------------------------------------------------------------------------------------------------- */
    play {
        //this.instVarPut('player', FoscPlayer(music).play);
        //this.instVarPut('player', FoscPlayer(segment).play);
        segment.play;
        player = segment.player;
    }
    /* --------------------------------------------------------------------------------------------------------
    • show
    -------------------------------------------------------------------------------------------------------- */
    show {
        //this.prReduceScore;
        segment.show;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prReduceScore

    Removes all non-active staves from score.

    !!!TODO: move this method to FoscScoreSegment
    -------------------------------------------------------------------------------------------------------- */
    prReduceScore {
        // var inactiveContexts, staff;
        // inactiveContexts = segment.staffNames.symmetricDifference([context]);
        // inactiveContexts.do { |name|
        //     staff = segment[name];
        //     staff.parent.remove(staff); 
        // };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • numEvents
    -------------------------------------------------------------------------------------------------------- */
    numEvents {
        ^music.selectLogicalTies.size;
    }
    /* --------------------------------------------------------------------------------------------------------
    • numPitchedEvents
    -------------------------------------------------------------------------------------------------------- */
    numPitchedEvents {
        ^this.pitchedEventsPerPhrase.sum;
    }
    /* --------------------------------------------------------------------------------------------------------
    • str
    -------------------------------------------------------------------------------------------------------- */
    str {
        var pieces;
        pieces = [
            "total phrases: %".format(this.numPhrases),
            "phrase sizes: %".format(this.phraseSizes.ccs),
            "total events: %".format(this.numEvents),
            "total pitched events: %".format(this.numPitchedEvents),
            "pitched events per phrase: %".format(this.pitchedEventsPerPhrase.ccs)
        ];
        ^pieces.join("\n");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCheckDuration
    -------------------------------------------------------------------------------------------------------- */
    prCheckDuration {
        var musicDuration, segmentDuration, pieces;
        if (segment.isNil) { ^nil };
        musicDuration = FoscInspection(music).duration;
        segmentDuration = segment.duration;
        if (musicDuration != segmentDuration) {
            pieces = [
                "%:new: duration of music is not equal to duration of segment: ",
                "music duration: %.",
                "segment duration: %."
            ];
            warn(pieces.join("\n").format(this.species, musicDuration.str, segmentDuration.str));
        };
    }
}
