/* ------------------------------------------------------------------------------------------------------------
• FoscEventSequenceList

A list of FoscEventSequences.

• Example 1

Initialize from FoscEventSequences.

a = FoscEventSequence.newFromPitchesAndDurations("C4 E4", [1/4, 2/4]);
b = FoscEventSequence.newFromPitchesAndDurations("D4 Eb4", [1/8, 2/8]);
x = FoscEventSequenceList([a, b]);
x.inspect;


• Example 2

Initialize from music.

a = FoscLeafMaker().(["C#4", "D4"], [[3, 8], [1, 8], [5, 16], [3, 16]]);
b = FoscLeafMaker().(["Eb4", "E4"], [[3, 8], [1, 8], [1, 16], [3, 16]]);
m = FoscStaff(a);
n = FoscStaff(b);
x = FoscScore([m, n]);
y = FoscEventSequenceList(x);
y.inspect;


• Example 3

Initialize from a FoscSelection.

a = FoscLeafMaker().("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
y = FoscEventSequenceList(a);
y.inspect;


• Example 4

Initialize from various objects.

a = FoscLeafMaker().("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
b = FoscLeafMaker().("Eb4 E4", [3/16, 5/16, 1/8, 3/8]);
m = FoscStaff(a.deepCopy);
y = FoscEventSequenceList([a, b, m]);
y.inspect;


• Example 5

Play the sequences.
!!!TODO: add tempo and dynamics to this example

a = FoscLeafMaker().("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
b = FoscLeafMaker().("Eb4 E4", [3/16, 5/16, 1/8, 3/8]);
m = FoscStaff(a, name: 'piano');
n = FoscStaff(b, name: 'bcl');
x = FoscScore([m, n]);
y = FoscEventSequenceList(x);
y.play;
y.pause;
y.resume;
y.stop;


• Example 6

Play the sequences mapping from names of components to playback output destinations.

a = FoscLeafMaker().("C#4 D4", [5/16, 3/16, 1/8, 3/8]);
b = FoscLeafMaker().("Eb4 E4", [3/8, 1/8, 3/16, 5/16]);
c = FoscLeafMaker().("F4 G4 A4, Bb4", [2/8]);
m = FoscStaff(a, name: 'piano');
n = FoscStaffGroup([FoscStaff(b), FoscStaff(c)], name: 'bcl');
x = FoscScore([m, n]);
y = FoscEventSequenceList(x);
y.play;
y.pause;
y.resume;
y.stop;
------------------------------------------------------------------------------------------------------------ */
FoscEventSequenceList : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <eventSequences;
    var player;
    *new { |eventSequences|
        if ([FoscComponent, FoscSelection].any { |type| eventSequences.isKindOf(type) }) {
            ^this.prNewFromMusic(eventSequences);
        };
        eventSequences = eventSequences ? [];
        assert(eventSequences.isSequenceableCollection);
        eventSequences.collectInPlace { |each| 
            if (each.isKindOf(FoscEventSequence).not) { FoscEventSequence(each) } { each };
        };
        assert(eventSequences.every { |each| each.isKindOf(FoscEventSequence) });
        ^super.new.init(eventSequences);
    }
    init { |argEventSequences|
        eventSequences = argEventSequences;
        //!!! eventSequences.do { |each| each.instrumentMapper.postln };
        //!!! player = FoscPlayer();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prNewFromMusic

    NB: only adds contexts that contain pitched events.

    a = FoscLeafMaker().("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
    b = FoscLeafMaker().("Eb4 E4", [3/16, 5/16, 1/8, 3/8]);
    m = FoscStaff(a);
    n = FoscStaff(b);
    x = FoscScore([m, n]);
    y = FoscEventSequenceList(x);
    y.inspect;

    a = FoscScoreSegment(Threads).read('A1.1');
    b = FoscEventSequenceList.prNewFromMusic(a.score);
    b.eventSequences;
    -------------------------------------------------------------------------------------------------------- */
    *prNewFromMusic { |music|
        var eventSequences, prototype, recurse, name, metronomeMark, eventSequence;

        assert(
            (music.isKindOf(FoscContext)),
            "%:%: argument must be a FoscScore, FoscStaffGroup, FoscStaff, or FoscVoice: %."
                .format(this.species, thisMethod.name, music);
        );
        
        eventSequences = List[];
        prototype = [FoscNote, FoscChord];

        recurse = { |context|
            name = context.name;
            if (context.isSimultaneous) {
                context.do { |each| recurse.(each) };
            } {
                if (context.includesAnyOfType([FoscNote, FoscChord])) {
                    eventSequence = FoscEventSequence.prNewFromMusic(context);
                    eventSequences.add(eventSequence); 
                };
            };
        };

        recurse.(music);
        ^this.new(eventSequences);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration

    a = FoscEventSequence.newFromPitchesAndDurations("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
    b = FoscEventSequence.newFromPitchesAndDurations("Eb4 E4", [3/8, 1/8, 5/16]);
    x = FoscEventSequenceList([a, b]);
    x.duration.pair;
    -------------------------------------------------------------------------------------------------------- */
    duration {
        var durations;
        durations = eventSequences.collect { |each| each.duration };
        ^durations.maxItem;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: DISPLAY
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • inspect
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        Post << this << nl << nl;
        eventSequences.do { |each|
            each.inspect;
            Post << nl;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • show

    a = FoscEventSequence.newFromPitchesAndDurations("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
    b = FoscEventSequence.newFromPitchesAndDurations("Eb4 E4", [3/16, 5/16, 1/8, 3/8]);
    x = FoscEventSequenceList([a, b]);
    x.show;

    FoscObject
    -------------------------------------------------------------------------------------------------------- */
    show {
        //!!!TODO: illustrate instead of show
        //!!!TODO: rewriteMeter
        var staffs, score;
        staffs = eventSequences.collect { |each| FoscStaff(each.asFoscSelection) };
        score = FoscScore(staffs);
        //^score.illustrate;
        score.show;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prepareToPlay

    a = FoscScoreSegment(Threads).read('A1');
    b = FoscEventSequenceList(a.score);
    b.prepareToPlay;
    b.play;
    -------------------------------------------------------------------------------------------------------- */
    // prepareToPlay { |mappers|
    //     var patterns, names, logicalVoice, mapper, pattern;
        
    //     "PREPARING ...".postln; //#####
    //     mappers = mappers ? (); //!!!TODO: assign defaults
    //     patterns = List[];
    //     names = mappers.keys;

    //     eventSequences.do { |eventSequence|
    //         logicalVoice = eventSequence.logicalVoice;
    //         logicalVoice.removeAt('score');
    //         logicalVoice.keysValuesChange { |key, val|
    //             if (val.isString) {
    //                 val = val.replace("FoscVoice-");  
    //                 val = val.replace("FoscStaff-");
    //                 val = val.replace("FoscStaffGroup-");
    //                 val = val.asSymbol;
    //             };
    //         };
    //         logicalVoice.values.do { |name| if (names.includes(name)) { mapper = mappers[name] } };
    //         pattern = eventSequence.prepareToPlay(mapper);
    //         patterns.add(pattern);
    //     };

    //     pattern = Ppar(patterns);
    //     player.pattern_(pattern);
    //     "READY".postln; //#####
    //     ^pattern; // return pattern for use by clients
    // }
    /* --------------------------------------------------------------------------------------------------------
    • isPlaying
    -------------------------------------------------------------------------------------------------------- */
    isPlaying {
        ^player.isPlaying;
    }
    /* --------------------------------------------------------------------------------------------------------
    • play
    -------------------------------------------------------------------------------------------------------- */
    play { |mappers|
        if (player.pattern.isNil) { this.prepareToPlay(mappers) };
        player.play;
    }
    /* --------------------------------------------------------------------------------------------------------
    • pause
    -------------------------------------------------------------------------------------------------------- */
    pause {
        player.pause;
    }
    /* --------------------------------------------------------------------------------------------------------
    • resume
    -------------------------------------------------------------------------------------------------------- */
    resume {
        player.resume;
    }
    /* --------------------------------------------------------------------------------------------------------
    • stop
    -------------------------------------------------------------------------------------------------------- */
    stop {
        player.stop;
    }
}
