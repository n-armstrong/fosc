/* ------------------------------------------------------------------------------------------------------------
• FoscVoiceMaker

!!!TODO:
• add initialOffset argument
• add padRight argument
• add specifiers: rewriteMeter, accidentalSpelling, beaming, etc.
• rename 'pitches' as 'writtenPitches'


• Example 1

m = FoscVoiceMaker();

n = m.(
    name: 'foo',
    divisions: 1/8 ! 22,
    ratios: Ptenney(#[[3,3,2,2],[2,3,3,2],[3,2,2,3],[3,2,3,2],[2,3,2,3]], alpha: 0.7),
    //masks: FoscSilenceMask(FoscPattern.indices(r.offsets[1..] - 1)),
    pitches: Pseq((60,60.5..65), inf), //!!! NOT WORKING Pseq((60,60.25..84), inf),
    seed: 7255389
);

m.show;
m.play;
m.stop;


• Example 2

a = FoscMIDIPlaybackManager(midiChan: 0);
a.midiOut.control(0, 64, 0);

m = FoscVoiceMaker(playbackManager: a);

p = [-2,6,6,6,6,6,6,-2];

n = m.(
    divisions: 1/4 ! 8,
    ratios: #[[1,1,1,1,1]],
    masks: FoscSustainMask(FoscPattern.indices(p.abs.offsets.drop(-1)), hold: true),
    pitches: Pseq((60,60.5..65), inf),
    seed: 7255389,
    timeSignatures: #[[8,4]],
    tempo: FoscMetronomeMark([1,4], 48)
);

m.voice.leafAt(0).attach(FoscLilypondComment('sustainOn'));
m.voice.leafAt(0).attach(FoscDynamic('pp'));
m.voice.format;

//m.show;
m.play;
m.stop;
------------------------------------------------------------------------------------------------------------ */
FoscVoiceMaker : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <rhythmMaker, <playbackManager;
    var <voice, <player;
    *new { |rhythmMaker, playbackManager|

        rhythmMaker = rhythmMaker ?? {
            FoscRhythmMaker(
                tupletSpecifier: FoscTupletSpecifier(
                    extractTrivial: true,
                    rewriteSustained: true,
                    rewriteRestFilled: true
                );
            );
        };

        playbackManager = playbackManager ?? { FoscMIDIPlaybackManager(midiChan: 0) };

        ^super.new.init(rhythmMaker, playbackManager);
    }
    init { |argRhythmMaker, argPlaybackManager|
        rhythmMaker = argRhythmMaker;
        playbackManager = argPlaybackManager;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |divisions, ratios, masks, pitches, timeSignatures, seed, tempo, name|
        var selections;

        //if (timeSignatures.notNil) { segment.addTimeSignatures };
        if (seed.notNil) { thisThread.randSeed_(seed) };
        //tempo = tempo ?? { FoscMetronomeMark([1,4], 60) };
        
        //divisions = divisions ?? { segment.timeSignatures };
        ratios = ratios ?? { #[[1]]};
        selections = rhythmMaker.(divisions: divisions, ratios: ratios, masks: masks);
        //music = FoscVoice(selections, lilypondType: lilypondType);

        //!!! TODO: timeSignatures = timeSignatures ?? { divisions.collect { |each| FoscTimeSignature(each) } };
        //!!! TODO: score['global']; // see FoscScoreSegment for adding global skips

        voice = FoscVoice(selections, name: name, playbackManager: playbackManager);
        //!!! this.prCheckDuration; // NOT WORKING
        if (pitches.notNil) { mutate(voice).rewritePitches(pitches) };
        if (tempo.notNil) { voice.leafAt(0).attach(tempo) };

        ^voice;
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate
    -------------------------------------------------------------------------------------------------------- */
    illustrate {   
        var staff, score, lilypondFile;

        staff = FoscStaff();
        set(staff).instrumentName_(voice.name);
        set(staff).shortInstrumentName_(voice.name);
        staff.add(voice.deepCopy);
        score = FoscScore(name: 'score');
        score.add(staff);
        
        lilypondFile = FoscLilypondFile(
            items: score,
            lilypondVersionToken: "2.19.24", //FoscLilypondVersionToken(this.lilypondVersion),
            includes: [
                "../stylesheets/voice-maker.ily",
                "../stylesheets/microtonal-accidentals.ily"
            ],
            useRelativeIncludes: true
        );

        ^lilypondFile;
    }
    /* --------------------------------------------------------------------------------------------------------
    • play

    m = FoscScoreSegment(ALAI, 'A');
    -------------------------------------------------------------------------------------------------------- */
    play {
        player = FoscPlayer(voice).play;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • numEvents
    -------------------------------------------------------------------------------------------------------- */
    // numEvents {
    //     ^voice.selectLogicalTies.size;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • numPitchedEvents
    -------------------------------------------------------------------------------------------------------- */
    // numPitchedEvents {
    //     ^this.pitchedEventsPerPhrase.sum;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • str
    -------------------------------------------------------------------------------------------------------- */
    // str {
    //     var pieces;
    //     pieces = [
    //         "total phrases: %".format(this.numPhrases),
    //         "phrase sizes: %".format(this.phraseSizes.ccs),
    //         "total events: %".format(this.numEvents),
    //         "total pitched events: %".format(this.numPitchedEvents),
    //         "pitched events per phrase: %".format(this.pitchedEventsPerPhrase.ccs)
    //     ];
    //     ^pieces.join("\n");
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCheckDuration
    -------------------------------------------------------------------------------------------------------- */
    // prCheckDuration {
    //     var voiceDuration, segmentDuration, pieces;
    //     //if (segment.isNil) { ^nil };
    //     voiceDuration = FoscInspection(voice).duration;
    //     //segmentDuration = segment.duration;
    //     if (voiceDuration != segmentDuration) {
    //         pieces = [
    //             "%:new: duration of voice is not equal to duration of segment: ",
    //             "voice duration: %.",
    //             "segment duration: %."
    //         ];
    //         warn(pieces.join("\n").format(this.species, voiceDuration.str, segmentDuration.str));
    //     };
    // }
}
